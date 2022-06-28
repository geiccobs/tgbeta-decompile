package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PullForegroundDrawable {
    public static final float SNAP_HEIGHT = 0.85f;
    public static final float endPullParallax = 0.25f;
    public static final long minPullingTime = 200;
    public static final float startPullOverScroll = 0.2f;
    public static final float startPullParallax = 0.45f;
    private ValueAnimator accentRevalAnimatorIn;
    private ValueAnimator accentRevalAnimatorOut;
    private boolean animateOut;
    private boolean animateToColorize;
    private boolean animateToEndText;
    private boolean animateToTextIn;
    private boolean arrowAnimateTo;
    private ValueAnimator arrowRotateAnimator;
    private boolean bounceIn;
    private float bounceProgress;
    private View cell;
    private boolean isOut;
    private RecyclerListView listView;
    private AnimatorSet outAnimator;
    public float outCx;
    public float outCy;
    public float outImageSize;
    public float outOverScroll;
    public float outProgress;
    public float outRadius;
    public float pullProgress;
    private String pullTooltip;
    private String releaseTooltip;
    public int scrollDy;
    private float textInProgress;
    private ValueAnimator textIntAnimator;
    private ValueAnimator textSwipingAnimator;
    private final Paint tooltipTextPaint;
    private float touchSlop;
    private boolean willDraw;
    private String backgroundColorKey = Theme.key_chats_archivePullDownBackground;
    private String backgroundActiveColorKey = Theme.key_chats_archivePullDownBackgroundActive;
    private String avatarBackgroundColorKey = Theme.key_avatar_backgroundArchivedHidden;
    private boolean changeAvatarColor = true;
    private final Paint paintSecondary = new Paint(1);
    private final Paint paintWhite = new Paint(1);
    private final Paint paintBackgroundAccent = new Paint(1);
    private final Paint backgroundPaint = new Paint();
    private final RectF rectF = new RectF();
    private final ArrowDrawable arrowDrawable = new ArrowDrawable();
    private final Path circleClipPath = new Path();
    private float textSwappingProgress = 1.0f;
    private float arrowRotateProgress = 1.0f;
    private float accentRevalProgress = 1.0f;
    private float accentRevalProgressOut = 1.0f;
    private ValueAnimator.AnimatorUpdateListener textSwappingUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda2
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            PullForegroundDrawable.this.m2918lambda$new$0$orgtelegramuiComponentsPullForegroundDrawable(valueAnimator);
        }
    };
    private ValueAnimator.AnimatorUpdateListener textInUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda3
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            PullForegroundDrawable.this.m2919lambda$new$1$orgtelegramuiComponentsPullForegroundDrawable(valueAnimator);
        }
    };
    Runnable textInRunnable = new Runnable() { // from class: org.telegram.ui.Components.PullForegroundDrawable.1
        @Override // java.lang.Runnable
        public void run() {
            PullForegroundDrawable.this.animateToTextIn = true;
            if (PullForegroundDrawable.this.textIntAnimator != null) {
                PullForegroundDrawable.this.textIntAnimator.cancel();
            }
            PullForegroundDrawable.this.textInProgress = 0.0f;
            PullForegroundDrawable.this.textIntAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            PullForegroundDrawable.this.textIntAnimator.addUpdateListener(PullForegroundDrawable.this.textInUpdateListener);
            PullForegroundDrawable.this.textIntAnimator.setInterpolator(new LinearInterpolator());
            PullForegroundDrawable.this.textIntAnimator.setDuration(150L);
            PullForegroundDrawable.this.textIntAnimator.start();
        }
    };
    boolean wasSendCallback = false;

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2918lambda$new$0$orgtelegramuiComponentsPullForegroundDrawable(ValueAnimator animation) {
        this.textSwappingProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2919lambda$new$1$orgtelegramuiComponentsPullForegroundDrawable(ValueAnimator animation) {
        this.textInProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public PullForegroundDrawable(String pullText, String releaseText) {
        TextPaint textPaint = new TextPaint(1);
        this.tooltipTextPaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        ViewConfiguration vc = ViewConfiguration.get(ApplicationLoader.applicationContext);
        this.touchSlop = vc.getScaledTouchSlop();
        this.pullTooltip = pullText;
        this.releaseTooltip = releaseText;
    }

    public static int getMaxOverscroll() {
        return AndroidUtilities.dp(72.0f);
    }

    public void setColors(String background, String active) {
        this.backgroundColorKey = background;
        this.backgroundActiveColorKey = active;
        this.changeAvatarColor = false;
        updateColors();
    }

    public void setCell(View view) {
        this.cell = view;
        updateColors();
    }

    public void updateColors() {
        int backgroundColor = Theme.getColor(this.backgroundColorKey);
        this.tooltipTextPaint.setColor(-1);
        this.paintWhite.setColor(-1);
        this.paintSecondary.setColor(ColorUtils.setAlphaComponent(-1, 100));
        this.backgroundPaint.setColor(backgroundColor);
        this.arrowDrawable.setColor(backgroundColor);
        this.paintBackgroundAccent.setColor(Theme.getColor(this.avatarBackgroundColorKey));
    }

    public void setListView(RecyclerListView listView) {
        this.listView = listView;
    }

    public void drawOverScroll(Canvas canvas) {
        draw(canvas, true);
    }

    public void draw(Canvas canvas) {
        draw(canvas, false);
    }

    protected float getViewOffset() {
        return 0.0f;
    }

    public void draw(Canvas canvas, boolean header) {
        int overscroll;
        int radius;
        float startPullProgress;
        float bounceP;
        int startPadding;
        int startPadding2;
        int radius2;
        if (!this.willDraw || this.isOut || this.cell == null || this.listView == null) {
            return;
        }
        int startPadding3 = AndroidUtilities.dp(28.0f);
        int smallMargin = AndroidUtilities.dp(8.0f);
        int radius3 = AndroidUtilities.dp(9.0f);
        int diameter = AndroidUtilities.dp(18.0f);
        int overscroll2 = (int) getViewOffset();
        float f = this.pullProgress;
        int visibleHeight = (int) (this.cell.getHeight() * f);
        float bounceP2 = this.bounceIn ? (this.bounceProgress * 0.07f) - 0.05f : this.bounceProgress * 0.02f;
        updateTextProgress(f);
        float outProgressHalf = this.outProgress * 2.0f;
        if (outProgressHalf > 1.0f) {
            outProgressHalf = 1.0f;
        }
        float cX = this.outCx;
        float cY = this.outCy;
        if (header) {
            cY += overscroll2;
        }
        int smallCircleX = startPadding3 + radius3;
        int smallCircleY = (this.cell.getMeasuredHeight() - smallMargin) - radius3;
        if (header) {
            smallCircleY += overscroll2;
        }
        float startPullProgress2 = visibleHeight > diameter + (smallMargin * 2) ? 1.0f : visibleHeight / (diameter + (smallMargin * 2));
        canvas.save();
        if (!header) {
            radius = radius3;
            overscroll = overscroll2;
        } else {
            radius = radius3;
            overscroll = overscroll2;
            canvas.clipRect(0, 0, this.listView.getMeasuredWidth(), overscroll2 + 1);
        }
        if (this.outProgress == 0.0f) {
            if (this.accentRevalProgress == 1.0f || this.accentRevalProgressOut == 1.0f) {
                startPadding = startPadding3;
                bounceP = bounceP2;
                startPullProgress = startPullProgress2;
            } else {
                canvas.drawPaint(this.backgroundPaint);
                startPadding = startPadding3;
                bounceP = bounceP2;
                startPullProgress = startPullProgress2;
            }
        } else {
            float f2 = this.outRadius;
            float f3 = this.outRadius;
            startPadding = startPadding3;
            float outBackgroundRadius = f2 + ((this.cell.getWidth() - f3) * (1.0f - this.outProgress)) + (f3 * bounceP2);
            if (this.accentRevalProgress != 1.0f && this.accentRevalProgressOut != 1.0f) {
                canvas.drawCircle(cX, cY, outBackgroundRadius, this.backgroundPaint);
            }
            this.circleClipPath.reset();
            bounceP = bounceP2;
            startPullProgress = startPullProgress2;
            this.rectF.set(cX - outBackgroundRadius, cY - outBackgroundRadius, cX + outBackgroundRadius, cY + outBackgroundRadius);
            this.circleClipPath.addOval(this.rectF, Path.Direction.CW);
            canvas.clipPath(this.circleClipPath);
        }
        if (this.animateToColorize) {
            if (this.accentRevalProgressOut > this.accentRevalProgress) {
                canvas.save();
                float f4 = this.outProgress;
                canvas.translate((cX - smallCircleX) * f4, (cY - smallCircleY) * f4);
                canvas.drawCircle(smallCircleX, smallCircleY, this.cell.getWidth() * this.accentRevalProgressOut, this.backgroundPaint);
                canvas.restore();
            }
            if (this.accentRevalProgress > 0.0f) {
                canvas.save();
                float f5 = this.outProgress;
                canvas.translate((cX - smallCircleX) * f5, (cY - smallCircleY) * f5);
                canvas.drawCircle(smallCircleX, smallCircleY, this.cell.getWidth() * this.accentRevalProgress, this.paintBackgroundAccent);
                canvas.restore();
            }
        } else {
            if (this.accentRevalProgress > this.accentRevalProgressOut) {
                canvas.save();
                float f6 = this.outProgress;
                canvas.translate((cX - smallCircleX) * f6, (cY - smallCircleY) * f6);
                canvas.drawCircle(smallCircleX, smallCircleY, this.cell.getWidth() * this.accentRevalProgress, this.paintBackgroundAccent);
                canvas.restore();
            }
            if (this.accentRevalProgressOut > 0.0f) {
                canvas.save();
                float f7 = this.outProgress;
                canvas.translate((cX - smallCircleX) * f7, (cY - smallCircleY) * f7);
                canvas.drawCircle(smallCircleX, smallCircleY, this.cell.getWidth() * this.accentRevalProgressOut, this.backgroundPaint);
                canvas.restore();
            }
        }
        if (visibleHeight <= (smallMargin * 2) + diameter) {
            radius2 = radius;
            startPadding2 = startPadding;
        } else {
            this.paintSecondary.setAlpha((int) ((1.0f - outProgressHalf) * 0.4f * startPullProgress * 255.0f));
            if (header) {
                startPadding2 = startPadding;
                this.rectF.set(startPadding2, smallMargin, startPadding2 + diameter, smallMargin + overscroll + radius);
            } else {
                startPadding2 = startPadding;
                this.rectF.set(startPadding2, ((this.cell.getHeight() - visibleHeight) + smallMargin) - overscroll, startPadding2 + diameter, this.cell.getHeight() - smallMargin);
            }
            radius2 = radius;
            canvas.drawRoundRect(this.rectF, radius2, radius2, this.paintSecondary);
        }
        if (header) {
            canvas.restore();
            return;
        }
        if (this.outProgress == 0.0f) {
            this.paintWhite.setAlpha((int) (startPullProgress * 255.0f));
            canvas.drawCircle(smallCircleX, smallCircleY, radius2, this.paintWhite);
            int ih = this.arrowDrawable.getIntrinsicHeight();
            int iw = this.arrowDrawable.getIntrinsicWidth();
            this.arrowDrawable.setBounds(smallCircleX - (iw >> 1), smallCircleY - (ih >> 1), smallCircleX + (iw >> 1), smallCircleY + (ih >> 1));
            float rotateProgress = 1.0f - this.arrowRotateProgress;
            if (rotateProgress < 0.0f) {
                rotateProgress = 0.0f;
            }
            float rotateProgress2 = 1.0f - rotateProgress;
            canvas.save();
            canvas.rotate(180.0f * rotateProgress2, smallCircleX, smallCircleY);
            canvas.translate(0.0f, (AndroidUtilities.dpf2(1.0f) * 1.0f) - rotateProgress2);
            this.arrowDrawable.setColor(this.animateToColorize ? this.paintBackgroundAccent.getColor() : Theme.getColor(this.backgroundColorKey));
            this.arrowDrawable.draw(canvas);
            canvas.restore();
        }
        if (this.pullProgress > 0.0f) {
            textIn();
        }
        float textY = (this.cell.getHeight() - (((smallMargin * 2) + diameter) / 2.0f)) + AndroidUtilities.dp(6.0f);
        this.tooltipTextPaint.setAlpha((int) (this.textSwappingProgress * 255.0f * startPullProgress * this.textInProgress));
        float textCx = (this.cell.getWidth() / 2.0f) - AndroidUtilities.dp(2.0f);
        float f8 = this.textSwappingProgress;
        if (f8 > 0.0f && f8 < 1.0f) {
            canvas.save();
            float scale = (this.textSwappingProgress * 0.2f) + 0.8f;
            canvas.scale(scale, scale, textCx, (AndroidUtilities.dp(16.0f) * (1.0f - this.textSwappingProgress)) + textY);
        }
        canvas.drawText(this.pullTooltip, textCx, (AndroidUtilities.dp(8.0f) * (1.0f - this.textSwappingProgress)) + textY, this.tooltipTextPaint);
        float f9 = this.textSwappingProgress;
        if (f9 > 0.0f && f9 < 1.0f) {
            canvas.restore();
        }
        float f10 = this.textSwappingProgress;
        if (f10 > 0.0f && f10 < 1.0f) {
            canvas.save();
            float scale2 = ((1.0f - this.textSwappingProgress) * 0.1f) + 0.9f;
            canvas.scale(scale2, scale2, textCx, textY - (AndroidUtilities.dp(8.0f) * this.textSwappingProgress));
        }
        this.tooltipTextPaint.setAlpha((int) ((1.0f - this.textSwappingProgress) * 255.0f * startPullProgress * this.textInProgress));
        canvas.drawText(this.releaseTooltip, textCx, textY - (AndroidUtilities.dp(8.0f) * this.textSwappingProgress), this.tooltipTextPaint);
        float f11 = this.textSwappingProgress;
        if (f11 > 0.0f && f11 < 1.0f) {
            canvas.restore();
        }
        canvas.restore();
        if (this.changeAvatarColor && this.outProgress > 0.0f) {
            canvas.save();
            int iw2 = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
            int startCx = startPadding2 + radius2;
            int startCy = (this.cell.getHeight() - smallMargin) - radius2;
            float scaleStart = AndroidUtilities.dp(24.0f) / iw2;
            float f12 = this.outProgress;
            float scale3 = scaleStart + ((1.0f - scaleStart) * f12) + bounceP;
            int smallMargin2 = (int) cX;
            int x = (int) cY;
            canvas.translate((startCx - cX) * (1.0f - f12), (startCy - cY) * (1.0f - f12));
            canvas.scale(scale3, scale3, cX, cY);
            Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
            if (!Theme.dialogs_archiveAvatarDrawableRecolored) {
                Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                Theme.dialogs_archiveAvatarDrawableRecolored = true;
            }
            Theme.dialogs_archiveAvatarDrawable.setBounds((int) (cX - (iw2 / 2.0f)), (int) (cY - (iw2 / 2.0f)), (int) ((iw2 / 2.0f) + cX), (int) ((iw2 / 2.0f) + cY));
            Theme.dialogs_archiveAvatarDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void updateTextProgress(float pullProgress) {
        boolean endText = pullProgress > 0.85f;
        float f = 1.0f;
        if (this.animateToEndText != endText) {
            this.animateToEndText = endText;
            if (this.textInProgress == 0.0f) {
                ValueAnimator valueAnimator = this.textSwipingAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.textSwappingProgress = endText ? 0.0f : 1.0f;
            } else {
                ValueAnimator valueAnimator2 = this.textSwipingAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.textSwappingProgress;
                fArr[1] = endText ? 0.0f : 1.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.textSwipingAnimator = ofFloat;
                ofFloat.addUpdateListener(this.textSwappingUpdateListener);
                this.textSwipingAnimator.setInterpolator(new LinearInterpolator());
                this.textSwipingAnimator.setDuration(170L);
                this.textSwipingAnimator.start();
            }
        }
        if (endText != this.arrowAnimateTo) {
            this.arrowAnimateTo = endText;
            ValueAnimator valueAnimator3 = this.arrowRotateAnimator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
            }
            float[] fArr2 = new float[2];
            fArr2[0] = this.arrowRotateProgress;
            if (this.arrowAnimateTo) {
                f = 0.0f;
            }
            fArr2[1] = f;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(fArr2);
            this.arrowRotateAnimator = ofFloat2;
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda7
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                    PullForegroundDrawable.this.m2923xbc2e1f31(valueAnimator4);
                }
            });
            this.arrowRotateAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.arrowRotateAnimator.setDuration(250L);
            this.arrowRotateAnimator.start();
        }
    }

    /* renamed from: lambda$updateTextProgress$2$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2923xbc2e1f31(ValueAnimator animation) {
        this.arrowRotateProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public void colorize(boolean colorize) {
        if (this.animateToColorize != colorize) {
            this.animateToColorize = colorize;
            if (colorize) {
                ValueAnimator valueAnimator = this.accentRevalAnimatorIn;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.accentRevalAnimatorIn = null;
                }
                this.accentRevalProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.accentRevalAnimatorIn = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        PullForegroundDrawable.this.m2916x8521df60(valueAnimator2);
                    }
                });
                this.accentRevalAnimatorIn.setInterpolator(AndroidUtilities.accelerateInterpolator);
                this.accentRevalAnimatorIn.setDuration(230L);
                this.accentRevalAnimatorIn.start();
                return;
            }
            ValueAnimator valueAnimator2 = this.accentRevalAnimatorOut;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.accentRevalAnimatorOut = null;
            }
            this.accentRevalProgressOut = 0.0f;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.accentRevalAnimatorOut = ofFloat2;
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    PullForegroundDrawable.this.m2917xc8acfd21(valueAnimator3);
                }
            });
            this.accentRevalAnimatorOut.setInterpolator(AndroidUtilities.accelerateInterpolator);
            this.accentRevalAnimatorOut.setDuration(230L);
            this.accentRevalAnimatorOut.start();
        }
    }

    /* renamed from: lambda$colorize$3$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2916x8521df60(ValueAnimator animation) {
        this.accentRevalProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    /* renamed from: lambda$colorize$4$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2917xc8acfd21(ValueAnimator animation) {
        this.accentRevalProgressOut = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    private void textIn() {
        if (!this.animateToTextIn) {
            if (Math.abs(this.scrollDy) < this.touchSlop * 0.5f) {
                if (!this.wasSendCallback) {
                    this.textInProgress = 1.0f;
                    this.animateToTextIn = true;
                    return;
                }
                return;
            }
            this.wasSendCallback = true;
            this.cell.removeCallbacks(this.textInRunnable);
            this.cell.postDelayed(this.textInRunnable, 200L);
        }
    }

    public void startOutAnimation() {
        if (this.animateOut || this.listView == null) {
            return;
        }
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
        this.animateOut = true;
        this.bounceIn = true;
        this.bounceProgress = 0.0f;
        this.outOverScroll = this.listView.getTranslationY() / AndroidUtilities.dp(100.0f);
        ValueAnimator out = ValueAnimator.ofFloat(0.0f, 1.0f);
        out.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PullForegroundDrawable.this.m2920x17d2e0cf(valueAnimator);
            }
        });
        out.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        out.setDuration(250L);
        ValueAnimator bounceIn = ValueAnimator.ofFloat(0.0f, 1.0f);
        bounceIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PullForegroundDrawable.this.m2921x5b5dfe90(valueAnimator);
            }
        });
        bounceIn.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        bounceIn.setDuration(150L);
        ValueAnimator bounceOut = ValueAnimator.ofFloat(1.0f, 0.0f);
        bounceOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PullForegroundDrawable.this.m2922x9ee91c51(valueAnimator);
            }
        });
        bounceOut.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        bounceOut.setDuration(135L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.outAnimator = animatorSet2;
        animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PullForegroundDrawable.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PullForegroundDrawable.this.doNotShow();
            }
        });
        AnimatorSet bounce = new AnimatorSet();
        bounce.playSequentially(bounceIn, bounceOut);
        bounce.setStartDelay(180L);
        this.outAnimator.playTogether(out, bounce);
        this.outAnimator.start();
    }

    /* renamed from: lambda$startOutAnimation$5$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2920x17d2e0cf(ValueAnimator animation) {
        setOutProgress(((Float) animation.getAnimatedValue()).floatValue());
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$startOutAnimation$6$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2921x5b5dfe90(ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.bounceIn = true;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$startOutAnimation$7$org-telegram-ui-Components-PullForegroundDrawable */
    public /* synthetic */ void m2922x9ee91c51(ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.bounceIn = false;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    private void setOutProgress(float value) {
        this.outProgress = value;
        int color = ColorUtils.blendARGB(Theme.getNonAnimatedColor(this.avatarBackgroundColorKey), Theme.getNonAnimatedColor(this.backgroundActiveColorKey), 1.0f - this.outProgress);
        this.paintBackgroundAccent.setColor(color);
        if (this.changeAvatarColor && isDraw()) {
            Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", color);
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", color);
            Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
        }
    }

    public void doNotShow() {
        ValueAnimator valueAnimator = this.textSwipingAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.textIntAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        View view = this.cell;
        if (view != null) {
            view.removeCallbacks(this.textInRunnable);
        }
        ValueAnimator valueAnimator3 = this.accentRevalAnimatorIn;
        if (valueAnimator3 != null) {
            valueAnimator3.cancel();
        }
        this.textSwappingProgress = 1.0f;
        this.arrowRotateProgress = 1.0f;
        this.animateToEndText = false;
        this.arrowAnimateTo = false;
        this.animateToTextIn = false;
        this.wasSendCallback = false;
        this.textInProgress = 0.0f;
        this.isOut = true;
        setOutProgress(1.0f);
        this.animateToColorize = false;
        this.accentRevalProgress = 0.0f;
    }

    public void showHidden() {
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
        setOutProgress(0.0f);
        this.isOut = false;
        this.animateOut = false;
    }

    public void destroyView() {
        this.cell = null;
        ValueAnimator valueAnimator = this.textSwipingAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
    }

    public boolean isDraw() {
        return this.willDraw && !this.isOut;
    }

    public void setWillDraw(boolean b) {
        this.willDraw = b;
    }

    public void resetText() {
        ValueAnimator valueAnimator = this.textIntAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        View view = this.cell;
        if (view != null) {
            view.removeCallbacks(this.textInRunnable);
        }
        this.textInProgress = 0.0f;
        this.animateToTextIn = false;
        this.wasSendCallback = false;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /* loaded from: classes5.dex */
    public class ArrowDrawable extends Drawable {
        private float lastDensity;
        private Path path = new Path();
        private Paint paint = new Paint(1);

        public ArrowDrawable() {
            PullForegroundDrawable.this = r2;
            updatePath();
        }

        private void updatePath() {
            int h = AndroidUtilities.dp(18.0f);
            this.path.reset();
            this.path.moveTo(h >> 1, AndroidUtilities.dpf2(4.98f));
            this.path.lineTo(AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(h - AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(h >> 1, AndroidUtilities.dpf2(4.98f));
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
            this.lastDensity = AndroidUtilities.density;
        }

        public void setColor(int color) {
            this.paint.setColor(color);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getIntrinsicHeight();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (this.lastDensity != AndroidUtilities.density) {
                updatePath();
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            canvas.drawPath(this.path, this.paint);
            int h = AndroidUtilities.dp(18.0f);
            canvas.drawRect(AndroidUtilities.dpf2(7.56f), AndroidUtilities.dpf2(8.0f), h - AndroidUtilities.dpf2(7.56f), AndroidUtilities.dpf2(11.1f), this.paint);
            canvas.restore();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }
    }
}
