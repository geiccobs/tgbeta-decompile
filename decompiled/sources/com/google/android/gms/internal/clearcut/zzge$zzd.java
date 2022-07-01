package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzcg;
/* loaded from: classes.dex */
public final class zzge$zzd extends zzcg<zzge$zzd, zza> implements zzdq {
    private static volatile zzdz<zzge$zzd> zzbg;
    private static final zzge$zzd zztx;
    private int zzbb;
    private int zztu;
    private String zztv = "";
    private String zztw = "";

    /* loaded from: classes.dex */
    public static final class zza extends zzcg.zza<zzge$zzd, zza> implements zzdq {
        private zza() {
            super(zzge$zzd.zztx);
        }

        /* synthetic */ zza(zzgf zzgfVar) {
            this();
        }
    }

    static {
        zzge$zzd zzge_zzd = new zzge$zzd();
        zztx = zzge_zzd;
        zzcg.zza(zzge$zzd.class, zzge_zzd);
    }

    private zzge$zzd() {
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.clearcut.zzdz<com.google.android.gms.internal.clearcut.zzge$zzd>, com.google.android.gms.internal.clearcut.zzcg$zzb] */
    @Override // com.google.android.gms.internal.clearcut.zzcg
    public final Object zza(int i, Object obj, Object obj2) {
        zzdz<zzge$zzd> zzdzVar;
        switch (zzgf.zzba[i - 1]) {
            case 1:
                return new zzge$zzd();
            case 2:
                return new zza(null);
            case 3:
                return zzcg.zza(zztx, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0004\u0000\u0000\u0000\u0001\u0004\u0000\u0002\b\u0001\u0003\b\u0002", new Object[]{"zzbb", "zztu", "zztv", "zztw"});
            case 4:
                return zztx;
            case 5:
                zzdz<zzge$zzd> zzdzVar2 = zzbg;
                zzdz<zzge$zzd> zzdzVar3 = zzdzVar2;
                if (zzdzVar2 == null) {
                    synchronized (zzge$zzd.class) {
                        zzdz<zzge$zzd> zzdzVar4 = zzbg;
                        zzdzVar = zzdzVar4;
                        if (zzdzVar4 == null) {
                            ?? zzbVar = new zzcg.zzb(zztx);
                            zzbg = zzbVar;
                            zzdzVar = zzbVar;
                        }
                    }
                    zzdzVar3 = zzdzVar;
                }
                return zzdzVar3;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
