package com.microsoft.appcenter.utils;

import android.util.Log;
/* loaded from: classes3.dex */
public class AppCenterLog {
    public static final String LOG_TAG = "AppCenter";
    public static final int NONE = 8;
    private static int sLogLevel = 7;

    public static int getLogLevel() {
        return sLogLevel;
    }

    public static void setLogLevel(int logLevel) {
        sLogLevel = logLevel;
    }

    public static void verbose(String tag, String message) {
        if (sLogLevel <= 2) {
            Log.v(tag, message);
        }
    }

    public static void verbose(String tag, String message, Throwable throwable) {
        if (sLogLevel <= 2) {
            Log.v(tag, message, throwable);
        }
    }

    public static void debug(String tag, String message) {
        if (sLogLevel <= 3) {
            Log.d(tag, message);
        }
    }

    public static void debug(String tag, String message, Throwable throwable) {
        if (sLogLevel <= 3) {
            Log.d(tag, message, throwable);
        }
    }

    public static void info(String tag, String message) {
        if (sLogLevel <= 4) {
            Log.i(tag, message);
        }
    }

    public static void info(String tag, String message, Throwable throwable) {
        if (sLogLevel <= 4) {
            Log.i(tag, message, throwable);
        }
    }

    public static void warn(String tag, String message) {
        if (sLogLevel <= 5) {
            Log.w(tag, message);
        }
    }

    public static void warn(String tag, String message, Throwable throwable) {
        if (sLogLevel <= 5) {
            Log.w(tag, message, throwable);
        }
    }

    public static void error(String tag, String message) {
        if (sLogLevel <= 6) {
            Log.e(tag, message);
        }
    }

    public static void error(String tag, String message, Throwable throwable) {
        if (sLogLevel <= 6) {
            Log.e(tag, message, throwable);
        }
    }

    public static void logAssert(String tag, String message) {
        if (sLogLevel <= 7) {
            Log.println(7, tag, message);
        }
    }

    public static void logAssert(String tag, String message, Throwable throwable) {
        if (sLogLevel <= 7) {
            Log.println(7, tag, message + "\n" + Log.getStackTraceString(throwable));
        }
    }
}
