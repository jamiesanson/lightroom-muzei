package dev.sanson.lightroom.unsplash.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

// TODO: Perhaps obscure this by removing references from source
private const val CONSUMER_KEY = "TeCwVYGHNALiUO_ZYQRsH1PsmLtvXcxUAEWtd27tkVo"

@Module
@InstallIn(SingletonComponent::class)
class UnsplashModule {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideUnsplashService(): UnsplashService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Client-ID $CONSUMER_KEY")
                    .build()

                chain.proceed(request)
            }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create()
    }
}
