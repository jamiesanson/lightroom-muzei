package dev.sanson.lightroom.arch

sealed interface Async<out T> {
    val value: T? get() = null
}

sealed interface Incomplete : Async<Nothing>

object Uninitialized : Incomplete

object Loading : Incomplete

data class Success<T>(
    override val value: T,
) : Async<T>

data class Failure<T>(
    val error: Throwable,
) : Async<T>
