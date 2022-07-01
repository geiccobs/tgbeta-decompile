package com.google.android.gms.common.api.internal;

import java.util.concurrent.locks.Lock;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
abstract class zaap implements Runnable {
    private final /* synthetic */ zaaf zaa;

    private zaap(zaaf zaafVar) {
        this.zaa = zaafVar;
    }

    protected abstract void zaa();

    @Override // java.lang.Runnable
    public void run() {
        Lock lock;
        zaaz zaazVar;
        Lock lock2;
        lock = this.zaa.zab;
        lock.lock();
        try {
            if (Thread.interrupted()) {
                return;
            }
            zaa();
        } catch (RuntimeException e) {
            zaazVar = this.zaa.zaa;
            zaazVar.zaa(e);
        } finally {
            lock2 = this.zaa.zab;
            lock2.unlock();
        }
    }

    public /* synthetic */ zaap(zaaf zaafVar, zaae zaaeVar) {
        this(zaafVar);
    }
}
