package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.ChannelApi;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
final class zzal extends zzn<Status> {
    private final String zza;
    private ChannelApi.ChannelListener zzb;

    public zzal(GoogleApiClient googleApiClient, ChannelApi.ChannelListener channelListener, @Nullable String str) {
        super(googleApiClient);
        this.zzb = (ChannelApi.ChannelListener) Preconditions.checkNotNull(channelListener);
        this.zza = str;
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final /* bridge */ /* synthetic */ Result createFailedResult(Status status) {
        this.zzb = null;
        return status;
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ApiMethodImpl
    protected final /* bridge */ /* synthetic */ void doExecute(zzhv zzhvVar) throws RemoteException {
        zzhvVar.zzA(this, this.zzb, this.zza);
        this.zzb = null;
    }
}
