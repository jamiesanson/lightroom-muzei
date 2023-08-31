package dev.sanson.lightroom.ui.filter

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Screen
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterAssetsScreen(
    val albumId: AlbumId,
) : Screen {

    data class State(
        val keywords: ImmutableList<String>,
        val rating: Int,
        val ratingUpToMax: Boolean,
        val flag: Asset.Flag?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class AddKeyword(val keyword: String) : Event
        data class RemoveKeyword(val keyword: String) : Event
        data class UpdateRating(val rating: Int) : Event
        data class UpdateUpToMax(val upToMax: Boolean) : Event
        data class UpdateFlag(val flag: Asset.Flag?) : Event
    }
}
