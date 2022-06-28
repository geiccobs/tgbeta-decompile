package com.google.android.gms.internal.clearcut;
/* loaded from: classes3.dex */
public enum zzfl {
    DOUBLE(zzfq.DOUBLE, 1),
    FLOAT(zzfq.FLOAT, 5),
    INT64(zzfq.LONG, 0),
    UINT64(zzfq.LONG, 0),
    INT32(zzfq.INT, 0),
    FIXED64(zzfq.LONG, 1),
    FIXED32(zzfq.INT, 5),
    BOOL(zzfq.BOOLEAN, 0),
    STRING(zzfq.STRING, 2) { // from class: com.google.android.gms.internal.clearcut.zzfm
    },
    GROUP(zzfq.MESSAGE, 3) { // from class: com.google.android.gms.internal.clearcut.zzfn
    },
    MESSAGE(zzfq.MESSAGE, 2) { // from class: com.google.android.gms.internal.clearcut.zzfo
    },
    BYTES(zzfq.BYTE_STRING, 2) { // from class: com.google.android.gms.internal.clearcut.zzfp
    },
    UINT32(zzfq.INT, 0),
    ENUM(zzfq.ENUM, 0),
    SFIXED32(zzfq.INT, 5),
    SFIXED64(zzfq.LONG, 1),
    SINT32(zzfq.INT, 0),
    SINT64(zzfq.LONG, 0);
    
    private final zzfq zzqu;
    private final int zzqv;

    zzfl(zzfq zzfqVar, int i) {
        this.zzqu = zzfqVar;
        this.zzqv = i;
    }

    /* synthetic */ zzfl(zzfq zzfqVar, int i, zzfk zzfkVar) {
        this(zzfqVar, i);
    }

    public final zzfq zzek() {
        return this.zzqu;
    }

    public final int zzel() {
        return this.zzqv;
    }
}
