package com.google.android.gms.common.util.concurrent;

import android.os.Process;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
final class zza implements Runnable {
    private final Runnable zza;
    private final int zzb = 0;

    public zza(Runnable runnable, int i) {
        this.zza = runnable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Process.setThreadPriority(this.zzb);
        this.zza.run();
    }
}
