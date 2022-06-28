package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public enum zzem {
    DOUBLE(zzen.DOUBLE, 1),
    FLOAT(zzen.FLOAT, 5),
    INT64(zzen.LONG, 0),
    UINT64(zzen.LONG, 0),
    INT32(zzen.INT, 0),
    FIXED64(zzen.LONG, 1),
    FIXED32(zzen.INT, 5),
    BOOL(zzen.BOOLEAN, 0),
    STRING(zzen.STRING, 2),
    GROUP(zzen.MESSAGE, 3),
    MESSAGE(zzen.MESSAGE, 2),
    BYTES(zzen.BYTE_STRING, 2),
    UINT32(zzen.INT, 0),
    ENUM(zzen.ENUM, 0),
    SFIXED32(zzen.INT, 5),
    SFIXED64(zzen.LONG, 1),
    SINT32(zzen.INT, 0),
    SINT64(zzen.LONG, 0);
    
    private final zzen zzs;

    zzem(zzen zzenVar, int i) {
        this.zzs = zzenVar;
    }

    public final zzen zza() {
        return this.zzs;
    }
}
