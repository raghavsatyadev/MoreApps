package com.rocky.moreapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public interface MoreAppsLifecycleListener {
    /**
     * {@link AppCompatActivity} or {@link Fragment} onStart LifeCycle Method
     */
    void onStart();

    /**
     * {@link AppCompatActivity} or {@link Fragment} onStop LifeCycle Method
     */
    void onStop();

    /**
     * updater dialog is showing, stop other works
     */
    void showingDialog();

    /**
     * on completing all processes regarding updater, continue other work
     */
    void onComplete();
}