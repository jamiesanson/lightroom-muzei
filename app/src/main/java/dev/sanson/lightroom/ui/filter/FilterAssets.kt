package dev.sanson.lightroom.ui.filter

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import javax.inject.Inject

class FilterAssetsUiFactory @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is FilterAssetsScreen -> ui<FilterAssetsScreen.State> { state, modifier ->
            FilterAssets(
                viewState = state,
                modifier = modifier,
            )
        }

        else -> null
    }
}

@Composable
private fun FilterAssets(
    viewState: FilterAssetsScreen.State,
    modifier: Modifier = Modifier,
) {
}
