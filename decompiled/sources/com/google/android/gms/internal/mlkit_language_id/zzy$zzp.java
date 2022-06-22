package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzp extends zzeo<zzy$zzp, zza> implements zzgb {
    private static final zzy$zzp zzg;
    private static volatile zzgj<zzy$zzp> zzh;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzn zze;
    private zzy$zzae zzf;

    private zzy$zzp() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzp, zza> implements zzgb {
        private zza() {
            super(zzy$zzp.zzg);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzp>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzp> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzp();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzp> zzgjVar2 = zzh;
                zzgj<zzy$zzp> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzp.class) {
                        zzgj<zzy$zzp> zzgjVar4 = zzh;
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
        zzy$zzp zzy_zzp = new zzy$zzp();
        zzg = zzy_zzp;
        zzeo.zza(zzy$zzp.class, zzy_zzp);
    }
}
