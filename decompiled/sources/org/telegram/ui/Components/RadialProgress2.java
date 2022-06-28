package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.MediaActionDrawable;
/* loaded from: classes5.dex */
public class RadialProgress2 {
    private int backgroundStroke;
    private float circleCheckProgress;
    private int circleColor;
    private String circleColorKey;
    private String circleCrossfadeColorKey;
    private float circleCrossfadeColorProgress;
    private Paint circleMiniPaint;
    private Paint circlePaint;
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
    private boolean drawBackground;
    private boolean drawMiniIcon;
    private int iconColor;
    private String iconColorKey;
    private int iconPressedColor;
    private String iconPressedColorKey;
    private boolean isPressed;
    private boolean isPressedMini;
    private MediaActionDrawable mediaActionDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private float miniIconScale;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint;
    private ImageReceiver overlayImageView;
    private Paint overlayPaint;
    private float overrideAlpha;
    private View parent;
    private boolean previousCheckDrawable;
    private int progressColor;
    private RectF progressRect;
    private final Theme.ResourcesProvider resourcesProvider;

    public RadialProgress2(View parentView) {
        this(parentView, null);
    }

    public RadialProgress2(final View parentView, Theme.ResourcesProvider resourcesProvider) {
        this.progressRect = new RectF();
        this.progressColor = -1;
        this.overlayPaint = new Paint(1);
        this.circlePaint = new Paint(1);
        this.circleMiniPaint = new Paint(1);
        this.miniIconScale = 1.0f;
        this.circleCheckProgress = 1.0f;
        this.drawBackground = true;
        this.overrideAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider;
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = parentView;
        ImageReceiver imageReceiver = new ImageReceiver(parentView);
        this.overlayImageView = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        MediaActionDrawable mediaActionDrawable = new MediaActionDrawable();
        this.mediaActionDrawable = mediaActionDrawable;
        parentView.getClass();
        mediaActionDrawable.setDelegate(new MediaActionDrawable.MediaActionDrawableDelegate() { // from class: org.telegram.ui.Components.RadialProgress2$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.MediaActionDrawable.MediaActionDrawableDelegate
            public final void invalidate() {
                parentView.invalidate();
            }
        });
        MediaActionDrawable mediaActionDrawable2 = new MediaActionDrawable();
        this.miniMediaActionDrawable = mediaActionDrawable2;
        parentView.getClass();
        mediaActionDrawable2.setDelegate(new MediaActionDrawable.MediaActionDrawableDelegate() { // from class: org.telegram.ui.Components.RadialProgress2$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.MediaActionDrawable.MediaActionDrawableDelegate
            public final void invalidate() {
                parentView.invalidate();
            }
        });
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        int dp = AndroidUtilities.dp(22.0f);
        this.circleRadius = dp;
        this.overlayImageView.setRoundRadius(dp);
        this.overlayPaint.setColor(1677721600);
    }

    public void setAsMini() {
        this.mediaActionDrawable.setMini(true);
    }

    public void setCircleRadius(int value) {
        this.circleRadius = value;
        this.overlayImageView.setRoundRadius(value);
    }

    public void setBackgroundStroke(int value) {
        this.backgroundStroke = value;
        this.circlePaint.setStrokeWidth(value);
        this.circlePaint.setStyle(Paint.Style.STROKE);
        invalidateParent();
    }

    public void setBackgroundDrawable(Theme.MessageDrawable drawable) {
        this.mediaActionDrawable.setBackgroundDrawable(drawable);
        this.miniMediaActionDrawable.setBackgroundDrawable(drawable);
    }

    public void setBackgroundGradientDrawable(LinearGradient drawable) {
        this.mediaActionDrawable.setBackgroundGradientDrawable(drawable);
        this.miniMediaActionDrawable.setBackgroundGradientDrawable(drawable);
    }

