package io.github.raghavsatyadev.moreapps.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.TextUtils
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.Builder
import io.github.raghavsatyadev.moreapps.R.string
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object MoreAppsNotifyUtil {
    private val TAG = MoreAppsNotifyUtil::class.java.simpleName
    private fun setNotificationStyle(
        context: Context,
        builder: Builder,
        imageURL: String?,
        title: String,
        message: String,
        @DrawableRes
        bigIconID: Int,
    ): Builder {
        if (!TextUtils.isEmpty(imageURL)) {
            val drawable = context.getConDrawable(bigIconID)
            var bigPictureStyle =
                BigPictureStyle().bigLargeIcon(drawable?.let { getBitmapFromDrawable(it) })
                    .setSummaryText(message).setBigContentTitle(title)
            val bitmapFromUrl = imageURL?.let { getBitmapFromUrl(it) }
            if (bitmapFromUrl != null) {
                bigPictureStyle = bigPictureStyle.bigPicture(bitmapFromUrl)
            }
            builder.setStyle(bigPictureStyle)
        } else {
            builder.setStyle(BigTextStyle().bigText(message))
        }
        return builder
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            Log.e(TAG, "getBitmapFromUrl: ", e)
            null
        }
    }

    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getSmallIcon(
        @DrawableRes
        smallIconID: Int,
    ): Int {
        return smallIconID
    }

    private fun getNotificationBuilder(
        context: Context,
        channelID: String,
        title: String,
        message: String,
        imageURL: String?,
        pendingIntent: PendingIntent,
        @DrawableRes
        bigIconID: Int,
        @DrawableRes
        smallIconID: Int,
        @ColorInt
        notificationColor: Int,
    ): Builder {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val drawable = context.getConDrawable(bigIconID)
        val builder = Builder(context, channelID).apply {
            setAutoCancel(true)
            setSmallIcon(getSmallIcon(smallIconID))
            setLargeIcon(drawable?.let { getBitmapFromDrawable(it) })
            setSound(defaultSoundUri)
            color = notificationColor
            setContentTitle(title)
            setContentText(message)
            setContentIntent(pendingIntent)
            setTicker(message)
        }
        return setNotificationStyle(context, builder, imageURL, title, message, bigIconID)
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    fun sendNotification(
        context: Context, NOTIFICATION_ID: Int, title: String,
        message: String, imageURL: String?,
        intent: Intent?, pendingIntentFlag: Int,
        @DrawableRes
        bigIconID: Int,
        @DrawableRes
        smallIconID: Int,
        @ColorInt
        notificationColor: Int,
    ) {
        val notificationManager = getNotificationManager(context)
        val channelID = context.getString(string.more_apps_channel_id)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            getNotificationChannel(context, notificationManager, channelID)
        }
        val builder = getNotificationBuilder(
            context,
            channelID,
            title,
            message,
            imageURL,
            PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag),
            bigIconID,
            smallIconID,
            notificationColor
        )
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    @RequiresApi(api = VERSION_CODES.O)
    private fun getNotificationChannel(
        context: Context,
        notificationManager: NotificationManager,
        channelID: String,
    ) {
        val channelName = context.getString(string.more_apps_channel_name)
        val channelDescription = context.getString(string.more_apps_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channelID, channelName, importance)
        mChannel.description = channelDescription
        notificationManager.createNotificationChannel(mChannel)
    }
}