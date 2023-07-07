package dev.sanson.lightroom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import dev.sanson.lightroom.backend.Lightroom
import dev.sanson.lightroom.backend.lightroom.AccountService
import javax.inject.Inject


@AndroidEntryPoint
class LightroomSettingsActivity : ComponentActivity() {

    @Inject
    lateinit var lightroom: Lightroom

    @Inject
    lateinit var accountService: AccountService

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isSignedIn: Boolean? by lightroom.isSignedIn.collectAsState(initial = null)

            AnimatedContent(targetState = isSignedIn, label = "Main Content") { signedIn ->
                when (signedIn) {
                    true -> SignedIn()
                    false -> SignedOut()
                    null -> Loading()
                }
            }
        }
    }

    @Composable
    fun SignedOut() {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Button(
                onClick = ::loginToLightroom,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Text("Connect Lightroom Account")
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun SignedIn() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            var name: String? by remember { mutableStateOf(null) }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = name,
                    label = "Content",
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    if (it == null) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Hello, $it",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            LaunchedEffect(true) {
                name = accountService.getAccount().firstName
            }
        }
    }

    @Composable
    fun Loading() {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            lightroom.handleSignInResponse(it)
        }
    }

    private fun loginToLightroom() {
        lightroom.signIn(this)
    }
}