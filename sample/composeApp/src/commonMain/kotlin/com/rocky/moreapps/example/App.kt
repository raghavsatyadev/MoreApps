@file:OptIn(ExperimentalMaterial3Api::class)

package com.rocky.moreapps.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rocky.moreapps.example.resources.Res
import com.rocky.moreapps.example.resources.app_title
import com.rocky.moreapps.example.resources.tab_more_apps
import com.rocky.moreapps.example.resources.tab_updater
import io.github.raghavsatyadev.moreapps.ui.UpdatePromptHost
import org.jetbrains.compose.resources.stringResource

/** The sample: one tab per library feature. Call `MoreApps.initialize(...)` before this. */
@Composable
public fun App() {
  SampleTheme {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }
    Scaffold(
      topBar = { TopAppBar(title = { Text(stringResource(Res.string.app_title)) }) },
      snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
      Column(Modifier.padding(padding)) {
        PrimaryTabRow(selectedTabIndex = tab) {
          Tab(
            selected = tab == 0,
            onClick = { tab = 0 },
            text = { Text(stringResource(Res.string.tab_more_apps)) },
            icon = { Icon(Icons.Rounded.Apps, contentDescription = null) },
          )
          Tab(
            selected = tab == 1,
            onClick = { tab = 1 },
            text = { Text(stringResource(Res.string.tab_updater)) },
            icon = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null) },
          )
        }
        AnimatedContent(targetState = tab) { current ->
          if (current == 0) MoreAppsTab(snackbar) else UpdaterTab()
        }
      }
    }
    // The real thing: shows whatever prompt the JSON asks for, on every start.
    UpdatePromptHost()
  }
}
