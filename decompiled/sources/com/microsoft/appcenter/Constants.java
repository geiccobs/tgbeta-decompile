package com.microsoft.appcenter;

import android.content.Context;
import com.microsoft.appcenter.utils.AppCenterLog;
/* loaded from: classes.dex */
public class Constants {
    public static boolean APPLICATION_DEBUGGABLE = false;
    public static String FILES_PATH;

    public static void loadFromContext(Context context) {
        loadFilesPath(context);
        setDebuggableFlag(context);
    }

    private static void loadFilesPath(Context context) {
        if (context != null) {
            try {
                FILES_PATH = context.getFilesDir().getAbsolutePath();
            } catch (Exception e) {
                AppCenterLog.error("AppCenter", "Exception thrown when accessing the application filesystem", e);
            }
        }
    }

    private static void setDebuggableFlag(Context context) {
        if (context == null || context.getApplicationInfo() == null) {
            return;
        }
        APPLICATION_DEBUGGABLE = (context.getApplicationInfo().flags & 2) > 0;
    }
}
