package com.google.android.gms.common.api.internal;

import android.os.Handler;
import java.util.concurrent.Executor;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zabc implements Executor {
    private final Handler zaa;

    private zabc(Handler handler) {
        this.zaa = handler;
    }

    public static Executor zaa(Handler handler) {
        return new zabc(handler);
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.zaa.post(runnable);
    }
}
