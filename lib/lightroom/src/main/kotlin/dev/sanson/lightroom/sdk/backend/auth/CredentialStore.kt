// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class Credential(
    val accessToken: String,
    val refreshToken: String,
)

interface CredentialStore {
    val credential: Flow<Credential?>

    suspend fun updateTokens(
        accessToken: String,
        refreshToken: String,
    )
}
