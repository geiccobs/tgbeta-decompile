package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PinnedLineView extends View {
    float animateFromPosition;
    int animateFromTotal;
    int animateToPosition;
    int animateToTotal;
    boolean animationInProgress;
    float animationProgress;
    ValueAnimator animator;
    private int color;
    private int lineHFrom;
    private int lineHTo;
    boolean replaceInProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private float startOffsetFrom;
    private float startOffsetTo;
    int selectedPosition = -1;
    int totalCount = 0;
    RectF rectF = new RectF();
    Paint paint = new Paint(1);
    Paint selectedPaint = new Paint(1);
    private int nextPosition = -1;
    Paint fadePaint = new Paint();
    Paint fadePaint2 = new Paint();

    public PinnedLineView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.selectedPaint.setStyle(Paint.Style.FILL);
        this.selectedPaint.setStrokeCap(Paint.Cap.ROUND);
        LinearGradient gradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(6.0f), new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
        this.fadePaint.setShader(gradient);
        this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        LinearGradient gradient2 = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(6.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
        this.fadePaint2.setShader(gradient2);
        this.fadePaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        updateColors();
    }

    public void updateColors() {
        int themedColor = getThemedColor(Theme.key_chat_topPanelLine);
        this.color = themedColor;
        this.paint.setColor(ColorUtils.setAlphaComponent(themedColor, (int) ((Color.alpha(themedColor) / 255.0f) * 112.0f)));
        this.selectedPaint.setColor(this.color);
    }

    public void selectPosition(int position) {
        if (this.replaceInProgress) {
            this.nextPosition = position;
            return;
        }
        if (this.animationInProgress) {
            if (this.animateToPosition == position) {
                return;
            }
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float f = this.animateFromPosition;
            float f2 = this.animationProgress;
            this.animateFromPosition = (f * (1.0f - f2)) + (this.animateToPosition * f2);
        } else {
            this.animateFromPosition = this.selectedPosition;
        }
        if (position != this.selectedPosition) {
            this.animateToPosition = position;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PinnedLineView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    PinnedLineView.this.m2859xd2955f1c(valueAnimator2);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PinnedLineView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PinnedLineView.this.animationInProgress = false;
                    PinnedLineView pinnedLineView = PinnedLineView.this;
                    pinnedLineView.selectedPosition = pinnedLineView.animateToPosition;
                    PinnedLineView.this.invalidate();
                    if (PinnedLineView.this.nextPosition >= 0) {
                        PinnedLineView pinnedLineView2 = PinnedLineView.this;
                        pinnedLineView2.selectPosition(pinnedLineView2.nextPosition);
                        PinnedLineView.this.nextPosition = -1;
                    }
                }
            });
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.setDuration(220L);
            this.animator.start();
        }
    }

    /* renamed from: lambda$selectPosition$0$org-telegram-ui-Components-PinnedLineView */
    public /* synthetic */ void m2859xd2955f1c(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        float lineH;
        float startOffset;
        int i2;
        int i3;
        int i4;
        int i5;
        float offset1;
        super.onDraw(canvas);
        if (this.selectedPosition < 0 || (i = this.totalCount) == 0) {
            return;
        }
        if (this.replaceInProgress) {
            i = Math.max(this.animateFromTotal, this.animateToTotal);
        }
        boolean drawFade = i > 3;
        if (drawFade) {
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
        }
        int viewPadding = AndroidUtilities.dp(8.0f);
        if (this.replaceInProgress) {
            float f = this.animationProgress;
            lineH = (this.lineHFrom * (1.0f - f)) + (this.lineHTo * f);
        } else if (this.totalCount != 0) {
            lineH = (getMeasuredHeight() - (viewPadding * 2)) / Math.min(this.totalCount, 3);
        } else {
            return;
        }
        float f2 = 0.0f;
        if (lineH == 0.0f) {
            return;
        }
        float linePadding = AndroidUtilities.dpf2(0.7f);
        if (this.replaceInProgress) {
            float f3 = this.startOffsetFrom;
            float f4 = this.animationProgress;
            startOffset = (f3 * (1.0f - f4)) + (this.startOffsetTo * f4);
        } else {
            if (this.animationInProgress) {
                float offset2 = (this.animateToPosition - 1) * lineH;
                float f5 = this.animationProgress;
                offset1 = ((1.0f - f5) * (this.animateFromPosition - 1.0f) * lineH) + (f5 * offset2);
            } else {
                offset1 = (this.selectedPosition - 1) * lineH;
            }
            if (offset1 < 0.0f) {
                startOffset = 0.0f;
            } else {
                startOffset = (((float) viewPadding) + (((float) (this.totalCount - 1)) * lineH)) - offset1 < ((float) (getMeasuredHeight() - viewPadding)) - lineH ? (viewPadding + ((this.totalCount - 1) * lineH)) - ((getMeasuredHeight() - viewPadding) - lineH) : offset1;
            }
        }
        float r = getMeasuredWidth() / 2.0f;
        int start = Math.max(0, (int) (((viewPadding + startOffset) / lineH) - 1.0f));
        int end = Math.min(start + 6, this.replaceInProgress ? Math.max(this.animateFromTotal, this.animateToTotal) : this.totalCount);
        int i6 = start;
        while (i6 < end) {
            float startY = (viewPadding + (i6 * lineH)) - startOffset;
            if (startY + lineH >= f2 && startY <= getMeasuredHeight()) {
                this.rectF.set(f2, startY + linePadding, getMeasuredWidth(), (startY + lineH) - linePadding);
                boolean z = this.replaceInProgress;
                if (z && i6 >= this.animateToTotal) {
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i4) / 255.0f) * 76.0f * (1.0f - this.animationProgress))));
                    canvas.drawRoundRect(this.rectF, r, r, this.paint);
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i5) / 255.0f) * 76.0f)));
                } else if (!z || i6 < this.animateFromTotal) {
                    canvas.drawRoundRect(this.rectF, r, r, this.paint);
                } else {
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i2) / 255.0f) * 76.0f * this.animationProgress)));
                    canvas.drawRoundRect(this.rectF, r, r, this.paint);
                    this.paint.setColor(ColorUtils.setAlphaComponent(this.color, (int) ((Color.alpha(i3) / 255.0f) * 76.0f)));
                }
            }
            i6++;
            f2 = 0.0f;
        }
        if (this.animationInProgress) {
            float f6 = this.animateFromPosition;
            float f7 = this.animationProgress;
            float startY2 = (viewPadding + (((f6 * (1.0f - f7)) + (this.animateToPosition * f7)) * lineH)) - startOffset;
            this.rectF.set(0.0f, startY2 + linePadding, getMeasuredWidth(), (startY2 + lineH) - linePadding);
            canvas.drawRoundRect(this.rectF, r, r, this.selectedPaint);
        } else {
            float startY3 = (viewPadding + (this.selectedPosition * lineH)) - startOffset;
            this.rectF.set(0.0f, startY3 + linePadding, getMeasuredWidth(), (startY3 + lineH) - linePadding);
            canvas.drawRoundRect(this.rectF, r, r, this.selectedPaint);
        }
        if (drawFade) {
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(6.0f), this.fadePaint);
            canvas.drawRect(0.0f, getMeasuredHeight() - AndroidUtilities.dp(6.0f), getMeasuredWidth(), getMeasuredHeight(), this.fadePaint);
            canvas.translate(0.0f, getMeasuredHeight() - AndroidUtilities.dp(6.0f));
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(6.0f), this.fadePaint2);
        }
    }

    public void set(int position, int totalCount, boolean animated) {
        int i;
        int i2;
        int i3 = this.selectedPosition;
        if (i3 < 0 || totalCount == 0 || this.totalCount == 0) {
            animated = false;
        }
        if (!animated) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.selectedPosition = position;
            this.totalCount = totalCount;
            invalidate();
        } else if (this.totalCount != totalCount || (Math.abs(i3 - position) > 2 && !this.animationInProgress && !this.replaceInProgress)) {
            ValueAnimator valueAnimator2 = this.animator;
            if (valueAnimator2 != null) {
                this.nextPosition = 0;
                valueAnimator2.cancel();
            }
            int viewPadding = AndroidUtilities.dp(8.0f);
            this.lineHFrom = (getMeasuredHeight() - (viewPadding * 2)) / Math.min(this.totalCount, 3);
            this.lineHTo = (getMeasuredHeight() - (viewPadding * 2)) / Math.min(totalCount, 3);
            float f = (this.selectedPosition - 1) * this.lineHFrom;
            this.startOffsetFrom = f;
            if (f < 0.0f) {
                this.startOffsetFrom = 0.0f;
            } else {
                int i4 = this.lineHFrom;
                if ((((this.totalCount - 1) * i) + viewPadding) - f < (getMeasuredHeight() - viewPadding) - i4) {
                    this.startOffsetFrom = (((this.totalCount - 1) * i4) + viewPadding) - ((getMeasuredHeight() - viewPadding) - this.lineHFrom);
                }
            }
            float f2 = (position - 1) * this.lineHTo;
            this.startOffsetTo = f2;
            if (f2 < 0.0f) {
                this.startOffsetTo = 0.0f;
            } else {
                int i5 = this.lineHTo;
                if ((((totalCount - 1) * i2) + viewPadding) - f2 < (getMeasuredHeight() - viewPadding) - i5) {
                    this.startOffsetTo = (((totalCount - 1) * i5) + viewPadding) - ((getMeasuredHeight() - viewPadding) - this.lineHTo);
                }
            }
            this.animateFromPosition = this.selectedPosition;
            this.animateToPosition = position;
            this.selectedPosition = position;
            this.animateFromTotal = this.totalCount;
            this.animateToTotal = totalCount;
            this.totalCount = totalCount;
            this.replaceInProgress = true;
            this.animationInProgress = true;
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PinnedLineView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    PinnedLineView.this.m2860lambda$set$1$orgtelegramuiComponentsPinnedLineView(valueAnimator3);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PinnedLineView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PinnedLineView.this.replaceInProgress = false;
                    PinnedLineView.this.animationInProgress = false;
                    PinnedLineView.this.invalidate();
                    if (PinnedLineView.this.nextPosition >= 0) {
                        PinnedLineView pinnedLineView = PinnedLineView.this;
                        pinnedLineView.selectPosition(pinnedLineView.nextPosition);
                        PinnedLineView.this.nextPosition = -1;
                    }
                }
            });
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.setDuration(220L);
            this.animator.start();
        } else {
            selectPosition(position);
        }
    }

    /* renamed from: lambda$set$1$org-telegram-ui-Components-PinnedLineView */
    public /* synthetic */ void m2860lambda$set$1$orgtelegramuiComponentsPinnedLineView(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
