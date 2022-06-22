package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzap extends zzeo<zzy$zzap, zza> implements zzgb {
    private static final zzy$zzap zzj;
    private static volatile zzgj<zzy$zzap> zzk;
    private int zzc;
    private zzy$zzaf zzd;
    private zzci$zzb zze;
    private zzy$zzae zzf;
    private zzy$zzac zzg;
    private int zzh;
    private int zzi;

    private zzy$zzap() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzap, zza> implements zzgb {
        private zza() {
            super(zzy$zzap.zzj);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzap>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzap> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzap();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဉ\u0003\u0005ဋ\u0004\u0006ဋ\u0005", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi"});
            case 4:
                return zzj;
            case 5:
                zzgj<zzy$zzap> zzgjVar2 = zzk;
                zzgj<zzy$zzap> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzap.class) {
                        zzgj<zzy$zzap> zzgjVar4 = zzk;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzj);
                            zzk = zzaVar;
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
        zzy$zzap zzy_zzap = new zzy$zzap();
        zzj = zzy_zzap;
        zzeo.zza(zzy$zzap.class, zzy_zzap);
    }
}
