package com.google.android.gms.internal.clearcut;
/* loaded from: classes3.dex */
public class zzcv {
    private static final zzbt zzez = zzbt.zzan();
    private zzbb zzln;
    private volatile zzdo zzlo;
    private volatile zzbb zzlp;

    private final zzdo zzh(zzdo zzdoVar) {
        if (this.zzlo == null) {
            synchronized (this) {
                if (this.zzlo == null) {
                    try {
                        this.zzlo = zzdoVar;
                        this.zzlp = zzbb.zzfi;
                    } catch (zzco e) {
                        this.zzlo = zzdoVar;
                        this.zzlp = zzbb.zzfi;
                    }
                }
            }
        }
        return this.zzlo;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzcv)) {
            return false;
        }
        zzcv zzcvVar = (zzcv) obj;
        zzdo zzdoVar = this.zzlo;
        zzdo zzdoVar2 = zzcvVar.zzlo;
        return (zzdoVar == null && zzdoVar2 == null) ? zzr().equals(zzcvVar.zzr()) : (zzdoVar == null || zzdoVar2 == null) ? zzdoVar != null ? zzdoVar.equals(zzcvVar.zzh(zzdoVar.zzbe())) : zzh(zzdoVar2.zzbe()).equals(zzdoVar2) : zzdoVar.equals(zzdoVar2);
    }

    public int hashCode() {
        return 1;
    }

    public final int zzas() {
        if (this.zzlp != null) {
            return this.zzlp.size();
        }
        if (this.zzlo == null) {
            return 0;
        }
        return this.zzlo.zzas();
    }

    public final zzdo zzi(zzdo zzdoVar) {
        zzdo zzdoVar2 = this.zzlo;
        this.zzln = null;
        this.zzlp = null;
        this.zzlo = zzdoVar;
        return zzdoVar2;
    }

    public final zzbb zzr() {
        if (this.zzlp != null) {
            return this.zzlp;
        }
        synchronized (this) {
            if (this.zzlp != null) {
                return this.zzlp;
            }
            this.zzlp = this.zzlo == null ? zzbb.zzfi : this.zzlo.zzr();
            return this.zzlp;
        }
    }
}
