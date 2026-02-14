package com.example.z407remotecontrol.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Esquema de colores oscuro usando Tailwind
private val DarkColorScheme = darkColorScheme(
    primary = Blue.blue500,
    onPrimary = Gray.gray50,
    primaryContainer = Blue.blue700,
    onPrimaryContainer = Blue.blue100,

    secondary = Violet.violet500,
    onSecondary = Gray.gray50,
    secondaryContainer = Violet.violet700,
    onSecondaryContainer = Violet.violet100,

    tertiary = Emerald.emerald500,
    onTertiary = Gray.gray50,
    tertiaryContainer = Emerald.emerald700,
    onTertiaryContainer = Emerald.emerald100,

    error = Red.red500,
    onError = Gray.gray50,
    errorContainer = Red.red700,
    onErrorContainer = Red.red100,

    background = Gray.gray900,
    onBackground = Gray.gray50,

    surface = Gray.gray800,
    onSurface = Gray.gray50,
    surfaceVariant = Gray.gray700,
    onSurfaceVariant = Gray.gray300,

    outline = Gray.gray600,
    outlineVariant = Gray.gray700
)

// Esquema de colores claro usando Tailwind
private val LightColorScheme = lightColorScheme(
    primary = Blue.blue600,
    onPrimary = Gray.gray50,
    primaryContainer = Blue.blue100,
    onPrimaryContainer = Blue.blue900,

    secondary = Violet.violet600,
    onSecondary = Gray.gray50,
    secondaryContainer = Violet.violet100,
    onSecondaryContainer = Violet.violet900,

    tertiary = Emerald.emerald600,
    onTertiary = Gray.gray50,
    tertiaryContainer = Emerald.emerald100,
    onTertiaryContainer = Emerald.emerald900,

    error = Red.red600,
    onError = Gray.gray50,
    errorContainer = Red.red100,
    onErrorContainer = Red.red900,

    background = Gray.gray50,
    onBackground = Gray.gray900,

    surface = Gray.gray50,
    onSurface = Gray.gray900,
    surfaceVariant = Gray.gray100,
    onSurfaceVariant = Gray.gray700,

    outline = Gray.gray400,
    outlineVariant = Gray.gray300
)

@Composable
fun Z407RemoteControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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