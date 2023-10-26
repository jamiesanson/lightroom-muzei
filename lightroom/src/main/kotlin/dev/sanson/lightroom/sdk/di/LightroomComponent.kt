package dev.sanson.lightroom.sdk.di

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@LightroomScoped
@DefineComponent(parent = SingletonComponent::class)
internal interface LightroomComponent