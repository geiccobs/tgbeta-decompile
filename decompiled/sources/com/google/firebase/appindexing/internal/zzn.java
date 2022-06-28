package com.google.firebase.appindexing.internal;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Queue;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzn {
    final /* synthetic */ zzo zza;
    private final zzz zzb;
    private final TaskCompletionSource<Void> zzc = new TaskCompletionSource<>();

    public zzn(zzo zzoVar, zzz zzzVar) {
        this.zza = zzoVar;
        this.zzb = zzzVar;
    }

    public final Task<Void> zza() {
        return this.zzc.getTask();
    }

    public final /* synthetic */ void zzc(Exception exc) {
        Queue queue;
        Queue queue2;
        zzn zznVar;
        Queue queue3;
        Queue queue4;
        queue = this.zza.zzc;
        synchronized (queue) {
            queue2 = this.zza.zzc;
            if (queue2.peek() == this) {
                queue3 = this.zza.zzc;
                queue3.remove();
                this.zza.zzd = 0;
                queue4 = this.zza.zzc;
                zznVar = (zzn) queue4.peek();
            } else {
                zznVar = null;
            }
        }
        this.zzc.trySetException(exc);
        if (zznVar != null) {
            zznVar.zzb();
        }
    }

    public final void zzb() {
        Queue queue;
        int i;
        GoogleApi googleApi;
        queue = this.zza.zzc;
        synchronized (queue) {
            i = this.zza.zzd;
            Preconditions.checkState(i == 0);
            this.zza.zzd = 1;
        }
        googleApi = this.zza.zza;
        googleApi.doWrite(new zzm(this, null)).addOnFailureListener(this.zza, new OnFailureListener(this) { // from class: com.google.firebase.appindexing.internal.zzk
            private final zzn zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
            }

            @Override // com.google.android.gms.tasks.OnFailureListener
            public final void onFailure(Exception exc) {
                this.zza.zzc(exc);
            }
        });
    }
}
