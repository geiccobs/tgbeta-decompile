package com.google.android.gms.internal.p000authapiphone;

import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.android.gms.tasks.TaskCompletionSource;
/* renamed from: com.google.android.gms.internal.auth-api-phone.zzm */
/* loaded from: classes.dex */
public abstract class zzm extends TaskApiCall<zzi, Void> {
    private TaskCompletionSource<Void> zzf;

    private zzm() {
    }

    public /* synthetic */ zzm(zzk zzkVar) {
        this();
    }

    @Override // com.google.android.gms.common.api.internal.TaskApiCall
    public /* synthetic */ void doExecute(zzi zziVar, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException {
        this.zzf = taskCompletionSource;
        zza((zze) zziVar.getService());
    }

    protected abstract void zza(zze zzeVar) throws RemoteException;

    public final void zzb(Status status) {
        TaskUtil.setResultOrApiException(status, this.zzf);
    }
}
