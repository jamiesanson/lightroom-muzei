// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.auth

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
internal data class Credential(
    val accessToken: String,
    val refreshToken: String,
)

internal interface CredentialStore {
    val credential: Flow<Credential?>

    suspend fun updateTokens(
        accessToken: String,
        refreshToken: String,
    )
}

internal class DefaultCredentialStore(
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
