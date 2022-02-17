
package io.github.raghavsatyadev.moreapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.github.raghavsatyadev.moreapps.listener.MoreAppsUpdateDialogListener;
import io.github.raghavsatyadev.moreapps.model.HardUpdateDetails;
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails;
import io.github.raghavsatyadev.moreapps.model.RedirectDetails;
import io.github.raghavsatyadev.moreapps.model.SoftUpdateDetails;
import io.github.raghavsatyadev.moreapps.utils.MoreAppsPrefUtil;
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils;

@SuppressWarnings("unused")
public class ForceUpdater {
    public static final String TAG = ForceUpdater.class.getSimpleName();

    /**
     * shows dialog if needed and keeps check of lifecycle
     *
     * @param context        {@link Context} of Activity or Fragment
     * @param lifecycleOwner Provide AppCompatActivity or Fragment Object
     * @param listener       {@link MoreAppsLifecycleListener}
     */
    public static void showDialogLive(Context context,
                                      LifecycleOwner lifecycleOwner,
                                      MoreAppsLifecycleListener listener) {
        showDialogLive(context, lifecycleOwner, 0, listener);
    }

    /**
     * shows dialog if needed and keeps check of lifecycle
     *
     * @param context  {@link Context} of Activity or Fragment
     * @param styleRes Style resource to change style of alert dialog style.
     *                 It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener {@link MoreAppsLifecycleListener}
     */
    public static void showDialogLive(Context context,
                                      LifecycleOwner lifecycleOwner,
                                      @StyleRes int styleRes,
                                      MoreAppsLifecycleListener listener) {
        ForceUpdater.UpdateDialogType updateDialogType = ForceUpdater.dialogToShow(context, MoreAppsUtils.getCurrentAppModel(context));

        if (updateDialogType == UpdateDialogType.NONE) {
            if (listener != null) listener.onComplete();
        } else {
            MoreAppsLifecycleObserver observer = new MoreAppsLifecycleObserver(context,
                    lifecycleOwner,
                    updateDialogType,
                    styleRes,
                    listener);
            if (listener != null) listener.showingDialog();
            observer.setShowing(true);
            ForceUpdater.showUpdateDialogs(context, styleRes, () -> {
                observer.removeObserver();
                if (listener != null) listener.onComplete();
            });
        }
    }

