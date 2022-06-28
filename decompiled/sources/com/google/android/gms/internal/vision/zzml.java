package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public enum zzml {
    DOUBLE(zzmo.DOUBLE, 1),
    FLOAT(zzmo.FLOAT, 5),
    INT64(zzmo.LONG, 0),
    UINT64(zzmo.LONG, 0),
    INT32(zzmo.INT, 0),
    FIXED64(zzmo.LONG, 1),
    FIXED32(zzmo.INT, 5),
    BOOL(zzmo.BOOLEAN, 0),
    STRING(zzmo.STRING, 2) { // from class: com.google.android.gms.internal.vision.zzmk
    },
    GROUP(zzmo.MESSAGE, 3) { // from class: com.google.android.gms.internal.vision.zzmn
    },
    MESSAGE(zzmo.MESSAGE, 2) { // from class: com.google.android.gms.internal.vision.zzmm
    },
    BYTES(zzmo.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.vision.zzmp
    },
    UINT32(zzmo.INT, 0),
    ENUM(zzmo.ENUM, 0),
    SFIXED32(zzmo.INT, 5),
    SFIXED64(zzmo.LONG, 1),
    SINT32(zzmo.INT, 0),
    SINT64(zzmo.LONG, 0);
    
    private final zzmo zzs;
    private final int zzt;

    zzml(zzmo zzmoVar, int i) {
        this.zzs = zzmoVar;
        this.zzt = i;
    }

    public final zzmo zza() {
        return this.zzs;
    }

    public final int zzb() {
        return this.zzt;
    }

    /* synthetic */ zzml(zzmo zzmoVar, int i, zzmi zzmiVar) {
        this(zzmoVar, i);
    }
}
