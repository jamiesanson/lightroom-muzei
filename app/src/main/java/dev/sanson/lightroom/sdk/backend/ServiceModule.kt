package dev.sanson.lightroom.sdk.backend

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Qualifier

@Qualifier
annotation class LightroomRetrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    fun provideAccountService(@LightroomRetrofit retrofit: Retrofit): AccountService =
        retrofit.create()

    @Provides
    fun provideAlbumService(@LightroomRetrofit retrofit: Retrofit): AlbumService = retrofit.create()

    @Provides
    fun provideAssetsService(@LightroomRetrofit retrofit: Retrofit): AssetsService =
        retrofit.create()

    @Provides
    fun provideCatalogService(@LightroomRetrofit retrofit: Retrofit): CatalogService =
        retrofit.create()
}
