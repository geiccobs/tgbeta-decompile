package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzid$zzk extends zzeo<zzid$zzk, zzb> implements zzgb {
    private static final zzid$zzk zzf;
    private static volatile zzgj<zzid$zzk> zzg;
    private int zzc;
    private int zzd;
    private float zze;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        UNKNOWN_METRIC(0),
        MEAN_ABSOLUTE_ERROR(1),
        MEAN_SQUARED_ERROR(2),
        ROOT_MEAN_SQUARED_ERROR(3);
        
        private final int zzf;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzf;
        }

        public static zzev zzb() {
            return zzip.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzf = i;
        }

        static {
            new zziq();
        }
    }

    private zzid$zzk() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzid$zzk, zzb> implements zzgb {
        private zzb() {
            super(zzid$zzk.zzf);
        }

        /* synthetic */ zzb(zzic zzicVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzk>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzid$zzk> zzgjVar;
        switch (zzic.zza[i - 1]) {
            case 1:
                return new zzid$zzk();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဌ\u0000\u0002ခ\u0001", new Object[]{"zzc", "zzd", zza.zzb(), "zze"});
            case 4:
                return zzf;
            case 5:
                zzgj<zzid$zzk> zzgjVar2 = zzg;
                zzgj<zzid$zzk> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzid$zzk.class) {
                        zzgj<zzid$zzk> zzgjVar4 = zzg;
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
        zzid$zzk zzid_zzk = new zzid$zzk();
        zzf = zzid_zzk;
        zzeo.zza(zzid$zzk.class, zzid_zzk);
    }
}
