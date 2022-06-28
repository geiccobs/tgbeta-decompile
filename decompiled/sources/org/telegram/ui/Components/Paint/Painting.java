package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLES20;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.DispatchQueue;
import org.telegram.ui.Components.Size;
/* loaded from: classes5.dex */
public class Painting {
    private Path activePath;
    private RectF activeStrokeBounds;
    private Slice backupSlice;
    private Texture bitmapTexture;
    private Brush brush;
    private Texture brushTexture;
    private ByteBuffer dataBuffer;
    private PaintingDelegate delegate;
    private int paintTexture;
    private boolean paused;
    private float[] projection;
    private float[] renderProjection;
    private RenderView renderView;
    private int reusableFramebuffer;
    private Map<String, Shader> shaders;
    private Size size;
    private int suppressChangesCounter;
    private ByteBuffer textureBuffer;
    private ByteBuffer vertexBuffer;
    private int[] buffers = new int[1];
    private RenderState renderState = new RenderState();

    /* loaded from: classes5.dex */
    public interface PaintingDelegate {
        void contentChanged();

        DispatchQueue requestDispatchQueue();

        UndoStore requestUndoStore();

        void strokeCommited();
    }

    /* loaded from: classes5.dex */
    public static class PaintingData {
        public Bitmap bitmap;
        public ByteBuffer data;

        PaintingData(Bitmap b, ByteBuffer buffer) {
            this.bitmap = b;
            this.data = buffer;
        }
    }

