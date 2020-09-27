package com.rocky.moreapps;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class MoreAppsLifecycleObserver implements LifecycleObserver {

    private boolean isFirstStart;

    private Context context;

    private LifecycleOwner lifecycleOwner;

    private ForceUpdater.UpdateDialogType updateDialogType;

    private int styleRes;

    private MoreAppsLifecycleListener listener;

    private boolean isShowing = false;

    public MoreAppsLifecycleObserver(@NonNull Context context,
                                     @NonNull LifecycleOwner lifecycleOwner,
                                     @NonNull ForceUpdater.UpdateDialogType updateDialogType,
                                     int styleRes,
                                     MoreAppsLifecycleListener listener) {
        super();
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.updateDialogType = updateDialogType;
        this.styleRes = styleRes;
        this.listener = listener;

        isFirstStart = true;

        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!isFirstStart) {
            if (listener != null) listener.onStart();
            if (updateDialogType == ForceUpdater.UpdateDialogType.HARD_UPDATE || updateDialogType == ForceUpdater.UpdateDialogType.HARD_REDIRECT) {
                if (!isShowing) {
                    isShowing = true;
                    ForceUpdater.showUpdateDialogs(context, styleRes, () -> {
                        isShowing = false;
                    });
                    if (listener != null) listener.showingDialog();
                }
            } else {
                removeObserver();
                if (listener != null) listener.onComplete();
            }
        } else {
            isFirstStart = false;
        }
    }

    public void removeObserver() {
        lifecycleOwner.getLifecycle().removeObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (listener != null) listener.onStop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        lifecycleOwner.getLifecycle().removeObserver(this);
    }
}
