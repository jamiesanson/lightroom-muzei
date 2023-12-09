// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.confirm

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.sdk.model.Asset

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
        val firstWallpaperAge: String,
        val artwork: List<Asset>,
        val eventSink: (ConfirmEvent) -> Unit,
    ) : ConfirmState
}

sealed interface ConfirmEvent : CircuitUiEvent {
    data object OnFinish : ConfirmEvent
}
