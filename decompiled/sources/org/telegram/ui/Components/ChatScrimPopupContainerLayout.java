package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.PopupSwipeBackLayout;
/* loaded from: classes5.dex */
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = this.maxHeight;
        if (i != 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        }
        if (this.reactionsLayout != null && this.popupWindowLayout != null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int reactionsLayoutTotalWidth = this.reactionsLayout.getTotalWidth();
            View menuContainer = this.popupWindowLayout.getSwipeBack() != null ? this.popupWindowLayout.getSwipeBack().getChildAt(0) : this.popupWindowLayout.getChildAt(0);
            int maxReactionsLayoutWidth = menuContainer.getMeasuredWidth() + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(36.0f);
            if (maxReactionsLayoutWidth > getMeasuredWidth()) {
                maxReactionsLayoutWidth = getMeasuredWidth();
            }
            if (reactionsLayoutTotalWidth > maxReactionsLayoutWidth) {
                int maxFullCount = ((maxReactionsLayoutWidth - AndroidUtilities.dp(16.0f)) / AndroidUtilities.dp(36.0f)) + 1;
                int newWidth = ((AndroidUtilities.dp(36.0f) * maxFullCount) + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(8.0f);
                if (newWidth > reactionsLayoutTotalWidth || maxFullCount == this.reactionsLayout.getItemsCount()) {
                    newWidth = reactionsLayoutTotalWidth;
                }
                this.reactionsLayout.getLayoutParams().width = newWidth;
            } else {
                this.reactionsLayout.getLayoutParams().width = -2;
            }
            int widthDiff = 0;
            if (this.popupWindowLayout.getSwipeBack() != null) {
                widthDiff = this.popupWindowLayout.getSwipeBack().getMeasuredWidth() - this.popupWindowLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
            }
            if (this.reactionsLayout.getLayoutParams().width != -2 && this.reactionsLayout.getLayoutParams().width + widthDiff > getMeasuredWidth() && this.popupWindowLayout.getSwipeBack() != null && this.popupWindowLayout.getSwipeBack().getMeasuredWidth() > getMeasuredWidth()) {
                widthDiff = (getMeasuredWidth() - this.reactionsLayout.getLayoutParams().width) + AndroidUtilities.dp(8.0f);
            }
            if (widthDiff < 0) {
                widthDiff = 0;
            }
            ((LinearLayout.LayoutParams) this.reactionsLayout.getLayoutParams()).rightMargin = widthDiff;
            if (this.bottomView != null) {
                if (this.popupWindowLayout.getSwipeBack() != null) {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f) + widthDiff;
                } else {
                    ((LinearLayout.LayoutParams) this.bottomView.getLayoutParams()).rightMargin = AndroidUtilities.dp(36.0f);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void applyViewBottom(FrameLayout bottomView) {
        this.bottomView = bottomView;
    }

    public void setReactionsLayout(ReactionsContainerLayout reactionsLayout) {
        this.reactionsLayout = reactionsLayout;
    }

    public void setPopupWindowLayout(final ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout) {
        this.popupWindowLayout = popupWindowLayout;
        popupWindowLayout.setOnSizeChangedListener(new ActionBarPopupWindow.onSizeChangedListener() { // from class: org.telegram.ui.Components.ChatScrimPopupContainerLayout$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.onSizeChangedListener
            public final void onSizeChanged() {
                ChatScrimPopupContainerLayout.this.m2523xaa93e743(popupWindowLayout);
            }
        });
        if (popupWindowLayout.getSwipeBack() != null) {
            popupWindowLayout.getSwipeBack().addOnSwipeBackProgressListener(new PopupSwipeBackLayout.OnSwipeBackProgressListener() { // from class: org.telegram.ui.Components.ChatScrimPopupContainerLayout$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.PopupSwipeBackLayout.OnSwipeBackProgressListener
                public final void onSwipeBackProgress(PopupSwipeBackLayout popupSwipeBackLayout, float f, float f2) {
                    ChatScrimPopupContainerLayout.this.m2524x3780fe62(popupSwipeBackLayout, f, f2);
                }
            });
        }
    }

    /* renamed from: lambda$setPopupWindowLayout$0$org-telegram-ui-Components-ChatScrimPopupContainerLayout */
    public /* synthetic */ void m2523xaa93e743(ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout) {
        View view = this.bottomView;
        if (view != null) {
            view.setTranslationY(popupWindowLayout.getVisibleHeight() - popupWindowLayout.getMeasuredHeight());
        }
    }

    /* renamed from: lambda$setPopupWindowLayout$1$org-telegram-ui-Components-ChatScrimPopupContainerLayout */
    public /* synthetic */ void m2524x3780fe62(PopupSwipeBackLayout layout, float toProgress, float progress) {
        View view = this.bottomView;
        if (view != null) {
            view.setAlpha(1.0f - progress);
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}
