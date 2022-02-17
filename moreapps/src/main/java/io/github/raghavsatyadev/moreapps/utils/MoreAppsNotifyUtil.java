package io.github.raghavsatyadev.moreapps.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import io.github.raghavsatyadev.moreapps.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoreAppsNotifyUtil {
    private static final String TAG = MoreAppsNotifyUtil.class.getSimpleName();

    private static NotificationCompat.Builder setNotificationStyle(Context context,
                                                                   NotificationCompat.Builder builder,
                                                                   String imageURL,
                                                                   String title,
                                                                   String message,
                                                                   @DrawableRes int bigIconID) {
        if (!TextUtils.isEmpty(imageURL)) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigLargeIcon(getBitmapFromDrawable(MoreAppsResource.getDrawable(context, bigIconID)))
                    .setSummaryText(message)
                    .setBigContentTitle(title);
            Bitmap bitmapFromUrl = getBitmapFromUrl(imageURL);
            if (bitmapFromUrl != null) {
                bigPictureStyle = bigPictureStyle
                        .bigPicture(bitmapFromUrl);
            }
            builder.setStyle(bigPictureStyle);
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }
        return builder;
    }

    private static Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromUrl: ", e);
            return null;
        }
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static int getSmallIcon(@DrawableRes int smallIconID) {
        return smallIconID;
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context,
                                                                     String channelID,
                                                                     String title,
                                                                     String message,
                                                                     String imageURL,
                                                                     PendingIntent pendingIntent,
                                                                     @DrawableRes int bigIconID,
                                                                     @DrawableRes int smallIconID,
                                                                     @ColorInt int notificationColor) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setAutoCancel(true)
                .setSmallIcon(getSmallIcon(smallIconID))
                .setLargeIcon(getBitmapFromDrawable(MoreAppsResource.getDrawable(context, bigIconID)))
                .setSound(defaultSoundUri)
                .setColor(notificationColor)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message);

        return setNotificationStyle(context, builder, imageURL, title, message, bigIconID);
    }

    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public static void sendNotification(Context context, int NOTIFICATION_ID, String title,
                                        String message, String imageURL,
                                        Intent intent, int pendingIntentFlag,
                                        @DrawableRes int bigIconID, @DrawableRes int smallIconID,
                                        @ColorInt int notificationColor) {

        NotificationManager notificationManager = getNotificationManager(context);

        String channelID = MoreAppsResource.getString(context, R.string.more_apps_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationChannel(context, notificationManager, channelID);
        }

        NotificationCompat.Builder builder;
        builder = getNotificationBuilder(context,
                channelID,
                title,
                message,
                imageURL,
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag),
                bigIconID,
                smallIconID,
                notificationColor);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void getNotificationChannel(Context context, NotificationManager notificationManager, String channelID) {

        String channelName = MoreAppsResource.getString(context, R.string.more_apps_channel_name);

        String channelDescription = MoreAppsResource.getString(context, R.string.more_apps_channel_description);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(channelID, channelName, importance);

        mChannel.setDescription(channelDescription);

        notificationManager.createNotificationChannel(mChannel);
    }
}