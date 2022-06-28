package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes3.dex */
public interface MediaSourceEventListener {
    void onDownstreamFormatChanged(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData);

    void onLoadCanceled(int i, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData);

    void onLoadCompleted(int i, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData);

    void onLoadError(int i, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException iOException, boolean z);

    void onLoadStarted(int i, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData);

    void onMediaPeriodCreated(int i, MediaSource.MediaPeriodId mediaPeriodId);

    void onMediaPeriodReleased(int i, MediaSource.MediaPeriodId mediaPeriodId);

    void onReadingStarted(int i, MediaSource.MediaPeriodId mediaPeriodId);

    void onUpstreamDiscarded(int i, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData);

    /* loaded from: classes3.dex */
    public static final class LoadEventInfo {
        public final long bytesLoaded;
        public final DataSpec dataSpec;
        public final long elapsedRealtimeMs;
        public final long loadDurationMs;
        public final Map<String, List<String>> responseHeaders;
        public final Uri uri;

        public LoadEventInfo(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            this.dataSpec = dataSpec;
            this.uri = uri;
            this.responseHeaders = responseHeaders;
            this.elapsedRealtimeMs = elapsedRealtimeMs;
            this.loadDurationMs = loadDurationMs;
            this.bytesLoaded = bytesLoaded;
        }
    }

    /* loaded from: classes3.dex */
    public static final class MediaLoadData {
        public final int dataType;
        public final long mediaEndTimeMs;
        public final long mediaStartTimeMs;
        public final Format trackFormat;
        public final Object trackSelectionData;
        public final int trackSelectionReason;
        public final int trackType;

        public MediaLoadData(int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs) {
            this.dataType = dataType;
            this.trackType = trackType;
            this.trackFormat = trackFormat;
            this.trackSelectionReason = trackSelectionReason;
            this.trackSelectionData = trackSelectionData;
            this.mediaStartTimeMs = mediaStartTimeMs;
            this.mediaEndTimeMs = mediaEndTimeMs;
        }
    }

    /* renamed from: com.google.android.exoplayer2.source.MediaSourceEventListener$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$onMediaPeriodCreated(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        }

        public static void $default$onMediaPeriodReleased(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        }

        public static void $default$onLoadStarted(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadCompleted(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadCanceled(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        }

        public static void $default$onLoadError(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        }

        public static void $default$onReadingStarted(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        }

        public static void $default$onUpstreamDiscarded(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        }

        public static void $default$onDownstreamFormatChanged(MediaSourceEventListener _this, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        }
    }

    /* loaded from: classes3.dex */
    public static final class EventDispatcher {
        private final CopyOnWriteArrayList<ListenerAndHandler> listenerAndHandlers;
        public final MediaSource.MediaPeriodId mediaPeriodId;
        private final long mediaTimeOffsetMs;
        public final int windowIndex;

        public EventDispatcher() {
            this(new CopyOnWriteArrayList(), 0, null, 0L);
        }

        private EventDispatcher(CopyOnWriteArrayList<ListenerAndHandler> listenerAndHandlers, int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
            this.listenerAndHandlers = listenerAndHandlers;
            this.windowIndex = windowIndex;
            this.mediaPeriodId = mediaPeriodId;
            this.mediaTimeOffsetMs = mediaTimeOffsetMs;
        }

        public EventDispatcher withParameters(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
            return new EventDispatcher(this.listenerAndHandlers, windowIndex, mediaPeriodId, mediaTimeOffsetMs);
        }

        public void addEventListener(Handler handler, MediaSourceEventListener eventListener) {
            Assertions.checkArgument((handler == null || eventListener == null) ? false : true);
            this.listenerAndHandlers.add(new ListenerAndHandler(handler, eventListener));
        }

        public void removeEventListener(MediaSourceEventListener eventListener) {
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                if (listenerAndHandler.listener == eventListener) {
                    this.listenerAndHandlers.remove(listenerAndHandler);
                }
            }
        }

