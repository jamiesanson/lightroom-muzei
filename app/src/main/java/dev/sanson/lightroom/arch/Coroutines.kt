package dev.sanson.lightroom.arch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.asAsyncFlow(): Flow<Async<T>> = flow {
    emit(Loading)

    emitAll(
        this@asAsyncFlow
            .map { Success(it) }
            .catch { Failure<T>(it) },
    )
}

suspend fun <T, State> Flow<T>.collectInto(store: Store<State>, embed: State.(T) -> State) =
    collect { store.update { embed(it) } }

suspend fun <T, State> (suspend () -> T).collectInto(
    store: Store<State>,
    embed: State.(Async<T>) -> State,
) {
    store.update { embed(Loading) }

    val nextEmission = runCatching { this@collectInto() }
        .fold(onSuccess = ::Success, onFailure = ::Failure)

    store.update { embed(nextEmission) }
}
