# Circuit Codegen for Hilt

This module contains an implementation of a Circuit extension which provides
support for Hilt integration, rather than the bundled Anvil support.

### Usage

1. Install circuit codegen processor
2. Install hilt for circuit codegen processor
3. Pass KSP argument to circuit to disable Anvil annotation being generated:

```kotlin
ksp {
  arg("circuit.generate-anvil-bindings", "false")
}
```

4. Annotate your factories, using dagger components instead of Anvil scopes:

```kotlin
@CircuitInject(ConfirmationScreen::class, SingletonComponent::class)
@AssistedFactory
interface Factory {
    fun create(navigator: Navigator): ConfirmationPresenter
}
```

### How it works

This implementation generates a Hilt module, installing it in the scope specified by the user,
and binding generated factories into a set using dagger multibinding.

The argument passed via KSP disables generation of the Anvil `@ContributeMultibinding` annotation,
which although not strictly necessary, allows for small savings in size of generated code.