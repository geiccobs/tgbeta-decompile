package com.google.android.gms.internal.vision;

import java.io.Serializable;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public abstract class zzcy<T> implements Serializable {
    public static <T> zzcy<T> zzc() {
        return zzcv.zza;
    }

    public abstract boolean zza();

    public abstract T zzb();

    public static <T> zzcy<T> zza(T t) {
        return new zzdd(zzde.zza(t));
    }
}
