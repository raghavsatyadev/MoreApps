package com.rocky.moreapps.model;

import android.support.annotation.ColorInt;
import android.support.annotation.FontRes;
import android.support.annotation.LayoutRes;

import com.rocky.moreapps.R;

import java.util.HashSet;
import java.util.List;

public class MoreAppsDesignSettings {
    private boolean shouldOpenInPlayStore = true;
    @LayoutRes
    private int dialogLayout = R.layout.more_apps_view;
    @LayoutRes
    private int dialogRowLayout = R.layout.row_more_apps;
    private String dialogTitle = "";
    private HashSet<String> ignoredPackageNames = new HashSet<>();
    @ColorInt
    private int themeColor = 0;
    @FontRes
    private int font;
    @ColorInt
    private int rowTitleColor;
    @ColorInt
    private int rowDescriptionColor;

    public boolean shouldOpenInPlayStore() {
        return shouldOpenInPlayStore;
    }

    public void setShouldOpenInPlayStore(boolean shouldOpenInPlayStore) {
        this.shouldOpenInPlayStore = shouldOpenInPlayStore;
    }

    public int getDialogLayout() {
        return dialogLayout;
    }

    public void setDialogLayout(int dialogLayout) {
        this.dialogLayout = dialogLayout;
    }

    public int getDialogRowLayout() {
        return dialogRowLayout;
    }

    public void setDialogRowLayout(int dialogRowLayout) {
        this.dialogRowLayout = dialogRowLayout;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public HashSet<String> getIgnoredPackageNames() {
        return ignoredPackageNames;
    }

    public void setIgnoredPackageNames(List<String> ignoredPackageNames) {
        this.ignoredPackageNames.addAll(ignoredPackageNames);
    }

    public void setIgnoredPackageNames(String ignoredPackageName) {
        this.ignoredPackageNames.add(ignoredPackageName);
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public int getFont() {
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }

    public int getRowTitleColor() {
        return rowTitleColor;
    }

    public void setRowTitleColor(int rowTitleColor) {
        this.rowTitleColor = rowTitleColor;
    }

    public int getRowDescriptionColor() {
        return rowDescriptionColor;
    }

    public void setRowDescriptionColor(int rowDescriptionColor) {
        this.rowDescriptionColor = rowDescriptionColor;
    }
}
