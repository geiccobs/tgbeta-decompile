package com.google.android.gms.internal.location;

import android.os.RemoteException;
import com.google.android.gms.common.api.internal.BaseImplementation$ResultHolder;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.location.LocationSettingsResult;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes.dex */
final class zzay extends zzan {
    private BaseImplementation$ResultHolder<LocationSettingsResult> zza;

    public zzay(BaseImplementation$ResultHolder<LocationSettingsResult> baseImplementation$ResultHolder) {
        Preconditions.checkArgument(baseImplementation$ResultHolder != null, "listener can't be null.");
        this.zza = baseImplementation$ResultHolder;
    }

    @Override // com.google.android.gms.internal.location.zzao
    public final void zzb(LocationSettingsResult locationSettingsResult) throws RemoteException {
        this.zza.setResult(locationSettingsResult);
        this.zza = null;
    }
}
