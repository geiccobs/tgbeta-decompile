package com.google.android.gms.internal.clearcut;

import java.io.IOException;
/* loaded from: classes.dex */
public final class zzhb extends zzfu<zzhb> implements Cloneable {
    private static volatile zzhb[] zzbkd;
    private String zzbke = "";
    private String value = "";

    public zzhb() {
        this.zzrj = null;
        this.zzrs = -1;
    }

    public static zzhb[] zzge() {
        if (zzbkd == null) {
            synchronized (zzfy.zzrr) {
                if (zzbkd == null) {
                    zzbkd = new zzhb[0];
                }
            }
        }
        return zzbkd;
    }

    /* renamed from: zzgf */
    public final zzhb clone() {
        try {
            return (zzhb) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzhb)) {
            return false;
        }
        zzhb zzhbVar = (zzhb) obj;
        String str = this.zzbke;
        if (str == null) {
            if (zzhbVar.zzbke != null) {
                return false;
            }
        } else if (!str.equals(zzhbVar.zzbke)) {
            return false;
        }
        String str2 = this.value;
        if (str2 == null) {
            if (zzhbVar.value != null) {
                return false;
            }
        } else if (!str2.equals(zzhbVar.value)) {
            return false;
        }
        zzfw zzfwVar = this.zzrj;
        if (zzfwVar != null && !zzfwVar.isEmpty()) {
            return this.zzrj.equals(zzhbVar.zzrj);
        }
        zzfw zzfwVar2 = zzhbVar.zzrj;
        return zzfwVar2 == null || zzfwVar2.isEmpty();
    }

    public final int hashCode() {
        int hashCode = (zzhb.class.getName().hashCode() + 527) * 31;
        String str = this.zzbke;
        int i = 0;
        int hashCode2 = (hashCode + (str == null ? 0 : str.hashCode())) * 31;
        String str2 = this.value;
        int hashCode3 = (hashCode2 + (str2 == null ? 0 : str2.hashCode())) * 31;
        zzfw zzfwVar = this.zzrj;
        if (zzfwVar != null && !zzfwVar.isEmpty()) {
            i = this.zzrj.hashCode();
        }
        return hashCode3 + i;
    }

    @Override // com.google.android.gms.internal.clearcut.zzfu, com.google.android.gms.internal.clearcut.zzfz
    public final void zza(zzfs zzfsVar) throws IOException {
        String str = this.zzbke;
        if (str != null && !str.equals("")) {
            zzfsVar.zza(1, this.zzbke);
        }
        String str2 = this.value;
        if (str2 != null && !str2.equals("")) {
            zzfsVar.zza(2, this.value);
        }
        super.zza(zzfsVar);
    }

    @Override // com.google.android.gms.internal.clearcut.zzfu, com.google.android.gms.internal.clearcut.zzfz
    public final int zzen() {
        int zzen = super.zzen();
        String str = this.zzbke;
        if (str != null && !str.equals("")) {
            zzen += zzfs.zzb(1, this.zzbke);
        }
        String str2 = this.value;
        return (str2 == null || str2.equals("")) ? zzen : zzen + zzfs.zzb(2, this.value);
    }

    @Override // com.google.android.gms.internal.clearcut.zzfu
    public final /* synthetic */ zzhb zzeo() throws CloneNotSupportedException {
        return (zzhb) clone();
    }

    @Override // com.google.android.gms.internal.clearcut.zzfu, com.google.android.gms.internal.clearcut.zzfz
    public final /* synthetic */ zzfz zzep() throws CloneNotSupportedException {
        return (zzhb) clone();
    }
}
