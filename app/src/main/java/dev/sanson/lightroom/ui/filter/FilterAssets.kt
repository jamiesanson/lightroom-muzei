package dev.sanson.lightroom.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import javax.inject.Inject

class FilterAssetsUiFactory @Inject constructor() : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is FilterAssetsScreen -> ui<FilterAssetsScreen.State> { state, modifier ->
            FilterAssets(
                state = state,
                modifier = modifier,
            )
        }

        else -> null
    }
}

@Composable
private fun FilterAssets(
    state: FilterAssetsScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier.fillMaxSize()) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(it),
        ) {
            Spacer(Modifier.size(54.dp))

            Text(
                text = "Keywords",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.size(8.dp))

            KeywordChipGroup(state)

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = "Rating",
                style = MaterialTheme.typography.titleMedium,
            )

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = "Review",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
private fun KeywordChipGroup(state: FilterAssetsScreen.State) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(1f)
            .wrapContentHeight(align = Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        state.keywords.forEach { keyword ->
            InputChip(
                selected = false,
                onClick = { /*TODO*/ },
                label = { Text(keyword) },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove $keyword filter",
                        modifier = Modifier.size(InputChipDefaults.IconSize),
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun FilterAssetsPreview() {
    MuzeiLightroomTheme(darkTheme = true) {
        FilterAssets(
            state = FilterAssetsScreen.State(
                keywords = listOf(
                    "wallpaper",
                    "wallpaper",
                    "walper",
                    "wadfllpaper",
                    "wallpgsdaper",
                    "wallpaper",
                    "wallpgsdfgsdfgaper",
                    "wallpaper",
                ),
                rating = 3..5,
                flag = Asset.Flag.Picked,
                eventSink = {},
            ),
        )
    }
}
