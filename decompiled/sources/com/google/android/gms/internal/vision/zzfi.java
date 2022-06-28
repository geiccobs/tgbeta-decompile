package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzfi {

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zza extends zzjb<zza, C0029zza> implements zzkm {
        private static final zza zzf;
        private static volatile zzkx<zza> zzg;
        private int zzc;
        private String zzd = "";
        private String zze = "";

        private zza() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* renamed from: com.google.android.gms.internal.vision.zzfi$zza$zza */
        /* loaded from: classes3.dex */
        public static final class C0029zza extends zzjb.zzb<zza, C0029zza> implements zzkm {
            private C0029zza() {
                super(zza.zzf);
            }

            public final C0029zza zza(String str) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zza) this.zza).zza(str);
                return this;
            }

            public final C0029zza zzb(String str) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zza) this.zza).zzb(str);
                return this;
            }

            /* synthetic */ C0029zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(String str) {
            str.getClass();
            this.zzc |= 1;
            this.zzd = str;
        }

        public final void zzb(String str) {
            str.getClass();
            this.zzc |= 2;
            this.zze = str;
        }

        public static C0029zza zza() {
            return zzf.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zza>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zza> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zza();
                case 2:
                    return new C0029zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzkx<zza> zzkxVar2 = zzg;
                    zzkx<zza> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zza.class) {
                            zzkx<zza> zzkxVar4 = zzg;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzf);
                                zzg = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzjb.zza(zza.class, zzaVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzb extends zzjb<zzb, zza> implements zzkm {
        private static final zzji<Integer, zzgz> zzd = new zzfl();
        private static final zzb zze;
        private static volatile zzkx<zzb> zzf;
        private zzjj zzc = zzn();

        private zzb() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzb, zza> implements zzkm {
            private zza() {
                super(zzb.zze);
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzb>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzb> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zze, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001e", new Object[]{"zzc", zzgz.zzb()});
                case 4:
                    return zze;
                case 5:
                    zzkx<zzb> zzkxVar2 = zzf;
                    zzkx<zzb> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzb.class) {
                            zzkx<zzb> zzkxVar4 = zzf;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zze);
                                zzf = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.vision.zzji<java.lang.Integer, com.google.android.gms.internal.vision.zzgz>, com.google.android.gms.internal.vision.zzfl] */
        static {
            zzb zzbVar = new zzb();
            zze = zzbVar;
            zzjb.zza(zzb.class, zzbVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzc extends zzjb<zzc, zza> implements zzkm {
        private static final zzc zzg;
        private static volatile zzkx<zzc> zzh;
        private int zzc;
        private int zzd;
        private int zze;
        private String zzf = "";

        private zzc() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzc, zza> implements zzkm {
            private zza() {
                super(zzc.zzg);
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzc>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzc> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဈ\u0002", new Object[]{"zzc", "zzd", zzgz.zzb(), "zze", zzha.zzb(), "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzkx<zzc> zzkxVar2 = zzh;
                    zzkx<zzc> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzc.class) {
                            zzkx<zzc> zzkxVar4 = zzh;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzg);
                                zzh = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzg = zzcVar;
            zzjb.zza(zzc.class, zzcVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzd extends zzjb<zzd, zza> implements zzkm {
        private static final zzd zzd;
        private static volatile zzkx<zzd> zze;
        private zzjl<zzm> zzc = zzo();

        private zzd() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzd, zza> implements zzkm {
            private zza() {
                super(zzd.zzd);
            }

            public final zza zza(zzm zzmVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzd) this.zza).zza(zzmVar);
                return this;
            }

            public final zza zza(zzm.zza zzaVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzd) this.zza).zza((zzm) ((zzjb) zzaVar.zzf()));
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(zzm zzmVar) {
            zzmVar.getClass();
            zzjl<zzm> zzjlVar = this.zzc;
            if (!zzjlVar.zza()) {
                this.zzc = zzjb.zza(zzjlVar);
            }
            this.zzc.add(zzmVar);
        }

        public static zza zza() {
            return zzd.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzd>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzd> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzd();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzc", zzm.class});
                case 4:
                    return zzd;
                case 5:
                    zzkx<zzd> zzkxVar2 = zze;
                    zzkx<zzd> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzd.class) {
                            zzkx<zzd> zzkxVar4 = zze;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzd);
                                zze = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzd = zzdVar;
            zzjb.zza(zzd.class, zzdVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zze extends zzjb<zze, zza> implements zzkm {
        private static final zze zzl;
        private static volatile zzkx<zze> zzm;
        private int zzc;
        private boolean zze;
        private int zzf;
        private long zzg;
        private long zzh;
        private long zzi;
        private boolean zzk;
        private String zzd = "";
        private String zzj = "";

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzje {
            REASON_UNKNOWN(0),
            REASON_MISSING(1),
            REASON_UPGRADE(2),
            REASON_INVALID(3);
            
            private static final zzjh<zzb> zze = new zzfm();
            private final int zzf;

            @Override // com.google.android.gms.internal.vision.zzje
            public final int zza() {
                return this.zzf;
            }

            public static zzb zza(int i) {
                switch (i) {
                    case 0:
                        return REASON_UNKNOWN;
                    case 1:
                        return REASON_MISSING;
                    case 2:
                        return REASON_UPGRADE;
                    case 3:
                        return REASON_INVALID;
                    default:
                        return null;
                }
            }

            public static zzjg zzb() {
                return zzfn.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzf = i;
            }
        }

        private zze() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zze, zza> implements zzkm {
            private zza() {
                super(zze.zzl);
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zze>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zze> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zze();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဇ\u0001\u0003ဌ\u0002\u0004ဂ\u0003\u0005ဂ\u0004\u0006ဂ\u0005\u0007ဈ\u0006\bဇ\u0007", new Object[]{"zzc", "zzd", "zze", "zzf", zzb.zzb(), "zzg", "zzh", "zzi", "zzj", "zzk"});
                case 4:
                    return zzl;
                case 5:
                    zzkx<zze> zzkxVar2 = zzm;
                    zzkx<zze> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zze.class) {
                            zzkx<zze> zzkxVar4 = zzm;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzl);
                                zzm = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzl = zzeVar;
            zzjb.zza(zze.class, zzeVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzf extends zzjb<zzf, zzb> implements zzkm {
        private static final zzf zzl;
        private static volatile zzkx<zzf> zzm;
        private int zzc;
        private int zzg;
        private long zzi;
        private long zzj;
        private String zzd = "";
        private String zze = "";
        private zzjl<String> zzf = zzjb.zzo();
        private String zzh = "";
        private zzjl<zzn> zzk = zzo();

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public enum zza implements zzje {
            RESULT_UNKNOWN(0),
            RESULT_SUCCESS(1),
            RESULT_FAIL(2),
            RESULT_SKIPPED(3);
            
            private static final zzjh<zza> zze = new zzfp();
            private final int zzf;

            @Override // com.google.android.gms.internal.vision.zzje
            public final int zza() {
                return this.zzf;
            }

            public static zza zza(int i) {
                switch (i) {
                    case 0:
                        return RESULT_UNKNOWN;
                    case 1:
                        return RESULT_SUCCESS;
                    case 2:
                        return RESULT_FAIL;
                    case 3:
                        return RESULT_SKIPPED;
                    default:
                        return null;
                }
            }

            public static zzjg zzb() {
                return zzfo.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzf = i;
            }
        }

        private zzf() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzjb.zzb<zzf, zzb> implements zzkm {
            private zzb() {
                super(zzf.zzl);
            }

            public final zzb zza(String str) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzf) this.zza).zza(str);
                return this;
            }

            public final zzb zza(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzf) this.zza).zza(j);
                return this;
            }

            public final zzb zzb(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzf) this.zza).zzb(j);
                return this;
            }

            public final zzb zza(Iterable<? extends zzn> iterable) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzf) this.zza).zza(iterable);
                return this;
            }

            /* synthetic */ zzb(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(String str) {
            str.getClass();
            this.zzc |= 1;
            this.zzd = str;
        }

        public final void zza(long j) {
            this.zzc |= 16;
            this.zzi = j;
        }

        public final void zzb(long j) {
            this.zzc |= 32;
            this.zzj = j;
        }

        public final void zza(Iterable<? extends zzn> iterable) {
            zzjl<zzn> zzjlVar = this.zzk;
            if (!zzjlVar.zza()) {
                this.zzk = zzjb.zza(zzjlVar);
            }
            zzhf.zza(iterable, this.zzk);
        }

        public static zzb zza() {
            return zzl.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzf>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzf> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzf();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0002\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003\u001a\u0004ဌ\u0002\u0005ဈ\u0003\u0006ဂ\u0004\u0007ဂ\u0005\b\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", zza.zzb(), "zzh", "zzi", "zzj", "zzk", zzn.class});
                case 4:
                    return zzl;
                case 5:
                    zzkx<zzf> zzkxVar2 = zzm;
                    zzkx<zzf> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzf.class) {
                            zzkx<zzf> zzkxVar4 = zzm;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzl);
                                zzm = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzf zzfVar = new zzf();
            zzl = zzfVar;
            zzjb.zza(zzf.class, zzfVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzg extends zzjb<zzg, zza> implements zzkm {
        private static final zzg zzj;
        private static volatile zzkx<zzg> zzk;
        private int zzc;
        private int zzd;
        private int zze;
        private int zzf;
        private boolean zzg;
        private boolean zzh;
        private float zzi;

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzje {
            CLASSIFICATION_UNKNOWN(0),
            CLASSIFICATION_NONE(1),
            CLASSIFICATION_ALL(2);
            
            private static final zzjh<zzb> zzd = new zzfq();
            private final int zze;

            @Override // com.google.android.gms.internal.vision.zzje
            public final int zza() {
                return this.zze;
            }

            public static zzb zza(int i) {
                switch (i) {
                    case 0:
                        return CLASSIFICATION_UNKNOWN;
                    case 1:
                        return CLASSIFICATION_NONE;
                    case 2:
                        return CLASSIFICATION_ALL;
                    default:
                        return null;
                }
            }

            public static zzjg zzb() {
                return zzfr.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zze = i;
            }
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public enum zzc implements zzje {
            LANDMARK_UNKNOWN(0),
            LANDMARK_NONE(1),
            LANDMARK_ALL(2),
            LANDMARK_CONTOUR(3);
            
            private static final zzjh<zzc> zze = new zzft();
            private final int zzf;

            @Override // com.google.android.gms.internal.vision.zzje
            public final int zza() {
                return this.zzf;
            }

            public static zzc zza(int i) {
                switch (i) {
                    case 0:
                        return LANDMARK_UNKNOWN;
                    case 1:
                        return LANDMARK_NONE;
                    case 2:
                        return LANDMARK_ALL;
                    case 3:
                        return LANDMARK_CONTOUR;
                    default:
                        return null;
                }
            }

            public static zzjg zzb() {
                return zzfs.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zzc(int i) {
                this.zzf = i;
            }
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public enum zzd implements zzje {
            MODE_UNKNOWN(0),
            MODE_ACCURATE(1),
            MODE_FAST(2),
            MODE_SELFIE(3);
            
            private static final zzjh<zzd> zze = new zzfu();
            private final int zzf;

            @Override // com.google.android.gms.internal.vision.zzje
            public final int zza() {
                return this.zzf;
            }

            public static zzd zza(int i) {
                switch (i) {
                    case 0:
                        return MODE_UNKNOWN;
                    case 1:
                        return MODE_ACCURATE;
                    case 2:
                        return MODE_FAST;
                    case 3:
                        return MODE_SELFIE;
                    default:
                        return null;
                }
            }

            public static zzjg zzb() {
                return zzfv.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zzd(int i) {
                this.zzf = i;
            }
        }

        private zzg() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzg, zza> implements zzkm {
            private zza() {
                super(zzg.zzj);
            }

            public final zza zza(zzd zzdVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzg) this.zza).zza(zzdVar);
                return this;
            }

            public final zza zza(zzc zzcVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzg) this.zza).zza(zzcVar);
                return this;
            }

            public final zza zza(zzb zzbVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzg) this.zza).zza(zzbVar);
                return this;
            }

            public final zza zza(boolean z) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzg) this.zza).zza(z);
                return this;
            }

            public final zza zzb(boolean z) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzg) this.zza).zzb(z);
                return this;
            }

            public final zza zza(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzg) this.zza).zza(f);
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(zzd zzdVar) {
            this.zzd = zzdVar.zza();
            this.zzc |= 1;
        }

        public final void zza(zzc zzcVar) {
            this.zze = zzcVar.zza();
            this.zzc |= 2;
        }

        public final void zza(zzb zzbVar) {
            this.zzf = zzbVar.zza();
            this.zzc |= 4;
        }

        public final void zza(boolean z) {
            this.zzc |= 8;
            this.zzg = z;
        }

        public final void zzb(boolean z) {
            this.zzc |= 16;
            this.zzh = z;
        }

        public final void zza(float f) {
            this.zzc |= 32;
            this.zzi = f;
        }

        public static zza zza() {
            return zzj.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzg>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzg> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzg();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဇ\u0003\u0005ဇ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", zzd.zzb(), "zze", zzc.zzb(), "zzf", zzb.zzb(), "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzkx<zzg> zzkxVar2 = zzk;
                    zzkx<zzg> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzg.class) {
                            zzkx<zzg> zzkxVar4 = zzk;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzj);
                                zzk = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzj = zzgVar;
            zzjb.zza(zzg.class, zzgVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzh extends zzjb<zzh, zza> implements zzkm {
        private static final zzh zzj;
        private static volatile zzkx<zzh> zzk;
        private int zzc;
        private float zzd;
        private float zze;
        private float zzf;
        private float zzg;
        private float zzh;
        private float zzi;

        private zzh() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzh, zza> implements zzkm {
            private zza() {
                super(zzh.zzj);
            }

            public final zza zza(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzh) this.zza).zza(f);
                return this;
            }

            public final zza zzb(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzh) this.zza).zzb(f);
                return this;
            }

            public final zza zzc(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzh) this.zza).zzc(f);
                return this;
            }

            public final zza zzd(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzh) this.zza).zzd(f);
                return this;
            }

            public final zza zze(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzh) this.zza).zze(f);
                return this;
            }

            public final zza zzf(float f) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzh) this.zza).zzf(f);
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(float f) {
            this.zzc |= 1;
            this.zzd = f;
        }

        public final void zzb(float f) {
            this.zzc |= 2;
            this.zze = f;
        }

        public final void zzc(float f) {
            this.zzc |= 4;
            this.zzf = f;
        }

        public final void zzd(float f) {
            this.zzc |= 8;
            this.zzg = f;
        }

        public final void zze(float f) {
            this.zzc |= 16;
            this.zzh = f;
        }

        public final void zzf(float f) {
            this.zzc |= 32;
            this.zzi = f;
        }

        public static zza zza() {
            return zzj.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzh>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzh> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzh();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ခ\u0000\u0002ခ\u0001\u0003ခ\u0002\u0004ခ\u0003\u0005ခ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzkx<zzh> zzkxVar2 = zzk;
                    zzkx<zzh> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzh.class) {
                            zzkx<zzh> zzkxVar4 = zzk;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzj);
                                zzk = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzj = zzhVar;
            zzjb.zza(zzh.class, zzhVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzi extends zzjb<zzi, zza> implements zzkm {
        private static final zzi zzg;
        private static volatile zzkx<zzi> zzh;
        private int zzc;
        private zzj zzd;
        private zzl zze;
        private zzjl<zzf> zzf = zzo();

        private zzi() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzi, zza> implements zzkm {
            private zza() {
                super(zzi.zzg);
            }

            public final zza zza(zzj zzjVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzi) this.zza).zza(zzjVar);
                return this;
            }

            public final zza zza(zzf.zzb zzbVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzi) this.zza).zza((zzf) ((zzjb) zzbVar.zzf()));
                return this;
            }

            public final zza zza(Iterable<? extends zzf> iterable) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzi) this.zza).zza(iterable);
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(zzj zzjVar) {
            zzjVar.getClass();
            this.zzd = zzjVar;
            this.zzc |= 1;
        }

        private final void zzc() {
            zzjl<zzf> zzjlVar = this.zzf;
            if (!zzjlVar.zza()) {
                this.zzf = zzjb.zza(zzjlVar);
            }
        }

        public final void zza(zzf zzfVar) {
            zzfVar.getClass();
            zzc();
            this.zzf.add(zzfVar);
        }

        public final void zza(Iterable<? extends zzf> iterable) {
            zzc();
            zzhf.zza(iterable, this.zzf);
        }

        public static zza zza() {
            return zzg.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzi>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzi> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzi();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", zzf.class});
                case 4:
                    return zzg;
                case 5:
                    zzkx<zzi> zzkxVar2 = zzh;
                    zzkx<zzi> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzi.class) {
                            zzkx<zzi> zzkxVar4 = zzh;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzg);
                                zzh = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzg = zziVar;
            zzjb.zza(zzi.class, zziVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzj extends zzjb<zzj, zzb> implements zzkm {
        private static final zzj zzi;
        private static volatile zzkx<zzj> zzj;
        private int zzc;
        private int zzd;
        private long zze;
        private long zzf;
        private long zzg;
        private long zzh;

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public enum zza implements zzje {
            FORMAT_UNKNOWN(0),
            FORMAT_LUMINANCE(1),
            FORMAT_RGB8(2),
            FORMAT_MONOCHROME(3);
            
            private static final zzjh<zza> zze = new zzfx();
            private final int zzf;

            @Override // com.google.android.gms.internal.vision.zzje
            public final int zza() {
                return this.zzf;
            }

            public static zza zza(int i) {
                switch (i) {
                    case 0:
                        return FORMAT_UNKNOWN;
                    case 1:
                        return FORMAT_LUMINANCE;
                    case 2:
                        return FORMAT_RGB8;
                    case 3:
                        return FORMAT_MONOCHROME;
                    default:
                        return null;
                }
            }

            public static zzjg zzb() {
                return zzfw.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzf = i;
            }
        }

        private zzj() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzjb.zzb<zzj, zzb> implements zzkm {
            private zzb() {
                super(zzj.zzi);
            }

            public final zzb zza(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzj) this.zza).zza(j);
                return this;
            }

            public final zzb zzb(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzj) this.zza).zzb(j);
                return this;
            }

            public final zzb zzc(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzj) this.zza).zzc(j);
                return this;
            }

            public final zzb zzd(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzj) this.zza).zzd(j);
                return this;
            }

            /* synthetic */ zzb(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(long j) {
            this.zzc |= 2;
            this.zze = j;
        }

        public final void zzb(long j) {
            this.zzc |= 4;
            this.zzf = j;
        }

        public final void zzc(long j) {
            this.zzc |= 8;
            this.zzg = j;
        }

        public final void zzd(long j) {
            this.zzc |= 16;
            this.zzh = j;
        }

        public static zzb zza() {
            return zzi.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzjb$zza, com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzj>] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzj> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzj();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဂ\u0001\u0003ဂ\u0002\u0004ဂ\u0004\u0005ဂ\u0003", new Object[]{"zzc", "zzd", zza.zzb(), "zze", "zzf", "zzh", "zzg"});
                case 4:
                    return zzi;
                case 5:
                    zzkx<zzj> zzkxVar2 = zzj;
                    zzkx<zzj> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzj.class) {
                            zzkx<zzj> zzkxVar4 = zzj;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzi);
                                zzj = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzi = zzjVar;
            zzjb.zza(zzj.class, zzjVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzk extends zzjb<zzk, zza> implements zzkm {
        private static final zzk zzj;
        private static volatile zzkx<zzk> zzk;
        private int zzc;
        private long zze;
        private zza zzf;
        private zzg zzh;
        private zzb zzi;
        private String zzd = "";
        private String zzg = "";

        private zzk() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzk, zza> implements zzkm {
            private zza() {
                super(zzk.zzj);
            }

            public final zza zza(String str) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzk) this.zza).zza(str);
                return this;
            }

            public final zza zza(long j) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzk) this.zza).zza(j);
                return this;
            }

            public final zza zza(zza zzaVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzk) this.zza).zza(zzaVar);
                return this;
            }

            public final zza zzb(String str) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzk) this.zza).zzb(str);
                return this;
            }

            public final zza zza(zzg.zza zzaVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzk) this.zza).zza((zzg) ((zzjb) zzaVar.zzf()));
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(String str) {
            str.getClass();
            this.zzc |= 1;
            this.zzd = str;
        }

        public final void zza(long j) {
            this.zzc |= 2;
            this.zze = j;
        }

        public final void zza(zza zzaVar) {
            zzaVar.getClass();
            this.zzf = zzaVar;
            this.zzc |= 4;
        }

        public final void zzb(String str) {
            str.getClass();
            this.zzc |= 8;
            this.zzg = str;
        }

        public final void zza(zzg zzgVar) {
            zzgVar.getClass();
            this.zzh = zzgVar;
            this.zzc |= 16;
        }

        public static zza zza() {
            return zzj.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzk>, com.google.android.gms.internal.vision.zzjb$zza] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzk> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzk();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0011\u0006\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဂ\u0001\u0003ဉ\u0002\u0006ဈ\u0003\u0010ဉ\u0004\u0011ဉ\u0005", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzkx<zzk> zzkxVar2 = zzk;
                    zzkx<zzk> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzk.class) {
                            zzkx<zzk> zzkxVar4 = zzk;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzj);
                                zzk = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
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
            zzj = zzkVar;
            zzjb.zza(zzk.class, zzkVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzl extends zzjb<zzl, zza> implements zzkm {
        private static final zzl zzf;
        private static volatile zzkx<zzl> zzg;
        private int zzc;
        private long zzd;
        private long zze;

        private zzl() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzl, zza> implements zzkm {
            private zza() {
                super(zzl.zzf);
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzjb$zza, com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzl>] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzl> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzl();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဂ\u0000\u0002ဂ\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzkx<zzl> zzkxVar2 = zzg;
                    zzkx<zzl> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzl.class) {
                            zzkx<zzl> zzkxVar4 = zzg;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzf);
                                zzg = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzl zzlVar = new zzl();
            zzf = zzlVar;
            zzjb.zza(zzl.class, zzlVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzm extends zzjb<zzm, zza> implements zzkm {
        private static final zzm zzf;
        private static volatile zzkx<zzm> zzg;
        private int zzc;
        private int zzd;
        private int zze;

        private zzm() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzm, zza> implements zzkm {
            private zza() {
                super(zzm.zzf);
            }

            public final zza zza(int i) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzm) this.zza).zza(i);
                return this;
            }

            public final zza zzb(int i) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzm) this.zza).zzc(i);
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(int i) {
            this.zzc |= 1;
            this.zzd = i;
        }

        public final void zzc(int i) {
            this.zzc |= 2;
            this.zze = i;
        }

        public static zza zza() {
            return zzf.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzjb$zza, com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzm>] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzm> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzm();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzkx<zzm> zzkxVar2 = zzg;
                    zzkx<zzm> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzm.class) {
                            zzkx<zzm> zzkxVar4 = zzg;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzf);
                                zzg = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzm zzmVar = new zzm();
            zzf = zzmVar;
            zzjb.zza(zzm.class, zzmVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzn extends zzjb<zzn, zza> implements zzkm {
        private static final zzn zzh;
        private static volatile zzkx<zzn> zzi;
        private int zzc;
        private zzd zzd;
        private int zze;
        private zzh zzf;
        private zzc zzg;

        private zzn() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzn, zza> implements zzkm {
            private zza() {
                super(zzn.zzh);
            }

            public final zza zza(zzd zzdVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzn) this.zza).zza(zzdVar);
                return this;
            }

            public final zza zza(zzd.zza zzaVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzn) this.zza).zza((zzd) ((zzjb) zzaVar.zzf()));
                return this;
            }

            public final zza zza(int i) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzn) this.zza).zza(i);
                return this;
            }

            public final zza zza(zzh zzhVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzn) this.zza).zza(zzhVar);
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(zzd zzdVar) {
            zzdVar.getClass();
            this.zzd = zzdVar;
            this.zzc |= 1;
        }

        public final void zza(int i) {
            this.zzc |= 2;
            this.zze = i;
        }

        public final void zza(zzh zzhVar) {
            zzhVar.getClass();
            this.zzf = zzhVar;
            this.zzc |= 4;
        }

        public static zza zza() {
            return zzh.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzjb$zza, com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzn>] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzn> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzn();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0011\u0004\u0000\u0000\u0000\u0001ဉ\u0000\u0002င\u0001\u0010ဉ\u0002\u0011ဉ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
                case 4:
                    return zzh;
                case 5:
                    zzkx<zzn> zzkxVar2 = zzi;
                    zzkx<zzn> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzn.class) {
                            zzkx<zzn> zzkxVar4 = zzi;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzh);
                                zzi = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzn zznVar = new zzn();
            zzh = zznVar;
            zzjb.zza(zzn.class, zznVar);
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static final class zzo extends zzjb<zzo, zza> implements zzkm {
        private static final zzo zzi;
        private static volatile zzkx<zzo> zzj;
        private int zzc;
        private zze zzd;
        private zzk zze;
        private zzi zzf;
        private int zzg;
        private boolean zzh;

        private zzo() {
        }

        /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzjb.zzb<zzo, zza> implements zzkm {
            private zza() {
                super(zzo.zzi);
            }

            public final zza zza(zzk.zza zzaVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzo) this.zza).zza((zzk) ((zzjb) zzaVar.zzf()));
                return this;
            }

            public final zza zza(zzi zziVar) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzo) this.zza).zza(zziVar);
                return this;
            }

            public final zza zza(boolean z) {
                if (this.zzb) {
                    zzb();
                    this.zzb = false;
                }
                ((zzo) this.zza).zza(true);
                return this;
            }

            /* synthetic */ zza(zzfk zzfkVar) {
                this();
            }
        }

        public final void zza(zzk zzkVar) {
            zzkVar.getClass();
            this.zze = zzkVar;
            this.zzc |= 2;
        }

        public final void zza(zzi zziVar) {
            zziVar.getClass();
            this.zzf = zziVar;
            this.zzc |= 4;
        }

        public final void zza(boolean z) {
            this.zzc |= 16;
            this.zzh = true;
        }

        public static zza zza() {
            return zzi.zzj();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzjb$zza, com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzo>] */
        @Override // com.google.android.gms.internal.vision.zzjb
        public final Object zza(int i, Object obj, Object obj2) {
            zzkx<zzo> zzkxVar;
            switch (zzfk.zza[i - 1]) {
                case 1:
                    return new zzo();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004င\u0003\u0005ဇ\u0004", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh"});
                case 4:
                    return zzi;
                case 5:
                    zzkx<zzo> zzkxVar2 = zzj;
                    zzkx<zzo> zzkxVar3 = zzkxVar2;
                    if (zzkxVar2 == null) {
                        synchronized (zzo.class) {
                            zzkx<zzo> zzkxVar4 = zzj;
                            zzkxVar = zzkxVar4;
                            if (zzkxVar4 == null) {
                                ?? zzaVar = new zzjb.zza(zzi);
                                zzj = zzaVar;
                                zzkxVar = zzaVar;
                            }
                        }
                        zzkxVar3 = zzkxVar;
                    }
                    return zzkxVar3;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzo zzoVar = new zzo();
            zzi = zzoVar;
            zzjb.zza(zzo.class, zzoVar);
        }
    }
}
