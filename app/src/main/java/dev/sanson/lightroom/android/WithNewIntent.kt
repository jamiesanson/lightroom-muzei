package dev.sanson.lightroom.android

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.util.Consumer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NewIntent {
    val next: Flow<Intent>
}

class DefaultNewIntent: NewIntent {
    val nextIntent = MutableSharedFlow<Intent>(replay = 0)

    override val next: Flow<Intent> = nextIntent.asSharedFlow()
}

val LocalNewIntent = compositionLocalOf<NewIntent> { error("No new intent listener created") }

@Composable
fun WithNewIntent(activity: ComponentActivity, block: @Composable () -> Unit) {
    val intentHandler = remember { DefaultNewIntent() }

    val listener = remember(intentHandler) {
        Consumer<Intent> {
            intentHandler.nextIntent.tryEmit(it)
        }
    }

    CompositionLocalProvider(LocalNewIntent provides intentHandler) {
        block()
    }

    DisposableEffect(true) {
        activity.addOnNewIntentListener(listener)

        onDispose {
            activity.removeOnNewIntentListener(listener)
        }
    }
}