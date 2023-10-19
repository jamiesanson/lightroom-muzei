package dev.sanson.circuit.codegen.hilt

import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.assisted.AssistedFactory

private const val CIRCUIT_RUNTIME_BASE_PACKAGE = "com.slack.circuit.runtime"
private const val DAGGER_PACKAGE = "dagger"
private const val DAGGER_HILT_PACKAGE = "$DAGGER_PACKAGE.hilt"
private const val DAGGER_MULTIBINDINGS_PACKAGE = "$DAGGER_PACKAGE.multibindings"
private const val CIRCUIT_RUNTIME_UI_PACKAGE = "$CIRCUIT_RUNTIME_BASE_PACKAGE.ui"
private const val CIRCUIT_RUNTIME_PRESENTER_PACKAGE = "$CIRCUIT_RUNTIME_BASE_PACKAGE.presenter"
private val CIRCUIT_INJECT_ANNOTATION =
  ClassName("com.slack.circuit.codegen.annotations", "CircuitInject")
private val CIRCUIT_PRESENTER = ClassName(CIRCUIT_RUNTIME_PRESENTER_PACKAGE, "Presenter")
private val CIRCUIT_PRESENTER_FACTORY = CIRCUIT_PRESENTER.nestedClass("Factory")
private val CIRCUIT_UI = ClassName(CIRCUIT_RUNTIME_UI_PACKAGE, "Ui")
private val CIRCUIT_UI_FACTORY = CIRCUIT_UI.nestedClass("Factory")
private val DAGGER_MODULE = ClassName(DAGGER_PACKAGE, "Module")
private val DAGGER_BINDS = ClassName(DAGGER_PACKAGE, "Binds")
private val DAGGER_INSTALL_IN = ClassName(DAGGER_HILT_PACKAGE, "InstallIn")
private val DAGGER_INTO_SET = ClassName(DAGGER_MULTIBINDINGS_PACKAGE, "IntoSet")
private const val FACTORY = "Factory"
private const val MODULE = "Module"

@AutoService(SymbolProcessorProvider::class)
class HiltForCircuitSymbolProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    return HiltForCircuitSymbolProcessor(
      environment.logger,
      environment.codeGenerator,
    )
  }
}

