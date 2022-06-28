package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.View;
import java.util.ArrayList;
/* loaded from: classes5.dex */
public class EllipsizeSpanAnimator {
    boolean attachedToWindow;
    private final AnimatorSet ellAnimator;
    private final TextAlphaSpan[] ellSpans;
    public ArrayList<View> ellipsizedViews = new ArrayList<>();

    public EllipsizeSpanAnimator(final View parentView) {
        TextAlphaSpan[] textAlphaSpanArr = {new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        this.ellSpans = textAlphaSpanArr;
        AnimatorSet animatorSet = new AnimatorSet();
        this.ellAnimator = animatorSet;
        animatorSet.playTogether(createEllipsizeAnimator(textAlphaSpanArr[0], 0, 255, 0, 300), createEllipsizeAnimator(textAlphaSpanArr[1], 0, 255, 150, 300), createEllipsizeAnimator(textAlphaSpanArr[2], 0, 255, 300, 300), createEllipsizeAnimator(textAlphaSpanArr[0], 255, 0, 1000, 400), createEllipsizeAnimator(textAlphaSpanArr[1], 255, 0, 1000, 400), createEllipsizeAnimator(textAlphaSpanArr[2], 255, 0, 1000, 400));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EllipsizeSpanAnimator.1
            private Runnable restarter = new Runnable() { // from class: org.telegram.ui.Components.EllipsizeSpanAnimator.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (EllipsizeSpanAnimator.this.attachedToWindow && !EllipsizeSpanAnimator.this.ellipsizedViews.isEmpty() && !EllipsizeSpanAnimator.this.ellAnimator.isRunning()) {
                        try {
                            EllipsizeSpanAnimator.this.ellAnimator.start();
                        } catch (Exception e) {
                        }
                    }
                }
            };

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (EllipsizeSpanAnimator.this.attachedToWindow) {
                    parentView.postDelayed(this.restarter, 300L);
                }
            }
        });
    }

    public void wrap(SpannableString string, int start) {
        string.setSpan(this.ellSpans[0], start, start + 1, 0);
        string.setSpan(this.ellSpans[1], start + 1, start + 2, 0);
        string.setSpan(this.ellSpans[2], start + 2, start + 3, 0);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
        if (!this.ellAnimator.isRunning()) {
            this.ellAnimator.start();
        }
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
        this.ellAnimator.cancel();
    }

    public void reset() {
        TextAlphaSpan[] textAlphaSpanArr;
        for (TextAlphaSpan s : this.ellSpans) {
            s.setAlpha(0);
        }
    }

    private Animator createEllipsizeAnimator(final TextAlphaSpan target, int startVal, int endVal, int startDelay, int duration) {
        ValueAnimator a = ValueAnimator.ofInt(startVal, endVal);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EllipsizeSpanAnimator$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                EllipsizeSpanAnimator.this.m2570x871035ba(target, valueAnimator);
            }
        });
        a.setDuration(duration);
        a.setStartDelay(startDelay);
        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return a;
    }

    /* renamed from: lambda$createEllipsizeAnimator$0$org-telegram-ui-Components-EllipsizeSpanAnimator */
    public /* synthetic */ void m2570x871035ba(TextAlphaSpan target, ValueAnimator valueAnimator) {
        target.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
        for (int i = 0; i < this.ellipsizedViews.size(); i++) {
            this.ellipsizedViews.get(i).invalidate();
        }
    }

    public void addView(View view) {
        if (this.ellipsizedViews.isEmpty()) {
            this.ellAnimator.start();
        }
        if (!this.ellipsizedViews.contains(view)) {
            this.ellipsizedViews.add(view);
        }
    }

    public void removeView(View view) {
        this.ellipsizedViews.remove(view);
        if (this.ellipsizedViews.isEmpty()) {
            this.ellAnimator.cancel();
        }
    }

    /* loaded from: classes5.dex */
    public static class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        @Override // android.text.style.CharacterStyle
        public void updateDrawState(TextPaint tp) {
            tp.setAlpha(this.alpha);
        }
    }
}
