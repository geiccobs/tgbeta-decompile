package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell2;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSettingsActivity;
/* loaded from: classes4.dex */
public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private ListAdapter adapter;
    private boolean addingException;
    private AnimatorSet animatorSet;
    ChatAvatarContainer avatarContainer;
    private int avatarRow;
    private int avatarSectionRow;
    private int callsRow;
    private int callsVibrateRow;
    private int colorRow;
    private int customResetRow;
    private int customResetShadowRow;
    private ProfileNotificationsActivityDelegate delegate;
    private long dialogId;
    private int enableRow;
    private int generalRow;
    private int ledInfoRow;
    private int ledRow;
    private RecyclerListView listView;
    private boolean needReset;
    private boolean notificationsEnabled;
    private int popupDisabledRow;
    private int popupEnabledRow;
    private int popupInfoRow;
    private int popupRow;
    private int previewRow;
    private int priorityInfoRow;
    private int priorityRow;
    private Theme.ResourcesProvider resourcesProvider;
    private int ringtoneInfoRow;
    private int ringtoneRow;
    private int rowCount;
    private int smartRow;
    private int soundRow;
    private int vibrateRow;

    /* loaded from: classes4.dex */
    public interface ProfileNotificationsActivityDelegate {
        void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException);

        void didRemoveException(long j);

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity$ProfileNotificationsActivityDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$didRemoveException(ProfileNotificationsActivityDelegate _this, long dialog_id) {
            }
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        this(args, null);
    }

    public ProfileNotificationsActivity(Bundle args, Theme.ResourcesProvider resourcesProvider) {
        super(args);
        this.resourcesProvider = resourcesProvider;
        this.dialogId = args.getLong("dialog_id");
        this.addingException = args.getBoolean("exception", false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        boolean isChannel;
        this.rowCount = 0;
        boolean z = this.addingException;
        if (z) {
            int i = 0 + 1;
            this.rowCount = i;
            this.avatarRow = 0;
            this.rowCount = i + 1;
            this.avatarSectionRow = i;
        } else {
            this.avatarRow = -1;
            this.avatarSectionRow = -1;
        }
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.generalRow = i2;
        if (z) {
            this.rowCount = i3 + 1;
            this.enableRow = i3;
        } else {
            this.enableRow = -1;
        }
        if (!DialogObject.isEncryptedDialog(this.dialogId)) {
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.previewRow = i4;
        } else {
            this.previewRow = -1;
        }
        int i5 = this.rowCount;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.soundRow = i5;
        this.rowCount = i6 + 1;
        this.vibrateRow = i6;
        if (DialogObject.isChatDialog(this.dialogId)) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.smartRow = i7;
        } else {
            this.smartRow = -1;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.priorityRow = i8;
        } else {
            this.priorityRow = -1;
        }
        int i9 = this.rowCount;
        this.rowCount = i9 + 1;
        this.priorityInfoRow = i9;
        if (DialogObject.isChatDialog(this.dialogId)) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId));
            isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
        } else {
            isChannel = false;
        }
        if (!DialogObject.isEncryptedDialog(this.dialogId) && !isChannel) {
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.popupRow = i10;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.popupEnabledRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.popupDisabledRow = i12;
            this.rowCount = i13 + 1;
            this.popupInfoRow = i13;
        } else {
            this.popupRow = -1;
            this.popupEnabledRow = -1;
            this.popupDisabledRow = -1;
            this.popupInfoRow = -1;
        }
        if (DialogObject.isUserDialog(this.dialogId)) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.callsRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.callsVibrateRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.ringtoneRow = i16;
            this.rowCount = i17 + 1;
            this.ringtoneInfoRow = i17;
        } else {
            this.callsRow = -1;
            this.callsVibrateRow = -1;
            this.ringtoneRow = -1;
            this.ringtoneInfoRow = -1;
        }
        int i18 = this.rowCount;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.ledRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.colorRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.ledInfoRow = i20;
        if (!this.addingException) {
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.customResetRow = i21;
            this.rowCount = i22 + 1;
            this.customResetShadowRow = i22;
        } else {
            this.customResetRow = -1;
            this.customResetShadowRow = -1;
        }
        boolean defaultEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.dialogId);
        if (this.addingException) {
            this.notificationsEnabled = !defaultEnabled;
        } else {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            boolean hasOverride = preferences.contains("notify2_" + this.dialogId);
            int value = preferences.getInt("notify2_" + this.dialogId, 0);
            if (value == 0) {
                if (hasOverride) {
                    this.notificationsEnabled = true;
                } else {
                    this.notificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.dialogId);
                }
            } else if (value == 1) {
                this.notificationsEnabled = true;
            } else if (value == 2) {
                this.notificationsEnabled = false;
            } else {
                this.notificationsEnabled = false;
            }
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (!this.needReset) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + this.dialogId, true).apply();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue, this.resourcesProvider), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon, this.resourcesProvider), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ProfileNotificationsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0).apply();
                    }
                } else if (id == 1) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + ProfileNotificationsActivity.this.dialogId, true);
                    TLRPC.Dialog dialog = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialogId);
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialogId, 0L);
                        if (dialog != null) {
                            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                        }
                    } else {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialogId);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialogId, 1L);
                        if (dialog != null) {
                            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                            dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    editor.apply();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialogId);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationsSettingsActivity.NotificationException exception = new NotificationsSettingsActivity.NotificationException();
                        exception.did = ProfileNotificationsActivity.this.dialogId;
                        exception.hasCustom = true;
                        exception.notify = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialogId, 0);
                        if (exception.notify != 0) {
                            exception.muteUntil = preferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialogId, 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(exception);
                    }
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, null, false, this.resourcesProvider);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        this.actionBar.setAllowOverlayTitle(false);
        if (this.dialogId < 0) {
            TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(-this.dialogId));
            this.avatarContainer.setChatAvatar(chatLocal);
            this.avatarContainer.setTitle(chatLocal.title);
        } else {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
            if (user != null) {
                this.avatarContainer.setUserAvatar(user);
                this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
            }
        }
        if (this.addingException) {
            this.avatarContainer.setSubtitle(LocaleController.getString("NotificationsNewException", R.string.NotificationsNewException));
            this.actionBar.createMenu().addItem(1, LocaleController.getString("Done", R.string.Done).toUpperCase());
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("CustomNotifications", R.string.CustomNotifications));
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context) { // from class: org.telegram.ui.ProfileNotificationsActivity.2
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ProfileNotificationsActivity.this.m4504lambda$createView$6$orgtelegramuiProfileNotificationsActivity(context, view, i);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4504lambda$createView$6$orgtelegramuiProfileNotificationsActivity(Context context, View view, int position) {
        if (!view.isEnabled()) {
            return;
        }
        if (position == this.customResetRow) {
            AlertDialog dialog = new AlertDialog.Builder(context, this.resourcesProvider).setTitle(LocaleController.getString((int) R.string.ResetCustomNotificationsAlertTitle)).setMessage(LocaleController.getString((int) R.string.ResetCustomNotificationsAlert)).setPositiveButton(LocaleController.getString((int) R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileNotificationsActivity.this.m4498lambda$createView$0$orgtelegramuiProfileNotificationsActivity(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        } else if (position == this.soundRow) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", this.dialogId);
            presentFragment(new NotificationsSoundActivity(bundle, this.resourcesProvider));
        } else if (position == this.ringtoneRow) {
            try {
                Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                Uri currentSound = null;
                String defaultPath = null;
                Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                if (defaultUri != null) {
                    defaultPath = defaultUri.getPath();
                }
                String path = preferences.getString("ringtone_path_" + this.dialogId, defaultPath);
                if (path != null && !path.equals("NoSound")) {
                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                }
                tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                startActivityForResult(tmpIntent, 13);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (position == this.vibrateRow) {
            showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), this.dialogId, false, false, new Runnable() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileNotificationsActivity.this.m4499lambda$createView$1$orgtelegramuiProfileNotificationsActivity();
                }
            }, this.resourcesProvider));
        } else if (position == this.enableRow) {
            TextCheckCell checkCell = (TextCheckCell) view;
            boolean isChecked = true ^ checkCell.isChecked();
            this.notificationsEnabled = isChecked;
            checkCell.setChecked(isChecked);
            checkRowsEnabled();
        } else if (position == this.previewRow) {
            TextCheckCell checkCell2 = (TextCheckCell) view;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("content_preview_" + this.dialogId, !checkCell2.isChecked()).apply();
            checkCell2.setChecked(true ^ checkCell2.isChecked());
        } else if (position == this.callsVibrateRow) {
            Activity parentActivity = getParentActivity();
            long j = this.dialogId;
            showDialog(AlertsCreator.createVibrationSelectDialog(parentActivity, j, "calls_vibrate_" + this.dialogId, new Runnable() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileNotificationsActivity.this.m4500lambda$createView$2$orgtelegramuiProfileNotificationsActivity();
                }
            }, this.resourcesProvider));
        } else if (position == this.priorityRow) {
            showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), this.dialogId, -1, new Runnable() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileNotificationsActivity.this.m4501lambda$createView$3$orgtelegramuiProfileNotificationsActivity();
                }
            }, this.resourcesProvider));
        } else if (position == this.smartRow) {
            if (getParentActivity() == null) {
                return;
            }
            SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
            int notifyMaxCount = preferences2.getInt("smart_max_count_" + this.dialogId, 2);
            int notifyDelay = preferences2.getInt("smart_delay_" + this.dialogId, 180);
            if (notifyMaxCount == 0) {
                notifyMaxCount = 2;
            }
            AlertsCreator.createSoundFrequencyPickerDialog(getParentActivity(), notifyMaxCount, notifyDelay, new AlertsCreator.SoundFrequencyDelegate() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda6
                @Override // org.telegram.ui.Components.AlertsCreator.SoundFrequencyDelegate
                public final void didSelectValues(int i, int i2) {
                    ProfileNotificationsActivity.this.m4502lambda$createView$4$orgtelegramuiProfileNotificationsActivity(i, i2);
                }
            }, this.resourcesProvider);
        } else if (position == this.colorRow) {
            if (getParentActivity() == null) {
                return;
            }
            showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), this.dialogId, -1, new Runnable() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileNotificationsActivity.this.m4503lambda$createView$5$orgtelegramuiProfileNotificationsActivity();
                }
            }, this.resourcesProvider));
        } else if (position == this.popupEnabledRow) {
            SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit2.putInt("popup_" + this.dialogId, 1).apply();
            ((RadioCell) view).setChecked(true, true);
            View view2 = this.listView.findViewWithTag(2);
            if (view2 != null) {
                ((RadioCell) view2).setChecked(false, true);
            }
        } else if (position == this.popupDisabledRow) {
            SharedPreferences.Editor edit3 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit3.putInt("popup_" + this.dialogId, 2).apply();
            ((RadioCell) view).setChecked(true, true);
            View view3 = this.listView.findViewWithTag(1);
            if (view3 != null) {
                ((RadioCell) view3).setChecked(false, true);
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4498lambda$createView$0$orgtelegramuiProfileNotificationsActivity(DialogInterface d, int w) {
        this.needReset = true;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        SharedPreferences.Editor putBoolean = edit.putBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + this.dialogId, false);
        putBoolean.remove("notify2_" + this.dialogId).apply();
        finishFragment();
        ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate = this.delegate;
        if (profileNotificationsActivityDelegate != null) {
            profileNotificationsActivityDelegate.didRemoveException(this.dialogId);
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4499lambda$createView$1$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.vibrateRow);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4500lambda$createView$2$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.callsVibrateRow);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4501lambda$createView$3$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.priorityRow);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4502lambda$createView$4$orgtelegramuiProfileNotificationsActivity(int time, int minute) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        SharedPreferences.Editor putInt = edit.putInt("smart_max_count_" + this.dialogId, time);
        putInt.putInt("smart_delay_" + this.dialogId, minute).apply();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.smartRow);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4503lambda$createView$5$orgtelegramuiProfileNotificationsActivity() {
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.colorRow);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode != -1 || data == null) {
            return;
        }
        Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
        String name = null;
        if (ringtone != null && (rng = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, ringtone)) != null) {
            if (requestCode == 13) {
                if (ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                    name = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
            } else if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
            } else {
                name = rng.getTitle(getParentActivity());
            }
            rng.stop();
        }
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        if (requestCode == 12) {
            if (name != null) {
                editor.putString("sound_" + this.dialogId, name);
                editor.putString("sound_path_" + this.dialogId, ringtone.toString());
            } else {
                editor.putString("sound_" + this.dialogId, "NoSound");
                editor.putString("sound_path_" + this.dialogId, "NoSound");
            }
            getNotificationsController().deleteNotificationChannel(this.dialogId);
        } else if (requestCode == 13) {
            if (name != null) {
                editor.putString("ringtone_" + this.dialogId, name);
                editor.putString("ringtone_path_" + this.dialogId, ringtone.toString());
            } else {
                editor.putString("ringtone_" + this.dialogId, "NoSound");
                editor.putString("ringtone_path_" + this.dialogId, "NoSound");
            }
        }
        editor.apply();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(requestCode == 13 ? this.ringtoneRow : this.soundRow);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            try {
                this.adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }
    }

    public void setDelegate(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate) {
        this.delegate = profileNotificationsActivityDelegate;
    }

    private void checkRowsEnabled() {
        int count = this.listView.getChildCount();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(child);
            int type = holder.getItemViewType();
            int position = holder.getAdapterPosition();
            if (position != this.enableRow && position != this.customResetRow) {
                switch (type) {
                    case 0:
                        HeaderCell textCell = (HeaderCell) holder.itemView;
                        textCell.setEnabled(this.notificationsEnabled, animators);
                        continue;
                    case 1:
                        TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                        textCell2.setEnabled(this.notificationsEnabled, animators);
                        continue;
                    case 2:
                        TextInfoPrivacyCell textCell3 = (TextInfoPrivacyCell) holder.itemView;
                        textCell3.setEnabled(this.notificationsEnabled, animators);
                        continue;
                    case 3:
                        TextColorCell textCell4 = (TextColorCell) holder.itemView;
                        textCell4.setEnabled(this.notificationsEnabled, animators);
                        continue;
                    case 4:
                        RadioCell radioCell = (RadioCell) holder.itemView;
                        radioCell.setEnabled(this.notificationsEnabled, animators);
                        continue;
                    case 7:
                        if (position == this.previewRow) {
                            TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                            checkCell.setEnabled(this.notificationsEnabled, animators);
                            break;
                        } else {
                            continue;
                        }
                }
            }
        }
        if (!animators.isEmpty()) {
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.playTogether(animators);
            this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileNotificationsActivity.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                        ProfileNotificationsActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setDuration(150L);
            this.animatorSet.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_INFO = 2;
        private static final int VIEW_TYPE_RADIO = 4;
        private static final int VIEW_TYPE_SHADOW = 6;
        private static final int VIEW_TYPE_TEXT_CHECK = 7;
        private static final int VIEW_TYPE_TEXT_COLOR = 3;
        private static final int VIEW_TYPE_TEXT_SETTINGS = 1;
        private static final int VIEW_TYPE_USER = 5;
        private Context context;

        public ListAdapter(Context ctx) {
            ProfileNotificationsActivity.this = r1;
            this.context = ctx;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                return ProfileNotificationsActivity.this.notificationsEnabled;
            }
            if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.customResetRow) {
                return true;
            }
            switch (holder.getItemViewType()) {
                case 0:
                case 2:
                case 5:
                case 6:
                    return false;
                case 1:
                case 3:
                case 4:
                    return ProfileNotificationsActivity.this.notificationsEnabled;
                case 7:
                    return true;
                default:
                    return true;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new HeaderCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view2.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    View view3 = new TextSettingsCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view3.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    break;
                case 3:
                    View view4 = new TextColorCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view4.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    View view5 = new RadioCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view5.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view = view5;
                    break;
                case 5:
                    View view6 = new UserCell2(this.context, 4, 0, ProfileNotificationsActivity.this.resourcesProvider);
                    view6.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view = view6;
                    break;
                case 6:
                    view = new ShadowSectionCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    break;
                default:
                    View view7 = new TextCheckCell(this.context, ProfileNotificationsActivity.this.resourcesProvider);
                    view7.setBackgroundColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    view = view7;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int color;
            boolean z = true;
            boolean z2 = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != ProfileNotificationsActivity.this.generalRow) {
                        if (position != ProfileNotificationsActivity.this.popupRow) {
                            if (position != ProfileNotificationsActivity.this.ledRow) {
                                if (position == ProfileNotificationsActivity.this.callsRow) {
                                    headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                                    return;
                                }
                                return;
                            }
                            headerCell.setText(LocaleController.getString("NotificationsLed", R.string.NotificationsLed));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", R.string.ProfilePopupNotification));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("General", R.string.General));
                    return;
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (position == ProfileNotificationsActivity.this.customResetRow) {
                        textCell.setText(LocaleController.getString((int) R.string.ResetCustomNotifications), false);
                        textCell.setTextColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_dialogTextRed));
                        return;
                    }
                    textCell.setTextColor(ProfileNotificationsActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position != ProfileNotificationsActivity.this.soundRow) {
                        if (position != ProfileNotificationsActivity.this.ringtoneRow) {
                            if (position != ProfileNotificationsActivity.this.vibrateRow) {
                                if (position != ProfileNotificationsActivity.this.priorityRow) {
                                    if (position != ProfileNotificationsActivity.this.smartRow) {
                                        if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                                            int value = preferences.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                                            if (value == 0 || value == 4) {
                                                textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                                                return;
                                            } else if (value == 1) {
                                                textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                                                return;
                                            } else if (value == 2) {
                                                textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                                                return;
                                            } else if (value == 3) {
                                                textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                                                return;
                                            } else {
                                                return;
                                            }
                                        }
                                        return;
                                    }
                                    int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialogId, 2);
                                    int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialogId, 180);
                                    if (notifyMaxCount == 0) {
                                        String string = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                                        String string2 = LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled);
                                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                            z = false;
                                        }
                                        textCell.setTextAndValue(string, string2, z);
                                        return;
                                    }
                                    String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60, new Object[0]);
                                    String string3 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                                    String formatString = LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, Integer.valueOf(notifyMaxCount), minutes);
                                    if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                        z = false;
                                    }
                                    textCell.setTextAndValue(string3, formatString, z);
                                    return;
                                }
                                int value2 = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialogId, 3);
                                if (value2 == 0) {
                                    textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                                    return;
                                } else if (value2 == 1 || value2 == 2) {
                                    textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), false);
                                    return;
                                } else if (value2 == 3) {
                                    textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), false);
                                    return;
                                } else if (value2 == 4) {
                                    textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), false);
                                    return;
                                } else if (value2 == 5) {
                                    textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), false);
                                    return;
                                } else {
                                    return;
                                }
                            }
                            int value3 = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialogId, 0);
                            if (value3 == 0 || value3 == 4) {
                                String string4 = LocaleController.getString("Vibrate", R.string.Vibrate);
                                String string5 = LocaleController.getString("VibrationDefault", R.string.VibrationDefault);
                                if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                    z = false;
                                }
                                textCell.setTextAndValue(string4, string5, z);
                                return;
                            } else if (value3 == 1) {
                                String string6 = LocaleController.getString("Vibrate", R.string.Vibrate);
                                String string7 = LocaleController.getString("Short", R.string.Short);
                                if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                    z = false;
                                }
                                textCell.setTextAndValue(string6, string7, z);
                                return;
                            } else if (value3 == 2) {
                                String string8 = LocaleController.getString("Vibrate", R.string.Vibrate);
                                String string9 = LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled);
                                if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                    z = false;
                                }
                                textCell.setTextAndValue(string8, string9, z);
                                return;
                            } else if (value3 == 3) {
                                String string10 = LocaleController.getString("Vibrate", R.string.Vibrate);
                                String string11 = LocaleController.getString("Long", R.string.Long);
                                if (ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1) {
                                    z = false;
                                }
                                textCell.setTextAndValue(string10, string11, z);
                                return;
                            } else {
                                return;
                            }
                        }
                        String value4 = preferences.getString("ringtone_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                        if (value4.equals("NoSound")) {
                            value4 = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value4, false);
                        return;
                    }
                    String value5 = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialogId, LocaleController.getString("SoundDefault", R.string.SoundDefault));
                    long documentId = preferences.getLong("sound_document_id_" + ProfileNotificationsActivity.this.dialogId, 0L);
                    if (documentId != 0) {
                        TLRPC.Document document = ProfileNotificationsActivity.this.getMediaDataController().ringtoneDataStore.getDocument(documentId);
                        if (document == null) {
                            value5 = LocaleController.getString("CustomSound", R.string.CustomSound);
                        } else {
                            value5 = NotificationsSoundActivity.trimTitle(document, document.file_name_fixed);
                        }
                    } else if (value5.equals("NoSound")) {
                        value5 = LocaleController.getString("NoSound", R.string.NoSound);
                    } else if (value5.equals("Default")) {
                        value5 = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                    }
                    textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value5, true);
                    return;
                case 2:
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (position != ProfileNotificationsActivity.this.popupInfoRow) {
                        if (position != ProfileNotificationsActivity.this.ledInfoRow) {
                            if (position == ProfileNotificationsActivity.this.priorityInfoRow) {
                                if (ProfileNotificationsActivity.this.priorityRow == -1) {
                                    textCell2.setText("");
                                } else {
                                    textCell2.setText(LocaleController.getString("PriorityInfo", R.string.PriorityInfo));
                                }
                                textCell2.setBackground(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                return;
                            } else if (position == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                                textCell2.setText(LocaleController.getString("VoipRingtoneInfo", R.string.VoipRingtoneInfo));
                                textCell2.setBackground(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                return;
                            } else {
                                return;
                            }
                        }
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", R.string.NotificationsLedInfo));
                        textCell2.setBackground(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", R.string.ProfilePopupNotificationInfo));
                    textCell2.setBackground(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 3:
                    TextColorCell textCell3 = (TextColorCell) holder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (!preferences2.contains("color_" + ProfileNotificationsActivity.this.dialogId)) {
                        if (DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId)) {
                            color = preferences2.getInt("GroupLed", -16776961);
                        } else {
                            color = preferences2.getInt("MessagesLed", -16776961);
                        }
                    } else {
                        color = preferences2.getInt("color_" + ProfileNotificationsActivity.this.dialogId, -16776961);
                    }
                    int a = 0;
                    while (true) {
                        if (a < 9) {
                            if (TextColorCell.colorsToSave[a] != color) {
                                a++;
                            } else {
                                color = TextColorCell.colors[a];
                            }
                        }
                    }
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), color, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    int popup = preferences3.getInt("popup_" + ProfileNotificationsActivity.this.dialogId, 0);
                    if (popup == 0) {
                        if (preferences3.getInt(DialogObject.isChatDialog(ProfileNotificationsActivity.this.dialogId) ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (position != ProfileNotificationsActivity.this.popupEnabledRow) {
                        if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                            String string12 = LocaleController.getString("PopupDisabled", R.string.PopupDisabled);
                            if (popup != 2) {
                                z = false;
                            }
                            radioCell.setText(string12, z, false);
                            radioCell.setTag(2);
                            return;
                        }
                        return;
                    }
                    String string13 = LocaleController.getString("PopupEnabled", R.string.PopupEnabled);
                    if (popup == 1) {
                        z2 = true;
                    }
                    radioCell.setText(string13, z2, true);
                    radioCell.setTag(1);
                    return;
                case 5:
                    UserCell2 userCell2 = (UserCell2) holder.itemView;
                    TLObject object = DialogObject.isUserDialog(ProfileNotificationsActivity.this.dialogId) ? MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Long.valueOf(ProfileNotificationsActivity.this.dialogId)) : MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Long.valueOf(-ProfileNotificationsActivity.this.dialogId));
                    userCell2.setData(object, null, null, 0);
                    return;
                case 6:
                default:
                    return;
                case 7:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    SharedPreferences preferences4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (position == ProfileNotificationsActivity.this.enableRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Notifications", R.string.Notifications), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (position == ProfileNotificationsActivity.this.previewRow) {
                        String string14 = LocaleController.getString("MessagePreview", R.string.MessagePreview);
                        checkCell.setTextAndCheck(string14, preferences4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialogId, true), true);
                        return;
                    } else {
                        return;
                    }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 0:
                    ((HeaderCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, null);
                    return;
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (holder.getAdapterPosition() != ProfileNotificationsActivity.this.customResetRow) {
                        textCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, null);
                        return;
                    } else {
                        textCell.setEnabled(true, null);
                        return;
                    }
                case 2:
                    ((TextInfoPrivacyCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, null);
                    return;
                case 3:
                    ((TextColorCell) holder.itemView).setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, null);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    radioCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, null);
                    return;
                case 5:
                case 6:
                default:
                    return;
                case 7:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                        checkCell.setEnabled(ProfileNotificationsActivity.this.notificationsEnabled, null);
                        return;
                    } else {
                        checkCell.setEnabled(true, null);
                        return;
                    }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == ProfileNotificationsActivity.this.generalRow || position == ProfileNotificationsActivity.this.popupRow || position == ProfileNotificationsActivity.this.ledRow || position == ProfileNotificationsActivity.this.callsRow) {
                return 0;
            }
            if (position != ProfileNotificationsActivity.this.soundRow && position != ProfileNotificationsActivity.this.vibrateRow && position != ProfileNotificationsActivity.this.priorityRow && position != ProfileNotificationsActivity.this.smartRow && position != ProfileNotificationsActivity.this.ringtoneRow && position != ProfileNotificationsActivity.this.callsVibrateRow && position != ProfileNotificationsActivity.this.customResetRow) {
                if (position != ProfileNotificationsActivity.this.popupInfoRow && position != ProfileNotificationsActivity.this.ledInfoRow && position != ProfileNotificationsActivity.this.priorityInfoRow && position != ProfileNotificationsActivity.this.ringtoneInfoRow) {
                    if (position != ProfileNotificationsActivity.this.colorRow) {
                        if (position != ProfileNotificationsActivity.this.popupEnabledRow && position != ProfileNotificationsActivity.this.popupDisabledRow) {
                            if (position != ProfileNotificationsActivity.this.avatarRow) {
                                if (position == ProfileNotificationsActivity.this.avatarSectionRow || position == ProfileNotificationsActivity.this.customResetShadowRow) {
                                    return 6;
                                }
                                return (position == ProfileNotificationsActivity.this.enableRow || position == ProfileNotificationsActivity.this.previewRow) ? 7 : 0;
                            }
                            return 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return getThemedColor(Theme.key_windowBackgroundGray);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ProfileNotificationsActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ProfileNotificationsActivity.this.m4505x9710d3ce();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, UserCell2.class, TextCheckCell.class, TextCheckBoxCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$7$org-telegram-ui-ProfileNotificationsActivity */
    public /* synthetic */ void m4505x9710d3ce() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell2) {
                    ((UserCell2) child).update(0);
                }
            }
        }
    }
}
