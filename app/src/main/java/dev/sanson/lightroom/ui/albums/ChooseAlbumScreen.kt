package dev.sanson.lightroom.ui.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.AssetId

@Composable
fun ChooseAlbum(
    viewModel: ChooseAlbumViewModel = hiltViewModel(),
    onAlbumSelected: () -> Unit,
) {
    val state by viewModel.store.state.collectAsState()

    ChooseAlbumScreen(
        state = state,
        onAlbumSelect = viewModel::selectAlbum,
        onConfirm = viewModel::saveAlbumChoice,
    )

    LaunchedEffect(state.albumChoiceSaved) {
        if (state.albumChoiceSaved) {
            onAlbumSelected()
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun ChooseAlbumScreen(
    state: ChooseAlbumState,
    onAlbumSelect: (AlbumId) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
    }
}

@Composable
fun AlbumRow(
    isSelected: Boolean,
    onClick: () -> Unit,
    name: String,
    coverAsset: AssetId,
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

            AssetThumbnail(
                id = coverAsset,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(48.dp),
            )

            Spacer(Modifier.size(12.dp))

            Text(text = name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun AssetThumbnail(
    id: AssetId,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(6.dp),
            ),
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
