package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class PrivacySettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int advancedSectionRow;
    private boolean archiveChats;
    private int blockedRow;
    private int botsDetailRow;
    private int botsSectionRow;
    private int callsRow;
    private boolean[] clear = new boolean[2];
    private int contactsDeleteRow;
    private int contactsDetailRow;
    private int contactsSectionRow;
    private int contactsSuggestRow;
    private int contactsSyncRow;
    private TLRPC.TL_account_password currentPassword;
    private boolean currentSuggest;
    private boolean currentSync;
    private int deleteAccountDetailRow;
    private int deleteAccountRow;
    private int forwardsRow;
    private int groupsDetailRow;
    private int groupsRow;
    private int lastSeenRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int newChatsHeaderRow;
    private int newChatsRow;
    private int newChatsSectionRow;
    private boolean newSuggest;
    private boolean newSync;
    private int passcodeRow;
    private int passportRow;
    private int passwordRow;
    private int paymentsClearRow;
    private int phoneNumberRow;
    private int privacySectionRow;
    private int profilePhotoRow;
    private AlertDialog progressDialog;
    private int rowCount;
    private int secretDetailRow;
    private int secretMapRow;
    private int secretSectionRow;
    private int secretWebpageRow;
    private int securitySectionRow;
    private int sessionsDetailRow;
    private int sessionsRow;
    private int webSessionsRow;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getContactsController().loadPrivacySettings();
        getMessagesController().getBlockedPeers(true);
        boolean z = getUserConfig().syncContacts;
        this.newSync = z;
        this.currentSync = z;
        boolean z2 = getUserConfig().suggestContacts;
        this.newSuggest = z2;
        this.currentSuggest = z2;
        TLRPC.TL_globalPrivacySettings privacySettings = getContactsController().getGlobalPrivacySettings();
        if (privacySettings != null) {
            this.archiveChats = privacySettings.archive_and_mute_new_noncontact_peers;
        }
        updateRows();
        loadPasswordSettings();
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.didSetOrRemoveTwoStepPassword);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.didSetOrRemoveTwoStepPassword);
        boolean save = false;
        if (this.currentSync != this.newSync) {
            getUserConfig().syncContacts = this.newSync;
            save = true;
            if (this.newSync) {
                getContactsController().forceImportContacts();
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", R.string.SyncContactsAdded), 0).show();
                }
            }
        }
        boolean z = this.newSuggest;
        if (z != this.currentSuggest) {
            if (!z) {
                getMediaDataController().clearTopPeers();
            }
            getUserConfig().suggestContacts = this.newSuggest;
            save = true;
            TLRPC.TL_contacts_toggleTopPeers req = new TLRPC.TL_contacts_toggleTopPeers();
            req.enabled = this.newSuggest;
            getConnectionsManager().sendRequest(req, PrivacySettingsActivity$$ExternalSyntheticLambda6.INSTANCE);
        }
        TLRPC.TL_globalPrivacySettings globalPrivacySettings = getContactsController().getGlobalPrivacySettings();
        if (globalPrivacySettings != null) {
            boolean z2 = globalPrivacySettings.archive_and_mute_new_noncontact_peers;
            boolean z3 = this.archiveChats;
            if (z2 != z3) {
                globalPrivacySettings.archive_and_mute_new_noncontact_peers = z3;
                save = true;
                TLRPC.TL_account_setGlobalPrivacySettings req2 = new TLRPC.TL_account_setGlobalPrivacySettings();
                req2.settings = new TLRPC.TL_globalPrivacySettings();
                req2.settings.flags |= 1;
                req2.settings.archive_and_mute_new_noncontact_peers = this.archiveChats;
                getConnectionsManager().sendRequest(req2, PrivacySettingsActivity$$ExternalSyntheticLambda7.INSTANCE);
            }
        }
        if (save) {
            getUserConfig().saveConfig(false);
        }
    }

    public static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$onFragmentDestroy$1(TLObject response, TLRPC.TL_error error) {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PrivacySettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacySettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.PrivacySettingsActivity.2
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                PrivacySettingsActivity.this.m4334lambda$createView$15$orgtelegramuiPrivacySettingsActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4334lambda$createView$15$orgtelegramuiPrivacySettingsActivity(View view, int position) {
        String name;
        int type;
        int selected;
        if (!view.isEnabled()) {
            return;
        }
        if (position == this.blockedRow) {
            presentFragment(new PrivacyUsersActivity());
            return;
        }
        boolean z = false;
        if (position == this.sessionsRow) {
            presentFragment(new SessionsActivity(0));
        } else if (position == this.webSessionsRow) {
            presentFragment(new SessionsActivity(1));
        } else {
            if (position != this.deleteAccountRow) {
                if (position == this.lastSeenRow) {
                    presentFragment(new PrivacyControlActivity(0));
                } else if (position == this.phoneNumberRow) {
                    presentFragment(new PrivacyControlActivity(6));
                } else if (position == this.groupsRow) {
                    presentFragment(new PrivacyControlActivity(1));
                } else if (position == this.callsRow) {
                    presentFragment(new PrivacyControlActivity(2));
                } else if (position == this.profilePhotoRow) {
                    presentFragment(new PrivacyControlActivity(4));
                } else if (position == this.forwardsRow) {
                    presentFragment(new PrivacyControlActivity(5));
                } else if (position == this.passwordRow) {
                    TLRPC.TL_account_password tL_account_password = this.currentPassword;
                    if (tL_account_password == null) {
                        return;
                    }
                    if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                        AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                    }
                    if (this.currentPassword.has_password) {
                        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
                        fragment.setPassword(this.currentPassword);
                        presentFragment(fragment);
                        return;
                    }
                    if (TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                        type = 6;
                    } else {
                        type = 5;
                    }
                    presentFragment(new TwoStepVerificationSetupActivity(type, this.currentPassword));
                } else if (position == this.passcodeRow) {
                    presentFragment(PasscodeActivity.determineOpenFragment());
                } else if (position == this.secretWebpageRow) {
                    if (getMessagesController().secretWebpagePreview == 1) {
                        getMessagesController().secretWebpagePreview = 0;
                    } else {
                        getMessagesController().secretWebpagePreview = 1;
                    }
                    MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", getMessagesController().secretWebpagePreview).commit();
                    if (view instanceof TextCheckCell) {
                        TextCheckCell textCheckCell = (TextCheckCell) view;
                        if (getMessagesController().secretWebpagePreview == 1) {
                            z = true;
                        }
                        textCheckCell.setChecked(z);
                    }
                } else if (position != this.contactsDeleteRow) {
                    if (position != this.contactsSuggestRow) {
                        if (position != this.newChatsRow) {
                            if (position == this.contactsSyncRow) {
                                boolean z2 = !this.newSync;
                                this.newSync = z2;
                                if (view instanceof TextCheckCell) {
                                    ((TextCheckCell) view).setChecked(z2);
                                    return;
                                }
                                return;
                            } else if (position == this.secretMapRow) {
                                AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new Runnable() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda14
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        PrivacySettingsActivity.this.m4330lambda$createView$10$orgtelegramuiPrivacySettingsActivity();
                                    }
                                }, false, null);
                                return;
                            } else if (position == this.paymentsClearRow) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", R.string.PrivacyPaymentsClearAlertTitle));
                                builder.setMessage(LocaleController.getString("PrivacyPaymentsClearAlertText", R.string.PrivacyPaymentsClearAlertText));
                                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                                linearLayout.setOrientation(1);
                                builder.setView(linearLayout);
                                int a = 0;
                                for (int i = 2; a < i; i = 2) {
                                    if (a == 0) {
                                        name = LocaleController.getString("PrivacyClearShipping", R.string.PrivacyClearShipping);
                                    } else {
                                        name = LocaleController.getString("PrivacyClearPayment", R.string.PrivacyClearPayment);
                                    }
                                    this.clear[a] = true;
                                    CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21, null);
                                    checkBoxCell.setTag(Integer.valueOf(a));
                                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    checkBoxCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                                    checkBoxCell.setText(name, null, true, false);
                                    checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                    checkBoxCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda12
                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View view2) {
                                            PrivacySettingsActivity.this.m4331lambda$createView$11$orgtelegramuiPrivacySettingsActivity(view2);
                                        }
                                    });
                                    a++;
                                }
                                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda9
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public final void onClick(DialogInterface dialogInterface, int i2) {
                                        PrivacySettingsActivity.this.m4333lambda$createView$14$orgtelegramuiPrivacySettingsActivity(dialogInterface, i2);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());
                                AlertDialog alertDialog = builder.create();
                                showDialog(alertDialog);
                                TextView button = (TextView) alertDialog.getButton(-1);
                                if (button != null) {
                                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                                    return;
                                }
                                return;
                            } else if (position == this.passportRow) {
                                presentFragment(new PassportActivity(5, 0L, "", "", (String) null, (String) null, (String) null, (TLRPC.TL_account_authorizationForm) null, (TLRPC.TL_account_password) null));
                                return;
                            } else {
                                return;
                            }
                        }
                        boolean z3 = !this.archiveChats;
                        this.archiveChats = z3;
                        ((TextCheckCell) view).setChecked(z3);
                        return;
                    }
                    final TextCheckCell cell = (TextCheckCell) view;
                    if (this.newSuggest) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                        builder2.setTitle(LocaleController.getString("SuggestContactsTitle", R.string.SuggestContactsTitle));
                        builder2.setMessage(LocaleController.getString("SuggestContactsAlert", R.string.SuggestContactsAlert));
                        builder2.setPositiveButton(LocaleController.getString("MuteDisable", R.string.MuteDisable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda11
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                PrivacySettingsActivity.this.m4342lambda$createView$9$orgtelegramuiPrivacySettingsActivity(cell, dialogInterface, i2);
                            }
                        });
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        AlertDialog alertDialog2 = builder2.create();
                        showDialog(alertDialog2);
                        TextView button2 = (TextView) alertDialog2.getButton(-1);
                        if (button2 != null) {
                            button2.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                            return;
                        }
                        return;
                    }
                    this.newSuggest = true;
                    cell.setChecked(true);
                } else if (getParentActivity() != null) {
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(getParentActivity());
                    builder3.setTitle(LocaleController.getString("SyncContactsDeleteTitle", R.string.SyncContactsDeleteTitle));
                    builder3.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("SyncContactsDeleteText", R.string.SyncContactsDeleteText)));
                    builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder3.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda10
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            PrivacySettingsActivity.this.m4339lambda$createView$6$orgtelegramuiPrivacySettingsActivity(dialogInterface, i2);
                        }
                    });
                    AlertDialog alertDialog3 = builder3.create();
                    showDialog(alertDialog3);
                    TextView button3 = (TextView) alertDialog3.getButton(-1);
                    if (button3 != null) {
                        button3.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                }
            } else if (getParentActivity() != null) {
                int ttl = getContactsController().getDeleteAccountTTL();
                if (ttl <= 31) {
                    selected = 0;
                } else if (ttl <= 93) {
                    selected = 1;
                } else if (ttl <= 182) {
                    selected = 2;
                } else {
                    selected = 3;
                }
                final AlertDialog.Builder builder4 = new AlertDialog.Builder(getParentActivity());
                builder4.setTitle(LocaleController.getString("DeleteAccountTitle", R.string.DeleteAccountTitle));
                String[] items = {LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
                LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                linearLayout2.setOrientation(1);
                builder4.setView(linearLayout2);
                int a2 = 0;
                while (a2 < items.length) {
                    RadioColorCell cell2 = new RadioColorCell(getParentActivity());
                    cell2.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                    cell2.setTag(Integer.valueOf(a2));
                    cell2.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                    cell2.setTextAndValue(items[a2], selected == a2);
                    linearLayout2.addView(cell2);
                    cell2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda13
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            PrivacySettingsActivity.this.m4337lambda$createView$4$orgtelegramuiPrivacySettingsActivity(builder4, view2);
                        }
                    });
                    a2++;
                }
                builder4.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder4.create());
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4337lambda$createView$4$orgtelegramuiPrivacySettingsActivity(AlertDialog.Builder builder, View v) {
        builder.getDismissRunnable().run();
        Integer which = (Integer) v.getTag();
        int value = 0;
        if (which.intValue() == 0) {
            value = 30;
        } else if (which.intValue() == 1) {
            value = 90;
        } else if (which.intValue() == 2) {
            value = 182;
        } else if (which.intValue() == 3) {
            value = 365;
        }
        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        final TLRPC.TL_account_setAccountTTL req = new TLRPC.TL_account_setAccountTTL();
        req.ttl = new TLRPC.TL_accountDaysTTL();
        req.ttl.days = value;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.this.m4336lambda$createView$3$orgtelegramuiPrivacySettingsActivity(progressDialog, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4336lambda$createView$3$orgtelegramuiPrivacySettingsActivity(final AlertDialog progressDialog, final TLRPC.TL_account_setAccountTTL req, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                PrivacySettingsActivity.this.m4335lambda$createView$2$orgtelegramuiPrivacySettingsActivity(progressDialog, response, req);
            }
        });
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4335lambda$createView$2$orgtelegramuiPrivacySettingsActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_account_setAccountTTL req) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (response instanceof TLRPC.TL_boolTrue) {
            getContactsController().setDeleteAccountTTL(req.ttl.days);
            this.listAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4339lambda$createView$6$orgtelegramuiPrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        AlertDialog.Builder builder12 = new AlertDialog.Builder(getParentActivity(), 3, null);
        AlertDialog show = builder12.show();
        this.progressDialog = show;
        show.setCanCancel(false);
        if (this.currentSync != this.newSync) {
            UserConfig userConfig = getUserConfig();
            boolean z = this.newSync;
            userConfig.syncContacts = z;
            this.currentSync = z;
            getUserConfig().saveConfig(false);
        }
        getContactsController().deleteAllContacts(new Runnable() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                PrivacySettingsActivity.this.m4338lambda$createView$5$orgtelegramuiPrivacySettingsActivity();
            }
        });
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4338lambda$createView$5$orgtelegramuiPrivacySettingsActivity() {
        this.progressDialog.dismiss();
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4342lambda$createView$9$orgtelegramuiPrivacySettingsActivity(final TextCheckCell cell, DialogInterface dialogInterface, int i) {
        TLRPC.TL_payments_clearSavedInfo req = new TLRPC.TL_payments_clearSavedInfo();
        req.credentials = this.clear[1];
        req.info = this.clear[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.this.m4341lambda$createView$8$orgtelegramuiPrivacySettingsActivity(cell, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4341lambda$createView$8$orgtelegramuiPrivacySettingsActivity(final TextCheckCell cell, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PrivacySettingsActivity.this.m4340lambda$createView$7$orgtelegramuiPrivacySettingsActivity(cell);
            }
        });
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4340lambda$createView$7$orgtelegramuiPrivacySettingsActivity(TextCheckCell cell) {
        boolean z = !this.newSuggest;
        this.newSuggest = z;
        cell.setChecked(z);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4330lambda$createView$10$orgtelegramuiPrivacySettingsActivity() {
        this.listAdapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4331lambda$createView$11$orgtelegramuiPrivacySettingsActivity(View v) {
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[num] = !zArr[num];
        cell.setChecked(zArr[num], true);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4333lambda$createView$14$orgtelegramuiPrivacySettingsActivity(DialogInterface dialogInterface, int i) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
        builder1.setTitle(LocaleController.getString("PrivacyPaymentsClearAlertTitle", R.string.PrivacyPaymentsClearAlertTitle));
        builder1.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", R.string.PrivacyPaymentsClearAlert));
        builder1.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface2, int i2) {
                PrivacySettingsActivity.this.m4332lambda$createView$13$orgtelegramuiPrivacySettingsActivity(dialogInterface2, i2);
            }
        });
        builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder1.create());
        AlertDialog alertDialog = builder1.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4332lambda$createView$13$orgtelegramuiPrivacySettingsActivity(DialogInterface dialogInterface2, int i2) {
        String text;
        TLRPC.TL_payments_clearSavedInfo req = new TLRPC.TL_payments_clearSavedInfo();
        req.credentials = this.clear[1];
        req.info = this.clear[0];
        getUserConfig().tmpPassword = null;
        getUserConfig().saveConfig(false);
        getConnectionsManager().sendRequest(req, PrivacySettingsActivity$$ExternalSyntheticLambda5.INSTANCE);
        boolean[] zArr = this.clear;
        if (zArr[0] && zArr[1]) {
            text = LocaleController.getString("PrivacyPaymentsPaymentShippingCleared", R.string.PrivacyPaymentsPaymentShippingCleared);
        } else if (zArr[0]) {
            text = LocaleController.getString("PrivacyPaymentsShippingInfoCleared", R.string.PrivacyPaymentsShippingInfoCleared);
        } else if (zArr[1]) {
            text = LocaleController.getString("PrivacyPaymentsPaymentInfoCleared", R.string.PrivacyPaymentsPaymentInfoCleared);
        } else {
            return;
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, text).show();
    }

    public static /* synthetic */ void lambda$createView$12(TLObject response, TLRPC.TL_error error) {
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            TLRPC.TL_globalPrivacySettings privacySettings = getContactsController().getGlobalPrivacySettings();
            if (privacySettings != null) {
                this.archiveChats = privacySettings.archive_and_mute_new_noncontact_peers;
            }
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            this.listAdapter.notifyItemChanged(this.blockedRow);
        } else if (id == NotificationCenter.didSetOrRemoveTwoStepPassword) {
            if (args.length > 0) {
                this.currentPassword = (TLRPC.TL_account_password) args[0];
                ListAdapter listAdapter2 = this.listAdapter;
                if (listAdapter2 != null) {
                    listAdapter2.notifyItemChanged(this.passwordRow);
                    return;
                }
                return;
            }
            this.currentPassword = null;
            loadPasswordSettings();
            updateRows();
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.privacySectionRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.blockedRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.phoneNumberRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.lastSeenRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.profilePhotoRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.forwardsRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.callsRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.groupsRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.groupsDetailRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.securitySectionRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.passcodeRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.passwordRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.sessionsRow = i12;
        this.rowCount = i13 + 1;
        this.sessionsDetailRow = i13;
        if (getMessagesController().autoarchiveAvailable || getUserConfig().isPremium()) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.newChatsHeaderRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.newChatsRow = i15;
            this.rowCount = i16 + 1;
            this.newChatsSectionRow = i16;
        } else {
            this.newChatsHeaderRow = -1;
            this.newChatsRow = -1;
            this.newChatsSectionRow = -1;
        }
        int i17 = this.rowCount;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.advancedSectionRow = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.deleteAccountRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.deleteAccountDetailRow = i19;
        this.rowCount = i20 + 1;
        this.botsSectionRow = i20;
        if (getUserConfig().hasSecureData) {
            int i21 = this.rowCount;
            this.rowCount = i21 + 1;
            this.passportRow = i21;
        } else {
            this.passportRow = -1;
        }
        int i22 = this.rowCount;
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.paymentsClearRow = i22;
        int i24 = i23 + 1;
        this.rowCount = i24;
        this.webSessionsRow = i23;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.botsDetailRow = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.contactsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.contactsDeleteRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.contactsSyncRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.contactsSuggestRow = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.contactsDetailRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.secretSectionRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.secretMapRow = i31;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.secretWebpageRow = i32;
        this.rowCount = i33 + 1;
        this.secretDetailRow = i33;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void loadPasswordSettings() {
        TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacySettingsActivity.this.m4344xbd2153e9(tLObject, tL_error);
            }
        }, 10);
    }

    /* renamed from: lambda$loadPasswordSettings$17$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4344xbd2153e9(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.TL_account_password password = (TLRPC.TL_account_password) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PrivacySettingsActivity$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    PrivacySettingsActivity.this.m4343x308128e8(password);
                }
            });
        }
    }

    /* renamed from: lambda$loadPasswordSettings$16$org-telegram-ui-PrivacySettingsActivity */
    public /* synthetic */ void m4343x308128e8(TLRPC.TL_account_password password) {
        this.currentPassword = password;
        TwoStepVerificationActivity.initPasswordNewAlgo(password);
        if (!getUserConfig().hasSecureData && password.has_secure_values) {
            getUserConfig().hasSecureData = true;
            getUserConfig().saveConfig(false);
            updateRows();
            return;
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.passwordRow);
        }
    }

    public static String formatRulesString(AccountInstance accountInstance, int rulesType) {
        ArrayList<TLRPC.PrivacyRule> privacyRules = accountInstance.getContactsController().getPrivacyRules(rulesType);
        if (privacyRules.size() == 0) {
            if (rulesType == 3) {
                return LocaleController.getString("P2PNobody", R.string.P2PNobody);
            }
            return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
        }
        int type = -1;
        int plus = 0;
        int minus = 0;
        for (int a = 0; a < privacyRules.size(); a++) {
            TLRPC.PrivacyRule rule = privacyRules.get(a);
            if (rule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                TLRPC.TL_privacyValueAllowChatParticipants participants = (TLRPC.TL_privacyValueAllowChatParticipants) rule;
                int N = participants.chats.size();
                for (int b = 0; b < N; b++) {
                    TLRPC.Chat chat = accountInstance.getMessagesController().getChat(participants.chats.get(b));
                    if (chat != null) {
                        plus += chat.participants_count;
                    }
                }
            } else if (rule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                TLRPC.TL_privacyValueDisallowChatParticipants participants2 = (TLRPC.TL_privacyValueDisallowChatParticipants) rule;
                int N2 = participants2.chats.size();
                for (int b2 = 0; b2 < N2; b2++) {
                    TLRPC.Chat chat2 = accountInstance.getMessagesController().getChat(participants2.chats.get(b2));
                    if (chat2 != null) {
                        minus += chat2.participants_count;
                    }
                }
            } else if (rule instanceof TLRPC.TL_privacyValueAllowUsers) {
                TLRPC.TL_privacyValueAllowUsers privacyValueAllowUsers = (TLRPC.TL_privacyValueAllowUsers) rule;
                plus += privacyValueAllowUsers.users.size();
            } else if (rule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                TLRPC.TL_privacyValueDisallowUsers privacyValueDisallowUsers = (TLRPC.TL_privacyValueDisallowUsers) rule;
                minus += privacyValueDisallowUsers.users.size();
            } else if (type == -1) {
                if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                    type = 0;
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                    type = 1;
                } else {
                    type = 2;
                }
            }
        }
        if (type == 0 || (type == -1 && minus > 0)) {
            if (rulesType == 3) {
                if (minus == 0) {
                    return LocaleController.getString("P2PEverybody", R.string.P2PEverybody);
                }
                return LocaleController.formatString("P2PEverybodyMinus", R.string.P2PEverybodyMinus, Integer.valueOf(minus));
            } else if (minus == 0) {
                return LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody);
            } else {
                return LocaleController.formatString("LastSeenEverybodyMinus", R.string.LastSeenEverybodyMinus, Integer.valueOf(minus));
            }
        } else if (type == 2 || (type == -1 && minus > 0 && plus > 0)) {
            if (rulesType == 3) {
                if (plus == 0 && minus == 0) {
                    return LocaleController.getString("P2PContacts", R.string.P2PContacts);
                }
                if (plus != 0 && minus != 0) {
                    return LocaleController.formatString("P2PContactsMinusPlus", R.string.P2PContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
                }
                if (minus != 0) {
                    return LocaleController.formatString("P2PContactsMinus", R.string.P2PContactsMinus, Integer.valueOf(minus));
                }
                return LocaleController.formatString("P2PContactsPlus", R.string.P2PContactsPlus, Integer.valueOf(plus));
            } else if (plus == 0 && minus == 0) {
                return LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
            } else {
                if (plus != 0 && minus != 0) {
                    return LocaleController.formatString("LastSeenContactsMinusPlus", R.string.LastSeenContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
                }
                if (minus != 0) {
                    return LocaleController.formatString("LastSeenContactsMinus", R.string.LastSeenContactsMinus, Integer.valueOf(minus));
                }
                return LocaleController.formatString("LastSeenContactsPlus", R.string.LastSeenContactsPlus, Integer.valueOf(plus));
            }
        } else if (type == 1 || plus > 0) {
            if (rulesType == 3) {
                if (plus == 0) {
                    return LocaleController.getString("P2PNobody", R.string.P2PNobody);
                }
                return LocaleController.formatString("P2PNobodyPlus", R.string.P2PNobodyPlus, Integer.valueOf(plus));
            } else if (plus == 0) {
                return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
            } else {
                return LocaleController.formatString("LastSeenNobodyPlus", R.string.LastSeenNobodyPlus, Integer.valueOf(plus));
            }
        } else {
            return "unknown";
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PrivacySettingsActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.secretWebpageRow || position == PrivacySettingsActivity.this.webSessionsRow || (position == PrivacySettingsActivity.this.groupsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) || ((position == PrivacySettingsActivity.this.lastSeenRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) || ((position == PrivacySettingsActivity.this.callsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) || ((position == PrivacySettingsActivity.this.profilePhotoRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) || ((position == PrivacySettingsActivity.this.forwardsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) || ((position == PrivacySettingsActivity.this.phoneNumberRow && !PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) || ((position == PrivacySettingsActivity.this.deleteAccountRow && !PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) || ((position == PrivacySettingsActivity.this.newChatsRow && !PrivacySettingsActivity.this.getContactsController().getLoadingGlobalSettings()) || position == PrivacySettingsActivity.this.paymentsClearRow || position == PrivacySettingsActivity.this.secretMapRow || position == PrivacySettingsActivity.this.contactsSyncRow || position == PrivacySettingsActivity.this.passportRow || position == PrivacySettingsActivity.this.contactsDeleteRow || position == PrivacySettingsActivity.this.contactsSuggestRow)))))));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PrivacySettingsActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    boolean showLoading = false;
                    String value2 = null;
                    int loadingLen = 16;
                    boolean animated = holder.itemView.getTag() != null && ((Integer) holder.itemView.getTag()).intValue() == position;
                    holder.itemView.setTag(Integer.valueOf(position));
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position != PrivacySettingsActivity.this.blockedRow) {
                        if (position != PrivacySettingsActivity.this.sessionsRow) {
                            if (position != PrivacySettingsActivity.this.webSessionsRow) {
                                if (position == PrivacySettingsActivity.this.passwordRow) {
                                    if (PrivacySettingsActivity.this.currentPassword != null) {
                                        if (PrivacySettingsActivity.this.currentPassword.has_password) {
                                            value2 = LocaleController.getString("PasswordOn", R.string.PasswordOn);
                                        } else {
                                            value2 = LocaleController.getString("PasswordOff", R.string.PasswordOff);
                                        }
                                    } else {
                                        showLoading = true;
                                    }
                                    textCell.setTextAndValue(LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), value2, true);
                                } else if (position != PrivacySettingsActivity.this.passcodeRow) {
                                    if (position == PrivacySettingsActivity.this.phoneNumberRow) {
                                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(6)) {
                                            showLoading = true;
                                            loadingLen = 30;
                                        } else {
                                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 6);
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), value2, true);
                                    } else if (position == PrivacySettingsActivity.this.lastSeenRow) {
                                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(0)) {
                                            showLoading = true;
                                            loadingLen = 30;
                                        } else {
                                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 0);
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), value2, true);
                                    } else if (position == PrivacySettingsActivity.this.groupsRow) {
                                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(1)) {
                                            showLoading = true;
                                            loadingLen = 30;
                                        } else {
                                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 1);
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), value2, false);
                                    } else if (position == PrivacySettingsActivity.this.callsRow) {
                                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(2)) {
                                            showLoading = true;
                                            loadingLen = 30;
                                        } else {
                                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 2);
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("Calls", R.string.Calls), value2, true);
                                    } else if (position == PrivacySettingsActivity.this.profilePhotoRow) {
                                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(4)) {
                                            showLoading = true;
                                            loadingLen = 30;
                                        } else {
                                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 4);
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), value2, true);
                                    } else if (position == PrivacySettingsActivity.this.forwardsRow) {
                                        if (PrivacySettingsActivity.this.getContactsController().getLoadingPrivicyInfo(5)) {
                                            showLoading = true;
                                            loadingLen = 30;
                                        } else {
                                            value2 = PrivacySettingsActivity.formatRulesString(PrivacySettingsActivity.this.getAccountInstance(), 5);
                                        }
                                        textCell.setTextAndValue(LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), value2, true);
                                    } else if (position != PrivacySettingsActivity.this.passportRow) {
                                        if (position == PrivacySettingsActivity.this.deleteAccountRow) {
                                            if (!PrivacySettingsActivity.this.getContactsController().getLoadingDeleteInfo()) {
                                                int ttl = PrivacySettingsActivity.this.getContactsController().getDeleteAccountTTL();
                                                if (ttl <= 182) {
                                                    value2 = LocaleController.formatPluralString("Months", ttl / 30, new Object[0]);
                                                } else if (ttl == 365) {
                                                    value2 = LocaleController.formatPluralString("Years", ttl / 365, new Object[0]);
                                                } else {
                                                    value2 = LocaleController.formatPluralString("Days", ttl, new Object[0]);
                                                }
                                            } else {
                                                showLoading = true;
                                            }
                                            textCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor3", R.string.DeleteAccountIfAwayFor3), value2, false);
                                        } else if (position != PrivacySettingsActivity.this.paymentsClearRow) {
                                            if (position != PrivacySettingsActivity.this.secretMapRow) {
                                                if (position == PrivacySettingsActivity.this.contactsDeleteRow) {
                                                    textCell.setText(LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), true);
                                                }
                                            } else {
                                                switch (SharedConfig.mapPreviewType) {
                                                    case 0:
                                                        value = LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram);
                                                        break;
                                                    case 1:
                                                        value = LocaleController.getString("MapPreviewProviderGoogle", R.string.MapPreviewProviderGoogle);
                                                        break;
                                                    case 2:
                                                        value = LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody);
                                                        break;
                                                    default:
                                                        value = LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex);
                                                        break;
                                                }
                                                textCell.setTextAndValue(LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), value, true);
                                            }
                                        } else {
                                            textCell.setText(LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), true);
                                        }
                                    } else {
                                        textCell.setText(LocaleController.getString("TelegramPassport", R.string.TelegramPassport), true);
                                    }
                                } else {
                                    textCell.setText(LocaleController.getString("Passcode", R.string.Passcode), true);
                                }
                            } else {
                                textCell.setText(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), false);
                            }
                        } else {
                            textCell.setText(LocaleController.getString("SessionsTitle", R.string.SessionsTitle), false);
                        }
                    } else {
                        int totalCount = PrivacySettingsActivity.this.getMessagesController().totalBlockedCount;
                        if (totalCount == 0) {
                            textCell.setTextAndValue(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("BlockedEmpty", R.string.BlockedEmpty), true);
                        } else if (totalCount > 0) {
                            textCell.setTextAndValue(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), String.format("%d", Integer.valueOf(totalCount)), true);
                        } else {
                            showLoading = true;
                            textCell.setText(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), true);
                        }
                    }
                    textCell.setDrawLoading(showLoading, loadingLen, animated);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != PrivacySettingsActivity.this.deleteAccountDetailRow) {
                        if (position != PrivacySettingsActivity.this.groupsDetailRow) {
                            if (position != PrivacySettingsActivity.this.sessionsDetailRow) {
                                if (position != PrivacySettingsActivity.this.secretDetailRow) {
                                    if (position != PrivacySettingsActivity.this.botsDetailRow) {
                                        if (position != PrivacySettingsActivity.this.contactsDetailRow) {
                                            if (position == PrivacySettingsActivity.this.newChatsSectionRow) {
                                                privacyCell.setText(LocaleController.getString("ArchiveAndMuteInfo", R.string.ArchiveAndMuteInfo));
                                                privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                                return;
                                            }
                                            return;
                                        }
                                        privacyCell.setText(LocaleController.getString("SuggestContactsInfo", R.string.SuggestContactsInfo));
                                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                        return;
                                    }
                                    privacyCell.setText(LocaleController.getString("PrivacyBotsInfo", R.string.PrivacyBotsInfo));
                                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                    return;
                                }
                                privacyCell.setText(LocaleController.getString("SecretWebPageInfo", R.string.SecretWebPageInfo));
                                privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                return;
                            }
                            privacyCell.setText(LocaleController.getString("SessionsInfo", R.string.SessionsInfo));
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        privacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", R.string.GroupsAndChannelsHelp));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    privacyCell.setText(LocaleController.getString("DeleteAccountHelp", R.string.DeleteAccountHelp));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != PrivacySettingsActivity.this.privacySectionRow) {
                        if (position != PrivacySettingsActivity.this.securitySectionRow) {
                            if (position != PrivacySettingsActivity.this.advancedSectionRow) {
                                if (position != PrivacySettingsActivity.this.secretSectionRow) {
                                    if (position != PrivacySettingsActivity.this.botsSectionRow) {
                                        if (position != PrivacySettingsActivity.this.contactsSectionRow) {
                                            if (position == PrivacySettingsActivity.this.newChatsHeaderRow) {
                                                headerCell.setText(LocaleController.getString("NewChatsFromNonContacts", R.string.NewChatsFromNonContacts));
                                                return;
                                            }
                                            return;
                                        }
                                        headerCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
                                        return;
                                    }
                                    headerCell.setText(LocaleController.getString("PrivacyBots", R.string.PrivacyBots));
                                    return;
                                }
                                headerCell.setText(LocaleController.getString("SecretChat", R.string.SecretChat));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("DeleteMyAccount", R.string.DeleteMyAccount));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("SecurityTitle", R.string.SecurityTitle));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("PrivacyTitle", R.string.PrivacyTitle));
                    return;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position != PrivacySettingsActivity.this.secretWebpageRow) {
                        if (position == PrivacySettingsActivity.this.contactsSyncRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("SyncContacts", R.string.SyncContacts), PrivacySettingsActivity.this.newSync, true);
                            return;
                        } else if (position == PrivacySettingsActivity.this.contactsSuggestRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("SuggestContacts", R.string.SuggestContacts), PrivacySettingsActivity.this.newSuggest, false);
                            return;
                        } else if (position == PrivacySettingsActivity.this.newChatsRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute), PrivacySettingsActivity.this.archiveChats, false);
                            return;
                        } else {
                            return;
                        }
                    }
                    String string = LocaleController.getString("SecretWebPage", R.string.SecretWebPage);
                    if (PrivacySettingsActivity.this.getMessagesController().secretWebpagePreview != 1) {
                        z = false;
                    }
                    textCheckCell.setTextAndCheck(string, z, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == PrivacySettingsActivity.this.passportRow || position == PrivacySettingsActivity.this.lastSeenRow || position == PrivacySettingsActivity.this.phoneNumberRow || position == PrivacySettingsActivity.this.blockedRow || position == PrivacySettingsActivity.this.deleteAccountRow || position == PrivacySettingsActivity.this.sessionsRow || position == PrivacySettingsActivity.this.webSessionsRow || position == PrivacySettingsActivity.this.passwordRow || position == PrivacySettingsActivity.this.passcodeRow || position == PrivacySettingsActivity.this.groupsRow || position == PrivacySettingsActivity.this.paymentsClearRow || position == PrivacySettingsActivity.this.secretMapRow || position == PrivacySettingsActivity.this.contactsDeleteRow) {
                return 0;
            }
            if (position != PrivacySettingsActivity.this.deleteAccountDetailRow && position != PrivacySettingsActivity.this.groupsDetailRow && position != PrivacySettingsActivity.this.sessionsDetailRow && position != PrivacySettingsActivity.this.secretDetailRow && position != PrivacySettingsActivity.this.botsDetailRow && position != PrivacySettingsActivity.this.contactsDetailRow && position != PrivacySettingsActivity.this.newChatsSectionRow) {
                if (position == PrivacySettingsActivity.this.securitySectionRow || position == PrivacySettingsActivity.this.advancedSectionRow || position == PrivacySettingsActivity.this.privacySectionRow || position == PrivacySettingsActivity.this.secretSectionRow || position == PrivacySettingsActivity.this.botsSectionRow || position == PrivacySettingsActivity.this.contactsSectionRow || position == PrivacySettingsActivity.this.newChatsHeaderRow) {
                    return 2;
                }
                return (position == PrivacySettingsActivity.this.secretWebpageRow || position == PrivacySettingsActivity.this.contactsSyncRow || position == PrivacySettingsActivity.this.contactsSuggestRow || position == PrivacySettingsActivity.this.newChatsRow) ? 3 : 0;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        return themeDescriptions;
    }
}
