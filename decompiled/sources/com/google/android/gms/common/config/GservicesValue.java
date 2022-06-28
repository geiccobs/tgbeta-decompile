package com.google.android.gms.common.config;

import android.content.Context;
import android.os.Binder;
import android.os.StrictMode;
import android.util.Log;
import java.util.Set;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public abstract class GservicesValue<T> {
    private static final Object zzc = new Object();
    private static zza zzd = null;
    private static int zze = 0;
    private static Context zzf;
    private static Set<String> zzg;
    protected final String zza;
    protected final T zzb;
    private T zzh = null;

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface zza {
        Boolean zza(String str, Boolean bool);

        Float zza(String str, Float f);

        Integer zza(String str, Integer num);

        Long zza(String str, Long l);

        String zza(String str, String str2);
    }

    private static boolean zza() {
        synchronized (zzc) {
        }
        return false;
    }

    protected abstract T zza(String str);

    public static boolean isInitialized() {
        synchronized (zzc) {
        }
        return false;
    }

    public GservicesValue(String str, T t) {
        this.zza = str;
        this.zzb = t;
    }

    public void override(T t) {
        Log.w("GservicesValue", "GservicesValue.override(): test should probably call initForTests() first");
        this.zzh = t;
        synchronized (zzc) {
            zza();
        }
    }

    public void resetOverride() {
        this.zzh = null;
    }

    public final T get() {
        T t = this.zzh;
        if (t != null) {
            return t;
        }
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        Object obj = zzc;
        synchronized (obj) {
        }
        synchronized (obj) {
            zzg = null;
            zzf = null;
        }
        try {
            return zza(this.zza);
        } catch (SecurityException e) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            T zza2 = zza(this.zza);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return zza2;
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
        }
    }

    @Deprecated
    public final T getBinderSafe() {
        return get();
    }

    public static GservicesValue<Boolean> value(String str, boolean z) {
        return new zzb(str, Boolean.valueOf(z));
    }

    public static GservicesValue<Long> value(String str, Long l) {
        return new com.google.android.gms.common.config.zza(str, l);
    }

    public static GservicesValue<Integer> value(String str, Integer num) {
        return new zzd(str, num);
    }

    public static GservicesValue<Float> value(String str, Float f) {
        return new zzc(str, f);
    }

    public static GservicesValue<String> value(String str, String str2) {
        return new zze(str, str2);
    }
}
