package com.microsoft.appcenter;

import com.microsoft.appcenter.utils.AppCenterLog;
/* loaded from: classes.dex */
public final class Flags {
    public static int getPersistenceFlag(int i, boolean z) {
        int i2 = i & 255;
        if (i2 == 1 || i2 == 2) {
            return i2;
        }
        if (i2 != 0 && z) {
            AppCenterLog.warn("AppCenter", "Invalid value=" + i2 + " for persistence flag, using NORMAL as a default.");
        }
        return 1;
    }
}
