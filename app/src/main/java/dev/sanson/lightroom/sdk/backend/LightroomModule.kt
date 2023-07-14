package dev.sanson.lightroom.sdk.backend

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.sanson.lightroom.BuildConfig
import dev.sanson.lightroom.sdk.backend.auth.AuthManager
import dev.sanson.lightroom.sdk.backend.auth.CredentialStore
import dev.sanson.lightroom.sdk.backend.interceptor.AuthInterceptor
import dev.sanson.lightroom.sdk.backend.interceptor.ClientIdInterceptor
import dev.sanson.lightroom.sdk.backend.interceptor.LightroomAuthenticator
import dev.sanson.lightroom.sdk.backend.interceptor.RemoveBodyPrefixInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier


@Qualifier
annotation class LightroomClientId

private const val LIGHTROOM_CLIENT_ID = "4a1404eeb6b442278a96dab428ecbc43"

@Module
@InstallIn(SingletonComponent::class)
class LightroomModule {

    @Provides
    @LightroomClientId
    fun provideLightroomClientId() = LIGHTROOM_CLIENT_ID

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    fun provideLightroomAuthenticator(
        authManager: AuthManager,
    ): Authenticator {
        return LightroomAuthenticator(authManager)
    }

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
        interceptors: Set<@JvmSuppressWildcards Interceptor>,
        authenticator: Authenticator,
    ): OkHttpClient {
        val builder =  OkHttpClient.Builder()

        // Ensure we remove the "abuse mitigation" prefix before sending the response to further interceptors
        builder.addNetworkInterceptor(RemoveBodyPrefixInterceptor())

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