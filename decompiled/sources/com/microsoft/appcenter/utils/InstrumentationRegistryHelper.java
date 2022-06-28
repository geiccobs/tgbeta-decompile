package com.microsoft.appcenter.utils;

import android.os.Bundle;
import java.lang.reflect.Method;
/* loaded from: classes3.dex */
public class InstrumentationRegistryHelper {
    private static final String[] LOCATIONS = {"androidx.test.platform.app.InstrumentationRegistry", "androidx.test.InstrumentationRegistry", "androidx.test.InstrumentationRegistry"};

    public static Bundle getArguments() throws IllegalStateException {
        String[] strArr;
        Exception exception = null;
        for (String location : LOCATIONS) {
            try {
                Class<?> aClass = getClass(location);
                Method getArguments = aClass.getMethod("getArguments", new Class[0]);
                return (Bundle) getArguments.invoke(null, new Object[0]);
            } catch (Exception e) {
                exception = e;
            }
        }
        throw new IllegalStateException(exception);
    }

    private static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
