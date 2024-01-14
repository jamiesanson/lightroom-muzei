// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.core.search.Config
import dev.sanson.lightroom.screens.ChooseAlbumScreen
import dev.sanson.lightroom.screens.ChooseSourceScreen
import dev.sanson.lightroom.screens.FilterAssetsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChooseSourcePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val configRepository: ConfigRepository,
) : Presenter<ChooseSourceState> {
    @Composable
    override fun present(): ChooseSourceState {
        val scope = rememberCoroutineScope()

        val persistedSource by produceState<Config.Source>(Config.Source.Album.Uninitialized) {
            value = configRepository.config.map { it?.source }.first()
                ?: Config.Source.Album.Uninitialized
        }

        var selectedSource by remember(persistedSource) { mutableStateOf(persistedSource) }

        return ChooseSourceState(
            selectedSource = selectedSource,
        ) { event ->
            when (event) {
                ChooseSourceEvent.OnChooseAlbum ->
                    selectedSource = Config.Source.Album.Uninitialized

                ChooseSourceEvent.OnChooseCatalog ->
                    selectedSource = Config.Source.Catalog

                ChooseSourceEvent.OnConfirm ->
                    scope.launch {
                        configRepository.setImageSource(selectedSource)
                        navigator.goTo(
                            screen =
                                when (selectedSource) {
                                    is Config.Source.Album -> ChooseAlbumScreen
                                    is Config.Source.Catalog -> FilterAssetsScreen
                                },
                        )
                    }
            }
        }
    }

    @CircuitInject(ChooseSourceScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ChooseSourcePresenter
    }
}
