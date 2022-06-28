package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzcg;
/* loaded from: classes3.dex */
public final class zzgc extends zzcg.zzd<zzgc, zza> implements zzdq {
    private static volatile zzdz<zzgc> zzbg;
    private static final zzgc zzsg;
    private byte zzsf = 2;

    /* loaded from: classes3.dex */
    public static final class zza extends zzcg.zzc<zzgc, zza> implements zzdq {
        private zza() {
            super(zzgc.zzsg);
        }

        /* synthetic */ zza(zzgd zzgdVar) {
            this();
        }
    }

    static {
        zzgc zzgcVar = new zzgc();
        zzsg = zzgcVar;
        zzcg.zza(zzgc.class, zzgcVar);
    }

    private zzgc() {
    }

    public static zzgc zzer() {
        return zzsg;
    }

    /* JADX WARN: Type inference failed for: r2v13, types: [com.google.android.gms.internal.clearcut.zzdz<com.google.android.gms.internal.clearcut.zzgc>, com.google.android.gms.internal.clearcut.zzcg$zzb] */
    @Override // com.google.android.gms.internal.clearcut.zzcg
    public final Object zza(int i, Object obj, Object obj2) {
        zzdz<zzgc> zzdzVar;
        int i2 = 1;
        switch (zzgd.zzba[i - 1]) {
            case 1:
                return new zzgc();
            case 2:
                return new zza(null);
            case 3:
                return zza(zzsg, "\u0003\u0000", (Object[]) null);
            case 4:
                return zzsg;
            case 5:
                zzdz<zzgc> zzdzVar2 = zzbg;
                zzdz<zzgc> zzdzVar3 = zzdzVar2;
                if (zzdzVar2 == null) {
                    synchronized (zzgc.class) {
                        zzdz<zzgc> zzdzVar4 = zzbg;
                        zzdzVar = zzdzVar4;
                        if (zzdzVar4 == null) {
                            ?? zzbVar = new zzcg.zzb(zzsg);
                            zzbg = zzbVar;
                            zzdzVar = zzbVar;
                        }
                    }
                    zzdzVar3 = zzdzVar;
                }
                return zzdzVar3;
            case 6:
                return Byte.valueOf(this.zzsf);
            case 7:
                if (obj == null) {
                    i2 = 0;
                }
                this.zzsf = (byte) i2;
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
