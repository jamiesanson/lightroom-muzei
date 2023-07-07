package dev.sanson.lightroom.backend

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.sanson.lightroom.BuildConfig
import dev.sanson.lightroom.backend.auth.AuthManager
import dev.sanson.lightroom.backend.auth.CredentialStore
import dev.sanson.lightroom.backend.interceptor.AuthInterceptor
import dev.sanson.lightroom.backend.interceptor.ClientIdInterceptor
import dev.sanson.lightroom.backend.interceptor.LightroomAuthenticator
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Qualifier


@Qualifier
annotation class LightroomClientId

private const val LIGHTROOM_CLIENT_ID = "4a1404eeb6b442278a96dab428ecbc43"

@Module
@InstallIn(SingletonComponent::class)
class BackendModule {

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
}