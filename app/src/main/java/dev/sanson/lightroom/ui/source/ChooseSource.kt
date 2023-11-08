package dev.sanson.lightroom.ui.source

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.R
import dev.sanson.lightroom.data.config.Config
import dev.sanson.lightroom.ui.component.DarkModePreviews
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnChooseAlbum
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnChooseCatalog
import dev.sanson.lightroom.ui.source.ChooseSourceScreen.Event.OnConfirm
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme

@CircuitInject(ChooseSourceScreen::class, SingletonComponent::class)
@Composable
fun ChooseSource(
    state: ChooseSourceScreen.State,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        Box(Modifier.systemBarsPadding()) {
            Text(
                text = "Where should we pull your wallpapers from?",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 64.dp)
                    .align(Alignment.TopCenter),
            )

            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
            ) {
                SourceRow(
                    title = stringResource(R.string.catalog_source_title),
                    subtitle = stringResource(R.string.catalog_source_description),
                    selected = state.selectedSource is Config.Source.Catalog,
                    onClick = { state.eventSink(OnChooseCatalog) },
                )

                Spacer(Modifier.height(24.dp))

                SourceRow(
                    title = stringResource(R.string.album_source_title),
                    subtitle = stringResource(R.string.album_source_description),
                    selected = state.selectedSource is Config.Source.Album,
                    onClick = { state.eventSink(OnChooseAlbum) },
                )
            }

            Button(
                onClick = { state.eventSink(OnConfirm) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
            ) {
                Spacer(Modifier.size(8.dp))

                Text("Continue")

                Spacer(Modifier.size(16.dp))

                Icon(Icons.Default.ArrowForward, contentDescription = "")
            }
        }
    }
}

@Composable
private fun SourceRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderWidth by animateDpAsState(
        if (selected) 2.dp else 1.dp,
        label = "Selection border width",
    )
    val borderAlpha by animateFloatAsState(
        if (selected) 0.64f else 0.08f,
        label = "Selection border opacity",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onClick() },
                modifier = Modifier.padding(4.dp),
            )

            Column(
                Modifier
                    .padding(4.dp)
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.size(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.54f),
                )
            }
        }
    }
}

@DarkModePreviews
@Composable
fun ChooseSourcePreview() {
    MuzeiLightroomTheme {
        ChooseSource(state = ChooseSourceScreen.State(Config.Source.Catalog) {})
    }
}
