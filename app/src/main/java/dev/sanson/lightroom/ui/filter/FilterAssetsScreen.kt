package dev.sanson.lightroom.ui.filter

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Screen
import dev.sanson.lightroom.sdk.model.AlbumId
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterAssetsScreen(
    val albumId: AlbumId,
) : Screen {

    data class State(
        val keywords: List<String>,
        val rating: IntRange?,
        val flag: Asset.Flag?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent
}
