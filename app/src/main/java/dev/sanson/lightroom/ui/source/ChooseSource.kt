package dev.sanson.lightroom.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import javax.inject.Inject

class ChooseSourceUiFactory @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is ChooseSourceScreen -> ui<ChooseSourceScreen.State> { state, modifier ->
            ChooseSource(
                state = state,
                modifier = modifier,
            )
        }

        else -> null
    }
}

@Composable
fun ChooseSource(
    state: ChooseSourceScreen.State,
    modifier: Modifier = Modifier,
) {
    TODO("Use $state, $modifier")
}
