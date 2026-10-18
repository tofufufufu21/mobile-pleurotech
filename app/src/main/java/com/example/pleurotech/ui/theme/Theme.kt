package com.example.pleurotech.ui.theme

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
    primary = Color(0xFF22C55E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF143820),
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color.White,
    background = Color(0xFF0B130E),
    onBackground = Color(0xFFF0FDF4),
    surface = Color(0xFF122117),
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = Color(0xFF1B3224),
    onSurfaceVariant = Color(0xFF86A792),
    outline = Color(0x334ADE80),
    outlineVariant = Color(0x1F4ADE80)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF16A34A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBF7EE),
    onPrimaryContainer = Color(0xFF14532D),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    background = Color(0xFFF6F8F6),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFEEF4EF),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE5EAE5),
    outlineVariant = Color(0xFFD8E2D9)
)

@Composable
fun PleurotechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}