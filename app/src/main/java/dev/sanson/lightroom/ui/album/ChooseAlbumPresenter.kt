package dev.sanson.lightroom.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.config.Config
import dev.sanson.lightroom.data.config.ConfigRepository
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AlbumTreeItem
import dev.sanson.lightroom.sdk.model.CollectionSet
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.Event.Confirm
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.Event.SelectAlbum
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.State.Loaded
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.State.Loading
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChooseAlbumPresenterFactory @Inject constructor(
    private val factory: ChooseAlbumPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is ChooseAlbumScreen -> factory.create(navigator)
            else -> null
        }
    }
}

class ChooseAlbumPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val lightroom: Lightroom,
    private val configRepository: ConfigRepository,
) : Presenter<ChooseAlbumScreen.State> {
    @Composable
    override fun present(): ChooseAlbumScreen.State {
        val scope = rememberCoroutineScope()

        val albumState by produceState<List<AlbumTreeItem>?>(
            initialValue = null,
            lightroom,
        ) {
            value = lightroom.getAlbums().sortedBy {
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
                Loading

            else -> Loaded(
                albumTree = albums,
                selectedAlbum = albumId,
                eventSink = { event ->
                    when (event) {
                        is SelectAlbum ->
                            scope.launch {
                                configRepository.setAlbum(event.albumId)
                            }

                        is Confirm ->
                            navigator.goTo(FilterAssetsScreen)
                    }
                },
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ChooseAlbumPresenter
    }
}
