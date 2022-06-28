package com.google.android.gms.internal.location;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
public final class zzaz extends zzi {
    private final zzav zzf;

    public zzaz(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str, ClientSettings clientSettings) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, str, clientSettings);
        this.zzf = new zzav(context, this.zze);
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient, com.google.android.gms.common.api.Api.Client
    public final void disconnect() {
        synchronized (this.zzf) {
            if (isConnected()) {
                try {
                    this.zzf.zzn();
                    this.zzf.zzo();
                } catch (Exception e) {
                    Log.e("LocationClientImpl", "Client disconnected before listeners could be cleaned up", e);
                }
            }
            super.disconnect();
        }
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    public final boolean usesClientTelemetry() {
        return true;
    }

    public final LocationAvailability zzA() throws RemoteException {
        return this.zzf.zzc();
    }

    public final void zzB(zzba zzbaVar, ListenerHolder<LocationCallback> listenerHolder, zzai zzaiVar) throws RemoteException {
        synchronized (this.zzf) {
            this.zzf.zze(zzbaVar, listenerHolder, zzaiVar);
        }
    }

    public final void zzC(LocationRequest locationRequest, ListenerHolder<LocationListener> listenerHolder, zzai zzaiVar) throws RemoteException {
        synchronized (this.zzf) {
            this.zzf.zzd(locationRequest, listenerHolder, zzaiVar);
        }
    }

    public final void zzD(zzba zzbaVar, PendingIntent pendingIntent, zzai zzaiVar) throws RemoteException {
        this.zzf.zzf(zzbaVar, pendingIntent, zzaiVar);
    }

    public final void zzE(LocationRequest locationRequest, PendingIntent pendingIntent, zzai zzaiVar) throws RemoteException {
        this.zzf.zzg(locationRequest, pendingIntent, zzaiVar);
    }

    public final void zzF(ListenerHolder.ListenerKey<LocationListener> listenerKey, zzai zzaiVar) throws RemoteException {
        this.zzf.zzh(listenerKey, zzaiVar);
    }

    public final void zzG(PendingIntent pendingIntent, zzai zzaiVar) throws RemoteException {
        this.zzf.zzj(pendingIntent, zzaiVar);
    }

    public final void zzH(ListenerHolder.ListenerKey<LocationCallback> listenerKey, zzai zzaiVar) throws RemoteException {
        this.zzf.zzi(listenerKey, zzaiVar);
    }

    public final void zzI(boolean z) throws RemoteException {
        this.zzf.zzk(z);
    }

    public final void zzJ(Location location) throws RemoteException {
        this.zzf.zzl(location);
    }

    public final void zzK(zzai zzaiVar) throws RemoteException {
        this.zzf.zzm(zzaiVar);
    }

    public final void zzL(LocationSettingsRequest locationSettingsRequest, BaseImplementation.ResultHolder<LocationSettingsResult> resultHolder, String str) throws RemoteException {
        checkConnected();
        boolean z = true;
        Preconditions.checkArgument(locationSettingsRequest != null, "locationSettingsRequest can't be null nor empty.");
        if (resultHolder == null) {
            z = false;
        }
        Preconditions.checkArgument(z, "listener can't be null.");
        ((zzam) getService()).zzt(locationSettingsRequest, new zzay(resultHolder), null);
    }

    public final void zzq(long j, PendingIntent pendingIntent) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(pendingIntent);
        Preconditions.checkArgument(j >= 0, "detectionIntervalMillis must be >= 0");
        ((zzam) getService()).zzh(j, true, pendingIntent);
    }

    public final void zzr(ActivityTransitionRequest activityTransitionRequest, PendingIntent pendingIntent, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(activityTransitionRequest, "activityTransitionRequest must be specified.");
        Preconditions.checkNotNull(pendingIntent, "PendingIntent must be specified.");
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zzi(activityTransitionRequest, pendingIntent, new StatusCallback(resultHolder));
    }

    public final void zzs(PendingIntent pendingIntent, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zzj(pendingIntent, new StatusCallback(resultHolder));
    }

    public final void zzt(PendingIntent pendingIntent) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(pendingIntent);
        ((zzam) getService()).zzk(pendingIntent);
    }

    public final void zzu(PendingIntent pendingIntent, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(pendingIntent, "PendingIntent must be specified.");
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zzl(pendingIntent, new StatusCallback(resultHolder));
    }

    public final void zzv(GeofencingRequest geofencingRequest, PendingIntent pendingIntent, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(geofencingRequest, "geofencingRequest can't be null.");
        Preconditions.checkNotNull(pendingIntent, "PendingIntent must be specified.");
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zzd(geofencingRequest, pendingIntent, new zzaw(resultHolder));
    }

    public final void zzw(com.google.android.gms.location.zzbq zzbqVar, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(zzbqVar, "removeGeofencingRequest can't be null.");
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zzg(zzbqVar, new zzax(resultHolder));
    }

    public final void zzx(PendingIntent pendingIntent, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkNotNull(pendingIntent, "PendingIntent must be specified.");
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zze(pendingIntent, new zzax(resultHolder), getContext().getPackageName());
    }

    public final void zzy(List<String> list, BaseImplementation.ResultHolder<Status> resultHolder) throws RemoteException {
        checkConnected();
        Preconditions.checkArgument(list != null && list.size() > 0, "geofenceRequestIds can't be null nor empty.");
        Preconditions.checkNotNull(resultHolder, "ResultHolder not provided.");
        ((zzam) getService()).zzf((String[]) list.toArray(new String[0]), new zzax(resultHolder), getContext().getPackageName());
    }

    public final Location zzz(String str) throws RemoteException {
        if (ArrayUtils.contains(getAvailableFeatures(), com.google.android.gms.location.zzu.zzc)) {
            return this.zzf.zza(str);
        }
        return this.zzf.zzb();
    }
}
