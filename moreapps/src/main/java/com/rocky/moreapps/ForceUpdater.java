package com.rocky.moreapps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;

import com.rocky.moreapps.listener.MoreAppsUpdateDialogListener;
import com.rocky.moreapps.model.HardUpdateDetails;
import com.rocky.moreapps.model.MoreAppsDetails;
import com.rocky.moreapps.model.RedirectDetails;
import com.rocky.moreapps.model.SoftUpdateDetails;
import com.rocky.moreapps.utils.MoreAppsUtils;

public class ForceUpdater {

    /**
     * check whether to show the dialogs or not
     * <p>
     * This method will check {@link android.content.SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsBuilder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @param context {@link Context} of Activity or Fragment
     */
    public static boolean shouldShowUpdateDialogs(Context context) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        MoreAppsDetails moreAppsDetails = MoreAppsUtils.getCurrentAppModel(context);

        if (moreAppsDetails != null) {
            if (moreAppsDetails.redirectDetails != null && moreAppsDetails.redirectDetails.enable) {
                //redirection enabled
                return true;
            } else if (moreAppsDetails.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                //hard update enabled
                return true;
            } else if (moreAppsDetails.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
                //soft update enabled
                return moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode;
            }
        }
        return false;
    }

    /**
     * call this method to show the update dialogs
     * <p>
     * This method will check {@link android.content.SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsBuilder#build()} first to load the data in {@link android.content.SharedPreferences}
     *
     * @param context              {@link Context} of Activity or Fragment
     * @param moreAppsUpdateDialogListener to listen for dialog close events
     */
    public static void showUpdateDialogs(Context context, MoreAppsUpdateDialogListener moreAppsUpdateDialogListener) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        MoreAppsDetails moreAppsDetails = MoreAppsUtils.getCurrentAppModel(context);

        if (moreAppsDetails != null) {
            if (moreAppsDetails.redirectDetails != null && moreAppsDetails.redirectDetails.enable) {
                //redirection called
                showRedirectDialog(context, moreAppsDetails, moreAppsUpdateDialogListener);
            } else if (moreAppsDetails.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                //hard update called
                showHardUpdateDialog(context, moreAppsDetails);
            } else if (moreAppsDetails.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
                if (moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode) {
                    //soft update called
                    showSoftUpdateDialog(context, moreAppsDetails, moreAppsUpdateDialogListener);
                }
            }
        }
    }

    /**
     * to know which type of dialog is needed to show
     *
     * @param moreAppsDetails {@link MoreAppsDetails} of current app, get this by calling {@link MoreAppsUtils#getCurrentAppModel}
     * @return {@link UpdateDialogType}
     */
    public static UpdateDialogType dialogToShow(Context context, MoreAppsDetails moreAppsDetails) throws PackageManager.NameNotFoundException {
        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;

        if (moreAppsDetails != null) {
            if (moreAppsDetails.redirectDetails != null && moreAppsDetails.redirectDetails.enable) {
                //redirection required
                return moreAppsDetails.redirectDetails.hardRedirect ? UpdateDialogType.HARD_REDIRECT : UpdateDialogType.SOFT_REDIRECT;
            } else if (moreAppsDetails.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                //hard update required
                return UpdateDialogType.HARD_UPDATE;
            } else if (moreAppsDetails.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
                if (moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode) {
                    //soft update required
                    return UpdateDialogType.SOFT_UPDATE;
                }
            }
        }
        return UpdateDialogType.NONE;
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftUpdateDialog(final Context context, final MoreAppsDetails moreAppsDetails, final MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
            SoftUpdateDetails softUpdateDetails = moreAppsDetails.softUpdateDetails;
            new AlertDialog.Builder(context)
                    .setTitle(softUpdateDetails.dialogTitle)
                    .setMessage(softUpdateDetails.dialogMessage)
                    .setPositiveButton(softUpdateDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MoreAppsUtils.openBrowser(context, moreAppsDetails.appLink);
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
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     */
    public static void showHardUpdateDialog(final Context context, final MoreAppsDetails moreAppsDetails) {
        if (moreAppsDetails != null && moreAppsDetails.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable) {
            HardUpdateDetails hardUpdateDetails = moreAppsDetails.hardUpdateDetails;
            new AlertDialog.Builder(context)
                    .setTitle(hardUpdateDetails.dialogTitle)
                    .setMessage(hardUpdateDetails.dialogMessage)
                    .setPositiveButton(hardUpdateDetails.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MoreAppsUtils.openBrowser(context, moreAppsDetails.appLink);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     */
    public static void showHardRedirectDialog(final Context context, final MoreAppsDetails moreAppsDetails) {
        if (moreAppsDetails != null && moreAppsDetails.redirectDetails != null &&
                moreAppsDetails.redirectDetails.enable && moreAppsDetails.redirectDetails.hardRedirect) {
            final RedirectDetails redirectDetails = moreAppsDetails.redirectDetails;
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
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftRedirectDialog(final Context context, MoreAppsDetails moreAppsDetails, final MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.redirectDetails != null &&
                moreAppsDetails.redirectDetails.enable && !moreAppsDetails.redirectDetails.hardRedirect) {
            final RedirectDetails redirectDetails = moreAppsDetails.redirectDetails;
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
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    private static void showRedirectDialog(final Context context, final MoreAppsDetails moreAppsDetails, final MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.redirectDetails != null &&
                moreAppsDetails.redirectDetails.enable) {
            if (moreAppsDetails.redirectDetails.hardRedirect) {
                showHardRedirectDialog(context, moreAppsDetails);
            } else {
                showSoftRedirectDialog(context, moreAppsDetails, listener);
            }
        }
    }

    public enum UpdateDialogType {
        NONE, SOFT_UPDATE, HARD_UPDATE, SOFT_REDIRECT, HARD_REDIRECT
    }
}
