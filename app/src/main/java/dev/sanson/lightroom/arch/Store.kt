package dev.sanson.lightroom.arch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * [Store] is an interface derived from an earlier version of [Mavericks](https://github.com/airbnb/mavericks),
 * and is a component which holds, and updates State.
 */
interface Store<T> {
    val state: StateFlow<T>
    fun update(reduce: T.() -> T)
}

fun <T> Store(initialValue: T): Store<T> = DefaultStore(initialValue)

val <T> Store<T>.value: T get() = state.value

internal class DefaultStore<T>(
    initialValue: T,
) : Store<T> {

    private val _state = MutableStateFlow(initialValue)

    override val state: StateFlow<T> = _state.asStateFlow()

    override fun update(reduce: T.() -> T) {
        _state.update { it.reduce() }
    }
}
