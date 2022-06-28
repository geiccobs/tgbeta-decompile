package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class NumberTextView extends View {
    private boolean addNumber;
    private ObjectAnimator animator;
    private boolean center;
    private float oldTextWidth;
    private OnTextWidthProgressChangedListener onTextWidthProgressChangedListener;
    private float textWidth;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    private ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private TextPaint textPaint = new TextPaint(1);
    private float progress = 0.0f;
    private int currentNumber = 1;

    /* loaded from: classes5.dex */
    public interface OnTextWidthProgressChangedListener {
        void onTextWidthProgress(float f, float f2, float f3);
    }

    public NumberTextView(Context context) {
        super(context);
    }

    public void setOnTextWidthProgressChangedListener(OnTextWidthProgressChangedListener onTextWidthProgressChangedListener) {
        this.onTextWidthProgressChangedListener = onTextWidthProgressChangedListener;
    }

    public void setProgress(float value) {
        if (this.progress == value) {
            return;
        }
        this.progress = value;
        OnTextWidthProgressChangedListener onTextWidthProgressChangedListener = this.onTextWidthProgressChangedListener;
        if (onTextWidthProgressChangedListener != null) {
            onTextWidthProgressChangedListener.onTextWidthProgress(this.oldTextWidth, this.textWidth, value);
        }
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    public void setAddNumber() {
        this.addNumber = true;
    }

    public void setNumber(int number, boolean animated) {
        boolean forwardAnimation;
        String text;
        String oldText;
        if (this.currentNumber == number && animated) {
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
        if (this.addNumber) {
            oldText = String.format(Locale.US, "#%d", Integer.valueOf(this.currentNumber));
            text = String.format(Locale.US, "#%d", Integer.valueOf(number));
            forwardAnimation = number < this.currentNumber;
        } else {
            oldText = String.format(Locale.US, "%d", Integer.valueOf(this.currentNumber));
            text = String.format(Locale.US, "%d", Integer.valueOf(number));
            forwardAnimation = number > this.currentNumber;
        }
        boolean replace = false;
        this.textWidth = this.textPaint.measureText(text);
        float measureText = this.textPaint.measureText(oldText);
        this.oldTextWidth = measureText;
        if (this.center && this.textWidth != measureText) {
            replace = true;
        }
        this.currentNumber = number;
        this.progress = 0.0f;
        int a = 0;
        while (a < text.length()) {
            String ch = text.substring(a, a + 1);
            String oldCh = (this.oldLetters.isEmpty() || a >= oldText.length()) ? null : oldText.substring(a, a + 1);
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
        if (animated && !this.oldLetters.isEmpty()) {
            float[] fArr = new float[2];
            fArr[0] = forwardAnimation ? -1.0f : 1.0f;
            fArr[1] = 0.0f;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
            this.animator = ofFloat;
            ofFloat.setDuration(this.addNumber ? 180L : 150L);
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.NumberTextView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NumberTextView.this.animator = null;
                    NumberTextView.this.oldLetters.clear();
                }
            });
            this.animator.start();
        } else {
            OnTextWidthProgressChangedListener onTextWidthProgressChangedListener = this.onTextWidthProgressChangedListener;
            if (onTextWidthProgressChangedListener != null) {
                onTextWidthProgressChangedListener.onTextWidthProgress(this.oldTextWidth, this.textWidth, this.progress);
            }
        }
        invalidate();
    }

    public void setTextSize(int size) {
        this.textPaint.setTextSize(AndroidUtilities.dp(size));
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setTextColor(int value) {
        this.textPaint.setColor(value);
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setCenterAlign(boolean center) {
        this.center = center;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.letters.isEmpty()) {
            return;
        }
        float height = this.letters.get(0).getHeight();
        float translationHeight = this.addNumber ? AndroidUtilities.dp(4.0f) : height;
        float x = 0.0f;
        float oldDx = 0.0f;
        if (this.center) {
            x = (getMeasuredWidth() - this.textWidth) / 2.0f;
            oldDx = ((getMeasuredWidth() - this.oldTextWidth) / 2.0f) - x;
        }
        canvas.save();
        canvas.translate(getPaddingLeft() + x, (getMeasuredHeight() - height) / 2.0f);
        int count = Math.max(this.letters.size(), this.oldLetters.size());
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
                    this.textPaint.setAlpha((int) (f * 255.0f));
                    canvas.save();
                    canvas.translate(oldDx, (this.progress - 1.0f) * translationHeight);
                    old.draw(canvas);
                    canvas.restore();
                    if (layout != null) {
                        this.textPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
                        canvas.translate(0.0f, this.progress * translationHeight);
                    }
                } else {
                    this.textPaint.setAlpha(255);
                }
            } else if (f < 0.0f) {
                if (old != null) {
                    this.textPaint.setAlpha((int) ((-f) * 255.0f));
                    canvas.save();
                    canvas.translate(oldDx, (this.progress + 1.0f) * translationHeight);
                    old.draw(canvas);
                    canvas.restore();
                }
                if (layout != null) {
                    if (a == count - 1 || old != null) {
                        this.textPaint.setAlpha((int) ((this.progress + 1.0f) * 255.0f));
                        canvas.translate(0.0f, this.progress * translationHeight);
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                }
            } else if (layout != null) {
                this.textPaint.setAlpha(255);
            }
            if (layout != null) {
                layout.draw(canvas);
            }
            canvas.restore();
            canvas.translate(layout != null ? layout.getLineWidth(0) : old.getLineWidth(0) + AndroidUtilities.dp(1.0f), 0.0f);
            if (layout != null && old != null) {
                oldDx += old.getLineWidth(0) - layout.getLineWidth(0);
            }
            a++;
        }
        canvas.restore();
    }

    public float getOldTextWidth() {
        return this.oldTextWidth;
    }

    public float getTextWidth() {
        return this.textWidth;
    }
}
