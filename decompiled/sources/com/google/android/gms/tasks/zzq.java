package com.google.android.gms.tasks;

import java.util.ArrayDeque;
import java.util.Queue;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes3.dex */
public final class zzq<TResult> {
    private final Object zza = new Object();
    private Queue<zzr<TResult>> zzb;
    private boolean zzc;

    public final void zza(zzr<TResult> zzrVar) {
        synchronized (this.zza) {
            if (this.zzb == null) {
                this.zzb = new ArrayDeque();
            }
            this.zzb.add(zzrVar);
        }
    }

    public final void zza(Task<TResult> task) {
        zzr<TResult> poll;
        synchronized (this.zza) {
            if (this.zzb != null && !this.zzc) {
                this.zzc = true;
                while (true) {
                    synchronized (this.zza) {
                        poll = this.zzb.poll();
                        if (poll == null) {
                            this.zzc = false;
                            return;
                        }
                    }
                    poll.zza(task);
                }
            }
        }
    }
}
