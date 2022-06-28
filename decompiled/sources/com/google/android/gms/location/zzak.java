package com.google.android.gms.location;

import android.os.RemoteException;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.tasks.TaskCompletionSource;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
public final class zzak extends zzap {
    final /* synthetic */ ListenerHolder zza;
    final /* synthetic */ FusedLocationProviderClient zzb;

    public zzak(FusedLocationProviderClient fusedLocationProviderClient, ListenerHolder listenerHolder) {
        this.zzb = fusedLocationProviderClient;
        this.zza = listenerHolder;
    }

    @Override // com.google.android.gms.common.api.internal.RemoteCall
    public final /* bridge */ /* synthetic */ void accept(com.google.android.gms.internal.location.zzaz zzazVar, TaskCompletionSource<Boolean> taskCompletionSource) throws RemoteException {
        com.google.android.gms.internal.location.zzaz zzazVar2 = zzazVar;
        TaskCompletionSource<Boolean> taskCompletionSource2 = taskCompletionSource;
        if (zza()) {
            zzal zzalVar = new zzal(this.zzb, taskCompletionSource2);
            try {
                ListenerHolder.ListenerKey<LocationCallback> listenerKey = this.zza.getListenerKey();
                if (listenerKey != null) {
                    zzazVar2.zzH(listenerKey, zzalVar);
                }
            } catch (RuntimeException e) {
                taskCompletionSource2.trySetException(e);
            }
        }
    }
}
