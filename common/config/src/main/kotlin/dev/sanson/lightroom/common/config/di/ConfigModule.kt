// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.common.config.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.common.config.DefaultConfigRepository
import dev.sanson.lightroom.common.di.ApplicationScope
import dev.sanson.lightroom.core.data.JsonSerializer
import dev.sanson.lightroom.lib.search.SearchConfig
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ConfigModule {
    @Provides
    @Singleton
    fun provideConfigStore(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext context: Context,
    ): DataStore<SearchConfig?> {
        return DataStoreFactory.create(
            serializer = JsonSerializer<SearchConfig>(),
            scope = scope,
            produceFile = {
                File("${context.filesDir.path}/config")
            },
        )
    }

    @Provides
    @Singleton
    fun provideConfigRepository(store: DataStore<SearchConfig?>): ConfigRepository = DefaultConfigRepository(store)
}
