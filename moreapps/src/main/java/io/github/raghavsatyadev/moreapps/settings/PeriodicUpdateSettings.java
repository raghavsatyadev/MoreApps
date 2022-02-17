package io.github.raghavsatyadev.moreapps.settings;

import androidx.annotation.DrawableRes;

import java.util.concurrent.TimeUnit;

public class PeriodicUpdateSettings {
    private int interval;
    private TimeUnit timeUnit;
    @DrawableRes
    private int bigIconID;
    @DrawableRes
    private int smallIconID;

    public PeriodicUpdateSettings() {
        interval = 7;
        timeUnit = TimeUnit.DAYS;
    }

    public int getInterval() {
        return interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getBigIconID() {
        return bigIconID;
    }

    public int getSmallIconID() {
        return smallIconID;
    }

    public void setSettings(int interval, TimeUnit timeUnit, int bigIconID, int smallIconID) {
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.bigIconID = bigIconID;
        this.smallIconID = smallIconID;
    }
}
