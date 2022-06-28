package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzga extends zzda<zzga, zzfz> implements zzef {
    private static final zzga zzi;
    private int zzb;
    private String zze = "";
    private String zzf = "";
    private zzdg<zzfy> zzg = zzw();
    private zzfw zzh;

    static {
        zzga zzgaVar = new zzga();
        zzi = zzgaVar;
        zzda.zzq(zzga.class, zzgaVar);
    }

    private zzga() {
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
                return zzr(zzi, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0001\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003\u001b\u0004ဉ\u0002", new Object[]{"zzb", "zze", "zzf", "zzg", zzfy.class, "zzh"});
            case 3:
                return new zzga();
            case 4:
                return new zzfz(null);
            case 5:
                return zzi;
        }
    }
}
