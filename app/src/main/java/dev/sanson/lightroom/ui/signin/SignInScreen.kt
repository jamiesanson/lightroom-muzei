package dev.sanson.lightroom.ui.signin

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object SignInScreen : Screen {

    sealed interface State : CircuitUiState {
        data object Loading : State

        data class NotSignedIn(
            val eventSink: (Event) -> Unit,
        ) : State
    }

    sealed interface Event : CircuitUiEvent {
        data object SignInWithLightroom : Event
    }
}
