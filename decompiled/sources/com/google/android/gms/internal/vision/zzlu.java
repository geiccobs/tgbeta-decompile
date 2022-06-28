package com.google.android.gms.internal.vision;

import java.io.IOException;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public abstract class zzlu<T, B> {
    public abstract B zza();

    abstract T zza(B b);

    abstract void zza(B b, int i, int i2);

    public abstract void zza(B b, int i, long j);

    public abstract void zza(B b, int i, zzht zzhtVar);

    abstract void zza(B b, int i, T t);

    public abstract void zza(T t, zzmr zzmrVar) throws IOException;

    public abstract void zza(Object obj, T t);

    public abstract boolean zza(zzld zzldVar);

    public abstract T zzb(Object obj);

    abstract void zzb(B b, int i, long j);

    public abstract void zzb(T t, zzmr zzmrVar) throws IOException;

    public abstract void zzb(Object obj, B b);

    public abstract B zzc(Object obj);

    public abstract T zzc(T t, T t2);

    public abstract void zzd(Object obj);

    public abstract int zze(T t);

    public abstract int zzf(T t);

    public final boolean zza(B b, zzld zzldVar) throws IOException {
        int zzb = zzldVar.zzb();
        int i = zzb >>> 3;
        switch (zzb & 7) {
            case 0:
                zza((zzlu<T, B>) b, i, zzldVar.zzg());
                return true;
            case 1:
                zzb(b, i, zzldVar.zzi());
                return true;
            case 2:
                zza((zzlu<T, B>) b, i, zzldVar.zzn());
                return true;
            case 3:
                B zza = zza();
                int i2 = (i << 3) | 4;
                while (zzldVar.zza() != Integer.MAX_VALUE && zza((zzlu<T, B>) zza, zzldVar)) {
                }
                if (i2 != zzldVar.zzb()) {
                    throw zzjk.zze();
                }
                zza((zzlu<T, B>) b, i, (int) zza((zzlu<T, B>) zza));
                return true;
            case 4:
                return false;
            case 5:
                zza((zzlu<T, B>) b, i, zzldVar.zzj());
                return true;
            default:
                throw zzjk.zzf();
        }
    }
}
