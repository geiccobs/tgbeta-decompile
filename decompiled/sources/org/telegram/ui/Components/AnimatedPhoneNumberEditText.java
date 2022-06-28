package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes5.dex */
public class AnimatedPhoneNumberEditText extends HintEditText {
    private static final float SPRING_MULTIPLIER = 100.0f;
    private static final boolean USE_NUMBERS_ANIMATION = false;
    private ObjectAnimator animator;
    private Runnable hintAnimationCallback;
    private float progress;
    private String wasHint;
    private Boolean wasHintVisible;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    private ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private TextPaint textPaint = new TextPaint(1);
    private String oldText = "";
    private HintFadeProperty hintFadeProperty = new HintFadeProperty();
    private List<Float> hintAnimationValues = new ArrayList();
    private List<SpringAnimation> hintAnimations = new ArrayList();

    public AnimatedPhoneNumberEditText(Context context) {
        super(context);
    }

    @Override // org.telegram.ui.Components.HintEditText
    public void setHintText(final String value) {
        final boolean show = !TextUtils.isEmpty(value);
        boolean runAnimation = false;
        Boolean bool = this.wasHintVisible;
        if (bool == null || bool.booleanValue() != show) {
            this.hintAnimationValues.clear();
            for (SpringAnimation a : this.hintAnimations) {
                a.cancel();
            }
            this.hintAnimations.clear();
            this.wasHintVisible = Boolean.valueOf(show);
            runAnimation = TextUtils.isEmpty(getText());
        }
        String str = show ? value : this.wasHint;
        if (str == null) {
            str = "";
        }
        this.wasHint = value;
        if (show || !runAnimation) {
            super.setHintText(value);
        }
        if (runAnimation) {
            runHintAnimation(str.length(), show, new Runnable() { // from class: org.telegram.ui.Components.AnimatedPhoneNumberEditText$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AnimatedPhoneNumberEditText.this.m2182xbfd6e9c9(show, value);
                }
            });
        }
    }

    /* renamed from: lambda$setHintText$0$org-telegram-ui-Components-AnimatedPhoneNumberEditText */
    public /* synthetic */ void m2182xbfd6e9c9(boolean show, String value) {
        this.hintAnimationValues.clear();
        for (SpringAnimation a : this.hintAnimations) {
            a.cancel();
        }
        if (!show) {
            super.setHintText(value);
        }
    }

    @Override // org.telegram.ui.Components.HintEditText
    public String getHintText() {
        return this.wasHint;
    }

    private void runHintAnimation(int length, boolean show, Runnable callback) {
        Runnable runnable = this.hintAnimationCallback;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        for (int i = 0; i < length; i++) {
            float finalValue = 0.0f;
            float startValue = show ? 0.0f : 1.0f;
            if (show) {
                finalValue = 1.0f;
            }
            final SpringAnimation springAnimation = new SpringAnimation(Integer.valueOf(i), this.hintFadeProperty).setSpring(new SpringForce(finalValue * SPRING_MULTIPLIER).setStiffness(500.0f).setDampingRatio(1.0f).setFinalPosition(finalValue * SPRING_MULTIPLIER)).setStartValue(SPRING_MULTIPLIER * startValue);
            this.hintAnimations.add(springAnimation);
            this.hintAnimationValues.add(Float.valueOf(startValue));
            springAnimation.getClass();
            postDelayed(new Runnable() { // from class: org.telegram.ui.Components.AnimatedPhoneNumberEditText$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SpringAnimation.this.start();
                }
            }, i * 5);
        }
        this.hintAnimationCallback = callback;
        postDelayed(callback, (length * 5) + 150);
    }

    @Override // org.telegram.ui.Components.HintEditText, android.widget.TextView
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        this.textPaint.setTextSize(TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
    }

    @Override // android.widget.TextView
    public void setTextColor(int color) {
        super.setTextColor(color);
        this.textPaint.setColor(color);
    }

    @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override // org.telegram.ui.Components.HintEditText, org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setNewText(String text) {
        if (this.oldLetters == null || this.letters == null || ColorUtils$$ExternalSyntheticBackport0.m(this.oldText, text)) {
            return;
        }
        ObjectAnimator objectAnimator = this.animator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.animator = null;
        }
        this.oldLetters.clear();
        this.oldLetters.addAll(this.letters);
        this.letters.clear();
        boolean replace = TextUtils.isEmpty(this.oldText) && !TextUtils.isEmpty(text);
        this.progress = 0.0f;
        int a = 0;
        while (a < text.length()) {
            String ch = text.substring(a, a + 1);
            String oldCh = (this.oldLetters.isEmpty() || a >= this.oldText.length()) ? null : this.oldText.substring(a, a + 1);
            if (!replace && oldCh != null && oldCh.equals(ch)) {
                this.letters.add(this.oldLetters.get(a));
                this.oldLetters.set(a, null);
            } else {
                if (replace && oldCh == null) {
                    this.oldLetters.add(new StaticLayout("", this.textPaint, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                }
                TextPaint textPaint = this.textPaint;
                StaticLayout layout = new StaticLayout(ch, textPaint, (int) Math.ceil(textPaint.measureText(ch)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.letters.add(layout);
            }
            a++;
        }
        if (!this.oldLetters.isEmpty()) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, -1.0f, 0.0f);
            this.animator = ofFloat;
            ofFloat.setDuration(150L);
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AnimatedPhoneNumberEditText.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AnimatedPhoneNumberEditText.this.animator = null;
                    AnimatedPhoneNumberEditText.this.oldLetters.clear();
                }
            });
            this.animator.start();
        }
        this.oldText = text;
        invalidate();
    }

    @Override // org.telegram.ui.Components.HintEditText
    protected void onPreDrawHintCharacter(int index, Canvas canvas, float pivotX, float pivotY) {
        if (index < this.hintAnimationValues.size()) {
            this.hintPaint.setAlpha((int) (this.hintAnimationValues.get(index).floatValue() * 255.0f));
        }
    }

    public void setProgress(float value) {
        if (this.progress == value) {
            return;
        }
        this.progress = value;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    /* loaded from: classes5.dex */
    public final class HintFadeProperty extends FloatPropertyCompat<Integer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public HintFadeProperty() {
            super("hint_fade");
            AnimatedPhoneNumberEditText.this = r1;
        }

        public float getValue(Integer object) {
            if (object.intValue() < AnimatedPhoneNumberEditText.this.hintAnimationValues.size()) {
                return ((Float) AnimatedPhoneNumberEditText.this.hintAnimationValues.get(object.intValue())).floatValue() * AnimatedPhoneNumberEditText.SPRING_MULTIPLIER;
            }
            return 0.0f;
        }

        public void setValue(Integer object, float value) {
            if (object.intValue() < AnimatedPhoneNumberEditText.this.hintAnimationValues.size()) {
                AnimatedPhoneNumberEditText.this.hintAnimationValues.set(object.intValue(), Float.valueOf(value / AnimatedPhoneNumberEditText.SPRING_MULTIPLIER));
                AnimatedPhoneNumberEditText.this.invalidate();
            }
        }
    }
}
