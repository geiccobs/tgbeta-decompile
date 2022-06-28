package com.google.android.gms.internal.phenotype;

import android.database.ContentObserver;
import android.os.Handler;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes3.dex */
public final class zzg extends ContentObserver {
    public zzg(Handler handler) {
        super(null);
    }

    @Override // android.database.ContentObserver
    public final void onChange(boolean z) {
        AtomicBoolean atomicBoolean;
        atomicBoolean = zzf.zzbh;
        atomicBoolean.set(true);
    }
}
