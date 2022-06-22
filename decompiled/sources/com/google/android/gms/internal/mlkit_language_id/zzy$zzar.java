package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzar extends zzeo<zzy$zzar, zza> implements zzgb {
    private static final zzy$zzar zzi;
    private static volatile zzgj<zzy$zzar> zzj;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzat zze;
    private zzy$zzae zzf;
    private int zzg;
    private float zzh;

    private zzy$zzar() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzar, zza> implements zzgb {
        private zza() {
            super(zzy$zzar.zzi);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzar>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzar> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzar();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဋ\u0003\u0005ခ\u0004", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh"});
            case 4:
                return zzi;
            case 5:
                zzgj<zzy$zzar> zzgjVar2 = zzj;
                zzgj<zzy$zzar> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzar.class) {
                        zzgj<zzy$zzar> zzgjVar4 = zzj;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzi);
                            zzj = zzaVar;
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
        zzy$zzar zzy_zzar = new zzy$zzar();
        zzi = zzy_zzar;
        zzeo.zza(zzy$zzar.class, zzy_zzar);
    }
}
