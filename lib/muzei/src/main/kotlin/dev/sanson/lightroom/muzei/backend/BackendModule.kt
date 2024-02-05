// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.backend

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.search.api.SearchService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
internal annotation class AppBackendRetrofit

@Module
@InstallIn(SingletonComponent::class)
internal class BackendModule {
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    @Provides
    @IntoSet
    fun provideLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @IntoSet
    fun provideAuthInterceptor(lightroom: Lightroom): Interceptor {
        return BackendAuthInterceptor(lightroom)
    }

    @Provides
    @IntoSet
    fun provideApiKeyInterceptor(): Interceptor {
        return ApiKeyInterceptor()
    }

    @Provides
    @IntoSet
    fun provideAndroidClientInterceptor(
        @ApplicationContext context: Context,
    ): Interceptor {
        return AndroidClientInterceptor(context)
    }

    @Provides
    fun provideOkHttp(interceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient {
        val builder = OkHttpClient.Builder()

        interceptors.forEach {
            builder.addInterceptor(it)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    @AppBackendRetrofit
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.lightroom.sanson.dev")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideSearchService(
        @AppBackendRetrofit retrofit: Retrofit,
    ): SearchService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    internal fun provideBackendSearchUseCase(searchService: SearchService): BackendSearchUseCase {
        return BackendSearchUseCase(searchService)
    }
}
