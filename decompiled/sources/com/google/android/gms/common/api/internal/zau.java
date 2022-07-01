package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zau implements zabn {
    private final /* synthetic */ zas zaa;

    private zau(zas zasVar) {
        this.zaa = zasVar;
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    public final void zaa(Bundle bundle) {
        Lock lock;
        Lock lock2;
        lock = this.zaa.zam;
        lock.lock();
        try {
            this.zaa.zaa(bundle);
            this.zaa.zaj = ConnectionResult.RESULT_SUCCESS;
            this.zaa.zah();
        } finally {
            lock2 = this.zaa.zam;
            lock2.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    public final void zaa(ConnectionResult connectionResult) {
        Lock lock;
        Lock lock2;
        lock = this.zaa.zam;
        lock.lock();
        try {
            this.zaa.zaj = connectionResult;
            this.zaa.zah();
        } finally {
            lock2 = this.zaa.zam;
            lock2.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    public final void zaa(int i, boolean z) {
        Lock lock;
        Lock lock2;
        boolean z2;
        ConnectionResult connectionResult;
        ConnectionResult connectionResult2;
        zaaz zaazVar;
        lock = this.zaa.zam;
        lock.lock();
        try {
            z2 = this.zaa.zal;
            if (!z2) {
                connectionResult = this.zaa.zak;
                if (connectionResult != null) {
                    connectionResult2 = this.zaa.zak;
                    if (connectionResult2.isSuccess()) {
                        this.zaa.zal = true;
                        zaazVar = this.zaa.zae;
                        zaazVar.onConnectionSuspended(i);
                        return;
                    }
                }
            }
            this.zaa.zal = false;
            this.zaa.zaa(i, z);
        } finally {
            lock2 = this.zaa.zam;
            lock2.unlock();
        }
    }

    public /* synthetic */ zau(zas zasVar, zar zarVar) {
        this(zasVar);
    }
}
