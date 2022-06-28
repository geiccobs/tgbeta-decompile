package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.BotWebViewSheet;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
/* loaded from: classes5.dex */
public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda9.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda10.INSTANCE).setMultiplier(100.0f);
    private static final int POLL_PERIOD = 60000;
    public static final int TYPE_BOT_MENU_BUTTON = 2;
    public static final int TYPE_SIMPLE_WEB_VIEW_BUTTON = 1;
    public static final int TYPE_WEB_VIEW_BUTTON = 0;
    private ActionBar actionBar;
    private long botId;
    private String buttonText;
    private int currentAccount;
    private boolean dismissed;
    private SizeNotifierFrameLayout frameLayout;
    private boolean ignoreLayout;
    private long lastSwipeTime;
    private TextView mainButton;
    private VerticalPositionAutoAnimator mainButtonAutoAnimator;
    private boolean mainButtonProgressWasVisible;
    private boolean mainButtonWasVisible;
    private boolean overrideBackgroundColor;
    private Activity parentActivity;
    private long peerId;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private VerticalPositionAutoAnimator radialProgressAutoAnimator;
    private RadialProgressView radialProgressView;
    private int replyToMsgId;
    private Theme.ResourcesProvider resourcesProvider;
    private ActionBarMenuSubItem settingsItem;
    private boolean silent;
    private SpringAnimation springAnimation;
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;
    private float actionBarTransitionProgress = 0.0f;
    private Paint linePaint = new Paint(1);
    private Paint dimPaint = new Paint();
    private Paint backgroundPaint = new Paint(1);
    private Paint actionBarPaint = new Paint(1);
    private Runnable pollRunnable = new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda16
        @Override // java.lang.Runnable
        public final void run() {
            BotWebViewSheet.this.m2270lambda$new$4$orgtelegramuiComponentsBotWebViewSheet();
        }
    };
    private int actionBarColor = getColor(Theme.key_windowBackgroundWhite);
    private Drawable actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface WebViewType {
    }

    public static /* synthetic */ void lambda$static$1(BotWebViewSheet obj, float value) {
        obj.actionBarTransitionProgress = value;
        obj.frameLayout.invalidate();
        obj.actionBar.setAlpha(value);
        obj.updateLightStatusBar();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2270lambda$new$4$orgtelegramuiComponentsBotWebViewSheet() {
        if (!this.dismissed) {
            TLRPC.TL_messages_prolongWebView prolongWebView = new TLRPC.TL_messages_prolongWebView();
            prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
            prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
            prolongWebView.query_id = this.queryId;
            prolongWebView.silent = this.silent;
            int i = this.replyToMsgId;
            if (i != 0) {
                prolongWebView.reply_to_msg_id = i;
                prolongWebView.flags |= 1;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(prolongWebView, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotWebViewSheet.this.m2269lambda$new$3$orgtelegramuiComponentsBotWebViewSheet(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2269lambda$new$3$orgtelegramuiComponentsBotWebViewSheet(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2268lambda$new$2$orgtelegramuiComponentsBotWebViewSheet(error);
            }
        });
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2268lambda$new$2$orgtelegramuiComponentsBotWebViewSheet(TLRPC.TL_error error) {
        if (this.dismissed) {
            return;
        }
        if (error != null) {
            dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
        }
    }

    public BotWebViewSheet(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.resourcesProvider = resourcesProvider;
        this.swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) { // from class: org.telegram.ui.Components.BotWebViewSheet.1
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
                int i = 0;
                if (getOffsetY() != padding && !BotWebViewSheet.this.dismissed) {
                    BotWebViewSheet.this.ignoreLayout = true;
                    setOffsetY(padding);
                    BotWebViewSheet.this.ignoreLayout = false;
                }
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), C.BUFFER_FLAG_ENCRYPTED);
                }
                int size = ((View.MeasureSpec.getSize(heightMeasureSpec) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(24.0f);
                if (BotWebViewSheet.this.mainButtonWasVisible) {
                    i = BotWebViewSheet.this.mainButton.getLayoutParams().height;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(size - i, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (BotWebViewSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor(Theme.key_windowBackgroundWhite));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(new AnonymousClass2(context, resourcesProvider));
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(C.BUFFER_FLAG_ENCRYPTED);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.Components.BotWebViewSheet.3
            {
                BotWebViewSheet.this = this;
                setWillNotDraw(false);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (!BotWebViewSheet.this.overrideBackgroundColor) {
                    BotWebViewSheet.this.backgroundPaint.setColor(BotWebViewSheet.this.getColor(Theme.key_windowBackgroundWhite));
                }
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth(), getHeight());
                canvas.drawRect(AndroidUtilities.rectTmp, BotWebViewSheet.this.dimPaint);
                BotWebViewSheet.this.actionBarPaint.setColor(ColorUtils.blendARGB(BotWebViewSheet.this.actionBarColor, BotWebViewSheet.this.getColor(Theme.key_windowBackgroundWhite), BotWebViewSheet.this.actionBarTransitionProgress));
                float dp = AndroidUtilities.dp(16.0f);
                float f = 1.0f;
                if (!AndroidUtilities.isTablet()) {
                    f = 1.0f - BotWebViewSheet.this.actionBarTransitionProgress;
                }
                float radius = dp * f;
                AndroidUtilities.rectTmp.set(BotWebViewSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress), BotWebViewSheet.this.swipeContainer.getRight(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + radius);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, BotWebViewSheet.this.actionBarPaint);
                AndroidUtilities.rectTmp.set(BotWebViewSheet.this.swipeContainer.getLeft(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewSheet.this.swipeContainer.getRight(), getHeight());
                canvas.drawRect(AndroidUtilities.rectTmp, BotWebViewSheet.this.backgroundPaint);
            }

            @Override // android.view.View
            public void draw(Canvas canvas) {
                super.draw(canvas);
                float transitionProgress = AndroidUtilities.isTablet() ? 0.0f : BotWebViewSheet.this.actionBarTransitionProgress;
                BotWebViewSheet.this.linePaint.setColor(Theme.getColor(Theme.key_sheet_scrollUp));
                BotWebViewSheet.this.linePaint.setAlpha((int) (BotWebViewSheet.this.linePaint.getAlpha() * (1.0f - (Math.min(0.5f, transitionProgress) / 0.5f))));
                canvas.save();
                float scale = 1.0f - transitionProgress;
                float y = AndroidUtilities.isTablet() ? AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(12.0f), AndroidUtilities.statusBarHeight / 2.0f, BotWebViewSheet.this.actionBarTransitionProgress) : AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), transitionProgress) + AndroidUtilities.dp(12.0f);
                canvas.scale(scale, scale, getWidth() / 2.0f, y);
                canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), y, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), y, BotWebViewSheet.this.linePaint);
                canvas.restore();
                BotWebViewSheet.this.actionBarShadow.setAlpha((int) (BotWebViewSheet.this.actionBar.getAlpha() * 255.0f));
                float y2 = BotWebViewSheet.this.actionBar.getY() + BotWebViewSheet.this.actionBar.getTranslationY() + BotWebViewSheet.this.actionBar.getHeight();
                BotWebViewSheet.this.actionBarShadow.setBounds(0, (int) y2, getWidth(), (int) (BotWebViewSheet.this.actionBarShadow.getIntrinsicHeight() + y2));
                BotWebViewSheet.this.actionBarShadow.draw(canvas);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0 && (event.getY() <= AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress) || event.getX() > BotWebViewSheet.this.swipeContainer.getRight() || event.getX() < BotWebViewSheet.this.swipeContainer.getLeft())) {
                    BotWebViewSheet.this.dismiss();
                    return true;
                }
                return super.onTouchEvent(event);
            }
        };
        this.frameLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
            public final void onSizeChanged(int i, boolean z) {
                BotWebViewSheet.this.m2271lambda$new$5$orgtelegramuiComponentsBotWebViewSheet(i, z);
            }
        });
        this.frameLayout.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 24.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.BotWebViewSheet.4
            @Override // android.widget.TextView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), C.BUFFER_FLAG_ENCRYPTED);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        this.mainButton = textView;
        textView.setVisibility(8);
        this.mainButton.setAlpha(0.0f);
        this.mainButton.setSingleLine();
        this.mainButton.setGravity(17);
        this.mainButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int padding = AndroidUtilities.dp(16.0f);
        this.mainButton.setPadding(padding, 0, padding, 0);
        this.mainButton.setTextSize(1, 14.0f);
        this.mainButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda13
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BotWebViewSheet.this.m2272lambda$new$6$orgtelegramuiComponentsBotWebViewSheet(view);
            }
        });
        this.frameLayout.addView(this.mainButton, LayoutHelper.createFrame(-1, 48, 81));
        this.mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(this.mainButton);
        RadialProgressView radialProgressView = new RadialProgressView(context) { // from class: org.telegram.ui.Components.BotWebViewSheet.5
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    params.rightMargin = (int) (AndroidUtilities.dp(10.0f) + (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.1f));
                } else {
                    params.rightMargin = AndroidUtilities.dp(10.0f);
                }
            }
        };
        this.radialProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(8);
        this.frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        this.radialProgressAutoAnimator = VerticalPositionAutoAnimator.attach(this.radialProgressView);
        ActionBar actionBar = new ActionBar(context, resourcesProvider) { // from class: org.telegram.ui.Components.BotWebViewSheet.6
            @Override // org.telegram.ui.ActionBar.ActionBar, android.widget.FrameLayout, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), C.BUFFER_FLAG_ENCRYPTED);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(0);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.BotWebViewSheet.7
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    BotWebViewSheet.this.dismiss();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        this.frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 49));
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.frameLayout;
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(context, resourcesProvider) { // from class: org.telegram.ui.Components.BotWebViewSheet.8
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), C.BUFFER_FLAG_ENCRYPTED);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        this.progressView = webProgressView;
        sizeNotifierFrameLayout2.addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda14
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                BotWebViewSheet.this.m2274lambda$new$8$orgtelegramuiComponentsBotWebViewSheet((Float) obj);
            }
        });
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2275lambda$new$9$orgtelegramuiComponentsBotWebViewSheet();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2266lambda$new$10$orgtelegramuiComponentsBotWebViewSheet();
            }
        });
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate
            public final void onDismiss() {
                BotWebViewSheet.this.dismiss();
            }
        });
        this.swipeContainer.setTopActionBarOffsetY((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f));
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.GenericProvider
            public final Object provide(Object obj) {
                return BotWebViewSheet.this.m2267lambda$new$11$orgtelegramuiComponentsBotWebViewSheet((Void) obj);
            }
        });
        setContentView(this.frameLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    /* renamed from: org.telegram.ui.Components.BotWebViewSheet$2 */
    /* loaded from: classes5.dex */
    public class AnonymousClass2 implements BotWebViewContainer.Delegate {
        private boolean sentWebViewData;
        final /* synthetic */ Context val$context;
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass2(Context context, Theme.ResourcesProvider resourcesProvider) {
            BotWebViewSheet.this = this$0;
            this.val$context = context;
            this.val$resourcesProvider = resourcesProvider;
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onCloseRequested(Runnable callback) {
            BotWebViewSheet.this.dismiss(callback);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSendWebViewData(String data) {
            if (BotWebViewSheet.this.queryId != 0 || this.sentWebViewData) {
                return;
            }
            this.sentWebViewData = true;
            TLRPC.TL_messages_sendWebViewData sendWebViewData = new TLRPC.TL_messages_sendWebViewData();
            sendWebViewData.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
            sendWebViewData.random_id = Utilities.random.nextLong();
            sendWebViewData.button_text = BotWebViewSheet.this.buttonText;
            sendWebViewData.data = data;
            ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(sendWebViewData, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BotWebViewSheet.AnonymousClass2.this.m2282x8641f313(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$onSendWebViewData$0$org-telegram-ui-Components-BotWebViewSheet$2 */
        public /* synthetic */ void m2282x8641f313(TLObject response, TLRPC.TL_error error) {
            if (response instanceof TLRPC.TL_updates) {
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).processUpdates((TLRPC.TL_updates) response, false);
            }
            final BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BotWebViewSheet.this.dismiss();
                }
            });
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetActionBarColor(String colorKey) {
            final int from = BotWebViewSheet.this.actionBarColor;
            final int to = BotWebViewSheet.this.getColor(colorKey);
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.AnonymousClass2.this.m2284x766285b2(from, to, valueAnimator);
                }
            });
            animator.start();
        }

        /* renamed from: lambda$onWebAppSetActionBarColor$1$org-telegram-ui-Components-BotWebViewSheet$2 */
        public /* synthetic */ void m2284x766285b2(int from, int to, ValueAnimator animation) {
            BotWebViewSheet.this.actionBarColor = ColorUtils.blendARGB(from, to, ((Float) animation.getAnimatedValue()).floatValue());
            BotWebViewSheet.this.frameLayout.invalidate();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetBackgroundColor(final int color) {
            BotWebViewSheet.this.overrideBackgroundColor = true;
            final int from = BotWebViewSheet.this.backgroundPaint.getColor();
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.AnonymousClass2.this.m2285x6adb50d4(from, color, valueAnimator);
                }
            });
            animator.start();
        }

        /* renamed from: lambda$onWebAppSetBackgroundColor$2$org-telegram-ui-Components-BotWebViewSheet$2 */
        public /* synthetic */ void m2285x6adb50d4(int from, int color, ValueAnimator animation) {
            BotWebViewSheet.this.backgroundPaint.setColor(ColorUtils.blendARGB(from, color, ((Float) animation.getAnimatedValue()).floatValue()));
            BotWebViewSheet.this.frameLayout.invalidate();
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetBackButtonVisible(boolean visible) {
            AndroidUtilities.updateImageViewImageAnimated(BotWebViewSheet.this.actionBar.getBackButton(), visible ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppOpenInvoice(final String slug, TLObject response) {
            BaseFragment parentFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
            PaymentFormActivity paymentFormActivity = null;
            if (response instanceof TLRPC.TL_payments_paymentForm) {
                TLRPC.TL_payments_paymentForm form = (TLRPC.TL_payments_paymentForm) response;
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).putUsers(form.users, false);
                paymentFormActivity = new PaymentFormActivity(form, slug, parentFragment);
            } else if (response instanceof TLRPC.TL_payments_paymentReceipt) {
                paymentFormActivity = new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response);
            }
            if (paymentFormActivity != null) {
                BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
                AndroidUtilities.hideKeyboard(BotWebViewSheet.this.frameLayout);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(this.val$context, this.val$resourcesProvider);
                overlayActionBarLayoutDialog.show();
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() { // from class: org.telegram.ui.Components.BotWebViewSheet$2$$ExternalSyntheticLambda4
                    @Override // org.telegram.ui.PaymentFormActivity.PaymentFormCallback
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewSheet.AnonymousClass2.this.m2283xbbcfe935(overlayActionBarLayoutDialog, slug, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(this.val$resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        /* renamed from: lambda$onWebAppOpenInvoice$3$org-telegram-ui-Components-BotWebViewSheet$2 */
        public /* synthetic */ void m2283xbbcfe935(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String slug, PaymentFormActivity.InvoiceStatus status) {
            overlayActionBarLayoutDialog.dismiss();
            BotWebViewSheet.this.webViewContainer.onInvoiceStatusUpdate(slug, status.name().toLowerCase(Locale.ROOT));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppExpand() {
            if (!BotWebViewSheet.this.swipeContainer.isSwipeInProgress()) {
                BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
            }
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetupMainButton(final boolean isVisible, boolean isActive, String text, int color, int textColor, final boolean isProgressVisible) {
            BotWebViewSheet.this.mainButton.setClickable(isActive);
            BotWebViewSheet.this.mainButton.setText(text);
            BotWebViewSheet.this.mainButton.setTextColor(textColor);
            BotWebViewSheet.this.mainButton.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(color));
            float f = 1.0f;
            float f2 = 0.0f;
            if (isVisible != BotWebViewSheet.this.mainButtonWasVisible) {
                BotWebViewSheet.this.mainButtonWasVisible = isVisible;
                BotWebViewSheet.this.mainButton.animate().cancel();
                if (isVisible) {
                    BotWebViewSheet.this.mainButton.setAlpha(0.0f);
                    BotWebViewSheet.this.mainButton.setVisibility(0);
                }
                BotWebViewSheet.this.mainButton.animate().alpha(isVisible ? 1.0f : 0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewSheet.2.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (!isVisible) {
                            BotWebViewSheet.this.mainButton.setVisibility(8);
                        }
                        BotWebViewSheet.this.swipeContainer.requestLayout();
                    }
                }).start();
            }
            BotWebViewSheet.this.radialProgressView.setProgressColor(textColor);
            if (isProgressVisible != BotWebViewSheet.this.mainButtonProgressWasVisible) {
                BotWebViewSheet.this.mainButtonProgressWasVisible = isProgressVisible;
                BotWebViewSheet.this.radialProgressView.animate().cancel();
                if (isProgressVisible) {
                    BotWebViewSheet.this.radialProgressView.setAlpha(0.0f);
                    BotWebViewSheet.this.radialProgressView.setVisibility(0);
                }
                ViewPropertyAnimator animate = BotWebViewSheet.this.radialProgressView.animate();
                if (isProgressVisible) {
                    f2 = 1.0f;
                }
                ViewPropertyAnimator scaleX = animate.alpha(f2).scaleX(isProgressVisible ? 1.0f : 0.1f);
                if (!isProgressVisible) {
                    f = 0.1f;
                }
                scaleX.scaleY(f).setDuration(250L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewSheet.2.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (!isProgressVisible) {
                            BotWebViewSheet.this.radialProgressView.setVisibility(8);
                        }
                    }
                }).start();
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2271lambda$new$5$orgtelegramuiComponentsBotWebViewSheet(int keyboardHeight, boolean isWidthGreater) {
        if (keyboardHeight > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2272lambda$new$6$orgtelegramuiComponentsBotWebViewSheet(View v) {
        this.webViewContainer.onMainButtonPressed();
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2274lambda$new$8$orgtelegramuiComponentsBotWebViewSheet(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.this.m2273lambda$new$7$orgtelegramuiComponentsBotWebViewSheet(valueAnimator);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewSheet.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BotWebViewSheet.this.progressView.setVisibility(8);
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2273lambda$new$7$orgtelegramuiComponentsBotWebViewSheet(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2275lambda$new$9$orgtelegramuiComponentsBotWebViewSheet() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / this.swipeContainer.getHeight(), 0.0f, 1.0f)) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.frameLayout.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float progress = 1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY());
            float newPos = (progress > 0.5f ? 1 : 0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != newPos) {
                this.springAnimation.getSpring().setFinalPosition(newPos);
                this.springAnimation.start();
            }
        }
        float offsetY = Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        this.mainButtonAutoAnimator.setOffsetY(offsetY);
        this.radialProgressAutoAnimator.setOffsetY(offsetY);
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2266lambda$new$10$orgtelegramuiComponentsBotWebViewSheet() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ Boolean m2267lambda$new$11$orgtelegramuiComponentsBotWebViewSheet(Void obj) {
        return Boolean.valueOf(this.frameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    private void updateActionBarColors() {
        this.actionBar.setTitleColor(getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.actionBar.setItemsColor(getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setItemsBackgroundColor(getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setPopupBackgroundColor(getColor(Theme.key_actionBarDefaultSubmenuBackground), false);
        this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItem), false, false);
        this.actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, false);
        this.actionBar.setPopupItemsSelectorColor(getColor(Theme.key_dialogButtonSelector), false);
    }

    private void updateLightStatusBar() {
        int flags;
        boolean z = true;
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        if (AndroidUtilities.isTablet() || ColorUtils.calculateLuminance(color) < 0.9d || this.actionBarTransitionProgress < 0.85f) {
            z = false;
        }
        boolean lightStatusBar = z;
        Boolean bool = this.wasLightStatusBar;
        if (bool != null && bool.booleanValue() == lightStatusBar) {
            return;
        }
        this.wasLightStatusBar = Boolean.valueOf(lightStatusBar);
        if (Build.VERSION.SDK_INT >= 23) {
            int flags2 = this.frameLayout.getSystemUiVisibility();
            if (lightStatusBar) {
                flags = flags2 | 8192;
            } else {
                flags = flags2 & (-8193);
            }
            this.frameLayout.setSystemUiVisibility(flags);
        }
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            window.addFlags(-2147483392);
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(-2147417856);
        }
        window.setWindowAnimations(R.style.DialogNoAnimation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        params.softInputMode = 16;
        params.height = -1;
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(params);
        if (Build.VERSION.SDK_INT >= 23) {
            window.setStatusBarColor(0);
        }
        this.frameLayout.setSystemUiVisibility(1280);
        if (Build.VERSION.SDK_INT >= 21) {
            this.frameLayout.setOnApplyWindowInsetsListener(BotWebViewSheet$$ExternalSyntheticLambda11.INSTANCE);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
            if (ColorUtils.calculateLuminance(color) < 0.9d) {
                z = false;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation = this.springAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.springAnimation = null;
        }
    }

    public void requestWebView(final int currentAccount, long peerId, final long botId, String buttonText, String buttonUrl, int type, int replyToMsgId, boolean silent) {
        this.currentAccount = currentAccount;
        this.peerId = peerId;
        this.botId = botId;
        this.replyToMsgId = replyToMsgId;
        this.silent = silent;
        this.buttonText = buttonText;
        this.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(currentAccount).getUser(Long.valueOf(botId))));
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.removeAllViews();
        ActionBarMenuItem otherItem = menu.addItem(0, R.drawable.ic_ab_other);
        otherItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString((int) R.string.BotWebViewOpenBot));
        otherItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString((int) R.string.BotWebViewReloadPage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.BotWebViewSheet.10
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!BotWebViewSheet.this.webViewContainer.onBackPressed()) {
                        BotWebViewSheet.this.dismiss();
                    }
                } else if (id == R.id.menu_open_bot) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", botId);
                    if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) BotWebViewSheet.this.parentActivity).m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(bundle));
                    }
                    BotWebViewSheet.this.dismiss();
                } else if (id == R.id.menu_reload_page) {
                    if (BotWebViewSheet.this.webViewContainer.getWebView() != null) {
                        BotWebViewSheet.this.webViewContainer.getWebView().animate().cancel();
                        BotWebViewSheet.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    }
                    BotWebViewSheet.this.progressView.setLoadProgress(0.0f);
                    BotWebViewSheet.this.progressView.setAlpha(1.0f);
                    BotWebViewSheet.this.progressView.setVisibility(0);
                    BotWebViewSheet.this.webViewContainer.setBotUser(MessagesController.getInstance(currentAccount).getUser(Long.valueOf(botId)));
                    BotWebViewSheet.this.webViewContainer.loadFlickerAndSettingsItem(currentAccount, botId, BotWebViewSheet.this.settingsItem);
                    BotWebViewSheet.this.webViewContainer.reload();
                } else if (id == R.id.menu_settings) {
                    BotWebViewSheet.this.webViewContainer.onSettingsButtonPressed();
                }
            }
        });
        boolean hasThemeParams = true;
        String themeParams = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getColor(Theme.key_windowBackgroundWhite));
            jsonObject.put("secondary_bg_color", getColor(Theme.key_windowBackgroundGray));
            jsonObject.put("text_color", getColor(Theme.key_windowBackgroundWhiteBlackText));
            jsonObject.put("hint_color", getColor(Theme.key_windowBackgroundWhiteHintText));
            jsonObject.put("link_color", getColor(Theme.key_windowBackgroundWhiteLinkText));
            jsonObject.put("button_color", getColor(Theme.key_featuredStickers_addButton));
            jsonObject.put("button_text_color", getColor(Theme.key_featuredStickers_buttonText));
            themeParams = jsonObject.toString();
        } catch (Exception e) {
            FileLog.e(e);
            hasThemeParams = false;
        }
        this.webViewContainer.setBotUser(MessagesController.getInstance(currentAccount).getUser(Long.valueOf(botId)));
        this.webViewContainer.loadFlickerAndSettingsItem(currentAccount, botId, this.settingsItem);
        switch (type) {
            case 0:
                TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
                req.peer = MessagesController.getInstance(currentAccount).getInputPeer(peerId);
                req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                if (buttonUrl != null) {
                    req.url = buttonUrl;
                    req.flags |= 2;
                }
                if (replyToMsgId != 0) {
                    req.reply_to_msg_id = replyToMsgId;
                    req.flags |= 1;
                }
                if (hasThemeParams) {
                    req.theme_params = new TLRPC.TL_dataJSON();
                    req.theme_params.data = themeParams;
                    req.flags |= 4;
                }
                ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda7
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        BotWebViewSheet.this.m2281x378d2f4c(currentAccount, tLObject, tL_error);
                    }
                });
                NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
                return;
            case 1:
                TLRPC.TL_messages_requestSimpleWebView req2 = new TLRPC.TL_messages_requestSimpleWebView();
                req2.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                if (hasThemeParams) {
                    req2.theme_params = new TLRPC.TL_dataJSON();
                    req2.theme_params.data = themeParams;
                    req2.flags |= 1;
                }
                req2.url = buttonUrl;
                ConnectionsManager.getInstance(currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda6
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        BotWebViewSheet.this.m2279xb1b45c8e(currentAccount, tLObject, tL_error);
                    }
                });
                return;
            case 2:
                TLRPC.TL_messages_requestWebView req3 = new TLRPC.TL_messages_requestWebView();
                req3.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                req3.peer = MessagesController.getInstance(currentAccount).getInputPeer(botId);
                req3.url = buttonUrl;
                req3.flags |= 2;
                if (hasThemeParams) {
                    req3.theme_params = new TLRPC.TL_dataJSON();
                    req3.theme_params.data = themeParams;
                    req3.flags |= 4;
                }
                ConnectionsManager.getInstance(currentAccount).sendRequest(req3, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda5
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        BotWebViewSheet.this.m2277x2bdb89d0(currentAccount, tLObject, tL_error);
                    }
                });
                NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
                return;
            default:
                return;
        }
    }

    /* renamed from: lambda$requestWebView$14$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2277x2bdb89d0(final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2276x68ef2071(response, currentAccount);
            }
        });
    }

    /* renamed from: lambda$requestWebView$13$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2276x68ef2071(TLObject response, int currentAccount) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(currentAccount, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
        }
    }

    /* renamed from: lambda$requestWebView$16$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2279xb1b45c8e(final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2278xeec7f32f(response, currentAccount);
            }
        });
    }

    /* renamed from: lambda$requestWebView$15$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2278xeec7f32f(TLObject response, int currentAccount) {
        if (response instanceof TLRPC.TL_simpleWebViewResultUrl) {
            TLRPC.TL_simpleWebViewResultUrl resultUrl = (TLRPC.TL_simpleWebViewResultUrl) response;
            this.queryId = 0L;
            this.webViewContainer.loadUrl(currentAccount, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        }
    }

    /* renamed from: lambda$requestWebView$18$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2281x378d2f4c(final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2280x74a0c5ed(response, currentAccount);
            }
        });
    }

    /* renamed from: lambda$requestWebView$17$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2280x74a0c5ed(TLObject response, int currentAccount) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(currentAccount, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
        }
    }

    public int getColor(String key) {
        Integer color;
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            color = resourcesProvider.getColor(key);
        } else {
            color = Integer.valueOf(Theme.getColor(key));
        }
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    @Override // android.app.Dialog
    public void show() {
        this.frameLayout.setAlpha(0.0f);
        this.frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: org.telegram.ui.Components.BotWebViewSheet.11
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetY(BotWebViewSheet.this.swipeContainer.getHeight());
                BotWebViewSheet.this.frameLayout.setAlpha(1.0f);
                new SpringAnimation(BotWebViewSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).start();
            }
        });
        super.show();
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        if (this.webViewContainer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        dismiss(null);
    }

    public void dismiss(final Runnable callback) {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        this.webViewContainer.destroyWebView();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo(webViewSwipeContainer.getHeight() + this.frameLayout.measureKeyboardHeight(), new Runnable() { // from class: org.telegram.ui.Components.BotWebViewSheet$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewSheet.this.m2265lambda$dismiss$19$orgtelegramuiComponentsBotWebViewSheet(callback);
            }
        });
    }

    /* renamed from: lambda$dismiss$19$org-telegram-ui-Components-BotWebViewSheet */
    public /* synthetic */ void m2265lambda$dismiss$19$orgtelegramuiComponentsBotWebViewSheet(Runnable callback) {
        super.dismiss();
        if (callback != null) {
            callback.run();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            long queryId = ((Long) args[0]).longValue();
            if (this.queryId == queryId) {
                dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            this.frameLayout.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }
}
