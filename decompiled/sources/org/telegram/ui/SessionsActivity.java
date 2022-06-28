package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
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
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.SessionBottomSheet;
import org.telegram.ui.SessionsActivity;
/* loaded from: classes4.dex */
public class SessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TLRPC.TL_authorization currentSession;
    private int currentSessionRow;
    private int currentSessionSectionRow;
    private int currentType;
    private EmptyTextProgressView emptyView;
    private FlickerLoadingView globalFlickerLoadingView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int noOtherSessionsRow;
    private int otherSessionsEndRow;
    private int otherSessionsSectionRow;
    private int otherSessionsStartRow;
    private int otherSessionsTerminateDetail;
    private int passwordSessionsDetailRow;
    private int passwordSessionsEndRow;
    private int passwordSessionsSectionRow;
    private int passwordSessionsStartRow;
    private int qrCodeDividerRow;
    private int qrCodeRow;
    private int rowCount;
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;
    private int ttlDays;
    private int ttlDivideRow;
    private int ttlHeaderRow;
    private int ttlRow;
    private UndoView undoView;
    private ArrayList<TLObject> sessions = new ArrayList<>();
    private ArrayList<TLObject> passwordSessions = new ArrayList<>();
    private int repeatLoad = 0;
    private final int VIEW_TYPE_TEXT = 0;
    private final int VIEW_TYPE_INFO = 1;
    private final int VIEW_TYPE_HEADER = 2;
    private final int VIEW_TYPE_SESSION = 4;
    private final int VIEW_TYPE_SCANQR = 5;
    private final int VIEW_TYPE_SETTINGS = 6;

    public SessionsActivity(int type) {
        this.currentType = type;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalFlickerLoadingView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("Devices", R.string.Devices));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.SessionsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    SessionsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.SessionsActivity.2
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setDurations(150L);
        itemAnimator.setMoveInterpolator(CubicBezierInterpolator.DEFAULT);
        itemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        this.listView.setItemAnimator(itemAnimator);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                SessionsActivity.this.m4552lambda$createView$13$orgtelegramuiSessionsActivity(view, i);
            }
        });
        if (this.currentType == 0) {
            AnonymousClass3 anonymousClass3 = new AnonymousClass3(context);
            this.undoView = anonymousClass3;
            frameLayout.addView(anonymousClass3, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4552lambda$createView$13$orgtelegramuiSessionsActivity(View view, final int position) {
        String buttonText;
        String name;
        TLRPC.TL_authorization authorization;
        String buttonText2;
        int selected;
        if (position != this.ttlRow) {
            if (position == this.terminateAllSessionsRow) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                if (this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("AreYouSureSessions", R.string.AreYouSureSessions));
                    builder.setTitle(LocaleController.getString("AreYouSureSessionsTitle", R.string.AreYouSureSessionsTitle));
                    buttonText2 = LocaleController.getString("Terminate", R.string.Terminate);
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSureWebSessions", R.string.AreYouSureWebSessions));
                    builder.setTitle(LocaleController.getString("TerminateWebSessionsTitle", R.string.TerminateWebSessionsTitle));
                    buttonText2 = LocaleController.getString("Disconnect", R.string.Disconnect);
                }
                builder.setPositiveButton(buttonText2, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SessionsActivity.this.m4557lambda$createView$6$orgtelegramuiSessionsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            } else if (((position < this.otherSessionsStartRow || position >= this.otherSessionsEndRow) && ((position < this.passwordSessionsStartRow || position >= this.passwordSessionsEndRow) && position != this.currentSessionRow)) || getParentActivity() == null) {
            } else {
                if (this.currentType == 0) {
                    boolean isCurrentSession = false;
                    if (position == this.currentSessionRow) {
                        authorization = this.currentSession;
                        isCurrentSession = true;
                    } else {
                        int i = this.otherSessionsStartRow;
                        if (position < i || position >= this.otherSessionsEndRow) {
                            authorization = (TLRPC.TL_authorization) this.passwordSessions.get(position - this.passwordSessionsStartRow);
                        } else {
                            authorization = (TLRPC.TL_authorization) this.sessions.get(position - i);
                        }
                    }
                    showSessionBottomSheet(authorization, isCurrentSession);
                    return;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                final boolean[] param = new boolean[1];
                if (this.currentType != 0) {
                    TLRPC.TL_webAuthorization authorization2 = (TLRPC.TL_webAuthorization) this.sessions.get(position - this.otherSessionsStartRow);
                    builder2.setMessage(LocaleController.formatString("TerminateWebSessionText", R.string.TerminateWebSessionText, authorization2.domain));
                    builder2.setTitle(LocaleController.getString("TerminateWebSessionTitle", R.string.TerminateWebSessionTitle));
                    String buttonText3 = LocaleController.getString("Disconnect", R.string.Disconnect);
                    FrameLayout frameLayout1 = new FrameLayout(getParentActivity());
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(authorization2.bot_id));
                    if (user != null) {
                        name = UserObject.getFirstName(user);
                    } else {
                        name = "";
                    }
                    CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell.setText(LocaleController.formatString("TerminateWebSessionStop", R.string.TerminateWebSessionStop, name), "", false, false);
                    cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                    frameLayout1.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda15
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            SessionsActivity.lambda$createView$7(param, view2);
                        }
                    });
                    builder2.setCustomViewOffset(16);
                    builder2.setView(frameLayout1);
                    buttonText = buttonText3;
                } else {
                    builder2.setMessage(LocaleController.getString("TerminateSessionText", R.string.TerminateSessionText));
                    builder2.setTitle(LocaleController.getString("AreYouSureSessionTitle", R.string.AreYouSureSessionTitle));
                    buttonText = LocaleController.getString("Terminate", R.string.Terminate);
                }
                builder2.setPositiveButton(buttonText, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda13
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        SessionsActivity.this.m4551lambda$createView$12$orgtelegramuiSessionsActivity(position, param, dialogInterface, i2);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog2 = builder2.create();
                showDialog(alertDialog2);
                TextView button2 = (TextView) alertDialog2.getButton(-1);
                if (button2 != null) {
                    button2.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        } else if (getParentActivity() == null) {
        } else {
            int i2 = this.ttlDays;
            if (i2 <= 7) {
                selected = 0;
            } else if (i2 <= 93) {
                selected = 1;
            } else if (i2 <= 183) {
                selected = 2;
            } else {
                selected = 3;
            }
            final AlertDialog.Builder builder3 = new AlertDialog.Builder(getParentActivity());
            builder3.setTitle(LocaleController.getString("SessionsSelfDestruct", R.string.SessionsSelfDestruct));
            String[] items = {LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setOrientation(1);
            builder3.setView(linearLayout);
            int a = 0;
            while (a < items.length) {
                RadioColorCell cell2 = new RadioColorCell(getParentActivity());
                cell2.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                cell2.setTag(Integer.valueOf(a));
                cell2.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                cell2.setTextAndValue(items[a], selected == a);
                linearLayout.addView(cell2);
                cell2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda14
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SessionsActivity.this.m4548lambda$createView$1$orgtelegramuiSessionsActivity(builder3, view2);
                    }
                });
                a++;
            }
            builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder3.create());
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4548lambda$createView$1$orgtelegramuiSessionsActivity(AlertDialog.Builder builder, View v) {
        builder.getDismissRunnable().run();
        Integer which = (Integer) v.getTag();
        int value = 0;
        if (which.intValue() == 0) {
            value = 7;
        } else if (which.intValue() == 1) {
            value = 90;
        } else if (which.intValue() == 2) {
            value = 183;
        } else if (which.intValue() == 3) {
            value = 365;
        }
        TLRPC.TL_account_setAuthorizationTTL req = new TLRPC.TL_account_setAuthorizationTTL();
        req.authorization_ttl_days = value;
        this.ttlDays = value;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        getConnectionsManager().sendRequest(req, SessionsActivity$$ExternalSyntheticLambda10.INSTANCE);
    }

    public static /* synthetic */ void lambda$createView$0(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4557lambda$createView$6$orgtelegramuiSessionsActivity(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            TLRPC.TL_auth_resetAuthorizations req = new TLRPC.TL_auth_resetAuthorizations();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.m4554lambda$createView$3$orgtelegramuiSessionsActivity(tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_account_resetWebAuthorizations req2 = new TLRPC.TL_account_resetWebAuthorizations();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SessionsActivity.this.m4556lambda$createView$5$orgtelegramuiSessionsActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4554lambda$createView$3$orgtelegramuiSessionsActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                SessionsActivity.this.m4553lambda$createView$2$orgtelegramuiSessionsActivity(error, response);
            }
        });
        for (int a = 0; a < 4; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            if (userConfig.isClientActivated()) {
                userConfig.registeredForPush = false;
                userConfig.saveConfig(false);
                MessagesController.getInstance(a).registerForPush(SharedConfig.pushString);
                ConnectionsManager.getInstance(a).setUserId(userConfig.getClientUserId());
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4553lambda$createView$2$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null && error == null && (response instanceof TLRPC.TL_boolTrue)) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString("AllSessionsTerminated", R.string.AllSessionsTerminated)).show();
            m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4556lambda$createView$5$orgtelegramuiSessionsActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                SessionsActivity.this.m4555lambda$createView$4$orgtelegramuiSessionsActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4555lambda$createView$4$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() == null) {
            return;
        }
        if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString("AllWebSessionsTerminated", R.string.AllWebSessionsTerminated)).show();
        } else {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString("UnknownError", R.string.UnknownError)).show();
        }
        m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
    }

    public static /* synthetic */ void lambda$createView$7(boolean[] param, View v) {
        if (!v.isEnabled()) {
            return;
        }
        CheckBoxCell cell1 = (CheckBoxCell) v;
        param[0] = !param[0];
        cell1.setChecked(param[0], true);
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4551lambda$createView$12$orgtelegramuiSessionsActivity(int position, boolean[] param, DialogInterface dialogInterface, int option) {
        final TLRPC.TL_authorization authorization;
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        if (this.currentType == 0) {
            int i = this.otherSessionsStartRow;
            if (position >= i && position < this.otherSessionsEndRow) {
                authorization = (TLRPC.TL_authorization) this.sessions.get(position - i);
            } else {
                authorization = (TLRPC.TL_authorization) this.passwordSessions.get(position - this.passwordSessionsStartRow);
            }
            TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
            req.hash = authorization.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.m4559lambda$createView$9$orgtelegramuiSessionsActivity(progressDialog, authorization, tLObject, tL_error);
                }
            });
            return;
        }
        final TLRPC.TL_webAuthorization authorization2 = (TLRPC.TL_webAuthorization) this.sessions.get(position - this.otherSessionsStartRow);
        TLRPC.TL_account_resetWebAuthorization req2 = new TLRPC.TL_account_resetWebAuthorization();
        req2.hash = authorization2.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SessionsActivity.this.m4550lambda$createView$11$orgtelegramuiSessionsActivity(progressDialog, authorization2, tLObject, tL_error);
            }
        });
        if (param[0]) {
            MessagesController.getInstance(this.currentAccount).blockPeer(authorization2.bot_id);
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4559lambda$createView$9$orgtelegramuiSessionsActivity(final AlertDialog progressDialog, final TLRPC.TL_authorization authorization, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                SessionsActivity.this.m4558lambda$createView$8$orgtelegramuiSessionsActivity(progressDialog, error, authorization);
            }
        });
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4558lambda$createView$8$orgtelegramuiSessionsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_authorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            this.passwordSessions.remove(authorization);
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4550lambda$createView$11$orgtelegramuiSessionsActivity(final AlertDialog progressDialog, final TLRPC.TL_webAuthorization authorization, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SessionsActivity.this.m4549lambda$createView$10$orgtelegramuiSessionsActivity(progressDialog, error, authorization);
            }
        });
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4549lambda$createView$10$orgtelegramuiSessionsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_webAuthorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$3 */
    /* loaded from: classes4.dex */
    public class AnonymousClass3 extends UndoView {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(Context context) {
            super(context);
            SessionsActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.UndoView
        public void hide(boolean apply, int animated) {
            if (!apply) {
                final TLRPC.TL_authorization authorization = (TLRPC.TL_authorization) getCurrentInfoObject();
                TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
                req.hash = authorization.hash;
                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$3$$ExternalSyntheticLambda1
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SessionsActivity.AnonymousClass3.this.m4568lambda$hide$1$orgtelegramuiSessionsActivity$3(authorization, tLObject, tL_error);
                    }
                });
            }
            super.hide(apply, animated);
        }

        /* renamed from: lambda$hide$1$org-telegram-ui-SessionsActivity$3 */
        public /* synthetic */ void m4568lambda$hide$1$orgtelegramuiSessionsActivity$3(final TLRPC.TL_authorization authorization, TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SessionsActivity.AnonymousClass3.this.m4567lambda$hide$0$orgtelegramuiSessionsActivity$3(error, authorization);
                }
            });
        }

        /* renamed from: lambda$hide$0$org-telegram-ui-SessionsActivity$3 */
        public /* synthetic */ void m4567lambda$hide$0$orgtelegramuiSessionsActivity$3(TLRPC.TL_error error, TLRPC.TL_authorization authorization) {
            if (error == null) {
                SessionsActivity.this.sessions.remove(authorization);
                SessionsActivity.this.passwordSessions.remove(authorization);
                SessionsActivity.this.updateRows();
                if (SessionsActivity.this.listAdapter != null) {
                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
                SessionsActivity.this.m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(true);
            }
        }
    }

    private void showSessionBottomSheet(TLRPC.TL_authorization authorization, boolean isCurrentSession) {
        if (authorization == null) {
            return;
        }
        SessionBottomSheet bottomSheet = new SessionBottomSheet(this, authorization, isCurrentSession, new AnonymousClass4());
        bottomSheet.show();
    }

    /* renamed from: org.telegram.ui.SessionsActivity$4 */
    /* loaded from: classes4.dex */
    public class AnonymousClass4 implements SessionBottomSheet.Callback {
        AnonymousClass4() {
            SessionsActivity.this = this$0;
        }

        @Override // org.telegram.ui.SessionBottomSheet.Callback
        public void onSessionTerminated(TLRPC.TL_authorization authorization) {
            SessionsActivity.this.sessions.remove(authorization);
            SessionsActivity.this.passwordSessions.remove(authorization);
            SessionsActivity.this.updateRows();
            if (SessionsActivity.this.listAdapter != null) {
                SessionsActivity.this.listAdapter.notifyDataSetChanged();
            }
            TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
            req.hash = authorization.hash;
            ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req, SessionsActivity$4$$ExternalSyntheticLambda1.INSTANCE);
        }

        public static /* synthetic */ void lambda$onSessionTerminated$0() {
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
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

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newSessionReceived) {
            m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(true);
        }
    }

    /* renamed from: loadSessions */
    public void m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(final boolean silent) {
        if (this.loading) {
            return;
        }
        if (!silent) {
            this.loading = true;
        }
        if (this.currentType == 0) {
            TLRPC.TL_account_getAuthorizations req = new TLRPC.TL_account_getAuthorizations();
            int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda8
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.m4562lambda$loadSessions$16$orgtelegramuiSessionsActivity(silent, tLObject, tL_error);
                }
            });
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
            return;
        }
        TLRPC.TL_account_getWebAuthorizations req2 = new TLRPC.TL_account_getWebAuthorizations();
        int reqId2 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SessionsActivity.this.m4565lambda$loadSessions$19$orgtelegramuiSessionsActivity(silent, tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId2, this.classGuid);
    }

    /* renamed from: lambda$loadSessions$16$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4562lambda$loadSessions$16$orgtelegramuiSessionsActivity(final boolean silent, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                SessionsActivity.this.m4561lambda$loadSessions$15$orgtelegramuiSessionsActivity(error, response, silent);
            }
        });
    }

    /* renamed from: lambda$loadSessions$15$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4561lambda$loadSessions$15$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response, final boolean silent) {
        this.loading = false;
        this.listAdapter.getItemCount();
        if (error == null) {
            this.sessions.clear();
            this.passwordSessions.clear();
            TLRPC.TL_account_authorizations res = (TLRPC.TL_account_authorizations) response;
            int N = res.authorizations.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_authorization authorization = res.authorizations.get(a);
                if ((authorization.flags & 1) != 0) {
                    this.currentSession = authorization;
                } else if (authorization.password_pending) {
                    this.passwordSessions.add(authorization);
                } else {
                    this.sessions.add(authorization);
                }
            }
            int a2 = res.authorization_ttl_days;
            this.ttlDays = a2;
            updateRows();
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        int i = this.repeatLoad;
        if (i > 0) {
            int i2 = i - 1;
            this.repeatLoad = i2;
            if (i2 > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SessionsActivity.this.m4560lambda$loadSessions$14$orgtelegramuiSessionsActivity(silent);
                    }
                }, 2500L);
            }
        }
    }

    /* renamed from: lambda$loadSessions$19$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4565lambda$loadSessions$19$orgtelegramuiSessionsActivity(final boolean silent, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                SessionsActivity.this.m4564lambda$loadSessions$18$orgtelegramuiSessionsActivity(error, response, silent);
            }
        });
    }

    /* renamed from: lambda$loadSessions$18$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4564lambda$loadSessions$18$orgtelegramuiSessionsActivity(TLRPC.TL_error error, TLObject response, final boolean silent) {
        this.loading = false;
        if (error == null) {
            this.sessions.clear();
            TLRPC.TL_account_webAuthorizations res = (TLRPC.TL_account_webAuthorizations) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.sessions.addAll(res.authorizations);
            updateRows();
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        int i = this.repeatLoad;
        if (i > 0) {
            int i2 = i - 1;
            this.repeatLoad = i2;
            if (i2 > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        SessionsActivity.this.m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(silent);
                    }
                }, 2500L);
            }
        }
    }

    public void updateRows() {
        this.rowCount = 0;
        this.currentSessionSectionRow = -1;
        this.currentSessionRow = -1;
        this.terminateAllSessionsRow = -1;
        this.terminateAllSessionsDetailRow = -1;
        this.passwordSessionsSectionRow = -1;
        this.passwordSessionsStartRow = -1;
        this.passwordSessionsEndRow = -1;
        this.passwordSessionsDetailRow = -1;
        this.otherSessionsSectionRow = -1;
        this.otherSessionsStartRow = -1;
        this.otherSessionsEndRow = -1;
        this.otherSessionsTerminateDetail = -1;
        this.noOtherSessionsRow = -1;
        this.qrCodeRow = -1;
        this.qrCodeDividerRow = -1;
        this.ttlHeaderRow = -1;
        this.ttlRow = -1;
        this.ttlDivideRow = -1;
        if (this.currentType == 0 && getMessagesController().qrLoginCamera) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.qrCodeRow = i;
            this.rowCount = i2 + 1;
            this.qrCodeDividerRow = i2;
        }
        if (this.loading) {
            if (this.currentType == 0) {
                int i3 = this.rowCount;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.currentSessionSectionRow = i3;
                this.rowCount = i4 + 1;
                this.currentSessionRow = i4;
                return;
            }
            return;
        }
        if (this.currentSession != null) {
            int i5 = this.rowCount;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.currentSessionSectionRow = i5;
            this.rowCount = i6 + 1;
            this.currentSessionRow = i6;
        }
        if (!this.passwordSessions.isEmpty() || !this.sessions.isEmpty()) {
            int i7 = this.rowCount;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.terminateAllSessionsRow = i7;
            this.rowCount = i8 + 1;
            this.terminateAllSessionsDetailRow = i8;
            this.noOtherSessionsRow = -1;
        } else {
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            if (this.currentType == 1 || this.currentSession != null) {
                int i9 = this.rowCount;
                this.rowCount = i9 + 1;
                this.noOtherSessionsRow = i9;
            } else {
                this.noOtherSessionsRow = -1;
            }
        }
        if (!this.passwordSessions.isEmpty()) {
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.passwordSessionsSectionRow = i10;
            this.passwordSessionsStartRow = i11;
            int size = i11 + this.passwordSessions.size();
            this.rowCount = size;
            this.passwordSessionsEndRow = size;
            this.rowCount = size + 1;
            this.passwordSessionsDetailRow = size;
        }
        if (!this.sessions.isEmpty()) {
            int i12 = this.rowCount;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.otherSessionsSectionRow = i12;
            this.otherSessionsStartRow = i13;
            this.otherSessionsEndRow = i13 + this.sessions.size();
            int size2 = this.rowCount + this.sessions.size();
            this.rowCount = size2;
            this.rowCount = size2 + 1;
            this.otherSessionsTerminateDetail = size2;
        }
        if (this.ttlDays > 0) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.ttlHeaderRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.ttlRow = i15;
            this.rowCount = i16 + 1;
            this.ttlDivideRow = i16;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            SessionsActivity.this = r1;
            this.mContext = context;
            setHasStableIds(true);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == SessionsActivity.this.terminateAllSessionsRow || (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) || ((position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) || position == SessionsActivity.this.currentSessionRow || position == SessionsActivity.this.ttlRow);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return SessionsActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                case 4:
                default:
                    view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new ScanQRCodeView(this.mContext);
                    break;
                case 6:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position != SessionsActivity.this.terminateAllSessionsRow) {
                        if (position == SessionsActivity.this.qrCodeRow) {
                            textCell.setColors(Theme.key_windowBackgroundWhiteBlueText4, Theme.key_windowBackgroundWhiteBlueText4);
                            textCell.setTag(Theme.key_windowBackgroundWhiteBlueText4);
                            textCell.setTextAndIcon(LocaleController.getString("AuthAnotherClient", R.string.AuthAnotherClient), R.drawable.msg_qrcode, true ^ SessionsActivity.this.sessions.isEmpty());
                            return;
                        }
                        return;
                    }
                    textCell.setColors(Theme.key_windowBackgroundWhiteRedText2, Theme.key_windowBackgroundWhiteRedText2);
                    textCell.setTag(Theme.key_windowBackgroundWhiteRedText2);
                    if (SessionsActivity.this.currentType == 0) {
                        textCell.setTextAndIcon(LocaleController.getString("TerminateAllSessions", R.string.TerminateAllSessions), R.drawable.msg_block2, false);
                        return;
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("TerminateAllWebSessions", R.string.TerminateAllWebSessions), R.drawable.msg_block2, false);
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    privacyCell.setFixedSize(0);
                    if (position == SessionsActivity.this.terminateAllSessionsDetailRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("ClearOtherSessionsHelp", R.string.ClearOtherSessionsHelp));
                        } else {
                            privacyCell.setText(LocaleController.getString("ClearOtherWebSessionsHelp", R.string.ClearOtherWebSessionsHelp));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                        if (SessionsActivity.this.currentType == 0) {
                            if (SessionsActivity.this.sessions.isEmpty()) {
                                privacyCell.setText("");
                            } else {
                                privacyCell.setText(LocaleController.getString("SessionsListInfo", R.string.SessionsListInfo));
                            }
                        } else {
                            privacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", R.string.TerminateWebSessionInfo));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position != SessionsActivity.this.passwordSessionsDetailRow) {
                        if (position == SessionsActivity.this.qrCodeDividerRow || position == SessionsActivity.this.ttlDivideRow || position == SessionsActivity.this.noOtherSessionsRow) {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            privacyCell.setText("");
                            privacyCell.setFixedSize(12);
                            return;
                        }
                        return;
                    } else {
                        privacyCell.setText(LocaleController.getString("LoginAttemptsInfo", R.string.LoginAttemptsInfo));
                        if (SessionsActivity.this.otherSessionsTerminateDetail == -1) {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        } else {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != SessionsActivity.this.currentSessionSectionRow) {
                        if (position == SessionsActivity.this.otherSessionsSectionRow) {
                            if (SessionsActivity.this.currentType == 0) {
                                headerCell.setText(LocaleController.getString("OtherSessions", R.string.OtherSessions));
                                return;
                            } else {
                                headerCell.setText(LocaleController.getString("OtherWebSessions", R.string.OtherWebSessions));
                                return;
                            }
                        } else if (position != SessionsActivity.this.passwordSessionsSectionRow) {
                            if (position == SessionsActivity.this.ttlHeaderRow) {
                                headerCell.setText(LocaleController.getString("TerminateOldSessionHeader", R.string.TerminateOldSessionHeader));
                                return;
                            }
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("LoginAttempts", R.string.LoginAttempts));
                            return;
                        }
                    }
                    headerCell.setText(LocaleController.getString("CurrentSession", R.string.CurrentSession));
                    return;
                case 3:
                case 4:
                default:
                    SessionCell sessionCell = (SessionCell) holder.itemView;
                    if (position == SessionsActivity.this.currentSessionRow) {
                        if (SessionsActivity.this.currentSession == null) {
                            sessionCell.showStub(SessionsActivity.this.globalFlickerLoadingView);
                            return;
                        }
                        TLRPC.TL_authorization tL_authorization = SessionsActivity.this.currentSession;
                        if (SessionsActivity.this.sessions.isEmpty() && SessionsActivity.this.passwordSessions.isEmpty() && SessionsActivity.this.qrCodeRow == -1) {
                            z = false;
                        }
                        sessionCell.setSession(tL_authorization, z);
                        return;
                    } else if (position < SessionsActivity.this.otherSessionsStartRow || position >= SessionsActivity.this.otherSessionsEndRow) {
                        if (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) {
                            TLObject tLObject = (TLObject) SessionsActivity.this.passwordSessions.get(position - SessionsActivity.this.passwordSessionsStartRow);
                            if (position == SessionsActivity.this.passwordSessionsEndRow - 1) {
                                z = false;
                            }
                            sessionCell.setSession(tLObject, z);
                            return;
                        }
                        return;
                    } else {
                        TLObject tLObject2 = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                        if (position == SessionsActivity.this.otherSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(tLObject2, z);
                        return;
                    }
                case 5:
                    return;
                case 6:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    String value = (SessionsActivity.this.ttlDays <= 30 || SessionsActivity.this.ttlDays > 183) ? SessionsActivity.this.ttlDays == 365 ? LocaleController.formatPluralString("Years", SessionsActivity.this.ttlDays / 365, new Object[0]) : LocaleController.formatPluralString("Weeks", SessionsActivity.this.ttlDays / 7, new Object[0]) : LocaleController.formatPluralString("Months", SessionsActivity.this.ttlDays / 30, new Object[0]);
                    textSettingsCell.setTextAndValue(LocaleController.getString("IfInactiveFor", R.string.IfInactiveFor), value, true, false);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return Arrays.hashCode(new Object[]{0, 0});
            }
            if (position == SessionsActivity.this.terminateAllSessionsDetailRow) {
                return Arrays.hashCode(new Object[]{0, 1});
            }
            if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                return Arrays.hashCode(new Object[]{0, 2});
            }
            if (position == SessionsActivity.this.passwordSessionsDetailRow) {
                return Arrays.hashCode(new Object[]{0, 3});
            }
            if (position == SessionsActivity.this.qrCodeDividerRow) {
                return Arrays.hashCode(new Object[]{0, 4});
            }
            if (position == SessionsActivity.this.ttlDivideRow) {
                return Arrays.hashCode(new Object[]{0, 5});
            }
            if (position == SessionsActivity.this.noOtherSessionsRow) {
                return Arrays.hashCode(new Object[]{0, 6});
            }
            if (position == SessionsActivity.this.currentSessionSectionRow) {
                return Arrays.hashCode(new Object[]{0, 7});
            }
            if (position == SessionsActivity.this.otherSessionsSectionRow) {
                return Arrays.hashCode(new Object[]{0, 8});
            }
            if (position == SessionsActivity.this.passwordSessionsSectionRow) {
                return Arrays.hashCode(new Object[]{0, 9});
            }
            if (position == SessionsActivity.this.ttlHeaderRow) {
                return Arrays.hashCode(new Object[]{0, 10});
            }
            if (position == SessionsActivity.this.currentSessionRow) {
                return Arrays.hashCode(new Object[]{0, 11});
            }
            if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) {
                TLObject session = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                if (session instanceof TLRPC.TL_authorization) {
                    return Arrays.hashCode(new Object[]{1, Long.valueOf(((TLRPC.TL_authorization) session).hash)});
                }
                if (session instanceof TLRPC.TL_webAuthorization) {
                    return Arrays.hashCode(new Object[]{1, Long.valueOf(((TLRPC.TL_webAuthorization) session).hash)});
                }
            } else if (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) {
                TLObject session2 = (TLObject) SessionsActivity.this.passwordSessions.get(position - SessionsActivity.this.passwordSessionsStartRow);
                if (session2 instanceof TLRPC.TL_authorization) {
                    return Arrays.hashCode(new Object[]{2, Long.valueOf(((TLRPC.TL_authorization) session2).hash)});
                }
                if (session2 instanceof TLRPC.TL_webAuthorization) {
                    return Arrays.hashCode(new Object[]{2, Long.valueOf(((TLRPC.TL_webAuthorization) session2).hash)});
                }
            } else if (position == SessionsActivity.this.qrCodeRow) {
                return Arrays.hashCode(new Object[]{0, 12});
            } else {
                if (position == SessionsActivity.this.ttlRow) {
                    return Arrays.hashCode(new Object[]{0, 13});
                }
            }
            return Arrays.hashCode(new Object[]{0, -1});
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position != SessionsActivity.this.terminateAllSessionsDetailRow && position != SessionsActivity.this.otherSessionsTerminateDetail && position != SessionsActivity.this.passwordSessionsDetailRow && position != SessionsActivity.this.qrCodeDividerRow && position != SessionsActivity.this.ttlDivideRow && position != SessionsActivity.this.noOtherSessionsRow) {
                if (position != SessionsActivity.this.currentSessionSectionRow && position != SessionsActivity.this.otherSessionsSectionRow && position != SessionsActivity.this.passwordSessionsSectionRow && position != SessionsActivity.this.ttlHeaderRow) {
                    if (position != SessionsActivity.this.currentSessionRow) {
                        if (position < SessionsActivity.this.otherSessionsStartRow || position >= SessionsActivity.this.otherSessionsEndRow) {
                            if (position < SessionsActivity.this.passwordSessionsStartRow || position >= SessionsActivity.this.passwordSessionsEndRow) {
                                if (position == SessionsActivity.this.qrCodeRow) {
                                    return 5;
                                }
                                return position == SessionsActivity.this.ttlRow ? 6 : 0;
                            }
                            return 4;
                        }
                        return 4;
                    }
                    return 4;
                }
                return 2;
            }
            return 1;
        }
    }

    /* loaded from: classes4.dex */
    public class ScanQRCodeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        BackupImageView imageView;
        TextView textView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ScanQRCodeView(Context context) {
            super(context);
            SessionsActivity.this = r19;
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(120, 120.0f, 1, 0.0f, 16.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionsActivity.ScanQRCodeView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation() != null && !ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().isRunning()) {
                        ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().restart();
                    }
                }
            });
            int[] colors = {3355443, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), ViewCompat.MEASURED_SIZE_MASK, Theme.getColor(Theme.key_windowBackgroundWhite), 5285866, Theme.getColor(Theme.key_featuredStickers_addButton), 2170912, Theme.getColor(Theme.key_windowBackgroundWhite)};
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.textView = linksTextView;
            addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 36.0f, 152.0f, 36.0f, 0.0f));
            this.textView.setGravity(1);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 15.0f);
            this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            this.textView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            String text = LocaleController.getString("AuthAnotherClientInfo4", R.string.AuthAnotherClientInfo4);
            SpannableStringBuilder spanned = new SpannableStringBuilder(text);
            int index1 = text.indexOf(42);
            int index2 = text.indexOf(42, index1 + 1);
            if (index1 != -1 && index2 != -1 && index1 != index2) {
                this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                spanned.replace(index2, index2 + 1, (CharSequence) "");
                spanned.replace(index1, index1 + 1, (CharSequence) "");
                spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", R.string.AuthAnotherClientDownloadClientUrl)), index1, index2 - 1, 33);
            }
            String text2 = spanned.toString();
            int index12 = text2.indexOf(42);
            int index22 = text2.indexOf(42, index12 + 1);
            if (index12 != -1 && index22 != -1 && index12 != index22) {
                this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                spanned.replace(index22, index22 + 1, (CharSequence) "");
                spanned.replace(index12, index12 + 1, (CharSequence) "");
                spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherWebClientUrl", R.string.AuthAnotherWebClientUrl)), index12, index22 - 1, 33);
            }
            this.textView.setText(spanned);
            TextView buttonTextView = new TextView(context);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) ".  ").append((CharSequence) LocaleController.getString("LinkDesktopDevice", R.string.LinkDesktopDevice));
            spannableStringBuilder.setSpan(new ColoredImageSpan(ContextCompat.getDrawable(getContext(), R.drawable.msg_mini_qr)), 0, 1, 0);
            buttonTextView.setText(spannableStringBuilder);
            buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
            buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.SessionsActivity$ScanQRCodeView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SessionsActivity.ScanQRCodeView.this.m4574lambda$new$0$orgtelegramuiSessionsActivity$ScanQRCodeView(view);
                }
            });
            addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
            setSticker();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-SessionsActivity$ScanQRCodeView */
        public /* synthetic */ void m4574lambda$new$0$orgtelegramuiSessionsActivity$ScanQRCodeView(View view) {
            if (SessionsActivity.this.getParentActivity() == null) {
                return;
            }
            if (Build.VERSION.SDK_INT < 23 || SessionsActivity.this.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                SessionsActivity.this.openCameraScanActivity();
            } else {
                SessionsActivity.this.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(276.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(SessionsActivity.this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(SessionsActivity.this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.diceStickersDidLoad) {
                String name = (String) args[0];
                if (AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME.equals(name)) {
                    setSticker();
                }
            }
        }

        private void setSticker() {
            TLRPC.Document document = null;
            TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(SessionsActivity.this.currentAccount).getStickerSetByName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME);
            if (set == null) {
                set = MediaDataController.getInstance(SessionsActivity.this.currentAccount).getStickerSetByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME);
            }
            if (set != null && set.documents.size() > 6) {
                document = set.documents.get(6);
            }
            SvgHelper.SvgDrawable svgThumb = null;
            if (document != null) {
                svgThumb = DocumentObject.getSvgThumb(document.thumbs, Theme.key_emptyListPlaceholder, 0.2f);
            }
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            if (document == null) {
                MediaDataController.getInstance(SessionsActivity.this.currentAccount).loadStickersByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME, false, set == null);
                return;
            }
            ImageLocation imageLocation = ImageLocation.getForDocument(document);
            this.imageView.setImage(imageLocation, "130_130", "tgs", svgThumb, set);
            this.imageView.getImageReceiver().setAutoRepeat(2);
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 implements CameraScanActivity.CameraScanActivityDelegate {
        private TLObject response = null;
        private TLRPC.TL_error error = null;

        @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
        public /* synthetic */ void didFindMrzInfo(MrzRecognizer.Result result) {
            CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
        }

        AnonymousClass5() {
            SessionsActivity.this = this$0;
        }

        @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
        public void didFindQr(String link) {
            TLObject tLObject = this.response;
            if (tLObject instanceof TLRPC.TL_authorization) {
                TLRPC.TL_authorization authorization = (TLRPC.TL_authorization) tLObject;
                if (((TLRPC.TL_authorization) tLObject).password_pending) {
                    SessionsActivity.this.passwordSessions.add(0, authorization);
                    SessionsActivity.this.repeatLoad = 4;
                    SessionsActivity.this.m4563lambda$loadSessions$17$orgtelegramuiSessionsActivity(false);
                } else {
                    SessionsActivity.this.sessions.add(0, authorization);
                }
                SessionsActivity.this.updateRows();
                SessionsActivity.this.listAdapter.notifyDataSetChanged();
                SessionsActivity.this.undoView.showWithAction(0L, 11, this.response);
            } else if (this.error != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SessionsActivity.AnonymousClass5.this.m4569lambda$didFindQr$0$orgtelegramuiSessionsActivity$5();
                    }
                });
            }
        }

        /* renamed from: lambda$didFindQr$0$org-telegram-ui-SessionsActivity$5 */
        public /* synthetic */ void m4569lambda$didFindQr$0$orgtelegramuiSessionsActivity$5() {
            String text;
            if (this.error.text != null && this.error.text.equals("AUTH_TOKEN_EXCEPTION")) {
                text = LocaleController.getString("AccountAlreadyLoggedIn", R.string.AccountAlreadyLoggedIn);
            } else {
                text = LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + this.error.text;
            }
            AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString("AuthAnotherClient", R.string.AuthAnotherClient), text);
        }

        @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
        public boolean processQr(final String link, final Runnable onLoadEnd) {
            this.response = null;
            this.error = null;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$5$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SessionsActivity.AnonymousClass5.this.m4573lambda$processQr$4$orgtelegramuiSessionsActivity$5(link, onLoadEnd);
                }
            }, 750L);
            return true;
        }

        /* renamed from: lambda$processQr$4$org-telegram-ui-SessionsActivity$5 */
        public /* synthetic */ void m4573lambda$processQr$4$orgtelegramuiSessionsActivity$5(String link, final Runnable onLoadEnd) {
            try {
                String code = link.substring("tg://login?token=".length());
                byte[] token = Base64.decode(code.replaceAll("\\/", "_").replaceAll("\\+", "-"), 8);
                TLRPC.TL_auth_acceptLoginToken req = new TLRPC.TL_auth_acceptLoginToken();
                req.token = token;
                SessionsActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.SessionsActivity$5$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SessionsActivity.AnonymousClass5.this.m4571lambda$processQr$2$orgtelegramuiSessionsActivity$5(onLoadEnd, tLObject, tL_error);
                    }
                });
            } catch (Exception e) {
                FileLog.e("Failed to pass qr code auth", e);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$5$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SessionsActivity.AnonymousClass5.this.m4572lambda$processQr$3$orgtelegramuiSessionsActivity$5();
                    }
                });
                onLoadEnd.run();
            }
        }

        /* renamed from: lambda$processQr$2$org-telegram-ui-SessionsActivity$5 */
        public /* synthetic */ void m4571lambda$processQr$2$orgtelegramuiSessionsActivity$5(final Runnable onLoadEnd, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.SessionsActivity$5$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SessionsActivity.AnonymousClass5.this.m4570lambda$processQr$1$orgtelegramuiSessionsActivity$5(response, error, onLoadEnd);
                }
            });
        }

        /* renamed from: lambda$processQr$1$org-telegram-ui-SessionsActivity$5 */
        public /* synthetic */ void m4570lambda$processQr$1$orgtelegramuiSessionsActivity$5(TLObject response, TLRPC.TL_error error, Runnable onLoadEnd) {
            this.response = response;
            this.error = error;
            onLoadEnd.run();
        }

        /* renamed from: lambda$processQr$3$org-telegram-ui-SessionsActivity$5 */
        public /* synthetic */ void m4572lambda$processQr$3$orgtelegramuiSessionsActivity$5() {
            AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString("AuthAnotherClient", R.string.AuthAnotherClient), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
        }
    }

    public void openCameraScanActivity() {
        CameraScanActivity.showAsSheet(this, false, 2, new AnonymousClass5());
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText2));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText2));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        return themeDescriptions;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (getParentActivity() != null && requestCode == 34) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                openCameraScanActivity();
            } else {
                new AlertDialog.Builder(getParentActivity()).setMessage(AndroidUtilities.replaceTags(LocaleController.getString("QRCodePermissionNoCameraWithHint", R.string.QRCodePermissionNoCameraWithHint))).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.SessionsActivity$$ExternalSyntheticLambda11
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SessionsActivity.this.m4566xcdb187e2(dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), null).setTopAnimation(R.raw.permission_request_camera, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).show();
            }
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$20$org-telegram-ui-SessionsActivity */
    public /* synthetic */ void m4566xcdb187e2(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
