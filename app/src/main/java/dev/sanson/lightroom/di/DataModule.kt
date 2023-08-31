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
import dev.sanson.lightroom.data.filter.DefaultFilterRepository
import dev.sanson.lightroom.data.filter.Filter
import dev.sanson.lightroom.data.filter.FilterRepository
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideFilterStore(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext context: Context,
    ): DataStore<Filter?> {
        return DataStoreFactory.create(
            serializer = JsonSerializer<Filter>(),
            scope = scope,
            produceFile = {
                File("${context.filesDir.path}/filters")
            },
        )
    }

    @Provides
    @Singleton
    fun provideFilterRepository(
        store: DataStore<Filter?>,
    ): FilterRepository = DefaultFilterRepository(store)
}
