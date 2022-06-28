package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzfw extends zzda<zzfw, zzfv> implements zzef {
    private static final zzfw zzj;
    private int zzb;
    private boolean zze;
    private int zzf;
    private String zzg = "";
    private zzdg<zzfy> zzh = zzw();
    private zzdg<zzfy> zzi = zzw();

    static {
        zzfw zzfwVar = new zzfw();
        zzj = zzfwVar;
        zzda.zzq(zzfw.class, zzfwVar);
    }

    private zzfw() {
    }

    public static zzfw zzd() {
        return zzj;
    }

    public final boolean zza() {
        return this.zze;
    }

    public final int zzb() {
        return this.zzf;
    }

    public final String zzc() {
        return this.zzg;
    }

    @Override // com.google.android.gms.internal.icing.zzda
    public final Object zzf(int i, Object obj, Object obj2) {
        switch (i - 1) {
            case 0:
                return (byte) 1;
            case 1:
            default:
                return null;
            case 2:
                return zzr(zzj, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဇ\u0000\u0002င\u0001\u0003ဈ\u0002\u0004\u001b\u0005\u001b", new Object[]{"zzb", "zze", "zzf", "zzg", "zzh", zzfy.class, "zzi", zzfy.class});
            case 3:
                return new zzfw();
            case 4:
                return new zzfv(null);
            case 5:
                return zzj;
        }
    }
}
