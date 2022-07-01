package com.google.android.gms.common.api.internal;

import java.util.concurrent.locks.Lock;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
abstract class zaay {
    private final zaaw zaa;

    public zaay(zaaw zaawVar) {
        this.zaa = zaawVar;
    }

    protected abstract void zaa();

    public final void zaa(zaaz zaazVar) {
        Lock lock;
        Lock lock2;
        zaaw zaawVar;
        lock = zaazVar.zaf;
        lock.lock();
        try {
            zaawVar = zaazVar.zan;
            if (zaawVar != this.zaa) {
                return;
            }
            zaa();
        } finally {
            lock2 = zaazVar.zaf;
            lock2.unlock();
        }
    }
}
