package com.google.android.exoplayer2;

import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.Player;
/* loaded from: classes.dex */
public final /* synthetic */ class ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda7 implements BasePlayer.ListenerInvocation {
    public static final /* synthetic */ ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda7 INSTANCE = new ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda7();

    private /* synthetic */ ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda7() {
    }

    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
    public final void invokeListener(Player.EventListener eventListener) {
        eventListener.onSeekProcessed();
    }
}
