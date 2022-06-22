package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzc extends zzjb<zzfi$zzc, zza> implements zzkm {
    private static final zzfi$zzc zzg;
    private static volatile zzkx<zzfi$zzc> zzh;
    private int zzc;
    private int zzd;
    private int zze;
    private String zzf = "";

    private zzfi$zzc() {
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zzc, zza> implements zzkm {
        private zza() {
            super(zzfi$zzc.zzg);
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzc>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzc> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzc();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဈ\u0002", new Object[]{"zzc", "zzd", zzgz.zzb(), "zze", zzha.zzb(), "zzf"});
            case 4:
                return zzg;
            case 5:
                zzkx<zzfi$zzc> zzkxVar2 = zzh;
                zzkx<zzfi$zzc> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzc.class) {
                        zzkx<zzfi$zzc> zzkxVar4 = zzh;
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
        zzfi$zzc zzfi_zzc = new zzfi$zzc();
        zzg = zzfi_zzc;
        zzjb.zza(zzfi$zzc.class, zzfi_zzc);
    }
}
