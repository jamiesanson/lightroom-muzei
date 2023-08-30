package dev.sanson.lightroom.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.rememberLightroom
import dev.sanson.lightroom.ui.album.ChooseAlbum
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
    /**
     * TODO: This is to become a stepped flow
     *
     * Step 1:                   Step 2:               You're all set!
     * ------           ----->   -------       ----->  ---------------
     * Choose an album           Filter album          It may take a while...
     */
    AnimatedContent(
        targetState = model,
        label = "Settings",
        modifier = modifier,
        transitionSpec = {
            if (targetState is SettingsModel.SignedIn) {
                EnterTransition.None.togetherWith(ExitTransition.None)
            } else {
                (
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
                    )
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            }
        },
    ) {
        when (it) {
            is SettingsModel.SignedIn ->
                ChooseAlbum(
                    onAlbumSelected = onAlbumChanged,
                )

            SettingsModel.SignedOut, SettingsModel.Loading ->
                SignIn(
                    isLoading = it is SettingsModel.Loading,
                )
        }
    }
}
