package com.rocky.moreapps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefsUtil {

    private static final String TAG = SharedPrefsUtil.class.getSimpleName();

    public static ArrayList<MoreAppsModel> getMoreApps(@NonNull Context context) {
        Gson gson = new Gson();
        String json = SharedPrefsHelper.getInstance(context).get(AppPrefStrings.MORE_APPS, "");
        ArrayList<MoreAppsModel> moreAppsModels = new ArrayList<>();
        try {
            Type appModelsType = new TypeToken<ArrayList<MoreAppsModel>>() {
            }.getType();
            List<MoreAppsModel> moreAppsModelsTemp = gson.fromJson(json, appModelsType);
            if (moreAppsModelsTemp != null) {
                moreAppsModels.addAll(moreAppsModelsTemp);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "getMoreApps: ", e);
        }
        return moreAppsModels;
    }

    public static void setMoreApps(@NonNull Context context, List<MoreAppsModel> moreAppsModels) {
        Gson gson = new Gson();
        if (moreAppsModels != null) {
            String appModelsJson = gson.toJson(moreAppsModels);
            SharedPrefsHelper.getInstance(context).save(AppPrefStrings.MORE_APPS, appModelsJson);
        }
    }

    interface AppPrefStrings {
        String MORE_APPS = "MORE_APPS";
    }
}
