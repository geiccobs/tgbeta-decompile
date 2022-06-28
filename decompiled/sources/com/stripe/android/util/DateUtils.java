package com.stripe.android.util;

import com.stripe.android.time.Clock;
import java.util.Calendar;
import java.util.Locale;
/* loaded from: classes3.dex */
public class DateUtils {
    public static boolean hasYearPassed(int year) {
        int normalized = normalizeYear(year);
        Calendar now = Clock.getCalendarInstance();
        return normalized < now.get(1);
    }

    public static boolean hasMonthPassed(int year, int month) {
        if (hasYearPassed(year)) {
            return true;
        }
        Calendar now = Clock.getCalendarInstance();
        return normalizeYear(year) == now.get(1) && month < now.get(2) + 1;
    }

    private static int normalizeYear(int year) {
        if (year < 100 && year >= 0) {
            Calendar now = Clock.getCalendarInstance();
            String currentYear = String.valueOf(now.get(1));
            String prefix = currentYear.substring(0, currentYear.length() - 2);
            return Integer.parseInt(String.format(Locale.US, "%s%02d", prefix, Integer.valueOf(year)));
        }
        return year;
    }
}
