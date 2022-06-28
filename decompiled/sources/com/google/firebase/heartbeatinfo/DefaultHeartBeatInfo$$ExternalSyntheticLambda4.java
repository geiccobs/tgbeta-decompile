package com.google.firebase.heartbeatinfo;

import java.util.concurrent.ThreadFactory;
/* loaded from: classes3.dex */
public final /* synthetic */ class DefaultHeartBeatInfo$$ExternalSyntheticLambda4 implements ThreadFactory {
    public static final /* synthetic */ DefaultHeartBeatInfo$$ExternalSyntheticLambda4 INSTANCE = new DefaultHeartBeatInfo$$ExternalSyntheticLambda4();

    private /* synthetic */ DefaultHeartBeatInfo$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        return DefaultHeartBeatInfo.lambda$static$0(runnable);
    }
}
