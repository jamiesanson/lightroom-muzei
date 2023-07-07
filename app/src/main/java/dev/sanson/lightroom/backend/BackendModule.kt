package dev.sanson.lightroom.backend

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
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
}