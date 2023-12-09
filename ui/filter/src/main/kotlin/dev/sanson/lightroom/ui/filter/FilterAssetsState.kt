// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.filter

import android.os.Parcelable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.sanson.lightroom.common.ui.component.Equality
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

data class FilterAssetsState(
    val stepNumber: Int,
    val keywords: ImmutableList<String>,
    val rating: Int,
    val equality: Equality,
    val flag: Asset.Flag?,
    val filtersApplied: FiltersApplied,
    val eventSink: (FilterAssetsEvent) -> Unit,
) : CircuitUiState {
    @Parcelize
    data class FiltersApplied(
        val keywords: Boolean = false,
        val rating: Boolean = false,
        val review: Boolean = false,
    ) : Parcelable
}

sealed interface FilterAssetsEvent : CircuitUiEvent {
    data class AddKeyword(val keyword: String) : FilterAssetsEvent

    data class RemoveKeyword(val keyword: String) : FilterAssetsEvent

    data class UpdateRating(val rating: Int) : FilterAssetsEvent

    data class UpdateEquality(val equality: Equality) : FilterAssetsEvent

    data class UpdateFlag(val flag: Asset.Flag?) : FilterAssetsEvent

    data object ToggleKeywords : FilterAssetsEvent

    data object ToggleRating : FilterAssetsEvent

    data object ToggleReview : FilterAssetsEvent

    data object Confirm : FilterAssetsEvent
}
