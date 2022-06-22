package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzbd extends zzeo<zzy$zzbd, zza> implements zzgb {
    private static final zzy$zzbd zzf;
    private static volatile zzgj<zzy$zzbd> zzg;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzae zze;

    private zzy$zzbd() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzbd, zza> implements zzgb {
        private zza() {
            super(zzy$zzbd.zzf);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbd>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzbd> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzbd();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001", new Object[]{"zzc", "zzd", "zze"});
            case 4:
                return zzf;
            case 5:
                zzgj<zzy$zzbd> zzgjVar2 = zzg;
                zzgj<zzy$zzbd> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzbd.class) {
                        zzgj<zzy$zzbd> zzgjVar4 = zzg;
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
        zzy$zzbd zzy_zzbd = new zzy$zzbd();
        zzf = zzy_zzbd;
        zzeo.zza(zzy$zzbd.class, zzy_zzbd);
    }
}
