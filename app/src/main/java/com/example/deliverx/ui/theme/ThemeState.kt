package com.example.deliverx.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val title: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class AppColorPreset(
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val gradient: Brush
) {
    CYBER_VIOLET(
        title = "Cyber Violet",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFC084FC),
        gradient = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))
    ),
    CYBER_CYAN(
        title = "Cyber Cyan",
        primaryColor = Color(0xFF06B6D4),
        secondaryColor = Color(0xFF3B82F6),
        gradient = Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)))
    ),
    AURORA_EMERALD(
        title = "Aurora Emerald",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF06B6D4),
        gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4)))
    ),
    SUNSET_AMBER(
        title = "Sunset Amber",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFF7A00),
        gradient = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFF7A00)))
    ),
    NEON_ROSE(
        title = "Neon Rose",
        primaryColor = Color(0xFFF43F5E),
        secondaryColor = Color(0xFFEC4899),
        gradient = Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFEC4899)))
    )
}

class ThemeState(context: Context) {
    private val prefs = context.getSharedPreferences("app_theme_prefs", Context.MODE_PRIVATE)

    var themeMode by mutableStateOf(
        try {
            AppThemeMode.valueOf(prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name)
        } catch (e: Exception) {
            AppThemeMode.DARK
        }
    )
        private set

    var colorPreset by mutableStateOf(
        try {
            AppColorPreset.valueOf(prefs.getString("color_preset", AppColorPreset.CYBER_VIOLET.name) ?: AppColorPreset.CYBER_VIOLET.name)
        } catch (e: Exception) {
            AppColorPreset.CYBER_VIOLET
        }
    )
        private set

    var dynamicColorEnabled by mutableStateOf(
        prefs.getBoolean("dynamic_color", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    )
        private set

    fun updateThemeMode(mode: AppThemeMode) {
        themeMode = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun updateColorPreset(preset: AppColorPreset) {
        colorPreset = preset
        dynamicColorEnabled = false
        prefs.edit()
            .putString("color_preset", preset.name)
            .putBoolean("dynamic_color", false)
            .apply()
    }

    fun updateDynamicColor(enabled: Boolean) {
        dynamicColorEnabled = enabled
        prefs.edit().putBoolean("dynamic_color", enabled).apply()
    }
}

val LocalThemeState = staticCompositionLocalOf<ThemeState> {
    error("ThemeState not provided")
}
