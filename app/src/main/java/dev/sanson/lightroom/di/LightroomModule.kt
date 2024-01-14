// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.BuildConfig
import dev.sanson.lightroom.common.di.ApplicationScope
import dev.sanson.lightroom.core.data.JsonSerializer
import dev.sanson.lightroom.credentials.DataStoreCredentialStore
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.backend.auth.Credential
import dev.sanson.lightroom.sdk.backend.auth.CredentialStore
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LightroomModule {
    @Provides
    @Singleton
    fun provideLightroom(
        credentialStore: CredentialStore,
        @ApplicationScope scope: CoroutineScope,
    ): Lightroom =
        Lightroom(
            credentialStore = credentialStore,
            coroutineScope = scope,
            verbose = BuildConfig.DEBUG,
        )

    @Provides
    @Singleton
    fun provideCredentialStore(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
    ): CredentialStore {
        val dataStore =
            DataStoreFactory.create(
                serializer = JsonSerializer<Credential>(),
                scope = scope,
                produceFile = {
                    File("${context.filesDir.path}/credentials")
                },
            )

        return DataStoreCredentialStore(dataStore)
    }
}
