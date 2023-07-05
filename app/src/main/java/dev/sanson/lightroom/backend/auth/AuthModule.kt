package dev.sanson.lightroom.backend.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    fun provideCredentialStore(): CredentialStore {
        return DefaultCredentialStore()
    }
}