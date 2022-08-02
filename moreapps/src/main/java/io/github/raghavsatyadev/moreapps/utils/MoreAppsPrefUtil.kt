@file:Suppress("MemberVisibilityCanBePrivate")

package io.github.raghavsatyadev.moreapps.utils

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.github.raghavsatyadev.moreapps.kotlinFileName
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails

object MoreAppsPrefUtil {
    private val TAG = MoreAppsPrefUtil::class.java.simpleName

    fun getMoreApps(context: Context): ArrayList<MoreAppsDetails> {
        return convertStringToModel(getMoreAppsString(context))
    }

    fun getMoreAppsString(context: Context?): String {
        return MoreAppsPrefHelper.getInstance(context!!)!![AppPrefStrings.MORE_APPS, ""]
    }

    fun convertStringToModel(json: String?): ArrayList<MoreAppsDetails> {
        val gson = Gson()
        val moreAppsDetails = ArrayList<MoreAppsDetails>()
        try {
            val appModelsType = object : TypeToken<ArrayList<MoreAppsDetails?>?>() {}.type
            val moreAppsModelsTemp = gson.fromJson<List<MoreAppsDetails>>(json, appModelsType)
            if (moreAppsModelsTemp != null) {
                moreAppsDetails.addAll(moreAppsModelsTemp)
            }
        } catch (e: JsonSyntaxException) {
            AppLog.loge(false, kotlinFileName, "convertStringToModel", e, Exception())
        }
        return moreAppsDetails
    }

    fun setMoreApps(context: Context, moreAppsDetails: List<MoreAppsDetails?>?) {
        val gson = Gson()
        if (moreAppsDetails != null) {
            val appModelsJson = gson.toJson(moreAppsDetails)
            MoreAppsPrefHelper.getInstance(context)!!
                .save(AppPrefStrings.MORE_APPS, appModelsJson)
        }
    }

    fun setMoreApps(context: Context, moreAppsJSON: String?) {
        if (!TextUtils.isEmpty(moreAppsJSON)) {
            MoreAppsPrefHelper.getInstance(context)!!
                .save(AppPrefStrings.MORE_APPS, moreAppsJSON)
        }
    }

    fun isFirstTimePeriodic(context: Context): Boolean {
        return MoreAppsPrefHelper.getInstance(context)!![AppPrefStrings.IS_FIRST_TIME_PERIODIC, true]
    }

    fun setFirstTimePeriodic(context: Context, status: Boolean) {
        MoreAppsPrefHelper.getInstance(context)!!
            .save(AppPrefStrings.IS_FIRST_TIME_PERIODIC, status)
    }

    fun saveCurrentVersion(context: Context?, currentVersion: Int) {
        saveSoftUpdateShownTimes(context, 0)
        saveSoftUpdateNotificationShownTimes(context, 0)
        MoreAppsPrefHelper.getInstance(context!!)!!
            .save(AppPrefStrings.CURRENT_VERSION, currentVersion)
    }

    private fun getCurrentVersion(context: Context): Int {
        return MoreAppsPrefHelper.getInstance(context)!![AppPrefStrings.CURRENT_VERSION, 0]
    }

    fun shouldShowSoftUpdate(context: Context, dialogShowCount: Int, currentVersion: Int): Boolean {
        val dialogShownTimes = getSoftUpdateShownTimes(context)
        val savedCurrentVersion = getCurrentVersion(context)
        if (currentVersion > savedCurrentVersion) {
            saveCurrentVersion(context, currentVersion)
            return true
        }
        return dialogShowCount == 0 || dialogShowCount > dialogShownTimes
    }

    private fun getSoftUpdateShownTimes(context: Context): Int {
        return MoreAppsPrefHelper.getInstance(context)!![AppPrefStrings.DIALOG_SHOW_COUNT, 0]
    }

    fun saveSoftUpdateShownTimes(context: Context?, softUpdateShownTimes: Int) {
        MoreAppsPrefHelper.getInstance(context!!)!!
            .save(AppPrefStrings.DIALOG_SHOW_COUNT, softUpdateShownTimes)
    }

    fun increaseSoftUpdateShownTimes(context: Context) {
        var softUpdateShownTimes = getSoftUpdateShownTimes(context)
        softUpdateShownTimes++
        saveSoftUpdateShownTimes(context, softUpdateShownTimes)
    }

    fun shouldShowSoftUpdateNotification(
        context: Context,
        notificationShowCount: Int,
        currentVersion: Int,
    ): Boolean {
        val notificationShownTimes = getSoftUpdateNotificationShownTimes(context)
        val savedCurrentVersion = getCurrentVersion(context)
        if (currentVersion > savedCurrentVersion) {
            saveCurrentVersion(context, currentVersion)
            return true
        }
        return notificationShowCount == 0 || notificationShowCount > notificationShownTimes
    }

    private fun getSoftUpdateNotificationShownTimes(context: Context): Int {
        return MoreAppsPrefHelper.getInstance(context)!![AppPrefStrings.NOTIFICATION_SHOW_COUNT, 0]
    }

    fun saveSoftUpdateNotificationShownTimes(
        context: Context?,
        softUpdateNotificationShownTimes: Int,
    ) {
        MoreAppsPrefHelper.getInstance(context!!)!!
            .save(AppPrefStrings.NOTIFICATION_SHOW_COUNT, softUpdateNotificationShownTimes)
    }

    fun increaseSoftUpdateNotificationShownTimes(context: Context) {
        var softUpdateNotificationShownTimes = getSoftUpdateNotificationShownTimes(context)
        softUpdateNotificationShownTimes++
        saveSoftUpdateNotificationShownTimes(context, softUpdateNotificationShownTimes)
    }

    internal interface AppPrefStrings {
        companion object {
            const val MORE_APPS = "MORE_APPS"
            const val IS_FIRST_TIME_PERIODIC = "IS_FIRST_TIME_PERIODIC"
            const val DIALOG_SHOW_COUNT = "DIALOG_SHOW_COUNT"
            const val NOTIFICATION_SHOW_COUNT = "NOTIFICATION_SHOW_COUNT"
            const val CURRENT_VERSION = "CURRENT_VERSION"
        }
    }
}