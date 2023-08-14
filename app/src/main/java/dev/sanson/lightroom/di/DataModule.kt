package dev.sanson.lightroom.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.data.AlbumIdSerializer
import dev.sanson.lightroom.sdk.model.AlbumId
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideAlbumIdStore(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext context: Context,
    ): DataStore<AlbumId?> {
        return DataStoreFactory.create(
            serializer = AlbumIdSerializer,
            scope = scope,
            produceFile = { File(context.filesDir, "data/album_id") },
        )
    }
}
