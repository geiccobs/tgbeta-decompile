package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzid$zzj extends zzeo<zzid$zzj, zza> implements zzgb {
    private static final zzid$zzj zzg;
    private static volatile zzgj<zzid$zzj> zzh;
    private int zzc;
    private int zzd;
    private zzid$zzh zze;
    private zzid$zze zzf;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        DELEGATE_NONE(0),
        NNAPI(1),
        GPU(2),
        HEXAGON(3),
        EDGETPU(4);
        
        private final int zzg;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzg;
        }

        public static zzev zzb() {
            return zzio.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzg + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zzg = i;
        }

        static {
            new zzin();
        }
    }

    private zzid$zzj() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzid$zzj, zza> implements zzgb {
        private zza() {
            super(zzid$zzj.zzg);
        }

        /* synthetic */ zza(zzic zzicVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzj>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzid$zzj> zzgjVar;
        switch (zzic.zza[i - 1]) {
            case 1:
                return new zzid$zzj();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဉ\u0001\u0003ဉ\u0002", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzid$zzj> zzgjVar2 = zzh;
                zzgj<zzid$zzj> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzid$zzj.class) {
                        zzgj<zzid$zzj> zzgjVar4 = zzh;
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
        zzid$zzj zzid_zzj = new zzid$zzj();
        zzg = zzid_zzj;
        zzeo.zza(zzid$zzj.class, zzid_zzj);
    }
}
