package com.google.android.gms.common.api.internal;

import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.Preconditions;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaah implements BaseGmsClient.ConnectionProgressReportCallbacks {
    private final WeakReference<zaaf> zaa;
    private final Api<?> zab;
    private final boolean zac;

    public zaah(zaaf zaafVar, Api<?> api, boolean z) {
        this.zaa = new WeakReference<>(zaafVar);
        this.zab = api;
        this.zac = z;
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks
    public final void onReportServiceBinding(ConnectionResult connectionResult) {
        zaaz zaazVar;
        Lock lock;
        Lock lock2;
        boolean zab;
        boolean zad;
        zaaf zaafVar = this.zaa.get();
        if (zaafVar == null) {
            return;
        }
        Looper myLooper = Looper.myLooper();
        zaazVar = zaafVar.zaa;
        Preconditions.checkState(myLooper == zaazVar.zad.getLooper(), "onReportServiceBinding must be called on the GoogleApiClient handler thread");
        lock = zaafVar.zab;
        lock.lock();
        try {
            zab = zaafVar.zab(0);
            if (!zab) {
                return;
            }
            if (!connectionResult.isSuccess()) {
                zaafVar.zab(connectionResult, this.zab, this.zac);
            }
            zad = zaafVar.zad();
            if (zad) {
                zaafVar.zae();
            }
        } finally {
            lock2 = zaafVar.zab;
            lock2.unlock();
        }
    }
}
