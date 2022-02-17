package com.rocky.moreapps.example;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Configuration;

import io.github.raghavsatyadev.moreapps.MoreAppsBuilder;
import io.github.raghavsatyadev.moreapps.MoreAppsDialog;

import java.util.concurrent.TimeUnit;

public class CoreApp extends Application implements Configuration.Provider {
    public static final String JSON_FILE_URL = "https://raghavsatyadev.github.io/more_apps_example.json";

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
        moreAppsDialog = new MoreAppsBuilder(this, JSON_FILE_URL)
                .setPeriodicSettings(15, TimeUnit.MINUTES, R.mipmap.ic_launcher, R.drawable.ic_small_icon)
                .build();
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.ERROR)
                .build();
    }
}