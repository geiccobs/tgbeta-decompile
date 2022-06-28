package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.TwoStepVerificationSetupActivity;
/* loaded from: classes4.dex */
public class TwoStepVerificationSetupActivity extends BaseFragment {
    public static final int TYPE_CREATE_PASSWORD_STEP_1 = 0;
    public static final int TYPE_CREATE_PASSWORD_STEP_2 = 1;
    public static final int TYPE_EMAIL_CONFIRM = 5;
    public static final int TYPE_EMAIL_RECOVERY = 4;
    public static final int TYPE_ENTER_EMAIL = 3;
    public static final int TYPE_ENTER_HINT = 2;
    public static final int TYPE_INTRO = 6;
    public static final int TYPE_PASSWORD_SET = 7;
    public static final int TYPE_VERIFY = 8;
    public static final int TYPE_VERIFY_OK = 9;
    private static final int item_abort = 1;
    private AnimatorSet actionBarAnimator;
    private View actionBarBackground;
    private RLottieDrawable[] animationDrawables;
    private TextView bottomSkipButton;
    private AnimatorSet buttonAnimation;
    private TextView buttonTextView;
    private boolean closeAfterSet;
    private CodeFieldContainer codeFieldContainer;
    private TLRPC.TL_account_password currentPassword;
    private byte[] currentPasswordHash;
    private byte[] currentSecret;
    private long currentSecretId;
    private int currentType;
    private TextView descriptionText;
    private TextView descriptionText2;
    private TextView descriptionText3;
    private boolean doneAfterPasswordLoad;
    private EditTextBoldCursor editTextFirstRow;
    private EditTextBoldCursor editTextSecondRow;
    private String email;
    private String emailCode;
    private int emailCodeLength;
    private boolean emailOnly;
    private Runnable errorColorTimeout;
    private Runnable finishCallback;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private RadialProgressView floatingProgressView;
    private ArrayList<BaseFragment> fragmentsToClose;
    private boolean fromRegistration;
    private String hint;
    private boolean ignoreTextChange;
    private RLottieImageView imageView;
    private boolean isPasswordVisible;
    private CustomPhoneKeyboardView keyboardView;
    private Runnable monkeyAfterSwitchCallback;
    private Runnable monkeyEndCallback;
    private boolean needPasswordButton;
    private int otherwiseReloginDays;
    private OutlineTextContainerView outlineTextFirstRow;
    private OutlineTextContainerView outlineTextSecondRow;
    private boolean paused;
    private boolean postedErrorColorTimeout;
    private RadialProgressView radialProgressView;
    private ScrollView scrollView;
    private Runnable setAnimationRunnable;
    private ImageView showPasswordButton;
    private TextView titleTextView;
    private boolean waitingForEmail;

