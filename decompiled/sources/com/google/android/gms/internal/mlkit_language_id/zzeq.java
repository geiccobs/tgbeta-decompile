package com.google.android.gms.internal.mlkit_language_id;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzeq {
    static final Charset zza = Charset.forName("UTF-8");
    public static final byte[] zzb;

    public static int zza(long j) {
        return (int) (j ^ (j >>> 32));
    }

    public static int zza(boolean z) {
        return z ? 1231 : 1237;
    }

    public static <T> T zza(T t) {
        t.getClass();
        return t;
    }

    public static <T> T zza(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    public static boolean zza(byte[] bArr) {
        return zzhp.zza(bArr);
    }

    public static String zzb(byte[] bArr) {
        return new String(bArr, zza);
    }

    public static int zzc(byte[] bArr) {
        int length = bArr.length;
        int zza2 = zza(length, bArr, 0, length);
        if (zza2 == 0) {
            return 1;
        }
        return zza2;
    }

    public static int zza(int i, byte[] bArr, int i2, int i3) {
        for (int i4 = i2; i4 < i2 + i3; i4++) {
            i = (i * 31) + bArr[i4];
        }
        return i;
    }

    public static boolean zza(zzfz zzfzVar) {
        if (zzfzVar instanceof zzdg) {
            zzdg zzdgVar = (zzdg) zzfzVar;
            return false;
        }
        return false;
    }

    public static Object zza(Object obj, Object obj2) {
        return ((zzfz) obj).zzm().zza((zzfz) obj2).zzf();
    }

    static {
        Charset.forName("ISO-8859-1");
        byte[] bArr = new byte[0];
        zzb = bArr;
        ByteBuffer.wrap(bArr);
        zzdz.zza(bArr, 0, bArr.length, false);
    }
}
