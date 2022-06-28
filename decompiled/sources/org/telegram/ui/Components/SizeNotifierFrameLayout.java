package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BlurSettingsBottomSheet;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.WallpaperParallaxEffect;
/* loaded from: classes5.dex */
public class SizeNotifierFrameLayout extends FrameLayout {
    private static DispatchQueue blurQueue;
    private final float DOWN_SCALE;
    private final int TOP_CLIP_OFFSET;
    protected AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean animationInProgress;
    private Drawable backgroundDrawable;
    private int backgroundTranslationY;
    protected View backgroundView;
    private float bgAngle;
    final BlurBackgroundTask blurBackgroundTask;
    public ArrayList<View> blurBehindViews;
    ValueAnimator blurCrossfade;
    public float blurCrossfadeProgress;
    public boolean blurGeneratingTuskIsRunning;
    public boolean blurIsRunning;
    public Paint blurPaintBottom;
    public Paint blurPaintBottom2;
    public Paint blurPaintTop;
    public Paint blurPaintTop2;
    private int bottomClip;
    int count;
    int count2;
    BlurBitmap currentBitmap;
    private SizeNotifierFrameLayoutDelegate delegate;
    private int emojiHeight;
    private float emojiOffset;
    public boolean invalidateBlur;
    protected int keyboardHeight;
    Matrix matrix;
    Matrix matrix2;
    public boolean needBlur;
    public boolean needBlurBottom;
    private boolean occupyStatusBar;
    private Drawable oldBackgroundDrawable;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private ActionBarLayout parentLayout;
    private boolean paused;
    BlurBitmap prevBitmap;
    private android.graphics.Rect rect;
    float saturation;
    private Paint selectedBlurPaint;
    private Paint selectedBlurPaint2;
    private boolean skipBackgroundDrawing;
    SnowflakesEffect snowflakesEffect;
    int times;
    int times2;
    private float translationX;
    private float translationY;
    public ArrayList<BlurBitmap> unusedBitmaps;

    /* loaded from: classes5.dex */
    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public void invalidateBlur() {
        this.invalidateBlur = true;
        invalidate();
    }

    public SizeNotifierFrameLayout(Context context) {
        this(context, null);
    }

