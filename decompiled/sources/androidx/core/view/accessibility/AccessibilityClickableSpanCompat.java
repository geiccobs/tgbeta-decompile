package androidx.core.view.accessibility;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
/* loaded from: classes.dex */
public final class AccessibilityClickableSpanCompat extends ClickableSpan {
    private final int mClickableSpanActionId;
    private final AccessibilityNodeInfoCompat mNodeInfoCompat;
    private final int mOriginalClickableSpanId;

    public AccessibilityClickableSpanCompat(int originalClickableSpanId, AccessibilityNodeInfoCompat nodeInfoCompat, int clickableSpanActionId) {
        this.mOriginalClickableSpanId = originalClickableSpanId;
        this.mNodeInfoCompat = nodeInfoCompat;
        this.mClickableSpanActionId = clickableSpanActionId;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View unused) {
        Bundle bundle = new Bundle();
        bundle.putInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", this.mOriginalClickableSpanId);
        this.mNodeInfoCompat.performAction(this.mClickableSpanActionId, bundle);
    }
}
