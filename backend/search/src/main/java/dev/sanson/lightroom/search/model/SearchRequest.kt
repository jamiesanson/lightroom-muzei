// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search.model

import dev.sanson.lightroom.core.search.SearchConfig
import dev.sanson.lightroom.sdk.backend.auth.Credential
import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val credential: Credential,
    val searchConfig: SearchConfig,
)
