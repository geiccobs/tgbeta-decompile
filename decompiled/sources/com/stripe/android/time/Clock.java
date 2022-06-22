package com.stripe.android.time;

import java.util.Calendar;
/* loaded from: classes.dex */
public class Clock {
    private static Clock instance;
    protected Calendar calendarInstance;

    protected static Clock getInstance() {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    private Calendar _calendarInstance() {
        Calendar calendar = this.calendarInstance;
        return calendar != null ? (Calendar) calendar.clone() : Calendar.getInstance();
    }

    public static Calendar getCalendarInstance() {
        return getInstance()._calendarInstance();
    }
}
