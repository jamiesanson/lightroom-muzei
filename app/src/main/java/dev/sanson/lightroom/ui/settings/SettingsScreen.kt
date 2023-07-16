@file:OptIn(ExperimentalAnimationApi::class)

package dev.sanson.lightroom.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.sanson.lightroom.arch.Success
import dev.sanson.lightroom.ui.albums.ChooseAlbum
import dev.sanson.lightroom.ui.signin.SignIn

@Composable
fun Settings(
    viewModel: SettingsViewModel = hiltViewModel(LocalContext.current as ViewModelStoreOwner),
    onAlbumChanged: () -> Unit,
) {
    val state by viewModel.store.state.collectAsState()

    Settings(state = state, onAlbumChanged = onAlbumChanged)
}

@Composable
fun Settings(
    state: SettingsState,
    onAlbumChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = state.isSignedIn,
        label = "Settings",
        modifier = modifier,
    ) { signedIn ->
        when (signedIn) {
            is Success -> when (signedIn.value) {
                true -> ChooseAlbum(
                    onAlbumSelected = onAlbumChanged,
                )

                false -> SignIn()
            }

            else -> Loading()
        }
    }
}

@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}
