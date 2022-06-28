package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public enum zzhv {
    DOUBLE(zzhy.DOUBLE, 1),
    FLOAT(zzhy.FLOAT, 5),
    INT64(zzhy.LONG, 0),
    UINT64(zzhy.LONG, 0),
    INT32(zzhy.INT, 0),
    FIXED64(zzhy.LONG, 1),
    FIXED32(zzhy.INT, 5),
    BOOL(zzhy.BOOLEAN, 0),
    STRING(zzhy.STRING, 2) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhu
    },
    GROUP(zzhy.MESSAGE, 3) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhx
    },
    MESSAGE(zzhy.MESSAGE, 2) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhw
    },
    BYTES(zzhy.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhz
    },
    UINT32(zzhy.INT, 0),
    ENUM(zzhy.ENUM, 0),
    SFIXED32(zzhy.INT, 5),
    SFIXED64(zzhy.LONG, 1),
    SINT32(zzhy.INT, 0),
    SINT64(zzhy.LONG, 0);
    
    private final zzhy zzs;
    private final int zzt;

    zzhv(zzhy zzhyVar, int i) {
        this.zzs = zzhyVar;
        this.zzt = i;
    }

    public final zzhy zza() {
        return this.zzs;
    }

    public final int zzb() {
        return this.zzt;
    }

    /* synthetic */ zzhv(zzhy zzhyVar, int i, zzhs zzhsVar) {
        this(zzhyVar, i);
    }
}
