package dev.sanson.lightroom.ui.source

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.sanson.lightroom.R
import dev.sanson.lightroom.ui.component.DarkModePreviews
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnChooseAlbum
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnChooseCatalog
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import javax.inject.Inject

class ChooseSourceUiFactory @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is ChooseSourceScreen -> ui<ChooseSourceScreen.State> { state, modifier ->
            ChooseSource(
                state = state,
                modifier = modifier,
            )
        }

        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSource(
    state: ChooseSourceScreen.State,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Choose a source",
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
            SourceCard(
                title = stringResource(R.string.catalog_source_title),
                subtitle = stringResource(R.string.catalog_source_description),
                onClick = { state.eventSink(OnChooseCatalog) },
            )

            Spacer(Modifier.height(12.dp))

            SourceCard(
                title = stringResource(R.string.album_source_title),
                subtitle = stringResource(R.string.album_source_description),
                onClick = { state.eventSink(OnChooseAlbum) },
            )
        }
    }
}

@Composable
private fun SourceCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // TODO: Use appropriate type style here
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(Modifier.size(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.54f),
                )
            }

            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.54f),
            )
        }
    }
}

@DarkModePreviews
@Composable
fun ChooseSourcePreview() {
    MuzeiLightroomTheme {
        ChooseSource(state = ChooseSourceScreen.State {})
    }
}
