// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.model

data class CollectionSet(
    override val id: CollectionSetId,
    override val catalogId: CatalogId,
    override val name: String,
    val children: List<AlbumTreeItem>,
) : AlbumTreeItem
