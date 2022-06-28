package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public enum zzcv {
    DOUBLE(0, 1, zzdk.DOUBLE),
    FLOAT(1, 1, zzdk.FLOAT),
    INT64(2, 1, zzdk.LONG),
    UINT64(3, 1, zzdk.LONG),
    INT32(4, 1, zzdk.INT),
    FIXED64(5, 1, zzdk.LONG),
    FIXED32(6, 1, zzdk.INT),
    BOOL(7, 1, zzdk.BOOLEAN),
    STRING(8, 1, zzdk.STRING),
    MESSAGE(9, 1, zzdk.MESSAGE),
    BYTES(10, 1, zzdk.BYTE_STRING),
    UINT32(11, 1, zzdk.INT),
    ENUM(12, 1, zzdk.ENUM),
    SFIXED32(13, 1, zzdk.INT),
    SFIXED64(14, 1, zzdk.LONG),
    SINT32(15, 1, zzdk.INT),
    SINT64(16, 1, zzdk.LONG),
    GROUP(17, 1, zzdk.MESSAGE),
    DOUBLE_LIST(18, 2, zzdk.DOUBLE),
    FLOAT_LIST(19, 2, zzdk.FLOAT),
    INT64_LIST(20, 2, zzdk.LONG),
    UINT64_LIST(21, 2, zzdk.LONG),
    INT32_LIST(22, 2, zzdk.INT),
    FIXED64_LIST(23, 2, zzdk.LONG),
    FIXED32_LIST(24, 2, zzdk.INT),
    BOOL_LIST(25, 2, zzdk.BOOLEAN),
    STRING_LIST(26, 2, zzdk.STRING),
    MESSAGE_LIST(27, 2, zzdk.MESSAGE),
    BYTES_LIST(28, 2, zzdk.BYTE_STRING),
    UINT32_LIST(29, 2, zzdk.INT),
    ENUM_LIST(30, 2, zzdk.ENUM),
    SFIXED32_LIST(31, 2, zzdk.INT),
    SFIXED64_LIST(32, 2, zzdk.LONG),
    SINT32_LIST(33, 2, zzdk.INT),
    SINT64_LIST(34, 2, zzdk.LONG),
    DOUBLE_LIST_PACKED(35, 3, zzdk.DOUBLE),
    FLOAT_LIST_PACKED(36, 3, zzdk.FLOAT),
    INT64_LIST_PACKED(37, 3, zzdk.LONG),
    UINT64_LIST_PACKED(38, 3, zzdk.LONG),
    INT32_LIST_PACKED(39, 3, zzdk.INT),
    FIXED64_LIST_PACKED(40, 3, zzdk.LONG),
    FIXED32_LIST_PACKED(41, 3, zzdk.INT),
    BOOL_LIST_PACKED(42, 3, zzdk.BOOLEAN),
    UINT32_LIST_PACKED(43, 3, zzdk.INT),
    ENUM_LIST_PACKED(44, 3, zzdk.ENUM),
    SFIXED32_LIST_PACKED(45, 3, zzdk.INT),
    SFIXED64_LIST_PACKED(46, 3, zzdk.LONG),
    SINT32_LIST_PACKED(47, 3, zzdk.INT),
    SINT64_LIST_PACKED(48, 3, zzdk.LONG),
    GROUP_LIST(49, 2, zzdk.MESSAGE),
    MAP(50, 4, zzdk.VOID);
    
    private static final zzcv[] zzac;
    private final zzdk zzZ;
    private final int zzaa;
    private final Class<?> zzab;

    static {
        zzcv[] values = values();
        zzac = new zzcv[values.length];
        for (zzcv zzcvVar : values) {
            zzac[zzcvVar.zzaa] = zzcvVar;
        }
    }

    zzcv(int i, int i2, zzdk zzdkVar) {
        this.zzaa = i;
        this.zzZ = zzdkVar;
        zzdk zzdkVar2 = zzdk.VOID;
        switch (i2 - 1) {
            case 1:
                this.zzab = zzdkVar.zza();
                break;
            case 2:
            default:
                this.zzab = null;
                break;
            case 3:
                this.zzab = zzdkVar.zza();
                break;
        }
        if (i2 == 1) {
            zzdkVar.ordinal();
        }
    }

    public final int zza() {
        return this.zzaa;
    }
}
