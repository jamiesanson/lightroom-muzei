package dev.sanson.lightroom.ui.album

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem

sealed interface ChooseAlbumState : CircuitUiState {
    data object Loading : ChooseAlbumState

    data class Loaded(
        val albumTree: List<AlbumTreeItem>,
        val selectedAlbum: AlbumId?,
        val eventSink: (ChooseAlbumEvent) -> Unit,
    ) : ChooseAlbumState
}

sealed interface ChooseAlbumEvent : CircuitUiEvent {
    data class SelectAlbum(val albumId: AlbumId) : ChooseAlbumEvent
    data object Confirm : ChooseAlbumEvent
}
