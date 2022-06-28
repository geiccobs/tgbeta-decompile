package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.view.Surface;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
/* loaded from: classes3.dex */
public interface VideoRendererEventListener {
    void onDroppedFrames(int i, long j);

    void onRenderedFirstFrame(Surface surface);

    void onVideoDecoderInitialized(String str, long j, long j2);

    void onVideoDisabled(DecoderCounters decoderCounters);

    void onVideoEnabled(DecoderCounters decoderCounters);

    void onVideoInputFormatChanged(Format format);

    void onVideoSizeChanged(int i, int i2, int i3, float f);

    /* renamed from: com.google.android.exoplayer2.video.VideoRendererEventListener$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$onVideoEnabled(VideoRendererEventListener _this, DecoderCounters counters) {
        }

        public static void $default$onVideoDecoderInitialized(VideoRendererEventListener _this, String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        }

        public static void $default$onVideoInputFormatChanged(VideoRendererEventListener _this, Format format) {
        }

        public static void $default$onDroppedFrames(VideoRendererEventListener _this, int count, long elapsedMs) {
        }

        public static void $default$onVideoSizeChanged(VideoRendererEventListener _this, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        public static void $default$onRenderedFirstFrame(VideoRendererEventListener _this, Surface surface) {
        }

        public static void $default$onVideoDisabled(VideoRendererEventListener _this, DecoderCounters counters) {
        }
    }

    /* loaded from: classes3.dex */
    public static final class EventDispatcher {
        private final Handler handler;
        private final VideoRendererEventListener listener;

        public EventDispatcher(Handler handler, VideoRendererEventListener listener) {
            this.handler = listener != null ? (Handler) Assertions.checkNotNull(handler) : null;
            this.listener = listener;
        }

        public void enabled(final DecoderCounters decoderCounters) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m82x14ecf85(decoderCounters);
                    }
                });
            }
        }

        /* renamed from: lambda$enabled$0$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m82x14ecf85(DecoderCounters decoderCounters) {
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onVideoEnabled(decoderCounters);
        }

        public void decoderInitialized(final String decoderName, final long initializedTimestampMs, final long initializationDurationMs) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m79xe61837fb(decoderName, initializedTimestampMs, initializationDurationMs);
                    }
                });
            }
        }

        /* renamed from: lambda$decoderInitialized$1$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m79xe61837fb(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onVideoDecoderInitialized(decoderName, initializedTimestampMs, initializationDurationMs);
        }

        public void inputFormatChanged(final Format format) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m83xbe305117(format);
                    }
                });
            }
        }

        /* renamed from: lambda$inputFormatChanged$2$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m83xbe305117(Format format) {
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onVideoInputFormatChanged(format);
        }

        public void droppedFrames(final int droppedFrameCount, final long elapsedMs) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m81xb0fc5cbd(droppedFrameCount, elapsedMs);
                    }
                });
            }
        }

        /* renamed from: lambda$droppedFrames$3$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m81xb0fc5cbd(int droppedFrameCount, long elapsedMs) {
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onDroppedFrames(droppedFrameCount, elapsedMs);
        }

        public void videoSizeChanged(final int width, final int height, final int unappliedRotationDegrees, final float pixelWidthHeightRatio) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m85x97e50750(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
                    }
                });
            }
        }

        /* renamed from: lambda$videoSizeChanged$4$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m85x97e50750(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        }

        public void renderedFirstFrame(final Surface surface) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m84x2ea70875(surface);
                    }
                });
            }
        }

        /* renamed from: lambda$renderedFirstFrame$5$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m84x2ea70875(Surface surface) {
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onRenderedFirstFrame(surface);
        }

        public void disabled(final DecoderCounters counters) {
            counters.ensureUpdated();
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.google.android.exoplayer2.video.VideoRendererEventListener$EventDispatcher$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.m80x5ace740a(counters);
                    }
                });
            }
        }

        /* renamed from: lambda$disabled$6$com-google-android-exoplayer2-video-VideoRendererEventListener$EventDispatcher */
        public /* synthetic */ void m80x5ace740a(DecoderCounters counters) {
            counters.ensureUpdated();
            ((VideoRendererEventListener) Util.castNonNull(this.listener)).onVideoDisabled(counters);
        }
    }
}
