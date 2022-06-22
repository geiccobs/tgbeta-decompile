package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzg extends zzeo<zzy$zzg, zza> implements zzgb {
    private static final zzy$zzg zzg;
    private static volatile zzgj<zzy$zzg> zzh;
    private int zzc;
    private zzb zzd;
    private int zze;
    private zzy$zzab zzf;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo<zzb, zza> implements zzgb {
        private static final zzb zzh;
        private static volatile zzgj<zzb> zzi;
        private int zzc;
        private int zzd;
        private boolean zze;
        private zzy$zzae zzf;
        private zzy$zzba zzg;

        private zzb() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzb, zza> implements zzgb {
            private zza() {
                super(zzb.zzh);
            }

            /* synthetic */ zza(zzx zzxVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzg$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzb> zzgjVar;
            switch (zzx.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဉ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", zzai.zzb(), "zze", "zzf", "zzg"});
                case 4:
                    return zzh;
                case 5:
                    zzgj<zzb> zzgjVar2 = zzi;
                    zzgj<zzb> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzb.class) {
                            zzgj<zzb> zzgjVar4 = zzi;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzh);
                                zzi = zzaVar;
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
            zzb zzbVar = new zzb();
            zzh = zzbVar;
            zzeo.zza(zzb.class, zzbVar);
        }
    }

    private zzy$zzg() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzg, zza> implements zzgb {
        private zza() {
            super(zzy$zzg.zzg);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzg>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzg> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzg();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzg> zzgjVar2 = zzh;
                zzgj<zzy$zzg> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzg.class) {
                        zzgj<zzy$zzg> zzgjVar4 = zzh;
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
        zzy$zzg zzy_zzg = new zzy$zzg();
        zzg = zzy_zzg;
        zzeo.zza(zzy$zzg.class, zzy_zzg);
    }
}
