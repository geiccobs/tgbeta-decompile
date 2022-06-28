package com.google.android.gms.cloudmessaging;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.util.concurrent.NamedThreadFactory;
import com.google.android.gms.tasks.Task;
import java.util.concurrent.ScheduledExecutorService;
/* compiled from: com.google.android.gms:play-services-cloud-messaging@@16.0.0 */
/* loaded from: classes3.dex */
public final class zze {
    private static zze zza;
    private final Context zzb;
    private final ScheduledExecutorService zzc;
    private zzf zzd = new zzf(this);
    private int zze = 1;

    public static synchronized zze zza(Context context) {
        zze zzeVar;
        synchronized (zze.class) {
            if (zza == null) {
                zza = new zze(context, com.google.android.gms.internal.cloudmessaging.zza.zza().zza(1, new NamedThreadFactory("MessengerIpcClient"), com.google.android.gms.internal.cloudmessaging.zzf.zzb));
            }
            zzeVar = zza;
        }
        return zzeVar;
    }

    private zze(Context context, ScheduledExecutorService scheduledExecutorService) {
        this.zzc = scheduledExecutorService;
        this.zzb = context.getApplicationContext();
    }

    public final Task<Void> zza(int i, Bundle bundle) {
        return zza(new zzn(zza(), 2, bundle));
    }

    public final Task<Bundle> zzb(int i, Bundle bundle) {
        return zza(new zzs(zza(), 1, bundle));
    }

    private final synchronized <T> Task<T> zza(zzq<T> zzqVar) {
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            String valueOf = String.valueOf(zzqVar);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 9);
            sb.append("Queueing ");
            sb.append(valueOf);
            Log.d("MessengerIpcClient", sb.toString());
        }
        if (!this.zzd.zza((zzq<?>) zzqVar)) {
            zzf zzfVar = new zzf(this);
            this.zzd = zzfVar;
            zzfVar.zza((zzq<?>) zzqVar);
        }
        return zzqVar.zzb.getTask();
    }

    private final synchronized int zza() {
        int i;
        i = this.zze;
        this.zze = i + 1;
        return i;
    }

    public static /* synthetic */ Context zza(zze zzeVar) {
        return zzeVar.zzb;
    }

    public static /* synthetic */ ScheduledExecutorService zzb(zze zzeVar) {
        return zzeVar.zzc;
    }
}
