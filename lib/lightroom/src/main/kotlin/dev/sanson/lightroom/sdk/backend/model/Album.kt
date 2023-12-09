// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Album(
    val name: String,
    val parent: Asset? = null,
    val cover: Asset? = null,
)
