package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.BotWebViewMenuContainer;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.PaymentFormActivity;
/* loaded from: classes5.dex */
public class BotWebViewMenuContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewMenuContainer> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewMenuContainer$$ExternalSyntheticLambda12.INSTANCE, BotWebViewMenuContainer$$ExternalSyntheticLambda13.INSTANCE).setMultiplier(100.0f);
    private static final int POLL_PERIOD = 60000;
    private ActionBar.ActionBarMenuOnItemClick actionBarOnItemClick;
    private float actionBarTransitionProgress;
    private long botId;
    private ActionBarMenuItem botMenuItem;
    private String botUrl;
    private SpringAnimation botWebViewButtonAnimator;
    private boolean botWebViewButtonWasVisible;
    private int currentAccount;
    private boolean dismissed;
    private Runnable globalOnDismissListener;
    private boolean ignoreLayout;
    private boolean ignoreMeasure;
    private boolean isLoaded;
    private long lastSwipeTime;
    private int overrideActionBarBackground;
    private float overrideActionBarBackgroundProgress;
    private boolean overrideBackgroundColor;
    private ChatActivityEnterView parentEnterView;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private MessageObject savedEditMessageObject;
    private Editable savedEditText;
    private MessageObject savedReplyMessageObject;
    private ActionBarMenuSubItem settingsItem;
    private SpringAnimation springAnimation;
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;
    private BotWebViewContainer.Delegate webViewDelegate;
    private ValueAnimator webViewScrollAnimator;
    private Paint dimPaint = new Paint();
    private Paint backgroundPaint = new Paint(1);
    private Paint actionBarPaint = new Paint(1);
    private Paint linePaint = new Paint();
    private Runnable pollRunnable = new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda21
        @Override // java.lang.Runnable
        public final void run() {
            BotWebViewMenuContainer.this.m2250lambda$new$4$orgtelegramuiComponentsBotWebViewMenuContainer();
        }
    };

    public static /* synthetic */ void lambda$static$1(BotWebViewMenuContainer obj, float value) {
        obj.actionBarTransitionProgress = value;
        obj.invalidate();
        obj.invalidateActionBar();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2250lambda$new$4$orgtelegramuiComponentsBotWebViewMenuContainer() {
        if (!this.dismissed) {
            TLRPC.TL_messages_prolongWebView prolongWebView = new TLRPC.TL_messages_prolongWebView();
            prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
            prolongWebView.query_id = this.queryId;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(prolongWebView, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotWebViewMenuContainer.this.m2249lambda$new$3$orgtelegramuiComponentsBotWebViewMenuContainer(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2249lambda$new$3$orgtelegramuiComponentsBotWebViewMenuContainer(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewMenuContainer.this.m2248lambda$new$2$orgtelegramuiComponentsBotWebViewMenuContainer(error);
            }
        });
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2248lambda$new$2$orgtelegramuiComponentsBotWebViewMenuContainer(TLRPC.TL_error error) {
        if (this.dismissed) {
            return;
        }
        if (error != null) {
            dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
        }
    }

    public BotWebViewMenuContainer(Context context, final ChatActivityEnterView parentEnterView) {
        super(context);
        this.parentEnterView = parentEnterView;
        ChatActivity chatActivity = parentEnterView.getParentFragment();
        final ActionBar actionBar = chatActivity.getActionBar();
        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem addItem = menu.addItem(1000, R.drawable.ic_ab_other);
        this.botMenuItem = addItem;
        addItem.setVisibility(8);
        this.botMenuItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString((int) R.string.BotWebViewReloadPage));
        this.actionBarOnItemClick = actionBar.getActionBarMenuOnItemClick();
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, parentEnterView.getParentFragment().getResourceProvider(), getColor(Theme.key_windowBackgroundWhite));
        this.webViewContainer = botWebViewContainer;
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(parentEnterView, actionBar);
        this.webViewDelegate = anonymousClass1;
        botWebViewContainer.setDelegate(anonymousClass1);
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(C.BUFFER_FLAG_ENCRYPTED);
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) { // from class: org.telegram.ui.Components.BotWebViewMenuContainer.2
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int availableHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    padding = (int) (availableHeight / 3.5f);
                } else {
                    padding = (availableHeight / 5) * 2;
                }
                if (padding < 0) {
                    padding = 0;
                }
                if (getOffsetY() != padding) {
                    BotWebViewMenuContainer.this.ignoreLayout = true;
                    setOffsetY(padding);
                    BotWebViewMenuContainer.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((((View.MeasureSpec.getSize(heightMeasureSpec) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(24.0f)) - AndroidUtilities.dp(5.0f), C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (BotWebViewMenuContainer.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.swipeContainer = webViewSwipeContainer;
        webViewSwipeContainer.setScrollListener(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewMenuContainer.this.m2251lambda$new$5$orgtelegramuiComponentsBotWebViewMenuContainer(actionBar);
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewMenuContainer.this.m2252lambda$new$6$orgtelegramuiComponentsBotWebViewMenuContainer();
            }
        });
        this.swipeContainer.addView(this.webViewContainer);
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate
            public final void onDismiss() {
                BotWebViewMenuContainer.this.dismiss();
            }
        });
        this.swipeContainer.setTopActionBarOffsetY((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f));
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda7
            @Override // org.telegram.messenger.GenericProvider
            public final Object provide(Object obj) {
                Boolean valueOf;
                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                Void r2 = (Void) obj;
                valueOf = Boolean.valueOf(parentEnterView.getSizeNotifierLayout().getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
                return valueOf;
            }
        });
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 24.0f, 0.0f, 0.0f));
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(context, parentEnterView.getParentFragment().getResourceProvider());
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 5.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda16
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                BotWebViewMenuContainer.this.m2254lambda$new$9$orgtelegramuiComponentsBotWebViewMenuContainer((Float) obj);
            }
        });
        setWillNotDraw(false);
    }

    /* renamed from: org.telegram.ui.Components.BotWebViewMenuContainer$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 implements BotWebViewContainer.Delegate {
        final /* synthetic */ ActionBar val$actionBar;
        final /* synthetic */ ChatActivityEnterView val$parentEnterView;

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onSendWebViewData(String str) {
            BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass1(ChatActivityEnterView chatActivityEnterView, ActionBar actionBar) {
            BotWebViewMenuContainer.this = this$0;
            this.val$parentEnterView = chatActivityEnterView;
            this.val$actionBar = actionBar;
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onCloseRequested(Runnable callback) {
            BotWebViewMenuContainer.this.dismiss(callback);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetActionBarColor(String colorKey) {
            final int from = BotWebViewMenuContainer.this.overrideActionBarBackground;
            final int to = BotWebViewMenuContainer.this.getColor(colorKey);
            if (from == 0) {
                BotWebViewMenuContainer.this.overrideActionBarBackground = to;
            }
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$1$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewMenuContainer.AnonymousClass1.this.m2262x44f631d5(from, to, valueAnimator);
                }
            });
            animator.start();
        }

        /* renamed from: lambda$onWebAppSetActionBarColor$0$org-telegram-ui-Components-BotWebViewMenuContainer$1 */
        public /* synthetic */ void m2262x44f631d5(int from, int to, ValueAnimator animation) {
            if (from != 0) {
                BotWebViewMenuContainer.this.overrideActionBarBackground = ColorUtils.blendARGB(from, to, ((Float) animation.getAnimatedValue()).floatValue());
            } else {
                BotWebViewMenuContainer.this.overrideActionBarBackgroundProgress = ((Float) animation.getAnimatedValue()).floatValue();
            }
            BotWebViewMenuContainer.this.actionBarPaint.setColor(BotWebViewMenuContainer.this.overrideActionBarBackground);
            BotWebViewMenuContainer.this.invalidateActionBar();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetBackgroundColor(final int color) {
            BotWebViewMenuContainer.this.overrideBackgroundColor = true;
            final int from = BotWebViewMenuContainer.this.backgroundPaint.getColor();
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$1$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewMenuContainer.AnonymousClass1.this.m2263x368abaf7(from, color, valueAnimator);
                }
            });
            animator.start();
        }

        /* renamed from: lambda$onWebAppSetBackgroundColor$1$org-telegram-ui-Components-BotWebViewMenuContainer$1 */
        public /* synthetic */ void m2263x368abaf7(int from, int color, ValueAnimator animation) {
            BotWebViewMenuContainer.this.backgroundPaint.setColor(ColorUtils.blendARGB(from, color, ((Float) animation.getAnimatedValue()).floatValue()));
            BotWebViewMenuContainer.this.invalidate();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppExpand() {
            if (!BotWebViewMenuContainer.this.swipeContainer.isSwipeInProgress()) {
                BotWebViewMenuContainer.this.swipeContainer.stickTo((-BotWebViewMenuContainer.this.swipeContainer.getOffsetY()) + BotWebViewMenuContainer.this.swipeContainer.getTopActionBarOffsetY());
            }
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppOpenInvoice(final String slug, TLObject response) {
            ChatActivity parentFragment = this.val$parentEnterView.getParentFragment();
            PaymentFormActivity paymentFormActivity = null;
            if (response instanceof TLRPC.TL_payments_paymentForm) {
                TLRPC.TL_payments_paymentForm form = (TLRPC.TL_payments_paymentForm) response;
                MessagesController.getInstance(BotWebViewMenuContainer.this.currentAccount).putUsers(form.users, false);
                paymentFormActivity = new PaymentFormActivity(form, slug, parentFragment);
            } else if (response instanceof TLRPC.TL_payments_paymentReceipt) {
                paymentFormActivity = new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response);
            }
            if (paymentFormActivity != null) {
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$1$$ExternalSyntheticLambda3
                    @Override // org.telegram.ui.PaymentFormActivity.PaymentFormCallback
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewMenuContainer.AnonymousClass1.this.m2261x23556258(slug, invoiceStatus);
                    }
                });
                parentFragment.presentFragment(paymentFormActivity);
            }
        }

        /* renamed from: lambda$onWebAppOpenInvoice$2$org-telegram-ui-Components-BotWebViewMenuContainer$1 */
        public /* synthetic */ void m2261x23556258(String slug, PaymentFormActivity.InvoiceStatus status) {
            BotWebViewMenuContainer.this.webViewContainer.onInvoiceStatusUpdate(slug, status.name().toLowerCase(Locale.ROOT));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetupMainButton(boolean isVisible, boolean isActive, String text, int color, int textColor, boolean isProgressVisible) {
            ChatActivityBotWebViewButton botWebViewButton = this.val$parentEnterView.getBotWebViewButton();
            botWebViewButton.setupButtonParams(isActive, text, color, textColor, isProgressVisible);
            botWebViewButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$1$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BotWebViewMenuContainer.AnonymousClass1.this.m2260xf2816c25(view);
                }
            });
            if (isVisible != BotWebViewMenuContainer.this.botWebViewButtonWasVisible) {
                BotWebViewMenuContainer.this.animateBotButton(isVisible);
            }
        }

        /* renamed from: lambda$onSetupMainButton$3$org-telegram-ui-Components-BotWebViewMenuContainer$1 */
        public /* synthetic */ void m2260xf2816c25(View v) {
            BotWebViewMenuContainer.this.webViewContainer.onMainButtonPressed();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetBackButtonVisible(boolean visible) {
            if (BotWebViewMenuContainer.this.actionBarTransitionProgress == 1.0f) {
                if (visible) {
                    AndroidUtilities.updateImageViewImageAnimated(this.val$actionBar.getBackButton(), this.val$actionBar.getBackButtonDrawable());
                } else {
                    AndroidUtilities.updateImageViewImageAnimated(this.val$actionBar.getBackButton(), (int) R.drawable.ic_close_white);
                }
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2251lambda$new$5$orgtelegramuiComponentsBotWebViewMenuContainer(ActionBar actionBar) {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - (Math.min(this.swipeContainer.getSwipeOffsetY(), this.swipeContainer.getHeight()) / this.swipeContainer.getHeight())) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float progress = 1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY());
            if (getVisibility() != 0) {
                progress = 0.0f;
            }
            float newPos = (progress > 0.5f ? 1 : 0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != newPos) {
                this.springAnimation.getSpring().setFinalPosition(newPos);
                this.springAnimation.start();
                if (!this.webViewContainer.isBackButtonVisible()) {
                    if (newPos == 100.0f) {
                        AndroidUtilities.updateImageViewImageAnimated(actionBar.getBackButton(), (int) R.drawable.ic_close_white);
                    } else {
                        AndroidUtilities.updateImageViewImageAnimated(actionBar.getBackButton(), actionBar.getBackButtonDrawable());
                    }
                }
            }
        }
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2252lambda$new$6$orgtelegramuiComponentsBotWebViewMenuContainer() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2254lambda$new$9$orgtelegramuiComponentsBotWebViewMenuContainer(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda10
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewMenuContainer.this.m2253lambda$new$8$orgtelegramuiComponentsBotWebViewMenuContainer(valueAnimator);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BotWebViewMenuContainer.this.progressView.setVisibility(8);
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2253lambda$new$8$orgtelegramuiComponentsBotWebViewMenuContainer(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    public void invalidateActionBar() {
        ChatActivity chatActivity = this.parentEnterView.getParentFragment();
        if (chatActivity == null || getVisibility() != 0) {
            return;
        }
        ChatAvatarContainer avatarContainer = chatActivity.getAvatarContainer();
        String subtitleDefaultColorKey = avatarContainer.getLastSubtitleColorKey() == null ? Theme.key_actionBarDefaultSubtitle : avatarContainer.getLastSubtitleColorKey();
        int subtitleColor = ColorUtils.blendARGB(getColor(subtitleDefaultColorKey), getColor(Theme.key_windowBackgroundWhiteGrayText), this.actionBarTransitionProgress);
        ActionBar actionBar = chatActivity.getActionBar();
        int backgroundColor = ColorUtils.blendARGB(getColor(Theme.key_actionBarDefault), getColor(Theme.key_windowBackgroundWhite), this.actionBarTransitionProgress);
        actionBar.setBackgroundColor(backgroundColor);
        actionBar.setItemsColor(ColorUtils.blendARGB(getColor(Theme.key_actionBarDefaultIcon), getColor(Theme.key_windowBackgroundWhiteBlackText), this.actionBarTransitionProgress), false);
        actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(getColor(Theme.key_actionBarDefaultSelector), getColor(Theme.key_actionBarWhiteSelector), this.actionBarTransitionProgress), false);
        actionBar.setSubtitleColor(subtitleColor);
        ChatAvatarContainer chatAvatarContainer = chatActivity.getAvatarContainer();
        chatAvatarContainer.getTitleTextView().setTextColor(ColorUtils.blendARGB(getColor(Theme.key_actionBarDefaultTitle), getColor(Theme.key_windowBackgroundWhiteBlackText), this.actionBarTransitionProgress));
        chatAvatarContainer.getSubtitleTextView().setTextColor(subtitleColor);
        chatAvatarContainer.setOverrideSubtitleColor(this.actionBarTransitionProgress == 0.0f ? null : Integer.valueOf(subtitleColor));
        updateLightStatusBar();
    }

    public boolean onBackPressed() {
        return this.webViewContainer.onBackPressed();
    }

    public void animateBotButton(final boolean isVisible) {
        final ChatActivityBotWebViewButton botWebViewButton = this.parentEnterView.getBotWebViewButton();
        SpringAnimation springAnimation = this.botWebViewButtonAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.botWebViewButtonAnimator = null;
        }
        float f = 0.0f;
        botWebViewButton.setProgress(isVisible ? 0.0f : 1.0f);
        if (isVisible) {
            botWebViewButton.setVisibility(0);
        }
        SpringAnimation springAnimation2 = new SpringAnimation(botWebViewButton, ChatActivityBotWebViewButton.PROGRESS_PROPERTY);
        if (isVisible) {
            f = 1.0f;
        }
        SpringAnimation addEndListener = springAnimation2.setSpring(new SpringForce(f * ChatActivityBotWebViewButton.PROGRESS_PROPERTY.getMultiplier()).setStiffness(isVisible ? 600.0f : 750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda20
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f2, float f3) {
                BotWebViewMenuContainer.this.m2243xd53aad85(dynamicAnimation, f2, f3);
            }
        }).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda19
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f2, float f3) {
                BotWebViewMenuContainer.this.m2244x31347e4(isVisible, botWebViewButton, dynamicAnimation, z, f2, f3);
            }
        });
        this.botWebViewButtonAnimator = addEndListener;
        addEndListener.start();
        this.botWebViewButtonWasVisible = isVisible;
    }

    /* renamed from: lambda$animateBotButton$10$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2243xd53aad85(DynamicAnimation animation, float value, float velocity) {
        float v = value / ChatActivityBotWebViewButton.PROGRESS_PROPERTY.getMultiplier();
        this.parentEnterView.setBotWebViewButtonOffsetX(AndroidUtilities.dp(64.0f) * v);
        this.parentEnterView.setComposeShadowAlpha(1.0f - v);
    }

    /* renamed from: lambda$animateBotButton$11$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2244x31347e4(boolean isVisible, ChatActivityBotWebViewButton botWebViewButton, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (!isVisible) {
            botWebViewButton.setVisibility(8);
        }
        if (this.botWebViewButtonAnimator == animation) {
            this.botWebViewButtonAnimator = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda18
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    BotWebViewMenuContainer.this.m2255xcac8d099(dynamicAnimation, z, f, f2);
                }
            });
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    /* renamed from: lambda$onAttachedToWindow$12$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2255xcac8d099(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        ChatActivity chatActivity = this.parentEnterView.getParentFragment();
        ChatAvatarContainer chatAvatarContainer = chatActivity.getAvatarContainer();
        chatAvatarContainer.setClickable(value == 0.0f);
        chatAvatarContainer.getAvatarImageView().setClickable(value == 0.0f);
        ActionBar actionBar = chatActivity.getActionBar();
        if (value == 100.0f && this.parentEnterView.hasBotWebView()) {
            chatActivity.showHeaderItem(false);
            this.botMenuItem.setVisibility(0);
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer.4
                @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
                public void onItemClick(int id) {
                    if (id == -1) {
                        if (!BotWebViewMenuContainer.this.webViewContainer.onBackPressed()) {
                            BotWebViewMenuContainer.this.dismiss();
                        }
                    } else if (id == R.id.menu_reload_page) {
                        if (BotWebViewMenuContainer.this.webViewContainer.getWebView() != null) {
                            BotWebViewMenuContainer.this.webViewContainer.getWebView().animate().cancel();
                            BotWebViewMenuContainer.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                        }
                        BotWebViewMenuContainer.this.isLoaded = false;
                        BotWebViewMenuContainer.this.progressView.setLoadProgress(0.0f);
                        BotWebViewMenuContainer.this.progressView.setAlpha(1.0f);
                        BotWebViewMenuContainer.this.progressView.setVisibility(0);
                        BotWebViewMenuContainer.this.webViewContainer.setBotUser(MessagesController.getInstance(BotWebViewMenuContainer.this.currentAccount).getUser(Long.valueOf(BotWebViewMenuContainer.this.botId)));
                        BotWebViewMenuContainer.this.webViewContainer.loadFlickerAndSettingsItem(BotWebViewMenuContainer.this.currentAccount, BotWebViewMenuContainer.this.botId, BotWebViewMenuContainer.this.settingsItem);
                        BotWebViewMenuContainer.this.webViewContainer.reload();
                    } else if (id == R.id.menu_settings) {
                        BotWebViewMenuContainer.this.webViewContainer.onSettingsButtonPressed();
                    }
                }
            });
            return;
        }
        chatActivity.showHeaderItem(true);
        this.botMenuItem.setVisibility(8);
        actionBar.setActionBarMenuOnItemClick(this.actionBarOnItemClick);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation = this.springAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.springAnimation = null;
        }
        this.actionBarTransitionProgress = 0.0f;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.ignoreMeasure) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void onPanTransitionStart(boolean keyboardVisible, int contentHeight) {
        if (!keyboardVisible) {
            return;
        }
        boolean doNotScroll = false;
        float openOffset = (-this.swipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY();
        if (this.swipeContainer.getSwipeOffsetY() != openOffset) {
            this.swipeContainer.stickTo(openOffset);
            doNotScroll = true;
        }
        int oldh = this.parentEnterView.getSizeNotifierLayout().measureKeyboardHeight() + contentHeight;
        setMeasuredDimension(getMeasuredWidth(), contentHeight);
        this.ignoreMeasure = true;
        if (!doNotScroll) {
            ValueAnimator valueAnimator = this.webViewScrollAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.webViewScrollAnimator = null;
            }
            if (this.webViewContainer.getWebView() != null) {
                int fromY = this.webViewContainer.getWebView().getScrollY();
                final int toY = (oldh - contentHeight) + fromY;
                ValueAnimator duration = ValueAnimator.ofInt(fromY, toY).setDuration(250L);
                this.webViewScrollAnimator = duration;
                duration.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                this.webViewScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda15
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        BotWebViewMenuContainer.this.m2259xba1f3ad9(valueAnimator2);
                    }
                });
                this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (BotWebViewMenuContainer.this.webViewContainer.getWebView() != null) {
                            BotWebViewMenuContainer.this.webViewContainer.getWebView().setScrollY(toY);
                        }
                        if (animation == BotWebViewMenuContainer.this.webViewScrollAnimator) {
                            BotWebViewMenuContainer.this.webViewScrollAnimator = null;
                        }
                    }
                });
                this.webViewScrollAnimator.start();
            }
        }
    }

    /* renamed from: lambda$onPanTransitionStart$13$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2259xba1f3ad9(ValueAnimator animation) {
        int val = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().setScrollY(val);
        }
    }

    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        requestLayout();
    }

    private void updateLightStatusBar() {
        int flags;
        boolean z = true;
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        if (ColorUtils.calculateLuminance(color) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
            z = false;
        }
        boolean lightStatusBar = z;
        Boolean bool = this.wasLightStatusBar;
        if (bool != null && bool.booleanValue() == lightStatusBar) {
            return;
        }
        this.wasLightStatusBar = Boolean.valueOf(lightStatusBar);
        if (Build.VERSION.SDK_INT >= 23) {
            int flags2 = getSystemUiVisibility();
            if (lightStatusBar) {
                flags = flags2 | 8192;
            } else {
                flags = flags2 & (-8193);
            }
            setSystemUiVisibility(flags);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.overrideBackgroundColor) {
            this.backgroundPaint.setColor(getColor(Theme.key_windowBackgroundWhite));
        }
        if (this.overrideActionBarBackgroundProgress == 0.0f) {
            this.actionBarPaint.setColor(getColor(Theme.key_windowBackgroundWhite));
        }
        AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth(), getHeight());
        canvas.drawRect(AndroidUtilities.rectTmp, this.dimPaint);
        float radius = AndroidUtilities.dp(16.0f) * (1.0f - this.actionBarTransitionProgress);
        AndroidUtilities.rectTmp.set(0.0f, AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), 0.0f, this.actionBarTransitionProgress), getWidth(), this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + radius);
        canvas.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, this.actionBarPaint);
        AndroidUtilities.rectTmp.set(0.0f, this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), getWidth(), getHeight() + radius);
        canvas.drawRect(AndroidUtilities.rectTmp, this.backgroundPaint);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0 && event.getY() <= AndroidUtilities.lerp(this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, this.actionBarTransitionProgress)) {
            dismiss();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.linePaint.setColor(getColor(Theme.key_sheet_scrollUp));
        Paint paint = this.linePaint;
        paint.setAlpha((int) (paint.getAlpha() * (1.0f - (Math.min(0.5f, this.actionBarTransitionProgress) / 0.5f))));
        canvas.save();
        float scale = 1.0f - this.actionBarTransitionProgress;
        float y = AndroidUtilities.lerp(this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), this.actionBarTransitionProgress) + AndroidUtilities.dp(12.0f);
        canvas.scale(scale, scale, getWidth() / 2.0f, y);
        canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), y, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), y, this.linePaint);
        canvas.restore();
    }

    public void show(int currentAccount, long botId, String botUrl) {
        this.dismissed = false;
        if (this.currentAccount != currentAccount || this.botId != botId || !ColorUtils$$ExternalSyntheticBackport0.m(this.botUrl, botUrl)) {
            this.isLoaded = false;
        }
        this.currentAccount = currentAccount;
        this.botId = botId;
        this.botUrl = botUrl;
        this.savedEditText = this.parentEnterView.getEditField().getText();
        this.parentEnterView.getEditField().setText((CharSequence) null);
        this.savedReplyMessageObject = this.parentEnterView.getReplyingMessageObject();
        this.savedEditMessageObject = this.parentEnterView.getEditingMessageObject();
        ChatActivity chatActivity = this.parentEnterView.getParentFragment();
        if (chatActivity != null) {
            chatActivity.hideFieldPanel(true);
        }
        if (!this.isLoaded) {
            loadWebView();
        }
        setVisibility(0);
        setAlpha(0.0f);
        addOnLayoutChangeListener(new AnonymousClass6());
    }

    /* renamed from: org.telegram.ui.Components.BotWebViewMenuContainer$6 */
    /* loaded from: classes5.dex */
    public class AnonymousClass6 implements View.OnLayoutChangeListener {
        AnonymousClass6() {
            BotWebViewMenuContainer.this = this$0;
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            v.removeOnLayoutChangeListener(this);
            BotWebViewMenuContainer.this.swipeContainer.setSwipeOffsetY(BotWebViewMenuContainer.this.swipeContainer.getHeight());
            BotWebViewMenuContainer.this.setAlpha(1.0f);
            new SpringAnimation(BotWebViewMenuContainer.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$6$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    BotWebViewMenuContainer.AnonymousClass6.this.m2264xd1dae4c3(dynamicAnimation, z, f, f2);
                }
            }).start();
        }

        /* renamed from: lambda$onLayoutChange$0$org-telegram-ui-Components-BotWebViewMenuContainer$6 */
        public /* synthetic */ void m2264xd1dae4c3(DynamicAnimation animation, boolean canceled, float value, float velocity) {
            BotWebViewMenuContainer.this.webViewContainer.restoreButtonData();
            BotWebViewMenuContainer.this.webViewContainer.invalidateViewPortHeight(true);
        }
    }

    private void loadWebView() {
        this.progressView.setLoadProgress(0.0f);
        this.progressView.setAlpha(1.0f);
        this.progressView.setVisibility(0);
        this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
        this.webViewContainer.loadFlickerAndSettingsItem(this.currentAccount, this.botId, this.settingsItem);
        TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
        req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.botId);
        req.url = this.botUrl;
        req.flags |= 2;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getColor(Theme.key_windowBackgroundWhite));
            jsonObject.put("secondary_bg_color", getColor(Theme.key_windowBackgroundGray));
            jsonObject.put("text_color", getColor(Theme.key_windowBackgroundWhiteBlackText));
            jsonObject.put("hint_color", getColor(Theme.key_windowBackgroundWhiteHintText));
            jsonObject.put("link_color", getColor(Theme.key_windowBackgroundWhiteLinkText));
            jsonObject.put("button_color", getColor(Theme.key_featuredStickers_addButton));
            jsonObject.put("button_text_color", getColor(Theme.key_featuredStickers_buttonText));
            req.theme_params = new TLRPC.TL_dataJSON();
            req.theme_params.data = jsonObject.toString();
            req.flags |= 4;
        } catch (Exception e) {
            FileLog.e(e);
        }
        req.from_bot_menu = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotWebViewMenuContainer.this.m2247x422f2861(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadWebView$15$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2247x422f2861(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewMenuContainer.this.m2246x14568e02(response);
            }
        });
    }

    /* renamed from: lambda$loadWebView$14$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2246x14568e02(TLObject response) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            this.isLoaded = true;
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(this.currentAccount, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
        }
    }

    public int getColor(String key) {
        Integer color;
        Theme.ResourcesProvider resourcesProvider = this.parentEnterView.getParentFragment().getResourceProvider();
        if (resourcesProvider != null) {
            color = resourcesProvider.getColor(key);
        } else {
            color = Integer.valueOf(Theme.getColor(key));
        }
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setOnDismissGlobalListener(Runnable callback) {
        this.globalOnDismissListener = callback;
    }

    public void dismiss() {
        dismiss(null);
    }

    public void dismiss(final Runnable callback) {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo(webViewSwipeContainer.getHeight() + this.parentEnterView.getSizeNotifierLayout().measureKeyboardHeight(), new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewMenuContainer.this.m2245x30fd9b37(callback);
            }
        });
    }

    /* renamed from: lambda$dismiss$16$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2245x30fd9b37(Runnable callback) {
        onDismiss();
        if (callback != null) {
            callback.run();
        }
        Runnable runnable = this.globalOnDismissListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void onDismiss() {
        setVisibility(8);
        this.overrideActionBarBackground = 0;
        this.overrideActionBarBackgroundProgress = 0.0f;
        this.actionBarPaint.setColor(getColor(Theme.key_windowBackgroundWhite));
        this.webViewContainer.destroyWebView();
        this.swipeContainer.removeView(this.webViewContainer);
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(getContext(), this.parentEnterView.getParentFragment().getResourceProvider(), getColor(Theme.key_windowBackgroundWhite));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(this.webViewDelegate);
        this.webViewContainer.setWebViewProgressListener(new Consumer() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda17
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                BotWebViewMenuContainer.this.m2257xaef3a196((Float) obj);
            }
        });
        this.swipeContainer.addView(this.webViewContainer);
        this.isLoaded = false;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        boolean delayRestoreText = this.botWebViewButtonWasVisible;
        if (this.botWebViewButtonWasVisible) {
            this.botWebViewButtonWasVisible = false;
            animateBotButton(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewMenuContainer.this.m2258xdccc3bf5();
            }
        }, delayRestoreText ? 200L : 0L);
    }

    /* renamed from: lambda$onDismiss$18$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2257xaef3a196(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda14
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewMenuContainer.this.m2256x811b0737(valueAnimator);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BotWebViewMenuContainer.this.progressView.setVisibility(8);
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$onDismiss$17$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2256x811b0737(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* renamed from: lambda$onDismiss$19$org-telegram-ui-Components-BotWebViewMenuContainer */
    public /* synthetic */ void m2258xdccc3bf5() {
        if (this.savedEditText != null) {
            this.parentEnterView.getEditField().setText(this.savedEditText);
            this.savedEditText = null;
        }
        if (this.savedReplyMessageObject != null) {
            ChatActivity chatActivity = this.parentEnterView.getParentFragment();
            if (chatActivity != null) {
                chatActivity.showFieldPanelForReply(this.savedReplyMessageObject);
            }
            this.savedReplyMessageObject = null;
        }
        if (this.savedEditMessageObject != null) {
            ChatActivity chatActivity2 = this.parentEnterView.getParentFragment();
            if (chatActivity2 != null) {
                chatActivity2.showFieldPanelForEdit(true, this.savedEditMessageObject);
            }
            this.savedEditMessageObject = null;
        }
    }

    public boolean hasSavedText() {
        return (this.savedEditText == null && this.savedReplyMessageObject == null && this.savedEditMessageObject == null) ? false : true;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            long queryId = ((Long) args[0]).longValue();
            if (this.queryId == queryId) {
                dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            this.webViewContainer.updateFlickerBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            invalidate();
            invalidateActionBar();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewMenuContainer$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BotWebViewMenuContainer.this.invalidateActionBar();
                }
            }, 300L);
        }
    }
}
