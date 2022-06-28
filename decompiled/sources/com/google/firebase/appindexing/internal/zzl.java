package com.google.firebase.appindexing.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzl extends IStatusCallback.Stub {
    final /* synthetic */ TaskCompletionSource zza;
    final /* synthetic */ zzm zzb;

    public zzl(zzm zzmVar, TaskCompletionSource taskCompletionSource) {
        this.zzb = zzmVar;
        this.zza = taskCompletionSource;
    }

    @Override // com.google.android.gms.common.api.internal.IStatusCallback
    public final void onResult(Status status) throws RemoteException {
        TaskCompletionSource taskCompletionSource;
        TaskCompletionSource taskCompletionSource2;
        if (this.zza.trySetResult(null)) {
            if (status.isSuccess()) {
                taskCompletionSource2 = this.zzb.zza.zzc;
                taskCompletionSource2.setResult(null);
                return;
            }
            taskCompletionSource = this.zzb.zza.zzc;
            taskCompletionSource.setException(zzaf.zza(status, "Indexing error, please try again."));
        }
    }
}
