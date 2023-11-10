package dev.sanson.lightroom.screens

import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.android.AndroidScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data object ChooseSourceScreen : Screen

@Parcelize
data object ChooseAlbumScreen : Screen

@Parcelize
data object ConfirmationScreen : Screen

@Parcelize
data object FilterAssetsScreen : Screen

@Parcelize
data object SignInScreen : Screen

/**
 * An [AndroidScreen] which finishes the current activity with a given [requestCode] set as result
 */
@Parcelize
data class FinishActivityScreen(val requestCode: Int) : AndroidScreen
