package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
/* loaded from: classes5.dex */
public class TextPaintSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintSpan(TextPaint paint) {
        this.textPaint = paint;
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint p) {
        p.setColor(this.textPaint.getColor());
        p.setTypeface(this.textPaint.getTypeface());
        p.setFlags(this.textPaint.getFlags());
        p.setTextSize(this.textPaint.getTextSize());
        p.baselineShift = this.textPaint.baselineShift;
        p.bgColor = this.textPaint.bgColor;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint p) {
        p.setColor(this.textPaint.getColor());
        p.setTypeface(this.textPaint.getTypeface());
        p.setFlags(this.textPaint.getFlags());
        p.setTextSize(this.textPaint.getTextSize());
        p.baselineShift = this.textPaint.baselineShift;
        p.bgColor = this.textPaint.bgColor;
    }
}
