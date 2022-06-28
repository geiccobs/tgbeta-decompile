package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class EmbedBottomSheet extends BottomSheet {
    private static EmbedBottomSheet instance;
    private boolean animationInProgress;
    private FrameLayout containerLayout;
    private TextView copyTextButton;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private String embedUrl;
    private FrameLayout fullscreenVideoContainer;
    private boolean fullscreenedByButton;
    private boolean hasDescription;
    private int height;
    private LinearLayout imageButtonsContainer;
    private boolean isYouTube;
    private String openUrl;
    private OrientationEventListener orientationEventListener;
    private Activity parentActivity;
    private ImageView pipButton;
    private RadialProgressView progressBar;
    private View progressBarBlackBackground;
    private int seekTimeOverride;
    private WebPlayerView videoView;
    private int waitingForDraw;
    private boolean wasInLandscape;
    private WebView webView;
    private int width;
    private int[] position = new int[2];
    private int lastOrientation = -1;
    private int prevOrientation = -2;
    private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>";
    private DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.EmbedBottomSheet.1
        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialog) {
            if (PipVideoOverlay.isVisible() && EmbedBottomSheet.this.videoView.isInline()) {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.EmbedBottomSheet.1.1
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }
    };

    /* loaded from: classes5.dex */
    public class YoutubeProxy {
        private YoutubeProxy() {
            EmbedBottomSheet.this = r1;
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            if ("loaded".equals(eventName)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmbedBottomSheet$YoutubeProxy$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmbedBottomSheet.YoutubeProxy.this.m2575x10d0a2ae();
                    }
                });
            }
        }

        /* renamed from: lambda$postEvent$0$org-telegram-ui-Components-EmbedBottomSheet$YoutubeProxy */
        public /* synthetic */ void m2575x10d0a2ae() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }
    }

    public static void show(Activity activity, MessageObject message, PhotoViewer.PhotoViewerProvider photoViewerProvider, String title, String description, String originalUrl, String url, int w, int h, boolean keyboardVisible) {
        show(activity, message, photoViewerProvider, title, description, originalUrl, url, w, h, -1, keyboardVisible);
    }

    public static void show(Activity activity, MessageObject message, PhotoViewer.PhotoViewerProvider photoViewerProvider, String title, String description, String originalUrl, String url, int w, int h, int seekTime, boolean keyboardVisible) {
        EmbedBottomSheet embedBottomSheet = instance;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        String youtubeId = (message == null || message.messageOwner.media == null || message.messageOwner.media.webpage == null) ? null : WebPlayerView.getYouTubeVideoId(url);
        if (youtubeId != null) {
            PhotoViewer.getInstance().setParentActivity(activity);
            PhotoViewer.getInstance().openPhoto(message, seekTime, null, 0L, 0L, photoViewerProvider);
            return;
        }
        EmbedBottomSheet sheet = new EmbedBottomSheet(activity, title, description, originalUrl, url, w, h, seekTime);
        sheet.setCalcMandatoryInsets(keyboardVisible);
        sheet.show();
    }

    private EmbedBottomSheet(Context context, String title, String description, String originalUrl, String url, int w, int h, int seekTime) {
        super(context, false);
        this.fullWidth = true;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        this.seekTimeOverride = seekTime;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.embedUrl = url;
        this.hasDescription = description != null && description.length() > 0;
        this.openUrl = originalUrl;
        this.width = w;
        this.height = h;
        if (w == 0 || h == 0) {
            this.width = AndroidUtilities.displaySize.x;
            this.height = AndroidUtilities.displaySize.y / 2;
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fullscreenVideoContainer = frameLayout;
        frameLayout.setKeepScreenOn(true);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        if (Build.VERSION.SDK_INT >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        this.fullscreenVideoContainer.setOnTouchListener(EmbedBottomSheet$$ExternalSyntheticLambda4.INSTANCE);
        this.container.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenVideoContainer.setVisibility(4);
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmbedBottomSheet.2
            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                try {
                    if ((!PipVideoOverlay.isVisible() || EmbedBottomSheet.this.webView.getVisibility() != 0) && EmbedBottomSheet.this.webView.getParent() != null) {
                        removeView(EmbedBottomSheet.this.webView);
                        EmbedBottomSheet.this.webView.stopLoading();
                        EmbedBottomSheet.this.webView.loadUrl("about:blank");
                        EmbedBottomSheet.this.webView.destroy();
                    }
                    if (!EmbedBottomSheet.this.videoView.isInline() && !PipVideoOverlay.isVisible()) {
                        if (EmbedBottomSheet.instance == EmbedBottomSheet.this) {
                            EmbedBottomSheet unused = EmbedBottomSheet.instance = null;
                        }
                        EmbedBottomSheet.this.videoView.destroy();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                float scale = EmbedBottomSheet.this.width / parentWidth;
                int h2 = (int) Math.min(EmbedBottomSheet.this.height / scale, AndroidUtilities.displaySize.y / 2);
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((EmbedBottomSheet.this.hasDescription ? 22 : 0) + 84) + h2 + 1, C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        this.containerLayout = frameLayout2;
        frameLayout2.setOnTouchListener(EmbedBottomSheet$$ExternalSyntheticLambda5.INSTANCE);
        setCustomView(this.containerLayout);
        WebView webView = new WebView(context) { // from class: org.telegram.ui.Components.EmbedBottomSheet.3
            @Override // android.webkit.WebView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                boolean result = super.onTouchEvent(event);
                if (result) {
                    if (event.getAction() == 1) {
                        EmbedBottomSheet.this.setDisableScroll(false);
                    } else {
                        EmbedBottomSheet.this.setDisableScroll(true);
                    }
                }
                return result;
            }
        };
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebChromeClient(new WebChromeClient() { // from class: org.telegram.ui.Components.EmbedBottomSheet.4
            @Override // android.webkit.WebChromeClient
            public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
                onShowCustomView(view, callback);
            }

            @Override // android.webkit.WebChromeClient
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                if (EmbedBottomSheet.this.customView != null || PipVideoOverlay.isVisible()) {
                    callback.onCustomViewHidden();
                    return;
                }
                EmbedBottomSheet.this.exitFromPip();
                EmbedBottomSheet.this.customView = view;
                EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                EmbedBottomSheet.this.customViewCallback = callback;
            }

            @Override // android.webkit.WebChromeClient
            public void onHideCustomView() {
                super.onHideCustomView();
                if (EmbedBottomSheet.this.customView == null) {
                    return;
                }
                EmbedBottomSheet.this.getSheetContainer().setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                EmbedBottomSheet.this.fullscreenVideoContainer.removeView(EmbedBottomSheet.this.customView);
                if (EmbedBottomSheet.this.customViewCallback != null && !EmbedBottomSheet.this.customViewCallback.getClass().getName().contains(".chromium.")) {
                    EmbedBottomSheet.this.customViewCallback.onCustomViewHidden();
                }
                EmbedBottomSheet.this.customView = null;
            }
        });
        this.webView.setWebViewClient(new WebViewClient() { // from class: org.telegram.ui.Components.EmbedBottomSheet.5
            @Override // android.webkit.WebViewClient
            public void onLoadResource(WebView view, String url2) {
                super.onLoadResource(view, url2);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(view, url2);
                if (!EmbedBottomSheet.this.isYouTube || Build.VERSION.SDK_INT < 17) {
                    EmbedBottomSheet.this.progressBar.setVisibility(4);
                    EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
                    EmbedBottomSheet.this.pipButton.setEnabled(true);
                    EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
                }
            }

            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView view, String url2) {
                if (EmbedBottomSheet.this.isYouTube) {
                    Browser.openUrl(view.getContext(), url2);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url2);
            }
        });
        int i = 22;
        this.containerLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 84));
        WebPlayerView webPlayerView = new WebPlayerView(context, true, false, new WebPlayerView.WebPlayerViewDelegate() { // from class: org.telegram.ui.Components.EmbedBottomSheet.6
            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public void onInitFailed() {
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
                EmbedBottomSheet.this.copyTextButton.setVisibility(4);
                EmbedBottomSheet.this.webView.setKeepScreenOn(true);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
                EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
                if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
                    EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
                }
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, null, false);
                HashMap<String, String> args = new HashMap<>();
                args.put("Referer", "messenger.telegram.org");
                try {
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public TextureView onSwitchToFullscreen(View controlsView, boolean fullscreen, float aspectRatio, int rotation, boolean byButton) {
                if (!fullscreen) {
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    EmbedBottomSheet.this.fullscreenedByButton = false;
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                            return null;
                        } catch (Exception e) {
                            FileLog.e(e);
                            return null;
                        }
                    }
                    return null;
                }
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
                EmbedBottomSheet.this.wasInLandscape = false;
                EmbedBottomSheet.this.fullscreenedByButton = byButton;
                if (EmbedBottomSheet.this.parentActivity != null) {
                    try {
                        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                        embedBottomSheet.prevOrientation = embedBottomSheet.parentActivity.getRequestedOrientation();
                        if (byButton) {
                            WindowManager manager = (WindowManager) EmbedBottomSheet.this.parentActivity.getSystemService("window");
                            int displayRotation = manager.getDefaultDisplay().getRotation();
                            if (displayRotation == 3) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(8);
                            } else {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
                            }
                        }
                        EmbedBottomSheet.this.containerView.setSystemUiVisibility(1028);
                        return null;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        return null;
                    }
                }
                return null;
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public void onVideoSizeChanged(float aspectRatio, int rotation) {
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public void onInlineSurfaceTextureReady() {
                if (EmbedBottomSheet.this.videoView.isInline()) {
                    EmbedBottomSheet.this.dismissInternal();
                }
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public void prepareToSwitchInlineMode(boolean inline, final Runnable switchInlineModeRunnable, float aspectRatio, boolean animated) {
                if (inline) {
                    if (EmbedBottomSheet.this.parentActivity != null) {
                        try {
                            EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                            if (EmbedBottomSheet.this.prevOrientation != -2) {
                                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                        EmbedBottomSheet.this.containerView.setTranslationY(EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f));
                        EmbedBottomSheet.this.backDrawable.setAlpha(0);
                    }
                    EmbedBottomSheet.this.setOnShowListener(null);
                    if (animated) {
                        TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                        View controlsView = EmbedBottomSheet.this.videoView.getControlsView();
                        ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                        Rect rect = PipVideoOverlay.getPipRect(true, aspectRatio);
                        float scale = rect.width / textureView.getWidth();
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, scale), ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, scale), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, rect.x), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, rect.y), ObjectAnimator.ofFloat(textureView, View.SCALE_X, scale), ObjectAnimator.ofFloat(textureView, View.SCALE_Y, scale), ObjectAnimator.ofFloat(textureView, View.TRANSLATION_X, rect.x), ObjectAnimator.ofFloat(textureView, View.TRANSLATION_Y, rect.y), ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, View.TRANSLATION_Y, EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f)), ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 0), ObjectAnimator.ofFloat(EmbedBottomSheet.this.fullscreenVideoContainer, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(controlsView, View.ALPHA, 0.0f));
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.setDuration(250L);
                        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmbedBottomSheet.6.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                                    EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                                }
                                switchInlineModeRunnable.run();
                            }
                        });
                        animatorSet.start();
                        return;
                    }
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                        EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                        EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    switchInlineModeRunnable.run();
                    EmbedBottomSheet.this.dismissInternal();
                    return;
                }
                if (ApplicationLoader.mainInterfacePaused) {
                    try {
                        EmbedBottomSheet.this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
                if (animated) {
                    EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                    embedBottomSheet.setOnShowListener(embedBottomSheet.onShowListener);
                    Rect rect2 = PipVideoOverlay.getPipRect(false, aspectRatio);
                    TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                    ImageView textureImageView2 = EmbedBottomSheet.this.videoView.getTextureImageView();
                    float scale2 = rect2.width / textureView2.getLayoutParams().width;
                    textureImageView2.setScaleX(scale2);
                    textureImageView2.setScaleY(scale2);
                    textureImageView2.setTranslationX(rect2.x);
                    textureImageView2.setTranslationY(rect2.y);
                    textureView2.setScaleX(scale2);
                    textureView2.setScaleY(scale2);
                    textureView2.setTranslationX(rect2.x);
                    textureView2.setTranslationY(rect2.y);
                } else {
                    PipVideoOverlay.dismiss();
                }
                EmbedBottomSheet.this.setShowWithoutAnimation(true);
                EmbedBottomSheet.this.show();
                if (animated) {
                    EmbedBottomSheet.this.waitingForDraw = 4;
                    EmbedBottomSheet.this.backDrawable.setAlpha(1);
                    EmbedBottomSheet.this.containerView.setTranslationY(EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f));
                }
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public TextureView onSwitchInlineMode(View controlsView, boolean inline, int videoWidth, int videoHeight, int rotation, boolean animated) {
                if (inline) {
                    controlsView.setTranslationY(0.0f);
                    TextureView textureView = new TextureView(EmbedBottomSheet.this.parentActivity);
                    if (!PipVideoOverlay.show(false, EmbedBottomSheet.this.parentActivity, textureView, videoWidth, videoHeight)) {
                        return null;
                    }
                    PipVideoOverlay.setParentSheet(EmbedBottomSheet.this);
                    return textureView;
                } else if (animated) {
                    EmbedBottomSheet.this.animationInProgress = true;
                    View view = EmbedBottomSheet.this.videoView.getAspectRatioView();
                    view.getLocationInWindow(EmbedBottomSheet.this.position);
                    int[] iArr = EmbedBottomSheet.this.position;
                    iArr[0] = iArr[0] - EmbedBottomSheet.this.getLeftInset();
                    int[] iArr2 = EmbedBottomSheet.this.position;
                    iArr2[1] = (int) (iArr2[1] - EmbedBottomSheet.this.containerView.getTranslationY());
                    TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                    ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, EmbedBottomSheet.this.position[0]), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, EmbedBottomSheet.this.position[1]), ObjectAnimator.ofFloat(textureView2, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(textureView2, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(textureView2, View.TRANSLATION_X, EmbedBottomSheet.this.position[0]), ObjectAnimator.ofFloat(textureView2, View.TRANSLATION_Y, EmbedBottomSheet.this.position[1]), ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 51));
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250L);
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmbedBottomSheet.6.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            EmbedBottomSheet.this.animationInProgress = false;
                        }
                    });
                    animatorSet.start();
                    return null;
                } else {
                    EmbedBottomSheet.this.containerView.setTranslationY(0.0f);
                    return null;
                }
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public void onSharePressed() {
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                if (playing) {
                    try {
                        EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public boolean checkInlinePermissions() {
                return EmbedBottomSheet.this.checkInlinePermissions();
            }

            @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
            public ViewGroup getTextureViewContainer() {
                return EmbedBottomSheet.this.container;
            }
        });
        this.videoView = webPlayerView;
        webPlayerView.setVisibility(4);
        this.containerLayout.addView(this.videoView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, ((this.hasDescription ? 22 : 0) + 84) - 10));
        View view = new View(context);
        this.progressBarBlackBackground = view;
        view.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        this.containerLayout.addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 84));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        this.containerLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, ((!this.hasDescription ? 0 : i) + 84) / 2));
        if (this.hasDescription) {
            TextView textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            textView.setText(description);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray));
        textView2.setText(title);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.containerLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View lineView = new View(context);
        lineView.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        this.containerLayout.addView(lineView, new FrameLayout.LayoutParams(-1, 1, 83));
        ((FrameLayout.LayoutParams) lineView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        FrameLayout frameLayout3 = new FrameLayout(context);
        frameLayout3.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerLayout.addView(frameLayout3, LayoutHelper.createFrame(-1, 48, 83));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        frameLayout3.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
        TextView textView3 = new TextView(context);
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        textView3.setGravity(17);
        textView3.setSingleLine(true);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        textView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        textView3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView3.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
        textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        frameLayout3.addView(textView3, LayoutHelper.createLinear(-2, -1, 51));
        textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                EmbedBottomSheet.this.m2571lambda$new$2$orgtelegramuiComponentsEmbedBottomSheet(view2);
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.imageButtonsContainer = linearLayout2;
        linearLayout2.setVisibility(4);
        frameLayout3.addView(this.imageButtonsContainer, LayoutHelper.createFrame(-2, -1, 17));
        ImageView imageView = new ImageView(context);
        this.pipButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.pipButton.setImageResource(R.drawable.ic_goinline);
        this.pipButton.setContentDescription(LocaleController.getString("AccDescrPipMode", R.string.AccDescrPipMode));
        this.pipButton.setEnabled(false);
        this.pipButton.setAlpha(0.5f);
        this.pipButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlue4), PorterDuff.Mode.MULTIPLY));
        this.pipButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        this.imageButtonsContainer.addView(this.pipButton, LayoutHelper.createFrame(48, 48.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        this.pipButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                EmbedBottomSheet.this.m2572lambda$new$3$orgtelegramuiComponentsEmbedBottomSheet(view2);
            }
        });
        View.OnClickListener copyClickListener = new View.OnClickListener() { // from class: org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                EmbedBottomSheet.this.m2573lambda$new$4$orgtelegramuiComponentsEmbedBottomSheet(view2);
            }
        };
        ImageView copyButton = new ImageView(context);
        copyButton.setScaleType(ImageView.ScaleType.CENTER);
        copyButton.setImageResource(R.drawable.msg_copy);
        copyButton.setContentDescription(LocaleController.getString("CopyLink", R.string.CopyLink));
        copyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlue4), PorterDuff.Mode.MULTIPLY));
        copyButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        this.imageButtonsContainer.addView(copyButton, LayoutHelper.createFrame(48, 48, 51));
        copyButton.setOnClickListener(copyClickListener);
        TextView textView4 = new TextView(context);
        this.copyTextButton = textView4;
        textView4.setTextSize(1, 14.0f);
        this.copyTextButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        this.copyTextButton.setGravity(17);
        this.copyTextButton.setSingleLine(true);
        this.copyTextButton.setEllipsize(TextUtils.TruncateAt.END);
        this.copyTextButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        this.copyTextButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.copyTextButton.setText(LocaleController.getString("Copy", R.string.Copy).toUpperCase());
        this.copyTextButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        linearLayout.addView(this.copyTextButton, LayoutHelper.createFrame(-2, -1, 51));
        this.copyTextButton.setOnClickListener(copyClickListener);
        TextView openInButton = new TextView(context);
        openInButton.setTextSize(1, 14.0f);
        openInButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue4));
        openInButton.setGravity(17);
        openInButton.setSingleLine(true);
        openInButton.setEllipsize(TextUtils.TruncateAt.END);
        openInButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        openInButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        openInButton.setText(LocaleController.getString("OpenInBrowser", R.string.OpenInBrowser).toUpperCase());
        openInButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        linearLayout.addView(openInButton, LayoutHelper.createFrame(-2, -1, 51));
        openInButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmbedBottomSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                EmbedBottomSheet.this.m2574lambda$new$5$orgtelegramuiComponentsEmbedBottomSheet(view2);
            }
        });
        final boolean canHandleUrl = this.videoView.canHandleUrl(this.embedUrl) || this.videoView.canHandleUrl(originalUrl);
        this.videoView.setVisibility(canHandleUrl ? 0 : 4);
        if (canHandleUrl) {
            this.videoView.willHandle();
        }
        setDelegate(new BottomSheet.BottomSheetDelegate() { // from class: org.telegram.ui.Components.EmbedBottomSheet.8
            @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate, org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
            public void onOpenAnimationEnd() {
                boolean handled = canHandleUrl && EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, null, EmbedBottomSheet.this.openUrl, true);
                if (!handled) {
                    EmbedBottomSheet.this.progressBar.setVisibility(0);
                    EmbedBottomSheet.this.webView.setVisibility(0);
                    EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
                    EmbedBottomSheet.this.copyTextButton.setVisibility(4);
                    EmbedBottomSheet.this.webView.setKeepScreenOn(true);
                    EmbedBottomSheet.this.videoView.setVisibility(4);
                    EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
                    EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
                    if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
                        EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
                    }
                    EmbedBottomSheet.this.videoView.loadVideo(null, null, null, null, false);
                    HashMap<String, String> args = new HashMap<>();
                    args.put("Referer", "messenger.telegram.org");
                    try {
                        String currentYoutubeId = EmbedBottomSheet.this.videoView.getYoutubeId();
                        if (currentYoutubeId != null) {
                            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(0);
                            EmbedBottomSheet.this.isYouTube = true;
                            String t = null;
                            if (Build.VERSION.SDK_INT >= 17) {
                                EmbedBottomSheet.this.webView.addJavascriptInterface(new YoutubeProxy(), "YoutubeProxy");
                            }
                            int seekToTime = 0;
                            if (EmbedBottomSheet.this.openUrl != null) {
                                try {
                                    Uri uri = Uri.parse(EmbedBottomSheet.this.openUrl);
                                    if (EmbedBottomSheet.this.seekTimeOverride > 0) {
                                        t = "" + EmbedBottomSheet.this.seekTimeOverride;
                                    }
                                    if (t == null && (t = uri.getQueryParameter(Theme.THEME_BACKGROUND_SLUG)) == null) {
                                        t = uri.getQueryParameter("time_continue");
                                    }
                                    if (t != null) {
                                        if (t.contains("m")) {
                                            String[] arg = t.split("m");
                                            seekToTime = (Utilities.parseInt((CharSequence) arg[0]).intValue() * 60) + Utilities.parseInt((CharSequence) arg[1]).intValue();
                                        } else {
                                            seekToTime = Utilities.parseInt((CharSequence) t).intValue();
                                        }
                                    }
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                            }
                            EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://messenger.telegram.org/", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>", currentYoutubeId, Integer.valueOf(seekToTime)), "text/html", "UTF-8", "https://youtube.com");
                            return;
                        }
                        EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, args);
                        return;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        return;
                    }
                }
                EmbedBottomSheet.this.progressBar.setVisibility(4);
                EmbedBottomSheet.this.webView.setVisibility(4);
                EmbedBottomSheet.this.videoView.setVisibility(0);
            }

            @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate, org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
            public boolean canDismiss() {
                if (EmbedBottomSheet.this.videoView.isInFullscreen()) {
                    EmbedBottomSheet.this.videoView.exitFullscreen();
                    return false;
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                    return true;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            }
        });
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) { // from class: org.telegram.ui.Components.EmbedBottomSheet.9
            @Override // android.view.OrientationEventListener
            public void onOrientationChanged(int orientation) {
                if (EmbedBottomSheet.this.orientationEventListener != null && EmbedBottomSheet.this.videoView.getVisibility() == 0 && EmbedBottomSheet.this.parentActivity != null && EmbedBottomSheet.this.videoView.isInFullscreen() && EmbedBottomSheet.this.fullscreenedByButton) {
                    if (orientation < 240 || orientation > 300) {
                        if (!EmbedBottomSheet.this.wasInLandscape || orientation <= 0) {
                            return;
                        }
                        if (orientation >= 330 || orientation <= 30) {
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                            EmbedBottomSheet.this.fullscreenedByButton = false;
                            EmbedBottomSheet.this.wasInLandscape = false;
                            return;
                        }
                        return;
                    }
                    EmbedBottomSheet.this.wasInLandscape = true;
                }
            }
        };
        String currentYoutubeId = WebPlayerView.getYouTubeVideoId(this.embedUrl);
        if (currentYoutubeId != null || !canHandleUrl) {
            this.progressBar.setVisibility(0);
            this.webView.setVisibility(0);
            this.imageButtonsContainer.setVisibility(0);
            if (currentYoutubeId != null) {
                this.progressBarBlackBackground.setVisibility(0);
            }
            this.copyTextButton.setVisibility(4);
            this.webView.setKeepScreenOn(true);
            this.videoView.setVisibility(4);
            this.videoView.getControlsView().setVisibility(4);
            this.videoView.getTextureView().setVisibility(4);
            if (this.videoView.getTextureImageView() != null) {
                this.videoView.getTextureImageView().setVisibility(4);
            }
            if (currentYoutubeId != null && "disabled".equals(MessagesController.getInstance(this.currentAccount).youtubePipType)) {
                this.pipButton.setVisibility(8);
            }
        }
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
        } else {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        instance = this;
    }

    public static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-EmbedBottomSheet */
    public /* synthetic */ void m2571lambda$new$2$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        dismiss();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-EmbedBottomSheet */
    public /* synthetic */ void m2572lambda$new$3$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        if (!PipVideoOverlay.isVisible()) {
            boolean inAppOnly = this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
            if ((inAppOnly || checkInlinePermissions()) && this.progressBar.getVisibility() != 0) {
                if (PipVideoOverlay.show(inAppOnly, this.parentActivity, this.webView, this.width, this.height)) {
                    PipVideoOverlay.setParentSheet(this);
                }
                if (this.isYouTube) {
                    runJsCode("hideControls();");
                }
                if (0 != 0) {
                    this.animationInProgress = true;
                    View view = this.videoView.getAspectRatioView();
                    view.getLocationInWindow(this.position);
                    int[] iArr = this.position;
                    iArr[0] = iArr[0] - getLeftInset();
                    int[] iArr2 = this.position;
                    iArr2[1] = (int) (iArr2[1] - this.containerView.getTranslationY());
                    TextureView textureView = this.videoView.getTextureView();
                    ImageView textureImageView = this.videoView.getTextureImageView();
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(ObjectAnimator.ofFloat(textureImageView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(textureImageView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_X, this.position[0]), ObjectAnimator.ofFloat(textureImageView, View.TRANSLATION_Y, this.position[1]), ObjectAnimator.ofFloat(textureView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(textureView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(textureView, View.TRANSLATION_X, this.position[0]), ObjectAnimator.ofFloat(textureView, View.TRANSLATION_Y, this.position[1]), ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofInt(this.backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, 51));
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250L);
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmbedBottomSheet.7
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            EmbedBottomSheet.this.animationInProgress = false;
                        }
                    });
                    animatorSet.start();
                } else {
                    this.containerView.setTranslationY(0.0f);
                }
                dismissInternal();
                return;
            }
            return;
        }
        PipVideoOverlay.dismiss();
        v.getClass();
        AndroidUtilities.runOnUIThread(new ChatActivityEnterView$$ExternalSyntheticLambda26(v), 300L);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-EmbedBottomSheet */
    public /* synthetic */ void m2573lambda$new$4$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        try {
            ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, this.openUrl);
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            FileLog.e(e);
        }
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).showBulletin(EmbedBottomSheet$$ExternalSyntheticLambda6.INSTANCE);
        }
        dismiss();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-EmbedBottomSheet */
    public /* synthetic */ void m2574lambda$new$5$orgtelegramuiComponentsEmbedBottomSheet(View v) {
        Browser.openUrl(this.parentActivity, this.openUrl);
        dismiss();
    }

    private void runJsCode(String code) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(code, null);
            return;
        }
        try {
            WebView webView = this.webView;
            webView.loadUrl("javascript:" + code);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, null);
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return this.videoView.getVisibility() != 0 || !this.videoView.isInFullscreen();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.videoView.getVisibility() == 0 && this.videoView.isInitied() && !this.videoView.isInline()) {
            if (newConfig.orientation == 2) {
                if (!this.videoView.isInFullscreen()) {
                    this.videoView.enterFullscreen();
                }
            } else if (this.videoView.isInFullscreen()) {
                this.videoView.exitFullscreen();
            }
        }
    }

    public void destroy() {
        WebView webView = this.webView;
        if (webView != null && webView.getVisibility() == 0) {
            this.containerLayout.removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        }
        PipVideoOverlay.dismiss();
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null) {
            webPlayerView.destroy();
        }
        instance = null;
        dismissInternal();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }

    public void exitFromPip() {
        if (this.webView == null || !PipVideoOverlay.isVisible()) {
            return;
        }
        if (ApplicationLoader.mainInterfacePaused) {
            try {
                this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (this.isYouTube) {
            runJsCode("showControls();");
        }
        ViewGroup parent = (ViewGroup) this.webView.getParent();
        if (parent != null) {
            parent.removeView(this.webView);
        }
        this.containerLayout.addView(this.webView, 0, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 84));
        setShowWithoutAnimation(true);
        show();
        PipVideoOverlay.dismiss(true);
    }

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public void updateTextureViewPosition() {
        View view = this.videoView.getAspectRatioView();
        view.getLocationInWindow(this.position);
        int[] iArr = this.position;
        iArr[0] = iArr[0] - getLeftInset();
        if (!this.videoView.isInline() && !this.animationInProgress) {
            TextureView textureView = this.videoView.getTextureView();
            textureView.setTranslationX(this.position[0]);
            textureView.setTranslationY(this.position[1]);
            View textureImageView = this.videoView.getTextureImageView();
            if (textureImageView != null) {
                textureImageView.setTranslationX(this.position[0]);
                textureImageView.setTranslationY(this.position[1]);
            }
        }
        View controlsView = this.videoView.getControlsView();
        if (controlsView.getParent() == this.container) {
            controlsView.setTranslationY(this.position[1]);
        } else {
            controlsView.setTranslationY(0.0f);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithTouchOutside() {
        return this.fullscreenVideoContainer.getVisibility() != 0;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onContainerTranslationYChanged(float translationY) {
        updateTextureViewPosition();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomMeasure(View view, int width, int height) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        if (view == this.videoView.getControlsView()) {
            updateTextureViewPosition();
            return false;
        }
        return false;
    }

    public void pause() {
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null && webPlayerView.isInitied()) {
            this.videoView.pause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onContainerDraw(Canvas canvas) {
        int i = this.waitingForDraw;
        if (i != 0) {
            int i2 = i - 1;
            this.waitingForDraw = i2;
            if (i2 == 0) {
                this.videoView.updateTextureImageView();
                PipVideoOverlay.dismiss();
                return;
            }
            this.container.invalidate();
        }
    }
}
