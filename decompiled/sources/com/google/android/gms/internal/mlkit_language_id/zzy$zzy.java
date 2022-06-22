package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzy extends zzeo<zzy$zzy, zza> implements zzgb {
    private static final zzy$zzy zzi;
    private static volatile zzgj<zzy$zzy> zzj;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzam zze;
    private zzew<zzb> zzf = zzeo.zzl();
    private zzew<zzb> zzg = zzeo.zzl();
    private long zzh;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo<zzb, zza> implements zzgb {
        private static final zzb zzf;
        private static volatile zzgj<zzb> zzg;
        private int zzc;
        private int zzd;
        private zzeu zze = zzeo.zzk();

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* renamed from: com.google.android.gms.internal.mlkit_language_id.zzy$zzy$zzb$zzb */
        /* loaded from: classes.dex */
        public enum EnumC0001zzb implements zzet {
            UNKNOWN_DATA_TYPE(0),
            TYPE_FLOAT32(1),
            TYPE_INT32(2),
            TYPE_BYTE(3),
            TYPE_LONG(4);
            
            private final int zzg;

            @Override // com.google.android.gms.internal.mlkit_language_id.zzet
            public final int zza() {
                return this.zzg;
            }

            public static zzev zzb() {
                return zzaf.zza;
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "<" + EnumC0001zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzg + " name=" + name() + '>';
            }

            EnumC0001zzb(int i) {
                this.zzg = i;
            }

            static {
                new zzae();
            }
        }

        private zzb() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzb, zza> implements zzgb {
            private zza() {
                super(zzb.zzf);
            }

            /* synthetic */ zza(zzx zzxVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzy$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzb> zzgjVar;
            switch (zzx.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001ဌ\u0000\u0002\u0016", new Object[]{"zzc", "zzd", EnumC0001zzb.zzb(), "zze"});
                case 4:
                    return zzf;
                case 5:
                    zzgj<zzb> zzgjVar2 = zzg;
                    zzgj<zzb> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzb.class) {
                            zzgj<zzb> zzgjVar4 = zzg;
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
            zzb zzbVar = new zzb();
            zzf = zzbVar;
            zzeo.zza(zzb.class, zzbVar);
        }
    }

    private zzy$zzy() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzy, zza> implements zzgb {
        private zza() {
            super(zzy$zzy.zzi);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzy>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzy> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzy();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzi, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001b\u0004\u001b\u0005ဃ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf", zzb.class, "zzg", zzb.class, "zzh"});
            case 4:
                return zzi;
            case 5:
                zzgj<zzy$zzy> zzgjVar2 = zzj;
                zzgj<zzy$zzy> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzy.class) {
                        zzgj<zzy$zzy> zzgjVar4 = zzj;
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
        zzy$zzy zzy_zzy = new zzy$zzy();
        zzi = zzy_zzy;
        zzeo.zza(zzy$zzy.class, zzy_zzy);
    }
}
