package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class zzcf {
    private static final zzbg zzb = zzbg.zza();
    protected volatile zzcx zza;
    private volatile zzau zzc;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzcf)) {
            return false;
        }
        zzcf zzcfVar = (zzcf) obj;
        zzcx zzcxVar = this.zza;
        zzcx zzcxVar2 = zzcfVar.zza;
        if (zzcxVar != null || zzcxVar2 != null) {
            if (zzcxVar == null || zzcxVar2 == null) {
                if (zzcxVar != null) {
                    zzcfVar.zzc(zzcxVar.zzac());
                    return zzcxVar.equals(zzcfVar.zza);
                }
                zzc(zzcxVar2.zzac());
                return this.zza.equals(zzcxVar2);
            }
            return zzcxVar.equals(zzcxVar2);
        }
        return zzb().equals(zzcfVar.zzb());
    }

    public int hashCode() {
        return 1;
    }

    public final int zza() {
        if (this.zzc != null) {
            return ((zzas) this.zzc).zza.length;
        }
        if (this.zza == null) {
            return 0;
        }
        return this.zza.zzP();
    }

    public final zzau zzb() {
        if (this.zzc != null) {
            return this.zzc;
        }
        synchronized (this) {
            if (this.zzc != null) {
                return this.zzc;
            }
            if (this.zza == null) {
                this.zzc = zzau.zzb;
            } else {
                this.zzc = this.zza.zzH();
            }
            return this.zzc;
        }
    }

    protected final void zzc(zzcx zzcxVar) {
        if (this.zza != null) {
            return;
        }
        synchronized (this) {
            if (this.zza != null) {
                return;
            }
            try {
                this.zza = zzcxVar;
                this.zzc = zzau.zzb;
            } catch (zzcc e) {
                this.zza = zzcxVar;
                this.zzc = zzau.zzb;
            }
        }
    }
}
