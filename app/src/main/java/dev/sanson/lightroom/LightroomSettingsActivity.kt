package dev.sanson.lightroom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.ui.settings.Settings
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import javax.inject.Inject

@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    @Inject
    lateinit var lightroom: Lightroom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MuzeiLightroomTheme {
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
