package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.EventDispatcher;
/* loaded from: classes3.dex */
public final /* synthetic */ class DefaultDrmSession$$ExternalSyntheticLambda2 implements EventDispatcher.Event {
    public static final /* synthetic */ DefaultDrmSession$$ExternalSyntheticLambda2 INSTANCE = new DefaultDrmSession$$ExternalSyntheticLambda2();

    private /* synthetic */ DefaultDrmSession$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmKeysRestored();
    }
}
