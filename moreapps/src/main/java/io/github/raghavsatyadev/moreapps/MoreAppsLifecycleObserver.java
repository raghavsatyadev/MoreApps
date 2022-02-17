package io.github.raghavsatyadev.moreapps;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class MoreAppsLifecycleObserver implements DefaultLifecycleObserver {
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

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        if (!isFirstStart) {
            if (listener != null) listener.onStart();
            if (updateDialogType == ForceUpdater.UpdateDialogType.HARD_UPDATE || updateDialogType == ForceUpdater.UpdateDialogType.HARD_REDIRECT) {
                if (!isShowing) {
                    isShowing = true;
                    ForceUpdater.showUpdateDialogs(context, styleRes, () -> isShowing = false);
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

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        if (listener != null) listener.onStop();
        DefaultLifecycleObserver.super.onStop(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        lifecycleOwner.getLifecycle().removeObserver(this);
        DefaultLifecycleObserver.super.onDestroy(owner);
    }
}
