// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.backend

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.search.api.SearchService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BackendModule {
    @Provides
    @Singleton
    fun provideSearchService(): SearchService {
        TODO("Provide a search service")
    }
}
