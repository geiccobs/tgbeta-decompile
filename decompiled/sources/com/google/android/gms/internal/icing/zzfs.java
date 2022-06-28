package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public enum zzfs {
    DOUBLE(zzft.DOUBLE, 1),
    FLOAT(zzft.FLOAT, 5),
    INT64(zzft.LONG, 0),
    UINT64(zzft.LONG, 0),
    INT32(zzft.INT, 0),
    FIXED64(zzft.LONG, 1),
    FIXED32(zzft.INT, 5),
    BOOL(zzft.BOOLEAN, 0),
    STRING(zzft.STRING, 2),
    GROUP(zzft.MESSAGE, 3),
    MESSAGE(zzft.MESSAGE, 2),
    BYTES(zzft.BYTE_STRING, 2),
    UINT32(zzft.INT, 0),
    ENUM(zzft.ENUM, 0),
    SFIXED32(zzft.INT, 5),
    SFIXED64(zzft.LONG, 1),
    SINT32(zzft.INT, 0),
    SINT64(zzft.LONG, 0);
    
    private final zzft zzs;

    zzfs(zzft zzftVar, int i) {
        this.zzs = zzftVar;
    }

    public final zzft zza() {
        return this.zzs;
    }
}
