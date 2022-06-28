package com.google.android.exoplayer2.video;

import android.graphics.SurfaceTexture;
/* loaded from: classes3.dex */
public interface VideoListener {
    void onRenderedFirstFrame();

    boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

    void onSurfaceSizeChanged(int i, int i2);

    void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

    void onVideoSizeChanged(int i, int i2, int i3, float f);

    /* renamed from: com.google.android.exoplayer2.video.VideoListener$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$onVideoSizeChanged(VideoListener _this, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        public static void $default$onSurfaceSizeChanged(VideoListener _this, int width, int height) {
        }

        public static void $default$onRenderedFirstFrame(VideoListener _this) {
        }
    }
}
