package org.unzer.project.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object AppColors {
    val Primary = Color(0xFFE6005B)
    val Secondary = Color(0xFF4A3BFF)
    val Accent = Color(0xFFFFAB00)
    val BackgroundLight = Color(0xFFF5F7FA)
    val BackgroundDark = Color(0xFF1A1E2A)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceDark = Color(0xFF252A3B)
    val TextPrimaryLight = Color(0xFF1A1E2A)
    val TextPrimaryDark = Color(0xFFE5E7EB)
    val TextSecondary = Color(0xFF6B7280)
    val Error = Color(0xFFB91C1C)
    val Success = Color(0xFF059669)
}

internal val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    tertiary = AppColors.Accent,
    background = AppColors.BackgroundLight,
    surface = AppColors.SurfaceLight,
    onPrimary = AppColors.SurfaceLight,
    onSecondary = AppColors.SurfaceLight,
    onBackground = AppColors.TextPrimaryLight,
    onSurface = AppColors.TextPrimaryLight,
    error = AppColors.Error
)

internal val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.Secondary,
    tertiary = AppColors.Accent,
    background = AppColors.BackgroundDark,
    surface = AppColors.SurfaceDark,
    onPrimary = AppColors.SurfaceLight,
    onSecondary = AppColors.SurfaceLight,
    onBackground = AppColors.TextPrimaryDark,
    onSurface = AppColors.TextPrimaryDark,
    error = AppColors.Error
)