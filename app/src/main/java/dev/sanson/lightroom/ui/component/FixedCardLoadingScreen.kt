package dev.sanson.lightroom.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sanson.lightroom.ui.signin.SignIn

@Composable
fun FixedCardLoadingScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: This isn't great, but it is a more-or-less pure composable, so naming might be a fix here.
    SignIn(isLoading = true, modifier = modifier)
}
