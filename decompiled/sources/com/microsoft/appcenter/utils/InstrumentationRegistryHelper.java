package com.microsoft.appcenter.utils;

import android.os.Bundle;
/* loaded from: classes.dex */
public class InstrumentationRegistryHelper {
    private static final String[] LOCATIONS = {"androidx.test.platform.app.InstrumentationRegistry", "androidx.test.InstrumentationRegistry", "androidx.test.InstrumentationRegistry"};

    public static Bundle getArguments() throws IllegalStateException {
        Exception e = null;
        for (String str : LOCATIONS) {
            try {
                return (Bundle) getClass(str).getMethod("getArguments", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e2) {
                e = e2;
            }
        }
        throw new IllegalStateException(e);
    }

    private static Class<?> getClass(String str) throws ClassNotFoundException {
        return Class.forName(str);
    }
}
