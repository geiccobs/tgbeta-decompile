package com.google.android.exoplayer2.video.spherical;

import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public class CameraMotionRenderer extends BaseRenderer {
    private static final int SAMPLE_WINDOW_DURATION_US = 100000;
    private long lastTimestampUs;
    private CameraMotionListener listener;
    private long offsetUs;
    private final DecoderInputBuffer buffer = new DecoderInputBuffer(1);
    private final ParsableByteArray scratch = new ParsableByteArray();

    public CameraMotionRenderer() {
        super(5);
    }

    @Override // com.google.android.exoplayer2.RendererCapabilities
    public int supportsFormat(Format format) {
        if (MimeTypes.APPLICATION_CAMERA_MOTION.equals(format.sampleMimeType)) {
            return RendererCapabilities.CC.create(4);
        }
        return RendererCapabilities.CC.create(0);
    }

    @Override // com.google.android.exoplayer2.BaseRenderer, com.google.android.exoplayer2.PlayerMessage.Target
    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        if (messageType == 7) {
            this.listener = (CameraMotionListener) message;
        } else {
            super.handleMessage(messageType, message);
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    public void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        this.offsetUs = offsetUs;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        resetListener();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onDisabled() {
        resetListener();
    }

    @Override // com.google.android.exoplayer2.Renderer
    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        float[] rotation;
        while (!hasReadStreamToEnd() && this.lastTimestampUs < 100000 + positionUs) {
            this.buffer.clear();
            FormatHolder formatHolder = getFormatHolder();
            int result = readSource(formatHolder, this.buffer, false);
            if (result != -4 || this.buffer.isEndOfStream()) {
                return;
            }
            this.buffer.flip();
            this.lastTimestampUs = this.buffer.timeUs;
            if (this.listener != null && (rotation = parseMetadata((ByteBuffer) Util.castNonNull(this.buffer.data))) != null) {
                ((CameraMotionListener) Util.castNonNull(this.listener)).onCameraMotion(this.lastTimestampUs - this.offsetUs, rotation);
            }
        }
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isEnded() {
        return hasReadStreamToEnd();
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        return true;
    }

    private float[] parseMetadata(ByteBuffer data) {
        if (data.remaining() != 16) {
            return null;
        }
        this.scratch.reset(data.array(), data.limit());
        this.scratch.setPosition(data.arrayOffset() + 4);
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = Float.intBitsToFloat(this.scratch.readLittleEndianInt());
        }
        return result;
    }

    private void resetListener() {
        this.lastTimestampUs = 0L;
        CameraMotionListener cameraMotionListener = this.listener;
        if (cameraMotionListener != null) {
            cameraMotionListener.onCameraMotionReset();
        }
    }
}
