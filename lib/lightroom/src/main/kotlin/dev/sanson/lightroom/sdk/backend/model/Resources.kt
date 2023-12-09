// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Resources<T>(
    val resources: List<Resource<T>>,
)
