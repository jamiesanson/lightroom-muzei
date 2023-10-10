package dev.sanson.lightroom.ui.confirmation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.sanson.lightroom.ui.component.DarkModePreviews
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import javax.inject.Inject

class ConfirmationUiFactory @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is ConfirmationScreen -> ui<ConfirmationScreen.State> { state, modifier ->
            Confirmation(
                state = state,
                modifier = modifier,
            )
        }

        else -> null
    }
}

/**
 * Note to self: Confirmation in a two-stage process
 *
 * * Loading your images... (paginating and fetching all images)
 * * Found XX photos, let's start here (date, time, details, use loaded image as background)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Confirmation(
    state: ConfirmationScreen.State,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(state) {
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Loading your images",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                scrollBehavior = topAppBarScrollBehaviour,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) { paddingValues ->
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection),
        ) {
        }
    }
}

@DarkModePreviews
@Composable
fun ChooseSourcePreview() {
    MuzeiLightroomTheme {
        Confirmation(state = ConfirmationScreen.State.LoadingArtwork)
    }
}
