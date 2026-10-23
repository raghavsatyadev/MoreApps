package com.rocky.moreapps.example

import android.app.Application
import io.github.raghavsatyadev.moreapps.MoreApps
import io.github.raghavsatyadev.moreapps.initialize
import io.github.raghavsatyadev.moreapps.schedulePeriodicCheck
import kotlin.time.Duration.Companion.minutes

class SampleApp : Application() {
  override fun onCreate() {
    super.onCreate()
    MoreApps.initialize(this, SampleConfig.JSON_URL)
    // 15 minutes is WorkManager's minimum; real apps would keep the 7-day default.
    MoreApps.schedulePeriodicCheck(this, R.drawable.ic_small_icon, interval = 15.minutes)
    startSampleKoin()
  }
}
