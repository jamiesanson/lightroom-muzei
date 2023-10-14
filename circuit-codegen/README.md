# Circuit Codegen for Hilt

This module contains a bashed-together implementation of a Circuit extension which provides
support for Hilt integration, rather than the bundled Anvil support.

This implementation attempts to modify the existing symbol processing code as little as possible,
relying on a KSP parameter to switch code generation between DI frameworks, defaulting to existing
behaviour.

### Usage

The current implementation is untested, and largely under-researched, so usage is generally not
advised. However, for future reference:

1. Swap circuit code-gen dependency for this one
2. Pass KSP argument to use Hilt as the circuit DI framework:

```kotlin
ksp {
    arg("circuit-di", "hilt")
}
```

3. Annotate your factories, using dagger components instead of Anvil scopes:

```kotlin
@CircuitInject(ConfirmationScreen::class, SingletonComponent::class)
@AssistedFactory
interface Factory {
    fun create(navigator: Navigator): ConfirmationPresenter
}
```

### How it works

Hilt support has been added to existing processor code as simply as possible. The argument passed
via
KSP disables generation of the Anvil `@ContributeMultibinding` annotation, and instead generates the
equivalent Hilt code: a module installed in the provided component, with `@IntoSet` binding.