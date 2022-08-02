package io.github.raghavsatyadev.moreapps

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data.Builder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy.KEEP
import androidx.work.NetworkType.CONNECTED
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.HARD_UPDATE
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_REDIRECT
import io.github.raghavsatyadev.moreapps.ForceUpdater.UpdateDialogType.SOFT_UPDATE
import io.github.raghavsatyadev.moreapps.listener.MoreAppsDownloadListener
import io.github.raghavsatyadev.moreapps.settings.PeriodicUpdateSettings
import io.github.raghavsatyadev.moreapps.utils.AppLog
import io.github.raghavsatyadev.moreapps.utils.MoreAppsNotifyUtil
import io.github.raghavsatyadev.moreapps.utils.MoreAppsPrefUtil
import io.github.raghavsatyadev.moreapps.utils.MoreAppsUtils
import io.github.raghavsatyadev.moreapps.utils.getColorPrimaryInHex
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random
import javax.net.ssl.HttpsURLConnection

class MoreAppsWorker(private val context: Context, workerParams: WorkerParameters) : Worker(
    context, workerParams
) {
    @Throws(IOException::class)
    private fun setConnectionProperties(con: HttpURLConnection): String? {
        con.requestMethod = "GET"
        con.setRequestProperty("User-Agent", USER_AGENT)
        val responseCode = con.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            val reader = BufferedReader(InputStreamReader(con.inputStream))
            return getMoreAppsResponse(reader)
        } else {
            AppLog.loge(
                true,
                kotlinFileName,
                "setConnectionProperties",
                "GET request did not work",
                Exception()
            )
        }
        return null
    }

    @Throws(IOException::class)
    private fun setConnectionProperties(con: HttpsURLConnection): String? {
        con.requestMethod = "GET"
        con.setRequestProperty("User-Agent", USER_AGENT)
        val responseCode = con.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) { // success
            val reader = BufferedReader(InputStreamReader(con.inputStream))
            return getMoreAppsResponse(reader)
        } else {
            AppLog.loge(
                true,
                kotlinFileName,
                "setConnectionProperties",
                "GET request di not work",
                Exception()
            )
        }
        return null
    }

    @Throws(IOException::class)
    private fun getMoreAppsResponse(reader: BufferedReader): String {
        var inputLine: String?
        val response = StringBuilder()
        while (reader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        reader.close()
        return response.toString()
    }

    override fun doWork(): Result {
        val moreAppsJSON = callMoreAppsAPI()
        if (!TextUtils.isEmpty(moreAppsJSON)) {
            MoreAppsPrefUtil.setMoreApps(applicationContext, moreAppsJSON)
            if (inputData.getBoolean(IS_PERIODIC, false)) {
                if (!MoreAppsPrefUtil.isFirstTimePeriodic(context)) {
                    try {
                        handleNotification(applicationContext)
                    } catch (e: NameNotFoundException) {
                        AppLog.loge(false, kotlinFileName, "doWork", e, Exception())
                    }
                } else {
                    MoreAppsPrefUtil.setFirstTimePeriodic(context, false)
                }
            }
            return Result.success()
        }
        return Result.failure()
    }

    @Throws(NameNotFoundException::class)
    private fun handleNotification(context: Context) {
        val bigIconID = inputData.getInt(BIG_ICON, 0)
        if (bigIconID != 0) {
            val smallIconData = inputData.getInt(SMALL_ICON, 0)
            val smallIconID = if (smallIconData == 0) bigIconID else smallIconData
            val notificationColor = if (inputData.getInt(
                    THEME_COLOR,
                    0
                ) == 0
            ) Color.parseColor(context.getColorPrimaryInHex()) else inputData.getInt(
                THEME_COLOR, 0
            )
            prepareNotification(context, bigIconID, smallIconID, notificationColor)
        }
    }

    private fun prepareNotification(
        context: Context,
        @DrawableRes
        bigIconID: Int,
        @DrawableRes
        smallIconID: Int,
        @ColorInt
        notificationColor: Int,
    ) {
        val currentAppModel =
            MoreAppsUtils.getCurrentAppModel(context, MoreAppsPrefUtil.getMoreApps(context))
        when (ForceUpdater.dialogToShow(context, currentAppModel)) {
            HARD_UPDATE -> sendNotification(
                context, currentAppModel?.hardUpdateDetails?.dialogTitle ?: "",
                currentAppModel?.hardUpdateDetails?.dialogMessage ?: "",
                currentAppModel?.appLink ?: "",
                bigIconID,
                smallIconID,
                notificationColor
            )
            SOFT_UPDATE -> if (MoreAppsPrefUtil.shouldShowSoftUpdateNotification(
                    context,
                    currentAppModel?.softUpdateDetails?.notificationShowCount ?: 0,
                    currentAppModel?.currentVersion ?: 0
                )
            ) {
                sendNotification(
                    context, currentAppModel?.softUpdateDetails?.dialogTitle ?: "",
                    currentAppModel?.softUpdateDetails?.dialogMessage ?: "",
                    currentAppModel?.appLink ?: "",
                    bigIconID,
                    smallIconID,
                    notificationColor
                )
                MoreAppsPrefUtil.increaseSoftUpdateNotificationShownTimes(context)
            }
            HARD_REDIRECT, SOFT_REDIRECT -> sendNotification(
                context, currentAppModel?.redirectDetails?.dialogTitle ?: "",
                currentAppModel?.redirectDetails?.dialogMessage ?: "",
                currentAppModel?.redirectDetails?.appLink ?: "",
                bigIconID,
                smallIconID,
                notificationColor
            )
            else -> {}
        }
    }

    private fun sendNotification(
        context: Context, notificationName: String,
        notificationDescription: String,
        appLink: String,
        @DrawableRes
        bigIconID: Int,
        @DrawableRes
        smallIconID: Int,
        @ColorInt
        notificationColor: Int,
    ) {
        MoreAppsNotifyUtil.sendNotification(
            context,
            Random().nextInt(),
            notificationName,
            notificationDescription,
            null,
            Intent(Intent.ACTION_VIEW, Uri.parse(appLink)),
            PendingIntent.FLAG_ONE_SHOT,
            bigIconID,
            smallIconID,
            notificationColor
        )
    }

    private fun callMoreAppsAPI(): String? {
        var response: String? = null
        try {
            val inputData = inputData
            val urlString = inputData.getString(URL)
            val url = URL(urlString)
            response = if (url.host.equals("https", ignoreCase = true)) {
                val con = url.openConnection() as HttpsURLConnection
                setConnectionProperties(con)
            } else {
                val con = url.openConnection() as HttpURLConnection
                setConnectionProperties(con)
            }
        } catch (e: IOException) {
            AppLog.loge(false, kotlinFileName, "callMoreAppsAPI", e, Exception())
        }
        return response
    }

    companion object {
        private const val WORKER_TAG_PERIODIC = "MORE_APPS_PERIODIC"
        private const val WORKER_TAG_ONE_TIME = "MORE_APPS_ONE_TIME"
        private val TAG = kotlinFileName
        private const val URL = "URL"
        private const val BIG_ICON = "BIG_ICON"
        private const val SMALL_ICON = "SMALL_ICON"
        private const val THEME_COLOR = "THEME_COLOR"
        private const val USER_AGENT = "Mozilla/5.0"
        private const val IS_PERIODIC = "IS_PERIODIC"


        fun startWorker(
            context: Context,
            url: String?,
            listener: MoreAppsDownloadListener?,
            moreAppsDialog: MoreAppsDialog,
            themeColor: Int,
            updateSettings: PeriodicUpdateSettings,
        ) {
            if (!isWorkScheduled(context)) {
                val constraints: Constraints =
                    Constraints.Builder().setRequiredNetworkType(CONNECTED).build()
                val dataBuilder = Builder()
                    .putInt(BIG_ICON, updateSettings.bigIconID)
                    .putInt(SMALL_ICON, updateSettings.smallIconID)
                    .putInt(THEME_COLOR, themeColor)
                    .putString(URL, url)
                val instance = WorkManager.getInstance(context)
                setupOneTimeRequest(
                    context,
                    constraints,
                    dataBuilder,
                    moreAppsDialog,
                    listener,
                    instance,
                    updateSettings
                )
            }
        }

        private fun setupOneTimeRequest(
            context: Context, constraints: Constraints,
            dataBuilder: Builder,
            moreAppsDialog: MoreAppsDialog,
            listener: MoreAppsDownloadListener?,
            instance: WorkManager,
            updateSettings: PeriodicUpdateSettings,
        ) {
            if (MoreAppsPrefUtil.getMoreApps(context).isEmpty()) {
                val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                    MoreAppsWorker::class.java
                )
                    .setConstraints(constraints)
                    .setInputData(dataBuilder.putBoolean(IS_PERIODIC, false).build())
                    .build()
                instance.enqueueUniqueWork(WORKER_TAG_ONE_TIME, KEEP, oneTimeWorkRequest)
                if (listener != null) {
                    val workInfo = instance.getWorkInfosForUniqueWorkLiveData(WORKER_TAG_ONE_TIME)
                    val observer: Observer<List<WorkInfo>> = object : Observer<List<WorkInfo>> {
                        override fun onChanged(workInfos: List<WorkInfo>?) {
                            handleResult(
                                context,
                                moreAppsDialog,
                                workInfos,
                                workInfo,
                                this,
                                listener,
                                constraints,
                                dataBuilder,
                                updateSettings,
                                instance
                            )
                        }
                    }
                    workInfo.observeForever(observer)
                }
            }
        }

        private fun setupPeriodicRequest(
            constraints: Constraints, dataBuilder: Builder,
            updateSettings: PeriodicUpdateSettings,
            instance: WorkManager,
        ) {
            val periodicWorkRequest = PeriodicWorkRequest.Builder(
                MoreAppsWorker::class.java,
                updateSettings.interval.toLong(),
                updateSettings.timeUnit
            )
                .setConstraints(constraints)
                .setInputData(dataBuilder.putBoolean(IS_PERIODIC, true).build())
                .build()
            instance.enqueueUniquePeriodicWork(
                WORKER_TAG_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }

        private fun handleResult(
            context: Context, moreAppsDialog: MoreAppsDialog,
            workInfos: List<WorkInfo>?,
            workInfoLiveData: LiveData<List<WorkInfo>>,
            observer: Observer<List<WorkInfo>>,
            listener: MoreAppsDownloadListener, constraints: Constraints,
            dataBuilder: Builder,
            updateSettings: PeriodicUpdateSettings, instance: WorkManager,
        ) {
            if (workInfos != null && workInfos.isNotEmpty()) {
                val workInfo = workInfos[0]
                val state = workInfo.state
                if (state == SUCCEEDED) {
                    listener.onSuccess(moreAppsDialog, MoreAppsPrefUtil.getMoreApps(context))
                    workInfoLiveData.removeObserver(observer)
                    setupPeriodicRequest(constraints, dataBuilder, updateSettings, instance)
                } else if (state == FAILED) {
                    listener.onFailure()
                    workInfoLiveData.removeObserver(observer)
                    setupPeriodicRequest(constraints, dataBuilder, updateSettings, instance)
                }
            }
        }

        private fun isWorkScheduled(context: Context): Boolean {
            val workerState = getWorkerState(context)
            return workerState == RUNNING || workerState == ENQUEUED
        }

        private fun getWorkerState(context: Context): State? {
            val instance = WorkManager.getInstance(context)
            val infos = instance.getWorkInfosForUniqueWorkLiveData(WORKER_TAG_PERIODIC)
            val value = infos.value
            if (value != null && value.isNotEmpty()) {
                val state = value[0].state
                if (state == ENQUEUED || state == RUNNING) {
                    return state
                }
            }
            return null
        }
    }
}