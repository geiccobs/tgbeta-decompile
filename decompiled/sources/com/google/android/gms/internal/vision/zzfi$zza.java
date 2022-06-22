package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzfi$zza extends zzjb<zzfi$zza, zza> implements zzkm {
    private static final zzfi$zza zzf;
    private static volatile zzkx<zzfi$zza> zzg;
    private int zzc;
    private String zzd = "";
    private String zze = "";

    private zzfi$zza() {
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static final class zza extends zzjb.zzb<zzfi$zza, zza> implements zzkm {
        private zza() {
            super(zzfi$zza.zzf);
        }

        public final zza zza(String str) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zza) this.zza).zza(str);
            return this;
        }

        public final zza zzb(String str) {
            if (this.zzb) {
                zzb();
                this.zzb = false;
            }
            ((zzfi$zza) this.zza).zzb(str);
            return this;
        }

        /* synthetic */ zza(zzfk zzfkVar) {
            this();
        }
    }

    public final void zza(String str) {
        str.getClass();
        this.zzc |= 1;
        this.zzd = str;
    }

    public final void zzb(String str) {
        str.getClass();
        this.zzc |= 2;
        this.zze = str;
    }

    public static zza zza() {
        return zzf.zzj();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.vision.zzkx<com.google.android.gms.internal.vision.zzfi$zza>, com.google.android.gms.internal.vision.zzjb$zza] */
    @Override // com.google.android.gms.internal.vision.zzjb
    public final Object zza(int i, Object obj, Object obj2) {
        zzkx<zzfi$zza> zzkxVar;
        switch (zzfk.zza[i - 1]) {
            case 1:
                return new zzfi$zza();
            case 2:
                return new zza(null);
            case 3:
                return zzjb.zza(zzf, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001", new Object[]{"zzc", "zzd", "zze"});
            case 4:
                return zzf;
            case 5:
                zzkx<zzfi$zza> zzkxVar2 = zzg;
                zzkx<zzfi$zza> zzkxVar3 = zzkxVar2;
                if (zzkxVar2 == null) {
                    synchronized (zzfi$zza.class) {
                        zzkx<zzfi$zza> zzkxVar4 = zzg;
                        zzkxVar = zzkxVar4;
                        if (zzkxVar4 == null) {
                            ?? zzaVar = new zzjb.zza(zzf);
                            zzg = zzaVar;
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
        zzfi$zza zzfi_zza = new zzfi$zza();
        zzf = zzfi_zza;
        zzjb.zza(zzfi$zza.class, zzfi_zza);
    }
}
