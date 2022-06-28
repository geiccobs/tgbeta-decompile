package com.google.android.exoplayer2;
/* loaded from: classes3.dex */
public class DefaultControlDispatcher implements ControlDispatcher {
    @Override // com.google.android.exoplayer2.ControlDispatcher
    public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
        player.setPlayWhenReady(playWhenReady);
        return true;
    }

    @Override // com.google.android.exoplayer2.ControlDispatcher
    public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
        player.seekTo(windowIndex, positionMs);
        return true;
    }

    @Override // com.google.android.exoplayer2.ControlDispatcher
    public boolean dispatchSetRepeatMode(Player player, int repeatMode) {
        player.setRepeatMode(repeatMode);
        return true;
    }

    @Override // com.google.android.exoplayer2.ControlDispatcher
    public boolean dispatchSetShuffleModeEnabled(Player player, boolean shuffleModeEnabled) {
        player.setShuffleModeEnabled(shuffleModeEnabled);
        return true;
    }

    @Override // com.google.android.exoplayer2.ControlDispatcher
    public boolean dispatchStop(Player player, boolean reset) {
        player.stop(reset);
        return true;
    }
}
