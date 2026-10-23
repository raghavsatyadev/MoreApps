@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.raghavsatyadev.moreapps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.raghavsatyadev.moreapps.MoreApps
import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.github.raghavsatyadev.moreapps.resources.Res
import io.github.raghavsatyadev.moreapps.resources.more_apps_close
import io.github.raghavsatyadev.moreapps.resources.more_apps_empty
import io.github.raghavsatyadev.moreapps.resources.more_apps_error
import io.github.raghavsatyadev.moreapps.resources.more_apps_retry
import io.github.raghavsatyadev.moreapps.resources.more_apps_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * The list of your other apps. Colors, shapes and type come from the host's `MaterialTheme`.
 *
 * @param onDismiss called once, when the user closes the dialog.
 * @param title defaults to "More Apps".
 * @param hidePackages package names to leave out (besides entries with `show_in_dialog: false`).
 * @param openInStore open entries in the Play Store app when there is one, instead of the browser.
 * @param onAppClick called with the tapped entry before it is opened.
 */
@Composable
public fun MoreAppsDialog(
  onDismiss: () -> Unit,
  title: String? = null,
  hidePackages: Set<String> = emptySet(),
  openInStore: Boolean = true,
  onAppClick: (AppDetails) -> Unit = {},
) {
  var attempt by remember { mutableIntStateOf(0) }
  // With the list already in memory the first frame shows it, so the dialog opens at full size.
  val initial =
    remember(hidePackages) {
      MoreApps.appsInMemory()?.let {
        ListState.Loaded(MoreApps.appsToList(it, hidePackages).toImmutableList())
      } ?: ListState.Loading
    }
  val state by
    produceState(initial, attempt, hidePackages) {
      val apps = MoreApps.apps()
      value =
        if (apps.isEmpty() && MoreApps.refresh().isFailure) ListState.Failed
        else ListState.Loaded(MoreApps.appsToList(apps, hidePackages).toImmutableList())
    }
  val uriHandler = LocalUriHandler.current

  // The standard Material 3 dialog; only the list in its content slot is ours.
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title ?: stringResource(Res.string.more_apps_title)) },
    text = {
      Box(Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 480.dp)) {
        when (val current = state) {
          ListState.Loading -> DelayedLoading()
          ListState.Failed -> Message(stringResource(Res.string.more_apps_error))
          is ListState.Loaded ->
            if (current.apps.isEmpty()) Message(stringResource(Res.string.more_apps_empty))
            else
              AppList(current.apps) { app ->
                onAppClick(app)
                uriHandler.openApp(app.appLink, app.packageName, openInStore)
              }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) {
        Text(stringResource(Res.string.more_apps_close))
      }
    },
    dismissButton =
      if (state == ListState.Failed) {
        {
          TextButton(onClick = { attempt++ }, shapes = ButtonDefaults.shapes()) {
            Text(stringResource(Res.string.more_apps_retry))
          }
        }
      } else null,
  )
}

@Composable
private fun AppList(apps: ImmutableList<AppDetails>, onClick: (AppDetails) -> Unit) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
    itemsIndexed(
      apps,
      key = { _, app -> app.packageName.ifBlank { app.name } },
      contentType = { _, _ -> "app" },
    ) { index, app ->
      AppRow(
        app = app,
        onClick = { onClick(app) },
        modifier = Modifier.clipToListShape(index, apps.size),
      )
    }
  }
}

/** Shows the indicator only if loading takes longer than 400 ms, so fast loads don't flash. */
@Composable
private fun DelayedLoading() {
  val show by
    produceState(false) {
      delay(400)
      value = true
    }
  Box(Modifier.fillMaxWidth().heightIn(min = 120.dp), contentAlignment = Alignment.Center) {
    if (show) LoadingIndicator()
  }
}

@Composable
private fun Message(text: String) {
  Box(
    Modifier.fillMaxWidth().heightIn(min = 120.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
  }
}

@Immutable
private sealed interface ListState {
  data object Loading : ListState

  data object Failed : ListState

  data class Loaded(val apps: ImmutableList<AppDetails>) : ListState
}
