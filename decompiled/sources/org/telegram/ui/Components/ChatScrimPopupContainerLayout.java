package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.PopupSwipeBackLayout;
/* loaded from: classes3.dex */
public class ChatScrimPopupContainerLayout extends LinearLayout {
    private View bottomView;
    private int maxHeight;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout;
    private ReactionsContainerLayout reactionsLayout;

    public ChatScrimPopupContainerLayout(Context context) {
        super(context);
        setOrientation(1);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3 = this.maxHeight;
        if (i3 != 0) {
            i2 = View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE);
        }
        if (this.reactionsLayout != null && this.popupWindowLayout != null) {
            super.onMeasure(i, i2);
            int totalWidth = this.reactionsLayout.getTotalWidth();
            int i4 = 0;
            int measuredWidth = (this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack() : this.popupWindowLayout).getChildAt(0).getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
            if (measuredWidth > getMeasuredWidth()) {
                measuredWidth = getMeasuredWidth();
            }
            if (totalWidth > measuredWidth) {
                int dp = ((measuredWidth - AndroidUtilities.dp(16.0f)) / AndroidUtilities.dp(36.0f)) + 1;
                int dp2 = ((AndroidUtilities.dp(36.0f) * dp) + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(8.0f);
                if (dp2 <= totalWidth && dp != this.reactionsLayout.getItemsCount()) {
                    totalWidth = dp2;
                }
                this.reactionsLayout.getLayoutParams().width = totalWidth;
            } else {
                this.reactionsLayout.getLayoutParams().width = -2;
            }
            int measuredWidth2 = this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth() : 0;
            if (this.reactionsLayout.getLayoutParams().width != -2 && this.reactionsLayout.getLayoutParams().width + measuredWidth2 > getMeasuredWidth() && this.popupWindowLayout.getSwipeBack() != null && this.popupWindowLayout.getSwipeBack().getMeasuredWidth() > getMeasuredWidth()) {
                measuredWidth2 = (getMeasuredWidth() - this.reactionsLayout.getLayoutParams().width) + AndroidUtilities.dp(8.0f);
            }
            if (measuredWidth2 >= 0) {
                i4 = measuredWidth2;
            }
            ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = i4;
            if (this.bottomView != null) {
                if (this.popupWindowLayout.getSwipeBack() != null) {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = i4 + AndroidUtilities.dp(36.0f);
                } else {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f);
                }
            }
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(i, i2);
    }

    public void applyViewBottom(FrameLayout frameLayout) {
        this.bottomView = frameLayout;
    }

    public void setReactionsLayout(ReactionsContainerLayout reactionsContainerLayout) {
        this.reactionsLayout = reactionsContainerLayout;
    }

    public void setPopupWindowLayout(final ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        this.popupWindowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setOnSizeChangedListener(new ActionBarPopupWindow.onSizeChangedListener() { // from class: org.telegram.ui.Components.ChatScrimPopupContainerLayout$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.onSizeChangedListener
            public final void onSizeChanged() {
                ChatScrimPopupContainerLayout.this.lambda$setPopupWindowLayout$0(actionBarPopupWindowLayout);
            }
        });
        if (actionBarPopupWindowLayout.getSwipeBack() != null) {
            actionBarPopupWindowLayout.getSwipeBack().addOnSwipeBackProgressListener(new PopupSwipeBackLayout.OnSwipeBackProgressListener() { // from class: org.telegram.ui.Components.ChatScrimPopupContainerLayout$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.PopupSwipeBackLayout.OnSwipeBackProgressListener
                public final void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
                    ChatScrimPopupContainerLayout.this.lambda$setPopupWindowLayout$1(popupSwipeBackLayout, f, f2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setPopupWindowLayout$0(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationY(actionBarPopupWindowLayout.getVisibleHeight() - actionBarPopupWindowLayout.getMeasuredHeight());
        }
    }

    public /* synthetic */ void lambda$setPopupWindowLayout$1(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(1.0f - f2);
        }
    }

    public void setMaxHeight(int i) {
        this.maxHeight = i;
    }
}
