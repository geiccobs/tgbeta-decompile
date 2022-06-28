package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ForegroundColorSpanThemable extends CharacterStyle implements UpdateAppearance {
    private int color;
    private String colorKey;
    private final Theme.ResourcesProvider resourcesProvider;

    public ForegroundColorSpanThemable(String colorKey) {
        this(colorKey, null);
    }

    public ForegroundColorSpanThemable(String colorKey, Theme.ResourcesProvider resourcesProvider) {
        this.colorKey = colorKey;
        this.resourcesProvider = resourcesProvider;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        this.color = Theme.getColor(this.colorKey, this.resourcesProvider);
        int color = textPaint.getColor();
        int i = this.color;
        if (color != i) {
            textPaint.setColor(i);
        }
    }
}
