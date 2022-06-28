package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class VoIPButtonsLayout extends FrameLayout {
    int childPadding;
    int childWidth;
    int visibleChildCount;
    private int childSize = 68;
    private boolean startPadding = true;

    public VoIPButtonsLayout(Context context) {
        super(context);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        this.visibleChildCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getVisibility() != 8) {
                this.visibleChildCount++;
            }
        }
        int i2 = this.childSize;
        this.childWidth = AndroidUtilities.dp(i2);
        int maxChildHeigth = 0;
        this.childPadding = ((width / getChildCount()) - this.childWidth) / 2;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            if (getChildAt(i3).getVisibility() != 8) {
                getChildAt(i3).measure(View.MeasureSpec.makeMeasureSpec(this.childWidth, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                if (getChildAt(i3).getMeasuredHeight() > maxChildHeigth) {
                    maxChildHeigth = getChildAt(i3).getMeasuredHeight();
                }
            }
        }
        int h = Math.max(maxChildHeigth, AndroidUtilities.dp(80.0f));
        setMeasuredDimension(width, h);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.startPadding) {
            int startFrom = (int) (((getChildCount() - this.visibleChildCount) / 2.0f) * (this.childWidth + (this.childPadding * 2)));
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    int i2 = this.childPadding;
                    child.layout(startFrom + i2, 0, i2 + startFrom + child.getMeasuredWidth(), child.getMeasuredHeight());
                    startFrom += (this.childPadding * 2) + child.getMeasuredWidth();
                }
            }
            return;
        }
        int padding = this.visibleChildCount > 0 ? (getMeasuredWidth() - this.childWidth) / (this.visibleChildCount - 1) : 0;
        int k = 0;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View child2 = getChildAt(i3);
            if (child2.getVisibility() != 8) {
                child2.layout(k * padding, 0, (k * padding) + child2.getMeasuredWidth(), child2.getMeasuredHeight());
                k++;
            }
        }
    }

    public void setChildSize(int childSize) {
        this.childSize = childSize;
    }

    public void setUseStartPadding(boolean startPadding) {
        this.startPadding = startPadding;
    }
}
