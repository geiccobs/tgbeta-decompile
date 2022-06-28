package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzgf extends zzda<zzgf, zzge> implements zzef {
    private static final zzgf zzg;
    private int zzb;
    private String zze = "";
    private zzdg<zzgd> zzf = zzw();

    static {
        zzgf zzgfVar = new zzgf();
        zzg = zzgfVar;
        zzda.zzq(zzgf.class, zzgfVar);
    }

    private zzgf() {
    }

    public static zzge zza() {
        return zzg.zzl();
    }

    public static /* synthetic */ void zzc(zzgf zzgfVar, String str) {
        zzgfVar.zzb |= 1;
        zzgfVar.zze = str;
    }

    public static /* synthetic */ void zzd(zzgf zzgfVar, zzgd zzgdVar) {
        zzgdVar.getClass();
        zzdg<zzgd> zzdgVar = zzgfVar.zzf;
        if (!zzdgVar.zza()) {
            zzgfVar.zzf = zzda.zzx(zzdgVar);
        }
        zzgfVar.zzf.add(zzgdVar);
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
                return zzr(zzg, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001ဈ\u0000\u0002\u001b", new Object[]{"zzb", "zze", "zzf", zzgd.class});
            case 3:
                return new zzgf();
            case 4:
                return new zzge(null);
            case 5:
                return zzg;
        }
    }
}
