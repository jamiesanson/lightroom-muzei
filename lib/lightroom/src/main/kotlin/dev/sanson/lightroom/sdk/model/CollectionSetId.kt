// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.model

@JvmInline
value class CollectionSetId(
    override val id: String,
) : AlbumTreeItemId