    /* renamed from: lambda$new$0$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4737lambda$new$0$orgtelegramuiTwoStepVerificationSetupActivity() {
        this.postedErrorColorTimeout = false;
        for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
            this.codeFieldContainer.codeField[i].animateErrorProgress(0.0f);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4738lambda$new$1$orgtelegramuiTwoStepVerificationSetupActivity() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor == null) {
            return;
        }
        if (editTextBoldCursor.length() != 0) {
            this.animationDrawables[2].setCustomEndFrame(49);
            this.animationDrawables[2].setProgress(0.0f, false);
            this.imageView.playAnimation();
            return;
        }
        setRandomMonkeyIdleAnimation(true);
    }

    public TwoStepVerificationSetupActivity(int type, TLRPC.TL_account_password password) {
        this.needPasswordButton = false;
        this.otherwiseReloginDays = -1;
        this.fragmentsToClose = new ArrayList<>();
        this.emailCodeLength = 6;
        this.currentPasswordHash = new byte[0];
        this.errorColorTimeout = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4737lambda$new$0$orgtelegramuiTwoStepVerificationSetupActivity();
            }
        };
        this.finishCallback = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4738lambda$new$1$orgtelegramuiTwoStepVerificationSetupActivity();
            }
        };
        this.currentType = type;
        this.currentPassword = password;
        if (password == null && (type == 6 || type == 8)) {
            loadPasswordInfo();
        } else {
            this.waitingForEmail = !TextUtils.isEmpty(password.email_unconfirmed_pattern);
        }
    }

    public TwoStepVerificationSetupActivity(int account, int type, TLRPC.TL_account_password password) {
        this.needPasswordButton = false;
        this.otherwiseReloginDays = -1;
        this.fragmentsToClose = new ArrayList<>();
        this.emailCodeLength = 6;
        this.currentPasswordHash = new byte[0];
        this.errorColorTimeout = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4737lambda$new$0$orgtelegramuiTwoStepVerificationSetupActivity();
            }
        };
        this.finishCallback = new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4738lambda$new$1$orgtelegramuiTwoStepVerificationSetupActivity();
            }
        };
        this.currentAccount = account;
        this.currentType = type;
        this.currentPassword = password;
        this.waitingForEmail = !TextUtils.isEmpty(password.email_unconfirmed_pattern);
        if (this.currentPassword == null) {
            int i = this.currentType;
            if (i == 6 || i == 8) {
                loadPasswordInfo();
            }
        }
    }

    public void setCurrentPasswordParams(byte[] passwordHash, long secretId, byte[] secret, boolean email) {
        this.currentPasswordHash = passwordHash;
        this.currentSecret = secret;
        this.currentSecretId = secretId;
        this.emailOnly = email;
    }

    public void setCurrentEmailCode(String code) {
        this.emailCode = code;
    }

    public void addFragmentToClose(BaseFragment fragment) {
        this.fragmentsToClose.add(fragment);
    }

    public void setFromRegistration(boolean fromRegistration) {
        this.fromRegistration = fromRegistration;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.doneAfterPasswordLoad = false;
        Runnable runnable = this.setAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.setAnimationRunnable = null;
        }
        if (this.animationDrawables != null) {
            int a = 0;
            while (true) {
                RLottieDrawable[] rLottieDrawableArr = this.animationDrawables;
                if (a >= rLottieDrawableArr.length) {
                    break;
                }
                rLottieDrawableArr[a].recycle();
                a++;
            }
            this.animationDrawables = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        CodeNumberField[] codeNumberFieldArr;
        this.actionBar.setBackgroundDrawable(null);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        boolean z = false;
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        if (this.currentType == 5) {
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_other);
            item.addSubItem(1, LocaleController.getString("AbortPasswordMenu", R.string.AbortPasswordMenu));
        }
        this.floatingButtonContainer = new FrameLayout(context);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.2
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.floatingAutoAnimator = VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda50
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.m4726x9cc3587(view);
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
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.floatingProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(22.0f));
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingButtonContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
        TextView textView = new TextView(context);
        this.bottomSkipButton = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
        this.bottomSkipButton.setTextSize(1, 14.0f);
        this.bottomSkipButton.setGravity(19);
        this.bottomSkipButton.setVisibility(8);
        VerticalPositionAutoAnimator.attach(this.bottomSkipButton);
        this.bottomSkipButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.bottomSkipButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.m4732xc21c7422(view);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
            this.imageView.setVisibility(8);
        } else if (!isIntro()) {
            this.imageView.setVisibility(isLandscape() ? 8 : 0);
        }
        TextView textView2 = new TextView(context);
        this.titleTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        SpoilersTextView spoilersTextView = new SpoilersTextView(context);
        this.descriptionText = spoilersTextView;
        spoilersTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setVisibility(8);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        TextView textView3 = new TextView(context);
        this.descriptionText2 = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setTextSize(1, 14.0f);
        this.descriptionText2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2.setVisibility(8);
        this.descriptionText2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.m4733xb3c61a41(view);
            }
        });
        TextView textView4 = new TextView(context);
        this.buttonTextView = textView4;
        textView4.setMinWidth(AndroidUtilities.dp(220.0f));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTextSize(1, 15.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 6.0f));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.m4734xa56fc060(view);
            }
        });
        switch (this.currentType) {
            case 6:
            case 7:
            case 9:
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 24.0f);
                break;
            case 8:
            default:
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.titleTextView.setTextSize(1, 18.0f);
                break;
        }
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                final FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.4
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.radialProgressView.getLayoutParams();
                        params.topMargin = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(16.0f);
                    }
                };
                final SizeNotifierFrameLayout keyboardFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.5
                    @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        int frameBottom;
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() == 8 || measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8) {
                                FrameLayout frameLayout2 = frameLayout;
                                int measuredWidth = getMeasuredWidth();
                                int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(230.0f);
                                frameBottom = measuredHeight;
                                frameLayout2.layout(0, 0, measuredWidth, measuredHeight);
                            } else {
                                FrameLayout frameLayout3 = frameLayout;
                                int measuredWidth2 = getMeasuredWidth();
                                int measuredHeight2 = getMeasuredHeight();
                                frameBottom = measuredHeight2;
                                frameLayout3.layout(0, 0, measuredWidth2, measuredHeight2);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.isCustomKeyboardVisible()) {
                            FrameLayout frameLayout4 = frameLayout;
                            int measuredWidth3 = getMeasuredWidth();
                            int measuredHeight3 = (getMeasuredHeight() - AndroidUtilities.dp(230.0f)) + measureKeyboardHeight();
                            frameBottom = measuredHeight3;
                            frameLayout4.layout(0, 0, measuredWidth3, measuredHeight3);
                        } else {
                            FrameLayout frameLayout5 = frameLayout;
                            int measuredWidth4 = getMeasuredWidth();
                            int measuredHeight4 = getMeasuredHeight();
                            frameBottom = measuredHeight4;
                            frameLayout5.layout(0, 0, measuredWidth4, measuredHeight4);
                        }
                        TwoStepVerificationSetupActivity.this.keyboardView.layout(0, frameBottom, getMeasuredWidth(), AndroidUtilities.dp(230.0f) + frameBottom);
                    }

                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        setMeasuredDimension(width, height);
                        int frameHeight = height;
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8 && measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            frameHeight -= AndroidUtilities.dp(230.0f);
                        }
                        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(frameHeight, C.BUFFER_FLAG_ENCRYPTED));
                        TwoStepVerificationSetupActivity.this.keyboardView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                keyboardFrameLayout.addView(frameLayout);
                ViewGroup container = new ViewGroup(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.6
                    @Override // android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), C.BUFFER_FLAG_ENCRYPTED));
                        keyboardFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                        setMeasuredDimension(width, height);
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        TwoStepVerificationSetupActivity.this.actionBarBackground.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredHeight());
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = keyboardFrameLayout;
                        sizeNotifierFrameLayout.layout(0, 0, sizeNotifierFrameLayout.getMeasuredWidth(), keyboardFrameLayout.getMeasuredHeight());
                    }
                };
                ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.7
                    private int scrollingUp;
                    private int[] location = new int[2];
                    private Rect tempRect = new Rect();
                    private boolean isLayoutDirty = true;

                    @Override // android.view.View
                    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                        super.onScrollChanged(l, t, oldl, oldt);
                        if (TwoStepVerificationSetupActivity.this.titleTextView != null) {
                            TwoStepVerificationSetupActivity.this.titleTextView.getLocationOnScreen(this.location);
                            boolean show = this.location[1] + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() < TwoStepVerificationSetupActivity.this.actionBar.getBottom();
                            boolean visible = TwoStepVerificationSetupActivity.this.titleTextView.getTag() == null;
                            if (show != visible) {
                                TwoStepVerificationSetupActivity.this.titleTextView.setTag(show ? null : 1);
                                if (TwoStepVerificationSetupActivity.this.actionBarAnimator != null) {
                                    TwoStepVerificationSetupActivity.this.actionBarAnimator.cancel();
                                    TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                                }
                                TwoStepVerificationSetupActivity.this.actionBarAnimator = new AnimatorSet();
                                AnimatorSet animatorSet = TwoStepVerificationSetupActivity.this.actionBarAnimator;
                                Animator[] animatorArr = new Animator[2];
                                View view = TwoStepVerificationSetupActivity.this.actionBarBackground;
                                Property property = View.ALPHA;
                                float[] fArr = new float[1];
                                float f = 1.0f;
                                fArr[0] = show ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                                SimpleTextView titleTextView = TwoStepVerificationSetupActivity.this.actionBar.getTitleTextView();
                                Property property2 = View.ALPHA;
                                float[] fArr2 = new float[1];
                                if (!show) {
                                    f = 0.0f;
                                }
                                fArr2[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                                animatorSet.playTogether(animatorArr);
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.setDuration(150L);
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.7.1
                                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation.equals(TwoStepVerificationSetupActivity.this.actionBarAnimator)) {
                                            TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                                        }
                                    }
                                });
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.start();
                            }
                        }
                    }

                    @Override // android.widget.ScrollView
                    public void scrollToDescendant(View child) {
                        child.getDrawingRect(this.tempRect);
                        offsetDescendantRectToMyCoords(child, this.tempRect);
                        this.tempRect.bottom += AndroidUtilities.dp(120.0f);
                        int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.tempRect);
                        if (scrollDelta < 0) {
                            int measuredHeight = (getMeasuredHeight() - child.getMeasuredHeight()) / 2;
                            this.scrollingUp = measuredHeight;
                            scrollDelta -= measuredHeight;
                        } else {
                            this.scrollingUp = 0;
                        }
                        if (scrollDelta != 0) {
                            smoothScrollBy(0, scrollDelta);
                        }
                    }

                    @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
                    public void requestChildFocus(View child, View focused) {
                        if (Build.VERSION.SDK_INT < 29 && focused != null && !this.isLayoutDirty) {
                            scrollToDescendant(focused);
                        }
                        super.requestChildFocus(child, focused);
                    }

                    @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
                    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                        if (Build.VERSION.SDK_INT < 23) {
                            rectangle.bottom += AndroidUtilities.dp(120.0f);
                            if (this.scrollingUp != 0) {
                                rectangle.top -= this.scrollingUp;
                                rectangle.bottom -= this.scrollingUp;
                                this.scrollingUp = 0;
                            }
                        }
                        return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                    }

                    @Override // android.widget.ScrollView, android.view.View, android.view.ViewParent
                    public void requestLayout() {
                        this.isLayoutDirty = true;
                        super.requestLayout();
                    }

                    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {
                        this.isLayoutDirty = false;
                        super.onLayout(changed, l, t, r, b);
                    }
                };
                this.scrollView = scrollView;
                scrollView.setVerticalScrollBarEnabled(false);
                frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout.addView(this.bottomSkipButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
                frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
                container.addView(keyboardFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout scrollViewLinearLayout = new LinearLayout(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.8
                    @Override // android.widget.LinearLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.titleTextView.getLayoutParams();
                        int i = 0;
                        int dp = ((TwoStepVerificationSetupActivity.this.imageView.getVisibility() != 8 || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(8.0f);
                        if (TwoStepVerificationSetupActivity.this.currentType == 2 && AndroidUtilities.isSmallScreen() && !TwoStepVerificationSetupActivity.this.isLandscape()) {
                            i = AndroidUtilities.dp(32.0f);
                        }
                        params.topMargin = dp + i;
                    }
                };
                scrollViewLinearLayout.setOrientation(1);
                this.scrollView.addView(scrollViewLinearLayout, LayoutHelper.createScroll(-1, -1, 51));
                scrollViewLinearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                scrollViewLinearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                scrollViewLinearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
                this.outlineTextFirstRow = outlineTextContainerView;
                outlineTextContainerView.animateSelection(1.0f, false);
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
                this.editTextFirstRow = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 18.0f);
                int padding = AndroidUtilities.dp(16.0f);
                this.editTextFirstRow.setPadding(padding, padding, padding, padding);
                this.editTextFirstRow.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
                this.editTextFirstRow.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.editTextFirstRow.setBackground(null);
                this.editTextFirstRow.setMaxLines(1);
                this.editTextFirstRow.setLines(1);
                this.editTextFirstRow.setGravity(3);
                this.editTextFirstRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextFirstRow.setSingleLine(true);
                this.editTextFirstRow.setCursorWidth(1.5f);
                this.editTextFirstRow.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda8
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView5, int i, KeyEvent keyEvent) {
                        return TwoStepVerificationSetupActivity.this.m4718x30c38ecd(textView5, i, keyEvent);
                    }
                });
                this.outlineTextFirstRow.attachEditText(this.editTextFirstRow);
                this.editTextFirstRow.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z2) {
                        TwoStepVerificationSetupActivity.this.m4719x226d34ec(view, z2);
                    }
                });
                LinearLayout firstRowLinearLayout = new LinearLayout(context);
                firstRowLinearLayout.setOrientation(0);
                firstRowLinearLayout.addView(this.editTextFirstRow, LayoutHelper.createLinear(0, -2, 1.0f));
                ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.9
                    @Override // android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(info);
                        boolean z2 = true;
                        info.setCheckable(true);
                        if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() != null) {
                            z2 = false;
                        }
                        info.setChecked(z2);
                    }
                };
                this.showPasswordButton = imageView;
                imageView.setImageResource(R.drawable.msg_message);
                this.showPasswordButton.setScaleType(ImageView.ScaleType.CENTER);
                this.showPasswordButton.setContentDescription(LocaleController.getString((int) R.string.TwoStepVerificationShowPassword));
                if (Build.VERSION.SDK_INT >= 21) {
                    this.showPasswordButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
                }
                this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                this.showPasswordButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda48
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        TwoStepVerificationSetupActivity.this.m4720x1416db0b(view);
                    }
                });
                firstRowLinearLayout.addView(this.showPasswordButton, LayoutHelper.createLinear(24, 24, 16, 0, 0, 16, 0));
                this.editTextFirstRow.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.10
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        if (TwoStepVerificationSetupActivity.this.needPasswordButton) {
                            if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() == 0 || TextUtils.isEmpty(s)) {
                                if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() != 8 && TextUtils.isEmpty(s)) {
                                    AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, false, 0.1f, true);
                                    return;
                                }
                                return;
                            }
                            AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, true, 0.1f, true);
                        }
                    }
                });
                this.outlineTextFirstRow.addView(firstRowLinearLayout, LayoutHelper.createFrame(-1, -2.0f));
                scrollViewLinearLayout.addView(this.outlineTextFirstRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 32.0f, 24.0f, 32.0f));
                this.outlineTextSecondRow = new OutlineTextContainerView(context);
                EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context);
                this.editTextSecondRow = editTextBoldCursor2;
                editTextBoldCursor2.setTextSize(1, 18.0f);
                int padding2 = AndroidUtilities.dp(16.0f);
                this.editTextSecondRow.setPadding(padding2, padding2, padding2, padding2);
                this.editTextSecondRow.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
                this.editTextSecondRow.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.editTextSecondRow.setBackground(null);
                this.editTextSecondRow.setMaxLines(1);
                this.editTextSecondRow.setLines(1);
                this.editTextSecondRow.setGravity(3);
                this.editTextSecondRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextSecondRow.setSingleLine(true);
                this.editTextSecondRow.setCursorWidth(1.5f);
                this.editTextSecondRow.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView5, int i, KeyEvent keyEvent) {
                        return TwoStepVerificationSetupActivity.this.m4721x5c0812a(textView5, i, keyEvent);
                    }
                });
                this.outlineTextSecondRow.attachEditText(this.editTextSecondRow);
                this.editTextSecondRow.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z2) {
                        TwoStepVerificationSetupActivity.this.m4722xf76a2749(view, z2);
                    }
                });
                this.outlineTextSecondRow.addView(this.editTextSecondRow, LayoutHelper.createFrame(-1, -2.0f));
                scrollViewLinearLayout.addView(this.outlineTextSecondRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 16.0f, 24.0f, 0.0f));
                this.outlineTextSecondRow.setVisibility(8);
                CustomPhoneKeyboardView customPhoneKeyboardView = new CustomPhoneKeyboardView(context);
                this.keyboardView = customPhoneKeyboardView;
                customPhoneKeyboardView.setVisibility(8);
                keyboardFrameLayout.addView(this.keyboardView);
                CodeFieldContainer codeFieldContainer = new CodeFieldContainer(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.11
                    @Override // org.telegram.ui.CodeFieldContainer
                    protected void processNextPressed() {
                        TwoStepVerificationSetupActivity.this.processNext();
                    }
                };
                this.codeFieldContainer = codeFieldContainer;
                codeFieldContainer.setNumbersCount(6, 1);
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    f.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
                    f.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.12
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (TwoStepVerificationSetupActivity.this.postedErrorColorTimeout) {
                                AndroidUtilities.cancelRunOnUIThread(TwoStepVerificationSetupActivity.this.errorColorTimeout);
                                TwoStepVerificationSetupActivity.this.errorColorTimeout.run();
                            }
                        }
                    });
                    f.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda6
                        @Override // android.view.View.OnFocusChangeListener
                        public final void onFocusChange(View view, boolean z2) {
                            TwoStepVerificationSetupActivity.this.m4723xe913cd68(view, z2);
                        }
                    });
                }
                this.codeFieldContainer.setVisibility(8);
                scrollViewLinearLayout.addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, -2, 1, 0, 32, 0, 0));
                FrameLayout frameLayout2 = new FrameLayout(context);
                scrollViewLinearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 51, 0, 36, 0, 22));
                frameLayout2.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2, 49));
                if (this.currentType == 4) {
                    TextView textView5 = new TextView(context);
                    this.descriptionText3 = textView5;
                    textView5.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
                    this.descriptionText3.setGravity(1);
                    this.descriptionText3.setTextSize(1, 14.0f);
                    this.descriptionText3.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                    this.descriptionText3.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                    this.descriptionText3.setText(LocaleController.getString("RestoreEmailTroubleNoEmail", R.string.RestoreEmailTroubleNoEmail));
                    scrollViewLinearLayout.addView(this.descriptionText3, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 25));
                    this.descriptionText3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda49
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            TwoStepVerificationSetupActivity.this.m4725xcc6719a6(view);
                        }
                    });
                }
                this.fragmentView = container;
                View view = new View(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.13
                    private Paint paint = new Paint();

                    @Override // android.view.View
                    protected void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        int h = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), h, this.paint);
                        TwoStepVerificationSetupActivity.this.parentLayout.drawHeaderShadow(canvas, h);
                    }
                };
                this.actionBarBackground = view;
                view.setAlpha(0.0f);
                container.addView(this.actionBarBackground);
                container.addView(this.actionBar);
                RadialProgressView radialProgressView2 = new RadialProgressView(context);
                this.radialProgressView = radialProgressView2;
                radialProgressView2.setSize(AndroidUtilities.dp(20.0f));
                this.radialProgressView.setAlpha(0.0f);
                this.radialProgressView.setScaleX(0.1f);
                this.radialProgressView.setScaleY(0.1f);
                this.radialProgressView.setProgressColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
                frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(32, 32.0f, 53, 0.0f, 16.0f, 16.0f, 0.0f));
                break;
            case 6:
            case 7:
            case 9:
                ViewGroup container2 = new ViewGroup(context) { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.3
                    @Override // android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                        if (width > height) {
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.45f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * 0.68f), C.BUFFER_FLAG_ENCRYPTED));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C.BUFFER_FLAG_ENCRYPTED));
                        } else {
                            int imageSize = TwoStepVerificationSetupActivity.this.currentType == 7 ? 160 : 140;
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(imageSize), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(imageSize), C.BUFFER_FLAG_ENCRYPTED));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                        setMeasuredDimension(width, height);
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, r, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        int width = r - l;
                        int height = b - t;
                        if (r > b) {
                            int y = (height - TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight()) / 2;
                            TwoStepVerificationSetupActivity.this.imageView.layout(0, y, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + y);
                            int x = (int) (width * 0.4f);
                            int y2 = (int) (height * 0.22f);
                            TwoStepVerificationSetupActivity.this.titleTextView.layout(x, y2, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth() + x, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + y2);
                            int x2 = (int) (width * 0.4f);
                            int y3 = (int) (height * 0.39f);
                            TwoStepVerificationSetupActivity.this.descriptionText.layout(x2, y3, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth() + x2, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + y3);
                            int x3 = (int) ((width * 0.4f) + (((width * 0.6f) - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2.0f));
                            int y4 = (int) (height * 0.64f);
                            TwoStepVerificationSetupActivity.this.buttonTextView.layout(x3, y4, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + x3, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + y4);
                            return;
                        }
                        int y5 = (int) (height * 0.3f);
                        int x4 = (width - TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth()) / 2;
                        TwoStepVerificationSetupActivity.this.imageView.layout(x4, y5, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth() + x4, TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + y5);
                        int y6 = y5 + TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(16.0f);
                        TwoStepVerificationSetupActivity.this.titleTextView.layout(0, y6, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + y6);
                        int y7 = y6 + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        TwoStepVerificationSetupActivity.this.descriptionText.layout(0, y7, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + y7);
                        int x5 = (width - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y8 = (height - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        TwoStepVerificationSetupActivity.this.buttonTextView.layout(x5, y8, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + x5, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + y8);
                    }
                };
                container2.setOnTouchListener(TwoStepVerificationSetupActivity$$ExternalSyntheticLambda7.INSTANCE);
                container2.addView(this.actionBar);
                container2.addView(this.imageView);
                container2.addView(this.titleTextView);
                container2.addView(this.descriptionText);
                container2.addView(this.buttonTextView);
                this.fragmentView = container2;
                break;
        }
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        switch (this.currentType) {
            case 0:
            case 1:
                if (this.currentPassword.has_password) {
                    this.actionBar.setTitle(LocaleController.getString("PleaseEnterNewFirstPassword", R.string.PleaseEnterNewFirstPassword));
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterNewFirstPassword", R.string.PleaseEnterNewFirstPassword));
                } else {
                    CharSequence title = LocaleController.getString(this.currentType == 0 ? R.string.CreatePassword : R.string.ReEnterPassword);
                    this.actionBar.setTitle(title);
                    this.titleTextView.setText(title);
                }
                if (!TextUtils.isEmpty(this.emailCode)) {
                    this.bottomSkipButton.setVisibility(0);
                    this.bottomSkipButton.setText(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip));
                }
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.outlineTextFirstRow.setText(LocaleController.getString(this.currentType == 0 ? R.string.EnterPassword : R.string.ReEnterPassword));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(this.currentType == 0 ? R.string.EnterPassword : R.string.ReEnterPassword));
                this.editTextFirstRow.setImeOptions(268435461);
                this.editTextFirstRow.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.editTextFirstRow.setTypeface(Typeface.DEFAULT);
                this.needPasswordButton = this.currentType == 0;
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                RLottieDrawable[] rLottieDrawableArr = new RLottieDrawable[7];
                this.animationDrawables = rLottieDrawableArr;
                rLottieDrawableArr[0] = new RLottieDrawable(R.raw.tsv_setup_monkey_idle1, "2131558562", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[1] = new RLottieDrawable(R.raw.tsv_setup_monkey_idle2, "2131558563", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[2] = new RLottieDrawable(R.raw.tsv_monkey_close, "2131558555", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[3] = new RLottieDrawable(R.raw.tsv_setup_monkey_peek, "2131558564", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[4] = new RLottieDrawable(R.raw.tsv_setup_monkey_close_and_peek_to_idle, "2131558561", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[5] = new RLottieDrawable(R.raw.tsv_setup_monkey_close_and_peek, "2131558560", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[6] = new RLottieDrawable(R.raw.tsv_setup_monkey_tracking, "2131558565", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, null);
                this.animationDrawables[6].setPlayInDirectionOfCustomEndFrame(true);
                this.animationDrawables[6].setCustomEndFrame(19);
                this.animationDrawables[2].setOnFinishCallback(this.finishCallback, 97);
                setRandomMonkeyIdleAnimation(true);
                if (this.currentType == 1) {
                    z = true;
                }
                switchMonkeyAnimation(z);
                break;
            case 2:
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", R.string.PasswordHint));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setText(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip));
                this.titleTextView.setText(LocaleController.getString("PasswordHint", R.string.PasswordHint));
                this.descriptionText.setText(LocaleController.getString((int) R.string.PasswordHintDescription));
                this.descriptionText.setVisibility(0);
                this.outlineTextFirstRow.setText(LocaleController.getString((int) R.string.PasswordHintPlaceholder));
                this.editTextFirstRow.setContentDescription(LocaleController.getString((int) R.string.PasswordHintPlaceholder));
                this.editTextFirstRow.setImeOptions(268435461);
                this.outlineTextSecondRow.setVisibility(8);
                this.imageView.setAnimation(R.raw.tsv_setup_hint, 120, 120);
                this.imageView.playAnimation();
                break;
            case 3:
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmailTitle", R.string.RecoveryEmailTitle));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                if (!this.emailOnly) {
                    this.bottomSkipButton.setVisibility(0);
                    this.bottomSkipButton.setText(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip));
                }
                this.titleTextView.setText(LocaleController.getString("RecoveryEmailTitle", R.string.RecoveryEmailTitle));
                this.outlineTextFirstRow.setText(LocaleController.getString((int) R.string.PaymentShippingEmailPlaceholder));
                this.editTextFirstRow.setContentDescription(LocaleController.getString((int) R.string.PaymentShippingEmailPlaceholder));
                this.editTextFirstRow.setImeOptions(268435461);
                this.editTextFirstRow.setInputType(33);
                this.outlineTextSecondRow.setVisibility(8);
                this.imageView.setAnimation(R.raw.tsv_setup_email_sent, 120, 120);
                this.imageView.playAnimation();
                break;
            case 4:
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                this.keyboardView.setVisibility(0);
                this.outlineTextFirstRow.setVisibility(8);
                String rawPattern = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                SpannableStringBuilder emailPattern = SpannableStringBuilder.valueOf(rawPattern);
                int startIndex = rawPattern.indexOf(42);
                int endIndex = rawPattern.lastIndexOf(42);
                if (startIndex != endIndex && startIndex != -1 && endIndex != -1) {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                    run.flags |= 256;
                    run.start = startIndex;
                    run.end = endIndex + 1;
                    emailPattern.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
                }
                this.descriptionText.setText(AndroidUtilities.formatSpannable(LocaleController.getString((int) R.string.RestoreEmailSent), emailPattern));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.codeFieldContainer.setVisibility(0);
                this.imageView.setAnimation(R.raw.tsv_setup_mail, 120, 120);
                this.imageView.playAnimation();
                break;
            case 5:
                this.actionBar.setTitle(LocaleController.getString("VerificationCode", R.string.VerificationCode));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("VerificationCode", R.string.VerificationCode));
                this.outlineTextFirstRow.setVisibility(8);
                this.keyboardView.setVisibility(0);
                TextView textView6 = this.descriptionText;
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textView6.setText(LocaleController.formatString("EmailPasswordConfirmText2", R.string.EmailPasswordConfirmText2, objArr));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setGravity(17);
                ((ViewGroup.MarginLayoutParams) this.bottomSkipButton.getLayoutParams()).bottomMargin = 0;
                this.bottomSkipButton.setText(LocaleController.getString((int) R.string.ResendCode));
                this.bottomSkipButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda51
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        TwoStepVerificationSetupActivity.this.m4727x82a5066f(view2);
                    }
                });
                this.codeFieldContainer.setVisibility(0);
                this.imageView.setAnimation(R.raw.tsv_setup_mail, 120, 120);
                this.imageView.playAnimation();
                break;
            case 6:
                this.titleTextView.setText(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle));
                this.descriptionText.setText(LocaleController.getString("SetAdditionalPasswordInfo", R.string.SetAdditionalPasswordInfo));
                this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationSetPassword", R.string.TwoStepVerificationSetPassword));
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(R.raw.tsv_setup_intro, 140, 140);
                this.imageView.playAnimation();
                break;
            case 7:
                this.titleTextView.setText(LocaleController.getString("TwoStepVerificationPasswordSet", R.string.TwoStepVerificationPasswordSet));
                this.descriptionText.setText(LocaleController.getString("TwoStepVerificationPasswordSetInfo", R.string.TwoStepVerificationPasswordSetInfo));
                if (this.closeAfterSet) {
                    this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationPasswordReturnPassport", R.string.TwoStepVerificationPasswordReturnPassport));
                } else if (this.fromRegistration) {
                    this.buttonTextView.setText(LocaleController.getString((int) R.string.Continue));
                } else {
                    this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationPasswordReturnSettings", R.string.TwoStepVerificationPasswordReturnSettings));
                }
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(R.raw.wallet_allset, 160, 160);
                this.imageView.playAnimation();
                break;
            case 8:
                this.actionBar.setTitle(LocaleController.getString("PleaseEnterCurrentPassword", R.string.PleaseEnterCurrentPassword));
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", R.string.PleaseEnterCurrentPassword));
                this.descriptionText.setText(LocaleController.getString("CheckPasswordInfo", R.string.CheckPasswordInfo));
                this.descriptionText.setVisibility(0);
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.descriptionText2.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
                this.descriptionText2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
                this.outlineTextFirstRow.setText(LocaleController.getString((int) R.string.LoginPassword));
                this.editTextFirstRow.setContentDescription(LocaleController.getString((int) R.string.LoginPassword));
                this.editTextFirstRow.setImeOptions(268435462);
                this.editTextFirstRow.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.editTextFirstRow.setTypeface(Typeface.DEFAULT);
                this.imageView.setAnimation(R.raw.wallet_science, 120, 120);
                this.imageView.playAnimation();
                break;
            case 9:
                this.titleTextView.setText(LocaleController.getString("CheckPasswordPerfect", R.string.CheckPasswordPerfect));
                this.descriptionText.setText(LocaleController.getString("CheckPasswordPerfectInfo", R.string.CheckPasswordPerfectInfo));
                this.buttonTextView.setText(LocaleController.getString("CheckPasswordBackToSettings", R.string.CheckPasswordBackToSettings));
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(R.raw.wallet_perfect, 140, 140);
                this.imageView.playAnimation();
                break;
        }
        EditTextBoldCursor editTextBoldCursor3 = this.editTextFirstRow;
        if (editTextBoldCursor3 != null) {
            editTextBoldCursor3.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.14
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    if (!TwoStepVerificationSetupActivity.this.ignoreTextChange) {
                        if (TwoStepVerificationSetupActivity.this.currentType == 0) {
                            RLottieDrawable currentDrawable = TwoStepVerificationSetupActivity.this.imageView.getAnimatedDrawable();
                            if (TwoStepVerificationSetupActivity.this.editTextFirstRow.length() > 0) {
                                if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null) {
                                    if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[3] && currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[5]);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[5].setProgress(0.0f, false);
                                        TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                                    }
                                } else if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[3]) {
                                    if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setProgress(0.0f, false);
                                        TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                                    } else if (TwoStepVerificationSetupActivity.this.animationDrawables[2].getCurrentFrame() < 49) {
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                    }
                                }
                            } else if ((currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[3] || TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() != null) && currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(-1);
                                if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                    TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                    TwoStepVerificationSetupActivity.this.animationDrawables[2].setCurrentFrame(49, false);
                                }
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } else {
                                TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[4]);
                                TwoStepVerificationSetupActivity.this.animationDrawables[4].setProgress(0.0f, false);
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType != 1) {
                            if (TwoStepVerificationSetupActivity.this.currentType == 8 && s.length() > 0) {
                                TwoStepVerificationSetupActivity.this.showDoneButton(true);
                            }
                        } else {
                            try {
                                float progress = Math.min(1.0f, TwoStepVerificationSetupActivity.this.editTextFirstRow.getLayout().getLineWidth(0) / TwoStepVerificationSetupActivity.this.editTextFirstRow.getWidth());
                                TwoStepVerificationSetupActivity.this.animationDrawables[6].setCustomEndFrame((int) ((142.0f * progress) + 18.0f));
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }
                }
            });
        }
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationSetupActivity$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
            TwoStepVerificationSetupActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            String text;
            if (id == -1) {
                if (TwoStepVerificationSetupActivity.this.otherwiseReloginDays >= 0 && TwoStepVerificationSetupActivity.this.parentLayout.fragmentsStack.size() == 1) {
                    TwoStepVerificationSetupActivity.this.showSetForcePasswordAlert();
                } else {
                    TwoStepVerificationSetupActivity.this.finishFragment();
                }
            } else if (id == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TwoStepVerificationSetupActivity.this.getParentActivity());
                if (TwoStepVerificationSetupActivity.this.currentPassword != null && TwoStepVerificationSetupActivity.this.currentPassword.has_password) {
                    text = LocaleController.getString("CancelEmailQuestion", R.string.CancelEmailQuestion);
                } else {
                    text = LocaleController.getString("CancelPasswordQuestion", R.string.CancelPasswordQuestion);
                }
                String title = LocaleController.getString("CancelEmailQuestionTitle", R.string.CancelEmailQuestionTitle);
                String buttonText = LocaleController.getString("Abort", R.string.Abort);
                builder.setMessage(text);
                builder.setTitle(title);
                builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationSetupActivity.AnonymousClass1.this.m4766xa0f7e77b(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                TwoStepVerificationSetupActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-TwoStepVerificationSetupActivity$1 */
        public /* synthetic */ void m4766xa0f7e77b(DialogInterface dialogInterface, int i) {
            TwoStepVerificationSetupActivity.this.setNewPassword(true);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4726x9cc3587(View view) {
        processNext();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4732xc21c7422(View v) {
        int i = this.currentType;
        if (i == 0) {
            needShowProgress();
            TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
            req.code = this.emailCode;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda36
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationSetupActivity.this.m4730xdec927e4(tLObject, tL_error);
                }
            });
        } else if (i == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", R.string.YourEmailSkipWarningText));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", R.string.YourEmailSkipWarning));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda22
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    TwoStepVerificationSetupActivity.this.m4731xd072ce03(dialogInterface, i2);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        } else if (i == 2) {
            onHintDone();
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4730xdec927e4(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4729xed1f81c5(error);
            }
        });
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4729xed1f81c5(TLRPC.TL_error error) {
        String timeString;
        needHideProgress();
        if (error == null) {
            getMessagesController().removeSuggestion(0L, "VALIDATE_PASSWORD");
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationSetupActivity.this.m4728xfb75dba6(dialogInterface, i);
                }
            });
            builder.setMessage(LocaleController.getString("PasswordReset", R.string.PasswordReset));
            builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt((CharSequence) error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle), error.text);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4728xfb75dba6(DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        int a2 = this.currentAccount;
        NotificationCenter.getInstance(a2).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        finishFragment();
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4731xd072ce03(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4733xb3c61a41(View v) {
        if (this.currentType == 8) {
            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
            fragment.setForgotPasswordOnShow();
            fragment.setPassword(this.currentPassword);
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment, true);
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4734xa56fc060(View v) {
        processNext();
    }

    public static /* synthetic */ boolean lambda$createView$10(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ boolean m4718x30c38ecd(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5 || i == 6) {
            if (this.outlineTextSecondRow.getVisibility() == 0) {
                this.editTextSecondRow.requestFocus();
                return true;
            }
            processNext();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4719x226d34ec(View v, boolean hasFocus) {
        this.outlineTextFirstRow.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4720x1416db0b(View v) {
        this.ignoreTextChange = true;
        if (this.editTextFirstRow.getTransformationMethod() == null) {
            this.isPasswordVisible = false;
            this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.editTextFirstRow.length() > 0 && this.editTextFirstRow.hasFocus() && this.monkeyEndCallback == null) {
                this.animationDrawables[3].setCustomEndFrame(-1);
                RLottieDrawable animatedDrawable = this.imageView.getAnimatedDrawable();
                RLottieDrawable[] rLottieDrawableArr = this.animationDrawables;
                if (animatedDrawable != rLottieDrawableArr[3]) {
                    this.imageView.setAnimation(rLottieDrawableArr[3]);
                    this.animationDrawables[3].setCurrentFrame(18, false);
                }
                this.imageView.playAnimation();
            }
        } else {
            this.isPasswordVisible = true;
            this.editTextFirstRow.setTransformationMethod(null);
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelSend), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.editTextFirstRow.length() > 0 && this.editTextFirstRow.hasFocus() && this.monkeyEndCallback == null) {
                this.animationDrawables[3].setCustomEndFrame(18);
                RLottieDrawable animatedDrawable2 = this.imageView.getAnimatedDrawable();
                RLottieDrawable[] rLottieDrawableArr2 = this.animationDrawables;
                if (animatedDrawable2 != rLottieDrawableArr2[3]) {
                    this.imageView.setAnimation(rLottieDrawableArr2[3]);
                }
                this.animationDrawables[3].setProgress(0.0f, false);
                this.imageView.playAnimation();
            }
        }
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.ignoreTextChange = false;
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ boolean m4721x5c0812a(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5 || i == 6) {
            processNext();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4722xf76a2749(View v, boolean hasFocus) {
        this.outlineTextSecondRow.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4723xe913cd68(View v, boolean hasFocus) {
        if (hasFocus) {
            this.keyboardView.setEditText((EditText) v);
            this.keyboardView.setDispatchBackWhenEmpty(true);
        }
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4725xcc6719a6(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.m4724xdabd7387(dialogInterface, i);
            }
        });
        builder.setTitle(LocaleController.getString("ResetPassword", R.string.ResetPassword));
        builder.setMessage(LocaleController.getString("RestoreEmailTroubleText2", R.string.RestoreEmailTroubleText2));
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4724xdabd7387(DialogInterface dialog, int which) {
        onReset();
        finishFragment();
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4727x82a5066f(View v) {
        TLRPC.TL_account_resendPasswordEmail req = new TLRPC.TL_account_resendPasswordEmail();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46.INSTANCE);
        showDialog(new AlertDialog.Builder(getParentActivity()).setMessage(LocaleController.getString("ResendCodeInfo", R.string.ResendCodeInfo)).setTitle(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).create());
    }

    public static /* synthetic */ void lambda$createView$19(TLObject response, TLRPC.TL_error error) {
    }

    private boolean isIntro() {
        int i = this.currentType;
        return i == 6 || i == 9 || i == 7;
    }

    public boolean isLandscape() {
        return AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int i = 0;
        if (this.imageView != null) {
            if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
                this.imageView.setVisibility(8);
            } else if (!isIntro()) {
                this.imageView.setVisibility(isLandscape() ? 8 : 0);
            }
        }
        CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
        if (customPhoneKeyboardView != null) {
            if (!isCustomKeyboardVisible()) {
                i = 8;
            }
            customPhoneKeyboardView.setVisibility(i);
        }
    }

    private void animateSuccess(final Runnable callback) {
        for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
            final CodeNumberField field = this.codeFieldContainer.codeField[i];
            field.postDelayed(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    CodeNumberField.this.animateSuccessProgress(1.0f);
                }
            }, i * 75);
        }
        this.codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4717x559d11ce(callback);
            }
        }, (this.codeFieldContainer.codeField.length * 75) + 350);
    }

    /* renamed from: lambda$animateSuccess$22$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4717x559d11ce(Runnable callback) {
        CodeNumberField[] codeNumberFieldArr;
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.animateSuccessProgress(0.0f);
        }
        callback.run();
    }

    private void switchMonkeyAnimation(boolean tracking) {
        if (tracking) {
            Runnable runnable = this.setAnimationRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.imageView.setAnimation(this.animationDrawables[6]);
            this.imageView.playAnimation();
            return;
        }
        this.editTextFirstRow.dispatchTextWatchersTextChanged();
        setRandomMonkeyIdleAnimation(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hasForceLightStatusBar() {
        return true;
    }

    public boolean isCustomKeyboardVisible() {
        int i = this.currentType;
        return (i == 5 || i == 4) && !AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
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
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
    }

    public void processNext() {
        if (getParentActivity() == null) {
            return;
        }
        int i = 1;
        switch (this.currentType) {
            case 0:
            case 1:
                if (this.editTextFirstRow.length() == 0) {
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                } else if (!this.editTextFirstRow.getText().toString().equals(this.firstPassword) && this.currentType == 1) {
                    AndroidUtilities.shakeViewSpring(this.outlineTextFirstRow, 5.0f);
                    try {
                        this.outlineTextFirstRow.performHapticFeedback(3, 2);
                    } catch (Exception e) {
                    }
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", R.string.PasswordDoNotMatch), 0).show();
                        return;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        return;
                    }
                } else {
                    int i2 = this.currentAccount;
                    if (this.currentType != 0) {
                        i = 2;
                    }
                    TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(i2, i, this.currentPassword);
                    fragment.fromRegistration = this.fromRegistration;
                    fragment.firstPassword = this.editTextFirstRow.getText().toString();
                    fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                    fragment.setCurrentEmailCode(this.emailCode);
                    fragment.fragmentsToClose.addAll(this.fragmentsToClose);
                    fragment.fragmentsToClose.add(this);
                    fragment.closeAfterSet = this.closeAfterSet;
                    fragment.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment);
                    return;
                }
            case 2:
                String obj = this.editTextFirstRow.getText().toString();
                this.hint = obj;
                if (obj.equalsIgnoreCase(this.firstPassword)) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", R.string.PasswordAsHintError), 0).show();
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                }
                onHintDone();
                return;
            case 3:
                String obj2 = this.editTextFirstRow.getText().toString();
                this.email = obj2;
                if (!isValidEmail(obj2)) {
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                } else {
                    setNewPassword(false);
                    return;
                }
            case 4:
                final String code = this.codeFieldContainer.getCode();
                TLRPC.TL_auth_checkRecoveryPassword req = new TLRPC.TL_auth_checkRecoveryPassword();
                req.code = code;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda41
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationSetupActivity.this.m4751x2a0ff6b6(code, tLObject, tL_error);
                    }
                }, 10);
                return;
            case 5:
                TLRPC.TL_account_confirmPasswordEmail req2 = new TLRPC.TL_account_confirmPasswordEmail();
                req2.code = this.codeFieldContainer.getCode();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda39
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        TwoStepVerificationSetupActivity.this.m4755xf0b68f32(tLObject, tL_error);
                    }
                }, 10);
                needShowProgress();
                return;
            case 6:
                if (this.currentPassword == null) {
                    needShowProgress();
                    this.doneAfterPasswordLoad = true;
                    return;
                }
                TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
                fragment2.fromRegistration = this.fromRegistration;
                fragment2.closeAfterSet = this.closeAfterSet;
                fragment2.setBlockingAlert(this.otherwiseReloginDays);
                presentFragment(fragment2, true);
                return;
            case 7:
                if (this.closeAfterSet) {
                    finishFragment();
                    return;
                } else if (this.fromRegistration) {
                    Bundle args = new Bundle();
                    args.putBoolean("afterSignup", true);
                    DialogsActivity dialogsActivity = new DialogsActivity(args);
                    presentFragment(dialogsActivity, true);
                    return;
                } else {
                    TwoStepVerificationActivity fragment3 = new TwoStepVerificationActivity();
                    fragment3.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
                    fragment3.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment3, true);
                    return;
                }
            case 8:
                if (this.currentPassword == null) {
                    needShowProgress();
                    this.doneAfterPasswordLoad = true;
                    return;
                }
                String oldPassword = this.editTextFirstRow.getText().toString();
                if (oldPassword.length() == 0) {
                    onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                    return;
                }
                final byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
                needShowProgress();
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda35
                    @Override // java.lang.Runnable
                    public final void run() {
                        TwoStepVerificationSetupActivity.this.m4748x822863ce(oldPasswordBytes);
                    }
                });
                return;
            case 9:
                finishFragment();
                return;
            default:
                return;
        }
    }

    /* renamed from: lambda$processNext$28$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4748x822863ce(byte[] oldPasswordBytes) {
        final byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            x_bytes = SRPHelper.getX(oldPasswordBytes, algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda45
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.this.m4747x907ebdaf(x_bytes, tLObject, tL_error);
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

    /* renamed from: lambda$processNext$27$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4747x907ebdaf(final byte[] x_bytes, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationSetupActivity.this.m4743xc9d82533(x_bytes);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationSetupActivity.this.m4746x9ed51790(error);
                }
            });
        }
    }

    /* renamed from: lambda$processNext$23$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4743xc9d82533(byte[] x_bytes) {
        needHideProgress();
        this.currentPasswordHash = x_bytes;
        getMessagesController().removeSuggestion(0L, "VALIDATE_PASSWORD");
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(9, this.currentPassword);
        fragment.fromRegistration = this.fromRegistration;
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
    }

    /* renamed from: lambda$processNext$26$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4746x9ed51790(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda38
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationSetupActivity.this.m4745xad2b7171(tLObject, tL_error);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            this.descriptionText.setText(LocaleController.getString("CheckPasswordWrong", R.string.CheckPasswordWrong));
            this.descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, true);
            showDoneButton(false);
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

    /* renamed from: lambda$processNext$25$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4745xad2b7171(final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4744xbb81cb52(error2, response2);
            }
        });
    }

    /* renamed from: lambda$processNext$24$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4744xbb81cb52(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processNext();
        }
    }

    /* renamed from: lambda$processNext$31$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4751x2a0ff6b6(final String code, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4750x38665097(response, code, error);
            }
        });
    }

    /* renamed from: lambda$processNext$30$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4750x38665097(TLObject response, final String code, TLRPC.TL_error error) {
        String timeString;
        if (response instanceof TLRPC.TL_boolTrue) {
            animateSuccess(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationSetupActivity.this.m4749x73d209ed(code);
                }
            });
        } else if (error == null || error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt((CharSequence) error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle), error.text);
        }
    }

    /* renamed from: lambda$processNext$29$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4749x73d209ed(String code) {
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        fragment.fromRegistration = this.fromRegistration;
        fragment.fragmentsToClose.addAll(this.fragmentsToClose);
        fragment.addFragmentToClose(this);
        fragment.setCurrentEmailCode(code);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
    }

    /* renamed from: lambda$processNext$35$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4755xf0b68f32(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4754xff0ce913(error);
            }
        });
    }

    /* renamed from: lambda$processNext$34$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4754xff0ce913(TLRPC.TL_error error) {
        String timeString;
        needHideProgress();
        if (error == null) {
            if (getParentActivity() == null) {
                return;
            }
            animateSuccess(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    TwoStepVerificationSetupActivity.this.m4753xd6342f4();
                }
            });
        } else if (error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
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

    /* renamed from: lambda$processNext$33$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4753xd6342f4() {
        if (this.currentPassword.has_password) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationSetupActivity.this.m4752x1bb99cd5(dialogInterface, i);
                }
            });
            if (this.currentPassword.has_recovery) {
                builder.setMessage(LocaleController.getString("YourEmailSuccessChangedText", R.string.YourEmailSuccessChangedText));
            } else {
                builder.setMessage(LocaleController.getString("YourEmailSuccessText", R.string.YourEmailSuccessText));
            }
            builder.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                return;
            }
            return;
        }
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        this.currentPassword.has_password = true;
        this.currentPassword.has_recovery = true;
        this.currentPassword.email_unconfirmed_pattern = "";
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(7, this.currentPassword);
        fragment.fromRegistration = this.fromRegistration;
        fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
        fragment.fragmentsToClose.addAll(this.fragmentsToClose);
        fragment.closeAfterSet = this.closeAfterSet;
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    /* renamed from: lambda$processNext$32$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4752x1bb99cd5(DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        int a2 = this.currentAccount;
        NotificationCenter.getInstance(a2).postNotificationName(NotificationCenter.twoStepPasswordChanged, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        this.currentPassword.has_password = true;
        this.currentPassword.has_recovery = true;
        this.currentPassword.email_unconfirmed_pattern = "";
        fragment.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    private void onCodeFieldError(boolean clear) {
        CodeNumberField[] codeNumberFieldArr;
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            if (clear) {
                f.setText("");
            }
            f.animateErrorProgress(1.0f);
        }
        if (clear) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
        AndroidUtilities.shakeViewSpring(this.codeFieldContainer, 8.0f, new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4740x2ddc0bc2();
            }
        });
    }

    /* renamed from: lambda$onCodeFieldError$37$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4740x2ddc0bc2() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4739x3c3265a3();
            }
        }, 150L);
    }

    /* renamed from: lambda$onCodeFieldError$36$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4739x3c3265a3() {
        CodeNumberField[] codeNumberFieldArr;
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.animateErrorProgress(0.0f);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hideKeyboardOnShow() {
        int i = this.currentType;
        return i == 7 || i == 9;
    }

    private void onHintDone() {
        if (!this.currentPassword.has_recovery) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            fragment.fromRegistration = this.fromRegistration;
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
            fragment.firstPassword = this.firstPassword;
            fragment.hint = this.hint;
            fragment.fragmentsToClose.addAll(this.fragmentsToClose);
            fragment.fragmentsToClose.add(this);
            fragment.closeAfterSet = this.closeAfterSet;
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment);
            return;
        }
        this.email = "";
        setNewPassword(false);
    }

    public void showDoneButton(final boolean show) {
        if (show == (this.buttonTextView.getTag() != null)) {
            return;
        }
        AnimatorSet animatorSet = this.buttonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.buttonTextView.setTag(show ? 1 : null);
        this.buttonAnimation = new AnimatorSet();
        if (show) {
            this.buttonTextView.setVisibility(0);
            this.buttonAnimation.playTogether(ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_X, 0.9f), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_Y, 0.9f), ObjectAnimator.ofFloat(this.descriptionText2, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.buttonTextView, View.ALPHA, 1.0f));
        } else {
            this.descriptionText2.setVisibility(0);
            this.buttonAnimation.playTogether(ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_X, 0.9f), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_Y, 0.9f), ObjectAnimator.ofFloat(this.buttonTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.descriptionText2, View.ALPHA, 1.0f));
        }
        this.buttonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity.15
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (TwoStepVerificationSetupActivity.this.buttonAnimation != null && TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animation)) {
                    if (show) {
                        TwoStepVerificationSetupActivity.this.descriptionText2.setVisibility(4);
                    } else {
                        TwoStepVerificationSetupActivity.this.buttonTextView.setVisibility(4);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (TwoStepVerificationSetupActivity.this.buttonAnimation != null && TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animation)) {
                    TwoStepVerificationSetupActivity.this.buttonAnimation = null;
                }
            }
        });
        this.buttonAnimation.setDuration(150L);
        this.buttonAnimation.start();
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x002e, code lost:
        if (r0.isRunning() != false) goto L25;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setRandomMonkeyIdleAnimation(boolean r6) {
        /*
            r5 = this;
            int r0 = r5.currentType
            if (r0 == 0) goto L5
            return
        L5:
            java.lang.Runnable r0 = r5.setAnimationRunnable
            if (r0 == 0) goto Lc
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
        Lc:
            org.telegram.ui.Components.RLottieImageView r0 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r1 = 1
            r2 = 0
            if (r6 != 0) goto L30
            org.telegram.ui.Components.RLottieDrawable[] r3 = r5.animationDrawables
            r4 = r3[r2]
            if (r0 == r4) goto L30
            r3 = r3[r1]
            if (r0 == r3) goto L30
            org.telegram.ui.Components.EditTextBoldCursor r3 = r5.editTextFirstRow
            int r3 = r3.length()
            if (r3 != 0) goto L63
            if (r0 == 0) goto L30
            boolean r3 = r0.isRunning()
            if (r3 != 0) goto L63
        L30:
            java.security.SecureRandom r3 = org.telegram.messenger.Utilities.random
            int r3 = r3.nextInt()
            int r3 = r3 % 2
            r4 = 0
            if (r3 != 0) goto L4c
            org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable[] r3 = r5.animationDrawables
            r3 = r3[r2]
            r1.setAnimation(r3)
            org.telegram.ui.Components.RLottieDrawable[] r1 = r5.animationDrawables
            r1 = r1[r2]
            r1.setProgress(r4)
            goto L5c
        L4c:
            org.telegram.ui.Components.RLottieImageView r2 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable[] r3 = r5.animationDrawables
            r3 = r3[r1]
            r2.setAnimation(r3)
            org.telegram.ui.Components.RLottieDrawable[] r2 = r5.animationDrawables
            r1 = r2[r1]
            r1.setProgress(r4)
        L5c:
            if (r6 != 0) goto L63
            org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
            r1.playAnimation()
        L63:
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda19 r1 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda19
            r1.<init>()
            r5.setAnimationRunnable = r1
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            r3 = 2000(0x7d0, float:2.803E-42)
            int r2 = r2.nextInt(r3)
            int r2 = r2 + 5000
            long r2 = (long) r2
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setRandomMonkeyIdleAnimation(boolean):void");
    }

    /* renamed from: lambda$setRandomMonkeyIdleAnimation$38$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4764xc4dba19f() {
        if (this.setAnimationRunnable == null) {
            return;
        }
        setRandomMonkeyIdleAnimation(false);
    }

    public void setCloseAfterSet(boolean value) {
        this.closeAfterSet = value;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            if (this.editTextFirstRow != null && !isCustomKeyboardVisible()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        TwoStepVerificationSetupActivity.this.m4741xc494c331();
                    }
                }, 200L);
            }
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null && codeFieldContainer.getVisibility() == 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        TwoStepVerificationSetupActivity.this.m4742x892909db();
                    }
                }, 200L);
            }
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$39$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4741xc494c331() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor != null && editTextBoldCursor.getVisibility() == 0) {
            this.editTextFirstRow.requestFocus();
            AndroidUtilities.showKeyboard(this.editTextFirstRow);
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$40$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4742x892909db() {
        CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
        if (codeFieldContainer != null && codeFieldContainer.getVisibility() == 0) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
    }

    private void loadPasswordInfo() {
        TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda37
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.this.m4736xfa43ad9d(tLObject, tL_error);
            }
        }, 10);
    }

    /* renamed from: lambda$loadPasswordInfo$42$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4736xfa43ad9d(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4735x89a077e(error, response);
            }
        });
    }

    /* renamed from: lambda$loadPasswordInfo$41$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4735x89a077e(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            if (!this.paused && this.closeAfterSet && this.currentPassword.has_password) {
                TLRPC.PasswordKdfAlgo pendingCurrentAlgo = this.currentPassword.current_algo;
                TLRPC.SecurePasswordKdfAlgo pendingNewSecureAlgo = this.currentPassword.new_secure_algo;
                byte[] pendingSecureRandom = this.currentPassword.secure_random;
                String pendingEmail = this.currentPassword.has_recovery ? IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE : null;
                String pendingHint = this.currentPassword.hint != null ? this.currentPassword.hint : "";
                if (!this.waitingForEmail && pendingCurrentAlgo != null) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, null, pendingCurrentAlgo, pendingNewSecureAlgo, pendingSecureRandom, pendingEmail, pendingHint, null, null);
                    finishFragment();
                }
            }
            if (this.doneAfterPasswordLoad) {
                needHideProgress();
                processNext();
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    private void needShowProgress() {
        if (getParentActivity() == null || getParentActivity().isFinishing()) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        if (this.floatingButtonContainer.getVisibility() == 0) {
            set.playTogether(ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, 0.1f));
        } else {
            set.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, 1.0f));
        }
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.start();
    }

    protected void needHideProgress() {
        AnimatorSet set = new AnimatorSet();
        if (this.floatingButtonContainer.getVisibility() == 0) {
            set.playTogether(ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, 1.0f));
        } else {
            set.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, 0.1f));
        }
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.start();
    }

    private boolean isValidEmail(String text) {
        if (text == null || text.length() < 3) {
            return false;
        }
        int dot = text.lastIndexOf(46);
        int dog = text.lastIndexOf(64);
        return dog >= 0 && dot >= dog;
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

    /* JADX WARN: Multi-variable type inference failed */
    public void setNewPassword(final boolean clear) {
        TLRPC.TL_account_updatePasswordSettings req;
        TLRPC.TL_account_password tL_account_password;
        if (clear && this.waitingForEmail && this.currentPassword.has_password) {
            needShowProgress();
            TLRPC.TL_account_cancelPasswordEmail req2 = new TLRPC.TL_account_cancelPasswordEmail();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda40
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationSetupActivity.this.m4757xebcd88c5(tLObject, tL_error);
                }
            });
            return;
        }
        final String password = this.firstPassword;
        final TLRPC.TL_account_passwordInputSettings new_settings = new TLRPC.TL_account_passwordInputSettings();
        if (clear) {
            UserConfig.getInstance(this.currentAccount).resetSavedPassword();
            this.currentSecret = null;
            if (this.waitingForEmail) {
                new_settings.flags = 2;
                new_settings.email = "";
            } else {
                new_settings.flags = 3;
                new_settings.hint = "";
                new_settings.new_password_hash = new byte[0];
                new_settings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
                new_settings.email = "";
            }
        } else {
            if (this.hint == null && (tL_account_password = this.currentPassword) != null) {
                this.hint = tL_account_password.hint;
            }
            if (this.hint == null) {
                this.hint = "";
            }
            if (password != null) {
                new_settings.flags |= 1;
                new_settings.hint = this.hint;
                new_settings.new_algo = this.currentPassword.new_algo;
            }
            if (this.email.length() > 0) {
                new_settings.flags = 2 | new_settings.flags;
                new_settings.email = this.email.trim();
            }
        }
        if (this.emailCode != null) {
            TLRPC.TL_auth_recoverPassword req3 = new TLRPC.TL_auth_recoverPassword();
            req3.code = this.emailCode;
            req3.new_settings = new_settings;
            req3.flags |= 1;
            req = req3;
        } else {
            TLRPC.TL_account_updatePasswordSettings req4 = new TLRPC.TL_account_updatePasswordSettings();
            byte[] bArr = this.currentPasswordHash;
            if (bArr == null || bArr.length == 0 || (clear && this.waitingForEmail)) {
                req4.password = new TLRPC.TL_inputCheckPasswordEmpty();
            }
            req4.new_settings = new_settings;
            req = req4;
        }
        needShowProgress();
        final TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings = req;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4763x68b20e0a(tL_account_updatePasswordSettings, clear, password, new_settings);
            }
        });
    }

    /* renamed from: lambda$setNewPassword$44$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4757xebcd88c5(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4756xfa23e2a6(error);
            }
        });
    }

    /* renamed from: lambda$setNewPassword$43$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4756xfa23e2a6(TLRPC.TL_error error) {
        needHideProgress();
        if (error == null) {
            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
            this.currentPassword.has_recovery = false;
            this.currentPassword.email_unconfirmed_pattern = "";
            fragment.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment, true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
        }
    }

    /* renamed from: lambda$setNewPassword$50$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4763x68b20e0a(TLObject request, final boolean clear, final String password, final TLRPC.TL_account_passwordInputSettings new_settings) {
        byte[] newPasswordHash;
        byte[] newPasswordBytes;
        byte[] bArr;
        if (request instanceof TLRPC.TL_account_updatePasswordSettings) {
            TLRPC.TL_account_updatePasswordSettings req = (TLRPC.TL_account_updatePasswordSettings) request;
            if (req.password == null) {
                req.password = getNewSrpPassword();
            }
        }
        if (!clear && password != null) {
            byte[] newPasswordBytes2 = AndroidUtilities.getStringBytes(password);
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo;
                byte[] newPasswordHash2 = SRPHelper.getX(newPasswordBytes2, algo);
                newPasswordBytes = newPasswordBytes2;
                newPasswordHash = newPasswordHash2;
            } else {
                newPasswordBytes = newPasswordBytes2;
                newPasswordHash = null;
            }
        } else {
            newPasswordBytes = null;
            newPasswordHash = null;
        }
        final byte[] bArr2 = newPasswordHash;
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda43
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationSetupActivity.this.m4762xa41dc760(clear, bArr2, password, new_settings, tLObject, tL_error);
            }
        };
        if (!clear) {
            if (password != null && (bArr = this.currentSecret) != null && bArr.length == 32 && (this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000)) {
                TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) this.currentPassword.new_secure_algo;
                byte[] passwordHash = Utilities.computePBKDF2(newPasswordBytes, newAlgo.salt);
                byte[] key = new byte[32];
                System.arraycopy(passwordHash, 0, key, 0, 32);
                byte[] iv = new byte[16];
                System.arraycopy(passwordHash, 32, iv, 0, 16);
                byte[] encryptedSecret = new byte[32];
                System.arraycopy(this.currentSecret, 0, encryptedSecret, 0, 32);
                Utilities.aesCbcEncryptionByteArraySafe(encryptedSecret, key, iv, 0, encryptedSecret.length, 0, 1);
                new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                new_settings.new_secure_settings.secure_algo = newAlgo;
                new_settings.new_secure_settings.secure_secret = encryptedSecret;
                new_settings.new_secure_settings.secure_secret_id = this.currentSecretId;
                new_settings.flags |= 4;
            }
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (password != null) {
                    TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo2 = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo;
                    new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo2);
                    if (new_settings.new_password_hash == null) {
                        TLRPC.TL_error error = new TLRPC.TL_error();
                        error.text = "ALGO_INVALID";
                        requestDelegate.run(null, error);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, requestDelegate, 10);
                return;
            }
            TLRPC.TL_error error2 = new TLRPC.TL_error();
            error2.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run(null, error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, requestDelegate, 10);
    }

    /* renamed from: lambda$setNewPassword$49$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4762xa41dc760(final boolean clear, final byte[] newPasswordHash, final String password, final TLRPC.TL_account_passwordInputSettings new_settings, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4761xb2742141(error, clear, response, newPasswordHash, password, new_settings);
            }
        });
    }

    /* renamed from: lambda$setNewPassword$48$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4761xb2742141(TLRPC.TL_error error, final boolean clear, TLObject response, final byte[] newPasswordHash, String password, TLRPC.TL_account_passwordInputSettings new_settings) {
        String timeString;
        TLRPC.TL_account_password tL_account_password;
        if (error != null && "SRP_ID_INVALID".equals(error.text)) {
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda42
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationSetupActivity.this.m4759xcf20d503(clear, tLObject, tL_error);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if (error == null && ((response instanceof TLRPC.TL_boolTrue) || (response instanceof TLRPC.auth_Authorization))) {
            getMessagesController().removeSuggestion(0L, "VALIDATE_PASSWORD");
            if (clear) {
                int N = this.fragmentsToClose.size();
                for (int a = 0; a < N; a++) {
                    this.fragmentsToClose.get(a).removeSelfFromStack();
                }
                int a2 = this.currentAccount;
                NotificationCenter.getInstance(a2).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
                finishFragment();
            } else if (getParentActivity() != null) {
                if (this.currentPassword.has_password) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda47
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationSetupActivity.this.m4760xc0ca7b22(newPasswordHash, dialogInterface, i);
                        }
                    });
                    if (password == null && (tL_account_password = this.currentPassword) != null && tL_account_password.has_password) {
                        builder.setMessage(LocaleController.getString("YourEmailSuccessText", R.string.YourEmailSuccessText));
                    } else {
                        builder.setMessage(LocaleController.getString("YourPasswordChangedSuccessText", R.string.YourPasswordChangedSuccessText));
                    }
                    builder.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
                    Dialog dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                    }
                    return;
                }
                int N2 = this.fragmentsToClose.size();
                for (int a3 = 0; a3 < N2; a3++) {
                    this.fragmentsToClose.get(a3).removeSelfFromStack();
                }
                this.currentPassword.has_password = true;
                if (!this.currentPassword.has_recovery) {
                    TLRPC.TL_account_password tL_account_password2 = this.currentPassword;
                    tL_account_password2.has_recovery = !TextUtils.isEmpty(tL_account_password2.email_unconfirmed_pattern);
                }
                if (this.closeAfterSet) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
                }
                TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(7, this.currentPassword);
                fragment.fromRegistration = this.fromRegistration;
                fragment.setCurrentPasswordParams(newPasswordHash != null ? newPasswordHash : this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                fragment.closeAfterSet = this.closeAfterSet;
                fragment.setBlockingAlert(this.otherwiseReloginDays);
                presentFragment(fragment, true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            }
        } else if (error != null) {
            if ("EMAIL_UNCONFIRMED".equals(error.text) || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
                int N3 = this.fragmentsToClose.size();
                for (int a4 = 0; a4 < N3; a4++) {
                    this.fragmentsToClose.get(a4).removeSelfFromStack();
                }
                int a5 = this.currentAccount;
                NotificationCenter notificationCenter = NotificationCenter.getInstance(a5);
                int i = NotificationCenter.twoStepPasswordChanged;
                String str = this.email;
                notificationCenter.postNotificationName(i, newPasswordHash, new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, str, this.hint, str, this.firstPassword);
                this.currentPassword.email_unconfirmed_pattern = this.email;
                TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(5, this.currentPassword);
                fragment2.fromRegistration = this.fromRegistration;
                fragment2.setCurrentPasswordParams(newPasswordHash != null ? newPasswordHash : this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                fragment2.closeAfterSet = this.closeAfterSet;
                fragment2.setBlockingAlert(this.otherwiseReloginDays);
                presentFragment(fragment2, true);
            } else if ("EMAIL_INVALID".equals(error.text)) {
                showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
            } else if (!error.text.startsWith("FLOOD_WAIT")) {
                showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
            } else {
                int time = Utilities.parseInt((CharSequence) error.text).intValue();
                if (time < 60) {
                    timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                } else {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                }
                showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
            }
        }
    }

    /* renamed from: lambda$setNewPassword$46$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4759xcf20d503(final boolean clear, final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                TwoStepVerificationSetupActivity.this.m4758xdd772ee4(error2, response2, clear);
            }
        });
    }

    /* renamed from: lambda$setNewPassword$45$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4758xdd772ee4(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            setNewPassword(clear);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    /* renamed from: lambda$setNewPassword$47$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4760xc0ca7b22(byte[] newPasswordHash, DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        this.currentPassword.has_password = true;
        if (!this.currentPassword.has_recovery) {
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            tL_account_password.has_recovery = !TextUtils.isEmpty(tL_account_password.email_unconfirmed_pattern);
        }
        fragment.setCurrentPasswordParams(this.currentPassword, newPasswordHash != null ? newPasswordHash : this.currentPasswordHash, this.currentSecretId, this.currentSecret);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    protected TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo;
            return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, algo);
        }
        return null;
    }

    protected void onReset() {
    }

    private void onFieldError(View shakeView, TextView field, boolean clear) {
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
        AndroidUtilities.shakeViewSpring(shakeView, 5.0f);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        return themeDescriptions;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        if (this.otherwiseReloginDays >= 0 && this.parentLayout.fragmentsStack.size() == 1) {
            return false;
        }
        return super.isSwipeBackEnabled(event);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.otherwiseReloginDays >= 0 && this.parentLayout.fragmentsStack.size() == 1) {
            showSetForcePasswordAlert();
            return false;
        }
        finishFragment();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void finishFragment(boolean animated) {
        Iterator<BaseFragment> it = getParentLayout().fragmentsStack.iterator();
        while (it.hasNext()) {
            BaseFragment fragment = it.next();
            if (fragment != this && (fragment instanceof TwoStepVerificationSetupActivity)) {
                ((TwoStepVerificationSetupActivity) fragment).floatingAutoAnimator.ignoreNextLayout();
            }
        }
        super.finishFragment(animated);
    }

    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", R.string.Warning));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays, new Object[0]));
        builder.setPositiveButton(LocaleController.getString("TwoStepVerificationSetPassword", R.string.TwoStepVerificationSetPassword), null);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", R.string.ForceSetPasswordCancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda44
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.m4765x6978abd1(dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.show();
        ((TextView) alertDialog.getButton(-2)).setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
    }

    /* renamed from: lambda$showSetForcePasswordAlert$51$org-telegram-ui-TwoStepVerificationSetupActivity */
    public /* synthetic */ void m4765x6978abd1(DialogInterface a1, int a2) {
        finishFragment();
    }

    public void setBlockingAlert(int otherwiseRelogin) {
        this.otherwiseReloginDays = otherwiseRelogin;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void finishFragment() {
        if (this.otherwiseReloginDays >= 0 && this.parentLayout.fragmentsStack.size() == 1) {
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