    /**
     * to know which type of dialog is needed to show
     *
     * @param moreAppsDetails {@link MoreAppsDetails} of current app,
     *                        get this by calling {@link MoreAppsUtils#getCurrentAppModel}
     * @return {@link UpdateDialogType}
     */
    public static UpdateDialogType dialogToShow(Context context,
                                                MoreAppsDetails moreAppsDetails) {
        try {
            int versionCode = getVersionCode(context);
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
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "dialogToShow: ", e);
        }
        return UpdateDialogType.NONE;
    }

    /**
     * call this method to show the update dialogs
     * <p>
     * This method will check {@link SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsBuilder#build()} first to load the data in {@link SharedPreferences}
     *
     * @param context  {@link Context} of Activity or Fragment
     * @param styleRes Style resource to change style of alert dialog style.
     *                 It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener to listen for dialog close events
     */
    public static void showUpdateDialogs(Context context,
                                         @StyleRes int styleRes,
                                         MoreAppsUpdateDialogListener listener) {
        try {
            int versionCode = getVersionCode(context);

            MoreAppsDetails moreAppsDetails = MoreAppsUtils.getCurrentAppModel(context);

            if (moreAppsDetails != null) {
                if (moreAppsDetails.redirectDetails != null && moreAppsDetails.redirectDetails.enable) {
                    //redirection called
                    showRedirectDialog(context, moreAppsDetails, styleRes, listener);
                } else if (moreAppsDetails.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                    //hard update called
                    showHardUpdateDialog(context, moreAppsDetails, styleRes, listener);
                } else if (moreAppsDetails.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
                    if (moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode) {
                        //soft update called
                        showSoftUpdateDialog(context, moreAppsDetails, styleRes, listener);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "showUpdateDialogs: ", e);
        }
    }

    private static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param styleRes        Style resource to change style of alert dialog style.
     *                        It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    private static void showRedirectDialog(final Context context,
                                           final MoreAppsDetails moreAppsDetails,
                                           @StyleRes int styleRes,
                                           final MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.redirectDetails != null &&
                    moreAppsDetails.redirectDetails.enable) {
            if (moreAppsDetails.redirectDetails.hardRedirect) {
                showHardRedirectDialog(context, moreAppsDetails, styleRes, listener);
            } else {
                showSoftRedirectDialog(context, moreAppsDetails, styleRes, listener);
            }
        }
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param styleRes        Style resource to change style of alert dialog style.
     */
    public static void showHardUpdateDialog(final Context context,
                                            final MoreAppsDetails moreAppsDetails,
                                            @StyleRes int styleRes,
                                            MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable) {
            HardUpdateDetails hardUpdateDetails = moreAppsDetails.hardUpdateDetails;
            AlertDialog alertDialog = getThemedDialog(context, styleRes)
                    .setTitle(hardUpdateDetails.dialogTitle)
                    .setMessage(hardUpdateDetails.dialogMessage)
                    .setPositiveButton(hardUpdateDetails.positiveButton, null)
                    .setCancelable(false)
                    .setOnDismissListener(dialog -> {
                        if (listener != null) listener.onClose();
                    })
                    .create();
            alertDialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> MoreAppsUtils.openBrowser(context, moreAppsDetails.appLink));
            });
            alertDialog.show();
        }
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param styleRes        Style resource to change style of alert dialog style.
     *                        It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftUpdateDialog(final Context context,
                                            final MoreAppsDetails moreAppsDetails,
                                            @StyleRes int styleRes,
                                            final MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
            SoftUpdateDetails softUpdateDetails = moreAppsDetails.softUpdateDetails;
            if (MoreAppsPrefUtil.shouldShowSoftUpdate(context, softUpdateDetails.dialogShowCount, moreAppsDetails.currentVersion)) {
                getThemedDialog(context, styleRes)
                        .setTitle(softUpdateDetails.dialogTitle)
                        .setMessage(softUpdateDetails.dialogMessage)
                        .setPositiveButton(softUpdateDetails.positiveButton, (dialog, which) -> MoreAppsUtils.openBrowser(context, moreAppsDetails.appLink))
                        .setNegativeButton(softUpdateDetails.negativeButton, (dialog, which) -> {
                            dialog.dismiss();
                            if (listener != null) listener.onClose();
                        })
                        .setOnDismissListener(dialogInterface -> {
                            if (listener != null) listener.onClose();
                        })
                        .setCancelable(true)
                        .show();
                MoreAppsPrefUtil.increaseSoftUpdateShownTimes(context);
            }
        }
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param styleRes        Style resource to change style of alert dialog style.
     */
    public static void showHardRedirectDialog(final Context context,
                                              final MoreAppsDetails moreAppsDetails,
                                              @StyleRes int styleRes, MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.redirectDetails != null &&
                    moreAppsDetails.redirectDetails.enable && moreAppsDetails.redirectDetails.hardRedirect) {
            final RedirectDetails redirectDetails = moreAppsDetails.redirectDetails;
            AlertDialog alertDialog = getThemedDialog(context, styleRes)
                    .setTitle(redirectDetails.dialogTitle)
                    .setMessage(redirectDetails.dialogMessage)
                    .setPositiveButton(redirectDetails.positiveButton, null)
                    .setOnDismissListener(dialog -> {
                        if (listener != null) listener.onClose();
                    })
                    .setCancelable(false)
                    .create();
            alertDialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> MoreAppsUtils.openBrowser(context, redirectDetails.appLink));
            });
            alertDialog.show();
        }
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param styleRes        Style resource to change style of alert dialog style.
     *                        It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftRedirectDialog(final Context context,
                                              MoreAppsDetails moreAppsDetails,
                                              @StyleRes int styleRes,
                                              final MoreAppsUpdateDialogListener listener) {
        if (moreAppsDetails != null && moreAppsDetails.redirectDetails != null &&
                    moreAppsDetails.redirectDetails.enable && !moreAppsDetails.redirectDetails.hardRedirect) {
            final RedirectDetails redirectDetails = moreAppsDetails.redirectDetails;
            getThemedDialog(context, styleRes)
                    .setTitle(redirectDetails.dialogTitle)
                    .setMessage(redirectDetails.dialogMessage)
                    .setPositiveButton(redirectDetails.positiveButton, (dialog, which) -> MoreAppsUtils.openBrowser(context, redirectDetails.appLink))
                    .setNegativeButton(redirectDetails.negativeButton, (dialog, which) -> {
                        if (listener != null) listener.onClose();
                    })
                    .setOnDismissListener(dialogInterface -> {
                        if (listener != null) listener.onClose();
                    })
                    .setCancelable(true)
                    .show();
        }
    }

    /**
     * @param styleRes Style resource to change style of alert dialog style.
     *                 It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @return {@link MaterialAlertDialogBuilder}
     */
    private static MaterialAlertDialogBuilder getThemedDialog(Context context, int styleRes) {
        return styleRes == 0 ? new MaterialAlertDialogBuilder(context) : new MaterialAlertDialogBuilder(context, styleRes);
    }

    /**
     * call this method to show the update dialogs
     * <p>
     * This method will check {@link SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsBuilder#build()} first to load the data in {@link SharedPreferences}
     *
     * @param context  {@link Context} of Activity or Fragment
     * @param listener to listen for dialog close events
     */
    public static void showUpdateDialogs(Context context,
                                         MoreAppsUpdateDialogListener listener) {
        showUpdateDialogs(context, 0, listener);
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    private static void showRedirectDialog(final Context context,
                                           final MoreAppsDetails moreAppsDetails,
                                           final MoreAppsUpdateDialogListener listener) {
        showRedirectDialog(context, moreAppsDetails, 0, listener);
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     */
    public static void showHardUpdateDialog(final Context context,
                                            final MoreAppsDetails moreAppsDetails,
                                            MoreAppsUpdateDialogListener listener) {
        showHardUpdateDialog(context, moreAppsDetails, 0, listener);
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftUpdateDialog(final Context context,
                                            final MoreAppsDetails moreAppsDetails,
                                            final MoreAppsUpdateDialogListener listener) {
        showSoftUpdateDialog(context, moreAppsDetails, 0, listener);
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     */
    public static void showHardRedirectDialog(final Context context,
                                              final MoreAppsDetails moreAppsDetails,
                                              MoreAppsUpdateDialogListener listener) {
        showHardRedirectDialog(context, moreAppsDetails, 0, listener);
    }

    /**
     * @param context         {@link Context} of Activity or Fragment
     * @param moreAppsDetails {@link MoreAppsDetails}
     * @param listener        {@link MoreAppsUpdateDialogListener} to know when dialog is closed
     */
    public static void showSoftRedirectDialog(final Context context,
                                              MoreAppsDetails moreAppsDetails,
                                              final MoreAppsUpdateDialogListener listener) {
        showSoftRedirectDialog(context, moreAppsDetails, 0, listener);
    }

    /**
     * check whether to show the dialogs or not
     * <p>
     * This method will check {@link SharedPreferences} for the already stored data
     * <p>
     * NOTE : call {@link MoreAppsBuilder#build()} first to load the data in {@link SharedPreferences}
     *
     * @param context {@link Context} of Activity or Fragment
     */
    public static boolean shouldShowUpdateDialogs(Context context) {

        try {
            int versionCode;
            versionCode = getVersionCode(context);

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
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "shouldShowUpdateDialogs: ", e);
        }
        return false;
    }

    public enum UpdateDialogType {
        NONE, SOFT_UPDATE, HARD_UPDATE, SOFT_REDIRECT, HARD_REDIRECT
    }
}
