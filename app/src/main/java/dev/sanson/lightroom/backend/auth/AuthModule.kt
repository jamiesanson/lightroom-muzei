package dev.sanson.lightroom.backend.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    fun provideCredentialStore(dataStore: DataStore<Credential?>): CredentialStore {
        return DefaultCredentialStore(dataStore)
    }

    @Provides
    fun provideCredentialDataStore(
        @ApplicationScope scope: CoroutineScope,
        @ApplicationContext context: Context
    ): DataStore<Credential?> {
        return DataStoreFactory.create(
            serializer = Credential.Serializer,
            scope = scope,
            produceFile = { File(context.filesDir, "data/credentials") }
        )
    }
}