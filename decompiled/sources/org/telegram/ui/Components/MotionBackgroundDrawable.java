package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.lang.ref.WeakReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
/* loaded from: classes5.dex */
public class MotionBackgroundDrawable extends Drawable {
    private static final int ANIMATION_CACHE_BITMAPS_COUNT = 3;
    private static boolean errorWhileGenerateLegacyBitmap;
    private static float legacyBitmapScale;
    private static final boolean useLegacyBitmap;
    private static final boolean useSoftLight;
    private int alpha;
    private float backgroundAlpha;
    private BitmapShader bitmapShader;
    private int[] colors;
    private Bitmap currentBitmap;
    private boolean disableGradientShaderScaling;
    private boolean fastAnimation;
    private Canvas gradientCanvas;
    private GradientDrawable gradientDrawable;
    private Bitmap gradientFromBitmap;
    private Canvas gradientFromCanvas;
    private BitmapShader gradientShader;
    private Bitmap[] gradientToBitmap;
    private int intensity;
    private final CubicBezierInterpolator interpolator;
    private boolean invalidateLegacy;
    private boolean isIndeterminateAnimation;
    private boolean isPreview;
    private long lastUpdateTime;
    private Bitmap legacyBitmap;
    private Bitmap legacyBitmap2;
    private int legacyBitmapColor;
    private ColorFilter legacyBitmapColorFilter;
    private Canvas legacyCanvas;
    private Canvas legacyCanvas2;
    private Matrix matrix;
    private Paint overrideBitmapPaint;
    private Paint paint;
    private Paint paint2;
    private Paint paint3;
    private WeakReference<View> parentView;
    private float patternAlpha;
    private Bitmap patternBitmap;
    private android.graphics.Rect patternBounds;
    private ColorFilter patternColorFilter;
    private int phase;
    public float posAnimationProgress;
    private boolean postInvalidateParent;
    private RectF rect;
    private boolean rotatingPreview;
    private boolean rotationBack;
    private int roundRadius;
    private int translationY;
    private Runnable updateAnimationRunnable;

    static {
        boolean z = true;
        useLegacyBitmap = Build.VERSION.SDK_INT < 28;
        if (Build.VERSION.SDK_INT < 29) {
            z = false;
        }
        useSoftLight = z;
        errorWhileGenerateLegacyBitmap = false;
        legacyBitmapScale = 0.7f;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-MotionBackgroundDrawable */
    public /* synthetic */ void m2767lambda$new$0$orgtelegramuiComponentsMotionBackgroundDrawable() {
        updateAnimation(true);
    }

    public MotionBackgroundDrawable() {
        this.colors = new int[]{-12423849, -531317, -7888252, -133430};
        this.interpolator = new CubicBezierInterpolator(0.33d, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d);
        this.posAnimationProgress = 1.0f;
        this.rect = new RectF();
        this.gradientToBitmap = new Bitmap[3];
        this.paint = new Paint(2);
        this.paint2 = new Paint(2);
        this.paint3 = new Paint();
        this.intensity = 100;
        this.gradientDrawable = new GradientDrawable();
        this.updateAnimationRunnable = new Runnable() { // from class: org.telegram.ui.Components.MotionBackgroundDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MotionBackgroundDrawable.this.m2767lambda$new$0$orgtelegramuiComponentsMotionBackgroundDrawable();
            }
        };
        this.patternBounds = new android.graphics.Rect();
        this.patternAlpha = 1.0f;
        this.backgroundAlpha = 1.0f;
        this.alpha = 255;
        init();
    }

    public MotionBackgroundDrawable(int c1, int c2, int c3, int c4, boolean preview) {
        this(c1, c2, c3, c4, 0, preview);
    }

