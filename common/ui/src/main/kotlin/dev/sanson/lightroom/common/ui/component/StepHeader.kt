// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.common.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanson.lightroom.common.ui.MuzeiLightroomTheme
import dev.sanson.lightroom.common.ui.R

@Composable
fun StepHeader(
    stepNumber: Int,
    stepName: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.step, stepNumber),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = stepName,
            style = MaterialTheme.typography.displayMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun StepHeaderPreview() {
    MuzeiLightroomTheme {
        StepHeader(stepNumber = 1, stepName = "Choose a source")
    }
}
