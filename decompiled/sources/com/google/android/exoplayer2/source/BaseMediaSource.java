package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes3.dex */
public abstract class BaseMediaSource implements MediaSource {
    private Looper looper;
    private Timeline timeline;
    private final ArrayList<MediaSource.MediaSourceCaller> mediaSourceCallers = new ArrayList<>(1);
    private final HashSet<MediaSource.MediaSourceCaller> enabledMediaSourceCallers = new HashSet<>(1);
    private final MediaSourceEventListener.EventDispatcher eventDispatcher = new MediaSourceEventListener.EventDispatcher();

    @Override // com.google.android.exoplayer2.source.MediaSource
    public /* synthetic */ Object getTag() {
        return MediaSource.CC.$default$getTag(this);
    }

    protected abstract void prepareSourceInternal(TransferListener transferListener);

    protected abstract void releaseSourceInternal();

    protected void enableInternal() {
    }

    protected void disableInternal() {
    }

    public final void refreshSourceInfo(Timeline timeline) {
        this.timeline = timeline;
        Iterator<MediaSource.MediaSourceCaller> it = this.mediaSourceCallers.iterator();
        while (it.hasNext()) {
            MediaSource.MediaSourceCaller caller = it.next();
            caller.onSourceInfoRefreshed(this, timeline);
        }
    }

    public final MediaSourceEventListener.EventDispatcher createEventDispatcher(MediaSource.MediaPeriodId mediaPeriodId) {
        return this.eventDispatcher.withParameters(0, mediaPeriodId, 0L);
    }

    public final MediaSourceEventListener.EventDispatcher createEventDispatcher(MediaSource.MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
        Assertions.checkArgument(mediaPeriodId != null);
        return this.eventDispatcher.withParameters(0, mediaPeriodId, mediaTimeOffsetMs);
    }

    public final MediaSourceEventListener.EventDispatcher createEventDispatcher(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
        return this.eventDispatcher.withParameters(windowIndex, mediaPeriodId, mediaTimeOffsetMs);
    }

    public final boolean isEnabled() {
        return !this.enabledMediaSourceCallers.isEmpty();
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public final void addEventListener(Handler handler, MediaSourceEventListener eventListener) {
        this.eventDispatcher.addEventListener(handler, eventListener);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public final void removeEventListener(MediaSourceEventListener eventListener) {
        this.eventDispatcher.removeEventListener(eventListener);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public final void prepareSource(MediaSource.MediaSourceCaller caller, TransferListener mediaTransferListener) {
        Looper looper = Looper.myLooper();
        Looper looper2 = this.looper;
        Assertions.checkArgument(looper2 == null || looper2 == looper);
        Timeline timeline = this.timeline;
        this.mediaSourceCallers.add(caller);
        if (this.looper == null) {
            this.looper = looper;
            this.enabledMediaSourceCallers.add(caller);
            prepareSourceInternal(mediaTransferListener);
        } else if (timeline != null) {
            enable(caller);
            caller.onSourceInfoRefreshed(this, timeline);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public final void enable(MediaSource.MediaSourceCaller caller) {
        Assertions.checkNotNull(this.looper);
        boolean wasDisabled = this.enabledMediaSourceCallers.isEmpty();
        this.enabledMediaSourceCallers.add(caller);
        if (wasDisabled) {
            enableInternal();
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public final void disable(MediaSource.MediaSourceCaller caller) {
        boolean wasEnabled = !this.enabledMediaSourceCallers.isEmpty();
        this.enabledMediaSourceCallers.remove(caller);
        if (wasEnabled && this.enabledMediaSourceCallers.isEmpty()) {
            disableInternal();
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public final void releaseSource(MediaSource.MediaSourceCaller caller) {
        this.mediaSourceCallers.remove(caller);
        if (this.mediaSourceCallers.isEmpty()) {
            this.looper = null;
            this.timeline = null;
            this.enabledMediaSourceCallers.clear();
            releaseSourceInternal();
            return;
        }
        disable(caller);
    }
}
