package com.rocky.moreapps.model;

import java.util.concurrent.TimeUnit;

public class PeriodicUpdateSettings {
    private int interval;
    private TimeUnit timeUnit;

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

    public void setSettings(int interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.timeUnit = timeUnit;
    }
}
