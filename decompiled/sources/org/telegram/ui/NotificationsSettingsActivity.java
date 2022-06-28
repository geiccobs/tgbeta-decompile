package org.telegram.ui;

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
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int accountsAllRow;
    private int accountsInfoRow;
    private int accountsSectionRow;
    private ListAdapter adapter;
    private int androidAutoAlertRow;
    private int badgeNumberMessagesRow;
    private int badgeNumberMutedRow;
    private int badgeNumberSection;
    private int badgeNumberSection2Row;
    private int badgeNumberShowRow;
    private int callsRingtoneRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int callsVibrateRow;
    private int channelsRow;
    private int contactJoinedRow;
    private int eventsSection2Row;
    private int eventsSectionRow;
    private int groupRow;
    private int inappPreviewRow;
    private int inappPriorityRow;
    private int inappSectionRow;
    private int inappSoundRow;
    private int inappVibrateRow;
    private int inchatSoundRow;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private int notificationsSection2Row;
    private int notificationsSectionRow;
    private int notificationsServiceConnectionRow;
    private int notificationsServiceRow;
    private int otherSection2Row;
    private int otherSectionRow;
    private int pinnedMessageRow;
    private int privateRow;
    private int repeatRow;
    private int resetNotificationsRow;
    private int resetNotificationsSectionRow;
    private int resetSection2Row;
    private int resetSectionRow;
    private boolean reseting = false;
    private ArrayList<NotificationException> exceptionUsers = null;
    private ArrayList<NotificationException> exceptionChats = null;
    private ArrayList<NotificationException> exceptionChannels = null;
    private int rowCount = 0;

    /* loaded from: classes4.dex */
    public static class NotificationException {
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        if (UserConfig.getActivatedAccountsCount() > 1) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.accountsSectionRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.accountsAllRow = i2;
            this.rowCount = i3 + 1;
            this.accountsInfoRow = i3;
        } else {
            this.accountsSectionRow = -1;
            this.accountsAllRow = -1;
            this.accountsInfoRow = -1;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.notificationsSectionRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.privateRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.groupRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.channelsRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.notificationsSection2Row = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.callsSectionRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.callsVibrateRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.callsRingtoneRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.eventsSection2Row = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.badgeNumberSection = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.badgeNumberShowRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.badgeNumberMutedRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.badgeNumberMessagesRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.badgeNumberSection2Row = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.inappSectionRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.inappSoundRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.inappVibrateRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.inappPreviewRow = i21;
        this.rowCount = i22 + 1;
        this.inchatSoundRow = i22;
        if (Build.VERSION.SDK_INT >= 21) {
            int i23 = this.rowCount;
            this.rowCount = i23 + 1;
            this.inappPriorityRow = i23;
        } else {
            this.inappPriorityRow = -1;
        }
        int i24 = this.rowCount;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.callsSection2Row = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.eventsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.contactJoinedRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.pinnedMessageRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.otherSection2Row = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.otherSectionRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.notificationsServiceRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.notificationsServiceConnectionRow = i31;
        this.androidAutoAlertRow = -1;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.repeatRow = i32;
        int i34 = i33 + 1;
        this.rowCount = i34;
        this.resetSection2Row = i33;
        int i35 = i34 + 1;
        this.rowCount = i35;
        this.resetSectionRow = i34;
        int i36 = i35 + 1;
        this.rowCount = i36;
        this.resetNotificationsRow = i35;
        this.rowCount = i36 + 1;
        this.resetNotificationsSectionRow = i36;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsSettingsActivity.this.m3959x4800c794();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:115:0x0302  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x031c A[LOOP:3: B:120:0x031a->B:121:0x031c, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0336  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x02b6  */
    /* renamed from: lambda$loadExceptions$1$org-telegram-ui-NotificationsSettingsActivity */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m3959x4800c794() {
        /*
            Method dump skipped, instructions count: 894
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.m3959x4800c794():void");
    }

    /* renamed from: lambda$loadExceptions$0$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3958xd286a153(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        this.exceptionUsers = usersResult;
        this.exceptionChats = chatsResult;
        this.exceptionChannels = channelsResult;
        this.adapter.notifyItemChanged(this.privateRow);
        this.adapter.notifyItemChanged(this.groupRow);
        this.adapter.notifyItemChanged(this.channelsRow);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.NotificationsSettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsSettingsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.NotificationsSettingsActivity.2
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsSettingsActivity.this.m3957xbde8ae12(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3957xbde8ae12(View view, final int position, float x, float y) {
        ArrayList<NotificationException> exceptions;
        int type;
        boolean enabled = false;
        if (getParentActivity() == null) {
            return;
        }
        int i = this.privateRow;
        if (position == i || position == this.groupRow || position == this.channelsRow) {
            if (position == i) {
                type = 1;
                exceptions = this.exceptionUsers;
            } else if (position == this.groupRow) {
                type = 0;
                exceptions = this.exceptionChats;
            } else {
                type = 2;
                exceptions = this.exceptionChannels;
            }
            if (exceptions == null) {
                return;
            }
            NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
            enabled = getNotificationsController().isGlobalNotificationsEnabled(type);
            if ((!LocaleController.isRTL || x > AndroidUtilities.dp(76.0f)) && (LocaleController.isRTL || x < view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                presentFragment(new NotificationsCustomSettingsActivity(type, exceptions));
            } else {
                getNotificationsController().setGlobalNotificationsEnabled(type, !enabled ? 0 : Integer.MAX_VALUE);
                showExceptionsAlert(position);
                checkCell.setChecked(!enabled, 0);
                this.adapter.notifyItemChanged(position);
            }
        } else if (position == this.callsRingtoneRow) {
            try {
                SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                Uri currentSound = null;
                String defaultPath = null;
                Uri defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                if (defaultUri != null) {
                    defaultPath = defaultUri.getPath();
                }
                String path = preferences.getString("CallsRingtonePath", defaultPath);
                if (path != null && !path.equals("NoSound")) {
                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                }
                tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                startActivityForResult(tmpIntent, position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (position != this.resetNotificationsRow) {
            if (position != this.inappSoundRow) {
                if (position != this.inappVibrateRow) {
                    if (position != this.inappPreviewRow) {
                        if (position != this.inchatSoundRow) {
                            if (position != this.inappPriorityRow) {
                                if (position != this.contactJoinedRow) {
                                    if (position != this.pinnedMessageRow) {
                                        if (position != this.androidAutoAlertRow) {
                                            if (position != this.badgeNumberShowRow) {
                                                if (position != this.badgeNumberMutedRow) {
                                                    if (position != this.badgeNumberMessagesRow) {
                                                        if (position != this.notificationsServiceConnectionRow) {
                                                            if (position != this.accountsAllRow) {
                                                                if (position != this.notificationsServiceRow) {
                                                                    if (position == this.callsVibrateRow) {
                                                                        if (getParentActivity() == null) {
                                                                            return;
                                                                        }
                                                                        String key = null;
                                                                        if (position == this.callsVibrateRow) {
                                                                            key = "vibrate_calls";
                                                                        }
                                                                        showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0L, key, new Runnable() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda5
                                                                            @Override // java.lang.Runnable
                                                                            public final void run() {
                                                                                NotificationsSettingsActivity.this.m3955xd2f46190(position);
                                                                            }
                                                                        }));
                                                                    } else if (position == this.repeatRow) {
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                                                        builder.setTitle(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications));
                                                                        builder.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5, new Object[0]), LocaleController.formatPluralString("Minutes", 10, new Object[0]), LocaleController.formatPluralString("Minutes", 30, new Object[0]), LocaleController.formatPluralString("Hours", 1, new Object[0]), LocaleController.formatPluralString("Hours", 2, new Object[0]), LocaleController.formatPluralString("Hours", 4, new Object[0])}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda1
                                                                            @Override // android.content.DialogInterface.OnClickListener
                                                                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                                                                NotificationsSettingsActivity.this.m3956x486e87d1(position, dialogInterface, i2);
                                                                            }
                                                                        });
                                                                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                                                        showDialog(builder.create());
                                                                    }
                                                                } else {
                                                                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
                                                                    enabled = preferences2.getBoolean("pushService", getMessagesController().keepAliveService);
                                                                    SharedPreferences.Editor editor = preferences2.edit();
                                                                    editor.putBoolean("pushService", !enabled);
                                                                    editor.commit();
                                                                    ApplicationLoader.startPushService();
                                                                }
                                                            } else {
                                                                SharedPreferences preferences3 = MessagesController.getGlobalNotificationsSettings();
                                                                enabled = preferences3.getBoolean("AllAccounts", true);
                                                                SharedPreferences.Editor editor2 = preferences3.edit();
                                                                editor2.putBoolean("AllAccounts", !enabled);
                                                                editor2.commit();
                                                                SharedConfig.showNotificationsForAllAccounts = !enabled;
                                                                for (int a = 0; a < 4; a++) {
                                                                    if (SharedConfig.showNotificationsForAllAccounts) {
                                                                        NotificationsController.getInstance(a).showNotifications();
                                                                    } else if (a == this.currentAccount) {
                                                                        NotificationsController.getInstance(a).showNotifications();
                                                                    } else {
                                                                        NotificationsController.getInstance(a).hideNotifications();
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            SharedPreferences preferences4 = MessagesController.getNotificationsSettings(this.currentAccount);
                                                            enabled = preferences4.getBoolean("pushConnection", getMessagesController().backgroundConnection);
                                                            SharedPreferences.Editor editor3 = preferences4.edit();
                                                            editor3.putBoolean("pushConnection", !enabled);
                                                            editor3.commit();
                                                            if (!enabled) {
                                                                ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(true);
                                                            } else {
                                                                ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(false);
                                                            }
                                                        }
                                                    } else {
                                                        SharedPreferences preferences5 = MessagesController.getNotificationsSettings(this.currentAccount);
                                                        SharedPreferences.Editor editor4 = preferences5.edit();
                                                        enabled = getNotificationsController().showBadgeMessages;
                                                        getNotificationsController().showBadgeMessages = !enabled;
                                                        editor4.putBoolean("badgeNumberMessages", getNotificationsController().showBadgeMessages);
                                                        editor4.commit();
                                                        getNotificationsController().updateBadge();
                                                    }
                                                } else {
                                                    SharedPreferences preferences6 = MessagesController.getNotificationsSettings(this.currentAccount);
                                                    SharedPreferences.Editor editor5 = preferences6.edit();
                                                    enabled = getNotificationsController().showBadgeMuted;
                                                    getNotificationsController().showBadgeMuted = !enabled;
                                                    editor5.putBoolean("badgeNumberMuted", getNotificationsController().showBadgeMuted);
                                                    editor5.commit();
                                                    getNotificationsController().updateBadge();
                                                    getMessagesStorage().updateMutedDialogsFiltersCounters();
                                                }
                                            } else {
                                                SharedPreferences preferences7 = MessagesController.getNotificationsSettings(this.currentAccount);
                                                SharedPreferences.Editor editor6 = preferences7.edit();
                                                enabled = getNotificationsController().showBadgeNumber;
                                                getNotificationsController().showBadgeNumber = !enabled;
                                                editor6.putBoolean("badgeNumber", getNotificationsController().showBadgeNumber);
                                                editor6.commit();
                                                getNotificationsController().updateBadge();
                                            }
                                        } else {
                                            SharedPreferences preferences8 = MessagesController.getNotificationsSettings(this.currentAccount);
                                            SharedPreferences.Editor editor7 = preferences8.edit();
                                            enabled = preferences8.getBoolean("EnableAutoNotifications", false);
                                            editor7.putBoolean("EnableAutoNotifications", !enabled);
                                            editor7.commit();
                                        }
                                    } else {
                                        SharedPreferences preferences9 = MessagesController.getNotificationsSettings(this.currentAccount);
                                        SharedPreferences.Editor editor8 = preferences9.edit();
                                        enabled = preferences9.getBoolean("PinnedMessages", true);
                                        editor8.putBoolean("PinnedMessages", !enabled);
                                        editor8.commit();
                                    }
                                } else {
                                    SharedPreferences preferences10 = MessagesController.getNotificationsSettings(this.currentAccount);
                                    SharedPreferences.Editor editor9 = preferences10.edit();
                                    enabled = preferences10.getBoolean("EnableContactJoined", true);
                                    MessagesController.getInstance(this.currentAccount).enableJoined = !enabled;
                                    editor9.putBoolean("EnableContactJoined", !enabled);
                                    editor9.commit();
                                    TLRPC.TL_account_setContactSignUpNotification req = new TLRPC.TL_account_setContactSignUpNotification();
                                    req.silent = enabled;
                                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, NotificationsSettingsActivity$$ExternalSyntheticLambda8.INSTANCE);
                                }
                            } else {
                                SharedPreferences preferences11 = MessagesController.getNotificationsSettings(this.currentAccount);
                                SharedPreferences.Editor editor10 = preferences11.edit();
                                enabled = preferences11.getBoolean("EnableInAppPriority", false);
                                editor10.putBoolean("EnableInAppPriority", !enabled);
                                editor10.commit();
                            }
                        } else {
                            SharedPreferences preferences12 = MessagesController.getNotificationsSettings(this.currentAccount);
                            SharedPreferences.Editor editor11 = preferences12.edit();
                            enabled = preferences12.getBoolean("EnableInChatSound", true);
                            editor11.putBoolean("EnableInChatSound", !enabled);
                            editor11.commit();
                            getNotificationsController().setInChatSoundEnabled(!enabled);
                        }
                    } else {
                        SharedPreferences preferences13 = MessagesController.getNotificationsSettings(this.currentAccount);
                        SharedPreferences.Editor editor12 = preferences13.edit();
                        enabled = preferences13.getBoolean("EnableInAppPreview", true);
                        editor12.putBoolean("EnableInAppPreview", !enabled);
                        editor12.commit();
                    }
                } else {
                    SharedPreferences preferences14 = MessagesController.getNotificationsSettings(this.currentAccount);
                    SharedPreferences.Editor editor13 = preferences14.edit();
                    enabled = preferences14.getBoolean("EnableInAppVibrate", true);
                    editor13.putBoolean("EnableInAppVibrate", !enabled);
                    editor13.commit();
                }
            } else {
                SharedPreferences preferences15 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor14 = preferences15.edit();
                enabled = preferences15.getBoolean("EnableInAppSounds", true);
                editor14.putBoolean("EnableInAppSounds", !enabled);
                editor14.commit();
            }
        } else {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("ResetNotificationsAlertTitle", R.string.ResetNotificationsAlertTitle));
            builder2.setMessage(LocaleController.getString("ResetNotificationsAlert", R.string.ResetNotificationsAlert));
            builder2.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    NotificationsSettingsActivity.this.m3954xe800150e(dialogInterface, i2);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder2.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
        if (view instanceof TextCheckCell) {
            ((TextCheckCell) view).setChecked(!enabled);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3954xe800150e(DialogInterface dialogInterface, int i) {
        if (this.reseting) {
            return;
        }
        this.reseting = true;
        TLRPC.TL_account_resetNotifySettings req = new TLRPC.TL_account_resetNotifySettings();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NotificationsSettingsActivity.this.m3953x7285eecd(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3953x7285eecd(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsSettingsActivity.this.m3952xfd0bc88c();
            }
        });
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3952xfd0bc88c() {
        getMessagesController().enableJoined = true;
        this.reseting = false;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            Toast toast = Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", R.string.ResetNotificationsText), 0);
            toast.show();
        }
        getMessagesStorage().updateMutedDialogsFiltersCounters();
    }

    public static /* synthetic */ void lambda$createView$5(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3955xd2f46190(int position) {
        this.adapter.notifyItemChanged(position);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3956x486e87d1(int position, DialogInterface dialog, int which) {
        int minutes = 0;
        if (which == 1) {
            minutes = 5;
        } else if (which == 2) {
            minutes = 10;
        } else if (which == 3) {
            minutes = 30;
        } else if (which == 4) {
            minutes = 60;
        } else if (which == 5) {
            minutes = 120;
        } else if (which == 6) {
            minutes = PsExtractor.VIDEO_STREAM_MASK;
        }
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        preferences.edit().putInt("repeat_messages", minutes).commit();
        this.adapter.notifyItemChanged(position);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null && (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) != null) {
                if (requestCode == this.callsRingtoneRow) {
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
            if (requestCode == this.callsRingtoneRow) {
                if (name != null && ringtone != null) {
                    editor.putString("CallsRingtone", name);
                    editor.putString("CallsRingtonePath", ringtone.toString());
                } else {
                    editor.putString("CallsRingtone", "NoSound");
                    editor.putString("CallsRingtonePath", "NoSound");
                }
            }
            editor.commit();
            this.adapter.notifyItemChanged(requestCode);
        }
    }

    private void showExceptionsAlert(int position) {
        final ArrayList<NotificationException> exceptions;
        String alertText = null;
        if (position == this.privateRow) {
            exceptions = this.exceptionUsers;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("ChatsException", exceptions.size(), new Object[0]);
            }
        } else if (position == this.groupRow) {
            exceptions = this.exceptionChats;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("Groups", exceptions.size(), new Object[0]);
            }
        } else {
            exceptions = this.exceptionChannels;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("Channels", exceptions.size(), new Object[0]);
            }
        }
        if (alertText == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (exceptions.size() == 1) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsSingleAlert", R.string.NotificationsExceptionsSingleAlert, alertText)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsAlert", R.string.NotificationsExceptionsAlert, alertText)));
        }
        builder.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
        builder.setNeutralButton(LocaleController.getString("ViewExceptions", R.string.ViewExceptions), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NotificationsSettingsActivity.this.m3960xe3755a33(exceptions, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showExceptionsAlert$9$org-telegram-ui-NotificationsSettingsActivity */
    public /* synthetic */ void m3960xe3755a33(ArrayList exceptions, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, exceptions));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            NotificationsSettingsActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position == NotificationsSettingsActivity.this.notificationsSectionRow || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection2Row || position == NotificationsSettingsActivity.this.accountsSectionRow || position == NotificationsSettingsActivity.this.accountsInfoRow || position == NotificationsSettingsActivity.this.resetNotificationsSectionRow) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextDetailSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<NotificationException> exceptions;
            String text;
            int offUntil;
            int iconType;
            boolean enabled;
            String value;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != NotificationsSettingsActivity.this.notificationsSectionRow) {
                        if (position != NotificationsSettingsActivity.this.inappSectionRow) {
                            if (position != NotificationsSettingsActivity.this.eventsSectionRow) {
                                if (position != NotificationsSettingsActivity.this.otherSectionRow) {
                                    if (position != NotificationsSettingsActivity.this.resetSectionRow) {
                                        if (position != NotificationsSettingsActivity.this.callsSectionRow) {
                                            if (position != NotificationsSettingsActivity.this.badgeNumberSection) {
                                                if (position == NotificationsSettingsActivity.this.accountsSectionRow) {
                                                    headerCell.setText(LocaleController.getString("ShowNotificationsFor", R.string.ShowNotificationsFor));
                                                    return;
                                                }
                                                return;
                                            }
                                            headerCell.setText(LocaleController.getString("BadgeNumber", R.string.BadgeNumber));
                                            return;
                                        }
                                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                                        return;
                                    }
                                    headerCell.setText(LocaleController.getString(TimerBuilder.RESET, R.string.Reset));
                                    return;
                                }
                                headerCell.setText(LocaleController.getString("NotificationsOther", R.string.NotificationsOther));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("Events", R.string.Events));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("InAppNotifications", R.string.InAppNotifications));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("NotificationsForChats", R.string.NotificationsForChats));
                    return;
                case 1:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (position != NotificationsSettingsActivity.this.inappSoundRow) {
                        if (position != NotificationsSettingsActivity.this.inappVibrateRow) {
                            if (position != NotificationsSettingsActivity.this.inappPreviewRow) {
                                if (position != NotificationsSettingsActivity.this.inappPriorityRow) {
                                    if (position != NotificationsSettingsActivity.this.contactJoinedRow) {
                                        if (position != NotificationsSettingsActivity.this.pinnedMessageRow) {
                                            if (position != NotificationsSettingsActivity.this.androidAutoAlertRow) {
                                                if (position != NotificationsSettingsActivity.this.notificationsServiceRow) {
                                                    if (position != NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                                                        if (position == NotificationsSettingsActivity.this.badgeNumberShowRow) {
                                                            checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", R.string.BadgeNumberShow), NotificationsSettingsActivity.this.getNotificationsController().showBadgeNumber, true);
                                                            return;
                                                        } else if (position == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                                                            checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", R.string.BadgeNumberMutedChats), NotificationsSettingsActivity.this.getNotificationsController().showBadgeMuted, true);
                                                            return;
                                                        } else if (position == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                                                            checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", R.string.BadgeNumberUnread), NotificationsSettingsActivity.this.getNotificationsController().showBadgeMessages, false);
                                                            return;
                                                        } else if (position != NotificationsSettingsActivity.this.inchatSoundRow) {
                                                            if (position != NotificationsSettingsActivity.this.callsVibrateRow) {
                                                                if (position == NotificationsSettingsActivity.this.accountsAllRow) {
                                                                    checkCell.setTextAndCheck(LocaleController.getString("AllAccounts", R.string.AllAccounts), MessagesController.getGlobalNotificationsSettings().getBoolean("AllAccounts", true), false);
                                                                    return;
                                                                }
                                                                return;
                                                            }
                                                            checkCell.setTextAndCheck(LocaleController.getString("Vibrate", R.string.Vibrate), preferences.getBoolean("EnableCallVibrate", true), true);
                                                            return;
                                                        } else {
                                                            checkCell.setTextAndCheck(LocaleController.getString("InChatSound", R.string.InChatSound), preferences.getBoolean("EnableInChatSound", true), true);
                                                            return;
                                                        }
                                                    }
                                                    checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", R.string.NotificationsServiceConnectionInfo), preferences.getBoolean("pushConnection", NotificationsSettingsActivity.this.getMessagesController().backgroundConnection), true, true);
                                                    return;
                                                }
                                                checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", R.string.NotificationsServiceInfo), preferences.getBoolean("pushService", NotificationsSettingsActivity.this.getMessagesController().keepAliveService), true, true);
                                                return;
                                            }
                                            checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                                            return;
                                        }
                                        checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", R.string.PinnedMessages), preferences.getBoolean("PinnedMessages", true), false);
                                        return;
                                    }
                                    checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", R.string.ContactJoined), preferences.getBoolean("EnableContactJoined", true), true);
                                    return;
                                }
                                checkCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), preferences.getBoolean("EnableInAppPriority", false), false);
                                return;
                            }
                            checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", R.string.InAppPreview), preferences.getBoolean("EnableInAppPreview", true), true);
                            return;
                        }
                        checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", R.string.InAppVibrate), preferences.getBoolean("EnableInAppVibrate", true), true);
                        return;
                    }
                    checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", R.string.InAppSounds), preferences.getBoolean("EnableInAppSounds", true), true);
                    return;
                case 2:
                    TextDetailSettingsCell settingsCell = (TextDetailSettingsCell) holder.itemView;
                    settingsCell.setMultilineDetail(true);
                    if (position == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        settingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", R.string.UndoAllCustom), false);
                        return;
                    }
                    return;
                case 3:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) holder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    int currentTime = ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).getCurrentTime();
                    if (position != NotificationsSettingsActivity.this.privateRow) {
                        if (position == NotificationsSettingsActivity.this.groupRow) {
                            String text2 = LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups);
                            text = text2;
                            exceptions = NotificationsSettingsActivity.this.exceptionChats;
                            offUntil = preferences2.getInt("EnableGroup2", 0);
                        } else {
                            String text3 = LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels);
                            text = text3;
                            exceptions = NotificationsSettingsActivity.this.exceptionChannels;
                            offUntil = preferences2.getInt("EnableChannel2", 0);
                        }
                    } else {
                        String text4 = LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats);
                        text = text4;
                        exceptions = NotificationsSettingsActivity.this.exceptionUsers;
                        offUntil = preferences2.getInt("EnableAll2", 0);
                    }
                    boolean z2 = offUntil < currentTime;
                    boolean enabled2 = z2;
                    if (z2) {
                        iconType = 0;
                    } else {
                        int iconType2 = offUntil - 31536000;
                        if (iconType2 >= currentTime) {
                            iconType = 0;
                        } else {
                            iconType = 2;
                        }
                    }
                    StringBuilder builder = new StringBuilder();
                    if (exceptions != null && !exceptions.isEmpty()) {
                        boolean z3 = offUntil < currentTime;
                        boolean enabled3 = z3;
                        if (z3) {
                            builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
                        } else if (offUntil - 31536000 >= currentTime) {
                            builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
                        } else {
                            builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate(offUntil)));
                        }
                        if (builder.length() != 0) {
                            builder.append(", ");
                        }
                        builder.append(LocaleController.formatPluralString("Exception", exceptions.size(), new Object[0]));
                        enabled = enabled3;
                    } else {
                        builder.append(LocaleController.getString("TapToChange", R.string.TapToChange));
                        enabled = enabled2;
                    }
                    if (position != NotificationsSettingsActivity.this.channelsRow) {
                        z = true;
                    }
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled, iconType, z);
                    return;
                case 4:
                    if (position == NotificationsSettingsActivity.this.resetNotificationsSectionRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (position != NotificationsSettingsActivity.this.callsRingtoneRow) {
                        if (position != NotificationsSettingsActivity.this.callsVibrateRow) {
                            if (position == NotificationsSettingsActivity.this.repeatRow) {
                                int minutes = preferences3.getInt("repeat_messages", 60);
                                if (minutes == 0) {
                                    value = LocaleController.getString("RepeatNotificationsNever", R.string.RepeatNotificationsNever);
                                } else if (minutes < 60) {
                                    value = LocaleController.formatPluralString("Minutes", minutes, new Object[0]);
                                } else {
                                    value = LocaleController.formatPluralString("Hours", minutes / 60, new Object[0]);
                                }
                                textCell.setTextAndValue(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications), value, false);
                                return;
                            }
                            return;
                        }
                        int value2 = preferences3.getInt("vibrate_calls", 0);
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                            return;
                        } else if (value2 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                            return;
                        } else if (value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                            return;
                        } else if (value2 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                            return;
                        } else {
                            return;
                        }
                    }
                    String value3 = preferences3.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                    if (value3.equals("NoSound")) {
                        value3 = LocaleController.getString("NoSound", R.string.NoSound);
                    }
                    textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value3, false);
                    return;
                case 6:
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (position == NotificationsSettingsActivity.this.accountsInfoRow) {
                        textCell2.setText(LocaleController.getString("ShowNotificationsForInfo", R.string.ShowNotificationsForInfo));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != NotificationsSettingsActivity.this.eventsSectionRow && position != NotificationsSettingsActivity.this.otherSectionRow && position != NotificationsSettingsActivity.this.resetSectionRow && position != NotificationsSettingsActivity.this.callsSectionRow && position != NotificationsSettingsActivity.this.badgeNumberSection && position != NotificationsSettingsActivity.this.inappSectionRow && position != NotificationsSettingsActivity.this.notificationsSectionRow && position != NotificationsSettingsActivity.this.accountsSectionRow) {
                if (position != NotificationsSettingsActivity.this.inappSoundRow && position != NotificationsSettingsActivity.this.inappVibrateRow && position != NotificationsSettingsActivity.this.notificationsServiceConnectionRow && position != NotificationsSettingsActivity.this.inappPreviewRow && position != NotificationsSettingsActivity.this.contactJoinedRow && position != NotificationsSettingsActivity.this.pinnedMessageRow && position != NotificationsSettingsActivity.this.notificationsServiceRow && position != NotificationsSettingsActivity.this.badgeNumberMutedRow && position != NotificationsSettingsActivity.this.badgeNumberMessagesRow && position != NotificationsSettingsActivity.this.badgeNumberShowRow && position != NotificationsSettingsActivity.this.inappPriorityRow && position != NotificationsSettingsActivity.this.inchatSoundRow && position != NotificationsSettingsActivity.this.androidAutoAlertRow && position != NotificationsSettingsActivity.this.accountsAllRow) {
                    if (position != NotificationsSettingsActivity.this.resetNotificationsRow) {
                        if (position != NotificationsSettingsActivity.this.privateRow && position != NotificationsSettingsActivity.this.groupRow && position != NotificationsSettingsActivity.this.channelsRow) {
                            if (position != NotificationsSettingsActivity.this.eventsSection2Row && position != NotificationsSettingsActivity.this.notificationsSection2Row && position != NotificationsSettingsActivity.this.otherSection2Row && position != NotificationsSettingsActivity.this.resetSection2Row && position != NotificationsSettingsActivity.this.callsSection2Row && position != NotificationsSettingsActivity.this.badgeNumberSection2Row && position != NotificationsSettingsActivity.this.resetNotificationsSectionRow) {
                                if (position == NotificationsSettingsActivity.this.accountsInfoRow) {
                                    return 6;
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
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        return themeDescriptions;
    }
}
