package dev.sanson.lightroom.ui.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.screens.ChooseSourceScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.ui.signin.SignInScreen.Event.SignInWithLightroom
import dev.sanson.lightroom.ui.signin.SignInScreen.State.Loading
import dev.sanson.lightroom.ui.signin.SignInScreen.State.NotSignedIn

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

    @CircuitInject(SignInScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SignInPresenter
    }
}
