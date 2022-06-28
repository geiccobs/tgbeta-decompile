package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public class VideoSeekPreviewImage extends View {
    private BitmapShader bitmapShader;
    private Bitmap bitmapToDraw;
    private Bitmap bitmapToRecycle;
    private VideoSeekPreviewImageDelegate delegate;
    private long duration;
    private AnimatedFileDrawable fileDrawable;
    private Drawable frameDrawable;
    private String frameTime;
    private Runnable loadRunnable;
    private float pendingProgress;
    private int pixelWidth;
    private Runnable progressRunnable;
    private boolean ready;
    private int timeWidth;
    private Uri videoUri;
    private int currentPixel = -1;
    private TextPaint textPaint = new TextPaint(1);
    private RectF dstR = new RectF();
    private Paint paint = new Paint(2);
    private Paint bitmapPaint = new Paint(2);
    private RectF bitmapRect = new RectF();
    private Matrix matrix = new Matrix();

    /* loaded from: classes5.dex */
    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        setVisibility(4);
        this.frameDrawable = context.getResources().getDrawable(R.drawable.videopreview);
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.delegate = videoSeekPreviewImageDelegate;
    }

    public void setProgress(final float progress, int w) {
        String formatShortDuration;
        if (w != 0) {
            this.pixelWidth = w;
            int pixel = ((int) (w * progress)) / 5;
            if (this.currentPixel == pixel) {
                return;
            }
            this.currentPixel = pixel;
        }
        final long time = ((float) this.duration) * progress;
        this.frameTime = AndroidUtilities.formatShortDuration((int) (time / 1000));
        this.timeWidth = (int) Math.ceil(this.textPaint.measureText(formatShortDuration));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable file = this.fileDrawable;
        if (file != null) {
            file.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.m3206xe0c1106a(progress, time);
            }
        };
        this.progressRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    /* renamed from: lambda$setProgress$1$org-telegram-ui-Components-VideoSeekPreviewImage */
    public /* synthetic */ void m3206xe0c1106a(float progress, long time) {
        int height;
        int width;
        if (this.fileDrawable == null) {
            this.pendingProgress = progress;
            return;
        }
        int bitmapSize = Math.max(200, AndroidUtilities.dp(100.0f));
        Bitmap bitmap = this.fileDrawable.getFrameAtTime(time);
        if (bitmap != null) {
            int width2 = bitmap.getWidth();
            int height2 = bitmap.getHeight();
            if (width2 > height2) {
                float scale = width2 / bitmapSize;
                width = bitmapSize;
                height = (int) (height2 / scale);
            } else {
                float scale2 = height2 / bitmapSize;
                height = bitmapSize;
                width = (int) (width2 / scale2);
            }
            try {
                Bitmap backgroundBitmap = Bitmaps.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                this.dstR.set(0.0f, 0.0f, width, height);
                Canvas canvas = new Canvas(backgroundBitmap);
                canvas.drawBitmap(bitmap, (android.graphics.Rect) null, this.dstR, this.paint);
                canvas.setBitmap(null);
                bitmap = backgroundBitmap;
            } catch (Throwable th) {
                bitmap = null;
            }
        }
        final Bitmap bitmapFinal = bitmap;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.m3205xef176a4b(bitmapFinal);
            }
        });
    }

    /* renamed from: lambda$setProgress$0$org-telegram-ui-Components-VideoSeekPreviewImage */
    public /* synthetic */ void m3205xef176a4b(Bitmap bitmapFinal) {
        int viewHeight;
        int viewWidth;
        if (bitmapFinal != null) {
            if (this.bitmapToDraw != null) {
                Bitmap bitmap = this.bitmapToRecycle;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                this.bitmapToRecycle = this.bitmapToDraw;
            }
            this.bitmapToDraw = bitmapFinal;
            BitmapShader bitmapShader = new BitmapShader(this.bitmapToDraw, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.bitmapShader = bitmapShader;
            bitmapShader.setLocalMatrix(this.matrix);
            this.bitmapPaint.setShader(this.bitmapShader);
            invalidate();
            int viewSize = AndroidUtilities.dp(150.0f);
            float bitmapWidth = bitmapFinal.getWidth();
            float bitmapHeight = bitmapFinal.getHeight();
            float aspect = bitmapWidth / bitmapHeight;
            if (aspect > 1.0f) {
                viewWidth = viewSize;
                viewHeight = (int) (viewSize / aspect);
            } else {
                viewHeight = viewSize;
                viewWidth = (int) (viewSize * aspect);
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (getVisibility() != 0 || layoutParams.width != viewWidth || layoutParams.height != viewHeight) {
                layoutParams.width = viewWidth;
                layoutParams.height = viewHeight;
                setVisibility(0);
                requestLayout();
            }
        }
        this.progressRunnable = null;
    }

    public void open(final Uri uri) {
        if (uri == null || uri.equals(this.videoUri)) {
            return;
        }
        this.videoUri = uri;
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.m3204lambda$open$3$orgtelegramuiComponentsVideoSeekPreviewImage(uri);
            }
        };
        this.loadRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    /* renamed from: lambda$open$3$org-telegram-ui-Components-VideoSeekPreviewImage */
    public /* synthetic */ void m3204lambda$open$3$orgtelegramuiComponentsVideoSeekPreviewImage(Uri uri) {
        String path;
        String scheme = uri.getScheme();
        if ("tg".equals(scheme)) {
            int currentAccount = Utilities.parseInt((CharSequence) uri.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(currentAccount).getParentObject(Utilities.parseInt((CharSequence) uri.getQueryParameter("rid")).intValue());
            TLRPC.TL_document document = new TLRPC.TL_document();
            document.access_hash = Utilities.parseLong(uri.getQueryParameter("hash")).longValue();
            document.id = Utilities.parseLong(uri.getQueryParameter("id")).longValue();
            document.size = Utilities.parseInt((CharSequence) uri.getQueryParameter("size")).intValue();
            document.dc_id = Utilities.parseInt((CharSequence) uri.getQueryParameter("dc")).intValue();
            document.mime_type = uri.getQueryParameter("mime");
            document.file_reference = Utilities.hexToBytes(uri.getQueryParameter("reference"));
            TLRPC.TL_documentAttributeFilename filename = new TLRPC.TL_documentAttributeFilename();
            filename.file_name = uri.getQueryParameter(CommonProperties.NAME);
            document.attributes.add(filename);
            document.attributes.add(new TLRPC.TL_documentAttributeVideo());
            String name = FileLoader.getAttachFileName(document);
            if (FileLoader.getInstance(currentAccount).isLoadingFile(name)) {
                File directory = FileLoader.getDirectory(4);
                path = new File(directory, document.dc_id + "_" + document.id + ".temp").getAbsolutePath();
            } else {
                path = FileLoader.getInstance(currentAccount).getPathToAttach(document, false).getAbsolutePath();
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(path), true, document.size, document, null, parentObject, 0L, currentAccount, true);
        } else {
            String path2 = uri.getPath();
            this.fileDrawable = new AnimatedFileDrawable(new File(path2), true, 0L, null, null, null, 0L, 0, true);
        }
        this.duration = this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.m3203lambda$open$2$orgtelegramuiComponentsVideoSeekPreviewImage();
            }
        });
    }

    /* renamed from: lambda$open$2$org-telegram-ui-Components-VideoSeekPreviewImage */
    public /* synthetic */ void m3203lambda$open$2$orgtelegramuiComponentsVideoSeekPreviewImage() {
        this.loadRunnable = null;
        if (this.fileDrawable != null) {
            this.ready = true;
            this.delegate.onReady();
        }
    }

    public boolean isReady() {
        return this.ready;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmapToRecycle;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmapToRecycle = null;
        }
        if (this.bitmapToDraw != null && this.bitmapShader != null) {
            this.matrix.reset();
            float scale = getMeasuredWidth() / this.bitmapToDraw.getWidth();
            this.matrix.preScale(scale, scale);
            this.bitmapRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRoundRect(this.bitmapRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.bitmapPaint);
            this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.frameDrawable.draw(canvas);
            canvas.drawText(this.frameTime, (getMeasuredWidth() - this.timeWidth) / 2, getMeasuredHeight() - AndroidUtilities.dp(9.0f), this.textPaint);
        }
    }

    public void close() {
        if (this.loadRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.loadRunnable);
            this.loadRunnable = null;
        }
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
            this.progressRunnable = null;
        }
        AnimatedFileDrawable drawable = this.fileDrawable;
        if (drawable != null) {
            drawable.resetStream(true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.m3202lambda$close$4$orgtelegramuiComponentsVideoSeekPreviewImage();
            }
        });
        setVisibility(4);
        this.bitmapToDraw = null;
        this.bitmapShader = null;
        invalidate();
        this.currentPixel = -1;
        this.videoUri = null;
        this.ready = false;
    }

    /* renamed from: lambda$close$4$org-telegram-ui-Components-VideoSeekPreviewImage */
    public /* synthetic */ void m3202lambda$close$4$orgtelegramuiComponentsVideoSeekPreviewImage() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }
}
