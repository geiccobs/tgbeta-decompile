package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
/* loaded from: classes5.dex */
public class ChatNotificationsPopupWrapper {
    private static final String LAST_SELECTED_TIME_KEY_1 = "last_selected_mute_until_time";
    private static final String LAST_SELECTED_TIME_KEY_2 = "last_selected_mute_until_time2";
    View backItem;
    Callback callback;
    int currentAccount;
    private final boolean isProfile;
    long lastDismissTime;
    ActionBarMenuSubItem muteForLastSelected;
    private int muteForLastSelected1Time;
    ActionBarMenuSubItem muteForLastSelected2;
    private int muteForLastSelected2Time;
    ActionBarMenuSubItem muteUnmuteButton;
    ActionBarPopupWindow popupWindow;
    ActionBarMenuSubItem soundToggle;
    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;

    public ChatNotificationsPopupWrapper(final Context context, final int currentAccount, final PopupSwipeBackLayout swipeBackLayout, boolean createBackground, boolean isProfile, final Callback callback, final Theme.ResourcesProvider resourcesProvider) {
        this.currentAccount = currentAccount;
        this.callback = callback;
        this.isProfile = isProfile;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, createBackground ? R.drawable.popup_fixed_alert : 0, resourcesProvider);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        if (swipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_arrow_back, LocaleController.getString("Back", R.string.Back), false, resourcesProvider);
            this.backItem = addItem;
            addItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PopupSwipeBackLayout.this.closeForeground();
                }
            });
        }
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_tone_on, LocaleController.getString("SoundOn", R.string.SoundOn), false, resourcesProvider);
        this.soundToggle = addItem2;
        addItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatNotificationsPopupWrapper.this.m2516xfd410a(callback, view);
            }
        });
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_mute_1h, LocaleController.getString("MuteFor1h", R.string.MuteFor1h), false, resourcesProvider);
        this.muteForLastSelected = addItem3;
        addItem3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatNotificationsPopupWrapper.this.m2517x8dea5829(callback, view);
            }
        });
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_mute_1h, LocaleController.getString("MuteFor1h", R.string.MuteFor1h), false, resourcesProvider);
        this.muteForLastSelected2 = addItem4;
        addItem4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatNotificationsPopupWrapper.this.m2518x1ad76f48(callback, view);
            }
        });
        ActionBarMenuSubItem item = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_mute_period, LocaleController.getString("MuteForPopup", R.string.MuteForPopup), false, resourcesProvider);
        item.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatNotificationsPopupWrapper.this.m2519xc19eb4a5(context, resourcesProvider, currentAccount, callback, view);
            }
        });
        ActionBarMenuSubItem item2 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_customize, LocaleController.getString("NotificationsCustomize", R.string.NotificationsCustomize), false, resourcesProvider);
        item2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatNotificationsPopupWrapper.this.m2520x4e8bcbc4(callback, view);
            }
        });
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this.windowLayout, 0, "", false, resourcesProvider);
        this.muteUnmuteButton = addItem5;
        addItem5.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatNotificationsPopupWrapper.this.m2521x6865fa02(callback, view);
            }
        });
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatNotificationsPopupWrapper */
    public /* synthetic */ void m2516xfd410a(Callback callback, View view) {
        dismiss();
        callback.toggleSound();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatNotificationsPopupWrapper */
    public /* synthetic */ void m2517x8dea5829(Callback callback, View view) {
        dismiss();
        callback.muteFor(this.muteForLastSelected1Time);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatNotificationsPopupWrapper */
    public /* synthetic */ void m2518x1ad76f48(Callback callback, View view) {
        dismiss();
        callback.muteFor(this.muteForLastSelected2Time);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatNotificationsPopupWrapper */
    public /* synthetic */ void m2519xc19eb4a5(Context context, Theme.ResourcesProvider resourcesProvider, final int currentAccount, final Callback callback, View view) {
        dismiss();
        AlertsCreator.createMuteForPickerDialog(context, resourcesProvider, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatNotificationsPopupWrapper.lambda$new$4(i, r2, r3);
                    }
                }, 16L);
            }
        });
    }

    public static /* synthetic */ void lambda$new$4(int inSecond, int currentAccount, Callback callback) {
        if (inSecond != 0) {
            SharedPreferences sharedPreferences = MessagesController.getNotificationsSettings(currentAccount);
            int time1 = sharedPreferences.getInt(LAST_SELECTED_TIME_KEY_1, 0);
            sharedPreferences.edit().putInt(LAST_SELECTED_TIME_KEY_1, inSecond).putInt(LAST_SELECTED_TIME_KEY_2, time1).apply();
        }
        callback.muteFor(inSecond);
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatNotificationsPopupWrapper */
    public /* synthetic */ void m2520x4e8bcbc4(Callback callback, View view) {
        dismiss();
        callback.showCustomize();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatNotificationsPopupWrapper */
    public /* synthetic */ void m2521x6865fa02(final Callback callback, View view) {
        dismiss();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ChatNotificationsPopupWrapper.Callback.this.toggleMute();
            }
        });
    }

    private void dismiss() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.popupWindow.dismiss();
        }
        this.callback.dismiss();
        this.lastDismissTime = System.currentTimeMillis();
    }

    /* renamed from: update */
    public void m2522x80790d7d(final long dialogId) {
        int color;
        int time1;
        int time12;
        if (System.currentTimeMillis() - this.lastDismissTime < 200) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatNotificationsPopupWrapper$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ChatNotificationsPopupWrapper.this.m2522x80790d7d(dialogId);
                }
            });
            return;
        }
        boolean muted = MessagesController.getInstance(this.currentAccount).isDialogMuted(dialogId);
        if (muted) {
            this.muteUnmuteButton.setTextAndIcon(LocaleController.getString("UnmuteNotifications", R.string.UnmuteNotifications), R.drawable.msg_unmute);
            color = Theme.getColor(Theme.key_wallet_greenText);
            this.soundToggle.setVisibility(8);
        } else {
            this.muteUnmuteButton.setTextAndIcon(LocaleController.getString("MuteNotifications", R.string.MuteNotifications), R.drawable.msg_mute);
            color = Theme.getColor(Theme.key_dialogTextRed);
            this.soundToggle.setVisibility(0);
            boolean soundOn = MessagesController.getInstance(this.currentAccount).isDialogNotificationsSoundEnabled(dialogId);
            if (soundOn) {
                this.soundToggle.setTextAndIcon(LocaleController.getString("SoundOff", R.string.SoundOff), R.drawable.msg_tone_off);
            } else {
                this.soundToggle.setTextAndIcon(LocaleController.getString("SoundOn", R.string.SoundOn), R.drawable.msg_tone_on);
            }
        }
        if (muted) {
            time12 = 0;
            time1 = 0;
        } else {
            SharedPreferences sharedPreferences = MessagesController.getNotificationsSettings(this.currentAccount);
            int time13 = sharedPreferences.getInt(LAST_SELECTED_TIME_KEY_1, 0);
            int i = sharedPreferences.getInt(LAST_SELECTED_TIME_KEY_2, 0);
            time12 = time13;
            time1 = i;
        }
        if (time12 == 0) {
            this.muteForLastSelected.setVisibility(8);
        } else {
            this.muteForLastSelected1Time = time12;
            this.muteForLastSelected.setVisibility(0);
            this.muteForLastSelected.getImageView().setImageDrawable(TimerDrawable.getTtlIcon(time12));
            this.muteForLastSelected.setText(formatMuteForTime(time12));
        }
        if (time1 == 0) {
            this.muteForLastSelected2.setVisibility(8);
        } else {
            this.muteForLastSelected2Time = time1;
            this.muteForLastSelected2.setVisibility(0);
            this.muteForLastSelected2.getImageView().setImageDrawable(TimerDrawable.getTtlIcon(time1));
            this.muteForLastSelected2.setText(formatMuteForTime(time1));
        }
        this.muteUnmuteButton.setColors(color, color);
    }

    private String formatMuteForTime(int time) {
        StringBuilder stringBuilder = new StringBuilder();
        int days = time / 86400;
        int hours = (time - (86400 * days)) / 3600;
        if (days != 0) {
            stringBuilder.append(days);
            stringBuilder.append(LocaleController.getString("SecretChatTimerDays", R.string.SecretChatTimerDays));
        }
        if (hours != 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(hours);
            stringBuilder.append(LocaleController.getString("SecretChatTimerHours", R.string.SecretChatTimerHours));
        }
        return LocaleController.formatString("MuteForButton", R.string.MuteForButton, stringBuilder.toString());
    }

    public void showAsOptions(BaseFragment parentFragment, View anchorView, float touchedX, float touchedY) {
        if (parentFragment == null || parentFragment.getFragmentView() == null) {
            return;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.windowLayout, -2, -2);
        this.popupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setPauseNotifications(true);
        this.popupWindow.setDismissAnimationDuration(220);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setClippingEnabled(true);
        this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        this.popupWindow.setFocusable(true);
        this.windowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.getContentView().setFocusableInTouchMode(true);
        float x = touchedX;
        float y = touchedY;
        for (View view = anchorView; view != parentFragment.getFragmentView(); view = (View) view.getParent()) {
            x += view.getX();
            y += view.getY();
        }
        this.popupWindow.showAtLocation(parentFragment.getFragmentView(), 0, (int) (x - (this.windowLayout.getMeasuredWidth() / 2.0f)), (int) (y - (this.windowLayout.getMeasuredHeight() / 2.0f)));
        this.popupWindow.dimBehind();
    }

    /* loaded from: classes5.dex */
    public interface Callback {
        void dismiss();

        void muteFor(int i);

        void showCustomize();

        void toggleMute();

        void toggleSound();

        /* renamed from: org.telegram.ui.Components.ChatNotificationsPopupWrapper$Callback$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$dismiss(Callback _this) {
            }
        }
    }
}
