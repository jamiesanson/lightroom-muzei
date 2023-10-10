package dev.sanson.lightroom.ui.filter

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.ui.component.Equality
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
data object FilterAssetsScreen : Screen {

    data class State(
        val keywords: ImmutableList<String>,
        val rating: Int,
        val equality: Equality,
        val flag: Asset.Flag?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data class AddKeyword(val keyword: String) : Event
        data class RemoveKeyword(val keyword: String) : Event
        data class UpdateRating(val rating: Int) : Event
        data class UpdateEquality(val equality: Equality) : Event
        data class UpdateFlag(val flag: Asset.Flag?) : Event

        data object PopBackToAlbumSelection : Event

        data object Confirm : Event
    }
}
