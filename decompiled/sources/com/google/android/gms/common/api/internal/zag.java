package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zag extends zac<Boolean> {
    private final ListenerHolder.ListenerKey<?> zac;

    public zag(ListenerHolder.ListenerKey<?> listenerKey, TaskCompletionSource<Boolean> taskCompletionSource) {
        super(4, taskCompletionSource);
        this.zac = listenerKey;
    }

    @Override // com.google.android.gms.common.api.internal.zac
    public final void zab(GoogleApiManager.zaa<?> zaaVar) throws RemoteException {
        zabv remove = zaaVar.zac().remove(this.zac);
        if (remove != null) {
            remove.zab.unregisterListener(zaaVar.zab(), this.zab);
            remove.zaa.clearListener();
            return;
        }
        this.zab.trySetResult(false);
    }

    @Override // com.google.android.gms.common.api.internal.zad
    public final Feature[] zac(GoogleApiManager.zaa<?> zaaVar) {
        zabv zabvVar = zaaVar.zac().get(this.zac);
        if (zabvVar == null) {
            return null;
        }
        return zabvVar.zaa.getRequiredFeatures();
    }

    @Override // com.google.android.gms.common.api.internal.zad
    public final boolean zad(GoogleApiManager.zaa<?> zaaVar) {
        zabv zabvVar = zaaVar.zac().get(this.zac);
        return zabvVar != null && zabvVar.zaa.zaa();
    }

    @Override // com.google.android.gms.common.api.internal.zac, com.google.android.gms.common.api.internal.zab
    public final /* bridge */ /* synthetic */ void zaa(zav zavVar, boolean z) {
    }

    @Override // com.google.android.gms.common.api.internal.zac, com.google.android.gms.common.api.internal.zab
    public final /* bridge */ /* synthetic */ void zaa(Exception exc) {
        super.zaa(exc);
    }

    @Override // com.google.android.gms.common.api.internal.zac, com.google.android.gms.common.api.internal.zab
    public final /* bridge */ /* synthetic */ void zaa(Status status) {
        super.zaa(status);
    }
}
