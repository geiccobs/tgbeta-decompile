package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import org.telegram.messenger.FileLoader;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzbc extends zzeo<zzy$zzbc, zzb> implements zzgb {
    private static final zzy$zzbc zzi;
    private static volatile zzgj<zzy$zzbc> zzj;
    private int zzc;
    private zzy$zzaf zzd;
    private zzew<zzc> zze = zzeo.zzl();
    private int zzf;
    private int zzg;
    private int zzh;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        NO_ERROR(0),
        STATUS_SENSITIVE_TOPIC(1),
        STATUS_QUALITY_THRESHOLDED(2),
        STATUS_INTERNAL_ERROR(3),
        STATUS_NOT_SUPPORTED_LANGUAGE(FileLoader.MEDIA_DIR_VIDEO_PUBLIC),
        STATUS_32_BIT_CPU(1001),
        STATUS_32_BIT_APP(1002);
        
        private final int zzi;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzi;
        }

        public static zzev zzb() {
            return zzbw.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzi + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzi = i;
        }

        static {
            new zzbx();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzc extends zzeo<zzc, zza> implements zzgb {
        private static final zzc zze;
        private static volatile zzgj<zzc> zzf;
        private int zzc;
        private float zzd;

        private zzc() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzc, zza> implements zzgb {
            private zza() {
                super(zzc.zze);
            }

            /* synthetic */ zza(zzx zzxVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbc$zzc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzc> zzgjVar;
            switch (zzx.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ခ\u0000", new Object[]{"zzc", "zzd"});
                case 4:
                    return zze;
                case 5:
                    zzgj<zzc> zzgjVar2 = zzf;
                    zzgj<zzc> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzc.class) {
                            zzgj<zzc> zzgjVar4 = zzf;
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
            zzc zzcVar = new zzc();
            zze = zzcVar;
            zzeo.zza(zzc.class, zzcVar);
        }
    }

    private zzy$zzbc() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzy$zzbc, zzb> implements zzgb {
        private zzb() {
            super(zzy$zzbc.zzi);
        }

        /* synthetic */ zzb(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzbc> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzbc();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0001\u0000\u0001ဉ\u0000\u0002\u001b\u0003ဌ\u0001\u0004င\u0002\u0005င\u0003", new Object[]{"zzc", "zzd", "zze", zzc.class, "zzf", zza.zzb(), "zzg", "zzh"});
            case 4:
                return zzi;
            case 5:
                zzgj<zzy$zzbc> zzgjVar2 = zzj;
                zzgj<zzy$zzbc> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzbc.class) {
                        zzgj<zzy$zzbc> zzgjVar4 = zzj;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzi);
                            zzj = zzaVar;
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
        zzy$zzbc zzy_zzbc = new zzy$zzbc();
        zzi = zzy_zzbc;
        zzeo.zza(zzy$zzbc.class, zzy_zzbc);
    }
}
