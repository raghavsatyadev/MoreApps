package com.rocky.moreapps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

public class ForceUpdater {
    static UpdateListener updateListener;
    static UpdateListener.UpdateStatus updateStatus = UpdateListener.UpdateStatus.NOT_STARTED;

    /**
     * call this method only to wait for API call to finish
     *
     * @param updateListener {@link UpdateDialogListener}
     */
    public static void addUpdateListener(UpdateListener updateListener) {
        if (updateStatus == UpdateListener.UpdateStatus.COMPLETE) {
            updateListener.onEvent(UpdateListener.UpdateStatus.COMPLETE);
        } else {
            ForceUpdater.updateListener = updateListener;
        }
    }

    public static void removeUpdateListener() {
        updateListener = null;
    }

    /**
     * check whether to show the dialogs or not
     * <p>
     * This method will check {@link android.content.SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsDialog.Builder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @param context {@link Context} of Activity or Fragment
     */
    public static boolean shouldShowUpdateDialogs(Context context) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        MoreAppsModel moreAppsModel = getCurrentAppModel(context);

        if (moreAppsModel != null) {
            if (moreAppsModel.redirectDetails != null && moreAppsModel.redirectDetails.enable) {
                //redirection enabled
                return true;
            } else if (moreAppsModel.hardUpdateDetails != null && moreAppsModel.hardUpdateDetails.enable) {
                //hard update enabled
                return moreAppsModel.minVersion > versionCode;
            } else if (moreAppsModel.softUpdateDetails != null && moreAppsModel.softUpdateDetails.enable) {
                //soft update enabled
                return moreAppsModel.currentVersion > versionCode || moreAppsModel.minVersion > versionCode;
            }
        }
        return false;
    }

    /**
     * call this method to show the update dialogs
     * <p>
     * This method will check {@link android.content.SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsDialog.Builder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @param context              {@link Context} of Activity or Fragment
     * @param updateDialogListener to listen for dialog close events
     */
    public static void showUpdateDialogs(Context context, UpdateDialogListener updateDialogListener) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        MoreAppsModel moreAppsModel = getCurrentAppModel(context);

        if (moreAppsModel != null) {
            if (moreAppsModel.redirectDetails != null && moreAppsModel.redirectDetails.enable) {
                //redirection called
                showRedirectDialog(context, moreAppsModel, updateDialogListener);
            } else if (moreAppsModel.hardUpdateDetails != null && moreAppsModel.hardUpdateDetails.enable) {
                if (moreAppsModel.minVersion > versionCode) {
                    //hard update called
                    showHardUpdateDialog(context, moreAppsModel);
                }
            } else if (moreAppsModel.softUpdateDetails != null && moreAppsModel.softUpdateDetails.enable) {
                if (moreAppsModel.currentVersion > versionCode || moreAppsModel.minVersion > versionCode) {
                    //soft update called
                    showSoftUpdateDialog(context, moreAppsModel, updateDialogListener);
                }
            }
        }
    }

    /**
     * to know which type of dialog is needed to show
     *
     * @param moreAppsModel {@link MoreAppsModel} of current app, get this by calling {@link ForceUpdater#getCurrentAppModel}
     * @return {@link UpdateDialogType}
     */
    public static UpdateDialogType dialogToShow(Context context, MoreAppsModel moreAppsModel) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        if (moreAppsModel != null) {
            if (moreAppsModel.redirectDetails != null && moreAppsModel.redirectDetails.enable) {
                //redirection required
                return moreAppsModel.redirectDetails.hardRedirect ? UpdateDialogType.HARD_REDIRECT : UpdateDialogType.SOFT_REDIRECT;
            } else if (moreAppsModel.hardUpdateDetails != null && moreAppsModel.hardUpdateDetails.enable) {
                if (moreAppsModel.minVersion > versionCode) {
                    //hard update required
                    return UpdateDialogType.HARD_UPDATE;
                }
            } else if (moreAppsModel.softUpdateDetails != null && moreAppsModel.softUpdateDetails.enable) {
                if (moreAppsModel.currentVersion > versionCode || moreAppsModel.minVersion > versionCode) {
                    //soft update required
                    return UpdateDialogType.SOFT_UPDATE;
                }
            }
        }
        return UpdateDialogType.NONE;
    }

    /**
     * NOTE : call {@link MoreAppsDialog.Builder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @return {@link MoreAppsModel} of current app if present
     */
    public static MoreAppsModel getCurrentAppModel(Context context) {
        ArrayList<MoreAppsModel> moreApps = SharedPrefsUtil.getMoreApps(context);
        if (moreApps != null && !moreApps.isEmpty()) {
            String currentPackageName = context.getPackageName();
            for (int i = 0; i < moreApps.size(); i++) {
                MoreAppsModel moreAppsModel = moreApps.get(i);
                if (moreAppsModel.packageName.equals(currentPackageName)) {
                    return moreAppsModel;
                }
            }
        }
        return null;
    }

    /**
     * @param context       {@link Context} of Activity or Fragment
     * @param moreAppsModel {@link MoreAppsModel}
     * @param listener      {@link UpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftUpdateDialog(final Context context, final MoreAppsModel moreAppsModel, final UpdateDialogListener listener) {
        if (moreAppsModel != null && moreAppsModel.softUpdateDetails != null && moreAppsModel.softUpdateDetails.enable) {
            SoftUpdateDetails softUpdateDetails = moreAppsModel.softUpdateDetails;
            new AlertDialog.Builder(context)
                    .setTitle(softUpdateDetails.dialogTitle)
                    .setMessage(softUpdateDetails.dialogMessage)
                    .setPositiveButton(softUpdateDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MoreAppsUtils.openBrowser(context, moreAppsModel.appLink);
                        }
                    })
                    .setNegativeButton(softUpdateDetails.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (listener != null) listener.onClose();
                        }
                    })
                    .setCancelable(true)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (listener != null) listener.onClose();
                        }
                    })
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context} of Activity or Fragment
     * @param moreAppsModel {@link MoreAppsModel}
     */
    public static void showHardUpdateDialog(final Context context, final MoreAppsModel moreAppsModel) {
        if (moreAppsModel != null && moreAppsModel.hardUpdateDetails != null && moreAppsModel.hardUpdateDetails.enable) {
            HardUpdateDetails hardUpdateDetails = moreAppsModel.hardUpdateDetails;
            new AlertDialog.Builder(context)
                    .setTitle(hardUpdateDetails.dialogTitle)
                    .setMessage(hardUpdateDetails.dialogMessage)
                    .setPositiveButton(hardUpdateDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MoreAppsUtils.openBrowser(context, moreAppsModel.appLink);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context} of Activity or Fragment
     * @param moreAppsModel {@link MoreAppsModel}
     */
    public static void showHardRedirectDialog(final Context context, final MoreAppsModel moreAppsModel) {
        if (moreAppsModel != null && moreAppsModel.redirectDetails != null &&
                moreAppsModel.redirectDetails.enable && moreAppsModel.redirectDetails.hardRedirect) {
            final RedirectDetails redirectDetails = moreAppsModel.redirectDetails;
            new AlertDialog.Builder(context)
                    .setTitle(redirectDetails.dialogTitle)
                    .setMessage(redirectDetails.dialogMessage)
                    .setPositiveButton(redirectDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MoreAppsUtils.openBrowser(context, redirectDetails.appLink);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context} of Activity or Fragment
     * @param moreAppsModel {@link MoreAppsModel}
     * @param listener      {@link UpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftRedirectDialog(final Context context, MoreAppsModel moreAppsModel, final UpdateDialogListener listener) {
        if (moreAppsModel != null && moreAppsModel.redirectDetails != null &&
                moreAppsModel.redirectDetails.enable && !moreAppsModel.redirectDetails.hardRedirect) {
            final RedirectDetails redirectDetails = moreAppsModel.redirectDetails;
            new AlertDialog.Builder(context)
                    .setTitle(redirectDetails.dialogTitle)
                    .setMessage(redirectDetails.dialogMessage)
                    .setPositiveButton(redirectDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MoreAppsUtils.openBrowser(context, redirectDetails.appLink);
                        }
                    })
                    .setNegativeButton(redirectDetails.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) listener.onClose();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (listener != null) listener.onClose();
                        }
                    })
                    .setCancelable(true)
                    .create()
                    .show();
        }
    }

    /**
     * @param context       {@link Context} of Activity or Fragment
     * @param moreAppsModel {@link MoreAppsModel}
     * @param listener      {@link UpdateDialogListener} to know when dialog is closed
     */
    public static void showRedirectDialog(final Context context, final MoreAppsModel moreAppsModel, final UpdateDialogListener listener) {
        if (moreAppsModel != null && moreAppsModel.redirectDetails != null &&
                moreAppsModel.redirectDetails.enable) {
            if (moreAppsModel.redirectDetails.hardRedirect) {
                showHardRedirectDialog(context, moreAppsModel);
            } else {
                showSoftRedirectDialog(context, moreAppsModel, listener);
            }
        }
    }

    public enum UpdateDialogType {
        NONE, SOFT_UPDATE, HARD_UPDATE, SOFT_REDIRECT, HARD_REDIRECT
    }
}
