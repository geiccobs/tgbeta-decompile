package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public enum zzef implements zzfv {
    UNRECOGNIZED(0),
    CODE_128(1),
    CODE_39(2),
    CODE_93(3),
    CODABAR(4),
    DATA_MATRIX(5),
    EAN_13(6),
    EAN_8(7),
    ITF(8),
    QR_CODE(9),
    UPC_A(10),
    UPC_E(11),
    PDF417(12),
    AZTEC(13),
    DATABAR(14),
    TEZ_CODE(16);
    
    private static final zzfu<zzef> zzq = new zzfu<zzef>() { // from class: com.google.android.gms.internal.mlkit_common.zzee
    };
    private final int zzr;

    @Override // com.google.android.gms.internal.mlkit_common.zzfv
    public final int zza() {
        return this.zzr;
    }

    public static zzfx zzb() {
        return zzeh.zza;
    }

    @Override // java.lang.Enum
    public final String toString() {
        return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzr + " name=" + name() + '>';
    }

    zzef(int i) {
        this.zzr = i;
    }
}
