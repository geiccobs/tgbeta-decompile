package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzd extends zzjb<zzfi$zzd, zza> implements zzkm {
    private static final zzfi$zzd zzd;
    private static volatile zzkx<zzfi$zzd> zze;
    private zzjl<zzfi$zzm> zzc = zzjb.zzo();

    private zzfi$zzd() {
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzd>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzd> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzd();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzc", zzfi$zzm.class});
            case 4:
                return zzd;
            case 5:
                zzkx<zzfi$zzd> zzkxVar2 = zze;
                zzkx<zzfi$zzd> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzd.class) {
                        zzkx<zzfi$zzd> zzkxVar4 = zze;
                        zzkxVar = zzkxVar4;
                        if (zzkxVar4 == null) {
                            ?? zzaVar = new zzjb.zza(zzd);
                            zze = zzaVar;
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

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zzd, zza> implements zzkm {
        private zza() {
            super(zzfi$zzd.zzd);
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    static {
        zzfi$zzd zzfi_zzd = new zzfi$zzd();
        zzd = zzfi_zzd;
        zzjb.zza(zzfi$zzd.class, zzfi_zzd);
    }
}
