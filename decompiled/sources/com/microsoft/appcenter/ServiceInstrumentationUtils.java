package com.microsoft.appcenter;

import android.os.Bundle;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.InstrumentationRegistryHelper;
/* loaded from: classes3.dex */
class ServiceInstrumentationUtils {
    static final String DISABLE_ALL_SERVICES = "All";
    static final String DISABLE_SERVICES = "APP_CENTER_DISABLE";

    ServiceInstrumentationUtils() {
    }

    public static boolean isServiceDisabledByInstrumentation(String serviceName) {
        try {
            Bundle arguments = InstrumentationRegistryHelper.getArguments();
            String disableServices = arguments.getString(DISABLE_SERVICES);
            if (disableServices == null) {
                return false;
            }
            String[] disableServicesList = disableServices.split(",");
            for (String service : disableServicesList) {
                String service2 = service.trim();
                if (service2.equals(DISABLE_ALL_SERVICES) || service2.equals(serviceName)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalStateException | LinkageError e) {
            AppCenterLog.debug("AppCenter", "Cannot read instrumentation variables in a non-test environment.");
            return false;
        }
    }
}
