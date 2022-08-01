package com.rocky.moreapps.example

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.Configuration.Builder
import androidx.work.Configuration.Provider
import com.rocky.moreapps.example.R.drawable
import com.rocky.moreapps.example.R.mipmap
import io.github.raghavsatyadev.moreapps.MoreAppsBuilder
import io.github.raghavsatyadev.moreapps.MoreAppsDialog
import java.util.concurrent.TimeUnit.MINUTES

class CoreApp : Application(), Provider {
    private var moreAppsDialog: MoreAppsDialog? = null
    override fun onCreate() {
        super.onCreate()
        instance = this

//        this pattern is part of option-2
        createMoreAppDialog()
    }

    fun getMoreAppsDialog(): MoreAppsDialog? {
        if (moreAppsDialog == null) createMoreAppDialog()
        return moreAppsDialog
    }

    private fun createMoreAppDialog() {
        moreAppsDialog = MoreAppsBuilder(this, JSON_FILE_URL)
            .setPeriodicSettings(15, MINUTES, mipmap.ic_launcher, drawable.ic_small_icon)
            .build()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()
    }

    companion object {
        const val JSON_FILE_URL = "https://raghavsatyadev.github.io/more_apps_example.json"

        @Volatile
        lateinit var instance: CoreApp
            private set
    }
}