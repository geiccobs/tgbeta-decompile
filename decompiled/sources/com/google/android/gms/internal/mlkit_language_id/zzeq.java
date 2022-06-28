package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.exoplayer2.C;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzeq {
    public static final byte[] zzb;
    private static final ByteBuffer zzd;
    private static final zzdz zze;
    static final Charset zza = Charset.forName("UTF-8");
    private static final Charset zzc = Charset.forName(C.ISO88591_NAME);

    public static <T> T zza(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    public static <T> T zza(T t, String str) {
        if (t == null) {
            throw new NullPointerException(str);
        }
        return t;
    }

    public static boolean zza(byte[] bArr) {
        return zzhp.zza(bArr);
    }

    public static String zzb(byte[] bArr) {
        return new String(bArr, zza);
    }

    public static int zza(long j) {
        return (int) (j ^ (j >>> 32));
    }

    public static int zza(boolean z) {
        return z ? 1231 : 1237;
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
        byte[] bArr = new byte[0];
        zzb = bArr;
        zzd = ByteBuffer.wrap(bArr);
        zze = zzdz.zza(bArr, 0, bArr.length, false);
    }
}
