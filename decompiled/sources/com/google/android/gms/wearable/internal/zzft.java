package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.internal.ApiExceptionUtil;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzft implements BaseImplementation.ResultHolder<zzfu> {
    final /* synthetic */ TaskCompletionSource zza;

    public zzft(zzfv zzfvVar, TaskCompletionSource taskCompletionSource) {
        this.zza = taskCompletionSource;
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder
    public final void setFailedResult(Status status) {
        this.zza.setException(ApiExceptionUtil.fromStatus(status));
    }

    @Override // com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder
    public final /* bridge */ /* synthetic */ void setResult(Object obj) {
        zzfu zzfuVar = (zzfu) obj;
        if (zzfuVar.getStatus().isSuccess()) {
            this.zza.setResult(zzfuVar.zza());
        } else {
            this.zza.setException(ApiExceptionUtil.fromStatus(zzfuVar.getStatus()));
        }
    }
}
