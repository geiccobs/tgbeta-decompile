package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzo extends zzjb<zzfi$zzo, zza> implements zzkm {
    private static final zzfi$zzo zzi;
    private static volatile zzkx<zzfi$zzo> zzj;
    private int zzc;
    private zzfi$zze zzd;
    private zzfi$zzk zze;
    private zzfi$zzi zzf;
    private int zzg;
    private boolean zzh;

    private zzfi$zzo() {
    }

    public final void zza(zzfi$zzi zzfi_zzi) {
        zzfi_zzi.getClass();
        this.zzf = zzfi_zzi;
        this.zzc |= 4;
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zzo, zza> implements zzkm {
        private zza() {
            super(zzfi$zzo.zzi);
        }

        public final zza zza(zzfi$zzi zzfi_zzi) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzo) this.zza).zza(zzfi_zzi);
            return this;
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    public static zza zza() {
        return zzi.zzj();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzjb$zza, com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzo>] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzo> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzo();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004င\u0003\u0005ဇ\u0004", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh"});
            case 4:
                return zzi;
            case 5:
                zzkx<zzfi$zzo> zzkxVar2 = zzj;
                zzkx<zzfi$zzo> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzo.class) {
                        zzkx<zzfi$zzo> zzkxVar4 = zzj;
                        zzkxVar = zzkxVar4;
                        if (zzkxVar4 == null) {
                            ?? zzaVar = new zzjb.zza(zzi);
                            zzj = zzaVar;
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
        zzfi$zzo zzfi_zzo = new zzfi$zzo();
        zzi = zzfi_zzo;
        zzjb.zza(zzfi$zzo.class, zzfi_zzo);
    }
}
