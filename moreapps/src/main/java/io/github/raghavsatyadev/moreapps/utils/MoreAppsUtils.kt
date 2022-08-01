package io.github.raghavsatyadev.moreapps.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails
import io.github.raghavsatyadev.moreapps.utils.MoreAppsPrefUtil.getMoreApps

object MoreAppsUtils {
    fun openBrowser(context: Context, link: String?) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    /**
     * NOTE : call [MoreAppsBuilder.build] first to load the data in [android.content.SharedPreferences]
     *
     * @return [MoreAppsDetails] of current app if present
     */
    fun getCurrentAppModel(context: Context): MoreAppsDetails? {
        val moreApps = getMoreApps(context)
        return getCurrentAppModel(context, moreApps)
    }

    fun getCurrentAppModel(
        context: Context,
        moreApps: ArrayList<MoreAppsDetails>,
    ): MoreAppsDetails? {
        if (moreApps.isNotEmpty()) {
            val currentPackageName = context.packageName
            for (i in moreApps.indices) {
                val moreAppsDetails = moreApps[i]
                if (moreAppsDetails.packageName == currentPackageName) {
                    return moreAppsDetails
                }
            }
        }
        return null
    }
}