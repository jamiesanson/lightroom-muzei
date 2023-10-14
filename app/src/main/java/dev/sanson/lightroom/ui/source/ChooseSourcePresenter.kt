package dev.sanson.lightroom.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.data.config.ConfigRepository
import dev.sanson.lightroom.ui.album.ChooseAlbumScreen
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnChooseAlbum
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnChooseCatalog
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.State
import kotlinx.coroutines.launch

class ChooseSourcePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val configRepository: ConfigRepository,
) : Presenter<State> {
    @Composable
    override fun present(): State {
        val scope = rememberCoroutineScope()

        return State(
            eventSink = { event ->
                when (event) {
                    OnChooseAlbum ->
                        navigator.goTo(ChooseAlbumScreen)

                    OnChooseCatalog ->
                        scope.launch {
                            configRepository.setUseCatalog()
                            navigator.goTo(FilterAssetsScreen)
                        }
                }
            },
        )
    }

    @CircuitInject(ChooseSourceScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ChooseSourcePresenter
    }
}
