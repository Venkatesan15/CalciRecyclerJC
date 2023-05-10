package com.example.calculatorjc.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import com.example.calculatorjc.R

private val DarkColorPalette = darkColors(
    primary = red,
    primaryVariant = white,
    secondary = lightPurple,
    onBackground = black,
    onPrimary = white,
    onSecondary = white,
    secondaryVariant = black
)

private val LightColorPalette = lightColors(
    primary = Teal200,
    primaryVariant = Purple700,
    secondary = Teal200,
    onBackground = white,
    onPrimary = black,
    onSecondary = black,
    secondaryVariant = cloud

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun CalculatorJCTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) view.resources.getColor(R.color.black) else view.resources.getColor(R.color.cloud)
        }
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}