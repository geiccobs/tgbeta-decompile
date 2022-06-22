package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzac extends zzeo<zzy$zzac, zza> implements zzgb {
    private static final zzy$zzac zzj;
    private static volatile zzgj<zzy$zzac> zzk;
    private int zzc;
    private int zzd;
    private int zze;
    private int zzf;
    private int zzg;
    private boolean zzh;
    private float zzi;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        UNKNOWN_CLASSIFICATIONS(0),
        NO_CLASSIFICATIONS(1),
        ALL_CLASSIFICATIONS(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zze;
        }

        public static zzev zzb() {
            return zzao.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zze = i;
        }

        static {
            new zzan();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzc implements zzet {
        UNKNOWN_CONTOURS(0),
        NO_CONTOURS(1),
        ALL_CONTOURS(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zze;
        }

        public static zzev zzb() {
            return zzap.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzc.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zzc(int i) {
            this.zze = i;
        }

        static {
            new zzaq();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzd implements zzet {
        UNKNOWN_LANDMARKS(0),
        NO_LANDMARKS(1),
        ALL_LANDMARKS(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zze;
        }

        public static zzev zzb() {
            return zzas.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzd.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zzd(int i) {
            this.zze = i;
        }

        static {
            new zzar();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zze implements zzet {
        UNKNOWN_PERFORMANCE(0),
        FAST(1),
        ACCURATE(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zze;
        }

        public static zzev zzb() {
            return zzat.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zze.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zze(int i) {
            this.zze = i;
        }

        static {
            new zzau();
        }
    }

    private zzy$zzac() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzac, zza> implements zzgb {
        private zza() {
            super(zzy$zzac.zzj);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzac>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzac> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzac();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဌ\u0003\u0005ဇ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", zzd.zzb(), "zze", zzb.zzb(), "zzf", zze.zzb(), "zzg", zzc.zzb(), "zzh", "zzi"});
            case 4:
                return zzj;
            case 5:
                zzgj<zzy$zzac> zzgjVar2 = zzk;
                zzgj<zzy$zzac> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzac.class) {
                        zzgj<zzy$zzac> zzgjVar4 = zzk;
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
        zzy$zzac zzy_zzac = new zzy$zzac();
        zzj = zzy_zzac;
        zzeo.zza(zzy$zzac.class, zzy_zzac);
    }
}
