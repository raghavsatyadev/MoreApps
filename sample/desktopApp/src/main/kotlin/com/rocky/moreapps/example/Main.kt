package com.rocky.moreapps.example

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.raghavsatyadev.moreapps.MoreApps
import io.github.raghavsatyadev.moreapps.initialize

fun main() {
  MoreApps.initialize(SampleConfig.JSON_URL, SampleConfig.PACKAGE_NAME, versionCode = 24)
  startSampleKoin()
  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "MoreApps",
      state = rememberWindowState(width = 480.dp, height = 800.dp),
    ) {
      App()
    }
  }
}
