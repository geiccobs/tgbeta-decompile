package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzu extends zzbs<zzu, zzo> implements zzcy {
    private static final zzu zzh;
    private int zzb;
    private zzt zzf;
    private byte zzg = 2;
    private int zze = 1;

    static {
        zzu zzuVar = new zzu();
        zzh = zzuVar;
        zzbs.zzR(zzu.class, zzuVar);
    }

    private zzu() {
    }

    public static zzo zzc() {
        return zzh.zzM();
    }

    public static zzu zzd() {
        return zzh;
    }

    public static /* synthetic */ void zzf(zzu zzuVar, zzr zzrVar) {
        zzuVar.zze = zzrVar.zza();
        zzuVar.zzb |= 1;
    }

    public static /* synthetic */ void zzg(zzu zzuVar, zzt zztVar) {
        zztVar.getClass();
        zzuVar.zzf = zztVar;
        zzuVar.zzb |= 2;
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
                return zzS(zzh, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0002\u0001ᔌ\u0000\u0002ᐉ\u0001", new Object[]{"zzb", "zze", zzr.zzc(), "zzf"});
            case 3:
                return new zzu();
            case 4:
                return new zzo(null);
            case 5:
                return zzh;
        }
    }

    public final zzr zza() {
        zzr zzb = zzr.zzb(this.zze);
        return zzb == null ? zzr.BYTE_ARRAY : zzb;
    }

    public final zzt zzb() {
        zzt zztVar = this.zzf;
        return zztVar == null ? zzt.zzq() : zztVar;
    }
}
