package dev.sanson.unsplash.api

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
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

        operator fun invoke(context: Context): UnsplashService {
            val json = Json {
                ignoreUnknownKeys = true
            }

            val consumerKey = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                .metaData
                .getString("dev.sanson.unsplash.consumerkey", "")

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", "Client-ID $consumerKey")
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
