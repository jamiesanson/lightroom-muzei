package dev.sanson.lightroom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuitx.android.AndroidScreenStarter
import com.slack.circuitx.android.IntentScreen
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.circuit.FinishActivityScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.ui.signin.SignInScreen
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import javax.inject.Inject

@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    @Inject
    lateinit var lightroom: Lightroom

    @Inject
    lateinit var circuit: Circuit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                CircuitCompositionLocals(circuit = circuit) {
                    NavigableCircuitContent(
                        navigator = navigator,
                        backstack = backstack,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let(lightroom::handleSignInResponse)
    }
}
