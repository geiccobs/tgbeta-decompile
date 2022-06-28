package org.telegram.ui.Components.spoilers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Layout;
import android.text.Spanned;
import android.view.MotionEvent;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.spoilers.SpoilersClickDetector;
/* loaded from: classes5.dex */
public class SpoilersTextView extends TextView {
    private boolean isSpoilersRevealed;
    private Paint xRefPaint;
    private List<SpoilerEffect> spoilers = new ArrayList();
    private Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private Path path = new Path();
    private SpoilersClickDetector clickDetector = new SpoilersClickDetector(this, this.spoilers, new SpoilersClickDetector.OnSpoilerClickedListener() { // from class: org.telegram.ui.Components.spoilers.SpoilersTextView$$ExternalSyntheticLambda2
        @Override // org.telegram.ui.Components.spoilers.SpoilersClickDetector.OnSpoilerClickedListener
        public final void onSpoilerClicked(SpoilerEffect spoilerEffect, float f, float f2) {
            SpoilersTextView.this.m3226x69fa7571(spoilerEffect, f, f2);
        }
    });

    public SpoilersTextView(Context context) {
        super(context);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-spoilers-SpoilersTextView */
    public /* synthetic */ void m3226x69fa7571(SpoilerEffect eff, float x, float y) {
        if (this.isSpoilersRevealed) {
            return;
        }
        eff.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Components.spoilers.SpoilersTextView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SpoilersTextView.this.m3225x4fdef6d2();
            }
        });
        float rad = (float) Math.sqrt(Math.pow(getWidth(), 2.0d) + Math.pow(getHeight(), 2.0d));
        for (SpoilerEffect ef : this.spoilers) {
            ef.startRipple(x, y, rad);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-spoilers-SpoilersTextView */
    public /* synthetic */ void m3225x4fdef6d2() {
        post(new Runnable() { // from class: org.telegram.ui.Components.spoilers.SpoilersTextView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SpoilersTextView.this.m3224x35c37833();
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-spoilers-SpoilersTextView */
    public /* synthetic */ void m3224x35c37833() {
        this.isSpoilersRevealed = true;
        invalidateSpoilers();
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.clickDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override // android.widget.TextView
    public void setText(CharSequence text, TextView.BufferType type) {
        this.isSpoilersRevealed = false;
        super.setText(text, type);
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        invalidateSpoilers();
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateSpoilers();
    }

    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        canvas.save();
        this.path.rewind();
        for (SpoilerEffect eff : this.spoilers) {
            Rect bounds = eff.getBounds();
            this.path.addRect(bounds.left + pl, bounds.top + pt, bounds.right + pl, bounds.bottom + pt, Path.Direction.CW);
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
        super.onDraw(canvas);
        canvas.restore();
        if (!this.spoilers.isEmpty()) {
            boolean useAlphaLayer = this.spoilers.get(0).getRippleProgress() != -1.0f;
            if (useAlphaLayer) {
                canvas.saveLayer(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), null, 31);
            } else {
                canvas.save();
            }
            canvas.translate(getPaddingLeft(), getPaddingTop() + AndroidUtilities.dp(2.0f));
            for (SpoilerEffect eff2 : this.spoilers) {
                eff2.setColor(getPaint().getColor());
                eff2.draw(canvas);
            }
            if (useAlphaLayer) {
                this.path.rewind();
                this.spoilers.get(0).getRipplePath(this.path);
                if (this.xRefPaint == null) {
                    Paint paint = new Paint(1);
                    this.xRefPaint = paint;
                    paint.setColor(-16777216);
                    this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                }
                canvas.drawPath(this.path, this.xRefPaint);
            }
            canvas.restore();
        }
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
        if (layout != null && (getText() instanceof Spanned)) {
            SpoilerEffect.addSpoilers(this, this.spoilersPool, this.spoilers);
        }
        invalidate();
    }
}
