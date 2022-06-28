package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzaq extends zzda<zzaq, zzan> implements zzef {
    private static final zzaq zze;
    private zzdg<zzap> zzb = zzw();

    static {
        zzaq zzaqVar = new zzaq();
        zze = zzaqVar;
        zzda.zzq(zzaq.class, zzaqVar);
    }

    private zzaq() {
    }

    public static zzan zza() {
        return zze.zzl();
    }

    public static /* synthetic */ void zzc(zzaq zzaqVar, Iterable iterable) {
        zzdg<zzap> zzdgVar = zzaqVar.zzb;
        if (!zzdgVar.zza()) {
            zzaqVar.zzb = zzda.zzx(zzdgVar);
        }
        zzbs.zzk(iterable, zzaqVar.zzb);
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
                return zzr(zze, "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b", new Object[]{"zzb", zzap.class});
            case 3:
                return new zzaq();
            case 4:
                return new zzan(null);
            case 5:
                return zze;
        }
    }
}
