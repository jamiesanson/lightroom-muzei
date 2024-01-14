// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class CatalogId(val id: String)
