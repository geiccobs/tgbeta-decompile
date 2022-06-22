package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzid$zzc extends zzeo<zzid$zzc, zzb> implements zzgb {
    private static final zzid$zzc zzm;
    private static volatile zzgj<zzid$zzc> zzn;
    private int zzc;
    private int zzd;
    private int zze;
    private zzid$zza zzh;
    private zzd zzi;
    private int zzj;
    private int zzl;
    private String zzf = "";
    private String zzg = "";
    private zzew<zzid$zzg> zzk = zzeo.zzl();

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        UNKNOWN_ACTION(0),
        INITIALIZATION(1),
        COMPILATION(2),
        EXECUTION(3),
        TEARDOWN(4),
        VALIDATION(5);
        
        private final int zzh;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzh;
        }

        public static zzev zzb() {
            return zzih.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzh = i;
        }

        static {
            new zzig();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzc extends zzeo<zzc, zza> implements zzgb {
        private static final zzc zzf;
        private static volatile zzgj<zzc> zzg;
        private int zzc;
        private int zzd;
        private int zze;

        private zzc() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzc, zza> implements zzgb {
            private zza() {
                super(zzc.zzf);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzc$zzc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzc> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzgj<zzc> zzgjVar2 = zzg;
                    zzgj<zzc> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzc.class) {
                            zzgj<zzc> zzgjVar4 = zzg;
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
            zzc zzcVar = new zzc();
            zzf = zzcVar;
            zzeo.zza(zzc.class, zzcVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzd extends zzeo<zzd, zza> implements zzgb {
        private static final zzd zzg;
        private static volatile zzgj<zzd> zzh;
        private int zzc;
        private zzc zzd;
        private zzc zze;
        private boolean zzf;

        private zzd() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzd, zza> implements zzgb {
            private zza() {
                super(zzd.zzg);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzc$zzd>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzd> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzd();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဇ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzgj<zzd> zzgjVar2 = zzh;
                    zzgj<zzd> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzd.class) {
                            zzgj<zzd> zzgjVar4 = zzh;
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
            zzd zzdVar = new zzd();
            zzg = zzdVar;
            zzeo.zza(zzd.class, zzdVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zze implements zzet {
        UNKNOWN_STATUS(0),
        COMPLETED_EVENT(1),
        MISSING_END_EVENT(2),
        HANG(3),
        ABANDONED_FROM_HANG(4),
        FORCED_CRASH_FROM_HANG(5);
        
        private final int zzh;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzh;
        }

        public static zzev zzb() {
            return zzik.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zze.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
        }

        zze(int i) {
            this.zzh = i;
        }

        static {
            new zzii();
        }
    }

    private zzid$zzc() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzid$zzc, zzb> implements zzgb {
        private zzb() {
            super(zzid$zzc.zzm);
        }

        /* synthetic */ zzb(zzic zzicVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzid$zzc> zzgjVar;
        switch (zzic.zza[i - 1]) {
            case 1:
                return new zzid$zzc();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzm, "\u0001\t\u0000\u0001\u0001\t\t\u0000\u0001\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဉ\u0004\u0006ဉ\u0005\u0007င\u0006\b\u001b\tင\u0007", new Object[]{"zzc", "zzd", zza.zzb(), "zze", zze.zzb(), "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", zzid$zzg.class, "zzl"});
            case 4:
                return zzm;
            case 5:
                zzgj<zzid$zzc> zzgjVar2 = zzn;
                zzgj<zzid$zzc> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzid$zzc.class) {
                        zzgj<zzid$zzc> zzgjVar4 = zzn;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzm);
                            zzn = zzaVar;
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
        zzid$zzc zzid_zzc = new zzid$zzc();
        zzm = zzid_zzc;
        zzeo.zza(zzid$zzc.class, zzid_zzc);
    }
}
