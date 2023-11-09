package dev.sanson.lightroom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuitx.android.AndroidScreenStarter
import com.slack.circuitx.android.IntentScreen
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.core.ui.MuzeiLightroomTheme
import dev.sanson.lightroom.screens.FinishActivityScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.ui.signin.SignInScreen
import nz.sanson.lightroom.coil.LocalLightroomImageLoader
import nz.sanson.lightroom.coil.createImageLoader
import javax.inject.Inject

@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    @Inject
    lateinit var lightroom: Lightroom

    @Inject
    lateinit var circuit: Circuit

    private val imageLoader by lazy { lightroom.createImageLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val backstack = rememberSaveableBackStack { push(SignInScreen) }
            val navigator = rememberAndroidScreenAwareNavigator(
                delegate = rememberCircuitNavigator(backstack),
                starter = remember {
                    AndroidScreenStarter { screen ->
                        when (screen) {
                            is IntentScreen ->
                                screen.startWith(context = this)

                            is FinishActivityScreen ->
                                finishActivity(screen.requestCode)
                        }
                    }
                },
            )

            MuzeiLightroomTheme {
                MuzeiLightroomCompositionLocals {
                    CircuitCompositionLocals(circuit = circuit) {
                        NavigableCircuitContent(
                            navigator = navigator,
                            backstack = backstack,
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let(lightroom::handleSignInResponse)
    }

    @Composable
    private fun MuzeiLightroomCompositionLocals(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalLightroomImageLoader provides imageLoader, content = content)
    }
}
