package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzal;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzaa extends zzeo<zzy$zzaa, zza> implements zzgb {
    private static final zzy$zzaa zzg;
    private static volatile zzgj<zzy$zzaa> zzh;
    private int zzc;
    private int zzd;
    private boolean zze;
    private String zzf = "";

    private zzy$zzaa() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzaa, zza> implements zzgb {
        private zza() {
            super(zzy$zzaa.zzg);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzaa>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzaa> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzaa();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဈ\u0002", new Object[]{"zzc", "zzd", zzy$zzal.zzb.zzb(), "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzaa> zzgjVar2 = zzh;
                zzgj<zzy$zzaa> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzaa.class) {
                        zzgj<zzy$zzaa> zzgjVar4 = zzh;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzg);
                            zzh = zzaVar;
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
        zzy$zzaa zzy_zzaa = new zzy$zzaa();
        zzg = zzy_zzaa;
        zzeo.zza(zzy$zzaa.class, zzy_zzaa);
    }
}
