package com.rocky.moreapps;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.rocky.moreapps.listener.MoreAppsDialogListener;
import com.rocky.moreapps.listener.MoreAppsDownloadListener;
import com.rocky.moreapps.settings.MoreAppsDesignSettings;
import com.rocky.moreapps.settings.PeriodicUpdateSettings;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class MoreAppsBuilder {
    private MoreAppsDesignSettings designSettings;
    private Context context;
    private String url;
    private PeriodicUpdateSettings updateSettings = new PeriodicUpdateSettings();

    /**
     * @param context context
     * @param url     URL of JSON file
     */
    public MoreAppsBuilder(@NonNull Context context, @NonNull String url) {
        this.context = context;
        this.url = url;
        designSettings = new MoreAppsDesignSettings();
    }

    /**
     * @param shouldOpenInPlayStore should open apps in Play Store
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder openAppsInPlayStore(boolean shouldOpenInPlayStore) {
        designSettings.setShouldOpenInPlayStore(shouldOpenInPlayStore);
        return this;
    }

    /**
     * to remove application from the list
     *
     * @param ignoredPackageName Package name of application
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder removeApplicationFromList(String ignoredPackageName) {
        designSettings.setIgnoredPackageNames(ignoredPackageName);
        return this;
    }

    /**
     * to remove application from the list
     *
     * @param ignoredPackageNames Package name of application
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder removeApplicationFromList(List<String> ignoredPackageNames) {
        if (ignoredPackageNames != null)
            designSettings.setIgnoredPackageNames(ignoredPackageNames);
        return this;
    }

    /**
     * call this method to show custom dialog.
     * <p>
     * NOTE : keep the IDs of the views same as given below
     * <p>
     * Dialog Title (TextView) : txt_more_apps_title
     * <p>
     * RecyclerView : list_more_apps
     * <p>
     * Dialog Close Button (View): btn_more_apps_close
     *
     * @param dialogLayout layout file for dialog
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder dialogLayout(@LayoutRes int dialogLayout) {
        designSettings.setDialogLayout(dialogLayout);
        return this;
    }

    /**
     * call this method to show custom dialog.
     * <p>
     * NOTE : keep the IDs of the views same as given below
     * <p>
     * App Image (ImageView) : img_more_apps
     * <p>
     * App Name (TextView): txt_more_apps_name
     * <p>
     * App Rating (SimpleRatingBar): rating_more_apps
     * <p>
     * App Description (TextView): txt_more_apps_description
     *
     * @param dialogRowLayout layout file for Recycler View items
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder dialogRowLayout(@LayoutRes int dialogRowLayout) {
        designSettings.setDialogRowLayout(dialogRowLayout);
        return this;
    }

    /**
     * @param dialogTitle custom dialog title
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder dialogTitle(@StringRes int dialogTitle) {
        designSettings.setDialogTitle(context.getString(dialogTitle));
        return this;
    }

    /**
     * @param dialogTitle custom dialog title
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder dialogTitle(String dialogTitle) {
        designSettings.setDialogTitle(dialogTitle);
        return this;
    }

    /**
     * @param primaryColor changes dialog title, rating bar, close button color
     * @param accentColor  changes close button image color
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder theme(@ColorInt int primaryColor, @ColorInt int accentColor) {
        designSettings.setTheme(context, primaryColor, accentColor);
        return this;
    }

    /**
     * @param font font to apply on whole dialog
     * @return {@link MoreAppsBuilder}
     */
    public MoreAppsBuilder font(@FontRes int font) {
        designSettings.setFont(font);
        return this;
    }

    /**
     * @param rowTitleColor color app title in list
     */
    public MoreAppsBuilder rowTitleColor(@ColorInt int rowTitleColor) {
        designSettings.setRowTitleColor(rowTitleColor);
        return this;
    }

    /**
     * @param rowDescriptionColor color app description in list
     */
    public MoreAppsBuilder rowDescriptionColor(@ColorInt int rowDescriptionColor) {
        designSettings.setRowDescriptionColor(rowDescriptionColor);
        return this;
    }

    /**
     * set interval for the API calling, after every interval app details will be updated.
     *
     * @param bigIconID app icon
     */
    public MoreAppsBuilder setPeriodicSettings(@DrawableRes int bigIconID) {
        return setPeriodicSettings(bigIconID, 0);
    }

    /**
     * set interval for the API calling, after every interval app details will be updated.
     *
     * @param bigIconID   app icon
     * @param smallIconID small notification icon
     */
    public MoreAppsBuilder setPeriodicSettings(@DrawableRes int bigIconID, @DrawableRes int smallIconID) {
        return setPeriodicSettings(7, TimeUnit.DAYS, bigIconID, smallIconID);
    }

    /**
     * set interval for the API calling, after every interval app details will be updated.
     *
     * @param interval    interval &gt; 0
     * @param timeUnit    {@link TimeUnit}
     * @param bigIconID   app icon
     * @param smallIconID small notification icon
     */
    public MoreAppsBuilder setPeriodicSettings(int interval, TimeUnit timeUnit, @DrawableRes int bigIconID, @DrawableRes int smallIconID) {
        if (interval > 0 && timeUnit != null)
            updateSettings.setSettings(interval, timeUnit, bigIconID, smallIconID);

        return this;
    }

    /**
     * for immediate calling of API and showing the dialog
     *
     * @param listener {@link MoreAppsDialogListener}
     */
    public void buildAndShow(MoreAppsDialogListener listener) {
        build(true, null, listener);
    }

    private MoreAppsDialog build(boolean shouldShow,
                                 MoreAppsDownloadListener listener,
                                 MoreAppsDialogListener dialogListener) {

        MoreAppsDialog moreAppsDialog = new MoreAppsDialog(
                url,
                designSettings,
                updateSettings
        );

        if (shouldShow) moreAppsDialog.show(context, dialogListener);
        else moreAppsDialog.startWorker(context, listener);

        return moreAppsDialog;
    }

    public MoreAppsDialog build() {
        return build(null);
    }

    public MoreAppsDialog build(MoreAppsDownloadListener listener) {
        return build(false, listener, null);
    }
}