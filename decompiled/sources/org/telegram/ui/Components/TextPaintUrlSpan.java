package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
/* loaded from: classes5.dex */
public class TextPaintUrlSpan extends MetricAffectingSpan {
    private int color;
    private String currentUrl;
    private TextPaint textPaint;
    private int textSize;

    public TextPaintUrlSpan(TextPaint paint, String url) {
        this.textPaint = paint;
        this.currentUrl = url;
    }

    public String getUrl() {
        return this.currentUrl;
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
