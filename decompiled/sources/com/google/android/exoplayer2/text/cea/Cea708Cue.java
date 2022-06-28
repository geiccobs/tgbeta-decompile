package com.google.android.exoplayer2.text.cea;

import android.text.Layout;
import com.google.android.exoplayer2.text.Cue;
/* loaded from: classes3.dex */
final class Cea708Cue extends Cue implements Comparable<Cea708Cue> {
    public final int priority;

    public Cea708Cue(CharSequence text, Layout.Alignment textAlignment, float line, int lineType, int lineAnchor, float position, int positionAnchor, float size, boolean windowColorSet, int windowColor, int priority) {
        super(text, textAlignment, line, lineType, lineAnchor, position, positionAnchor, size, windowColorSet, windowColor);
        this.priority = priority;
    }

    public int compareTo(Cea708Cue other) {
        int i = other.priority;
        int i2 = this.priority;
        if (i < i2) {
            return -1;
        }
        if (i > i2) {
            return 1;
        }
        return 0;
    }
}
