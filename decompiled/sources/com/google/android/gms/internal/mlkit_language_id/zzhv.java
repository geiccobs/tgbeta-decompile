package com.google.android.gms.internal.mlkit_language_id;
/* JADX WARN: Init of enum zzc can be incorrect */
/* JADX WARN: Init of enum zzd can be incorrect */
/* JADX WARN: Init of enum zze can be incorrect */
/* JADX WARN: Init of enum zzf can be incorrect */
/* JADX WARN: Init of enum zzg can be incorrect */
/* JADX WARN: Init of enum zzj can be incorrect */
/* JADX WARN: Init of enum zzk can be incorrect */
/* JADX WARN: Init of enum zzm can be incorrect */
/* JADX WARN: Init of enum zzo can be incorrect */
/* JADX WARN: Init of enum zzp can be incorrect */
/* JADX WARN: Init of enum zzq can be incorrect */
/* JADX WARN: Init of enum zzr can be incorrect */
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public enum zzhv {
    DOUBLE(zzhy.DOUBLE, 1),
    FLOAT(zzhy.FLOAT, 5),
    INT64(r5, 0),
    UINT64(r5, 0),
    INT32(r11, 0),
    FIXED64(r5, 1),
    FIXED32(r11, 5),
    BOOL(zzhy.BOOLEAN, 0),
    STRING(zzhy.STRING, 2) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhu
    },
    GROUP(r13, 3) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhx
    },
    MESSAGE(r13, 2) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhw
    },
    BYTES(zzhy.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.mlkit_language_id.zzhz
    },
    UINT32(r11, 0),
    ENUM(zzhy.ENUM, 0),
    SFIXED32(r11, 5),
    SFIXED64(r5, 1),
    SINT32(r11, 0),
    SINT64(r5, 0);
    
    private final zzhy zzs;
    private final int zzt;

    /* JADX INFO: Access modifiers changed from: private */
    zzhv(zzhy zzhyVar, int i) {
        this.zzs = zzhyVar;
        this.zzt = i;
    }

    public final zzhy zza() {
        return this.zzs;
    }

    static {
        zzhy zzhyVar = zzhy.LONG;
        zzhy zzhyVar2 = zzhy.INT;
        zzhy zzhyVar3 = zzhy.MESSAGE;
    }
}
