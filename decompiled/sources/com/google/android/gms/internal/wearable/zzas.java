package com.google.android.gms.internal.wearable;

import java.io.IOException;
import java.nio.charset.Charset;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class zzas extends zzar {
    protected final byte[] zza;

    public zzas(byte[] bArr) {
        if (bArr != null) {
            this.zza = bArr;
            return;
        }
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzau) || zzc() != ((zzau) obj).zzc()) {
            return false;
        }
        if (zzc() == 0) {
            return true;
        }
        if (obj instanceof zzas) {
            zzas zzasVar = (zzas) obj;
            int zzp = zzp();
            int zzp2 = zzasVar.zzp();
            if (zzp != 0 && zzp2 != 0 && zzp != zzp2) {
                return false;
            }
            int zzc = zzc();
            if (zzc > zzasVar.zzc()) {
                int zzc2 = zzc();
                StringBuilder sb = new StringBuilder(40);
                sb.append("Length too large: ");
                sb.append(zzc);
                sb.append(zzc2);
                throw new IllegalArgumentException(sb.toString());
            } else if (zzc > zzasVar.zzc()) {
                int zzc3 = zzasVar.zzc();
                StringBuilder sb2 = new StringBuilder(59);
                sb2.append("Ran off end of other: 0, ");
                sb2.append(zzc);
                sb2.append(", ");
                sb2.append(zzc3);
                throw new IllegalArgumentException(sb2.toString());
            } else if (zzasVar instanceof zzas) {
                byte[] bArr = this.zza;
                byte[] bArr2 = zzasVar.zza;
                zzasVar.zzd();
                int i = 0;
                int i2 = 0;
                while (i < zzc) {
                    if (bArr[i] != bArr2[i2]) {
                        return false;
                    }
                    i++;
                    i2++;
                }
                return true;
            } else {
                return zzasVar.zzf(0, zzc).equals(zzf(0, zzc));
            }
        }
        return obj.equals(this);
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public byte zza(int i) {
        return this.zza[i];
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public byte zzb(int i) {
        return this.zza[i];
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public int zzc() {
        return this.zza.length;
    }

    protected int zzd() {
        return 0;
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    protected void zze(byte[] bArr, int i, int i2, int i3) {
        System.arraycopy(this.zza, 0, bArr, 0, i3);
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public final zzau zzf(int i, int i2) {
        int zzq = zzq(0, i2, zzc());
        return zzq == 0 ? zzau.zzb : new zzap(this.zza, 0, zzq);
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public final void zzg(zzal zzalVar) throws IOException {
        ((zzaz) zzalVar).zzp(this.zza, 0, zzc());
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    protected final String zzh(Charset charset) {
        return new String(this.zza, 0, zzc(), charset);
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    public final boolean zzi() {
        return zzel.zzb(this.zza, 0, zzc());
    }

    @Override // com.google.android.gms.internal.wearable.zzau
    protected final int zzj(int i, int i2, int i3) {
        return zzca.zzh(i, this.zza, 0, i3);
    }
}
