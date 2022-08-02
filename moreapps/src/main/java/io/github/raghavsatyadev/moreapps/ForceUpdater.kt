@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.raghavsatyadev.moreapps

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.PackageManager.PackageInfoFlags
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.NONE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_UPDATE
import io.github.raghavsatyadev.moreapps.listener.MoreAppsUpdateDialogListener
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails
import io.github.raghavsatyadev.moreapps.utils.AppLog
import io.github.raghavsatyadev.moreapps.utils.MoreAppsPrefUtil
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils

object ForceUpdater {
    val TAG: String = ForceUpdater::class.java.simpleName

    /**
     * shows dialog if needed and keeps check of lifecycle
     *
     * @param context        [Context] of Activity or Fragment
     * @param lifecycleOwner Provide AppCompatActivity or Fragment Object
     * @param listener       [MoreAppsLifecycleListener]
     */
    fun showDialogLive(
        context: Context,
        lifecycleOwner: LifecycleOwner?,
        listener: MoreAppsLifecycleListener?,
    ) {
        showDialogLive(context, lifecycleOwner, 0, listener)
    }

    /**
     * shows dialog if needed and keeps check of lifecycle
     *
     * @param context  [Context] of Activity or Fragment
     * @param styleRes Style resource to change style of alert dialog style.
     * It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener [MoreAppsLifecycleListener]
     */

    fun showDialogLive(
        context: Context,
        lifecycleOwner: LifecycleOwner?,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsLifecycleListener?,
    ) {
        val updateDialogType = dialogToShow(context, MoreAppsUtils.getCurrentAppModel(context))
        if (updateDialogType == NONE) {
            listener?.onComplete()
        } else {
            val observer = MoreAppsLifecycleObserver(
                context,
                lifecycleOwner!!,
                updateDialogType,
                styleRes,
                listener
            )
            listener?.showingDialog()
            observer.setShowing(true)
            showUpdateDialogs(context, styleRes) {
                observer.removeObserver()
                listener?.onComplete()
            }
        }
    }

    /**
     * to know which type of dialog is needed to show
     *
     * @param moreAppsDetails [MoreAppsDetails] of current app,
     * get this by calling [MoreAppsUtils.getCurrentAppModel]
     * @return [UpdateDialogType]
     */

    fun dialogToShow(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
    ): UpdateDialogType {
        try {
            val versionCode = getVersionCode(context)
            if (moreAppsDetails != null) {
                if (moreAppsDetails.redirectDetails.enable) {
                    //redirection required
                    return if (moreAppsDetails.redirectDetails.hardRedirect) HARD_REDIRECT else SOFT_REDIRECT
                } else if (moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                    //hard update required
                    return HARD_UPDATE
                } else if (moreAppsDetails.softUpdateDetails.enable && (moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode)) {
                    //soft update required
                    return SOFT_UPDATE
                }
            }
        } catch (e: NameNotFoundException) {
            AppLog.loge(false, kotlinFileName, "dialogToShow", e, Exception())
        }
        return NONE
    }

    /**
     * call this method to show the update dialogs
     *
     *
     * This method will check [android.content.SharedPreferences] for the already stored data
     *
     *
     * NOTE : call [MoreAppsBuilder.build] first to load the data in [android.content.SharedPreferences]
     *
     * @param context  [Context] of Activity or Fragment
     * @param styleRes Style resource to change style of alert dialog style.
     * It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener to listen for dialog close events
     */

    fun showUpdateDialogs(
        context: Context,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsUpdateDialogListener,
    ) {
        try {
            val versionCode = getVersionCode(context)
            val moreAppsDetails = MoreAppsUtils.getCurrentAppModel(context)
            if (moreAppsDetails != null) {
                if (moreAppsDetails.redirectDetails.enable) {
                    //redirection called
                    showRedirectDialog(context, moreAppsDetails, styleRes, listener)
                } else if (moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                    //hard update called
                    showHardUpdateDialog(context, moreAppsDetails, styleRes, listener)
                } else if (moreAppsDetails.softUpdateDetails.enable) {
                    if (moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode) {
                        //soft update called
                        showSoftUpdateDialog(context, moreAppsDetails, styleRes, listener)
                    }
                }
            }
        } catch (e: NameNotFoundException) {
            AppLog.loge(false, kotlinFileName, "showUpdateDialogs", e, Exception())
        }
    }

