package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzaf;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzau extends zzeo<zzy$zzau, zza> implements zzgb {
    private static final zzy$zzau zzh;
    private static volatile zzgj<zzy$zzau> zzi;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzai zze;
    private zzc zzf;
    private zzd zzg;

    private zzy$zzau() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzc extends zzeo<zzc, zza> implements zzgb {
        private static final zzc zze;
        private static volatile zzgj<zzc> zzf;
        private int zzc;
        private zzb zzd;

        private zzc() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzc, zza> implements zzgb {
            private zza() {
                super(zzc.zze);
            }

            public final zza zza(zzb.zza zzaVar) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzc) this.zza).zza((zzb) ((zzeo) zzaVar.zzg()));
                return this;
            }

            /* synthetic */ zza(zzx zzxVar) {
                this();
            }
        }

        public final void zza(zzb zzbVar) {
            zzbVar.getClass();
            this.zzd = zzbVar;
            this.zzc |= 1;
        }

        public static zza zza() {
            return zze.zzh();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzau$zzc>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzc> zzgjVar;
            switch (zzx.zza[i - 1]) {
                case 1:
                    return new zzc();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ဉ\u0000", new Object[]{"zzc", "zzd"});
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

        public static zzc zzb() {
            return zze;
        }

        static {
            zzc zzcVar = new zzc();
            zze = zzcVar;
            zzeo.zza(zzc.class, zzcVar);
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzau, zza> implements zzgb {
        private zza() {
            super(zzy$zzau.zzh);
        }

        public final zza zza(zzy$zzaf.zza zzaVar) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzau) this.zza).zza((zzy$zzaf) ((zzeo) zzaVar.zzg()));
            return this;
        }

        public final zza zza(zzy$zzai zzy_zzai) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzau) this.zza).zza(zzy_zzai);
            return this;
        }

        public final zza zza(zzc zzcVar) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzau) this.zza).zza(zzcVar);
            return this;
        }

        public final zza zza(zzd zzdVar) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzau) this.zza).zza(zzdVar);
            return this;
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    public final void zza(zzy$zzaf zzy_zzaf) {
        zzy_zzaf.getClass();
        this.zzd = zzy_zzaf;
        this.zzc |= 1;
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo<zzb, zza> implements zzgb {
        private static final zzb zzf;
        private static volatile zzgj<zzb> zzg;
        private int zzc;
        private float zzd;
        private String zze = "";

        private zzb() {
        }

        public final void zza(String str) {
            str.getClass();
            this.zzc |= 2;
            this.zze = str;
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzb, zza> implements zzgb {
            private zza() {
                super(zzb.zzf);
            }

            public final zza zza(String str) {
                if (this.zzb) {
                    zzc();
                    this.zzb = false;
                }
                ((zzb) this.zza).zza(str);
                return this;
            }

            /* synthetic */ zza(zzx zzxVar) {
                this();
            }
        }

        public static zza zza() {
            return zzf.zzh();
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzau$zzb>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzb> zzgjVar;
            switch (zzx.zza[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ခ\u0000\u0002ဈ\u0001", new Object[]{"zzc", "zzd", "zze"});
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

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzd extends zzeo<zzd, zza> implements zzgb {
        private static final zzd zzd;
        private static volatile zzgj<zzd> zze;
        private zzew<zzb> zzc = zzeo.zzl();

        private zzd() {
        }

        /* compiled from: com.google.mlkit:language-id@@16.1.1 */
        /* loaded from: classes.dex */
        public static final class zza extends zzeo.zzb<zzd, zza> implements zzgb {
            private zza() {
                super(zzd.zzd);
            }

            /* synthetic */ zza(zzx zzxVar) {
                this();
            }
        }

        /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzau$zzd>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
        public final Object zza(int i, Object obj, Object obj2) {
            zzgj<zzd> zzgjVar;
            switch (zzx.zza[i - 1]) {
                case 1:
                    return new zzd();
                case 2:
                    return new zza(null);
                case 3:
                    return zzeo.zza(zzd, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzc", zzb.class});
                case 4:
                    return zzd;
                case 5:
                    zzgj<zzd> zzgjVar2 = zze;
                    zzgj<zzd> zzgjVar3 = zzgjVar2;
                    if (zzgjVar2 == null) {
                        synchronized (zzd.class) {
                            zzgj<zzd> zzgjVar4 = zze;
                            zzgjVar = zzgjVar4;
                            if (zzgjVar4 == null) {
                                ?? zzaVar = new zzeo.zza(zzd);
                                zze = zzaVar;
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
            zzd zzdVar = new zzd();
            zzd = zzdVar;
            zzeo.zza(zzd.class, zzdVar);
        }
    }

    public final void zza(zzy$zzai zzy_zzai) {
        zzy_zzai.getClass();
        this.zze = zzy_zzai;
        this.zzc |= 2;
    }

    public final void zza(zzc zzcVar) {
        zzcVar.getClass();
        this.zzf = zzcVar;
        this.zzc |= 4;
    }

    public final void zza(zzd zzdVar) {
        zzdVar.getClass();
        this.zzg = zzdVar;
        this.zzc |= 8;
    }

    public static zza zza() {
        return zzh.zzh();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzau>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzau> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzau();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဉ\u0003", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg"});
            case 4:
                return zzh;
            case 5:
                zzgj<zzy$zzau> zzgjVar2 = zzi;
                zzgj<zzy$zzau> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzau.class) {
                        zzgj<zzy$zzau> zzgjVar4 = zzi;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzh);
                            zzi = zzaVar;
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
        zzy$zzau zzy_zzau = new zzy$zzau();
        zzh = zzy_zzau;
        zzeo.zza(zzy$zzau.class, zzy_zzau);
    }
}
