package dev.sanson.lightroom.ui.filter

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Screen
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data object FilterAssetsScreen : Screen {

    sealed interface State : CircuitUiState {
        data object Loading : State

        data class Loaded(
            val keywords: List<String>,
            val rating: IntRange?,
            val flag: Asset.Flag?,
            val eventSink: (Event) -> Unit,
        ) : State
    }

    sealed interface Event : CircuitUiEvent
}
