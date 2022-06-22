package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzal;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzah extends zzeo<zzy$zzah, zza> implements zzgb {
    private static final zzy$zzah zzg;
    private static volatile zzgj<zzy$zzah> zzh;
    private int zzc;
    private int zzd;
    private boolean zze;
    private String zzf = "";

    private zzy$zzah() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzah, zza> implements zzgb {
        private zza() {
            super(zzy$zzah.zzg);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzah>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzah> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzah();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဈ\u0002", new Object[]{"zzc", "zzd", zzy$zzal.zzb.zzb(), "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzah> zzgjVar2 = zzh;
                zzgj<zzy$zzah> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzah.class) {
                        zzgj<zzy$zzah> zzgjVar4 = zzh;
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
        zzy$zzah zzy_zzah = new zzy$zzah();
        zzg = zzy_zzah;
        zzeo.zza(zzy$zzah.class, zzy_zzah);
    }
}
