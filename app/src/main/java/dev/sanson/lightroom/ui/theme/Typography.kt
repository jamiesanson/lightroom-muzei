package dev.sanson.lightroom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import dev.sanson.lightroom.R

private val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val NotoSans = FontFamily(
    Font(
        googleFont = GoogleFont("Noto Sans"),
        fontProvider = GoogleFontProvider,
    ),
)

val MuzeiTypography
    @Composable get() = Typography(
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = NotoSans,
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = NotoSans,
        ),
        bodySmall = MaterialTheme.typography.bodySmall.copy(
            fontFamily = NotoSans,
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontFamily = NotoSans,
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontFamily = NotoSans,
        ),
    )
