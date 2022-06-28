package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzho implements zzgz {
    private final zzhb zza;
    private final String zzb;
    private final Object[] zzc;
    private final int zzd;

    public zzho(zzhb zzhbVar, String str, Object[] objArr) {
        this.zza = zzhbVar;
        this.zzb = str;
        this.zzc = objArr;
        char charAt = str.charAt(0);
        if (charAt < 55296) {
            this.zzd = charAt;
            return;
        }
        int i = charAt & 8191;
        int i2 = 13;
        int i3 = 1;
        while (true) {
            int i4 = i3 + 1;
            char charAt2 = str.charAt(i3);
            if (charAt2 >= 55296) {
                i |= (charAt2 & 8191) << i2;
                i2 += 13;
                i3 = i4;
            } else {
                this.zzd = i | (charAt2 << i2);
                return;
            }
        }
    }

    public final String zzd() {
        return this.zzb;
    }

    public final Object[] zze() {
        return this.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgz
    public final zzhb zzc() {
        return this.zza;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgz
    public final int zza() {
        return (this.zzd & 1) == 1 ? zzhn.zza : zzhn.zzb;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgz
    public final boolean zzb() {
        return (this.zzd & 2) == 2;
    }
}
