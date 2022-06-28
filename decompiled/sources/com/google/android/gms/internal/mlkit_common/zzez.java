package com.google.android.gms.internal.mlkit_common;

import java.io.IOException;
import java.nio.charset.Charset;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class zzez extends zzew {
    protected final byte[] zzb;

    public zzez(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException();
        }
        this.zzb = bArr;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public byte zza(int i) {
        return this.zzb[i];
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public byte zzb(int i) {
        return this.zzb[i];
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public int zza() {
        return this.zzb.length;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public final zzep zza(int i, int i2) {
        int zzb = zzb(0, i2, zza());
        if (zzb == 0) {
            return zzep.zza;
        }
        return new zzes(this.zzb, zze(), zzb);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public final void zza(zzem zzemVar) throws IOException {
        zzemVar.zza(this.zzb, zze(), zza());
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    protected final String zza(Charset charset) {
        return new String(this.zzb, zze(), zza(), charset);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public final boolean zzc() {
        int zze = zze();
        return zzir.zza(this.zzb, zze, zza() + zze);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzep) || zza() != ((zzep) obj).zza()) {
            return false;
        }
        if (zza() == 0) {
            return true;
        }
        if (obj instanceof zzez) {
            zzez zzezVar = (zzez) obj;
            int zzd = zzd();
            int zzd2 = zzezVar.zzd();
            if (zzd != 0 && zzd2 != 0 && zzd != zzd2) {
                return false;
            }
            return zza(zzezVar, 0, zza());
        }
        return obj.equals(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.mlkit_common.zzew
    public final boolean zza(zzep zzepVar, int i, int i2) {
        if (i2 > zzepVar.zza()) {
            int zza = zza();
            StringBuilder sb = new StringBuilder(40);
            sb.append("Length too large: ");
            sb.append(i2);
            sb.append(zza);
            throw new IllegalArgumentException(sb.toString());
        } else if (i2 > zzepVar.zza()) {
            int zza2 = zzepVar.zza();
            StringBuilder sb2 = new StringBuilder(59);
            sb2.append("Ran off end of other: 0, ");
            sb2.append(i2);
            sb2.append(", ");
            sb2.append(zza2);
            throw new IllegalArgumentException(sb2.toString());
        } else if (zzepVar instanceof zzez) {
            zzez zzezVar = (zzez) zzepVar;
            byte[] bArr = this.zzb;
            byte[] bArr2 = zzezVar.zzb;
            int zze = zze() + i2;
            int zze2 = zze();
            int zze3 = zzezVar.zze();
            while (zze2 < zze) {
                if (bArr[zze2] != bArr2[zze3]) {
                    return false;
                }
                zze2++;
                zze3++;
            }
            return true;
        } else {
            return zzepVar.zza(0, i2).equals(zza(0, i2));
        }
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzep
    protected final int zza(int i, int i2, int i3) {
        return zzfs.zza(i, this.zzb, zze(), i3);
    }

    protected int zze() {
        return 0;
    }
}
