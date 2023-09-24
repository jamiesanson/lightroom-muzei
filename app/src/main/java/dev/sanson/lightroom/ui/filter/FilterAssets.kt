package dev.sanson.lightroom.ui.filter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.sanson.lightroom.R
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.AddKeyword
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.RemoveKeyword
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.UpdateRating
import dev.sanson.lightroom.ui.filter.FilterAssetsScreen.Event.UpdateUpToMax
import dev.sanson.lightroom.ui.theme.MuzeiLightroomTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
    Scaffold(modifier.fillMaxSize()) { paddingValues ->
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(paddingValues),
        ) {
            Spacer(Modifier.size(54.dp))

            Text(
                text = "Keywords",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.size(8.dp))

            KeywordChipGroup(
                keywords = state.keywords,
                onAddKeyword = { state.eventSink(AddKeyword(it)) },
                onRemoveKeyword = { state.eventSink(RemoveKeyword(it)) },
            )

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = "Rating",
                style = MaterialTheme.typography.titleMedium,
            )

            RatingRow(
                rating = state.rating,
                upToMax = state.ratingUpToMax,
                onRatingChange = { state.eventSink(UpdateRating(it)) },
                onUpToMaxChange = { state.eventSink(UpdateUpToMax(it)) },
                modifier = Modifier.padding(top = 8.dp),
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
private fun KeywordChipGroup(
    keywords: ImmutableList<String>,
    onAddKeyword: (String) -> Unit,
    onRemoveKeyword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth(1f)
            .wrapContentHeight(align = Alignment.Top)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        keywords.forEach { keyword ->
            KeywordChip(
                keyword = keyword,
                onRemoveKeyword = { onRemoveKeyword(keyword) },
            )
        }

        KeywordTextField(
            onAddKeyword = onAddKeyword,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun KeywordTextField(
    onAddKeyword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var keywordText by remember { mutableStateOf("") }
    val textColor = MaterialTheme.colorScheme.onSurface

    BasicTextField(
        value = keywordText,
        onValueChange = { keywordText = it },
        singleLine = true,
        textStyle = MaterialTheme.typography.labelLarge.copy(color = textColor),
        cursorBrush = SolidColor(textColor),
        keyboardActions = KeyboardActions(
            onDone = {
                onAddKeyword(keywordText)
                keywordText = ""
            },
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
        ),
        modifier = modifier
            .requiredHeightIn(min = InputChipDefaults.Height)
            .requiredWidthIn(min = 48.dp)
            .fillMaxWidth(),
    )
}

/**
 * TODO:
 * * Some way of clearing the rating
 * * Colors, assets match
 */
@Composable
private fun RatingRow(
    rating: Int,
    upToMax: Boolean,
    onRatingChange: (Int) -> Unit,
    onUpToMaxChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        CompositionLocalProvider(LocalIndication provides rememberRipple(bounded = false)) {
            Row(Modifier.align(Alignment.Center)) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface
                            .copy(alpha = if (index < rating) 1f else 0.32f),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(28.dp)
                            .clickable { onRatingChange(index + 1) },
                    )
                }
            }

            val iconRotation by animateFloatAsState(
                targetValue = if (!upToMax) 180f else 0f,
                label = "Thumb rotation",
            )

            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onUpToMaxChange(!upToMax) }
                    .padding(4.dp)
                    .size(24.dp)
                    .rotate(iconRotation)
                    .align(Alignment.CenterStart),
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun KeywordChip(
    keyword: String,
    onRemoveKeyword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The following removes the implicit padding around clickable elements in the chip,
    // used to enforce minimum touch target sizes. We turn this off here, as the target height with
    // vertical padding DOES equal minimum touch target size. The vertical arrangement
    // originally leads to far bigger vertical spacing than necessary.
    //
    // https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#LocalMinimumInteractiveComponentEnforcement()
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        InputChip(
            selected = false,
            onClick = {},
            label = {
                Text(
                    text = keyword,
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            colors = InputChipDefaults.inputChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = "tag",
                    modifier = Modifier
                        .size(14.dp),
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove $keyword filter",
                    modifier = Modifier
                        .size(InputChipDefaults.IconSize)
                        .clickable { onRemoveKeyword() },
                )
            },
            modifier = modifier,
            border = null,
            shape = RoundedCornerShape(4.dp),
        )
    }
}

@Preview
@Composable
fun FilterAssetsPreview() {
    MuzeiLightroomTheme(darkTheme = false) {
        FilterAssets(
            state = FilterAssetsScreen.State(
                keywords = persistentListOf(
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
                ratingUpToMax = false,
                flag = Asset.Flag.Picked,
                eventSink = {},
            ),
        )
    }
}
