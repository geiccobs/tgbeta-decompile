package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzdh implements zzcu {
    private final zzcx zza;
    private final String zzb;
    private final Object[] zzc;
    private final int zzd;

    public zzdh(zzcx zzcxVar, String str, Object[] objArr) {
        char c;
        Throwable e;
        String str2;
        this.zza = zzcxVar;
        this.zzb = str;
        this.zzc = objArr;
        int i = 1;
        try {
            c = str.charAt(0);
        } catch (StringIndexOutOfBoundsException e2) {
            char[] charArray = str.toCharArray();
            String str3 = new String(charArray);
            try {
                c = str3.charAt(0);
                str = str3;
            } catch (StringIndexOutOfBoundsException e3) {
                try {
                    char[] cArr = new char[str3.length()];
                    str3.getChars(0, str3.length(), cArr, 0);
                    str2 = new String(cArr);
                } catch (ArrayIndexOutOfBoundsException e4) {
                    e = e4;
                } catch (StringIndexOutOfBoundsException e5) {
                    e = e5;
                }
                try {
                    c = str2.charAt(0);
                    str = str2;
                } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e6) {
                    e = e6;
                    str3 = str2;
                    throw new IllegalStateException(String.format("Failed parsing '%s' with charArray.length of %d", str3, Integer.valueOf(charArray.length)), e);
                }
            }
        }
        if (c < 55296) {
            this.zzd = c;
            return;
        }
        int i2 = c & 8191;
        int i3 = 13;
        while (true) {
            int i4 = i + 1;
            char charAt = str.charAt(i);
            if (charAt < 55296) {
                this.zzd = (charAt << i3) | i2;
                return;
            }
            i2 |= (charAt & 8191) << i3;
            i3 += 13;
            i = i4;
        }
    }

    @Override // com.google.android.gms.internal.wearable.zzcu
    public final boolean zza() {
        return (this.zzd & 2) == 2;
    }

    @Override // com.google.android.gms.internal.wearable.zzcu
    public final zzcx zzb() {
        return this.zza;
    }

    @Override // com.google.android.gms.internal.wearable.zzcu
    public final int zzc() {
        return (this.zzd & 1) == 1 ? 1 : 2;
    }

    public final String zzd() {
        return this.zzb;
    }

    public final Object[] zze() {
        return this.zzc;
    }
}
