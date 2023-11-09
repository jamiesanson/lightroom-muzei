package dev.sanson.lightroom.screens

import com.slack.circuit.runtime.screen.Screen
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