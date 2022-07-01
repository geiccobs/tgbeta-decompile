package com.google.android.gms.internal.vision;
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
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public enum zzml {
    DOUBLE(zzmo.DOUBLE, 1),
    FLOAT(zzmo.FLOAT, 5),
    INT64(r5, 0),
    UINT64(r5, 0),
    INT32(r11, 0),
    FIXED64(r5, 1),
    FIXED32(r11, 5),
    BOOL(zzmo.BOOLEAN, 0),
    STRING(zzmo.STRING, 2) { // from class: com.google.android.gms.internal.vision.zzmk
    },
    GROUP(r13, 3) { // from class: com.google.android.gms.internal.vision.zzmn
    },
    MESSAGE(r13, 2) { // from class: com.google.android.gms.internal.vision.zzmm
    },
    BYTES(zzmo.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.vision.zzmp
    },
    UINT32(r11, 0),
    ENUM(zzmo.ENUM, 0),
    SFIXED32(r11, 5),
    SFIXED64(r5, 1),
    SINT32(r11, 0),
    SINT64(r5, 0);
    
    private final zzmo zzs;
    private final int zzt;

    /* JADX INFO: Access modifiers changed from: private */
    zzml(zzmo zzmoVar, int i) {
        this.zzs = zzmoVar;
        this.zzt = i;
    }

    public final zzmo zza() {
        return this.zzs;
    }

    static {
        zzmo zzmoVar = zzmo.LONG;
        zzmo zzmoVar2 = zzmo.INT;
        zzmo zzmoVar3 = zzmo.MESSAGE;
    }
}
