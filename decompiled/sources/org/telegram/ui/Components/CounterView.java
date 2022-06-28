package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CounterView;
/* loaded from: classes5.dex */
public class CounterView extends View {
    public CounterDrawable counterDrawable;
    private final Theme.ResourcesProvider resourcesProvider;

    public CounterView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setVisibility(8);
        CounterDrawable counterDrawable = new CounterDrawable(this, true, resourcesProvider);
        this.counterDrawable = counterDrawable;
        counterDrawable.updateVisibility = true;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.counterDrawable.setSize(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.counterDrawable.draw(canvas);
    }

    public void setColors(String textKey, String circleKey) {
        this.counterDrawable.textColorKey = textKey;
        this.counterDrawable.circleColorKey = circleKey;
    }

    public void setGravity(int gravity) {
        this.counterDrawable.gravity = gravity;
    }

    public void setReverse(boolean b) {
        this.counterDrawable.reverseAnimation = b;
    }

    public void setCount(int count, boolean animated) {
        this.counterDrawable.setCount(count, animated);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes5.dex */
    public static class CounterDrawable {
        private static final int ANIMATION_TYPE_IN = 0;
        private static final int ANIMATION_TYPE_OUT = 1;
        private static final int ANIMATION_TYPE_REPLACE = 2;
        public static final int TYPE_CHAT_PULLING_DOWN = 1;
        public static final int TYPE_CHAT_REACTIONS = 2;
        public static final int TYPE_DEFAULT = 0;
        public boolean addServiceGradient;
        private int circleColor;
        public Paint circlePaint;
        private StaticLayout countAnimationInLayout;
        private boolean countAnimationIncrement;
        private StaticLayout countAnimationStableLayout;
        private ValueAnimator countAnimator;
        private StaticLayout countLayout;
        float countLeft;
        private StaticLayout countOldLayout;
        private int countWidth;
        private int countWidthOld;
        int currentCount;
        private boolean drawBackground;
        public float horizontalPadding;
        int lastH;
        private View parent;
        private final Theme.ResourcesProvider resourcesProvider;
        private boolean reverseAnimation;
        public boolean shortFormat;
        private int textColor;
        public boolean updateVisibility;
        int width;
        float x;
        int animationType = -1;
        public TextPaint textPaint = new TextPaint(1);
        public RectF rectF = new RectF();
        public float countChangeProgress = 1.0f;
        private String textColorKey = Theme.key_chat_goDownButtonCounter;
        private String circleColorKey = Theme.key_chat_goDownButtonCounterBackground;
        public int gravity = 17;
        int type = 0;

        public CounterDrawable(View parent, boolean drawBackground, Theme.ResourcesProvider resourcesProvider) {
            this.drawBackground = true;
            this.parent = parent;
            this.resourcesProvider = resourcesProvider;
            this.drawBackground = drawBackground;
            if (drawBackground) {
                Paint paint = new Paint(1);
                this.circlePaint = paint;
                paint.setColor(-16777216);
            }
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        }

        public void setSize(int h, int w) {
            if (h != this.lastH) {
                int count = this.currentCount;
                this.currentCount = -1;
                setCount(count, this.animationType == 0);
                this.lastH = h;
            }
            this.width = w;
        }

        private void drawInternal(Canvas canvas) {
            float countTop = (this.lastH - AndroidUtilities.dp(23.0f)) / 2.0f;
            updateX(this.countWidth);
            RectF rectF = this.rectF;
            float f = this.x;
            rectF.set(f, countTop, this.countWidth + f + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f) + countTop);
            if (this.circlePaint != null && this.drawBackground) {
                canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, this.circlePaint);
                if (this.addServiceGradient && Theme.hasGradientService()) {
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.chat_actionBackgroundGradientDarkenPaint);
                }
            }
            if (this.countLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft, AndroidUtilities.dp(4.0f) + countTop);
                this.countLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void setCount(int count, boolean animated) {
            boolean animated2;
            View view;
            View view2;
            if (count == this.currentCount) {
                return;
            }
            ValueAnimator valueAnimator = this.countAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (count > 0 && this.updateVisibility && (view2 = this.parent) != null) {
                view2.setVisibility(0);
            }
            if (Math.abs(count - this.currentCount) <= 99) {
                animated2 = animated;
            } else {
                animated2 = false;
            }
            if (!animated2) {
                this.currentCount = count;
                if (count == 0) {
                    if (this.updateVisibility && (view = this.parent) != null) {
                        view.setVisibility(8);
                        return;
                    }
                    return;
                }
                String newStr = getStringOfCCount(count);
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(this.textPaint.measureText(newStr)));
                this.countLayout = new StaticLayout(newStr, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                View view3 = this.parent;
                if (view3 != null) {
                    view3.invalidate();
                }
            }
            String newStr2 = getStringOfCCount(count);
            if (animated2) {
                ValueAnimator valueAnimator2 = this.countAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                this.countChangeProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.countAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.CounterView$CounterDrawable$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                        CounterView.CounterDrawable.this.m2543x2f809de4(valueAnimator3);
                    }
                });
                this.countAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.CounterView.CounterDrawable.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        CounterDrawable.this.countChangeProgress = 1.0f;
                        CounterDrawable.this.countOldLayout = null;
                        CounterDrawable.this.countAnimationStableLayout = null;
                        CounterDrawable.this.countAnimationInLayout = null;
                        if (CounterDrawable.this.parent != null) {
                            if (CounterDrawable.this.currentCount == 0 && CounterDrawable.this.updateVisibility) {
                                CounterDrawable.this.parent.setVisibility(8);
                            }
                            CounterDrawable.this.parent.invalidate();
                        }
                        CounterDrawable.this.animationType = -1;
                    }
                });
                if (this.currentCount <= 0) {
                    this.animationType = 0;
                    this.countAnimator.setDuration(220L);
                    this.countAnimator.setInterpolator(new OvershootInterpolator());
                } else if (count == 0) {
                    this.animationType = 1;
                    this.countAnimator.setDuration(150L);
                    this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                } else {
                    this.animationType = 2;
                    this.countAnimator.setDuration(430L);
                    this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                }
                if (this.countLayout != null) {
                    String oldStr = getStringOfCCount(this.currentCount);
                    if (oldStr.length() != newStr2.length()) {
                        this.countOldLayout = this.countLayout;
                    } else {
                        SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                        SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr2);
                        SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr2);
                        for (int i = 0; i < oldStr.length(); i++) {
                            if (oldStr.charAt(i) == newStr2.charAt(i)) {
                                oldSpannableStr.setSpan(new EmptyStubSpan(), i, i + 1, 0);
                                newSpannableStr.setSpan(new EmptyStubSpan(), i, i + 1, 0);
                            } else {
                                stableStr.setSpan(new EmptyStubSpan(), i, i + 1, 0);
                            }
                        }
                        int i2 = AndroidUtilities.dp(12.0f);
                        int countOldWidth = Math.max(i2, (int) Math.ceil(this.textPaint.measureText(oldStr)));
                        this.countOldLayout = new StaticLayout(oldSpannableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationStableLayout = new StaticLayout(stableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.countAnimationInLayout = new StaticLayout(newSpannableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                }
                this.countWidthOld = this.countWidth;
                this.countAnimationIncrement = count > this.currentCount;
                this.countAnimator.start();
            }
            if (count > 0) {
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(this.textPaint.measureText(newStr2)));
                this.countLayout = new StaticLayout(newStr2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            }
            this.currentCount = count;
            View view4 = this.parent;
            if (view4 != null) {
                view4.invalidate();
            }
        }

        /* renamed from: lambda$setCount$0$org-telegram-ui-Components-CounterView$CounterDrawable */
        public /* synthetic */ void m2543x2f809de4(ValueAnimator valueAnimator) {
            this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            View view = this.parent;
            if (view != null) {
                view.invalidate();
            }
        }

        private String getStringOfCCount(int count) {
            if (this.shortFormat) {
                return AndroidUtilities.formatWholeNumber(count, 0);
            }
            return String.valueOf(count);
        }

        public void draw(Canvas canvas) {
            float countWidth;
            int i = this.type;
            boolean increment = true;
            if (i != 1 && i != 2) {
                int textColor = getThemedColor(this.textColorKey);
                int circleColor = getThemedColor(this.circleColorKey);
                if (this.textColor != textColor) {
                    this.textColor = textColor;
                    this.textPaint.setColor(textColor);
                }
                Paint paint = this.circlePaint;
                if (paint != null && this.circleColor != circleColor) {
                    this.circleColor = circleColor;
                    paint.setColor(circleColor);
                }
            }
            float f = this.countChangeProgress;
            if (f != 1.0f) {
                int i2 = this.animationType;
                if (i2 == 0 || i2 == 1) {
                    updateX(this.countWidth);
                    float cx = this.countLeft + (this.countWidth / 2.0f);
                    float cy = this.lastH / 2.0f;
                    canvas.save();
                    float progress = this.animationType == 0 ? this.countChangeProgress : 1.0f - this.countChangeProgress;
                    canvas.scale(progress, progress, cx, cy);
                    drawInternal(canvas);
                    canvas.restore();
                    return;
                }
                float progressHalf = f * 2.0f;
                if (progressHalf > 1.0f) {
                    progressHalf = 1.0f;
                }
                float countTop = (this.lastH - AndroidUtilities.dp(23.0f)) / 2.0f;
                int i3 = this.countWidth;
                int i4 = this.countWidthOld;
                if (i3 == i4) {
                    countWidth = i3;
                } else {
                    countWidth = (i3 * progressHalf) + (i4 * (1.0f - progressHalf));
                }
                updateX(countWidth);
                float scale = 1.0f;
                if (this.countAnimationIncrement) {
                    scale = this.countChangeProgress <= 0.5f ? 1.0f + (CubicBezierInterpolator.EASE_OUT.getInterpolation(this.countChangeProgress * 2.0f) * 0.1f) : 1.0f + (CubicBezierInterpolator.EASE_IN.getInterpolation(1.0f - ((this.countChangeProgress - 0.5f) * 2.0f)) * 0.1f);
                }
                RectF rectF = this.rectF;
                float f2 = this.x;
                rectF.set(f2, countTop, f2 + countWidth + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f) + countTop);
                canvas.save();
                canvas.scale(scale, scale, this.rectF.centerX(), this.rectF.centerY());
                if (this.drawBackground && this.circlePaint != null) {
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, this.circlePaint);
                    if (this.addServiceGradient && Theme.hasGradientService()) {
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.chat_actionBackgroundGradientDarkenPaint);
                    }
                }
                canvas.clipRect(this.rectF);
                if (this.reverseAnimation == this.countAnimationIncrement) {
                    increment = false;
                }
                if (this.countAnimationInLayout != null) {
                    canvas.save();
                    float f3 = this.countLeft;
                    float dp = AndroidUtilities.dp(4.0f) + countTop;
                    int dp2 = AndroidUtilities.dp(13.0f);
                    if (!increment) {
                        dp2 = -dp2;
                    }
                    canvas.translate(f3, dp + (dp2 * (1.0f - progressHalf)));
                    this.textPaint.setAlpha((int) (progressHalf * 255.0f));
                    this.countAnimationInLayout.draw(canvas);
                    canvas.restore();
                } else if (this.countLayout != null) {
                    canvas.save();
                    float f4 = this.countLeft;
                    float dp3 = AndroidUtilities.dp(4.0f) + countTop;
                    int dp4 = AndroidUtilities.dp(13.0f);
                    if (!increment) {
                        dp4 = -dp4;
                    }
                    canvas.translate(f4, dp3 + (dp4 * (1.0f - progressHalf)));
                    this.textPaint.setAlpha((int) (progressHalf * 255.0f));
                    this.countLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.countOldLayout != null) {
                    canvas.save();
                    float f5 = this.countLeft;
                    float dp5 = AndroidUtilities.dp(4.0f) + countTop;
                    int dp6 = AndroidUtilities.dp(13.0f);
                    if (increment) {
                        dp6 = -dp6;
                    }
                    canvas.translate(f5, dp5 + (dp6 * progressHalf));
                    this.textPaint.setAlpha((int) ((1.0f - progressHalf) * 255.0f));
                    this.countOldLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.countAnimationStableLayout != null) {
                    canvas.save();
                    canvas.translate(this.countLeft, AndroidUtilities.dp(4.0f) + countTop);
                    this.textPaint.setAlpha(255);
                    this.countAnimationStableLayout.draw(canvas);
                    canvas.restore();
                }
                this.textPaint.setAlpha(255);
                canvas.restore();
                return;
            }
            drawInternal(canvas);
        }

        public void updateBackgroundRect() {
            float countWidth;
            float f = this.countChangeProgress;
            if (f != 1.0f) {
                int i = this.animationType;
                if (i == 0 || i == 1) {
                    updateX(this.countWidth);
                    float countTop = (this.lastH - AndroidUtilities.dp(23.0f)) / 2.0f;
                    RectF rectF = this.rectF;
                    float f2 = this.x;
                    rectF.set(f2, countTop, this.countWidth + f2 + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f) + countTop);
                    return;
                }
                float progressHalf = f * 2.0f;
                if (progressHalf > 1.0f) {
                    progressHalf = 1.0f;
                }
                float countTop2 = (this.lastH - AndroidUtilities.dp(23.0f)) / 2.0f;
                int i2 = this.countWidth;
                int i3 = this.countWidthOld;
                if (i2 == i3) {
                    countWidth = i2;
                } else {
                    countWidth = (i2 * progressHalf) + (i3 * (1.0f - progressHalf));
                }
                updateX(countWidth);
                RectF rectF2 = this.rectF;
                float f3 = this.x;
                rectF2.set(f3, countTop2, f3 + countWidth + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f) + countTop2);
                return;
            }
            updateX(this.countWidth);
            float countTop3 = (this.lastH - AndroidUtilities.dp(23.0f)) / 2.0f;
            RectF rectF3 = this.rectF;
            float f4 = this.x;
            rectF3.set(f4, countTop3, this.countWidth + f4 + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f) + countTop3);
        }

        private void updateX(float countWidth) {
            float padding = this.drawBackground ? AndroidUtilities.dp(5.5f) : 0.0f;
            int i = this.gravity;
            if (i == 5) {
                float f = this.width - padding;
                this.countLeft = f;
                float f2 = this.horizontalPadding;
                if (f2 != 0.0f) {
                    this.countLeft = f - Math.max(f2 + (countWidth / 2.0f), countWidth);
                } else {
                    this.countLeft = f - countWidth;
                }
            } else if (i == 3) {
                this.countLeft = padding;
            } else {
                this.countLeft = (int) ((this.width - countWidth) / 2.0f);
            }
            this.x = this.countLeft - padding;
        }

        public float getCenterX() {
            updateX(this.countWidth);
            return this.countLeft + (this.countWidth / 2.0f);
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setParent(View parent) {
            this.parent = parent;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public float getEnterProgress() {
        if (this.counterDrawable.countChangeProgress == 1.0f || !(this.counterDrawable.animationType == 0 || this.counterDrawable.animationType == 1)) {
            return this.counterDrawable.currentCount == 0 ? 0.0f : 1.0f;
        } else if (this.counterDrawable.animationType == 0) {
            return this.counterDrawable.countChangeProgress;
        } else {
            return 1.0f - this.counterDrawable.countChangeProgress;
        }
    }

    public boolean isInOutAnimation() {
        return this.counterDrawable.animationType == 0 || this.counterDrawable.animationType == 1;
    }
}
