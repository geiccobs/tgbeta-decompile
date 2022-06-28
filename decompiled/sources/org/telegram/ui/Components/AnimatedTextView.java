package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import j$.util.stream.IntStream;
import j$.wrappers.C$r8$wrapper$java$util$stream$IntStream$VWRP;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class AnimatedTextView extends View {
    private AnimatedTextDrawable drawable;
    private boolean first = true;
    private int lastMaxWidth;
    private boolean toSetMoveDown;
    private CharSequence toSetText;

    /* loaded from: classes5.dex */
    public static class AnimatedTextDrawable extends Drawable {
        private int alpha;
        private long animateDelay;
        private long animateDuration;
        private TimeInterpolator animateInterpolator;
        private ValueAnimator animator;
        private android.graphics.Rect bounds;
        private int currentHeight;
        private StaticLayout[] currentLayout;
        private Integer[] currentLayoutOffsets;
        private Integer[] currentLayoutToOldIndex;
        private CharSequence currentText;
        private int currentWidth;
        private int gravity;
        private boolean isRTL;
        private float moveAmplitude;
        private boolean moveDown;
        private int oldHeight;
        private StaticLayout[] oldLayout;
        private Integer[] oldLayoutOffsets;
        private Integer[] oldLayoutToCurrentIndex;
        private CharSequence oldText;
        private int oldWidth;
        private Runnable onAnimationFinishListener;
        private boolean preserveIndex;
        private boolean splitByWords;
        private boolean startFromEnd;
        private float t;
        private TextPaint textPaint;
        private CharSequence toSetText;
        private boolean toSetTextMoveDown;

        /* loaded from: classes5.dex */
        public interface RegionCallback {
            void run(CharSequence charSequence, int i, int i2);
        }

        public AnimatedTextDrawable() {
            this(false, false, false);
        }

        public AnimatedTextDrawable(boolean splitByWords, boolean preserveIndex, boolean startFromEnd) {
            this.textPaint = new TextPaint();
            this.gravity = 0;
            this.isRTL = false;
            this.t = 0.0f;
            this.moveDown = true;
            this.animateDelay = 0L;
            this.animateDuration = 450L;
            this.animateInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.moveAmplitude = 1.0f;
            this.alpha = 255;
            this.bounds = new android.graphics.Rect();
            this.splitByWords = splitByWords;
            this.preserveIndex = preserveIndex;
            this.startFromEnd = startFromEnd;
        }

        public void setOnAnimationFinishListener(Runnable listener) {
            this.onAnimationFinishListener = listener;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(this.bounds.left, this.bounds.top);
            int fullWidth = this.bounds.width();
            int fullHeight = this.bounds.height();
            if (this.currentLayout != null && this.oldLayout != null) {
                int width = AndroidUtilities.lerp(this.oldWidth, this.currentWidth, this.t);
                int height = AndroidUtilities.lerp(this.oldHeight, this.currentHeight, this.t);
                canvas.translate(0.0f, (fullHeight - height) / 2.0f);
                int i = 0;
                while (true) {
                    float f = -1.0f;
                    if (i >= this.currentLayout.length) {
                        break;
                    }
                    int j = this.currentLayoutToOldIndex[i].intValue();
                    float x = this.currentLayoutOffsets[i].intValue();
                    float y = 0.0f;
                    if (j >= 0) {
                        float oldX = this.oldLayoutOffsets[j].intValue();
                        x = AndroidUtilities.lerp(oldX, x, this.t);
                        this.textPaint.setAlpha(this.alpha);
                    } else {
                        float f2 = (-this.textPaint.getTextSize()) * this.moveAmplitude;
                        float f3 = this.t;
                        float f4 = f2 * (1.0f - f3);
                        if (this.moveDown) {
                            f = 1.0f;
                        }
                        y = f4 * f;
                        this.textPaint.setAlpha((int) (this.alpha * f3));
                    }
                    canvas.save();
                    int lwidth = j >= 0 ? width : this.currentWidth;
                    if (this.isRTL) {
                        x = ((lwidth - x) - this.currentLayout[i].getWidth()) - (fullWidth - lwidth);
                    }
                    int i2 = this.gravity;
                    if ((i2 & 5) > 0) {
                        x += fullWidth - lwidth;
                    } else if ((i2 & 1) > 0) {
                        x += (fullWidth - lwidth) / 2.0f;
                    }
                    canvas.translate(x, y);
                    this.currentLayout[i].draw(canvas);
                    canvas.restore();
                    i++;
                }
                for (int i3 = 0; i3 < this.oldLayout.length; i3++) {
                    if (this.oldLayoutToCurrentIndex[i3].intValue() < 0) {
                        float x2 = this.oldLayoutOffsets[i3].intValue();
                        float textSize = this.textPaint.getTextSize() * this.moveAmplitude;
                        float f5 = this.t;
                        float y2 = textSize * f5 * (this.moveDown ? 1.0f : -1.0f);
                        this.textPaint.setAlpha((int) (this.alpha * (1.0f - f5)));
                        canvas.save();
                        if (this.isRTL) {
                            x2 = ((this.oldWidth - x2) - this.oldLayout[i3].getWidth()) - (fullWidth - this.oldWidth);
                        }
                        int i4 = this.gravity;
                        if ((i4 & 5) > 0) {
                            x2 += fullWidth - this.oldWidth;
                        } else if ((i4 & 1) > 0) {
                            x2 += (fullWidth - this.oldWidth) / 2.0f;
                        }
                        canvas.translate(x2, y2);
                        this.oldLayout[i3].draw(canvas);
                        canvas.restore();
                    }
                }
            } else {
                canvas.translate(0.0f, (fullHeight - this.currentHeight) / 2.0f);
                if (this.currentLayout != null) {
                    for (int i5 = 0; i5 < this.currentLayout.length; i5++) {
                        this.textPaint.setAlpha(this.alpha);
                        canvas.save();
                        float x3 = this.currentLayoutOffsets[i5].intValue();
                        if (this.isRTL) {
                            x3 = ((this.currentWidth - x3) - this.currentLayout[i5].getWidth()) - (fullWidth - this.currentWidth);
                        }
                        int i6 = this.gravity;
                        if ((i6 & 5) > 0) {
                            x3 += fullWidth - this.currentWidth;
                        } else if ((i6 & 1) > 0) {
                            x3 += (fullWidth - this.currentWidth) / 2.0f;
                        }
                        canvas.translate(x3, 0.0f);
                        this.currentLayout[i5].draw(canvas);
                        canvas.restore();
                    }
                }
            }
            canvas.restore();
        }

        public boolean isAnimating() {
            ValueAnimator valueAnimator = this.animator;
            return valueAnimator != null && valueAnimator.isRunning();
        }

        public void setText(CharSequence text) {
            setText(text, true);
        }

        public void setText(CharSequence text, boolean animated) {
            setText(text, animated, true);
        }

        /* JADX WARN: Code restructure failed: missing block: B:30:0x00bb, code lost:
            if (r0.length == r0.size()) goto L33;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void setText(java.lang.CharSequence r19, boolean r20, boolean r21) {
            /*
                Method dump skipped, instructions count: 536
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedTextView.AnimatedTextDrawable.setText(java.lang.CharSequence, boolean, boolean):void");
        }

        /* renamed from: lambda$setText$0$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable */
        public /* synthetic */ void m2184x604cea82(ArrayList oldLayoutToCurrentIndex, ArrayList currentLayoutList, ArrayList currentLayoutToOldIndex, ArrayList oldLayoutList, ArrayList currentLayoutOffsets, ArrayList oldLayoutOffsets, CharSequence part, int from, int to) {
            StaticLayout layout = makeLayout(part, this.bounds.width() - Math.min(this.currentWidth, this.oldWidth));
            oldLayoutToCurrentIndex.add(Integer.valueOf(currentLayoutList.size()));
            currentLayoutToOldIndex.add(Integer.valueOf(oldLayoutList.size()));
            currentLayoutOffsets.add(Integer.valueOf(this.currentWidth));
            currentLayoutList.add(layout);
            oldLayoutOffsets.add(Integer.valueOf(this.oldWidth));
            oldLayoutList.add(layout);
            float partWidth = layout.getLineWidth(0);
            this.currentWidth = (int) (this.currentWidth + partWidth);
            this.oldWidth = (int) (this.oldWidth + partWidth);
            this.currentHeight = Math.max(this.currentHeight, layout.getHeight());
            this.oldHeight = Math.max(this.oldHeight, layout.getHeight());
        }

        /* renamed from: lambda$setText$1$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable */
        public /* synthetic */ void m2185xca7c72a1(ArrayList currentLayoutOffsets, ArrayList currentLayoutList, ArrayList currentLayoutToOldIndex, CharSequence part, int from, int to) {
            StaticLayout layout = makeLayout(part, this.bounds.width() - this.currentWidth);
            currentLayoutOffsets.add(Integer.valueOf(this.currentWidth));
            currentLayoutList.add(layout);
            currentLayoutToOldIndex.add(-1);
            this.currentWidth = (int) (this.currentWidth + layout.getLineWidth(0));
            this.currentHeight = Math.max(this.currentHeight, layout.getHeight());
        }

        /* renamed from: lambda$setText$2$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable */
        public /* synthetic */ void m2186x34abfac0(ArrayList oldLayoutOffsets, ArrayList oldLayoutList, ArrayList oldLayoutToCurrentIndex, CharSequence part, int from, int to) {
            StaticLayout layout = makeLayout(part, this.bounds.width() - this.oldWidth);
            oldLayoutOffsets.add(Integer.valueOf(this.oldWidth));
            oldLayoutList.add(layout);
            oldLayoutToCurrentIndex.add(-1);
            this.oldWidth = (int) (this.oldWidth + layout.getLineWidth(0));
            this.oldHeight = Math.max(this.oldHeight, layout.getHeight());
        }

        /* renamed from: lambda$setText$3$org-telegram-ui-Components-AnimatedTextView$AnimatedTextDrawable */
        public /* synthetic */ void m2187x9edb82df(ValueAnimator anm) {
            this.t = ((Float) anm.getAnimatedValue()).floatValue();
            invalidateSelf();
        }

        public CharSequence getText() {
            return this.currentText;
        }

        public int getWidth() {
            return Math.max(this.currentWidth, this.oldWidth);
        }

        public int getCurrentWidth() {
            if (this.currentLayout != null && this.oldLayout != null) {
                return AndroidUtilities.lerp(this.oldWidth, this.currentWidth, this.t);
            }
            return this.currentWidth;
        }

        private StaticLayout makeLayout(CharSequence textPart, int width) {
            if (width <= 0) {
                width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                return StaticLayout.Builder.obtain(textPart, 0, textPart.length(), this.textPaint, width).setMaxLines(1).setLineSpacing(0.0f, 1.0f).setAlignment(Layout.Alignment.ALIGN_NORMAL).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(width).build();
            }
            return new StaticLayout(textPart, 0, textPart.length(), this.textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, width);
        }

        /* loaded from: classes5.dex */
        public static class WordSequence implements CharSequence {
            private static final char SPACE = ' ';
            private final int length;
            private CharSequence[] words;

            public WordSequence(CharSequence text) {
                if (text == null) {
                    this.words = new CharSequence[0];
                    this.length = 0;
                    return;
                }
                this.length = text.length();
                int spacesCount = 0;
                for (int i = 0; i < this.length; i++) {
                    if (text.charAt(i) == ' ') {
                        spacesCount++;
                    }
                }
                int j = 0;
                this.words = new CharSequence[spacesCount + 1];
                int start = 0;
                int i2 = 0;
                while (true) {
                    int i3 = this.length;
                    if (i2 <= i3) {
                        if (i2 == i3 || text.charAt(i2) == ' ') {
                            int j2 = j + 1;
                            this.words[j] = text.subSequence(start, (i2 < this.length ? 1 : 0) + i2);
                            start = i2 + 1;
                            j = j2;
                        }
                        i2++;
                    } else {
                        return;
                    }
                }
            }

            public WordSequence(CharSequence[] words) {
                if (words == null) {
                    this.words = new CharSequence[0];
                    this.length = 0;
                    return;
                }
                this.words = words;
                int length = 0;
                int i = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i < charSequenceArr.length) {
                        if (charSequenceArr[i] != null) {
                            length += charSequenceArr[i].length();
                        }
                        i++;
                    } else {
                        this.length = length;
                        return;
                    }
                }
            }

            public CharSequence wordAt(int i) {
                if (i >= 0) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i >= charSequenceArr.length) {
                        return null;
                    }
                    return charSequenceArr[i];
                }
                return null;
            }

            @Override // java.lang.CharSequence
            public int length() {
                return this.words.length;
            }

            @Override // java.lang.CharSequence
            public char charAt(int i) {
                int j = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (j < charSequenceArr.length) {
                        if (i < charSequenceArr[j].length()) {
                            return this.words[j].charAt(i);
                        }
                        i -= this.words[j].length();
                        j++;
                    } else {
                        return (char) 0;
                    }
                }
            }

            @Override // java.lang.CharSequence
            public CharSequence subSequence(int from, int to) {
                return TextUtils.concat((CharSequence[]) Arrays.copyOfRange(this.words, from, to));
            }

            @Override // java.lang.CharSequence
            public String toString() {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (true) {
                    CharSequence[] charSequenceArr = this.words;
                    if (i < charSequenceArr.length) {
                        sb.append(charSequenceArr[i]);
                        i++;
                    } else {
                        return sb.toString();
                    }
                }
            }

            public CharSequence toCharSequence() {
                return TextUtils.concat(this.words);
            }

            @Override // java.lang.CharSequence
            public IntStream chars() {
                if (Build.VERSION.SDK_INT >= 24) {
                    return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(toCharSequence().chars());
                }
                return null;
            }

            @Override // java.lang.CharSequence
            public IntStream codePoints() {
                if (Build.VERSION.SDK_INT >= 24) {
                    return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(toCharSequence().codePoints());
                }
                return null;
            }
        }

        public static boolean partEquals(CharSequence a, CharSequence b, int aIndex, int bIndex) {
            if ((a instanceof WordSequence) && (b instanceof WordSequence)) {
                CharSequence wordA = ((WordSequence) a).wordAt(aIndex);
                CharSequence wordB = ((WordSequence) b).wordAt(bIndex);
                if (wordA == null && wordB == null) {
                    return true;
                }
                return wordA != null && wordA.equals(wordB);
            } else if (a == null && b == null) {
                return true;
            } else {
                return (a == null || b == null || a.charAt(aIndex) != b.charAt(bIndex)) ? false : true;
            }
        }

        private void diff(CharSequence oldText, CharSequence newText, RegionCallback onEqualPart, RegionCallback onNewPart, RegionCallback onOldPart) {
            int i;
            int i2 = 1;
            if (this.preserveIndex) {
                boolean equal = true;
                int start = 0;
                int minLength = Math.min(newText.length(), oldText.length());
                if (this.startFromEnd) {
                    ArrayList<Integer> indexes = new ArrayList<>();
                    boolean eq = true;
                    int i3 = 0;
                    while (i3 <= minLength) {
                        int a = (newText.length() - i3) - i2;
                        int b = (oldText.length() - i3) - i2;
                        boolean thisEqual = a >= 0 && b >= 0 && partEquals(newText, oldText, a, b);
                        if (equal != thisEqual || i3 == minLength) {
                            if (i3 - start > 0) {
                                if (indexes.size() == 0) {
                                    eq = equal;
                                }
                                indexes.add(Integer.valueOf(i3 - start));
                            }
                            equal = thisEqual;
                            start = i3;
                        }
                        i3++;
                        i2 = 1;
                    }
                    int a2 = newText.length() - minLength;
                    int b2 = oldText.length() - minLength;
                    if (a2 <= 0) {
                        i = 0;
                    } else {
                        i = 0;
                        onNewPart.run(newText.subSequence(0, a2), 0, a2);
                    }
                    if (b2 > 0) {
                        onOldPart.run(oldText.subSequence(i, b2), i, b2);
                    }
                    for (int i4 = indexes.size() - 1; i4 >= 0; i4--) {
                        int count = indexes.get(i4).intValue();
                        if (i4 % 2 != 0 ? !eq : eq) {
                            if (newText.length() > oldText.length()) {
                                onEqualPart.run(newText.subSequence(a2, a2 + count), a2, a2 + count);
                            } else {
                                onEqualPart.run(oldText.subSequence(b2, b2 + count), b2, b2 + count);
                            }
                        } else {
                            onNewPart.run(newText.subSequence(a2, a2 + count), a2, a2 + count);
                            onOldPart.run(oldText.subSequence(b2, b2 + count), b2, b2 + count);
                        }
                        a2 += count;
                        b2 += count;
                    }
                    return;
                }
                int i5 = 0;
                while (i5 <= minLength) {
                    boolean thisEqual2 = i5 < minLength && partEquals(newText, oldText, i5, i5);
                    if (equal != thisEqual2 || i5 == minLength) {
                        if (i5 - start > 0) {
                            if (equal) {
                                onEqualPart.run(newText.subSequence(start, i5), start, i5);
                            } else {
                                onNewPart.run(newText.subSequence(start, i5), start, i5);
                                onOldPart.run(oldText.subSequence(start, i5), start, i5);
                            }
                        }
                        equal = thisEqual2;
                        start = i5;
                    }
                    i5++;
                }
                int i6 = newText.length();
                if (i6 - minLength > 0) {
                    onNewPart.run(newText.subSequence(minLength, newText.length()), minLength, newText.length());
                }
                if (oldText.length() - minLength > 0) {
                    onOldPart.run(oldText.subSequence(minLength, oldText.length()), minLength, oldText.length());
                    return;
                }
                return;
            }
            int astart = 0;
            int bstart = 0;
            boolean equal2 = true;
            int a3 = 0;
            int b3 = 0;
            int minLength2 = Math.min(newText.length(), oldText.length());
            while (a3 <= minLength2) {
                boolean thisEqual3 = a3 < minLength2 && partEquals(newText, oldText, a3, b3);
                if (equal2 != thisEqual3 || a3 == minLength2) {
                    if (a3 == minLength2) {
                        a3 = newText.length();
                        b3 = oldText.length();
                    }
                    int alen = a3 - astart;
                    int blen = b3 - bstart;
                    if (alen > 0 || blen > 0) {
                        if (alen == blen && equal2) {
                            onEqualPart.run(newText.subSequence(astart, a3), astart, a3);
                        } else if (!equal2) {
                            if (alen > 0) {
                                onNewPart.run(newText.subSequence(astart, a3), astart, a3);
                            }
                            if (blen > 0) {
                                onOldPart.run(oldText.subSequence(bstart, b3), bstart, b3);
                            }
                        }
                    }
                    equal2 = thisEqual3;
                    astart = a3;
                    bstart = b3;
                }
                if (thisEqual3) {
                    b3++;
                }
                a3++;
            }
        }

        public void setTextSize(float textSizePx) {
            this.textPaint.setTextSize(textSizePx);
        }

        public void setTextColor(int color) {
            this.textPaint.setColor(color);
        }

        public void setTypeface(Typeface typeface) {
            this.textPaint.setTypeface(typeface);
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public void setAnimationProperties(float moveAmplitude, long startDelay, long duration, TimeInterpolator interpolator) {
            this.moveAmplitude = moveAmplitude;
            this.animateDelay = startDelay;
            this.animateDuration = duration;
            this.animateInterpolator = interpolator;
        }

        public void copyStylesFrom(TextPaint paint) {
            setTextColor(paint.getColor());
            setTextSize(paint.getTextSize());
            setTypeface(paint.getTypeface());
        }

        public TextPaint getPaint() {
            return this.textPaint;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.textPaint.setColorFilter(colorFilter);
        }

        @Override // android.graphics.drawable.Drawable
        @Deprecated
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(android.graphics.Rect bounds) {
            super.setBounds(bounds);
            this.bounds.set(bounds);
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            this.bounds.set(left, top, right, bottom);
        }
    }

    public AnimatedTextView(Context context) {
        super(context);
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable();
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
    }

    public AnimatedTextView(Context context, boolean splitByWords, boolean preserveIndex, boolean startFromEnd) {
        super(context);
        AnimatedTextDrawable animatedTextDrawable = new AnimatedTextDrawable(splitByWords, preserveIndex, startFromEnd);
        this.drawable = animatedTextDrawable;
        animatedTextDrawable.setCallback(this);
        this.drawable.setOnAnimationFinishListener(new Runnable() { // from class: org.telegram.ui.Components.AnimatedTextView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnimatedTextView.this.m2183lambda$new$0$orgtelegramuiComponentsAnimatedTextView();
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AnimatedTextView */
    public /* synthetic */ void m2183lambda$new$0$orgtelegramuiComponentsAnimatedTextView() {
        CharSequence charSequence = this.toSetText;
        if (charSequence != null) {
            setText(charSequence, this.toSetMoveDown, true);
            this.toSetText = null;
            this.toSetMoveDown = false;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.lastMaxWidth != width) {
            this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
            setText(this.drawable.getText(), false);
        }
        this.lastMaxWidth = width;
        if (View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
            width = getPaddingLeft() + this.drawable.getWidth() + getPaddingRight();
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        this.drawable.draw(canvas);
    }

    public void setText(CharSequence text) {
        setText(text, true, true);
    }

    public void setText(CharSequence text, boolean animated) {
        setText(text, animated, true);
    }

    public void setText(CharSequence text, boolean animated, boolean moveDown) {
        boolean animated2 = !this.first && animated;
        this.first = false;
        if (animated2 && this.drawable.isAnimating()) {
            this.toSetText = text;
            this.toSetMoveDown = moveDown;
            return;
        }
        int wasWidth = this.drawable.getWidth();
        this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), this.lastMaxWidth - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        this.drawable.setText(text, animated2, moveDown);
        if (wasWidth < this.drawable.getWidth()) {
            requestLayout();
        }
    }

    public CharSequence getText() {
        return this.drawable.getText();
    }

    public void setTextSize(float textSizePx) {
        this.drawable.setTextSize(textSizePx);
    }

    public void setTextColor(int color) {
        this.drawable.setTextColor(color);
    }

    public void setTypeface(Typeface typeface) {
        this.drawable.setTypeface(typeface);
    }

    public void setGravity(int gravity) {
        this.drawable.setGravity(gravity);
    }

    public void setAnimationProperties(float moveAmplitude, long startDelay, long duration, TimeInterpolator interpolator) {
        this.drawable.setAnimationProperties(moveAmplitude, startDelay, duration, interpolator);
    }

    public TextPaint getPaint() {
        return this.drawable.getPaint();
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidate();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.TextView");
        info.setText(getText());
    }
}
