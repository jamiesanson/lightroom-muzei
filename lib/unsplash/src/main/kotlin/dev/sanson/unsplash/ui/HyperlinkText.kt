// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.unsplash.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import kotlinx.collections.immutable.ImmutableMap

/**
 * Implementation modified from: https://gist.github.com/stevdza-san/ff9dbec0e072d8090e1e6d16e6b73c91
 */
@Composable
fun HyperlinkText(
    fullText: String,
    hyperLinks: ImmutableMap<String, String>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    linkTextColor: Color = Color.Blue,
    linkTextFontWeight: FontWeight = FontWeight.Normal,
    linkTextDecoration: TextDecoration = TextDecoration.None,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    val annotatedString =
        buildAnnotatedString {
            append(fullText)

            for ((key, value) in hyperLinks) {
                val startIndex = fullText.indexOf(key)
                val endIndex = startIndex + key.length
                addStyle(
                    style =
                        SpanStyle(
                            color = linkTextColor,
                            fontSize = fontSize,
                            fontWeight = linkTextFontWeight,
                            textDecoration = linkTextDecoration,
                        ),
                    start = startIndex,
                    end = endIndex,
                )
                addLink(
                    url = LinkAnnotation.Url(value),
                    start = startIndex,
                    end = endIndex,
                )
            }
            addStyle(
                style =
                    SpanStyle(
                        fontSize = fontSize,
                    ),
                start = 0,
                end = fullText.length,
            )
        }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = textStyle,
    )
}
