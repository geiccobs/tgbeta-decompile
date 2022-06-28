package com.google.android.gms.common;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zab implements SuccessContinuation {
    static final SuccessContinuation zaa = new zab();

    private zab() {
    }

    @Override // com.google.android.gms.tasks.SuccessContinuation
    public final Task then(Object obj) {
        Task forResult;
        Map map = (Map) obj;
        forResult = Tasks.forResult(null);
        return forResult;
    }
}
