package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Property;
import android.view.View;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.ui.Components.AnimationProperties;
/* loaded from: classes5.dex */
public class AnimatedNumberLayout {
    public static final Property<AnimatedNumberLayout, Float> PROGRESS = new AnimationProperties.FloatProperty<AnimatedNumberLayout>(NotificationCompat.CATEGORY_PROGRESS) { // from class: org.telegram.ui.Components.AnimatedNumberLayout.1
        public void setValue(AnimatedNumberLayout object, float value) {
            object.setProgress(value);
        }

        public Float get(AnimatedNumberLayout object) {
            return Float.valueOf(object.progress);
        }
    };
    private ObjectAnimator animator;
    private final View parentView;
    private final TextPaint textPaint;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    private ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private float progress = 0.0f;
    private int currentNumber = 1;

    public AnimatedNumberLayout(View parent, TextPaint paint) {
        this.textPaint = paint;
        this.parentView = parent;
    }

    public void setProgress(float value) {
        if (this.progress == value) {
            return;
        }
        this.progress = value;
        this.parentView.invalidate();
    }

    private float getProgress() {
        return this.progress;
    }

    public int getWidth() {
        float width = 0.0f;
        int count = this.letters.size();
        for (int a = 0; a < count; a++) {
            width += this.letters.get(a).getLineWidth(0);
        }
        return (int) Math.ceil(width);
    }

    public void setNumber(int number, boolean animated) {
        if (this.currentNumber == number && !this.letters.isEmpty()) {
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
        String oldText = String.format(Locale.US, "%d", Integer.valueOf(this.currentNumber));
        String text = String.format(Locale.US, "%d", Integer.valueOf(number));
        boolean forwardAnimation = number > this.currentNumber;
        this.currentNumber = number;
        this.progress = 0.0f;
        int a = 0;
        while (a < text.length()) {
            String ch = text.substring(a, a + 1);
            String oldCh = (this.oldLetters.isEmpty() || a >= oldText.length()) ? null : oldText.substring(a, a + 1);
            if (oldCh == null || !oldCh.equals(ch)) {
                TextPaint textPaint = this.textPaint;
                StaticLayout layout = new StaticLayout(ch, textPaint, (int) Math.ceil(textPaint.measureText(ch)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.letters.add(layout);
            } else {
                this.letters.add(this.oldLetters.get(a));
                this.oldLetters.set(a, null);
            }
            a++;
        }
        if (animated && !this.oldLetters.isEmpty()) {
            Property<AnimatedNumberLayout, Float> property = PROGRESS;
            float[] fArr = new float[2];
            fArr[0] = forwardAnimation ? -1.0f : 1.0f;
            fArr[1] = 0.0f;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, property, fArr);
            this.animator = ofFloat;
            ofFloat.setDuration(150L);
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AnimatedNumberLayout.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AnimatedNumberLayout.this.animator = null;
                    AnimatedNumberLayout.this.oldLetters.clear();
                }
            });
            this.animator.start();
        }
        this.parentView.invalidate();
    }

    public void draw(Canvas canvas) {
        if (this.letters.isEmpty()) {
            return;
        }
        float height = this.letters.get(0).getHeight();
        int count = Math.max(this.letters.size(), this.oldLetters.size());
        canvas.save();
        int currentAlpha = this.textPaint.getAlpha();
        int a = 0;
        while (a < count) {
            canvas.save();
            StaticLayout layout = null;
            StaticLayout old = a < this.oldLetters.size() ? this.oldLetters.get(a) : null;
            if (a < this.letters.size()) {
                layout = this.letters.get(a);
            }
            float f = this.progress;
            if (f > 0.0f) {
                if (old != null) {
                    this.textPaint.setAlpha((int) (currentAlpha * f));
                    canvas.save();
                    canvas.translate(0.0f, (this.progress - 1.0f) * height);
                    old.draw(canvas);
                    canvas.restore();
                    if (layout != null) {
                        this.textPaint.setAlpha((int) (currentAlpha * (1.0f - this.progress)));
                        canvas.translate(0.0f, this.progress * height);
                    }
                } else {
                    this.textPaint.setAlpha(currentAlpha);
                }
            } else if (f < 0.0f) {
                if (old != null) {
                    this.textPaint.setAlpha((int) (currentAlpha * (-f)));
                    canvas.save();
                    canvas.translate(0.0f, (this.progress + 1.0f) * height);
                    old.draw(canvas);
                    canvas.restore();
                }
                if (layout != null) {
                    if (a == count - 1 || old != null) {
                        this.textPaint.setAlpha((int) (currentAlpha * (this.progress + 1.0f)));
                        canvas.translate(0.0f, this.progress * height);
                    } else {
                        this.textPaint.setAlpha(currentAlpha);
                    }
                }
            } else if (layout != null) {
                this.textPaint.setAlpha(currentAlpha);
            }
            if (layout != null) {
                layout.draw(canvas);
            }
            canvas.restore();
            canvas.translate(layout != null ? layout.getLineWidth(0) : old.getLineWidth(0), 0.0f);
            a++;
        }
        canvas.restore();
    }
}
