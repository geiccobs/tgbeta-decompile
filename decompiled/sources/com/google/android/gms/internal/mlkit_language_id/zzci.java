package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzci {

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zza extends zzeo<zza, C0020zza> implements zzgb {
        private static final zzex<Integer, zzdd> zzd = new zzcj();
        private static final zza zze;
        private static volatile zzgj<zza> zzf;
        private zzeu zzc = zzk();

        private zza() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* renamed from: com.google.android.gms.internal.mlkit_language_id.zzci$zza$zza */
        /* loaded from: classes3.dex */
        public static final class C0020zza extends zzeo.zzb<zza, C0020zza> implements zzgb {
            private C0020zza() {
                super(zza.zze);
            }

            /* synthetic */ C0020zza(zzch zzchVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzci$zza>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zza> zzgjVar;
            switch (zzch.zza[i - 1]) {
                case 1:
                    return new zza();
                case 2:
                    return new C0020zza(null);
                case 3:
                    return zza(zze, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001e", new Object[]{"zzc", zzdd.zzb()});
                case 4:
                    return zze;
                case 5:
                    zzgj<zza> zzgjVar2 = zzf;
                    zzgj<zza> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zza.class) {
                            zzgj<zza> zzgjVar4 = zzf;
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

        /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_language_id.zzcj, com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzdd>] */
        static {
            zza zzaVar = new zza();
            zze = zzaVar;
            zzeo.zza(zza.class, zzaVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static final class zzb extends zzeo<zzb, C0021zzb> implements zzgb {
        private static final zzb zzj;
        private static volatile zzgj<zzb> zzk;
        private int zzc;
        private int zzd;
        private int zze;
        private int zzf;
        private boolean zzg;
        private boolean zzh;
        private float zzi;

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zza implements zzet {
            CLASSIFICATION_UNKNOWN(0),
            CLASSIFICATION_NONE(1),
            CLASSIFICATION_ALL(2);
            
            private static final zzes<zza> zzd = new zzcl();
            private final int zze;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zze;
            }

            public static zzev zzb() {
                return zzck.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
            }

            zza(int i) {
                this.zze = i;
            }
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zzc implements zzet {
            LANDMARK_UNKNOWN(0),
            LANDMARK_NONE(1),
            LANDMARK_ALL(2),
            LANDMARK_CONTOUR(3);
            
            private static final zzes<zzc> zze = new zzcm();
            private final int zzf;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zzf;
            }

            public static zzev zzb() {
                return zzcn.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zzc(int i) {
                this.zzf = i;
            }
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes3.dex */
        public enum zzd implements zzet {
            MODE_UNKNOWN(0),
            MODE_ACCURATE(1),
            MODE_FAST(2),
            MODE_SELFIE(3);
            
            private static final zzes<zzd> zze = new zzcp();
            private final int zzf;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zzf;
            }

            public static zzev zzb() {
                return zzco.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
            }

            zzd(int i) {
                this.zzf = i;
            }
        }

        private zzb() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* renamed from: com.google.android.gms.internal.mlkit_language_id.zzci$zzb$zzb */
        /* loaded from: classes3.dex */
        public static final class C0021zzb extends zzeo.zzb<zzb, C0021zzb> implements zzgb {
            private C0021zzb() {
                super(zzb.zzj);
            }

            /* synthetic */ C0021zzb(zzch zzchVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzci$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzb> zzgjVar;
            switch (zzch.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new C0021zzb(null);
                case 3:
                    return zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဇ\u0003\u0005ဇ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", zzd.zzb(), "zze", zzc.zzb(), "zzf", zza.zzb(), "zzg", "zzh", "zzi"});
                case 4:
                    return zzj;
                case 5:
                    zzgj<zzb> zzgjVar2 = zzk;
                    zzgj<zzb> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzb.class) {
                            zzgj<zzb> zzgjVar4 = zzk;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzj);
                                zzk = zzaVar;
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
            zzj = zzbVar;
            zzeo.zza(zzb.class, zzbVar);
        }
    }
}
