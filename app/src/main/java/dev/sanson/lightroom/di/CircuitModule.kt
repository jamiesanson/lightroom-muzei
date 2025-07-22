// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CircuitModule {
    @Provides
    fun provideCircuit(
        presenterFactories: @JvmSuppressWildcards Set<Presenter.Factory>,
        uiFactories: @JvmSuppressWildcards Set<Ui.Factory>,
    ): Circuit =
        Circuit
            .Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()
}
