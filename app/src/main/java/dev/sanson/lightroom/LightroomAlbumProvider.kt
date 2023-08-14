package dev.sanson.lightroom

import androidx.datastore.core.DataStore
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.AlbumId
import java.io.InputStream

class LightroomAlbumProvider : MuzeiArtProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LightroomAlbumProviderEntryPoint {
        val lightroom: Lightroom
        val albumIdStore: DataStore<AlbumId?>
    }

    override fun onLoadRequested(initial: Boolean) {
        val entryPoint = EntryPointAccessors.fromApplication<LightroomAlbumProviderEntryPoint>(
            requireNotNull(context).applicationContext,
        )

        val lightroom = entryPoint.lightroom

        if (initial) {
            // TODO: Kick off a workmanager job to load assets
        } else {
            // TODO: Grab all artwork added, query album to see if there's any more
        }
    }

    // TODO: Check if rendition exists for artwork
    override fun isArtworkValid(artwork: Artwork): Boolean {
        return super.isArtworkValid(artwork)
    }

    // TODO: Take persistent URI from artwork, use headers, generate rendition if needed
    override fun openFile(artwork: Artwork): InputStream {
        return super.openFile(artwork)
    }
}
