package io.github.raghavsatyadev.moreapps.settings

import androidx.annotation.DrawableRes
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.DAYS

class PeriodicUpdateSettings {
    var interval = 7
        private set
    var timeUnit: TimeUnit
        private set

    @DrawableRes
    var bigIconID = 0
        private set

    @DrawableRes
    var smallIconID = 0
        private set

    init {
        timeUnit = DAYS
    }

    fun setSettings(interval: Int, timeUnit: TimeUnit, bigIconID: Int, smallIconID: Int) {
        this.interval = interval
        this.timeUnit = timeUnit
        this.bigIconID = bigIconID
        this.smallIconID = smallIconID
    }
}