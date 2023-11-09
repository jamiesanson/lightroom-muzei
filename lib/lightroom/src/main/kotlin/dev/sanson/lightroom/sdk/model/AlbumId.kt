package dev.sanson.lightroom.sdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@JvmInline
value class AlbumId(override val id: String) : Parcelable, AlbumTreeItemId
