package dev.sanson.circuit.codegen

import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.assisted.AssistedFactory
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

/**
 * The following code is derived from Circuit's Anvil-based KSP codegen processor, but instead
 * generates hilt-compatible code using the same CircuitInject paradigm
 *
 * TODO: Generate module; Re-implement function code-gen for UI
 */
private const val CIRCUIT_RUNTIME_BASE_PACKAGE = "com.slack.circuit.runtime"
private const val CIRCUIT_RUNTIME_UI_PACKAGE = "$CIRCUIT_RUNTIME_BASE_PACKAGE.ui"
private const val CIRCUIT_RUNTIME_SCREEN_PACKAGE = "$CIRCUIT_RUNTIME_BASE_PACKAGE.screen"
private const val CIRCUIT_RUNTIME_PRESENTER_PACKAGE = "$CIRCUIT_RUNTIME_BASE_PACKAGE.presenter"
private val MODIFIER = ClassName("androidx.compose.ui", "Modifier")
private val CIRCUIT_INJECT_ANNOTATION =
    ClassName("com.slack.circuit.codegen.annotations", "CircuitInject")
private val CIRCUIT_PRESENTER = ClassName(CIRCUIT_RUNTIME_PRESENTER_PACKAGE, "Presenter")
private val CIRCUIT_PRESENTER_FACTORY = CIRCUIT_PRESENTER.nestedClass("Factory")
private val CIRCUIT_UI = ClassName(CIRCUIT_RUNTIME_UI_PACKAGE, "Ui")
private val CIRCUIT_UI_FACTORY = CIRCUIT_UI.nestedClass("Factory")
private val CIRCUIT_UI_STATE = ClassName(CIRCUIT_RUNTIME_BASE_PACKAGE, "CircuitUiState")
private val SCREEN = ClassName(CIRCUIT_RUNTIME_SCREEN_PACKAGE, "Screen")
private val NAVIGATOR = ClassName(CIRCUIT_RUNTIME_BASE_PACKAGE, "Navigator")
private val CIRCUIT_CONTEXT = ClassName(CIRCUIT_RUNTIME_BASE_PACKAGE, "CircuitContext")
private const val FACTORY = "Factory"

private class CircuitSymbols private constructor(resolver: Resolver) {
    val screen = resolver.loadKSType(SCREEN.canonicalName)
    val navigator = resolver.loadKSType(NAVIGATOR.canonicalName)

    companion object {
        fun create(resolver: Resolver): CircuitSymbols? {
            @Suppress("SwallowedException")
            return try {
                CircuitSymbols(resolver)
            } catch (e: IllegalStateException) {
                null
            }
        }
    }
}

private fun Resolver.loadKSType(name: String): KSType =
    loadOptionalKSType(name) ?: error("Could not find $name in classpath")

private fun Resolver.loadOptionalKSType(name: String?): KSType? {
    if (name == null) return null
    return getClassDeclarationByName(getKSNameFromString(name))?.asType(emptyList())
}

private enum class FactoryType { Presenter, Ui }

@AutoService(SymbolProcessorProvider::class)
class CircuitSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CircuitHiltSymbolProcessor(environment.logger, environment.codeGenerator)
    }
}

