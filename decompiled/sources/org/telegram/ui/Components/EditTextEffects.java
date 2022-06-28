package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.spoilers.SpoilersClickDetector;
/* loaded from: classes5.dex */
public class EditTextEffects extends EditText {
    private static final int SPOILER_TIMEOUT = 10000;
    private boolean isSpoilersRevealed;
    private float lastRippleX;
    private float lastRippleY;
    private boolean postedSpoilerTimeout;
    private int selEnd;
    private int selStart;
    private boolean suppressOnTextChanged;
    private List<SpoilerEffect> spoilers = new ArrayList();
    private Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private boolean shouldRevealSpoilersByTouch = true;
    private Path path = new Path();
    private Runnable spoilerTimeout = new Runnable() { // from class: org.telegram.ui.Components.EditTextEffects$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            EditTextEffects.this.m2563lambda$new$2$orgtelegramuiComponentsEditTextEffects();
        }
    };
    private android.graphics.Rect rect = new android.graphics.Rect();
    private SpoilersClickDetector clickDetector = new SpoilersClickDetector(this, this.spoilers, new SpoilersClickDetector.OnSpoilerClickedListener() { // from class: org.telegram.ui.Components.EditTextEffects$$ExternalSyntheticLambda5
        @Override // org.telegram.ui.Components.spoilers.SpoilersClickDetector.OnSpoilerClickedListener
        public final void onSpoilerClicked(SpoilerEffect spoilerEffect, float f, float f2) {
            EditTextEffects.this.onSpoilerClicked(spoilerEffect, f, f2);
        }
    });

    /* renamed from: lambda$new$2$org-telegram-ui-Components-EditTextEffects */
    public /* synthetic */ void m2563lambda$new$2$orgtelegramuiComponentsEditTextEffects() {
        this.postedSpoilerTimeout = false;
        this.isSpoilersRevealed = false;
        invalidateSpoilers();
        if (!this.spoilers.isEmpty()) {
            this.spoilers.get(0).setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Components.EditTextEffects$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EditTextEffects.this.m2562lambda$new$1$orgtelegramuiComponentsEditTextEffects();
                }
            });
            float rad = (float) Math.sqrt(Math.pow(getWidth(), 2.0d) + Math.pow(getHeight(), 2.0d));
            for (SpoilerEffect eff : this.spoilers) {
                eff.startRipple(this.lastRippleX, this.lastRippleY, rad, true);
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-EditTextEffects */
    public /* synthetic */ void m2561lambda$new$0$orgtelegramuiComponentsEditTextEffects() {
        setSpoilersRevealed(false, true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-EditTextEffects */
    public /* synthetic */ void m2562lambda$new$1$orgtelegramuiComponentsEditTextEffects() {
        post(new Runnable() { // from class: org.telegram.ui.Components.EditTextEffects$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EditTextEffects.this.m2561lambda$new$0$orgtelegramuiComponentsEditTextEffects();
            }
        });
    }

    public EditTextEffects(Context context) {
        super(context);
    }

    public void onSpoilerClicked(SpoilerEffect eff, float x, float y) {
        if (this.isSpoilersRevealed) {
            return;
        }
        this.lastRippleX = x;
        this.lastRippleY = y;
        this.postedSpoilerTimeout = false;
        removeCallbacks(this.spoilerTimeout);
        setSpoilersRevealed(true, false);
        eff.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Components.EditTextEffects$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                EditTextEffects.this.m2565xd5a1f33d();
            }
        });
        float rad = (float) Math.sqrt(Math.pow(getWidth(), 2.0d) + Math.pow(getHeight(), 2.0d));
        for (SpoilerEffect ef : this.spoilers) {
            ef.startRipple(x, y, rad);
        }
    }

    /* renamed from: lambda$onSpoilerClicked$4$org-telegram-ui-Components-EditTextEffects */
    public /* synthetic */ void m2565xd5a1f33d() {
        post(new Runnable() { // from class: org.telegram.ui.Components.EditTextEffects$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                EditTextEffects.this.m2564x12b589de();
            }
        });
    }

    /* renamed from: lambda$onSpoilerClicked$3$org-telegram-ui-Components-EditTextEffects */
    public /* synthetic */ void m2564x12b589de() {
        invalidateSpoilers();
        checkSpoilerTimeout();
    }

    @Override // android.widget.TextView
    public void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (this.suppressOnTextChanged) {
            return;
        }
        this.selStart = selStart;
        this.selEnd = selEnd;
        checkSpoilerTimeout();
    }

    private void checkSpoilerTimeout() {
        int i;
        int i2;
        boolean onSpoiler = false;
        CharSequence cs = getLayout() != null ? getLayout().getText() : null;
        if (cs instanceof Spannable) {
            Spannable e = (Spannable) cs;
            TextStyleSpan[] spans = (TextStyleSpan[]) e.getSpans(0, e.length(), TextStyleSpan.class);
            for (TextStyleSpan span : spans) {
                int ss = e.getSpanStart(span);
                int se = e.getSpanEnd(span);
                if (span.isSpoiler() && ((ss > (i = this.selStart) && se < this.selEnd) || ((i > ss && i < se) || ((i2 = this.selEnd) > ss && i2 < se)))) {
                    onSpoiler = true;
                    removeCallbacks(this.spoilerTimeout);
                    this.postedSpoilerTimeout = false;
                    break;
                }
            }
        }
        if (this.isSpoilersRevealed && !onSpoiler && !this.postedSpoilerTimeout) {
            this.postedSpoilerTimeout = true;
            postDelayed(this.spoilerTimeout, 10000L);
        }
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.spoilerTimeout);
    }

    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateEffects();
    }

    @Override // android.widget.TextView
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!this.suppressOnTextChanged) {
            invalidateEffects();
            Layout layout = getLayout();
            if ((text instanceof Spannable) && layout != null) {
                int line = layout.getLineForOffset(start);
                int x = (int) layout.getPrimaryHorizontal(start);
                int y = (int) ((layout.getLineTop(line) + layout.getLineBottom(line)) / 2.0f);
                for (SpoilerEffect eff : this.spoilers) {
                    if (eff.getBounds().contains(x, y)) {
                        int selOffset = lengthAfter - lengthBefore;
                        this.selStart += selOffset;
                        this.selEnd += selOffset;
                        onSpoilerClicked(eff, x, y);
                        return;
                    }
                }
            }
        }
    }

    @Override // android.widget.EditText, android.widget.TextView
    public void setText(CharSequence text, TextView.BufferType type) {
        if (!this.suppressOnTextChanged) {
            this.isSpoilersRevealed = false;
            Stack<SpoilerEffect> stack = this.spoilersPool;
            if (stack != null) {
                stack.clear();
            }
        }
        super.setText(text, type);
    }

    public void setShouldRevealSpoilersByTouch(boolean shouldRevealSpoilersByTouch) {
        this.shouldRevealSpoilersByTouch = shouldRevealSpoilersByTouch;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean detector = false;
        if (this.shouldRevealSpoilersByTouch && this.clickDetector.onTouchEvent(event)) {
            int act = event.getActionMasked();
            if (act == 1) {
                MotionEvent c = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                super.dispatchTouchEvent(c);
                c.recycle();
            }
            detector = true;
        }
        return super.dispatchTouchEvent(event) || detector;
    }

    public void setSpoilersRevealed(boolean spoilersRevealed, boolean notifyEffects) {
        this.isSpoilersRevealed = spoilersRevealed;
        Spannable text = getText();
        if (text != null) {
            TextStyleSpan[] spans = (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class);
            for (TextStyleSpan span : spans) {
                if (span.isSpoiler()) {
                    span.setSpoilerRevealed(spoilersRevealed);
                }
            }
        }
        this.suppressOnTextChanged = true;
        setText(text, TextView.BufferType.EDITABLE);
        setSelection(this.selStart, this.selEnd);
        this.suppressOnTextChanged = false;
        if (notifyEffects) {
            invalidateSpoilers();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        canvas.save();
        this.path.rewind();
        for (SpoilerEffect eff : this.spoilers) {
            android.graphics.Rect bounds = eff.getBounds();
            this.path.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
        canvas.clipPath(this.path, Region.Op.DIFFERENCE);
        super.onDraw(canvas);
        canvas.restore();
        canvas.save();
        canvas.clipPath(this.path);
        this.path.rewind();
        if (!this.spoilers.isEmpty()) {
            this.spoilers.get(0).getRipplePath(this.path);
        }
        canvas.clipPath(this.path);
        canvas.translate(0.0f, -getPaddingTop());
        super.onDraw(canvas);
        canvas.restore();
        this.rect.set(0, getScrollY(), getWidth(), (getScrollY() + getHeight()) - getPaddingBottom());
        canvas.save();
        canvas.clipRect(this.rect);
        for (SpoilerEffect eff2 : this.spoilers) {
            android.graphics.Rect b = eff2.getBounds();
            if ((this.rect.top <= b.bottom && this.rect.bottom >= b.top) || (b.top <= this.rect.bottom && b.bottom >= this.rect.top)) {
                eff2.setColor(getPaint().getColor());
                eff2.draw(canvas);
            }
        }
        canvas.restore();
    }

    public void invalidateEffects() {
        TextStyleSpan[] textStyleSpanArr;
        Editable text = getText();
        if (text != null) {
            for (TextStyleSpan span : (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class)) {
                if (span.isSpoiler()) {
                    span.setSpoilerRevealed(this.isSpoilersRevealed);
                }
            }
        }
        invalidateSpoilers();
    }

    private void invalidateSpoilers() {
        List<SpoilerEffect> list = this.spoilers;
        if (list == null) {
            return;
        }
        this.spoilersPool.addAll(list);
        this.spoilers.clear();
        if (this.isSpoilersRevealed) {
            invalidate();
            return;
        }
        Layout layout = getLayout();
        if (layout != null && (layout.getText() instanceof Spannable)) {
            SpoilerEffect.addSpoilers(this, this.spoilersPool, this.spoilers);
        }
        invalidate();
    }
}
