package com.mygym.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PulseColorScheme = darkColorScheme(
    primary = PulseBlue,
    onPrimary = PulseTextPrimary,
    primaryContainer = PulseSurfaceVariant,
    onPrimaryContainer = PulseTextPrimary,
    secondary = PulsePink,
    onSecondary = PulseTextPrimary,
    secondaryContainer = PulseSurfaceVariant,
    onSecondaryContainer = PulseTextSecondary,
    tertiary = PulseLightBlue,
    onTertiary = PulseBackground,
    background = PulseBackground,
    onBackground = PulseTextPrimary,
    surface = PulseSurface,
    onSurface = PulseTextPrimary,
    surfaceVariant = PulseSurfaceVariant,
    onSurfaceVariant = PulseTextSecondary,
    outline = PulseBorder
)

@Composable
fun MyGymTheme(
    darkTheme: Boolean = true, // Default to Pulse Dark Theme
    content: @Composable () -> Unit
) {
    val colorScheme = PulseColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = PulseBackground.toArgb()
                window.navigationBarColor = PulseNavBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
