package io.github.raghavsatyadev.moreapps.settings

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import io.github.raghavsatyadev.moreapps.R.layout
import io.github.raghavsatyadev.moreapps.utils.getColorOnPrimaryColorInHex
import io.github.raghavsatyadev.moreapps.utils.getColorPrimaryInHex

class MoreAppsDesignSettings {
    private var shouldOpenInPlayStore = true

    @LayoutRes
    var dialogLayout = layout.more_apps_view

    @LayoutRes
    var dialogRowLayout = layout.row_more_apps
    var dialogTitle = ""
    val ignoredPackageNames = HashSet<String>()

    @ColorInt
    var primaryColor = 0
        private set

    @ColorInt
    var accentColor = 0
        private set

    @FontRes
    var font = 0

    @ColorInt
    var rowTitleColor = 0

    @ColorInt
    var rowDescriptionColor = 0
    fun shouldOpenInPlayStore(): Boolean {
        return shouldOpenInPlayStore
    }

    fun setShouldOpenInPlayStore(shouldOpenInPlayStore: Boolean) {
        this.shouldOpenInPlayStore = shouldOpenInPlayStore
    }

    fun setIgnoredPackageNames(ignoredPackageNames: List<String>?) {
        this.ignoredPackageNames.addAll(ignoredPackageNames!!)
    }

    fun setIgnoredPackageNames(ignoredPackageName: String) {
        ignoredPackageNames.add(ignoredPackageName)
    }

    fun setTheme(
        context: Context?,
        @ColorInt
        primaryColor: Int,
        @ColorInt
        accentColor: Int,
    ) {
        if (primaryColor == 0) {
            this.primaryColor = Color.parseColor(
                context!!.getColorPrimaryInHex()
            )
            this.accentColor = Color.parseColor(
                context.getColorOnPrimaryColorInHex()
            )
        } else {
            this.primaryColor = primaryColor
            this.accentColor = accentColor
        }
    }
}