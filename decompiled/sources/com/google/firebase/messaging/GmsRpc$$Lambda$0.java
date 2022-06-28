package com.google.firebase.messaging;

import java.util.concurrent.Executor;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class GmsRpc$$Lambda$0 implements Executor {
    static final Executor $instance = new GmsRpc$$Lambda$0();

    private GmsRpc$$Lambda$0() {
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
