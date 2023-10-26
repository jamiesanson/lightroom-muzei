package dev.sanson.lightroom.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.sdk.Lightroom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LightroomModule {

    @Provides
    @Singleton
    fun provideLightroom(@ApplicationContext context: Context): Lightroom = Lightroom(context)
}
