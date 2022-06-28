package org.webrtc;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.telegram.messenger.FileLog;
import org.webrtc.RendererCommon;
/* loaded from: classes5.dex */
public class GlGenericDrawer implements RendererCommon.GlDrawer {
    private static final String DEFAULT_VERTEX_SHADER_STRING = "varying vec2 tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\nuniform mat4 tex_mat;\nvoid main() {\n  gl_Position = in_pos;\n  tc = (tex_mat * in_tc).xy;\n}\n";
    private static final FloatBuffer FULL_RECTANGLE_BUFFER = GlUtil.createFloatBuffer(new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f});
    private static final FloatBuffer FULL_RECTANGLE_TEXTURE_BUFFER = GlUtil.createFloatBuffer(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f});
    private static final String INPUT_TEXTURE_COORDINATE_NAME = "in_tc";
    private static final String INPUT_VERTEX_COORDINATE_NAME = "in_pos";
    private static final int OES = 0;
    private static final int RGB = 1;
    private static final String TEXTURE_MATRIX_NAME = "tex_mat";
    private static final int YUV = 2;
    private GlShader[][] currentShader;
    private final String genericFragmentSource;
    private int[][] inPosLocation;
    private int[][] inTcLocation;
    private int[] renderFrameBuffer;
    private float[] renderMatrix;
    private int[] renderTexture;
    private float renderTextureDownscale;
    private int[] renderTextureHeight;
    private int[] renderTextureWidth;
    private final ShaderCallbacks shaderCallbacks;
    private int[][] texMatrixLocation;
    private int[][] texelLocation;
    private float[] textureMatrix;
    private final String vertexShader;

    /* loaded from: classes5.dex */
    public interface ShaderCallbacks {
        void onNewShader(GlShader glShader);

        void onPrepareShader(GlShader glShader, float[] fArr, int i, int i2, int i3, int i4);
    }

    /* loaded from: classes5.dex */
    public interface TextureCallback {
        void run(Bitmap bitmap, int i);
    }

    static String createFragmentShaderString(String genericFragmentSource, int shaderType, boolean blur) {
        StringBuilder stringBuilder = new StringBuilder();
        if (shaderType == 0) {
            stringBuilder.append("#extension GL_OES_EGL_image_external : require\n");
        }
        stringBuilder.append("precision highp float;\n");
        if (!blur) {
            stringBuilder.append("varying vec2 tc;\n");
        }
        if (shaderType == 2) {
            stringBuilder.append("uniform sampler2D y_tex;\n");
            stringBuilder.append("uniform sampler2D u_tex;\n");
            stringBuilder.append("uniform sampler2D v_tex;\n");
            stringBuilder.append("vec4 sample(vec2 p) {\n");
            stringBuilder.append("  float y = texture2D(y_tex, p).r * 1.16438;\n");
            stringBuilder.append("  float u = texture2D(u_tex, p).r;\n");
            stringBuilder.append("  float v = texture2D(v_tex, p).r;\n");
            stringBuilder.append("  return vec4(y + 1.59603 * v - 0.874202,\n");
            stringBuilder.append("    y - 0.391762 * u - 0.812968 * v + 0.531668,\n");
            stringBuilder.append("    y + 2.01723 * u - 1.08563, 1);\n");
            stringBuilder.append("}\n");
            stringBuilder.append(genericFragmentSource);
        } else {
            String samplerName = shaderType == 0 ? "samplerExternalOES" : "sampler2D";
            stringBuilder.append("uniform ");
            stringBuilder.append(samplerName);
            stringBuilder.append(" tex;\n");
            if (blur) {
                stringBuilder.append("precision mediump float;\n");
                stringBuilder.append("varying vec2 tc;\n");
                stringBuilder.append("const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);\n");
                stringBuilder.append("uniform float texelWidthOffset;\n");
                stringBuilder.append("uniform float texelHeightOffset;\n");
                stringBuilder.append("void main(){\n");
                stringBuilder.append("int rad = 3;\n");
                stringBuilder.append("int diameter = 2 * rad + 1;\n");
                stringBuilder.append("vec4 sampleTex = vec4(0, 0, 0, 0);\n");
                stringBuilder.append("vec3 col = vec3(0, 0, 0);\n");
                stringBuilder.append("float weightSum = 0.0;\n");
                stringBuilder.append("for(int i = 0; i < diameter; i++) {\n");
                stringBuilder.append("vec2 offset = vec2(float(i - rad) * texelWidthOffset, float(i - rad) * texelHeightOffset);\n");
                stringBuilder.append("sampleTex = vec4(texture2D(tex, tc.st+offset));\n");
                stringBuilder.append("float index = float(i);\n");
                stringBuilder.append("float boxWeight = float(rad) + 1.0 - abs(index - float(rad));\n");
                stringBuilder.append("col += sampleTex.rgb * boxWeight;\n");
                stringBuilder.append("weightSum += boxWeight;\n");
                stringBuilder.append("}\n");
                stringBuilder.append("vec3 result = col / weightSum;\n");
                stringBuilder.append("lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);\n");
                stringBuilder.append("lowp vec3 greyScaleColor = vec3(satLuminance);\n");
                stringBuilder.append("gl_FragColor = vec4(clamp(mix(greyScaleColor, result.rgb, 1.1), 0.0, 1.0), 1.0);\n");
                stringBuilder.append("}\n");
            } else {
                stringBuilder.append(genericFragmentSource.replace("sample(", "texture2D(tex, "));
            }
        }
        return stringBuilder.toString();
    }

    public GlGenericDrawer(String genericFragmentSource, ShaderCallbacks shaderCallbacks) {
        this(DEFAULT_VERTEX_SHADER_STRING, genericFragmentSource, shaderCallbacks);
    }

    public GlGenericDrawer(String vertexShader, String genericFragmentSource, ShaderCallbacks shaderCallbacks) {
        this.currentShader = (GlShader[][]) Array.newInstance(GlShader.class, 3, 3);
        this.inPosLocation = (int[][]) Array.newInstance(int.class, 3, 3);
        this.inTcLocation = (int[][]) Array.newInstance(int.class, 3, 3);
        this.texMatrixLocation = (int[][]) Array.newInstance(int.class, 3, 3);
        this.texelLocation = (int[][]) Array.newInstance(int.class, 3, 3);
        this.renderTexture = new int[2];
        this.renderTextureWidth = new int[2];
        this.renderTextureHeight = new int[2];
        this.vertexShader = vertexShader;
        this.genericFragmentSource = genericFragmentSource;
        this.shaderCallbacks = shaderCallbacks;
    }

    GlShader createShader(int shaderType, boolean blur) {
        return new GlShader(this.vertexShader, createFragmentShaderString(this.genericFragmentSource, shaderType, blur));
    }

    private void ensureRenderTargetCreated(int originalWidth, int originalHeight, int texIndex) {
        if (this.renderFrameBuffer == null) {
            int[] iArr = new int[2];
            this.renderFrameBuffer = iArr;
            GLES20.glGenFramebuffers(2, iArr, 0);
            GLES20.glGenTextures(2, this.renderTexture, 0);
            int a = 0;
            while (true) {
                int[] iArr2 = this.renderTexture;
                if (a >= iArr2.length) {
                    break;
                }
                GLES20.glBindTexture(3553, iArr2[a]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                a++;
            }
            float[] fArr = new float[16];
            this.renderMatrix = fArr;
            Matrix.setIdentityM(fArr, 0);
        }
        if (this.renderTextureWidth[texIndex] != originalWidth) {
            this.renderTextureDownscale = Math.max(1.0f, Math.max(originalWidth, originalHeight) / 50.0f);
            GLES20.glBindTexture(3553, this.renderTexture[texIndex]);
            float f = this.renderTextureDownscale;
            GLES20.glTexImage2D(3553, 0, 6408, (int) (originalWidth / f), (int) (originalHeight / f), 0, 6408, 5121, null);
            this.renderTextureWidth[texIndex] = originalWidth;
            this.renderTextureHeight[texIndex] = originalHeight;
        }
    }

    public void getRenderBufferBitmap(int baseRotation, TextureCallback callback) {
        float[] fArr;
        int rotation;
        if (this.renderFrameBuffer == null || (fArr = this.textureMatrix) == null) {
            callback.run(null, 0);
            return;
        }
        double Ry = Math.asin(fArr[2]);
        if (Ry < 1.5707963267948966d && Ry > -1.5707963267948966d) {
            float[] fArr2 = this.textureMatrix;
            rotation = (int) ((-Math.atan((-fArr2[1]) / fArr2[0])) / 0.017453292519943295d);
        } else {
            rotation = baseRotation;
        }
        float f = this.renderTextureDownscale;
        int viewportW = (int) (this.renderTextureWidth[0] / f);
        int viewportH = (int) (this.renderTextureHeight[0] / f);
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
        ByteBuffer buffer = ByteBuffer.allocateDirect(viewportW * viewportH * 4);
        GLES20.glReadPixels(0, 0, viewportW, viewportH, 6408, 5121, buffer);
        Bitmap bitmap = Bitmap.createBitmap(viewportW, viewportH, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        GLES20.glBindFramebuffer(36160, 0);
        callback.run(bitmap, rotation);
    }

    @Override // org.webrtc.RendererCommon.GlDrawer
    public void drawOes(int oesTextureId, int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        if (blur) {
            ensureRenderTargetCreated(originalWidth, originalHeight, 1);
            this.textureMatrix = texMatrix;
            float f = this.renderTextureDownscale;
            int viewportW = (int) (originalWidth / f);
            int viewportH = (int) (originalHeight / f);
            GLES20.glViewport(0, 0, viewportW, viewportH);
            int viewportH2 = viewportH;
            int viewportW2 = viewportW;
            prepareShader(0, this.renderMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(36197, oesTextureId);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindTexture(36197, 0);
            GLES20.glBindFramebuffer(36160, 0);
            if (rotatedWidth != originalWidth) {
                viewportH2 = viewportW2;
                viewportW2 = viewportH2;
            }
            ensureRenderTargetCreated(originalWidth, originalHeight, 0);
            prepareShader(1, this.renderMatrix, rotatedWidth != originalWidth ? viewportH2 : viewportW2, rotatedWidth != originalWidth ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 1);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
            prepareShader(1, texMatrix, rotatedWidth != originalWidth ? viewportH2 : viewportW2, rotatedWidth != originalWidth ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            return;
        }
        prepareShader(0, texMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, oesTextureId);
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(36197, 0);
    }

    @Override // org.webrtc.RendererCommon.GlDrawer
    public void drawRgb(int textureId, int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        prepareShader(1, texMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, textureId);
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(3553, 0);
    }

    @Override // org.webrtc.RendererCommon.GlDrawer
    public void drawYuv(int[] yuvTextures, int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, float[] texMatrix, int frameWidth, int frameHeight, int viewportX, int viewportY, int viewportWidth, int viewportHeight, boolean blur) {
        if (blur && originalWidth > 0 && originalHeight > 0) {
            this.textureMatrix = texMatrix;
            ensureRenderTargetCreated(originalWidth, originalHeight, 1);
            float f = this.renderTextureDownscale;
            int viewportW = (int) (originalWidth / f);
            int viewportH = (int) (originalHeight / f);
            GLES20.glViewport(0, 0, viewportW, viewportH);
            int viewportH2 = viewportH;
            int viewportW2 = viewportW;
            prepareShader(2, this.renderMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
            for (int i = 0; i < 3; i++) {
                GLES20.glActiveTexture(i + 33984);
                GLES20.glBindTexture(3553, yuvTextures[i]);
            }
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glDrawArrays(5, 0, 4);
            for (int i2 = 0; i2 < 3; i2++) {
                GLES20.glActiveTexture(i2 + 33984);
                GLES20.glBindTexture(3553, 0);
            }
            GLES20.glBindFramebuffer(36160, 0);
            if (rotatedWidth != originalWidth) {
                viewportH2 = viewportW2;
                viewportW2 = viewportH2;
            }
            ensureRenderTargetCreated(originalWidth, originalHeight, 0);
            prepareShader(1, this.renderMatrix, rotatedWidth != originalWidth ? viewportH2 : viewportW2, rotatedWidth != originalWidth ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 1);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
            prepareShader(1, texMatrix, rotatedWidth != originalWidth ? viewportH2 : viewportW2, rotatedWidth != originalWidth ? viewportW2 : viewportH2, frameWidth, frameHeight, viewportWidth, viewportHeight, 2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            return;
        }
        prepareShader(2, texMatrix, rotatedWidth, rotatedHeight, frameWidth, frameHeight, viewportWidth, viewportHeight, 0);
        for (int i3 = 0; i3 < 3; i3++) {
            GLES20.glActiveTexture(i3 + 33984);
            GLES20.glBindTexture(3553, yuvTextures[i3]);
        }
        GLES20.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        GLES20.glDrawArrays(5, 0, 4);
        for (int i4 = 0; i4 < 3; i4++) {
            GLES20.glActiveTexture(i4 + 33984);
            GLES20.glBindTexture(3553, 0);
        }
    }

    private void prepareShader(int shaderType, float[] texMatrix, int texWidth, int texHeight, int frameWidth, int frameHeight, int viewportWidth, int viewportHeight, int blurPass) {
        GlShader shader;
        boolean blur = blurPass != 0;
        GlShader[][] glShaderArr = this.currentShader;
        if (glShaderArr[shaderType][blurPass] != null) {
            shader = glShaderArr[shaderType][blurPass];
        } else {
            try {
                shader = createShader(shaderType, blur);
                this.currentShader[shaderType][blurPass] = shader;
                shader.useProgram();
                if (shaderType == 2) {
                    GLES20.glUniform1i(shader.getUniformLocation("y_tex"), 0);
                    GLES20.glUniform1i(shader.getUniformLocation("u_tex"), 1);
                    GLES20.glUniform1i(shader.getUniformLocation("v_tex"), 2);
                } else {
                    GLES20.glUniform1i(shader.getUniformLocation("tex"), 0);
                }
                GlUtil.checkNoGLES2Error("Create shader");
                this.shaderCallbacks.onNewShader(shader);
                if (blur) {
                    this.texelLocation[shaderType][0] = shader.getUniformLocation("texelWidthOffset");
                    this.texelLocation[shaderType][1] = shader.getUniformLocation("texelHeightOffset");
                }
                this.texMatrixLocation[shaderType][blurPass] = shader.getUniformLocation(TEXTURE_MATRIX_NAME);
                this.inPosLocation[shaderType][blurPass] = shader.getAttribLocation(INPUT_VERTEX_COORDINATE_NAME);
                this.inTcLocation[shaderType][blurPass] = shader.getAttribLocation(INPUT_TEXTURE_COORDINATE_NAME);
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        shader.useProgram();
        if (blur) {
            float f = 0.0f;
            GLES20.glUniform1f(this.texelLocation[shaderType][0], blurPass == 1 ? 1.0f / texWidth : 0.0f);
            int i = this.texelLocation[shaderType][1];
            if (blurPass == 2) {
                f = 1.0f / texHeight;
            }
            GLES20.glUniform1f(i, f);
        }
        GLES20.glEnableVertexAttribArray(this.inPosLocation[shaderType][blurPass]);
        GLES20.glVertexAttribPointer(this.inPosLocation[shaderType][blurPass], 2, 5126, false, 0, (Buffer) FULL_RECTANGLE_BUFFER);
        GLES20.glEnableVertexAttribArray(this.inTcLocation[shaderType][blurPass]);
        GLES20.glVertexAttribPointer(this.inTcLocation[shaderType][blurPass], 2, 5126, false, 0, (Buffer) FULL_RECTANGLE_TEXTURE_BUFFER);
        GLES20.glUniformMatrix4fv(this.texMatrixLocation[shaderType][blurPass], 1, false, texMatrix, 0);
        this.shaderCallbacks.onPrepareShader(shader, texMatrix, frameWidth, frameHeight, viewportWidth, viewportHeight);
        GlUtil.checkNoGLES2Error("Prepare shader");
    }

    @Override // org.webrtc.RendererCommon.GlDrawer
    public void release() {
        for (int a = 0; a < this.currentShader.length; a++) {
            int b = 0;
            while (true) {
                GlShader[][] glShaderArr = this.currentShader;
                if (b < glShaderArr[a].length) {
                    if (glShaderArr[a][b] != null) {
                        glShaderArr[a][b].release();
                        this.currentShader[a][b] = null;
                    }
                    b++;
                }
            }
        }
        int[] iArr = this.renderFrameBuffer;
        if (iArr != null) {
            GLES20.glDeleteFramebuffers(2, iArr, 0);
            GLES20.glDeleteTextures(2, this.renderTexture, 0);
        }
    }
}
