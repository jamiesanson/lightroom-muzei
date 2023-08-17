package dev.sanson.lightroom.ui.signin

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.sanson.lightroom.R
import dev.sanson.lightroom.sdk.rememberLightroom
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import dev.sanson.lightroom.unsplash.AttributionChip
import dev.sanson.lightroom.unsplash.rememberRandomImage

@Composable
fun SignIn(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lightroom = rememberLightroom()

    SignInScreen(
        onSignIn = { lightroom.signIn(context) },
        modifier = modifier,
    )
}

@Composable
private fun SignInScreen(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.DarkGray

    Box(
        modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        RandomBackgroundImage(
            backgroundColor = backgroundColor,
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentHeight()
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp))
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),

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
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
            )

            Spacer(Modifier.size(2.dp))
        }
    }
}

@Composable
private fun BoxScope.RandomBackgroundImage(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    val backgroundImage = rememberRandomImage()
    if (backgroundImage != null) {
        var showAttribution by rememberSaveable { mutableStateOf(false) }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(backgroundImage.url)
                .crossfade(300)
                .build(),
            contentDescription = "",
            placeholder = ColorPainter(backgroundColor),
            onSuccess = {
                showAttribution = true
            },
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxSize(),
        )

        AnimatedVisibility(
            visible = showAttribution,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding(),
        ) {
            AttributionChip(
                name = backgroundImage.attribution.name,
                username = backgroundImage.attribution.username,
                modifier = Modifier.padding(16.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f)),
        )
    }
}

@Composable
fun SignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.36f),
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_adobe_logo),
            contentDescription = "Adobe",
            modifier = Modifier
                .padding(vertical = 4.dp)
                .align(Alignment.CenterVertically)
                .size(24.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = stringResource(R.string.continue_with_adobe),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignInScreenPreview() {
    MuzeiLightroomTheme {
        SignInScreen(onSignIn = {})
    }
}
