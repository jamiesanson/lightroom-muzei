package dev.sanson.lightroom.ui.filter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.Filter
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.AddKeyword
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.RemoveKeyword
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.UpdateRating
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.UpdateUpToMax
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilterAssetsPresenterFactory @Inject constructor(
    private val factory: FilterAssetsPresenter.Factory,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is FilterAssetsScreen -> factory.create(navigator, screen)
            else -> null
        }
    }
}

class FilterAssetsPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: FilterAssetsScreen,
    private val filterStore: DataStore<Filter?>,
) : Presenter<FilterAssetsScreen.State> {
    @Composable
    override fun present(): FilterAssetsScreen.State {
        val filter by produceState(Filter(albumId = screen.albumId)) {
            filterStore.data.filterNotNull().collect { value = it }
        }

        val scope = rememberCoroutineScope()

        return FilterAssetsScreen.State(
            keywords = filter.keywords.toPersistentList(),
            rating = filter.rating?.first ?: 0,
            ratingUpToMax = filter.rating?.run { last == 5 } ?: false,
            flag = filter.review,
            eventSink = { event ->
                when (event) {
                    is AddKeyword ->
                        scope.launch {
                            filterStore.updateData { it?.copy(keywords = it.keywords + event.keyword) }
                        }

                    is RemoveKeyword ->
                        scope.launch {
                            filterStore.updateData { it?.copy(keywords = it.keywords - event.keyword) }
                        }

                    is UpdateRating ->
                        scope.launch {
                            filterStore.updateData {
                                it?.copy(
                                    rating = IntRange(
                                        start = event.rating,
                                        endInclusive = it.rating?.endInclusive ?: event.rating,
                                    ),
                                )
                            }
                        }

                    is UpdateUpToMax ->
                        scope.launch {
                            filterStore.updateData {
                                val start = it?.rating?.first ?: 0
                                it?.copy(
                                    rating = IntRange(
                                        start = start,
                                        endInclusive = if (event.upToMax) 5 else start,
                                    ),
                                )
                            }
                        }
                }
            },
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator, screen: FilterAssetsScreen): FilterAssetsPresenter
    }
}