private class CircuitHiltSymbolProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = CircuitSymbols.create(resolver) ?: return emptyList()

        resolver.getSymbolsWithAnnotation(CIRCUIT_INJECT_ANNOTATION.canonicalName)
            .forEach { annotated ->
                when (annotated) {
                    is KSClassDeclaration ->
                        generateFactory(
                            annotatedElement = annotated,
                            symbols = symbols,
                        )

                    else ->
                        logger.error(
                            message = "CircuitInject for Hilt is only applicable on classes.",
                            symbol = annotated,
                        )
                }
            }

        return emptyList()
    }

    private fun generateFactory(
        annotatedElement: KSAnnotated,
        symbols: CircuitSymbols,
    ) {
        val circuitInjectAnnotation =
            annotatedElement.annotations.first {
                it.annotationType.resolve().declaration.qualifiedName?.asString() ==
                        CIRCUIT_INJECT_ANNOTATION.canonicalName
            }
        val screenKSType = circuitInjectAnnotation.arguments[0].value as KSType
        val screenIsObject =
            screenKSType.declaration.let { it is KSClassDeclaration && it.classKind == ClassKind.OBJECT }
        val screenType = screenKSType.toTypeName()

        val factoryData =
            computeFactoryData(annotatedElement, symbols, screenKSType, logger)
                ?: return

        val className =
            factoryData.className.replaceFirstChar { char ->
                char.takeIf { char.isLowerCase() }?.run { uppercase(Locale.getDefault()) }
                    ?: char.toString()
            }

        val builder =
            TypeSpec.classBuilder(className + FACTORY)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addAnnotation(Inject::class)
                        .addParameters(factoryData.constructorParams)
                        .build(),
                )
                .apply {
                    if (factoryData.constructorParams.isNotEmpty()) {
                        for (param in factoryData.constructorParams) {
                            addProperty(
                                PropertySpec.builder(param.name, param.type, KModifier.PRIVATE)
                                    .initializer(param.name)
                                    .build(),
                            )
                        }
                    }
                }
        val screenBranch =
            if (screenIsObject) {
                CodeBlock.of("%T", screenType)
            } else {
                CodeBlock.of("is·%T", screenType)
            }
        val typeSpec =
            when (factoryData.factoryType) {
                FactoryType.Presenter ->
                    builder.buildPresenterFactory(
                        annotatedElement,
                        screenBranch,
                        factoryData.codeBlock,
                    )

                FactoryType.Ui ->
                    builder.buildUiFactory(annotatedElement, screenBranch, factoryData.codeBlock)
            }

        FileSpec.get(factoryData.packageName, typeSpec)
            .writeTo(codeGenerator = codeGenerator, aggregating = false)
    }

    private data class FactoryData(
        val className: String,
        val packageName: String,
        val factoryType: FactoryType,
        val constructorParams: List<ParameterSpec>,
        val codeBlock: CodeBlock,
    )

    /** Computes the data needed to generate a factory. */
    // Detekt and ktfmt don't agree on whether or not the rectangle rule makes for readable code.
    @Suppress("ComplexMethod", "LongMethod", "ReturnCount")
    @OptIn(KspExperimental::class)
    private fun computeFactoryData(
        annotatedElement: KSAnnotated,
        symbols: CircuitSymbols,
        screenKSType: KSType,
        logger: KSPLogger,
    ): FactoryData? {
        val className: String
        val packageName: String
        val factoryType: FactoryType
        val constructorParams = mutableListOf<ParameterSpec>()
        val codeBlock: CodeBlock

        val cd = annotatedElement as KSClassDeclaration
        cd.checkVisibility(logger) {
            return null
        }
        val isAssisted = cd.isAnnotationPresent(AssistedFactory::class)
        val creatorOrConstructor: KSFunctionDeclaration?
        val targetClass: KSClassDeclaration
        if (isAssisted) {
            val creatorFunction = cd.getAllFunctions().filter { it.isAbstract }.single()
            creatorOrConstructor = creatorFunction
            targetClass = creatorFunction.returnType!!.resolve().declaration as KSClassDeclaration
            targetClass.checkVisibility(logger) {
                return null
            }
        } else {
            creatorOrConstructor = cd.primaryConstructor
            targetClass = cd
        }
        val useProvider =
            !isAssisted && creatorOrConstructor?.isAnnotationPresent(Inject::class) == true
        className = targetClass.simpleName.getShortName()
        packageName = targetClass.packageName.asString()
        factoryType =
            targetClass
                .getAllSuperTypes()
                .mapNotNull {
                    when (it.declaration.qualifiedName?.asString()) {
                        CIRCUIT_UI.canonicalName -> FactoryType.Ui
                        CIRCUIT_PRESENTER.canonicalName -> FactoryType.Presenter
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
        val assistedParams =
            if (useProvider) {
                // Nothing to do here, we'll just use the provider directly.
                CodeBlock.of("")
            } else {
                creatorOrConstructor?.assistedParameters(
                    symbols,
                    logger,
                    screenKSType,
                    allowNavigator = factoryType == FactoryType.Presenter,
                )
            }
        codeBlock =
            if (useProvider) {
                // Inject a Provider<TargetClass> that we'll call get() on.
                constructorParams.add(
                    ParameterSpec.builder(
                        "provider",
                        Provider::class.asClassName().parameterizedBy(targetClass.toClassName()),
                    )
                        .build(),
                )
                CodeBlock.of("provider.get()")
            } else if (isAssisted) {
                // Inject the target class's assisted factory that we'll call its create() on.
                constructorParams.add(ParameterSpec.builder("factory", cd.toClassName()).build())
                CodeBlock.of(
                    "factory.%L(%L)",
                    creatorOrConstructor!!.simpleName.getShortName(),
                    assistedParams,
                )
            } else {
                // Simple constructor call, no injection.
                CodeBlock.of("%T(%L)", targetClass.toClassName(), assistedParams)
            }
        return FactoryData(className, packageName, factoryType, constructorParams, codeBlock)
    }
}


private fun TypeSpec.Builder.buildUiFactory(
    originatingSymbol: KSAnnotated,
    screenBranch: CodeBlock,
    instantiationCodeBlock: CodeBlock,
): TypeSpec {
    return addSuperinterface(CIRCUIT_UI_FACTORY)
        .addFunction(
            FunSpec.builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("screen", SCREEN)
                .addParameter("context", CIRCUIT_CONTEXT)
                .returns(CIRCUIT_UI.parameterizedBy(STAR).copy(nullable = true))
                .beginControlFlow("return·when·(screen)")
                .addStatement("%L·->·%L", screenBranch, instantiationCodeBlock)
                .addStatement("else·->·null")
                .endControlFlow()
                .build(),
        )
        .addOriginatingKSFile(originatingSymbol.containingFile!!)
        .build()
}

private fun TypeSpec.Builder.buildPresenterFactory(
    originatingSymbol: KSAnnotated,
    screenBranch: CodeBlock,
    instantiationCodeBlock: CodeBlock,
): TypeSpec {
    // The TypeSpec below will generate something similar to the following.
    //  public class AboutPresenterFactory : Presenter.Factory {
    //    public override fun create(
    //      screen: Screen,
    //      navigator: Navigator,
    //      context: CircuitContext,
    //    ): Presenter<*>? = when (screen) {
    //      is AboutScreen -> AboutPresenter()
    //      is AboutScreen -> presenterOf { AboutPresenter() }
    //      else -> null
    //    }
    //  }

    return addSuperinterface(CIRCUIT_PRESENTER_FACTORY)
        .addFunction(
            FunSpec.builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("screen", SCREEN)
                .addParameter("navigator", NAVIGATOR)
                .addParameter("context", CIRCUIT_CONTEXT)
                .returns(CIRCUIT_PRESENTER.parameterizedBy(STAR).copy(nullable = true))
                .beginControlFlow("return when (screen)")
                .addStatement("%L·->·%L", screenBranch, instantiationCodeBlock)
                .addStatement("else·->·null")
                .endControlFlow()
                .build(),
        )
        .addOriginatingKSFile(originatingSymbol.containingFile!!)
        .build()
}

private data class AssistedType(
    val factoryName: String,
    val type: TypeName,
    val name: String,
)


/**
 * Returns a [CodeBlock] representation of all named assisted parameters on this
 * [KSFunctionDeclaration] to be used in generated invocation code.
 *
 * Example: this function
 *
 * ```kotlin
 * @Composable
 * fun HomePresenter(screen: Screen, navigator: Navigator)
 * ```
 *
 * Yields this CodeBlock: `screen = screen, navigator = navigator`
 */
private fun KSFunctionDeclaration.assistedParameters(
    symbols: CircuitSymbols,
    logger: KSPLogger,
    screenType: KSType,
    allowNavigator: Boolean,
): CodeBlock {
    return buildSet {
        for (param in parameters) {
            fun <E> MutableSet<E>.addOrError(element: E) {
                val added = add(element)
                if (!added) {
                    logger.error("Multiple parameters of type $element are not allowed.", param)
                }
            }

            val type = param.type.resolve()
            when {
                type.isInstanceOf(symbols.screen) -> {
                    if (screenType.isSameDeclarationAs(type)) {
                        addOrError(
                            AssistedType(
                                "screen",
                                type.toTypeName(),
                                param.name!!.getShortName(),
                            ),
                        )
                    } else {
                        logger.error(
                            "Screen type mismatch. Expected $screenType but found $type",
                            param,
                        )
                    }
                }

                type.isInstanceOf(symbols.navigator) -> {
                    if (allowNavigator) {
                        addOrError(
                            AssistedType(
                                "navigator",
                                type.toTypeName(),
                                param.name!!.getShortName(),
                            ),
                        )
                    } else {
                        logger.error(
                            "Navigator type mismatch. Navigators are not injectable on this type.",
                            param,
                        )
                    }
                }
            }
        }
    }
        .toList()
        .map { CodeBlock.of("${it.name} = ${it.factoryName}") }
        .joinToCode(",·")
}


private fun KSType.isSameDeclarationAs(type: KSType): Boolean {
    return this.declaration == type.declaration
}

private fun KSType.isInstanceOf(type: KSType): Boolean {
    return type.isAssignableFrom(this)
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
