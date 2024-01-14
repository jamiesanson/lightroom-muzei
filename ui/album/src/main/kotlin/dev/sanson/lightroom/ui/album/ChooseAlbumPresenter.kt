// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.core.search.SearchConfig
import dev.sanson.lightroom.screens.ChooseAlbumScreen
import dev.sanson.lightroom.screens.FilterAssetsScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem
import dev.sanson.lightroom.sdk.model.CollectionSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChooseAlbumPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val lightroom: Lightroom,
    private val configRepository: ConfigRepository,
) : Presenter<ChooseAlbumState> {
    @Composable
    override fun present(): ChooseAlbumState {
        val scope = rememberCoroutineScope()

        fun CollectionSet.sortChildren(): CollectionSet =
            copy(
                children =
                    children
                        .sortedBy {
                            when (it) {
                                is Album -> 1
                                is CollectionSet -> 0
                            }
                        }
                        .map { if (it is CollectionSet) it.sortChildren() else it },
            )

        var albums by rememberSaveable {
            mutableStateOf<List<AlbumTreeItem>?>(null)
        }

        LaunchedEffect(true) {
            albums =
                lightroom.getAlbums()
                    .sortedBy {
                        when (it) {
                            is Album -> 1
                            is CollectionSet -> 0
                        }
                    }
                    .map { if (it is CollectionSet) it.sortChildren() else it }
        }

        val albumId by produceState<AlbumId?>(initialValue = null, configRepository) {
            configRepository.searchConfig
                .map { it?.source as? SearchConfig.Source.Album }
                .map { it?.id }
                .collect { value = it }
        }

        return when (val albumTree = albums) {
            null ->
                ChooseAlbumState.Loading

            else ->
                ChooseAlbumState.Loaded(
                    albumTree = albumTree,
                    selectedAlbum = albumId,
                    eventSink = { event ->
                        when (event) {
                            is ChooseAlbumEvent.SelectAlbum ->
                                scope.launch {
                                    configRepository.setAlbum(event.albumId)
                                }

                            is ChooseAlbumEvent.Confirm ->
                                navigator.goTo(FilterAssetsScreen)
                        }
                    },
                )
        }
    }

    @CircuitInject(ChooseAlbumScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ChooseAlbumPresenter
    }
}
