@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.raghavsatyadev.moreapps.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import io.github.raghavsatyadev.moreapps.MoreApps
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType
import io.github.raghavsatyadev.moreapps.model.UpdatePrompt
import io.github.raghavsatyadev.moreapps.model.isRedirect
import io.github.raghavsatyadev.moreapps.resources.Res
import io.github.raghavsatyadev.moreapps.resources.more_apps_later
import io.github.raghavsatyadev.moreapps.resources.more_apps_update
import org.jetbrains.compose.resources.stringResource

/**
 * One update or redirect prompt. Hard prompts ignore back and outside taps and stay up after the
 * button opens the store, since the user may come back without updating.
 *
 * @param onDismiss called once, when a soft prompt is closed by either button, back or outside.
 */
@Composable
public fun UpdateDialog(prompt: UpdatePrompt, onDismiss: () -> Unit, openInStore: Boolean = true) {
  val uriHandler = LocalUriHandler.current
  val blocking = prompt.type.isBlocking
  // Redirects point at another app, so they open the link rather than this app's store page.
  val storePackage = if (prompt.type.isRedirect) "" else prompt.app.packageName
  val positive = prompt.positiveButton.ifBlank { stringResource(Res.string.more_apps_update) }
  val negative = prompt.negativeButton.ifBlank { stringResource(Res.string.more_apps_later) }

  AlertDialog(
    onDismissRequest = { if (!blocking) onDismiss() },
    properties = DialogProperties(dismissOnBackPress = !blocking, dismissOnClickOutside = !blocking),
    icon = {
      Icon(
        if (prompt.type.isRedirect) Icons.AutoMirrored.Rounded.OpenInNew
        else Icons.Rounded.SystemUpdate,
        contentDescription = null,
      )
    },
    title = { Text(prompt.title) },
    text = { Text(prompt.message) },
    confirmButton = {
      Button(
        onClick = {
          uriHandler.openApp(prompt.link, storePackage, openInStore)
          if (!blocking) onDismiss()
        },
        shapes = ButtonDefaults.shapes(),
      ) {
        Text(positive)
      }
    },
    dismissButton =
      if (blocking) null
      else {
        { TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) { Text(negative) } }
      },
  )
}

/**
 * Checks for an update prompt each time the screen starts (so a hard update reappears when the user
 * comes back from the store without updating) and shows it. A soft prompt the user closed stays
 * closed until this leaves composition.
 *
 * @param onResult called with the prompt type each time a check finishes ([UpdateDialogType.NONE]
 *   when there is nothing to show), e.g. to delay a splash screen.
 */
@Composable
public fun UpdatePromptHost(openInStore: Boolean = true, onResult: (UpdateDialogType) -> Unit = {}) {
  var prompt by remember { mutableStateOf<UpdatePrompt?>(null) }
  var softDismissed by rememberSaveable { mutableStateOf(false) }
  val latestOnResult by rememberUpdatedState(onResult)
  val lifecycle = LocalLifecycleOwner.current.lifecycle

  LaunchedEffect(lifecycle) {
    var refreshed = false
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
      suspend fun check() {
        // A soft prompt on screen stays until the user closes it; re-checking would count it again.
        if (prompt?.type?.isBlocking == false) return
        val next = if (softDismissed) hardPromptOnly() else MoreApps.pendingPrompt()
        if (next != null && next != prompt) MoreApps.markPromptShown(next)
        prompt = next
        latestOnResult(next?.type ?: UpdateDialogType.NONE)
      }
      // The cached list answers at once; one fresh download per host then checks again.
      check()
      if (!refreshed && MoreApps.refresh().isSuccess) {
        refreshed = true
        check()
      }
    }
  }

  prompt?.let { current ->
    UpdateDialog(
      prompt = current,
      openInStore = openInStore,
      onDismiss = {
        softDismissed = true
        prompt = null
      },
    )
  }
}

/** After a soft prompt was closed, only a newly required (blocking) prompt may appear. */
private suspend fun hardPromptOnly(): UpdatePrompt? =
  MoreApps.pendingPrompt()?.takeIf { it.type.isBlocking }
