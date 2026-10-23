@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rocky.moreapps.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rocky.moreapps.example.resources.Res
import com.rocky.moreapps.example.resources.preview_body
import com.rocky.moreapps.example.resources.preview_hard_redirect
import com.rocky.moreapps.example.resources.preview_hard_update
import com.rocky.moreapps.example.resources.preview_soft_redirect
import com.rocky.moreapps.example.resources.preview_soft_update
import com.rocky.moreapps.example.resources.preview_title
import com.rocky.moreapps.example.resources.refresh
import com.rocky.moreapps.example.resources.status_decision
import com.rocky.moreapps.example.resources.status_installed
import com.rocky.moreapps.example.resources.status_latest
import com.rocky.moreapps.example.resources.status_loading
import com.rocky.moreapps.example.resources.status_min
import com.rocky.moreapps.example.resources.status_missing
import com.rocky.moreapps.example.resources.status_title
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType
import io.github.raghavsatyadev.moreapps.model.UpdatePrompt
import io.github.raghavsatyadev.moreapps.ui.UpdateDialog
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val PREVIEWS: List<Pair<UpdateDialogType, StringResource>> =
  listOf(
    UpdateDialogType.SOFT_UPDATE to Res.string.preview_soft_update,
    UpdateDialogType.HARD_UPDATE to Res.string.preview_hard_update,
    UpdateDialogType.SOFT_REDIRECT to Res.string.preview_soft_redirect,
    UpdateDialogType.HARD_REDIRECT to Res.string.preview_hard_redirect,
  )

private const val HARD_PREVIEW_MS = 4_000L

@Composable
internal fun UpdaterTab(viewModel: UpdaterViewModel = koinViewModel()) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var preview by rememberSaveable { mutableStateOf<UpdateDialogType?>(null) }

  Column(
    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    SampleCard(stringResource(Res.string.status_title), "") {
      val app = state.app
      when {
        state.loading -> {
          Text(stringResource(Res.string.status_loading))
          LoadingIndicator()
        }
        app == null -> Text(stringResource(Res.string.status_missing))
        else -> {
          Text(stringResource(Res.string.status_installed, state.installedVersion.toString()))
          Text(stringResource(Res.string.status_latest, app.currentVersion.toString()))
          Text(stringResource(Res.string.status_min, app.minVersion.toString()))
          Text(stringResource(Res.string.status_decision, state.decision.name))
        }
      }
      TextButton(onClick = { viewModel.load(refresh = true) }, shapes = ButtonDefaults.shapes()) {
        Text(stringResource(Res.string.refresh))
      }
    }
    SampleCard(stringResource(Res.string.preview_title), stringResource(Res.string.preview_body)) {
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PREVIEWS.forEach { (type, label) ->
          OutlinedButton(
            onClick = { preview = type },
            enabled = state.app != null,
            shapes = ButtonDefaults.shapes(),
          ) {
            Text(stringResource(label))
          }
        }
      }
    }
  }

  val app = state.app
  val type = preview
  if (app != null && type != null) {
    // Hard prompts ignore every dismiss on purpose, so the preview closes itself.
    if (type.isBlocking) {
      LaunchedEffect(type) {
        delay(HARD_PREVIEW_MS)
        preview = null
      }
    }
    UpdateDialog(prompt = UpdatePrompt(type, app), onDismiss = { preview = null })
  }
}
