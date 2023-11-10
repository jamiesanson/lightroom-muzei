package dev.sanson.lightroom.ui.filter.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.ui.filter.R
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun KeywordSection(
    keywords: ImmutableList<String>,
    onAddKeyword: (String) -> Unit,
    onRemoveKeyword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: This should probably be in the presenter
    var selected by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { selected = !selected }
            .padding(vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier) {
            Checkbox(
                checked = selected,
                onCheckedChange = { selected = it },
                modifier = Modifier.padding(end = 8.dp),
            )
            Column {
                Text(
                    text = "Keywords",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(Modifier.size(4.dp))

                Text(
                    text = "Use images with a specific keyword",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        AnimatedVisibility(visible = selected) {
            KeywordChipGroup(
                keywords = keywords,
                onAddKeyword = onAddKeyword,
                onRemoveKeyword = onRemoveKeyword,
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 16.dp),
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
        modifier =
            modifier
                .fillMaxWidth(1f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                )
                .padding(8.dp)
                .requiredHeightIn(min = 24.dp),
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
            modifier =
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
        )
    }
}

@Composable
private fun KeywordTextField(
    onAddKeyword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var keywordText by remember { mutableStateOf("") }
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier
            .requiredHeightIn(min = 32.dp)
            .fillMaxWidth(),
    ) {
        BasicTextField(
            value = keywordText,
            onValueChange = { keywordText = it },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        onAddKeyword(keywordText)
                        keywordText = ""
                    },
                ),
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
            modifier =
                Modifier
                    .requiredWidthIn(min = 48.dp)
                    .fillMaxWidth()
                    .padding(start = 4.dp)
                    .align(Alignment.CenterStart),
        )
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
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors =
                InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = "tag",
                    modifier =
                        Modifier
                            .size(14.dp),
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove $keyword filter",
                    modifier =
                        Modifier
                            .size(InputChipDefaults.IconSize)
                            .clickable { onRemoveKeyword() },
                )
            },
            modifier = modifier,
            border = null,
            shape = RoundedCornerShape(8.dp),
        )
    }
}
