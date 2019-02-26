package com.rocky.moreapps;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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

import com.rocky.moreapps.listener.MoreAppsDownloadListener;
import com.rocky.moreapps.model.PeriodicUpdateSettings;
import com.rocky.moreapps.utils.SharedPrefsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MoreAppsWorker extends Worker {

    private static final String WORKER_TAG_PERIODIC = "MORE_APPS_PERIODIC";
    private static final String WORKER_TAG_ONE_TIME = "MORE_APPS_ONE_TIME";
    private static final String TAG = MoreAppsWorker.class.getSimpleName();
    private static final String URL = "Url";
    private static final String USER_AGENT = "Mozilla/5.0";

    public MoreAppsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    static void startWorker(final Context context,
                            String url,
                            final MoreAppsDownloadListener listener,
                            final MoreAppsDialog moreAppsDialog,
                            PeriodicUpdateSettings updateSettings) {
        if (!isWorkScheduled()) {
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            Data data = new Data.Builder().putString(URL, url).build();
            WorkManager instance = WorkManager.getInstance();

            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                    .Builder(MoreAppsWorker.class,
                    updateSettings.getInterval(),
                    updateSettings.getTimeUnit())
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();

            instance.enqueueUniquePeriodicWork(WORKER_TAG_PERIODIC, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);

            if (SharedPrefsUtil.getMoreApps(context).isEmpty()) {
                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MoreAppsWorker.class)
                        .setConstraints(constraints)
                        .setInputData(data)
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
    }

    private static void handleResult(Context context, MoreAppsDialog moreAppsDialog, List<WorkInfo> workInfos, LiveData<List<WorkInfo>> workInfoLiveData, Observer<List<WorkInfo>> observer, MoreAppsDownloadListener listener) {
        if (workInfos != null && !workInfos.isEmpty()) {
            WorkInfo.State state = workInfos.get(0).getState();
            if (state == WorkInfo.State.SUCCEEDED) {
                Log.d(TAG, "run: " + SharedPrefsUtil.getMoreAppsString(context));
                listener.onSuccess(moreAppsDialog, SharedPrefsUtil.getMoreApps(context));
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
        return setupMoreAppsAPI();
    }

    private Result setupMoreAppsAPI() {
        String moreAppsJSON = callMoreAppsAPI();

        if (!TextUtils.isEmpty(moreAppsJSON)) {
            SharedPrefsUtil.setMoreApps(getApplicationContext(), moreAppsJSON);
            return Result.success();
        }

        return Result.failure();
    }

    private String callMoreAppsAPI() {
        String response = null;
        try {
            URL url = new URL(getInputData().getString(URL));

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
}