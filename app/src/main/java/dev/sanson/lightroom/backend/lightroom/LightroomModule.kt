package dev.sanson.lightroom.backend.lightroom

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.backend.interceptor.RemoveBodyPrefixInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
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

    @Provides
    fun provideAccountService(@LightroomRetrofit retrofit: Retrofit): AccountService = retrofit.create()
}