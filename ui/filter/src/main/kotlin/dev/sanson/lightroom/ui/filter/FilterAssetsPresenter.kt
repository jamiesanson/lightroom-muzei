package dev.sanson.lightroom.ui.filter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.Config
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.common.ui.component.Equality
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.screens.FilterAssetsScreen
import dev.sanson.lightroom.sdk.model.AlbumId
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private val Config.starRating: Int
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

private val Config.ratingEquality: Equality
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
        val filter by produceState(Config(source = Config.Source.Album(id = AlbumId("")))) {
            configRepository.config.filterNotNull().collect { value = it }
        }

        var equality by remember {
            mutableStateOf(filter.ratingEquality)
        }

        val scope = rememberCoroutineScope()

        return FilterAssetsState(
            keywords = filter.keywords.toPersistentList(),
            rating = filter.starRating,
            equality = equality,
            flag = filter.review,
            eventSink = { event ->
                when (event) {
                    is FilterAssetsEvent.AddKeyword ->
                        scope.launch {
                            configRepository.addKeyword(event.keyword)
                        }

                    is FilterAssetsEvent.RemoveKeyword ->
                        scope.launch {
                            configRepository.removeKeyword(event.keyword)
                        }

                    is FilterAssetsEvent.UpdateRating ->
                        scope.launch {
                            configRepository.setRatingRange(
                                start = event.rating,
                                end = when (filter.ratingEquality) {
                                    Equality.GreaterThan -> 5
                                    Equality.EqualTo -> event.rating
                                    Equality.LessThan -> 0
                                },
                            )
                        }

                    is FilterAssetsEvent.UpdateEquality ->
                        scope.launch {
                            equality = event.equality

                            configRepository.setRatingRange(
                                start = when (filter.ratingEquality) {
                                    Equality.GreaterThan,
                                    Equality.EqualTo,
                                    -> filter.starRating

                                    Equality.LessThan -> 0
                                },
                                end = when (filter.ratingEquality) {
                                    Equality.GreaterThan -> 5
                                    Equality.EqualTo,
                                    Equality.LessThan,
                                    -> filter.starRating
                                },
                            )
                        }

                    is FilterAssetsEvent.UpdateFlag ->
                        scope.launch {
                            configRepository.updateFlag(event.flag)
                        }

                    is FilterAssetsEvent.PopBackToAlbumSelection ->
                        navigator.pop()

                    is FilterAssetsEvent.Confirm ->
                        navigator.goTo(ConfirmationScreen)
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
