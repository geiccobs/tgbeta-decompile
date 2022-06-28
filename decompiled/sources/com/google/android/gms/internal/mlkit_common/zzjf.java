package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzfq;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzjf {

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zza extends zzfq<zza, C0018zza> implements zzhd {
        private static final zza zzf;
        private static volatile zzhl<zza> zzg;
        private int zzc;
        private int zzd;
        private zzj zze;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            UNKNOWN_ENGINE(0),
            TFLITE(1);
            
            private static final zzfu<zzb> zzc = new zzjg();
            private final int zzd;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzd;
            }

            public static zzfx zzb() {
                return zzjh.zza;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* renamed from: com.google.android.gms.internal.mlkit_common.zzjf$zza$zza */
        /* loaded from: classes3.dex */
        public static final class C0018zza extends zzfq.zzb<zza, C0018zza> implements zzhd {
            private C0018zza() {
                super(zza.zzf);
            }

            /* synthetic */ C0018zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zza>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zza> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zza();
                case 2:
                    return new C0018zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဉ\u0001", new Object[]{"zzc", "zzd", zzb.zzb(), "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zza> zzhlVar2 = zzg;
                    zzhl<zza> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zza.class) {
                            zzhl<zza> zzhlVar4 = zzg;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzf);
                                zzg = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zza.class, zzaVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzb extends zzfq<zzb, zza> implements zzhd {
        private static final zzb zzn;
        private static volatile zzhl<zzb> zzo;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
            private zza() {
                super(zzb.zzn);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzb> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzn, "\u0001\n\u0000\u0001\u0001\n\n\u0000\u0000\u0000\u0001င\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဈ\u0004\u0006ဈ\u0005\u0007ဈ\u0006\bဈ\u0007\tဈ\b\nဈ\t", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", "zzl", "zzm"});
                case 4:
                    return zzn;
                case 5:
                    zzhl<zzb> zzhlVar2 = zzo;
                    zzhl<zzb> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzb.class) {
                            zzhl<zzb> zzhlVar4 = zzo;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzn);
                                zzo = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzb.class, zzbVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzc extends zzfq<zzc, zzb> implements zzhd {
        private static final zzc zzm;
        private static volatile zzhl<zzc> zzn;
        private int zzc;
        private int zzd;
        private int zze;
        private zza zzh;
        private zzd zzi;
        private int zzj;
        private int zzl;
        private String zzf = "";
        private String zzg = "";
        private zzfy<zzg> zzk = zzl();

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN_ACTION(0),
            INITIALIZATION(1),
            COMPILATION(2),
            EXECUTION(3),
            TEARDOWN(4),
            VALIDATION(5);
            
            private static final zzfu<zza> zzg = new zzji();
            private final int zzh;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzh;
            }

            public static zzfx zzb() {
                return zzjj.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzh = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* renamed from: com.google.android.gms.internal.mlkit_common.zzjf$zzc$zzc */
        /* loaded from: classes3.dex */
        public static final class C0019zzc extends zzfq<C0019zzc, zza> implements zzhd {
            private static final C0019zzc zzf;
            private static volatile zzhl<C0019zzc> zzg;
            private int zzc;
            private int zzd;
            private int zze;

            private C0019zzc() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* renamed from: com.google.android.gms.internal.mlkit_common.zzjf$zzc$zzc$zza */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<C0019zzc, zza> implements zzhd {
                private zza() {
                    super(C0019zzc.zzf);
                }

                /* synthetic */ zza(zzje zzjeVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzc$zzc>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<C0019zzc> zzhlVar;
                switch (zzje.zza[i - 1]) {
                    case 1:
                        return new C0019zzc();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", "zze"});
                    case 4:
                        return zzf;
                    case 5:
                        zzhl<C0019zzc> zzhlVar2 = zzg;
                        zzhl<C0019zzc> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (C0019zzc.class) {
                                zzhl<C0019zzc> zzhlVar4 = zzg;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzf);
                                    zzg = zzaVar;
                                    zzhlVar = zzaVar;
                                }
                            }
                            zzhlVar3 = zzhlVar;
                        }
                        return zzhlVar3;
                    case 6:
                        return (byte) 1;
                    case 7:
                        return null;
                    default:
                        throw new UnsupportedOperationException();
                }
            }

            static {
                C0019zzc c0019zzc = new C0019zzc();
                zzf = c0019zzc;
                zzfq.zza(C0019zzc.class, c0019zzc);
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzd extends zzfq<zzd, zza> implements zzhd {
            private static final zzd zzg;
            private static volatile zzhl<zzd> zzh;
            private int zzc;
            private C0019zzc zzd;
            private C0019zzc zze;
            private boolean zzf;

            private zzd() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzd, zza> implements zzhd {
                private zza() {
                    super(zzd.zzg);
                }

                /* synthetic */ zza(zzje zzjeVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzc$zzd>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzd> zzhlVar;
                switch (zzje.zza[i - 1]) {
                    case 1:
                        return new zzd();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဇ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                    case 4:
                        return zzg;
                    case 5:
                        zzhl<zzd> zzhlVar2 = zzh;
                        zzhl<zzd> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzd.class) {
                                zzhl<zzd> zzhlVar4 = zzh;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzg);
                                    zzh = zzaVar;
                                    zzhlVar = zzaVar;
                                }
                            }
                            zzhlVar3 = zzhlVar;
                        }
                        return zzhlVar3;
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
                zzfq.zza(zzd.class, zzdVar);
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zze implements zzfv {
            UNKNOWN_STATUS(0),
            COMPLETED_EVENT(1),
            MISSING_END_EVENT(2),
            HANG(3),
            ABANDONED_FROM_HANG(4),
            FORCED_CRASH_FROM_HANG(5);
            
            private static final zzfu<zze> zzg = new zzjk();
            private final int zzh;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzh;
            }

            public static zzfx zzb() {
                return zzjm.zza;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzc, zzb> implements zzhd {
            private zzb() {
                super(zzc.zzm);
            }

            /* synthetic */ zzb(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzc>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzc> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzm, "\u0001\t\u0000\u0001\u0001\t\t\u0000\u0001\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဉ\u0004\u0006ဉ\u0005\u0007င\u0006\b\u001b\tင\u0007", new Object[]{"zzc", "zzd", zza.zzb(), "zze", zze.zzb(), "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", zzg.class, "zzl"});
                case 4:
                    return zzm;
                case 5:
                    zzhl<zzc> zzhlVar2 = zzn;
                    zzhl<zzc> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzc.class) {
                            zzhl<zzc> zzhlVar4 = zzn;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzm);
                                zzn = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzc.class, zzcVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzd extends zzfq<zzd, zza> implements zzhd {
        private static final zzd zzk;
        private static volatile zzhl<zzd> zzl;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzd, zza> implements zzhd {
            private zza() {
                super(zzd.zzk);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzd>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzd> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzd();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzk, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004င\u0003\u0005င\u0004\u0006ဈ\u0005\u0007င\u0006", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj"});
                case 4:
                    return zzk;
                case 5:
                    zzhl<zzd> zzhlVar2 = zzl;
                    zzhl<zzd> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzd.class) {
                            zzhl<zzd> zzhlVar4 = zzl;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzk);
                                zzl = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzd.class, zzdVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zze extends zzfq<zze, zza> implements zzhd {
        private static final zze zze;
        private static volatile zzhl<zze> zzf;
        private int zzc;
        private int zzd;

        private zze() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zze, zza> implements zzhd {
            private zza() {
                super(zze.zze);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zze>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zze> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zze();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001င\u0000", new Object[]{"zzc", "zzd"});
                case 4:
                    return zze;
                case 5:
                    zzhl<zze> zzhlVar2 = zzf;
                    zzhl<zze> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zze.class) {
                            zzhl<zze> zzhlVar4 = zzf;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zze);
                                zzf = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zze.class, zzeVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzf extends zzfq<zzf, zza> implements zzhd {
        private static final zzf zzo;
        private static volatile zzhl<zzf> zzp;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzf, zza> implements zzhd {
            private zza() {
                super(zzf.zzo);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzf>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzf> zzhlVar;
            int i2 = 1;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzf();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzo, "\u0001\n\u0000\u0001\u0001\n\n\u0000\u0000\u0001\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဌ\u0003\u0004ဉ\u0004\u0005ᐉ\u0005\u0006ဂ\u0006\u0007ဂ\u0007\bဇ\b\tင\t\nဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzg", zzjl.zzb(), "zzh", "zzi", "zzj", "zzk", "zzl", "zzm", "zzf"});
                case 4:
                    return zzo;
                case 5:
                    zzhl<zzf> zzhlVar2 = zzp;
                    zzhl<zzf> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzf.class) {
                            zzhl<zzf> zzhlVar4 = zzp;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzo);
                                zzp = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzf.class, zzfVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzg extends zzfq<zzg, zza> implements zzhd {
        private static final zzg zzd;
        private static volatile zzhl<zzg> zze;
        private zzfw zzc = zzk();

        private zzg() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzg, zza> implements zzhd {
            private zza() {
                super(zzg.zzd);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r1v13, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzg>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzg> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzg();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u0016", new Object[]{"zzc"});
                case 4:
                    return zzd;
                case 5:
                    zzhl<zzg> zzhlVar2 = zze;
                    zzhl<zzg> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzg.class) {
                            zzhl<zzg> zzhlVar4 = zze;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzd);
                                zze = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzg.class, zzgVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzh extends zzfq<zzh, zza> implements zzhd {
        private static final zzh zzf;
        private static volatile zzhl<zzh> zzg;
        private int zzc;
        private String zzd = "";
        private int zze;

        private zzh() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzh, zza> implements zzhd {
            private zza() {
                super(zzh.zzf);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzh>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzh> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzh();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဈ\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzh> zzhlVar2 = zzg;
                    zzhl<zzh> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzh.class) {
                            zzhl<zzh> zzhlVar4 = zzg;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzf);
                                zzg = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzh.class, zzhVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzi extends zzfq<zzi, zza> implements zzhd {
        private static final zzi zzd;
        private static volatile zzhl<zzi> zze;
        private zzfy<zzb> zzc = zzl();

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzh;
            private static volatile zzhl<zzb> zzi;
            private int zzc;
            private int zze;
            private long zzg;
            private String zzd = "";
            private String zzf = "";

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ zza(zzje zzjeVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzi$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (zzje.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဈ\u0000\u0002င\u0001\u0003ဈ\u0002\u0004ဂ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
                    case 4:
                        return zzh;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzi;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzi;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzh);
                                    zzi = zzaVar;
                                    zzhlVar = zzaVar;
                                }
                            }
                            zzhlVar3 = zzhlVar;
                        }
                        return zzhlVar3;
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
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        private zzi() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzi, zza> implements zzhd {
            private zza() {
                super(zzi.zzd);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzi>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzi> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzi();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzc", zzb.class});
                case 4:
                    return zzd;
                case 5:
                    zzhl<zzi> zzhlVar2 = zze;
                    zzhl<zzi> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzi.class) {
                            zzhl<zzi> zzhlVar4 = zze;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzd);
                                zze = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzi.class, zziVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzj extends zzfq<zzj, zza> implements zzhd {
        private static final zzj zzg;
        private static volatile zzhl<zzj> zzh;
        private int zzc;
        private int zzd;
        private zzh zze;
        private zze zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            DELEGATE_NONE(0),
            NNAPI(1),
            GPU(2),
            HEXAGON(3),
            EDGETPU(4);
            
            private static final zzfu<zzb> zzf = new zzjp();
            private final int zzg;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzg;
            }

            public static zzfx zzb() {
                return zzjq.zza;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzj, zza> implements zzhd {
            private zza() {
                super(zzj.zzg);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzj>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzj> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzj();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzj> zzhlVar2 = zzh;
                    zzhl<zzj> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzj.class) {
                            zzhl<zzj> zzhlVar4 = zzh;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzg);
                                zzh = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzj.class, zzjVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzk extends zzfq<zzk, zzb> implements zzhd {
        private static final zzk zzf;
        private static volatile zzhl<zzk> zzg;
        private int zzc;
        private int zzd;
        private float zze;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN_METRIC(0),
            MEAN_ABSOLUTE_ERROR(1),
            MEAN_SQUARED_ERROR(2),
            ROOT_MEAN_SQUARED_ERROR(3);
            
            private static final zzfu<zza> zze = new zzjs();
            private final int zzf;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzf;
            }

            public static zzfx zzb() {
                return zzjr.zza;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzk, zzb> implements zzhd {
            private zzb() {
                super(zzk.zzf);
            }

            /* synthetic */ zzb(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzk>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzk> zzhlVar;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzk();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002ခ\u0001", new Object[]{"zzc", "zzd", zza.zzb(), "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzk> zzhlVar2 = zzg;
                    zzhl<zzk> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzk.class) {
                            zzhl<zzk> zzhlVar4 = zzg;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzf);
                                zzg = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzk.class, zzkVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzl extends zzfq.zzc<zzl, zza> implements zzhd {
        private static final zzl zzf;
        private static volatile zzhl<zzl> zzg;
        private byte zze = 2;
        private zzfy<zzb> zzd = zzl();

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzg;
            private static volatile zzhl<zzb> zzh;
            private int zzc;
            private int zzd;
            private int zze;
            private zzfy<zzk> zzf = zzl();

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzg);
                }

                /* synthetic */ zza(zzje zzjeVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzl$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (zzje.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0001\u0000\u0001င\u0000\u0002င\u0001\u0003\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", zzk.class});
                    case 4:
                        return zzg;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzh;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzh;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzg);
                                    zzh = zzaVar;
                                    zzhlVar = zzaVar;
                                }
                            }
                            zzhlVar3 = zzhlVar;
                        }
                        return zzhlVar3;
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
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        private zzl() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzd<zzl, zza> implements zzhd {
            private zza() {
                super(zzl.zzf);
            }

            /* synthetic */ zza(zzje zzjeVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzjf$zzl>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzl> zzhlVar;
            int i2 = 1;
            switch (zzje.zza[i - 1]) {
                case 1:
                    return new zzl();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzd", zzb.class});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzl> zzhlVar2 = zzg;
                    zzhl<zzl> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzl.class) {
                            zzhl<zzl> zzhlVar4 = zzg;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzf);
                                zzg = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
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
            zzfq.zza(zzl.class, zzlVar);
        }
    }
}
