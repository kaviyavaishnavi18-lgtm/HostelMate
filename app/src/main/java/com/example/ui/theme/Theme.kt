package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = NeoDarkText,
    primaryContainer = PastelLilacDark,
    onPrimaryContainer = Color.White,
    secondary = PastelPeachDark,
    onSecondary = Color.White,
    tertiary = PastelMintDark,
    background = Color(0xFF181520),
    onBackground = Color.White,
    surface = Color(0xFF231F30),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2F2A3F),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF4A445C)
)

private val LightColorScheme = lightColorScheme(
    primary = NeoDarkOutline,
    onPrimary = Color.White,
    primaryContainer = PastelLilacSurface,
    onPrimaryContainer = NeoDarkText,
    secondary = PastelPeach,
    onSecondary = NeoDarkText,
    tertiary = PastelMint,
    background = PastelLilacBackground,
    onBackground = NeoDarkText,
    surface = Color.White,
    onSurface = NeoDarkText,
    surfaceVariant = PastelLilacSurface,
    onSurfaceVariant = NeoMutedText,
    outline = NeoDarkOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Always enforce our playful pastel aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
