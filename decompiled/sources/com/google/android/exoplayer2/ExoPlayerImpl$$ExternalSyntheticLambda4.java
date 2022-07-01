package com.google.android.exoplayer2;

import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.Player;
/* loaded from: classes.dex */
public final /* synthetic */ class ExoPlayerImpl$$ExternalSyntheticLambda4 implements BasePlayer.ListenerInvocation {
    public static final /* synthetic */ ExoPlayerImpl$$ExternalSyntheticLambda4 INSTANCE = new ExoPlayerImpl$$ExternalSyntheticLambda4();

    private /* synthetic */ ExoPlayerImpl$$ExternalSyntheticLambda4() {
    }

    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
    public final void invokeListener(Player.EventListener eventListener) {
        eventListener.onPositionDiscontinuity(1);
    }
}
