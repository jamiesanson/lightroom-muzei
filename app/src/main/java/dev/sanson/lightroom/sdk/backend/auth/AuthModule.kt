package dev.sanson.lightroom.sdk.backend.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.BuildConfig
import dev.sanson.lightroom.di.ApplicationScope
import dev.sanson.lightroom.sdk.backend.auth.api.LightroomAuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class LoginHost

private const val ADOBE_LOGIN_HOST = "https://ims-na1.adobelogin.com"

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun provideCredentialStore(dataStore: DataStore<Credential?>): CredentialStore {
        return DefaultCredentialStore(dataStore)
    }

    @Provides
    @Singleton
    fun provideCredentialDataStore(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext context: Context,
    ): DataStore<Credential?> {
        return DataStoreFactory.create(
            serializer = Credential.Serializer,
            scope = scope,
            produceFile = {
                File("${context.filesDir.path}/credentials")
            },
        )
    }

    @Provides
    @LoginHost
    fun provideLoginHost(): String = ADOBE_LOGIN_HOST

    @Provides
    fun provideAuthService(
        @LoginHost loginHost: String,
        json: Json,
    ): LightroomAuthService {
        return Retrofit.Builder().client(
            OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
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
