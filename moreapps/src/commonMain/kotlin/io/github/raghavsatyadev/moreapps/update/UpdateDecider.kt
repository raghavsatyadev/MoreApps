package io.github.raghavsatyadev.moreapps.update

import io.github.raghavsatyadev.moreapps.data.KeyValueStore
import io.github.raghavsatyadev.moreapps.data.StoreKeys
import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType

/**
 * The 2.x rules: a redirect wins over everything, then a hard update (installed version below
 * `min_version`), then a soft update (below `current_version` or `min_version`).
 */
internal fun decideDialog(app: AppDetails?, installedVersion: Long): UpdateDialogType {
  if (app == null) return UpdateDialogType.NONE
  return when {
    app.redirectDetails.enable ->
      if (app.redirectDetails.hardRedirect) UpdateDialogType.HARD_REDIRECT
      else UpdateDialogType.SOFT_REDIRECT
    app.hardUpdateDetails.enable && app.minVersion > installedVersion -> UpdateDialogType.HARD_UPDATE
    app.softUpdateDetails.enable &&
      (app.currentVersion > installedVersion || app.minVersion > installedVersion) ->
      UpdateDialogType.SOFT_UPDATE
    else -> UpdateDialogType.NONE
  }
}

/** Where a soft update was shown; each keeps its own count so one never resets the other. */
internal enum class SoftUpdateChannel(val countKey: String, val versionKey: String) {
  DIALOG(StoreKeys.DIALOG_SHOW_COUNT, StoreKeys.CURRENT_VERSION),
  NOTIFICATION(StoreKeys.NOTIFICATION_SHOW_COUNT, StoreKeys.NOTIFICATION_VERSION),
}

/**
 * Show-count bookkeeping for soft updates. A newly published `current_version` starts the count
 * again for that channel only (2.x shared one saved version, so a notification reset the dialog's
 * count and vice versa).
 */
internal class SoftUpdateLimiter(private val store: KeyValueStore) {

  fun shouldShow(channel: SoftUpdateChannel, limit: Int, publishedVersion: Long): Boolean {
    val savedVersion = store.getLong(channel.versionKey, 0)
    if (publishedVersion > savedVersion) {
      store.putLong(channel.versionKey, publishedVersion)
      store.putInt(channel.countKey, 0)
      return true
    }
    return limit == 0 || store.getInt(channel.countKey, 0) < limit
  }

  fun recordShown(channel: SoftUpdateChannel) {
    store.putInt(channel.countKey, store.getInt(channel.countKey, 0) + 1)
  }
}
