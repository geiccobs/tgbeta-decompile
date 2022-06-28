package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
/* loaded from: classes5.dex */
public class AutoDeletePopupWrapper {
    View backItem;
    Callback callback;
    private final ActionBarMenuSubItem disableItem;
    long lastDismissTime;
    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;

    /* loaded from: classes5.dex */
    public interface Callback {
        void dismiss();

        void setAutoDeleteHistory(int i, int i2);
    }

    public AutoDeletePopupWrapper(final Context context, final PopupSwipeBackLayout swipeBackLayout, final Callback callback, boolean createBackground, final Theme.ResourcesProvider resourcesProvider) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, createBackground ? R.drawable.popup_fixed_alert : 0, resourcesProvider);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        this.callback = callback;
        if (swipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_arrow_back, LocaleController.getString("Back", R.string.Back), false, resourcesProvider);
            this.backItem = addItem;
            addItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PopupSwipeBackLayout.this.closeForeground();
                }
            });
        }
        ActionBarMenuSubItem item = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_autodelete_1d, LocaleController.getString("AutoDelete1Day", R.string.AutoDelete1Day), false, resourcesProvider);
        item.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoDeletePopupWrapper.this.m2207lambda$new$1$orgtelegramuiComponentsAutoDeletePopupWrapper(callback, view);
            }
        });
        ActionBarMenuSubItem item2 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_autodelete_1w, LocaleController.getString("AutoDelete7Days", R.string.AutoDelete7Days), false, resourcesProvider);
        item2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoDeletePopupWrapper.this.m2208lambda$new$2$orgtelegramuiComponentsAutoDeletePopupWrapper(callback, view);
            }
        });
        ActionBarMenuSubItem item3 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_autodelete_1m, LocaleController.getString("AutoDelete1Month", R.string.AutoDelete1Month), false, resourcesProvider);
        item3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoDeletePopupWrapper.this.m2209lambda$new$3$orgtelegramuiComponentsAutoDeletePopupWrapper(callback, view);
            }
        });
        ActionBarMenuSubItem item4 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_customize, LocaleController.getString("AutoDeleteCustom", R.string.AutoDeleteCustom), false, resourcesProvider);
        item4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoDeletePopupWrapper.this.m2210lambda$new$5$orgtelegramuiComponentsAutoDeletePopupWrapper(context, resourcesProvider, callback, view);
            }
        });
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_disable, LocaleController.getString("AutoDeleteDisable", R.string.AutoDeleteDisable), false, resourcesProvider);
        this.disableItem = addItem2;
        addItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoDeletePopupWrapper.this.m2211lambda$new$6$orgtelegramuiComponentsAutoDeletePopupWrapper(callback, view);
            }
        });
        addItem2.setColors(Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogTextRed2));
        View gap = new FrameLayout(context);
        gap.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator, resourcesProvider));
        gap.setTag(R.id.fit_width_tag, 1);
        this.windowLayout.addView(gap, LayoutHelper.createLinear(-1, 8));
        TextView textView = new TextView(context);
        textView.setTag(R.id.fit_width_tag, 1);
        textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setText(LocaleController.getString("AutoDeletePopupDescription", R.string.AutoDeletePopupDescription));
        this.windowLayout.addView((View) textView, LayoutHelper.createLinear(-1, -2));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AutoDeletePopupWrapper */
    public /* synthetic */ void m2207lambda$new$1$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback, View view) {
        dismiss();
        callback.setAutoDeleteHistory(86400, 70);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-AutoDeletePopupWrapper */
    public /* synthetic */ void m2208lambda$new$2$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback, View view) {
        dismiss();
        callback.setAutoDeleteHistory(604800, 70);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-AutoDeletePopupWrapper */
    public /* synthetic */ void m2209lambda$new$3$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback, View view) {
        dismiss();
        callback.setAutoDeleteHistory(2678400, 70);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-AutoDeletePopupWrapper */
    public /* synthetic */ void m2210lambda$new$5$orgtelegramuiComponentsAutoDeletePopupWrapper(Context context, Theme.ResourcesProvider resourcesProvider, final Callback callback, View view) {
        dismiss();
        AlertsCreator.createAutoDeleteDatePickerDialog(context, resourcesProvider, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i) {
                AutoDeletePopupWrapper.Callback.this.setAutoDeleteHistory(i * 60, timeInMinutes == 0 ? 71 : 70);
            }
        });
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-AutoDeletePopupWrapper */
    public /* synthetic */ void m2211lambda$new$6$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback, View view) {
        dismiss();
        callback.setAutoDeleteHistory(0, 71);
    }

    private void dismiss() {
        this.callback.dismiss();
        this.lastDismissTime = System.currentTimeMillis();
    }

    /* renamed from: updateItems */
    public void m2212xf8eeadab(final int ttl) {
        if (System.currentTimeMillis() - this.lastDismissTime < 200) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AutoDeletePopupWrapper$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    AutoDeletePopupWrapper.this.m2212xf8eeadab(ttl);
                }
            });
        } else if (ttl == 0) {
            this.disableItem.setVisibility(8);
        } else {
            this.disableItem.setVisibility(0);
        }
    }
}