    public Painting(Size sz) {
        this.size = sz;
        this.dataBuffer = ByteBuffer.allocateDirect(((int) sz.width) * ((int) this.size.height) * 4);
        this.projection = GLMatrix.LoadOrtho(0.0f, this.size.width, 0.0f, this.size.height, -1.0f, 1.0f);
        if (this.vertexBuffer == null) {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
            this.vertexBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.nativeOrder());
        }
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.rewind();
        if (this.textureBuffer == null) {
            ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
            this.textureBuffer = allocateDirect2;
            allocateDirect2.order(ByteOrder.nativeOrder());
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.rewind();
        }
    }

    public void setDelegate(PaintingDelegate paintingDelegate) {
        this.delegate = paintingDelegate;
    }

    public void setRenderView(RenderView view) {
        this.renderView = view;
    }

    public Size getSize() {
        return this.size;
    }

    public RectF getBounds() {
        return new RectF(0.0f, 0.0f, this.size.width, this.size.height);
    }

    private boolean isSuppressingChanges() {
        return this.suppressChangesCounter > 0;
    }

    private void beginSuppressingChanges() {
        this.suppressChangesCounter++;
    }

    private void endSuppressingChanges() {
        this.suppressChangesCounter--;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmapTexture != null) {
            return;
        }
        this.bitmapTexture = new Texture(bitmap);
    }

    public void paintStroke(final Path path, final boolean clearBuffer, final Runnable action) {
        this.renderView.performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.Painting$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                Painting.this.m2775lambda$paintStroke$0$orgtelegramuiComponentsPaintPainting(path, clearBuffer, action);
            }
        });
    }

    /* renamed from: lambda$paintStroke$0$org-telegram-ui-Components-Paint-Painting */
    public /* synthetic */ void m2775lambda$paintStroke$0$orgtelegramuiComponentsPaintPainting(Path path, boolean clearBuffer, Runnable action) {
        this.activePath = path;
        RectF bounds = null;
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getPaintTexture(), 0);
        Utils.HasGLError();
        int status = GLES20.glCheckFramebufferStatus(36160);
        if (status == 36053) {
            GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
            if (clearBuffer) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GLES20.glClear(16384);
            }
            Map<String, Shader> map = this.shaders;
            if (map == null) {
                return;
            }
            Shader shader = map.get(this.brush.isLightSaber() ? "brushLight" : "brush");
            if (shader == null) {
                return;
            }
            GLES20.glUseProgram(shader.program);
            if (this.brushTexture == null) {
                this.brushTexture = new Texture(this.brush.getStamp());
            }
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.brushTexture.texture());
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.projection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            this.renderState.viewportScale = this.renderView.getScaleX();
            bounds = Render.RenderPath(path, this.renderState);
        }
        GLES20.glBindFramebuffer(36160, 0);
        PaintingDelegate paintingDelegate = this.delegate;
        if (paintingDelegate != null) {
            paintingDelegate.contentChanged();
        }
        RectF rectF = this.activeStrokeBounds;
        if (rectF != null) {
            rectF.union(bounds);
        } else {
            this.activeStrokeBounds = bounds;
        }
        if (action != null) {
            action.run();
        }
    }

    public void commitStroke(final int color) {
        this.renderView.performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.Painting$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                Painting.this.m2773lambda$commitStroke$1$orgtelegramuiComponentsPaintPainting(color);
            }
        });
    }

    /* renamed from: lambda$commitStroke$1$org-telegram-ui-Components-Paint-Painting */
    public /* synthetic */ void m2773lambda$commitStroke$1$orgtelegramuiComponentsPaintPainting(int color) {
        PaintingDelegate paintingDelegate;
        registerUndo(this.activeStrokeBounds);
        beginSuppressingChanges();
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getTexture(), 0);
        GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
        Shader shader = this.shaders.get(this.brush.isLightSaber() ? "compositeWithMaskLight" : "compositeWithMask");
        GLES20.glUseProgram(shader.program);
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.projection));
        GLES20.glUniform1i(shader.getUniform("texture"), 0);
        GLES20.glUniform1i(shader.getUniform("mask"), 1);
        Shader.SetColorUniform(shader.getUniform(TtmlNode.ATTR_TTS_COLOR), color);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, getPaintTexture());
        GLES20.glBlendFunc(1, 0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, (Buffer) this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, (Buffer) this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glBindFramebuffer(36160, 0);
        if (!isSuppressingChanges() && (paintingDelegate = this.delegate) != null) {
            paintingDelegate.contentChanged();
        }
        endSuppressingChanges();
        this.renderState.reset();
        this.activeStrokeBounds = null;
        this.activePath = null;
    }

    public void clearStroke() {
        this.renderView.performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.Painting$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Painting.this.m2772lambda$clearStroke$2$orgtelegramuiComponentsPaintPainting();
            }
        });
    }

    /* renamed from: lambda$clearStroke$2$org-telegram-ui-Components-Paint-Painting */
    public /* synthetic */ void m2772lambda$clearStroke$2$orgtelegramuiComponentsPaintPainting() {
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getPaintTexture(), 0);
        Utils.HasGLError();
        int status = GLES20.glCheckFramebufferStatus(36160);
        if (status == 36053) {
            GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(16384);
        }
        GLES20.glBindFramebuffer(36160, 0);
        PaintingDelegate paintingDelegate = this.delegate;
        if (paintingDelegate != null) {
            paintingDelegate.contentChanged();
        }
        this.renderState.reset();
        this.activeStrokeBounds = null;
        this.activePath = null;
    }

    private void registerUndo(RectF rect) {
        if (rect == null) {
            return;
        }
        boolean intersect = rect.setIntersect(rect, getBounds());
        if (!intersect) {
            return;
        }
        PaintingData paintingData = getPaintingData(rect, true);
        ByteBuffer data = paintingData.data;
        final Slice slice = new Slice(data, rect, this.delegate.requestDispatchQueue());
        this.delegate.requestUndoStore().registerUndo(UUID.randomUUID(), new Runnable() { // from class: org.telegram.ui.Components.Paint.Painting$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                Painting.this.m2776lambda$registerUndo$3$orgtelegramuiComponentsPaintPainting(slice);
            }
        });
    }

    /* renamed from: restoreSlice */
    public void m2776lambda$registerUndo$3$orgtelegramuiComponentsPaintPainting(final Slice slice) {
        this.renderView.performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.Painting$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                Painting.this.m2777lambda$restoreSlice$4$orgtelegramuiComponentsPaintPainting(slice);
            }
        });
    }

    /* renamed from: lambda$restoreSlice$4$org-telegram-ui-Components-Paint-Painting */
    public /* synthetic */ void m2777lambda$restoreSlice$4$orgtelegramuiComponentsPaintPainting(Slice slice) {
        PaintingDelegate paintingDelegate;
        ByteBuffer buffer = slice.getData();
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glTexSubImage2D(3553, 0, slice.getX(), slice.getY(), slice.getWidth(), slice.getHeight(), 6408, 5121, buffer);
        if (!isSuppressingChanges() && (paintingDelegate = this.delegate) != null) {
            paintingDelegate.contentChanged();
        }
        slice.cleanResources();
    }

    public void setRenderProjection(float[] proj) {
        this.renderProjection = proj;
    }

    public void render() {
        if (this.shaders == null) {
            return;
        }
        if (this.activePath != null) {
            render(getPaintTexture(), this.activePath.getColor());
        } else {
            renderBlit();
        }
    }

    private void render(int mask, int color) {
        Shader shader = this.shaders.get(this.brush.isLightSaber() ? "blitWithMaskLight" : "blitWithMask");
        if (shader == null) {
            return;
        }
        GLES20.glUseProgram(shader.program);
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
        GLES20.glUniform1i(shader.getUniform("texture"), 0);
        GLES20.glUniform1i(shader.getUniform("mask"), 1);
        Shader.SetColorUniform(shader.getUniform(TtmlNode.ATTR_TTS_COLOR), color);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, mask);
        GLES20.glBlendFunc(1, 771);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, (Buffer) this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, (Buffer) this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        Utils.HasGLError();
    }

    private void renderBlit() {
        Shader shader = this.shaders.get("blit");
        if (shader == null) {
            return;
        }
        GLES20.glUseProgram(shader.program);
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
        GLES20.glUniform1i(shader.getUniform("texture"), 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glBlendFunc(1, 771);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, (Buffer) this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, (Buffer) this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        Utils.HasGLError();
    }

    public PaintingData getPaintingData(RectF rect, boolean undo) {
        PaintingData data;
        int minX = (int) rect.left;
        int minY = (int) rect.top;
        int width = (int) rect.width();
        int height = (int) rect.height();
        GLES20.glGenFramebuffers(1, this.buffers, 0);
        int framebuffer = this.buffers[0];
        GLES20.glBindFramebuffer(36160, framebuffer);
        GLES20.glGenTextures(1, this.buffers, 0);
        int texture = this.buffers[0];
        GLES20.glBindTexture(3553, texture);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, null);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, texture, 0);
        GLES20.glViewport(0, 0, (int) this.size.width, (int) this.size.height);
        Map<String, Shader> map = this.shaders;
        if (map == null) {
            return null;
        }
        Shader shader = map.get(undo ? "nonPremultipliedBlit" : "blit");
        if (shader == null) {
            return null;
        }
        GLES20.glUseProgram(shader.program);
        Matrix translate = new Matrix();
        translate.preTranslate(-minX, -minY);
        float[] effective = GLMatrix.LoadGraphicsMatrix(translate);
        float[] finalProjection = GLMatrix.MultiplyMat4f(this.projection, effective);
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(finalProjection));
        GLES20.glUniform1i(shader.getUniform("texture"), 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16384);
        GLES20.glBlendFunc(1, 0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, (Buffer) this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, (Buffer) this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        this.dataBuffer.limit(width * height * 4);
        GLES20.glReadPixels(0, 0, width, height, 6408, 5121, this.dataBuffer);
        if (undo) {
            data = new PaintingData(null, this.dataBuffer);
        } else {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(this.dataBuffer);
            data = new PaintingData(bitmap, null);
        }
        int[] iArr = this.buffers;
        iArr[0] = framebuffer;
        GLES20.glDeleteFramebuffers(1, iArr, 0);
        int[] iArr2 = this.buffers;
        iArr2[0] = texture;
        GLES20.glDeleteTextures(1, iArr2, 0);
        return data;
    }

    public void setBrush(Brush value) {
        this.brush = value;
        Texture texture = this.brushTexture;
        if (texture != null) {
            texture.cleanResources(true);
            this.brushTexture = null;
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void onPause(final Runnable completionRunnable) {
        this.renderView.performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.Painting$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Painting.this.m2774lambda$onPause$5$orgtelegramuiComponentsPaintPainting(completionRunnable);
            }
        });
    }

    /* renamed from: lambda$onPause$5$org-telegram-ui-Components-Paint-Painting */
    public /* synthetic */ void m2774lambda$onPause$5$orgtelegramuiComponentsPaintPainting(Runnable completionRunnable) {
        this.paused = true;
        PaintingData data = getPaintingData(getBounds(), true);
        this.backupSlice = new Slice(data.data, getBounds(), this.delegate.requestDispatchQueue());
        cleanResources(false);
        if (completionRunnable != null) {
            completionRunnable.run();
        }
    }

    public void onResume() {
        m2776lambda$registerUndo$3$orgtelegramuiComponentsPaintPainting(this.backupSlice);
        this.backupSlice = null;
        this.paused = false;
    }

    public void cleanResources(boolean recycle) {
        int i = this.reusableFramebuffer;
        if (i != 0) {
            int[] iArr = this.buffers;
            iArr[0] = i;
            GLES20.glDeleteFramebuffers(1, iArr, 0);
            this.reusableFramebuffer = 0;
        }
        this.bitmapTexture.cleanResources(recycle);
        int i2 = this.paintTexture;
        if (i2 != 0) {
            int[] iArr2 = this.buffers;
            iArr2[0] = i2;
            GLES20.glDeleteTextures(1, iArr2, 0);
            this.paintTexture = 0;
        }
        Texture texture = this.brushTexture;
        if (texture != null) {
            texture.cleanResources(true);
            this.brushTexture = null;
        }
        Map<String, Shader> map = this.shaders;
        if (map != null) {
            for (Shader shader : map.values()) {
                shader.cleanResources();
            }
            this.shaders = null;
        }
    }

    private int getReusableFramebuffer() {
        if (this.reusableFramebuffer == 0) {
            int[] buffers = new int[1];
            GLES20.glGenFramebuffers(1, buffers, 0);
            this.reusableFramebuffer = buffers[0];
            Utils.HasGLError();
        }
        return this.reusableFramebuffer;
    }

    private int getTexture() {
        Texture texture = this.bitmapTexture;
        if (texture != null) {
            return texture.texture();
        }
        return 0;
    }

    private int getPaintTexture() {
        if (this.paintTexture == 0) {
            this.paintTexture = Texture.generateTexture(this.size);
        }
        return this.paintTexture;
    }

    public void setupShaders() {
        this.shaders = ShaderSet.setup();
    }
}
