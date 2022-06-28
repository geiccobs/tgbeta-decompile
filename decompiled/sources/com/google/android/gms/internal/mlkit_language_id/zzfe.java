package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public class zzfe {
    private static final zzef zza = zzef.zza();
    private zzdn zzb;
    private volatile zzfz zzc;
    private volatile zzdn zzd;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzfe)) {
            return false;
        }
        zzfe zzfeVar = (zzfe) obj;
        zzfz zzfzVar = this.zzc;
        zzfz zzfzVar2 = zzfeVar.zzc;
        if (zzfzVar == null && zzfzVar2 == null) {
            return zzc().equals(zzfeVar.zzc());
        }
        if (zzfzVar != null && zzfzVar2 != null) {
            return zzfzVar.equals(zzfzVar2);
        }
        if (zzfzVar != null) {
            return zzfzVar.equals(zzfeVar.zzb(zzfzVar.zzn()));
        }
        return zzb(zzfzVar2.zzn()).equals(zzfzVar2);
    }

    public int hashCode() {
        return 1;
    }

    private final zzfz zzb(zzfz zzfzVar) {
        if (this.zzc == null) {
            synchronized (this) {
                if (this.zzc == null) {
                    try {
                        this.zzc = zzfzVar;
                        this.zzd = zzdn.zza;
                    } catch (zzez e) {
                        this.zzc = zzfzVar;
                        this.zzd = zzdn.zza;
                    }
                }
            }
        }
        return this.zzc;
    }

    public final zzfz zza(zzfz zzfzVar) {
        zzfz zzfzVar2 = this.zzc;
        this.zzb = null;
        this.zzd = null;
        this.zzc = zzfzVar;
        return zzfzVar2;
    }

    public final int zzb() {
        if (this.zzd != null) {
            return this.zzd.zza();
        }
        if (this.zzc != null) {
            return this.zzc.zzj();
        }
        return 0;
    }

    public final zzdn zzc() {
        if (this.zzd != null) {
            return this.zzd;
        }
        synchronized (this) {
            if (this.zzd != null) {
                return this.zzd;
            }
            if (this.zzc == null) {
                this.zzd = zzdn.zza;
            } else {
                this.zzd = this.zzc.zze();
            }
            return this.zzd;
        }
    }
}
