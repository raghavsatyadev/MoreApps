package com.rocky.moreapps;

public interface MoreAppsLifecycleListener {
    /**
     * AppCompatActivity or Fragment onStart LifeCycle Method
     */
    void onStart();

    /**
     * AppCompatActivity or Fragment onStop LifeCycle Method
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