package com.example.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
  primary = RoyalBlue,
  onPrimary = Color.White,
  primaryContainer = SoftBlue,
  onPrimaryContainer = RoyalBlue,
  secondary = DeepNavy,
  onSecondary = Color.White,
  secondaryContainer = SecondaryBackground,
  onSecondaryContainer = PrimaryText,
  tertiary = PositiveGreen,
  onTertiary = Color.White,
  background = MainBackground,
  onBackground = PrimaryText,
  surface = CardBackground,
  onSurface = PrimaryText,
  surfaceVariant = SecondaryBackground,
  onSurfaceVariant = SecondaryText,
  outline = BorderColor,
  outlineVariant = DividerColor
)

private val DarkColorScheme = darkColorScheme(
  primary = RoyalBlue,
  onPrimary = Color.White,
  primaryContainer = DarkSurfaceElevated,
  onPrimaryContainer = SoftBlue,
  secondary = SoftBlue,
  onSecondary = DeepNavy,
  secondaryContainer = DarkSurfaceElevated,
  onSecondaryContainer = DarkPrimaryText,
  tertiary = PositiveGreen,
  onTertiary = Color.White,
  background = DarkMainBackground,
  onBackground = DarkPrimaryText,
  surface = DarkCardBackground,
  onSurface = DarkPrimaryText,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = DarkSecondaryText,
  outline = DarkDivider,
  outlineVariant = DarkDivider
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
