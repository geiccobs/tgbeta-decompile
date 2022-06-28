package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.nio.charset.Charset;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public class zzid extends zzia {
    protected final byte[] zzb;

    public zzid(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException();
        }
        this.zzb = bArr;
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public byte zza(int i) {
        return this.zzb[i];
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public byte zzb(int i) {
        return this.zzb[i];
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public int zza() {
        return this.zzb.length;
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public final zzht zza(int i, int i2) {
        int zzb = zzb(0, i2, zza());
        if (zzb == 0) {
            return zzht.zza;
        }
        return new zzhw(this.zzb, zze(), zzb);
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public void zza(byte[] bArr, int i, int i2, int i3) {
        System.arraycopy(this.zzb, 0, bArr, 0, i3);
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public final void zza(zzhq zzhqVar) throws IOException {
        zzhqVar.zza(this.zzb, zze(), zza());
    }

    @Override // com.google.android.gms.internal.vision.zzht
    protected final String zza(Charset charset) {
        return new String(this.zzb, zze(), zza(), charset);
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public final boolean zzc() {
        int zze = zze();
        return zzmd.zza(this.zzb, zze, zza() + zze);
    }

    @Override // com.google.android.gms.internal.vision.zzht
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzht) || zza() != ((zzht) obj).zza()) {
            return false;
        }
        if (zza() == 0) {
            return true;
        }
        if (obj instanceof zzid) {
            zzid zzidVar = (zzid) obj;
            int zzd = zzd();
            int zzd2 = zzidVar.zzd();
            if (zzd != 0 && zzd2 != 0 && zzd != zzd2) {
                return false;
            }
            return zza(zzidVar, 0, zza());
        }
        return obj.equals(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.vision.zzia
    public final boolean zza(zzht zzhtVar, int i, int i2) {
        if (i2 > zzhtVar.zza()) {
            int zza = zza();
            StringBuilder sb = new StringBuilder(40);
            sb.append("Length too large: ");
            sb.append(i2);
            sb.append(zza);
            throw new IllegalArgumentException(sb.toString());
        } else if (i2 > zzhtVar.zza()) {
            int zza2 = zzhtVar.zza();
            StringBuilder sb2 = new StringBuilder(59);
            sb2.append("Ran off end of other: 0, ");
            sb2.append(i2);
            sb2.append(", ");
            sb2.append(zza2);
            throw new IllegalArgumentException(sb2.toString());
        } else if (zzhtVar instanceof zzid) {
            zzid zzidVar = (zzid) zzhtVar;
            byte[] bArr = this.zzb;
            byte[] bArr2 = zzidVar.zzb;
            int zze = zze() + i2;
            int zze2 = zze();
            int zze3 = zzidVar.zze();
            while (zze2 < zze) {
                if (bArr[zze2] != bArr2[zze3]) {
                    return false;
                }
                zze2++;
                zze3++;
            }
            return true;
        } else {
            return zzhtVar.zza(0, i2).equals(zza(0, i2));
        }
    }

    @Override // com.google.android.gms.internal.vision.zzht
    protected final int zza(int i, int i2, int i3) {
        return zzjf.zza(i, this.zzb, zze(), i3);
    }

    public int zze() {
        return 0;
    }
}
