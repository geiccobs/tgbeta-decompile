package com.google.android.exoplayer2;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
/* loaded from: classes.dex */
public abstract class BasePlayer implements Player {
    protected final Timeline.Window window = new Timeline.Window();

    /* loaded from: classes.dex */
    public interface ListenerInvocation {
        void invokeListener(Player.EventListener eventListener);
    }

    public final boolean isPlaying() {
        return getPlaybackState() == 3 && getPlayWhenReady() && getPlaybackSuppressionReason() == 0;
    }

    public final void seekTo(long j) {
        seekTo(getCurrentWindowIndex(), j);
    }

    public final long getContentDuration() {
        Timeline currentTimeline = getCurrentTimeline();
        if (currentTimeline.isEmpty()) {
            return -9223372036854775807L;
        }
        return currentTimeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
    }

    /* loaded from: classes.dex */
    protected static final class ListenerHolder {
        public final Player.EventListener listener;
        private boolean released;

        public ListenerHolder(Player.EventListener eventListener) {
            this.listener = eventListener;
        }

        public void invoke(ListenerInvocation listenerInvocation) {
            if (!this.released) {
                listenerInvocation.invokeListener(this.listener);
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && ListenerHolder.class == obj.getClass()) {
                return this.listener.equals(((ListenerHolder) obj).listener);
            }
            return false;
        }

        public int hashCode() {
            return this.listener.hashCode();
        }
    }
}