        public void mediaPeriodCreated() {
            final MediaSource.MediaPeriodId mediaPeriodId = (MediaSource.MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m63xd1be9f74(listener, mediaPeriodId);
                    }
                });
            }
        }

        /* renamed from: lambda$mediaPeriodCreated$0$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m63xd1be9f74(MediaSourceEventListener listener, MediaSource.MediaPeriodId mediaPeriodId) {
            listener.onMediaPeriodCreated(this.windowIndex, mediaPeriodId);
        }

        public void mediaPeriodReleased() {
            final MediaSource.MediaPeriodId mediaPeriodId = (MediaSource.MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m64xbd0c861a(listener, mediaPeriodId);
                    }
                });
            }
        }

        /* renamed from: lambda$mediaPeriodReleased$1$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m64xbd0c861a(MediaSourceEventListener listener, MediaSource.MediaPeriodId mediaPeriodId) {
            listener.onMediaPeriodReleased(this.windowIndex, mediaPeriodId);
        }

        public void loadStarted(DataSpec dataSpec, int dataType, long elapsedRealtimeMs) {
            loadStarted(dataSpec, dataType, -1, null, 0, null, C.TIME_UNSET, C.TIME_UNSET, elapsedRealtimeMs);
        }

        public void loadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs) {
            loadStarted(new LoadEventInfo(dataSpec, dataSpec.uri, Collections.emptyMap(), elapsedRealtimeMs, 0L, 0L), new MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
        }

        public void loadStarted(final LoadEventInfo loadEventInfo, final MediaLoadData mediaLoadData) {
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m62xb7ad06f4(listener, loadEventInfo, mediaLoadData);
                    }
                });
            }
        }

        /* renamed from: lambda$loadStarted$2$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m62xb7ad06f4(MediaSourceEventListener listener, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            listener.onLoadStarted(this.windowIndex, this.mediaPeriodId, loadEventInfo, mediaLoadData);
        }

        public void loadCompleted(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            loadCompleted(dataSpec, uri, responseHeaders, dataType, -1, null, 0, null, C.TIME_UNSET, C.TIME_UNSET, elapsedRealtimeMs, loadDurationMs, bytesLoaded);
        }

        public void loadCompleted(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            loadCompleted(new LoadEventInfo(dataSpec, uri, responseHeaders, elapsedRealtimeMs, loadDurationMs, bytesLoaded), new MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
        }

        public void loadCompleted(final LoadEventInfo loadEventInfo, final MediaLoadData mediaLoadData) {
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m60x39a9b7bf(listener, loadEventInfo, mediaLoadData);
                    }
                });
            }
        }

        /* renamed from: lambda$loadCompleted$3$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m60x39a9b7bf(MediaSourceEventListener listener, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            listener.onLoadCompleted(this.windowIndex, this.mediaPeriodId, loadEventInfo, mediaLoadData);
        }

        public void loadCanceled(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            loadCanceled(dataSpec, uri, responseHeaders, dataType, -1, null, 0, null, C.TIME_UNSET, C.TIME_UNSET, elapsedRealtimeMs, loadDurationMs, bytesLoaded);
        }

        public void loadCanceled(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            loadCanceled(new LoadEventInfo(dataSpec, uri, responseHeaders, elapsedRealtimeMs, loadDurationMs, bytesLoaded), new MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
        }

        public void loadCanceled(final LoadEventInfo loadEventInfo, final MediaLoadData mediaLoadData) {
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m59xf522b174(listener, loadEventInfo, mediaLoadData);
                    }
                });
            }
        }

        /* renamed from: lambda$loadCanceled$4$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m59xf522b174(MediaSourceEventListener listener, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            listener.onLoadCanceled(this.windowIndex, this.mediaPeriodId, loadEventInfo, mediaLoadData);
        }

        public void loadError(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            loadError(dataSpec, uri, responseHeaders, dataType, -1, null, 0, null, C.TIME_UNSET, C.TIME_UNSET, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, wasCanceled);
        }

        public void loadError(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            loadError(new LoadEventInfo(dataSpec, uri, responseHeaders, elapsedRealtimeMs, loadDurationMs, bytesLoaded), new MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)), error, wasCanceled);
        }

        public void loadError(final LoadEventInfo loadEventInfo, final MediaLoadData mediaLoadData, final IOException error, final boolean wasCanceled) {
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m61x58f92c7e(listener, loadEventInfo, mediaLoadData, error, wasCanceled);
                    }
                });
            }
        }

        /* renamed from: lambda$loadError$5$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m61x58f92c7e(MediaSourceEventListener listener, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            listener.onLoadError(this.windowIndex, this.mediaPeriodId, loadEventInfo, mediaLoadData, error, wasCanceled);
        }

        public void readingStarted() {
            final MediaSource.MediaPeriodId mediaPeriodId = (MediaSource.MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m65x22c40b6c(listener, mediaPeriodId);
                    }
                });
            }
        }

        /* renamed from: lambda$readingStarted$6$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m65x22c40b6c(MediaSourceEventListener listener, MediaSource.MediaPeriodId mediaPeriodId) {
            listener.onReadingStarted(this.windowIndex, mediaPeriodId);
        }

        public void upstreamDiscarded(int trackType, long mediaStartTimeUs, long mediaEndTimeUs) {
            upstreamDiscarded(new MediaLoadData(1, trackType, null, 3, null, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
        }

        public void upstreamDiscarded(final MediaLoadData mediaLoadData) {
            final MediaSource.MediaPeriodId mediaPeriodId = (MediaSource.MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m66x52e40f60(listener, mediaPeriodId, mediaLoadData);
                    }
                });
            }
        }

        /* renamed from: lambda$upstreamDiscarded$7$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m66x52e40f60(MediaSourceEventListener listener, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
            listener.onUpstreamDiscarded(this.windowIndex, mediaPeriodId, mediaLoadData);
        }

        public void downstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeUs) {
            downstreamFormatChanged(new MediaLoadData(1, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaTimeUs), C.TIME_UNSET));
        }

        public void downstreamFormatChanged(final MediaLoadData mediaLoadData) {
            Iterator<ListenerAndHandler> it = this.listenerAndHandlers.iterator();
            while (it.hasNext()) {
                ListenerAndHandler listenerAndHandler = it.next();
                final MediaSourceEventListener listener = listenerAndHandler.listener;
                postOrRun(listenerAndHandler.handler, new Runnable() { // from class: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSourceEventListener.EventDispatcher.this.m58x1857167a(listener, mediaLoadData);
                    }
                });
            }
        }

        /* renamed from: lambda$downstreamFormatChanged$8$com-google-android-exoplayer2-source-MediaSourceEventListener$EventDispatcher */
        public /* synthetic */ void m58x1857167a(MediaSourceEventListener listener, MediaLoadData mediaLoadData) {
            listener.onDownstreamFormatChanged(this.windowIndex, this.mediaPeriodId, mediaLoadData);
        }

        private long adjustMediaTime(long mediaTimeUs) {
            long mediaTimeMs = C.usToMs(mediaTimeUs);
            return mediaTimeMs == C.TIME_UNSET ? C.TIME_UNSET : this.mediaTimeOffsetMs + mediaTimeMs;
        }

        private void postOrRun(Handler handler, Runnable runnable) {
            if (handler.getLooper() == Looper.myLooper()) {
                runnable.run();
            } else {
                handler.post(runnable);
            }
        }

        /* loaded from: classes3.dex */
        public static final class ListenerAndHandler {
            public final Handler handler;
            public final MediaSourceEventListener listener;

            public ListenerAndHandler(Handler handler, MediaSourceEventListener listener) {
                this.handler = handler;
                this.listener = listener;
            }
        }
    }
}