    public MotionBackgroundDrawable(int c1, int c2, int c3, int c4, int rotation, boolean preview) {
        this.colors = new int[]{-12423849, -531317, -7888252, -133430};
        this.interpolator = new CubicBezierInterpolator(0.33d, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d);
        this.posAnimationProgress = 1.0f;
        this.rect = new RectF();
        this.gradientToBitmap = new Bitmap[3];
        this.paint = new Paint(2);
        this.paint2 = new Paint(2);
        this.paint3 = new Paint();
        this.intensity = 100;
        this.gradientDrawable = new GradientDrawable();
        this.updateAnimationRunnable = new Runnable() { // from class: org.telegram.ui.Components.MotionBackgroundDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MotionBackgroundDrawable.this.m2767lambda$new$0$orgtelegramuiComponentsMotionBackgroundDrawable();
            }
        };
        this.patternBounds = new android.graphics.Rect();
        this.patternAlpha = 1.0f;
        this.backgroundAlpha = 1.0f;
        this.alpha = 255;
        this.isPreview = preview;
        setColors(c1, c2, c3, c4, rotation, false);
        init();
    }

    private void init() {
        this.currentBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 3; i++) {
            this.gradientToBitmap[i] = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        }
        this.gradientCanvas = new Canvas(this.currentBitmap);
        this.gradientFromBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
        this.gradientFromCanvas = new Canvas(this.gradientFromBitmap);
        Utilities.generateGradient(this.currentBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        if (useSoftLight) {
            this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
        }
    }

    public void setRoundRadius(int rad) {
        this.roundRadius = rad;
        this.matrix = new Matrix();
        BitmapShader bitmapShader = new BitmapShader(this.currentBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.bitmapShader = bitmapShader;
        this.paint.setShader(bitmapShader);
        invalidateParent();
    }

    public BitmapShader getBitmapShader() {
        return this.bitmapShader;
    }

    public Bitmap getBitmap() {
        return this.currentBitmap;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public static boolean isDark(int color1, int color2, int color3, int color4) {
        int averageColor = AndroidUtilities.getAverageColor(color1, color2);
        if (color3 != 0) {
            averageColor = AndroidUtilities.getAverageColor(averageColor, color3);
        }
        if (color4 != 0) {
            averageColor = AndroidUtilities.getAverageColor(averageColor, color4);
        }
        float[] hsb = AndroidUtilities.RGBtoHSB(Color.red(averageColor), Color.green(averageColor), Color.blue(averageColor));
        return hsb[2] < 0.3f;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(android.graphics.Rect bounds) {
        super.setBounds(bounds);
        this.patternBounds.set(bounds);
    }

    public void setPatternBounds(int left, int top, int right, int bottom) {
        this.patternBounds.set(left, top, right, bottom);
    }

    public static int getPatternColor(int color1, int color2, int color3, int color4) {
        if (isDark(color1, color2, color3, color4)) {
            return !useSoftLight ? Integer.MAX_VALUE : -1;
        } else if (!useSoftLight) {
            int averageColor = AndroidUtilities.getAverageColor(color3, AndroidUtilities.getAverageColor(color1, color2));
            if (color4 != 0) {
                averageColor = AndroidUtilities.getAverageColor(color4, averageColor);
            }
            return (AndroidUtilities.getPatternColor(averageColor, true) & ViewCompat.MEASURED_SIZE_MASK) | 1677721600;
        } else {
            return -16777216;
        }
    }

    public int getPatternColor() {
        int[] iArr = this.colors;
        return getPatternColor(iArr[0], iArr[1], iArr[2], iArr[3]);
    }

    public int getPhase() {
        return this.phase;
    }

    public void setPostInvalidateParent(boolean value) {
        this.postInvalidateParent = value;
    }

    public void rotatePreview(boolean back) {
        if (this.posAnimationProgress < 1.0f) {
            return;
        }
        this.rotatingPreview = true;
        this.posAnimationProgress = 0.0f;
        this.rotationBack = back;
        invalidateParent();
    }

    public void setPhase(int value) {
        this.phase = value;
        if (value < 0) {
            this.phase = 0;
        } else if (value > 7) {
            this.phase = 7;
        }
        Utilities.generateGradient(this.currentBitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
    }

    public void switchToNextPosition() {
        switchToNextPosition(false);
    }

    public void switchToNextPosition(boolean fast) {
        if (this.posAnimationProgress < 1.0f) {
            return;
        }
        this.rotatingPreview = false;
        this.rotationBack = false;
        this.fastAnimation = fast;
        this.posAnimationProgress = 0.0f;
        int i = this.phase - 1;
        this.phase = i;
        if (i < 0) {
            this.phase = 7;
        }
        invalidateParent();
        this.gradientFromCanvas.drawBitmap(this.currentBitmap, 0.0f, 0.0f, (Paint) null);
        generateNextGradient();
    }

    private void generateNextGradient() {
        if (useLegacyBitmap && this.intensity < 0) {
            try {
                if (this.legacyBitmap != null) {
                    Bitmap bitmap = this.legacyBitmap2;
                    if (bitmap != null && bitmap.getHeight() == this.legacyBitmap.getHeight() && this.legacyBitmap2.getWidth() == this.legacyBitmap.getWidth()) {
                        this.legacyBitmap2.eraseColor(0);
                        this.legacyCanvas2.drawBitmap(this.legacyBitmap, 0.0f, 0.0f, (Paint) null);
                    }
                    Bitmap bitmap2 = this.legacyBitmap2;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    this.legacyBitmap2 = Bitmap.createBitmap(this.legacyBitmap.getWidth(), this.legacyBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    this.legacyCanvas2 = new Canvas(this.legacyBitmap2);
                    this.legacyCanvas2.drawBitmap(this.legacyBitmap, 0.0f, 0.0f, (Paint) null);
                }
            } catch (Exception e) {
                FileLog.e(e);
                Bitmap bitmap3 = this.legacyBitmap2;
                if (bitmap3 != null) {
                    bitmap3.recycle();
                    this.legacyBitmap2 = null;
                }
            }
            Bitmap bitmap4 = this.currentBitmap;
            Utilities.generateGradient(bitmap4, true, this.phase, 1.0f, bitmap4.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
            this.invalidateLegacy = true;
        }
        for (int i = 0; i < 3; i++) {
            float p = (i + 1) / 3.0f;
            Utilities.generateGradient(this.gradientToBitmap[i], true, this.phase, p, this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        }
    }

    public void switchToPrevPosition(boolean fast) {
        if (this.posAnimationProgress < 1.0f) {
            return;
        }
        this.rotatingPreview = false;
        this.fastAnimation = fast;
        this.rotationBack = true;
        this.posAnimationProgress = 0.0f;
        invalidateParent();
        Utilities.generateGradient(this.gradientFromBitmap, true, this.phase, 0.0f, this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
        generateNextGradient();
    }

    public int[] getColors() {
        return this.colors;
    }

    public void setParentView(View view) {
        this.parentView = new WeakReference<>(view);
    }

    public void setColors(int c1, int c2, int c3, int c4) {
        setColors(c1, c2, c3, c4, 0, true);
    }

    public void setColors(int c1, int c2, int c3, int c4, Bitmap bitmap) {
        int[] iArr = this.colors;
        iArr[0] = c1;
        iArr[1] = c2;
        iArr[2] = c3;
        iArr[3] = c4;
        Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
    }

    public void setColors(int c1, int c2, int c3, int c4, int rotation, boolean invalidate) {
        if (!this.isPreview || c3 != 0 || c4 != 0) {
            this.gradientDrawable = null;
        } else {
            this.gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(rotation), new int[]{c1, c2});
        }
        int[] iArr = this.colors;
        if (iArr[0] == c1 && iArr[1] == c2 && iArr[2] == c3 && iArr[3] == c4) {
            return;
        }
        iArr[0] = c1;
        iArr[1] = c2;
        iArr[2] = c3;
        iArr[3] = c4;
        Bitmap bitmap = this.currentBitmap;
        if (bitmap != null) {
            Utilities.generateGradient(bitmap, true, this.phase, this.interpolator.getInterpolation(this.posAnimationProgress), this.currentBitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
            if (invalidate) {
                invalidateParent();
            }
        }
    }

    private void invalidateParent() {
        invalidateSelf();
        WeakReference<View> weakReference = this.parentView;
        if (weakReference != null && weakReference.get() != null) {
            this.parentView.get().invalidate();
        }
        if (this.postInvalidateParent) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.invalidateMotionBackground, new Object[0]);
            updateAnimation(false);
            AndroidUtilities.cancelRunOnUIThread(this.updateAnimationRunnable);
            AndroidUtilities.runOnUIThread(this.updateAnimationRunnable, 16L);
        }
    }

    public boolean hasPattern() {
        return this.patternBitmap != null;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        Bitmap bitmap = this.patternBitmap;
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return super.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        Bitmap bitmap = this.patternBitmap;
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return super.getIntrinsicHeight();
    }

    public void setTranslationY(int y) {
        this.translationY = y;
    }

    public void setPatternBitmap(int intensity) {
        setPatternBitmap(intensity, this.patternBitmap, true);
    }

    public void setPatternBitmap(int intensity, Bitmap bitmap) {
        setPatternBitmap(intensity, bitmap, true);
    }

    public void setPatternBitmap(int intensity, Bitmap bitmap, boolean doNotScale) {
        this.intensity = intensity;
        this.patternBitmap = bitmap;
        this.invalidateLegacy = true;
        if (bitmap == null) {
            return;
        }
        if (useSoftLight) {
            if (intensity >= 0) {
                this.paint2.setBlendMode(BlendMode.SOFT_LIGHT);
            } else {
                this.paint2.setBlendMode(null);
            }
        }
        if (intensity < 0) {
            if (!useLegacyBitmap) {
                this.bitmapShader = new BitmapShader(this.currentBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                this.gradientShader = new BitmapShader(this.patternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                this.disableGradientShaderScaling = doNotScale;
                this.paint2.setShader(new ComposeShader(this.bitmapShader, this.gradientShader, PorterDuff.Mode.DST_IN));
                this.paint2.setFilterBitmap(true);
                this.matrix = new Matrix();
                return;
            }
            createLegacyBitmap();
            if (!errorWhileGenerateLegacyBitmap) {
                this.paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            } else {
                this.paint2.setXfermode(null);
            }
        } else if (useLegacyBitmap) {
            this.paint2.setXfermode(null);
        }
    }

    public void setPatternColorFilter(int color) {
        this.patternColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        invalidateParent();
    }

    public void setPatternAlpha(float alpha) {
        this.patternAlpha = alpha;
        invalidateParent();
    }

    public void setBackgroundAlpha(float alpha) {
        this.backgroundAlpha = alpha;
        invalidateParent();
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.patternBounds.set(left, top, right, bottom);
        createLegacyBitmap();
    }

    private void createLegacyBitmap() {
        if (useLegacyBitmap && this.intensity < 0 && !errorWhileGenerateLegacyBitmap) {
            int w = (int) (this.patternBounds.width() * legacyBitmapScale);
            int h = (int) (this.patternBounds.height() * legacyBitmapScale);
            if (w <= 0 || h <= 0) {
                return;
            }
            Bitmap bitmap = this.legacyBitmap;
            if (bitmap == null || bitmap.getWidth() != w || this.legacyBitmap.getHeight() != h) {
                Bitmap bitmap2 = this.legacyBitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                Bitmap bitmap3 = this.legacyBitmap2;
                if (bitmap3 != null) {
                    bitmap3.recycle();
                    this.legacyBitmap2 = null;
                }
                try {
                    this.legacyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    this.legacyCanvas = new Canvas(this.legacyBitmap);
                    this.invalidateLegacy = true;
                } catch (Exception e) {
                    Bitmap bitmap4 = this.legacyBitmap;
                    if (bitmap4 != null) {
                        bitmap4.recycle();
                        this.legacyBitmap = null;
                    }
                    FileLog.e(e);
                    errorWhileGenerateLegacyBitmap = true;
                    this.paint2.setXfermode(null);
                }
            }
        }
    }

    public void drawBackground(Canvas canvas) {
        android.graphics.Rect bounds = getBounds();
        canvas.save();
        float tr = this.patternBitmap != null ? bounds.top : this.translationY;
        int bitmapWidth = this.currentBitmap.getWidth();
        int bitmapHeight = this.currentBitmap.getHeight();
        float w = bounds.width();
        float h = bounds.height();
        float maxScale = Math.max(w / bitmapWidth, h / bitmapHeight);
        float width = bitmapWidth * maxScale;
        float height = bitmapHeight * maxScale;
        float x = (w - width) / 2.0f;
        float y = (h - height) / 2.0f;
        if (this.isPreview) {
            x += bounds.left;
            y += bounds.top;
            canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }
        if (this.intensity < 0) {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (this.alpha * this.backgroundAlpha)));
        } else if (this.roundRadius != 0) {
            this.matrix.reset();
            this.matrix.setTranslate(x, y);
            float scaleW = this.currentBitmap.getWidth() / bounds.width();
            float scaleH = this.currentBitmap.getHeight() / bounds.height();
            float scale = 1.0f / Math.min(scaleW, scaleH);
            this.matrix.preScale(scale, scale);
            this.bitmapShader.setLocalMatrix(this.matrix);
            this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
            int wasAlpha = this.paint.getAlpha();
            this.paint.setAlpha((int) (wasAlpha * this.backgroundAlpha));
            RectF rectF = this.rect;
            int i = this.roundRadius;
            canvas.drawRoundRect(rectF, i, i, this.paint);
            this.paint.setAlpha(wasAlpha);
        } else {
            canvas.translate(0.0f, tr);
            GradientDrawable gradientDrawable = this.gradientDrawable;
            if (gradientDrawable != null) {
                gradientDrawable.setBounds((int) x, (int) y, (int) (x + width), (int) (y + height));
                this.gradientDrawable.setAlpha((int) (this.backgroundAlpha * 255.0f));
                this.gradientDrawable.draw(canvas);
            } else {
                this.rect.set(x, y, x + width, y + height);
                Paint bitmapPaint = this.overrideBitmapPaint;
                if (bitmapPaint == null) {
                    bitmapPaint = this.paint;
                }
                int wasAlpha2 = bitmapPaint.getAlpha();
                bitmapPaint.setAlpha((int) (wasAlpha2 * this.backgroundAlpha));
                canvas.drawBitmap(this.currentBitmap, (android.graphics.Rect) null, this.rect, bitmapPaint);
                bitmapPaint.setAlpha(wasAlpha2);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    public void drawPattern(Canvas canvas) {
        int bitmapWidth;
        float maxScale;
        int bitmapHeight;
        int bitmapHeight2;
        Bitmap bitmap;
        int bitmapWidth2;
        android.graphics.Rect bounds = getBounds();
        canvas.save();
        float tr = this.patternBitmap != null ? bounds.top : this.translationY;
        int bitmapWidth3 = this.currentBitmap.getWidth();
        int bitmapHeight3 = this.currentBitmap.getHeight();
        float w = bounds.width();
        float h = bounds.height();
        float maxScale2 = Math.max(w / bitmapWidth3, h / bitmapHeight3);
        float x = (w - (bitmapWidth3 * maxScale2)) / 2.0f;
        float y = (h - (bitmapHeight3 * maxScale2)) / 2.0f;
        if (!this.isPreview) {
            bitmapWidth = bitmapWidth3;
        } else {
            x += bounds.left;
            y += bounds.top;
            bitmapWidth = bitmapWidth3;
            canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }
        if (this.intensity >= 0) {
            bitmapHeight = bitmapHeight3;
            maxScale = maxScale2;
            Bitmap bitmap2 = this.patternBitmap;
            if (bitmap2 != null) {
                int bitmapWidth4 = bitmap2.getWidth();
                int bitmapHeight4 = this.patternBitmap.getHeight();
                float maxScale3 = Math.max(w / bitmapWidth4, h / bitmapHeight4);
                float width = bitmapWidth4 * maxScale3;
                float height = bitmapHeight4 * maxScale3;
                float x2 = (w - width) / 2.0f;
                float y2 = (h - height) / 2.0f;
                this.rect.set(x2, y2, x2 + width, y2 + height);
                this.paint2.setColorFilter(this.patternColorFilter);
                this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * this.alpha * this.patternAlpha));
                canvas.drawBitmap(this.patternBitmap, (android.graphics.Rect) null, this.rect, this.paint2);
            }
        } else {
            Bitmap bitmap3 = this.patternBitmap;
            if (bitmap3 == null) {
                bitmapHeight = bitmapHeight3;
                maxScale = maxScale2;
            } else if (!useLegacyBitmap) {
                if (this.matrix == null) {
                    this.matrix = new Matrix();
                }
                this.matrix.reset();
                this.matrix.setTranslate(x, y + tr);
                float scaleW = this.currentBitmap.getWidth() / bounds.width();
                float scaleH = this.currentBitmap.getHeight() / bounds.height();
                float scale = 1.0f / Math.min(scaleW, scaleH);
                this.matrix.preScale(scale, scale);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.matrix.reset();
                int bitmapWidth5 = this.patternBitmap.getWidth();
                float scaleW2 = this.patternBitmap.getHeight();
                float maxScale4 = Math.max(w / bitmapWidth5, h / scaleW2);
                this.matrix.setTranslate((int) ((w - (bitmapWidth5 * maxScale4)) / 2.0f), (int) (((h - (bitmapHeight2 * maxScale4)) / 2.0f) + tr));
                if (!this.disableGradientShaderScaling || maxScale4 > 1.4f || maxScale4 < 0.8f) {
                    this.matrix.preScale(maxScale4, maxScale4);
                }
                this.gradientShader.setLocalMatrix(this.matrix);
                this.paint2.setColorFilter(null);
                this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * this.alpha * this.patternAlpha));
                this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                RectF rectF = this.rect;
                int i = this.roundRadius;
                canvas.drawRoundRect(rectF, i, i, this.paint2);
            } else if (!errorWhileGenerateLegacyBitmap) {
                if (this.legacyBitmap == null) {
                    bitmapHeight = bitmapHeight3;
                    maxScale = maxScale2;
                } else {
                    if (this.invalidateLegacy) {
                        this.rect.set(0.0f, 0.0f, bitmap.getWidth(), this.legacyBitmap.getHeight());
                        int oldAlpha = this.paint.getAlpha();
                        this.paint.setAlpha(255);
                        this.legacyCanvas.drawBitmap(this.currentBitmap, (android.graphics.Rect) null, this.rect, this.paint);
                        this.paint.setAlpha(oldAlpha);
                        int bitmapWidth6 = this.patternBitmap.getWidth();
                        int bitmapHeight5 = this.patternBitmap.getHeight();
                        float maxScale5 = Math.max(w / bitmapWidth6, h / bitmapHeight5);
                        float width2 = bitmapWidth6 * maxScale5;
                        float height2 = bitmapHeight5 * maxScale5;
                        float x3 = (w - width2) / 2.0f;
                        float y3 = (h - height2) / 2.0f;
                        this.rect.set(x3, y3, x3 + width2, y3 + height2);
                        this.paint2.setColorFilter(null);
                        this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * 255.0f));
                        this.legacyCanvas.save();
                        Canvas canvas2 = this.legacyCanvas;
                        float f = legacyBitmapScale;
                        canvas2.scale(f, f);
                        this.legacyCanvas.drawBitmap(this.patternBitmap, (android.graphics.Rect) null, this.rect, this.paint2);
                        this.legacyCanvas.restore();
                        this.invalidateLegacy = false;
                        bitmapWidth2 = bitmapWidth6;
                        bitmapHeight3 = bitmapHeight5;
                    } else {
                        bitmapWidth2 = bitmapWidth;
                    }
                    this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                    if (this.legacyBitmap2 != null) {
                        float f2 = this.posAnimationProgress;
                        if (f2 != 1.0f) {
                            this.paint.setAlpha((int) (this.alpha * this.patternAlpha * (1.0f - f2)));
                            canvas.drawBitmap(this.legacyBitmap2, (android.graphics.Rect) null, this.rect, this.paint);
                            this.paint.setAlpha((int) (this.alpha * this.patternAlpha * this.posAnimationProgress));
                            canvas.drawBitmap(this.legacyBitmap, (android.graphics.Rect) null, this.rect, this.paint);
                            this.paint.setAlpha(this.alpha);
                        }
                    }
                    canvas.drawBitmap(this.legacyBitmap, (android.graphics.Rect) null, this.rect, this.paint);
                }
            } else {
                int bitmapWidth7 = bitmap3.getWidth();
                int bitmapHeight6 = this.patternBitmap.getHeight();
                float maxScale6 = Math.max(w / bitmapWidth7, h / bitmapHeight6);
                float width3 = bitmapWidth7 * maxScale6;
                float height3 = bitmapHeight6 * maxScale6;
                float x4 = (w - width3) / 2.0f;
                float y4 = (h - height3) / 2.0f;
                this.rect.set(x4, y4, x4 + width3, y4 + height3);
                int[] iArr = this.colors;
                int averageColor = AndroidUtilities.getAverageColor(iArr[2], AndroidUtilities.getAverageColor(iArr[0], iArr[1]));
                int[] iArr2 = this.colors;
                if (iArr2[3] != 0) {
                    averageColor = AndroidUtilities.getAverageColor(iArr2[3], averageColor);
                }
                if (this.legacyBitmapColorFilter == null || averageColor != this.legacyBitmapColor) {
                    this.legacyBitmapColor = averageColor;
                    this.legacyBitmapColorFilter = new PorterDuffColorFilter(averageColor, PorterDuff.Mode.SRC_IN);
                }
                this.paint2.setColorFilter(this.legacyBitmapColorFilter);
                this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * this.alpha * this.patternAlpha));
                canvas.translate(0.0f, tr);
                canvas.drawBitmap(this.patternBitmap, (android.graphics.Rect) null, this.rect, this.paint2);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int bitmapWidth;
        float maxScale;
        int bitmapHeight;
        int bitmapHeight2;
        Bitmap bitmap;
        int bitmapWidth2;
        android.graphics.Rect bounds = getBounds();
        canvas.save();
        float tr = this.patternBitmap != null ? bounds.top : this.translationY;
        int bitmapWidth3 = this.currentBitmap.getWidth();
        int bitmapHeight3 = this.currentBitmap.getHeight();
        float w = bounds.width();
        float h = bounds.height();
        float maxScale2 = Math.max(w / bitmapWidth3, h / bitmapHeight3);
        float width = bitmapWidth3 * maxScale2;
        float height = bitmapHeight3 * maxScale2;
        float x = (w - width) / 2.0f;
        float y = (h - height) / 2.0f;
        if (!this.isPreview) {
            bitmapWidth = bitmapWidth3;
        } else {
            x += bounds.left;
            y += bounds.top;
            bitmapWidth = bitmapWidth3;
            canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }
        if (this.intensity < 0) {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (this.alpha * this.backgroundAlpha)));
            Bitmap bitmap2 = this.patternBitmap;
            if (bitmap2 == null) {
                bitmapHeight = bitmapHeight3;
                maxScale = maxScale2;
            } else if (!useLegacyBitmap) {
                if (this.matrix == null) {
                    this.matrix = new Matrix();
                }
                this.matrix.reset();
                this.matrix.setTranslate(x, y + tr);
                float scaleW = this.currentBitmap.getWidth() / bounds.width();
                float scaleH = this.currentBitmap.getHeight() / bounds.height();
                float scale = 1.0f / Math.min(scaleW, scaleH);
                this.matrix.preScale(scale, scale);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.matrix.reset();
                int bitmapWidth4 = this.patternBitmap.getWidth();
                float scaleW2 = this.patternBitmap.getHeight();
                float maxScale3 = Math.max(w / bitmapWidth4, h / scaleW2);
                this.matrix.setTranslate((int) ((w - (bitmapWidth4 * maxScale3)) / 2.0f), (int) (((h - (bitmapHeight2 * maxScale3)) / 2.0f) + tr));
                if (!this.disableGradientShaderScaling || maxScale3 > 1.4f || maxScale3 < 0.8f) {
                    this.matrix.preScale(maxScale3, maxScale3);
                }
                this.gradientShader.setLocalMatrix(this.matrix);
                this.paint2.setColorFilter(null);
                this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * this.alpha * this.patternAlpha));
                this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                RectF rectF = this.rect;
                int i = this.roundRadius;
                canvas.drawRoundRect(rectF, i, i, this.paint2);
            } else if (!errorWhileGenerateLegacyBitmap) {
                if (this.legacyBitmap == null) {
                    bitmapHeight = bitmapHeight3;
                    maxScale = maxScale2;
                } else {
                    if (this.invalidateLegacy) {
                        this.rect.set(0.0f, 0.0f, bitmap.getWidth(), this.legacyBitmap.getHeight());
                        int oldAlpha = this.paint.getAlpha();
                        this.paint.setAlpha(255);
                        this.legacyCanvas.drawBitmap(this.currentBitmap, (android.graphics.Rect) null, this.rect, this.paint);
                        this.paint.setAlpha(oldAlpha);
                        int bitmapWidth5 = this.patternBitmap.getWidth();
                        int bitmapHeight4 = this.patternBitmap.getHeight();
                        float maxScale4 = Math.max(w / bitmapWidth5, h / bitmapHeight4);
                        float width2 = bitmapWidth5 * maxScale4;
                        float height2 = bitmapHeight4 * maxScale4;
                        float x2 = (w - width2) / 2.0f;
                        float y2 = (h - height2) / 2.0f;
                        this.rect.set(x2, y2, x2 + width2, y2 + height2);
                        this.paint2.setColorFilter(null);
                        this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * 255.0f));
                        this.legacyCanvas.save();
                        Canvas canvas2 = this.legacyCanvas;
                        float f = legacyBitmapScale;
                        canvas2.scale(f, f);
                        this.legacyCanvas.drawBitmap(this.patternBitmap, (android.graphics.Rect) null, this.rect, this.paint2);
                        this.legacyCanvas.restore();
                        this.invalidateLegacy = false;
                        bitmapWidth2 = bitmapWidth5;
                        bitmapHeight3 = bitmapHeight4;
                    } else {
                        bitmapWidth2 = bitmapWidth;
                    }
                    this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                    if (this.legacyBitmap2 != null) {
                        float f2 = this.posAnimationProgress;
                        if (f2 != 1.0f) {
                            this.paint.setAlpha((int) (this.alpha * this.patternAlpha * (1.0f - f2)));
                            canvas.drawBitmap(this.legacyBitmap2, (android.graphics.Rect) null, this.rect, this.paint);
                            this.paint.setAlpha((int) (this.alpha * this.patternAlpha * this.posAnimationProgress));
                            canvas.drawBitmap(this.legacyBitmap, (android.graphics.Rect) null, this.rect, this.paint);
                            this.paint.setAlpha(this.alpha);
                        }
                    }
                    canvas.drawBitmap(this.legacyBitmap, (android.graphics.Rect) null, this.rect, this.paint);
                }
            } else {
                int bitmapWidth6 = bitmap2.getWidth();
                int bitmapHeight5 = this.patternBitmap.getHeight();
                float maxScale5 = Math.max(w / bitmapWidth6, h / bitmapHeight5);
                float width3 = bitmapWidth6 * maxScale5;
                float height3 = bitmapHeight5 * maxScale5;
                float x3 = (w - width3) / 2.0f;
                float y3 = (h - height3) / 2.0f;
                this.rect.set(x3, y3, x3 + width3, y3 + height3);
                int[] iArr = this.colors;
                int averageColor = AndroidUtilities.getAverageColor(iArr[2], AndroidUtilities.getAverageColor(iArr[0], iArr[1]));
                int[] iArr2 = this.colors;
                if (iArr2[3] != 0) {
                    averageColor = AndroidUtilities.getAverageColor(iArr2[3], averageColor);
                }
                if (this.legacyBitmapColorFilter == null || averageColor != this.legacyBitmapColor) {
                    this.legacyBitmapColor = averageColor;
                    this.legacyBitmapColorFilter = new PorterDuffColorFilter(averageColor, PorterDuff.Mode.SRC_IN);
                }
                this.paint2.setColorFilter(this.legacyBitmapColorFilter);
                this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * this.alpha * this.patternAlpha));
                canvas.translate(0.0f, tr);
                canvas.drawBitmap(this.patternBitmap, (android.graphics.Rect) null, this.rect, this.paint2);
            }
        } else {
            bitmapHeight = bitmapHeight3;
            maxScale = maxScale2;
            if (this.roundRadius != 0) {
                this.matrix.reset();
                this.matrix.setTranslate(x, y);
                float scaleW3 = this.currentBitmap.getWidth() / bounds.width();
                float scaleH2 = this.currentBitmap.getHeight() / bounds.height();
                float scale2 = 1.0f / Math.min(scaleW3, scaleH2);
                this.matrix.preScale(scale2, scale2);
                this.bitmapShader.setLocalMatrix(this.matrix);
                this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                RectF rectF2 = this.rect;
                int i2 = this.roundRadius;
                canvas.drawRoundRect(rectF2, i2, i2, this.paint);
            } else {
                canvas.translate(0.0f, tr);
                GradientDrawable gradientDrawable = this.gradientDrawable;
                if (gradientDrawable != null) {
                    gradientDrawable.setBounds((int) x, (int) y, (int) (x + width), (int) (y + height));
                    this.gradientDrawable.setAlpha((int) (this.backgroundAlpha * 255.0f));
                    this.gradientDrawable.draw(canvas);
                } else {
                    this.rect.set(x, y, x + width, y + height);
                    Paint bitmapPaint = this.overrideBitmapPaint;
                    if (bitmapPaint == null) {
                        bitmapPaint = this.paint;
                    }
                    int wasAlpha = bitmapPaint.getAlpha();
                    bitmapPaint.setAlpha((int) (wasAlpha * this.backgroundAlpha));
                    canvas.drawBitmap(this.currentBitmap, (android.graphics.Rect) null, this.rect, bitmapPaint);
                    bitmapPaint.setAlpha(wasAlpha);
                }
            }
            Bitmap bitmap3 = this.patternBitmap;
            if (bitmap3 != null) {
                int bitmapWidth7 = bitmap3.getWidth();
                int bitmapHeight6 = this.patternBitmap.getHeight();
                float maxScale6 = Math.max(w / bitmapWidth7, h / bitmapHeight6);
                float width4 = bitmapWidth7 * maxScale6;
                float height4 = bitmapHeight6 * maxScale6;
                float x4 = (w - width4) / 2.0f;
                float y4 = (h - height4) / 2.0f;
                this.rect.set(x4, y4, x4 + width4, y4 + height4);
                this.paint2.setColorFilter(this.patternColorFilter);
                this.paint2.setAlpha((int) ((Math.abs(this.intensity) / 100.0f) * this.alpha * this.patternAlpha));
                canvas.drawBitmap(this.patternBitmap, (android.graphics.Rect) null, this.rect, this.paint2);
            }
        }
        canvas.restore();
        updateAnimation(true);
    }

    public void updateAnimation(boolean invalidate) {
        float progress;
        int stageBefore;
        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - this.lastUpdateTime;
        if (dt > 20) {
            dt = 17;
        }
        this.lastUpdateTime = newTime;
        if (dt <= 1) {
            return;
        }
        boolean z = this.isIndeterminateAnimation;
        if (z && this.posAnimationProgress == 1.0f) {
            this.posAnimationProgress = 0.0f;
        }
        float f = this.posAnimationProgress;
        if (f < 1.0f) {
            boolean isNeedGenerateGradient = this.postInvalidateParent || this.rotatingPreview;
            if (z) {
                float f2 = f + (((float) dt) / 12000.0f);
                this.posAnimationProgress = f2;
                if (f2 >= 1.0f) {
                    this.posAnimationProgress = 0.0f;
                }
                float f3 = this.posAnimationProgress;
                int i = (int) (f3 / 0.125f);
                this.phase = i;
                progress = 1.0f - ((f3 - (i * 0.125f)) / 0.125f);
                isNeedGenerateGradient = true;
            } else if (!this.rotatingPreview) {
                float f4 = f + (((float) dt) / (this.fastAnimation ? 300.0f : 500.0f));
                this.posAnimationProgress = f4;
                if (f4 > 1.0f) {
                    this.posAnimationProgress = 1.0f;
                }
                progress = this.interpolator.getInterpolation(this.posAnimationProgress);
                if (this.rotationBack) {
                    progress = 1.0f - progress;
                    if (this.posAnimationProgress >= 1.0f) {
                        int i2 = this.phase + 1;
                        this.phase = i2;
                        if (i2 > 7) {
                            this.phase = 0;
                        }
                        progress = 1.0f;
                    }
                }
            } else {
                float progressBefore = this.interpolator.getInterpolation(f);
                if (progressBefore <= 0.25f) {
                    stageBefore = 0;
                } else if (progressBefore <= 0.5f) {
                    stageBefore = 1;
                } else if (progressBefore <= 0.75f) {
                    stageBefore = 2;
                } else {
                    stageBefore = 3;
                }
                float f5 = this.posAnimationProgress + (((float) dt) / (this.rotationBack ? 1000.0f : 2000.0f));
                this.posAnimationProgress = f5;
                if (f5 > 1.0f) {
                    this.posAnimationProgress = 1.0f;
                }
                float progress2 = this.interpolator.getInterpolation(this.posAnimationProgress);
                if ((stageBefore == 0 && progress2 > 0.25f) || ((stageBefore == 1 && progress2 > 0.5f) || (stageBefore == 2 && progress2 > 0.75f))) {
                    if (this.rotationBack) {
                        int i3 = this.phase + 1;
                        this.phase = i3;
                        if (i3 > 7) {
                            this.phase = 0;
                        }
                    } else {
                        int i4 = this.phase - 1;
                        this.phase = i4;
                        if (i4 < 0) {
                            this.phase = 7;
                        }
                    }
                }
                if (progress2 <= 0.25f) {
                    progress = progress2 / 0.25f;
                } else if (progress2 <= 0.5f) {
                    progress = (progress2 - 0.25f) / 0.25f;
                } else if (progress2 <= 0.75f) {
                    progress = (progress2 - 0.5f) / 0.25f;
                } else {
                    progress = (progress2 - 0.75f) / 0.25f;
                }
                if (this.rotationBack) {
                    progress = 1.0f - progress;
                    if (this.posAnimationProgress >= 1.0f) {
                        int i5 = this.phase + 1;
                        this.phase = i5;
                        if (i5 > 7) {
                            this.phase = 0;
                        }
                        progress = 1.0f;
                    }
                }
            }
            if (isNeedGenerateGradient) {
                Bitmap bitmap = this.currentBitmap;
                Utilities.generateGradient(bitmap, true, this.phase, progress, bitmap.getWidth(), this.currentBitmap.getHeight(), this.currentBitmap.getRowBytes(), this.colors);
                this.invalidateLegacy = true;
            } else if (!useLegacyBitmap || this.intensity >= 0) {
                if (progress != 1.0f) {
                    int i6 = (int) (progress / 0.33333334f);
                    if (i6 == 0) {
                        this.gradientCanvas.drawBitmap(this.gradientFromBitmap, 0.0f, 0.0f, (Paint) null);
                    } else {
                        this.gradientCanvas.drawBitmap(this.gradientToBitmap[i6 - 1], 0.0f, 0.0f, (Paint) null);
                    }
                    float alpha = (progress - (i6 * 0.33333334f)) / 0.33333334f;
                    this.paint3.setAlpha((int) (255.0f * alpha));
                    this.gradientCanvas.drawBitmap(this.gradientToBitmap[i6], 0.0f, 0.0f, this.paint3);
                } else {
                    this.gradientCanvas.drawBitmap(this.gradientToBitmap[2], 0.0f, 0.0f, this.paint3);
                }
            }
            if (invalidate) {
                invalidateParent();
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        this.paint.setAlpha(alpha);
        this.paint2.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    public boolean isOneColor() {
        int[] iArr = this.colors;
        return iArr[0] == iArr[1] && iArr[0] == iArr[2] && iArr[0] == iArr[3];
    }

    public void setIndeterminateAnimation(boolean isIndeterminateAnimation) {
        this.isIndeterminateAnimation = isIndeterminateAnimation;
    }

    public void setOverrideBitmapPaint(Paint overrideBitmapPaint) {
        this.overrideBitmapPaint = overrideBitmapPaint;
    }
}
