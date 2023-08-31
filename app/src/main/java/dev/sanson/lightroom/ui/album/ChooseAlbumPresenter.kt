package dev.sanson.lightroom.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.filter.Filter
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.Event.Confirm
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.Event.SelectAlbum
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.State.Loaded
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen.State.Loading
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen
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
    private val filterStore: DataStore<Filter?>,
) : Presenter<ChooseAlbumScreen.State> {
    @Composable
    override fun present(): ChooseAlbumScreen.State {
        val scope = rememberCoroutineScope()

        val albumState by produceState<List<Album>?>(initialValue = null, lightroom) {
            value = lightroom.getAlbums()
        }

        val filter by filterStore.data.collectAsState(initial = null)

        return when (val albums = albumState) {
            null ->
                Loading

            else -> Loaded(
                albums = albums,
                selectedAlbum = filter?.albumId,
                eventSink = { event ->
                    when (event) {
                        is SelectAlbum ->
                            scope.launch {
                                filterStore.updateData { Filter(albumId = event.albumId) }
                            }

                        is Confirm ->
                            navigator.goTo(FilterAssetsScreen(albumId = requireNotNull(filter).albumId))
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
