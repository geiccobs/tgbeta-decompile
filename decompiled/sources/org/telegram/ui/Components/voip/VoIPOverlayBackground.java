package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
/* loaded from: classes5.dex */
public class VoIPOverlayBackground extends ImageView {
    ValueAnimator animator;
    int blackoutColor = ColorUtils.setAlphaComponent(-16777216, 102);
    float blackoutProgress;
    boolean imageSet;
    boolean showBlackout;

    public VoIPOverlayBackground(Context context) {
        super(context);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        float f = this.blackoutProgress;
        if (f == 1.0f) {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 102));
        } else if (f == 0.0f) {
            setImageAlpha(255);
            super.onDraw(canvas);
        } else {
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (f * 102.0f)));
            setImageAlpha((int) ((1.0f - this.blackoutProgress) * 255.0f));
            super.onDraw(canvas);
        }
    }

    public void setBackground(final ImageReceiver.BitmapHolder src) {
        new Thread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPOverlayBackground$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VoIPOverlayBackground.this.m3263xc59027ae(src);
            }
        }).start();
    }

    /* renamed from: lambda$setBackground$1$org-telegram-ui-Components-voip-VoIPOverlayBackground */
    public /* synthetic */ void m3263xc59027ae(final ImageReceiver.BitmapHolder src) {
        try {
            final Bitmap blur1 = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blur1);
            canvas.drawBitmap(src.bitmap, (Rect) null, new Rect(0, 0, 150, 150), new Paint(2));
            Utilities.blurBitmap(blur1, 3, 0, blur1.getWidth(), blur1.getHeight(), blur1.getRowBytes());
            Palette palette = Palette.from(src.bitmap).generate();
            Paint paint = new Paint();
            paint.setColor((palette.getDarkMutedColor(-11242343) & ViewCompat.MEASURED_SIZE_MASK) | 1140850688);
            canvas.drawColor(637534208);
            canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), paint);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPOverlayBackground$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPOverlayBackground.this.m3262x9c3bd26d(blur1, src);
                }
            });
        } catch (Throwable th) {
        }
    }

    /* renamed from: lambda$setBackground$0$org-telegram-ui-Components-voip-VoIPOverlayBackground */
    public /* synthetic */ void m3262x9c3bd26d(Bitmap blur1, ImageReceiver.BitmapHolder src) {
        setImageBitmap(blur1);
        this.imageSet = true;
        src.release();
    }

    public void setShowBlackout(boolean showBlackout, boolean animated) {
        if (this.showBlackout == showBlackout) {
            return;
        }
        this.showBlackout = showBlackout;
        float f = 1.0f;
        if (!animated) {
            if (!showBlackout) {
                f = 0.0f;
            }
            this.blackoutProgress = f;
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = this.blackoutProgress;
        if (!showBlackout) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPOverlayBackground$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPOverlayBackground.this.m3264x593fed8d(valueAnimator);
            }
        });
        animator.setDuration(150L).start();
    }

    /* renamed from: lambda$setShowBlackout$2$org-telegram-ui-Components-voip-VoIPOverlayBackground */
    public /* synthetic */ void m3264x593fed8d(ValueAnimator valueAnimator) {
        this.blackoutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }
}
