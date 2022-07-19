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
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
/* loaded from: classes3.dex */
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

    /* loaded from: classes3.dex */
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

    public void setProgress(final float f, int i) {
        String formatShortDuration;
        if (i != 0) {
            this.pixelWidth = i;
            int i2 = ((int) (i * f)) / 5;
            if (this.currentPixel == i2) {
                return;
            }
            this.currentPixel = i2;
        }
        final long j = ((float) this.duration) * f;
        this.frameTime = AndroidUtilities.formatShortDuration((int) (j / 1000));
        this.timeWidth = (int) Math.ceil(this.textPaint.measureText(formatShortDuration));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$1(f, j);
            }
        };
        this.progressRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public /* synthetic */ void lambda$setProgress$1(float f, long j) {
        int i;
        if (this.fileDrawable == null) {
            this.pendingProgress = f;
            return;
        }
        int max = Math.max(200, AndroidUtilities.dp(100.0f));
        final Bitmap frameAtTime = this.fileDrawable.getFrameAtTime(j);
        if (frameAtTime != null) {
            int width = frameAtTime.getWidth();
            int height = frameAtTime.getHeight();
            if (width > height) {
                i = (int) (height / (width / max));
            } else {
                int i2 = (int) (width / (height / max));
                i = max;
                max = i2;
            }
            try {
                Bitmap createBitmap = Bitmaps.createBitmap(max, i, Bitmap.Config.ARGB_8888);
                this.dstR.set(0.0f, 0.0f, max, i);
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawBitmap(frameAtTime, (android.graphics.Rect) null, this.dstR, this.paint);
                canvas.setBitmap(null);
                frameAtTime = createBitmap;
            } catch (Throwable unused) {
                frameAtTime = null;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$0(frameAtTime);
            }
        });
    }

    public /* synthetic */ void lambda$setProgress$0(Bitmap bitmap) {
        int i;
        if (bitmap != null) {
            if (this.bitmapToDraw != null) {
                Bitmap bitmap2 = this.bitmapToRecycle;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                this.bitmapToRecycle = this.bitmapToDraw;
            }
            this.bitmapToDraw = bitmap;
            Bitmap bitmap3 = this.bitmapToDraw;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader = new BitmapShader(bitmap3, tileMode, tileMode);
            this.bitmapShader = bitmapShader;
            bitmapShader.setLocalMatrix(this.matrix);
            this.bitmapPaint.setShader(this.bitmapShader);
            invalidate();
            int dp = AndroidUtilities.dp(150.0f);
            float width = bitmap.getWidth() / bitmap.getHeight();
            if (width > 1.0f) {
                i = (int) (dp / width);
            } else {
                dp = (int) (dp * width);
                i = dp;
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (getVisibility() != 0 || layoutParams.width != dp || layoutParams.height != i) {
                layoutParams.width = dp;
                layoutParams.height = i;
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
                VideoSeekPreviewImage.this.lambda$open$3(uri);
            }
        };
        this.loadRunnable = runnable;
        dispatchQueue.postRunnable(runnable);
    }

    public /* synthetic */ void lambda$open$3(Uri uri) {
        String str;
        if ("tg".equals(uri.getScheme())) {
            int intValue = Utilities.parseInt((CharSequence) uri.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt((CharSequence) uri.getQueryParameter("rid")).intValue());
            TLRPC$TL_document tLRPC$TL_document = new TLRPC$TL_document();
            tLRPC$TL_document.access_hash = Utilities.parseLong(uri.getQueryParameter("hash")).longValue();
            tLRPC$TL_document.id = Utilities.parseLong(uri.getQueryParameter("id")).longValue();
            tLRPC$TL_document.size = Utilities.parseInt((CharSequence) uri.getQueryParameter("size")).intValue();
            tLRPC$TL_document.dc_id = Utilities.parseInt((CharSequence) uri.getQueryParameter("dc")).intValue();
            tLRPC$TL_document.mime_type = uri.getQueryParameter("mime");
            tLRPC$TL_document.file_reference = Utilities.hexToBytes(uri.getQueryParameter("reference"));
            TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
            tLRPC$TL_documentAttributeFilename.file_name = uri.getQueryParameter("name");
            tLRPC$TL_document.attributes.add(tLRPC$TL_documentAttributeFilename);
            tLRPC$TL_document.attributes.add(new TLRPC$TL_documentAttributeVideo());
            if (FileLoader.getInstance(intValue).isLoadingFile(FileLoader.getAttachFileName(tLRPC$TL_document))) {
                File directory = FileLoader.getDirectory(4);
                str = new File(directory, tLRPC$TL_document.dc_id + "_" + tLRPC$TL_document.id + ".temp").getAbsolutePath();
            } else {
                str = FileLoader.getInstance(intValue).getPathToAttach(tLRPC$TL_document, false).getAbsolutePath();
            }
            this.fileDrawable = new AnimatedFileDrawable(new File(str), true, tLRPC$TL_document.size, tLRPC$TL_document, null, parentObject, 0L, intValue, true, null);
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0L, null, null, null, 0L, 0, true, null);
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
                VideoSeekPreviewImage.this.lambda$open$2();
            }
        });
    }

    public /* synthetic */ void lambda$open$2() {
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
        if (this.bitmapToDraw == null || this.bitmapShader == null) {
            return;
        }
        this.matrix.reset();
        float measuredWidth = getMeasuredWidth() / this.bitmapToDraw.getWidth();
        this.matrix.preScale(measuredWidth, measuredWidth);
        this.bitmapRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRoundRect(this.bitmapRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.bitmapPaint);
        this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.frameDrawable.draw(canvas);
        canvas.drawText(this.frameTime, (getMeasuredWidth() - this.timeWidth) / 2, getMeasuredHeight() - AndroidUtilities.dp(9.0f), this.textPaint);
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
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.resetStream(true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.VideoSeekPreviewImage$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VideoSeekPreviewImage.this.lambda$close$4();
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

    public /* synthetic */ void lambda$close$4() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }
}
