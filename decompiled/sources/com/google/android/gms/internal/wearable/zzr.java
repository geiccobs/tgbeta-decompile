package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public enum zzr implements zzbu {
    BYTE_ARRAY(1),
    STRING(2),
    DOUBLE(3),
    FLOAT(4),
    LONG(5),
    INT(6),
    BYTE(7),
    BOOLEAN(8),
    DATA_BUNDLE(9),
    ARRAY_LIST(10),
    STRING_ARRAY(11),
    LONG_ARRAY(12),
    FLOAT_ARRAY(15),
    ASSET_INDEX(13),
    NULL_VALUE(14);
    
    private static final zzbv<zzr> zzp = new zzbv<zzr>() { // from class: com.google.android.gms.internal.wearable.zzp
    };
    private final int zzq;

    zzr(int i) {
        this.zzq = i;
    }

    public static zzr zzb(int i) {
        switch (i) {
            case 1:
                return BYTE_ARRAY;
            case 2:
                return STRING;
            case 3:
                return DOUBLE;
            case 4:
                return FLOAT;
            case 5:
                return LONG;
            case 6:
                return INT;
            case 7:
                return BYTE;
            case 8:
                return BOOLEAN;
            case 9:
                return DATA_BUNDLE;
            case 10:
                return ARRAY_LIST;
            case 11:
                return STRING_ARRAY;
            case 12:
                return LONG_ARRAY;
            case 13:
                return ASSET_INDEX;
            case 14:
                return NULL_VALUE;
            case 15:
                return FLOAT_ARRAY;
            default:
                return null;
        }
    }

    public static zzbw zzc() {
        return zzq.zza;
    }

    @Override // java.lang.Enum
    public final String toString() {
        return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzq + " name=" + name() + '>';
    }

    public final int zza() {
        return this.zzq;
    }
}
