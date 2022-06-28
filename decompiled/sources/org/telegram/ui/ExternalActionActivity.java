package org.telegram.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
/* loaded from: classes4.dex */
public class ExternalActionActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate {
    protected ActionBarLayout actionBarLayout;
    protected SizeNotifierFrameLayout backgroundTablet;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    protected ActionBarLayout layersActionBarLayout;
    private Runnable lockRunnable;
    private Intent passcodeSaveIntent;
    private int passcodeSaveIntentAccount;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private int passcodeSaveIntentState;
    private PasscodeView passcodeView;
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationLoader.postInitApplication();
        requestWindowFeature(1);
        setTheme(R.style.Theme_TMessages);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
            try {
                getWindow().setFlags(8192, 8192);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        super.onCreate(savedInstanceState);
        if (SharedConfig.passcodeHash.length() != 0 && SharedConfig.appLocked) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
        }
        AndroidUtilities.fillStatusBarHeight(this);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        this.actionBarLayout = new ActionBarLayout(this);
        DrawerLayoutContainer drawerLayoutContainer = new DrawerLayoutContainer(this);
        this.drawerLayoutContainer = drawerLayoutContainer;
        drawerLayoutContainer.setAllowOpenDrawer(false, false);
        setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(16);
            RelativeLayout launchLayout = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(launchLayout);
            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) launchLayout.getLayoutParams();
            layoutParams1.width = -1;
            layoutParams1.height = -1;
            launchLayout.setLayoutParams(layoutParams1);
            SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(this) { // from class: org.telegram.ui.ExternalActionActivity.1
                @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
                protected boolean isActionBarVisible() {
                    return false;
                }
            };
            this.backgroundTablet = sizeNotifierFrameLayout;
            sizeNotifierFrameLayout.setOccupyStatusBar(false);
            this.backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            launchLayout.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
            FrameLayout shadowTablet = new FrameLayout(this);
            shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            launchLayout.addView(shadowTablet, LayoutHelper.createRelative(-1, -1));
            shadowTablet.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda5
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ExternalActionActivity.this.m3416lambda$onCreate$0$orgtelegramuiExternalActionActivity(view, motionEvent);
                }
            });
            shadowTablet.setOnClickListener(ExternalActionActivity$$ExternalSyntheticLambda4.INSTANCE);
            ActionBarLayout actionBarLayout = new ActionBarLayout(this);
            this.layersActionBarLayout = actionBarLayout;
            actionBarLayout.setRemoveActionBarExtraHeight(true);
            this.layersActionBarLayout.setBackgroundView(shadowTablet);
            this.layersActionBarLayout.setUseAlphaAnimations(true);
            this.layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            launchLayout.addView(this.layersActionBarLayout, LayoutHelper.createRelative(530, AndroidUtilities.isSmallTablet() ? 528 : 700));
            this.layersActionBarLayout.init(layerFragmentsStack);
            this.layersActionBarLayout.setDelegate(this);
            this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        } else {
            RelativeLayout launchLayout2 = new RelativeLayout(this);
            this.drawerLayoutContainer.addView(launchLayout2, LayoutHelper.createFrame(-1, -1.0f));
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = new SizeNotifierFrameLayout(this) { // from class: org.telegram.ui.ExternalActionActivity.2
                @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
                protected boolean isActionBarVisible() {
                    return false;
                }
            };
            this.backgroundTablet = sizeNotifierFrameLayout2;
            sizeNotifierFrameLayout2.setOccupyStatusBar(false);
            this.backgroundTablet.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            launchLayout2.addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            launchLayout2.addView(this.actionBarLayout, LayoutHelper.createRelative(-1, -1));
        }
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        PasscodeView passcodeView = new PasscodeView(this);
        this.passcodeView = passcodeView;
        this.drawerLayoutContainer.addView(passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        this.actionBarLayout.removeAllFragments();
        ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
        if (actionBarLayout2 != null) {
            actionBarLayout2.removeAllFragments();
        }
        handleIntent(getIntent(), false, savedInstanceState != null, false, UserConfig.selectedAccount, 0);
        needLayout();
    }

    /* renamed from: lambda$onCreate$0$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ boolean m3416lambda$onCreate$0$orgtelegramuiExternalActionActivity(View v, MotionEvent event) {
        if (this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int[] location = new int[2];
        this.layersActionBarLayout.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        if (this.layersActionBarLayout.checkTransitionAnimation() || (x > viewX && x < this.layersActionBarLayout.getWidth() + viewX && y > viewY && y < this.layersActionBarLayout.getHeight() + viewY)) {
            return false;
        }
        if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                actionBarLayout.removeFragmentFromStack(actionBarLayout.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(true);
        }
        return true;
    }

    public static /* synthetic */ void lambda$onCreate$1(View v) {
    }

    public void showPasscodeActivity() {
        if (this.passcodeView == null) {
            return;
        }
        SharedConfig.appLocked = true;
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        this.passcodeView.onShow(true, false);
        SharedConfig.isWaitingForPasscodeEnter = true;
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate
            public final void didAcceptedPassword() {
                ExternalActionActivity.this.m3417x2765c575();
            }
        });
    }

    /* renamed from: lambda$showPasscodeActivity$2$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3417x2765c575() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, this.passcodeSaveIntentAccount, this.passcodeSaveIntentState);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
        }
    }

    public void onFinishLogin() {
        handleIntent(this.passcodeSaveIntent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true, this.passcodeSaveIntentAccount, this.passcodeSaveIntentState);
        this.actionBarLayout.removeAllFragments();
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.removeAllFragments();
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.backgroundTablet;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setVisibility(0);
        }
    }

    public boolean checkPasscode(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
        if (fromPassword || (!AndroidUtilities.needShowPasscode(true) && !SharedConfig.isWaitingForPasscodeEnter)) {
            return true;
        }
        showPasscodeActivity();
        this.passcodeSaveIntent = intent;
        this.passcodeSaveIntentIsNew = isNew;
        this.passcodeSaveIntentIsRestore = restore;
        this.passcodeSaveIntentAccount = intentAccount;
        this.passcodeSaveIntentState = state;
        UserConfig.getInstance(intentAccount).saveConfig(false);
        return false;
    }

    protected boolean handleIntent(final Intent intent, final boolean isNew, final boolean restore, final boolean fromPassword, final int intentAccount, int state) {
        boolean z;
        if (!checkPasscode(intent, isNew, restore, fromPassword, intentAccount, state)) {
            return false;
        }
        if ("org.telegram.passport.AUTHORIZE".equals(intent.getAction())) {
            if (state == 0) {
                int activatedAccountsCount = UserConfig.getActivatedAccountsCount();
                if (activatedAccountsCount == 0) {
                    this.passcodeSaveIntent = intent;
                    this.passcodeSaveIntentIsNew = isNew;
                    this.passcodeSaveIntentIsRestore = restore;
                    this.passcodeSaveIntentAccount = intentAccount;
                    this.passcodeSaveIntentState = state;
                    LoginActivity fragment = new LoginActivity();
                    if (AndroidUtilities.isTablet()) {
                        this.layersActionBarLayout.addFragmentToStack(fragment);
                    } else {
                        this.actionBarLayout.addFragmentToStack(fragment);
                    }
                    if (!AndroidUtilities.isTablet()) {
                        this.backgroundTablet.setVisibility(8);
                    }
                    this.actionBarLayout.showLastFragment();
                    if (AndroidUtilities.isTablet()) {
                        this.layersActionBarLayout.showLastFragment();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("PleaseLoginPassport", R.string.PleaseLoginPassport));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.show();
                    return true;
                } else if (activatedAccountsCount >= 2) {
                    AlertDialog alertDialog = AlertsCreator.createAccountSelectDialog(this, new AlertsCreator.AccountSelectDelegate() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda10
                        @Override // org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate
                        public final void didSelectAccount(int i) {
                            ExternalActionActivity.this.m3410lambda$handleIntent$3$orgtelegramuiExternalActionActivity(intentAccount, intent, isNew, restore, fromPassword, i);
                        }
                    });
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda2
                        @Override // android.content.DialogInterface.OnDismissListener
                        public final void onDismiss(DialogInterface dialogInterface) {
                            ExternalActionActivity.this.m3411lambda$handleIntent$4$orgtelegramuiExternalActionActivity(dialogInterface);
                        }
                    });
                    return true;
                }
            }
            long bot_id = intent.getLongExtra("bot_id", intent.getIntExtra("bot_id", 0));
            final String nonce = intent.getStringExtra("nonce");
            final String payload = intent.getStringExtra("payload");
            final TLRPC.TL_account_getAuthorizationForm req = new TLRPC.TL_account_getAuthorizationForm();
            req.bot_id = bot_id;
            req.scope = intent.getStringExtra("scope");
            req.public_key = intent.getStringExtra("public_key");
            if (bot_id != 0) {
                if (!TextUtils.isEmpty(payload) || !TextUtils.isEmpty(nonce)) {
                    if (!TextUtils.isEmpty(req.scope)) {
                        if (!TextUtils.isEmpty(req.public_key)) {
                            final int[] requestId = {0};
                            final AlertDialog progressDialog = new AlertDialog(this, 3);
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda0
                                @Override // android.content.DialogInterface.OnCancelListener
                                public final void onCancel(DialogInterface dialogInterface) {
                                    ConnectionsManager.getInstance(intentAccount).cancelRequest(requestId[0], true);
                                }
                            });
                            progressDialog.show();
                            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda9
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    ExternalActionActivity.this.m3409lambda$handleIntent$10$orgtelegramuiExternalActionActivity(requestId, intentAccount, progressDialog, req, payload, nonce, tLObject, tL_error);
                                }
                            }, 10);
                            return false;
                        }
                    }
                }
                z = false;
                finish();
                return z;
            }
            z = false;
            finish();
            return z;
        }
        if (AndroidUtilities.isTablet()) {
            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                this.layersActionBarLayout.addFragmentToStack(new CacheControlActivity());
            }
        } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            this.actionBarLayout.addFragmentToStack(new CacheControlActivity());
        }
        if (!AndroidUtilities.isTablet()) {
            this.backgroundTablet.setVisibility(8);
        }
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
        }
        intent.setAction(null);
        return false;
    }

    /* renamed from: lambda$handleIntent$3$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3410lambda$handleIntent$3$orgtelegramuiExternalActionActivity(int intentAccount, Intent intent, boolean isNew, boolean restore, boolean fromPassword, int account) {
        if (account != intentAccount) {
            switchToAccount(account);
        }
        handleIntent(intent, isNew, restore, fromPassword, account, 1);
    }

    /* renamed from: lambda$handleIntent$4$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3411lambda$handleIntent$4$orgtelegramuiExternalActionActivity(DialogInterface dialog) {
        setResult(0);
        finish();
    }

    /* renamed from: lambda$handleIntent$10$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3409lambda$handleIntent$10$orgtelegramuiExternalActionActivity(int[] requestId, final int intentAccount, final AlertDialog progressDialog, final TLRPC.TL_account_getAuthorizationForm req, final String payload, final String nonce, TLObject response, final TLRPC.TL_error error) {
        final TLRPC.TL_account_authorizationForm authorizationForm = (TLRPC.TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            TLRPC.TL_account_getPassword req2 = new TLRPC.TL_account_getPassword();
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda8
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ExternalActionActivity.this.m3413lambda$handleIntent$7$orgtelegramuiExternalActionActivity(progressDialog, intentAccount, authorizationForm, req, payload, nonce, tLObject, tL_error);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ExternalActionActivity.this.m3415lambda$handleIntent$9$orgtelegramuiExternalActionActivity(progressDialog, error);
            }
        });
    }

    /* renamed from: lambda$handleIntent$7$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3413lambda$handleIntent$7$orgtelegramuiExternalActionActivity(final AlertDialog progressDialog, final int intentAccount, final TLRPC.TL_account_authorizationForm authorizationForm, final TLRPC.TL_account_getAuthorizationForm req, final String payload, final String nonce, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ExternalActionActivity.this.m3412lambda$handleIntent$6$orgtelegramuiExternalActionActivity(progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce);
            }
        });
    }

    /* renamed from: lambda$handleIntent$6$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3412lambda$handleIntent$6$orgtelegramuiExternalActionActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (response1 != null) {
            TLRPC.TL_account_password accountPassword = (TLRPC.TL_account_password) response1;
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            PassportActivity fragment = new PassportActivity(5, req.bot_id, req.scope, req.public_key, payload, nonce, (String) null, authorizationForm, accountPassword);
            fragment.setNeedActivityResult(true);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.addFragmentToStack(fragment);
            } else {
                this.actionBarLayout.addFragmentToStack(fragment);
            }
            if (!AndroidUtilities.isTablet()) {
                this.backgroundTablet.setVisibility(8);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
            }
        }
    }

    /* renamed from: lambda$handleIntent$9$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3415lambda$handleIntent$9$orgtelegramuiExternalActionActivity(AlertDialog progressDialog, final TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                AlertDialog dialog = AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                if (dialog != null) {
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ExternalActionActivity$$ExternalSyntheticLambda3
                        @Override // android.content.DialogInterface.OnDismissListener
                        public final void onDismiss(DialogInterface dialogInterface) {
                            ExternalActionActivity.this.m3414lambda$handleIntent$8$orgtelegramuiExternalActionActivity(error, dialogInterface);
                        }
                    });
                } else {
                    setResult(1, new Intent().putExtra(Constants.IPC_BUNDLE_KEY_SEND_ERROR, error.text));
                    finish();
                }
                return;
            }
            if (!"BOT_INVALID".equals(error.text) && !"PUBLIC_KEY_REQUIRED".equals(error.text) && !"PUBLIC_KEY_INVALID".equals(error.text) && !"SCOPE_EMPTY".equals(error.text) && !"PAYLOAD_EMPTY".equals(error.text)) {
                setResult(0);
                finish();
                return;
            }
            setResult(1, new Intent().putExtra(Constants.IPC_BUNDLE_KEY_SEND_ERROR, error.text));
            finish();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$handleIntent$8$org-telegram-ui-ExternalActionActivity */
    public /* synthetic */ void m3414lambda$handleIntent$8$orgtelegramuiExternalActionActivity(TLRPC.TL_error error, DialogInterface dialog1) {
        setResult(1, new Intent().putExtra(Constants.IPC_BUNDLE_KEY_SEND_ERROR, error.text));
        finish();
    }

    public void switchToAccount(int account) {
        if (account == UserConfig.selectedAccount) {
            return;
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(true, false);
        UserConfig.selectedAccount = account;
        UserConfig.getInstance(0).saveConfig(false);
        if (!ApplicationLoader.mainInterfacePaused) {
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(false, false);
        }
    }

    @Override // org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate
    public boolean onPreIme() {
        return false;
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false, UserConfig.selectedAccount, 0);
    }

    private void onFinish() {
        if (this.finished) {
            return;
        }
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        this.finished = true;
    }

    public void presentFragment(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public void needLayout() {
        if (AndroidUtilities.isTablet()) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) this.layersActionBarLayout.getLayoutParams();
            relativeLayoutParams.leftMargin = (AndroidUtilities.displaySize.x - relativeLayoutParams.width) / 2;
            int y = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            relativeLayoutParams.topMargin = (((AndroidUtilities.displaySize.y - relativeLayoutParams.height) - y) / 2) + y;
            this.layersActionBarLayout.setLayoutParams(relativeLayoutParams);
            if (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2) {
                int leftWidth = (AndroidUtilities.displaySize.x / 100) * 35;
                if (leftWidth < AndroidUtilities.dp(320.0f)) {
                    leftWidth = AndroidUtilities.dp(320.0f);
                }
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
                relativeLayoutParams2.width = leftWidth;
                relativeLayoutParams2.height = -1;
                this.actionBarLayout.setLayoutParams(relativeLayoutParams2);
                if (AndroidUtilities.isSmallTablet() && this.actionBarLayout.fragmentsStack.size() == 2) {
                    BaseFragment chatFragment = this.actionBarLayout.fragmentsStack.get(1);
                    chatFragment.onPause();
                    this.actionBarLayout.fragmentsStack.remove(1);
                    this.actionBarLayout.showLastFragment();
                    return;
                }
                return;
            }
            RelativeLayout.LayoutParams relativeLayoutParams3 = (RelativeLayout.LayoutParams) this.actionBarLayout.getLayoutParams();
            relativeLayoutParams3.width = -1;
            relativeLayoutParams3.height = -1;
            this.actionBarLayout.setLayoutParams(relativeLayoutParams3);
        }
    }

    public void fixLayout() {
        ActionBarLayout actionBarLayout;
        if (!AndroidUtilities.isTablet() || (actionBarLayout = this.actionBarLayout) == null) {
            return;
        }
        actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: org.telegram.ui.ExternalActionActivity.3
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                ExternalActionActivity.this.needLayout();
                if (ExternalActionActivity.this.actionBarLayout != null) {
                    ExternalActionActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onPause();
        }
        ApplicationLoader.externalInterfacePaused = true;
        onPasscodePause();
        PasscodeView passcodeView = this.passcodeView;
        if (passcodeView != null) {
            passcodeView.onPause();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        onFinish();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.actionBarLayout.onResume();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onResume();
        }
        ApplicationLoader.externalInterfacePaused = false;
        onPasscodeResume();
        if (this.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.onResume();
                return;
            }
            return;
        }
        this.actionBarLayout.dismissDialogs();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.dismissDialogs();
        }
        this.passcodeView.onResume();
    }

    private void onPasscodePause() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            this.lockRunnable = new Runnable() { // from class: org.telegram.ui.ExternalActionActivity.4
                @Override // java.lang.Runnable
                public void run() {
                    if (ExternalActionActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            ExternalActionActivity.this.showPasscodeActivity();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        ExternalActionActivity.this.lockRunnable = null;
                    }
                }
            };
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000L);
            } else if (SharedConfig.autoLockIn != 0) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, (SharedConfig.autoLockIn * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    private void onPasscodeResume() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity();
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (AndroidUtilities.isTablet()) {
            if (this.layersActionBarLayout.getVisibility() == 0) {
                this.layersActionBarLayout.onBackPressed();
            } else {
                this.actionBarLayout.onBackPressed();
            }
        } else {
            this.actionBarLayout.onBackPressed();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.onLowMemory();
        }
    }

    @Override // org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate
    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate
    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate
    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == this.actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate
    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        if (AndroidUtilities.isTablet() && layout == this.layersActionBarLayout) {
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }
}
