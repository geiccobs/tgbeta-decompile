package com.google.android.gms.internal.clearcut;
/* JADX WARN: Init of enum zzqe can be incorrect */
/* JADX WARN: Init of enum zzqf can be incorrect */
/* JADX WARN: Init of enum zzqg can be incorrect */
/* JADX WARN: Init of enum zzqh can be incorrect */
/* JADX WARN: Init of enum zzqi can be incorrect */
/* JADX WARN: Init of enum zzql can be incorrect */
/* JADX WARN: Init of enum zzqm can be incorrect */
/* JADX WARN: Init of enum zzqo can be incorrect */
/* JADX WARN: Init of enum zzqq can be incorrect */
/* JADX WARN: Init of enum zzqr can be incorrect */
/* JADX WARN: Init of enum zzqs can be incorrect */
/* JADX WARN: Init of enum zzqt can be incorrect */
/* loaded from: classes.dex */
public enum zzfl {
    DOUBLE(zzfq.DOUBLE, 1),
    FLOAT(zzfq.FLOAT, 5),
    INT64(r5, 0),
    UINT64(r5, 0),
    INT32(r11, 0),
    FIXED64(r5, 1),
    FIXED32(r11, 5),
    BOOL(zzfq.BOOLEAN, 0),
    STRING(zzfq.STRING, 2) { // from class: com.google.android.gms.internal.clearcut.zzfm
    },
    GROUP(r13, 3) { // from class: com.google.android.gms.internal.clearcut.zzfn
    },
    MESSAGE(r13, 2) { // from class: com.google.android.gms.internal.clearcut.zzfo
    },
    BYTES(zzfq.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.clearcut.zzfp
    },
    UINT32(r11, 0),
    ENUM(zzfq.ENUM, 0),
    SFIXED32(r11, 5),
    SFIXED64(r5, 1),
    SINT32(r11, 0),
    SINT64(r5, 0);
    
    private final zzfq zzqu;
    private final int zzqv;

    static {
        zzfq zzfqVar = zzfq.LONG;
        zzfq zzfqVar2 = zzfq.INT;
        zzfq zzfqVar3 = zzfq.MESSAGE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    zzfl(zzfq zzfqVar, int i) {
        this.zzqu = zzfqVar;
        this.zzqv = i;
    }

    public final zzfq zzek() {
        return this.zzqu;
    }
}
