package org.telegram.ui.Components;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.ViewCompat;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes5.dex */
public abstract class SeekBarAccessibilityDelegate extends View.AccessibilityDelegate {
    private static final CharSequence SEEK_BAR_CLASS_NAME = android.widget.SeekBar.class.getName();
    private final Map<View, Runnable> accessibilityEventRunnables = new HashMap(4);
    private final View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.Components.SeekBarAccessibilityDelegate.1
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            v.removeCallbacks((Runnable) SeekBarAccessibilityDelegate.this.accessibilityEventRunnables.remove(v));
            v.removeOnAttachStateChangeListener(this);
        }
    };

    protected abstract boolean canScrollBackward(View view);

    protected abstract boolean canScrollForward(View view);

    protected abstract void doScroll(View view, boolean z);

    @Override // android.view.View.AccessibilityDelegate
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (super.performAccessibilityAction(host, action, args)) {
            return true;
        }
        return performAccessibilityActionInternal(host, action, args);
    }

    public boolean performAccessibilityActionInternal(View host, int action, Bundle args) {
        boolean z = false;
        if (action != 4096 && action != 8192) {
            return false;
        }
        if (action == 8192) {
            z = true;
        }
        doScroll(host, z);
        if (host != null) {
            postAccessibilityEventRunnable(host);
        }
        return true;
    }

    public final boolean performAccessibilityActionInternal(int action, Bundle args) {
        return performAccessibilityActionInternal(null, action, args);
    }

    private void postAccessibilityEventRunnable(final View host) {
        if (!ViewCompat.isAttachedToWindow(host)) {
            return;
        }
        Runnable runnable = this.accessibilityEventRunnables.get(host);
        if (runnable == null) {
            Map<View, Runnable> map = this.accessibilityEventRunnables;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.SeekBarAccessibilityDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SeekBarAccessibilityDelegate.this.m2985x308b6507(host);
                }
            };
            runnable = runnable2;
            map.put(host, runnable2);
            host.addOnAttachStateChangeListener(this.onAttachStateChangeListener);
        } else {
            host.removeCallbacks(runnable);
        }
        host.postDelayed(runnable, 400L);
    }

    /* renamed from: lambda$postAccessibilityEventRunnable$0$org-telegram-ui-Components-SeekBarAccessibilityDelegate */
    public /* synthetic */ void m2985x308b6507(View host) {
        sendAccessibilityEvent(host, 4);
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        onInitializeAccessibilityNodeInfoInternal(host, info);
    }

    public void onInitializeAccessibilityNodeInfoInternal(View host, AccessibilityNodeInfo info) {
        info.setClassName(SEEK_BAR_CLASS_NAME);
        CharSequence contentDescription = getContentDescription(host);
        if (!TextUtils.isEmpty(contentDescription)) {
            info.setText(contentDescription);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (canScrollBackward(host)) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            }
            if (canScrollForward(host)) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            }
        }
    }

    public final void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        onInitializeAccessibilityNodeInfoInternal(null, info);
    }

    protected CharSequence getContentDescription(View host) {
        return null;
    }
}
