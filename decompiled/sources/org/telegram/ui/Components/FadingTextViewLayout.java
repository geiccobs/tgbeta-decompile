package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes5.dex */
public class FadingTextViewLayout extends FrameLayout {
    private final ValueAnimator animator;
    private TextView currentView;
    private TextView foregroundView;
    private TextView nextView;
    private CharSequence text;

    public FadingTextViewLayout(Context context) {
        this(context, false);
    }

    public FadingTextViewLayout(Context context, boolean hasStaticChars) {
        super(context);
        for (int i = 0; i < (hasStaticChars ? 1 : 0) + 2; i++) {
            TextView textView = new TextView(context);
            onTextViewCreated(textView);
            addView(textView);
            if (i == 0) {
                this.currentView = textView;
            } else {
                textView.setVisibility(8);
                if (i == 1) {
                    textView.setAlpha(0.0f);
                    this.nextView = textView;
                } else {
                    this.foregroundView = textView;
                }
            }
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.setDuration(200L);
        ofFloat.setInterpolator(null);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.FadingTextViewLayout$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                FadingTextViewLayout.this.m2608lambda$new$0$orgtelegramuiComponentsFadingTextViewLayout(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FadingTextViewLayout.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FadingTextViewLayout.this.currentView.setLayerType(0, null);
                FadingTextViewLayout.this.nextView.setLayerType(0, null);
                FadingTextViewLayout.this.nextView.setVisibility(8);
                if (FadingTextViewLayout.this.foregroundView != null) {
                    FadingTextViewLayout.this.currentView.setText(FadingTextViewLayout.this.text);
                    FadingTextViewLayout.this.foregroundView.setVisibility(8);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                FadingTextViewLayout.this.currentView.setLayerType(2, null);
                FadingTextViewLayout.this.nextView.setLayerType(2, null);
                if (ViewCompat.isAttachedToWindow(FadingTextViewLayout.this.currentView)) {
                    FadingTextViewLayout.this.currentView.buildLayer();
                }
                if (ViewCompat.isAttachedToWindow(FadingTextViewLayout.this.nextView)) {
                    FadingTextViewLayout.this.nextView.buildLayer();
                }
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FadingTextViewLayout */
    public /* synthetic */ void m2608lambda$new$0$orgtelegramuiComponentsFadingTextViewLayout(ValueAnimator a) {
        float fraction = a.getAnimatedFraction();
        this.currentView.setAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(fraction));
        this.nextView.setAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(1.0f - fraction));
    }

    public void setText(CharSequence text) {
        setText(text, true, true);
    }

    public void setText(CharSequence text, boolean animated) {
        setText(text, animated, true);
    }

    public void setText(CharSequence text, boolean animated, boolean dontAnimateUnchangedStaticChars) {
        int staticCharsCount;
        CharSequence currentText;
        CharSequence text2 = text;
        if (!TextUtils.equals(text2, this.currentView.getText())) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.end();
            }
            this.text = text2;
            if (animated) {
                if (dontAnimateUnchangedStaticChars && this.foregroundView != null && (staticCharsCount = getStaticCharsCount()) > 0) {
                    CharSequence currentText2 = this.currentView.getText();
                    int length = Math.min(staticCharsCount, Math.min(text.length(), currentText2.length()));
                    List<android.graphics.Point> points = new ArrayList<>();
                    int startIndex = -1;
                    for (int i = 0; i < length; i++) {
                        char c = currentText2.charAt(i);
                        if (text2.charAt(i) == c) {
                            if (startIndex >= 0) {
                                points.add(new android.graphics.Point(startIndex, i));
                                startIndex = -1;
                            }
                        } else if (startIndex == -1) {
                            startIndex = i;
                        }
                    }
                    if (startIndex != 0) {
                        if (startIndex > 0) {
                            points.add(new android.graphics.Point(startIndex, length));
                        } else {
                            points.add(new android.graphics.Point(length, 0));
                        }
                    }
                    if (!points.isEmpty()) {
                        SpannableString foregroundText = new SpannableString(text2.subSequence(0, length));
                        SpannableString currentSpannableText = new SpannableString(currentText2);
                        SpannableString spannableText = new SpannableString(text2);
                        int lastIndex = 0;
                        int i2 = 0;
                        int N = points.size();
                        while (i2 < N) {
                            android.graphics.Point point = points.get(i2);
                            int staticCharsCount2 = staticCharsCount;
                            if (point.y <= point.x) {
                                currentText = currentText2;
                            } else {
                                currentText = currentText2;
                                foregroundText.setSpan(new ForegroundColorSpan(0), point.x, point.y, 17);
                            }
                            if (point.x > lastIndex) {
                                currentSpannableText.setSpan(new ForegroundColorSpan(0), lastIndex, point.x, 17);
                                spannableText.setSpan(new ForegroundColorSpan(0), lastIndex, point.x, 17);
                            }
                            lastIndex = point.y;
                            i2++;
                            staticCharsCount = staticCharsCount2;
                            currentText2 = currentText;
                        }
                        this.foregroundView.setVisibility(0);
                        this.foregroundView.setText(foregroundText);
                        this.currentView.setText(currentSpannableText);
                        text2 = spannableText;
                    }
                }
                this.nextView.setVisibility(0);
                this.nextView.setText(text2);
                showNext();
                return;
            }
            this.currentView.setText(text2);
        }
    }

    public CharSequence getText() {
        return this.text;
    }

    public TextView getCurrentView() {
        return this.currentView;
    }

    public TextView getNextView() {
        return this.nextView;
    }

    private void showNext() {
        TextView prevView = this.currentView;
        this.currentView = this.nextView;
        this.nextView = prevView;
        this.animator.start();
    }

    public void onTextViewCreated(TextView textView) {
        textView.setSingleLine(true);
        textView.setMaxLines(1);
    }

    protected int getStaticCharsCount() {
        return 0;
    }
}
