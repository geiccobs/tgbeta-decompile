package com.google.android.datatransport.runtime.dagger.internal;
/* loaded from: classes.dex */
public final class Preconditions {
    public static <T> T checkNotNull(T t) {
        t.getClass();
        return t;
    }

    public static <T> T checkNotNull(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    public static <T> void checkBuilderRequirement(T t, Class<T> cls) {
        if (t != null) {
            return;
        }
        throw new IllegalStateException(cls.getCanonicalName() + " must be set");
    }
}
