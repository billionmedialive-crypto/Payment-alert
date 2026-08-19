package com.paymentannouncer.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF00251A),
    secondary = GoldAccent,
    onSecondary = Color(0xFF241A00),
    background = MidnightBg,
    onBackground = TextPrimary,
    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = MidnightBorder,
    error = DangerRed
)

@Composable
fun PaymentAnnouncerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
