package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
/* loaded from: classes4.dex */
public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private SimpleTextView bottomButton;
    private TextView bottomTextView;
    private TextView cancelResetButton;
    private int changePasswordRow;
    private int changeRecoveryEmailRow;
    private TLRPC.TL_account_password currentPassword;
    private byte[] currentPasswordHash;
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    private String email;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    private Runnable errorColorTimeout;
    private String firstPassword;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private boolean forgotPasswordOnShow;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private RLottieImageView lockImageView;
    int otherwiseReloginDays;
    private EditTextBoldCursor passwordEditText;
    private int passwordEnabledDetailRow;
    private boolean passwordEntered;
    private OutlineTextContainerView passwordOutlineView;
    private boolean paused;
    private boolean postedErrorColorTimeout;
    private AlertDialog progressDialog;
    private RadialProgressView radialProgressView;
    private boolean resetPasswordOnShow;
    private TextView resetWaitView;
    private int rowCount;
    private ScrollView scrollView;
    private int setPasswordDetailRow;
    private int setPasswordRow;
    private int setRecoveryEmailRow;
    private TextView subtitleTextView;
    private TextView titleTextView;
    private int turnPasswordOffRow;
    private Runnable updateTimeRunnable;

    /* loaded from: classes4.dex */
    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4699lambda$new$0$orgtelegramuiTwoStepVerificationActivity() {
        this.postedErrorColorTimeout = false;
        this.passwordOutlineView.animateError(0.0f);
    }

    public TwoStepVerificationActivity() {
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.errorColorTimeout = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4699lambda$new$0$orgtelegramuiTwoStepVerificationActivity();
            }
        };
        this.otherwiseReloginDays = -1;
        this.updateTimeRunnable = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.updateBottomButton();
            }
        };
    }

    public TwoStepVerificationActivity(int account) {
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.errorColorTimeout = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4699lambda$new$0$orgtelegramuiTwoStepVerificationActivity();
            }
        };
        this.otherwiseReloginDays = -1;
        this.updateTimeRunnable = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.updateBottomButton();
            }
        };
        this.currentAccount = account;
    }

    public void setPassword(TLRPC.TL_account_password password) {
        this.currentPassword = password;
        this.passwordEntered = false;
    }

    public void setCurrentPasswordParams(TLRPC.TL_account_password password, byte[] passwordHash, long secretId, byte[] secret) {
        this.currentPassword = password;
        this.currentPasswordHash = passwordHash;
        this.currentSecret = secret;
        this.currentSecretId = secretId;
        this.passwordEntered = (passwordHash != null && passwordHash.length > 0) || !password.has_password;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        byte[] bArr;
        super.onFragmentCreate();
        TLRPC.TL_account_password tL_account_password = this.currentPassword;
        if (tL_account_password == null || tL_account_password.current_algo == null || (bArr = this.currentPasswordHash) == null || bArr.length <= 0) {
            loadPasswordInfo(true, this.currentPassword != null);
        }
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        this.destroyed = true;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (!this.passwordEntered) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
            this.actionBar.setCastShadows(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.TwoStepVerificationActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (TwoStepVerificationActivity.this.otherwiseReloginDays >= 0) {
                        TwoStepVerificationActivity.this.showSetForcePasswordAlert();
                    } else {
                        TwoStepVerificationActivity.this.finishFragment();
                    }
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.lockImageView = rLottieImageView;
        rLottieImageView.setAnimation(R.raw.tsv_setup_intro, 120, 120);
        this.lockImageView.playAnimation();
        this.lockImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) ? 8 : 0);
        linearLayout.addView(this.lockImageView, LayoutHelper.createLinear(120, 120, 1));
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 24, 8, 24, 0));
        TextView textView2 = new TextView(context);
        this.subtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setGravity(1);
        this.subtitleTextView.setVisibility(8);
        linearLayout.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, 1, 24, 8, 24, 0));
        OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
        this.passwordOutlineView = outlineTextContainerView;
        outlineTextContainerView.setText(LocaleController.getString((int) R.string.EnterPassword));
        this.passwordOutlineView.animateSelection(1.0f, false);
        linearLayout.addView(this.passwordOutlineView, LayoutHelper.createLinear(-1, -2, 1, 24, 24, 24, 0));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.passwordEditText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.passwordEditText.setBackground(null);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
        this.passwordEditText.setCursorWidth(1.5f);
        this.passwordEditText.setContentDescription(LocaleController.getString((int) R.string.EnterPassword));
        int padding = AndroidUtilities.dp(16.0f);
        this.passwordEditText.setPadding(padding, padding, padding, padding);
        this.passwordOutlineView.addView(this.passwordEditText, LayoutHelper.createFrame(-1, -2.0f));
        this.passwordOutlineView.attachEditText(this.passwordEditText);
        this.passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda38
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                TwoStepVerificationActivity.this.m4690lambda$createView$1$orgtelegramuiTwoStepVerificationActivity(view, z);
            }
        });
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda1
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                return TwoStepVerificationActivity.this.m4691lambda$createView$2$orgtelegramuiTwoStepVerificationActivity(textView3, i, keyEvent);
            }
        });
        this.passwordEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.TwoStepVerificationActivity.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (TwoStepVerificationActivity.this.postedErrorColorTimeout) {
                    AndroidUtilities.cancelRunOnUIThread(TwoStepVerificationActivity.this.errorColorTimeout);
                    TwoStepVerificationActivity.this.errorColorTimeout.run();
                }
            }
        });
        TextView textView3 = new TextView(context);
        this.bottomTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", R.string.YourEmailInfo));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        TextView textView4 = new TextView(context);
        this.resetWaitView = textView4;
        textView4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.resetWaitView.setTextSize(1, 12.0f);
        this.resetWaitView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        linearLayout.addView(this.resetWaitView, LayoutHelper.createLinear(-1, -2, 40.0f, 8.0f, 40.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(80);
        linearLayout2.setClipChildren(false);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, 0, 1.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.bottomButton = simpleTextView;
        simpleTextView.setTextSize(15);
        this.bottomButton.setGravity(19);
        this.bottomButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        frameLayout.addView(this.bottomButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
        this.bottomButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationActivity.this.m4692lambda$createView$3$orgtelegramuiTwoStepVerificationActivity(view);
            }
        });
        VerticalPositionAutoAnimator.attach(this.bottomButton);
        TextView textView5 = new TextView(context);
        this.cancelResetButton = textView5;
        textView5.setTextSize(1, 15.0f);
        this.cancelResetButton.setGravity(19);
        this.cancelResetButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.cancelResetButton.setText(LocaleController.getString("CancelReset", R.string.CancelReset));
        this.cancelResetButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.cancelResetButton.setVisibility(8);
        frameLayout.addView(this.cancelResetButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
        this.cancelResetButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda36
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationActivity.this.m4693lambda$createView$4$orgtelegramuiTwoStepVerificationActivity(view);
            }
        });
        VerticalPositionAutoAnimator.attach(this.cancelResetButton);
        this.floatingButtonContainer = new FrameLayout(context);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.TwoStepVerificationActivity.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda37
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationActivity.this.m4694lambda$createView$5$orgtelegramuiTwoStepVerificationActivity(view);
            }
        });
        TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(context);
        this.floatingButtonIcon = transformableLoginButtonView;
        transformableLoginButtonView.setTransformType(1);
        this.floatingButtonIcon.setProgress(0.0f);
        this.floatingButtonIcon.setColor(Theme.getColor(Theme.key_chats_actionIcon));
        this.floatingButtonIcon.setDrawBackground(false);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString((int) R.string.Next));
        this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
        frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda31
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                TwoStepVerificationActivity.this.m4696lambda$createView$7$orgtelegramuiTwoStepVerificationActivity(view, i);
            }
        });
        RadialProgressView radialProgressView = new RadialProgressView(context) { // from class: org.telegram.ui.TwoStepVerificationActivity.4
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
                params.topMargin = AndroidUtilities.statusBarHeight / 2;
            }
        };
        this.radialProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(20.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setProgressColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
        this.actionBar.addView(this.radialProgressView, LayoutHelper.createFrame(32, 32.0f, 21, 0.0f, 0.0f, 12.0f, 0.0f));
        updateRows();
        if (this.passwordEntered) {
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle));
        } else {
            this.actionBar.setTitle(null);
        }
        if (this.delegate != null) {
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPasswordTransfer", R.string.PleaseEnterCurrentPasswordTransfer));
        } else {
            this.titleTextView.setText(LocaleController.getString((int) R.string.YourPassword));
            this.subtitleTextView.setVisibility(0);
            this.subtitleTextView.setText(LocaleController.getString((int) R.string.LoginPasswordTextShort));
        }
        if (this.passwordEntered) {
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4690lambda$createView$1$orgtelegramuiTwoStepVerificationActivity(View v, boolean hasFocus) {
        this.passwordOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ boolean m4691lambda$createView$2$orgtelegramuiTwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5 || i == 6) {
            processDone();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4692lambda$createView$3$orgtelegramuiTwoStepVerificationActivity(View v) {
        onPasswordForgot();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4693lambda$createView$4$orgtelegramuiTwoStepVerificationActivity(View v) {
        cancelPasswordReset();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4694lambda$createView$5$orgtelegramuiTwoStepVerificationActivity(View view) {
        processDone();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4696lambda$createView$7$orgtelegramuiTwoStepVerificationActivity(View view, int position) {
        if (position == this.setPasswordRow || position == this.changePasswordRow) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            fragment.addFragmentToClose(this);
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(fragment);
        } else if (position == this.setRecoveryEmailRow || position == this.changeRecoveryEmailRow) {
            TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            fragment2.addFragmentToClose(this);
            fragment2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            presentFragment(fragment2);
        } else if (position == this.turnPasswordOffRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            String text = LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion);
            if (this.currentPassword.has_secure_values) {
                text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", R.string.TurnPasswordOffPassport);
            }
            String title = LocaleController.getString("TurnPasswordOffQuestionTitle", R.string.TurnPasswordOffQuestionTitle);
            String buttonText = LocaleController.getString("Disable", R.string.Disable);
            builder.setMessage(text);
            builder.setTitle(title);
            builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.m4695lambda$createView$6$orgtelegramuiTwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4695lambda$createView$6$orgtelegramuiTwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        clearPassword();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.lockImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) ? 8 : 0);
    }

    private void cancelPasswordReset() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("CancelPasswordResetYes", R.string.CancelPasswordResetYes), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.m4680xec9f1fee(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("CancelPasswordResetNo", R.string.CancelPasswordResetNo), null);
        builder.setTitle(LocaleController.getString("CancelReset", R.string.CancelReset));
        builder.setMessage(LocaleController.getString("CancelPasswordReset", R.string.CancelPasswordReset));
        showDialog(builder.create());
    }

    /* renamed from: lambda$cancelPasswordReset$10$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4680xec9f1fee(DialogInterface dialog, int which) {
        TLRPC.TL_account_declinePasswordReset req = new TLRPC.TL_account_declinePasswordReset();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda20
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.m4682x90dee49a(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$cancelPasswordReset$9$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4682x90dee49a(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4681xf63e2219(response);
            }
        });
    }

    /* renamed from: lambda$cancelPasswordReset$8$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4681xf63e2219(TLObject response) {
        if (response instanceof TLRPC.TL_boolTrue) {
            this.currentPassword.pending_reset_date = 0;
            updateBottomButton();
        }
    }

    public void setForgotPasswordOnShow() {
        this.forgotPasswordOnShow = true;
    }

    private void resetPassword() {
        needShowProgress(true);
        TLRPC.TL_account_resetPassword req = new TLRPC.TL_account_resetPassword();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda27
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.m4714x681c141(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$resetPassword$13$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4714x681c141(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4713x6be0fec0(response);
            }
        });
    }

    /* renamed from: lambda$resetPassword$12$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4713x6be0fec0(TLObject response) {
        String timeString;
        needHideProgress();
        if (response instanceof TLRPC.TL_account_resetPasswordOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
            builder.setTitle(LocaleController.getString("ResetPassword", R.string.ResetPassword));
            builder.setMessage(LocaleController.getString("RestorePasswordResetPasswordOk", R.string.RestorePasswordResetPasswordOk));
            showDialog(builder.create(), new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda34
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    TwoStepVerificationActivity.this.m4712xd1403c3f(dialogInterface);
                }
            });
        } else if (response instanceof TLRPC.TL_account_resetPasswordRequestedWait) {
            TLRPC.TL_account_resetPasswordRequestedWait res = (TLRPC.TL_account_resetPasswordRequestedWait) response;
            this.currentPassword.pending_reset_date = res.until_date;
            updateBottomButton();
        } else if (response instanceof TLRPC.TL_account_resetPasswordFailedWait) {
            TLRPC.TL_account_resetPasswordFailedWait res2 = (TLRPC.TL_account_resetPasswordFailedWait) response;
            int time = res2.retry_date - getConnectionsManager().getCurrentTime();
            if (time > 86400) {
                timeString = LocaleController.formatPluralString("Days", time / 86400, new Object[0]);
            } else if (time > 3600) {
                timeString = LocaleController.formatPluralString("Hours", time / 86400, new Object[0]);
            } else if (time > 60) {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Seconds", Math.max(1, time), new Object[0]);
            }
            showAlertWithText(LocaleController.getString("ResetPassword", R.string.ResetPassword), LocaleController.formatString("ResetPasswordWait", R.string.ResetPasswordWait, timeString));
        }
    }

    /* renamed from: lambda$resetPassword$11$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4712xd1403c3f(DialogInterface dialog) {
        getNotificationCenter().postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
        finishFragment();
    }

    public void updateBottomButton() {
        String time;
        if (this.passwordEntered) {
            return;
        }
        if (this.currentPassword.pending_reset_date == 0 || getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
            if (this.resetWaitView.getVisibility() != 8) {
                this.resetWaitView.setVisibility(8);
            }
            if (this.currentPassword.pending_reset_date == 0) {
                this.bottomButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
                this.cancelResetButton.setVisibility(8);
                this.bottomButton.setVisibility(0);
            } else {
                this.bottomButton.setText(LocaleController.getString("ResetPassword", R.string.ResetPassword));
                this.cancelResetButton.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.bottomButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        } else {
            int t = Math.max(1, this.currentPassword.pending_reset_date - getConnectionsManager().getCurrentTime());
            if (t > 86400) {
                time = LocaleController.formatPluralString("Days", t / 86400, new Object[0]);
            } else if (t >= 3600) {
                time = LocaleController.formatPluralString("Hours", t / 3600, new Object[0]);
            } else {
                time = String.format(Locale.US, "%02d:%02d", Integer.valueOf(t / 60), Integer.valueOf(t % 60));
            }
            this.resetWaitView.setText(LocaleController.formatString("RestorePasswordResetIn", R.string.RestorePasswordResetIn, time));
            this.resetWaitView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            if (this.bottomButton.getVisibility() != 8) {
                this.bottomButton.setVisibility(8);
            }
            if (this.resetWaitView.getVisibility() != 0) {
                this.resetWaitView.setVisibility(0);
            }
            this.cancelResetButton.setVisibility(0);
            AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
            AndroidUtilities.runOnUIThread(this.updateTimeRunnable, 1000L);
        }
        if (this.currentPassword == null || this.bottomButton == null || this.resetWaitView.getVisibility() != 0) {
            AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
            TextView textView = this.cancelResetButton;
            if (textView != null) {
                textView.setVisibility(8);
            }
        }
    }

    private void onPasswordForgot() {
        if (this.currentPassword.pending_reset_date == 0 && this.currentPassword.has_recovery) {
            needShowProgress(true);
            TLRPC.TL_auth_requestPasswordRecovery req = new TLRPC.TL_auth_requestPasswordRecovery();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda25
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.this.m4702xda2170f6(tLObject, tL_error);
                }
            }, 10);
        } else if (getParentActivity() == null) {
        } else {
            if (this.currentPassword.pending_reset_date != 0) {
                if (getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda22
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationActivity.this.m4703x74c23377(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.setTitle(LocaleController.getString("ResetPassword", R.string.ResetPassword));
                    builder.setMessage(LocaleController.getString("RestorePasswordResetPasswordText", R.string.RestorePasswordResetPasswordText));
                    AlertDialog dialog = builder.create();
                    showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                        return;
                    }
                    return;
                }
                cancelPasswordReset();
                return;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda32
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.m4704xf62f5f8(dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder2.setTitle(LocaleController.getString("ResetPassword", R.string.ResetPassword));
            builder2.setMessage(LocaleController.getString("RestorePasswordNoEmailText2", R.string.RestorePasswordNoEmailText2));
            showDialog(builder2.create());
        }
    }

    /* renamed from: lambda$onPasswordForgot$15$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4702xda2170f6(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4701x3f80ae75(error, response);
            }
        });
    }

    /* renamed from: lambda$onPasswordForgot$14$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4701x3f80ae75(TLRPC.TL_error error, TLObject response) {
        String timeString;
        needHideProgress();
        if (error == null) {
            TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
            this.currentPassword.email_unconfirmed_pattern = res.email_pattern;
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 4, this.currentPassword) { // from class: org.telegram.ui.TwoStepVerificationActivity.5
                @Override // org.telegram.ui.TwoStepVerificationSetupActivity
                protected void onReset() {
                    TwoStepVerificationActivity.this.resetPasswordOnShow = true;
                }
            };
            fragment.addFragmentToClose(this);
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(fragment);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt((CharSequence) error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    /* renamed from: lambda$onPasswordForgot$16$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4703x74c23377(DialogInterface dialog, int which) {
        resetPassword();
    }

    /* renamed from: lambda$onPasswordForgot$17$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4704xf62f5f8(DialogInterface dialog, int which) {
        resetPassword();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.twoStepPasswordChanged) {
            if (args != null && args.length > 0 && args[0] != null) {
                this.currentPasswordHash = (byte[]) args[0];
            }
            loadPasswordInfo(false, false);
            updateRows();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        this.paused = false;
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void setCurrentPasswordInfo(byte[] hash, TLRPC.TL_account_password password) {
        if (hash != null) {
            this.currentPasswordHash = hash;
        }
        this.currentPassword = password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public static boolean canHandleCurrentPassword(TLRPC.TL_account_password password, boolean login) {
        if (login) {
            if (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) {
                return false;
            }
            return true;
        } else if ((password.new_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initPasswordNewAlgo(TLRPC.TL_account_password password) {
        if (password.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) password.new_algo;
            byte[] salt = new byte[algo.salt1.length + 32];
            Utilities.random.nextBytes(salt);
            System.arraycopy(algo.salt1, 0, salt, 0, algo.salt1.length);
            algo.salt1 = salt;
        }
        if (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) password.new_secure_algo;
            byte[] salt2 = new byte[algo2.salt.length + 32];
            Utilities.random.nextBytes(salt2);
            System.arraycopy(algo2.salt, 0, salt2, 0, algo2.salt.length);
            algo2.salt = salt2;
        }
    }

    private void loadPasswordInfo(final boolean first, final boolean silent) {
        if (!silent) {
            this.loading = true;
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
        TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda28
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.m4698x983e2028(silent, first, tLObject, tL_error);
            }
        }, 10);
    }

    /* renamed from: lambda$loadPasswordInfo$19$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4698x983e2028(final boolean silent, final boolean first, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4697xfd9d5da7(error, response, silent, first);
            }
        });
    }

    /* renamed from: lambda$loadPasswordInfo$18$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4697xfd9d5da7(TLRPC.TL_error error, TLObject response, boolean silent, boolean first) {
        if (error == null) {
            this.loading = false;
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            if (!silent || first) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            initPasswordNewAlgo(this.currentPassword);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
        updateRows();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen) {
            if (this.forgotPasswordOnShow) {
                onPasswordForgot();
                this.forgotPasswordOnShow = false;
            } else if (this.resetPasswordOnShow) {
                resetPassword();
                this.resetPasswordOnShow = false;
            }
        }
    }

    private void updateRows() {
        TLRPC.TL_account_password tL_account_password;
        StringBuilder lastValue = new StringBuilder();
        lastValue.append(this.setPasswordRow);
        lastValue.append(this.setPasswordDetailRow);
        lastValue.append(this.changePasswordRow);
        lastValue.append(this.turnPasswordOffRow);
        lastValue.append(this.setRecoveryEmailRow);
        lastValue.append(this.changeRecoveryEmailRow);
        lastValue.append(this.passwordEnabledDetailRow);
        lastValue.append(this.rowCount);
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.passwordEnabledDetailRow = -1;
        if (!this.loading && (tL_account_password = this.currentPassword) != null && this.passwordEntered) {
            if (tL_account_password.has_password) {
                int i = this.rowCount;
                int i2 = i + 1;
                this.rowCount = i2;
                this.changePasswordRow = i;
                this.rowCount = i2 + 1;
                this.turnPasswordOffRow = i2;
                if (this.currentPassword.has_recovery) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.changeRecoveryEmailRow = i3;
                } else {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.setRecoveryEmailRow = i4;
                }
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.passwordEnabledDetailRow = i5;
            } else {
                int i6 = this.rowCount;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.setPasswordRow = i6;
                this.rowCount = i7 + 1;
                this.setPasswordDetailRow = i7;
            }
        }
        StringBuilder newValue = new StringBuilder();
        newValue.append(this.setPasswordRow);
        newValue.append(this.setPasswordDetailRow);
        newValue.append(this.changePasswordRow);
        newValue.append(this.turnPasswordOffRow);
        newValue.append(this.setRecoveryEmailRow);
        newValue.append(this.changeRecoveryEmailRow);
        newValue.append(this.passwordEnabledDetailRow);
        newValue.append(this.rowCount);
        if (this.listAdapter != null && !lastValue.toString().equals(newValue.toString())) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.fragmentView != null) {
            if (this.loading || this.passwordEntered) {
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null) {
                    recyclerListView.setVisibility(0);
                    this.scrollView.setVisibility(4);
                    this.listView.setEmptyView(this.emptyView);
                }
                if (this.passwordEditText != null) {
                    this.floatingButtonContainer.setVisibility(8);
                    this.passwordEditText.setVisibility(4);
                    this.titleTextView.setVisibility(4);
                    this.bottomTextView.setVisibility(8);
                    this.bottomButton.setVisibility(4);
                    updateBottomButton();
                }
                this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                this.fragmentView.setTag(Theme.key_windowBackgroundGray);
                return;
            }
            RecyclerListView recyclerListView2 = this.listView;
            if (recyclerListView2 != null) {
                recyclerListView2.setEmptyView(null);
                this.listView.setVisibility(4);
                this.scrollView.setVisibility(0);
                this.emptyView.setVisibility(4);
            }
            if (this.passwordEditText != null) {
                this.floatingButtonContainer.setVisibility(0);
                this.passwordEditText.setVisibility(0);
                this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
                this.titleTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
                updateBottomButton();
                this.bottomTextView.setVisibility(8);
                if (!TextUtils.isEmpty(this.currentPassword.hint)) {
                    this.passwordEditText.setHint(this.currentPassword.hint);
                } else {
                    this.passwordEditText.setHint((CharSequence) null);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        TwoStepVerificationActivity.this.m4716lambda$updateRows$20$orgtelegramuiTwoStepVerificationActivity();
                    }
                }, 200L);
            }
        }
    }

    /* renamed from: lambda$updateRows$20$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4716lambda$updateRows$20$orgtelegramuiTwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void needShowProgress() {
        needShowProgress(false);
    }

    private void needShowProgress(boolean delay) {
        if (getParentActivity() == null || getParentActivity().isFinishing() || this.progressDialog != null) {
            return;
        }
        if (!this.passwordEntered) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, 1.0f));
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.start();
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        if (delay) {
            this.progressDialog.showDelayed(300L);
        } else {
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        if (!this.passwordEntered) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, 0.1f));
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.start();
            return;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog == null) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.progressDialog = null;
    }

    private void showAlertWithText(String title, String text) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void clearPassword() {
        String str = this.firstPassword;
        final TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            req.password = new TLRPC.TL_inputCheckPasswordEmpty();
        }
        req.new_settings = new TLRPC.TL_account_passwordInputSettings();
        UserConfig.getInstance(this.currentAccount).resetSavedPassword();
        this.currentSecret = null;
        req.new_settings.flags = 3;
        req.new_settings.hint = "";
        req.new_settings.new_password_hash = new byte[0];
        req.new_settings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
        req.new_settings.email = "";
        needShowProgress();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4689xf787fa26(req);
            }
        });
    }

    /* renamed from: lambda$clearPassword$27$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4689xf787fa26(TLRPC.TL_account_updatePasswordSettings req) {
        if (req.password == null) {
            if (this.currentPassword.current_algo == null) {
                TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda21
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationActivity.this.m4684xf2642da1(tLObject, tL_error);
                    }
                }, 8);
                return;
            }
            req.password = getNewSrpPassword();
        }
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda24
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.m4688x5ce737a5(tLObject, tL_error);
            }
        };
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
    }

    /* renamed from: lambda$clearPassword$22$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4684xf2642da1(final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4683x57c36b20(error2, response2);
            }
        });
    }

    /* renamed from: lambda$clearPassword$21$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4683x57c36b20(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    /* renamed from: lambda$clearPassword$26$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4688x5ce737a5(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4687xc2467524(error, response);
            }
        });
    }

    /* renamed from: lambda$clearPassword$25$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4687xc2467524(TLRPC.TL_error error, TLObject response) {
        String timeString;
        if (error != null && "SRP_ID_INVALID".equals(error.text)) {
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda23
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.this.m4686x27a5b2a3(tLObject, tL_error);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
            this.currentPassword = null;
            this.currentPasswordHash = new byte[0];
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
            finishFragment();
        } else if (error != null) {
            if (error.text.startsWith("FLOOD_WAIT")) {
                int time = Utilities.parseInt((CharSequence) error.text).intValue();
                if (time >= 60) {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                } else {
                    timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                }
                showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                return;
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    /* renamed from: lambda$clearPassword$24$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4686x27a5b2a3(final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4685x8d04f022(error2, response2);
            }
        });
    }

    /* renamed from: lambda$clearPassword$23$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4685x8d04f022(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, algo);
        }
        return null;
    }

    private boolean checkSecretValues(byte[] passwordBytes, TLRPC.TL_account_passwordSettings passwordSettings) {
        byte[] passwordHash;
        if (passwordSettings.secure_settings != null) {
            this.currentSecret = passwordSettings.secure_settings.secure_secret;
            if (passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                passwordHash = Utilities.computePBKDF2(passwordBytes, ((TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) passwordSettings.secure_settings.secure_algo).salt);
            } else if (!(passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                TLRPC.TL_securePasswordKdfAlgoSHA512 algo = (TLRPC.TL_securePasswordKdfAlgoSHA512) passwordSettings.secure_settings.secure_algo;
                passwordHash = Utilities.computeSHA512(algo.salt, passwordBytes, algo.salt);
            }
            this.currentSecretId = passwordSettings.secure_settings.secure_secret_id;
            byte[] key = new byte[32];
            System.arraycopy(passwordHash, 0, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(passwordHash, 32, iv, 0, 16);
            byte[] bArr = this.currentSecret;
            Utilities.aesCbcEncryptionByteArraySafe(bArr, key, iv, 0, bArr.length, 0, 0);
            if (!PassportActivity.checkSecret(passwordSettings.secure_settings.secure_secret, Long.valueOf(passwordSettings.secure_settings.secure_secret_id))) {
                TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
                req.password = getNewSrpPassword();
                req.new_settings = new TLRPC.TL_account_passwordInputSettings();
                req.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                req.new_settings.new_secure_settings.secure_secret = new byte[0];
                req.new_settings.new_secure_settings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
                req.new_settings.new_secure_settings.secure_secret_id = 0L;
                req.new_settings.flags |= 4;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TwoStepVerificationActivity$$ExternalSyntheticLambda30.INSTANCE);
                this.currentSecret = null;
                this.currentSecretId = 0L;
                return true;
            }
            return true;
        }
        this.currentSecret = null;
        this.currentSecretId = 0L;
        return true;
    }

    public static /* synthetic */ void lambda$checkSecretValues$28(TLObject response, TLRPC.TL_error error) {
    }

    private void processDone() {
        if (!this.passwordEntered) {
            String oldPassword = this.passwordEditText.getText().toString();
            if (oldPassword.length() == 0) {
                onFieldError(this.passwordOutlineView, this.passwordEditText, false);
                return;
            }
            final byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
            needShowProgress();
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationActivity.this.m4711x8a83005a(oldPasswordBytes);
                }
            });
        }
    }

    /* renamed from: lambda$processDone$35$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4711x8a83005a(final byte[] oldPasswordBytes) {
        final byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            x_bytes = SRPHelper.getX(oldPasswordBytes, algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda29
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivity.this.m4710xefe23dd9(oldPasswordBytes, x_bytes, tLObject, tL_error);
            }
        };
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo2 = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, algo2);
            if (req.password == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run(null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error2);
    }

    /* renamed from: lambda$processDone$34$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4710xefe23dd9(final byte[] oldPasswordBytes, final byte[] x_bytes, final TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationActivity.this.m4706x855f33d5(oldPasswordBytes, response, x_bytes);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationActivity.this.m4709x55417b58(error);
                }
            });
        }
    }

    /* renamed from: lambda$processDone$30$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4706x855f33d5(byte[] oldPasswordBytes, TLObject response, final byte[] x_bytes) {
        final boolean secretOk = checkSecretValues(oldPasswordBytes, (TLRPC.TL_account_passwordSettings) response);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4705x3b8e7cbf(secretOk, x_bytes);
            }
        });
    }

    /* renamed from: lambda$processDone$29$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4705x3b8e7cbf(boolean secretOk, byte[] x_bytes) {
        if (this.delegate == null || !secretOk) {
            needHideProgress();
        }
        if (!secretOk) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
            return;
        }
        this.currentPasswordHash = x_bytes;
        this.passwordEntered = true;
        if (this.delegate != null) {
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            this.delegate.didEnterPassword(getNewSrpPassword());
        } else if (!TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 5, this.currentPassword);
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            presentFragment(fragment, true);
        } else {
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            TwoStepVerificationActivity fragment2 = new TwoStepVerificationActivity();
            fragment2.passwordEntered = true;
            fragment2.currentPasswordHash = this.currentPasswordHash;
            fragment2.currentPassword = this.currentPassword;
            fragment2.currentSecret = this.currentSecret;
            fragment2.currentSecretId = this.currentSecretId;
            presentFragment(fragment2, true);
        }
    }

    /* renamed from: lambda$processDone$33$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4709x55417b58(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda26
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivity.this.m4708xbaa0b8d7(tLObject, tL_error);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            onFieldError(this.passwordOutlineView, this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt((CharSequence) error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    /* renamed from: lambda$processDone$32$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4708xbaa0b8d7(final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4707x1ffff656(error2, response2);
            }
        });
    }

    /* renamed from: lambda$processDone$31$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4707x1ffff656(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processDone();
        }
    }

    private void onFieldError(OutlineTextContainerView outlineView, TextView field, boolean clear) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            field.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        if (clear) {
            field.setText("");
        }
        outlineView.animateError(1.0f);
        AndroidUtilities.shakeViewSpring(outlineView, 5.0f, new Runnable() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationActivity.this.m4700x551b7b25();
            }
        });
    }

    /* renamed from: lambda$onFieldError$36$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4700x551b7b25() {
        AndroidUtilities.cancelRunOnUIThread(this.errorColorTimeout);
        AndroidUtilities.runOnUIThread(this.errorColorTimeout, 1500L);
        this.postedErrorColorTimeout = true;
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            TwoStepVerificationActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position != TwoStepVerificationActivity.this.changePasswordRow) {
                        if (position != TwoStepVerificationActivity.this.setPasswordRow) {
                            if (position != TwoStepVerificationActivity.this.turnPasswordOffRow) {
                                if (position != TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                                    if (position == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                                        textCell.setText(LocaleController.getString("SetRecoveryEmail", R.string.SetRecoveryEmail), false);
                                        return;
                                    }
                                    return;
                                }
                                textCell.setText(LocaleController.getString("ChangeRecoveryEmail", R.string.ChangeRecoveryEmail), false);
                                return;
                            }
                            textCell.setText(LocaleController.getString("TurnPasswordOff", R.string.TurnPasswordOff), true);
                            return;
                        }
                        textCell.setText(LocaleController.getString("SetAdditionalPassword", R.string.SetAdditionalPassword), true);
                        return;
                    }
                    textCell.setText(LocaleController.getString("ChangePassword", R.string.ChangePassword), true);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != TwoStepVerificationActivity.this.setPasswordDetailRow) {
                        if (position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                            privacyCell.setText(LocaleController.getString("EnabledPasswordText", R.string.EnabledPasswordText));
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        return;
                    }
                    privacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", R.string.SetAdditionalPasswordInfo));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == TwoStepVerificationActivity.this.setPasswordDetailRow || position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                return 1;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText3));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        return themeDescriptions;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.otherwiseReloginDays >= 0) {
            showSetForcePasswordAlert();
            return false;
        }
        return super.onBackPressed();
    }

    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", R.string.Warning));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays, new Object[0]));
        builder.setPositiveButton(LocaleController.getString("TwoStepVerificationSetPassword", R.string.TwoStepVerificationSetPassword), null);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", R.string.ForceSetPasswordCancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationActivity$$ExternalSyntheticLambda33
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.m4715xa71edc92(dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.show();
        ((TextView) alertDialog.getButton(-2)).setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
    }

    /* renamed from: lambda$showSetForcePasswordAlert$37$org-telegram-ui-TwoStepVerificationActivity */
    public /* synthetic */ void m4715xa71edc92(DialogInterface a1, int a2) {
        finishFragment();
    }

    public void setBlockingAlert(int otherwiseRelogin) {
        this.otherwiseReloginDays = otherwiseRelogin;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void finishFragment() {
        if (this.otherwiseReloginDays >= 0) {
            Bundle args = new Bundle();
            args.putBoolean("afterSignup", true);
            presentFragment(new DialogsActivity(args), true);
            return;
        }
        super.finishFragment();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
