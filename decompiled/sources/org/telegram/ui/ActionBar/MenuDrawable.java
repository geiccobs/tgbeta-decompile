package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.MediaActionDrawable;
/* loaded from: classes4.dex */
public class MenuDrawable extends Drawable {
    public static int TYPE_DEFAULT = 0;
    public static int TYPE_UDPATE_AVAILABLE = 1;
    public static int TYPE_UDPATE_DOWNLOADING = 2;
    private int alpha;
    private float animatedDownloadProgress;
    private boolean animationInProgress;
    private int backColor;
    private Paint backPaint;
    private int currentAnimationTime;
    private float currentRotation;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private float finalRotation;
    private int iconColor;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private boolean miniIcon;
    private Paint paint;
    private int previousType;
    private RectF rect;
    private boolean reverseAngle;
    private boolean rotateToBack;
    private int type;
    private float typeAnimationProgress;

    public MenuDrawable() {
        this(TYPE_DEFAULT);
    }

    public MenuDrawable(int type) {
        this.paint = new Paint(1);
        this.backPaint = new Paint(1);
        this.rotateToBack = true;
        this.interpolator = new DecelerateInterpolator();
        this.rect = new RectF();
        this.alpha = 255;
        this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.backPaint.setStrokeWidth(AndroidUtilities.density * 1.66f);
        this.backPaint.setStrokeCap(Paint.Cap.ROUND);
        this.backPaint.setStyle(Paint.Style.STROKE);
        this.previousType = TYPE_DEFAULT;
        this.type = type;
        this.typeAnimationProgress = 1.0f;
    }

    public void setRotateToBack(boolean value) {
        this.rotateToBack = value;
    }

    public float getCurrentRotation() {
        return this.currentRotation;
    }

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0L;
        float f = this.currentRotation;
        if (f == 1.0f) {
            this.reverseAngle = true;
        } else if (f == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0L;
        if (animated) {
            if (f >= rotation) {
                this.currentAnimationTime = (int) ((1.0f - f) * 200.0f);
            } else {
                this.currentAnimationTime = (int) (f * 200.0f);
            }
            this.lastFrameTime = SystemClock.elapsedRealtime();
            this.finalRotation = rotation;
        } else {
            this.currentRotation = rotation;
            this.finalRotation = rotation;
        }
        invalidateSelf();
    }

