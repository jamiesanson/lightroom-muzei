package dev.sanson.lightroom.ui.filter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.filter.Filter
import dev.sanson.lightroom.data.filter.FilterRepository
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
    private val filterRepository: FilterRepository,
) : Presenter<FilterAssetsScreen.State> {
    @Composable
    override fun present(): FilterAssetsScreen.State {
        val filter by produceState(Filter(albumId = screen.albumId)) {
            filterRepository.filter.filterNotNull().collect { value = it }
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
                            filterRepository.addKeyword(event.keyword)
                        }

                    is RemoveKeyword ->
                        scope.launch {
                            filterRepository.removeKeyword(event.keyword)
                        }

                    is UpdateRating ->
                        scope.launch {
                            filterRepository.setRating(event.rating)
                        }

                    is UpdateUpToMax ->
                        scope.launch {
                            filterRepository.setRatingUpToMax(event.upToMax)
                        }

                    is FilterAssetsScreen.Event.UpdateFlag ->
                        scope.launch {
                            filterRepository.updateFlag(event.flag)
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
