package dev.sanson.lightroom.sdk.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.backend.LightroomModule
import dev.sanson.lightroom.sdk.backend.ServiceModule
import dev.sanson.lightroom.sdk.backend.auth.AuthModule
import kotlinx.coroutines.CoroutineScope

@Component(
    modules = [
        AuthModule::class,
        LightroomModule::class,
        ServiceModule::class,
    ],
)
interface LightroomComponent {
    fun lightroom(): Lightroom

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun coroutineScope(@BindsInstance scope: CoroutineScope): Builder
        fun build(): LightroomComponent
    }
}

