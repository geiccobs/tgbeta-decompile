package com.google.android.exoplayer2.video;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.GlUtil;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicReference;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
/* loaded from: classes3.dex */
class VideoDecoderRenderer implements GLSurfaceView.Renderer, VideoDecoderOutputBufferRenderer {
    private static final String FRAGMENT_SHADER = "precision mediump float;\nvarying vec2 interp_tc_y;\nvarying vec2 interp_tc_u;\nvarying vec2 interp_tc_v;\nuniform sampler2D y_tex;\nuniform sampler2D u_tex;\nuniform sampler2D v_tex;\nuniform mat3 mColorConversion;\nvoid main() {\n  vec3 yuv;\n  yuv.x = texture2D(y_tex, interp_tc_y).r - 0.0625;\n  yuv.y = texture2D(u_tex, interp_tc_u).r - 0.5;\n  yuv.z = texture2D(v_tex, interp_tc_v).r - 0.5;\n  gl_FragColor = vec4(mColorConversion * yuv, 1.0);\n}\n";
    private static final String VERTEX_SHADER = "varying vec2 interp_tc_y;\nvarying vec2 interp_tc_u;\nvarying vec2 interp_tc_v;\nattribute vec4 in_pos;\nattribute vec2 in_tc_y;\nattribute vec2 in_tc_u;\nattribute vec2 in_tc_v;\nvoid main() {\n  gl_Position = in_pos;\n  interp_tc_y = in_tc_y;\n  interp_tc_u = in_tc_u;\n  interp_tc_v = in_tc_v;\n}\n";
    private int colorMatrixLocation;
    private int program;
    private VideoDecoderOutputBuffer renderedOutputBuffer;
    private final GLSurfaceView surfaceView;
    private static final float[] kColorConversion601 = {1.164f, 1.164f, 1.164f, 0.0f, -0.392f, 2.017f, 1.596f, -0.813f, 0.0f};
    private static final float[] kColorConversion709 = {1.164f, 1.164f, 1.164f, 0.0f, -0.213f, 2.112f, 1.793f, -0.533f, 0.0f};
    private static final float[] kColorConversion2020 = {1.168f, 1.168f, 1.168f, 0.0f, -0.188f, 2.148f, 1.683f, -0.652f, 0.0f};
    private static final String[] TEXTURE_UNIFORMS = {"y_tex", "u_tex", "v_tex"};
    private static final FloatBuffer TEXTURE_VERTICES = GlUtil.createBuffer(new float[]{-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f});
    private final int[] yuvTextures = new int[3];
    private final AtomicReference<VideoDecoderOutputBuffer> pendingOutputBufferReference = new AtomicReference<>();
    private FloatBuffer[] textureCoords = new FloatBuffer[3];
    private int[] texLocations = new int[3];
    private int[] previousWidths = new int[3];
    private int[] previousStrides = new int[3];

