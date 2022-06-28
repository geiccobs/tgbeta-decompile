package org.telegram.ui.Components;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
/* loaded from: classes5.dex */
public abstract class FloatSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    private final boolean setPercentsEnabled;

    protected abstract float getProgress();

    protected abstract void setProgress(float f);

    public FloatSeekBarAccessibilityDelegate() {
        this(false);
    }

    public FloatSeekBarAccessibilityDelegate(boolean setPercentsEnabled) {
        this.setPercentsEnabled = setPercentsEnabled;
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    public void onInitializeAccessibilityNodeInfoInternal(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(host, info);
        if (this.setPercentsEnabled) {
            AccessibilityNodeInfoCompat infoCompat = AccessibilityNodeInfoCompat.wrap(info);
            infoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS);
            infoCompat.setRangeInfo(AccessibilityNodeInfoCompat.RangeInfoCompat.obtain(1, getMinValue(), getMaxValue(), getProgress()));
        }
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    public boolean performAccessibilityActionInternal(View host, int action, Bundle args) {
        if (super.performAccessibilityActionInternal(host, action, args)) {
            return true;
        }
        if (action == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS.getId()) {
            setProgress(args.getFloat(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_PROGRESS_VALUE));
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected void doScroll(View host, boolean backward) {
        float delta = getDelta();
        if (backward) {
            delta *= -1.0f;
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

    protected float getMinValue() {
        return 0.0f;
    }

    protected float getMaxValue() {
        return 1.0f;
    }

    public float getDelta() {
        return 0.05f;
    }
}