    public SizeNotifierFrameLayout(Context context, ActionBarLayout layout) {
        super(context);
        this.rect = new android.graphics.Rect();
        this.occupyStatusBar = true;
        this.parallaxScale = 1.0f;
        this.paused = true;
        this.unusedBitmaps = new ArrayList<>(10);
        this.blurBehindViews = new ArrayList<>();
        this.matrix = new Matrix();
        this.matrix2 = new Matrix();
        this.blurPaintTop = new Paint();
        this.blurPaintTop2 = new Paint();
        this.blurPaintBottom = new Paint();
        this.blurPaintBottom2 = new Paint();
        this.DOWN_SCALE = 12.0f;
        this.TOP_CLIP_OFFSET = 34;
        this.blurBackgroundTask = new BlurBackgroundTask();
        setWillNotDraw(false);
        this.parentLayout = layout;
        this.adjustPanLayoutHelper = createAdjustPanLayoutHelper();
        View view = new View(context) { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout.1
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                Drawable newDrawable;
                int a;
                int i;
                if (SizeNotifierFrameLayout.this.backgroundDrawable == null || SizeNotifierFrameLayout.this.skipBackgroundDrawing) {
                    return;
                }
                Drawable newDrawable2 = SizeNotifierFrameLayout.this.getNewDrawable();
                if (newDrawable2 != SizeNotifierFrameLayout.this.backgroundDrawable && newDrawable2 != null) {
                    if (Theme.isAnimatingColor()) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
                        sizeNotifierFrameLayout.oldBackgroundDrawable = sizeNotifierFrameLayout.backgroundDrawable;
                    }
                    if (newDrawable2 instanceof MotionBackgroundDrawable) {
                        ((MotionBackgroundDrawable) newDrawable2).setParentView(SizeNotifierFrameLayout.this.backgroundView);
                    }
                    SizeNotifierFrameLayout.this.backgroundDrawable = newDrawable2;
                }
                float themeAnimationValue = SizeNotifierFrameLayout.this.parentLayout != null ? SizeNotifierFrameLayout.this.parentLayout.getThemeAnimationValue() : 1.0f;
                int a2 = 0;
                while (a2 < 2) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout2 = SizeNotifierFrameLayout.this;
                    Drawable drawable = a2 == 0 ? sizeNotifierFrameLayout2.oldBackgroundDrawable : sizeNotifierFrameLayout2.backgroundDrawable;
                    if (drawable == null) {
                        newDrawable = newDrawable2;
                        a = a2;
                    } else {
                        if (a2 == 1 && SizeNotifierFrameLayout.this.oldBackgroundDrawable != null && SizeNotifierFrameLayout.this.parentLayout != null) {
                            drawable.setAlpha((int) (255.0f * themeAnimationValue));
                        } else {
                            drawable.setAlpha(255);
                        }
                        if (drawable instanceof MotionBackgroundDrawable) {
                            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                            if (motionBackgroundDrawable.hasPattern()) {
                                int actionBarHeight = (SizeNotifierFrameLayout.this.isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !SizeNotifierFrameLayout.this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                                int viewHeight = getRootView().getMeasuredHeight() - actionBarHeight;
                                float scaleX = getMeasuredWidth() / drawable.getIntrinsicWidth();
                                float scaleY = viewHeight / drawable.getIntrinsicHeight();
                                float scale = Math.max(scaleX, scaleY);
                                int width = (int) Math.ceil(drawable.getIntrinsicWidth() * scale * SizeNotifierFrameLayout.this.parallaxScale);
                                a = a2;
                                int height = (int) Math.ceil(drawable.getIntrinsicHeight() * scale * SizeNotifierFrameLayout.this.parallaxScale);
                                int x = ((getMeasuredWidth() - width) / 2) + ((int) SizeNotifierFrameLayout.this.translationX);
                                int y = SizeNotifierFrameLayout.this.backgroundTranslationY + ((viewHeight - height) / 2) + actionBarHeight + ((int) SizeNotifierFrameLayout.this.translationY);
                                canvas.save();
                                newDrawable = newDrawable2;
                                canvas.clipRect(0, actionBarHeight, width, getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                drawable.setBounds(x, y, x + width, y + height);
                                drawable.draw(canvas);
                                SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                canvas.restore();
                            } else {
                                newDrawable = newDrawable2;
                                a = a2;
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.save();
                                    canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                }
                                motionBackgroundDrawable.setTranslationY(SizeNotifierFrameLayout.this.backgroundTranslationY);
                                int bottom = (int) ((getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.backgroundTranslationY) + SizeNotifierFrameLayout.this.translationY);
                                if (SizeNotifierFrameLayout.this.animationInProgress) {
                                    bottom = (int) (bottom - SizeNotifierFrameLayout.this.emojiOffset);
                                } else if (SizeNotifierFrameLayout.this.emojiHeight != 0) {
                                    bottom -= SizeNotifierFrameLayout.this.emojiHeight;
                                }
                                drawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                                drawable.draw(canvas);
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.restore();
                                }
                            }
                        } else {
                            newDrawable = newDrawable2;
                            a = a2;
                            if (drawable instanceof ColorDrawable) {
                                if (SizeNotifierFrameLayout.this.bottomClip == 0) {
                                    i = 0;
                                } else {
                                    canvas.save();
                                    i = 0;
                                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                }
                                drawable.setBounds(i, i, getMeasuredWidth(), getRootView().getMeasuredHeight());
                                drawable.draw(canvas);
                                SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.restore();
                                }
                            } else if (drawable instanceof GradientDrawable) {
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.save();
                                    canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                }
                                drawable.setBounds(0, SizeNotifierFrameLayout.this.backgroundTranslationY, getMeasuredWidth(), SizeNotifierFrameLayout.this.backgroundTranslationY + getRootView().getMeasuredHeight());
                                drawable.draw(canvas);
                                SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                if (SizeNotifierFrameLayout.this.bottomClip != 0) {
                                    canvas.restore();
                                }
                            } else if (drawable instanceof BitmapDrawable) {
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                if (bitmapDrawable.getTileModeX() != Shader.TileMode.REPEAT) {
                                    int actionBarHeight2 = (SizeNotifierFrameLayout.this.isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !SizeNotifierFrameLayout.this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
                                    int viewHeight2 = getRootView().getMeasuredHeight() - actionBarHeight2;
                                    float scaleX2 = getMeasuredWidth() / drawable.getIntrinsicWidth();
                                    float scaleY2 = viewHeight2 / drawable.getIntrinsicHeight();
                                    float scale2 = Math.max(scaleX2, scaleY2);
                                    int width2 = (int) Math.ceil(drawable.getIntrinsicWidth() * scale2 * SizeNotifierFrameLayout.this.parallaxScale);
                                    int height2 = (int) Math.ceil(drawable.getIntrinsicHeight() * scale2 * SizeNotifierFrameLayout.this.parallaxScale);
                                    int x2 = ((getMeasuredWidth() - width2) / 2) + ((int) SizeNotifierFrameLayout.this.translationX);
                                    int y2 = SizeNotifierFrameLayout.this.backgroundTranslationY + ((viewHeight2 - height2) / 2) + actionBarHeight2 + ((int) SizeNotifierFrameLayout.this.translationY);
                                    canvas.save();
                                    canvas.clipRect(0, actionBarHeight2, width2, getMeasuredHeight() - SizeNotifierFrameLayout.this.bottomClip);
                                    drawable.setBounds(x2, y2, x2 + width2, y2 + height2);
                                    drawable.draw(canvas);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                    canvas.restore();
                                } else {
                                    canvas.save();
                                    float scale3 = 2.0f / AndroidUtilities.density;
                                    canvas.scale(scale3, scale3);
                                    drawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale3), (int) Math.ceil(getRootView().getMeasuredHeight() / scale3));
                                    drawable.draw(canvas);
                                    SizeNotifierFrameLayout.this.checkSnowflake(canvas);
                                    canvas.restore();
                                }
                            }
                        }
                        if (a == 0 && SizeNotifierFrameLayout.this.oldBackgroundDrawable != null) {
                            if (themeAnimationValue >= 1.0f) {
                                SizeNotifierFrameLayout.this.oldBackgroundDrawable = null;
                                SizeNotifierFrameLayout.this.backgroundView.invalidate();
                            }
                        }
                    }
                    a2 = a + 1;
                    newDrawable2 = newDrawable;
                }
            }
        };
        this.backgroundView = view;
        addView(view, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void setBackgroundImage(Drawable bitmap, boolean motion) {
        if (this.backgroundDrawable == bitmap) {
            return;
        }
        if (bitmap instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) bitmap;
            motionBackgroundDrawable.setParentView(this.backgroundView);
        }
        this.backgroundDrawable = bitmap;
        if (motion) {
            if (this.parallaxEffect == null) {
                WallpaperParallaxEffect wallpaperParallaxEffect = new WallpaperParallaxEffect(getContext());
                this.parallaxEffect = wallpaperParallaxEffect;
                wallpaperParallaxEffect.setCallback(new WallpaperParallaxEffect.Callback() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.WallpaperParallaxEffect.Callback
                    public final void onOffsetsChanged(int i, int i2, float f) {
                        SizeNotifierFrameLayout.this.m3060x49b6021f(i, i2, f);
                    }
                });
                if (getMeasuredWidth() != 0 && getMeasuredHeight() != 0) {
                    this.parallaxScale = this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                }
            }
            if (!this.paused) {
                this.parallaxEffect.setEnabled(true);
            }
        } else {
            WallpaperParallaxEffect wallpaperParallaxEffect2 = this.parallaxEffect;
            if (wallpaperParallaxEffect2 != null) {
                wallpaperParallaxEffect2.setEnabled(false);
                this.parallaxEffect = null;
                this.parallaxScale = 1.0f;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
            }
        }
        this.backgroundView.invalidate();
    }

    /* renamed from: lambda$setBackgroundImage$0$org-telegram-ui-Components-SizeNotifierFrameLayout */
    public /* synthetic */ void m3060x49b6021f(int offsetX, int offsetY, float angle) {
        this.translationX = offsetX;
        this.translationY = offsetY;
        this.bgAngle = angle;
        this.backgroundView.invalidate();
    }

    public Drawable getBackgroundImage() {
        return this.backgroundDrawable;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate delegate) {
        this.delegate = delegate;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
    }

    public void onPause() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            wallpaperParallaxEffect.setEnabled(false);
        }
        this.paused = true;
    }

    public void onResume() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            wallpaperParallaxEffect.setEnabled(true);
        }
        this.paused = false;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int measureKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        if (this.rect.bottom == 0 && this.rect.top == 0) {
            return 0;
        }
        int usableViewHeight = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
        int max = Math.max(0, usableViewHeight - (this.rect.bottom - this.rect.top));
        this.keyboardHeight = max;
        return max;
    }

    public int getKeyboardHeight() {
        return this.keyboardHeight;
    }

    public void notifyHeightChanged() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            this.parallaxScale = wallpaperParallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
        }
        if (this.delegate != null) {
            this.keyboardHeight = measureKeyboardHeight();
            final boolean isWidthGreater = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
            post(new Runnable() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SizeNotifierFrameLayout.this.m3059xe23b7f7f(isWidthGreater);
                }
            });
        }
    }

    /* renamed from: lambda$notifyHeightChanged$1$org-telegram-ui-Components-SizeNotifierFrameLayout */
    public /* synthetic */ void m3059xe23b7f7f(boolean isWidthGreater) {
        SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate = this.delegate;
        if (sizeNotifierFrameLayoutDelegate != null) {
            sizeNotifierFrameLayoutDelegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }

    public void setBottomClip(int value) {
        if (value != this.bottomClip) {
            this.bottomClip = value;
            this.backgroundView.invalidate();
        }
    }

    public void setBackgroundTranslation(int translation) {
        if (translation != this.backgroundTranslationY) {
            this.backgroundTranslationY = translation;
            this.backgroundView.invalidate();
        }
    }

    public int getBackgroundTranslationY() {
        if (this.backgroundDrawable instanceof MotionBackgroundDrawable) {
            if (this.animationInProgress) {
                return (int) this.emojiOffset;
            }
            int i = this.emojiHeight;
            if (i != 0) {
                return i;
            }
            return this.backgroundTranslationY;
        }
        return 0;
    }

    public int getBackgroundSizeY() {
        int offset = 0;
        Drawable drawable = this.backgroundDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
            if (!motionBackgroundDrawable.hasPattern()) {
                if (this.animationInProgress) {
                    offset = (int) this.emojiOffset;
                } else if (this.emojiHeight != 0) {
                    offset = this.emojiHeight;
                } else {
                    offset = this.backgroundTranslationY;
                }
            } else {
                offset = this.backgroundTranslationY != 0 ? 0 : -this.keyboardHeight;
            }
        }
        return getMeasuredHeight() - offset;
    }

    public int getHeightWithKeyboard() {
        return this.keyboardHeight + getMeasuredHeight();
    }

    public void setEmojiKeyboardHeight(int height) {
        if (this.emojiHeight != height) {
            this.emojiHeight = height;
            this.backgroundView.invalidate();
        }
    }

    public void setEmojiOffset(boolean animInProgress, float offset) {
        if (this.emojiOffset != offset || this.animationInProgress != animInProgress) {
            this.emojiOffset = offset;
            this.animationInProgress = animInProgress;
            this.backgroundView.invalidate();
        }
    }

    public void checkSnowflake(Canvas canvas) {
        if (Theme.canStartHolidayAnimation()) {
            if (this.snowflakesEffect == null) {
                this.snowflakesEffect = new SnowflakesEffect(1);
            }
            this.snowflakesEffect.onDraw(this, canvas);
        }
    }

    protected boolean isActionBarVisible() {
        return true;
    }

    protected AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
        return null;
    }

    public void setSkipBackgroundDrawing(boolean skipBackgroundDrawing) {
        if (this.skipBackgroundDrawing != skipBackgroundDrawing) {
            this.skipBackgroundDrawing = skipBackgroundDrawing;
            this.backgroundView.invalidate();
        }
    }

    public Drawable getNewDrawable() {
        return Theme.getCachedWallpaperNonBlocking();
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return who == getBackgroundImage() || super.verifyDrawable(who);
    }

    public void startBlur() {
        if (!this.blurIsRunning || this.blurGeneratingTuskIsRunning || !this.invalidateBlur || !SharedConfig.chatBlurEnabled()) {
            return;
        }
        int blurAlpha = Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha));
        if (blurAlpha == 255) {
            return;
        }
        int lastW = getMeasuredWidth();
        int lastH = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100.0f);
        if (lastW != 0 && lastH != 0) {
            this.invalidateBlur = false;
            this.blurGeneratingTuskIsRunning = true;
            int bitmapH = ((int) (lastH / 12.0f)) + 34;
            int bitmapW = (int) (lastW / 12.0f);
            long time = System.currentTimeMillis();
            BlurBitmap bitmap = null;
            if (this.unusedBitmaps.size() > 0) {
                ArrayList<BlurBitmap> arrayList = this.unusedBitmaps;
                BlurBitmap bitmap2 = arrayList.remove(arrayList.size() - 1);
                bitmap = bitmap2;
            }
            if (bitmap != null) {
                bitmap.topBitmap.eraseColor(0);
                if (bitmap.bottomBitmap != null) {
                    bitmap.bottomBitmap.eraseColor(0);
                }
            } else {
                bitmap = new BlurBitmap();
                bitmap.topBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
                bitmap.topCanvas = new Canvas(bitmap.topBitmap);
                if (this.needBlurBottom) {
                    bitmap.bottomBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
                    bitmap.bottomCanvas = new Canvas(bitmap.bottomBitmap);
                }
            }
            BlurBitmap finalBitmap = bitmap;
            float sX = finalBitmap.topBitmap.getWidth() / lastW;
            float sY = (finalBitmap.topBitmap.getHeight() - 34) / lastH;
            finalBitmap.topCanvas.save();
            finalBitmap.pixelFixOffset = getScrollOffset() % 24;
            finalBitmap.topCanvas.clipRect(1.0f, sY * 10.0f, finalBitmap.topBitmap.getWidth(), finalBitmap.topBitmap.getHeight() - 1);
            finalBitmap.topCanvas.scale(sX, sY);
            finalBitmap.topCanvas.translate(0.0f, (sY * 10.0f) + finalBitmap.pixelFixOffset);
            finalBitmap.topScaleX = 1.0f / sX;
            finalBitmap.topScaleY = 1.0f / sY;
            drawList(finalBitmap.topCanvas, true);
            finalBitmap.topCanvas.restore();
            if (this.needBlurBottom) {
                float sX2 = finalBitmap.bottomBitmap.getWidth() / lastW;
                float sY2 = (finalBitmap.bottomBitmap.getHeight() - 34) / lastH;
                finalBitmap.needBlurBottom = true;
                finalBitmap.bottomOffset = getBottomOffset() - lastH;
                finalBitmap.drawnLisetTranslationY = getBottomOffset();
                finalBitmap.bottomCanvas.save();
                finalBitmap.bottomCanvas.clipRect(1.0f, sY2 * 10.0f, finalBitmap.bottomBitmap.getWidth(), finalBitmap.bottomBitmap.getHeight() - 1);
                finalBitmap.bottomCanvas.scale(sX2, sY2);
                finalBitmap.bottomCanvas.translate(0.0f, ((sY2 * 10.0f) - finalBitmap.bottomOffset) + finalBitmap.pixelFixOffset);
                finalBitmap.bottomScaleX = 1.0f / sX2;
                finalBitmap.bottomScaleY = 1.0f / sY2;
                drawList(finalBitmap.bottomCanvas, false);
                finalBitmap.bottomCanvas.restore();
            } else {
                finalBitmap.needBlurBottom = false;
            }
            this.times2 = (int) (this.times2 + (System.currentTimeMillis() - time));
            int i = this.count2 + 1;
            this.count2 = i;
            if (i >= 20) {
                this.count2 = 0;
                this.times2 = 0;
            }
            if (blurQueue == null) {
                blurQueue = new DispatchQueue("BlurQueue");
            }
            this.blurBackgroundTask.radius = (int) (((int) (Math.max(6, Math.max(lastH, lastW) / 180) * 2.5f)) * BlurSettingsBottomSheet.blurRadius);
            this.blurBackgroundTask.finalBitmap = finalBitmap;
            blurQueue.postRunnable(this.blurBackgroundTask);
        }
    }

    /* loaded from: classes5.dex */
    public class BlurBackgroundTask implements Runnable {
        BlurBitmap finalBitmap;
        int radius;

        private BlurBackgroundTask() {
            SizeNotifierFrameLayout.this = r1;
        }

        @Override // java.lang.Runnable
        public void run() {
            SizeNotifierFrameLayout sizeNotifierFrameLayout;
            long time = System.currentTimeMillis();
            Utilities.stackBlurBitmap(this.finalBitmap.topBitmap, this.radius);
            if (this.finalBitmap.needBlurBottom && this.finalBitmap.bottomBitmap != null) {
                Utilities.stackBlurBitmap(this.finalBitmap.bottomBitmap, this.radius);
            }
            SizeNotifierFrameLayout.this.times = (int) (sizeNotifierFrameLayout.times + (System.currentTimeMillis() - time));
            SizeNotifierFrameLayout.this.count++;
            if (SizeNotifierFrameLayout.this.count > 1000) {
                FileLog.d("chat blur generating average time" + (SizeNotifierFrameLayout.this.times / SizeNotifierFrameLayout.this.count));
                SizeNotifierFrameLayout.this.count = 0;
                SizeNotifierFrameLayout.this.times = 0;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SizeNotifierFrameLayout.BlurBackgroundTask.this.m3063xa0c67787();
                }
            });
        }

        /* renamed from: lambda$run$2$org-telegram-ui-Components-SizeNotifierFrameLayout$BlurBackgroundTask */
        public /* synthetic */ void m3063xa0c67787() {
            if (!SizeNotifierFrameLayout.this.blurIsRunning) {
                BlurBitmap blurBitmap = this.finalBitmap;
                if (blurBitmap != null) {
                    blurBitmap.recycle();
                }
                SizeNotifierFrameLayout.this.blurGeneratingTuskIsRunning = false;
                return;
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = SizeNotifierFrameLayout.this;
            sizeNotifierFrameLayout.prevBitmap = sizeNotifierFrameLayout.currentBitmap;
            final BlurBitmap oldBitmap = SizeNotifierFrameLayout.this.currentBitmap;
            SizeNotifierFrameLayout.this.blurPaintTop2.setShader(SizeNotifierFrameLayout.this.blurPaintTop.getShader());
            SizeNotifierFrameLayout.this.blurPaintBottom2.setShader(SizeNotifierFrameLayout.this.blurPaintBottom.getShader());
            BitmapShader bitmapShader = new BitmapShader(this.finalBitmap.topBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            SizeNotifierFrameLayout.this.blurPaintTop.setShader(bitmapShader);
            if (this.finalBitmap.needBlurBottom && this.finalBitmap.bottomBitmap != null) {
                BitmapShader bitmapShader2 = new BitmapShader(this.finalBitmap.bottomBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                SizeNotifierFrameLayout.this.blurPaintBottom.setShader(bitmapShader2);
            }
            if (SizeNotifierFrameLayout.this.blurCrossfade != null) {
                SizeNotifierFrameLayout.this.blurCrossfade.cancel();
            }
            SizeNotifierFrameLayout.this.blurCrossfadeProgress = 0.0f;
            SizeNotifierFrameLayout.this.blurCrossfade = ValueAnimator.ofFloat(0.0f, 1.0f);
            SizeNotifierFrameLayout.this.blurCrossfade.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SizeNotifierFrameLayout.BlurBackgroundTask.this.m3061xda6f1105(valueAnimator);
                }
            });
            SizeNotifierFrameLayout.this.blurCrossfade.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout.BlurBackgroundTask.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    SizeNotifierFrameLayout.this.blurCrossfadeProgress = 1.0f;
                    SizeNotifierFrameLayout.this.unusedBitmaps.add(oldBitmap);
                    SizeNotifierFrameLayout.this.blurPaintTop2.setShader(null);
                    SizeNotifierFrameLayout.this.blurPaintBottom2.setShader(null);
                    SizeNotifierFrameLayout.this.invalidateBlurredViews();
                    super.onAnimationEnd(animation);
                }
            });
            SizeNotifierFrameLayout.this.blurCrossfade.setDuration(50L);
            SizeNotifierFrameLayout.this.blurCrossfade.start();
            SizeNotifierFrameLayout.this.invalidateBlurredViews();
            SizeNotifierFrameLayout.this.currentBitmap = this.finalBitmap;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayout$BlurBackgroundTask$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SizeNotifierFrameLayout.BlurBackgroundTask.this.m3062xbd9ac446();
                }
            }, 16L);
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Components-SizeNotifierFrameLayout$BlurBackgroundTask */
        public /* synthetic */ void m3061xda6f1105(ValueAnimator valueAnimator) {
            SizeNotifierFrameLayout.this.blurCrossfadeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            SizeNotifierFrameLayout.this.invalidateBlurredViews();
        }

        /* renamed from: lambda$run$1$org-telegram-ui-Components-SizeNotifierFrameLayout$BlurBackgroundTask */
        public /* synthetic */ void m3062xbd9ac446() {
            SizeNotifierFrameLayout.this.blurGeneratingTuskIsRunning = false;
            SizeNotifierFrameLayout.this.startBlur();
        }
    }

    public void invalidateBlurredViews() {
        for (int i = 0; i < this.blurBehindViews.size(); i++) {
            this.blurBehindViews.get(i).invalidate();
        }
    }

    protected float getBottomOffset() {
        return getMeasuredHeight();
    }

    protected float getListTranslationY() {
        return 0.0f;
    }

    protected Theme.ResourcesProvider getResourceProvider() {
        return null;
    }

    public void drawList(Canvas blurCanvas, boolean top) {
    }

    protected int getScrollOffset() {
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.blurIsRunning) {
            startBlur();
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.needBlur && !this.blurIsRunning) {
            this.blurIsRunning = true;
            this.invalidateBlur = true;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.blurPaintTop.setShader(null);
        this.blurPaintTop2.setShader(null);
        this.blurPaintBottom.setShader(null);
        this.blurPaintBottom2.setShader(null);
        ValueAnimator valueAnimator = this.blurCrossfade;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        BlurBitmap blurBitmap = this.currentBitmap;
        if (blurBitmap != null) {
            blurBitmap.recycle();
            this.currentBitmap = null;
        }
        for (int i = 0; i < this.unusedBitmaps.size(); i++) {
            if (this.unusedBitmaps.get(i) != null) {
                this.unusedBitmaps.get(i).recycle();
            }
        }
        this.unusedBitmaps.clear();
        this.blurIsRunning = false;
    }

    public void drawBlurRect(Canvas canvas, float y, android.graphics.Rect rectTmp, Paint blurScrimPaint, boolean top) {
        int blurAlpha = Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha));
        if (this.currentBitmap == null || !SharedConfig.chatBlurEnabled()) {
            canvas.drawRect(rectTmp, blurScrimPaint);
            return;
        }
        updateBlurShaderPosition(y, top);
        blurScrimPaint.setAlpha(255);
        if (this.blurCrossfadeProgress != 1.0f && this.selectedBlurPaint2.getShader() != null) {
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, this.selectedBlurPaint2);
            canvas.saveLayerAlpha(rectTmp.left, rectTmp.top, rectTmp.right, rectTmp.bottom, (int) (this.blurCrossfadeProgress * 255.0f), 31);
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, this.selectedBlurPaint);
            canvas.restore();
        } else {
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, this.selectedBlurPaint);
        }
        blurScrimPaint.setAlpha(blurAlpha);
        canvas.drawRect(rectTmp, blurScrimPaint);
    }

    public void drawBlurCircle(Canvas canvas, float viewY, float cx, float cy, float radius, Paint blurScrimPaint, boolean top) {
        int blurAlpha = Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha));
        if (this.currentBitmap != null && SharedConfig.chatBlurEnabled()) {
            updateBlurShaderPosition(viewY, top);
            blurScrimPaint.setAlpha(255);
            if (this.blurCrossfadeProgress != 1.0f && this.selectedBlurPaint2.getShader() != null) {
                canvas.drawCircle(cx, cy, radius, blurScrimPaint);
                canvas.drawCircle(cx, cy, radius, this.selectedBlurPaint2);
                canvas.saveLayerAlpha(cx - radius, cy - radius, cx + radius, cy + radius, (int) (this.blurCrossfadeProgress * 255.0f), 31);
                canvas.drawCircle(cx, cy, radius, blurScrimPaint);
                canvas.drawCircle(cx, cy, radius, this.selectedBlurPaint);
                canvas.restore();
            } else {
                canvas.drawCircle(cx, cy, radius, blurScrimPaint);
                canvas.drawCircle(cx, cy, radius, this.selectedBlurPaint);
            }
            blurScrimPaint.setAlpha(blurAlpha);
            canvas.drawCircle(cx, cy, radius, blurScrimPaint);
            return;
        }
        canvas.drawCircle(cx, cy, radius, blurScrimPaint);
    }

    private void updateBlurShaderPosition(float viewY, boolean top) {
        this.selectedBlurPaint = top ? this.blurPaintTop : this.blurPaintBottom;
        this.selectedBlurPaint2 = top ? this.blurPaintTop2 : this.blurPaintBottom2;
        if (top) {
            viewY += getTranslationY();
        }
        if (this.selectedBlurPaint.getShader() != null) {
            this.matrix.reset();
            this.matrix2.reset();
            if (!top) {
                float y1 = ((((-viewY) + this.currentBitmap.bottomOffset) - this.currentBitmap.pixelFixOffset) - 34.0f) - (this.currentBitmap.drawnLisetTranslationY - (getBottomOffset() + getListTranslationY()));
                this.matrix.setTranslate(0.0f, y1);
                this.matrix.preScale(this.currentBitmap.bottomScaleX, this.currentBitmap.bottomScaleY);
                BlurBitmap blurBitmap = this.prevBitmap;
                if (blurBitmap != null) {
                    float y12 = ((((-viewY) + blurBitmap.bottomOffset) - this.prevBitmap.pixelFixOffset) - 34.0f) - (this.prevBitmap.drawnLisetTranslationY - (getBottomOffset() + getListTranslationY()));
                    this.matrix2.setTranslate(0.0f, y12);
                    this.matrix2.preScale(this.prevBitmap.bottomScaleX, this.prevBitmap.bottomScaleY);
                }
            } else {
                this.matrix.setTranslate(0.0f, ((-viewY) - this.currentBitmap.pixelFixOffset) - 34.0f);
                this.matrix.preScale(this.currentBitmap.topScaleX, this.currentBitmap.topScaleY);
                BlurBitmap blurBitmap2 = this.prevBitmap;
                if (blurBitmap2 != null) {
                    this.matrix2.setTranslate(0.0f, ((-viewY) - blurBitmap2.pixelFixOffset) - 34.0f);
                    this.matrix2.preScale(this.prevBitmap.topScaleX, this.prevBitmap.topScaleY);
                }
            }
            this.selectedBlurPaint.getShader().setLocalMatrix(this.matrix);
            if (this.selectedBlurPaint2.getShader() != null) {
                this.selectedBlurPaint2.getShader().setLocalMatrix(this.matrix);
            }
        }
    }

    protected float getBottomTranslation() {
        return 0.0f;
    }

    /* loaded from: classes5.dex */
    public static class BlurBitmap {
        Bitmap bottomBitmap;
        Canvas bottomCanvas;
        float bottomOffset;
        float bottomScaleX;
        float bottomScaleY;
        float drawnLisetTranslationY;
        public boolean needBlurBottom;
        int pixelFixOffset;
        Bitmap topBitmap;
        Canvas topCanvas;
        float topScaleX;
        float topScaleY;

        private BlurBitmap() {
        }

        public void recycle() {
            this.topBitmap.recycle();
            Bitmap bitmap = this.bottomBitmap;
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }
}
