package dev.sanson.lightroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.android.LocalNewIntent
import dev.sanson.lightroom.android.WithNewIntent
import dev.sanson.lightroom.arch.Success
import dev.sanson.lightroom.ui.signin.SignInScreen

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LightroomSettingsViewModel by hiltViewModel()

            WithNewIntent(activity = this) {
                SettingsRoute(viewModel = viewModel)
            }
        }
    }

    @Composable
    fun SettingsRoute(viewModel: LightroomSettingsViewModel) {
        val state by viewModel.store.state.collectAsState()
        val context = LocalContext.current
        val nextIntent = LocalNewIntent.current

        LaunchedEffect(true) {
            nextIntent.next.collect { viewModel.onCompleteSignIn(it) }
        }

        Settings(
            state = state,
            onSignIn = { viewModel.signIn(context) }
        )
    }

    @Composable
    fun Settings(
        state: SettingsState,
        onSignIn: () -> Unit,
    ) {
        AnimatedContent(targetState = state.isSignedIn, label = "Settings") { signedIn ->
            when (signedIn) {
                is Success -> when (signedIn.value) {
                    true -> SignedIn()
                    false -> SignInScreen(
                        onSignIn = onSignIn,
                    )
                }
                else -> Loading()
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun SignedIn() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            var name: String? by remember { mutableStateOf(null) }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = name,
                    label = "Content",
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    if (it == null) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Hello, $it",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Loading() {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}