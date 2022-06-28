package com.google.android.exoplayer2;

import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.Player;
/* loaded from: classes3.dex */
public final /* synthetic */ class ExoPlayerImpl$$ExternalSyntheticLambda5 implements BasePlayer.ListenerInvocation {
    public static final /* synthetic */ ExoPlayerImpl$$ExternalSyntheticLambda5 INSTANCE = new ExoPlayerImpl$$ExternalSyntheticLambda5();

    private /* synthetic */ ExoPlayerImpl$$ExternalSyntheticLambda5() {
    }

    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
    public final void invokeListener(Player.EventListener eventListener) {
        eventListener.onPositionDiscontinuity(1);
    }
}
