package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zzf extends zzjb<zzfi$zzf, zzb> implements zzkm {
    private static final zzfi$zzf zzl;
    private static volatile zzkx<zzfi$zzf> zzm;
    private int zzc;
    private int zzg;
    private long zzi;
    private long zzj;
    private String zzd = "";
    private String zze = "";
    private zzjl<String> zzf = zzjb.zzo();
    private String zzh = "";
    private zzjl<zzfi$zzn> zzk = zzjb.zzo();

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public enum zza implements zzje {
        RESULT_UNKNOWN(0),
        RESULT_SUCCESS(1),
        RESULT_FAIL(2),
        RESULT_SKIPPED(3);
        
        private final int zzf;

        @Override // com.google.android.gms.internal.vision.zzje
        public final int zza() {
            return this.zzf;
        }

        public static zza zza(int i) {
            if (i != 0) {
                if (i == 1) {
                    return RESULT_SUCCESS;
                }
                if (i == 2) {
                    return RESULT_FAIL;
                }
                if (i == 3) {
                    return RESULT_SKIPPED;
                }
                return null;
            }
            return RESULT_UNKNOWN;
        }

        public static zzjg zzb() {
            return zzfo.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzf + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzf = i;
        }

        static {
            new zzfp();
        }
    }

    private zzfi$zzf() {
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzjb.zzb<zzfi$zzf, zzb> implements zzkm {
        private zzb() {
            super(zzfi$zzf.zzl);
        }

        public final zzb zza(String str) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzf) this.zza).zza(str);
            return this;
        }

        public final zzb zza(long j) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzf) this.zza).zza(j);
            return this;
        }

        public final zzb zzb(long j) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzf) this.zza).zzb(j);
            return this;
        }

        public final zzb zza(Iterable<? extends zzfi$zzn> iterable) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zzf) this.zza).zza(iterable);
            return this;
        }

        /* synthetic */ zzb(zzfk zzfkVar) {
            this();
        }
    }

    public final void zza(String str) {
        str.getClass();
        this.zzc |= 1;
        this.zzd = str;
    }

    public final void zza(long j) {
        this.zzc |= 16;
        this.zzi = j;
    }

    public final void zzb(long j) {
        this.zzc |= 32;
        this.zzj = j;
    }

    public final void zza(Iterable<? extends zzfi$zzn> iterable) {
        zzjl<zzfi$zzn> zzjlVar = this.zzk;
        if (!zzjlVar.zza()) {
            this.zzk = zzjb.zza(zzjlVar);
        }
        zzhf.zza(iterable, this.zzk);
    }

    public static zzb zza() {
        return zzl.zzj();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zzf>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zzf> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zzf();
            case 2:
                return new zzb(null);
            case 3:
                return zzjb.zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0002\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003\u001a\u0004ဌ\u0002\u0005ဈ\u0003\u0006ဂ\u0004\u0007ဂ\u0005\b\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", zza.zzb(), "zzh", "zzi", "zzj", "zzk", zzfi$zzn.class});
            case 4:
                return zzl;
            case 5:
                zzkx<zzfi$zzf> zzkxVar2 = zzm;
                zzkx<zzfi$zzf> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zzf.class) {
                        zzkx<zzfi$zzf> zzkxVar4 = zzm;
                        zzkxVar = zzkxVar4;
                        if (zzkxVar4 == null) {
                            ?? zzaVar = new zzjb.zza(zzl);
                            zzm = zzaVar;
                            zzkxVar = zzaVar;
                        }
                    }
                    zzkxVar3 = zzkxVar;
                }
                return zzkxVar3;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        zzfi$zzf zzfi_zzf = new zzfi$zzf();
        zzl = zzfi_zzf;
        zzjb.zza(zzfi$zzf.class, zzfi_zzf);
    }
}
