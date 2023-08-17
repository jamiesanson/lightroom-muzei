package dev.sanson.lightroom.unsplash.api

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {

    @GET("photos/random")
    suspend fun getRandomPhoto(
        @Query("collections") collections: String = "textures-patterns",
    ): Photo
}

@Serializable
data class Photo(
    val urls: Urls,
    val user: User,
)

@Serializable
data class Urls(
    val regular: String,
)

@Serializable
data class User(
    val name: String,
    val username: String,
)
