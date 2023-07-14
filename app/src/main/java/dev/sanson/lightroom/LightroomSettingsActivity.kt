package dev.sanson.lightroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.android.WithNewIntent
import dev.sanson.lightroom.ui.settings.Settings

@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WithNewIntent(activity = this) {
                Settings()
            }
        }
    }
}
