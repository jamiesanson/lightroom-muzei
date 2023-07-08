package dev.sanson.lightroom.ui.signin

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.sanson.lightroom.android.LocalNewIntent
import dev.sanson.lightroom.android.NewIntent
import dev.sanson.lightroom.backend.Lightroom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class SignInState(
    newIntent: NewIntent,
    coroutineScope: CoroutineScope,
    private val lightroom: Lightroom,
    private val context: Context,
) {
    init {
        coroutineScope.launch {
            launch {
                lightroom.isSignedIn.collect {
                    isSignedIn = it
                }
            }

            launch {
                newIntent.next.collect {
                    lightroom.handleSignInResponse(it)
                }
            }
        }
    }

    var isSignedIn by mutableStateOf<Boolean?>(null)

    fun signIn() {
        lightroom.signIn(context)
    }
}

@Composable
fun rememberSignInState(lightroom: Lightroom): SignInState {
    val newIntent = LocalNewIntent.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    return remember(lightroom, newIntent, context, scope) {
        SignInState(
            newIntent = newIntent,
            context = context,
            coroutineScope = scope,
            lightroom = lightroom
        )
    }
}