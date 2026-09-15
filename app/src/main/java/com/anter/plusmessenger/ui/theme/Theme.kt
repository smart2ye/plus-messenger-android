package com.anter.plusmessenger.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private val LightColors = lightColorScheme(
    primary = AnterBlue,
    onPrimary = Color.White,
    primaryContainer = AnterBlueLight,
    secondary = AnterBlueDark,
    background = AnterBg,
    onBackground = AnterText,
    surface = AnterBg,
    onSurface = AnterText,
    surfaceVariant = AnterSurface,
    onSurfaceVariant = AnterTextMuted,
    error = AnterError
)

private val DarkColors = darkColorScheme(
    primary = AnterBlueLight,
    onPrimary = Color.White,
    primaryContainer = AnterBlue,
    secondary = AnterBlue,
    background = Color(0xFF18191A),
    onBackground = Color(0xFFE4E6EB),
    surface = Color(0xFF242526),
    onSurface = Color(0xFFE4E6EB),
    surfaceVariant = Color(0xFF3A3B3C),
    onSurfaceVariant = Color(0xFFB0B3B8),
    error = AnterError
)

@Composable
fun PlusMessengerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity() ?: return@SideEffect
            val window = activity.window ?: return@SideEffect
            try {
                window.statusBarColor = colors.primary.toArgb()
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = false
            } catch (_: Throwable) { }
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
