package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzc<T> extends zzn<Status> {
    private T zza;
    private ListenerHolder<T> zzb;
    private final zzb<T> zzc;

    private zzc(GoogleApiClient googleApiClient, T t, ListenerHolder<T> listenerHolder, zzb<T> zzbVar) {
        super(googleApiClient);
        this.zza = (T) Preconditions.checkNotNull(t);
        this.zzb = (ListenerHolder) Preconditions.checkNotNull(listenerHolder);
        this.zzc = (zzb) Preconditions.checkNotNull(zzbVar);
    }

    public static <T> PendingResult<Status> zza(GoogleApiClient googleApiClient, zzb<T> zzbVar, T t) {
        return googleApiClient.enqueue(new zzc(googleApiClient, t, googleApiClient.registerListener(t), zzbVar));
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final /* bridge */ /* synthetic */ Result createFailedResult(Status status) {
        this.zza = null;
        this.zzb = null;
        return status;
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl
    protected final /* bridge */ /* synthetic */ void doExecute(zzhv zzhvVar) throws RemoteException {
        this.zzc.zza(zzhvVar, this, this.zza, this.zzb);
        this.zza = null;
        this.zzb = null;
    }
}
