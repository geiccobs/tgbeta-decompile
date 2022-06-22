package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzay extends zzeo<zzy$zzay, zza> implements zzgb {
    private static final zzy$zzay zzi;
    private static volatile zzgj<zzy$zzay> zzj;
    private int zzc;
    private zzy$zzaw zzd;
    private int zze;
    private long zzf;
    private long zzg;
    private zzew<zzy$zzbf> zzh = zzeo.zzl();

    private zzy$zzay() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzay, zza> implements zzgb {
        private zza() {
            super(zzy$zzay.zzi);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzay>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzay> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzay();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဌ\u0001\u0003ဃ\u0002\u0004ဃ\u0003\u0005\u001b", new Object[]{"zzc", "zzd", "zze", zzai.zzb(), "zzf", "zzg", "zzh", zzy$zzbf.class});
            case 4:
                return zzi;
            case 5:
                zzgj<zzy$zzay> zzgjVar2 = zzj;
                zzgj<zzy$zzay> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzay.class) {
                        zzgj<zzy$zzay> zzgjVar4 = zzj;
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
        zzy$zzay zzy_zzay = new zzy$zzay();
        zzi = zzy_zzay;
        zzeo.zza(zzy$zzay.class, zzy_zzay);
    }
}
