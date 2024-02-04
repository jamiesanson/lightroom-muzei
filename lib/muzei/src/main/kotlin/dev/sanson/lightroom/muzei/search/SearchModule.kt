// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.search

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.lib.search.SearchUseCase
import dev.sanson.lightroom.lib.search.searchUseCase
import dev.sanson.lightroom.muzei.backend.BackendSearchUseCase
import dev.sanson.lightroom.sdk.Lightroom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchModule {
    @Provides
    @Singleton
    internal fun provideSearchUseCase(
        lightroom: Lightroom,
        backendSearchUseCase: BackendSearchUseCase,
    ): SearchUseCase {
        return SearchUseCase(lightroom.searchUseCase(), backendSearchUseCase)
    }
}
