package com.google.android.gms.internal.icing;

import java.io.IOException;
import java.nio.charset.Charset;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class zzcd extends zzcc {
    protected final byte[] zza;

    public zzcd(byte[] bArr) {
        if (bArr != null) {
            this.zza = bArr;
            return;
        }
        throw null;
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzcf) || zzc() != ((zzcf) obj).zzc()) {
            return false;
        }
        if (zzc() == 0) {
            return true;
        }
        if (obj instanceof zzcd) {
            zzcd zzcdVar = (zzcd) obj;
            int zzl = zzl();
            int zzl2 = zzcdVar.zzl();
            if (zzl != 0 && zzl2 != 0 && zzl != zzl2) {
                return false;
            }
            int zzc = zzc();
            if (zzc > zzcdVar.zzc()) {
                int zzc2 = zzc();
                StringBuilder sb = new StringBuilder(40);
                sb.append("Length too large: ");
                sb.append(zzc);
                sb.append(zzc2);
                throw new IllegalArgumentException(sb.toString());
            } else if (zzc > zzcdVar.zzc()) {
                int zzc3 = zzcdVar.zzc();
                StringBuilder sb2 = new StringBuilder(59);
                sb2.append("Ran off end of other: 0, ");
                sb2.append(zzc);
                sb2.append(", ");
                sb2.append(zzc3);
                throw new IllegalArgumentException(sb2.toString());
            } else if (zzcdVar instanceof zzcd) {
                byte[] bArr = this.zza;
                byte[] bArr2 = zzcdVar.zza;
                zzcdVar.zzd();
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
                return zzcdVar.zze(0, zzc).equals(zze(0, zzc));
            }
        }
        return obj.equals(this);
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public byte zza(int i) {
        return this.zza[i];
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public byte zzb(int i) {
        return this.zza[i];
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public int zzc() {
        return this.zza.length;
    }

    protected int zzd() {
        return 0;
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public final zzcf zze(int i, int i2) {
        zzm(0, i2, zzc());
        return i2 == 0 ? zzcf.zzb : new zzca(this.zza, 0, i2);
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public final void zzf(zzbw zzbwVar) throws IOException {
        ((zzck) zzbwVar).zzp(this.zza, 0, zzc());
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    protected final String zzg(Charset charset) {
        return new String(this.zza, 0, zzc(), charset);
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    public final boolean zzh() {
        return zzfr.zzb(this.zza, 0, zzc());
    }

    @Override // com.google.android.gms.internal.icing.zzcf
    protected final int zzi(int i, int i2, int i3) {
        return zzdh.zzh(i, this.zza, 0, i3);
    }
}
