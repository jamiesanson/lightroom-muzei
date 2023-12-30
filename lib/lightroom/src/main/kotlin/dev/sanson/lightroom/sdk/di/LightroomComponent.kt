// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.di

import dagger.BindsInstance
import dagger.Component
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.backend.LightroomModule
import dev.sanson.lightroom.sdk.backend.ServiceModule
import dev.sanson.lightroom.sdk.backend.auth.AuthModule
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AuthModule::class,
        LightroomModule::class,
        ServiceModule::class,
    ],
)
internal interface LightroomComponent {
    fun lightroom(): Lightroom

    @Component.Builder
    interface Builder {
        fun filesDir(
            @BindsInstance @FilesDir filesDir: File,
        ): Builder

        fun coroutineScope(
            @BindsInstance scope: CoroutineScope,
        ): Builder

        fun verboseLogging(
            @BindsInstance @VerboseLogging verboseLogging: Boolean,
        ): Builder

        fun build(): LightroomComponent
    }
}
