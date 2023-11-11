package dev.sanson.lightroom.ui.confirm

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.datetime.Instant

sealed interface ConfirmState : CircuitUiState {
    val stepNumber: Int

    data class LoadingArtwork(
        override val stepNumber: Int,
    ) : ConfirmState

    data class LoadingFirstImage(
        override val stepNumber: Int,
        val artwork: List<Asset>,
    ) : ConfirmState

    data class Loaded(
        override val stepNumber: Int,
        val firstWallpaper: Asset,
        val firstArtworkCaptureDate: Instant,
        val artwork: List<Asset>,
        val eventSink: (ConfirmEvent) -> Unit,
    ) : ConfirmState
}

sealed interface ConfirmEvent : CircuitUiEvent {
    data object OnFinish : ConfirmEvent
}
