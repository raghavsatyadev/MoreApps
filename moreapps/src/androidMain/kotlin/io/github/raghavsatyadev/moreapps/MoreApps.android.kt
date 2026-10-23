package io.github.raghavsatyadev.moreapps

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.github.raghavsatyadev.moreapps.data.SharedPrefsStore
import io.github.raghavsatyadev.moreapps.worker.UpdateCheckWorker
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Android setup; call from `Application.onCreate`. The package name and version code are read
 * from the installed app.
 */
public fun MoreApps.initialize(context: Context, jsonUrl: String) {
  val app = context.applicationContext
  start(MoreAppsConfig(jsonUrl, app.packageName, app.installedVersionCode()), SharedPrefsStore(app))
}

/**
 * Refreshes the list in the background every [interval] (at least 15 minutes) and posts a
 * notification when an update or redirect is due. The first run after scheduling stays silent.
 * Apps targeting Android 13+ must ask for `POST_NOTIFICATIONS`; without it nothing is posted.
 *
 * @param smallIcon status-bar icon (monochrome).
 * @param color accent for the notification; 0 uses the system default.
 */
public fun MoreApps.schedulePeriodicCheck(
  context: Context,
  @DrawableRes smallIcon: Int,
  interval: Duration = 7.days,
  @ColorInt color: Int = 0,
) {
  val request =
    PeriodicWorkRequestBuilder<UpdateCheckWorker>(
        interval.inWholeMinutes.coerceAtLeast(15),
        TimeUnit.MINUTES,
      )
      .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
      .setInputData(UpdateCheckWorker.inputData(config.jsonUrl, smallIcon, color))
      .build()
  WorkManager.getInstance(context.applicationContext)
    .enqueueUniquePeriodicWork(
      UpdateCheckWorker.UNIQUE_NAME,
      ExistingPeriodicWorkPolicy.UPDATE,
      request,
    )
}

/** Stops the background check started by [schedulePeriodicCheck]. */
public fun MoreApps.cancelPeriodicCheck(context: Context) {
  WorkManager.getInstance(context.applicationContext)
    .cancelUniqueWork(UpdateCheckWorker.UNIQUE_NAME)
}

internal fun Context.installedVersionCode(): Long {
  val info =
    if (Build.VERSION.SDK_INT >= 33) {
      packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
      @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
    }
  return if (Build.VERSION.SDK_INT >= 28) info.longVersionCode
  else @Suppress("DEPRECATION") info.versionCode.toLong()
}
