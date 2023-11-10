package dev.sanson.unsplash.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentMapOf

private const val UNSPLASH_REFERRAL = "utm_source=Lightroom%20for%20Muzei&utm_medium=referral"

private fun unsplash(path: String) = "https://unsplash.com$path?$UNSPLASH_REFERRAL"

@Composable
fun AttributionChip(
    name: String,
    username: String,
    modifier: Modifier = Modifier,
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // Always use dark theme for contrast against most backgrounds
    MaterialTheme(
        colorScheme =
            if (dynamicColor) {
                dynamicDarkColorScheme(LocalContext.current)
            } else {
                darkColorScheme()
            },
    ) {
        Box(
            modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.36f),
                    shape = RoundedCornerShape(50),
                ),
        ) {
            HyperlinkText(
                fullText = "Photo by $name on Unsplash",
                hyperLinks =
                    persistentMapOf(
                        name to unsplash("/@$username"),
                        "Unsplash" to unsplash("/"),
                    ),
                textStyle =
                    MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                linkTextColor = MaterialTheme.colorScheme.secondary,
                linkTextDecoration = TextDecoration.Underline,
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}

@Preview
@Composable
private fun AttributionTextPreview() {
    MaterialTheme {
        AttributionChip(name = "Jamie Sanson", username = "jamiesanson")
    }
}
