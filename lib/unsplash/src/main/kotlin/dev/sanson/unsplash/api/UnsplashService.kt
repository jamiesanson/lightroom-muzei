package dev.sanson.unsplash.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

internal interface UnsplashService {

    @GET("photos/random")
    suspend fun getRandomPhoto(
        @Query("collections") collections: String = "textures-patterns",
    ): Photo

    companion object {

        private const val CONSUMER_KEY = "TeCwVYGHNALiUO_ZYQRsH1PsmLtvXcxUAEWtd27tkVo"

        operator fun invoke(): UnsplashService {
            val json = Json {
                ignoreUnknownKeys = true
            }

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
}

@Serializable
internal data class Photo(
    val urls: Urls,
    val user: User,
)

@Serializable
internal data class Urls(
    val regular: String,
)

@Serializable
internal data class User(
    val name: String,
    val username: String,
)
