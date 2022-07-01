package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzbf extends zzeo<zzy$zzbf, zzb> implements zzgb {
    private static final zzy$zzbf zzf;
    private static volatile zzgj<zzy$zzbf> zzg;
    private int zzc;
    private int zzd;
    private int zze;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        UNKNOWN(0),
        CANONICAL(1),
        TFLITE(2),
        TFLITE_SUPPORT(3);
        
        private final int zzf;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzf;
        }

        public static zzev zzb() {
            return zzca.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzf = i;
        }

        static {
            new zzcb();
        }
    }

    private zzy$zzbf() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzy$zzbf, zzb> implements zzgb {
        private zzb() {
            super(zzy$zzbf.zzf);
        }

        /* synthetic */ zzb(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbf>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzbf> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzbf();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002င\u0001", new Object[]{"zzc", "zzd", zza.zzb(), "zze"});
            case 4:
                return zzf;
            case 5:
                zzgj<zzy$zzbf> zzgjVar2 = zzg;
                zzgj<zzy$zzbf> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzbf.class) {
                        zzgj<zzy$zzbf> zzgjVar4 = zzg;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzf);
                            zzg = zzaVar;
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
        zzy$zzbf zzy_zzbf = new zzy$zzbf();
        zzf = zzy_zzbf;
        zzeo.zza(zzy$zzbf.class, zzy_zzbf);
    }
}
