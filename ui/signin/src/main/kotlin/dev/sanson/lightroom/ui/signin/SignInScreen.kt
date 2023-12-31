// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.signin

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

sealed interface SignInState : CircuitUiState {
    data object Loading : SignInState

    data class NotSignedIn(
        val eventSink: (SignInEvent) -> Unit,
    ) : SignInState
}

sealed interface SignInEvent : CircuitUiEvent {
    data object SignInWithLightroom : SignInEvent
}
