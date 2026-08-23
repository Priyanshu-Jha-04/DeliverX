package com.example.deliverx.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Helper to create rich custom color schemes per preset
fun createDeliverXColorScheme(
    preset: AppColorPreset,
    isDark: Boolean
): ColorScheme {
    return if (isDark) {
        when (preset) {
            AppColorPreset.CYBER_VIOLET -> darkColorScheme(
                primary = DeliverXViolet,
                onPrimary = Color.White,
                primaryContainer = DeliverXDarkPurple,
                onPrimaryContainer = Color.White,
                secondary = DeliverXCyan,
                onSecondary = Color.Black,
                secondaryContainer = DeliverXCyan.copy(alpha = 0.2f),
                onSecondaryContainer = DeliverXElectricCyan,
                tertiary = DeliverXPink,
                onTertiary = Color.White,
                background = DeliverXDarkBg,
                onBackground = DeliverXTextPrimary,
                surface = DeliverXSurface,
                onSurface = DeliverXTextPrimary,
                surfaceVariant = DeliverXCardNavy,
                onSurfaceVariant = DeliverXTextSecondary,
                error = DeliverXError,
                onError = Color.White,
                outline = DeliverXBorderGlass
            )
            AppColorPreset.CYBER_CYAN -> darkColorScheme(
                primary = DeliverXCyan,
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF0E7490),
                onPrimaryContainer = Color.White,
                secondary = DeliverXBlue,
                onSecondary = Color.White,
                secondaryContainer = DeliverXBlue.copy(alpha = 0.2f),
                onSecondaryContainer = DeliverXElectricCyan,
                tertiary = DeliverXViolet,
                onTertiary = Color.White,
                background = Color(0xFF060D17),
                onBackground = DeliverXTextPrimary,
                surface = Color(0xFF0F172A),
                onSurface = DeliverXTextPrimary,
                surfaceVariant = Color(0xFF1E293B),
                onSurfaceVariant = DeliverXTextSecondary,
                error = DeliverXError,
                onError = Color.White,
                outline = DeliverXBorderGlass
            )
            AppColorPreset.AURORA_EMERALD -> darkColorScheme(
                primary = DeliverXEmerald,
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF065F46),
                onPrimaryContainer = Color.White,
                secondary = DeliverXCyan,
                onSecondary = Color.Black,
                secondaryContainer = DeliverXCyan.copy(alpha = 0.2f),
                onSecondaryContainer = DeliverXElectricCyan,
                tertiary = DeliverXAmber,
                onTertiary = Color.Black,
                background = Color(0xFF06140F),
                onBackground = DeliverXTextPrimary,
                surface = Color(0xFF0B251B),
                onSurface = DeliverXTextPrimary,
                surfaceVariant = Color(0xFF133E2D),
                onSurfaceVariant = DeliverXTextSecondary,
                error = DeliverXError,
                onError = Color.White,
                outline = DeliverXBorderGlass
            )
            AppColorPreset.SUNSET_AMBER -> darkColorScheme(
                primary = DeliverXAmber,
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF92400E),
                onPrimaryContainer = Color.White,
                secondary = DeliverXOrange,
                onSecondary = Color.Black,
                secondaryContainer = DeliverXOrange.copy(alpha = 0.2f),
                onSecondaryContainer = DeliverXAmber,
                tertiary = DeliverXRose,
                onTertiary = Color.White,
                background = Color(0xFF120C06),
                onBackground = DeliverXTextPrimary,
                surface = Color(0xFF1F140A),
                onSurface = DeliverXTextPrimary,
                surfaceVariant = Color(0xFF332010),
                onSurfaceVariant = DeliverXTextSecondary,
                error = DeliverXError,
                onError = Color.White,
                outline = DeliverXBorderGlass
            )
            AppColorPreset.NEON_ROSE -> darkColorScheme(
                primary = DeliverXRose,
                onPrimary = Color.White,
                primaryContainer = Color(0xFF9F1239),
                onPrimaryContainer = Color.White,
                secondary = DeliverXPink,
                onSecondary = Color.White,
                secondaryContainer = DeliverXPink.copy(alpha = 0.2f),
                onSecondaryContainer = DeliverXHotPink,
                tertiary = DeliverXViolet,
                onTertiary = Color.White,
                background = Color(0xFF14080D),
                onBackground = DeliverXTextPrimary,
                surface = Color(0xFF220C17),
                onSurface = DeliverXTextPrimary,
                surfaceVariant = Color(0xFF371526),
                onSurfaceVariant = DeliverXTextSecondary,
                error = DeliverXError,
                onError = Color.White,
                outline = DeliverXBorderGlass
            )
        }
    } else {
        // Light Schemes
        when (preset) {
            AppColorPreset.CYBER_VIOLET -> lightColorScheme(
                primary = Color(0xFF7C3AED),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFEDE9FE),
                onPrimaryContainer = Color(0xFF5B21B6),
                secondary = Color(0xFF0891B2),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFCFFAFE),
                onSecondaryContainer = Color(0xFF155E75),
                tertiary = Color(0xFFDB2777),
                onTertiary = Color.White,
                background = Color(0xFFF8FAFC),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFCBD5E1)
            )
            AppColorPreset.CYBER_CYAN -> lightColorScheme(
                primary = Color(0xFF0284C7),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE0F2FE),
                onPrimaryContainer = Color(0xFF0369A1),
                secondary = Color(0xFF2563EB),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFDBEAFE),
                onSecondaryContainer = Color(0xFF1D4ED8),
                tertiary = Color(0xFF7C3AED),
                onTertiary = Color.White,
                background = Color(0xFFF0F9FF),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE2E8F0),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFCBD5E1)
            )
            AppColorPreset.AURORA_EMERALD -> lightColorScheme(
                primary = Color(0xFF059669),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFD1FAE5),
                onPrimaryContainer = Color(0xFF047857),
                secondary = Color(0xFF0D9488),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFCCFBF1),
                onSecondaryContainer = Color(0xFF0F766E),
                tertiary = Color(0xFFD97706),
                onTertiary = Color.White,
                background = Color(0xFFF0FDF4),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE2E8F0),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFCBD5E1)
            )
            AppColorPreset.SUNSET_AMBER -> lightColorScheme(
                primary = Color(0xFFD97706),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFEF3C7),
                onPrimaryContainer = Color(0xFFB45309),
                secondary = Color(0xFFEA580C),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFFEDD5),
                onSecondaryContainer = Color(0xFFC2410C),
                tertiary = Color(0xFFE11D48),
                onTertiary = Color.White,
                background = Color(0xFFFFFBEB),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFCBD5E1)
            )
            AppColorPreset.NEON_ROSE -> lightColorScheme(
                primary = Color(0xFFE11D48),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFE4E6),
                onPrimaryContainer = Color(0xFFBE123C),
                secondary = Color(0xFFDB2777),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFCE7F3),
                onSecondaryContainer = Color(0xFF9D174D),
                tertiary = Color(0xFF7C3AED),
                onTertiary = Color.White,
                background = Color(0xFFFFF1F2),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
fun DeliverXTheme(
    content: @Composable () -> Unit
) {
    val themeState = LocalThemeState.current
    val context = LocalContext.current
    val systemInDark = isSystemInDarkTheme()

    val isDark = when (themeState.themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when {
        themeState.dynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> createDeliverXColorScheme(themeState.colorPreset, isDark)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}