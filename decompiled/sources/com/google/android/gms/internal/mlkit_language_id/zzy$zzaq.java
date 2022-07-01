package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzaq extends zzeo<zzy$zzaq, zza> implements zzgb {
    private static final zzy$zzaq zzf;
    private static volatile zzgj<zzy$zzaq> zzg;
    private int zzc;
    private zzy$zzat zzd;
    private int zze;

    private zzy$zzaq() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzaq, zza> implements zzgb {
        private zza() {
            super(zzy$zzaq.zzf);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzaq>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzaq> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzaq();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဌ\u0001", new Object[]{"zzc", "zzd", "zze", zzai.zzb()});
            case 4:
                return zzf;
            case 5:
                zzgj<zzy$zzaq> zzgjVar2 = zzg;
                zzgj<zzy$zzaq> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzaq.class) {
                        zzgj<zzy$zzaq> zzgjVar4 = zzg;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzf);
                            zzg = zzaVar;
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
        zzy$zzaq zzy_zzaq = new zzy$zzaq();
        zzf = zzy_zzaq;
        zzeo.zza(zzy$zzaq.class, zzy_zzaq);
    }
}
