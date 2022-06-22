package com.google.android.gms.common.api.internal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaw implements OnCompleteListener<TResult> {
    private final /* synthetic */ TaskCompletionSource zaa;
    private final /* synthetic */ zav zab;

    public zaw(zav zavVar, TaskCompletionSource taskCompletionSource) {
        this.zab = zavVar;
        this.zaa = taskCompletionSource;
    }

    @Override // com.google.android.gms.tasks.OnCompleteListener
    public final void onComplete(Task<TResult> task) {
        Map map;
        map = this.zab.zab;
        map.remove(this.zaa);
    }
}
