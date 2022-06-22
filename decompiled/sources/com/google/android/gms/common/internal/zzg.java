package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import com.google.android.gms.common.internal.GmsClientSupervisor;
import com.google.android.gms.common.stats.ConnectionTracker;
import java.util.HashMap;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public final class zzg extends GmsClientSupervisor {
    private final Context zzb;
    private final Handler zzc;
    @GuardedBy("connectionStatus")
    private final HashMap<GmsClientSupervisor.zza, zzi> zza = new HashMap<>();
    private final ConnectionTracker zzd = ConnectionTracker.getInstance();
    private final long zze = 5000;
    private final long zzf = 300000;

    public zzg(Context context) {
        this.zzb = context.getApplicationContext();
        this.zzc = new com.google.android.gms.internal.common.zzi(context.getMainLooper(), new zzh(this));
    }

    @Override // com.google.android.gms.common.internal.GmsClientSupervisor
    public final boolean zza(GmsClientSupervisor.zza zzaVar, ServiceConnection serviceConnection, String str) {
        boolean zza;
        Preconditions.checkNotNull(serviceConnection, "ServiceConnection must not be null");
        synchronized (this.zza) {
            zzi zziVar = this.zza.get(zzaVar);
            if (zziVar == null) {
                zziVar = new zzi(this, zzaVar);
                zziVar.zza(serviceConnection, serviceConnection, str);
                zziVar.zza(str);
                this.zza.put(zzaVar, zziVar);
            } else {
                this.zzc.removeMessages(0, zzaVar);
                if (zziVar.zza(serviceConnection)) {
                    String valueOf = String.valueOf(zzaVar);
                    StringBuilder sb = new StringBuilder(valueOf.length() + 81);
                    sb.append("Trying to bind a GmsServiceConnection that was already connected before.  config=");
                    sb.append(valueOf);
                    throw new IllegalStateException(sb.toString());
                }
                zziVar.zza(serviceConnection, serviceConnection, str);
                int zzb = zziVar.zzb();
                if (zzb == 1) {
                    serviceConnection.onServiceConnected(zziVar.zze(), zziVar.zzd());
                } else if (zzb == 2) {
                    zziVar.zza(str);
                }
            }
            zza = zziVar.zza();
        }
        return zza;
    }

    @Override // com.google.android.gms.common.internal.GmsClientSupervisor
    protected final void zzb(GmsClientSupervisor.zza zzaVar, ServiceConnection serviceConnection, String str) {
        Preconditions.checkNotNull(serviceConnection, "ServiceConnection must not be null");
        synchronized (this.zza) {
            zzi zziVar = this.zza.get(zzaVar);
            if (zziVar == null) {
                String valueOf = String.valueOf(zzaVar);
                StringBuilder sb = new StringBuilder(valueOf.length() + 50);
                sb.append("Nonexistent connection status for service config: ");
                sb.append(valueOf);
                throw new IllegalStateException(sb.toString());
            } else if (!zziVar.zza(serviceConnection)) {
                String valueOf2 = String.valueOf(zzaVar);
                StringBuilder sb2 = new StringBuilder(valueOf2.length() + 76);
                sb2.append("Trying to unbind a GmsServiceConnection  that was not bound before.  config=");
                sb2.append(valueOf2);
                throw new IllegalStateException(sb2.toString());
            } else {
                zziVar.zza(serviceConnection, str);
                if (zziVar.zzc()) {
                    this.zzc.sendMessageDelayed(this.zzc.obtainMessage(0, zzaVar), this.zze);
                }
            }
        }
    }
}
