package com.google.android.gms.internal.clearcut;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class zzbs {
    private static final Class<?> zzgl = zzak();

    private static Class<?> zzak() {
        try {
            return Class.forName("com.google.protobuf.ExtensionRegistry");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static zzbt zzal() {
        Class<?> cls = zzgl;
        if (cls != null) {
            try {
                return (zzbt) cls.getDeclaredMethod("getEmptyRegistry", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e) {
            }
        }
        return zzbt.zzgo;
    }
}
