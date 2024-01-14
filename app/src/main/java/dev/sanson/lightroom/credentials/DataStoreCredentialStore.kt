// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.credentials

import androidx.datastore.core.DataStore
import dev.sanson.lightroom.sdk.backend.auth.Credential
import dev.sanson.lightroom.sdk.backend.auth.CredentialStore
import kotlinx.coroutines.flow.Flow

internal class DataStoreCredentialStore(
    private val dataStore: DataStore<Credential?>,
) : CredentialStore {
    override val credential: Flow<Credential?> = dataStore.data

    override suspend fun updateTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.updateData {
            Credential(accessToken = accessToken, refreshToken = refreshToken)
        }
    }
}
