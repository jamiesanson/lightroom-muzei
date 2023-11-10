package dev.sanson.lightroom.ui.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.ui.MuzeiLightroomTheme
import dev.sanson.lightroom.common.ui.component.Equality
import dev.sanson.lightroom.common.ui.component.PreviewLightDark
import dev.sanson.lightroom.common.ui.component.StepHeader
import dev.sanson.lightroom.screens.FilterAssetsScreen
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.ui.filter.ui.KeywordSection
import dev.sanson.lightroom.ui.filter.ui.RatingSection
import kotlinx.collections.immutable.persistentListOf

@CircuitInject(FilterAssetsScreen::class, SingletonComponent::class)
@Composable
fun FilterAssets(
    state: FilterAssetsState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            StepHeader(
                stepNumber = state.stepNumber,
                stepName = stringResource(R.string.filter_images),
                modifier =
                    Modifier
                        .padding(24.dp)
                        .padding(top = 64.dp),
            )
        },
        modifier =
            modifier
                .fillMaxSize(),
    ) { paddingValues ->
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
                .systemBarsPadding()
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
        ) {
            Spacer(Modifier.size(8.dp))

            KeywordSection(
                keywords = state.keywords,
                onAddKeyword = { state.eventSink(FilterAssetsEvent.AddKeyword(it)) },
                onRemoveKeyword = { state.eventSink(FilterAssetsEvent.RemoveKeyword(it)) },
            )

            Divider(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )

            RatingSection(
                rating = state.rating,
                equality = state.equality,
                onRatingChange = { state.eventSink(FilterAssetsEvent.UpdateRating(it)) },
                onEqualityChange = { state.eventSink(FilterAssetsEvent.UpdateEquality(it)) },
            )

            Divider(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )

            Text(
                text = "Review",
                style = MaterialTheme.typography.titleMedium,
            )

            FlagRow(
                flag = state.flag,
                onFlagChange = { state.eventSink(FilterAssetsEvent.UpdateFlag(it)) },
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun FlagRow(
    flag: Asset.Flag?,
    onFlagChange: (Asset.Flag?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "Picked",
                tint =
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = if (flag == Asset.Flag.Picked) 1f else 0.32f),
                modifier =
                    Modifier
                        .clickable { onFlagChange(Asset.Flag.Picked) },
            )

            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "Any",
                tint =
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = if (flag == null) 1f else 0.32f),
                modifier =
                    Modifier
                        .rotate(90f)
                        .clickable { onFlagChange(null) },
            )

            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "Rejected",
                tint =
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = if (flag == Asset.Flag.Rejected) 1f else 0.32f),
                modifier =
                    Modifier
                        .rotate(180f)
                        .clickable { onFlagChange(Asset.Flag.Rejected) },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FilterAssetsPreview() {
    MuzeiLightroomTheme {
        FilterAssets(
            state =
                FilterAssetsState(
                    stepNumber = 2,
                    keywords =
                        persistentListOf(
                            "wallpaper",
                            "wallpaper",
                            "walper",
                            "wadfllpaper",
                            "wallpgsdaper",
                            "wallpaper",
                            "wallpgsdfgsdfgaper",
                            "wallpaper",
                        ),
                    rating = 3,
                    equality = Equality.GreaterThan,
                    flag = Asset.Flag.Picked,
                    eventSink = {},
                ),
        )
    }
}
