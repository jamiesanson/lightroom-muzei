package dev.sanson.lightroom.backend.lightroom

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.sanson.lightroom.BuildConfig
import dev.sanson.lightroom.backend.LightroomClientId
import dev.sanson.lightroom.backend.auth.CredentialStore
import dev.sanson.lightroom.backend.lightroom.interceptor.AuthInterceptor
import dev.sanson.lightroom.backend.lightroom.interceptor.ClientIdInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier

@Qualifier
annotation class LightroomRetrofit

@Module
@InstallIn(SingletonComponent::class)
class LightroomModule {

    @Provides
    @IntoSet
    fun provideClientIdInterceptor(
        @LightroomClientId clientId: String,
    ): Interceptor {
        return ClientIdInterceptor(clientId)
    }

    @Provides
    @IntoSet
    fun provideLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @IntoSet
    fun provideAuthInterceptor(
        credentialStore: CredentialStore,
    ): Interceptor {
        return AuthInterceptor(credentialStore)
    }

    @Provides
    fun provideOkHttp(
        interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        val builder =  OkHttpClient.Builder()

        interceptors.forEach {
            builder.addInterceptor(it)
        }

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