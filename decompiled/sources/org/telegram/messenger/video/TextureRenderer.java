package org.telegram.messenger.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.view.View;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes4.dex */
public class TextureRenderer {
    private static final String FRAGMENT_EXTERNAL_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "precision highp float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private FloatBuffer bitmapVerticesBuffer;
    private boolean blendEnabled;
    private FilterShaders filterShaders;
    private int imageOrientation;
    private String imagePath;
    private boolean isPhoto;
    private int[] mProgram;
    private int mTextureID;
    private int[] maPositionHandle;
    private int[] maTextureHandle;
    private ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
    private int[] muMVPMatrixHandle;
    private int[] muSTMatrixHandle;
    private int originalHeight;
    private int originalWidth;
    private String paintPath;
    private int[] paintTexture;
    private FloatBuffer renderTextureBuffer;
    private int simpleInputTexCoordHandle;
    private int simplePositionHandle;
    private int simpleShaderProgram;
    private int simpleSourceImageHandle;
    private Bitmap stickerBitmap;
    private Canvas stickerCanvas;
    private int[] stickerTexture;
    private FloatBuffer textureBuffer;
    private int transformedHeight;
    private int transformedWidth;
    private FloatBuffer verticesBuffer;
    private float videoFps;
    float[] bitmapData = {-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private float[] mSTMatrixIdentity = new float[16];
    private boolean firstFrame = true;

    public TextureRenderer(MediaController.SavedFilterState savedFilterState, String image, String paint, ArrayList<VideoEditedInfo.MediaEntity> entities, MediaController.CropState cropState, int w, int h, int originalWidth, int originalHeight, int rotation, float fps, boolean photo) {
        int count;
        int textureRotation;
        float[] textureData;
        this.isPhoto = photo;
        float[] texData = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start textureRenderer w = " + w + " h = " + h + " r = " + rotation + " fps = " + fps);
            if (cropState != null) {
                FileLog.d("cropState px = " + cropState.cropPx + " py = " + cropState.cropPy + " cScale = " + cropState.cropScale + " cropRotate = " + cropState.cropRotate + " pw = " + cropState.cropPw + " ph = " + cropState.cropPh + " tw = " + cropState.transformWidth + " th = " + cropState.transformHeight + " tr = " + cropState.transformRotation + " mirror = " + cropState.mirrored);
            }
        }
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBuffer = asFloatBuffer;
        asFloatBuffer.put(texData).position(0);
        FloatBuffer asFloatBuffer2 = ByteBuffer.allocateDirect(this.bitmapData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.bitmapVerticesBuffer = asFloatBuffer2;
        asFloatBuffer2.put(this.bitmapData).position(0);
        Matrix.setIdentityM(this.mSTMatrix, 0);
        Matrix.setIdentityM(this.mSTMatrixIdentity, 0);
        if (savedFilterState != null) {
            FilterShaders filterShaders = new FilterShaders(true);
            this.filterShaders = filterShaders;
            filterShaders.setDelegate(FilterShaders.getFilterShadersDelegate(savedFilterState));
        }
        this.transformedWidth = w;
        this.transformedHeight = h;
        int originalWidth2 = originalWidth;
        this.originalWidth = originalWidth2;
        int originalHeight2 = originalHeight;
        this.originalHeight = originalHeight2;
        this.imagePath = image;
        this.paintPath = paint;
        this.mediaEntities = entities;
        this.videoFps = fps == 0.0f ? 30.0f : fps;
        if (this.filterShaders != null) {
            count = 2;
        } else {
            count = 1;
        }
        this.mProgram = new int[count];
        this.muMVPMatrixHandle = new int[count];
        this.muSTMatrixHandle = new int[count];
        this.maPositionHandle = new int[count];
        this.maTextureHandle = new int[count];
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        if (cropState != null) {
            float[] verticesData = new float[8];
            verticesData[0] = 0.0f;
            verticesData[1] = 0.0f;
            verticesData[2] = w;
            verticesData[3] = 0.0f;
            verticesData[4] = 0.0f;
            verticesData[5] = h;
            verticesData[6] = w;
            verticesData[7] = h;
            int textureRotation2 = cropState.transformRotation;
            if (textureRotation2 == 90 || textureRotation2 == 270) {
                originalWidth2 = originalHeight;
                originalHeight2 = originalWidth;
            }
            int temp = this.transformedWidth;
            this.transformedWidth = (int) (temp * cropState.cropPw);
            this.transformedHeight = (int) (this.transformedHeight * cropState.cropPh);
            int count2 = count;
            double d = -cropState.cropRotate;
            Double.isNaN(d);
            float angle = (float) (d * 0.017453292519943295d);
            int a = 0;
            while (a < 4) {
                float[] texData2 = texData;
                float x1 = verticesData[a * 2] - (w / 2);
                int originalWidth3 = originalWidth2;
                float y1 = verticesData[(a * 2) + 1] - (h / 2);
                double d2 = x1;
                int count3 = count2;
                double cos = Math.cos(angle);
                Double.isNaN(d2);
                double d3 = d2 * cos;
                double d4 = y1;
                int originalHeight3 = originalHeight2;
                double sin = Math.sin(angle);
                Double.isNaN(d4);
                double d5 = d3 - (d4 * sin);
                double d6 = cropState.cropPx * w;
                Double.isNaN(d6);
                float x2 = ((float) (d5 + d6)) * cropState.cropScale;
                double d7 = x1;
                double sin2 = Math.sin(angle);
                Double.isNaN(d7);
                double d8 = d7 * sin2;
                double d9 = y1;
                double cos2 = Math.cos(angle);
                Double.isNaN(d9);
                double d10 = cropState.cropPy * h;
                Double.isNaN(d10);
                float y2 = ((float) ((d8 + (d9 * cos2)) - d10)) * cropState.cropScale;
                verticesData[a * 2] = (x2 / this.transformedWidth) * 2.0f;
                verticesData[(a * 2) + 1] = (y2 / this.transformedHeight) * 2.0f;
                a++;
                originalWidth2 = originalWidth3;
                originalHeight2 = originalHeight3;
                texData = texData2;
                count2 = count3;
            }
            int a2 = verticesData.length;
            FloatBuffer asFloatBuffer3 = ByteBuffer.allocateDirect(a2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticesBuffer = asFloatBuffer3;
            asFloatBuffer3.put(verticesData).position(0);
            textureRotation = textureRotation2;
        } else {
            float[] verticesData2 = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
            FloatBuffer asFloatBuffer4 = ByteBuffer.allocateDirect(verticesData2.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticesBuffer = asFloatBuffer4;
            asFloatBuffer4.put(verticesData2).position(0);
            textureRotation = 0;
        }
        if (this.filterShaders != null) {
            if (textureRotation == 90) {
                textureData = new float[]{1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f};
            } else if (textureRotation == 180) {
                textureData = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
            } else if (textureRotation == 270) {
                textureData = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
            } else {
                textureData = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
            }
        } else if (textureRotation == 90) {
            textureData = new float[]{1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        } else if (textureRotation == 180) {
            textureData = new float[]{1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
        } else if (textureRotation == 270) {
            textureData = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
        } else {
            textureData = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        }
        if (cropState != null && cropState.mirrored) {
            for (int a3 = 0; a3 < 4; a3++) {
                if (textureData[a3 * 2] > 0.5f) {
                    textureData[a3 * 2] = 0.0f;
                } else {
                    textureData[a3 * 2] = 1.0f;
                }
            }
        }
        int a4 = textureData.length;
        FloatBuffer asFloatBuffer5 = ByteBuffer.allocateDirect(a4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.renderTextureBuffer = asFloatBuffer5;
        asFloatBuffer5.put(textureData).position(0);
    }

    public int getTextureId() {
        return this.mTextureID;
    }

    public void drawFrame(SurfaceTexture st) {
        int target;
        int index;
        int texture;
        float[] stMatrix;
        int i = 0;
        if (this.isPhoto) {
            GLES20.glUseProgram(this.simpleShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.simpleInputTexCoordHandle, 2, 5126, false, 8, (Buffer) this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
        } else {
            st.getTransformMatrix(this.mSTMatrix);
            if (BuildVars.LOGS_ENABLED && this.firstFrame) {
                StringBuilder builder = new StringBuilder();
                int a = 0;
                while (true) {
                    float[] fArr = this.mSTMatrix;
                    if (a >= fArr.length) {
                        break;
                    }
                    builder.append(fArr[a]);
                    builder.append(", ");
                    a++;
                }
                FileLog.d("stMatrix = " + ((Object) builder));
                this.firstFrame = false;
            }
            if (this.blendEnabled) {
                GLES20.glDisable(3042);
                this.blendEnabled = false;
            }
            FilterShaders filterShaders = this.filterShaders;
            if (filterShaders != null) {
                filterShaders.onVideoFrameUpdate(this.mSTMatrix);
                GLES20.glViewport(0, 0, this.originalWidth, this.originalHeight);
                this.filterShaders.drawSkinSmoothPass();
                this.filterShaders.drawEnhancePass();
                this.filterShaders.drawSharpenPass();
                this.filterShaders.drawCustomParamsPass();
                boolean blurred = this.filterShaders.drawBlurPass();
                GLES20.glBindFramebuffer(36160, 0);
                int i2 = this.transformedWidth;
                if (i2 != this.originalWidth || this.transformedHeight != this.originalHeight) {
                    GLES20.glViewport(0, 0, i2, this.transformedHeight);
                }
                texture = this.filterShaders.getRenderTexture(!blurred);
                index = 1;
                target = 3553;
                stMatrix = this.mSTMatrixIdentity;
            } else {
                texture = this.mTextureID;
                index = 0;
                target = 36197;
                stMatrix = this.mSTMatrix;
            }
            GLES20.glUseProgram(this.mProgram[index]);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(target, texture);
            GLES20.glVertexAttribPointer(this.maPositionHandle[index], 2, 5126, false, 8, (Buffer) this.verticesBuffer);
            GLES20.glEnableVertexAttribArray(this.maPositionHandle[index]);
            GLES20.glVertexAttribPointer(this.maTextureHandle[index], 2, 5126, false, 8, (Buffer) this.renderTextureBuffer);
            GLES20.glEnableVertexAttribArray(this.maTextureHandle[index]);
            GLES20.glUniformMatrix4fv(this.muSTMatrixHandle[index], 1, false, stMatrix, 0);
            GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle[index], 1, false, this.mMVPMatrix, 0);
            GLES20.glDrawArrays(5, 0, 4);
        }
        float[] stMatrix2 = this.paintTexture;
        if (stMatrix2 != null || this.stickerTexture != null) {
            GLES20.glUseProgram(this.simpleShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(this.simpleSourceImageHandle, 0);
            GLES20.glEnableVertexAttribArray(this.simpleInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.simpleInputTexCoordHandle, 2, 5126, false, 8, (Buffer) this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.simplePositionHandle);
        }
        if (this.paintTexture != null) {
            int a2 = 0;
            while (true) {
                int[] iArr = this.paintTexture;
                if (a2 >= iArr.length) {
                    break;
                }
                drawTexture(true, iArr[a2]);
                a2++;
            }
        }
        if (this.stickerTexture != null) {
            int N = this.mediaEntities.size();
            int a3 = 0;
            while (a3 < N) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a3);
                if (entity.ptr != 0) {
                    Bitmap bitmap = this.stickerBitmap;
                    RLottieDrawable.getFrame(entity.ptr, (int) entity.currentFrame, bitmap, 512, 512, bitmap.getRowBytes(), true);
                    GLES20.glBindTexture(3553, this.stickerTexture[i]);
                    GLUtils.texImage2D(3553, i, this.stickerBitmap, i);
                    entity.currentFrame += entity.framesPerDraw;
                    if (entity.currentFrame >= entity.metadata[i]) {
                        entity.currentFrame = 0.0f;
                    }
                    drawTexture(false, this.stickerTexture[i], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType & 2) != 0);
                } else if (entity.animatedFileDrawable != null) {
                    int lastFrame = (int) entity.currentFrame;
                    entity.currentFrame += entity.framesPerDraw;
                    for (int currentFrame = (int) entity.currentFrame; lastFrame != currentFrame; currentFrame--) {
                        entity.animatedFileDrawable.getNextFrame();
                    }
                    Bitmap frameBitmap = entity.animatedFileDrawable.getBackgroundBitmap();
                    if (this.stickerCanvas == null && this.stickerBitmap != null) {
                        this.stickerCanvas = new Canvas(this.stickerBitmap);
                    }
                    Bitmap bitmap2 = this.stickerBitmap;
                    if (bitmap2 != null && frameBitmap != null) {
                        bitmap2.eraseColor(i);
                        this.stickerCanvas.drawBitmap(frameBitmap, 0.0f, 0.0f, (Paint) null);
                        GLES20.glBindTexture(3553, this.stickerTexture[i]);
                        GLUtils.texImage2D(3553, i, this.stickerBitmap, i);
                        drawTexture(false, this.stickerTexture[i], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType & 2) != 0);
                    }
                } else if (entity.bitmap != null) {
                    GLES20.glBindTexture(3553, this.stickerTexture[0]);
                    GLUtils.texImage2D(3553, 0, entity.bitmap, 0);
                    drawTexture(false, this.stickerTexture[0], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType & 2) != 0);
                }
                a3++;
                i = 0;
            }
        }
        GLES20.glFinish();
    }

    private void drawTexture(boolean bind, int texture) {
        drawTexture(bind, texture, -10000.0f, -10000.0f, -10000.0f, -10000.0f, 0.0f, false);
    }

    private void drawTexture(boolean bind, int texture, float x, float y, float w, float h, float rotation, boolean mirror) {
        float h2;
        float w2;
        float y2;
        if (!this.blendEnabled) {
            GLES20.glEnable(3042);
            GLES20.glBlendFunc(1, 771);
            this.blendEnabled = true;
        }
        if (x <= -10000.0f) {
            float[] fArr = this.bitmapData;
            fArr[0] = -1.0f;
            fArr[1] = 1.0f;
            fArr[2] = 1.0f;
            fArr[3] = 1.0f;
            fArr[4] = -1.0f;
            fArr[5] = -1.0f;
            fArr[6] = 1.0f;
            fArr[7] = -1.0f;
            y2 = y;
            w2 = w;
            h2 = h;
        } else {
            float x2 = (x * 2.0f) - 1.0f;
            y2 = ((1.0f - y) * 2.0f) - 1.0f;
            w2 = w * 2.0f;
            h2 = h * 2.0f;
            float[] fArr2 = this.bitmapData;
            fArr2[0] = x2;
            fArr2[1] = y2;
            fArr2[2] = x2 + w2;
            fArr2[3] = y2;
            fArr2[4] = x2;
            fArr2[5] = y2 - h2;
            fArr2[6] = x2 + w2;
            fArr2[7] = y2 - h2;
        }
        float[] fArr3 = this.bitmapData;
        float mx = (fArr3[0] + fArr3[2]) / 2.0f;
        if (mirror) {
            float temp = fArr3[2];
            fArr3[2] = fArr3[0];
            fArr3[0] = temp;
            float temp2 = fArr3[6];
            float temp3 = fArr3[4];
            fArr3[6] = temp3;
            fArr3[4] = temp2;
        }
        if (rotation != 0.0f) {
            float ratio = this.transformedWidth / this.transformedHeight;
            float my = (fArr3[5] + fArr3[1]) / 2.0f;
            int a = 0;
            for (int i = 4; a < i; i = 4) {
                float[] fArr4 = this.bitmapData;
                float x1 = fArr4[a * 2] - mx;
                float y1 = (fArr4[(a * 2) + 1] - my) / ratio;
                double d = x1;
                int a2 = a;
                double cos = Math.cos(rotation);
                Double.isNaN(d);
                double d2 = d * cos;
                double d3 = y1;
                double sin = Math.sin(rotation);
                Double.isNaN(d3);
                fArr4[a * 2] = ((float) (d2 - (d3 * sin))) + mx;
                double d4 = x1;
                float w3 = w2;
                double sin2 = Math.sin(rotation);
                Double.isNaN(d4);
                double d5 = d4 * sin2;
                double d6 = y1;
                double cos2 = Math.cos(rotation);
                Double.isNaN(d6);
                this.bitmapData[(a2 * 2) + 1] = (((float) (d5 + (d6 * cos2))) * ratio) + my;
                a = a2 + 1;
                y2 = y2;
                w2 = w3;
                h2 = h2;
            }
        }
        this.bitmapVerticesBuffer.put(this.bitmapData).position(0);
        GLES20.glVertexAttribPointer(this.simplePositionHandle, 2, 5126, false, 8, (Buffer) this.bitmapVerticesBuffer);
        if (bind) {
            GLES20.glBindTexture(3553, texture);
        }
        GLES20.glDrawArrays(5, 0, 4);
    }

    public void setBreakStrategy(EditTextOutline editText) {
        editText.setBreakStrategy(0);
    }

    public void surfaceCreated() {
        String path;
        float scale;
        String str;
        int a = 0;
        while (true) {
            int[] iArr = this.mProgram;
            if (a >= iArr.length) {
                break;
            }
            iArr[a] = createProgram(VERTEX_SHADER, a == 0 ? FRAGMENT_EXTERNAL_SHADER : FRAGMENT_SHADER);
            this.maPositionHandle[a] = GLES20.glGetAttribLocation(this.mProgram[a], "aPosition");
            this.maTextureHandle[a] = GLES20.glGetAttribLocation(this.mProgram[a], "aTextureCoord");
            this.muMVPMatrixHandle[a] = GLES20.glGetUniformLocation(this.mProgram[a], "uMVPMatrix");
            this.muSTMatrixHandle[a] = GLES20.glGetUniformLocation(this.mProgram[a], "uSTMatrix");
            a++;
        }
        int i = 1;
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int i2 = textures[0];
        this.mTextureID = i2;
        GLES20.glBindTexture(36197, i2);
        GLES20.glTexParameteri(36197, 10241, 9729);
        GLES20.glTexParameteri(36197, 10240, 9729);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        if (this.filterShaders != null || this.imagePath != null || this.paintPath != null || this.mediaEntities != null) {
            int vertexShader = FilterShaders.loadShader(35633, FilterShaders.simpleVertexShaderCode);
            int fragmentShader = FilterShaders.loadShader(35632, FilterShaders.simpleFragmentShaderCode);
            if (vertexShader != 0 && fragmentShader != 0) {
                int glCreateProgram = GLES20.glCreateProgram();
                this.simpleShaderProgram = glCreateProgram;
                GLES20.glAttachShader(glCreateProgram, vertexShader);
                GLES20.glAttachShader(this.simpleShaderProgram, fragmentShader);
                GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.simpleShaderProgram);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, linkStatus, 0);
                if (linkStatus[0] != 0) {
                    this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                    this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                    this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                } else {
                    GLES20.glDeleteProgram(this.simpleShaderProgram);
                    this.simpleShaderProgram = 0;
                }
            }
        }
        FilterShaders filterShaders = this.filterShaders;
        if (filterShaders != null) {
            filterShaders.create();
            this.filterShaders.setRenderData(null, 0, this.mTextureID, this.originalWidth, this.originalHeight);
        }
        String str2 = this.imagePath;
        int i3 = -16777216;
        if (str2 != null || this.paintPath != null) {
            int[] iArr2 = new int[(str2 != null ? 1 : 0) + (this.paintPath != null ? 1 : 0)];
            this.paintTexture = iArr2;
            GLES20.glGenTextures(iArr2.length, iArr2, 0);
            int a2 = 0;
            while (a2 < this.paintTexture.length) {
                try {
                    int angle = 0;
                    if (a2 == 0 && (str = this.imagePath) != null) {
                        path = str;
                        try {
                            ExifInterface exif = new ExifInterface(path);
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, i);
                            switch (orientation) {
                                case 3:
                                    angle = 180;
                                    break;
                                case 6:
                                    angle = 90;
                                    break;
                                case 8:
                                    angle = 270;
                                    break;
                            }
                        } catch (Throwable th) {
                        }
                    } else {
                        path = this.paintPath;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap != null) {
                        if (a2 == 0 && this.imagePath != null) {
                            Bitmap newBitmap = Bitmap.createBitmap(this.transformedWidth, this.transformedHeight, Bitmap.Config.ARGB_8888);
                            newBitmap.eraseColor(i3);
                            Canvas canvas = new Canvas(newBitmap);
                            if (angle != 90 && angle != 270) {
                                scale = Math.max(bitmap.getWidth() / this.transformedWidth, bitmap.getHeight() / this.transformedHeight);
                                android.graphics.Matrix matrix = new android.graphics.Matrix();
                                matrix.postTranslate((-bitmap.getWidth()) / 2, (-bitmap.getHeight()) / 2);
                                matrix.postScale(1.0f / scale, 1.0f / scale);
                                matrix.postRotate(angle);
                                matrix.postTranslate(newBitmap.getWidth() / 2, newBitmap.getHeight() / 2);
                                canvas.drawBitmap(bitmap, matrix, new Paint(2));
                                bitmap = newBitmap;
                            }
                            scale = Math.max(bitmap.getHeight() / this.transformedWidth, bitmap.getWidth() / this.transformedHeight);
                            android.graphics.Matrix matrix2 = new android.graphics.Matrix();
                            matrix2.postTranslate((-bitmap.getWidth()) / 2, (-bitmap.getHeight()) / 2);
                            matrix2.postScale(1.0f / scale, 1.0f / scale);
                            matrix2.postRotate(angle);
                            matrix2.postTranslate(newBitmap.getWidth() / 2, newBitmap.getHeight() / 2);
                            canvas.drawBitmap(bitmap, matrix2, new Paint(2));
                            bitmap = newBitmap;
                        }
                        GLES20.glBindTexture(3553, this.paintTexture[a2]);
                        GLES20.glTexParameteri(3553, 10241, 9729);
                        GLES20.glTexParameteri(3553, 10240, 9729);
                        GLES20.glTexParameteri(3553, 10242, 33071);
                        GLES20.glTexParameteri(3553, 10243, 33071);
                        GLUtils.texImage2D(3553, 0, bitmap, 0);
                    }
                    a2++;
                    i = 1;
                    i3 = -16777216;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
        if (this.mediaEntities != null) {
            try {
                this.stickerBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                int[] iArr3 = new int[1];
                this.stickerTexture = iArr3;
                GLES20.glGenTextures(1, iArr3, 0);
                GLES20.glBindTexture(3553, this.stickerTexture[0]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                int N = this.mediaEntities.size();
                for (int a3 = 0; a3 < N; a3++) {
                    VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a3);
                    if (entity.type == 0) {
                        if ((entity.subType & 1) != 0) {
                            entity.metadata = new int[3];
                            entity.ptr = RLottieDrawable.create(entity.text, null, 512, 512, entity.metadata, false, null, false, 0);
                            entity.framesPerDraw = entity.metadata[1] / this.videoFps;
                        } else if ((entity.subType & 4) != 0) {
                            entity.animatedFileDrawable = new AnimatedFileDrawable(new File(entity.text), true, 0L, null, null, null, 0L, UserConfig.selectedAccount, true, 512, 512);
                            entity.framesPerDraw = this.videoFps / 30.0f;
                            entity.currentFrame = 0.0f;
                        } else {
                            if (Build.VERSION.SDK_INT >= 19) {
                                entity.bitmap = BitmapFactory.decodeFile(entity.text);
                            } else {
                                File path2 = new File(entity.text);
                                RandomAccessFile file = new RandomAccessFile(path2, "r");
                                ByteBuffer buffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, path2.length());
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                bmOptions.inJustDecodeBounds = true;
                                Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                entity.bitmap = Bitmaps.createBitmap(bmOptions.outWidth, bmOptions.outHeight, Bitmap.Config.ARGB_8888);
                                Utilities.loadWebpImage(entity.bitmap, buffer, buffer.limit(), null, true);
                                file.close();
                            }
                            if (entity.bitmap != null) {
                                float aspect = entity.bitmap.getWidth() / entity.bitmap.getHeight();
                                if (aspect > 1.0f) {
                                    float h = entity.height / aspect;
                                    entity.y += (entity.height - h) / 2.0f;
                                    entity.height = h;
                                } else if (aspect < 1.0f) {
                                    float w = entity.width * aspect;
                                    entity.x += (entity.width - w) / 2.0f;
                                    entity.width = w;
                                }
                            }
                        }
                    } else if (entity.type == 1) {
                        EditTextOutline editText = new EditTextOutline(ApplicationLoader.applicationContext);
                        editText.setBackgroundColor(0);
                        editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
                        editText.setTextSize(0, entity.fontSize);
                        editText.setText(entity.text);
                        editText.setTextColor(entity.color);
                        editText.setTypeface(null, 1);
                        editText.setGravity(17);
                        editText.setHorizontallyScrolling(false);
                        editText.setImeOptions(268435456);
                        editText.setFocusableInTouchMode(true);
                        editText.setInputType(editText.getInputType() | 16384);
                        if (Build.VERSION.SDK_INT >= 23) {
                            setBreakStrategy(editText);
                        }
                        if ((entity.subType & 1) != 0) {
                            editText.setTextColor(-1);
                            editText.setStrokeColor(entity.color);
                            editText.setFrameColor(0);
                            editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        } else if ((entity.subType & 4) != 0) {
                            editText.setTextColor(-16777216);
                            editText.setStrokeColor(0);
                            editText.setFrameColor(entity.color);
                            editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        } else {
                            editText.setTextColor(entity.color);
                            editText.setStrokeColor(0);
                            editText.setFrameColor(0);
                            editText.setShadowLayer(5.0f, 0.0f, 1.0f, 1711276032);
                        }
                        editText.measure(View.MeasureSpec.makeMeasureSpec(entity.viewWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(entity.viewHeight, C.BUFFER_FLAG_ENCRYPTED));
                        editText.layout(0, 0, entity.viewWidth, entity.viewHeight);
                        entity.bitmap = Bitmap.createBitmap(entity.viewWidth, entity.viewHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas2 = new Canvas(entity.bitmap);
                        editText.draw(canvas2);
                    }
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int pixelShader;
        int program;
        int vertexShader = FilterShaders.loadShader(35633, vertexSource);
        if (vertexShader == 0 || (pixelShader = FilterShaders.loadShader(35632, fragmentSource)) == 0 || (program = GLES20.glCreateProgram()) == 0) {
            return 0;
        }
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, pixelShader);
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, 35714, linkStatus, 0);
        if (linkStatus[0] != 1) {
            GLES20.glDeleteProgram(program);
            return 0;
        }
        return program;
    }

    public void release() {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.mediaEntities;
        if (arrayList != null) {
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a);
                if (entity.ptr != 0) {
                    RLottieDrawable.destroy(entity.ptr);
                }
                if (entity.animatedFileDrawable != null) {
                    entity.animatedFileDrawable.recycle();
                }
            }
        }
    }

    public void changeFragmentShader(String fragmentExternalShader, String fragmentShader) {
        GLES20.glDeleteProgram(this.mProgram[0]);
        this.mProgram[0] = createProgram(VERTEX_SHADER, fragmentExternalShader);
        int[] iArr = this.mProgram;
        if (iArr.length > 1) {
            iArr[1] = createProgram(VERTEX_SHADER, fragmentShader);
        }
    }
}
