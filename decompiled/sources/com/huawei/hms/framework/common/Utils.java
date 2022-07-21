package com.huawei.hms.framework.common;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public class Utils {
    private static final String TAG = "Utils";

    public static long getCurrentTime(boolean z) {
        if (z) {
            return SystemClock.elapsedRealtime();
        }
        return System.currentTimeMillis();
    }

    public static boolean is64Bit(Context context) {
        if (context == null) {
            Logger.e(TAG, "Null context, please check it.");
            return false;
        }
        Context applicationContext = context.getApplicationContext() == null ? context : context.getApplicationContext();
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            return Process.is64Bit();
        }
        if (i < 21) {
            return false;
        }
        try {
            return applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), ConnectionsManager.RequestFlagNeedQuickAck).nativeLibraryDir.contains("64");
        } catch (PackageManager.NameNotFoundException unused) {
            Logger.e(TAG, "Get application info failed: name not found, try to get baseContext.");
            if (!(context instanceof ContextWrapper)) {
                return false;
            }
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if (baseContext == null) {
                Logger.w(TAG, "Get baseContext failed: null. Return default: is64-bit.");
                return true;
            }
            try {
                return baseContext.getPackageManager().getApplicationInfo(baseContext.getPackageName(), ConnectionsManager.RequestFlagNeedQuickAck).nativeLibraryDir.contains("64");
            } catch (PackageManager.NameNotFoundException unused2) {
                Logger.e(TAG, "Get baseContext application info failed: name not found");
                return true;
            }
        }
    }
}
