package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.FrameLayout;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
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
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
/* loaded from: classes5.dex */
public class ChatAttachAlertBotWebViewLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int POLL_PERIOD = 60000;
    private long botId;
    private int currentAccount;
    private int customBackground;
    private boolean destroyed;
    private boolean hasCustomBackground;
    private boolean ignoreLayout;
    private boolean ignoreMeasure;
    private boolean isBotButtonAvailable;
    private long lastSwipeTime;
    private int measureOffsetY;
    private boolean needReload;
    private ActionBarMenuItem otherItem;
    private long peerId;
    private Runnable pollRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda7
        @Override // java.lang.Runnable
        public final void run() {
            ChatAttachAlertBotWebViewLayout.this.m2411xcc10dcad();
        }
    };
    private WebProgressView progressView;
    private long queryId;
    private int replyToMsgId;
    private ActionBarMenuSubItem settingsItem;
    private boolean silent;
    private String startCommand;
    private WebViewSwipeContainer swipeContainer;
    private BotWebViewContainer webViewContainer;
    private ValueAnimator webViewScrollAnimator;

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2411xcc10dcad() {
        TLRPC.ChatFull chatFull;
        TLRPC.Peer peer;
        if (!this.destroyed) {
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
            if (this.peerId < 0 && (chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-this.peerId)) != null && (peer = chatFull.default_send_as) != null) {
                prolongWebView.send_as = MessagesController.getInstance(this.currentAccount).getInputPeer(peer);
                prolongWebView.flags |= 8192;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(prolongWebView, new RequestDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatAttachAlertBotWebViewLayout.this.m2410xc60d114e(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2410xc60d114e(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.m2409xc00945ef(error);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2409xc00945ef(TLRPC.TL_error error) {
        if (this.destroyed) {
            return;
        }
        if (error != null) {
            this.parentAlert.dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
        }
    }

    public ChatAttachAlertBotWebViewLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        ActionBarMenuItem addItem = menu.addItem(0, R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString((int) R.string.BotWebViewOpenBot));
        this.settingsItem = this.otherItem.addSubItem(R.id.menu_settings, R.drawable.msg_settings, LocaleController.getString((int) R.string.BotWebViewSettings));
        this.otherItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString((int) R.string.BotWebViewReloadPage));
        this.otherItem.addSubItem(R.id.menu_delete_bot, R.drawable.msg_delete, LocaleController.getString((int) R.string.BotWebViewDeleteBot));
        this.parentAlert.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!ChatAttachAlertBotWebViewLayout.this.onBackPressed()) {
                        ChatAttachAlertBotWebViewLayout.this.parentAlert.dismiss();
                    }
                } else if (id == R.id.menu_open_bot) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", ChatAttachAlertBotWebViewLayout.this.botId);
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.baseFragment.presentFragment(new ChatActivity(bundle));
                    ChatAttachAlertBotWebViewLayout.this.parentAlert.dismiss();
                } else if (id == R.id.menu_reload_page) {
                    if (ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView() != null) {
                        ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().animate().cancel();
                        ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    }
                    ChatAttachAlertBotWebViewLayout.this.progressView.setLoadProgress(0.0f);
                    ChatAttachAlertBotWebViewLayout.this.progressView.setAlpha(1.0f);
                    ChatAttachAlertBotWebViewLayout.this.progressView.setVisibility(0);
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.setBotUser(MessagesController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getUser(Long.valueOf(ChatAttachAlertBotWebViewLayout.this.botId)));
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.loadFlickerAndSettingsItem(ChatAttachAlertBotWebViewLayout.this.currentAccount, ChatAttachAlertBotWebViewLayout.this.botId, ChatAttachAlertBotWebViewLayout.this.settingsItem);
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.reload();
                } else if (id == R.id.menu_delete_bot) {
                    Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getAttachMenuBots().bots.iterator();
                    while (it.hasNext()) {
                        TLRPC.TL_attachMenuBot bot = it.next();
                        if (bot.bot_id == ChatAttachAlertBotWebViewLayout.this.botId) {
                            ChatAttachAlertBotWebViewLayout.this.parentAlert.onLongClickBotButton(bot, MessagesController.getInstance(ChatAttachAlertBotWebViewLayout.this.currentAccount).getUser(Long.valueOf(ChatAttachAlertBotWebViewLayout.this.botId)));
                            return;
                        }
                    }
                } else if (id == R.id.menu_settings) {
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.onSettingsButtonPressed();
                }
            }
        });
        this.webViewContainer = new BotWebViewContainer(context, resourcesProvider, getThemedColor(Theme.key_dialogBackground)) { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.2
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && !ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable) {
                    ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable = true;
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.restoreButtonData();
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        WebViewSwipeContainer webViewSwipeContainer = new WebViewSwipeContainer(context) { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.3
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(((View.MeasureSpec.getSize(heightMeasureSpec) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(84.0f)) + ChatAttachAlertBotWebViewLayout.this.measureOffsetY, C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        this.swipeContainer = webViewSwipeContainer;
        webViewSwipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.m2412xd214a80c();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.m2413xd818736b();
            }
        });
        this.swipeContainer.setDelegate(new WebViewSwipeContainer.Delegate() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate
            public final void onDismiss() {
                ChatAttachAlertBotWebViewLayout.this.m2414xde1c3eca();
            }
        });
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.GenericProvider
            public final Object provide(Object obj) {
                return ChatAttachAlertBotWebViewLayout.this.m2415xe4200a29((Void) obj);
            }
        });
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f));
        WebProgressView webProgressView = new WebProgressView(context, resourcesProvider);
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 84.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda6
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ChatAttachAlertBotWebViewLayout.this.m2417xf027a0e7((Float) obj);
            }
        });
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2412xd214a80c() {
        this.parentAlert.updateLayout(this, true, 0);
        this.webViewContainer.invalidateViewPortHeight();
        this.lastSwipeTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2413xd818736b() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2414xde1c3eca() {
        this.parentAlert.dismiss();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ Boolean m2415xe4200a29(Void obj) {
        return Boolean.valueOf(this.parentAlert.sizeNotifierFrameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2417xf027a0e7(Float progress) {
        this.progressView.setLoadProgressAnimated(progress.floatValue());
        if (progress.floatValue() == 1.0f) {
            ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlertBotWebViewLayout.this.m2416xea23d588(valueAnimator);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ChatAttachAlertBotWebViewLayout.this.progressView.setVisibility(8);
                }
            });
            animator.start();
            requestEnableKeyboard();
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2416xea23d588(ValueAnimator animation) {
        this.progressView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    public void setCustomBackground(int customBackground) {
        this.customBackground = customBackground;
        this.hasCustomBackground = true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean hasCustomBackground() {
        return this.hasCustomBackground;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCustomBackground() {
        return this.customBackground;
    }

    public boolean canExpandByRequest() {
        return !this.swipeContainer.isSwipeInProgress();
    }

    public void setMeasureOffsetY(int measureOffsetY) {
        this.measureOffsetY = measureOffsetY;
        this.swipeContainer.requestLayout();
    }

    public void disallowSwipeOffsetAnimation() {
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.ignoreMeasure) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onPanTransitionStart(boolean keyboardVisible, int contentHeight) {
        if (!keyboardVisible) {
            return;
        }
        this.webViewContainer.setViewPortByMeasureSuppressed(true);
        boolean doNotScroll = false;
        float openOffset = (-this.swipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY();
        if (this.swipeContainer.getSwipeOffsetY() != openOffset) {
            this.swipeContainer.stickTo(openOffset);
            doNotScroll = true;
        }
        int oldh = this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() + contentHeight;
        setMeasuredDimension(getMeasuredWidth(), contentHeight);
        this.ignoreMeasure = true;
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
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
                this.webViewScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda5
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatAttachAlertBotWebViewLayout.this.m2418x184ad17d(valueAnimator2);
                    }
                });
                this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView() != null) {
                            ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().setScrollY(toY);
                        }
                        if (animation == ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator) {
                            ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator = null;
                        }
                    }
                });
                this.webViewScrollAnimator.start();
            }
        }
    }

    /* renamed from: lambda$onPanTransitionStart$9$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2418x184ad17d(ValueAnimator animation) {
        int val = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().setScrollY(val);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        this.webViewContainer.setViewPortByMeasureSuppressed(false);
        requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.parentAlert.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId))));
        this.swipeContainer.setSwipeOffsetY(0.0f);
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().scrollTo(0, 0);
        }
        if (this.parentAlert.getBaseFragment() != null) {
            this.webViewContainer.setParentActivity(this.parentAlert.getBaseFragment().getParentActivity());
        }
        this.otherItem.setVisibility(0);
        if (!this.webViewContainer.isBackButtonVisible()) {
            AndroidUtilities.updateImageViewImageAnimated(this.parentAlert.actionBar.getBackButton(), (int) R.drawable.ic_close_white);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShown() {
        if (this.webViewContainer.isPageLoaded()) {
            requestEnableKeyboard();
        }
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.m2419xf7d46710();
            }
        });
    }

    /* renamed from: lambda$onShown$10$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2419xf7d46710() {
        this.webViewContainer.restoreButtonData();
    }

    public void requestEnableKeyboard() {
        BaseFragment fragment = this.parentAlert.getBaseFragment();
        if ((fragment instanceof ChatActivity) && ((ChatActivity) fragment).contentView.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView());
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertBotWebViewLayout.this.requestEnableKeyboard();
                }
            }, 250L);
            return;
        }
        this.parentAlert.getWindow().setSoftInputMode(20);
        setFocusable(true);
        this.parentAlert.setFocusable(true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHidden() {
        super.onHidden();
        this.parentAlert.setFocusable(false);
        this.parentAlert.getWindow().setSoftInputMode(48);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        return (int) (this.swipeContainer.getSwipeOffsetY() + this.swipeContainer.getOffsetY());
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    public String getStartCommand() {
        return this.startCommand;
    }

    public void requestWebView(int currentAccount, long peerId, long botId, boolean silent, int replyToMsgId) {
        requestWebView(currentAccount, peerId, botId, silent, replyToMsgId, null);
    }

    public void requestWebView(final int currentAccount, long peerId, long botId, boolean silent, int replyToMsgId, String startCommand) {
        TLRPC.ChatFull chatFull;
        TLRPC.Peer peer;
        this.currentAccount = currentAccount;
        this.peerId = peerId;
        this.botId = botId;
        this.silent = silent;
        this.replyToMsgId = replyToMsgId;
        this.startCommand = startCommand;
        this.webViewContainer.setBotUser(MessagesController.getInstance(currentAccount).getUser(Long.valueOf(botId)));
        this.webViewContainer.loadFlickerAndSettingsItem(currentAccount, botId, this.settingsItem);
        TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
        req.peer = MessagesController.getInstance(currentAccount).getInputPeer(peerId);
        req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
        req.silent = silent;
        if (peerId < 0 && (chatFull = MessagesController.getInstance(currentAccount).getChatFull(-peerId)) != null && (peer = chatFull.default_send_as) != null) {
            req.send_as = MessagesController.getInstance(currentAccount).getInputPeer(peer);
            req.flags |= 8192;
        }
        if (startCommand != null) {
            req.start_param = startCommand;
            req.flags |= 8;
        }
        if (replyToMsgId != 0) {
            req.reply_to_msg_id = replyToMsgId;
            req.flags |= 1;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getThemedColor(Theme.key_dialogBackground));
            jsonObject.put("secondary_bg_color", getThemedColor(Theme.key_windowBackgroundGray));
            jsonObject.put("text_color", getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            jsonObject.put("hint_color", getThemedColor(Theme.key_windowBackgroundWhiteHintText));
            jsonObject.put("link_color", getThemedColor(Theme.key_windowBackgroundWhiteLinkText));
            jsonObject.put("button_color", getThemedColor(Theme.key_featuredStickers_addButton));
            jsonObject.put("button_text_color", getThemedColor(Theme.key_featuredStickers_buttonText));
            req.theme_params = new TLRPC.TL_dataJSON();
            req.theme_params.data = jsonObject.toString();
            req.flags |= 4;
        } catch (Exception e) {
            FileLog.e(e);
        }
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatAttachAlertBotWebViewLayout.this.m2421x3b4af8c0(currentAccount, tLObject, tL_error);
            }
        });
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
    }

    /* renamed from: lambda$requestWebView$12$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2421x3b4af8c0(final int currentAccount, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.m2420x35472d61(response, currentAccount);
            }
        });
    }

    /* renamed from: lambda$requestWebView$11$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout */
    public /* synthetic */ void m2420x35472d61(TLObject response, int currentAccount) {
        if (response instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
            this.queryId = resultUrl.query_id;
            this.webViewContainer.loadUrl(currentAccount, resultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        this.otherItem.removeAllSubItems();
        menu.removeView(this.otherItem);
        this.webViewContainer.destroyWebView();
        this.destroyed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        super.onHide();
        this.otherItem.setVisibility(8);
        this.isBotButtonAvailable = false;
        if (!this.webViewContainer.isBackButtonVisible()) {
            AndroidUtilities.updateImageViewImageAnimated(this.parentAlert.actionBar.getBackButton(), (int) R.drawable.ic_ab_back);
        }
        this.parentAlert.actionBar.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        if (this.webViewContainer.hasUserPermissions()) {
            this.webViewContainer.destroyWebView();
            this.needReload = true;
        }
    }

    public boolean needReload() {
        if (this.needReload) {
            this.needReload = false;
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return (int) this.swipeContainer.getOffsetY();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            padding = (int) (availableHeight / 3.5f);
        } else {
            padding = (availableHeight / 5) * 2;
        }
        this.parentAlert.setAllowNestedScroll(true);
        if (padding < 0) {
            padding = 0;
        }
        if (this.swipeContainer.getOffsetY() != padding) {
            this.ignoreLayout = true;
            this.swipeContainer.setOffsetY(padding);
            this.ignoreLayout = false;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getButtonsHideOffset() {
        return ((int) this.swipeContainer.getTopActionBarOffsetY()) + AndroidUtilities.dp(12.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onBackPressed() {
        return this.webViewContainer.onBackPressed();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void scrollToTop() {
        WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean shouldHideBottomButtons() {
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    public BotWebViewContainer getWebViewContainer() {
        return this.webViewContainer;
    }

    public void setDelegate(BotWebViewContainer.Delegate delegate) {
        this.webViewContainer.setDelegate(delegate);
    }

    public boolean isBotButtonAvailable() {
        return this.isBotButtonAvailable;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            long queryId = ((Long) args[0]).longValue();
            if (this.queryId == queryId) {
                this.webViewContainer.destroyWebView();
                this.needReload = true;
                this.parentAlert.dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            this.webViewContainer.updateFlickerBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        }
    }

    /* loaded from: classes5.dex */
    public static class WebViewSwipeContainer extends FrameLayout {
        public static final SimpleFloatPropertyCompat<WebViewSwipeContainer> SWIPE_OFFSET_Y = new SimpleFloatPropertyCompat<>("swipeOffsetY", ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda4.INSTANCE, ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda5.INSTANCE);
        private Delegate delegate;
        private boolean flingInProgress;
        private GestureDetectorCompat gestureDetector;
        private boolean isScrolling;
        private boolean isSwipeDisallowed;
        private boolean isSwipeOffsetAnimationDisallowed;
        private SpringAnimation offsetYAnimator;
        private SpringAnimation scrollAnimator;
        private Runnable scrollEndListener;
        private Runnable scrollListener;
        private float swipeOffsetY;
        private int swipeStickyRange;
        private WebView webView;
        private float topActionBarOffsetY = ActionBar.getCurrentActionBarHeight();
        private float offsetY = 0.0f;
        private float pendingOffsetY = -1.0f;
        private float pendingSwipeOffsetY = -2.14748365E9f;
        private GenericProvider<Void, Boolean> isKeyboardVisible = ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda3.INSTANCE;

        /* loaded from: classes5.dex */
        public interface Delegate {
            void onDismiss();
        }

        static /* synthetic */ float access$1124(WebViewSwipeContainer x0, float x1) {
            float f = x0.swipeOffsetY - x1;
            x0.swipeOffsetY = f;
            return f;
        }

        public static /* synthetic */ Boolean lambda$new$0(Void obj) {
            return false;
        }

        public WebViewSwipeContainer(Context context) {
            super(context);
            final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.1
                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (WebViewSwipeContainer.this.isSwipeDisallowed) {
                        return false;
                    }
                    if (velocityY >= 700.0f && (WebViewSwipeContainer.this.webView == null || WebViewSwipeContainer.this.webView.getScrollY() == 0)) {
                        WebViewSwipeContainer.this.flingInProgress = true;
                        if (WebViewSwipeContainer.this.swipeOffsetY >= WebViewSwipeContainer.this.swipeStickyRange) {
                            if (WebViewSwipeContainer.this.delegate != null) {
                                WebViewSwipeContainer.this.delegate.onDismiss();
                            }
                        } else {
                            WebViewSwipeContainer.this.stickTo(0.0f);
                        }
                        return true;
                    } else if (velocityY > -700.0f || WebViewSwipeContainer.this.swipeOffsetY <= (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                        return true;
                    } else {
                        WebViewSwipeContainer.this.flingInProgress = true;
                        WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                        webViewSwipeContainer.stickTo((-webViewSwipeContainer.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY);
                        return true;
                    }
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (!WebViewSwipeContainer.this.isScrolling && !WebViewSwipeContainer.this.isSwipeDisallowed) {
                        if (((Boolean) WebViewSwipeContainer.this.isKeyboardVisible.provide(null)).booleanValue() && WebViewSwipeContainer.this.swipeOffsetY == (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                            WebViewSwipeContainer.this.isSwipeDisallowed = true;
                        } else if (Math.abs(distanceY) < touchSlop || Math.abs(distanceY) * 1.5f < Math.abs(distanceX) || (WebViewSwipeContainer.this.swipeOffsetY == (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY && WebViewSwipeContainer.this.webView != null && (distanceY >= 0.0f || WebViewSwipeContainer.this.webView.getScrollY() != 0))) {
                            if (WebViewSwipeContainer.this.webView != null) {
                                if (WebViewSwipeContainer.this.webView.canScrollHorizontally(distanceX >= 0.0f ? 1 : -1)) {
                                    WebViewSwipeContainer.this.isSwipeDisallowed = true;
                                }
                            }
                        } else {
                            WebViewSwipeContainer.this.isScrolling = true;
                            MotionEvent ev = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                            for (int i = 0; i < WebViewSwipeContainer.this.getChildCount(); i++) {
                                WebViewSwipeContainer.this.getChildAt(i).dispatchTouchEvent(ev);
                            }
                            ev.recycle();
                            return true;
                        }
                    }
                    if (WebViewSwipeContainer.this.isScrolling) {
                        if (distanceY < 0.0f) {
                            if (WebViewSwipeContainer.this.swipeOffsetY <= (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                                if (WebViewSwipeContainer.this.webView != null) {
                                    float newWebScrollY = WebViewSwipeContainer.this.webView.getScrollY() + distanceY;
                                    WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(newWebScrollY, 0.0f, Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight()) - WebViewSwipeContainer.this.topActionBarOffsetY));
                                    if (newWebScrollY < 0.0f) {
                                        WebViewSwipeContainer.access$1124(WebViewSwipeContainer.this, newWebScrollY);
                                    }
                                } else {
                                    WebViewSwipeContainer.access$1124(WebViewSwipeContainer.this, distanceY);
                                }
                            } else {
                                WebViewSwipeContainer.access$1124(WebViewSwipeContainer.this, distanceY);
                            }
                        } else {
                            WebViewSwipeContainer.access$1124(WebViewSwipeContainer.this, distanceY);
                            if (WebViewSwipeContainer.this.webView != null && WebViewSwipeContainer.this.swipeOffsetY < (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY) {
                                WebViewSwipeContainer.this.webView.setScrollY((int) MathUtils.clamp(WebViewSwipeContainer.this.webView.getScrollY() - ((WebViewSwipeContainer.this.swipeOffsetY + WebViewSwipeContainer.this.offsetY) - WebViewSwipeContainer.this.topActionBarOffsetY), 0.0f, Math.max(WebViewSwipeContainer.this.webView.getContentHeight(), WebViewSwipeContainer.this.webView.getHeight()) - WebViewSwipeContainer.this.topActionBarOffsetY));
                            }
                        }
                        WebViewSwipeContainer webViewSwipeContainer = WebViewSwipeContainer.this;
                        webViewSwipeContainer.swipeOffsetY = MathUtils.clamp(webViewSwipeContainer.swipeOffsetY, (-WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY, (WebViewSwipeContainer.this.getHeight() - WebViewSwipeContainer.this.offsetY) + WebViewSwipeContainer.this.topActionBarOffsetY);
                        WebViewSwipeContainer.this.invalidateTranslation();
                        return true;
                    }
                    return true;
                }
            });
            updateStickyRange();
        }

        public void setIsKeyboardVisible(GenericProvider<Void, Boolean> isKeyboardVisible) {
            this.isKeyboardVisible = isKeyboardVisible;
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateStickyRange();
        }

        private void updateStickyRange() {
            this.swipeStickyRange = AndroidUtilities.dp(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 8.0f : 64.0f);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            if (disallowIntercept) {
                this.isSwipeDisallowed = true;
                this.isScrolling = false;
            }
        }

        public void setSwipeOffsetAnimationDisallowed(boolean swipeOffsetAnimationDisallowed) {
            this.isSwipeOffsetAnimationDisallowed = swipeOffsetAnimationDisallowed;
        }

        public void setScrollListener(Runnable scrollListener) {
            this.scrollListener = scrollListener;
        }

        public void setScrollEndListener(Runnable scrollEndListener) {
            this.scrollEndListener = scrollEndListener;
        }

        public void setWebView(WebView webView) {
            this.webView = webView;
        }

        public void setTopActionBarOffsetY(float topActionBarOffsetY) {
            this.topActionBarOffsetY = topActionBarOffsetY;
            invalidateTranslation();
        }

        public void setSwipeOffsetY(float swipeOffsetY) {
            this.swipeOffsetY = swipeOffsetY;
            invalidateTranslation();
        }

        public void setOffsetY(final float offsetY) {
            if (this.pendingSwipeOffsetY != -2.14748365E9f) {
                this.pendingOffsetY = offsetY;
                return;
            }
            SpringAnimation springAnimation = this.offsetYAnimator;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            final float wasOffsetY = this.offsetY;
            final float deltaOffsetY = offsetY - wasOffsetY;
            final boolean wasOnTop = Math.abs((this.swipeOffsetY + wasOffsetY) - this.topActionBarOffsetY) <= ((float) AndroidUtilities.dp(1.0f));
            if (!this.isSwipeOffsetAnimationDisallowed) {
                SpringAnimation springAnimation2 = this.offsetYAnimator;
                if (springAnimation2 != null) {
                    springAnimation2.cancel();
                }
                SpringAnimation addEndListener = new SpringAnimation(new FloatValueHolder(wasOffsetY)).setSpring(new SpringForce(offsetY).setStiffness(1400.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda2
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.m2422x533cf60c(wasOffsetY, deltaOffsetY, wasOnTop, offsetY, dynamicAnimation, f, f2);
                    }
                }).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.m2423x81ee602b(offsetY, dynamicAnimation, z, f, f2);
                    }
                });
                this.offsetYAnimator = addEndListener;
                addEndListener.start();
                return;
            }
            this.offsetY = offsetY;
            if (wasOnTop) {
                this.swipeOffsetY = MathUtils.clamp(this.swipeOffsetY - Math.max(0.0f, deltaOffsetY), (-this.offsetY) + this.topActionBarOffsetY, (getHeight() - this.offsetY) + this.topActionBarOffsetY);
            }
            invalidateTranslation();
        }

        /* renamed from: lambda$setOffsetY$1$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer */
        public /* synthetic */ void m2422x533cf60c(float wasOffsetY, float deltaOffsetY, boolean wasOnTop, float offsetY, DynamicAnimation animation, float value, float velocity) {
            this.offsetY = value;
            float progress = (value - wasOffsetY) / deltaOffsetY;
            if (wasOnTop) {
                this.swipeOffsetY = MathUtils.clamp(this.swipeOffsetY - (Math.max(0.0f, deltaOffsetY) * progress), (-this.offsetY) + this.topActionBarOffsetY, (getHeight() - this.offsetY) + this.topActionBarOffsetY);
            }
            SpringAnimation springAnimation = this.scrollAnimator;
            if (springAnimation != null && springAnimation.getSpring().getFinalPosition() == (-wasOffsetY) + this.topActionBarOffsetY) {
                this.scrollAnimator.getSpring().setFinalPosition((-offsetY) + this.topActionBarOffsetY);
            }
            invalidateTranslation();
        }

        /* renamed from: lambda$setOffsetY$2$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer */
        public /* synthetic */ void m2423x81ee602b(float offsetY, DynamicAnimation animation, boolean canceled, float value, float velocity) {
            this.offsetYAnimator = null;
            if (!canceled) {
                this.offsetY = offsetY;
                invalidateTranslation();
                return;
            }
            this.pendingOffsetY = offsetY;
        }

        public void invalidateTranslation() {
            setTranslationY(Math.max(this.topActionBarOffsetY, this.offsetY + this.swipeOffsetY));
            Runnable runnable = this.scrollListener;
            if (runnable != null) {
                runnable.run();
            }
        }

        public float getTopActionBarOffsetY() {
            return this.topActionBarOffsetY;
        }

        public float getOffsetY() {
            return this.offsetY;
        }

        public float getSwipeOffsetY() {
            return this.swipeOffsetY;
        }

        public void setDelegate(Delegate delegate) {
            this.delegate = delegate;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (!this.isScrolling || ev.getActionIndex() == 0) {
                MotionEvent rawEvent = MotionEvent.obtain(ev);
                int index = ev.getActionIndex();
                if (Build.VERSION.SDK_INT >= 29) {
                    rawEvent.setLocation(ev.getRawX(index), ev.getRawY(index));
                } else {
                    float offsetX = ev.getRawX() - ev.getX();
                    float offsetY = ev.getRawY() - ev.getY();
                    rawEvent.setLocation(ev.getX(index) + offsetX, ev.getY(index) + offsetY);
                }
                boolean detector = this.gestureDetector.onTouchEvent(rawEvent);
                rawEvent.recycle();
                if (ev.getAction() == 1 || ev.getAction() == 3) {
                    this.isSwipeDisallowed = false;
                    this.isScrolling = false;
                    if (this.flingInProgress) {
                        this.flingInProgress = false;
                    } else {
                        float f = this.swipeOffsetY;
                        int i = this.swipeStickyRange;
                        if (f <= (-i)) {
                            stickTo((-this.offsetY) + this.topActionBarOffsetY);
                        } else if (f > (-i) && f <= i) {
                            stickTo(0.0f);
                        } else {
                            Delegate delegate = this.delegate;
                            if (delegate != null) {
                                delegate.onDismiss();
                            }
                        }
                    }
                }
                boolean superTouch = super.dispatchTouchEvent(ev);
                return (!superTouch && !detector && ev.getAction() == 0) || superTouch || detector;
            }
            return false;
        }

        public void stickTo(float offset) {
            stickTo(offset, null);
        }

        public void stickTo(float offset, final Runnable callback) {
            SpringAnimation springAnimation;
            if (this.swipeOffsetY == offset || ((springAnimation = this.scrollAnimator) != null && springAnimation.getSpring().getFinalPosition() == offset)) {
                if (callback != null) {
                    callback.run();
                }
                Runnable runnable = this.scrollEndListener;
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
            this.pendingSwipeOffsetY = offset;
            SpringAnimation springAnimation2 = this.offsetYAnimator;
            if (springAnimation2 != null) {
                springAnimation2.cancel();
            }
            SpringAnimation springAnimation3 = this.scrollAnimator;
            if (springAnimation3 != null) {
                springAnimation3.cancel();
            }
            SpringAnimation addEndListener = new SpringAnimation(this, SWIPE_OFFSET_Y, offset).setSpring(new SpringForce(offset).setStiffness(1400.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda1
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.m2424x212eaa3d(callback, dynamicAnimation, z, f, f2);
                }
            });
            this.scrollAnimator = addEndListener;
            addEndListener.start();
        }

        /* renamed from: lambda$stickTo$3$org-telegram-ui-Components-ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer */
        public /* synthetic */ void m2424x212eaa3d(Runnable callback, DynamicAnimation animation, boolean canceled, float value, float velocity) {
            if (animation == this.scrollAnimator) {
                this.scrollAnimator = null;
                if (callback != null) {
                    callback.run();
                }
                Runnable runnable = this.scrollEndListener;
                if (runnable != null) {
                    runnable.run();
                }
                float f = this.pendingOffsetY;
                if (f != -1.0f) {
                    boolean wasDisallowed = this.isSwipeOffsetAnimationDisallowed;
                    this.isSwipeOffsetAnimationDisallowed = true;
                    setOffsetY(f);
                    this.pendingOffsetY = -1.0f;
                    this.isSwipeOffsetAnimationDisallowed = wasDisallowed;
                }
                this.pendingSwipeOffsetY = -2.14748365E9f;
            }
        }

        public boolean isSwipeInProgress() {
            return this.isScrolling;
        }
    }

    /* loaded from: classes5.dex */
    public static class WebProgressView extends View {
        private final SimpleFloatPropertyCompat<WebProgressView> LOAD_PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("loadProgress", ChatAttachAlertBotWebViewLayout$WebProgressView$$ExternalSyntheticLambda0.INSTANCE, ChatAttachAlertBotWebViewLayout$WebProgressView$$ExternalSyntheticLambda1.INSTANCE).setMultiplier(100.0f);
        private Paint bluePaint;
        private float loadProgress;
        private Theme.ResourcesProvider resourcesProvider;
        private SpringAnimation springAnimation;

        public WebProgressView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            Paint paint = new Paint(1);
            this.bluePaint = paint;
            this.resourcesProvider = resourcesProvider;
            paint.setColor(getThemedColor(Theme.key_featuredStickers_addButton));
            this.bluePaint.setStyle(Paint.Style.STROKE);
            this.bluePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.bluePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        protected int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.springAnimation = new SpringAnimation(this, this.LOAD_PROGRESS_PROPERTY).setSpring(new SpringForce().setStiffness(400.0f).setDampingRatio(1.0f));
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.springAnimation.cancel();
            this.springAnimation = null;
        }

        public void setLoadProgressAnimated(float loadProgress) {
            SpringAnimation springAnimation = this.springAnimation;
            if (springAnimation == null) {
                setLoadProgress(loadProgress);
                return;
            }
            springAnimation.getSpring().setFinalPosition(100.0f * loadProgress);
            this.springAnimation.start();
        }

        public void setLoadProgress(float loadProgress) {
            this.loadProgress = loadProgress;
            invalidate();
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            super.draw(canvas);
            float y = getHeight() - (this.bluePaint.getStrokeWidth() / 2.0f);
            canvas.drawLine(0.0f, y, getWidth() * this.loadProgress, y, this.bluePaint);
        }
    }
}
