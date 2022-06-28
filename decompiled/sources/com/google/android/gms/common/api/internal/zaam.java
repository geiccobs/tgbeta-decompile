package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.locks.Lock;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final class zaam implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final /* synthetic */ zaaf zaa;

    private zaam(zaaf zaafVar) {
        this.zaa = zaafVar;
    }

    @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
    public final void onConnected(Bundle bundle) {
        ClientSettings clientSettings;
        com.google.android.gms.signin.zae zaeVar;
        clientSettings = this.zaa.zar;
        ClientSettings clientSettings2 = (ClientSettings) Preconditions.checkNotNull(clientSettings);
        zaeVar = this.zaa.zak;
        ((com.google.android.gms.signin.zae) Preconditions.checkNotNull(zaeVar)).zaa(new zaak(this.zaa));
    }

    @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
    public final void onConnectionSuspended(int i) {
    }

    @Override // com.google.android.gms.common.api.internal.OnConnectionFailedListener
    public final void onConnectionFailed(ConnectionResult connectionResult) {
        Lock lock;
        Lock lock2;
        boolean zaa;
        lock = this.zaa.zab;
        lock.lock();
        try {
            zaa = this.zaa.zaa(connectionResult);
            if (!zaa) {
                this.zaa.zab(connectionResult);
            } else {
                this.zaa.zag();
                this.zaa.zae();
            }
        } finally {
            lock2 = this.zaa.zab;
            lock2.unlock();
        }
    }

    public /* synthetic */ zaam(zaaf zaafVar, zaae zaaeVar) {
        this(zaafVar);
    }
}
