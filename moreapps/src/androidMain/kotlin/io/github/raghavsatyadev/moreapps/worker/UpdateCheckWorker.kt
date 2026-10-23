package io.github.raghavsatyadev.moreapps.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.github.raghavsatyadev.moreapps.MoreApps
import io.github.raghavsatyadev.moreapps.data.KeyValueStore
import io.github.raghavsatyadev.moreapps.data.MoreAppsKoin
import io.github.raghavsatyadev.moreapps.data.StoreKeys
import io.github.raghavsatyadev.moreapps.initialize
import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType
import io.github.raghavsatyadev.moreapps.model.UpdatePrompt
import io.github.raghavsatyadev.moreapps.update.SoftUpdateChannel
import io.github.raghavsatyadev.moreapps.update.SoftUpdateLimiter
import io.github.raghavsatyadev.moreapps.update.decideDialog

/**
 * Periodic refresh. WorkManager may start the process without the host's `initialize` having run
 * yet, so the URL and icons travel in the input data and the library starts itself if needed.
 */
internal class UpdateCheckWorker(context: Context, params: WorkerParameters) :
  CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    val url = inputData.getString(KEY_URL) ?: return Result.failure()
    if (!MoreAppsKoin.isStarted) MoreApps.initialize(applicationContext, url)

    val apps = MoreApps.refresh().getOrElse { return Result.retry() }
    val store: KeyValueStore = MoreAppsKoin.get()
    if (store.getBoolean(StoreKeys.IS_FIRST_TIME_PERIODIC, true)) {
      // The app itself just checked at launch; don't notify on top of its dialog.
      store.putBoolean(StoreKeys.IS_FIRST_TIME_PERIODIC, false)
      return Result.success()
    }

    val config = MoreApps.config
    val app = apps.firstOrNull { it.packageName == config.packageName } ?: return Result.success()
    val type = decideDialog(app, config.versionCode)
    if (type == UpdateDialogType.SOFT_UPDATE) {
      val limiter: SoftUpdateLimiter = MoreAppsKoin.get()
      val limit = app.softUpdateDetails.notificationShowCount
      if (!limiter.shouldShow(SoftUpdateChannel.NOTIFICATION, limit, app.currentVersion)) {
        return Result.success()
      }
      if (notify(type, app)) limiter.recordShown(SoftUpdateChannel.NOTIFICATION)
    } else if (type != UpdateDialogType.NONE) {
      notify(type, app)
    }
    return Result.success()
  }

  private fun notify(type: UpdateDialogType, app: AppDetails): Boolean =
    UpdateNotifier.notify(
      context = applicationContext,
      prompt = UpdatePrompt(type, app),
      smallIcon = inputData.getInt(KEY_SMALL_ICON, 0),
      color = inputData.getInt(KEY_COLOR, 0),
    )

  internal companion object {
    const val UNIQUE_NAME = "io.github.raghavsatyadev.moreapps.periodic"
    private const val KEY_URL = "url"
    private const val KEY_SMALL_ICON = "small_icon"
    private const val KEY_COLOR = "color"

    fun inputData(url: String, smallIcon: Int, color: Int): Data =
      workDataOf(KEY_URL to url, KEY_SMALL_ICON to smallIcon, KEY_COLOR to color)
  }
}
