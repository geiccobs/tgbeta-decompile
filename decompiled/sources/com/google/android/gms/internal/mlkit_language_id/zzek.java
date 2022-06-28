package com.google.android.gms.internal.mlkit_language_id;

import java.lang.reflect.Type;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public enum zzek {
    DOUBLE(0, zzem.SCALAR, zzfb.DOUBLE),
    FLOAT(1, zzem.SCALAR, zzfb.FLOAT),
    INT64(2, zzem.SCALAR, zzfb.LONG),
    UINT64(3, zzem.SCALAR, zzfb.LONG),
    INT32(4, zzem.SCALAR, zzfb.INT),
    FIXED64(5, zzem.SCALAR, zzfb.LONG),
    FIXED32(6, zzem.SCALAR, zzfb.INT),
    BOOL(7, zzem.SCALAR, zzfb.BOOLEAN),
    STRING(8, zzem.SCALAR, zzfb.STRING),
    MESSAGE(9, zzem.SCALAR, zzfb.MESSAGE),
    BYTES(10, zzem.SCALAR, zzfb.BYTE_STRING),
    UINT32(11, zzem.SCALAR, zzfb.INT),
    ENUM(12, zzem.SCALAR, zzfb.ENUM),
    SFIXED32(13, zzem.SCALAR, zzfb.INT),
    SFIXED64(14, zzem.SCALAR, zzfb.LONG),
    SINT32(15, zzem.SCALAR, zzfb.INT),
    SINT64(16, zzem.SCALAR, zzfb.LONG),
    GROUP(17, zzem.SCALAR, zzfb.MESSAGE),
    DOUBLE_LIST(18, zzem.VECTOR, zzfb.DOUBLE),
    FLOAT_LIST(19, zzem.VECTOR, zzfb.FLOAT),
    INT64_LIST(20, zzem.VECTOR, zzfb.LONG),
    UINT64_LIST(21, zzem.VECTOR, zzfb.LONG),
    INT32_LIST(22, zzem.VECTOR, zzfb.INT),
    FIXED64_LIST(23, zzem.VECTOR, zzfb.LONG),
    FIXED32_LIST(24, zzem.VECTOR, zzfb.INT),
    BOOL_LIST(25, zzem.VECTOR, zzfb.BOOLEAN),
    STRING_LIST(26, zzem.VECTOR, zzfb.STRING),
    MESSAGE_LIST(27, zzem.VECTOR, zzfb.MESSAGE),
    BYTES_LIST(28, zzem.VECTOR, zzfb.BYTE_STRING),
    UINT32_LIST(29, zzem.VECTOR, zzfb.INT),
    ENUM_LIST(30, zzem.VECTOR, zzfb.ENUM),
    SFIXED32_LIST(31, zzem.VECTOR, zzfb.INT),
    SFIXED64_LIST(32, zzem.VECTOR, zzfb.LONG),
    SINT32_LIST(33, zzem.VECTOR, zzfb.INT),
    SINT64_LIST(34, zzem.VECTOR, zzfb.LONG),
    DOUBLE_LIST_PACKED(35, zzem.PACKED_VECTOR, zzfb.DOUBLE),
    FLOAT_LIST_PACKED(36, zzem.PACKED_VECTOR, zzfb.FLOAT),
    INT64_LIST_PACKED(37, zzem.PACKED_VECTOR, zzfb.LONG),
    UINT64_LIST_PACKED(38, zzem.PACKED_VECTOR, zzfb.LONG),
    INT32_LIST_PACKED(39, zzem.PACKED_VECTOR, zzfb.INT),
    FIXED64_LIST_PACKED(40, zzem.PACKED_VECTOR, zzfb.LONG),
    FIXED32_LIST_PACKED(41, zzem.PACKED_VECTOR, zzfb.INT),
    BOOL_LIST_PACKED(42, zzem.PACKED_VECTOR, zzfb.BOOLEAN),
    UINT32_LIST_PACKED(43, zzem.PACKED_VECTOR, zzfb.INT),
    ENUM_LIST_PACKED(44, zzem.PACKED_VECTOR, zzfb.ENUM),
    SFIXED32_LIST_PACKED(45, zzem.PACKED_VECTOR, zzfb.INT),
    SFIXED64_LIST_PACKED(46, zzem.PACKED_VECTOR, zzfb.LONG),
    SINT32_LIST_PACKED(47, zzem.PACKED_VECTOR, zzfb.INT),
    SINT64_LIST_PACKED(48, zzem.PACKED_VECTOR, zzfb.LONG),
    GROUP_LIST(49, zzem.VECTOR, zzfb.MESSAGE),
    MAP(50, zzem.MAP, zzfb.VOID);
    
    private static final zzek[] zzbe;
    private static final Type[] zzbf = new Type[0];
    private final zzfb zzaz;
    private final int zzba;
    private final zzem zzbb;
    private final Class<?> zzbc;
    private final boolean zzbd;

    zzek(int i, zzem zzemVar, zzfb zzfbVar) {
        this.zzba = i;
        this.zzbb = zzemVar;
        this.zzaz = zzfbVar;
        switch (zzen.zza[zzemVar.ordinal()]) {
            case 1:
                this.zzbc = zzfbVar.zza();
                break;
            case 2:
                this.zzbc = zzfbVar.zza();
                break;
            default:
                this.zzbc = null;
                break;
        }
        boolean z = false;
        if (zzemVar == zzem.SCALAR) {
            switch (zzen.zzb[zzfbVar.ordinal()]) {
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
        zzek[] values = values();
        zzbe = new zzek[values.length];
        for (zzek zzekVar : values) {
            zzbe[zzekVar.zzba] = zzekVar;
        }
    }
}
