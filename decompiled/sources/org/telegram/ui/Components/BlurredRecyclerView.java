package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
/* loaded from: classes5.dex */
public class BlurredRecyclerView extends RecyclerListView {
    public int additionalClipBottom;
    public int blurTopPadding;
    boolean globalIgnoreLayout;
    int topPadding;

    public BlurredRecyclerView(Context context) {
        super(context);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        this.globalIgnoreLayout = true;
        updateTopPadding();
        super.setPadding(getPaddingLeft(), this.topPadding + this.blurTopPadding, getPaddingRight(), getPaddingBottom());
        this.globalIgnoreLayout = false;
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateTopPadding();
    }

    private void updateTopPadding() {
        if (getLayoutParams() == null) {
            return;
        }
        if (SharedConfig.chatBlurEnabled()) {
            this.blurTopPadding = AndroidUtilities.dp(203.0f);
            ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = -this.blurTopPadding;
            return;
        }
        this.blurTopPadding = 0;
        ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = 0;
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.globalIgnoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        int i = this.blurTopPadding;
        if (i != 0) {
            canvas.clipRect(0, i, getMeasuredWidth(), getMeasuredHeight() + this.additionalClipBottom);
            super.dispatchDraw(canvas);
            return;
        }
        super.dispatchDraw(canvas);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child.getY() + child.getMeasuredHeight() < this.blurTopPadding) {
            return true;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override // android.view.View
    public void setPadding(int left, int top, int right, int bottom) {
        this.topPadding = top;
        super.setPadding(left, this.blurTopPadding + top, right, bottom);
    }
}
