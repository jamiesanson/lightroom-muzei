package dev.sanson.lightroom.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.data.JsonSerializer
import dev.sanson.lightroom.data.config.Config
import dev.sanson.lightroom.data.config.ConfigRepository
import dev.sanson.lightroom.data.config.DefaultConfigRepository
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideConfigStore(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext context: Context,
    ): DataStore<Config?> {
        return DataStoreFactory.create(
            serializer = JsonSerializer<Config>(),
            scope = scope,
            produceFile = {
                File("${context.filesDir.path}/config")
            },
        )
    }

    @Provides
    @Singleton
    fun provideConfigRepository(
        store: DataStore<Config?>,
    ): ConfigRepository = DefaultConfigRepository(store)
}
