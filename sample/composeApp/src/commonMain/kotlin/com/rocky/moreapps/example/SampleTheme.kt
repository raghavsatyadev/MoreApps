@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rocky.moreapps.example

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable

/** Wallpaper colors where the platform has them (Android 12+), else the expressive baseline. */
@Composable internal expect fun platformColorScheme(dark: Boolean): ColorScheme?

@Composable
internal fun SampleTheme(content: @Composable () -> Unit) {
  val dark = isSystemInDarkTheme()
  val scheme =
    platformColorScheme(dark) ?: if (dark) darkColorScheme() else expressiveLightColorScheme()
  MaterialExpressiveTheme(
    colorScheme = scheme,
    motionScheme = MotionScheme.expressive(),
    content = content,
  )
}
