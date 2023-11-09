package dev.sanson.lightroom.feature.filter

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.common.ui.component.Equality
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.collections.immutable.ImmutableList

data class FilterAssetsState(
    val keywords: ImmutableList<String>,
    val rating: Int,
    val equality: Equality,
    val flag: Asset.Flag?,
    val eventSink: (FilterAssetsEvent) -> Unit,
) : CircuitUiState

sealed interface FilterAssetsEvent : CircuitUiEvent {
    data class AddKeyword(val keyword: String) : FilterAssetsEvent
    data class RemoveKeyword(val keyword: String) : FilterAssetsEvent
    data class UpdateRating(val rating: Int) : FilterAssetsEvent
    data class UpdateEquality(val equality: Equality) : FilterAssetsEvent
    data class UpdateFlag(val flag: Asset.Flag?) : FilterAssetsEvent

    data object PopBackToAlbumSelection : FilterAssetsEvent

    data object Confirm : FilterAssetsEvent
}
