package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzal extends zzeo<zzy$zzal, zza> implements zzgb {
    private static final zzy$zzal zzl;
    private static volatile zzgj<zzy$zzal> zzm;
    private int zzc;
    private int zzf;
    private int zzi;
    private long zzj;
    private boolean zzk;
    private String zzd = "";
    private String zze = "";
    private String zzg = "";
    private String zzh = "";

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        TYPE_UNKNOWN(0),
        CUSTOM(1),
        AUTOML_IMAGE_LABELING(2),
        BASE_TRANSLATE(3),
        CUSTOM_OBJECT_DETECTION(4),
        CUSTOM_IMAGE_LABELING(5),
        BASE_ENTITY_EXTRACTION(6);
        
        private final int zzi;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzi;
        }

        public static zzev zzb() {
            return zzbe.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzi + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zzi = i;
        }

        static {
            new zzbd();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzc implements zzet {
        SOURCE_UNKNOWN(0),
        APP_ASSET(1),
        LOCAL(2),
        CLOUD(3),
        SDK_BUILT_IN(4),
        URI(5);
        
        private final int zzh;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzh;
        }

        public static zzev zzb() {
            return zzbf.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzc.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
        }

        zzc(int i) {
            this.zzh = i;
        }

        static {
            new zzbg();
        }
    }

    private zzy$zzal() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzal, zza> implements zzgb {
        private zza() {
            super(zzy$zzal.zzl);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzal>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzal> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzal();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဌ\u0002\u0004ဈ\u0003\u0005ဈ\u0004\u0006ဌ\u0005\u0007ဃ\u0006\bဇ\u0007", new Object[]{"zzc", "zzd", "zze", "zzf", zzc.zzb(), "zzg", "zzh", "zzi", zzb.zzb(), "zzj", "zzk"});
            case 4:
                return zzl;
            case 5:
                zzgj<zzy$zzal> zzgjVar2 = zzm;
                zzgj<zzy$zzal> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzal.class) {
                        zzgj<zzy$zzal> zzgjVar4 = zzm;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzl);
                            zzm = zzaVar;
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
        zzy$zzal zzy_zzal = new zzy$zzal();
        zzl = zzy_zzal;
        zzeo.zza(zzy$zzal.class, zzy_zzal);
    }
}
