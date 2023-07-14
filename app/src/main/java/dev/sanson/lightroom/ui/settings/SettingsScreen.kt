@file:OptIn(ExperimentalAnimationApi::class)

package dev.sanson.lightroom.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sanson.lightroom.LightroomSettingsViewModel
import dev.sanson.lightroom.SettingsState
import dev.sanson.lightroom.arch.Success
import dev.sanson.lightroom.ui.signin.SignIn


@Composable
fun Settings(viewModel: LightroomSettingsViewModel = hiltViewModel()) {
    val state by viewModel.store.state.collectAsState()

    Settings(state = state)
}

@Composable
fun Settings(
    state: SettingsState,
) {
    AnimatedContent(targetState = state.isSignedIn, label = "Settings") { signedIn ->
        when (signedIn) {
            is Success -> when (signedIn.value) {
                true -> SignedIn()
                false -> SignIn()
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