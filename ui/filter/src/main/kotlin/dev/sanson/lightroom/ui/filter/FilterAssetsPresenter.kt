// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.filter

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.common.ui.component.Equality
import dev.sanson.lightroom.lib.search.SearchConfig
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.screens.FilterAssetsScreen
import dev.sanson.lightroom.sdk.model.AlbumId
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private val SearchConfig.starRating: Int
    get() {
        val rating = rating

        return when {
            rating == null -> 0
            rating.isEmpty() -> rating.first
            rating.first == 0 -> rating.last
            rating.last == 5 -> rating.first
            else -> 0
        }
    }

private val SearchConfig.ratingEquality: Equality
    get() {
        val rating = rating

        return when {
            rating == null || rating.isEmpty() -> Equality.EqualTo
            rating.first == 0 -> Equality.LessThan
            rating.last == 5 -> Equality.GreaterThan
            else -> Equality.EqualTo
        }
    }

class FilterAssetsPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val configRepository: ConfigRepository,
) : Presenter<FilterAssetsState> {
    @Composable
    override fun present(): FilterAssetsState {
        val searchConfig by produceState(SearchConfig(source = SearchConfig.Source.Album(id = AlbumId("")))) {
            configRepository.searchConfig.filterNotNull().collect { value = it }
        }

        var starRating by rememberSaveable(searchConfig.starRating) {
            mutableIntStateOf(searchConfig.starRating)
        }

        var equality by rememberSaveable(searchConfig.ratingEquality) {
            mutableStateOf(searchConfig.ratingEquality)
        }

        val keywords =
            rememberSaveable(searchConfig.keywords, saver = keywordSaver()) {
                searchConfig.keywords.toMutableStateList()
            }

        var flag by rememberSaveable(searchConfig) {
            mutableStateOf(searchConfig.review)
        }

        var filtersApplied by rememberSaveable(searchConfig) {
            mutableStateOf(
                FilterAssetsState.FiltersApplied(
                    keywords = searchConfig.keywords.isNotEmpty(),
                    rating = searchConfig.rating != null,
                    review = searchConfig.review != null,
                ),
            )
        }

        val scope = rememberCoroutineScope()

        return FilterAssetsState(
            stepNumber = if (searchConfig.source is SearchConfig.Source.Album) 3 else 2,
            keywords = keywords.toPersistentList(),
            rating = starRating,
            equality = equality,
            flag = flag,
            filtersApplied = filtersApplied,
            eventSink = { event ->
                when (event) {
                    is FilterAssetsEvent.AddKeyword ->
                        keywords += event.keyword

                    is FilterAssetsEvent.RemoveKeyword ->
                        keywords -= event.keyword

                    is FilterAssetsEvent.UpdateRating ->
                        starRating = event.rating

                    is FilterAssetsEvent.UpdateEquality ->
                        equality = event.equality

                    is FilterAssetsEvent.UpdateFlag ->
                        flag = event.flag

                    is FilterAssetsEvent.ToggleKeywords ->
                        filtersApplied =
                            filtersApplied.copy(keywords = !filtersApplied.keywords)

                    is FilterAssetsEvent.ToggleRating ->
                        filtersApplied = filtersApplied.copy(rating = !filtersApplied.rating)

                    is FilterAssetsEvent.ToggleReview ->
                        filtersApplied = filtersApplied.copy(review = !filtersApplied.review)

                    is FilterAssetsEvent.Confirm ->
                        scope.launch {
                            val newConfig =
                                searchConfig.copy(
                                    keywords =
                                        keywords.toSet().takeIf { filtersApplied.keywords }
                                            ?: emptySet(),
                                    review = flag.takeIf { filtersApplied.review },
                                    rating =
                                        IntRange(
                                            start = if (equality == Equality.LessThan) 0 else starRating,
                                            endInclusive = if (equality == Equality.GreaterThan) 5 else starRating,
                                        ).takeIf { starRating > 0 && filtersApplied.rating },
                                )

                            configRepository.updateConfig(newConfig)

                            navigator.goTo(ConfirmationScreen)
                        }
                }
            },
        )
    }

    @CircuitInject(FilterAssetsScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): FilterAssetsPresenter
    }
}

private fun keywordSaver(): Saver<SnapshotStateList<String>, Bundle> =
    Saver(
        save = { original ->
            Bundle().apply {
                putStringArray("state_list", original.toTypedArray())
            }
        },
        restore = {
            it.getStringArray("state_list")?.toList()?.toMutableStateList()
        },
    )
