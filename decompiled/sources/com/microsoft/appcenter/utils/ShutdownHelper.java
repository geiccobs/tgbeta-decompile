package com.microsoft.appcenter.utils;

import android.os.Process;
/* loaded from: classes.dex */
public class ShutdownHelper {
    public static void shutdown(int i) {
        Process.killProcess(Process.myPid());
        System.exit(i);
    }
}