    public void setType(int value, boolean animated) {
        int i = this.type;
        if (i == value) {
            return;
        }
        this.previousType = i;
        this.type = value;
        if (animated) {
            this.typeAnimationProgress = 0.0f;
        } else {
            this.typeAnimationProgress = 1.0f;
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float diffMiddle;
        float diffUp;
        float startXDiff;
        float endXDiff;
        float endYDiff;
        float startYDiff;
        int backColor1;
        int backColor12;
        float rad;
        float cy;
        float cx;
        int backColor13;
        long newTime = SystemClock.elapsedRealtime();
        long j = this.lastFrameTime;
        long dt = newTime - j;
        float f = this.currentRotation;
        float f2 = this.finalRotation;
        if (f != f2) {
            if (j != 0) {
                int i = (int) (this.currentAnimationTime + dt);
                this.currentAnimationTime = i;
                if (i >= 200) {
                    this.currentRotation = f2;
                } else if (f < f2) {
                    this.currentRotation = this.interpolator.getInterpolation(i / 200.0f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(i / 200.0f);
                }
            }
            invalidateSelf();
        }
        float f3 = this.typeAnimationProgress;
        if (f3 < 1.0f) {
            float f4 = f3 + (((float) dt) / 200.0f);
            this.typeAnimationProgress = f4;
            if (f4 > 1.0f) {
                this.typeAnimationProgress = 1.0f;
            }
            invalidateSelf();
        }
        this.lastFrameTime = newTime;
        canvas.save();
        canvas.translate((getIntrinsicWidth() / 2) - AndroidUtilities.dp(9.0f), getIntrinsicHeight() / 2);
        int i2 = this.iconColor;
        if (i2 == 0) {
            i2 = Theme.getColor(Theme.key_actionBarDefaultIcon);
        }
        int color1 = i2;
        int i3 = this.backColor;
        if (i3 == 0) {
            i3 = Theme.getColor(Theme.key_actionBarDefault);
        }
        int backColor14 = i3;
        int i4 = this.type;
        int i5 = TYPE_DEFAULT;
        if (i4 == i5) {
            if (this.previousType != i5) {
                float diffUp2 = AndroidUtilities.dp(9.0f) * (1.0f - this.typeAnimationProgress);
                diffUp = diffUp2;
                diffMiddle = AndroidUtilities.dp(7.0f) * (1.0f - this.typeAnimationProgress);
            } else {
                diffUp = 0.0f;
                diffMiddle = 0.0f;
            }
        } else if (this.previousType == i5) {
            float diffUp3 = AndroidUtilities.dp(9.0f) * this.typeAnimationProgress * (1.0f - this.currentRotation);
            diffUp = diffUp3;
            diffMiddle = AndroidUtilities.dp(7.0f) * this.typeAnimationProgress * (1.0f - this.currentRotation);
        } else {
            float diffUp4 = AndroidUtilities.dp(9.0f) * (1.0f - this.currentRotation);
            diffUp = diffUp4;
            diffMiddle = AndroidUtilities.dp(7.0f) * (1.0f - this.currentRotation);
        }
        if (this.rotateToBack) {
            canvas.rotate(this.currentRotation * (this.reverseAngle ? -180 : 180), AndroidUtilities.dp(9.0f), 0.0f);
            this.paint.setColor(color1);
            this.paint.setAlpha(this.alpha);
            canvas.drawLine(0.0f, 0.0f, (AndroidUtilities.dp(18.0f) - (AndroidUtilities.dp(3.0f) * this.currentRotation)) - diffMiddle, 0.0f, this.paint);
            float endYDiff2 = (AndroidUtilities.dp(5.0f) * (1.0f - Math.abs(this.currentRotation))) - (AndroidUtilities.dp(0.5f) * Math.abs(this.currentRotation));
            float endXDiff2 = AndroidUtilities.dp(18.0f) - (AndroidUtilities.dp(2.5f) * Math.abs(this.currentRotation));
            endYDiff = endYDiff2;
            startYDiff = AndroidUtilities.dp(5.0f) + (AndroidUtilities.dp(2.0f) * Math.abs(this.currentRotation));
            startXDiff = AndroidUtilities.dp(7.5f) * Math.abs(this.currentRotation);
            backColor1 = backColor14;
            endXDiff = endXDiff2;
        } else {
            canvas.rotate(this.currentRotation * (this.reverseAngle ? -225 : TsExtractor.TS_STREAM_TYPE_E_AC3), AndroidUtilities.dp(9.0f), 0.0f);
            if (this.miniIcon) {
                this.paint.setColor(color1);
                this.paint.setAlpha(this.alpha);
                canvas.drawLine((AndroidUtilities.dp(1.0f) * this.currentRotation) + (AndroidUtilities.dpf2(2.0f) * (1.0f - Math.abs(this.currentRotation))), 0.0f, ((AndroidUtilities.dpf2(16.0f) * (1.0f - this.currentRotation)) + (AndroidUtilities.dp(17.0f) * this.currentRotation)) - diffMiddle, 0.0f, this.paint);
                float endYDiff3 = (AndroidUtilities.dpf2(5.0f) * (1.0f - Math.abs(this.currentRotation))) - (AndroidUtilities.dpf2(0.5f) * Math.abs(this.currentRotation));
                endXDiff = (AndroidUtilities.dpf2(16.0f) * (1.0f - Math.abs(this.currentRotation))) + (AndroidUtilities.dpf2(9.0f) * Math.abs(this.currentRotation));
                startYDiff = AndroidUtilities.dpf2(5.0f) + (AndroidUtilities.dpf2(3.0f) * Math.abs(this.currentRotation));
                startXDiff = AndroidUtilities.dpf2(2.0f) + (AndroidUtilities.dpf2(7.0f) * Math.abs(this.currentRotation));
                backColor1 = backColor14;
                endYDiff = endYDiff3;
            } else {
                int color2 = Theme.getColor(Theme.key_actionBarActionModeDefaultIcon);
                int backColor2 = Theme.getColor(Theme.key_actionBarActionModeDefault);
                int backColor15 = AndroidUtilities.getOffsetColor(backColor14, backColor2, this.currentRotation, 1.0f);
                this.paint.setColor(AndroidUtilities.getOffsetColor(color1, color2, this.currentRotation, 1.0f));
                this.paint.setAlpha(this.alpha);
                canvas.drawLine(this.currentRotation * AndroidUtilities.dp(1.0f), 0.0f, (AndroidUtilities.dp(18.0f) - (AndroidUtilities.dp(1.0f) * this.currentRotation)) - diffMiddle, 0.0f, this.paint);
                float endYDiff4 = (AndroidUtilities.dp(5.0f) * (1.0f - Math.abs(this.currentRotation))) - (AndroidUtilities.dp(0.5f) * Math.abs(this.currentRotation));
                endXDiff = AndroidUtilities.dp(18.0f) - (AndroidUtilities.dp(9.0f) * Math.abs(this.currentRotation));
                startYDiff = AndroidUtilities.dp(5.0f) + (AndroidUtilities.dp(3.0f) * Math.abs(this.currentRotation));
                startXDiff = AndroidUtilities.dp(9.0f) * Math.abs(this.currentRotation);
                backColor1 = backColor15;
                endYDiff = endYDiff4;
            }
        }
        if (this.miniIcon) {
            float f5 = startXDiff;
            float f6 = endXDiff;
            backColor12 = backColor1;
            canvas.drawLine(f5, -startYDiff, f6, -endYDiff, this.paint);
            canvas.drawLine(f5, startYDiff, f6, endYDiff, this.paint);
        } else {
            backColor12 = backColor1;
            float f7 = startXDiff;
            canvas.drawLine(f7, -startYDiff, endXDiff - diffUp, -endYDiff, this.paint);
            canvas.drawLine(f7, startYDiff, endXDiff, endYDiff, this.paint);
        }
        int i6 = this.type;
        int i7 = TYPE_DEFAULT;
        if ((i6 != i7 && this.currentRotation != 1.0f) || (this.previousType != i7 && this.typeAnimationProgress != 1.0f)) {
            float cx2 = AndroidUtilities.dp(17.0f);
            float cy2 = -AndroidUtilities.dp(4.5f);
            float rad2 = AndroidUtilities.density * 5.5f;
            float f8 = this.currentRotation;
            canvas.scale(1.0f - f8, 1.0f - f8, cx2, cy2);
            if (this.type != TYPE_DEFAULT) {
                rad = rad2;
            } else {
                rad = rad2 * (1.0f - this.typeAnimationProgress);
            }
            int backColor16 = backColor12;
            this.backPaint.setColor(backColor16);
            this.backPaint.setAlpha(this.alpha);
            canvas.drawCircle(cx2, cy2, rad, this.paint);
            int i8 = this.type;
            int i9 = TYPE_UDPATE_AVAILABLE;
            if (i8 == i9 || this.previousType == i9) {
                this.backPaint.setStrokeWidth(AndroidUtilities.density * 1.66f);
                if (this.previousType == TYPE_UDPATE_AVAILABLE) {
                    backColor13 = backColor16;
                    this.backPaint.setAlpha((int) (this.alpha * (1.0f - this.typeAnimationProgress)));
                } else {
                    backColor13 = backColor16;
                    this.backPaint.setAlpha(this.alpha);
                }
                cy = cy2;
                cx = cx2;
                canvas.drawLine(cx2, cy2 - AndroidUtilities.dp(2.0f), cx2, cy2, this.backPaint);
                canvas.drawPoint(cx, cy + AndroidUtilities.dp(2.5f), this.backPaint);
            } else {
                cy = cy2;
                cx = cx2;
            }
            int i10 = this.type;
            int i11 = TYPE_UDPATE_DOWNLOADING;
            if (i10 == i11 || this.previousType == i11) {
                this.backPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                if (this.previousType == TYPE_UDPATE_DOWNLOADING) {
                    this.backPaint.setAlpha((int) (this.alpha * (1.0f - this.typeAnimationProgress)));
                } else {
                    this.backPaint.setAlpha(this.alpha);
                }
                float arcRad = Math.max(4.0f, this.animatedDownloadProgress * 360.0f);
                this.rect.set(cx - AndroidUtilities.dp(3.0f), cy - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f) + cx, cy + AndroidUtilities.dp(3.0f));
                canvas.drawArc(this.rect, this.downloadRadOffset, arcRad, false, this.backPaint);
                float f9 = this.downloadRadOffset + (((float) (360 * dt)) / 2500.0f);
                this.downloadRadOffset = f9;
                this.downloadRadOffset = MediaActionDrawable.getCircleValue(f9);
                float f10 = this.downloadProgress;
                float f11 = this.downloadProgressAnimationStart;
                float progressDiff = f10 - f11;
                if (progressDiff > 0.0f) {
                    float f12 = this.downloadProgressTime + ((float) dt);
                    this.downloadProgressTime = f12;
                    if (f12 < 200.0f) {
                        this.animatedDownloadProgress = f11 + (this.interpolator.getInterpolation(f12 / 200.0f) * progressDiff);
                    } else {
                        this.animatedDownloadProgress = f10;
                        this.downloadProgressAnimationStart = f10;
                        this.downloadProgressTime = 0.0f;
                    }
                }
                invalidateSelf();
            }
        }
        canvas.restore();
    }

    public void setUpdateDownloadProgress(float value, boolean animated) {
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

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.alpha != alpha) {
            this.alpha = alpha;
            this.paint.setAlpha(alpha);
            this.backPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void setRoundCap() {
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setMiniIcon(boolean miniIcon) {
        this.miniIcon = miniIcon;
    }
}
