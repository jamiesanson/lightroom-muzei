package dev.sanson.lightroom.ui.signin

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.R
import dev.sanson.lightroom.sdk.rememberLightroom
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme

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
    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black), // TODO: Use a photo as the background here, like the Lightroom login does
    ) {
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
