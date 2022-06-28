package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public class ReadAllMentionsMenu {
    public static final int TYPE_MENTIONS = 1;
    public static final int TYPE_REACTIONS = 0;

    public static ActionBarPopupWindow show(int type, Activity activity, FrameLayout contentView, View mentionButton, Theme.ResourcesProvider resourcesProvider, final Runnable onRead) {
        String str;
        int i;
        ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(activity);
        popupWindowLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
        ActionBarMenuSubItem cell = new ActionBarMenuSubItem((Context) activity, true, true, resourcesProvider);
        cell.setMinimumWidth(AndroidUtilities.dp(200.0f));
        if (type == 0) {
            i = R.string.ReadAllReactions;
            str = "ReadAllReactions";
        } else {
            i = R.string.ReadAllMentions;
            str = "ReadAllMentions";
        }
        cell.setTextAndIcon(LocaleController.getString(str, i), R.drawable.msg_seen);
        cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ReadAllMentionsMenu$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ReadAllMentionsMenu.lambda$show$0(onRead, view);
            }
        });
        popupWindowLayout.addView(cell);
        ActionBarPopupWindow scrimPopupWindow = new ActionBarPopupWindow(popupWindowLayout, -2, -2);
        scrimPopupWindow.setPauseNotifications(true);
        scrimPopupWindow.setDismissAnimationDuration(220);
        scrimPopupWindow.setOutsideTouchable(true);
        scrimPopupWindow.setClippingEnabled(true);
        scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        scrimPopupWindow.setFocusable(true);
        popupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        scrimPopupWindow.setInputMethodMode(2);
        scrimPopupWindow.setSoftInputMode(0);
        scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
        float x = ((mentionButton.getX() + mentionButton.getWidth()) - popupWindowLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f);
        float y = mentionButton.getY() - popupWindowLayout.getMeasuredHeight();
        scrimPopupWindow.showAtLocation(contentView, 51, (int) x, (int) y);
        return scrimPopupWindow;
    }

    public static /* synthetic */ void lambda$show$0(Runnable onRead, View view) {
        if (onRead != null) {
            onRead.run();
        }
    }
}
