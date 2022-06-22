package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzci$zzb extends zzeo<zzci$zzb, zzb> implements zzgb {
    private static final zzci$zzb zzj;
    private static volatile zzgj<zzci$zzb> zzk;
    private int zzc;
    private int zzd;
    private int zze;
    private int zzf;
    private boolean zzg;
    private boolean zzh;
    private float zzi;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        CLASSIFICATION_UNKNOWN(0),
        CLASSIFICATION_NONE(1),
        CLASSIFICATION_ALL(2);
        
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
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zza(int i) {
            this.zze = i;
        }

        static {
            new zzcl();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzc implements zzet {
        LANDMARK_UNKNOWN(0),
        LANDMARK_NONE(1),
        LANDMARK_ALL(2),
        LANDMARK_CONTOUR(3);
        
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
            return "<" + zzc.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zzc(int i) {
            this.zzf = i;
        }

        static {
            new zzcm();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzd implements zzet {
        MODE_UNKNOWN(0),
        MODE_ACCURATE(1),
        MODE_FAST(2),
        MODE_SELFIE(3);
        
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
            return "<" + zzd.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zzd(int i) {
            this.zzf = i;
        }

        static {
            new zzcp();
        }
    }

    private zzci$zzb() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzci$zzb, zzb> implements zzgb {
        private zzb() {
            super(zzci$zzb.zzj);
        }

        /* synthetic */ zzb(zzch zzchVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzci$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzci$zzb> zzgjVar;
        switch (zzch.zza[i - 1]) {
            case 1:
                return new zzci$zzb();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzj, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဇ\u0003\u0005ဇ\u0004\u0006ခ\u0005", new Object[]{"zzc", "zzd", zzd.zzb(), "zze", zzc.zzb(), "zzf", zza.zzb(), "zzg", "zzh", "zzi"});
            case 4:
                return zzj;
            case 5:
                zzgj<zzci$zzb> zzgjVar2 = zzk;
                zzgj<zzci$zzb> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzci$zzb.class) {
                        zzgj<zzci$zzb> zzgjVar4 = zzk;
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
        zzci$zzb zzci_zzb = new zzci$zzb();
        zzj = zzci_zzb;
        zzeo.zza(zzci$zzb.class, zzci_zzb);
    }
}
