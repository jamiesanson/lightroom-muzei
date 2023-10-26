package dev.sanson.lightroom.ui.confirmation

import com.google.android.apps.muzei.api.provider.Artwork
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.sanson.lightroom.sdk.model.AssetId
import kotlinx.parcelize.Parcelize

@Parcelize
data object ConfirmationScreen : Screen {

    sealed interface State : CircuitUiState {
        data object LoadingArtwork : State

        data class LoadingFirstImage(
            val artwork: List<Artwork>,
        ) : State

        data class Loaded(
            val firstWallpaperId: AssetId,
            val firstArtworkCaptureDate: String,
            val artwork: List<Artwork>,
            val eventSink: (Event) -> Unit,
        ) : State
    }

    sealed interface Event : CircuitUiEvent {

        data object OnFinish : Event
    }
}
