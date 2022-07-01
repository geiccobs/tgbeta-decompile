package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzid$zze extends zzeo<zzid$zze, zza> implements zzgb {
    private static final zzid$zze zze;
    private static volatile zzgj<zzid$zze> zzf;
    private int zzc;
    private int zzd;

    private zzid$zze() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzid$zze, zza> implements zzgb {
        private zza() {
            super(zzid$zze.zze);
        }

        /* synthetic */ zza(zzic zzicVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zze>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzid$zze> zzgjVar;
        switch (zzic.zza[i - 1]) {
            case 1:
                return new zzid$zze();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001င\u0000", new Object[]{"zzc", "zzd"});
            case 4:
                return zze;
            case 5:
                zzgj<zzid$zze> zzgjVar2 = zzf;
                zzgj<zzid$zze> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzid$zze.class) {
                        zzgj<zzid$zze> zzgjVar4 = zzf;
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
        zzid$zze zzid_zze = new zzid$zze();
        zze = zzid_zze;
        zzeo.zza(zzid$zze.class, zzid_zze);
    }
}
