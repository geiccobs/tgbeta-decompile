package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SearchCounterView extends View {
    private static final int ANIMATION_TYPE_REPLACE = 2;
    private StaticLayout countAnimationInLayout;
    private boolean countAnimationIncrement;
    private StaticLayout countAnimationStableLayout;
    private StaticLayout countAnimationStableLayout2;
    private ValueAnimator countAnimator;
    private StaticLayout countLayout;
    float countLeft;
    private StaticLayout countOldLayout;
    private int countWidth;
    private int countWidthOld;
    int currentCount;
    String currentString;
    public float horizontalPadding;
    int lastH;
    private final Theme.ResourcesProvider resourcesProvider;
    private int textColor;
    float x;
    int animationType = -1;
    TextPaint textPaint = new TextPaint(1);
    RectF rectF = new RectF();
    private float countChangeProgress = 1.0f;
    private String textColorKey = Theme.key_chat_searchPanelText;
    int gravity = 17;
    float dx = 0.0f;

    public SearchCounterView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() != this.lastH) {
            int count = this.currentCount;
            String str = this.currentString;
            this.currentString = null;
            setCount(str, count, false);
            this.lastH = getMeasuredHeight();
        }
    }

    public void setCount(String newStr, int count, boolean animated) {
        String newStr2 = newStr;
        String str = this.currentString;
        if (str != null && str.equals(newStr2)) {
            return;
        }
        ValueAnimator valueAnimator = this.countAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        boolean animated2 = (this.currentCount == 0 || count <= 0 || newStr2 == null || LocaleController.isRTL || TextUtils.isEmpty(newStr)) ? false : animated;
        if (animated2 && newStr2 != null && !newStr2.contains("**")) {
            animated2 = false;
        }
        if (!animated2) {
            if (newStr2 != null) {
                newStr2 = newStr2.replaceAll("\\*\\*", "");
            }
            this.currentCount = count;
            if (newStr2 != null) {
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(this.textPaint.measureText(newStr2)));
                this.countLayout = new StaticLayout(newStr2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            } else {
                this.countWidth = 0;
                this.countLayout = null;
            }
            invalidate();
        }
        this.dx = 0.0f;
        if (animated2) {
            ValueAnimator valueAnimator2 = this.countAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.countChangeProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.countAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SearchCounterView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    SearchCounterView.this.m2972lambda$setCount$0$orgtelegramuiComponentsSearchCounterView(valueAnimator3);
                }
            });
            this.countAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SearchCounterView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    SearchCounterView.this.animationType = -1;
                    SearchCounterView.this.countChangeProgress = 1.0f;
                    SearchCounterView.this.countOldLayout = null;
                    SearchCounterView.this.countAnimationStableLayout = null;
                    SearchCounterView.this.countAnimationInLayout = null;
                    SearchCounterView.this.invalidate();
                }
            });
            this.animationType = 2;
            this.countAnimator.setDuration(200L);
            this.countAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            if (this.countLayout != null) {
                String oldStr = this.currentString;
                int countStartIndex = newStr2.indexOf("**");
                if (countStartIndex >= 0) {
                    newStr2 = newStr2.replaceAll("\\*\\*", "");
                } else {
                    countStartIndex = 0;
                }
                SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr2);
                SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr2);
                boolean replaceAllDigits = Integer.toString(this.currentCount).length() != Integer.toString(count).length();
                int n = Math.min(oldStr.length(), newStr2.length());
                int cutIndexNew = 0;
                if (countStartIndex > 0) {
                    oldSpannableStr.setSpan(new EmptyStubSpan(), 0, countStartIndex, 33);
                    newSpannableStr.setSpan(new EmptyStubSpan(), 0, countStartIndex, 33);
                    stableStr.setSpan(new EmptyStubSpan(), 0, countStartIndex, 33);
                }
                boolean newEndReached = false;
                boolean oldEndReached = false;
                int cutIndexOld = 0;
                for (int i = countStartIndex; i < n; i++) {
                    if (!newEndReached && !oldEndReached) {
                        if (replaceAllDigits) {
                            stableStr.setSpan(new EmptyStubSpan(), i, i + 1, 33);
                        } else if (oldStr.charAt(i) == newStr2.charAt(i)) {
                            oldSpannableStr.setSpan(new EmptyStubSpan(), i, i + 1, 33);
                            newSpannableStr.setSpan(new EmptyStubSpan(), i, i + 1, 33);
                        } else {
                            stableStr.setSpan(new EmptyStubSpan(), i, i + 1, 33);
                        }
                    }
                    if (!Character.isDigit(newStr2.charAt(i))) {
                        newSpannableStr.setSpan(new EmptyStubSpan(), i, newStr2.length(), 33);
                        newEndReached = true;
                        cutIndexNew = i;
                    }
                    if (!Character.isDigit(oldStr.charAt(i))) {
                        oldSpannableStr.setSpan(new EmptyStubSpan(), i, oldStr.length(), 33);
                        oldEndReached = true;
                        cutIndexOld = i;
                    }
                }
                int countOldWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(this.textPaint.measureText(oldStr)));
                int cutIndexOld2 = cutIndexOld;
                int countNewWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(this.textPaint.measureText(newStr2)));
                int cutIndexNew2 = cutIndexNew;
                this.countOldLayout = new StaticLayout(oldSpannableStr, this.textPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                this.countAnimationStableLayout = new StaticLayout(stableStr, this.textPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                this.countAnimationInLayout = new StaticLayout(newSpannableStr, this.textPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (countStartIndex <= 0) {
                    this.countAnimationStableLayout2 = null;
                } else {
                    SpannableStringBuilder stableString2 = new SpannableStringBuilder(newStr2);
                    stableString2.setSpan(new EmptyStubSpan(), countStartIndex, newStr2.length(), 0);
                    this.countAnimationStableLayout2 = new StaticLayout(stableString2, this.textPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                }
                this.dx = this.countOldLayout.getPrimaryHorizontal(cutIndexOld2) - this.countAnimationStableLayout.getPrimaryHorizontal(cutIndexNew2);
            }
            this.countWidthOld = this.countWidth;
            this.countAnimationIncrement = count < this.currentCount;
            this.countAnimator.start();
        }
        if (count > 0) {
            this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(this.textPaint.measureText(newStr2)));
            this.countLayout = new StaticLayout(newStr2, this.textPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        }
        this.currentCount = count;
        invalidate();
        this.currentString = newStr2;
    }

    /* renamed from: lambda$setCount$0$org-telegram-ui-Components-SearchCounterView */
    public /* synthetic */ void m2972lambda$setCount$0$orgtelegramuiComponentsSearchCounterView(ValueAnimator valueAnimator) {
        this.countChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float countWidth;
        super.onDraw(canvas);
        int textColor = Theme.getColor(this.textColorKey, this.resourcesProvider);
        if (this.textColor != textColor) {
            this.textColor = textColor;
            this.textPaint.setColor(textColor);
        }
        if (this.countChangeProgress != 1.0f) {
            float countTop = (getMeasuredHeight() - AndroidUtilities.dp(23.0f)) / 2.0f;
            int i = this.countWidth;
            int i2 = this.countWidthOld;
            if (i == i2) {
                countWidth = i;
            } else {
                float f = this.countChangeProgress;
                countWidth = (i * f) + (i2 * (1.0f - f));
            }
            updateX(countWidth);
            RectF rectF = this.rectF;
            float f2 = this.x;
            rectF.set(f2, countTop, f2 + countWidth + AndroidUtilities.dp(11.0f), AndroidUtilities.dp(23.0f) + countTop);
            boolean increment = this.countAnimationIncrement;
            if (this.countAnimationInLayout != null) {
                canvas.save();
                float f3 = this.countLeft;
                float dp = AndroidUtilities.dp(2.0f) + countTop;
                int dp2 = AndroidUtilities.dp(13.0f);
                if (!increment) {
                    dp2 = -dp2;
                }
                canvas.translate(f3, dp + (dp2 * (1.0f - this.countChangeProgress)));
                this.textPaint.setAlpha((int) (this.countChangeProgress * 255.0f));
                this.countAnimationInLayout.draw(canvas);
                canvas.restore();
            } else if (this.countLayout != null) {
                canvas.save();
                float f4 = this.countLeft;
                float dp3 = AndroidUtilities.dp(2.0f) + countTop;
                int dp4 = AndroidUtilities.dp(13.0f);
                if (!increment) {
                    dp4 = -dp4;
                }
                canvas.translate(f4, dp3 + (dp4 * (1.0f - this.countChangeProgress)));
                this.textPaint.setAlpha((int) (this.countChangeProgress * 255.0f));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countOldLayout != null) {
                canvas.save();
                float f5 = this.countLeft;
                float dp5 = AndroidUtilities.dp(2.0f) + countTop;
                int dp6 = AndroidUtilities.dp(13.0f);
                if (increment) {
                    dp6 = -dp6;
                }
                canvas.translate(f5, dp5 + (dp6 * this.countChangeProgress));
                this.textPaint.setAlpha((int) ((1.0f - this.countChangeProgress) * 255.0f));
                this.countOldLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countAnimationStableLayout != null) {
                canvas.save();
                canvas.translate(this.countLeft + (this.dx * (1.0f - this.countChangeProgress)), AndroidUtilities.dp(2.0f) + countTop);
                this.textPaint.setAlpha(255);
                this.countAnimationStableLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countAnimationStableLayout2 != null) {
                canvas.save();
                canvas.translate(this.countLeft, AndroidUtilities.dp(2.0f) + countTop);
                this.textPaint.setAlpha(255);
                this.countAnimationStableLayout2.draw(canvas);
                canvas.restore();
            }
            this.textPaint.setAlpha(255);
            return;
        }
        drawInternal(canvas);
    }

    private void updateX(float countWidth) {
        int i = this.gravity;
        if (i == 5) {
            float measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(5.5f);
            this.countLeft = measuredWidth;
            float f = this.horizontalPadding;
            if (f != 0.0f) {
                this.countLeft = measuredWidth - Math.max(f + (countWidth / 2.0f), countWidth);
            } else {
                this.countLeft = measuredWidth - countWidth;
            }
        } else if (i == 3) {
            this.countLeft = AndroidUtilities.dp(5.5f);
        } else {
            this.countLeft = (int) ((getMeasuredWidth() - countWidth) / 2.0f);
        }
        this.x = this.countLeft - AndroidUtilities.dp(5.5f);
    }

    private void drawInternal(Canvas canvas) {
        float countTop = (getMeasuredHeight() - AndroidUtilities.dp(23.0f)) / 2.0f;
        updateX(this.countWidth);
        if (this.countLayout != null) {
            canvas.save();
            canvas.translate(this.countLeft, AndroidUtilities.dp(2.0f) + countTop);
            this.countLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }
}
