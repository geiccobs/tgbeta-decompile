package org.telegram.ui.Components;

import android.view.View;
/* loaded from: classes5.dex */
public abstract class IntSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    protected abstract int getMaxValue();

    protected abstract int getProgress();

    protected abstract void setProgress(int i);

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected void doScroll(View host, boolean backward) {
        int delta = getDelta();
        if (backward) {
            delta *= -1;
        }
        setProgress(Math.min(getMaxValue(), Math.max(getMinValue(), getProgress() + delta)));
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected boolean canScrollBackward(View host) {
        return getProgress() > getMinValue();
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected boolean canScrollForward(View host) {
        return getProgress() < getMaxValue();
    }

    protected int getMinValue() {
        return 0;
    }

    protected int getDelta() {
        return 1;
    }
}
