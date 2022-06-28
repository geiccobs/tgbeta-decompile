package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.wearable.DataApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzce implements zzb<DataApi.DataListener> {
    final /* synthetic */ IntentFilter[] zza;

    public zzce(IntentFilter[] intentFilterArr) {
        this.zza = intentFilterArr;
    }

    @Override // com.google.android.gms.wearable.internal.zzb
    public final /* bridge */ /* synthetic */ void zza(zzhv zzhvVar, BaseImplementation.ResultHolder resultHolder, DataApi.DataListener dataListener, ListenerHolder<DataApi.DataListener> listenerHolder) throws RemoteException {
        zzhvVar.zzt(resultHolder, dataListener, listenerHolder, this.zza);
    }
}
