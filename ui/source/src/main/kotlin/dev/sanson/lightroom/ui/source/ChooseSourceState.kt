// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.source

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.common.config.Config

data class ChooseSourceState(
    val selectedSource: Config.Source,
    val eventSink: (ChooseSourceEvent) -> Unit,
) : CircuitUiState

sealed interface ChooseSourceEvent : CircuitUiEvent {
    data object OnChooseCatalog : ChooseSourceEvent

    data object OnChooseAlbum : ChooseSourceEvent

    data object OnConfirm : ChooseSourceEvent
}
