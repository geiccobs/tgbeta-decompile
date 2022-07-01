package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzg extends zzjb<zzfi$zzg, zza> implements zzkm {
    private static final zzfi$zzg zzj;
    private static volatile zzkx<zzfi$zzg> zzk;
    private int zzc;
    private int zzd;
    private int zze;
    private int zzf;
    private boolean zzg;
    private boolean zzh;
    private float zzi;

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public enum zzb implements zzje {
        CLASSIFICATION_UNKNOWN(0),
        CLASSIFICATION_NONE(1),
        CLASSIFICATION_ALL(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.vision.zzje
        public final int zza() {
            return this.zze;
        }

        public static zzb zza(int i) {
            if (i != 0) {
                if (i == 1) {
                    return CLASSIFICATION_NONE;
                }
                if (i == 2) {
                    return CLASSIFICATION_ALL;
                }
                return null;
            }
            return CLASSIFICATION_UNKNOWN;
        }

        public static zzjg zzb() {
            return zzfr.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zze = i;
        }

        static {
            new zzfq();
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public enum zzc implements zzje {
        LANDMARK_UNKNOWN(0),
        LANDMARK_NONE(1),
        LANDMARK_ALL(2),
        LANDMARK_CONTOUR(3);
        
        private final int zzf;

        @Override // com.google.android.gms.internal.vision.zzje
        public final int zza() {
            return this.zzf;
        }

        public static zzc zza(int i) {
            if (i != 0) {
                if (i == 1) {
                    return LANDMARK_NONE;
                }
                if (i == 2) {
                    return LANDMARK_ALL;
                }
                if (i == 3) {
                    return LANDMARK_CONTOUR;
                }
                return null;
            }
            return LANDMARK_UNKNOWN;
        }

        public static zzjg zzb() {
            return zzfs.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzc.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zzc(int i) {
            this.zzf = i;
        }

        static {
            new zzft();
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public enum zzd implements zzje {
        MODE_UNKNOWN(0),
        MODE_ACCURATE(1),
        MODE_FAST(2),
        MODE_SELFIE(3);
        
        private final int zzf;

        @Override // com.google.android.gms.internal.vision.zzje
        public final int zza() {
            return this.zzf;
        }

        public static zzd zza(int i) {
            if (i != 0) {
                if (i == 1) {
                    return MODE_ACCURATE;
                }
                if (i == 2) {
                    return MODE_FAST;
                }
                if (i == 3) {
                    return MODE_SELFIE;
                }
                return null;
            }
            return MODE_UNKNOWN;
        }

        public static zzjg zzb() {
            return zzfv.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzd.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zzd(int i) {
            this.zzf = i;
        }

        static {
            new zzfu();
        }
    }

    private zzfi$zzg() {
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzg>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzg> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzg();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဇ\u0003\u0005ဇ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", zzd.zzb(), "zze", zzc.zzb(), "zzf", zzb.zzb(), "zzg", "zzh", "zzi"});
            case 4:
                return zzj;
            case 5:
                zzkx<zzfi$zzg> zzkxVar2 = zzk;
                zzkx<zzfi$zzg> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzg.class) {
                        zzkx<zzfi$zzg> zzkxVar4 = zzk;
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

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zzg, zza> implements zzkm {
        private zza() {
            super(zzfi$zzg.zzj);
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    static {
        zzfi$zzg zzfi_zzg = new zzfi$zzg();
        zzj = zzfi_zzg;
        zzjb.zza(zzfi$zzg.class, zzfi_zzg);
    }
}
