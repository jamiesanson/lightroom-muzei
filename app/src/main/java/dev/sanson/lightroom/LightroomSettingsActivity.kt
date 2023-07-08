package dev.sanson.lightroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.android.WithNewIntent
import dev.sanson.lightroom.ui.signin.SignInScreen
import dev.sanson.lightroom.ui.signin.rememberSignInState

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    // TODO("Add viewmodel compose dependency")
    val viewModel: LightroomSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WithNewIntent(activity = this) {
                App(viewModel = viewModel)
            }
        }
    }

    @Composable
    fun App(viewModel: LightroomSettingsViewModel) {
        val state = rememberSignInState(lightroom = viewModel.lightroom)

        AnimatedContent(targetState = state.isSignedIn, label = "Main Content") { signedIn ->
            when (signedIn) {
                true -> SignedIn()
                false -> SignInScreen(
                    onSignIn = state::signIn,
                )
                null -> Loading()
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