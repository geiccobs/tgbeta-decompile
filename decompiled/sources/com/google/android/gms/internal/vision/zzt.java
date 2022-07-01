package com.google.android.gms.internal.vision;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.vision.L;
import javax.annotation.concurrent.GuardedBy;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public abstract class zzt<T> {
    private final Context zza;
    private final String zzc;
    private final String zzd;
    private final String zze;
    @GuardedBy("lock")
    private T zzh;
    private final Object zzb = new Object();
    private boolean zzf = false;
    private boolean zzg = false;

    public zzt(Context context, String str, String str2) {
        this.zza = context;
        this.zzc = str;
        String valueOf = String.valueOf(str2);
        this.zzd = valueOf.length() != 0 ? "com.google.android.gms.vision.dynamite.".concat(valueOf) : new String("com.google.android.gms.vision.dynamite.");
        this.zze = str2;
    }

    protected abstract T zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, DynamiteModule.LoadingException;

    protected abstract void zza() throws RemoteException;

    public final boolean zzb() {
        return zzd() != null;
    }

    public final void zzc() {
        synchronized (this.zzb) {
            if (this.zzh == null) {
                return;
            }
            try {
                zza();
            } catch (RemoteException e) {
                Log.e(this.zzc, "Could not finalize native handle", e);
            }
        }
    }

    @RequiresNonNull({"context", "thickFeatureName", "featureName"})
    public final T zzd() {
        synchronized (this.zzb) {
            T t = this.zzh;
            if (t != null) {
                return t;
            }
            DynamiteModule dynamiteModule = null;
            try {
                dynamiteModule = DynamiteModule.load(this.zza, DynamiteModule.PREFER_HIGHEST_OR_REMOTE_VERSION, this.zzd);
            } catch (DynamiteModule.LoadingException unused) {
                String format = String.format("%s.%s", "com.google.android.gms.vision", this.zze);
                L.d("Cannot load thick client module, fall back to load optional module %s", format);
                try {
                    dynamiteModule = DynamiteModule.load(this.zza, DynamiteModule.PREFER_REMOTE, format);
                } catch (DynamiteModule.LoadingException e) {
                    L.e(e, "Error loading optional module %s", format);
                    if (!this.zzf) {
                        L.d("Broadcasting download intent for dependency %s", this.zze);
                        String str = this.zze;
                        Intent intent = new Intent();
                        intent.setClassName("com.google.android.gms", "com.google.android.gms.vision.DependencyBroadcastReceiverProxy");
                        intent.putExtra("com.google.android.gms.vision.DEPENDENCIES", str);
                        intent.setAction("com.google.android.gms.vision.DEPENDENCY");
                        this.zza.sendBroadcast(intent);
                        this.zzf = true;
                    }
                }
            }
            if (dynamiteModule != null) {
                try {
                    this.zzh = zza(dynamiteModule, this.zza);
                } catch (RemoteException | DynamiteModule.LoadingException e2) {
                    Log.e(this.zzc, "Error creating remote native handle", e2);
                }
            }
            boolean z = this.zzg;
            if (!z && this.zzh == null) {
                Log.w(this.zzc, "Native handle not yet available. Reverting to no-op handle.");
                this.zzg = true;
            } else if (z && this.zzh != null) {
                Log.w(this.zzc, "Native handle is now available.");
            }
            return this.zzh;
        }
    }
}
