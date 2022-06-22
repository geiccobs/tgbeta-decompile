package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzj extends zzeo<zzy$zzj, zza> implements zzgb {
    private static final zzy$zzj zze;
    private static volatile zzgj<zzy$zzj> zzf;
    private int zzc;
    private int zzd;

    private zzy$zzj() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzj, zza> implements zzgb {
        private zza() {
            super(zzy$zzj.zze);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzj>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzj> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzj();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ဌ\u0000", new Object[]{"zzc", "zzd", zzai.zzb()});
            case 4:
                return zze;
            case 5:
                zzgj<zzy$zzj> zzgjVar2 = zzf;
                zzgj<zzy$zzj> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzj.class) {
                        zzgj<zzy$zzj> zzgjVar4 = zzf;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zze);
                            zzf = zzaVar;
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
        zzy$zzj zzy_zzj = new zzy$zzj();
        zze = zzy_zzj;
        zzeo.zza(zzy$zzj.class, zzy_zzj);
    }
}
