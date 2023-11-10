package dev.sanson.lightroom.ui.filter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.sdk.model.Asset

@Composable
internal fun ReviewSection(
    flag: Asset.Flag?,
    onFlagChange: (Asset.Flag?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable { mutableStateOf(false) }

    SectionLayout(
        title = "Review",
        description = "Accepted or Rejected images",
        selected = selected,
        onSelectedChange = { selected = it },
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
