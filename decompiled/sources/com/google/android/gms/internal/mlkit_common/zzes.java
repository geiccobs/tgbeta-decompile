package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzes extends zzez {
    private final int zzc;
    private final int zzd;

    public zzes(byte[] bArr, int i, int i2) {
        super(bArr);
        zzb(i, i + i2, bArr.length);
        this.zzc = i;
        this.zzd = i2;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzez, com.google.android.gms.internal.mlkit_common.zzep
    public final byte zza(int i) {
        int zza = zza();
        if (((zza - (i + 1)) | i) < 0) {
            if (i < 0) {
                StringBuilder sb = new StringBuilder(22);
                sb.append("Index < 0: ");
                sb.append(i);
                throw new ArrayIndexOutOfBoundsException(sb.toString());
            }
            StringBuilder sb2 = new StringBuilder(40);
            sb2.append("Index > length: ");
            sb2.append(i);
            sb2.append(", ");
            sb2.append(zza);
            throw new ArrayIndexOutOfBoundsException(sb2.toString());
        }
        return this.zzb[this.zzc + i];
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzez, com.google.android.gms.internal.mlkit_common.zzep
    public final byte zzb(int i) {
        return this.zzb[this.zzc + i];
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzez, com.google.android.gms.internal.mlkit_common.zzep
    public final int zza() {
        return this.zzd;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzez
    protected final int zze() {
        return this.zzc;
    }
}
