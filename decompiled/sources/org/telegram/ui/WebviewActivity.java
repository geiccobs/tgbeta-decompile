package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.WebviewActivity;
/* loaded from: classes4.dex */
public class WebviewActivity extends BaseFragment {
    private static final int TYPE_GAME = 0;
    private static final int TYPE_STAT = 1;
    private static final int open_in = 2;
    private static final int share = 1;
    private String currentBot;
    private long currentDialogId;
    private String currentGame;
    private MessageObject currentMessageObject;
    private String currentUrl;
    private String linkToCopy;
    private boolean loadStats;
    private ActionBarMenuItem progressItem;
    private ContextProgressView progressView;
    private String short_param;
    private int type;
    public Runnable typingRunnable;
    private WebView webView;

    /* loaded from: classes4.dex */
    public class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
            WebviewActivity.this = r1;
        }

        @JavascriptInterface
        public void postEvent(final String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WebviewActivity$TelegramWebviewProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WebviewActivity.TelegramWebviewProxy.this.m4819xaa8f84a3(eventName);
                }
            });
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-WebviewActivity$TelegramWebviewProxy */
        public /* synthetic */ void m4819xaa8f84a3(String eventName) {
            if (WebviewActivity.this.getParentActivity() == null) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d(eventName);
            }
            char c = 65535;
            switch (eventName.hashCode()) {
                case -1788360622:
                    if (eventName.equals("share_game")) {
                        c = 0;
                        break;
                    }
                    break;
                case 406539826:
                    if (eventName.equals("share_score")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                    break;
                case 1:
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                    break;
            }
            WebviewActivity webviewActivity = WebviewActivity.this;
            webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
        }
    }

    public WebviewActivity(String url, String botName, String gameName, String startParam, MessageObject messageObject) {
        String str;
        this.typingRunnable = new Runnable() { // from class: org.telegram.ui.WebviewActivity.1
            @Override // java.lang.Runnable
            public void run() {
                if (WebviewActivity.this.currentMessageObject != null && WebviewActivity.this.getParentActivity() != null && WebviewActivity.this.typingRunnable != null) {
                    MessagesController.getInstance(WebviewActivity.this.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 0, 6, 0);
                    AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000L);
                }
            }
        };
        this.currentUrl = url;
        this.currentBot = botName;
        this.currentGame = gameName;
        this.currentMessageObject = messageObject;
        this.short_param = startParam;
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        sb.append("/");
        sb.append(this.currentBot);
        if (TextUtils.isEmpty(startParam)) {
            str = "";
        } else {
            str = "?game=" + startParam;
        }
        sb.append(str);
        this.linkToCopy = sb.toString();
        this.type = 0;
    }

    public WebviewActivity(String statUrl, long did) {
        this.typingRunnable = new Runnable() { // from class: org.telegram.ui.WebviewActivity.1
            @Override // java.lang.Runnable
            public void run() {
                if (WebviewActivity.this.currentMessageObject != null && WebviewActivity.this.getParentActivity() != null && WebviewActivity.this.typingRunnable != null) {
                    MessagesController.getInstance(WebviewActivity.this.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 0, 6, 0);
                    AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000L);
                }
            }
        };
        this.currentUrl = statUrl;
        this.currentDialogId = did;
        this.type = 1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.webView.setLayerType(0, null);
        this.typingRunnable = null;
        try {
            ViewParent parent = this.webView.getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(this.webView);
            }
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
            this.webView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.WebviewActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    WebviewActivity.this.finishFragment();
                } else if (id == 1) {
                    if (WebviewActivity.this.currentMessageObject != null) {
                        WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                        WebviewActivity webviewActivity = WebviewActivity.this;
                        webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
                    }
                } else if (id == 2) {
                    WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.progressItem = menu.addItemWithWidth(1, R.drawable.share, AndroidUtilities.dp(54.0f));
        int i = this.type;
        if (i == 0) {
            ActionBarMenuItem menuItem = menu.addItem(0, R.drawable.ic_ab_other);
            menuItem.addSubItem(2, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
            this.actionBar.setTitle(this.currentGame);
            ActionBar actionBar = this.actionBar;
            actionBar.setSubtitle("@" + this.currentBot);
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            this.progressItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
        } else if (i == 1) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_player_actionBar));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_player_actionBarItems), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_player_actionBarSelector), false);
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_player_actionBarTitle));
            this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
            this.actionBar.setTitle(LocaleController.getString("Statistics", R.string.Statistics));
            ContextProgressView contextProgressView2 = new ContextProgressView(context, 3);
            this.progressView = contextProgressView2;
            this.progressItem.addView(contextProgressView2, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getContentView().setVisibility(8);
            this.progressItem.setEnabled(false);
        }
        WebView webView = new WebView(context);
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (Build.VERSION.SDK_INT >= 19) {
            this.webView.setLayerType(2, null);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this.webView, true);
            if (this.type == 0) {
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
            }
        }
        this.webView.setWebViewClient(new WebViewClient() { // from class: org.telegram.ui.WebviewActivity.3
            private boolean isInternalUrl(String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                Uri uri = Uri.parse(url);
                if (!"tg".equals(uri.getScheme())) {
                    return false;
                }
                if (WebviewActivity.this.type == 1) {
                    try {
                        WebviewActivity.this.reloadStats(Uri.parse(url.replace("tg:statsrefresh", "tg://telegram.org")).getQueryParameter("params"));
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else {
                    WebviewActivity.this.finishFragment(false);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", uri);
                        ComponentName componentName = new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName());
                        intent.setComponent(componentName);
                        intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                        ApplicationLoader.applicationContext.startActivity(intent);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                return true;
            }

            @Override // android.webkit.WebViewClient
            public void onLoadResource(WebView view, String url) {
                if (isInternalUrl(url)) {
                    return;
                }
                super.onLoadResource(view, url);
            }

            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return isInternalUrl(url) || super.shouldOverrideUrlLoading(view, url);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (WebviewActivity.this.progressView != null && WebviewActivity.this.progressView.getVisibility() == 0) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (WebviewActivity.this.type == 0) {
                        WebviewActivity.this.progressItem.getContentView().setVisibility(0);
                        WebviewActivity.this.progressItem.setEnabled(true);
                        animatorSet.playTogether(ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleX", 0.0f, 1.0f), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleY", 0.0f, 1.0f), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "alpha", 0.0f, 1.0f));
                    } else {
                        animatorSet.playTogether(ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", 1.0f, 0.0f));
                    }
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.WebviewActivity.3.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (WebviewActivity.this.type == 1) {
                                WebviewActivity.this.progressItem.setVisibility(8);
                            } else {
                                WebviewActivity.this.progressView.setVisibility(4);
                            }
                        }
                    });
                    animatorSet.setDuration(150L);
                    animatorSet.start();
                }
            }
        });
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable.run();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        WebView webView;
        if (isOpen && !backward && (webView = this.webView) != null) {
            webView.loadUrl(this.currentUrl);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    public static boolean supportWebview() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if ("samsung".equals(manufacturer) && "GT-I9500".equals(model)) {
            return false;
        }
        return true;
    }

    public void reloadStats(String params) {
        if (this.loadStats) {
            return;
        }
        this.loadStats = true;
        TLRPC.TL_messages_getStatsURL req = new TLRPC.TL_messages_getStatsURL();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.currentDialogId);
        req.params = params != null ? params : "";
        req.dark = Theme.getCurrentTheme().isDark();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.WebviewActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WebviewActivity.this.m4818lambda$reloadStats$1$orgtelegramuiWebviewActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$reloadStats$1$org-telegram-ui-WebviewActivity */
    public /* synthetic */ void m4818lambda$reloadStats$1$orgtelegramuiWebviewActivity(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.WebviewActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WebviewActivity.this.m4817lambda$reloadStats$0$orgtelegramuiWebviewActivity(response);
            }
        });
    }

    /* renamed from: lambda$reloadStats$0$org-telegram-ui-WebviewActivity */
    public /* synthetic */ void m4817lambda$reloadStats$0$orgtelegramuiWebviewActivity(TLObject response) {
        this.loadStats = false;
        if (response != null) {
            TLRPC.TL_statsURL url = (TLRPC.TL_statsURL) response;
            WebView webView = this.webView;
            String str = url.url;
            this.currentUrl = str;
            webView.loadUrl(str);
        }
    }

    public static void openGameInBrowser(String urlStr, MessageObject messageObject, Activity parentActivity, String short_name, String username) {
        Exception e;
        String url;
        SerializedData serializedData;
        String str = "";
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String existing = sharedPreferences.getString(str + messageObject.getId(), null);
            StringBuilder hash = new StringBuilder(existing != null ? existing : str);
            StringBuilder addHash = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
            if (existing == null) {
                char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    hash.append(chars[Utilities.random.nextInt(chars.length)]);
                }
            }
            addHash.append((CharSequence) hash);
            int index = urlStr.indexOf(35);
            if (index < 0) {
                url = urlStr + "#" + ((Object) addHash);
            } else {
                String curHash = urlStr.substring(index + 1);
                if (curHash.indexOf(61) < 0 && curHash.indexOf(63) < 0) {
                    url = curHash.length() > 0 ? urlStr + "?" + ((Object) addHash) : urlStr + ((Object) addHash);
                }
                url = urlStr + "&" + ((Object) addHash);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(((Object) hash) + "_date", (int) (System.currentTimeMillis() / 1000));
            serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            editor.putString(((Object) hash) + "_m", Utilities.bytesToHex(serializedData.toByteArray()));
            String str2 = ((Object) hash) + "_link";
            StringBuilder sb = new StringBuilder();
            sb.append("https://");
            sb.append(MessagesController.getInstance(messageObject.currentAccount).linkPrefix);
            sb.append("/");
            try {
                sb.append(username);
                if (!TextUtils.isEmpty(short_name)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("?game=");
                    try {
                        sb2.append(short_name);
                        str = sb2.toString();
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                    }
                }
                sb.append(str);
                editor.putString(str2, sb.toString());
                editor.commit();
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception e4) {
            e = e4;
        }
        try {
            Browser.openUrl((Context) parentActivity, url, false);
            serializedData.cleanup();
        } catch (Exception e5) {
            e = e5;
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        if (this.type == 0) {
            themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
            themeDescriptions.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
            themeDescriptions.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        } else {
            themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_player_actionBar));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_player_actionBarItems));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_player_actionBarSelector));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
            themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
            themeDescriptions.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner4));
            themeDescriptions.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter4));
        }
        return themeDescriptions;
    }
}
