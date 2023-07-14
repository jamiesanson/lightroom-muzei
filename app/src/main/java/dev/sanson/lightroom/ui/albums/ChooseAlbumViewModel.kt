package dev.sanson.lightroom.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.arch.Async
import dev.sanson.lightroom.arch.Store
import dev.sanson.lightroom.arch.Uninitialized
import dev.sanson.lightroom.arch.collectInto
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Album
import dev.sanson.lightroom.sdk.model.AlbumId
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChooseAlbumState(
    val albums: Async<List<Album>> = Uninitialized,
    val selectedAlbum: AlbumId? = null,
)

@HiltViewModel
class ChooseAlbumViewModel @Inject constructor(
    private val lightroom: Lightroom,
): ViewModel() {

    val store = Store(ChooseAlbumState())

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            lightroom::getAlbums.collectInto(store) {
                copy(albums = it)
            }
        }
    }
}