package com.rocky.moreapps;

public interface MoreAppsLifecycleListener {
    void onStart();

    void onStop();

    void showingDialog();

    void onComplete();
}