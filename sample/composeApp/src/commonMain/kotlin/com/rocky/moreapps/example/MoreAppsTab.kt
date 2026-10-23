@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rocky.moreapps.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocky.moreapps.example.resources.Res
import com.rocky.moreapps.example.resources.clicked_app
import com.rocky.moreapps.example.resources.custom_body
import com.rocky.moreapps.example.resources.custom_dialog_title
import com.rocky.moreapps.example.resources.custom_title
import com.rocky.moreapps.example.resources.default_body
import com.rocky.moreapps.example.resources.default_title
import com.rocky.moreapps.example.resources.open_in_store
import com.rocky.moreapps.example.resources.show_dialog
import io.github.raghavsatyadev.moreapps.ui.MoreAppsDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private enum class Shown {
  NONE,
  CUSTOM,
  DEFAULT,
}

/** WhatsCut is left out of the customized dialog to show `hidePackages`. */
private val HIDDEN = setOf("com.appdroidtechnologies.whatscut")

@Composable
internal fun MoreAppsTab(snackbar: SnackbarHostState) {
  var shown by rememberSaveable { mutableStateOf(Shown.NONE) }
  var openInStore by rememberSaveable { mutableStateOf(true) }
  val scope = rememberCoroutineScope()

  Column(
    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    SampleCard(stringResource(Res.string.custom_title), stringResource(Res.string.custom_body)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(Res.string.open_in_store), Modifier.weight(1f))
        Switch(checked = openInStore, onCheckedChange = { openInStore = it })
      }
      Button(onClick = { shown = Shown.CUSTOM }, shapes = ButtonDefaults.shapes()) {
        Text(stringResource(Res.string.show_dialog))
      }
    }
    SampleCard(stringResource(Res.string.default_title), stringResource(Res.string.default_body)) {
      FilledTonalButton(onClick = { shown = Shown.DEFAULT }, shapes = ButtonDefaults.shapes()) {
        Text(stringResource(Res.string.show_dialog))
      }
    }
  }

  when (shown) {
    Shown.CUSTOM ->
      MoreAppsDialog(
        onDismiss = { shown = Shown.NONE },
        title = stringResource(Res.string.custom_dialog_title),
        hidePackages = HIDDEN,
        openInStore = openInStore,
        onAppClick = { app ->
          scope.launch { snackbar.showSnackbar(getString(Res.string.clicked_app, app.name)) }
        },
      )
    Shown.DEFAULT -> MoreAppsDialog(onDismiss = { shown = Shown.NONE })
    Shown.NONE -> Unit
  }
}
