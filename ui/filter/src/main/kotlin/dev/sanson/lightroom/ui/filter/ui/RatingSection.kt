// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.filter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.common.ui.component.Equality
import dev.sanson.lightroom.common.ui.component.EqualityToggle

@Composable
internal fun RatingSection(
    rating: Int,
    equality: Equality,
    active: Boolean,
    onActiveStateChange: (Boolean) -> Unit,
    onRatingChange: (Int) -> Unit,
    onEqualityChange: (Equality) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionLayout(
        title = "Rating",
        description = "Use images above, below or equal to a star rating",
        selected = active,
        onSelectedChange = onActiveStateChange,
        modifier = modifier,
    ) {
        RatingRow(
            rating = rating,
            equality = equality,
            onRatingChange = onRatingChange,
            onEqualityChange = onEqualityChange,
        )
    }
}

@Composable
private fun RatingRow(
    rating: Int,
    equality: Equality,
    onRatingChange: (Int) -> Unit,
    onEqualityChange: (Equality) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.align(Alignment.Center)) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "",
                    tint =
                        MaterialTheme.colorScheme.onSurface
                            .copy(alpha = if (index < rating) 1f else 0.32f),
                    modifier =
                        Modifier
                            .padding(4.dp)
                            .size(28.dp)
                            .clickable(
                                indication =
                                    ripple(
                                        bounded = false,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onRatingChange(index + 1) },
                )
            }
        }

        EqualityToggle(
            equality = equality,
            onEqualityChange = onEqualityChange,
            modifier = Modifier.size(48.dp),
        )
    }
}
