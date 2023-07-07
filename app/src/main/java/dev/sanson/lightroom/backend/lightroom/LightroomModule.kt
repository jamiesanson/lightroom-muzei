package dev.sanson.lightroom.backend.lightroom

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier

@Qualifier
annotation class LightroomRetrofit

@Module
@InstallIn(SingletonComponent::class)
class LightroomModule {

    @Provides
    fun provideOkHttp(
        interceptors: Set<@JvmSuppressWildcards Interceptor>,
        authenticator: Authenticator,
    ): OkHttpClient {
        val builder =  OkHttpClient.Builder()

        interceptors.forEach {
            builder.addInterceptor(it)
        }

        builder.authenticator(authenticator)

        return builder.build()
    }

    @Provides
    @LightroomRetrofit
    fun provideLightroomRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lr.adobe.io")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}