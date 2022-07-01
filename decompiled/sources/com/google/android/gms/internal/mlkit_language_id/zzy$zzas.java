package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzas extends zzeo<zzy$zzas, zza> implements zzgb {
    private static final zzex<Integer, zzai> zzf = new zzbp();
    private static final zzy$zzas zzj;
    private static volatile zzgj<zzy$zzas> zzk;
    private int zzc;
    private zzy$zzat zzd;
    private long zzg;
    private long zzh;
    private zzeu zze = zzeo.zzk();
    private zzew<zzy$zzbf> zzi = zzeo.zzl();

    private zzy$zzas() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzas, zza> implements zzgb {
        private zza() {
            super(zzy$zzas.zzj);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzas>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzas> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzas();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzj, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဉ\u0000\u0002\u001e\u0003ဃ\u0001\u0004ဃ\u0002\u0005\u001b", new Object[]{"zzc", "zzd", "zze", zzai.zzb(), "zzg", "zzh", "zzi", zzy$zzbf.class});
            case 4:
                return zzj;
            case 5:
                zzgj<zzy$zzas> zzgjVar2 = zzk;
                zzgj<zzy$zzas> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzas.class) {
                        zzgj<zzy$zzas> zzgjVar4 = zzk;
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

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzai>, com.google.android.gms.internal.mlkit_language_id.zzbp] */
    static {
        zzy$zzas zzy_zzas = new zzy$zzas();
        zzj = zzy_zzas;
        zzeo.zza(zzy$zzas.class, zzy_zzas);
    }
}
