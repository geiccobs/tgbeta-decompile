package com.google.firebase.heartbeatinfo;

import java.util.concurrent.ThreadFactory;
/* loaded from: classes.dex */
public final /* synthetic */ class DefaultHeartBeatInfo$$ExternalSyntheticLambda2 implements ThreadFactory {
    public static final /* synthetic */ DefaultHeartBeatInfo$$ExternalSyntheticLambda2 INSTANCE = new DefaultHeartBeatInfo$$ExternalSyntheticLambda2();

    private /* synthetic */ DefaultHeartBeatInfo$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        Thread lambda$static$0;
        lambda$static$0 = DefaultHeartBeatInfo.lambda$static$0(runnable);
        return lambda$static$0;
    }
}
