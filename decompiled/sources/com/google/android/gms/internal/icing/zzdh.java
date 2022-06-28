package com.google.android.gms.internal.icing;

import com.google.android.exoplayer2.C;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzdh {
    static final Charset zza = Charset.forName("UTF-8");
    static final Charset zzb = Charset.forName(C.ISO88591_NAME);
    public static final byte[] zzc;
    public static final ByteBuffer zzd;
    public static final zzci zze;

    static {
        byte[] bArr = new byte[0];
        zzc = bArr;
        zzd = ByteBuffer.wrap(bArr);
        zzch zzchVar = new zzch(bArr, 0, 0, false, null);
        try {
            zzchVar.zza(0);
            zze = zzchVar;
        } catch (zzdj e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T zza(T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }

    public static <T> T zzb(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    public static boolean zzc(byte[] bArr) {
        return zzfr.zza(bArr);
    }

    public static String zzd(byte[] bArr) {
        return new String(bArr, zza);
    }

    public static int zze(long j) {
        return (int) (j ^ (j >>> 32));
    }

    public static int zzf(boolean z) {
        return z ? 1231 : 1237;
    }

    public static int zzg(byte[] bArr) {
        int length = bArr.length;
        int zzh = zzh(length, bArr, 0, length);
        if (zzh == 0) {
            return 1;
        }
        return zzh;
    }

    public static int zzh(int i, byte[] bArr, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            i = (i * 31) + bArr[i4];
        }
        return i;
    }

    public static Object zzi(Object obj, Object obj2) {
        return ((zzee) obj).zzy().zzf((zzee) obj2).zzl();
    }
}
