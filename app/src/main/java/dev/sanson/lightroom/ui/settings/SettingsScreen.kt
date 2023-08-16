package dev.sanson.lightroom.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.rememberLightroom
import dev.sanson.lightroom.ui.albums.ChooseAlbum
import dev.sanson.lightroom.ui.signin.SignIn
import kotlinx.coroutines.flow.Flow

sealed class SettingsModel {
    object Loading : SettingsModel()

    object SignedOut : SettingsModel()

    class SignedIn : SettingsModel()
}

/**
 * TODO: Test this presenter with Molecule
 */
@Composable
internal fun rememberSettingsModel(
    lightroom: Lightroom = rememberLightroom(),
    isSignedIn: Flow<Boolean> = lightroom.isSignedIn,
): SettingsModel {
    val signedIn by isSignedIn.collectAsState(initial = null)

    return when (signedIn) {
        true -> SettingsModel.SignedIn()
        false -> SettingsModel.SignedOut
        else -> SettingsModel.Loading
    }
}

@Composable
fun Settings(
    onAlbumChanged: () -> Unit,
    modifier: Modifier = Modifier,
    model: SettingsModel = rememberSettingsModel(),
) {
    AnimatedContent(
        targetState = model,
        label = "Settings",
        modifier = modifier,
    ) {
        when (it) {
            SettingsModel.Loading ->
                Loading()

            is SettingsModel.SignedIn ->
                ChooseAlbum(
                    onAlbumSelected = onAlbumChanged,
                )

            SettingsModel.SignedOut ->
                SignIn()
        }
    }
}

// TODO: Come up with a cooler, less jarring loading indicator
@Composable
fun Loading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}
