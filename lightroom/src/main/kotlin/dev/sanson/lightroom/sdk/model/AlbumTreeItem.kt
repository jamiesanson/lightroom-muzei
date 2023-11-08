package dev.sanson.lightroom.sdk.model

/**
 * An album tree item is an item found in the album folder structure - either a collection, or
 * collection_set.
 */
sealed interface AlbumTreeItem {
    val id: AlbumTreeItemId
    val name: String
}

interface AlbumTreeItemId {
    val id: String
}
