package com.rocky.moreapps.example;

import android.app.Application;
import android.util.Log;

import com.rocky.moreapps.BuildConfig;
import com.rocky.moreapps.MoreAppsDialog;
import com.rocky.moreapps.MoreAppsDownloadListener;
import com.rocky.moreapps.MoreAppsModel;

import java.util.List;

public class CoreApp extends Application {
    public static final String JSON_FILE_URL = "https://raghavsatyadev.github.io/more_apps.json";
    private static final String TAG = CoreApp.class.getSimpleName();
    private static CoreApp mInstance;
    private MoreAppsDialog moreAppsDialog;

    public static synchronized CoreApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

//        this pattern is part of option-2
        createMoreAppDialog();
    }

    public MoreAppsDialog getMoreAppsDialog() {
        if (moreAppsDialog == null) createMoreAppDialog();
        return moreAppsDialog;
    }

    private void createMoreAppDialog() {
        moreAppsDialog = new MoreAppsDialog.Builder(this, JSON_FILE_URL)
                .removeApplicationFromList(BuildConfig.APPLICATION_ID)
                .dialogTitle("More Apps")
                .build(new MoreAppsDownloadListener() {
                    @Override
                    public void onSuccess(MoreAppsDialog moreAppsDialog, List<MoreAppsModel> moreAppsModels) {

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
    }
}
