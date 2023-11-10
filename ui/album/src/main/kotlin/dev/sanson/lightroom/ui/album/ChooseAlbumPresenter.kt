package dev.sanson.lightroom.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.Config
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.screens.ChooseAlbumScreen
import dev.sanson.lightroom.screens.FilterAssetsScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem
import dev.sanson.lightroom.sdk.model.CollectionSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChooseAlbumPresenter
    @AssistedInject
    constructor(
        @Assisted private val navigator: Navigator,
        private val lightroom: Lightroom,
        private val configRepository: ConfigRepository,
    ) : Presenter<ChooseAlbumState> {
        @Composable
        override fun present(): ChooseAlbumState {
            val scope = rememberCoroutineScope()

            val albumState by produceState<List<AlbumTreeItem>?>(
                initialValue = null,
                lightroom,
            ) {
                value =
                    lightroom.getAlbums().sortedBy {
                        when (it) {
                            is Album -> 1
                            is CollectionSet -> 0
                        }
                    }
            }

            val albumId by produceState<AlbumId?>(initialValue = null, configRepository) {
                configRepository.config
                    .map { it?.source as? Config.Source.Album }
                    .map { it?.id }
                    .collect { value = it }
            }

            return when (val albums = albumState) {
                null ->
                    ChooseAlbumState.Loading

                else ->
                    ChooseAlbumState.Loaded(
                        albumTree = albums,
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
