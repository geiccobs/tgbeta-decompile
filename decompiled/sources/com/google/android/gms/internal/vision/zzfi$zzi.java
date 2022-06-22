package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzi extends zzjb<zzfi$zzi, zza> implements zzkm {
    private static final zzfi$zzi zzg;
    private static volatile zzkx<zzfi$zzi> zzh;
    private int zzc;
    private zzfi$zzj zzd;
    private zzfi$zzl zze;
    private zzjl<zzfi$zzf> zzf = zzjb.zzo();

    private zzfi$zzi() {
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zzi, zza> implements zzkm {
        private zza() {
            super(zzfi$zzi.zzg);
        }

        public final zza zza(zzfi$zzj zzfi_zzj) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzi) this.zza).zza(zzfi_zzj);
            return this;
        }

        public final zza zza(Iterable<? extends zzfi$zzf> iterable) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzi) this.zza).zza(iterable);
            return this;
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    public final void zza(zzfi$zzj zzfi_zzj) {
        zzfi_zzj.getClass();
        this.zzd = zzfi_zzj;
        this.zzc |= 1;
    }

    private final void zzc() {
        zzjl<zzfi$zzf> zzjlVar = this.zzf;
        if (!zzjlVar.zza()) {
            this.zzf = zzjb.zza(zzjlVar);
        }
    }

    public final void zza(Iterable<? extends zzfi$zzf> iterable) {
        zzc();
        zzhf.zza(iterable, this.zzf);
    }

    public static zza zza() {
        return zzg.zzj();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzi>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzi> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzi();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", zzfi$zzf.class});
            case 4:
                return zzg;
            case 5:
                zzkx<zzfi$zzi> zzkxVar2 = zzh;
                zzkx<zzfi$zzi> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzi.class) {
                        zzkx<zzfi$zzi> zzkxVar4 = zzh;
                        zzkxVar = zzkxVar4;
                        if (zzkxVar4 == null) {
                            ?? zzaVar = new zzjb.zza(zzg);
                            zzh = zzaVar;
                            zzkxVar = zzaVar;
                        }
                    }
                    zzkxVar3 = zzkxVar;
                }
                return zzkxVar3;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        zzfi$zzi zzfi_zzi = new zzfi$zzi();
        zzg = zzfi_zzi;
        zzjb.zza(zzfi$zzi.class, zzfi_zzi);
    }
}
