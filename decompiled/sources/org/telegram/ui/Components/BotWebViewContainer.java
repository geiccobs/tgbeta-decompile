package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.core.util.Consumer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
/* loaded from: classes5.dex */
public class BotWebViewContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String DURGER_KING_USERNAME = "DurgerKingBot";
    private static final int REQUEST_CODE_WEB_PERMISSION = 4000;
    private static final int REQUEST_CODE_WEB_VIEW_FILE = 3000;
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList("http", "https");
    private TLRPC.User botUser;
    private String buttonData;
    private int currentAccount;
    private String currentPaymentSlug;
    private Delegate delegate;
    private BackupImageView flickerView;
    private boolean hasUserPermissions;
    private boolean isBackButtonVisible;
    private boolean isFlickeringCenter;
    private boolean isPageLoaded;
    private boolean isRequestingPageOpen;
    private boolean isViewPortByMeasureSuppressed;
    private long lastClickMs;
    private boolean lastExpanded;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mUrl;
    private Runnable onPermissionsRequestResultCallback;
    private Activity parentActivity;
    private Theme.ResourcesProvider resourcesProvider;
    private WebView webView;
    private boolean webViewNotAvailable;
    private TextView webViewNotAvailableText;
    private Consumer<Float> webViewProgressListener;
    private WebViewScrollListener webViewScrollListener;
    private CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();
    private int lastButtonColor = getColor(Theme.key_featuredStickers_addButton);
    private int lastButtonTextColor = getColor(Theme.key_featuredStickers_buttonText);
    private String lastButtonText = "";

    /* loaded from: classes5.dex */
    public interface WebViewScrollListener {
        void onWebViewScrolled(WebView webView, int i, int i2);
    }

    public BotWebViewContainer(Context context, Theme.ResourcesProvider resourcesProvider, int backgroundColor) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.flickerDrawable.drawFrame = false;
        this.flickerDrawable.setColors(backgroundColor, 153, 204);
        BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.Components.BotWebViewContainer.1
            {
                BotWebViewContainer.this = this;
                this.imageReceiver = new C00511(this);
            }

            /* renamed from: org.telegram.ui.Components.BotWebViewContainer$1$1 */
            /* loaded from: classes5.dex */
            public class C00511 extends ImageReceiver {
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                C00511(View view) {
                    super(view);
                    AnonymousClass1.this = this$1;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.messenger.ImageReceiver
                public boolean setImageBitmapByKey(Drawable drawable, String key, int type, boolean memCache, int guid) {
                    boolean set = super.setImageBitmapByKey(drawable, key, type, memCache, guid);
                    ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.BotWebViewContainer$1$1$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            BotWebViewContainer.AnonymousClass1.C00511.this.m2235x2638d59d(valueAnimator);
                        }
                    });
                    anim.start();
                    return set;
                }

                /* renamed from: lambda$setImageBitmapByKey$0$org-telegram-ui-Components-BotWebViewContainer$1$1 */
                public /* synthetic */ void m2235x2638d59d(ValueAnimator animation) {
                    AnonymousClass1.this.imageReceiver.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
                    invalidate();
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.BackupImageView, android.view.View
            public void onDraw(Canvas canvas) {
                if (BotWebViewContainer.this.isFlickeringCenter) {
                    super.onDraw(canvas);
                    return;
                }
                Drawable drawable = this.imageReceiver.getDrawable();
                if (drawable != null) {
                    this.imageReceiver.setImageCoords(0.0f, 0.0f, getWidth(), drawable.getIntrinsicHeight() * (getWidth() / drawable.getIntrinsicWidth()));
                    this.imageReceiver.draw(canvas);
                }
            }
        };
        this.flickerView = backupImageView;
        backupImageView.setColorFilter(new PorterDuffColorFilter(getColor(Theme.key_dialogSearchHint), PorterDuff.Mode.SRC_IN));
        this.flickerView.getImageReceiver().setAspectFit(true);
        addView(this.flickerView, LayoutHelper.createFrame(-1, -2, 48));
        TextView textView = new TextView(context);
        this.webViewNotAvailableText = textView;
        textView.setText(LocaleController.getString((int) R.string.BotWebViewNotAvailablePlaceholder));
        this.webViewNotAvailableText.setTextColor(getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.webViewNotAvailableText.setTextSize(1, 15.0f);
        this.webViewNotAvailableText.setGravity(17);
        this.webViewNotAvailableText.setVisibility(8);
        int padding = AndroidUtilities.dp(16.0f);
        this.webViewNotAvailableText.setPadding(padding, padding, padding, padding);
        addView(this.webViewNotAvailableText, LayoutHelper.createFrame(-1, -2, 17));
        setFocusable(false);
    }

    public void setViewPortByMeasureSuppressed(boolean viewPortByMeasureSuppressed) {
        this.isViewPortByMeasureSuppressed = viewPortByMeasureSuppressed;
    }

    private void checkCreateWebView() {
        if (this.webView == null && !this.webViewNotAvailable) {
            try {
                setupWebView();
            } catch (Throwable t) {
                FileLog.e(t);
                this.flickerView.setVisibility(8);
                this.webViewNotAvailable = true;
                this.webViewNotAvailableText.setVisibility(0);
                if (this.webView != null) {
                    removeView(this.webView);
                }
            }
        }
    }

    private void setupWebView() {
        WebView webView = this.webView;
        if (webView != null) {
            webView.destroy();
            removeView(this.webView);
        }
        WebView webView2 = new WebView(getContext()) { // from class: org.telegram.ui.Components.BotWebViewContainer.2
            private int prevScrollX;
            private int prevScrollY;

            @Override // android.webkit.WebView, android.view.View
            protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                if (BotWebViewContainer.this.webViewScrollListener != null) {
                    BotWebViewContainer.this.webViewScrollListener.onWebViewScrolled(this, getScrollX() - this.prevScrollX, getScrollY() - this.prevScrollY);
                }
                this.prevScrollX = getScrollX();
                this.prevScrollY = getScrollY();
            }

            @Override // android.view.View
            public void setScrollX(int value) {
                super.setScrollX(value);
                this.prevScrollX = value;
            }

            @Override // android.view.View
            public void setScrollY(int value) {
                super.setScrollY(value);
                this.prevScrollY = value;
            }

            @Override // android.webkit.WebView, android.view.View
            public boolean onCheckIsTextEditor() {
                return BotWebViewContainer.this.isFocusable();
            }

            @Override // android.webkit.WebView, android.widget.AbsoluteLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.webkit.WebView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    BotWebViewContainer.this.lastClickMs = System.currentTimeMillis();
                }
                return super.onTouchEvent(event);
            }
        };
        this.webView = webView2;
        webView2.setBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        File databaseStorage = new File(ApplicationLoader.getFilesDirFixed(), "webview_database");
        if ((databaseStorage.exists() && databaseStorage.isDirectory()) || databaseStorage.mkdirs()) {
            settings.setDatabasePath(databaseStorage.getAbsolutePath());
        }
        GeolocationPermissions.getInstance().clearAll();
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setWebViewClient(new WebViewClient() { // from class: org.telegram.ui.Components.BotWebViewContainer.3
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uriOrig = Uri.parse(BotWebViewContainer.this.mUrl);
                Uri uriNew = Uri.parse(url);
                if (BotWebViewContainer.this.isPageLoaded && (!ColorUtils$$ExternalSyntheticBackport0.m(uriOrig.getHost(), uriNew.getHost()) || !ColorUtils$$ExternalSyntheticBackport0.m(uriOrig.getPath(), uriNew.getPath()))) {
                    if (BotWebViewContainer.WHITELISTED_SCHEMES.contains(uriNew.getScheme())) {
                        BotWebViewContainer.this.onOpenUri(uriNew);
                        return true;
                    }
                    return true;
                }
                return false;
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url) {
                BotWebViewContainer.this.setPageLoaded(url);
            }
        });
        this.webView.setWebChromeClient(new AnonymousClass4());
        this.webView.setAlpha(0.0f);
        addView(this.webView);
        if (Build.VERSION.SDK_INT >= 17) {
            this.webView.addJavascriptInterface(new WebViewProxy(), "TelegramWebviewProxy");
        }
    }

    /* renamed from: org.telegram.ui.Components.BotWebViewContainer$4 */
    /* loaded from: classes5.dex */
    public class AnonymousClass4 extends WebChromeClient {
        private Dialog lastPermissionsDialog;

        AnonymousClass4() {
            BotWebViewContainer.this = this$0;
        }

        @Override // android.webkit.WebChromeClient
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            Context ctx = BotWebViewContainer.this.getContext();
            if (!(ctx instanceof Activity)) {
                return false;
            }
            Activity activity = (Activity) ctx;
            if (BotWebViewContainer.this.mFilePathCallback != null) {
                BotWebViewContainer.this.mFilePathCallback.onReceiveValue(null);
            }
            BotWebViewContainer.this.mFilePathCallback = filePathCallback;
            if (Build.VERSION.SDK_INT >= 21) {
                activity.startActivityForResult(fileChooserParams.createIntent(), 3000);
                return true;
            }
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("*/*");
            activity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString((int) R.string.BotWebViewFileChooserTitle)), 3000);
            return true;
        }

        @Override // android.webkit.WebChromeClient
        public void onProgressChanged(WebView view, int newProgress) {
            if (BotWebViewContainer.this.webViewProgressListener != null) {
                BotWebViewContainer.this.webViewProgressListener.accept(Float.valueOf(newProgress / 100.0f));
            }
        }

        @Override // android.webkit.WebChromeClient
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            if (BotWebViewContainer.this.parentActivity != null) {
                Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, R.raw.permission_request_location, LocaleController.formatString(R.string.BotWebViewRequestGeolocationPermission, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(R.string.BotWebViewRequestGeolocationPermissionWithHint, UserObject.getUserName(BotWebViewContainer.this.botUser)), new Consumer() { // from class: org.telegram.ui.Components.BotWebViewContainer$4$$ExternalSyntheticLambda1
                    @Override // androidx.core.util.Consumer
                    public final void accept(Object obj) {
                        BotWebViewContainer.AnonymousClass4.this.m2237x53986a9a(callback, origin, (Boolean) obj);
                    }
                });
                this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                createWebViewPermissionsRequestDialog.show();
                return;
            }
            callback.invoke(origin, false, false);
        }

        /* renamed from: lambda$onGeolocationPermissionsShowPrompt$1$org-telegram-ui-Components-BotWebViewContainer$4 */
        public /* synthetic */ void m2237x53986a9a(final GeolocationPermissions.Callback callback, final String origin, Boolean allow) {
            if (this.lastPermissionsDialog != null) {
                this.lastPermissionsDialog = null;
                if (allow.booleanValue()) {
                    BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new Consumer() { // from class: org.telegram.ui.Components.BotWebViewContainer$4$$ExternalSyntheticLambda0
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            BotWebViewContainer.AnonymousClass4.this.m2236x61eec47b(callback, origin, (Boolean) obj);
                        }
                    });
                } else {
                    callback.invoke(origin, false, false);
                }
            }
        }

        /* renamed from: lambda$onGeolocationPermissionsShowPrompt$0$org-telegram-ui-Components-BotWebViewContainer$4 */
        public /* synthetic */ void m2236x61eec47b(GeolocationPermissions.Callback callback, String origin, Boolean allowSystem) {
            callback.invoke(origin, allowSystem.booleanValue(), false);
            if (allowSystem.booleanValue()) {
                BotWebViewContainer.this.hasUserPermissions = true;
            }
        }

        @Override // android.webkit.WebChromeClient
        public void onGeolocationPermissionsHidePrompt() {
            Dialog dialog = this.lastPermissionsDialog;
            if (dialog != null) {
                dialog.dismiss();
                this.lastPermissionsDialog = null;
            }
        }

        @Override // android.webkit.WebChromeClient
        public void onPermissionRequest(final PermissionRequest request) {
            Dialog dialog = this.lastPermissionsDialog;
            if (dialog != null) {
                dialog.dismiss();
                this.lastPermissionsDialog = null;
            }
            String[] resources = request.getResources();
            if (resources.length == 1) {
                final String resource = resources[0];
                if (BotWebViewContainer.this.parentActivity == null) {
                    request.deny();
                    return;
                }
                char c = 65535;
                switch (resource.hashCode()) {
                    case -1660821873:
                        if (resource.equals("android.webkit.resource.VIDEO_CAPTURE")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 968612586:
                        if (resource.equals("android.webkit.resource.AUDIO_CAPTURE")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Dialog createWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.RECORD_AUDIO"}, R.raw.permission_request_microphone, LocaleController.formatString(R.string.BotWebViewRequestMicrophonePermission, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(R.string.BotWebViewRequestMicrophonePermissionWithHint, UserObject.getUserName(BotWebViewContainer.this.botUser)), new Consumer() { // from class: org.telegram.ui.Components.BotWebViewContainer$4$$ExternalSyntheticLambda3
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                BotWebViewContainer.AnonymousClass4.this.m2239x14aacaef(request, resource, (Boolean) obj);
                            }
                        });
                        this.lastPermissionsDialog = createWebViewPermissionsRequestDialog;
                        createWebViewPermissionsRequestDialog.show();
                        return;
                    case 1:
                        Dialog createWebViewPermissionsRequestDialog2 = AlertsCreator.createWebViewPermissionsRequestDialog(BotWebViewContainer.this.parentActivity, BotWebViewContainer.this.resourcesProvider, new String[]{"android.permission.CAMERA"}, R.raw.permission_request_camera, LocaleController.formatString(R.string.BotWebViewRequestCameraPermission, UserObject.getUserName(BotWebViewContainer.this.botUser)), LocaleController.formatString(R.string.BotWebViewRequestCameraPermissionWithHint, UserObject.getUserName(BotWebViewContainer.this.botUser)), new Consumer() { // from class: org.telegram.ui.Components.BotWebViewContainer$4$$ExternalSyntheticLambda5
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                BotWebViewContainer.AnonymousClass4.this.m2241xf7fe172d(request, resource, (Boolean) obj);
                            }
                        });
                        this.lastPermissionsDialog = createWebViewPermissionsRequestDialog2;
                        createWebViewPermissionsRequestDialog2.show();
                        return;
                    default:
                        return;
                }
            }
        }

        /* renamed from: lambda$onPermissionRequest$3$org-telegram-ui-Components-BotWebViewContainer$4 */
        public /* synthetic */ void m2239x14aacaef(final PermissionRequest request, final String resource, Boolean allow) {
            if (this.lastPermissionsDialog != null) {
                this.lastPermissionsDialog = null;
                if (allow.booleanValue()) {
                    BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.RECORD_AUDIO"}, new Consumer() { // from class: org.telegram.ui.Components.BotWebViewContainer$4$$ExternalSyntheticLambda2
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            BotWebViewContainer.AnonymousClass4.this.m2238x230124d0(request, resource, (Boolean) obj);
                        }
                    });
                } else {
                    request.deny();
                }
            }
        }

        /* renamed from: lambda$onPermissionRequest$2$org-telegram-ui-Components-BotWebViewContainer$4 */
        public /* synthetic */ void m2238x230124d0(PermissionRequest request, String resource, Boolean allowSystem) {
            if (allowSystem.booleanValue()) {
                request.grant(new String[]{resource});
                BotWebViewContainer.this.hasUserPermissions = true;
                return;
            }
            request.deny();
        }

        /* renamed from: lambda$onPermissionRequest$5$org-telegram-ui-Components-BotWebViewContainer$4 */
        public /* synthetic */ void m2241xf7fe172d(final PermissionRequest request, final String resource, Boolean allow) {
            if (this.lastPermissionsDialog != null) {
                this.lastPermissionsDialog = null;
                if (allow.booleanValue()) {
                    BotWebViewContainer.this.runWithPermissions(new String[]{"android.permission.CAMERA"}, new Consumer() { // from class: org.telegram.ui.Components.BotWebViewContainer$4$$ExternalSyntheticLambda4
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            BotWebViewContainer.AnonymousClass4.this.m2240x654710e(request, resource, (Boolean) obj);
                        }
                    });
                } else {
                    request.deny();
                }
            }
        }

        /* renamed from: lambda$onPermissionRequest$4$org-telegram-ui-Components-BotWebViewContainer$4 */
        public /* synthetic */ void m2240x654710e(PermissionRequest request, String resource, Boolean allowSystem) {
            if (allowSystem.booleanValue()) {
                request.grant(new String[]{resource});
                BotWebViewContainer.this.hasUserPermissions = true;
                return;
            }
            request.deny();
        }

        @Override // android.webkit.WebChromeClient
        public void onPermissionRequestCanceled(PermissionRequest request) {
            Dialog dialog = this.lastPermissionsDialog;
            if (dialog != null) {
                dialog.dismiss();
                this.lastPermissionsDialog = null;
            }
        }
    }

    public void onOpenUri(Uri uri) {
        onOpenUri(uri, false);
    }

    private void onOpenUri(final Uri uri, boolean suppressPopup) {
        if (!this.isRequestingPageOpen) {
            if (System.currentTimeMillis() - this.lastClickMs > 10000 && suppressPopup) {
                return;
            }
            this.lastClickMs = 0L;
            boolean[] forceBrowser = {false};
            boolean internal = Browser.isInternalUri(uri, forceBrowser);
            if (internal && !forceBrowser[0]) {
                if (this.delegate == null) {
                    Browser.openUrl(getContext(), uri, true, false);
                    return;
                }
                setDescendantFocusability(393216);
                setFocusable(false);
                this.webView.setFocusable(false);
                this.webView.setDescendantFocusability(393216);
                this.webView.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService("input_method");
                imm.hideSoftInputFromWindow(getWindowToken(), 2);
                this.delegate.onCloseRequested(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        BotWebViewContainer.this.m2231xb32d06c0(uri);
                    }
                });
            } else if (suppressPopup) {
                Browser.openUrl(getContext(), uri, true, false);
            } else {
                this.isRequestingPageOpen = true;
                new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString((int) R.string.OpenUrlTitle)).setMessage(LocaleController.formatString(R.string.OpenUrlAlert2, uri.toString())).setPositiveButton(LocaleController.getString((int) R.string.Open), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        BotWebViewContainer.this.m2232xecf7a89f(uri, dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        BotWebViewContainer.this.m2233x26c24a7e(dialogInterface);
                    }
                }).show();
            }
        }
    }

    /* renamed from: lambda$onOpenUri$0$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2231xb32d06c0(Uri uri) {
        Browser.openUrl(getContext(), uri, true, false);
    }

    /* renamed from: lambda$onOpenUri$1$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2232xecf7a89f(Uri uri, DialogInterface dialog, int which) {
        Browser.openUrl(getContext(), uri, true, false);
    }

    /* renamed from: lambda$onOpenUri$2$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2233x26c24a7e(DialogInterface dialog) {
        this.isRequestingPageOpen = false;
    }

    public static int getMainButtonRippleColor(int buttonColor) {
        return ColorUtils.calculateLuminance(buttonColor) >= 0.30000001192092896d ? 301989888 : 385875967;
    }

    public static Drawable getMainButtonRippleDrawable(int buttonColor) {
        return Theme.createSelectorWithBackgroundDrawable(buttonColor, getMainButtonRippleColor(buttonColor));
    }

    public void updateFlickerBackgroundColor(int backgroundColor) {
        this.flickerDrawable.setColors(backgroundColor, 153, 204);
    }

    public boolean onBackPressed() {
        if (this.webView != null && this.isBackButtonVisible) {
            notifyEvent("back_button_pressed", null);
            return true;
        }
        return false;
    }

    public void setPageLoaded(String url) {
        if (this.isPageLoaded) {
            return;
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(this.webView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.flickerView, View.ALPHA, 0.0f));
        set.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BotWebViewContainer.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                BotWebViewContainer.this.flickerView.setVisibility(8);
            }
        });
        set.start();
        this.mUrl = url;
        this.isPageLoaded = true;
        setFocusable(true);
        this.delegate.onWebAppReady();
    }

    public boolean hasUserPermissions() {
        return this.hasUserPermissions;
    }

    public void setBotUser(TLRPC.User botUser) {
        this.botUser = botUser;
    }

    public void runWithPermissions(final String[] permissions, final Consumer<Boolean> callback) {
        if (Build.VERSION.SDK_INT < 23) {
            callback.accept(true);
        } else if (checkPermissions(permissions)) {
            callback.accept(true);
        } else {
            this.onPermissionsRequestResultCallback = new Runnable() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    BotWebViewContainer.this.m2234x4d467b13(callback, permissions);
                }
            };
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.requestPermissions(permissions, 4000);
            }
        }
    }

    /* renamed from: lambda$runWithPermissions$3$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2234x4d467b13(Consumer callback, String[] permissions) {
        callback.accept(Boolean.valueOf(checkPermissions(permissions)));
    }

    public boolean isPageLoaded() {
        return this.isPageLoaded;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    private boolean checkPermissions(String[] permissions) {
        for (String perm : permissions) {
            if (getContext().checkSelfPermission(perm) != 0) {
                return false;
            }
        }
        return true;
    }

    public void restoreButtonData() {
        String str = this.buttonData;
        if (str != null) {
            onEventReceived("web_app_setup_main_button", str);
        }
    }

    public void onInvoiceStatusUpdate(String slug, String status) {
        onInvoiceStatusUpdate(slug, status, false);
    }

    public void onInvoiceStatusUpdate(String slug, String status, boolean ignoreCurrentCheck) {
        try {
            JSONObject data = new JSONObject();
            data.put("slug", slug);
            data.put(NotificationCompat.CATEGORY_STATUS, status);
            notifyEvent("invoice_closed", data);
            if (!ignoreCurrentCheck && ColorUtils$$ExternalSyntheticBackport0.m(this.currentPaymentSlug, slug)) {
                this.currentPaymentSlug = null;
            }
        } catch (JSONException e) {
            FileLog.e(e);
        }
    }

    public void onSettingsButtonPressed() {
        this.lastClickMs = System.currentTimeMillis();
        notifyEvent("settings_button_pressed", null);
    }

    public void onMainButtonPressed() {
        this.lastClickMs = System.currentTimeMillis();
        notifyEvent("main_button_pressed", null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Runnable runnable;
        if (requestCode == 4000 && (runnable = this.onPermissionsRequestResultCallback) != null) {
            runnable.run();
            this.onPermissionsRequestResultCallback = null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3000 && this.mFilePathCallback != null) {
            Uri[] results = null;
            if (resultCode == -1 && data != null && data.getDataString() != null) {
                results = new Uri[]{Uri.parse(data.getDataString())};
            }
            this.mFilePathCallback.onReceiveValue(results);
            this.mFilePathCallback = null;
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!this.isViewPortByMeasureSuppressed) {
            invalidateViewPortHeight(true);
        }
    }

    public void invalidateViewPortHeight() {
        invalidateViewPortHeight(false);
    }

    public void invalidateViewPortHeight(boolean isStable) {
        invalidateViewPortHeight(isStable, false);
    }

    public void invalidateViewPortHeight(boolean isStable, boolean force) {
        invalidate();
        if ((this.isPageLoaded || force) && (getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer = (ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent();
            if (isStable) {
                this.lastExpanded = swipeContainer.getSwipeOffsetY() == (-swipeContainer.getOffsetY()) + swipeContainer.getTopActionBarOffsetY();
            }
            int viewPortHeight = (int) (((swipeContainer.getMeasuredHeight() - swipeContainer.getOffsetY()) - swipeContainer.getSwipeOffsetY()) + swipeContainer.getTopActionBarOffsetY());
            try {
                JSONObject data = new JSONObject();
                data.put("height", viewPortHeight / AndroidUtilities.density);
                data.put("is_state_stable", isStable);
                data.put("is_expanded", this.lastExpanded);
                notifyEvent("viewport_changed", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.flickerView) {
            if (this.isFlickeringCenter) {
                canvas.save();
                View parent = (View) getParent();
                canvas.translate(0.0f, (ActionBar.getCurrentActionBarHeight() - parent.getTranslationY()) / 2.0f);
            }
            boolean draw = super.drawChild(canvas, child, drawingTime);
            if (this.isFlickeringCenter) {
                canvas.restore();
            }
            AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth(), getHeight());
            this.flickerDrawable.draw(canvas, AndroidUtilities.rectTmp, 0.0f, this);
            invalidate();
            return draw;
        } else if (child == this.webViewNotAvailableText) {
            canvas.save();
            View parent2 = (View) getParent();
            canvas.translate(0.0f, (ActionBar.getCurrentActionBarHeight() - parent2.getTranslationY()) / 2.0f);
            boolean draw2 = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return draw2;
        } else {
            return super.drawChild(canvas, child, drawingTime);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.flickerDrawable.setParentWidth(getMeasuredWidth());
    }

    public void setWebViewProgressListener(Consumer<Float> webViewProgressListener) {
        this.webViewProgressListener = webViewProgressListener;
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void loadFlickerAndSettingsItem(int currentAccount, long botId, final ActionBarMenuSubItem settingsItem) {
        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(botId));
        int i = 0;
        if (user.username != null && ColorUtils$$ExternalSyntheticBackport0.m(user.username, DURGER_KING_USERNAME)) {
            this.flickerView.setVisibility(0);
            this.flickerView.setAlpha(1.0f);
            this.flickerView.setImageDrawable(SvgHelper.getDrawable(R.raw.durgerking_placeholder, getColor(Theme.key_windowBackgroundGray)));
            setupFlickerParams(false);
            return;
        }
        TLRPC.TL_attachMenuBot cachedBot = null;
        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(currentAccount).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TLRPC.TL_attachMenuBot bot = it.next();
            if (bot.bot_id == botId) {
                cachedBot = bot;
                break;
            }
        }
        if (cachedBot != null) {
            boolean center = false;
            TLRPC.TL_attachMenuBotIcon botIcon = MediaDataController.getPlaceholderStaticAttachMenuBotIcon(cachedBot);
            if (botIcon == null) {
                botIcon = MediaDataController.getStaticAttachMenuBotIcon(cachedBot);
                center = true;
            }
            if (botIcon != null) {
                this.flickerView.setVisibility(0);
                this.flickerView.setAlpha(1.0f);
                this.flickerView.setImage(ImageLocation.getForDocument(botIcon.icon), (String) null, (Drawable) null, cachedBot);
                setupFlickerParams(center);
            }
            if (settingsItem != null) {
                if (!cachedBot.has_settings) {
                    i = 8;
                }
                settingsItem.setVisibility(i);
                return;
            }
            return;
        }
        TLRPC.TL_messages_getAttachMenuBot req = new TLRPC.TL_messages_getAttachMenuBot();
        req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotWebViewContainer.this.m2228xcc4935e9(settingsItem, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadFlickerAndSettingsItem$5$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2228xcc4935e9(final ActionBarMenuSubItem settingsItem, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewContainer.this.m2227x927e940a(response, settingsItem);
            }
        });
    }

    /* renamed from: lambda$loadFlickerAndSettingsItem$4$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2227x927e940a(TLObject response, ActionBarMenuSubItem settingsItem) {
        int i = 8;
        if (response instanceof TLRPC.TL_attachMenuBotsBot) {
            TLRPC.TL_attachMenuBot bot = ((TLRPC.TL_attachMenuBotsBot) response).bot;
            boolean center = false;
            TLRPC.TL_attachMenuBotIcon botIcon = MediaDataController.getPlaceholderStaticAttachMenuBotIcon(bot);
            if (botIcon == null) {
                botIcon = MediaDataController.getStaticAttachMenuBotIcon(bot);
                center = true;
            }
            if (botIcon != null) {
                this.flickerView.setVisibility(0);
                this.flickerView.setAlpha(1.0f);
                this.flickerView.setImage(ImageLocation.getForDocument(botIcon.icon), (String) null, (Drawable) null, bot);
                setupFlickerParams(center);
            }
            if (settingsItem != null) {
                if (bot.has_settings) {
                    i = 0;
                }
                settingsItem.setVisibility(i);
            }
        } else if (settingsItem != null) {
            settingsItem.setVisibility(8);
        }
    }

    private void setupFlickerParams(boolean center) {
        this.isFlickeringCenter = center;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.flickerView.getLayoutParams();
        params.gravity = center ? 17 : 48;
        if (center) {
            int dp = AndroidUtilities.dp(64.0f);
            params.height = dp;
            params.width = dp;
        } else {
            params.width = -1;
            params.height = -2;
        }
        this.flickerView.requestLayout();
    }

    public void reload() {
        checkCreateWebView();
        this.isPageLoaded = false;
        this.lastClickMs = 0L;
        this.hasUserPermissions = false;
        WebView webView = this.webView;
        if (webView != null) {
            webView.reload();
        }
    }

    public void loadUrl(int currentAccount, String url) {
        checkCreateWebView();
        this.currentAccount = currentAccount;
        this.isPageLoaded = false;
        this.lastClickMs = 0L;
        this.hasUserPermissions = false;
        this.mUrl = url;
        WebView webView = this.webView;
        if (webView != null) {
            webView.loadUrl(url);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.onActivityResultReceived);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.onRequestPermissionResultReceived);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onActivityResultReceived);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.onRequestPermissionResultReceived);
    }

    public void destroyWebView() {
        WebView webView = this.webView;
        if (webView != null) {
            if (webView.getParent() != null) {
                removeView(this.webView);
            }
            this.webView.destroy();
        }
    }

    public boolean isBackButtonVisible() {
        return this.isBackButtonVisible;
    }

    public void evaluateJs(String script) {
        checkCreateWebView();
        if (this.webView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            this.webView.evaluateJavascript(script, BotWebViewContainer$$ExternalSyntheticLambda2.INSTANCE);
            return;
        }
        try {
            WebView webView = this.webView;
            webView.loadUrl("javascript:" + URLEncoder.encode(script, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            WebView webView2 = this.webView;
            webView2.loadUrl("javascript:" + URLEncoder.encode(script));
        }
    }

    public static /* synthetic */ void lambda$evaluateJs$6(String value) {
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetNewTheme) {
            WebView webView = this.webView;
            if (webView != null) {
                webView.setBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            }
            this.flickerView.setColorFilter(new PorterDuffColorFilter(getColor(Theme.key_dialogSearchHint), PorterDuff.Mode.SRC_IN));
            notifyThemeChanged();
        } else if (id == NotificationCenter.onActivityResultReceived) {
            onActivityResult(((Integer) args[0]).intValue(), ((Integer) args[1]).intValue(), (Intent) args[2]);
        } else if (id == NotificationCenter.onRequestPermissionResultReceived) {
            onRequestPermissionsResult(((Integer) args[0]).intValue(), (String[]) args[1], (int[]) args[2]);
        }
    }

    private void notifyThemeChanged() {
        notifyEvent("theme_changed", buildThemeParams());
    }

    private void notifyEvent(String event, JSONObject eventData) {
        evaluateJs("window.Telegram.WebView.receiveEvent('" + event + "', " + eventData + ");");
    }

    public void setWebViewScrollListener(WebViewScrollListener webViewScrollListener) {
        this.webViewScrollListener = webViewScrollListener;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void onEventReceived(String eventType, String eventData) {
        char c;
        char c2;
        if (this.webView == null || this.delegate == null) {
            return;
        }
        char c3 = 4;
        char c4 = 2;
        char c5 = 65535;
        boolean z = false;
        switch (eventType.hashCode()) {
            case -1717314938:
                if (eventType.equals("web_app_open_link")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1390641887:
                if (eventType.equals("web_app_open_invoice")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1259935152:
                if (eventType.equals("web_app_request_theme")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -921083201:
                if (eventType.equals("web_app_request_viewport")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -439770054:
                if (eventType.equals("web_app_open_tg_link")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -71726289:
                if (eventType.equals("web_app_close")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -58095910:
                if (eventType.equals("web_app_ready")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 668142772:
                if (eventType.equals("web_app_data_send")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1011447167:
                if (eventType.equals("web_app_setup_back_button")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1273834781:
                if (eventType.equals("web_app_trigger_haptic_feedback")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1398490221:
                if (eventType.equals("web_app_setup_main_button")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 1917103703:
                if (eventType.equals("web_app_set_header_color")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 2001330488:
                if (eventType.equals("web_app_set_background_color")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 2139805763:
                if (eventType.equals("web_app_expand")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.delegate.onCloseRequested(null);
                return;
            case 1:
                try {
                    JSONObject jsonObject = new JSONObject(eventData);
                    this.delegate.onWebAppSetBackgroundColor(Color.parseColor(jsonObject.optString(TtmlNode.ATTR_TTS_COLOR)) | (-16777216));
                    return;
                } catch (JSONException e) {
                    FileLog.e(e);
                    return;
                }
            case 2:
                try {
                    JSONObject jsonObject2 = new JSONObject(eventData);
                    String key = jsonObject2.getString("color_key");
                    String themeKey = null;
                    switch (key.hashCode()) {
                        case -1265068311:
                            if (key.equals("bg_color")) {
                                c5 = 0;
                                break;
                            }
                            break;
                        case -210781868:
                            if (key.equals("secondary_bg_color")) {
                                c5 = 1;
                                break;
                            }
                            break;
                    }
                    switch (c5) {
                        case 0:
                            themeKey = Theme.key_windowBackgroundWhite;
                            break;
                        case 1:
                            themeKey = Theme.key_windowBackgroundGray;
                            break;
                    }
                    if (themeKey != null) {
                        this.delegate.onWebAppSetActionBarColor(themeKey);
                        return;
                    }
                    return;
                } catch (JSONException e2) {
                    FileLog.e(e2);
                    return;
                }
            case 3:
                try {
                    this.delegate.onSendWebViewData(new JSONObject(eventData).optString("data"));
                    return;
                } catch (JSONException e3) {
                    FileLog.e(e3);
                    return;
                }
            case 4:
                try {
                    JSONObject jsonData = new JSONObject(eventData);
                    String type = jsonData.optString(CommonProperties.TYPE);
                    BotWebViewVibrationEffect vibrationEffect = null;
                    switch (type.hashCode()) {
                        case -1184809658:
                            if (type.equals("impact")) {
                                c2 = 0;
                                break;
                            }
                            c2 = 65535;
                            break;
                        case 193071555:
                            if (type.equals("selection_change")) {
                                c2 = 2;
                                break;
                            }
                            c2 = 65535;
                            break;
                        case 595233003:
                            if (type.equals("notification")) {
                                c2 = 1;
                                break;
                            }
                            c2 = 65535;
                            break;
                        default:
                            c2 = 65535;
                            break;
                    }
                    switch (c2) {
                        case 0:
                            String optString = jsonData.optString("impact_style");
                            switch (optString.hashCode()) {
                                case -1078030475:
                                    if (optString.equals(Constants.ScionAnalytics.PARAM_MEDIUM)) {
                                        c3 = 1;
                                        break;
                                    }
                                    c3 = 65535;
                                    break;
                                case 3535914:
                                    if (optString.equals("soft")) {
                                        break;
                                    }
                                    c3 = 65535;
                                    break;
                                case 99152071:
                                    if (optString.equals("heavy")) {
                                        c3 = 2;
                                        break;
                                    }
                                    c3 = 65535;
                                    break;
                                case 102970646:
                                    if (optString.equals("light")) {
                                        c3 = 0;
                                        break;
                                    }
                                    c3 = 65535;
                                    break;
                                case 108511787:
                                    if (optString.equals("rigid")) {
                                        c3 = 3;
                                        break;
                                    }
                                    c3 = 65535;
                                    break;
                                default:
                                    c3 = 65535;
                                    break;
                            }
                            switch (c3) {
                                case 0:
                                    vibrationEffect = BotWebViewVibrationEffect.IMPACT_LIGHT;
                                    break;
                                case 1:
                                    vibrationEffect = BotWebViewVibrationEffect.IMPACT_MEDIUM;
                                    break;
                                case 2:
                                    vibrationEffect = BotWebViewVibrationEffect.IMPACT_HEAVY;
                                    break;
                                case 3:
                                    vibrationEffect = BotWebViewVibrationEffect.IMPACT_RIGID;
                                    break;
                                case 4:
                                    vibrationEffect = BotWebViewVibrationEffect.IMPACT_SOFT;
                                    break;
                            }
                        case 1:
                            String optString2 = jsonData.optString("notification_type");
                            switch (optString2.hashCode()) {
                                case -1867169789:
                                    if (optString2.equals("success")) {
                                        c4 = 1;
                                        break;
                                    }
                                    c4 = 65535;
                                    break;
                                case 96784904:
                                    if (optString2.equals(Constants.IPC_BUNDLE_KEY_SEND_ERROR)) {
                                        c4 = 0;
                                        break;
                                    }
                                    c4 = 65535;
                                    break;
                                case 1124446108:
                                    if (optString2.equals("warning")) {
                                        break;
                                    }
                                    c4 = 65535;
                                    break;
                                default:
                                    c4 = 65535;
                                    break;
                            }
                            switch (c4) {
                                case 0:
                                    vibrationEffect = BotWebViewVibrationEffect.NOTIFICATION_ERROR;
                                    break;
                                case 1:
                                    vibrationEffect = BotWebViewVibrationEffect.NOTIFICATION_SUCCESS;
                                    break;
                                case 2:
                                    vibrationEffect = BotWebViewVibrationEffect.NOTIFICATION_WARNING;
                                    break;
                            }
                            break;
                        case 2:
                            vibrationEffect = BotWebViewVibrationEffect.SELECTION_CHANGE;
                            break;
                    }
                    if (vibrationEffect != null) {
                        if (Build.VERSION.SDK_INT >= 26) {
                            AndroidUtilities.getVibrator().vibrate(vibrationEffect.getVibrationEffectForOreo());
                            return;
                        } else {
                            AndroidUtilities.getVibrator().vibrate(vibrationEffect.fallbackTimings, -1);
                            return;
                        }
                    }
                    return;
                } catch (Exception e4) {
                    FileLog.e(e4);
                    return;
                }
            case 5:
                try {
                    Uri uri = Uri.parse(new JSONObject(eventData).optString(ImagesContract.URL));
                    if (WHITELISTED_SCHEMES.contains(uri.getScheme())) {
                        onOpenUri(uri, true);
                        return;
                    }
                    return;
                } catch (Exception e5) {
                    FileLog.e(e5);
                    return;
                }
            case 6:
                try {
                    String pathFull = new JSONObject(eventData).optString("path_full");
                    if (pathFull.startsWith("/")) {
                        pathFull = pathFull.substring(1);
                    }
                    onOpenUri(Uri.parse("https://t.me/" + pathFull));
                    return;
                } catch (JSONException e6) {
                    FileLog.e(e6);
                    return;
                }
            case 7:
                try {
                    boolean newVisible = new JSONObject(eventData).optBoolean("is_visible");
                    if (newVisible != this.isBackButtonVisible) {
                        this.isBackButtonVisible = newVisible;
                        this.delegate.onSetBackButtonVisible(newVisible);
                        return;
                    }
                    return;
                } catch (JSONException e7) {
                    FileLog.e(e7);
                    return;
                }
            case '\b':
                try {
                    final String slug = new JSONObject(eventData).optString("slug");
                    if (this.currentPaymentSlug != null) {
                        onInvoiceStatusUpdate(slug, "cancelled", true);
                    } else {
                        this.currentPaymentSlug = slug;
                        TLRPC.TL_payments_getPaymentForm req = new TLRPC.TL_payments_getPaymentForm();
                        TLRPC.TL_inputInvoiceSlug invoiceSlug = new TLRPC.TL_inputInvoiceSlug();
                        invoiceSlug.slug = slug;
                        req.invoice = invoiceSlug;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda7
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                BotWebViewContainer.this.m2230x7d6dddff(slug, tLObject, tL_error);
                            }
                        });
                    }
                    return;
                } catch (JSONException e8) {
                    FileLog.e(e8);
                    return;
                }
            case '\t':
                this.delegate.onWebAppExpand();
                return;
            case '\n':
                boolean hasSwipeInProgress = (getParent() instanceof ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) && ((ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) getParent()).isSwipeInProgress();
                if (!hasSwipeInProgress) {
                    z = true;
                }
                invalidateViewPortHeight(z, true);
                return;
            case 11:
                notifyThemeChanged();
                return;
            case '\f':
                setPageLoaded(this.webView.getUrl());
                return;
            case '\r':
                try {
                    JSONObject info = new JSONObject(eventData);
                    boolean isActive = info.optBoolean("is_active", false);
                    String text = info.optString("text", this.lastButtonText).trim();
                    boolean isVisible = info.optBoolean("is_visible", false) && !TextUtils.isEmpty(text);
                    int color = info.has(TtmlNode.ATTR_TTS_COLOR) ? Color.parseColor(info.optString(TtmlNode.ATTR_TTS_COLOR)) : this.lastButtonColor;
                    int textColor = info.has("text_color") ? Color.parseColor(info.optString("text_color")) : this.lastButtonTextColor;
                    boolean isProgressVisible = info.optBoolean("is_progress_visible", false) && isVisible;
                    this.lastButtonColor = color;
                    this.lastButtonTextColor = textColor;
                    this.lastButtonText = text;
                    this.buttonData = eventData;
                    this.delegate.onSetupMainButton(isVisible, isActive, text, color, textColor, isProgressVisible);
                    return;
                } catch (IllegalArgumentException | JSONException e9) {
                    FileLog.e(e9);
                    return;
                }
            default:
                return;
        }
    }

    /* renamed from: lambda$onEventReceived$8$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2230x7d6dddff(final String slug, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewContainer$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                BotWebViewContainer.this.m2229x43a33c20(error, slug, response);
            }
        });
    }

    /* renamed from: lambda$onEventReceived$7$org-telegram-ui-Components-BotWebViewContainer */
    public /* synthetic */ void m2229x43a33c20(TLRPC.TL_error error, String slug, TLObject response) {
        if (error != null) {
            onInvoiceStatusUpdate(slug, "failed");
        } else {
            this.delegate.onWebAppOpenInvoice(slug, response);
        }
    }

    private JSONObject buildThemeParams() {
        try {
            JSONObject object = new JSONObject();
            object.put("bg_color", formatColor(Theme.key_windowBackgroundWhite));
            object.put("secondary_bg_color", formatColor(Theme.key_windowBackgroundGray));
            object.put("text_color", formatColor(Theme.key_windowBackgroundWhiteBlackText));
            object.put("hint_color", formatColor(Theme.key_windowBackgroundWhiteHintText));
            object.put("link_color", formatColor(Theme.key_windowBackgroundWhiteLinkText));
            object.put("button_color", formatColor(Theme.key_featuredStickers_addButton));
            object.put("button_text_color", formatColor(Theme.key_featuredStickers_buttonText));
            return new JSONObject().put("theme_params", object);
        } catch (Exception e) {
            FileLog.e(e);
            return new JSONObject();
        }
    }

    private int getColor(String colorKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = Integer.valueOf(resourcesProvider != null ? resourcesProvider.getColor(colorKey).intValue() : Theme.getColor(colorKey));
        if (color == null) {
            color = Integer.valueOf(Theme.getColor(colorKey));
        }
        return color.intValue();
    }

    private String formatColor(String colorKey) {
        int color = getColor(colorKey);
        return "#" + hexFixed(Color.red(color)) + hexFixed(Color.green(color)) + hexFixed(Color.blue(color));
    }

    private String hexFixed(int h) {
        String hex = Integer.toHexString(h);
        if (hex.length() < 2) {
            return "0" + hex;
        }
        return hex;
    }

    /* loaded from: classes5.dex */
    public class WebViewProxy {
        private WebViewProxy() {
            BotWebViewContainer.this = r1;
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-Components-BotWebViewContainer$WebViewProxy */
        public /* synthetic */ void m2242x6a2a9d10(String eventType, String eventData) {
            BotWebViewContainer.this.onEventReceived(eventType, eventData);
        }

        @JavascriptInterface
        public void postEvent(final String eventType, final String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BotWebViewContainer$WebViewProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BotWebViewContainer.WebViewProxy.this.m2242x6a2a9d10(eventType, eventData);
                }
            });
        }
    }

    /* loaded from: classes5.dex */
    public interface Delegate {
        void onCloseRequested(Runnable runnable);

        void onSendWebViewData(String str);

        void onSetBackButtonVisible(boolean z);

        void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3);

        void onWebAppExpand();

        void onWebAppOpenInvoice(String str, TLObject tLObject);

        void onWebAppReady();

        void onWebAppSetActionBarColor(String str);

        void onWebAppSetBackgroundColor(int i);

        /* renamed from: org.telegram.ui.Components.BotWebViewContainer$Delegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onSendWebViewData(Delegate _this, String data) {
            }

            public static void $default$onWebAppReady(Delegate _this) {
            }
        }
    }
}
