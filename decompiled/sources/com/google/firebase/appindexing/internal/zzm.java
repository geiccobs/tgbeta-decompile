package com.google.firebase.appindexing.internal;

import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseAppIndexingException;
import java.util.Queue;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzm extends TaskApiCall<zzf, Void> {
    final /* synthetic */ zzn zza;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ zzm(zzn zznVar, zzi zziVar) {
        super(null, false, 9002);
        this.zza = zznVar;
    }

    @Override // com.google.android.gms.common.api.internal.TaskApiCall
    public final /* bridge */ /* synthetic */ void doExecute(zzf zzfVar, TaskCompletionSource<Void> taskCompletionSource) throws RemoteException {
        zzz zzzVar;
        Queue queue;
        Queue queue2;
        Queue queue3;
        TaskCompletionSource taskCompletionSource2;
        Queue queue4;
        int i;
        Queue queue5;
        zzl zzlVar = new zzl(this, taskCompletionSource);
        zzzVar = this.zza.zzb;
        zzg zzd = ((zzv) zzfVar.getService()).zzd(zzlVar, zzzVar);
        int i2 = zzd == null ? 2 : zzd.zzd;
        boolean z = false;
        boolean z2 = true;
        zzn zznVar = null;
        if (i2 == 3) {
            if (zzw.zzb(4)) {
                Log.i(FirebaseAppIndex.APP_INDEXING_API_TAG, "Queue was full. API call will be retried.");
            }
            if (taskCompletionSource.trySetResult(null)) {
                queue4 = this.zza.zza.zzc;
                synchronized (queue4) {
                    i = this.zza.zza.zzd;
                    if (i == 0) {
                        queue5 = this.zza.zza.zzc;
                        zzn zznVar2 = (zzn) queue5.peek();
                        if (zznVar2 == this.zza) {
                            z = true;
                        }
                        Preconditions.checkState(z);
                        zznVar = zznVar2;
                    } else {
                        this.zza.zza.zzd = 2;
                    }
                }
            }
        } else {
            if (i2 != 1) {
                StringBuilder sb = new StringBuilder(41);
                sb.append("API call failed. Status code: ");
                sb.append(i2);
                String sb2 = sb.toString();
                if (zzw.zzb(6)) {
                    Log.e(FirebaseAppIndex.APP_INDEXING_API_TAG, sb2);
                }
                if (taskCompletionSource.trySetResult(null)) {
                    taskCompletionSource2 = this.zza.zzc;
                    taskCompletionSource2.setException(new FirebaseAppIndexingException("Indexing error."));
                }
            }
            queue = this.zza.zza.zzc;
            synchronized (queue) {
                queue2 = this.zza.zza.zzc;
                if (((zzn) queue2.poll()) != this.zza) {
                    z2 = false;
                }
                Preconditions.checkState(z2);
                queue3 = this.zza.zza.zzc;
                zznVar = (zzn) queue3.peek();
                this.zza.zza.zzd = 0;
            }
        }
        if (zznVar != null) {
            zznVar.zzb();
        }
    }
}
