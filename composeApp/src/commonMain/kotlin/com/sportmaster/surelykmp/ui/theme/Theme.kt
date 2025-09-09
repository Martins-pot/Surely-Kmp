package com.mertswork.footyreserve.ui.theme



import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = BlackFaded,         // Your new primary color
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    background = BlackFaded,
    onBackground = Color.White,
    surface = BlackFaded,
    onSurface = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = BlackFaded,         // Your new primary color
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    background = BlackFaded,
    onBackground = Color.White,
    surface = BlackFaded,
    onSurface = Color.White,
)


@Composable
fun FootyReserveTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
