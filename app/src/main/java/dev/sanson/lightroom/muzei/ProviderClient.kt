package dev.sanson.lightroom.muzei

import android.content.ContentResolver
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderClient

fun ProviderClient.getArtwork(contentResolver: ContentResolver): List<Artwork> {
    return contentResolver.query(contentUri, null, null, null, null)
        ?.use { cursor ->
            cursor.moveToFirst()

            buildList {
                while (!cursor.isAfterLast) {
                    add(Artwork.fromCursor(cursor))

                    cursor.moveToNext()
                }
            }
        } ?: emptyList()
}