private class HiltForCircuitSymbolProcessor(
  private val logger: KSPLogger,
  private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

  override fun process(resolver: Resolver): List<KSAnnotated> {
    resolver.getSymbolsWithAnnotation(CIRCUIT_INJECT_ANNOTATION.canonicalName)
      .forEach { annotatedElement ->
        when (annotatedElement) {
          is KSClassDeclaration ->
            generateProvider(
              annotatedElement = annotatedElement,
              instantiationType = InstantiationType.CLASS,
            )

          is KSFunctionDeclaration ->
            generateProvider(
              annotatedElement = annotatedElement,
              instantiationType = InstantiationType.FUNCTION,
            )

          else ->
            logger.error(
              "CircuitInject is only applicable on classes and functions.",
              annotatedElement,
            )
        }
      }
    return emptyList()
  }

  private fun generateProvider(
    annotatedElement: KSAnnotated,
    instantiationType: InstantiationType,
  ) {
    val circuitInjectAnnotation =
      annotatedElement.annotations.first {
        it.annotationType.resolve().declaration.qualifiedName?.asString() ==
                CIRCUIT_INJECT_ANNOTATION.canonicalName
      }

    val scope = (circuitInjectAnnotation.arguments[1].value as KSType).toTypeName()

    val moduleData =
      computeModuleData(annotatedElement, instantiationType, logger)
        ?: return

    val moduleTypeSpec = createDaggerModuleTypeSpec(
      factory = moduleData.factory,
      factoryType = moduleData.factoryType,
      scope = scope,
    )

    FileSpec
      .get(moduleData.factory.packageName, moduleTypeSpec)
      .writeTo(codeGenerator = codeGenerator, aggregating = false)
  }

  private data class ModuleData(
    val factory: ClassName,
    val factoryType: FactoryType,
  )

  @OptIn(KspExperimental::class)
  private fun computeModuleData(
    annotatedElement: KSAnnotated,
    instantiationType: InstantiationType,
    logger: KSPLogger,
  ): ModuleData? {
    val className: String
    val packageName: String
    val factoryType: FactoryType

    when (instantiationType) {
      InstantiationType.FUNCTION -> {
        val fd = annotatedElement as KSFunctionDeclaration
        fd.checkVisibility(logger) {
          return null
        }
        val name = annotatedElement.simpleName.getShortName()
        className = name
        packageName = fd.packageName.asString()
        factoryType =
          if (name.endsWith("Presenter")) {
            FactoryType.PRESENTER
          } else {
            FactoryType.UI
          }
      }

      InstantiationType.CLASS -> {
        val cd = annotatedElement as KSClassDeclaration
        cd.checkVisibility(logger) {
          return null
        }
        val isAssisted = cd.isAnnotationPresent(AssistedFactory::class)
        val targetClass: KSClassDeclaration
        if (isAssisted) {
          val creatorFunction = cd.getAllFunctions().filter { it.isAbstract }.single()
          targetClass =
            creatorFunction.returnType!!.resolve().declaration as KSClassDeclaration
          targetClass.checkVisibility(logger) {
            return null
          }
        } else {
          targetClass = cd
        }

        className = targetClass.simpleName.getShortName()
        packageName = targetClass.packageName.asString()
        factoryType =
          targetClass
            .getAllSuperTypes()
            .mapNotNull {
              when (it.declaration.qualifiedName?.asString()) {
                CIRCUIT_UI.canonicalName -> FactoryType.UI
                CIRCUIT_PRESENTER.canonicalName -> FactoryType.PRESENTER
                else -> null
              }
            }
            .firstOrNull()
            ?: run {
              logger.error(
                "Factory must be for a UI or Presenter class, but was " +
                        "${targetClass.qualifiedName?.asString()}. " +
                        "Supertypes: ${targetClass.getAllSuperTypes().toList()}",
                targetClass,
              )
              return null
            }
      }
    }

    return ModuleData(
      factory = ClassName(packageName, className.replaceFirstChar { it.uppercase() } + FACTORY),
      factoryType = factoryType,
    )
  }

  private fun createDaggerModuleTypeSpec(
    factory: ClassName,
    factoryType: FactoryType,
    scope: TypeName,
  ): TypeSpec {
    val moduleAnnotations = listOf(
      AnnotationSpec.builder(DAGGER_MODULE)
        .build(),
      AnnotationSpec.builder(DAGGER_INSTALL_IN)
        .addMember("%T::class", scope)
        .build(),
    )

    val providerAnnotations = listOf(
      AnnotationSpec.builder(DAGGER_BINDS)
        .build(),
      AnnotationSpec.builder(DAGGER_INTO_SET)
        .build(),
    )

    val providerReturns = if (factoryType == FactoryType.UI) {
      CIRCUIT_UI_FACTORY
    } else {
      CIRCUIT_PRESENTER_FACTORY
    }

    val factoryName = factory.simpleName

    val providerSpec = FunSpec
      .builder("bind${factoryName}")
      .addModifiers(KModifier.ABSTRACT)
      .addAnnotations(providerAnnotations)
      .addParameter(name = factoryName.replaceFirstChar { it.lowercase() }, type = factory)
      .returns(providerReturns)
      .build()

    return TypeSpec
      .classBuilder(factory.peerClass(factoryName + MODULE))
      .addModifiers(KModifier.ABSTRACT)
      .addAnnotations(moduleAnnotations)
      .addFunction(providerSpec)
      .build()
  }
}

private enum class FactoryType {
  PRESENTER,
  UI
}

private enum class InstantiationType {
  FUNCTION,
  CLASS
}

private inline fun KSDeclaration.checkVisibility(logger: KSPLogger, returnBody: () -> Unit) {
  if (!getVisibility().isVisible) {
    logger.error(
      "CircuitInject is not applicable to private or local functions and classes.",
      this,
    )
    returnBody()
  }
}

private val Visibility.isVisible: Boolean
  get() = this != Visibility.PRIVATE && this != Visibility.LOCAL