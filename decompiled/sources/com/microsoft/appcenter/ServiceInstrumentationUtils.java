package com.microsoft.appcenter;

import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.InstrumentationRegistryHelper;
/* loaded from: classes.dex */
class ServiceInstrumentationUtils {
    public static boolean isServiceDisabledByInstrumentation(String str) {
        try {
            String string = InstrumentationRegistryHelper.getArguments().getString("APP_CENTER_DISABLE");
            if (string == null) {
                return false;
            }
            for (String str2 : string.split(",")) {
                String trim = str2.trim();
                if (trim.equals("All") || trim.equals(str)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalStateException | LinkageError unused) {
            AppCenterLog.debug("AppCenter", "Cannot read instrumentation variables in a non-test environment.");
            return false;
        }
    }
}
