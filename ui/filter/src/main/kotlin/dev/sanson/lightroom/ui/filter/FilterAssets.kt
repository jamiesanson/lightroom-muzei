package dev.sanson.lightroom.ui.filter

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import dev.sanson.lightroom.ui.filter.ui.ReviewSection
import kotlinx.collections.immutable.persistentListOf

@CircuitInject(FilterAssetsScreen::class, SingletonComponent::class)
@Composable
fun FilterAssets(
    state: FilterAssetsState,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        Box(Modifier.systemBarsPadding()) {
            Column {
                StepHeader(
                    stepNumber = state.stepNumber,
                    stepName = stringResource(R.string.filter_images),
                    modifier =
                        Modifier
                            .padding(24.dp)
                            .padding(top = 64.dp),
                )

                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
                ) {
                    Spacer(Modifier.size(8.dp))

                    KeywordSection(
                        keywords = state.keywords,
                        onAddKeyword = { state.eventSink(FilterAssetsEvent.AddKeyword(it)) },
                        onRemoveKeyword = { state.eventSink(FilterAssetsEvent.RemoveKeyword(it)) },
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                    )

                    RatingSection(
                        rating = state.rating,
                        equality = state.equality,
                        onRatingChange = { state.eventSink(FilterAssetsEvent.UpdateRating(it)) },
                        onEqualityChange = { state.eventSink(FilterAssetsEvent.UpdateEquality(it)) },
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                    )

                    ReviewSection(
                        flag = state.flag,
                        onFlagChange = { state.eventSink(FilterAssetsEvent.UpdateFlag(it)) },
                    )
                }
            }

            Button(
                onClick = { state.eventSink(FilterAssetsEvent.Confirm) },
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp),
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
            ) {
                Spacer(Modifier.size(8.dp))

                Text(
                    text = "Review",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimary,
                )

                Spacer(Modifier.size(16.dp))

                Icon(Icons.Default.ArrowForward, contentDescription = "")
            }
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
