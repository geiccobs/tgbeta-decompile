package org.webrtc;

import android.graphics.Matrix;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;
import org.webrtc.GlGenericDrawer;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoFrame;
/* loaded from: classes5.dex */
public class YuvConverter {
    private static final String FRAGMENT_SHADER = "uniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      sample(tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      sample(tc + 1.5 * xUnit).rgb);\n}\n";
    private final GlGenericDrawer drawer;
    private final GlTextureFrameBuffer i420TextureFrameBuffer;
    private final ShaderCallbacks shaderCallbacks;
    private final ThreadUtils.ThreadChecker threadChecker;
    private final VideoFrameDrawer videoFrameDrawer;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class ShaderCallbacks implements GlGenericDrawer.ShaderCallbacks {
        private float[] coeffs;
        private int coeffsLoc;
        private float stepSize;
        private int xUnitLoc;
        private static final float[] yCoeffs = {0.256788f, 0.504129f, 0.0979059f, 0.0627451f};
        private static final float[] uCoeffs = {-0.148223f, -0.290993f, 0.439216f, 0.501961f};
        private static final float[] vCoeffs = {0.439216f, -0.367788f, -0.0714274f, 0.501961f};

        private ShaderCallbacks() {
        }

        public void setPlaneY() {
            this.coeffs = yCoeffs;
            this.stepSize = 1.0f;
        }

        public void setPlaneU() {
            this.coeffs = uCoeffs;
            this.stepSize = 2.0f;
        }

        public void setPlaneV() {
            this.coeffs = vCoeffs;
            this.stepSize = 2.0f;
        }

        @Override // org.webrtc.GlGenericDrawer.ShaderCallbacks
        public void onNewShader(GlShader shader) {
            this.xUnitLoc = shader.getUniformLocation("xUnit");
            this.coeffsLoc = shader.getUniformLocation("coeffs");
        }

        @Override // org.webrtc.GlGenericDrawer.ShaderCallbacks
        public void onPrepareShader(GlShader shader, float[] texMatrix, int frameWidth, int frameHeight, int viewportWidth, int viewportHeight) {
            GLES20.glUniform4fv(this.coeffsLoc, 1, this.coeffs, 0);
            int i = this.xUnitLoc;
            float f = this.stepSize;
            GLES20.glUniform2f(i, (texMatrix[0] * f) / frameWidth, (f * texMatrix[1]) / frameWidth);
        }
    }

    public YuvConverter() {
        this(new VideoFrameDrawer());
    }

    public YuvConverter(VideoFrameDrawer videoFrameDrawer) {
        ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();
        this.threadChecker = threadChecker;
        this.i420TextureFrameBuffer = new GlTextureFrameBuffer(6408);
        ShaderCallbacks shaderCallbacks = new ShaderCallbacks();
        this.shaderCallbacks = shaderCallbacks;
        this.drawer = new GlGenericDrawer(FRAGMENT_SHADER, shaderCallbacks);
        this.videoFrameDrawer = videoFrameDrawer;
        threadChecker.detachThread();
    }

