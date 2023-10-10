package dev.sanson.lightroom.ui.album

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem
import kotlinx.parcelize.Parcelize

@Parcelize
data object ChooseAlbumScreen : Screen {

    sealed interface State : CircuitUiState {
        data object Loading : State

        data class Loaded(
            val albumTree: List<AlbumTreeItem>,
            val selectedAlbum: AlbumId?,
            val eventSink: (Event) -> Unit,
        ) : State
    }

    sealed interface Event : CircuitUiEvent {
        data class SelectAlbum(val albumId: AlbumId) : Event
        data object Confirm : Event
    }
}
