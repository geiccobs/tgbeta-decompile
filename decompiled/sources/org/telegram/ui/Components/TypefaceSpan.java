package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class TypefaceSpan extends MetricAffectingSpan {
    private int color;
    private int textSize;
    private Typeface typeface;

    public TypefaceSpan(Typeface tf) {
        this.typeface = tf;
    }

    public TypefaceSpan(Typeface tf, int size) {
        this.typeface = tf;
        this.textSize = size;
    }

    public TypefaceSpan(Typeface tf, int size, int textColor) {
        this.typeface = tf;
        if (size > 0) {
            this.textSize = size;
        }
        this.color = textColor;
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

    public void setColor(int value) {
        this.color = value;
    }

    public boolean isMono() {
        return this.typeface == Typeface.MONOSPACE;
    }

    public boolean isBold() {
        return this.typeface == AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM);
    }

    public boolean isItalic() {
        return this.typeface == AndroidUtilities.getTypeface("fonts/ritalic.ttf");
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint p) {
        Typeface typeface = this.typeface;
        if (typeface != null) {
            p.setTypeface(typeface);
        }
        int i = this.textSize;
        if (i != 0) {
            p.setTextSize(i);
        }
        p.setFlags(p.getFlags() | 128);
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint tp) {
        Typeface typeface = this.typeface;
        if (typeface != null) {
            tp.setTypeface(typeface);
        }
        int i = this.textSize;
        if (i != 0) {
            tp.setTextSize(i);
        }
        int i2 = this.color;
        if (i2 != 0) {
            tp.setColor(i2);
        }
        tp.setFlags(tp.getFlags() | 128);
    }
}
