package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzb extends zzjb<zzfi$zzb, zza> implements zzkm {
    private static final zzji<Integer, zzgz> zzd = new zzfl();
    private static final zzfi$zzb zze;
    private static volatile zzkx<zzfi$zzb> zzf;
    private zzjj zzc = zzjb.zzn();

    private zzfi$zzb() {
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zzb, zza> implements zzkm {
        private zza() {
            super(zzfi$zzb.zze);
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzb>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzb> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzb();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zze, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001e", new Object[]{"zzc", zzgz.zzb()});
            case 4:
                return zze;
            case 5:
                zzkx<zzfi$zzb> zzkxVar2 = zzf;
                zzkx<zzfi$zzb> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzb.class) {
                        zzkx<zzfi$zzb> zzkxVar4 = zzf;
                        zzkxVar = zzkxVar4;
                        if (zzkxVar4 == null) {
                            ?? zzaVar = new zzjb.zza(zze);
                            zzf = zzaVar;
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

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.vision.zzji<java.lang.Integer, com.google.android.gms.internal.vision.zzgz>, com.google.android.gms.internal.vision.zzfl] */
    static {
        zzfi$zzb zzfi_zzb = new zzfi$zzb();
        zze = zzfi_zzb;
        zzjb.zza(zzfi$zzb.class, zzfi_zzb);
    }
}
