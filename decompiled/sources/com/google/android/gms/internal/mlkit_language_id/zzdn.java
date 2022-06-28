package com.google.android.gms.internal.mlkit_language_id;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public abstract class zzdn implements Serializable, Iterable<Byte> {
    public static final zzdn zza = new zzdx(zzeq.zzb);
    private static final zzdt zzb;
    private static final Comparator<zzdn> zzd;
    private int zzc = 0;

    public abstract boolean equals(Object obj);

    public abstract byte zza(int i);

    public abstract int zza();

    protected abstract int zza(int i, int i2, int i3);

    public abstract zzdn zza(int i, int i2);

    protected abstract String zza(Charset charset);

    public abstract void zza(zzdk zzdkVar) throws IOException;

    public abstract byte zzb(int i);

    public abstract boolean zzc();

    public static int zzb(byte b) {
        return b & 255;
    }

    public static zzdn zza(String str) {
        return new zzdx(str.getBytes(zzeq.zza));
    }

    public final String zzb() {
        return zza() == 0 ? "" : zza(zzeq.zza);
    }

    public final int hashCode() {
        int i = this.zzc;
        if (i == 0) {
            int zza2 = zza();
            i = zza(zza2, 0, zza2);
            if (i == 0) {
                i = 1;
            }
            this.zzc = i;
        }
        return i;
    }

    public static zzdv zzc(int i) {
        return new zzdv(i, null);
    }

    public final int zzd() {
        return this.zzc;
    }

    public static int zzb(int i, int i2, int i3) {
        int i4 = i2 - i;
        if ((i | i2 | i4 | (i3 - i2)) < 0) {
            if (i < 0) {
                StringBuilder sb = new StringBuilder(32);
                sb.append("Beginning index: ");
                sb.append(i);
                sb.append(" < 0");
                throw new IndexOutOfBoundsException(sb.toString());
            } else if (i2 < i) {
                StringBuilder sb2 = new StringBuilder(66);
                sb2.append("Beginning index larger than ending index: ");
                sb2.append(i);
                sb2.append(", ");
                sb2.append(i2);
                throw new IndexOutOfBoundsException(sb2.toString());
            } else {
                StringBuilder sb3 = new StringBuilder(37);
                sb3.append("End index: ");
                sb3.append(i2);
                sb3.append(" >= ");
                sb3.append(i3);
                throw new IndexOutOfBoundsException(sb3.toString());
            }
        }
        return i4;
    }

    public final String toString() {
        Locale locale = Locale.ROOT;
        Object[] objArr = new Object[3];
        objArr[0] = Integer.toHexString(System.identityHashCode(this));
        objArr[1] = Integer.valueOf(zza());
        objArr[2] = zza() <= 50 ? zzhd.zza(this) : String.valueOf(zzhd.zza(zza(0, 47))).concat("...");
        return String.format(locale, "<ByteString@%s size=%d contents=\"%s\">", objArr);
    }

    @Override // java.lang.Iterable
    public /* synthetic */ Iterator<Byte> iterator() {
        return new zzdm(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.google.android.gms.internal.mlkit_language_id.zzdr] */
    /* JADX WARN: Type inference failed for: r0v5, types: [com.google.android.gms.internal.mlkit_language_id.zzdw] */
    static {
        zzb = zzdl.zza() ? new zzdw(null) : new zzdr(null);
        zzd = new zzdp();
    }
}
