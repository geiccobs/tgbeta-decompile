package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzgh extends zzda<zzgh, zzgg> implements zzef {
    private static final zzgh zzj;
    private int zzb;
    private boolean zze;
    private String zzf = "";
    private long zzg;
    private double zzh;
    private zzgf zzi;

    static {
        zzgh zzghVar = new zzgh();
        zzj = zzghVar;
        zzda.zzq(zzgh.class, zzghVar);
    }

    private zzgh() {
    }

    public static zzgg zza() {
        return zzj.zzl();
    }

    public static /* synthetic */ void zzc(zzgh zzghVar, boolean z) {
        zzghVar.zzb |= 1;
        zzghVar.zze = z;
    }

    public static /* synthetic */ void zzd(zzgh zzghVar, String str) {
        str.getClass();
        zzghVar.zzb |= 2;
        zzghVar.zzf = str;
    }

    public static /* synthetic */ void zze(zzgh zzghVar, zzgf zzgfVar) {
        zzgfVar.getClass();
        zzghVar.zzi = zzgfVar;
        zzghVar.zzb |= 16;
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
                return zzr(zzj, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001ဇ\u0000\u0002ဈ\u0001\u0003ဂ\u0002\u0004က\u0003\u0005ဉ\u0004", new Object[]{"zzb", "zze", "zzf", "zzg", "zzh", "zzi"});
            case 3:
                return new zzgh();
            case 4:
                return new zzgg(null);
            case 5:
                return zzj;
        }
    }
}
