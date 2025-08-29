package com.example.mychatapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Unique Custom Colors
val ChatPurple80 = Color(0xFF6200EE)
val ChatPurpleGrey80 = Color(0xFF3700B3)
val ChatPink80 = Color(0xFFBB86FC)

val ChatPurple40 = Color(0xFF6200EE)
val ChatPurpleGrey40 = Color(0xFF3700B3)
val ChatPink40 = Color(0xFFBB86FC)

private val DarkColorScheme = darkColorScheme(
    primary = ChatPurple80,
    secondary = ChatPurpleGrey80,
    tertiary = ChatPink80,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ChatPurple40,
    secondary = ChatPurpleGrey40,
    tertiary = ChatPink40,
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun MyChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
