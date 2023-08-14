package dev.sanson.lightroom

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.ui.settings.Settings
import javax.inject.Inject

@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    @Inject
    lateinit var lightroom: Lightroom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            val darkTheme = isSystemInDarkTheme()

            MaterialTheme(
                colorScheme = when {
                    dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
                    dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
                    darkTheme -> darkColorScheme()
                    else -> lightColorScheme()
                },
            ) {
                Settings(
                    onAlbumChanged = {
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let(lightroom::handleSignInResponse)
    }
}
