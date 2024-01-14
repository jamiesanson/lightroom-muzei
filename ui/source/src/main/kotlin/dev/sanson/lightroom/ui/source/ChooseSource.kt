// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.source

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.ui.LightroomForMuzeiTheme
import dev.sanson.lightroom.common.ui.component.PreviewLightDark
import dev.sanson.lightroom.common.ui.component.StepHeader
import dev.sanson.lightroom.core.search.SearchConfig
import dev.sanson.lightroom.feature.source.R
import dev.sanson.lightroom.screens.ChooseSourceScreen

@CircuitInject(ChooseSourceScreen::class, SingletonComponent::class)
@Composable
fun ChooseSource(
    state: ChooseSourceState,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize()) {
        Box(Modifier.systemBarsPadding()) {
            StepHeader(
                stepNumber = 1,
                stepName = stringResource(R.string.choose_a_source),
                modifier =
                    Modifier
                        .padding(24.dp)
                        .padding(top = 64.dp)
                        .align(Alignment.TopStart),
            )

            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
            ) {
                SourceRow(
                    title = stringResource(R.string.catalog_source_title),
                    subtitle = stringResource(R.string.catalog_source_description),
                    selected = state.selectedSource is SearchConfig.Source.Catalog,
                    onClick = { state.eventSink(ChooseSourceEvent.OnChooseCatalog) },
                )

                Spacer(Modifier.height(16.dp))

                SourceRow(
                    title = stringResource(R.string.album_source_title),
                    subtitle = stringResource(R.string.album_source_description),
                    selected = state.selectedSource is SearchConfig.Source.Album,
                    onClick = { state.eventSink(ChooseSourceEvent.OnChooseAlbum) },
                )
            }

            Button(
                onClick = { state.eventSink(ChooseSourceEvent.OnConfirm) },
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp),
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
            ) {
                Spacer(Modifier.size(8.dp))

                Text(
                    text =
                        if (state.selectedSource is SearchConfig.Source.Album) {
                            stringResource(id = R.string.button_choose_album)
                        } else {
                            stringResource(id = R.string.button_filter_catalog)
                        },
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.animateContentSize(),
                )

                Spacer(Modifier.size(16.dp))

                Icon(Icons.Default.ArrowForward, contentDescription = "")
            }
        }
    }
}

@Composable
private fun SourceRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderWidth by animateDpAsState(
        if (selected) 2.dp else 1.dp,
        label = "Selection border width",
    )
    val borderAlpha by animateFloatAsState(
        if (selected) 0.64f else 0.16f,
        label = "Selection border opacity",
    )
    val backgroundColor by animateColorAsState(
        MaterialTheme.colorScheme.surfaceColorAtElevation(if (selected) 2.dp else 0.dp),
        label = "Selection background color",
    )

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha),
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(onClick = onClick),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(8.dp)
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.padding(4.dp),
            )

            Column(
                Modifier
                    .padding(4.dp)
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.size(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.54f),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ChooseSourcePreview() {
    LightroomForMuzeiTheme {
        ChooseSource(state = ChooseSourceState(SearchConfig.Source.Catalog) {})
    }
}
