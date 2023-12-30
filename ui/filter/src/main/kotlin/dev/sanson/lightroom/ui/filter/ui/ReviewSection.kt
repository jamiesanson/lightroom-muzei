// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.filter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.common.ui.LightroomForMuzeiTheme
import dev.sanson.lightroom.common.ui.component.PreviewLightDark
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.ui.filter.R

@Composable
internal fun ReviewSection(
    flag: Asset.Flag?,
    active: Boolean,
    onActiveStateChange: (Boolean) -> Unit,
    onFlagChange: (Asset.Flag?) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionLayout(
        title = stringResource(R.string.review),
        description = stringResource(R.string.accepted_or_rejected_images),
        selected = active,
        onSelectedChange = onActiveStateChange,
        modifier = modifier,
    ) {
        FlagRow(flag = flag, onFlagChange = onFlagChange)
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
            horizontalArrangement = Arrangement.spacedBy(36.dp),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_accept),
                contentDescription = "Picked",
                tint =
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = if (flag == Asset.Flag.Picked) 1f else 0.24f),
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable { onFlagChange(Asset.Flag.Picked) },
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_neutral),
                contentDescription = "Any",
                tint =
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = if (flag == null) 1f else 0.24f),
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable { onFlagChange(null) },
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_reject),
                contentDescription = "Rejected",
                tint =
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = if (flag == Asset.Flag.Rejected) 1f else 0.24f),
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable { onFlagChange(Asset.Flag.Rejected) },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReviewSectionPreview() {
    LightroomForMuzeiTheme {
        Surface {
            ReviewSection(
                flag = Asset.Flag.Picked,
                active = true,
                onActiveStateChange = {},
                onFlagChange = {},
            )
        }
    }
}
