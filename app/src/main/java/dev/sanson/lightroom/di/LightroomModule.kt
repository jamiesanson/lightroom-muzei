// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.di.ApplicationScope
import dev.sanson.lightroom.sdk.Lightroom
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LightroomModule {
    @Provides
    @Singleton
    fun provideLightroom(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
    ): Lightroom = Lightroom(context, scope)
}
