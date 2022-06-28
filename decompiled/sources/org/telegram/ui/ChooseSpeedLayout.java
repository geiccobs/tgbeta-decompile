package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ChooseSpeedLayout;
import org.telegram.ui.Components.PopupSwipeBackLayout;
/* loaded from: classes4.dex */
public class ChooseSpeedLayout {
    ActionBarMenuSubItem[] speedItems = new ActionBarMenuSubItem[5];
    ActionBarPopupWindow.ActionBarPopupWindowLayout speedSwipeBackLayout;

    /* loaded from: classes4.dex */
    public interface Callback {
        void onSpeedSelected(float f);
    }

    public ChooseSpeedLayout(Context context, final PopupSwipeBackLayout swipeBackLayout, final Callback callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, null);
        this.speedSwipeBackLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem backItem = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_arrow_back, LocaleController.getString("Back", R.string.Back), false, null);
        backItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PopupSwipeBackLayout.this.closeForeground();
            }
        });
        backItem.setColors(-328966, -328966);
        backItem.setSelectorColor(268435455);
        ActionBarMenuSubItem item = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_2, LocaleController.getString("SpeedVerySlow", R.string.SpeedVerySlow), false, null);
        item.setColors(-328966, -328966);
        item.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(0.25f);
            }
        });
        item.setSelectorColor(268435455);
        this.speedItems[0] = item;
        ActionBarMenuSubItem item2 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_0_5, LocaleController.getString("SpeedSlow", R.string.SpeedSlow), false, null);
        item2.setColors(-328966, -328966);
        item2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(0.5f);
            }
        });
        item2.setSelectorColor(268435455);
        this.speedItems[1] = item2;
        ActionBarMenuSubItem item3 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_1, LocaleController.getString("SpeedNormal", R.string.SpeedNormal), false, null);
        item3.setColors(-328966, -328966);
        item3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(1.0f);
            }
        });
        item3.setSelectorColor(268435455);
        this.speedItems[2] = item3;
        ActionBarMenuSubItem item4 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_1_5, LocaleController.getString("SpeedFast", R.string.SpeedFast), false, null);
        item4.setColors(-328966, -328966);
        item4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(1.5f);
            }
        });
        item4.setSelectorColor(268435455);
        this.speedItems[3] = item4;
        ActionBarMenuSubItem item5 = ActionBarMenuItem.addItem(this.speedSwipeBackLayout, R.drawable.msg_speed_2, LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast), false, null);
        item5.setColors(-328966, -328966);
        item5.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChooseSpeedLayout$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChooseSpeedLayout.Callback.this.onSpeedSelected(2.0f);
            }
        });
        item5.setSelectorColor(268435455);
        this.speedItems[4] = item5;
    }

    public void update(float currentVideoSpeed) {
        for (int a = 0; a < this.speedItems.length; a++) {
            if ((a == 0 && Math.abs(currentVideoSpeed - 0.25f) < 0.001f) || ((a == 1 && Math.abs(currentVideoSpeed - 0.5f) < 0.001f) || ((a == 2 && Math.abs(currentVideoSpeed - 1.0f) < 0.001f) || ((a == 3 && Math.abs(currentVideoSpeed - 1.5f) < 0.001f) || (a == 4 && Math.abs(currentVideoSpeed - 2.0f) < 0.001f))))) {
                this.speedItems[a].setColors(-9718023, -9718023);
            } else {
                this.speedItems[a].setColors(-328966, -328966);
            }
        }
    }
}
