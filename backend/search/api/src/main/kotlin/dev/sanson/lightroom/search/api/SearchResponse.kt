// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search.api

import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val assets: List<Asset>,
)
