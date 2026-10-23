package io.github.raghavsatyadev.moreapps.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.raghavsatyadev.moreapps.R
import io.github.raghavsatyadev.moreapps.model.UpdatePrompt

/** Posts the update or redirect notification; tapping it opens the store or redirect link. */
internal object UpdateNotifier {
  private const val CHANNEL_ID = "app_update"
  private const val NOTIFICATION_ID = 0x4d41

  /** @return whether a notification was posted (false without permission, icon or link). */
  fun notify(context: Context, prompt: UpdatePrompt, smallIcon: Int, color: Int): Boolean {
    if (smallIcon == 0 || prompt.link.isBlank() || !canPost(context)) return false
    val manager = NotificationManagerCompat.from(context)
    manager.createNotificationChannel(
      NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
        .setName(context.getString(R.string.more_apps_channel_name))
        .setDescription(
          context.getString(R.string.more_apps_channel_description)
        )
        .build()
    )
    val open =
      PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        Intent(Intent.ACTION_VIEW, Uri.parse(prompt.link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
    val builder =
      NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(smallIcon)
        .setContentTitle(prompt.title)
        .setContentText(prompt.message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(prompt.message))
        .setContentIntent(open)
        .setAutoCancel(true)
        .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
    if (color != 0) builder.setColor(color)
    @Suppress("MissingPermission") manager.notify(NOTIFICATION_ID, builder.build())
    return true
  }

  private fun canPost(context: Context): Boolean {
    val granted =
      Build.VERSION.SDK_INT < 33 ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
    return granted && NotificationManagerCompat.from(context).areNotificationsEnabled()
  }
}
