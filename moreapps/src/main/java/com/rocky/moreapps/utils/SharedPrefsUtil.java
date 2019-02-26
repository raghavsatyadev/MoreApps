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

public class SharedPrefsUtil {

    private static final String TAG = SharedPrefsUtil.class.getSimpleName();

    public static ArrayList<MoreAppsDetails> getMoreApps(@NonNull Context context) {
        return convertStringToModel(getMoreAppsString(context));
    }

    public static String getMoreAppsString(Context context) {
        return SharedPrefsHelper.getInstance(context).get(AppPrefStrings.MORE_APPS, "");
    }

    private static ArrayList<MoreAppsDetails> convertStringToModel(String json) {
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
            SharedPrefsHelper.getInstance(context).save(AppPrefStrings.MORE_APPS, appModelsJson);
        }
    }

    public static void setMoreApps(@NonNull Context context, String moreAppsJSON) {
        if (!TextUtils.isEmpty(moreAppsJSON)) {
            SharedPrefsHelper.getInstance(context).save(AppPrefStrings.MORE_APPS, moreAppsJSON);
        }
    }

    interface AppPrefStrings {
        String MORE_APPS = "MORE_APPS";
    }
}
