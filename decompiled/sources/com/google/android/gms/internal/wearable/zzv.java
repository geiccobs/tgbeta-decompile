package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzv extends zzbs<zzv, zzn> implements zzcy {
    private static final zzv zzh;
    private int zzb;
    private zzu zzf;
    private byte zzg = 2;
    private String zze = "";

    static {
        zzv zzvVar = new zzv();
        zzh = zzvVar;
        zzbs.zzR(zzv.class, zzvVar);
    }

    private zzv() {
    }

    public static zzn zzc() {
        return zzh.zzM();
    }

    public static /* synthetic */ void zze(zzv zzvVar, String str) {
        str.getClass();
        zzvVar.zzb |= 1;
        zzvVar.zze = str;
    }

    public static /* synthetic */ void zzf(zzv zzvVar, zzu zzuVar) {
        zzuVar.getClass();
        zzvVar.zzf = zzuVar;
        zzvVar.zzb |= 2;
    }

    @Override // com.google.android.gms.internal.wearable.zzbs
    public final Object zzG(int i, Object obj, Object obj2) {
        byte b = 1;
        switch (i - 1) {
            case 0:
                return Byte.valueOf(this.zzg);
            case 1:
            default:
                if (obj == null) {
                    b = 0;
                }
                this.zzg = b;
                return null;
            case 2:
                return zzS(zzh, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0002\u0001ᔈ\u0000\u0002ᔉ\u0001", new Object[]{"zzb", "zze", "zzf"});
            case 3:
                return new zzv();
            case 4:
                return new zzn(null);
            case 5:
                return zzh;
        }
    }

    public final String zza() {
        return this.zze;
    }

    public final zzu zzb() {
        zzu zzuVar = this.zzf;
        return zzuVar == null ? zzu.zzd() : zzuVar;
    }
}
