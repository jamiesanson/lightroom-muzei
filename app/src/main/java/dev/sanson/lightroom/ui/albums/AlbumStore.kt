package dev.sanson.lightroom.ui.albums

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.sdk.model.AlbumId
import javax.inject.Inject

@HiltViewModel
private class AlbumStoreViewModel @Inject constructor(
    val albumStore: DataStore<AlbumId?>,
) : ViewModel()

@Composable
fun rememberAlbumStore(viewModel: AlbumStoreViewModel = hiltViewModel()): DataStore<AlbumId?> {
    return viewModel.albumStore
}
