package com.rocky.moreapps;

import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.rocky.moreapps.listener.MoreAppsDownloadListener;
import com.rocky.moreapps.model.MoreAppsDetails;
import com.rocky.moreapps.settings.PeriodicUpdateSettings;
import com.rocky.moreapps.utils.MoreAppsNotifyUtil;
import com.rocky.moreapps.utils.MoreAppsPrefUtil;
import com.rocky.moreapps.utils.MoreAppsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MoreAppsWorker extends Worker {

    private static final String WORKER_TAG_PERIODIC = "MORE_APPS_PERIODIC";
    private static final String WORKER_TAG_ONE_TIME = "MORE_APPS_ONE_TIME";
    private static final String TAG = MoreAppsWorker.class.getSimpleName();
    private static final String URL = "URL";
    private static final String BIG_ICON = "BIG_ICON";
    private static final String SMALL_ICON = "SMALL_ICON";
    private static final String THEME_COLOR = "THEME_COLOR";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String IS_PERIODIC = "IS_PERIODIC";
    private Context context;

    public MoreAppsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    static void startWorker(final Context context,
                            String url,
                            final MoreAppsDownloadListener listener,
                            final MoreAppsDialog moreAppsDialog,
                            final int themeColor,
                            final PeriodicUpdateSettings updateSettings) {

        if (!isWorkScheduled()) {
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            Data.Builder dataBuilder = new Data.Builder()
                    .putInt(BIG_ICON, updateSettings.getBigIconID())
                    .putInt(SMALL_ICON, updateSettings.getSmallIconID())
                    .putInt(THEME_COLOR, themeColor)
                    .putString(URL, url);
            WorkManager instance = WorkManager.getInstance();

            setupPeriodicRequest(constraints, dataBuilder, updateSettings, instance);

            setupOneTimeRequest(context, constraints, dataBuilder, moreAppsDialog, listener, instance);
        }
    }

    private static void setupOneTimeRequest(final Context context, Constraints constraints, Data.Builder dataBuilder,
                                            final MoreAppsDialog moreAppsDialog,
                                            final MoreAppsDownloadListener listener, WorkManager instance) {
        if (MoreAppsPrefUtil.getMoreApps(context).isEmpty()) {
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MoreAppsWorker.class)
                    .setConstraints(constraints)
                    .setInputData(dataBuilder.putBoolean(IS_PERIODIC, false).build())
                    .build();

            instance.enqueueUniqueWork(WORKER_TAG_ONE_TIME, ExistingWorkPolicy.KEEP, oneTimeWorkRequest);

            if (listener != null) {
                final LiveData<List<WorkInfo>> workInfo = instance.getWorkInfosForUniqueWorkLiveData(WORKER_TAG_ONE_TIME);
                Observer<List<WorkInfo>> observer = new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(@Nullable List<WorkInfo> workInfos) {
                        handleResult(context, moreAppsDialog, workInfos, workInfo, this, listener);
                    }
                };
                workInfo.observeForever(observer);
            }
        }
    }

    private static void setupPeriodicRequest(Constraints constraints, Data.Builder dataBuilder,
                                             final PeriodicUpdateSettings updateSettings, WorkManager instance) {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(MoreAppsWorker.class, updateSettings.getInterval(), updateSettings.getTimeUnit())
                        .setConstraints(constraints)
                        .setInputData(dataBuilder.putBoolean(IS_PERIODIC, true).build())
                        .build();

        instance.enqueueUniquePeriodicWork(WORKER_TAG_PERIODIC, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    private static void handleResult(Context context, MoreAppsDialog moreAppsDialog, List<WorkInfo> workInfos, LiveData<List<WorkInfo>> workInfoLiveData, Observer<List<WorkInfo>> observer, MoreAppsDownloadListener listener) {
        if (workInfos != null && !workInfos.isEmpty()) {
            WorkInfo.State state = workInfos.get(0).getState();
            if (state == WorkInfo.State.SUCCEEDED) {
                listener.onSuccess(moreAppsDialog, MoreAppsPrefUtil.getMoreApps(context));
                workInfoLiveData.removeObserver(observer);
            } else if (state == WorkInfo.State.FAILED) {
                listener.onFailure();
                workInfoLiveData.removeObserver(observer);
            }
        }
    }

    private static boolean isWorkScheduled() {
        WorkInfo.State workerState = getWorkerState();
        return workerState == WorkInfo.State.RUNNING || workerState == WorkInfo.State.ENQUEUED;
    }

    private String setConnectionProperties(HttpURLConnection con) throws IOException {
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return getMoreAppsResponse(in);
        } else {
            Log.d(TAG, "updateAppsNew: GET request not worked");
        }
        return null;
    }

    private String setConnectionProperties(HttpsURLConnection con) throws IOException {
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            return getMoreAppsResponse(in);
        } else {
            Log.d(TAG, "updateAppsNew: GET request not worked");
        }
        return null;
    }

    private String getMoreAppsResponse(BufferedReader in) throws IOException {
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static WorkInfo.State getWorkerState() {
        WorkManager instance = WorkManager.getInstance();
        final LiveData<List<WorkInfo>> infos = instance.getWorkInfosForUniqueWorkLiveData(MoreAppsWorker.WORKER_TAG_PERIODIC);
        List<WorkInfo> value = infos.getValue();
        if (value != null && !value.isEmpty()) {
            WorkInfo.State state = value.get(0).getState();
            if (state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.RUNNING) {
                return state;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public Result doWork() {
        String moreAppsJSON = callMoreAppsAPI();

        if (!TextUtils.isEmpty(moreAppsJSON)) {
            MoreAppsPrefUtil.setMoreApps(getApplicationContext(), moreAppsJSON);
            if (getInputData().getBoolean(IS_PERIODIC, false)) {
                if (!MoreAppsPrefUtil.isFirstTimePeriodic(context)) {
                    try {
                        handleNotification(getApplicationContext());
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "doWork: ", e);
                    }
                } else {
                    MoreAppsPrefUtil.setFirstTimePeriodic(context, false);
                }
            }
            return Result.success();
        }

        return Result.failure();
    }

    private void handleNotification(Context context) throws
            PackageManager.NameNotFoundException {
        int bigIconID = getInputData().getInt(BIG_ICON, 0);

        if (bigIconID != 0) {
            int smallIconData = getInputData().getInt(SMALL_ICON, 0);
            int smallIconID = smallIconData == 0 ? bigIconID : smallIconData;

            int notificationColor = getInputData().getInt(THEME_COLOR, 0) == 0 ?
                    Color.parseColor(MoreAppsUtils.getPrimaryColorInHex(context)) :
                    getInputData().getInt(THEME_COLOR, 0);

            prepareNotification(context, bigIconID, smallIconID, notificationColor);
        }
    }

    private void prepareNotification(Context context,
                                     @DrawableRes int bigIconID,
                                     @DrawableRes int smallIconID,
                                     @ColorInt int notificationColor) throws PackageManager.NameNotFoundException {
        MoreAppsDetails currentAppModel = MoreAppsUtils.getCurrentAppModel(context, MoreAppsPrefUtil.getMoreApps(context));
        ForceUpdater.UpdateDialogType updateDialogType = ForceUpdater.dialogToShow(context, currentAppModel);

        switch (updateDialogType) {
            case HARD_UPDATE:
                sendNotification(context, currentAppModel.hardUpdateDetails.dialogTitle,
                        currentAppModel.hardUpdateDetails.dialogMessage,
                        currentAppModel.appLink,
                        bigIconID,
                        smallIconID,
                        notificationColor);
                break;
            case SOFT_UPDATE:
                sendNotification(context, currentAppModel.softUpdateDetails.dialogTitle,
                        currentAppModel.softUpdateDetails.dialogMessage,
                        currentAppModel.appLink,
                        bigIconID,
                        smallIconID,
                        notificationColor);
                break;
            case HARD_REDIRECT:
                sendNotification(context, currentAppModel.redirectDetails.dialogTitle,
                        currentAppModel.redirectDetails.dialogMessage,
                        currentAppModel.redirectDetails.appLink,
                        bigIconID,
                        smallIconID,
                        notificationColor);
                break;
            case SOFT_REDIRECT:
                sendNotification(context, currentAppModel.redirectDetails.dialogTitle,
                        currentAppModel.redirectDetails.dialogMessage,
                        currentAppModel.redirectDetails.appLink,
                        bigIconID,
                        smallIconID,
                        notificationColor);
                break;
        }
    }

    private void sendNotification(Context context, String notificationName,
                                  String notificationDescription,
                                  String appLink,
                                  @DrawableRes int bigIconID,
                                  @DrawableRes int smallIconID,
                                  @ColorInt int notificationColor) {

        MoreAppsNotifyUtil.sendNotification(context,
                new Random().nextInt(),
                notificationName,
                notificationDescription,
                null,
                new Intent(Intent.ACTION_VIEW, Uri.parse(appLink)),
                PendingIntent.FLAG_ONE_SHOT,
                bigIconID,
                smallIconID,
                notificationColor
        );
    }

    private String callMoreAppsAPI() {
        String response = null;
        try {
            Data inputData = getInputData();
            String urlString = inputData.getString(URL);
            URL url = new URL(urlString);

            if (url.getHost().equalsIgnoreCase("https")) {
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                response = setConnectionProperties(con);
            } else {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                response = setConnectionProperties(con);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "callMoreAppsAPI: ", e);
        } catch (IOException e) {
            Log.e(TAG, "callMoreAppsAPI: ", e);
        }
        return response;
    }
}