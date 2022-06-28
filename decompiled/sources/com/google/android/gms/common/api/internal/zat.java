package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zat implements zabn {
    private final /* synthetic */ zas zaa;

    private zat(zas zasVar) {
        this.zaa = zasVar;
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    public final void zaa(Bundle bundle) {
        Lock lock;
        Lock lock2;
        lock = this.zaa.zam;
        lock.lock();
        try {
            this.zaa.zak = ConnectionResult.RESULT_SUCCESS;
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
            this.zaa.zak = connectionResult;
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
        zaaz zaazVar;
        lock = this.zaa.zam;
        lock.lock();
        try {
            z2 = this.zaa.zal;
            if (z2) {
                this.zaa.zal = false;
                this.zaa.zaa(i, z);
                return;
            }
            this.zaa.zal = true;
            zaazVar = this.zaa.zad;
            zaazVar.onConnectionSuspended(i);
        } finally {
            lock2 = this.zaa.zam;
            lock2.unlock();
        }
    }

    public /* synthetic */ zat(zas zasVar, zar zarVar) {
        this(zasVar);
    }
}
