package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final class zzgm implements zzfx {
    private final zzfz zza;
    private final String zzb;
    private final Object[] zzc;
    private final int zzd;

    public zzgm(zzfz zzfzVar, String str, Object[] objArr) {
        this.zza = zzfzVar;
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
            if (charAt2 < 55296) {
                this.zzd = i | (charAt2 << i2);
                return;
            }
            i |= (charAt2 & 8191) << i2;
            i2 += 13;
            i3 = i4;
        }
    }

    public final String zzd() {
        return this.zzb;
    }

    public final Object[] zze() {
        return this.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfx
    public final zzfz zzc() {
        return this.zza;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfx
    public final int zza() {
        return (this.zzd & 1) == 1 ? zzgl.zza : zzgl.zzb;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfx
    public final boolean zzb() {
        return (this.zzd & 2) == 2;
    }
}
