package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.google.android.gms.common.internal.GmsClientSupervisor;
import com.google.android.gms.common.stats.ConnectionTracker;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class zzi implements ServiceConnection, zzk {
    private final Map<ServiceConnection, ServiceConnection> zza = new HashMap();
    private int zzb = 2;
    private boolean zzc;
    private IBinder zzd;
    private final GmsClientSupervisor.zza zze;
    private ComponentName zzf;
    private final /* synthetic */ zzg zzg;

    public zzi(zzg zzgVar, GmsClientSupervisor.zza zzaVar) {
        this.zzg = zzgVar;
        this.zze = zzaVar;
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        HashMap hashMap;
        Handler handler;
        hashMap = this.zzg.zza;
        synchronized (hashMap) {
            handler = this.zzg.zzc;
            handler.removeMessages(1, this.zze);
            this.zzd = iBinder;
            this.zzf = componentName;
            for (ServiceConnection serviceConnection : this.zza.values()) {
                serviceConnection.onServiceConnected(componentName, iBinder);
            }
            this.zzb = 1;
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        HashMap hashMap;
        Handler handler;
        hashMap = this.zzg.zza;
        synchronized (hashMap) {
            handler = this.zzg.zzc;
            handler.removeMessages(1, this.zze);
            this.zzd = null;
            this.zzf = componentName;
            for (ServiceConnection serviceConnection : this.zza.values()) {
                serviceConnection.onServiceDisconnected(componentName);
            }
            this.zzb = 2;
        }
    }

    public final void zza(String str) {
        ConnectionTracker connectionTracker;
        Context context;
        Context context2;
        ConnectionTracker connectionTracker2;
        Context context3;
        Handler handler;
        Handler handler2;
        long j;
        this.zzb = 3;
        connectionTracker = this.zzg.zzd;
        context = this.zzg.zzb;
        GmsClientSupervisor.zza zzaVar = this.zze;
        context2 = this.zzg.zzb;
        boolean zza = connectionTracker.zza(context, str, zzaVar.zza(context2), this, this.zze.zzc());
        this.zzc = zza;
        if (!zza) {
            this.zzb = 2;
            try {
                connectionTracker2 = this.zzg.zzd;
                context3 = this.zzg.zzb;
                connectionTracker2.unbindService(context3, this);
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        handler = this.zzg.zzc;
        Message obtainMessage = handler.obtainMessage(1, this.zze);
        handler2 = this.zzg.zzc;
        j = this.zzg.zzf;
        handler2.sendMessageDelayed(obtainMessage, j);
    }

    public final void zzb(String str) {
        Handler handler;
        ConnectionTracker connectionTracker;
        Context context;
        handler = this.zzg.zzc;
        handler.removeMessages(1, this.zze);
        connectionTracker = this.zzg.zzd;
        context = this.zzg.zzb;
        connectionTracker.unbindService(context, this);
        this.zzc = false;
        this.zzb = 2;
    }

    public final void zza(ServiceConnection serviceConnection, ServiceConnection serviceConnection2, String str) {
        this.zza.put(serviceConnection, serviceConnection2);
    }

    public final void zza(ServiceConnection serviceConnection, String str) {
        this.zza.remove(serviceConnection);
    }

    public final boolean zza() {
        return this.zzc;
    }

    public final int zzb() {
        return this.zzb;
    }

    public final boolean zza(ServiceConnection serviceConnection) {
        return this.zza.containsKey(serviceConnection);
    }

    public final boolean zzc() {
        return this.zza.isEmpty();
    }

    public final IBinder zzd() {
        return this.zzd;
    }

    public final ComponentName zze() {
        return this.zzf;
    }
}
