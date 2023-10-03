package dev.sanson.lightroom.ui.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.sanson.lightroom.coil.rememberImageRequest
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import dev.sanson.lightroom.ui.component.DarkModePreviews
import dev.sanson.lightroom.ui.component.FixedCardLoadingScreen
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseAlbum(
    viewState: ChooseAlbumScreen.State,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Choose an album",
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
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                    .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection),
            ) {
                AnimatedContent(
                    targetState = viewState,
                    label = "Choose album",
                ) {
                    // TODO: Wee bit of skeleton loading here would be nice
                    when (val state = it) {
                        is ChooseAlbumScreen.State.Loaded ->
                            LazyColumn(Modifier.background(MaterialTheme.colorScheme.background)) {
                                items(state.albums) { (parent, albums) ->
                                    if (parent != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { /* TODO: Expand and contract folder */ }
                                                .padding(vertical = 4.dp),
                                        ) {
                                            Icon(
                                                Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                            )

                                            Spacer(modifier = Modifier.size(4.dp))

                                            Text(text = parent)
                                        }
                                    }

                                    Column {
                                        Spacer(Modifier.size(8.dp))
                                        for (album in albums) {
                                            AlbumRow(
                                                isSelected = state.selectedAlbum == album.id,
                                                onClick = {
                                                    state.eventSink(
                                                        ChooseAlbumScreen.Event.SelectAlbum(
                                                            albumId = album.id,
                                                        ),
                                                    )
                                                },
                                                name = album.name,
                                                coverAsset = album.cover,
                                                assetCount = remember(album) { album.assets.size.toString() },
                                                modifier = Modifier
                                                    .padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp,
                                                    )
                                                    .padding(start = if (parent != null) 12.dp else 0.dp),
                                            )
                                        }
                                    }
                                }
                            }

                        else ->
                            FixedCardLoadingScreen()
                    }
                }
            }

            Button(
                onClick = { },
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                Text("Confirm")
            }
        }
    }
}

@Composable
fun AlbumRow(
    isSelected: Boolean,
    onClick: () -> Unit,
    name: String,
    coverAsset: AssetId?,
    assetCount: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "Album selection background",
    )

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp),
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (coverAsset != null) {
                AssetThumbnail(
                    id = coverAsset,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp),
                )
            } else {
                Box(
                    Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f),
                            shape = RoundedCornerShape(6.dp),
                        ),
                ) {
                    if (assetCount.toInt() != 0) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                        )
                    }
                }
            }

            Spacer(Modifier.size(12.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = assetCount,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 12.dp),
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

@DarkModePreviews
@Composable
fun ChooseAlbumScreenPreview() {
    MuzeiLightroomTheme {
        ChooseAlbum(
            viewState = ChooseAlbumScreen.State.Loaded(
                albums = listOf(
                    "Travel" to listOf(
                        Album(
                            id = AlbumId("1"),
                            name = "Portugal",
                            folder = "Travel",
                            cover = null,
                            assets = List(168) { AssetId("") },
                        ),
                        Album(
                            id = AlbumId("2"),
                            name = "Spain",
                            folder = "Travel",
                            cover = null,
                            assets = List(340) { AssetId("") },
                        ),
                        Album(
                            id = AlbumId("3"),
                            name = "Morocco",
                            folder = "Travel",
                            cover = null,
                            assets = List(210) { AssetId("") },
                        ),
                    ),
                    null to listOf(
                        Album(
                            id = AlbumId("4"),
                            name = "NZ Birdlife",
                            folder = null,
                            cover = null,
                            assets = List(10) { AssetId("") },
                        ),
                    ),
                ),
                selectedAlbum = AlbumId("3"),
                eventSink = {},
            ),
        )
    }
}
