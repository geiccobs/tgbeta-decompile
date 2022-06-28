package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BlurBehindDrawable;
/* loaded from: classes5.dex */
public class BlurBehindDrawable {
    public static final int ADJUST_PAN_TRANSLATION_CONTENT = 1;
    public static final int STATIC_CONTENT = 0;
    public static final int TAG_DRAWING_AS_BACKGROUND = 67108867;
    private Bitmap[] backgroundBitmap;
    private Canvas[] backgroundBitmapCanvas;
    private View behindView;
    private float blurAlpha;
    private Canvas[] blurCanvas;
    private Bitmap[] blurredBitmapTmp;
    private boolean error;
    Paint errorBlackoutPaint;
    private int lastH;
    private int lastW;
    private float panTranslationY;
    private View parentView;
    private boolean processingNextFrame;
    DispatchQueue queue;
    private Bitmap[] renderingBitmap;
    private Canvas[] renderingBitmapCanvas;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean show;
    private boolean skipDraw;
    private int toolbarH;
    private final int type;
    private boolean wasDraw;
    private boolean invalidate = true;
    private boolean animateAlpha = true;
    private final float DOWN_SCALE = 6.0f;
    BlurBackgroundTask blurBackgroundTask = new BlurBackgroundTask();
    Paint emptyPaint = new Paint(2);

    public BlurBehindDrawable(View behindView, View parentView, int type, Theme.ResourcesProvider resourcesProvider) {
        Paint paint = new Paint();
        this.errorBlackoutPaint = paint;
        this.type = type;
        this.behindView = behindView;
        this.parentView = parentView;
        this.resourcesProvider = resourcesProvider;
        paint.setColor(-16777216);
    }

