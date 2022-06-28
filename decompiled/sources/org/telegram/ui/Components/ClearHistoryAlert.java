package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
/* loaded from: classes5.dex */
public class ClearHistoryAlert extends BottomSheet {
    private boolean autoDeleteOnly;
    private CheckBoxCell cell;
    private int currentTimer;
    private ClearHistoryAlertDelegate delegate;
    private boolean dismissedDelayed;
    private LinearLayout linearLayout;
    private int[] location = new int[2];
    private int newTimer;
    private int scrollOffsetY;
    private BottomSheetCell setTimerButton;
    private Drawable shadowDrawable;

    /* loaded from: classes5.dex */
    public interface ClearHistoryAlertDelegate {
        void onAutoDeleteHistory(int i, int i2);

        void onClearHistory(boolean z);

        /* renamed from: org.telegram.ui.Components.ClearHistoryAlert$ClearHistoryAlertDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onClearHistory(ClearHistoryAlertDelegate _this, boolean revoke) {
            }

            public static void $default$onAutoDeleteHistory(ClearHistoryAlertDelegate _this, int ttl, int action) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private LinearLayout linearLayout;
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setText(CharSequence text) {
            this.textView.setText(text);
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x00df  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0110  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x032a  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x04ae  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x04bd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ClearHistoryAlert(android.content.Context r36, org.telegram.tgnet.TLRPC.User r37, org.telegram.tgnet.TLRPC.Chat r38, boolean r39, org.telegram.ui.ActionBar.Theme.ResourcesProvider r40) {
        /*
            Method dump skipped, instructions count: 1272
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ClearHistoryAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static /* synthetic */ void lambda$new$0(boolean[] deleteForAll, View v) {
        CheckBoxCell cell1 = (CheckBoxCell) v;
        deleteForAll[0] = !deleteForAll[0];
        cell1.setChecked(deleteForAll[0], true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ClearHistoryAlert */
    public /* synthetic */ void m2535lambda$new$1$orgtelegramuiComponentsClearHistoryAlert(View v) {
        if (this.dismissedDelayed) {
            return;
        }
        ClearHistoryAlertDelegate clearHistoryAlertDelegate = this.delegate;
        CheckBoxCell checkBoxCell = this.cell;
        clearHistoryAlertDelegate.onClearHistory(checkBoxCell != null && checkBoxCell.isChecked());
        dismiss();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ClearHistoryAlert */
    public /* synthetic */ void m2536lambda$new$2$orgtelegramuiComponentsClearHistoryAlert(View v) {
        int action;
        int time;
        if (this.dismissedDelayed) {
            return;
        }
        int time2 = this.newTimer;
        if (time2 != this.currentTimer) {
            this.dismissedDelayed = true;
            if (time2 == 3) {
                time = 2678400;
                action = 70;
            } else if (time2 == 2) {
                time = 604800;
                action = 70;
            } else if (time2 == 1) {
                time = 86400;
                action = 70;
            } else {
                time = 0;
                action = 71;
            }
            this.delegate.onAutoDeleteHistory(time, action);
        }
        if (this.dismissedDelayed) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ClearHistoryAlert.this.dismiss();
                }
            }, 200L);
        } else {
            dismiss();
        }
    }

    public void updateTimerButton(boolean animated) {
        if (this.currentTimer != this.newTimer || this.autoDeleteOnly) {
            this.setTimerButton.setVisibility(0);
            if (animated) {
                this.setTimerButton.animate().alpha(1.0f).setDuration(180L).start();
            } else {
                this.setTimerButton.setAlpha(1.0f);
            }
        } else if (animated) {
            this.setTimerButton.animate().alpha(0.0f).setDuration(180L).start();
        } else {
            this.setTimerButton.setVisibility(4);
            this.setTimerButton.setAlpha(0.0f);
        }
    }

    public void updateLayout() {
        View child = this.linearLayout.getChildAt(0);
        child.getLocationInWindow(this.location);
        int top = this.location[1] - AndroidUtilities.dp(this.autoDeleteOnly ? 6.0f : 19.0f);
        int newOffset = Math.max(top, 0);
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            this.containerView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void setDelegate(ClearHistoryAlertDelegate clearHistoryAlertDelegate) {
        this.delegate = clearHistoryAlertDelegate;
    }
}
