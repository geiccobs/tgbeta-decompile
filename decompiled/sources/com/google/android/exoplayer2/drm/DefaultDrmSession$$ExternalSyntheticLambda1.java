package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.EventDispatcher;
/* loaded from: classes3.dex */
public final /* synthetic */ class DefaultDrmSession$$ExternalSyntheticLambda1 implements EventDispatcher.Event {
    public static final /* synthetic */ DefaultDrmSession$$ExternalSyntheticLambda1 INSTANCE = new DefaultDrmSession$$ExternalSyntheticLambda1();

    private /* synthetic */ DefaultDrmSession$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmKeysLoaded();
    }
}
