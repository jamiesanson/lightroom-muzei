// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.model

/**
 * An album tree item is an item found in the album folder structure - either a collection, or
 * collection_set.
 */
sealed interface AlbumTreeItem {
    val id: AlbumTreeItemId
    val catalogId: CatalogId
    val name: String
}

interface AlbumTreeItemId {
    val id: String
}
