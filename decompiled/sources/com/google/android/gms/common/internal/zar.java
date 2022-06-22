package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.TimeUnit;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zar implements PendingResult.StatusListener {
    private final /* synthetic */ PendingResult zaa;
    private final /* synthetic */ TaskCompletionSource zab;
    private final /* synthetic */ PendingResultUtil.ResultConverter zac;
    private final /* synthetic */ PendingResultUtil.zaa zad;

    public zar(PendingResult pendingResult, TaskCompletionSource taskCompletionSource, PendingResultUtil.ResultConverter resultConverter, PendingResultUtil.zaa zaaVar) {
        this.zaa = pendingResult;
        this.zab = taskCompletionSource;
        this.zac = resultConverter;
        this.zad = zaaVar;
    }

    @Override // com.google.android.gms.common.api.PendingResult.StatusListener
    public final void onComplete(Status status) {
        if (status.isSuccess()) {
            this.zab.setResult(this.zac.convert(this.zaa.await(0L, TimeUnit.MILLISECONDS)));
            return;
        }
        this.zab.setException(this.zad.zaa(status));
    }
}
