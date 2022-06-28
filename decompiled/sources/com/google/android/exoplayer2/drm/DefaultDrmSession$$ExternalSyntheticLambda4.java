package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.EventDispatcher;
/* loaded from: classes3.dex */
public final /* synthetic */ class DefaultDrmSession$$ExternalSyntheticLambda4 implements EventDispatcher.Event {
    public static final /* synthetic */ DefaultDrmSession$$ExternalSyntheticLambda4 INSTANCE = new DefaultDrmSession$$ExternalSyntheticLambda4();

    private /* synthetic */ DefaultDrmSession$$ExternalSyntheticLambda4() {
    }

    @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmSessionReleased();
    }
}
