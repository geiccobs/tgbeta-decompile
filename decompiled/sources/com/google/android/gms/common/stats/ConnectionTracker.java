package com.google.android.gms.common.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.zzk;
import com.google.android.gms.common.util.ClientLibraryUtils;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class ConnectionTracker {
    @Nullable
    private static volatile ConnectionTracker zzb;
    private ConcurrentHashMap<ServiceConnection, ServiceConnection> zzd = new ConcurrentHashMap<>();
    private static final Object zza = new Object();
    private static boolean zzc = false;

    public static ConnectionTracker getInstance() {
        if (zzb == null) {
            synchronized (zza) {
                if (zzb == null) {
                    zzb = new ConnectionTracker();
                }
            }
        }
        return (ConnectionTracker) Preconditions.checkNotNull(zzb);
    }

    private ConnectionTracker() {
    }

    public final boolean zza(Context context, String str, Intent intent, ServiceConnection serviceConnection, int i) {
        return zza(context, str, intent, serviceConnection, i, true);
    }

    private final boolean zza(Context context, String str, Intent intent, ServiceConnection serviceConnection, int i, boolean z) {
        boolean z2;
        ComponentName component = intent.getComponent();
        if (component == null) {
            z2 = false;
        } else {
            z2 = ClientLibraryUtils.zza(context, component.getPackageName());
        }
        if (z2) {
            Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
            return false;
        } else if (zza(serviceConnection)) {
            ServiceConnection putIfAbsent = this.zzd.putIfAbsent(serviceConnection, serviceConnection);
            if (putIfAbsent != null && serviceConnection != putIfAbsent) {
                Log.w("ConnectionTracker", String.format("Duplicate binding with the same ServiceConnection: %s, %s, %s.", serviceConnection, str, intent.getAction()));
            }
            try {
                boolean bindService = context.bindService(intent, serviceConnection, i);
                return !bindService ? bindService : bindService;
            } finally {
                this.zzd.remove(serviceConnection, serviceConnection);
            }
        } else {
            return context.bindService(intent, serviceConnection, i);
        }
    }

    public boolean bindService(Context context, Intent intent, ServiceConnection serviceConnection, int i) {
        return zza(context, context.getClass().getName(), intent, serviceConnection, i);
    }

    public void unbindService(Context context, ServiceConnection serviceConnection) {
        if (zza(serviceConnection) && this.zzd.containsKey(serviceConnection)) {
            try {
                try {
                    context.unbindService(this.zzd.get(serviceConnection));
                } catch (IllegalArgumentException e) {
                } catch (IllegalStateException e2) {
                } catch (NoSuchElementException e3) {
                }
                return;
            } finally {
                this.zzd.remove(serviceConnection);
            }
        }
        try {
            context.unbindService(serviceConnection);
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e4) {
        }
    }

    private static boolean zza(ServiceConnection serviceConnection) {
        return !(serviceConnection instanceof zzk);
    }

    public void unbindServiceSafe(Context context, ServiceConnection serviceConnection) {
        try {
            unbindService(context, serviceConnection);
        } catch (IllegalArgumentException e) {
            Log.w("ConnectionTracker", "Exception thrown while unbinding", e);
        }
    }
}
