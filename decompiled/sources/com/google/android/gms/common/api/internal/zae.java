package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zae extends zac<Void> {
    private final zabv zac;

    public zae(zabv zabvVar, TaskCompletionSource<Void> taskCompletionSource) {
        super(3, taskCompletionSource);
        this.zac = zabvVar;
    }

    @Override // com.google.android.gms.common.api.internal.zac
    public final void zab(GoogleApiManager.zaa<?> zaaVar) throws RemoteException {
        this.zac.zaa.registerListener(zaaVar.zab(), this.zab);
        ListenerHolder.ListenerKey<?> listenerKey = this.zac.zaa.getListenerKey();
        if (listenerKey != null) {
            zaaVar.zac().put(listenerKey, this.zac);
        }
    }

    @Override // com.google.android.gms.common.api.internal.zad
    public final Feature[] zac(GoogleApiManager.zaa<?> zaaVar) {
        return this.zac.zaa.getRequiredFeatures();
    }

    @Override // com.google.android.gms.common.api.internal.zad
    public final boolean zad(GoogleApiManager.zaa<?> zaaVar) {
        return this.zac.zaa.zaa();
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
