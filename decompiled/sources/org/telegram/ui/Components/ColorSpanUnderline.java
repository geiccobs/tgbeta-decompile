package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
/* loaded from: classes5.dex */
public class ColorSpanUnderline extends ForegroundColorSpan {
    public ColorSpanUnderline(int color) {
        super(color);
    }

    @Override // android.text.style.ForegroundColorSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(true);
    }
}
