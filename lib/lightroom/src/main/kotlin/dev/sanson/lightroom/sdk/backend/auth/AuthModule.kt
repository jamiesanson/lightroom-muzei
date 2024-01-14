// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.auth

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dev.sanson.lightroom.sdk.backend.auth.api.LightroomAuthService
import dev.sanson.lightroom.sdk.di.VerboseLogging
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Qualifier

@Qualifier
internal annotation class LoginHost

private const val ADOBE_LOGIN_HOST = "https://ims-na1.adobelogin.com"

@Module
internal class AuthModule {
    @Provides
    @LoginHost
    fun provideLoginHost(): String = ADOBE_LOGIN_HOST

    @Provides
    fun provideAuthService(
        @LoginHost loginHost: String,
        @VerboseLogging verboseLogging: Boolean,
        json: Json,
    ): LightroomAuthService {
        return Retrofit.Builder().client(
            OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (verboseLogging) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).build(),
        ).baseUrl(loginHost)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
            .create<LightroomAuthService>()
    }
}
