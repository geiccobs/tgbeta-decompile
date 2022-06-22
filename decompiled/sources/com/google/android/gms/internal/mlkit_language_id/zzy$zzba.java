package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzba extends zzeo<zzy$zzba, zzb> implements zzgb {
    private static final zzy$zzba zzg;
    private static volatile zzgj<zzy$zzba> zzh;
    private int zzc;
    private int zzd;
    private int zze;
    private int zzf;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        INVALID_MODE(0),
        STREAM(1),
        SINGLE_IMAGE(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zze;
        }

        public static zzev zzb() {
            return zzbs.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zza(int i) {
            this.zze = i;
        }

        static {
            new zzbt();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzc implements zzet {
        UNKNOWN_PERFORMANCE(0),
        FAST(1),
        ACCURATE(2);
        
        private final int zze;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zze;
        }

        public static zzev zzb() {
            return zzbv.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzc.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zze + " name=" + name() + '>';
        }

        zzc(int i) {
            this.zze = i;
        }

        static {
            new zzbu();
        }
    }

    private zzy$zzba() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzy$zzba, zzb> implements zzgb {
        private zzb() {
            super(zzy$zzba.zzg);
        }

        /* synthetic */ zzb(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzba>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzba> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzba();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဌ\u0001\u0003ဌ\u0002", new Object[]{"zzc", "zzd", zza.zzb(), "zze", zzc.zzb(), "zzf", zzc.zzb()});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzba> zzgjVar2 = zzh;
                zzgj<zzy$zzba> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzba.class) {
                        zzgj<zzy$zzba> zzgjVar4 = zzh;
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
        zzy$zzba zzy_zzba = new zzy$zzba();
        zzg = zzy_zzba;
        zzeo.zza(zzy$zzba.class, zzy_zzba);
    }
}
