// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search

import dev.sanson.lightroom.sdk.backend.auth.Credential
import dev.sanson.lightroom.sdk.backend.auth.CredentialStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class InMemoryCredentialStore(credential: Credential) : CredentialStore {
    override val credential: Flow<Credential?> = MutableStateFlow(credential)

    override suspend fun updateTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        (credential as MutableStateFlow).update {
            Credential(accessToken, refreshToken)
        }
    }
}
