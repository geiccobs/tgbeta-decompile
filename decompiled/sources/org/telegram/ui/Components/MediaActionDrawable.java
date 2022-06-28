package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class MediaActionDrawable extends Drawable {
    private static final float CANCEL_TO_CHECK_STAGE1 = 0.5f;
    private static final float CANCEL_TO_CHECK_STAGE2 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE1 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE2 = 0.2f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE3 = 0.3f;
    private static final float EPS = 0.001f;
    public static final int ICON_CANCEL = 3;
    public static final int ICON_CANCEL_FILL = 14;
    public static final int ICON_CANCEL_NOPROFRESS = 12;
    public static final int ICON_CANCEL_PERCENT = 13;
    public static final int ICON_CHECK = 6;
    public static final int ICON_DOWNLOAD = 2;
    public static final int ICON_EMPTY = 10;
    public static final int ICON_EMPTY_NOPROGRESS = 11;
    public static final int ICON_FILE = 5;
    public static final int ICON_FIRE = 7;
    public static final int ICON_GIF = 8;
    public static final int ICON_NONE = 4;
    public static final int ICON_PAUSE = 1;
    public static final int ICON_PLAY = 0;
    public static final int ICON_SECRETCHECK = 9;
    public static final int ICON_UPDATE = 15;
    private float animatedDownloadProgress;
    private boolean animatingTransition;
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private LinearGradient gradientDrawable;
    private Matrix gradientMatrix;
    private boolean hasOverlayImage;
    private boolean isMini;
    private long lastAnimationTime;
    private Theme.MessageDrawable messageDrawable;
    private int nextIcon;
    private String percentString;
    private int percentStringWidth;
    private float savedTransitionProgress;
    private TextPaint textPaint = new TextPaint(1);
    private Paint paint = new Paint(1);
    private Paint backPaint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private Paint paint3 = new Paint(1);
    private RectF rect = new RectF();
    private float scale = 1.0f;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private float transitionAnimationTime = 400.0f;
    private int lastPercent = -1;
    private float overrideAlpha = 1.0f;
    private float transitionProgress = 1.0f;

    /* loaded from: classes5.dex */
    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint3.setColor(-1);
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.paint2.setColor(-1);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        this.paint2.setColorFilter(colorFilter);
        this.paint3.setColorFilter(colorFilter);
        this.textPaint.setColorFilter(colorFilter);
    }

    public void setColor(int value) {
        this.paint.setColor(value | (-16777216));
        this.paint2.setColor(value | (-16777216));
        this.paint3.setColor(value | (-16777216));
        this.textPaint.setColor((-16777216) | value);
        this.colorFilter = new PorterDuffColorFilter(value, PorterDuff.Mode.MULTIPLY);
    }

    public void setBackColor(int value) {
        this.backPaint.setColor((-16777216) | value);
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean value) {
        this.isMini = value;
        this.paint.setStrokeWidth(AndroidUtilities.dp(value ? 2.0f : 3.0f));
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int icon, boolean animated) {
        int i;
        int i2;
        if (this.currentIcon == icon && (i2 = this.nextIcon) != icon) {
            this.currentIcon = i2;
            this.transitionProgress = 1.0f;
        }
        if (animated) {
            int i3 = this.currentIcon;
            if (i3 == icon || (i = this.nextIcon) == icon) {
                return false;
            }
            if ((i3 == 0 && icon == 1) || (i3 == 1 && icon == 0)) {
                this.transitionAnimationTime = 300.0f;
            } else if (i3 == 2 && (icon == 3 || icon == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (i3 != 4 && icon == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((i3 == 4 && icon == 14) || (i3 == 14 && icon == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = i;
            }
            this.animatingTransition = true;
            this.nextIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else if (this.currentIcon == icon) {
            return false;
        } else {
            this.animatingTransition = false;
            this.nextIcon = icon;
            this.currentIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (icon == 3 || icon == 14) {
            this.downloadRadOffset = 112.0f;
            this.animatedDownloadProgress = 0.0f;
            this.downloadProgressAnimationStart = 0.0f;
            this.downloadProgressTime = 0.0f;
        }
        invalidateSelf();
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public int getPreviousIcon() {
        return this.currentIcon;
    }

    public void setProgress(float value, boolean animated) {
        if (!animated) {
            this.animatedDownloadProgress = value;
            this.downloadProgressAnimationStart = value;
        } else {
            if (this.animatedDownloadProgress > value) {
                this.animatedDownloadProgress = value;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        }
        this.downloadProgress = value;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    public float getProgress() {
        return this.downloadProgress;
    }

    public static float getCircleValue(float value) {
        while (value > 360.0f) {
            value -= 360.0f;
        }
        return value;
    }

    public float getProgressAlpha() {
        return 1.0f - this.transitionProgress;
    }

    public float getTransitionProgress() {
        if (this.animatingTransition) {
            return this.transitionProgress;
        }
        return 1.0f;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable drawable) {
        this.messageDrawable = drawable;
    }

    public void setBackgroundGradientDrawable(LinearGradient drawable) {
        this.gradientDrawable = drawable;
        this.gradientMatrix = new Matrix();
    }

    public void setHasOverlayImage(boolean value) {
        this.hasOverlayImage = value;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        float intrinsicWidth = (right - left) / getIntrinsicWidth();
        this.scale = intrinsicWidth;
        if (intrinsicWidth < 0.7f) {
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        super.invalidateSelf();
        MediaActionDrawableDelegate mediaActionDrawableDelegate = this.delegate;
        if (mediaActionDrawableDelegate != null) {
            mediaActionDrawableDelegate.invalidate();
        }
    }

    private void applyShaderMatrix(boolean path) {
        Theme.MessageDrawable messageDrawable = this.messageDrawable;
        if (messageDrawable != null && messageDrawable.hasGradient() && !this.hasOverlayImage) {
            android.graphics.Rect bounds = getBounds();
            Shader shader = this.messageDrawable.getGradientShader();
            Matrix matrix = this.messageDrawable.getMatrix();
            matrix.reset();
            this.messageDrawable.applyMatrixScale();
            if (path) {
                matrix.postTranslate(-bounds.centerX(), (-this.messageDrawable.getTopY()) + bounds.top);
            } else {
                matrix.postTranslate(0.0f, -this.messageDrawable.getTopY());
            }
            shader.setLocalMatrix(matrix);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:334:0x0957, code lost:
        if (r44.nextIcon != 1) goto L336;
     */
    /* JADX WARN: Code restructure failed: missing block: B:356:0x0989, code lost:
        if (r2 == 1) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:492:0x0cee, code lost:
        if (r44.nextIcon == 14) goto L500;
     */
    /* JADX WARN: Removed duplicated region for block: B:229:0x0710  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x0717  */
    /* JADX WARN: Removed duplicated region for block: B:242:0x074c  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x074f  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x075c  */
    /* JADX WARN: Removed duplicated region for block: B:249:0x0761  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x0772  */
    /* JADX WARN: Removed duplicated region for block: B:256:0x0775  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0782  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x0787  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x07ba  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x07bd  */
    /* JADX WARN: Removed duplicated region for block: B:277:0x07d9  */
    /* JADX WARN: Removed duplicated region for block: B:280:0x082b  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x0844  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x0848  */
    /* JADX WARN: Removed duplicated region for block: B:294:0x085b  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x085e  */
    /* JADX WARN: Removed duplicated region for block: B:298:0x0878  */
    /* JADX WARN: Removed duplicated region for block: B:301:0x08b5  */
    /* JADX WARN: Removed duplicated region for block: B:308:0x08cc  */
    /* JADX WARN: Removed duplicated region for block: B:309:0x08cf  */
    /* JADX WARN: Removed duplicated region for block: B:315:0x08f7  */
    /* JADX WARN: Removed duplicated region for block: B:323:0x093d  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0954  */
    /* JADX WARN: Removed duplicated region for block: B:335:0x095a  */
    /* JADX WARN: Removed duplicated region for block: B:351:0x0980  */
    /* JADX WARN: Removed duplicated region for block: B:352:0x0983  */
    /* JADX WARN: Removed duplicated region for block: B:355:0x0988  */
    /* JADX WARN: Removed duplicated region for block: B:357:0x098c  */
    /* JADX WARN: Removed duplicated region for block: B:359:0x098f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:371:0x09e7  */
    /* JADX WARN: Removed duplicated region for block: B:372:0x09ea  */
    /* JADX WARN: Removed duplicated region for block: B:374:0x09ed  */
    /* JADX WARN: Removed duplicated region for block: B:386:0x0a2c  */
    /* JADX WARN: Removed duplicated region for block: B:390:0x0a34  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x0a41  */
    /* JADX WARN: Removed duplicated region for block: B:397:0x0a64  */
    /* JADX WARN: Removed duplicated region for block: B:410:0x0a9b  */
    /* JADX WARN: Removed duplicated region for block: B:419:0x0ac6  */
    /* JADX WARN: Removed duplicated region for block: B:426:0x0af8  */
    /* JADX WARN: Removed duplicated region for block: B:427:0x0b2b  */
    /* JADX WARN: Removed duplicated region for block: B:430:0x0b34  */
    /* JADX WARN: Removed duplicated region for block: B:432:0x0b52  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0b95  */
    /* JADX WARN: Removed duplicated region for block: B:441:0x0b99  */
    /* JADX WARN: Removed duplicated region for block: B:447:0x0bd5  */
    /* JADX WARN: Removed duplicated region for block: B:461:0x0c39  */
    /* JADX WARN: Removed duplicated region for block: B:463:0x0c41  */
    /* JADX WARN: Removed duplicated region for block: B:484:0x0cd8  */
    /* JADX WARN: Removed duplicated region for block: B:502:0x0d1e  */
    /* JADX WARN: Removed duplicated region for block: B:509:0x0d51  */
    /* JADX WARN: Removed duplicated region for block: B:513:0x0d5a  */
    /* JADX WARN: Removed duplicated region for block: B:521:0x0d7e  */
    /* JADX WARN: Removed duplicated region for block: B:523:? A[RETURN, SYNTHETIC] */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void draw(android.graphics.Canvas r45) {
        /*
            Method dump skipped, instructions count: 3458
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MediaActionDrawable.draw(android.graphics.Canvas):void");
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return AndroidUtilities.dp(48.0f);
    }
}
