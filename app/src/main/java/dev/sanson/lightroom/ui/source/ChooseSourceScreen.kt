package dev.sanson.lightroom.ui.source

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.sanson.lightroom.data.config.Config
import kotlinx.parcelize.Parcelize

@Parcelize
data object ChooseSourceScreen : Screen {

    data class State(
        val selectedSource: Config.Source?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object OnChooseCatalog : Event

        data object OnChooseAlbum : Event

        data object OnConfirm : Event
    }
}
