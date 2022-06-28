package androidx.core.view;

import android.view.View;
/* loaded from: classes3.dex */
public interface NestedScrollingParent3 extends NestedScrollingParent2 {
    void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed);
}
