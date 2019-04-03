package com.rocky.moreapps.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.rocky.moreapps.model.MoreAppsDetails;

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

    interface AppPrefStrings {
        String MORE_APPS = "MORE_APPS";
        String IS_FIRST_TIME_PERIODIC = "IS_FIRST_TIME_PERIODIC";
    }
}
