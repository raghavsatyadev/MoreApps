package io.github.raghavsatyadev.moreapps.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MoreAppsPrefUtil {

    private static final String TAG = MoreAppsPrefUtil.class.getSimpleName();

    public static ArrayList<MoreAppsDetails> getMoreApps(@NonNull Context context) {
        return convertStringToModel(getMoreAppsString(context));
    }

    public static String getMoreAppsString(Context context) {
        return MoreAppsPrefHelper.getInstance(context).get(AppPrefStrings.MORE_APPS, "");
    }

    public static ArrayList<MoreAppsDetails> convertStringToModel(String json) {
        Gson gson = new Gson();
        ArrayList<MoreAppsDetails> moreAppsDetails = new ArrayList<>();
        try {
            Type appModelsType = new TypeToken<ArrayList<MoreAppsDetails>>() {
            }.getType();
            List<MoreAppsDetails> moreAppsModelsTemp = gson.fromJson(json, appModelsType);
            if (moreAppsModelsTemp != null) {
                moreAppsDetails.addAll(moreAppsModelsTemp);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "getMoreApps: ", e);
        }
        return moreAppsDetails;
    }

    public static void setMoreApps(@NonNull Context context, List<MoreAppsDetails> moreAppsDetails) {
        Gson gson = new Gson();
        if (moreAppsDetails != null) {
            String appModelsJson = gson.toJson(moreAppsDetails);
            MoreAppsPrefHelper.getInstance(context).save(AppPrefStrings.MORE_APPS, appModelsJson);
        }
    }

    public static void setMoreApps(@NonNull Context context, String moreAppsJSON) {
        if (!TextUtils.isEmpty(moreAppsJSON)) {
            MoreAppsPrefHelper.getInstance(context).save(AppPrefStrings.MORE_APPS, moreAppsJSON);
        }
    }

    public static boolean isFirstTimePeriodic(@NonNull Context context) {
        return MoreAppsPrefHelper.getInstance(context).get(AppPrefStrings.IS_FIRST_TIME_PERIODIC, true);
    }

    public static void setFirstTimePeriodic(@NonNull Context context, boolean status) {
        MoreAppsPrefHelper.getInstance(context).save(AppPrefStrings.IS_FIRST_TIME_PERIODIC, status);
    }

    public static void saveCurrentVersion(Context context, int currentVersion) {
        saveSoftUpdateShownTimes(context, 0);
        saveSoftUpdateNotificationShownTimes(context, 0);
        MoreAppsPrefHelper.getInstance(context).save(AppPrefStrings.CURRENT_VERSION, currentVersion);
    }

    private static int getCurrentVersion(Context context) {
        return MoreAppsPrefHelper.getInstance(context).get(AppPrefStrings.CURRENT_VERSION, 0);
    }

    public static boolean shouldShowSoftUpdate(Context context, int dialogShowCount, int currentVersion) {
        int dialogShownTimes = getSoftUpdateShownTimes(context);
        int savedCurrentVersion = getCurrentVersion(context);
        if (currentVersion > savedCurrentVersion) {
            saveCurrentVersion(context, currentVersion);
            return true;
        }
        return dialogShowCount == 0 || dialogShowCount > dialogShownTimes;
    }

    private static int getSoftUpdateShownTimes(Context context) {
        return MoreAppsPrefHelper.getInstance(context).get(AppPrefStrings.DIALOG_SHOW_COUNT, 0);
    }

    public static void saveSoftUpdateShownTimes(Context context, int softUpdateShownTimes) {
        MoreAppsPrefHelper.getInstance(context).save(AppPrefStrings.DIALOG_SHOW_COUNT, softUpdateShownTimes);
    }

    public static void increaseSoftUpdateShownTimes(Context context) {
        int softUpdateShownTimes = getSoftUpdateShownTimes(context);
        softUpdateShownTimes++;
        saveSoftUpdateShownTimes(context, softUpdateShownTimes);
    }

    public static boolean shouldShowSoftUpdateNotification(Context context, int notificationShowCount, int currentVersion) {
        int notificationShownTimes = getSoftUpdateNotificationShownTimes(context);
        int savedCurrentVersion = getCurrentVersion(context);
        if (currentVersion > savedCurrentVersion) {
            saveCurrentVersion(context, currentVersion);
            return true;
        }
        return notificationShowCount == 0 || notificationShowCount > notificationShownTimes;
    }

    private static int getSoftUpdateNotificationShownTimes(Context context) {
        return MoreAppsPrefHelper.getInstance(context).get(AppPrefStrings.NOTIFICATION_SHOW_COUNT, 0);
    }

    public static void saveSoftUpdateNotificationShownTimes(Context context, int softUpdateNotificationShownTimes) {
        MoreAppsPrefHelper.getInstance(context).save(AppPrefStrings.NOTIFICATION_SHOW_COUNT, softUpdateNotificationShownTimes);
    }

    public static void increaseSoftUpdateNotificationShownTimes(Context context) {
        int softUpdateNotificationShownTimes = getSoftUpdateNotificationShownTimes(context);
        softUpdateNotificationShownTimes++;
        saveSoftUpdateNotificationShownTimes(context, softUpdateNotificationShownTimes);
    }

    interface AppPrefStrings {
        String MORE_APPS = "MORE_APPS";
        String IS_FIRST_TIME_PERIODIC = "IS_FIRST_TIME_PERIODIC";
        String DIALOG_SHOW_COUNT = "DIALOG_SHOW_COUNT";
        String NOTIFICATION_SHOW_COUNT = "NOTIFICATION_SHOW_COUNT";
        String CURRENT_VERSION = "CURRENT_VERSION";
    }
}
