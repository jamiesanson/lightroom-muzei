package dev.sanson.lightroom.ui.filter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sanson.lightroom.data.Filter
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.State.Loading
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
            is FilterAssetsScreen -> factory.create(navigator)
            else -> null
        }
    }
}

class FilterAssetsPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val filterStore: DataStore<Filter?>,
) : Presenter<FilterAssetsScreen.State> {
    @Composable
    override fun present(): FilterAssetsScreen.State {
        val filter by filterStore.data.collectAsState(initial = null)

        return when (null) {
            null ->
                Loading

            else -> TODO()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): FilterAssetsPresenter
    }
}
