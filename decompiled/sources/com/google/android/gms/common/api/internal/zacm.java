package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.internal.Preconditions;
import java.lang.ref.WeakReference;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final class zacm implements Runnable {
    private final /* synthetic */ Result zaa;
    private final /* synthetic */ zacn zab;

    public zacm(zacn zacnVar, Result result) {
        this.zab = zacnVar;
        this.zaa = result;
    }

    @Override // java.lang.Runnable
    public final void run() {
        zacp zacpVar;
        zacp zacpVar2;
        WeakReference weakReference;
        WeakReference weakReference2;
        ResultTransform resultTransform;
        zacp zacpVar3;
        zacp zacpVar4;
        WeakReference weakReference3;
        try {
            try {
                BasePendingResult.zaa.set(true);
                resultTransform = this.zab.zaa;
                PendingResult onSuccess = ((ResultTransform) Preconditions.checkNotNull(resultTransform)).onSuccess(this.zaa);
                zacpVar3 = this.zab.zah;
                zacpVar4 = this.zab.zah;
                zacpVar3.sendMessage(zacpVar4.obtainMessage(0, onSuccess));
                BasePendingResult.zaa.set(false);
                zacn zacnVar = this.zab;
                zacn.zaa(this.zaa);
                weakReference3 = this.zab.zag;
                GoogleApiClient googleApiClient = (GoogleApiClient) weakReference3.get();
                if (googleApiClient != null) {
                    googleApiClient.zab(this.zab);
                }
            } catch (RuntimeException e) {
                zacpVar = this.zab.zah;
                zacpVar2 = this.zab.zah;
                zacpVar.sendMessage(zacpVar2.obtainMessage(1, e));
                BasePendingResult.zaa.set(false);
                zacn zacnVar2 = this.zab;
                zacn.zaa(this.zaa);
                weakReference = this.zab.zag;
                GoogleApiClient googleApiClient2 = (GoogleApiClient) weakReference.get();
                if (googleApiClient2 != null) {
                    googleApiClient2.zab(this.zab);
                }
            }
        } catch (Throwable th) {
            BasePendingResult.zaa.set(false);
            zacn zacnVar3 = this.zab;
            zacn.zaa(this.zaa);
            weakReference2 = this.zab.zag;
            GoogleApiClient googleApiClient3 = (GoogleApiClient) weakReference2.get();
            if (googleApiClient3 != null) {
                googleApiClient3.zab(this.zab);
            }
            throw th;
        }
    }
}
