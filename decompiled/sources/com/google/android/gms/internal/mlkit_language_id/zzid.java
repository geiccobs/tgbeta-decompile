package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzid {

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zza extends zzeo<zza, C0022zza> implements zzgb {
        private static final zza zzf;
        private static volatile zzgj<zza> zzg;
        private int zzc;
        private int zzd;
        private zzj zze;

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzet {
            UNKNOWN_ENGINE(0),
            TFLITE(1);
            
            private static final zzes<zzb> zzc = new zzie();
            private final int zzd;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zzd;
            }

            public static zzev zzb() {
                return zzif.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzd + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzd = i;
            }
        }

        private zza() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* renamed from: com.google.android.gms.internal.mlkit_language_id.zzid$zza$zza */
        /* loaded from: classes3.dex */
        public static final class C0022zza extends zzeo.zzb<zza, C0022zza> implements zzgb {
            private C0022zza() {
                super(zza.zzf);
            }

            /* synthetic */ C0022zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zza>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zza> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zza();
                case 2:
                    return new C0022zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဉ\u0001", new Object[]{"zzc", "zzd", zzb.zzb(), "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzgj<zza> zzgjVar2 = zzg;
                    zzgj<zza> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zza.class) {
                            zzgj<zza> zzgjVar4 = zzg;
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
            zza zzaVar = new zza();
            zzf = zzaVar;
            zzeo.zza(zza.class, zzaVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzb extends zzeo<zzb, zza> implements zzgb {
        private static final zzb zzn;
        private static volatile zzgj<zzb> zzo;
        private int zzc;
        private int zzd;
        private String zze = "";
        private String zzf = "";
        private String zzg = "";
        private String zzh = "";
        private String zzi = "";
        private String zzj = "";
        private String zzk = "";
        private String zzl = "";
        private String zzm = "";

        private zzb() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzb, zza> implements zzgb {
            private zza() {
                super(zzb.zzn);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzb> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzn, "\u0001\n\u0000\u0001\u0001\n\n\u0000\u0000\u0000\u0001င\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဈ\u0004\u0006ဈ\u0005\u0007ဈ\u0006\bဈ\u0007\tဈ\b\nဈ\t", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", "zzl", "zzm"});
                case 4:
                    return zzn;
                case 5:
                    zzgj<zzb> zzgjVar2 = zzo;
                    zzgj<zzb> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzb.class) {
                            zzgj<zzb> zzgjVar4 = zzo;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzn);
                                zzo = zzaVar;
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
            zzn = zzbVar;
            zzeo.zza(zzb.class, zzbVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzc extends zzeo<zzc, zzb> implements zzgb {
        private static final zzc zzm;
        private static volatile zzgj<zzc> zzn;
        private int zzc;
        private int zzd;
        private int zze;
        private zza zzh;
        private zzd zzi;
        private int zzj;
        private int zzl;
        private String zzf = "";
        private String zzg = "";
        private zzew<zzg> zzk = zzl();

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zza implements zzet {
            UNKNOWN_ACTION(0),
            INITIALIZATION(1),
            COMPILATION(2),
            EXECUTION(3),
            TEARDOWN(4),
            VALIDATION(5);
            
            private static final zzes<zza> zzg = new zzig();
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
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzh = i;
            }
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* renamed from: com.google.android.gms.internal.mlkit_language_id.zzid$zzc$zzc */
        /* loaded from: classes3.dex */
        public static final class C0023zzc extends zzeo<C0023zzc, zza> implements zzgb {
            private static final C0023zzc zzf;
            private static volatile zzgj<C0023zzc> zzg;
            private int zzc;
            private int zzd;
            private int zze;

            private C0023zzc() {
            }

            /* compiled from: com.google.mlkit:language-id@@16.1.1 */
            /* renamed from: com.google.android.gms.internal.mlkit_language_id.zzid$zzc$zzc$zza */
            /* loaded from: classes3.dex */
            public static final class zza extends zzeo.zzb<C0023zzc, zza> implements zzgb {
                private zza() {
                    super(C0023zzc.zzf);
                }

                /* synthetic */ zza(zzic zzicVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzc$zzc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
            @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
            public final Object zza(int i, Object obj, Object obj2) {
                zzgj<C0023zzc> zzgjVar;
                switch (zzic.zza[i - 1]) {
                    case 1:
                        return new C0023zzc();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", "zze"});
                    case 4:
                        return zzf;
                    case 5:
                        zzgj<C0023zzc> zzgjVar2 = zzg;
                        zzgj<C0023zzc> zzgjVar3 = zzgjVar2;
                        if (zzgjVar2 == null) {
                            synchronized (C0023zzc.class) {
                                zzgj<C0023zzc> zzgjVar4 = zzg;
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
                C0023zzc c0023zzc = new C0023zzc();
                zzf = c0023zzc;
                zzeo.zza(C0023zzc.class, c0023zzc);
            }
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zzd extends zzeo<zzd, zza> implements zzgb {
            private static final zzd zzg;
            private static volatile zzgj<zzd> zzh;
            private int zzc;
            private C0023zzc zzd;
            private C0023zzc zze;
            private boolean zzf;

            private zzd() {
            }

            /* compiled from: com.google.mlkit:language-id@@16.1.1 */
            /* loaded from: classes3.dex */
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
                        return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဇ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
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
        /* loaded from: classes3.dex */
        public enum zze implements zzet {
            UNKNOWN_STATUS(0),
            COMPLETED_EVENT(1),
            MISSING_END_EVENT(2),
            HANG(3),
            ABANDONED_FROM_HANG(4),
            FORCED_CRASH_FROM_HANG(5);
            
            private static final zzes<zze> zzg = new zzii();
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
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
            }

            zze(int i) {
                this.zzh = i;
            }
        }

        private zzc() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzeo.zzb<zzc, zzb> implements zzgb {
            private zzb() {
                super(zzc.zzm);
            }

            /* synthetic */ zzb(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzc> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzm, "\u0001\t\u0000\u0001\u0001\t\t\u0000\u0001\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဉ\u0004\u0006ဉ\u0005\u0007င\u0006\b\u001b\tင\u0007", new Object[]{"zzc", "zzd", zza.zzb(), "zze", zze.zzb(), "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", zzg.class, "zzl"});
                case 4:
                    return zzm;
                case 5:
                    zzgj<zzc> zzgjVar2 = zzn;
                    zzgj<zzc> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzc.class) {
                            zzgj<zzc> zzgjVar4 = zzn;
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
            zzc zzcVar = new zzc();
            zzm = zzcVar;
            zzeo.zza(zzc.class, zzcVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzd extends zzeo<zzd, zza> implements zzgb {
        private static final zzd zzk;
        private static volatile zzgj<zzd> zzl;
        private int zzc;
        private int zzg;
        private int zzh;
        private int zzj;
        private String zzd = "";
        private String zze = "";
        private String zzf = "";
        private String zzi = "";

        private zzd() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzd, zza> implements zzgb {
            private zza() {
                super(zzd.zzk);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzd>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzd> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzd();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzk, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004င\u0003\u0005င\u0004\u0006ဈ\u0005\u0007င\u0006", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj"});
                case 4:
                    return zzk;
                case 5:
                    zzgj<zzd> zzgjVar2 = zzl;
                    zzgj<zzd> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzd.class) {
                            zzgj<zzd> zzgjVar4 = zzl;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzk);
                                zzl = zzaVar;
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
            zzk = zzdVar;
            zzeo.zza(zzd.class, zzdVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zze extends zzeo<zze, zza> implements zzgb {
        private static final zze zze;
        private static volatile zzgj<zze> zzf;
        private int zzc;
        private int zzd;

        private zze() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zze, zza> implements zzgb {
            private zza() {
                super(zze.zze);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zze>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zze> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zze();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001င\u0000", new Object[]{"zzc", "zzd"});
                case 4:
                    return zze;
                case 5:
                    zzgj<zze> zzgjVar2 = zzf;
                    zzgj<zze> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zze.class) {
                            zzgj<zze> zzgjVar4 = zzf;
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
            zze zzeVar = new zze();
            zze = zzeVar;
            zzeo.zza(zze.class, zzeVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzf extends zzeo<zzf, zza> implements zzgb {
        private static final zzf zzo;
        private static volatile zzgj<zzf> zzp;
        private int zzc;
        private zzb zzd;
        private zzi zze;
        private zzd zzf;
        private int zzg;
        private zzc zzh;
        private zzl zzi;
        private long zzj;
        private long zzk;
        private boolean zzl;
        private int zzm;
        private byte zzn = 2;

        private zzf() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzf, zza> implements zzgb {
            private zza() {
                super(zzf.zzo);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzf>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzf> zzgjVar;
            int i2 = 1;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzf();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzo, "\u0001\n\u0000\u0001\u0001\n\n\u0000\u0000\u0001\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဌ\u0003\u0004ဉ\u0004\u0005ᐉ\u0005\u0006ဂ\u0006\u0007ဂ\u0007\bဇ\b\tင\t\nဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzg", zzij.zzb(), "zzh", "zzi", "zzj", "zzk", "zzl", "zzm", "zzf"});
                case 4:
                    return zzo;
                case 5:
                    zzgj<zzf> zzgjVar2 = zzp;
                    zzgj<zzf> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzf.class) {
                            zzgj<zzf> zzgjVar4 = zzp;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzo);
                                zzp = zzaVar;
                                zzgjVar = zzaVar;
                            }
                        }
                        zzgjVar3 = zzgjVar;
                    }
                    return zzgjVar3;
                case 6:
                    return Byte.valueOf(this.zzn);
                case 7:
                    if (obj == null) {
                        i2 = 0;
                    }
                    this.zzn = (byte) i2;
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzf zzfVar = new zzf();
            zzo = zzfVar;
            zzeo.zza(zzf.class, zzfVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzg extends zzeo<zzg, zza> implements zzgb {
        private static final zzg zzd;
        private static volatile zzgj<zzg> zze;
        private zzeu zzc = zzk();

        private zzg() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzg, zza> implements zzgb {
            private zza() {
                super(zzg.zzd);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r1v13, types: [com.google.android.gms.internal.mlkit_language_id.zzeo$zza, com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzg>] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzg> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzg();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u0016", new Object[]{"zzc"});
                case 4:
                    return zzd;
                case 5:
                    zzgj<zzg> zzgjVar2 = zze;
                    zzgj<zzg> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzg.class) {
                            zzgj<zzg> zzgjVar4 = zze;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzd);
                                zze = zzaVar;
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
            zzg zzgVar = new zzg();
            zzd = zzgVar;
            zzeo.zza(zzg.class, zzgVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzh extends zzeo<zzh, zza> implements zzgb {
        private static final zzh zzf;
        private static volatile zzgj<zzh> zzg;
        private int zzc;
        private String zzd = "";
        private int zze;

        private zzh() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzh, zza> implements zzgb {
            private zza() {
                super(zzh.zzf);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzh>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzh> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzh();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဈ\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzgj<zzh> zzgjVar2 = zzg;
                    zzgj<zzh> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzh.class) {
                            zzgj<zzh> zzgjVar4 = zzg;
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
            zzh zzhVar = new zzh();
            zzf = zzhVar;
            zzeo.zza(zzh.class, zzhVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzi extends zzeo<zzi, zza> implements zzgb {
        private static final zzi zzd;
        private static volatile zzgj<zzi> zze;
        private zzew<zzb> zzc = zzl();

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzeo<zzb, zza> implements zzgb {
            private static final zzb zzh;
            private static volatile zzgj<zzb> zzi;
            private int zzc;
            private int zze;
            private long zzg;
            private String zzd = "";
            private String zzf = "";

            private zzb() {
            }

            /* compiled from: com.google.mlkit:language-id@@16.1.1 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzeo.zzb<zzb, zza> implements zzgb {
                private zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ zza(zzic zzicVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzi$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
            @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
            public final Object zza(int i, Object obj, Object obj2) {
                zzgj<zzb> zzgjVar;
                switch (zzic.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဈ\u0000\u0002င\u0001\u0003ဈ\u0002\u0004ဂ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
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

        private zzi() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzi, zza> implements zzgb {
            private zza() {
                super(zzi.zzd);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzi>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzi> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzi();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzc", zzb.class});
                case 4:
                    return zzd;
                case 5:
                    zzgj<zzi> zzgjVar2 = zze;
                    zzgj<zzi> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzi.class) {
                            zzgj<zzi> zzgjVar4 = zze;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzd);
                                zze = zzaVar;
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
            zzi zziVar = new zzi();
            zzd = zziVar;
            zzeo.zza(zzi.class, zziVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzj extends zzeo<zzj, zza> implements zzgb {
        private static final zzj zzg;
        private static volatile zzgj<zzj> zzh;
        private int zzc;
        private int zzd;
        private zzh zze;
        private zze zzf;

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzet {
            DELEGATE_NONE(0),
            NNAPI(1),
            GPU(2),
            HEXAGON(3),
            EDGETPU(4);
            
            private static final zzes<zzb> zzf = new zzin();
            private final int zzg;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zzg;
            }

            public static zzev zzb() {
                return zzio.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzg + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzg = i;
            }
        }

        private zzj() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzb<zzj, zza> implements zzgb {
            private zza() {
                super(zzj.zzg);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzj>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzj> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzj();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzgj<zzj> zzgjVar2 = zzh;
                    zzgj<zzj> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzj.class) {
                            zzgj<zzj> zzgjVar4 = zzh;
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
            zzj zzjVar = new zzj();
            zzg = zzjVar;
            zzeo.zza(zzj.class, zzjVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzk extends zzeo<zzk, zzb> implements zzgb {
        private static final zzk zzf;
        private static volatile zzgj<zzk> zzg;
        private int zzc;
        private int zzd;
        private float zze;

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zza implements zzet {
            UNKNOWN_METRIC(0),
            MEAN_ABSOLUTE_ERROR(1),
            MEAN_SQUARED_ERROR(2),
            ROOT_MEAN_SQUARED_ERROR(3);
            
            private static final zzes<zza> zze = new zziq();
            private final int zzf;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zzf;
            }

            public static zzev zzb() {
                return zzip.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzf = i;
            }
        }

        private zzk() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzeo.zzb<zzk, zzb> implements zzgb {
            private zzb() {
                super(zzk.zzf);
            }

            /* synthetic */ zzb(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzk>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzk> zzgjVar;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzk();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002ခ\u0001", new Object[]{"zzc", "zzd", zza.zzb(), "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzgj<zzk> zzgjVar2 = zzg;
                    zzgj<zzk> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzk.class) {
                            zzgj<zzk> zzgjVar4 = zzg;
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
            zzk zzkVar = new zzk();
            zzf = zzkVar;
            zzeo.zza(zzk.class, zzkVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzl extends zzeo.zzc<zzl, zza> implements zzgb {
        private static final zzl zzf;
        private static volatile zzgj<zzl> zzg;
        private byte zze = 2;
        private zzew<zzb> zzd = zzl();

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzeo<zzb, zza> implements zzgb {
            private static final zzb zzg;
            private static volatile zzgj<zzb> zzh;
            private int zzc;
            private int zzd;
            private int zze;
            private zzew<zzk> zzf = zzl();

            private zzb() {
            }

            /* compiled from: com.google.mlkit:language-id@@16.1.1 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzeo.zzb<zzb, zza> implements zzgb {
                private zza() {
                    super(zzb.zzg);
                }

                /* synthetic */ zza(zzic zzicVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzl$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
            @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
            public final Object zza(int i, Object obj, Object obj2) {
                zzgj<zzb> zzgjVar;
                switch (zzic.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0001\u0000\u0001င\u0000\u0002င\u0001\u0003\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", zzk.class});
                    case 4:
                        return zzg;
                    case 5:
                        zzgj<zzb> zzgjVar2 = zzh;
                        zzgj<zzb> zzgjVar3 = zzgjVar2;
                        if (zzgjVar2 == null) {
                            synchronized (zzb.class) {
                                zzgj<zzb> zzgjVar4 = zzh;
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
                zzb zzbVar = new zzb();
                zzg = zzbVar;
                zzeo.zza(zzb.class, zzbVar);
            }
        }

        private zzl() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzeo.zzd<zzl, zza> implements zzgb {
            private zza() {
                super(zzl.zzf);
            }

            /* synthetic */ zza(zzic zzicVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzl>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzl> zzgjVar;
            int i2 = 1;
            switch (zzic.zza[i - 1]) {
                case 1:
                    return new zzl();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzd", zzb.class});
                case 4:
                    return zzf;
                case 5:
                    zzgj<zzl> zzgjVar2 = zzg;
                    zzgj<zzl> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzl.class) {
                            zzgj<zzl> zzgjVar4 = zzg;
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
                    return Byte.valueOf(this.zze);
                case 7:
                    if (obj == null) {
                        i2 = 0;
                    }
                    this.zze = (byte) i2;
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzl zzlVar = new zzl();
            zzf = zzlVar;
            zzeo.zza(zzl.class, zzlVar);
        }
    }
}
