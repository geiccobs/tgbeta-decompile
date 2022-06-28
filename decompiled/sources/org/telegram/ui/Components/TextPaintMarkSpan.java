package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
/* loaded from: classes5.dex */
public class TextPaintMarkSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintMarkSpan(TextPaint paint) {
        this.textPaint = paint;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint p) {
        TextPaint textPaint = this.textPaint;
        if (textPaint != null) {
            p.setColor(textPaint.getColor());
            p.setTypeface(this.textPaint.getTypeface());
            p.setFlags(this.textPaint.getFlags());
            p.setTextSize(this.textPaint.getTextSize());
            p.baselineShift = this.textPaint.baselineShift;
            p.bgColor = this.textPaint.bgColor;
        }
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint p) {
        TextPaint textPaint = this.textPaint;
        if (textPaint != null) {
            p.setColor(textPaint.getColor());
            p.setTypeface(this.textPaint.getTypeface());
            p.setFlags(this.textPaint.getFlags());
            p.setTextSize(this.textPaint.getTextSize());
            p.baselineShift = this.textPaint.baselineShift;
            p.bgColor = this.textPaint.bgColor;
        }
    }
}