    public VideoDecoderRenderer(GLSurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        for (int i = 0; i < 3; i++) {
            int[] iArr = this.previousWidths;
            this.previousStrides[i] = -1;
            iArr[i] = -1;
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        int compileProgram = GlUtil.compileProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        this.program = compileProgram;
        GLES20.glUseProgram(compileProgram);
        int posLocation = GLES20.glGetAttribLocation(this.program, "in_pos");
        GLES20.glEnableVertexAttribArray(posLocation);
        GLES20.glVertexAttribPointer(posLocation, 2, 5126, false, 0, (Buffer) TEXTURE_VERTICES);
        this.texLocations[0] = GLES20.glGetAttribLocation(this.program, "in_tc_y");
        GLES20.glEnableVertexAttribArray(this.texLocations[0]);
        this.texLocations[1] = GLES20.glGetAttribLocation(this.program, "in_tc_u");
        GLES20.glEnableVertexAttribArray(this.texLocations[1]);
        this.texLocations[2] = GLES20.glGetAttribLocation(this.program, "in_tc_v");
        GLES20.glEnableVertexAttribArray(this.texLocations[2]);
        GlUtil.checkGlError();
        this.colorMatrixLocation = GLES20.glGetUniformLocation(this.program, "mColorConversion");
        GlUtil.checkGlError();
        setupTextures();
        GlUtil.checkGlError();
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 unused) {
        VideoDecoderOutputBuffer pendingOutputBuffer = this.pendingOutputBufferReference.getAndSet(null);
        if (pendingOutputBuffer == null && this.renderedOutputBuffer == null) {
            return;
        }
        if (pendingOutputBuffer != null) {
            VideoDecoderOutputBuffer videoDecoderOutputBuffer = this.renderedOutputBuffer;
            if (videoDecoderOutputBuffer != null) {
                videoDecoderOutputBuffer.release();
            }
            this.renderedOutputBuffer = pendingOutputBuffer;
        }
        VideoDecoderOutputBuffer outputBuffer = this.renderedOutputBuffer;
        float[] colorConversion = kColorConversion709;
        switch (outputBuffer.colorspace) {
            case 1:
                colorConversion = kColorConversion601;
                break;
            case 3:
                colorConversion = kColorConversion2020;
                break;
        }
        GLES20.glUniformMatrix3fv(this.colorMatrixLocation, 1, false, colorConversion, 0);
        int i = 0;
        while (i < 3) {
            int h = i == 0 ? outputBuffer.height : (outputBuffer.height + 1) / 2;
            GLES20.glActiveTexture(33984 + i);
            GLES20.glBindTexture(3553, this.yuvTextures[i]);
            GLES20.glPixelStorei(3317, 1);
            GLES20.glTexImage2D(3553, 0, 6409, outputBuffer.yuvStrides[i], h, 0, 6409, 5121, outputBuffer.yuvPlanes[i]);
            i++;
        }
        int i2 = (widths[0] + 1) / 2;
        int[] widths = {outputBuffer.width, i2, i2};
        for (int i3 = 0; i3 < 3; i3++) {
            if (this.previousWidths[i3] != widths[i3] || this.previousStrides[i3] != outputBuffer.yuvStrides[i3]) {
                Assertions.checkState(outputBuffer.yuvStrides[i3] != 0);
                float widthRatio = widths[i3] / outputBuffer.yuvStrides[i3];
                this.textureCoords[i3] = GlUtil.createBuffer(new float[]{0.0f, 0.0f, 0.0f, 1.0f, widthRatio, 0.0f, widthRatio, 1.0f});
                GLES20.glVertexAttribPointer(this.texLocations[i3], 2, 5126, false, 0, (Buffer) this.textureCoords[i3]);
                this.previousWidths[i3] = widths[i3];
                this.previousStrides[i3] = outputBuffer.yuvStrides[i3];
            }
        }
        GLES20.glClear(16384);
        GLES20.glDrawArrays(5, 0, 4);
        GlUtil.checkGlError();
    }

    @Override // com.google.android.exoplayer2.video.VideoDecoderOutputBufferRenderer
    public void setOutputBuffer(VideoDecoderOutputBuffer outputBuffer) {
        VideoDecoderOutputBuffer oldPendingOutputBuffer = this.pendingOutputBufferReference.getAndSet(outputBuffer);
        if (oldPendingOutputBuffer != null) {
            oldPendingOutputBuffer.release();
        }
        this.surfaceView.requestRender();
    }

    private void setupTextures() {
        GLES20.glGenTextures(3, this.yuvTextures, 0);
        for (int i = 0; i < 3; i++) {
            GLES20.glUniform1i(GLES20.glGetUniformLocation(this.program, TEXTURE_UNIFORMS[i]), i);
            GLES20.glActiveTexture(33984 + i);
            GLES20.glBindTexture(3553, this.yuvTextures[i]);
            GLES20.glTexParameterf(3553, 10241, 9729.0f);
            GLES20.glTexParameterf(3553, 10240, 9729.0f);
            GLES20.glTexParameterf(3553, 10242, 33071.0f);
            GLES20.glTexParameterf(3553, 10243, 33071.0f);
        }
        GlUtil.checkGlError();
    }
}