    @Throws(NameNotFoundException::class)
    private fun getVersionCode(context: Context): Int {
        return PackageInfoCompat.getLongVersionCode(
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_ACTIVITIES
                )
            }
        ).toInt()
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param styleRes        Style resource to change style of alert dialog style.
     * It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener        [MoreAppsUpdateDialogListener] to know when dialog is closed
     */
    private fun showRedirectDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsUpdateDialogListener,
    ) {
        if (moreAppsDetails?.redirectDetails != null &&
            moreAppsDetails.redirectDetails.enable
        ) {
            if (moreAppsDetails.redirectDetails.hardRedirect) {
                showHardRedirectDialog(context, moreAppsDetails, styleRes, listener)
            } else {
                showSoftRedirectDialog(context, moreAppsDetails, styleRes, listener)
            }
        }
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param styleRes        Style resource to change style of alert dialog style.
     */
    fun showHardUpdateDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        if (moreAppsDetails?.hardUpdateDetails != null && moreAppsDetails.hardUpdateDetails.enable) {
            val hardUpdateDetails = moreAppsDetails.hardUpdateDetails
            val alertDialog = getThemedDialog(context, styleRes)
                .setTitle(hardUpdateDetails.dialogTitle)
                .setMessage(hardUpdateDetails.dialogMessage)
                .setPositiveButton(hardUpdateDetails.positiveButton, null)
                .setCancelable(false)
                .setOnDismissListener { listener?.onClose() }
                .create()
            alertDialog.setOnShowListener { dialog: DialogInterface ->
                val button = (dialog as AlertDialog).getButton(
                    AlertDialog.BUTTON_POSITIVE
                )
                button.setOnClickListener {
                    MoreAppsUtils.openBrowser(
                        context,
                        moreAppsDetails.appLink
                    )
                }
            }
            alertDialog.show()
        }
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param styleRes        Style resource to change style of alert dialog style.
     * It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener        [MoreAppsUpdateDialogListener] to know when dialog is closed
     */
    fun showSoftUpdateDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        if (moreAppsDetails?.softUpdateDetails != null && moreAppsDetails.softUpdateDetails.enable) {
            val softUpdateDetails = moreAppsDetails.softUpdateDetails
            if (MoreAppsPrefUtil.shouldShowSoftUpdate(
                    context,
                    softUpdateDetails.dialogShowCount,
                    moreAppsDetails.currentVersion
                )
            ) {
                getThemedDialog(context, styleRes)
                    .setTitle(softUpdateDetails.dialogTitle)
                    .setMessage(softUpdateDetails.dialogMessage)
                    .setPositiveButton(softUpdateDetails.positiveButton) { _: DialogInterface?, _: Int ->
                        MoreAppsUtils.openBrowser(
                            context,
                            moreAppsDetails.appLink
                        )
                    }
                    .setNegativeButton(softUpdateDetails.negativeButton) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        listener?.onClose()
                    }
                    .setOnDismissListener { listener?.onClose() }
                    .setCancelable(true)
                    .show()
                MoreAppsPrefUtil.increaseSoftUpdateShownTimes(context)
            }
        }
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param styleRes        Style resource to change style of alert dialog style.
     */
    fun showHardRedirectDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        if (moreAppsDetails?.redirectDetails != null &&
            moreAppsDetails.redirectDetails.enable && moreAppsDetails.redirectDetails.hardRedirect
        ) {
            val redirectDetails = moreAppsDetails.redirectDetails
            val alertDialog = getThemedDialog(context, styleRes)
                .setTitle(redirectDetails.dialogTitle)
                .setMessage(redirectDetails.dialogMessage)
                .setPositiveButton(redirectDetails.positiveButton, null)
                .setOnDismissListener { listener?.onClose() }
                .setCancelable(false)
                .create()
            alertDialog.setOnShowListener { dialog: DialogInterface ->
                val button = (dialog as AlertDialog).getButton(
                    AlertDialog.BUTTON_POSITIVE
                )
                button.setOnClickListener {
                    MoreAppsUtils.openBrowser(
                        context,
                        redirectDetails.appLink
                    )
                }
            }
            alertDialog.show()
        }
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param styleRes        Style resource to change style of alert dialog style.
     * It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @param listener        [MoreAppsUpdateDialogListener] to know when dialog is closed
     */
    fun showSoftRedirectDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        @StyleRes
        styleRes: Int,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        if (moreAppsDetails?.redirectDetails != null &&
            moreAppsDetails.redirectDetails.enable && !moreAppsDetails.redirectDetails.hardRedirect
        ) {
            val redirectDetails = moreAppsDetails.redirectDetails
            getThemedDialog(context, styleRes)
                .setTitle(redirectDetails.dialogTitle)
                .setMessage(redirectDetails.dialogMessage)
                .setPositiveButton(redirectDetails.positiveButton) { _: DialogInterface?, _: Int ->
                    MoreAppsUtils.openBrowser(
                        context,
                        redirectDetails.appLink
                    )
                }
                .setNegativeButton(redirectDetails.negativeButton) { _: DialogInterface?, _: Int -> listener?.onClose() }
                .setOnDismissListener { listener?.onClose() }
                .setCancelable(true)
                .show()
        }
    }

    /**
     * @param styleRes Style resource to change style of alert dialog style.
     * It must extend instances of Theme.MaterialComponents.Dialog.Alert
     * @return [MaterialAlertDialogBuilder]
     */
    private fun getThemedDialog(context: Context, styleRes: Int): MaterialAlertDialogBuilder {
        return if (styleRes == 0) MaterialAlertDialogBuilder(context) else MaterialAlertDialogBuilder(
            context,
            styleRes
        )
    }

    /**
     * call this method to show the update dialogs
     *
     *
     * This method will check [android.content.SharedPreferences] for the already stored data
     *
     *
     * NOTE : call [MoreAppsBuilder.build] first to load the data in [android.content.SharedPreferences]
     *
     * @param context  [Context] of Activity or Fragment
     * @param listener to listen for dialog close events
     */

    fun showUpdateDialogs(
        context: Context,
        listener: MoreAppsUpdateDialogListener,
    ) {
        showUpdateDialogs(context, 0, listener)
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param listener        [MoreAppsUpdateDialogListener] to know when dialog is closed
     */
    private fun showRedirectDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails,
        listener: MoreAppsUpdateDialogListener,
    ) {
        showRedirectDialog(context, moreAppsDetails, 0, listener)
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     */

    fun showHardUpdateDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        showHardUpdateDialog(context, moreAppsDetails, 0, listener)
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param listener        [MoreAppsUpdateDialogListener] to know when dialog is closed
     */

    fun showSoftUpdateDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        showSoftUpdateDialog(context, moreAppsDetails, 0, listener)
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     */

    fun showHardRedirectDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        showHardRedirectDialog(context, moreAppsDetails, 0, listener)
    }

    /**
     * @param context         [Context] of Activity or Fragment
     * @param moreAppsDetails [MoreAppsDetails]
     * @param listener        [MoreAppsUpdateDialogListener] to know when dialog is closed
     */

    fun showSoftRedirectDialog(
        context: Context,
        moreAppsDetails: MoreAppsDetails?,
        listener: MoreAppsUpdateDialogListener?,
    ) {
        showSoftRedirectDialog(context, moreAppsDetails, 0, listener)
    }

    /**
     * check whether to show the dialogs or not
     *
     *
     * This method will check [android.content.SharedPreferences] for the already stored data
     *
     *
     * NOTE : call [MoreAppsBuilder.build] first to load the data in [android.content.SharedPreferences]
     *
     * @param context [Context] of Activity or Fragment
     */

    fun shouldShowUpdateDialogs(context: Context): Boolean {
        try {
            val versionCode = getVersionCode(context)
            val moreAppsDetails = MoreAppsUtils.getCurrentAppModel(context)
            if (moreAppsDetails != null) {
                if (moreAppsDetails.redirectDetails.enable) {
                    //redirection enabled
                    return true
                } else if (moreAppsDetails.hardUpdateDetails.enable && moreAppsDetails.minVersion > versionCode) {
                    //hard update enabled
                    return true
                } else if (moreAppsDetails.softUpdateDetails.enable) {
                    //soft update enabled
                    return moreAppsDetails.currentVersion > versionCode || moreAppsDetails.minVersion > versionCode
                }
            }
        } catch (e: NameNotFoundException) {
            AppLog.loge(false, kotlinFileName, "shouldShowUpdateDialogs", e, Exception())
        }
        return false
    }

    enum class UpdateDialogType {
        NONE, SOFT_UPDATE, HARD_UPDATE, SOFT_REDIRECT, HARD_REDIRECT
    }
}