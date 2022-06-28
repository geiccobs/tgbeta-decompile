package com.google.android.gms.internal.vision;

import java.lang.reflect.Type;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public enum zziv {
    DOUBLE(0, zzix.SCALAR, zzjm.DOUBLE),
    FLOAT(1, zzix.SCALAR, zzjm.FLOAT),
    INT64(2, zzix.SCALAR, zzjm.LONG),
    UINT64(3, zzix.SCALAR, zzjm.LONG),
    INT32(4, zzix.SCALAR, zzjm.INT),
    FIXED64(5, zzix.SCALAR, zzjm.LONG),
    FIXED32(6, zzix.SCALAR, zzjm.INT),
    BOOL(7, zzix.SCALAR, zzjm.BOOLEAN),
    STRING(8, zzix.SCALAR, zzjm.STRING),
    MESSAGE(9, zzix.SCALAR, zzjm.MESSAGE),
    BYTES(10, zzix.SCALAR, zzjm.BYTE_STRING),
    UINT32(11, zzix.SCALAR, zzjm.INT),
    ENUM(12, zzix.SCALAR, zzjm.ENUM),
    SFIXED32(13, zzix.SCALAR, zzjm.INT),
    SFIXED64(14, zzix.SCALAR, zzjm.LONG),
    SINT32(15, zzix.SCALAR, zzjm.INT),
    SINT64(16, zzix.SCALAR, zzjm.LONG),
    GROUP(17, zzix.SCALAR, zzjm.MESSAGE),
    DOUBLE_LIST(18, zzix.VECTOR, zzjm.DOUBLE),
    FLOAT_LIST(19, zzix.VECTOR, zzjm.FLOAT),
    INT64_LIST(20, zzix.VECTOR, zzjm.LONG),
    UINT64_LIST(21, zzix.VECTOR, zzjm.LONG),
    INT32_LIST(22, zzix.VECTOR, zzjm.INT),
    FIXED64_LIST(23, zzix.VECTOR, zzjm.LONG),
    FIXED32_LIST(24, zzix.VECTOR, zzjm.INT),
    BOOL_LIST(25, zzix.VECTOR, zzjm.BOOLEAN),
    STRING_LIST(26, zzix.VECTOR, zzjm.STRING),
    MESSAGE_LIST(27, zzix.VECTOR, zzjm.MESSAGE),
    BYTES_LIST(28, zzix.VECTOR, zzjm.BYTE_STRING),
    UINT32_LIST(29, zzix.VECTOR, zzjm.INT),
    ENUM_LIST(30, zzix.VECTOR, zzjm.ENUM),
    SFIXED32_LIST(31, zzix.VECTOR, zzjm.INT),
    SFIXED64_LIST(32, zzix.VECTOR, zzjm.LONG),
    SINT32_LIST(33, zzix.VECTOR, zzjm.INT),
    SINT64_LIST(34, zzix.VECTOR, zzjm.LONG),
    DOUBLE_LIST_PACKED(35, zzix.PACKED_VECTOR, zzjm.DOUBLE),
    FLOAT_LIST_PACKED(36, zzix.PACKED_VECTOR, zzjm.FLOAT),
    INT64_LIST_PACKED(37, zzix.PACKED_VECTOR, zzjm.LONG),
    UINT64_LIST_PACKED(38, zzix.PACKED_VECTOR, zzjm.LONG),
    INT32_LIST_PACKED(39, zzix.PACKED_VECTOR, zzjm.INT),
    FIXED64_LIST_PACKED(40, zzix.PACKED_VECTOR, zzjm.LONG),
    FIXED32_LIST_PACKED(41, zzix.PACKED_VECTOR, zzjm.INT),
    BOOL_LIST_PACKED(42, zzix.PACKED_VECTOR, zzjm.BOOLEAN),
    UINT32_LIST_PACKED(43, zzix.PACKED_VECTOR, zzjm.INT),
    ENUM_LIST_PACKED(44, zzix.PACKED_VECTOR, zzjm.ENUM),
    SFIXED32_LIST_PACKED(45, zzix.PACKED_VECTOR, zzjm.INT),
    SFIXED64_LIST_PACKED(46, zzix.PACKED_VECTOR, zzjm.LONG),
    SINT32_LIST_PACKED(47, zzix.PACKED_VECTOR, zzjm.INT),
    SINT64_LIST_PACKED(48, zzix.PACKED_VECTOR, zzjm.LONG),
    GROUP_LIST(49, zzix.VECTOR, zzjm.MESSAGE),
    MAP(50, zzix.MAP, zzjm.VOID);
    
    private static final zziv[] zzbe;
    private static final Type[] zzbf = new Type[0];
    private final zzjm zzaz;
    private final int zzba;
    private final zzix zzbb;
    private final Class<?> zzbc;
    private final boolean zzbd;

    zziv(int i, zzix zzixVar, zzjm zzjmVar) {
        this.zzba = i;
        this.zzbb = zzixVar;
        this.zzaz = zzjmVar;
        switch (zziy.zza[zzixVar.ordinal()]) {
            case 1:
                this.zzbc = zzjmVar.zza();
                break;
            case 2:
                this.zzbc = zzjmVar.zza();
                break;
            default:
                this.zzbc = null;
                break;
        }
        boolean z = false;
        if (zzixVar == zzix.SCALAR) {
            switch (zziy.zzb[zzjmVar.ordinal()]) {
                case 1:
                case 2:
                case 3:
                    break;
                default:
                    z = true;
                    break;
            }
        }
        this.zzbd = z;
    }

    public final int zza() {
        return this.zzba;
    }

    static {
        zziv[] values = values();
        zzbe = new zziv[values.length];
        for (zziv zzivVar : values) {
            zzbe[zzivVar.zzba] = zzivVar;
        }
    }
}
