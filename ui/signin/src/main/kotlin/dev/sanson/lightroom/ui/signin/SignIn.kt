package dev.sanson.lightroom.ui.signin

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.ui.MuzeiLightroomTheme
import dev.sanson.lightroom.common.ui.component.LightroomCard
import dev.sanson.lightroom.common.ui.component.PreviewLightDark
import dev.sanson.lightroom.screens.SignInScreen
import dev.sanson.unsplash.ui.RandomBackgroundImage

@CircuitInject(SignInScreen::class, SingletonComponent::class)
@Composable
fun SignIn(
    viewState: SignInState,
    modifier: Modifier = Modifier,
) {
    SignInScreen(
        isLoading = viewState is SignInState.Loading,
        onSignIn = { (viewState as? SignInState.NotSignedIn)?.eventSink?.invoke(SignInEvent.SignInWithLightroom) },
        modifier = modifier,
    )
}

@Composable
private fun SignInScreen(
    isLoading: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        RandomBackgroundImage(
            backgroundColor = MaterialTheme.colorScheme.surface,
        )

        Crossfade(
            targetState = isLoading,
            label = "Sign in card",
            modifier = Modifier.align(Alignment.Center),
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier =
                        Modifier
                            .wrapContentHeight()
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(2.dp))
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surface),
                ) {
                    SignInCard(onSignIn = {}, modifier = Modifier.alpha(0f))

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                SignInCard(onSignIn = onSignIn)
            }
        }
    }
}

@Composable
private fun SignInCard(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LightroomCard(
        modifier = modifier.padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.sign_in),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = stringResource(R.string.sign_in_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.size(24.dp))

        SignInButton(
            onClick = onSignIn,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
        )

        Spacer(Modifier.size(2.dp))
    }
}

@Composable
private fun SignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.sign_in_with_adobe_id),
        contentDescription = "Adobe",
        modifier =
            modifier
                .scale(1.1f)
                .wrapContentWidth()
                .clip(RoundedCornerShape(50))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                    onClick = onClick,
                ),
        colorFilter =
            ColorFilter
                .tint(MaterialTheme.colorScheme.onSurface),
    )
}

@PreviewLightDark
@Composable
private fun SignInScreenPreview() {
    MuzeiLightroomTheme {
        SignInScreen(isLoading = false, onSignIn = {})
    }
}