    public void draw(Canvas canvas) {
        if (this.type == 1 && !this.wasDraw && !this.animateAlpha) {
            generateBlurredBitmaps();
            this.invalidate = false;
        }
        Bitmap[] bitmap = this.renderingBitmap;
        if ((bitmap != null || this.error) && this.animateAlpha) {
            boolean z = this.show;
            if (z) {
                float f = this.blurAlpha;
                if (f != 1.0f) {
                    float f2 = f + 0.09f;
                    this.blurAlpha = f2;
                    if (f2 > 1.0f) {
                        this.blurAlpha = 1.0f;
                    }
                    this.parentView.invalidate();
                }
            }
            if (!z) {
                float f3 = this.blurAlpha;
                if (f3 != 0.0f) {
                    float f4 = f3 - 0.09f;
                    this.blurAlpha = f4;
                    if (f4 < 0.0f) {
                        this.blurAlpha = 0.0f;
                    }
                    this.parentView.invalidate();
                }
            }
        }
        float alpha = this.animateAlpha ? this.blurAlpha : 1.0f;
        if (bitmap != null || !this.error) {
            if (alpha == 1.0f) {
                canvas.save();
            } else {
                canvas.saveLayerAlpha(0.0f, 0.0f, this.parentView.getMeasuredWidth(), this.parentView.getMeasuredHeight(), (int) (alpha * 255.0f), 31);
            }
            if (bitmap != null) {
                this.emptyPaint.setAlpha((int) (255.0f * alpha));
                if (this.type == 1) {
                    canvas.translate(0.0f, this.panTranslationY);
                }
                canvas.save();
                canvas.scale(this.parentView.getMeasuredWidth() / bitmap[1].getWidth(), this.parentView.getMeasuredHeight() / bitmap[1].getHeight());
                canvas.drawBitmap(bitmap[1], 0.0f, 0.0f, this.emptyPaint);
                canvas.restore();
                canvas.save();
                if (this.type == 0) {
                    canvas.translate(0.0f, this.panTranslationY);
                }
                canvas.scale(this.parentView.getMeasuredWidth() / bitmap[0].getWidth(), this.toolbarH / bitmap[0].getHeight());
                canvas.drawBitmap(bitmap[0], 0.0f, 0.0f, this.emptyPaint);
                canvas.restore();
                this.wasDraw = true;
                canvas.drawColor(436207616);
            }
            canvas.restore();
            if (!this.show || this.processingNextFrame) {
                return;
            }
            if (this.renderingBitmap == null || this.invalidate) {
                this.processingNextFrame = true;
                this.invalidate = false;
                if (this.blurredBitmapTmp == null) {
                    this.blurredBitmapTmp = new Bitmap[2];
                    this.blurCanvas = new Canvas[2];
                }
                for (int i = 0; i < 2; i++) {
                    if (this.blurredBitmapTmp[i] == null || this.parentView.getMeasuredWidth() != this.lastW || this.parentView.getMeasuredHeight() != this.lastH) {
                        int lastH = this.parentView.getMeasuredHeight();
                        int lastW = this.parentView.getMeasuredWidth();
                        int h = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(200.0f);
                        this.toolbarH = h;
                        if (i != 0) {
                            h = lastH;
                        }
                        try {
                            this.blurredBitmapTmp[i] = Bitmap.createBitmap((int) (lastW / 6.0f), (int) (h / 6.0f), Bitmap.Config.ARGB_8888);
                            this.blurCanvas[i] = new Canvas(this.blurredBitmapTmp[i]);
                        } catch (Exception e) {
                            FileLog.e(e);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BlurBehindDrawable$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    BlurBehindDrawable.this.m2224lambda$draw$0$orgtelegramuiComponentsBlurBehindDrawable();
                                }
                            });
                            return;
                        }
                    } else {
                        this.blurredBitmapTmp[i].eraseColor(0);
                    }
                    if (i == 1) {
                        this.blurredBitmapTmp[i].eraseColor(getThemedColor(Theme.key_windowBackgroundWhite));
                    }
                    this.blurCanvas[i].save();
                    this.blurCanvas[i].scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                    Drawable backDrawable = this.behindView.getBackground();
                    if (backDrawable == null) {
                        backDrawable = getBackgroundDrawable();
                    }
                    this.behindView.setTag(TAG_DRAWING_AS_BACKGROUND, Integer.valueOf(i));
                    if (i == 0) {
                        this.blurCanvas[i].translate(0.0f, -this.panTranslationY);
                        this.behindView.draw(this.blurCanvas[i]);
                    }
                    if (backDrawable != null && i == 1) {
                        android.graphics.Rect oldBounds = backDrawable.getBounds();
                        backDrawable.setBounds(0, 0, this.behindView.getMeasuredWidth(), this.behindView.getMeasuredHeight());
                        backDrawable.draw(this.blurCanvas[i]);
                        backDrawable.setBounds(oldBounds);
                        this.behindView.draw(this.blurCanvas[i]);
                    }
                    this.behindView.setTag(TAG_DRAWING_AS_BACKGROUND, null);
                    this.blurCanvas[i].restore();
                }
                this.lastH = this.parentView.getMeasuredHeight();
                this.lastW = this.parentView.getMeasuredWidth();
                this.blurBackgroundTask.width = this.parentView.getMeasuredWidth();
                this.blurBackgroundTask.height = this.parentView.getMeasuredHeight();
                if (this.blurBackgroundTask.width == 0 || this.blurBackgroundTask.height == 0) {
                    this.processingNextFrame = false;
                    return;
                }
                if (this.queue == null) {
                    this.queue = new DispatchQueue("blur_thread_" + this);
                }
                this.queue.postRunnable(this.blurBackgroundTask);
                return;
            }
            return;
        }
        this.errorBlackoutPaint.setAlpha((int) (50.0f * alpha));
        canvas.drawPaint(this.errorBlackoutPaint);
    }

    /* renamed from: lambda$draw$0$org-telegram-ui-Components-BlurBehindDrawable */
    public /* synthetic */ void m2224lambda$draw$0$orgtelegramuiComponentsBlurBehindDrawable() {
        this.error = true;
        this.parentView.invalidate();
    }

    public int getBlurRadius() {
        return Math.max(7, Math.max(this.lastH, this.lastW) / 180);
    }

    public void clear() {
        this.invalidate = true;
        this.wasDraw = false;
        this.error = false;
        this.blurAlpha = 0.0f;
        this.lastW = 0;
        this.lastH = 0;
        DispatchQueue dispatchQueue = this.queue;
        if (dispatchQueue != null) {
            dispatchQueue.cleanupQueue();
            this.queue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.BlurBehindDrawable$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BlurBehindDrawable.this.m2223lambda$clear$2$orgtelegramuiComponentsBlurBehindDrawable();
                }
            });
        }
    }

    /* renamed from: lambda$clear$2$org-telegram-ui-Components-BlurBehindDrawable */
    public /* synthetic */ void m2223lambda$clear$2$orgtelegramuiComponentsBlurBehindDrawable() {
        Bitmap[] bitmapArr = this.renderingBitmap;
        if (bitmapArr != null) {
            if (bitmapArr[0] != null) {
                bitmapArr[0].recycle();
            }
            Bitmap[] bitmapArr2 = this.renderingBitmap;
            if (bitmapArr2[1] != null) {
                bitmapArr2[1].recycle();
            }
            this.renderingBitmap = null;
        }
        Bitmap[] bitmapArr3 = this.backgroundBitmap;
        if (bitmapArr3 != null) {
            if (bitmapArr3[0] != null) {
                bitmapArr3[0].recycle();
            }
            Bitmap[] bitmapArr4 = this.backgroundBitmap;
            if (bitmapArr4[1] != null) {
                bitmapArr4[1].recycle();
            }
            this.backgroundBitmap = null;
        }
        this.renderingBitmapCanvas = null;
        this.skipDraw = false;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BlurBehindDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BlurBehindDrawable.this.m2222lambda$clear$1$orgtelegramuiComponentsBlurBehindDrawable();
            }
        });
    }

    /* renamed from: lambda$clear$1$org-telegram-ui-Components-BlurBehindDrawable */
    public /* synthetic */ void m2222lambda$clear$1$orgtelegramuiComponentsBlurBehindDrawable() {
        DispatchQueue dispatchQueue = this.queue;
        if (dispatchQueue != null) {
            dispatchQueue.recycle();
            this.queue = null;
        }
    }

    public void invalidate() {
        this.invalidate = true;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public boolean isFullyDrawing() {
        return !this.skipDraw && this.wasDraw && (this.blurAlpha == 1.0f || !this.animateAlpha) && this.show && this.parentView.getAlpha() == 1.0f;
    }

    public void checkSizes() {
        Bitmap[] bitmap = this.renderingBitmap;
        if (bitmap == null || this.parentView.getMeasuredHeight() == 0 || this.parentView.getMeasuredWidth() == 0) {
            return;
        }
        generateBlurredBitmaps();
        this.lastH = this.parentView.getMeasuredHeight();
        this.lastW = this.parentView.getMeasuredWidth();
    }

    private void generateBlurredBitmaps() {
        Bitmap[] bitmap = this.renderingBitmap;
        if (bitmap == null) {
            Bitmap[] bitmapArr = new Bitmap[2];
            this.renderingBitmap = bitmapArr;
            bitmap = bitmapArr;
            this.renderingBitmapCanvas = new Canvas[2];
        }
        if (this.blurredBitmapTmp == null) {
            this.blurredBitmapTmp = new Bitmap[2];
            this.blurCanvas = new Canvas[2];
        }
        this.blurBackgroundTask.canceled = true;
        this.blurBackgroundTask = new BlurBackgroundTask();
        int i = 0;
        for (int i2 = 2; i < i2; i2 = 2) {
            int lastH = this.parentView.getMeasuredHeight();
            int lastW = this.parentView.getMeasuredWidth();
            int h = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(200.0f);
            this.toolbarH = h;
            if (i != 0) {
                h = lastH;
            }
            if (bitmap[i] == null || bitmap[i].getHeight() != h || bitmap[i].getWidth() != this.parentView.getMeasuredWidth()) {
                DispatchQueue dispatchQueue = this.queue;
                if (dispatchQueue != null) {
                    dispatchQueue.cleanupQueue();
                }
                this.blurredBitmapTmp[i] = Bitmap.createBitmap((int) (lastW / 6.0f), (int) (h / 6.0f), Bitmap.Config.ARGB_8888);
                if (i == 1) {
                    this.blurredBitmapTmp[i].eraseColor(getThemedColor(Theme.key_windowBackgroundWhite));
                }
                this.blurCanvas[i] = new Canvas(this.blurredBitmapTmp[i]);
                int bitmapH = (int) ((i == 0 ? this.toolbarH : lastH) / 6.0f);
                int bitmapW = (int) (lastW / 6.0f);
                this.renderingBitmap[i] = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
                this.renderingBitmapCanvas[i] = new Canvas(this.renderingBitmap[i]);
                this.renderingBitmapCanvas[i].scale(this.renderingBitmap[i].getWidth() / this.blurredBitmapTmp[i].getWidth(), this.renderingBitmap[i].getHeight() / this.blurredBitmapTmp[i].getHeight());
                this.blurCanvas[i].save();
                this.blurCanvas[i].scale(0.16666667f, 0.16666667f, 0.0f, 0.0f);
                Drawable backDrawable = this.behindView.getBackground();
                if (backDrawable == null) {
                    backDrawable = getBackgroundDrawable();
                }
                this.behindView.setTag(TAG_DRAWING_AS_BACKGROUND, Integer.valueOf(i));
                if (i == 0) {
                    this.blurCanvas[i].translate(0.0f, -this.panTranslationY);
                    this.behindView.draw(this.blurCanvas[i]);
                }
                if (i == 1) {
                    android.graphics.Rect oldBounds = backDrawable.getBounds();
                    backDrawable.setBounds(0, 0, this.behindView.getMeasuredWidth(), this.behindView.getMeasuredHeight());
                    backDrawable.draw(this.blurCanvas[i]);
                    backDrawable.setBounds(oldBounds);
                    this.behindView.draw(this.blurCanvas[i]);
                }
                this.behindView.setTag(TAG_DRAWING_AS_BACKGROUND, null);
                this.blurCanvas[i].restore();
                Utilities.stackBlurBitmap(this.blurredBitmapTmp[i], getBlurRadius());
                this.emptyPaint.setAlpha(255);
                if (i == 1) {
                    this.renderingBitmap[i].eraseColor(getThemedColor(Theme.key_windowBackgroundWhite));
                }
                this.renderingBitmapCanvas[i].drawBitmap(this.blurredBitmapTmp[i], 0.0f, 0.0f, this.emptyPaint);
            }
            i++;
        }
    }

    public void show(boolean show) {
        this.show = show;
    }

    public void setAnimateAlpha(boolean animateAlpha) {
        this.animateAlpha = animateAlpha;
    }

    public void onPanTranslationUpdate(float y) {
        this.panTranslationY = y;
        this.parentView.invalidate();
    }

    /* loaded from: classes5.dex */
    public class BlurBackgroundTask implements Runnable {
        boolean canceled;
        int height;
        int width;

        public BlurBackgroundTask() {
            BlurBehindDrawable.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (BlurBehindDrawable.this.backgroundBitmap == null) {
                BlurBehindDrawable.this.backgroundBitmap = new Bitmap[2];
                BlurBehindDrawable.this.backgroundBitmapCanvas = new Canvas[2];
            }
            int bitmapWidth = (int) (this.width / 6.0f);
            int i = 0;
            while (i < 2) {
                int h = (int) ((i == 0 ? BlurBehindDrawable.this.toolbarH : this.height) / 6.0f);
                if (BlurBehindDrawable.this.backgroundBitmap[i] != null && ((BlurBehindDrawable.this.backgroundBitmap[i].getHeight() != h || BlurBehindDrawable.this.backgroundBitmap[i].getWidth() != bitmapWidth) && BlurBehindDrawable.this.backgroundBitmap[i] != null)) {
                    BlurBehindDrawable.this.backgroundBitmap[i].recycle();
                    BlurBehindDrawable.this.backgroundBitmap[i] = null;
                }
                System.currentTimeMillis();
                if (BlurBehindDrawable.this.backgroundBitmap[i] == null) {
                    try {
                        BlurBehindDrawable.this.backgroundBitmap[i] = Bitmap.createBitmap(bitmapWidth, h, Bitmap.Config.ARGB_8888);
                        BlurBehindDrawable.this.backgroundBitmapCanvas[i] = new Canvas(BlurBehindDrawable.this.backgroundBitmap[i]);
                        BlurBehindDrawable.this.backgroundBitmapCanvas[i].scale(bitmapWidth / BlurBehindDrawable.this.blurredBitmapTmp[i].getWidth(), h / BlurBehindDrawable.this.blurredBitmapTmp[i].getHeight());
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (i == 1) {
                    BlurBehindDrawable.this.backgroundBitmap[i].eraseColor(BlurBehindDrawable.this.getThemedColor(Theme.key_windowBackgroundWhite));
                } else {
                    BlurBehindDrawable.this.backgroundBitmap[i].eraseColor(0);
                }
                BlurBehindDrawable.this.emptyPaint.setAlpha(255);
                Utilities.stackBlurBitmap(BlurBehindDrawable.this.blurredBitmapTmp[i], BlurBehindDrawable.this.getBlurRadius());
                if (BlurBehindDrawable.this.backgroundBitmapCanvas[i] != null) {
                    BlurBehindDrawable.this.backgroundBitmapCanvas[i].drawBitmap(BlurBehindDrawable.this.blurredBitmapTmp[i], 0.0f, 0.0f, BlurBehindDrawable.this.emptyPaint);
                }
                if (!this.canceled) {
                    i++;
                } else {
                    return;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BlurBehindDrawable$BlurBackgroundTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BlurBehindDrawable.BlurBackgroundTask.this.m2225xc3c2eb08();
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Components-BlurBehindDrawable$BlurBackgroundTask */
        public /* synthetic */ void m2225xc3c2eb08() {
            if (!this.canceled) {
                Bitmap[] bitmap = BlurBehindDrawable.this.renderingBitmap;
                Canvas[] canvas = BlurBehindDrawable.this.renderingBitmapCanvas;
                BlurBehindDrawable blurBehindDrawable = BlurBehindDrawable.this;
                blurBehindDrawable.renderingBitmap = blurBehindDrawable.backgroundBitmap;
                BlurBehindDrawable blurBehindDrawable2 = BlurBehindDrawable.this;
                blurBehindDrawable2.renderingBitmapCanvas = blurBehindDrawable2.backgroundBitmapCanvas;
                BlurBehindDrawable.this.backgroundBitmap = bitmap;
                BlurBehindDrawable.this.backgroundBitmapCanvas = canvas;
                BlurBehindDrawable.this.processingNextFrame = false;
                if (BlurBehindDrawable.this.parentView != null) {
                    BlurBehindDrawable.this.parentView.invalidate();
                }
            }
        }
    }

    private Drawable getBackgroundDrawable() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider instanceof ChatActivity.ThemeDelegate) {
            return ((ChatActivity.ThemeDelegate) resourcesProvider).getWallpaperDrawable();
        }
        return Theme.getCachedWallpaperNonBlocking();
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
