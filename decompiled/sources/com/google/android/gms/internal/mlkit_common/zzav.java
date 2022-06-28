package com.google.android.gms.internal.mlkit_common;

import androidx.core.app.FrameMetricsAggregator;
import com.google.android.gms.internal.mlkit_common.zzdf;
import com.google.android.gms.internal.mlkit_common.zzfq;
import com.google.android.gms.internal.mlkit_common.zzjf;
import com.google.android.gms.wallet.WalletConstants;
import org.telegram.ui.Cells.ChatMessageCell;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzav {

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zza extends zzfq<zza, C0011zza> implements zzhd {
        private static final zza zzg;
        private static volatile zzhl<zza> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, C0012zza> implements zzhd {
            private static final zzb zzh;
            private static volatile zzhl<zzb> zzi;
            private int zzc;
            private int zzd;
            private boolean zze;
            private zzae zzf;
            private zzam zzg;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zza$zzb$zza */
            /* loaded from: classes3.dex */
            public static final class C0012zza extends zzfq.zzb<zzb, C0012zza> implements zzhd {
                private C0012zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ C0012zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zza$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new C0012zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဉ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg"});
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

        private zza() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zza$zza */
        /* loaded from: classes3.dex */
        public static final class C0011zza extends zzfq.zzb<zza, C0011zza> implements zzhd {
            private C0011zza() {
                super(zza.zzg);
            }

            /* synthetic */ C0011zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zza>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zza> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zza();
                case 2:
                    return new C0011zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zza> zzhlVar2 = zzh;
                    zzhl<zza> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zza.class) {
                            zzhl<zza> zzhlVar4 = zzh;
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
            zza zzaVar = new zza();
            zzg = zzaVar;
            zzfq.zza(zza.class, zzaVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzaa extends zzfq<zzaa, zza> implements zzhd {
        private static final zzaa zzg;
        private static volatile zzhl<zzaa> zzh;
        private int zzc;
        private int zzd;
        private boolean zze;
        private String zzf = "";

        private zzaa() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzaa, zza> implements zzhd {
            private zza() {
                super(zzaa.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzaa>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzaa> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzaa();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဈ\u0002", new Object[]{"zzc", "zzd", zzal.zzb.zzb(), "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzaa> zzhlVar2 = zzh;
                    zzhl<zzaa> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzaa.class) {
                            zzhl<zzaa> zzhlVar4 = zzh;
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
            zzaa zzaaVar = new zzaa();
            zzg = zzaaVar;
            zzfq.zza(zzaa.class, zzaaVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzab extends zzfq<zzab, zza> implements zzhd {
        private static final zzab zzj;
        private static volatile zzhl<zzab> zzk;
        private int zzc;
        private long zzd;
        private long zze;
        private long zzf;
        private long zzg;
        private long zzh;
        private long zzi;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzab, zza> implements zzhd {
            private zza() {
                super(zzab.zzj);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        static {
            zzab zzabVar = new zzab();
            zzj = zzabVar;
            zzfq.zza(zzab.class, zzabVar);
        }

        private zzab() {
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzab>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzab> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzab();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဃ\u0000\u0002ဃ\u0001\u0003ဃ\u0002\u0004ဃ\u0003\u0005ဃ\u0004\u0006ဃ\u0005", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzhl<zzab> zzhlVar2 = zzk;
                    zzhl<zzab> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzab.class) {
                            zzhl<zzab> zzhlVar4 = zzk;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzj);
                                zzk = zzaVar;
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
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzac extends zzfq<zzac, zza> implements zzhd {
        private static final zzac zzj;
        private static volatile zzhl<zzac> zzk;
        private int zzc;
        private int zzd;
        private int zze;
        private int zzf;
        private int zzg;
        private boolean zzh;
        private float zzi;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            UNKNOWN_CLASSIFICATIONS(0),
            NO_CLASSIFICATIONS(1),
            ALL_CLASSIFICATIONS(2);
            
            private static final zzfu<zzb> zzd = new zzbk();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzbl.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zze = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzc implements zzfv {
            UNKNOWN_CONTOURS(0),
            NO_CONTOURS(1),
            ALL_CONTOURS(2);
            
            private static final zzfu<zzc> zzd = new zzbn();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzbm.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zzc(int i) {
                this.zze = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzd implements zzfv {
            UNKNOWN_LANDMARKS(0),
            NO_LANDMARKS(1),
            ALL_LANDMARKS(2);
            
            private static final zzfu<zzd> zzd = new zzbo();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzbp.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zzd(int i) {
                this.zze = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zze implements zzfv {
            UNKNOWN_PERFORMANCE(0),
            FAST(1),
            ACCURATE(2);
            
            private static final zzfu<zze> zzd = new zzbr();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzbq.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zze(int i) {
                this.zze = i;
            }
        }

        private zzac() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzac, zza> implements zzhd {
            private zza() {
                super(zzac.zzj);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzac>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzac> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzac();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဌ\u0003\u0005ဇ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", zzd.zzb(), "zze", zzb.zzb(), "zzf", zze.zzb(), "zzg", zzc.zzb(), "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzhl<zzac> zzhlVar2 = zzk;
                    zzhl<zzac> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzac.class) {
                            zzhl<zzac> zzhlVar4 = zzk;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzj);
                                zzk = zzaVar;
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
            zzac zzacVar = new zzac();
            zzj = zzacVar;
            zzfq.zza(zzac.class, zzacVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzae extends zzfq<zzae, zza> implements zzhd {
        private static final zzae zzg;
        private static volatile zzhl<zzae> zzh;
        private int zzc;
        private int zzd;
        private int zze;
        private int zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            UNKNOWN_FORMAT(0),
            NV16(1),
            NV21(2),
            YV12(3),
            YUV_420_888(7),
            JPEG(8),
            BITMAP(4),
            CM_SAMPLE_BUFFER_REF(5),
            UI_IMAGE(6);
            
            private static final zzfu<zzb> zzj = new zzbs();
            private final int zzk;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzk;
            }

            public static zzfx zzb() {
                return zzbt.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzk + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzk = i;
            }
        }

        private zzae() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzae, zza> implements zzhd {
            private zza() {
                super(zzae.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzae>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzae> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzae();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဋ\u0001\u0003ဋ\u0002", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzae> zzhlVar2 = zzh;
                    zzhl<zzae> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzae.class) {
                            zzhl<zzae> zzhlVar4 = zzh;
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
            zzae zzaeVar = new zzae();
            zzg = zzaeVar;
            zzfq.zza(zzae.class, zzaeVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzaf extends zzfq<zzaf, zza> implements zzhd {
        private static final zzaf zzl;
        private static volatile zzhl<zzaf> zzm;
        private int zzc;
        private long zzd;
        private int zze;
        private boolean zzf;
        private boolean zzg;
        private boolean zzh;
        private boolean zzi;
        private int zzj;
        private zzfy<zzbf> zzk = zzl();

        private zzaf() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzaf, zza> implements zzhd {
            private zza() {
                super(zzaf.zzl);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzaf>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzaf> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzaf();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0001\u0000\u0001ဃ\u0000\u0002ဌ\u0001\u0003ဇ\u0002\u0004ဇ\u0003\u0005ဇ\u0004\u0006ဇ\u0005\u0007ဋ\u0006\b\u001b", new Object[]{"zzc", "zzd", "zze", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", zzbf.class});
                case 4:
                    return zzl;
                case 5:
                    zzhl<zzaf> zzhlVar2 = zzm;
                    zzhl<zzaf> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzaf.class) {
                            zzhl<zzaf> zzhlVar4 = zzm;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzl);
                                zzm = zzaVar;
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
            zzaf zzafVar = new zzaf();
            zzl = zzafVar;
            zzfq.zza(zzaf.class, zzafVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzag extends zzfq<zzag, zzb> implements zzhd {
        private static final zzag zzk;
        private static volatile zzhl<zzag> zzl;
        private int zzc;
        private long zzd;
        private int zze;
        private int zzf;
        private int zzg;
        private int zzh;
        private int zzi;
        private int zzj;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            SOURCE_UNKNOWN(0),
            BITMAP(1),
            BYTEARRAY(2),
            BYTEBUFFER(3),
            FILEPATH(4),
            ANDROID_MEDIA_IMAGE(5);
            
            private static final zzfu<zza> zzg = new zzbv();
            private final int zzh;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzh;
            }

            public static zzfx zzb() {
                return zzbu.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzh = i;
            }
        }

        static {
            zzag zzagVar = new zzag();
            zzk = zzagVar;
            zzfq.zza(zzag.class, zzagVar);
        }

        private zzag() {
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzag>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzag> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzag();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzk, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0000\u0000\u0001ဃ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဋ\u0003\u0005ဋ\u0004\u0006ဋ\u0005\u0007ဋ\u0006", new Object[]{"zzc", "zzd", "zze", zza.zzb(), "zzf", zzae.zzb.zzb(), "zzg", "zzh", "zzi", "zzj"});
                case 4:
                    return zzk;
                case 5:
                    zzhl<zzag> zzhlVar2 = zzl;
                    zzhl<zzag> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzag.class) {
                            zzhl<zzag> zzhlVar4 = zzl;
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

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzag, zzb> implements zzhd {
            private zzb() {
                super(zzag.zzk);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzah extends zzfq<zzah, zza> implements zzhd {
        private static final zzah zzg;
        private static volatile zzhl<zzah> zzh;
        private int zzc;
        private int zzd;
        private boolean zze;
        private String zzf = "";

        private zzah() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzah, zza> implements zzhd {
            private zza() {
                super(zzah.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzah>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzah> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzah();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဈ\u0002", new Object[]{"zzc", "zzd", zzal.zzb.zzb(), "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzah> zzhlVar2 = zzh;
                    zzhl<zzah> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzah.class) {
                            zzhl<zzah> zzhlVar4 = zzh;
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
            zzah zzahVar = new zzah();
            zzg = zzahVar;
            zzfq.zza(zzah.class, zzahVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzai extends zzfq<zzai, zza> implements zzhd {
        private static final zzai zzg;
        private static volatile zzhl<zzai> zzh;
        private int zzc;
        private float zzd;
        private float zze;
        private float zzf;

        private zzai() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzai, zza> implements zzhd {
            private zza() {
                super(zzai.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzai>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzai> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzai();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ခ\u0000\u0002ခ\u0001\u0003ခ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzai> zzhlVar2 = zzh;
                    zzhl<zzai> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzai.class) {
                            zzhl<zzai> zzhlVar4 = zzh;
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
            zzai zzaiVar = new zzai();
            zzg = zzaiVar;
            zzfq.zza(zzai.class, zzaiVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzaj extends zzfq<zzaj, zzb> implements zzhd {
        private static final zzaj zze;
        private static volatile zzhl<zzaj> zzf;
        private int zzc;
        private int zzd;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN(0),
            TRANSLATE(1);
            
            private static final zzfu<zza> zzc = new zzbx();
            private final int zzd;

            zza(int i) {
                this.zzd = i;
            }

            public static zza zza(int i) {
                switch (i) {
                    case 0:
                        return UNKNOWN;
                    case 1:
                        return TRANSLATE;
                    default:
                        return null;
                }
            }

            public static zzfx zzb() {
                return zzbw.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzd + " name=" + name() + '>';
            }

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzd;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzaj, zzb> implements zzhd {
            private zzb() {
                super(zzaj.zze);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }

            public final zzb zza(zza zzaVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzaj) this.zza).zza(zzaVar);
                return this;
            }
        }

        static {
            zzaj zzajVar = new zzaj();
            zze = zzajVar;
            zzfq.zza(zzaj.class, zzajVar);
        }

        private zzaj() {
        }

        public static zzb zza() {
            return zze.zzh();
        }

        public final void zza(zza zzaVar) {
            this.zzd = zzaVar.zza();
            this.zzc |= 1;
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzaj>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzaj> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzaj();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ဌ\u0000", new Object[]{"zzc", "zzd", zza.zzb()});
                case 4:
                    return zze;
                case 5:
                    zzhl<zzaj> zzhlVar2 = zzf;
                    zzhl<zzaj> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzaj.class) {
                            zzhl<zzaj> zzhlVar4 = zzf;
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
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzak extends zzfq<zzak, zzb> implements zzhd {
        private static final zzak zzk;
        private static volatile zzhl<zzak> zzl;
        private int zzc;
        private zzam zzd;
        private long zze;
        private int zzf;
        private long zzg;
        private int zzh;
        private long zzi;
        private zzfw zzj = zzk();

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN_STATUS(0),
            EXPLICITLY_REQUESTED(1),
            IMPLICITLY_REQUESTED(2),
            MODEL_INFO_RETRIEVAL_SUCCEEDED(3),
            MODEL_INFO_RETRIEVAL_FAILED(4),
            SCHEDULED(5),
            DOWNLOADING(6),
            SUCCEEDED(7),
            FAILED(8),
            LIVE(9),
            UPDATE_AVAILABLE(10),
            DOWNLOADED(11),
            STARTED(12);
            
            private static final zzfu<zza> zzn = new zzbz();
            private final int zzo;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzo;
            }

            public static zzfx zzb() {
                return zzby.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzo + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzo = i;
            }
        }

        private zzak() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzak, zzb> implements zzhd {
            private zzb() {
                super(zzak.zzk);
            }

            public final zzb zza(zzam zzamVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzak) this.zza).zza(zzamVar);
                return this;
            }

            public final zzb zza(long j) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzak) this.zza).zza(j);
                return this;
            }

            public final zzb zza(com.google.android.gms.internal.mlkit_common.zzbf zzbfVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzak) this.zza).zza(zzbfVar);
                return this;
            }

            public final zzb zzb(long j) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzak) this.zza).zzb(j);
                return this;
            }

            public final zzb zza(zza zzaVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzak) this.zza).zza(zzaVar);
                return this;
            }

            public final zzb zzc(long j) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzak) this.zza).zzc(j);
                return this;
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        public final void zza(zzam zzamVar) {
            zzamVar.getClass();
            this.zzd = zzamVar;
            this.zzc |= 1;
        }

        public final void zza(long j) {
            this.zzc |= 2;
            this.zze = j;
        }

        public final void zza(com.google.android.gms.internal.mlkit_common.zzbf zzbfVar) {
            this.zzf = zzbfVar.zza();
            this.zzc |= 4;
        }

        public final void zzb(long j) {
            this.zzc |= 8;
            this.zzg = j;
        }

        public final void zza(zza zzaVar) {
            this.zzh = zzaVar.zza();
            this.zzc |= 16;
        }

        public final void zzc(long j) {
            this.zzc |= 32;
            this.zzi = j;
        }

        public static zzb zza() {
            return zzk.zzh();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzak>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzak> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzak();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzk, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဃ\u0001\u0003ဌ\u0002\u0004ဃ\u0003\u0005ဌ\u0004\u0006ဂ\u0005\u0007\u0016", new Object[]{"zzc", "zzd", "zze", "zzf", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zzg", "zzh", zza.zzb(), "zzi", "zzj"});
                case 4:
                    return zzk;
                case 5:
                    zzhl<zzak> zzhlVar2 = zzl;
                    zzhl<zzak> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzak.class) {
                            zzhl<zzak> zzhlVar4 = zzl;
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
            zzak zzakVar = new zzak();
            zzk = zzakVar;
            zzfq.zza(zzak.class, zzakVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzal extends zzfq<zzal, zza> implements zzhd {
        private static final zzal zzl;
        private static volatile zzhl<zzal> zzm;
        private int zzc;
        private int zzf;
        private int zzi;
        private long zzj;
        private boolean zzk;
        private String zzd = "";
        private String zze = "";
        private String zzg = "";
        private String zzh = "";

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            TYPE_UNKNOWN(0),
            CUSTOM(1),
            AUTOML_IMAGE_LABELING(2),
            BASE_TRANSLATE(3),
            CUSTOM_OBJECT_DETECTION(4),
            CUSTOM_IMAGE_LABELING(5),
            BASE_ENTITY_EXTRACTION(6);
            
            private static final zzfu<zzb> zzh = new zzca();
            private final int zzi;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzi;
            }

            public static zzfx zzb() {
                return zzcb.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzi + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzi = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzc implements zzfv {
            SOURCE_UNKNOWN(0),
            APP_ASSET(1),
            LOCAL(2),
            CLOUD(3),
            SDK_BUILT_IN(4),
            URI(5);
            
            private static final zzfu<zzc> zzg = new zzcd();
            private final int zzh;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzh;
            }

            public static zzfx zzb() {
                return zzcc.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
            }

            zzc(int i) {
                this.zzh = i;
            }
        }

        private zzal() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzal, zza> implements zzhd {
            private zza() {
                super(zzal.zzl);
            }

            public final zza zza(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzal) this.zza).zza(str);
                return this;
            }

            public final zza zza(zzc zzcVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzal) this.zza).zza(zzcVar);
                return this;
            }

            public final zza zzb(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzal) this.zza).zzb(str);
                return this;
            }

            public final zza zza(zzb zzbVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzal) this.zza).zza(zzbVar);
                return this;
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        public final void zza(String str) {
            str.getClass();
            this.zzc |= 1;
            this.zzd = str;
        }

        public final void zza(zzc zzcVar) {
            this.zzf = zzcVar.zza();
            this.zzc |= 4;
        }

        public final void zzb(String str) {
            str.getClass();
            this.zzc |= 16;
            this.zzh = str;
        }

        public final void zza(zzb zzbVar) {
            this.zzi = zzbVar.zza();
            this.zzc |= 32;
        }

        public static zza zza() {
            return zzl.zzh();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzal>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzal> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzal();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဌ\u0002\u0004ဈ\u0003\u0005ဈ\u0004\u0006ဌ\u0005\u0007ဃ\u0006\bဇ\u0007", new Object[]{"zzc", "zzd", "zze", "zzf", zzc.zzb(), "zzg", "zzh", "zzi", zzb.zzb(), "zzj", "zzk"});
                case 4:
                    return zzl;
                case 5:
                    zzhl<zzal> zzhlVar2 = zzm;
                    zzhl<zzal> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzal.class) {
                            zzhl<zzal> zzhlVar4 = zzm;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzl);
                                zzm = zzaVar;
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
            zzal zzalVar = new zzal();
            zzl = zzalVar;
            zzfq.zza(zzal.class, zzalVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzam extends zzfq<zzam, zza> implements zzhd {
        private static final zzam zzh;
        private static volatile zzhl<zzam> zzi;
        private int zzc;
        private zzal zzd;
        private zzb zze;
        private zzb zzf;
        private boolean zzg;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzh;
            private static volatile zzhl<zzb> zzi;
            private int zzc;
            private boolean zzd;
            private boolean zze;
            private boolean zzf;
            private boolean zzg;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzam$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဇ\u0000\u0002ဇ\u0001\u0003ဇ\u0002\u0004ဇ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
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

        private zzam() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzam, zza> implements zzhd {
            private zza() {
                super(zzam.zzh);
            }

            public final zza zza(zzal.zza zzaVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzam) this.zza).zza((zzal) ((zzfq) zzaVar.zzg()));
                return this;
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        public final void zza(zzal zzalVar) {
            zzalVar.getClass();
            this.zzd = zzalVar;
            this.zzc |= 1;
        }

        public static zza zza() {
            return zzh.zzh();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzam>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzam> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzam();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဇ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
                case 4:
                    return zzh;
                case 5:
                    zzhl<zzam> zzhlVar2 = zzi;
                    zzhl<zzam> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzam.class) {
                            zzhl<zzam> zzhlVar4 = zzi;
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
            zzam zzamVar = new zzam();
            zzh = zzamVar;
            zzfq.zza(zzam.class, zzamVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzan extends zzfq<zzan, zza> implements zzhd {
        private static final zzan zzh;
        private static volatile zzhl<zzan> zzi;
        private int zzc;
        private int zzd;
        private float zze;
        private int zzf;
        private int zzg;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            CATEGORY_UNKNOWN(0),
            CATEGORY_HOME_GOOD(1),
            CATEGORY_FASHION_GOOD(2),
            CATEGORY_ANIMAL(3),
            CATEGORY_FOOD(4),
            CATEGORY_PLACE(5),
            CATEGORY_PLANT(6);
            
            private static final zzfu<zzb> zzh = new zzce();
            private final int zzi;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzi;
            }

            public static zzfx zzb() {
                return zzcf.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzi + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzi = i;
            }
        }

        private zzan() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzan, zza> implements zzhd {
            private zza() {
                super(zzan.zzh);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzan>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzan> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzan();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ခ\u0001\u0003င\u0002\u0004ဋ\u0003", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf", "zzg"});
                case 4:
                    return zzh;
                case 5:
                    zzhl<zzan> zzhlVar2 = zzi;
                    zzhl<zzan> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzan.class) {
                            zzhl<zzan> zzhlVar4 = zzi;
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
            zzan zzanVar = new zzan();
            zzh = zzanVar;
            zzfq.zza(zzan.class, zzanVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzao extends zzfq<zzao, zzc> implements zzhd {
        private static final zzfz<Integer, zza> zzg = new zzch();
        private static final zzfz<Integer, zzb> zzi = new zzcg();
        private static final zzao zzk;
        private static volatile zzhl<zzao> zzl;
        private int zzc;
        private zzaf zzd;
        private zzdf.zza zze;
        private zzfw zzf = zzk();
        private zzfw zzh = zzk();
        private zzae zzj;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            FORMAT_UNKNOWN(0),
            FORMAT_CODE_128(1),
            FORMAT_CODE_39(2),
            FORMAT_CODE_93(4),
            FORMAT_CODABAR(8),
            FORMAT_DATA_MATRIX(16),
            FORMAT_EAN_13(32),
            FORMAT_EAN_8(64),
            FORMAT_ITF(128),
            FORMAT_QR_CODE(256),
            FORMAT_UPC_A(512),
            FORMAT_UPC_E(1024),
            FORMAT_PDF417(2048),
            FORMAT_AZTEC(4096);
            
            private static final zzfu<zza> zzo = new zzci();
            private final int zzp;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzp;
            }

            public static zzfx zzb() {
                return zzcj.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzp + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzp = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            TYPE_UNKNOWN(0),
            TYPE_CONTACT_INFO(1),
            TYPE_EMAIL(2),
            TYPE_ISBN(3),
            TYPE_PHONE(4),
            TYPE_PRODUCT(5),
            TYPE_SMS(6),
            TYPE_TEXT(7),
            TYPE_URL(8),
            TYPE_WIFI(9),
            TYPE_GEO(10),
            TYPE_CALENDAR_EVENT(11),
            TYPE_DRIVER_LICENSE(12);
            
            private static final zzfu<zzb> zzn = new zzcl();
            private final int zzo;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzo;
            }

            public static zzfx zzb() {
                return zzck.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzo + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzo = i;
            }
        }

        private zzao() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzc extends zzfq.zzb<zzao, zzc> implements zzhd {
            private zzc() {
                super(zzao.zzk);
            }

            /* synthetic */ zzc(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzao>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzao> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzao();
                case 2:
                    return new zzc(null);
                case 3:
                    return zza(zzk, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001e\u0004\u001e\u0005ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf", zza.zzb(), "zzh", zzb.zzb(), "zzj"});
                case 4:
                    return zzk;
                case 5:
                    zzhl<zzao> zzhlVar2 = zzl;
                    zzhl<zzao> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzao.class) {
                            zzhl<zzao> zzhlVar4 = zzl;
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

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_common.zzch, com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzao$zza>] */
        /* JADX WARN: Type inference failed for: r0v1, types: [com.google.android.gms.internal.mlkit_common.zzcg, com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzao$zzb>] */
        static {
            zzao zzaoVar = new zzao();
            zzk = zzaoVar;
            zzfq.zza(zzao.class, zzaoVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzap extends zzfq<zzap, zza> implements zzhd {
        private static final zzap zzj;
        private static volatile zzhl<zzap> zzk;
        private int zzc;
        private zzaf zzd;
        private zzdf.zzb zze;
        private zzae zzf;
        private zzac zzg;
        private int zzh;
        private int zzi;

        private zzap() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzap, zza> implements zzhd {
            private zza() {
                super(zzap.zzj);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzap>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzap> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzap();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဉ\u0003\u0005ဋ\u0004\u0006ဋ\u0005", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzhl<zzap> zzhlVar2 = zzk;
                    zzhl<zzap> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzap.class) {
                            zzhl<zzap> zzhlVar4 = zzk;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzj);
                                zzk = zzaVar;
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
            zzap zzapVar = new zzap();
            zzj = zzapVar;
            zzfq.zza(zzap.class, zzapVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzaq extends zzfq<zzaq, zza> implements zzhd {
        private static final zzaq zzf;
        private static volatile zzhl<zzaq> zzg;
        private int zzc;
        private zzat zzd;
        private int zze;

        private zzaq() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzaq, zza> implements zzhd {
            private zza() {
                super(zzaq.zzf);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzaq>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzaq> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzaq();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဌ\u0001", new Object[]{"zzc", "zzd", "zze", com.google.android.gms.internal.mlkit_common.zzbf.zzb()});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzaq> zzhlVar2 = zzg;
                    zzhl<zzaq> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzaq.class) {
                            zzhl<zzaq> zzhlVar4 = zzg;
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
            zzaq zzaqVar = new zzaq();
            zzf = zzaqVar;
            zzfq.zza(zzaq.class, zzaqVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzar extends zzfq<zzar, zza> implements zzhd {
        private static final zzar zzi;
        private static volatile zzhl<zzar> zzj;
        private int zzc;
        private zzaf zzd;
        private zzat zze;
        private zzae zzf;
        private int zzg;
        private float zzh;

        private zzar() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzar, zza> implements zzhd {
            private zza() {
                super(zzar.zzi);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzar>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzar> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzar();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဋ\u0003\u0005ခ\u0004", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh"});
                case 4:
                    return zzi;
                case 5:
                    zzhl<zzar> zzhlVar2 = zzj;
                    zzhl<zzar> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzar.class) {
                            zzhl<zzar> zzhlVar4 = zzj;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzi);
                                zzj = zzaVar;
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
            zzar zzarVar = new zzar();
            zzi = zzarVar;
            zzfq.zza(zzar.class, zzarVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzas extends zzfq<zzas, zza> implements zzhd {
        private static final zzfz<Integer, com.google.android.gms.internal.mlkit_common.zzbf> zzf = new zzcm();
        private static final zzas zzj;
        private static volatile zzhl<zzas> zzk;
        private int zzc;
        private zzat zzd;
        private long zzg;
        private long zzh;
        private zzfw zze = zzk();
        private zzfy<zzbf> zzi = zzl();

        private zzas() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzas, zza> implements zzhd {
            private zza() {
                super(zzas.zzj);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzas>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzas> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzas();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဉ\u0000\u0002\u001e\u0003ဃ\u0001\u0004ဃ\u0002\u0005\u001b", new Object[]{"zzc", "zzd", "zze", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zzg", "zzh", "zzi", zzbf.class});
                case 4:
                    return zzj;
                case 5:
                    zzhl<zzas> zzhlVar2 = zzk;
                    zzhl<zzas> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzas.class) {
                            zzhl<zzas> zzhlVar4 = zzk;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzj);
                                zzk = zzaVar;
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

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzbf>, com.google.android.gms.internal.mlkit_common.zzcm] */
        static {
            zzas zzasVar = new zzas();
            zzj = zzasVar;
            zzfq.zza(zzas.class, zzasVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzat extends zzfq<zzat, zza> implements zzhd {
        private static final zzat zzg;
        private static volatile zzhl<zzat> zzh;
        private int zzc;
        private int zzd;
        private float zze;
        private zzam zzf;

        private zzat() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzat, zza> implements zzhd {
            private zza() {
                super(zzat.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzat>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzat> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzat();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဋ\u0000\u0002ခ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzat> zzhlVar2 = zzh;
                    zzhl<zzat> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzat.class) {
                            zzhl<zzat> zzhlVar4 = zzh;
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
            zzat zzatVar = new zzat();
            zzg = zzatVar;
            zzfq.zza(zzat.class, zzatVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzau extends zzfq<zzau, zza> implements zzhd {
        private static final zzau zzh;
        private static volatile zzhl<zzau> zzi;
        private int zzc;
        private zzaf zzd;
        private zzai zze;
        private zzc zzf;
        private zzd zzg;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzf;
            private static volatile zzhl<zzb> zzg;
            private int zzc;
            private float zzd;
            private String zze = "";

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzf);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzau$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ခ\u0000\u0002ဈ\u0001", new Object[]{"zzc", "zzd", "zze"});
                    case 4:
                        return zzf;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzg;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzg;
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
                zzb zzbVar = new zzb();
                zzf = zzbVar;
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzc extends zzfq<zzc, zza> implements zzhd {
            private static final zzc zze;
            private static volatile zzhl<zzc> zzf;
            private int zzc;
            private zzb zzd;

            private zzc() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzc, zza> implements zzhd {
                private zza() {
                    super(zzc.zze);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzau$zzc>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzc> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzc();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ဉ\u0000", new Object[]{"zzc", "zzd"});
                    case 4:
                        return zze;
                    case 5:
                        zzhl<zzc> zzhlVar2 = zzf;
                        zzhl<zzc> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzc.class) {
                                zzhl<zzc> zzhlVar4 = zzf;
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
                zzc zzcVar = new zzc();
                zze = zzcVar;
                zzfq.zza(zzc.class, zzcVar);
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzd extends zzfq<zzd, zza> implements zzhd {
            private static final zzd zzd;
            private static volatile zzhl<zzd> zze;
            private zzfy<zzb> zzc = zzl();

            private zzd() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzd, zza> implements zzhd {
                private zza() {
                    super(zzd.zzd);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzau$zzd>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzd> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzd();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzc", zzb.class});
                    case 4:
                        return zzd;
                    case 5:
                        zzhl<zzd> zzhlVar2 = zze;
                        zzhl<zzd> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzd.class) {
                                zzhl<zzd> zzhlVar4 = zze;
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
                zzd zzdVar = new zzd();
                zzd = zzdVar;
                zzfq.zza(zzd.class, zzdVar);
            }
        }

        private zzau() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzau, zza> implements zzhd {
            private zza() {
                super(zzau.zzh);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzau>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzau> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzau();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
                case 4:
                    return zzh;
                case 5:
                    zzhl<zzau> zzhlVar2 = zzi;
                    zzhl<zzau> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzau.class) {
                            zzhl<zzau> zzhlVar4 = zzi;
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
            zzau zzauVar = new zzau();
            zzh = zzauVar;
            zzfq.zza(zzau.class, zzauVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zzav */
    /* loaded from: classes3.dex */
    public static final class C0013zzav extends zzfq<C0013zzav, zza> implements zzhd {
        private static final C0013zzav zzf;
        private static volatile zzhl<C0013zzav> zzg;
        private int zzc;
        private zzaw zzd;
        private int zze;

        private C0013zzav() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zzav$zza */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<C0013zzav, zza> implements zzhd {
            private zza() {
                super(C0013zzav.zzf);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzav>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<C0013zzav> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new C0013zzav();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဌ\u0001", new Object[]{"zzc", "zzd", "zze", com.google.android.gms.internal.mlkit_common.zzbf.zzb()});
                case 4:
                    return zzf;
                case 5:
                    zzhl<C0013zzav> zzhlVar2 = zzg;
                    zzhl<C0013zzav> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (C0013zzav.class) {
                            zzhl<C0013zzav> zzhlVar4 = zzg;
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
            C0013zzav c0013zzav = new C0013zzav();
            zzf = c0013zzav;
            zzfq.zza(C0013zzav.class, c0013zzav);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzaw extends zzfq<zzaw, zza> implements zzhd {
        private static final zzaw zzj;
        private static volatile zzhl<zzaw> zzk;
        private int zzc;
        private int zzd;
        private boolean zze;
        private boolean zzf;
        private int zzg;
        private float zzh;
        private zzam zzi;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            MODE_UNSPECIFIED(0),
            STREAM(1),
            SINGLE_IMAGE(2);
            
            private static final zzfu<zzb> zzd = new zzcn();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzco.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zze = i;
            }
        }

        private zzaw() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzaw, zza> implements zzhd {
            private zza() {
                super(zzaw.zzj);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzaw>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzaw> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzaw();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဇ\u0002\u0004ဋ\u0003\u0005ခ\u0004\u0006ဉ\u0005", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf", "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzhl<zzaw> zzhlVar2 = zzk;
                    zzhl<zzaw> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzaw.class) {
                            zzhl<zzaw> zzhlVar4 = zzk;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzj);
                                zzk = zzaVar;
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
            zzaw zzawVar = new zzaw();
            zzj = zzawVar;
            zzfq.zza(zzaw.class, zzawVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzax extends zzfq<zzax, zza> implements zzhd {
        private static final zzax zzh;
        private static volatile zzhl<zzax> zzi;
        private int zzc;
        private zzaf zzd;
        private zzae zze;
        private zzaw zzf;
        private zzfy<zzan> zzg = zzl();

        private zzax() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzax, zza> implements zzhd {
            private zza() {
                super(zzax.zzh);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzax>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzax> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzax();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", zzan.class});
                case 4:
                    return zzh;
                case 5:
                    zzhl<zzax> zzhlVar2 = zzi;
                    zzhl<zzax> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzax.class) {
                            zzhl<zzax> zzhlVar4 = zzi;
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
            zzax zzaxVar = new zzax();
            zzh = zzaxVar;
            zzfq.zza(zzax.class, zzaxVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzay extends zzfq<zzay, zza> implements zzhd {
        private static final zzay zzi;
        private static volatile zzhl<zzay> zzj;
        private int zzc;
        private zzaw zzd;
        private int zze;
        private long zzf;
        private long zzg;
        private zzfy<zzbf> zzh = zzl();

        private zzay() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzay, zza> implements zzhd {
            private zza() {
                super(zzay.zzi);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzay>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzay> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzay();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဌ\u0001\u0003ဃ\u0002\u0004ဃ\u0003\u0005\u001b", new Object[]{"zzc", "zzd", "zze", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zzf", "zzg", "zzh", zzbf.class});
                case 4:
                    return zzi;
                case 5:
                    zzhl<zzay> zzhlVar2 = zzj;
                    zzhl<zzay> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzay.class) {
                            zzhl<zzay> zzhlVar4 = zzj;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzi);
                                zzj = zzaVar;
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
            zzay zzayVar = new zzay();
            zzi = zzayVar;
            zzfq.zza(zzay.class, zzayVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzaz extends zzfq<zzaz, zza> implements zzhd {
        private static final zzaz zzg;
        private static volatile zzhl<zzaz> zzh;
        private int zzc;
        private zzaf zzd;
        private zzae zze;
        private zzba zzf;

        private zzaz() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzaz, zza> implements zzhd {
            private zza() {
                super(zzaz.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzaz>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzaz> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzaz();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzaz> zzhlVar2 = zzh;
                    zzhl<zzaz> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzaz.class) {
                            zzhl<zzaz> zzhlVar4 = zzh;
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
            zzaz zzazVar = new zzaz();
            zzg = zzazVar;
            zzfq.zza(zzaz.class, zzazVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzb extends zzfq<zzb, zza> implements zzhd {
        private static final zzb zzg;
        private static volatile zzhl<zzb> zzh;
        private int zzc;
        private C0014zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zzb$zzb */
        /* loaded from: classes3.dex */
        public static final class C0014zzb extends zzfq<C0014zzb, zza> implements zzhd {
            private static final C0014zzb zzi;
            private static volatile zzhl<C0014zzb> zzj;
            private int zzc;
            private int zzd;
            private boolean zze;
            private zzfy<zzy.zzb> zzf = zzl();
            private zzfy<zzy.zzb> zzg = zzl();
            private zzam zzh;

            private C0014zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zzb$zzb$zza */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<C0014zzb, zza> implements zzhd {
                private zza() {
                    super(C0014zzb.zzi);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzb$zzb>] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<C0014zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new C0014zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003\u001b\u0004\u001b\u0005ဉ\u0002", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", zzy.zzb.class, "zzg", zzy.zzb.class, "zzh"});
                    case 4:
                        return zzi;
                    case 5:
                        zzhl<C0014zzb> zzhlVar2 = zzj;
                        zzhl<C0014zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (C0014zzb.class) {
                                zzhl<C0014zzb> zzhlVar4 = zzj;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzi);
                                    zzj = zzaVar;
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
                C0014zzb c0014zzb = new C0014zzb();
                zzi = c0014zzb;
                zzfq.zza(C0014zzb.class, c0014zzb);
            }
        }

        private zzb() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
            private zza() {
                super(zzb.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzb>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzb> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
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

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzba extends zzfq<zzba, zzb> implements zzhd {
        private static final zzba zzg;
        private static volatile zzhl<zzba> zzh;
        private int zzc;
        private int zzd;
        private int zze;
        private int zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            INVALID_MODE(0),
            STREAM(1),
            SINGLE_IMAGE(2);
            
            private static final zzfu<zza> zzd = new zzcq();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzcp.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zza(int i) {
                this.zze = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzc implements zzfv {
            UNKNOWN_PERFORMANCE(0),
            FAST(1),
            ACCURATE(2);
            
            private static final zzfu<zzc> zzd = new zzcr();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return zzcs.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zzc(int i) {
                this.zze = i;
            }
        }

        private zzba() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzba, zzb> implements zzhd {
            private zzb() {
                super(zzba.zzg);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzba>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzba> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzba();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002", new Object[]{"zzc", "zzd", zza.zzb(), "zze", zzc.zzb(), "zzf", zzc.zzb()});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzba> zzhlVar2 = zzh;
                    zzhl<zzba> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzba.class) {
                            zzhl<zzba> zzhlVar4 = zzh;
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
            zzba zzbaVar = new zzba();
            zzg = zzbaVar;
            zzfq.zza(zzba.class, zzbaVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbb extends zzfq<zzbb, zza> implements zzhd {
        private static final zzbb zzf;
        private static volatile zzhl<zzbb> zzg;
        private int zzc;
        private zzaf zzd;
        private zzae zze;

        private zzbb() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzbb, zza> implements zzhd {
            private zza() {
                super(zzbb.zzf);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbb>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbb> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbb();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzbb> zzhlVar2 = zzg;
                    zzhl<zzbb> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbb.class) {
                            zzhl<zzbb> zzhlVar4 = zzg;
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
            zzbb zzbbVar = new zzbb();
            zzf = zzbbVar;
            zzfq.zza(zzbb.class, zzbbVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbc extends zzfq<zzbc, zzb> implements zzhd {
        private static final zzbc zzi;
        private static volatile zzhl<zzbc> zzj;
        private int zzc;
        private zzaf zzd;
        private zzfy<zzc> zze = zzl();
        private int zzf;
        private int zzg;
        private int zzh;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            NO_ERROR(0),
            STATUS_SENSITIVE_TOPIC(1),
            STATUS_QUALITY_THRESHOLDED(2),
            STATUS_INTERNAL_ERROR(3),
            STATUS_NOT_SUPPORTED_LANGUAGE(101),
            STATUS_32_BIT_CPU(1001),
            STATUS_32_BIT_APP(1002);
            
            private static final zzfu<zza> zzh = new zzcu();
            private final int zzi;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzi;
            }

            public static zzfx zzb() {
                return zzct.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzi + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzi = i;
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzc extends zzfq<zzc, zza> implements zzhd {
            private static final zzc zze;
            private static volatile zzhl<zzc> zzf;
            private int zzc;
            private float zzd;

            private zzc() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzc, zza> implements zzhd {
                private zza() {
                    super(zzc.zze);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbc$zzc>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzc> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzc();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ခ\u0000", new Object[]{"zzc", "zzd"});
                    case 4:
                        return zze;
                    case 5:
                        zzhl<zzc> zzhlVar2 = zzf;
                        zzhl<zzc> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzc.class) {
                                zzhl<zzc> zzhlVar4 = zzf;
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
                zzc zzcVar = new zzc();
                zze = zzcVar;
                zzfq.zza(zzc.class, zzcVar);
            }
        }

        private zzbc() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzbc, zzb> implements zzhd {
            private zzb() {
                super(zzbc.zzi);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbc>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbc> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbc();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0001\u0000\u0001ဉ\u0000\u0002\u001b\u0003ဌ\u0001\u0004င\u0002\u0005င\u0003", new Object[]{"zzc", "zzd", "zze", zzc.class, "zzf", zza.zzb(), "zzg", "zzh"});
                case 4:
                    return zzi;
                case 5:
                    zzhl<zzbc> zzhlVar2 = zzj;
                    zzhl<zzbc> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbc.class) {
                            zzhl<zzbc> zzhlVar4 = zzj;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzi);
                                zzj = zzaVar;
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
            zzbc zzbcVar = new zzbc();
            zzi = zzbcVar;
            zzfq.zza(zzbc.class, zzbcVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbd extends zzfq<zzbd, zza> implements zzhd {
        private static final zzbd zzf;
        private static volatile zzhl<zzbd> zzg;
        private int zzc;
        private zzaf zzd;
        private zzae zze;

        private zzbd() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzbd, zza> implements zzhd {
            private zza() {
                super(zzbd.zzf);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbd>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbd> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbd();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzbd> zzhlVar2 = zzg;
                    zzhl<zzbd> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbd.class) {
                            zzhl<zzbd> zzhlVar4 = zzg;
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
            zzbd zzbdVar = new zzbd();
            zzf = zzbdVar;
            zzfq.zza(zzbd.class, zzbdVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbe extends zzfq<zzbe, zza> implements zzhd {
        private static final zzbe zzl;
        private static volatile zzhl<zzbe> zzm;
        private int zzc;
        private zzaf zzd;
        private zzbi zze;
        private int zzf;
        private int zzg;
        private int zzh;
        private int zzi;
        private int zzj;
        private int zzk;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zzb implements zzfv {
            NO_ERROR(0),
            METADATA_FILE_UNAVAILABLE(1),
            METADATA_ENTRY_NOT_FOUND(2),
            METADATA_JSON_INVALID(3),
            METADATA_HASH_NOT_FOUND(4),
            DOWNLOAD_MANAGER_SERVICE_MISSING(5),
            DOWNLOAD_MANAGER_HTTP_UNKNOWN_STATUS(6),
            DOWNLOAD_MANAGER_HTTP_BAD_REQUEST(400),
            DOWNLOAD_MANAGER_HTTP_UNAUTHORIZED(401),
            DOWNLOAD_MANAGER_HTTP_FORBIDDEN(403),
            DOWNLOAD_MANAGER_HTTP_NOT_FOUND(WalletConstants.ERROR_CODE_INVALID_PARAMETERS),
            DOWNLOAD_MANAGER_HTTP_REQUEST_TIMEOUT(408),
            DOWNLOAD_MANAGER_HTTP_ABORTED(WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR),
            DOWNLOAD_MANAGER_HTTP_TOO_MANY_REQUESTS(429),
            DOWNLOAD_MANAGER_HTTP_CANCELLED(ChatMessageCell.MessageAccessibilityNodeProvider.INSTANT_VIEW),
            DOWNLOAD_MANAGER_HTTP_UNIMPLEMENTED(501),
            DOWNLOAD_MANAGER_HTTP_INTERNAL_SERVICE_ERROR(500),
            DOWNLOAD_MANAGER_HTTP_SERVICE_UNAVAILABLE(503),
            DOWNLOAD_MANAGER_HTTP_DEADLINE_EXCEEDED(504),
            DOWNLOAD_MANAGER_HTTP_NETWORK_AUTHENTICATION_REQUIRED(FrameMetricsAggregator.EVERY_DURATION),
            DOWNLOAD_MANAGER_FILE_ERROR(7),
            DOWNLOAD_MANAGER_UNHANDLED_HTTP_CODE(8),
            DOWNLOAD_MANAGER_HTTP_DATA_ERROR(9),
            DOWNLOAD_MANAGER_TOO_MANY_REDIRECTS(10),
            DOWNLOAD_MANAGER_INSUFFICIENT_SPACE(11),
            DOWNLOAD_MANAGER_DEVICE_NOT_FOUND(12),
            DOWNLOAD_MANAGER_CANNOT_RESUME(13),
            DOWNLOAD_MANAGER_FILE_ALREADY_EXISTS(14),
            DOWNLOAD_MANAGER_UNKNOWN_ERROR(15),
            POST_DOWNLOAD_FILE_NOT_FOUND(16),
            POST_DOWNLOAD_MOVE_FILE_FAILED(17),
            POST_DOWNLOAD_UNZIP_FAILED(18),
            RAPID_RESPONSE_COULD_NOT_BE_WRITTEN(19),
            DRIVER_OBJECT_DEALLOCATED(20);
            
            private static final zzfu<zzb> zzai = new zzcv();
            private final int zzaj;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzaj;
            }

            public static zzfx zzb() {
                return zzcw.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzaj + " name=" + name() + '>';
            }

            zzb(int i) {
                this.zzaj = i;
            }
        }

        private zzbe() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzbe, zza> implements zzhd {
            private zza() {
                super(zzbe.zzl);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbe>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbe> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbe();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003င\u0002\u0004င\u0003\u0005င\u0004\u0006င\u0005\u0007ဌ\u0006\bင\u0007", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj", zzb.zzb(), "zzk"});
                case 4:
                    return zzl;
                case 5:
                    zzhl<zzbe> zzhlVar2 = zzm;
                    zzhl<zzbe> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbe.class) {
                            zzhl<zzbe> zzhlVar4 = zzm;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzl);
                                zzm = zzaVar;
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
            zzbe zzbeVar = new zzbe();
            zzl = zzbeVar;
            zzfq.zza(zzbe.class, zzbeVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbf extends zzfq<zzbf, zzb> implements zzhd {
        private static final zzbf zzf;
        private static volatile zzhl<zzbf> zzg;
        private int zzc;
        private int zzd;
        private int zze;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN(0),
            CANONICAL(1),
            TFLITE(2),
            TFLITE_SUPPORT(3);
            
            private static final zzfu<zza> zze = new zzcy();
            private final int zzf;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzf;
            }

            public static zzfx zzb() {
                return zzcx.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzf = i;
            }
        }

        private zzbf() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzbf, zzb> implements zzhd {
            private zzb() {
                super(zzbf.zzf);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbf>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbf> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbf();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", zza.zzb(), "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzbf> zzhlVar2 = zzg;
                    zzhl<zzbf> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbf.class) {
                            zzhl<zzbf> zzhlVar4 = zzg;
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
            zzbf zzbfVar = new zzbf();
            zzf = zzbfVar;
            zzfq.zza(zzbf.class, zzbfVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbg extends zzfq<zzbg, zzb> implements zzhd {
        private static final zzfz<Integer, zza> zzf = new zzcz();
        private static final zzfz<Integer, zza> zzh = new zzdb();
        private static final zzfz<Integer, zza> zzj = new zzda();
        private static final zzbg zzl;
        private static volatile zzhl<zzbg> zzm;
        private int zzc;
        private long zzd;
        private zzfw zze = zzk();
        private zzfw zzg = zzk();
        private zzfw zzi = zzk();
        private int zzk;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN_ERROR(0),
            NO_CONNECTION(1),
            RPC_ERROR(2),
            RPC_RETURNED_INVALID_RESULT(3),
            RPC_RETURNED_MALFORMED_RESULT(4),
            RPC_EXPONENTIAL_BACKOFF_FAILED(5),
            DIRECTORY_CREATION_FAILED(10),
            FILE_WRITE_FAILED_DISK_FULL(11),
            FILE_WRITE_FAILED(12),
            FILE_READ_FAILED(13),
            FILE_READ_RETURNED_INVALID_DATA(14),
            FILE_READ_RETURNED_MALFORMED_DATA(15);
            
            private static final zzfu<zza> zzm = new zzdd();
            private final int zzn;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zzn;
            }

            public static zzfx zzb() {
                return zzdc.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzn + " name=" + name() + '>';
            }

            zza(int i) {
                this.zzn = i;
            }
        }

        private zzbg() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzbg, zzb> implements zzhd {
            private zzb() {
                super(zzbg.zzl);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbg>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbg> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbg();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzl, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0003\u0000\u0001ဃ\u0000\u0002\u001e\u0003\u001e\u0004\u001e\u0005င\u0001", new Object[]{"zzc", "zzd", "zze", zza.zzb(), "zzg", zza.zzb(), "zzi", zza.zzb(), "zzk"});
                case 4:
                    return zzl;
                case 5:
                    zzhl<zzbg> zzhlVar2 = zzm;
                    zzhl<zzbg> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbg.class) {
                            zzhl<zzbg> zzhlVar4 = zzm;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzl);
                                zzm = zzaVar;
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

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzbg$zza>, com.google.android.gms.internal.mlkit_common.zzcz] */
        /* JADX WARN: Type inference failed for: r0v1, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzbg$zza>, com.google.android.gms.internal.mlkit_common.zzdb] */
        /* JADX WARN: Type inference failed for: r0v2, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzbg$zza>, com.google.android.gms.internal.mlkit_common.zzda] */
        static {
            zzbg zzbgVar = new zzbg();
            zzl = zzbgVar;
            zzfq.zza(zzbg.class, zzbgVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbh extends zzfq<zzbh, zza> implements zzhd {
        private static final zzbh zzo;
        private static volatile zzhl<zzbh> zzp;
        private int zzc;
        private String zzd = "";
        private String zze = "";
        private String zzf = "";
        private String zzg = "";
        private String zzh = "";
        private String zzi = "";
        private String zzj = "";
        private zzfy<String> zzk = zzfq.zzl();
        private String zzl = "";
        private boolean zzm;
        private boolean zzn;

        private zzbh() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzbh, zza> implements zzhd {
            private zza() {
                super(zzbh.zzo);
            }

            public final zza zza(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zza(str);
                return this;
            }

            public final zza zzb(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zzb(str);
                return this;
            }

            public final zza zzc(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zzc(str);
                return this;
            }

            public final zza zzd(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zzd(str);
                return this;
            }

            public final zza zza(Iterable<String> iterable) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zza(iterable);
                return this;
            }

            public final zza zze(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zze(str);
                return this;
            }

            public final zza zza(boolean z) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zza(true);
                return this;
            }

            public final zza zzb(boolean z) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzbh) this.zza).zzb(true);
                return this;
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
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

        public final void zzc(String str) {
            str.getClass();
            this.zzc |= 8;
            this.zzg = str;
        }

        public final String zza() {
            return this.zzh;
        }

        public final void zzd(String str) {
            str.getClass();
            this.zzc |= 16;
            this.zzh = str;
        }

        public final void zza(Iterable<String> iterable) {
            zzfy<String> zzfyVar = this.zzk;
            if (!zzfyVar.zza()) {
                int size = zzfyVar.size();
                this.zzk = zzfyVar.zzb(size == 0 ? 10 : size << 1);
            }
            zzeg.zza(iterable, this.zzk);
        }

        public final void zze(String str) {
            str.getClass();
            this.zzc |= 128;
            this.zzl = str;
        }

        public final void zza(boolean z) {
            this.zzc |= 256;
            this.zzm = true;
        }

        public final void zzb(boolean z) {
            this.zzc |= 512;
            this.zzn = true;
        }

        public static zza zzb() {
            return zzo.zzh();
        }

        public static zza zza(zzbh zzbhVar) {
            return zzo.zza(zzbhVar);
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbh>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbh> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbh();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzo, "\u0001\u000b\u0000\u0001\u0001\u000b\u000b\u0000\u0001\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဈ\u0004\u0006ဈ\u0005\u0007ဈ\u0006\b\u001a\tဈ\u0007\nဇ\b\u000bဇ\t", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", "zzl", "zzm", "zzn"});
                case 4:
                    return zzo;
                case 5:
                    zzhl<zzbh> zzhlVar2 = zzp;
                    zzhl<zzbh> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbh.class) {
                            zzhl<zzbh> zzhlVar4 = zzp;
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
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public static zzbh zzc() {
            return zzo;
        }

        static {
            zzbh zzbhVar = new zzbh();
            zzo = zzbhVar;
            zzfq.zza(zzbh.class, zzbhVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzbi extends zzfq<zzbi, zza> implements zzhd {
        private static final zzbi zzf;
        private static volatile zzhl<zzbi> zzg;
        private int zzc;
        private String zzd = "";
        private String zze = "";

        private zzbi() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzbi, zza> implements zzhd {
            private zza() {
                super(zzbi.zzf);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzbi>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzbi> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzbi();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001", new Object[]{"zzc", "zzd", "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzbi> zzhlVar2 = zzg;
                    zzhl<zzbi> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzbi.class) {
                            zzhl<zzbi> zzhlVar4 = zzg;
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
            zzbi zzbiVar = new zzbi();
            zzf = zzbiVar;
            zzfq.zza(zzbi.class, zzbiVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzc extends zzfq<zzc, zza> implements zzhd {
        private static final zzc zzg;
        private static volatile zzhl<zzc> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzfz<Integer, zzao.zza> zzj = new com.google.android.gms.internal.mlkit_common.zzaw();
            private static final zzfz<Integer, zzao.zzb> zzl = new com.google.android.gms.internal.mlkit_common.zzax();
            private static final zzb zzm;
            private static volatile zzhl<zzb> zzn;
            private int zzc;
            private int zzd;
            private boolean zze;
            private boolean zzf;
            private zzae zzg;
            private zzdf.zza zzh;
            private zzfw zzi = zzk();
            private zzfw zzk = zzk();

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzm);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzc$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzm, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0002\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဇ\u0002\u0004ဉ\u0003\u0005ဉ\u0004\u0006\u001e\u0007\u001e", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg", "zzh", "zzi", zzao.zza.zzb(), "zzk", zzao.zzb.zzb()});
                    case 4:
                        return zzm;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzn;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzn;
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

            /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzao$zza>, com.google.android.gms.internal.mlkit_common.zzaw] */
            /* JADX WARN: Type inference failed for: r0v1, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzav$zzao$zzb>, com.google.android.gms.internal.mlkit_common.zzax] */
            static {
                zzb zzbVar = new zzb();
                zzm = zzbVar;
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        private zzc() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzc, zza> implements zzhd {
            private zza() {
                super(zzc.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzc>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzc> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzc> zzhlVar2 = zzh;
                    zzhl<zzc> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzc.class) {
                            zzhl<zzc> zzhlVar4 = zzh;
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
            zzc zzcVar = new zzc();
            zzg = zzcVar;
            zzfq.zza(zzc.class, zzcVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzd extends zzfq<zzd, zza> implements zzhd {
        private static final zzd zzg;
        private static volatile zzhl<zzd> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzj;
            private static volatile zzhl<zzb> zzk;
            private int zzc;
            private int zzd;
            private boolean zze;
            private zzae zzf;
            private zzac zzg;
            private int zzh;
            private int zzi;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzj);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzd$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဉ\u0002\u0004ဉ\u0003\u0005ဋ\u0004\u0006ဋ\u0005", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg", "zzh", "zzi"});
                    case 4:
                        return zzj;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzk;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzk;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzj);
                                    zzk = zzaVar;
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
                zzj = zzbVar;
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        private zzd() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzd, zza> implements zzhd {
            private zza() {
                super(zzd.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzd>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzd> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzd();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
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
    public static final class zze extends zzfq<zze, zza> implements zzhd {
        private static final zze zzg;
        private static volatile zzhl<zze> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzh;
            private static volatile zzhl<zzb> zzi;
            private int zzc;
            private int zzd;
            private boolean zze;
            private zzae zzf;
            private zzat zzg;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zze$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဉ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg"});
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

        private zze() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zze, zza> implements zzhd {
            private zza() {
                super(zze.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zze>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zze> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zze();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zze> zzhlVar2 = zzh;
                    zzhl<zze> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zze.class) {
                            zzhl<zze> zzhlVar4 = zzh;
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
            zze zzeVar = new zze();
            zzg = zzeVar;
            zzfq.zza(zze.class, zzeVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzf extends zzfq<zzf, zza> implements zzhd {
        private static final zzf zzg;
        private static volatile zzhl<zzf> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzi;
            private static volatile zzhl<zzb> zzj;
            private int zzc;
            private int zzd;
            private boolean zze;
            private boolean zzf;
            private zzae zzg;
            private zzaw zzh;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzi);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzf$zzb>] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဇ\u0002\u0004ဉ\u0003\u0005ဉ\u0004", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg", "zzh"});
                    case 4:
                        return zzi;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzj;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzj;
                                zzhlVar = zzhlVar4;
                                if (zzhlVar4 == null) {
                                    ?? zzaVar = new zzfq.zza(zzi);
                                    zzj = zzaVar;
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
                zzi = zzbVar;
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        private zzf() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzf, zza> implements zzhd {
            private zza() {
                super(zzf.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzf>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzf> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzf();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzf> zzhlVar2 = zzh;
                    zzhl<zzf> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzf.class) {
                            zzhl<zzf> zzhlVar4 = zzh;
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
            zzf zzfVar = new zzf();
            zzg = zzfVar;
            zzfq.zza(zzf.class, zzfVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzg extends zzfq<zzg, zza> implements zzhd {
        private static final zzg zzg;
        private static volatile zzhl<zzg> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzh;
            private static volatile zzhl<zzb> zzi;
            private int zzc;
            private int zzd;
            private boolean zze;
            private zzae zzf;
            private zzba zzg;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzg$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဉ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg"});
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

        private zzg() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzg, zza> implements zzhd {
            private zza() {
                super(zzg.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzg>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzg> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzg();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzg> zzhlVar2 = zzh;
                    zzhl<zzg> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzg.class) {
                            zzhl<zzg> zzhlVar4 = zzh;
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
            zzg zzgVar = new zzg();
            zzg = zzgVar;
            zzfq.zza(zzg.class, zzgVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzh extends zzfq<zzh, zza> implements zzhd {
        private static final zzh zzg;
        private static volatile zzhl<zzh> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzg;
            private static volatile zzhl<zzb> zzh;
            private int zzc;
            private int zzd;
            private boolean zze;
            private zzae zzf;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzg);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzh$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf"});
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

        private zzh() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzh, zza> implements zzhd {
            private zza() {
                super(zzh.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzh>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzh> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzh();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzh> zzhlVar2 = zzh;
                    zzhl<zzh> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzh.class) {
                            zzhl<zzh> zzhlVar4 = zzh;
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
            zzh zzhVar = new zzh();
            zzg = zzhVar;
            zzfq.zza(zzh.class, zzhVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzi extends zzfq<zzi, zza> implements zzhd {
        private static final zzi zzg;
        private static volatile zzhl<zzi> zzh;
        private int zzc;
        private zzb zzd;
        private int zze;
        private zzab zzf;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzh;
            private static volatile zzhl<zzb> zzi;
            private int zzc;
            private int zzd;
            private boolean zze;
            private boolean zzf;
            private zzae zzg;

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzh);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzi$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဇ\u0001\u0003ဇ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zze", "zzf", "zzg"});
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
                super(zzi.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzi>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzi> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzi();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဋ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzi> zzhlVar2 = zzh;
                    zzhl<zzi> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzi.class) {
                            zzhl<zzi> zzhlVar4 = zzh;
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
            zzi zziVar = new zzi();
            zzg = zziVar;
            zzfq.zza(zzi.class, zziVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzj extends zzfq<zzj, zza> implements zzhd {
        private static final zzj zze;
        private static volatile zzhl<zzj> zzf;
        private int zzc;
        private int zzd;

        private zzj() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzj, zza> implements zzhd {
            private zza() {
                super(zzj.zze);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzj>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzj> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzj();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ဌ\u0000", new Object[]{"zzc", "zzd", com.google.android.gms.internal.mlkit_common.zzbf.zzb()});
                case 4:
                    return zze;
                case 5:
                    zzhl<zzj> zzhlVar2 = zzf;
                    zzhl<zzj> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzj.class) {
                            zzhl<zzj> zzhlVar4 = zzf;
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
            zzj zzjVar = new zzj();
            zze = zzjVar;
            zzfq.zza(zzj.class, zzjVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzk extends zzfq<zzk, zza> implements zzhd {
        private static final zzk zzi;
        private static volatile zzhl<zzk> zzj;
        private int zzc;
        private zzaf zzd;
        private zzam zze;
        private long zzf;
        private float zzg;
        private zzae zzh;

        private zzk() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzk, zza> implements zzhd {
            private zza() {
                super(zzk.zzi);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzk>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzk> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzk();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဃ\u0002\u0004ခ\u0003\u0005ဉ\u0004", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh"});
                case 4:
                    return zzi;
                case 5:
                    zzhl<zzk> zzhlVar2 = zzj;
                    zzhl<zzk> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzk.class) {
                            zzhl<zzk> zzhlVar4 = zzj;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzi);
                                zzj = zzaVar;
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
            zzi = zzkVar;
            zzfq.zza(zzk.class, zzkVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzl extends zzfq<zzl, zza> implements zzhd {
        private static final zzfz<Integer, com.google.android.gms.internal.mlkit_common.zzbf> zzg = new com.google.android.gms.internal.mlkit_common.zzay();
        private static final zzl zzi;
        private static volatile zzhl<zzl> zzj;
        private int zzc;
        private zzam zzd;
        private zzam zze;
        private zzfw zzf = zzk();
        private long zzh;

        private zzl() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzl, zza> implements zzhd {
            private zza() {
                super(zzl.zzi);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzl>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzl> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzl();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzi, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001e\u0004ဃ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zzh"});
                case 4:
                    return zzi;
                case 5:
                    zzhl<zzl> zzhlVar2 = zzj;
                    zzhl<zzl> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzl.class) {
                            zzhl<zzl> zzhlVar4 = zzj;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzi);
                                zzj = zzaVar;
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

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzbf>, com.google.android.gms.internal.mlkit_common.zzay] */
        static {
            zzl zzlVar = new zzl();
            zzi = zzlVar;
            zzfq.zza(zzl.class, zzlVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzm extends zzfq<zzm, zza> implements zzhd {
        private static final zzm zzg;
        private static volatile zzhl<zzm> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzm() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzm, zza> implements zzhd {
            private zza() {
                super(zzm.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzm>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzm> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzm();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzm> zzhlVar2 = zzh;
                    zzhl<zzm> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzm.class) {
                            zzhl<zzm> zzhlVar4 = zzh;
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
            zzm zzmVar = new zzm();
            zzg = zzmVar;
            zzfq.zza(zzm.class, zzmVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzn extends zzfq<zzn, zzb> implements zzhd {
        private static final zzn zzf;
        private static volatile zzhl<zzn> zzg;
        private int zzc;
        private int zzd;
        private int zze;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum zza implements zzfv {
            UNKNOWN_MODEL_TYPE(0),
            STABLE_MODEL(1),
            LATEST_MODEL(2);
            
            private static final zzfu<zza> zzd = new com.google.android.gms.internal.mlkit_common.zzba();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_common.zzfv
            public final int zza() {
                return this.zze;
            }

            public static zzfx zzb() {
                return com.google.android.gms.internal.mlkit_common.zzaz.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zza(int i) {
                this.zze = i;
            }
        }

        private zzn() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq.zzb<zzn, zzb> implements zzhd {
            private zzb() {
                super(zzn.zzf);
            }

            /* synthetic */ zzb(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzn>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzn> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzn();
                case 2:
                    return new zzb(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002ဌ\u0001", new Object[]{"zzc", "zzd", "zze", zza.zzb()});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzn> zzhlVar2 = zzg;
                    zzhl<zzn> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzn.class) {
                            zzhl<zzn> zzhlVar4 = zzg;
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
            zzn zznVar = new zzn();
            zzf = zznVar;
            zzfq.zza(zzn.class, zznVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzo extends zzfq<zzo, zza> implements zzhd {
        private static final zzo zzg;
        private static volatile zzhl<zzo> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzo() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzo, zza> implements zzhd {
            private zza() {
                super(zzo.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzo>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzo> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzo();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzo> zzhlVar2 = zzh;
                    zzhl<zzo> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzo.class) {
                            zzhl<zzo> zzhlVar4 = zzh;
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
            zzo zzoVar = new zzo();
            zzg = zzoVar;
            zzfq.zza(zzo.class, zzoVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzp extends zzfq<zzp, zza> implements zzhd {
        private static final zzp zzg;
        private static volatile zzhl<zzp> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzp() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzp, zza> implements zzhd {
            private zza() {
                super(zzp.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzp>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzp> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzp();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzp> zzhlVar2 = zzh;
                    zzhl<zzp> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzp.class) {
                            zzhl<zzp> zzhlVar4 = zzh;
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
            zzp zzpVar = new zzp();
            zzg = zzpVar;
            zzfq.zza(zzp.class, zzpVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzq extends zzfq<zzq, zza> implements zzhd {
        private static final zzq zzg;
        private static volatile zzhl<zzq> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzq() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzq, zza> implements zzhd {
            private zza() {
                super(zzq.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzq>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzq> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzq();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzq> zzhlVar2 = zzh;
                    zzhl<zzq> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzq.class) {
                            zzhl<zzq> zzhlVar4 = zzh;
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
            zzq zzqVar = new zzq();
            zzg = zzqVar;
            zzfq.zza(zzq.class, zzqVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzr extends zzfq<zzr, zza> implements zzhd {
        private static final zzr zzg;
        private static volatile zzhl<zzr> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzr() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzr, zza> implements zzhd {
            private zza() {
                super(zzr.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzfq$zza, com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzr>] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzr> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzr();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzr> zzhlVar2 = zzh;
                    zzhl<zzr> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzr.class) {
                            zzhl<zzr> zzhlVar4 = zzh;
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
            zzr zzrVar = new zzr();
            zzg = zzrVar;
            zzfq.zza(zzr.class, zzrVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzs extends zzfq<zzs, zza> implements zzhd {
        private static final zzs zzg;
        private static volatile zzhl<zzs> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzs() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzs, zza> implements zzhd {
            private zza() {
                super(zzs.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzs>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzs> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzs();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzs> zzhlVar2 = zzh;
                    zzhl<zzs> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzs.class) {
                            zzhl<zzs> zzhlVar4 = zzh;
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
            zzs zzsVar = new zzs();
            zzg = zzsVar;
            zzfq.zza(zzs.class, zzsVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzt extends zzfq<zzt, zza> implements zzhd {
        private static final zzt zzg;
        private static volatile zzhl<zzt> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzt() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzt, zza> implements zzhd {
            private zza() {
                super(zzt.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzt>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzt> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzt();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzt> zzhlVar2 = zzh;
                    zzhl<zzt> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzt.class) {
                            zzhl<zzt> zzhlVar4 = zzh;
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
            zzt zztVar = new zzt();
            zzg = zztVar;
            zzfq.zza(zzt.class, zztVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzu extends zzfq<zzu, zza> implements zzhd {
        private static final zzu zzg;
        private static volatile zzhl<zzu> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzu() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzu, zza> implements zzhd {
            private zza() {
                super(zzu.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzu>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzu> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzu();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzu> zzhlVar2 = zzh;
                    zzhl<zzu> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzu.class) {
                            zzhl<zzu> zzhlVar4 = zzh;
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
            zzu zzuVar = new zzu();
            zzg = zzuVar;
            zzfq.zza(zzu.class, zzuVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzv extends zzfq<zzv, zza> implements zzhd {
        private static final zzv zzg;
        private static volatile zzhl<zzv> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzv() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzv, zza> implements zzhd {
            private zza() {
                super(zzv.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzv>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzv> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzv();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzv> zzhlVar2 = zzh;
                    zzhl<zzv> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzv.class) {
                            zzhl<zzv> zzhlVar4 = zzh;
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
            zzv zzvVar = new zzv();
            zzg = zzvVar;
            zzfq.zza(zzv.class, zzvVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzw extends zzfq<zzw, zza> implements zzhd {
        private static final zzw zzg;
        private static volatile zzhl<zzw> zzh;
        private int zzc;
        private zzaf zzd;
        private zzn zze;
        private zzae zzf;

        private zzw() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzw, zza> implements zzhd {
            private zza() {
                super(zzw.zzg);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzw>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzw> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzw();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
                case 4:
                    return zzg;
                case 5:
                    zzhl<zzw> zzhlVar2 = zzh;
                    zzhl<zzw> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzw.class) {
                            zzhl<zzw> zzhlVar4 = zzh;
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
            zzw zzwVar = new zzw();
            zzg = zzwVar;
            zzfq.zza(zzw.class, zzwVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzx extends zzfq<zzx, zza> implements zzhd {
        private static final zzx zzf;
        private static volatile zzhl<zzx> zzg;
        private int zzc;
        private zzam zzd;
        private int zze;

        private zzx() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzx, zza> implements zzhd {
            private zza() {
                super(zzx.zzf);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzx>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzx> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzx();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဌ\u0001", new Object[]{"zzc", "zzd", "zze", com.google.android.gms.internal.mlkit_common.zzbf.zzb()});
                case 4:
                    return zzf;
                case 5:
                    zzhl<zzx> zzhlVar2 = zzg;
                    zzhl<zzx> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzx.class) {
                            zzhl<zzx> zzhlVar4 = zzg;
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
            zzx zzxVar = new zzx();
            zzf = zzxVar;
            zzfq.zza(zzx.class, zzxVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzy extends zzfq<zzy, zza> implements zzhd {
        private static final zzy zzi;
        private static volatile zzhl<zzy> zzj;
        private int zzc;
        private zzaf zzd;
        private zzam zze;
        private zzfy<zzb> zzf = zzl();
        private zzfy<zzb> zzg = zzl();
        private long zzh;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zzb extends zzfq<zzb, zza> implements zzhd {
            private static final zzb zzf;
            private static volatile zzhl<zzb> zzg;
            private int zzc;
            private int zzd;
            private zzfw zze = zzk();

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* renamed from: com.google.android.gms.internal.mlkit_common.zzav$zzy$zzb$zzb */
            /* loaded from: classes3.dex */
            public enum EnumC0015zzb implements zzfv {
                UNKNOWN_DATA_TYPE(0),
                TYPE_FLOAT32(1),
                TYPE_INT32(2),
                TYPE_BYTE(3),
                TYPE_LONG(4);
                
                private static final zzfu<EnumC0015zzb> zzf = new com.google.android.gms.internal.mlkit_common.zzbb();
                private final int zzg;

                @Override // com.google.android.gms.internal.mlkit_common.zzfv
                public final int zza() {
                    return this.zzg;
                }

                public static zzfx zzb() {
                    return com.google.android.gms.internal.mlkit_common.zzbc.zza;
                }

                @Override // java.lang.Enum
                public final String toString() {
                    return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzg + " name=" + name() + '>';
                }

                EnumC0015zzb(int i) {
                    this.zzg = i;
                }
            }

            private zzb() {
            }

            /* compiled from: com.google.mlkit:common@@17.0.0 */
            /* loaded from: classes3.dex */
            public static final class zza extends zzfq.zzb<zzb, zza> implements zzhd {
                private zza() {
                    super(zzb.zzf);
                }

                /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                    this();
                }
            }

            /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzy$zzb>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
            @Override // com.google.android.gms.internal.mlkit_common.zzfq
            public final Object zza(int i, Object obj, Object obj2) {
                zzhl<zzb> zzhlVar;
                switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                    case 1:
                        return new zzb();
                    case 2:
                        return new zza(null);
                    case 3:
                        return zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001ဌ\u0000\u0002\u0016", new Object[]{"zzc", "zzd", EnumC0015zzb.zzb(), "zze"});
                    case 4:
                        return zzf;
                    case 5:
                        zzhl<zzb> zzhlVar2 = zzg;
                        zzhl<zzb> zzhlVar3 = zzhlVar2;
                        if (zzhlVar2 == null) {
                            synchronized (zzb.class) {
                                zzhl<zzb> zzhlVar4 = zzg;
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
                zzb zzbVar = new zzb();
                zzf = zzbVar;
                zzfq.zza(zzb.class, zzbVar);
            }
        }

        private zzy() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzy, zza> implements zzhd {
            private zza() {
                super(zzy.zzi);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzy>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzy> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzy();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001b\u0004\u001b\u0005ဃ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf", zzb.class, "zzg", zzb.class, "zzh"});
                case 4:
                    return zzi;
                case 5:
                    zzhl<zzy> zzhlVar2 = zzj;
                    zzhl<zzy> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzy.class) {
                            zzhl<zzy> zzhlVar4 = zzj;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzi);
                                zzj = zzaVar;
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
            zzy zzyVar = new zzy();
            zzi = zzyVar;
            zzfq.zza(zzy.class, zzyVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzz extends zzfq<zzz, zza> implements zzhd {
        private static final zzfz<Integer, com.google.android.gms.internal.mlkit_common.zzbf> zzg = new com.google.android.gms.internal.mlkit_common.zzbd();
        private static final zzz zzj;
        private static volatile zzhl<zzz> zzk;
        private int zzc;
        private zzam zzd;
        private zzam zze;
        private zzfw zzf = zzk();
        private long zzh;
        private boolean zzi;

        private zzz() {
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzb<zzz, zza> implements zzhd {
            private zza() {
                super(zzz.zzj);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzz>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzz> zzhlVar;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzz();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzj, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001e\u0004ဃ\u0002\u0005ဇ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", com.google.android.gms.internal.mlkit_common.zzbf.zzb(), "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzhl<zzz> zzhlVar2 = zzk;
                    zzhl<zzz> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzz.class) {
                            zzhl<zzz> zzhlVar4 = zzk;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzj);
                                zzk = zzaVar;
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

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_common.zzfz<java.lang.Integer, com.google.android.gms.internal.mlkit_common.zzbf>, com.google.android.gms.internal.mlkit_common.zzbd] */
        static {
            zzz zzzVar = new zzz();
            zzj = zzzVar;
            zzfq.zza(zzz.class, zzzVar);
        }
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static final class zzad extends zzfq.zzc<zzad, zza> implements zzhd {
        private static final zzad zzbd;
        private static volatile zzhl<zzad> zzbe;
        private zzp zzaa;
        private zzm zzab;
        private zzo zzac;
        private zzr zzad;
        private zzq zzae;
        private zzs zzaf;
        private zzt zzag;
        private zzu zzah;
        private zzv zzai;
        private zzw zzaj;
        private zzj zzak;
        private zzl zzal;
        private zzk zzam;
        private zzah zzan;
        private zzaa zzao;
        private zza zzap;
        private zzb zzaq;
        private zzd zzar;
        private zzc zzas;
        private zze zzat;
        private zzf zzau;
        private zzi zzav;
        private zzg zzaw;
        private zzh zzax;
        private zzbg zzaz;
        private zzag zzba;
        private zzaj zzbb;
        private int zzd;
        private int zze;
        private zzbh zzf;
        private int zzg;
        private boolean zzh;
        private zzak zzi;
        private zzz zzj;
        private zzy zzk;
        private zzx zzl;
        private zzap zzm;
        private zzbd zzn;
        private zzao zzo;
        private zzaq zzp;
        private zzas zzq;
        private zzar zzr;
        private C0013zzav zzs;
        private zzay zzt;
        private zzax zzu;
        private zzaz zzv;
        private zzbb zzw;
        private zzbc zzx;
        private zzau zzy;
        private zzbe zzz;
        private byte zzbc = 2;
        private zzfy<zzjf.zzf> zzay = zzl();

        static {
            zzad zzadVar = new zzad();
            zzbd = zzadVar;
            zzfq.zza(zzad.class, zzadVar);
        }

        private zzad() {
        }

        public static zza zza(zzad zzadVar) {
            return (zza) zzbd.zza(zzadVar);
        }

        public final void zza(zzaj zzajVar) {
            zzajVar.getClass();
            this.zzbb = zzajVar;
            this.zze |= 32768;
        }

        public final void zza(zzak zzakVar) {
            zzakVar.getClass();
            this.zzi = zzakVar;
            this.zzd |= 8;
        }

        public final void zza(zzbh zzbhVar) {
            zzbhVar.getClass();
            this.zzf = zzbhVar;
            this.zzd |= 1;
        }

        public final void zza(com.google.android.gms.internal.mlkit_common.zzbg zzbgVar) {
            this.zzg = zzbgVar.zza();
            this.zzd |= 2;
        }

        public static zza zzb() {
            return (zza) zzbd.zzh();
        }

        public final zzbh zza() {
            zzbh zzbhVar = this.zzf;
            return zzbhVar == null ? zzbh.zzc() : zzbhVar;
        }

        /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_common.zzhl<com.google.android.gms.internal.mlkit_common.zzav$zzad>, com.google.android.gms.internal.mlkit_common.zzfq$zza] */
        @Override // com.google.android.gms.internal.mlkit_common.zzfq
        public final Object zza(int i, Object obj, Object obj2) {
            zzhl<zzad> zzhlVar;
            int i2 = 1;
            switch (com.google.android.gms.internal.mlkit_common.zzau.zza[i - 1]) {
                case 1:
                    return new zzad();
                case 2:
                    return new zza(null);
                case 3:
                    return zza(zzbd, "\u00011\u0000\u0002\u000131\u0000\u0001\u0001\u0001ဉ\u0000\u0002ဌ\u0001\u0003ဉ\u0003\u0004ဉ\u0005\u0005ဉ\u0007\u0006ဉ\b\u0007ဉ\t\bဉ\u0015\tဉ\u0016\nဉ\u0017\u000bဉ\u0018\fဉ\u0019\rဉ\u001a\u000eဉ\u001b\u000fဉ\u001c\u0010ဉ\u001d\u0011ဉ\u001e\u0012ဉ\f\u0013ဉ\u0012\u0014ဉ\u0004\u0015ဉ\u0013\u0016ဉ\u0014\u0017ဉ\u001f\u0018ဉ \u0019ဉ!\u001aဉ\r\u001bဉ\u000e\u001cဉ\u000f\u001dဉ\u0006\u001eဉ$\u001fဉ% ဉ&!ဉ'\"ဉ(#ဉ)$ဉ*%ဇ\u0002'ဉ\"(ဉ#)Л*ဉ-,ဉ\u0010-ဉ\u0011.ဉ+/ဉ,0ဉ\n1ဉ\u000b2ဉ.3ဉ/", new Object[]{"zzd", "zze", "zzf", "zzg", com.google.android.gms.internal.mlkit_common.zzbg.zzb(), "zzi", "zzk", "zzm", "zzn", "zzo", "zzaa", "zzab", "zzac", "zzad", "zzae", "zzaf", "zzag", "zzah", "zzai", "zzaj", "zzr", "zzx", "zzj", "zzy", "zzz", "zzak", "zzal", "zzam", "zzs", "zzt", "zzu", "zzl", "zzap", "zzaq", "zzar", "zzas", "zzat", "zzau", "zzav", "zzh", "zzan", "zzao", "zzay", zzjf.zzf.class, "zzaz", "zzv", "zzw", "zzaw", "zzax", "zzp", "zzq", "zzba", "zzbb"});
                case 4:
                    return zzbd;
                case 5:
                    zzhl<zzad> zzhlVar2 = zzbe;
                    zzhl<zzad> zzhlVar3 = zzhlVar2;
                    if (zzhlVar2 == null) {
                        synchronized (zzad.class) {
                            zzhl<zzad> zzhlVar4 = zzbe;
                            zzhlVar = zzhlVar4;
                            if (zzhlVar4 == null) {
                                ?? zzaVar = new zzfq.zza(zzbd);
                                zzbe = zzaVar;
                                zzhlVar = zzaVar;
                            }
                        }
                        zzhlVar3 = zzhlVar;
                    }
                    return zzhlVar3;
                case 6:
                    return Byte.valueOf(this.zzbc);
                case 7:
                    if (obj == null) {
                        i2 = 0;
                    }
                    this.zzbc = (byte) i2;
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public static final class zza extends zzfq.zzd<zzad, zza> implements zzhd {
            private zza() {
                super(zzad.zzbd);
            }

            /* synthetic */ zza(com.google.android.gms.internal.mlkit_common.zzau zzauVar) {
                this();
            }

            public final zza zza(zzbh.zza zzaVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzad) this.zza).zza((zzbh) ((zzfq) zzaVar.zzg()));
                return this;
            }

            public final zzbh zza() {
                return ((zzad) this.zza).zza();
            }

            public final zza zza(com.google.android.gms.internal.mlkit_common.zzbg zzbgVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzad) this.zza).zza(zzbgVar);
                return this;
            }

            public final zza zza(zzak.zzb zzbVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzad) this.zza).zza((zzak) ((zzfq) zzbVar.zzg()));
                return this;
            }

            public final zza zza(zzaj.zzb zzbVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzad) this.zza).zza((zzaj) ((zzfq) zzbVar.zzg()));
                return this;
            }
        }
    }
}
