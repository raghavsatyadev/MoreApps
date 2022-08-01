@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.raghavsatyadev.moreapps

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDialogListener
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDownloadListener
import io.github.raghavsatyadev.moreapps.settings.MoreAppsDesignSettings
import io.github.raghavsatyadev.moreapps.settings.PeriodicUpdateSettings
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.DAYS

class MoreAppsBuilder(private val context: Context, private val url: String) {
    private val designSettings = MoreAppsDesignSettings()
    private val updateSettings = PeriodicUpdateSettings()

    /**
     * @param shouldOpenInPlayStore should open apps in Play Store
     * @return [MoreAppsBuilder]
     */
    fun openAppsInPlayStore(shouldOpenInPlayStore: Boolean): MoreAppsBuilder {
        designSettings.setShouldOpenInPlayStore(shouldOpenInPlayStore)
        return this
    }

    /**
     * to remove application from the list
     *
     * @param ignoredPackageName Package name of application
     * @return [MoreAppsBuilder]
     */
    fun removeApplicationFromList(ignoredPackageName: String): MoreAppsBuilder {
        designSettings.setIgnoredPackageNames(ignoredPackageName)
        return this
    }

    /**
     * to remove application from the list
     *
     * @param ignoredPackageNames Package name of application
     * @return [MoreAppsBuilder]
     */
    fun removeApplicationFromList(ignoredPackageNames: List<String>): MoreAppsBuilder {
        designSettings.setIgnoredPackageNames(ignoredPackageNames)
        return this
    }

    /**
     * call this method to show custom dialog.
     *
     *
     * NOTE : keep the IDs of the views same as given below
     *
     *
     * Dialog Title (TextView) : txt_more_apps_title
     *
     *
     * RecyclerView : list_more_apps
     *
     *
     * Dialog Close Button (View): btn_more_apps_close
     *
     * @param dialogLayout layout file for dialog
     * @return [MoreAppsBuilder]
     */
    fun dialogLayout(
        @LayoutRes
        dialogLayout: Int,
    ): MoreAppsBuilder {
        designSettings.dialogLayout = dialogLayout
        return this
    }

    /**
     * call this method to show custom dialog.
     *
     *
     * NOTE : keep the IDs of the views same as given below
     *
     *
     * App Image (ImageView) : img_more_apps
     *
     *
     * App Name (TextView): txt_more_apps_name
     *
     *
     * App Rating (SimpleRatingBar): rating_more_apps
     *
     *
     * App Description (TextView): txt_more_apps_description
     *
     * @param dialogRowLayout layout file for Recycler View items
     * @return [MoreAppsBuilder]
     */
    fun dialogRowLayout(
        @LayoutRes
        dialogRowLayout: Int,
    ): MoreAppsBuilder {
        designSettings.dialogRowLayout = dialogRowLayout
        return this
    }

    /**
     * @param dialogTitle custom dialog title
     * @return [MoreAppsBuilder]
     */
    fun dialogTitle(
        @StringRes
        dialogTitle: Int,
    ): MoreAppsBuilder {
        designSettings.dialogTitle = context.getString(dialogTitle)
        return this
    }

    /**
     * @param dialogTitle custom dialog title
     * @return [MoreAppsBuilder]
     */
    fun dialogTitle(dialogTitle: String): MoreAppsBuilder {
        designSettings.dialogTitle = dialogTitle
        return this
    }

    /**
     * @param primaryColor changes dialog title, rating bar, close button color
     * @param accentColor  changes close button image color
     * @return [MoreAppsBuilder]
     */
    fun theme(
        @ColorInt
        primaryColor: Int,
        @ColorInt
        accentColor: Int,
    ): MoreAppsBuilder {
        designSettings.setTheme(context, primaryColor, accentColor)
        return this
    }

    /**
     * @param font font to apply on whole dialog
     * @return [MoreAppsBuilder]
     */
    fun font(
        @FontRes
        font: Int,
    ): MoreAppsBuilder {
        designSettings.font = font
        return this
    }

    /**
     * @param rowTitleColor color app title in list
     */
    fun rowTitleColor(
        @ColorInt
        rowTitleColor: Int,
    ): MoreAppsBuilder {
        designSettings.rowTitleColor = rowTitleColor
        return this
    }

    /**
     * @param rowDescriptionColor color app description in list
     */
    fun rowDescriptionColor(
        @ColorInt
        rowDescriptionColor: Int,
    ): MoreAppsBuilder {
        designSettings.rowDescriptionColor = rowDescriptionColor
        return this
    }

    /**
     * set interval for the API calling, after every interval app details will be updated.
     *
     * @param bigIconID app icon
     */
    fun setPeriodicSettings(
        @DrawableRes
        bigIconID: Int,
    ): MoreAppsBuilder {
        return setPeriodicSettings(bigIconID, 0)
    }

    /**
     * set interval for the API calling, after every interval app details will be updated.
     *
     * @param bigIconID   app icon
     * @param smallIconID small notification icon
     */
    fun setPeriodicSettings(
        @DrawableRes
        bigIconID: Int,
        @DrawableRes
        smallIconID: Int,
    ): MoreAppsBuilder {
        return setPeriodicSettings(7, DAYS, bigIconID, smallIconID)
    }

    /**
     * set interval for the API calling, after every interval app details will be updated.
     *
     * @param interval    interval &gt; 0
     * @param timeUnit    [TimeUnit]
     * @param bigIconID   app icon
     * @param smallIconID small notification icon
     */
    fun setPeriodicSettings(
        interval: Int,
        timeUnit: TimeUnit?,
        @DrawableRes
        bigIconID: Int,
        @DrawableRes
        smallIconID: Int,
    ): MoreAppsBuilder {
        if (interval > 0 && timeUnit != null) updateSettings.setSettings(
            interval,
            timeUnit,
            bigIconID,
            smallIconID
        )
        return this
    }

    /**
     * for immediate calling of API and showing the dialog
     *
     * @param listener [MoreAppsDialogListener]
     */
    fun buildAndShow(listener: MoreAppsDialogListener?) {
        build(true, null, listener)
    }

    private fun build(
        shouldShow: Boolean,
        listener: MoreAppsDownloadListener?,
        dialogListener: MoreAppsDialogListener?,
    ): MoreAppsDialog {
        val moreAppsDialog = MoreAppsDialog(
            url,
            designSettings,
            updateSettings
        )
        if (shouldShow) moreAppsDialog.show(
            context,
            dialogListener
        ) else moreAppsDialog.startWorker(
            context, listener
        )
        return moreAppsDialog
    }

    @JvmOverloads
    fun build(listener: MoreAppsDownloadListener? = null): MoreAppsDialog {
        return build(false, listener, null)
    }
}