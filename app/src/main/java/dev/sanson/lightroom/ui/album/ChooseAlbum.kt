package dev.sanson.lightroom.ui.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.sanson.lightroom.coil.rememberImageRequest
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import dev.sanson.lightroom.ui.component.FixedCardLoadingScreen
import javax.inject.Inject

class ChooseAlbumUiFactory @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is ChooseAlbumScreen -> ui<ChooseAlbumScreen.State> { state, modifier ->
            ChooseAlbum(
                viewState = state,
                modifier = modifier,
            )
        }

        else -> null
    }
}

@Composable
private fun ChooseAlbum(
    viewState: ChooseAlbumScreen.State,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(targetState = viewState, label = "Choose album") {
        // TODO: Wee bit of skeleton loading here would be nice
        when (val state = it) {
            is ChooseAlbumScreen.State.Loaded ->
                LazyColumn(modifier.background(MaterialTheme.colorScheme.background)) {
                    items(state.albums) { album ->
                        AlbumRow(
                            isSelected = state.selectedAlbum == album.id,
                            onClick = { state.eventSink(ChooseAlbumScreen.Event.SelectAlbum(album.id)) },
                            name = album.name,
                            coverAsset = album.cover,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    item {
                        Button(
                            onClick = { state.eventSink(ChooseAlbumScreen.Event.Confirm) },
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .fillMaxWidth(),
                        ) {
                            Text("Confirm")
                        }
                    }
                }

            else ->
                FixedCardLoadingScreen(modifier)
        }
    }
}

@Composable
fun AlbumRow(
    isSelected: Boolean,
    onClick: () -> Unit,
    name: String,
    coverAsset: AssetId?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
            )

            if (coverAsset != null) {
                AssetThumbnail(
                    id = coverAsset,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(48.dp),
                )
            }

            Spacer(Modifier.size(12.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun AssetThumbnail(
    id: AssetId,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = rememberImageRequest(
            assetId = id,
            rendition = Rendition.Thumbnail,
        ),
        contentDescription = "Album cover photo",
        modifier = modifier
            .clip(shape = RoundedCornerShape(6.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Preview
@Composable
fun AlbumRowPreview() {
    MaterialTheme {
        Box(Modifier.padding(8.dp)) {
            var selected by remember { mutableStateOf(false) }
            AlbumRow(
                isSelected = selected,
                onClick = { selected = !selected },
                name = "Big trip - 2023",
                coverAsset = AssetId(""),
            )
        }
    }
}
