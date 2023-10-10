package dev.sanson.lightroom.ui.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.ui.signin.SignInScreen.Event.SignInWithLightroom
import dev.sanson.lightroom.ui.signin.SignInScreen.State.Loading
import dev.sanson.lightroom.ui.signin.SignInScreen.State.NotSignedIn
import dev.sanson.lightroom.ui.source.ChooseSourceScreen
import javax.inject.Inject

// TODO: There's a significant amount of boilerplate that comes with wiring up a screen, when
//       not using anvil. Live templates/file templates would solve this, given it's repetitive
class SignInPresenterFactory @Inject constructor(
    private val factory: SignInPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is SignInScreen -> factory.create(navigator)
            else -> null
        }
    }
}

class SignInPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val lightroom: Lightroom,
) : Presenter<SignInScreen.State> {

    @Composable
    override fun present(): SignInScreen.State {
        val isSignedIn by lightroom.isSignedIn.collectAsState(initial = null)
        val context = LocalContext.current

        // TODO: Handle errors and retry
        var signInRequested by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(isSignedIn) {
            if (isSignedIn == true) {
                navigator.resetRoot(ChooseSourceScreen)
            }
        }

        return if (signInRequested || isSignedIn != false) {
            Loading
        } else {
            NotSignedIn(
                eventSink = { event ->
                    when (event) {
                        SignInWithLightroom -> {
                            signInRequested = true
                            lightroom.signIn(context)
                        }
                    }
                },
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SignInPresenter
    }
}
