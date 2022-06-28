package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import java.io.IOException;
@Deprecated
/* loaded from: classes3.dex */
public abstract class DefaultMediaSourceEventListener implements MediaSourceEventListener {
    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onDownstreamFormatChanged(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        MediaSourceEventListener.CC.$default$onDownstreamFormatChanged(this, i, mediaPeriodId, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onLoadCanceled(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        MediaSourceEventListener.CC.$default$onLoadCanceled(this, i, mediaPeriodId, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onLoadCompleted(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        MediaSourceEventListener.CC.$default$onLoadCompleted(this, i, mediaPeriodId, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onLoadError(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
        MediaSourceEventListener.CC.$default$onLoadError(this, i, mediaPeriodId, loadEventInfo, mediaLoadData, iOException, z);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onLoadStarted(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        MediaSourceEventListener.CC.$default$onLoadStarted(this, i, mediaPeriodId, loadEventInfo, mediaLoadData);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onMediaPeriodCreated(int i, MediaSource.MediaPeriodId mediaPeriodId) {
        MediaSourceEventListener.CC.$default$onMediaPeriodCreated(this, i, mediaPeriodId);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onMediaPeriodReleased(int i, MediaSource.MediaPeriodId mediaPeriodId) {
        MediaSourceEventListener.CC.$default$onMediaPeriodReleased(this, i, mediaPeriodId);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onReadingStarted(int i, MediaSource.MediaPeriodId mediaPeriodId) {
        MediaSourceEventListener.CC.$default$onReadingStarted(this, i, mediaPeriodId);
    }

    @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
    public /* synthetic */ void onUpstreamDiscarded(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        MediaSourceEventListener.CC.$default$onUpstreamDiscarded(this, i, mediaPeriodId, mediaLoadData);
    }
}