    public VideoFrame.I420Buffer convert(VideoFrame.TextureBuffer inputTextureBuffer) {
        ByteBuffer i420ByteBuffer;
        int i;
        Exception e;
        this.threadChecker.checkIsOnValidThread();
        VideoFrame.TextureBuffer preparedBuffer = (VideoFrame.TextureBuffer) this.videoFrameDrawer.prepareBufferForViewportSize(inputTextureBuffer, inputTextureBuffer.getWidth(), inputTextureBuffer.getHeight());
        int frameWidth = preparedBuffer.getWidth();
        int frameHeight = preparedBuffer.getHeight();
        int stride = ((frameWidth + 7) / 8) * 8;
        int uvHeight = (frameHeight + 1) / 2;
        int totalHeight = frameHeight + uvHeight;
        ByteBuffer i420ByteBuffer2 = JniCommon.nativeAllocateByteBuffer(stride * totalHeight);
        int viewportWidth = stride / 4;
        Matrix renderMatrix = new Matrix();
        renderMatrix.preTranslate(0.5f, 0.5f);
        renderMatrix.preScale(1.0f, -1.0f);
        renderMatrix.preTranslate(-0.5f, -0.5f);
        try {
            this.i420TextureFrameBuffer.setSize(viewportWidth, totalHeight);
            GLES20.glBindFramebuffer(36160, this.i420TextureFrameBuffer.getFrameBufferId());
            GlUtil.checkNoGLES2Error("glBindFramebuffer");
            this.shaderCallbacks.setPlaneY();
            i420ByteBuffer = i420ByteBuffer2;
            try {
                VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, frameWidth, frameHeight, 0, 0, viewportWidth, frameHeight, false);
                this.shaderCallbacks.setPlaneU();
                VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, frameWidth, frameHeight, 0, frameHeight, viewportWidth / 2, uvHeight, false);
                this.shaderCallbacks.setPlaneV();
                VideoFrameDrawer.drawTexture(this.drawer, preparedBuffer, renderMatrix, frameWidth, frameHeight, frameWidth, frameHeight, viewportWidth / 2, frameHeight, viewportWidth / 2, uvHeight, false);
                GLES20.glReadPixels(0, 0, this.i420TextureFrameBuffer.getWidth(), this.i420TextureFrameBuffer.getHeight(), 6408, 5121, i420ByteBuffer);
                GlUtil.checkNoGLES2Error("YuvConverter.convert");
                i = 0;
            } catch (Exception e2) {
                e = e2;
                i = 0;
            }
        } catch (Exception e3) {
            e = e3;
            i420ByteBuffer = i420ByteBuffer2;
            i = 0;
        }
        try {
            GLES20.glBindFramebuffer(36160, 0);
        } catch (Exception e4) {
            e = e4;
            FileLog.e(e);
            int uPos = (stride * frameHeight) + 0;
            int vPos = uPos + (stride / 2);
            final ByteBuffer i420ByteBuffer3 = i420ByteBuffer;
            i420ByteBuffer3.position(i);
            i420ByteBuffer3.limit((stride * frameHeight) + i);
            ByteBuffer dataY = i420ByteBuffer3.slice();
            i420ByteBuffer3.position(uPos);
            int uvSize = ((uvHeight - 1) * stride) + (stride / 2);
            i420ByteBuffer3.limit(uPos + uvSize);
            ByteBuffer dataU = i420ByteBuffer3.slice();
            i420ByteBuffer3.position(vPos);
            i420ByteBuffer3.limit(vPos + uvSize);
            ByteBuffer dataV = i420ByteBuffer3.slice();
            preparedBuffer.release();
            return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY, stride, dataU, stride, dataV, stride, new Runnable() { // from class: org.webrtc.YuvConverter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    JniCommon.nativeFreeByteBuffer(i420ByteBuffer3);
                }
            });
        }
        int uPos2 = (stride * frameHeight) + 0;
        int vPos2 = uPos2 + (stride / 2);
        final ByteBuffer i420ByteBuffer32 = i420ByteBuffer;
        i420ByteBuffer32.position(i);
        i420ByteBuffer32.limit((stride * frameHeight) + i);
        ByteBuffer dataY2 = i420ByteBuffer32.slice();
        i420ByteBuffer32.position(uPos2);
        int uvSize2 = ((uvHeight - 1) * stride) + (stride / 2);
        i420ByteBuffer32.limit(uPos2 + uvSize2);
        ByteBuffer dataU2 = i420ByteBuffer32.slice();
        i420ByteBuffer32.position(vPos2);
        i420ByteBuffer32.limit(vPos2 + uvSize2);
        ByteBuffer dataV2 = i420ByteBuffer32.slice();
        preparedBuffer.release();
        return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY2, stride, dataU2, stride, dataV2, stride, new Runnable() { // from class: org.webrtc.YuvConverter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                JniCommon.nativeFreeByteBuffer(i420ByteBuffer32);
            }
        });
    }

    public void release() {
        this.threadChecker.checkIsOnValidThread();
        this.drawer.release();
        this.i420TextureFrameBuffer.release();
        this.videoFrameDrawer.release();
        this.threadChecker.detachThread();
    }
}
