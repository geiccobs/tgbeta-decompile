package com.google.android.gms.tasks;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes.dex */
final class zzl<TResult> implements zzr<TResult> {
    private final Executor zza;
    private final Object zzb = new Object();
    @Nullable
    private OnFailureListener zzc;

    public zzl(Executor executor, OnFailureListener onFailureListener) {
        this.zza = executor;
        this.zzc = onFailureListener;
    }

    @Override // com.google.android.gms.tasks.zzr
    public final void zza(Task<TResult> task) {
        if (!task.isSuccessful() && !task.isCanceled()) {
            synchronized (this.zzb) {
                if (this.zzc == null) {
                    return;
                }
                this.zza.execute(new zzk(this, task));
            }
        }
    }

    @Override // com.google.android.gms.tasks.zzr
    public final void zza() {
        synchronized (this.zzb) {
            this.zzc = null;
        }
    }
}
