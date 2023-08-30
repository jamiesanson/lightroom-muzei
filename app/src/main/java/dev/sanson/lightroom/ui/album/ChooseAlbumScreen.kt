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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import coil.compose.AsyncImage
import dev.sanson.lightroom.coil.rememberImageRequest
import dev.sanson.lightroom.data.Filter
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import dev.sanson.lightroom.sdk.rememberLightroom
import dev.sanson.lightroom.ui.component.FixedCardLoadingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ChooseAlbumModel {

    object Loading : ChooseAlbumModel()

    data class Loaded(
        val albums: List<Album>,
        val selectedAlbum: AlbumId? = null,
    ) : ChooseAlbumModel()
}

@Stable
class ChooseAlbumState(
    private val dataStore: DataStore<Filter?>,
) {
    var model by mutableStateOf<ChooseAlbumModel>(ChooseAlbumModel.Loading)

    fun selectAlbum(id: AlbumId) {
        val loaded = model as? ChooseAlbumModel.Loaded ?: return
        model = loaded.copy(
            selectedAlbum = id,
        )
    }

    fun onAlbumsLoaded(albums: List<Album>) {
        model = (model as? ChooseAlbumModel.Loaded)?.copy(albums = albums)
            ?: ChooseAlbumModel.Loaded(albums = albums)
    }

    suspend fun saveAlbumChoice() = withContext(Dispatchers.IO) {
        val loaded = model as? ChooseAlbumModel.Loaded ?: return@withContext

        dataStore.updateData { loaded.selectedAlbum?.let(::Filter) }
    }
}

@Composable
fun rememberChooseAlbumState(
    lightroom: Lightroom = rememberLightroom(),
    albumStore: DataStore<Filter?> = rememberFilterStore(),
): ChooseAlbumState {
    val state = remember(lightroom, albumStore) { ChooseAlbumState(albumStore) }

    LaunchedEffect(lightroom) {
        val albums = lightroom.getAlbums()
        state.onAlbumsLoaded(albums)
    }

    return state
}

@Composable
fun ChooseAlbum(
    onAlbumSelected: () -> Unit,
) {
    val state = rememberChooseAlbumState()
    val scope = rememberCoroutineScope()

    ChooseAlbumScreen(
        model = state.model,
        onAlbumSelect = {
            state.selectAlbum(it)
        },
        onConfirm = {
            scope.launch {
                state.saveAlbumChoice()
                onAlbumSelected()
            }
        },
    )
}

@Composable
private fun ChooseAlbumScreen(
    model: ChooseAlbumModel,
    onAlbumSelect: (AlbumId) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(targetState = model, label = "Choose album") {
        // TODO: Wee bit of skeleton loading here would be nice
        when (it) {
            is ChooseAlbumModel.Loaded ->
                LazyColumn(modifier.background(MaterialTheme.colorScheme.background)) {
                    items(it.albums) { album ->
                        AlbumRow(
                            isSelected = it.selectedAlbum == album.id,
                            onClick = { onAlbumSelect(album.id) },
                            name = album.name,
                            coverAsset = album.cover,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    item {
                        Button(
                            onClick = onConfirm,
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
