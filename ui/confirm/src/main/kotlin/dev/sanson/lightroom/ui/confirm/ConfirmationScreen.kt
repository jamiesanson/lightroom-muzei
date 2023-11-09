package dev.sanson.lightroom.ui.confirm

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.AssetId
import kotlinx.datetime.Instant

sealed interface ConfirmState : CircuitUiState {
    data object LoadingArtwork : ConfirmState

    data class LoadingFirstImage(
        val artwork: List<Asset>,
    ) : ConfirmState

    data class Loaded(
        val firstWallpaper: Asset,
        val firstArtworkCaptureDate: Instant,
        val artwork: List<Asset>,
        val eventSink: (ConfirmEvent) -> Unit,
    ) : ConfirmState
}

sealed interface ConfirmEvent : CircuitUiEvent {

    data object OnFinish : ConfirmEvent
}
