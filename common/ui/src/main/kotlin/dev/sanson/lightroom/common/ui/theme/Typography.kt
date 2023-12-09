// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import dev.sanson.lightroom.common.ui.R

private val GoogleFontProvider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
    )

private val NotoSans =
    FontFamily(
        Font(
            googleFont = GoogleFont("Noto Sans"),
            fontProvider = GoogleFontProvider,
        ),
    )

private val JosefinSans =
    FontFamily(
        Font(
            googleFont = GoogleFont("Josefin Sans"),
            fontProvider = GoogleFontProvider,
        ),
    )

val MuzeiTypography
    @Composable get() =
        Typography(
            bodyLarge =
                MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = NotoSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            bodyMedium =
                MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = NotoSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            bodySmall =
                MaterialTheme.typography.bodySmall.copy(
                    fontFamily = NotoSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            labelLarge =
                MaterialTheme.typography.labelLarge.copy(
                    fontFamily = NotoSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            titleLarge =
                MaterialTheme.typography.titleLarge.copy(
                    fontFamily = NotoSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            headlineLarge =
                MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = NotoSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            displaySmall =
                MaterialTheme.typography.displaySmall.copy(
                    fontFamily = JosefinSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            displayMedium =
                MaterialTheme.typography.displayMedium.copy(
                    fontFamily = JosefinSans,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
        )
