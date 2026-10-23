package com.rocky.moreapps.example

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.raghavsatyadev.moreapps.MoreApps
import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
internal data class UpdaterState(
  val loading: Boolean = true,
  val installedVersion: Long = 0,
  val app: AppDetails? = null,
  val decision: UpdateDialogType = UpdateDialogType.NONE,
)

/** What the library decides for this app, for the Updater tab. */
internal class UpdaterViewModel : ViewModel() {
  private val _state = MutableStateFlow(UpdaterState())
  val state: StateFlow<UpdaterState> = _state.asStateFlow()

  init {
    load(refresh = false)
  }

  fun load(refresh: Boolean) {
    _state.update { it.copy(loading = true) }
    viewModelScope.launch {
      if (refresh) MoreApps.refresh()
      _state.value =
        UpdaterState(
          loading = false,
          installedVersion = MoreApps.config.versionCode,
          app = MoreApps.currentApp(),
          decision = MoreApps.dialogToShow(),
        )
    }
  }
}
