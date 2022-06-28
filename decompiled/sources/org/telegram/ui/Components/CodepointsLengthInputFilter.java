package org.telegram.ui.Components;

import android.text.InputFilter;
import android.text.Spanned;
/* loaded from: classes5.dex */
public class CodepointsLengthInputFilter implements InputFilter {
    private final int mMax;

    public CodepointsLengthInputFilter(int max) {
        this.mMax = max;
    }

    @Override // android.text.InputFilter
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int destAfter = Character.codePointCount(dest, 0, dest.length()) - Character.codePointCount(dest, dstart, dend);
        int keep = this.mMax - destAfter;
        if (keep <= 0) {
            return "";
        }
        if (keep >= Character.codePointCount(source, start, end)) {
            return null;
        }
        int keep2 = keep + start;
        if (Character.isHighSurrogate(source.charAt(keep2 - 1)) && keep2 - 1 == start) {
            return "";
        }
        return source.subSequence(start, keep2);
    }

    public int getMax() {
        return this.mMax;
    }
}
