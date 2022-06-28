package org.webrtc;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;
/* loaded from: classes5.dex */
class MediaCodecWrapperFactoryImpl implements MediaCodecWrapperFactory {

    /* loaded from: classes5.dex */
    private static class MediaCodecWrapperImpl implements MediaCodecWrapper {
        private final MediaCodec mediaCodec;

        public MediaCodecWrapperImpl(MediaCodec mediaCodec) {
            this.mediaCodec = mediaCodec;
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void configure(MediaFormat format, Surface surface, MediaCrypto crypto, int flags) {
            this.mediaCodec.configure(format, surface, crypto, flags);
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void start() {
            this.mediaCodec.start();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void flush() {
            this.mediaCodec.flush();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void stop() {
            this.mediaCodec.stop();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void release() {
            this.mediaCodec.release();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public int dequeueInputBuffer(long timeoutUs) {
            return this.mediaCodec.dequeueInputBuffer(timeoutUs);
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
            this.mediaCodec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
        }

        @Override // org.webrtc.MediaCodecWrapper
        public int dequeueOutputBuffer(MediaCodec.BufferInfo info, long timeoutUs) {
            return this.mediaCodec.dequeueOutputBuffer(info, timeoutUs);
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void releaseOutputBuffer(int index, boolean render) {
            this.mediaCodec.releaseOutputBuffer(index, render);
        }

        @Override // org.webrtc.MediaCodecWrapper
        public MediaFormat getOutputFormat() {
            return this.mediaCodec.getOutputFormat();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public ByteBuffer[] getInputBuffers() {
            return this.mediaCodec.getInputBuffers();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public ByteBuffer[] getOutputBuffers() {
            return this.mediaCodec.getOutputBuffers();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public Surface createInputSurface() {
            return this.mediaCodec.createInputSurface();
        }

        @Override // org.webrtc.MediaCodecWrapper
        public void setParameters(Bundle params) {
            this.mediaCodec.setParameters(params);
        }
    }

    @Override // org.webrtc.MediaCodecWrapperFactory
    public MediaCodecWrapper createByCodecName(String name) throws IOException {
        return new MediaCodecWrapperImpl(MediaCodec.createByCodecName(name));
    }
}
