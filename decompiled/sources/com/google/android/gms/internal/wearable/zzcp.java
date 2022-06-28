package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzcp implements zzdj {
    private static final zzcv zzb = new zzcn();
    private final zzcv zza;

    public zzcp() {
        zzcv zzcvVar;
        zzcv[] zzcvVarArr = new zzcv[2];
        zzcvVarArr[0] = zzbo.zza();
        try {
            zzcvVar = (zzcv) Class.forName("com.google.protobuf.DescriptorMessageInfoFactory").getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        } catch (Exception e) {
            zzcvVar = zzb;
        }
        zzcvVarArr[1] = zzcvVar;
        zzco zzcoVar = new zzco(zzcvVarArr);
        zzca.zzb(zzcoVar, "messageInfoFactory");
        this.zza = zzcoVar;
    }

    private static boolean zzb(zzcu zzcuVar) {
        return zzcuVar.zzc() == 1;
    }

    @Override // com.google.android.gms.internal.wearable.zzdj
    public final <T> zzdi<T> zza(Class<T> cls) {
        zzdk.zza(cls);
        zzcu zzc = this.zza.zzc(cls);
        if (!zzc.zza()) {
            if (zzbs.class.isAssignableFrom(cls)) {
                if (zzb(zzc)) {
                    return zzda.zzk(cls, zzc, zzdd.zzb(), zzcl.zzd(), zzdk.zzC(), zzbj.zza(), zzct.zzb());
                }
                return zzda.zzk(cls, zzc, zzdd.zzb(), zzcl.zzd(), zzdk.zzC(), null, zzct.zzb());
            } else if (zzb(zzc)) {
                return zzda.zzk(cls, zzc, zzdd.zza(), zzcl.zzc(), zzdk.zzA(), zzbj.zzb(), zzct.zza());
            } else {
                return zzda.zzk(cls, zzc, zzdd.zza(), zzcl.zzc(), zzdk.zzB(), null, zzct.zza());
            }
        } else if (zzbs.class.isAssignableFrom(cls)) {
            return zzdb.zzf(zzdk.zzC(), zzbj.zza(), zzc.zzb());
        } else {
            return zzdb.zzf(zzdk.zzA(), zzbj.zzb(), zzc.zzb());
        }
    }
}
