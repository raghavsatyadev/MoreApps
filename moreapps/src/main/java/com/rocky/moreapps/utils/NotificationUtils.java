package com.rocky.moreapps.utils;

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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.rocky.moreapps.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationUtils {
    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static Uri defaultSoundUri;
    private static Bitmap icLauncher;
    private static int notificationColor;

    private static NotificationCompat.Builder setNotificationStyle(NotificationCompat.Builder builder,
                                                                   String imageURL,
                                                                   String title,
                                                                   String message) {
        if (!TextUtils.isEmpty(imageURL)) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigLargeIcon(icLauncher)
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

    // TODO: 20-Feb-19 update small icon as required
    private int getSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT);
//        return useWhiteIcon ? R.drawable.ic_small_icon : R.mipmap.ic_launcher;
        return R.drawable.ic_close;
    }

    // TODO: 20-Feb-19 update big icon as required
    private int getBigIcon() {
//        return R.mipmap.ic_launcher;
        return R.drawable.ic_close;
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context,
                                                              String channelID,
                                                              String title,
                                                              String message,
                                                              String imageURL,
                                                              PendingIntent pendingIntent) {

        if (defaultSoundUri == null)
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (icLauncher == null) {
            icLauncher = getBitmapFromDrawable(ResourceUtils.getDrawable(context, getBigIcon()));
        }
        if (notificationColor == 0) {
            // TODO: 20-Feb-19 update notification color as required
//            notificationColor = ResourceUtils.getColor(context, R.color.notification_color);
            notificationColor = ResourceUtils.getColor(context, R.color.app_background);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setAutoCancel(true)
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(icLauncher)
                .setSound(defaultSoundUri)
                .setColor(notificationColor)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message);

        return setNotificationStyle(builder, imageURL, title, message);
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public void sendNotification(Context context, int NOTIFICATION_ID, String title,
                                 String message, String imageURL,
                                 Intent intent, int pendingIntentFlag) {
        NotificationManager notificationManager = getNotificationManager(context);
//        String appName = ResourceUtils.getString(R.string.app_name);
        // TODO: 20-Feb-19 update app name as required
        String appName = ResourceUtils.getString(context, R.string.more_apps);

//        String channelID = ResourceUtils.getString(R.string.channel_id);
        // TODO: 20-Feb-19 update channel id as required
        String channelID = ResourceUtils.getString(context, R.string.more_apps_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationChannel(context, notificationManager, channelID);
        }

        NotificationCompat.Builder builder;
        builder = getNotificationBuilder(context,
                channelID,
                !TextUtils.isEmpty(title) ? title : appName,
                message,
                imageURL,
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag));
        if (builder != null) {

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getNotificationChannel(Context context, NotificationManager notificationManager, String channelID) {

        // The user-visible name of the channel.
//        String channelName = ResourceUtils.getString(R.string.channel_name);
        // TODO: 20-Feb-19 update channel name
        String channelName = ResourceUtils.getString(context, R.string.more_apps);

        // The user-visible description of the channel.
//        String channelDescription = ResourceUtils.getString(R.string.channel_description);
        // TODO: 20-Feb-19 update channel description
        String channelDescription = ResourceUtils.getString(context, R.string.more_apps);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(channelID, channelName, importance);

        // Configure the notification channel.
        mChannel.setDescription(channelDescription);

        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        notificationManager.createNotificationChannel(mChannel);
    }
}