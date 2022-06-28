package com.google.android.gms.internal.mlkit_language_id;

import java.io.IOException;
import java.nio.charset.Charset;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public class zzdx extends zzdu {
    protected final byte[] zzb;

    public zzdx(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException();
        }
        this.zzb = bArr;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public byte zza(int i) {
        return this.zzb[i];
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public byte zzb(int i) {
        return this.zzb[i];
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public int zza() {
        return this.zzb.length;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public final zzdn zza(int i, int i2) {
        int zzb = zzb(0, i2, zza());
        if (zzb == 0) {
            return zzdn.zza;
        }
        return new zzdq(this.zzb, zze(), zzb);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public final void zza(zzdk zzdkVar) throws IOException {
        zzdkVar.zza(this.zzb, zze(), zza());
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    protected final String zza(Charset charset) {
        return new String(this.zzb, zze(), zza(), charset);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public final boolean zzc() {
        int zze = zze();
        return zzhp.zza(this.zzb, zze, zza() + zze);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzdn) || zza() != ((zzdn) obj).zza()) {
            return false;
        }
        if (zza() == 0) {
            return true;
        }
        if (obj instanceof zzdx) {
            zzdx zzdxVar = (zzdx) obj;
            int zzd = zzd();
            int zzd2 = zzdxVar.zzd();
            if (zzd != 0 && zzd2 != 0 && zzd != zzd2) {
                return false;
            }
            return zza(zzdxVar, 0, zza());
        }
        return obj.equals(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzdu
    public final boolean zza(zzdn zzdnVar, int i, int i2) {
        if (i2 > zzdnVar.zza()) {
            int zza = zza();
            StringBuilder sb = new StringBuilder(40);
            sb.append("Length too large: ");
            sb.append(i2);
            sb.append(zza);
            throw new IllegalArgumentException(sb.toString());
        } else if (i2 > zzdnVar.zza()) {
            int zza2 = zzdnVar.zza();
            StringBuilder sb2 = new StringBuilder(59);
            sb2.append("Ran off end of other: 0, ");
            sb2.append(i2);
            sb2.append(", ");
            sb2.append(zza2);
            throw new IllegalArgumentException(sb2.toString());
        } else if (zzdnVar instanceof zzdx) {
            zzdx zzdxVar = (zzdx) zzdnVar;
            byte[] bArr = this.zzb;
            byte[] bArr2 = zzdxVar.zzb;
            int zze = zze() + i2;
            int zze2 = zze();
            int zze3 = zzdxVar.zze();
            while (zze2 < zze) {
                if (bArr[zze2] != bArr2[zze3]) {
                    return false;
                }
                zze2++;
                zze3++;
            }
            return true;
        } else {
            return zzdnVar.zza(0, i2).equals(zza(0, i2));
        }
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdn
    protected final int zza(int i, int i2, int i3) {
        return zzeq.zza(i, this.zzb, zze(), i3);
    }

    protected int zze() {
        return 0;
    }
}
