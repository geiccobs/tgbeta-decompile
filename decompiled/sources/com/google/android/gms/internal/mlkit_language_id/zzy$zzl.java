package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzl extends zzeo<zzy$zzl, zza> implements zzgb {
    private static final zzex<Integer, zzai> zzg = new zzab();
    private static final zzy$zzl zzi;
    private static volatile zzgj<zzy$zzl> zzj;
    private int zzc;
    private zzy$zzam zzd;
    private zzy$zzam zze;
    private zzeu zzf = zzeo.zzk();
    private long zzh;

    private zzy$zzl() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzl, zza> implements zzgb {
        private zza() {
            super(zzy$zzl.zzi);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzl>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzl> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzl();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzi, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001e\u0004ဃ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf", zzai.zzb(), "zzh"});
            case 4:
                return zzi;
            case 5:
                zzgj<zzy$zzl> zzgjVar2 = zzj;
                zzgj<zzy$zzl> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzl.class) {
                        zzgj<zzy$zzl> zzgjVar4 = zzj;
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

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzai>, com.google.android.gms.internal.mlkit_language_id.zzab] */
    static {
        zzy$zzl zzy_zzl = new zzy$zzl();
        zzi = zzy_zzl;
        zzeo.zza(zzy$zzl.class, zzy_zzl);
    }
}
