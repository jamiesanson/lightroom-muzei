package dev.sanson.lightroom.ui.source

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.config.Config
import dev.sanson.lightroom.sdk.Lightroom
import javax.inject.Inject

class ChooseSourcePresenterFactory @Inject constructor(
    private val factory: ChooseSourcePresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is ChooseSourceScreen -> factory.create(navigator)
            else -> null
        }
    }
}

class ChooseSourcePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val lightroom: Lightroom,
    private val filterStore: DataStore<Config?>,
) : Presenter<ChooseSourceScreen.State> {
    @Composable
    override fun present(): ChooseSourceScreen.State {
        return ChooseSourceScreen.State(
            eventSink = {
            },
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ChooseSourcePresenter
    }
}
