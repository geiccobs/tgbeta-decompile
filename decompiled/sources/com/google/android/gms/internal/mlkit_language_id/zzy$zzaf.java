package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzaf extends zzeo<zzy$zzaf, zza> implements zzgb {
    private static final zzy$zzaf zzl;
    private static volatile zzgj<zzy$zzaf> zzm;
    private int zzc;
    private long zzd;
    private int zze;
    private boolean zzf;
    private boolean zzg;
    private boolean zzh;
    private boolean zzi;
    private int zzj;
    private zzew<zzy$zzbf> zzk = zzeo.zzl();

    private zzy$zzaf() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzaf, zza> implements zzgb {
        private zza() {
            super(zzy$zzaf.zzl);
        }

        public final zza zza(long j) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzaf) this.zza).zza(j);
            return this;
        }

        public final zza zza(zzai zzaiVar) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzaf) this.zza).zza(zzaiVar);
            return this;
        }

        public final zza zza(boolean z) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzaf) this.zza).zza(z);
            return this;
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    public final void zza(long j) {
        this.zzc |= 1;
        this.zzd = j;
    }

    public final void zza(zzai zzaiVar) {
        this.zze = zzaiVar.zza();
        this.zzc |= 2;
    }

    public final void zza(boolean z) {
        this.zzc |= 4;
        this.zzf = z;
    }

    public static zza zza() {
        return zzl.zzh();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzaf>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzaf> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzaf();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0001\u0000\u0001ဃ\u0000\u0002ဌ\u0001\u0003ဇ\u0002\u0004ဇ\u0003\u0005ဇ\u0004\u0006ဇ\u0005\u0007ဋ\u0006\b\u001b", new Object[]{"zzc", "zzd", "zze", zzai.zzb(), "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", zzy$zzbf.class});
            case 4:
                return zzl;
            case 5:
                zzgj<zzy$zzaf> zzgjVar2 = zzm;
                zzgj<zzy$zzaf> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzaf.class) {
                        zzgj<zzy$zzaf> zzgjVar4 = zzm;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzl);
                            zzm = zzaVar;
                            zzgjVar = zzaVar;
                        }
                    }
                    zzgjVar3 = zzgjVar;
                }
                return zzgjVar3;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        zzy$zzaf zzy_zzaf = new zzy$zzaf();
        zzl = zzy_zzaf;
        zzeo.zza(zzy$zzaf.class, zzy_zzaf);
    }
}
