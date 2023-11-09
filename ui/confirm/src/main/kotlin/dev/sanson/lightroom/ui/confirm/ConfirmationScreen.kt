package dev.sanson.lightroom.ui.confirm

import com.google.android.apps.muzei.api.provider.Artwork
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.sdk.model.AssetId

sealed interface ConfirmState : CircuitUiState {
    data object LoadingArtwork : ConfirmState

    data class LoadingFirstImage(
        val artwork: List<Artwork>,
    ) : ConfirmState

    data class Loaded(
        val firstWallpaperId: AssetId,
        val firstArtworkCaptureDate: String,
        val artwork: List<Artwork>,
        val eventSink: (ConfirmEvent) -> Unit,
    ) : ConfirmState
}

sealed interface ConfirmEvent : CircuitUiEvent {

    data object OnFinish : ConfirmEvent
}
