package dev.sanson.lightroom.ui.confirmation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.config.ConfigRepository
import dev.sanson.lightroom.ui.confirmation.ConfirmationScreen.State
import javax.inject.Inject

class ConfirmationPresenterFactory @Inject constructor(
    private val factory: ConfirmationPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is ConfirmationScreen -> factory.create(navigator)
            else -> null
        }
    }
}

@Suppress("unused") // TODO: Remove
class ConfirmationPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val configRepository: ConfigRepository,
) : Presenter<State> {

    @Suppress("UNUSED_VARIABLE")
    @Composable
    override fun present(): State {
        val scope = rememberCoroutineScope()

        return State(
            eventSink = {
            },
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ConfirmationPresenter
    }
}
