package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public enum zzbm {
    DOUBLE(0, 1, zzcd.DOUBLE),
    FLOAT(1, 1, zzcd.FLOAT),
    INT64(2, 1, zzcd.LONG),
    UINT64(3, 1, zzcd.LONG),
    INT32(4, 1, zzcd.INT),
    FIXED64(5, 1, zzcd.LONG),
    FIXED32(6, 1, zzcd.INT),
    BOOL(7, 1, zzcd.BOOLEAN),
    STRING(8, 1, zzcd.STRING),
    MESSAGE(9, 1, zzcd.MESSAGE),
    BYTES(10, 1, zzcd.BYTE_STRING),
    UINT32(11, 1, zzcd.INT),
    ENUM(12, 1, zzcd.ENUM),
    SFIXED32(13, 1, zzcd.INT),
    SFIXED64(14, 1, zzcd.LONG),
    SINT32(15, 1, zzcd.INT),
    SINT64(16, 1, zzcd.LONG),
    GROUP(17, 1, zzcd.MESSAGE),
    DOUBLE_LIST(18, 2, zzcd.DOUBLE),
    FLOAT_LIST(19, 2, zzcd.FLOAT),
    INT64_LIST(20, 2, zzcd.LONG),
    UINT64_LIST(21, 2, zzcd.LONG),
    INT32_LIST(22, 2, zzcd.INT),
    FIXED64_LIST(23, 2, zzcd.LONG),
    FIXED32_LIST(24, 2, zzcd.INT),
    BOOL_LIST(25, 2, zzcd.BOOLEAN),
    STRING_LIST(26, 2, zzcd.STRING),
    MESSAGE_LIST(27, 2, zzcd.MESSAGE),
    BYTES_LIST(28, 2, zzcd.BYTE_STRING),
    UINT32_LIST(29, 2, zzcd.INT),
    ENUM_LIST(30, 2, zzcd.ENUM),
    SFIXED32_LIST(31, 2, zzcd.INT),
    SFIXED64_LIST(32, 2, zzcd.LONG),
    SINT32_LIST(33, 2, zzcd.INT),
    SINT64_LIST(34, 2, zzcd.LONG),
    DOUBLE_LIST_PACKED(35, 3, zzcd.DOUBLE),
    FLOAT_LIST_PACKED(36, 3, zzcd.FLOAT),
    INT64_LIST_PACKED(37, 3, zzcd.LONG),
    UINT64_LIST_PACKED(38, 3, zzcd.LONG),
    INT32_LIST_PACKED(39, 3, zzcd.INT),
    FIXED64_LIST_PACKED(40, 3, zzcd.LONG),
    FIXED32_LIST_PACKED(41, 3, zzcd.INT),
    BOOL_LIST_PACKED(42, 3, zzcd.BOOLEAN),
    UINT32_LIST_PACKED(43, 3, zzcd.INT),
    ENUM_LIST_PACKED(44, 3, zzcd.ENUM),
    SFIXED32_LIST_PACKED(45, 3, zzcd.INT),
    SFIXED64_LIST_PACKED(46, 3, zzcd.LONG),
    SINT32_LIST_PACKED(47, 3, zzcd.INT),
    SINT64_LIST_PACKED(48, 3, zzcd.LONG),
    GROUP_LIST(49, 2, zzcd.MESSAGE),
    MAP(50, 4, zzcd.VOID);
    
    private static final zzbm[] zzac;
    private final zzcd zzZ;
    private final int zzaa;
    private final Class<?> zzab;

    static {
        zzbm[] values = values();
        zzac = new zzbm[values.length];
        for (zzbm zzbmVar : values) {
            zzac[zzbmVar.zzaa] = zzbmVar;
        }
    }

    zzbm(int i, int i2, zzcd zzcdVar) {
        this.zzaa = i;
        this.zzZ = zzcdVar;
        zzcd zzcdVar2 = zzcd.VOID;
        switch (i2 - 1) {
            case 1:
                this.zzab = zzcdVar.zza();
                break;
            case 2:
            default:
                this.zzab = null;
                break;
            case 3:
                this.zzab = zzcdVar.zza();
                break;
        }
        if (i2 == 1) {
            zzcdVar.ordinal();
        }
    }

    public final int zza() {
        return this.zzaa;
    }
}
