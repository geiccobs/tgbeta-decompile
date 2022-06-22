package com.google.android.gms.internal.clearcut;
/* JADX WARN: Init of enum zzky can be incorrect */
/* JADX WARN: Init of enum zzlf can be incorrect */
/* loaded from: classes.dex */
public enum zzcq {
    VOID(Void.class, Void.class, null),
    INT(r1, Integer.class, 0),
    LONG(Long.TYPE, Long.class, 0L),
    FLOAT(Float.TYPE, Float.class, Float.valueOf(0.0f)),
    DOUBLE(Double.TYPE, Double.class, Double.valueOf(0.0d)),
    BOOLEAN(Boolean.TYPE, Boolean.class, Boolean.FALSE),
    STRING(String.class, String.class, ""),
    BYTE_STRING(zzbb.class, zzbb.class, zzbb.zzfi),
    ENUM(r1, Integer.class, null),
    MESSAGE(Object.class, Object.class, null);
    
    private final Class<?> zzli;
    private final Object zzlj;

    static {
        Class cls = Integer.TYPE;
    }

    zzcq(Class cls, Class cls2, Object obj) {
        this.zzli = cls2;
        this.zzlj = obj;
    }

    public final Class<?> zzbq() {
        return this.zzli;
    }
}