    public void setImageOverlay(TLRPC.PhotoSize image, TLRPC.Document document, Object parentObject) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(image, document), String.format(Locale.US, "%d_%d", Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)), null, null, parentObject, 1);
    }

    public void setImageOverlay(String url) {
        this.overlayImageView.setImage(url, url != null ? String.format(Locale.US, "%d_%d", Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)) : null, null, null, -1L);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColors(int circle, int circlePressed, int icon, int iconPressed) {
        this.circleColor = circle;
        this.circlePressedColor = circlePressed;
        this.iconColor = icon;
        this.iconPressedColor = iconPressed;
        this.circleColorKey = null;
        this.circlePressedColorKey = null;
        this.iconColorKey = null;
        this.iconPressedColorKey = null;
    }

    public void setColors(String circle, String circlePressed, String icon, String iconPressed) {
        this.circleColorKey = circle;
        this.circlePressedColorKey = circlePressed;
        this.iconColorKey = icon;
        this.iconPressedColorKey = iconPressed;
    }

    public void setCircleCrossfadeColor(String color, float progress, float checkProgress) {
        this.circleCrossfadeColorKey = color;
        this.circleCrossfadeColorProgress = progress;
        this.circleCheckProgress = checkProgress;
        this.miniIconScale = 1.0f;
        if (color != null) {
            initMiniIcons();
        }
    }

    public void setDrawBackground(boolean value) {
        this.drawBackground = value;
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        this.progressRect.set(left, top, right, bottom);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public void setMiniProgressBackgroundColor(int color) {
        this.miniProgressBackgroundPaint.setColor(color);
    }

    public void setProgress(float value, boolean animated) {
        if (this.drawMiniIcon) {
            this.miniMediaActionDrawable.setProgress(value, animated);
        } else {
            this.mediaActionDrawable.setProgress(value, animated);
        }
    }

    public float getProgress() {
        return (this.drawMiniIcon ? this.miniMediaActionDrawable : this.mediaActionDrawable).getProgress();
    }

    private void invalidateParent() {
        int offset = AndroidUtilities.dp(2.0f);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int icon, boolean ifSame, boolean animated) {
        if (ifSame && icon == this.mediaActionDrawable.getCurrentIcon()) {
            return;
        }
        this.mediaActionDrawable.setIcon(icon, animated);
        if (!animated) {
            this.parent.invalidate();
        } else {
            invalidateParent();
        }
    }

    public void setMiniIconScale(float scale) {
        this.miniIconScale = scale;
    }

    public void setMiniIcon(int icon, boolean ifSame, boolean animated) {
        if (icon != 2 && icon != 3 && icon != 4) {
            return;
        }
        if (ifSame && icon == this.miniMediaActionDrawable.getCurrentIcon()) {
            return;
        }
        this.miniMediaActionDrawable.setIcon(icon, animated);
        boolean z = icon != 4 || this.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
        this.drawMiniIcon = z;
        if (z) {
            initMiniIcons();
        }
        if (!animated) {
            this.parent.invalidate();
        } else {
            invalidateParent();
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Bitmap.Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable th) {
            }
        }
    }

    public boolean swapIcon(int icon) {
        return this.mediaActionDrawable.setIcon(icon, false);
    }

    public void setPressed(boolean value, boolean mini) {
        if (mini) {
            this.isPressedMini = value;
        } else {
            this.isPressed = value;
        }
        invalidateParent();
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public float getOverrideAlpha() {
        return this.overrideAlpha;
    }

    public void draw(Canvas canvas) {
        float wholeAlpha;
        int color;
        int centerY;
        int centerX;
        float cy;
        float cx;
        int size;
        int offset;
        float alpha;
        int restore;
        Canvas canvas2;
        Canvas canvas3;
        Canvas canvas4;
        int r;
        if ((this.mediaActionDrawable.getCurrentIcon() == 4 && this.mediaActionDrawable.getTransitionProgress() >= 1.0f) || this.progressRect.isEmpty()) {
            return;
        }
        int currentIcon = this.mediaActionDrawable.getCurrentIcon();
        int prevIcon = this.mediaActionDrawable.getPreviousIcon();
        if (this.backgroundStroke != 0) {
            if (currentIcon == 3) {
                wholeAlpha = 1.0f - this.mediaActionDrawable.getTransitionProgress();
            } else if (prevIcon == 3) {
                wholeAlpha = this.mediaActionDrawable.getTransitionProgress();
            } else {
                wholeAlpha = 1.0f;
            }
        } else if ((currentIcon == 3 || currentIcon == 6 || currentIcon == 10 || currentIcon == 8 || currentIcon == 0) && prevIcon == 4) {
            wholeAlpha = this.mediaActionDrawable.getTransitionProgress();
        } else {
            wholeAlpha = currentIcon != 4 ? 1.0f : 1.0f - this.mediaActionDrawable.getTransitionProgress();
        }
        if (this.isPressedMini && this.circleCrossfadeColorKey == null) {
            String str = this.iconPressedColorKey;
            if (str != null) {
                this.miniMediaActionDrawable.setColor(getThemedColor(str));
            } else {
                this.miniMediaActionDrawable.setColor(this.iconPressedColor);
            }
            String str2 = this.circlePressedColorKey;
            if (str2 != null) {
                this.circleMiniPaint.setColor(getThemedColor(str2));
            } else {
                this.circleMiniPaint.setColor(this.circlePressedColor);
            }
        } else {
            String str3 = this.iconColorKey;
            if (str3 != null) {
                this.miniMediaActionDrawable.setColor(getThemedColor(str3));
            } else {
                this.miniMediaActionDrawable.setColor(this.iconColor);
            }
            String str4 = this.circleColorKey;
            if (str4 == null) {
                this.circleMiniPaint.setColor(this.circleColor);
            } else if (this.circleCrossfadeColorKey != null) {
                this.circleMiniPaint.setColor(AndroidUtilities.getOffsetColor(getThemedColor(str4), getThemedColor(this.circleCrossfadeColorKey), this.circleCrossfadeColorProgress, this.circleCheckProgress));
            } else {
                this.circleMiniPaint.setColor(getThemedColor(str4));
            }
        }
        if (this.isPressed) {
            String str5 = this.iconPressedColorKey;
            if (str5 != null) {
                MediaActionDrawable mediaActionDrawable = this.mediaActionDrawable;
                int themedColor = getThemedColor(str5);
                color = themedColor;
                mediaActionDrawable.setColor(themedColor);
                this.mediaActionDrawable.setBackColor(getThemedColor(this.circlePressedColorKey));
            } else {
                MediaActionDrawable mediaActionDrawable2 = this.mediaActionDrawable;
                int i = this.iconPressedColor;
                color = i;
                mediaActionDrawable2.setColor(i);
                this.mediaActionDrawable.setBackColor(this.circlePressedColor);
            }
            String str6 = this.circlePressedColorKey;
            if (str6 != null) {
                this.circlePaint.setColor(getThemedColor(str6));
            } else {
                this.circlePaint.setColor(this.circlePressedColor);
            }
        } else {
            String str7 = this.iconColorKey;
            if (str7 != null) {
                MediaActionDrawable mediaActionDrawable3 = this.mediaActionDrawable;
                int themedColor2 = getThemedColor(str7);
                color = themedColor2;
                mediaActionDrawable3.setColor(themedColor2);
                this.mediaActionDrawable.setBackColor(getThemedColor(this.circleColorKey));
            } else {
                MediaActionDrawable mediaActionDrawable4 = this.mediaActionDrawable;
                int i2 = this.iconColor;
                color = i2;
                mediaActionDrawable4.setColor(i2);
                this.mediaActionDrawable.setBackColor(this.circleColor);
            }
            String str8 = this.circleColorKey;
            if (str8 != null) {
                this.circlePaint.setColor(getThemedColor(str8));
            } else {
                this.circlePaint.setColor(this.circleColor);
            }
        }
        if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && this.miniDrawCanvas != null) {
            this.miniDrawBitmap.eraseColor(0);
        }
        int originalAlpha = this.circlePaint.getAlpha();
        this.circlePaint.setAlpha((int) (originalAlpha * wholeAlpha * this.overrideAlpha));
        int originalAlpha2 = this.circleMiniPaint.getAlpha();
        this.circleMiniPaint.setAlpha((int) (originalAlpha2 * wholeAlpha * this.overrideAlpha));
        boolean drawCircle = true;
        if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && this.miniDrawCanvas != null) {
            centerX = (int) Math.ceil(this.progressRect.width() / 2.0f);
            centerY = (int) Math.ceil(this.progressRect.height() / 2.0f);
        } else {
            centerX = (int) this.progressRect.centerX();
            centerY = (int) this.progressRect.centerY();
        }
        if (this.overlayImageView.hasBitmapImage()) {
            float alpha2 = this.overlayImageView.getCurrentAlpha();
            this.overlayPaint.setAlpha((int) (this.overrideAlpha * 100.0f * alpha2 * wholeAlpha));
            if (alpha2 >= 1.0f) {
                drawCircle = false;
                r = -1;
            } else {
                int r2 = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int a = Color.alpha(color);
                int rD = (int) ((255 - r2) * alpha2);
                int originalAlpha3 = 255 - g;
                int gD = (int) (originalAlpha3 * alpha2);
                int bD = (int) ((255 - b) * alpha2);
                int aD = (int) ((255 - a) * alpha2);
                int i3 = a + aD;
                int a2 = r2 + rD;
                int rD2 = g + gD;
                int gD2 = b + bD;
                r = Color.argb(i3, a2, rD2, gD2);
                drawCircle = true;
            }
            this.mediaActionDrawable.setColor(r);
            ImageReceiver imageReceiver = this.overlayImageView;
            int i4 = this.circleRadius;
            imageReceiver.setImageCoords(centerX - i4, centerY - i4, i4 * 2, i4 * 2);
        }
        int restore2 = Integer.MIN_VALUE;
        Canvas canvas5 = this.miniDrawCanvas;
        if (canvas5 != null && this.circleCrossfadeColorKey != null && this.circleCheckProgress != 1.0f) {
            restore2 = canvas5.save();
            float scaleMini = 1.0f - ((1.0f - this.circleCheckProgress) * 0.1f);
            this.miniDrawCanvas.scale(scaleMini, scaleMini, centerX, centerY);
        }
        if (drawCircle && this.drawBackground) {
            if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && (canvas4 = this.miniDrawCanvas) != null) {
                canvas4.drawCircle(centerX, centerY, this.circleRadius, this.circlePaint);
            } else if (currentIcon != 4 || wholeAlpha != 0.0f) {
                if (this.backgroundStroke != 0) {
                    canvas.drawCircle(centerX, centerY, this.circleRadius - AndroidUtilities.dp(3.5f), this.circlePaint);
                } else {
                    canvas.drawCircle(centerX, centerY, this.circleRadius, this.circlePaint);
                }
            }
        }
        if (this.overlayImageView.hasBitmapImage()) {
            this.overlayImageView.setAlpha(this.overrideAlpha * wholeAlpha);
            if ((this.drawMiniIcon || this.circleCrossfadeColorKey != null) && (canvas3 = this.miniDrawCanvas) != null) {
                this.overlayImageView.draw(canvas3);
                this.miniDrawCanvas.drawCircle(centerX, centerY, this.circleRadius, this.overlayPaint);
            } else {
                this.overlayImageView.draw(canvas);
                canvas.drawCircle(centerX, centerY, this.circleRadius, this.overlayPaint);
            }
        }
        MediaActionDrawable mediaActionDrawable5 = this.mediaActionDrawable;
        int i5 = this.circleRadius;
        mediaActionDrawable5.setBounds(centerX - i5, centerY - i5, centerX + i5, i5 + centerY);
        this.mediaActionDrawable.setHasOverlayImage(this.overlayImageView.hasBitmapImage());
        if (!this.drawMiniIcon && this.circleCrossfadeColorKey == null) {
            this.mediaActionDrawable.setOverrideAlpha(this.overrideAlpha);
            this.mediaActionDrawable.draw(canvas);
        } else {
            Canvas canvas6 = this.miniDrawCanvas;
            if (canvas6 != null) {
                this.mediaActionDrawable.draw(canvas6);
            } else {
                this.mediaActionDrawable.draw(canvas);
            }
        }
        if (restore2 != Integer.MIN_VALUE && (canvas2 = this.miniDrawCanvas) != null) {
            canvas2.restoreToCount(restore2);
        }
        if (this.drawMiniIcon || this.circleCrossfadeColorKey != null) {
            if (Math.abs(this.progressRect.width() - AndroidUtilities.dp(44.0f)) < AndroidUtilities.density) {
                offset = 0;
                size = 20;
                cx = this.progressRect.centerX() + AndroidUtilities.dp(0 + 16);
                cy = this.progressRect.centerY() + AndroidUtilities.dp(0 + 16);
            } else {
                offset = 2;
                size = 22;
                cx = this.progressRect.centerX() + AndroidUtilities.dp(18.0f);
                cy = AndroidUtilities.dp(18.0f) + this.progressRect.centerY();
            }
            int halfSize = size / 2;
            if (this.drawMiniIcon) {
                alpha = this.miniMediaActionDrawable.getCurrentIcon() != 4 ? 1.0f : 1.0f - this.miniMediaActionDrawable.getTransitionProgress();
                if (alpha == 0.0f) {
                    this.drawMiniIcon = false;
                }
            } else {
                alpha = 1.0f;
            }
            Canvas canvas7 = this.miniDrawCanvas;
            if (canvas7 != null) {
                canvas7.drawCircle(AndroidUtilities.dp(size + 18 + offset), AndroidUtilities.dp(size + 18 + offset), AndroidUtilities.dp(halfSize + 1) * alpha * this.miniIconScale, Theme.checkboxSquare_eraserPaint);
            } else {
                this.miniProgressBackgroundPaint.setColor(this.progressColor);
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
            }
            if (this.miniDrawCanvas != null) {
                canvas.drawBitmap(this.miniDrawBitmap, (int) this.progressRect.left, (int) this.progressRect.top, (Paint) null);
            }
            if (this.miniIconScale >= 1.0f) {
                restore = Integer.MIN_VALUE;
            } else {
                int restore3 = canvas.save();
                float f = this.miniIconScale;
                canvas.scale(f, f, cx, cy);
                restore = restore3;
            }
            canvas.drawCircle(cx, cy, (AndroidUtilities.dp(halfSize) * alpha) + (AndroidUtilities.dp(1.0f) * (1.0f - this.circleCheckProgress)), this.circleMiniPaint);
            if (this.drawMiniIcon) {
                this.miniMediaActionDrawable.setBounds((int) (cx - (AndroidUtilities.dp(halfSize) * alpha)), (int) (cy - (AndroidUtilities.dp(halfSize) * alpha)), (int) ((AndroidUtilities.dp(halfSize) * alpha) + cx), (int) ((AndroidUtilities.dp(halfSize) * alpha) + cy));
                this.miniMediaActionDrawable.draw(canvas);
            }
            if (restore != Integer.MIN_VALUE) {
                canvas.restoreToCount(restore);
            }
        }
    }

    public String getCircleColorKey() {
        return this.circleColorKey;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
