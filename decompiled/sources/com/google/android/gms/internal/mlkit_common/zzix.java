package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public enum zzix {
    DOUBLE(zzja.DOUBLE, 1),
    FLOAT(zzja.FLOAT, 5),
    INT64(zzja.LONG, 0),
    UINT64(zzja.LONG, 0),
    INT32(zzja.INT, 0),
    FIXED64(zzja.LONG, 1),
    FIXED32(zzja.INT, 5),
    BOOL(zzja.BOOLEAN, 0),
    STRING(zzja.STRING, 2) { // from class: com.google.android.gms.internal.mlkit_common.zziw
    },
    GROUP(zzja.MESSAGE, 3) { // from class: com.google.android.gms.internal.mlkit_common.zziz
    },
    MESSAGE(zzja.MESSAGE, 2) { // from class: com.google.android.gms.internal.mlkit_common.zziy
    },
    BYTES(zzja.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.mlkit_common.zzjb
    },
    UINT32(zzja.INT, 0),
    ENUM(zzja.ENUM, 0),
    SFIXED32(zzja.INT, 5),
    SFIXED64(zzja.LONG, 1),
    SINT32(zzja.INT, 0),
    SINT64(zzja.LONG, 0);
    
    private final zzja zzs;
    private final int zzt;

    zzix(zzja zzjaVar, int i) {
        this.zzs = zzjaVar;
        this.zzt = i;
    }

    public final zzja zza() {
        return this.zzs;
    }

    public final int zzb() {
        return this.zzt;
    }

    /* synthetic */ zzix(zzja zzjaVar, int i, zziu zziuVar) {
        this(zzjaVar, i);
    }
}
