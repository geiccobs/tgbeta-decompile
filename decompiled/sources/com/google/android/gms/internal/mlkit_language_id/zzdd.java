package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public enum zzdd implements zzet {
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
    
    private final int zzr;

    @Override // com.google.android.gms.internal.mlkit_language_id.zzet
    public final int zza() {
        return this.zzr;
    }

    public static zzev zzb() {
        return zzdf.zza;
    }

    @Override // java.lang.Enum
    public final String toString() {
        return "<" + zzdd.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzr + " name=" + name() + '>';
    }

    zzdd(int i) {
        this.zzr = i;
    }

    static {
        new Object() { // from class: com.google.android.gms.internal.mlkit_language_id.zzdc
        };
    }
}
