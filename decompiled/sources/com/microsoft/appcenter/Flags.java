package com.microsoft.appcenter;

import com.microsoft.appcenter.utils.AppCenterLog;
/* loaded from: classes3.dex */
public final class Flags {
    public static final int CRITICAL = 2;
    public static final int DEFAULTS = 1;
    public static final int NORMAL = 1;
    @Deprecated
    public static final int PERSISTENCE_CRITICAL = 2;
    private static final int PERSISTENCE_MASK = 255;
    @Deprecated
    public static final int PERSISTENCE_NORMAL = 1;

    public static int getPersistenceFlag(int flags, boolean warnFallback) {
        int persistencePriority = flags & 255;
        if (persistencePriority != 1 && persistencePriority != 2) {
            if (persistencePriority != 0 && warnFallback) {
                AppCenterLog.warn("AppCenter", "Invalid value=" + persistencePriority + " for persistence flag, using NORMAL as a default.");
            }
            return 1;
        }
        return persistencePriority;
    }
}
