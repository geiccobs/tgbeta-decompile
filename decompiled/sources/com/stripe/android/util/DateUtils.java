package com.stripe.android.util;

import com.stripe.android.time.Clock;
import java.util.Calendar;
import java.util.Locale;
/* loaded from: classes.dex */
public class DateUtils {
    public static boolean hasYearPassed(int i) {
        return normalizeYear(i) < Clock.getCalendarInstance().get(1);
    }

    public static boolean hasMonthPassed(int i, int i2) {
        if (hasYearPassed(i)) {
            return true;
        }
        Calendar calendarInstance = Clock.getCalendarInstance();
        return normalizeYear(i) == calendarInstance.get(1) && i2 < calendarInstance.get(2) + 1;
    }

    private static int normalizeYear(int i) {
        if (i >= 100 || i < 0) {
            return i;
        }
        String valueOf = String.valueOf(Clock.getCalendarInstance().get(1));
        return Integer.parseInt(String.format(Locale.US, "%s%02d", valueOf.substring(0, valueOf.length() - 2), Integer.valueOf(i)));
    }
}
