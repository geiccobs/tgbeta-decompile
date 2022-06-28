package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.internal.view.SupportMenu;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.wearable.WearableStatusCodes;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.WebPlayerView;
/* loaded from: classes5.dex */
public class WebPlayerView extends ViewGroup implements VideoPlayer.VideoPlayerDelegate, AudioManager.OnAudioFocusChangeListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
    private boolean allowInlineAnimation;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private int audioFocus;
    private Paint backgroundPaint;
    private TextureView changedTextureView;
    private boolean changingTextureView;
    private ControlsView controlsView;
    private float currentAlpha;
    private Bitmap currentBitmap;
    private AsyncTask currentTask;
    private String currentYoutubeId;
    private WebPlayerViewDelegate delegate;
    private boolean drawImage;
    private boolean firstFrameRendered;
    private int fragment_container_id;
    private ImageView fullscreenButton;
    private boolean hasAudioFocus;
    private boolean inFullscreen;
    private boolean initFailed;
    private boolean initied;
    private ImageView inlineButton;
    private String interfaceName;
    private boolean isAutoplay;
    private boolean isCompleted;
    private boolean isInline;
    private boolean isLoading;
    private boolean isStream;
    private long lastUpdateTime;
    private String playAudioType;
    private String playAudioUrl;
    private ImageView playButton;
    private String playVideoType;
    private String playVideoUrl;
    private AnimatorSet progressAnimation;
    private Runnable progressRunnable;
    private RadialProgressView progressView;
    private boolean resumeAudioOnFocusGain;
    private int seekToTime;
    private ImageView shareButton;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Runnable switchToInlineRunnable;
    private boolean switchingInlineMode;
    private ImageView textureImageView;
    private TextureView textureView;
    private ViewGroup textureViewContainer;
    private int videoHeight;
    private VideoPlayer videoPlayer;
    private int videoWidth;
    private int waitingForFirstTextureUpload;
    private WebView webView;
    private static int lastContainerId = WearableStatusCodes.DUPLICATE_LISTENER;
    private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    private static final Pattern vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern twitchClipIdRegex = Pattern.compile("https?://clips\\.twitch\\.tv/(?:[^/]+/)*([^/?#&]+)");
    private static final Pattern twitchStreamIdRegex = Pattern.compile("https?://(?:(?:www\\.)?twitch\\.tv/|player\\.twitch\\.tv/\\?.*?\\bchannel=)([^/#?]+)");
    private static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern twitchClipFilePattern = Pattern.compile("clipInfo\\s*=\\s*(\\{[^']+\\});");
    private static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    private static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    private static final Pattern sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    private static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    private static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    private static final Pattern exprParensPattern = Pattern.compile("[()]");
    private static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|(?:/[a-z]{2}_[A-Z]{2})?/base)?\\.([a-z]+)$");

    /* loaded from: classes5.dex */
    public interface CallJavaResultInterface {
        void jsCallFinished(String str);
    }

    /* loaded from: classes5.dex */
    public interface WebPlayerViewDelegate {
        boolean checkInlinePermissions();

        ViewGroup getTextureViewContainer();

        void onInitFailed();

        void onInlineSurfaceTextureReady();

        void onPlayStateChanged(WebPlayerView webPlayerView, boolean z);

        void onSharePressed();

        TextureView onSwitchInlineMode(View view, boolean z, int i, int i2, int i3, boolean z2);

        TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2);

        void onVideoSizeChanged(float f, int i);

        void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2);
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
    }

    static /* synthetic */ float access$4724(WebPlayerView x0, float x1) {
        float f = x0.currentAlpha - x1;
        x0.currentAlpha = f;
        return f;
    }

    /* loaded from: classes5.dex */
    private static abstract class function {
        public abstract Object run(Object[] objArr);

        private function() {
        }
    }

    /* loaded from: classes5.dex */
    public static class JSExtractor {
        private String jsCode;
        ArrayList<String> codeLines = new ArrayList<>();
        private String[] operators = {"|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*"};
        private String[] assign_operators = {"|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "="};

        public JSExtractor(String js) {
            this.jsCode = js;
        }

        private void interpretExpression(String expr, HashMap<String, String> localVars, int allowRecursion) throws Exception {
            String expr2 = expr.trim();
            if (TextUtils.isEmpty(expr2)) {
                return;
            }
            if (expr2.charAt(0) == '(') {
                int parens_count = 0;
                Matcher matcher = WebPlayerView.exprParensPattern.matcher(expr2);
                while (true) {
                    if (!matcher.find()) {
                        break;
                    }
                    String group = matcher.group(0);
                    if (group.indexOf(48) == 40) {
                        parens_count++;
                    } else {
                        parens_count--;
                        if (parens_count == 0) {
                            String sub_expr = expr2.substring(1, matcher.start());
                            interpretExpression(sub_expr, localVars, allowRecursion);
                            String remaining_expr = expr2.substring(matcher.end()).trim();
                            if (TextUtils.isEmpty(remaining_expr)) {
                                return;
                            }
                            expr2 = remaining_expr;
                        }
                    }
                }
                if (parens_count != 0) {
                    throw new Exception(String.format("Premature end of parens in %s", expr2));
                }
            }
            int a = 0;
            while (true) {
                String[] strArr = this.assign_operators;
                if (a < strArr.length) {
                    Matcher matcher2 = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", WebPlayerView.exprName, Pattern.quote(strArr[a]))).matcher(expr2);
                    if (!matcher2.find()) {
                        a++;
                    } else {
                        interpretExpression(matcher2.group(3), localVars, allowRecursion - 1);
                        String index = matcher2.group(2);
                        if (!TextUtils.isEmpty(index)) {
                            interpretExpression(index, localVars, allowRecursion);
                            return;
                        } else {
                            localVars.put(matcher2.group(1), "");
                            return;
                        }
                    }
                } else {
                    try {
                        Integer.parseInt(expr2);
                        return;
                    } catch (Exception e) {
                        if (Pattern.compile(String.format(Locale.US, "(?!if|return|true|false)(%s)$", WebPlayerView.exprName)).matcher(expr2).find()) {
                            return;
                        }
                        if (expr2.charAt(0) == '\"' && expr2.charAt(expr2.length() - 1) == '\"') {
                            return;
                        }
                        try {
                            new JSONObject(expr2).toString();
                            return;
                        } catch (Exception e2) {
                            Matcher matcher3 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", WebPlayerView.exprName)).matcher(expr2);
                            if (matcher3.find()) {
                                matcher3.group(1);
                                interpretExpression(matcher3.group(2), localVars, allowRecursion - 1);
                                return;
                            }
                            Matcher matcher4 = Pattern.compile(String.format(Locale.US, "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$", WebPlayerView.exprName)).matcher(expr2);
                            if (matcher4.find()) {
                                String variable = matcher4.group(1);
                                String m1 = matcher4.group(2);
                                String m2 = matcher4.group(3);
                                (TextUtils.isEmpty(m1) ? m2 : m1).replace("\"", "");
                                String arg_str = matcher4.group(4);
                                if (localVars.get(variable) == null) {
                                    extractObject(variable);
                                }
                                if (arg_str == null) {
                                    return;
                                }
                                if (expr2.charAt(expr2.length() - 1) != ')') {
                                    throw new Exception("last char not ')'");
                                }
                                if (arg_str.length() != 0) {
                                    String[] args = arg_str.split(",");
                                    for (String str : args) {
                                        interpretExpression(str, localVars, allowRecursion);
                                    }
                                    return;
                                }
                                return;
                            }
                            Matcher matcher5 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", WebPlayerView.exprName)).matcher(expr2);
                            if (matcher5.find()) {
                                localVars.get(matcher5.group(1));
                                interpretExpression(matcher5.group(2), localVars, allowRecursion - 1);
                                return;
                            }
                            int a2 = 0;
                            while (true) {
                                String[] strArr2 = this.operators;
                                if (a2 < strArr2.length) {
                                    String func = strArr2[a2];
                                    Matcher matcher6 = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", Pattern.quote(func))).matcher(expr2);
                                    if (matcher6.find()) {
                                        boolean[] abort = new boolean[1];
                                        interpretStatement(matcher6.group(1), localVars, abort, allowRecursion - 1);
                                        if (abort[0]) {
                                            throw new Exception(String.format("Premature left-side return of %s in %s", func, expr2));
                                        }
                                        interpretStatement(matcher6.group(2), localVars, abort, allowRecursion - 1);
                                        if (abort[0]) {
                                            throw new Exception(String.format("Premature right-side return of %s in %s", func, expr2));
                                        }
                                    }
                                    a2++;
                                } else {
                                    Matcher matcher7 = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", WebPlayerView.exprName)).matcher(expr2);
                                    if (matcher7.find()) {
                                        String fname = matcher7.group(1);
                                        extractFunction(fname);
                                    }
                                    throw new Exception(String.format("Unsupported JS expression %s", expr2));
                                }
                            }
                        }
                    }
                }
            }
        }

        private void interpretStatement(String stmt, HashMap<String, String> localVars, boolean[] abort, int allowRecursion) throws Exception {
            String expr;
            if (allowRecursion < 0) {
                throw new Exception("recursion limit reached");
            }
            abort[0] = false;
            String stmt2 = stmt.trim();
            Matcher matcher = WebPlayerView.stmtVarPattern.matcher(stmt2);
            if (!matcher.find()) {
                Matcher matcher2 = WebPlayerView.stmtReturnPattern.matcher(stmt2);
                if (matcher2.find()) {
                    String expr2 = stmt2.substring(matcher2.group(0).length());
                    abort[0] = true;
                    expr = expr2;
                } else {
                    expr = stmt2;
                }
            } else {
                expr = stmt2.substring(matcher.group(0).length());
            }
            interpretExpression(expr, localVars, allowRecursion);
        }

        private HashMap<String, Object> extractObject(String objname) throws Exception {
            HashMap<String, Object> obj = new HashMap<>();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", Pattern.quote(objname), "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')")).matcher(this.jsCode);
            String fields = null;
            while (true) {
                if (!matcher.find()) {
                    break;
                }
                String code = matcher.group();
                fields = matcher.group(2);
                if (!TextUtils.isEmpty(fields)) {
                    if (!this.codeLines.contains(code)) {
                        this.codeLines.add(matcher.group());
                    }
                }
            }
            Matcher matcher2 = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')")).matcher(fields);
            while (matcher2.find()) {
                String[] argnames = matcher2.group(2).split(",");
                buildFunction(argnames, matcher2.group(3));
            }
            return obj;
        }

        private void buildFunction(String[] argNames, String funcCode) throws Exception {
            HashMap<String, String> localVars = new HashMap<>();
            for (String str : argNames) {
                localVars.put(str, "");
            }
            String[] stmts = funcCode.split(";");
            boolean[] abort = new boolean[1];
            for (String str2 : stmts) {
                interpretStatement(str2, localVars, abort, 100);
                if (abort[0]) {
                    return;
                }
            }
        }

        public String extractFunction(String funcName) {
            try {
                String quote = Pattern.quote(funcName);
                Pattern funcPattern = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", quote, quote, quote));
                Matcher matcher = funcPattern.matcher(this.jsCode);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (!this.codeLines.contains(group)) {
                        ArrayList<String> arrayList = this.codeLines;
                        arrayList.add(group + ";");
                    }
                    buildFunction(matcher.group(1).split(","), matcher.group(2));
                }
            } catch (Exception e) {
                this.codeLines.clear();
                FileLog.e(e);
            }
            return TextUtils.join("", this.codeLines);
        }
    }

    /* loaded from: classes5.dex */
    public static class JavaScriptInterface {
        private final CallJavaResultInterface callJavaResultInterface;

        public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
            this.callJavaResultInterface = callJavaResult;
        }

        @JavascriptInterface
        public void returnResultToJava(String value) {
            this.callJavaResultInterface.jsCallFinished(value);
        }
    }

    protected String downloadUrlContent(AsyncTask parentTask, String url) {
        return downloadUrlContent(parentTask, url, null, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:124:0x01e7, code lost:
        r10 = r20;
     */
    /* JADX WARN: Removed duplicated region for block: B:137:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0219 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:144:0x0202 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:156:0x01a2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:172:0x0118 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x013b A[Catch: all -> 0x0144, TRY_LEAVE, TryCatch #0 {all -> 0x0144, blocks: (B:22:0x0066, B:58:0x0113, B:60:0x0118, B:65:0x0127, B:67:0x012e, B:68:0x013b), top: B:141:0x0066, inners: #16 }] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x016b  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0174  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected java.lang.String downloadUrlContent(android.os.AsyncTask r24, java.lang.String r25, java.util.HashMap<java.lang.String, java.lang.String> r26, boolean r27) {
        /*
            Method dump skipped, instructions count: 539
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    /* loaded from: classes5.dex */
    public class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private boolean canRetry = true;
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String vid) {
            WebPlayerView.this = r2;
            this.videoId = vid;
        }

        public String[] doInBackground(Void... voids) {
            char c;
            int index2;
            String playerId;
            String functionName;
            String functionCode;
            String[] extra;
            String params;
            String[] extra2;
            String params2;
            boolean encrypted;
            Exception e;
            String embedCode = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/embed/" + this.videoId);
            String[] strArr = null;
            if (isCancelled()) {
                return null;
            }
            String params3 = "video_id=" + this.videoId + "&ps=default&gl=US&hl=en";
            try {
                params3 = params3 + "&eurl=" + URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (embedCode != null) {
                Matcher matcher = WebPlayerView.stsPattern.matcher(embedCode);
                if (matcher.find()) {
                    params3 = params3 + "&sts=" + embedCode.substring(matcher.start() + 6, matcher.end());
                } else {
                    params3 = params3 + "&sts=";
                }
            }
            this.result[1] = DownloadRequest.TYPE_DASH;
            boolean encrypted2 = false;
            String otherUrl = null;
            int i = 2;
            String[] extra3 = {"", "&el=leanback", "&el=embedded", "&el=detailpage", "&el=vevo"};
            int i2 = 0;
            while (i2 < extra3.length) {
                String videoInfo = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/get_video_info?" + params3 + extra3[i2]);
                if (isCancelled()) {
                    return strArr;
                }
                boolean exists = false;
                String hls = null;
                boolean isLive = false;
                if (videoInfo == null) {
                    params = params3;
                    extra = extra3;
                } else {
                    String[] args = videoInfo.split("&");
                    int a = 0;
                    boolean isLive2 = false;
                    String hls2 = null;
                    boolean exists2 = false;
                    String otherUrl2 = otherUrl;
                    boolean encrypted3 = encrypted2;
                    while (a < args.length) {
                        if (args[a].startsWith("dashmpd")) {
                            exists2 = true;
                            String[] args2 = args[a].split("=");
                            if (args2.length == i) {
                                try {
                                    this.result[0] = URLDecoder.decode(args2[1], "UTF-8");
                                } catch (Exception e3) {
                                    FileLog.e(e3);
                                }
                            }
                            params2 = params3;
                            extra2 = extra3;
                        } else if (args[a].startsWith("url_encoded_fmt_stream_map")) {
                            String[] args22 = args[a].split("=");
                            params2 = params3;
                            if (args22.length != 2) {
                                encrypted = encrypted3;
                                extra2 = extra3;
                            } else {
                                try {
                                    String[] args3 = URLDecoder.decode(args22[1], "UTF-8").split("[&,]");
                                    boolean isMp4 = false;
                                    String currentUrl = null;
                                    int c2 = 0;
                                    while (true) {
                                        encrypted = encrypted3;
                                        try {
                                            if (c2 >= args3.length) {
                                                extra2 = extra3;
                                                break;
                                            }
                                            String[] args4 = args3[c2].split("=");
                                            String[] args32 = args3;
                                            extra2 = extra3;
                                            try {
                                                if (args4[0].startsWith(CommonProperties.TYPE)) {
                                                    String type = URLDecoder.decode(args4[1], "UTF-8");
                                                    if (type.contains(MimeTypes.VIDEO_MP4)) {
                                                        isMp4 = true;
                                                    }
                                                } else if (args4[0].startsWith(ImagesContract.URL)) {
                                                    currentUrl = URLDecoder.decode(args4[1], "UTF-8");
                                                } else if (args4[0].startsWith("itag")) {
                                                    currentUrl = null;
                                                    isMp4 = false;
                                                }
                                                if (!isMp4 || currentUrl == null) {
                                                    c2++;
                                                    encrypted3 = encrypted;
                                                    args3 = args32;
                                                    extra3 = extra2;
                                                } else {
                                                    String otherUrl3 = currentUrl;
                                                    otherUrl2 = otherUrl3;
                                                    break;
                                                }
                                            } catch (Exception e4) {
                                                e = e4;
                                                FileLog.e(e);
                                                encrypted3 = encrypted;
                                                a++;
                                                params3 = params2;
                                                extra3 = extra2;
                                                i = 2;
                                            }
                                        } catch (Exception e5) {
                                            e = e5;
                                            extra2 = extra3;
                                        }
                                    }
                                } catch (Exception e6) {
                                    e = e6;
                                    encrypted = encrypted3;
                                    extra2 = extra3;
                                }
                            }
                            encrypted3 = encrypted;
                        } else {
                            params2 = params3;
                            boolean encrypted4 = encrypted3;
                            extra2 = extra3;
                            if (args[a].startsWith("use_cipher_signature")) {
                                String[] args23 = args[a].split("=");
                                if (args23.length == 2 && args23[1].toLowerCase().equals("true")) {
                                    encrypted3 = true;
                                } else {
                                    encrypted3 = encrypted4;
                                }
                            } else if (!args[a].startsWith("hlsvp")) {
                                if (args[a].startsWith("livestream")) {
                                    String[] args24 = args[a].split("=");
                                    if (args24.length == 2 && args24[1].toLowerCase().equals(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE)) {
                                        isLive2 = true;
                                        encrypted3 = encrypted4;
                                    }
                                }
                                encrypted3 = encrypted4;
                            } else {
                                String[] args25 = args[a].split("=");
                                if (args25.length == 2) {
                                    try {
                                        String hls3 = URLDecoder.decode(args25[1], "UTF-8");
                                        hls2 = hls3;
                                    } catch (Exception e7) {
                                        FileLog.e(e7);
                                    }
                                }
                                encrypted3 = encrypted4;
                            }
                        }
                        a++;
                        params3 = params2;
                        extra3 = extra2;
                        i = 2;
                    }
                    params = params3;
                    boolean encrypted5 = encrypted3;
                    extra = extra3;
                    otherUrl = otherUrl2;
                    exists = exists2;
                    hls = hls2;
                    isLive = isLive2;
                    encrypted2 = encrypted5;
                }
                if (isLive) {
                    if (hls == null || encrypted2 || hls.contains("/s/")) {
                        return null;
                    }
                    String[] strArr2 = this.result;
                    strArr2[0] = hls;
                    strArr2[1] = DownloadRequest.TYPE_HLS;
                }
                if (exists) {
                    break;
                }
                i2++;
                params3 = params;
                extra3 = extra;
                strArr = null;
                i = 2;
            }
            String[] strArr3 = this.result;
            if (strArr3[0] == null && otherUrl != null) {
                strArr3[0] = otherUrl;
                strArr3[1] = "other";
            }
            if (strArr3[0] != null && ((encrypted2 || strArr3[0].contains("/s/")) && embedCode != null)) {
                int index = this.result[0].indexOf("/s/");
                int index22 = this.result[0].indexOf(47, index + 10);
                if (index != -1) {
                    if (index22 != -1) {
                        c = 0;
                        index2 = index22;
                    } else {
                        c = 0;
                        index2 = this.result[0].length();
                    }
                    this.sig = this.result[c].substring(index, index2);
                    String jsUrl = null;
                    Matcher matcher2 = WebPlayerView.jsPattern.matcher(embedCode);
                    if (matcher2.find()) {
                        try {
                            JSONTokener tokener = new JSONTokener(matcher2.group(1));
                            Object value = tokener.nextValue();
                            if (value instanceof String) {
                                jsUrl = (String) value;
                            }
                        } catch (Exception e8) {
                            FileLog.e(e8);
                        }
                    }
                    if (jsUrl != null) {
                        Matcher matcher3 = WebPlayerView.playerIdPattern.matcher(jsUrl);
                        if (matcher3.find()) {
                            playerId = matcher3.group(1) + matcher3.group(2);
                        } else {
                            playerId = null;
                        }
                        String functionCode2 = null;
                        String functionName2 = null;
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("youtubecode", 0);
                        if (playerId != null) {
                            functionCode2 = preferences.getString(playerId, null);
                            functionName2 = preferences.getString(playerId + "n", null);
                        }
                        if (functionCode2 != null) {
                            functionName = functionName2;
                        } else {
                            if (jsUrl.startsWith("//")) {
                                jsUrl = "https:" + jsUrl;
                            } else if (jsUrl.startsWith("/")) {
                                jsUrl = "https://www.youtube.com" + jsUrl;
                            }
                            String jsCode = WebPlayerView.this.downloadUrlContent(this, jsUrl);
                            if (isCancelled()) {
                                return null;
                            }
                            if (jsCode != null) {
                                Matcher matcher4 = WebPlayerView.sigPattern.matcher(jsCode);
                                if (!matcher4.find()) {
                                    Matcher matcher5 = WebPlayerView.sigPattern2.matcher(jsCode);
                                    if (!matcher5.find()) {
                                        functionName = functionName2;
                                    } else {
                                        String functionName3 = matcher5.group(1);
                                        functionName = functionName3;
                                    }
                                } else {
                                    String functionName4 = matcher4.group(1);
                                    functionName = functionName4;
                                }
                                if (functionName != null) {
                                    try {
                                        JSExtractor extractor = new JSExtractor(jsCode);
                                        functionCode2 = extractor.extractFunction(functionName);
                                        if (!TextUtils.isEmpty(functionCode2) && playerId != null) {
                                            preferences.edit().putString(playerId, functionCode2).putString(playerId + "n", functionName).commit();
                                        }
                                    } catch (Exception e9) {
                                        FileLog.e(e9);
                                    }
                                }
                            } else {
                                functionName = functionName2;
                            }
                        }
                        if (!TextUtils.isEmpty(functionCode2)) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                functionCode = functionCode2 + functionName + "('" + this.sig.substring(3) + "');";
                            } else {
                                functionCode = functionCode2 + "window." + WebPlayerView.this.interfaceName + ".returnResultToJava(" + functionName + "('" + this.sig.substring(3) + "'));";
                            }
                            final String functionCodeFinal = functionCode;
                            try {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask$$ExternalSyntheticLambda1
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        WebPlayerView.YoutubeVideoTask.this.m3220x81fc3c6c(functionCodeFinal);
                                    }
                                });
                                this.countDownLatch.await();
                                encrypted2 = false;
                            } catch (Exception e10) {
                                FileLog.e(e10);
                            }
                        }
                    }
                }
                encrypted2 = true;
            }
            boolean encrypted6 = isCancelled();
            if (!encrypted6 && !encrypted2) {
                return this.result;
            }
            return null;
        }

        /* renamed from: lambda$doInBackground$1$org-telegram-ui-Components-WebPlayerView$YoutubeVideoTask */
        public /* synthetic */ void m3220x81fc3c6c(String functionCodeFinal) {
            if (Build.VERSION.SDK_INT >= 21) {
                WebPlayerView.this.webView.evaluateJavascript(functionCodeFinal, new ValueCallback() { // from class: org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask$$ExternalSyntheticLambda0
                    @Override // android.webkit.ValueCallback
                    public final void onReceiveValue(Object obj) {
                        WebPlayerView.YoutubeVideoTask.this.m3219x71466fab((String) obj);
                    }
                });
                return;
            }
            try {
                String javascript = "<script>" + functionCodeFinal + "</script>";
                byte[] data = javascript.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, 0);
                WebPlayerView.this.webView.loadUrl("data:text/html;charset=utf-8;base64," + base64);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        /* renamed from: lambda$doInBackground$0$org-telegram-ui-Components-WebPlayerView$YoutubeVideoTask */
        public /* synthetic */ void m3219x71466fab(String value) {
            String[] strArr = this.result;
            String str = strArr[0];
            String str2 = this.sig;
            strArr[0] = str.replace(str2, "/signature/" + value.substring(1, value.length() - 1));
            this.countDownLatch.countDown();
        }

        public void onInterfaceResult(String value) {
            String[] strArr = this.result;
            String str = strArr[0];
            String str2 = this.sig;
            strArr[0] = str.replace(str2, "/signature/" + value);
            this.countDownLatch.countDown();
        }

        public void onPostExecute(String[] result) {
            if (result[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start play youtube video " + result[1] + " " + result[0]);
                }
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result[0];
                WebPlayerView.this.playVideoType = result[1];
                if (WebPlayerView.this.playVideoType.equals(DownloadRequest.TYPE_HLS)) {
                    WebPlayerView.this.isStream = true;
                }
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String vid) {
            WebPlayerView.this = r1;
            this.videoId = vid;
        }

        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", this.videoId));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject json = new JSONObject(playerCode);
                JSONObject files = json.getJSONObject("request").getJSONObject("files");
                if (files.has(DownloadRequest.TYPE_HLS)) {
                    JSONObject hls = files.getJSONObject(DownloadRequest.TYPE_HLS);
                    try {
                        this.results[0] = hls.getString(ImagesContract.URL);
                    } catch (Exception e) {
                        String defaultCdn = hls.getString("default_cdn");
                        JSONObject cdns = hls.getJSONObject("cdns");
                        this.results[0] = cdns.getJSONObject(defaultCdn).getString(ImagesContract.URL);
                    }
                    this.results[1] = DownloadRequest.TYPE_HLS;
                } else if (files.has(DownloadRequest.TYPE_PROGRESSIVE)) {
                    this.results[1] = "other";
                    JSONArray progressive = files.getJSONArray(DownloadRequest.TYPE_PROGRESSIVE);
                    JSONObject format = progressive.getJSONObject(0);
                    this.results[0] = format.getString(ImagesContract.URL);
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (!isCancelled()) {
                return this.results[0];
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String vid) {
            WebPlayerView.this = r1;
            this.videoId = vid;
        }

        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", this.videoId));
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher filelist = WebPlayerView.aparatFileListPattern.matcher(playerCode);
                if (filelist.find()) {
                    String jsonCode = filelist.group(1);
                    JSONArray json = new JSONArray(jsonCode);
                    for (int a = 0; a < json.length(); a++) {
                        JSONArray array = json.getJSONArray(a);
                        if (array.length() != 0) {
                            JSONObject object = array.getJSONObject(0);
                            if (object.has("file")) {
                                this.results[0] = object.getString("file");
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                return this.results[0];
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class TwitchClipVideoTask extends AsyncTask<Void, Void, String> {
        private String currentUrl;
        private String videoId;
        private boolean canRetry = true;
        private String[] results = new String[2];

        public TwitchClipVideoTask(String url, String vid) {
            WebPlayerView.this = r1;
            this.videoId = vid;
            this.currentUrl = url;
        }

        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher filelist = WebPlayerView.twitchClipFilePattern.matcher(playerCode);
                if (filelist.find()) {
                    String jsonCode = filelist.group(1);
                    JSONObject json = new JSONObject(jsonCode);
                    JSONArray array = json.getJSONArray("quality_options");
                    JSONObject obj = array.getJSONObject(0);
                    this.results[0] = obj.getString(Constants.ScionAnalytics.PARAM_SOURCE);
                    this.results[1] = "other";
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                return this.results[0];
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class TwitchStreamVideoTask extends AsyncTask<Void, Void, String> {
        private String currentUrl;
        private String videoId;
        private boolean canRetry = true;
        private String[] results = new String[2];

        public TwitchStreamVideoTask(String url, String vid) {
            WebPlayerView.this = r1;
            this.videoId = vid;
            this.currentUrl = url;
        }

        public String doInBackground(Void... voids) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Client-ID", "jzkbprff40iqj646a697cyrvl0zt2m6");
            int idx = this.videoId.indexOf(38);
            if (idx > 0) {
                this.videoId = this.videoId.substring(0, idx);
            }
            String streamCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", this.videoId), headers, false);
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject obj = new JSONObject(streamCode);
                obj.getJSONObject("stream");
                String accessTokenCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", this.videoId), headers, false);
                JSONObject accessToken = new JSONObject(accessTokenCode);
                String sig = URLEncoder.encode(accessToken.getString("sig"), "UTF-8");
                String token = URLEncoder.encode(accessToken.getString("token"), "UTF-8");
                URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
                String params = "allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=" + ((int) (Math.random() * 1.0E7d)) + "&sig=" + sig + "&token=" + token;
                String m3uUrl = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", this.videoId, params);
                String[] strArr = this.results;
                strArr[0] = m3uUrl;
                strArr[1] = DownloadRequest.TYPE_HLS;
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                return this.results[0];
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String vid) {
            WebPlayerView.this = r1;
            this.videoId = vid;
        }

        private String decodeUrl(String input) {
            StringBuilder source = new StringBuilder(input);
            for (int a = 0; a < source.length(); a++) {
                char c = source.charAt(a);
                char lower = Character.toLowerCase(c);
                source.setCharAt(a, c == lower ? Character.toUpperCase(c) : lower);
            }
            try {
                return new String(Base64.decode(source.toString(), 0), "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }

        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", this.videoId));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject json = new JSONObject(playerCode).getJSONObject("file_versions").getJSONObject("mobile");
                String video = json.getString("video");
                String audio = json.getJSONArray("audio").getString(0);
                if (video != null && audio != null) {
                    String[] strArr = this.results;
                    strArr[0] = video;
                    strArr[1] = "other";
                    strArr[2] = audio;
                    strArr[3] = "other";
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!isCancelled()) {
                return this.results[0];
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (result != null) {
                WebPlayerView.this.initied = true;
                WebPlayerView.this.playVideoUrl = result;
                WebPlayerView.this.playVideoType = this.results[1];
                WebPlayerView.this.playAudioUrl = this.results[2];
                WebPlayerView.this.playAudioType = this.results[3];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class ControlsView extends FrameLayout {
        private int bufferedPosition;
        private AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private int lastProgressX;
        private int progress;
        private Paint progressBufferedPaint;
        private Paint progressInnerPaint;
        private StaticLayout progressLayout;
        private Paint progressPaint;
        private boolean progressPressed;
        private TextPaint textPaint;
        private boolean isVisible = true;
        private Runnable hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.WebPlayerView$ControlsView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WebPlayerView.ControlsView.this.m3218x5bbf40d1();
            }
        };
        private ImageReceiver imageReceiver = new ImageReceiver(this);

        /* renamed from: lambda$new$0$org-telegram-ui-Components-WebPlayerView$ControlsView */
        public /* synthetic */ void m3218x5bbf40d1() {
            show(false, true);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ControlsView(Context context) {
            super(context);
            WebPlayerView.this = r4;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setColor(-1);
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            Paint paint = new Paint(1);
            this.progressPaint = paint;
            paint.setColor(-15095832);
            Paint paint2 = new Paint();
            this.progressInnerPaint = paint2;
            paint2.setColor(-6975081);
            Paint paint3 = new Paint(1);
            this.progressBufferedPaint = paint3;
            paint3.setColor(-1);
        }

        public void setDuration(int value) {
            if (this.duration == value || value < 0 || WebPlayerView.this.isStream) {
                return;
            }
            this.duration = value;
            StaticLayout staticLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.duration), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.durationLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.durationWidth = (int) Math.ceil(this.durationLayout.getLineWidth(0));
            }
            invalidate();
        }

        public void setBufferedProgress(int position) {
            this.bufferedPosition = position;
            invalidate();
        }

        public void setProgress(int value) {
            if (this.progressPressed || value < 0 || WebPlayerView.this.isStream) {
                return;
            }
            this.progress = value;
            this.progressLayout = new StaticLayout(AndroidUtilities.formatShortDuration(this.progress), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            invalidate();
        }

        public void show(boolean value, boolean animated) {
            if (this.isVisible == value) {
                return;
            }
            this.isVisible = value;
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (this.isVisible) {
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f));
                    this.currentAnimation.setDuration(150L);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.WebPlayerView.ControlsView.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            ControlsView.this.currentAnimation = null;
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    setAlpha(1.0f);
                }
            } else if (animated) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.currentAnimation = animatorSet3;
                animatorSet3.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f));
                this.currentAnimation.setDuration(150L);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.WebPlayerView.ControlsView.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        ControlsView.this.currentAnimation = null;
                    }
                });
                this.currentAnimation.start();
            } else {
                setAlpha(0.0f);
            }
            checkNeedHide();
        }

        public void checkNeedHide() {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            if (this.isVisible && WebPlayerView.this.videoPlayer.isPlaying()) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
            }
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() == 0) {
                if (!this.isVisible) {
                    show(true, true);
                    return true;
                }
                onTouchEvent(ev);
                return this.progressPressed;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            checkNeedHide();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            int progressY;
            int progressLineEndX;
            int progressLineX;
            if (WebPlayerView.this.inFullscreen) {
                progressLineX = AndroidUtilities.dp(36.0f) + this.durationWidth;
                progressLineEndX = (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                progressY = getMeasuredHeight() - AndroidUtilities.dp(28.0f);
            } else {
                progressLineX = 0;
                progressLineEndX = getMeasuredWidth();
                progressY = getMeasuredHeight() - AndroidUtilities.dp(12.0f);
            }
            int i = this.duration;
            int progressX = (i != 0 ? (int) ((progressLineEndX - progressLineX) * (this.progress / i)) : 0) + progressLineX;
            if (event.getAction() == 0) {
                if (this.isVisible && !WebPlayerView.this.isInline && !WebPlayerView.this.isStream) {
                    if (this.duration != 0) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        if (x >= progressX - AndroidUtilities.dp(10.0f) && x <= AndroidUtilities.dp(10.0f) + progressX && y >= progressY - AndroidUtilities.dp(10.0f) && y <= AndroidUtilities.dp(10.0f) + progressY) {
                            this.progressPressed = true;
                            this.lastProgressX = x;
                            this.currentProgressX = progressX;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            invalidate();
                        }
                    }
                } else {
                    show(true, true);
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (WebPlayerView.this.initied && WebPlayerView.this.videoPlayer.isPlaying()) {
                    AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
                }
                if (this.progressPressed) {
                    this.progressPressed = false;
                    if (WebPlayerView.this.initied) {
                        this.progress = (int) (this.duration * ((this.currentProgressX - progressLineX) / (progressLineEndX - progressLineX)));
                        WebPlayerView.this.videoPlayer.seekTo(this.progress * 1000);
                    }
                }
            } else if (event.getAction() == 2 && this.progressPressed) {
                int x2 = (int) event.getX();
                int i2 = this.currentProgressX - (this.lastProgressX - x2);
                this.currentProgressX = i2;
                this.lastProgressX = x2;
                if (i2 < progressLineX) {
                    this.currentProgressX = progressLineX;
                } else if (i2 > progressLineEndX) {
                    this.currentProgressX = progressLineEndX;
                }
                setProgress((int) (this.duration * 1000 * ((this.currentProgressX - progressLineX) / (progressLineEndX - progressLineX))));
                invalidate();
            }
            super.onTouchEvent(event);
            return true;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int cy;
            int progressLineEndX;
            int progressLineX;
            int progressLineY;
            int progressX;
            int progressX2;
            int i;
            if (WebPlayerView.this.drawImage) {
                if (WebPlayerView.this.firstFrameRendered && WebPlayerView.this.currentAlpha != 0.0f) {
                    long newTime = System.currentTimeMillis();
                    long dt = newTime - WebPlayerView.this.lastUpdateTime;
                    WebPlayerView.this.lastUpdateTime = newTime;
                    WebPlayerView.access$4724(WebPlayerView.this, ((float) dt) / 150.0f);
                    if (WebPlayerView.this.currentAlpha < 0.0f) {
                        WebPlayerView.this.currentAlpha = 0.0f;
                    }
                    invalidate();
                }
                this.imageReceiver.setAlpha(WebPlayerView.this.currentAlpha);
                this.imageReceiver.draw(canvas);
            }
            if (WebPlayerView.this.videoPlayer.isPlayerPrepared() && !WebPlayerView.this.isStream) {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                if (!WebPlayerView.this.isInline) {
                    int i2 = 6;
                    if (this.durationLayout != null) {
                        canvas.save();
                        canvas.translate((width - AndroidUtilities.dp(58.0f)) - this.durationWidth, height - AndroidUtilities.dp((WebPlayerView.this.inFullscreen ? 6 : 10) + 29));
                        this.durationLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.progressLayout != null) {
                        canvas.save();
                        float dp = AndroidUtilities.dp(18.0f);
                        if (!WebPlayerView.this.inFullscreen) {
                            i2 = 10;
                        }
                        canvas.translate(dp, height - AndroidUtilities.dp(i2 + 29));
                        this.progressLayout.draw(canvas);
                        canvas.restore();
                    }
                }
                if (this.duration != 0) {
                    if (!WebPlayerView.this.isInline) {
                        if (WebPlayerView.this.inFullscreen) {
                            int progressLineX2 = AndroidUtilities.dp(36.0f) + this.durationWidth;
                            int progressLineEndX2 = (width - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                            progressLineY = height - AndroidUtilities.dp(29.0f);
                            progressLineX = progressLineX2;
                            progressLineEndX = progressLineEndX2;
                            cy = height - AndroidUtilities.dp(28.0f);
                        } else {
                            progressLineY = height - AndroidUtilities.dp(13.0f);
                            progressLineX = 0;
                            progressLineEndX = width;
                            cy = height - AndroidUtilities.dp(12.0f);
                        }
                    } else {
                        progressLineY = height - AndroidUtilities.dp(3.0f);
                        progressLineX = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(7.0f);
                    }
                    if (WebPlayerView.this.inFullscreen) {
                        canvas.drawRect(progressLineX, progressLineY, progressLineEndX, AndroidUtilities.dp(3.0f) + progressLineY, this.progressInnerPaint);
                    }
                    if (!this.progressPressed) {
                        progressX = ((int) ((progressLineEndX - progressLineX) * (this.progress / this.duration))) + progressLineX;
                    } else {
                        progressX = this.currentProgressX;
                    }
                    int i3 = this.bufferedPosition;
                    if (i3 != 0 && (i = this.duration) != 0) {
                        progressX2 = progressX;
                        canvas.drawRect(progressLineX, progressLineY, progressLineX + ((progressLineEndX - progressLineX) * (i3 / i)), AndroidUtilities.dp(3.0f) + progressLineY, WebPlayerView.this.inFullscreen ? this.progressBufferedPaint : this.progressInnerPaint);
                    } else {
                        progressX2 = progressX;
                    }
                    canvas.drawRect(progressLineX, progressLineY, progressX2, AndroidUtilities.dp(3.0f) + progressLineY, this.progressPaint);
                    if (!WebPlayerView.this.isInline) {
                        canvas.drawCircle(progressX2, cy, AndroidUtilities.dp(this.progressPressed ? 7.0f : 5.0f), this.progressPaint);
                    }
                }
            }
        }
    }

    public WebPlayerView(Context context, boolean allowInline, boolean allowShare, WebPlayerViewDelegate webPlayerViewDelegate) {
        super(context);
        int i = lastContainerId;
        lastContainerId = i + 1;
        this.fragment_container_id = i;
        this.allowInlineAnimation = Build.VERSION.SDK_INT >= 21;
        this.backgroundPaint = new Paint();
        this.progressRunnable = new Runnable() { // from class: org.telegram.ui.Components.WebPlayerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (WebPlayerView.this.videoPlayer != null && WebPlayerView.this.videoPlayer.isPlaying()) {
                    WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                    WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000));
                    AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000L);
                }
            }
        };
        this.surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: org.telegram.ui.Components.WebPlayerView.2
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (WebPlayerView.this.changingTextureView) {
                    if (WebPlayerView.this.switchingInlineMode) {
                        WebPlayerView.this.waitingForFirstTextureUpload = 2;
                    }
                    WebPlayerView.this.textureView.setSurfaceTexture(surface);
                    WebPlayerView.this.textureView.setVisibility(0);
                    WebPlayerView.this.changingTextureView = false;
                    return false;
                }
                return true;
            }

            /* renamed from: org.telegram.ui.Components.WebPlayerView$2$1 */
            /* loaded from: classes5.dex */
            public class AnonymousClass1 implements ViewTreeObserver.OnPreDrawListener {
                AnonymousClass1() {
                    AnonymousClass2.this = this$1;
                }

                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (WebPlayerView.this.textureImageView != null) {
                        WebPlayerView.this.textureImageView.setVisibility(4);
                        WebPlayerView.this.textureImageView.setImageDrawable(null);
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            WebPlayerView.this.currentBitmap = null;
                        }
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.WebPlayerView$2$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WebPlayerView.AnonymousClass2.AnonymousClass1.this.m3217lambda$onPreDraw$0$orgtelegramuiComponentsWebPlayerView$2$1();
                        }
                    });
                    WebPlayerView.this.waitingForFirstTextureUpload = 0;
                    return true;
                }

                /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Components-WebPlayerView$2$1 */
                public /* synthetic */ void m3217lambda$onPreDraw$0$orgtelegramuiComponentsWebPlayerView$2$1() {
                    WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                }
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                if (WebPlayerView.this.waitingForFirstTextureUpload == 1) {
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new AnonymousClass1());
                    WebPlayerView.this.changedTextureView.invalidate();
                }
            }
        };
        this.switchToInlineRunnable = new Runnable() { // from class: org.telegram.ui.Components.WebPlayerView.3
            @Override // java.lang.Runnable
            public void run() {
                WebPlayerView.this.switchingInlineMode = false;
                if (WebPlayerView.this.currentBitmap != null) {
                    WebPlayerView.this.currentBitmap.recycle();
                    WebPlayerView.this.currentBitmap = null;
                }
                WebPlayerView.this.changingTextureView = true;
                if (WebPlayerView.this.textureImageView != null) {
                    try {
                        WebPlayerView webPlayerView = WebPlayerView.this;
                        webPlayerView.currentBitmap = Bitmaps.createBitmap(webPlayerView.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                        WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
                    } catch (Throwable e) {
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            WebPlayerView.this.currentBitmap = null;
                        }
                        FileLog.e(e);
                    }
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.textureImageView.setVisibility(0);
                        WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
                    } else {
                        WebPlayerView.this.textureImageView.setImageDrawable(null);
                    }
                }
                WebPlayerView.this.isInline = true;
                WebPlayerView.this.updatePlayButton();
                WebPlayerView.this.updateShareButton();
                WebPlayerView.this.updateFullscreenButton();
                WebPlayerView.this.updateInlineButton();
                ViewGroup viewGroup = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(WebPlayerView.this.controlsView);
                }
                WebPlayerView webPlayerView2 = WebPlayerView.this;
                webPlayerView2.changedTextureView = webPlayerView2.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.videoWidth, WebPlayerView.this.videoHeight, WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation);
                WebPlayerView.this.changedTextureView.setVisibility(4);
                ViewGroup parent = (ViewGroup) WebPlayerView.this.textureView.getParent();
                if (parent != null) {
                    parent.removeView(WebPlayerView.this.textureView);
                }
                WebPlayerView.this.controlsView.show(false, false);
            }
        };
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(-16777216);
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(context) { // from class: org.telegram.ui.Components.WebPlayerView.4
            @Override // com.google.android.exoplayer2.ui.AspectRatioFrameLayout, android.widget.FrameLayout, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (WebPlayerView.this.textureViewContainer != null) {
                    ViewGroup.LayoutParams layoutParams = WebPlayerView.this.textureView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                    if (WebPlayerView.this.textureImageView != null) {
                        ViewGroup.LayoutParams layoutParams2 = WebPlayerView.this.textureImageView.getLayoutParams();
                        layoutParams2.width = getMeasuredWidth();
                        layoutParams2.height = getMeasuredHeight();
                    }
                }
            }
        };
        this.aspectRatioFrameLayout = aspectRatioFrameLayout;
        addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        WebView webView = new WebView(context);
        this.webView = webView;
        webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() { // from class: org.telegram.ui.Components.WebPlayerView$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.WebPlayerView.CallJavaResultInterface
            public final void jsCallFinished(String str) {
                WebPlayerView.this.m3211lambda$new$0$orgtelegramuiComponentsWebPlayerView(str);
            }
        }), this.interfaceName);
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        TextureView textureView = new TextureView(context);
        this.textureView = textureView;
        textureView.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup == null) {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        } else {
            viewGroup.addView(this.textureView);
        }
        if (this.allowInlineAnimation && this.textureViewContainer != null) {
            ImageView imageView = new ImageView(context);
            this.textureImageView = imageView;
            imageView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
            this.textureImageView.setPivotX(0.0f);
            this.textureImageView.setPivotY(0.0f);
            this.textureImageView.setVisibility(4);
            this.textureViewContainer.addView(this.textureImageView);
        }
        VideoPlayer videoPlayer = new VideoPlayer();
        this.videoPlayer = videoPlayer;
        videoPlayer.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        ControlsView controlsView = new ControlsView(context);
        this.controlsView = controlsView;
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 == null) {
            addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
        } else {
            viewGroup2.addView(controlsView);
        }
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        ImageView imageView2 = new ImageView(context);
        this.fullscreenButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.WebPlayerView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WebPlayerView.this.m3212lambda$new$1$orgtelegramuiComponentsWebPlayerView(view);
            }
        });
        ImageView imageView3 = new ImageView(context);
        this.playButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.WebPlayerView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WebPlayerView.this.m3213lambda$new$2$orgtelegramuiComponentsWebPlayerView(view);
            }
        });
        if (allowInline) {
            ImageView imageView4 = new ImageView(context);
            this.inlineButton = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.WebPlayerView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    WebPlayerView.this.m3214lambda$new$3$orgtelegramuiComponentsWebPlayerView(view);
                }
            });
        }
        if (allowShare) {
            ImageView imageView5 = new ImageView(context);
            this.shareButton = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setImageResource(R.drawable.ic_share_video);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.WebPlayerView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    WebPlayerView.this.m3215lambda$new$4$orgtelegramuiComponentsWebPlayerView(view);
                }
            });
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-WebPlayerView */
    public /* synthetic */ void m3211lambda$new$0$orgtelegramuiComponentsWebPlayerView(String value) {
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null && !asyncTask.isCancelled()) {
            AsyncTask asyncTask2 = this.currentTask;
            if (asyncTask2 instanceof YoutubeVideoTask) {
                ((YoutubeVideoTask) asyncTask2).onInterfaceResult(value);
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-WebPlayerView */
    public /* synthetic */ void m3212lambda$new$1$orgtelegramuiComponentsWebPlayerView(View v) {
        if (!this.initied || this.changingTextureView || this.switchingInlineMode || !this.firstFrameRendered) {
            return;
        }
        this.inFullscreen = !this.inFullscreen;
        updateFullscreenState(true);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-WebPlayerView */
    public /* synthetic */ void m3213lambda$new$2$orgtelegramuiComponentsWebPlayerView(View v) {
        if (!this.initied || this.playVideoUrl == null) {
            return;
        }
        if (!this.videoPlayer.isPlayerPrepared()) {
            preparePlayer();
        }
        if (this.videoPlayer.isPlaying()) {
            this.videoPlayer.pause();
        } else {
            this.isCompleted = false;
            this.videoPlayer.play();
        }
        updatePlayButton();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-WebPlayerView */
    public /* synthetic */ void m3214lambda$new$3$orgtelegramuiComponentsWebPlayerView(View v) {
        if (this.textureView == null || !this.delegate.checkInlinePermissions() || this.changingTextureView || this.switchingInlineMode || !this.firstFrameRendered) {
            return;
        }
        this.switchingInlineMode = true;
        if (!this.isInline) {
            this.inFullscreen = false;
            this.delegate.prepareToSwitchInlineMode(true, this.switchToInlineRunnable, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
            return;
        }
        ViewGroup parent = (ViewGroup) this.aspectRatioFrameLayout.getParent();
        if (parent != this) {
            if (parent != null) {
                parent.removeView(this.aspectRatioFrameLayout);
            }
            addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
            this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - AndroidUtilities.dp(10.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        Bitmap bitmap = this.currentBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.currentBitmap = null;
        }
        this.changingTextureView = true;
        this.isInline = false;
        updatePlayButton();
        updateShareButton();
        updateFullscreenButton();
        updateInlineButton();
        this.textureView.setVisibility(4);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView);
        }
        ViewGroup parent2 = (ViewGroup) this.controlsView.getParent();
        if (parent2 != this) {
            if (parent2 != null) {
                parent2.removeView(this.controlsView);
            }
            ViewGroup viewGroup2 = this.textureViewContainer;
            if (viewGroup2 == null) {
                addView(this.controlsView, 1);
            } else {
                viewGroup2.addView(this.controlsView);
            }
        }
        this.controlsView.show(false, false);
        this.delegate.prepareToSwitchInlineMode(false, null, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-WebPlayerView */
    public /* synthetic */ void m3215lambda$new$4$orgtelegramuiComponentsWebPlayerView(View v) {
        WebPlayerViewDelegate webPlayerViewDelegate = this.delegate;
        if (webPlayerViewDelegate != null) {
            webPlayerViewDelegate.onSharePressed();
        }
    }

    public void onInitFailed() {
        if (this.controlsView.getParent() != this) {
            this.controlsView.setVisibility(8);
        }
        this.delegate.onInitFailed();
    }

    public void updateTextureImageView() {
        if (this.textureImageView == null) {
            return;
        }
        try {
            Bitmap createBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
            this.currentBitmap = createBitmap;
            this.changedTextureView.getBitmap(createBitmap);
        } catch (Throwable e) {
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            FileLog.e(e);
        }
        if (this.currentBitmap == null) {
            this.textureImageView.setImageDrawable(null);
            return;
        }
        this.textureImageView.setVisibility(0);
        this.textureImageView.setImageBitmap(this.currentBitmap);
    }

    public String getYoutubeId() {
        return this.currentYoutubeId;
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState != 2) {
            if (this.videoPlayer.getDuration() == C.TIME_UNSET) {
                this.controlsView.setDuration(0);
            } else {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            }
        }
        if (playbackState == 4 || playbackState == 1 || !this.videoPlayer.isPlaying()) {
            this.delegate.onPlayStateChanged(this, false);
        } else {
            this.delegate.onPlayStateChanged(this, true);
        }
        if (this.videoPlayer.isPlaying() && playbackState != 4) {
            updatePlayButton();
        } else if (playbackState == 4) {
            this.isCompleted = true;
            this.videoPlayer.pause();
            this.videoPlayer.seekTo(0L);
            updatePlayButton();
            this.controlsView.show(true, true);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f), this.backgroundPaint);
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public void onError(VideoPlayer player, Exception e) {
        FileLog.e(e);
        onInitFailed();
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                width = height;
                height = width;
            }
            this.videoWidth = (int) (width * pixelWidthHeightRatio);
            this.videoHeight = height;
            float ratio = height == 0 ? 1.0f : (width * pixelWidthHeightRatio) / height;
            aspectRatioFrameLayout.setAspectRatio(ratio, unappliedRotationDegrees);
            if (this.inFullscreen) {
                this.delegate.onVideoSizeChanged(ratio, unappliedRotationDegrees);
            }
        }
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public void onRenderedFirstFrame() {
        this.firstFrameRendered = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.controlsView.invalidate();
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        if (this.changingTextureView) {
            this.changingTextureView = false;
            if (this.inFullscreen || this.isInline) {
                if (this.isInline) {
                    this.waitingForFirstTextureUpload = 1;
                }
                this.changedTextureView.setSurfaceTexture(surfaceTexture);
                this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
                this.changedTextureView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (this.waitingForFirstTextureUpload == 2) {
            ImageView imageView = this.textureImageView;
            if (imageView != null) {
                imageView.setVisibility(4);
                this.textureImageView.setImageDrawable(null);
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
            }
            this.switchingInlineMode = false;
            this.delegate.onSwitchInlineMode(this.controlsView, false, this.videoWidth, this.videoHeight, this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
            this.waitingForFirstTextureUpload = 0;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = ((r - l) - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int y = (((b - t) - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        aspectRatioFrameLayout.layout(x, y, aspectRatioFrameLayout.getMeasuredWidth() + x, this.aspectRatioFrameLayout.getMeasuredHeight() + y);
        if (this.controlsView.getParent() == this) {
            ControlsView controlsView = this.controlsView;
            controlsView.layout(0, 0, controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        int x2 = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        int y2 = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        RadialProgressView radialProgressView = this.progressView;
        radialProgressView.layout(x2, y2, radialProgressView.getMeasuredWidth() + x2, this.progressView.getMeasuredHeight() + y2);
        this.controlsView.imageReceiver.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height - AndroidUtilities.dp(10.0f), C.BUFFER_FLAG_ENCRYPTED));
        if (this.controlsView.getParent() == this) {
            this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        }
        this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), C.BUFFER_FLAG_ENCRYPTED));
        setMeasuredDimension(width, height);
    }

    public void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (!this.videoPlayer.isPlaying()) {
            if (this.isCompleted) {
                this.playButton.setImageResource(this.isInline ? R.drawable.ic_againinline : R.drawable.ic_again);
                return;
            } else {
                this.playButton.setImageResource(this.isInline ? R.drawable.ic_playinline : R.drawable.ic_play);
                return;
            }
        }
        this.playButton.setImageResource(this.isInline ? R.drawable.ic_pauseinline : R.drawable.ic_pause);
        AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
        checkAudioFocus();
    }

    private void checkAudioFocus() {
        if (!this.hasAudioFocus) {
            AudioManager audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
            this.hasAudioFocus = true;
            if (audioManager.requestAudioFocus(this, 3, 1) == 1) {
                this.audioFocus = 2;
            }
        }
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(final int focusChange) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.WebPlayerView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                WebPlayerView.this.m3216xfb493dbe(focusChange);
            }
        });
    }

    /* renamed from: lambda$onAudioFocusChange$5$org-telegram-ui-Components-WebPlayerView */
    public /* synthetic */ void m3216xfb493dbe(int focusChange) {
        if (focusChange == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
            this.audioFocus = 0;
        } else if (focusChange == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (focusChange == -3) {
            this.audioFocus = 1;
        } else if (focusChange == -2) {
            this.audioFocus = 0;
            if (this.videoPlayer.isPlaying()) {
                this.resumeAudioOnFocusGain = true;
                this.videoPlayer.pause();
                updatePlayButton();
            }
        }
    }

    public void updateFullscreenButton() {
        if (!this.videoPlayer.isPlayerPrepared() || this.isInline) {
            this.fullscreenButton.setVisibility(8);
            return;
        }
        this.fullscreenButton.setVisibility(0);
        if (!this.inFullscreen) {
            this.fullscreenButton.setImageResource(R.drawable.ic_gofullscreen);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
            return;
        }
        this.fullscreenButton.setImageResource(R.drawable.ic_outfullscreen);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
    }

    public void updateShareButton() {
        ImageView imageView = this.shareButton;
        if (imageView == null) {
            return;
        }
        imageView.setVisibility((this.isInline || !this.videoPlayer.isPlayerPrepared()) ? 8 : 0);
    }

    private View getControlView() {
        return this.controlsView;
    }

    private View getProgressView() {
        return this.progressView;
    }

    public void updateInlineButton() {
        ImageView imageView = this.inlineButton;
        if (imageView == null) {
            return;
        }
        imageView.setImageResource(this.isInline ? R.drawable.ic_goinline : R.drawable.ic_outinline);
        this.inlineButton.setVisibility(this.videoPlayer.isPlayerPrepared() ? 0 : 8);
        if (this.isInline) {
            this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
        } else {
            this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
        }
    }

    public void preparePlayer() {
        String str = this.playVideoUrl;
        if (str == null) {
            return;
        }
        if (str != null && this.playAudioUrl != null) {
            this.videoPlayer.preparePlayerLoop(Uri.parse(str), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
        } else {
            this.videoPlayer.preparePlayer(Uri.parse(str), this.playVideoType);
        }
        this.videoPlayer.setPlayWhenReady(this.isAutoplay);
        this.isLoading = false;
        if (this.videoPlayer.getDuration() == C.TIME_UNSET) {
            this.controlsView.setDuration(0);
        } else {
            this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
        }
        updateFullscreenButton();
        updateShareButton();
        updateInlineButton();
        this.controlsView.invalidate();
        int i = this.seekToTime;
        if (i != -1) {
            this.videoPlayer.seekTo(i * 1000);
        }
    }

    public void pause() {
        this.videoPlayer.pause();
        updatePlayButton();
        this.controlsView.show(true, true);
    }

    private void updateFullscreenState(boolean byButton) {
        ViewGroup parent;
        if (this.textureView == null) {
            return;
        }
        updateFullscreenButton();
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup == null) {
            this.changingTextureView = true;
            if (!this.inFullscreen) {
                if (viewGroup != null) {
                    viewGroup.addView(this.textureView);
                } else {
                    this.aspectRatioFrameLayout.addView(this.textureView);
                }
            }
            if (this.inFullscreen) {
                ViewGroup viewGroup2 = (ViewGroup) this.controlsView.getParent();
                if (viewGroup2 != null) {
                    viewGroup2.removeView(this.controlsView);
                }
            } else {
                ViewGroup parent2 = (ViewGroup) this.controlsView.getParent();
                if (parent2 != this) {
                    if (parent2 != null) {
                        parent2.removeView(this.controlsView);
                    }
                    ViewGroup viewGroup3 = this.textureViewContainer;
                    if (viewGroup3 == null) {
                        addView(this.controlsView, 1);
                    } else {
                        viewGroup3.addView(this.controlsView);
                    }
                }
            }
            TextureView onSwitchToFullscreen = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
            this.changedTextureView = onSwitchToFullscreen;
            onSwitchToFullscreen.setVisibility(4);
            if (this.inFullscreen && this.changedTextureView != null && (parent = (ViewGroup) this.textureView.getParent()) != null) {
                parent.removeView(this.textureView);
            }
            this.controlsView.checkNeedHide();
            return;
        }
        if (this.inFullscreen) {
            ViewGroup viewGroup4 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
            if (viewGroup4 != null) {
                viewGroup4.removeView(this.aspectRatioFrameLayout);
            }
        } else {
            ViewGroup parent3 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
            if (parent3 != this) {
                if (parent3 != null) {
                    parent3.removeView(this.aspectRatioFrameLayout);
                }
                addView(this.aspectRatioFrameLayout, 0);
            }
        }
        this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
    }

    public void exitFullscreen() {
        if (!this.inFullscreen) {
            return;
        }
        this.inFullscreen = false;
        updateInlineButton();
        updateFullscreenState(false);
    }

    public boolean isInitied() {
        return this.initied;
    }

    public boolean isInline() {
        return this.isInline || this.switchingInlineMode;
    }

    public void enterFullscreen() {
        if (this.inFullscreen) {
            return;
        }
        this.inFullscreen = true;
        updateInlineButton();
        updateFullscreenState(false);
    }

    public boolean isInFullscreen() {
        return this.inFullscreen;
    }

    public static String getYouTubeVideoId(String url) {
        Matcher matcher = youtubeIdRegex.matcher(url);
        if (!matcher.find()) {
            return null;
        }
        String id = matcher.group(1);
        return id;
    }

    public boolean canHandleUrl(String url) {
        if (url != null) {
            if (url.endsWith(".mp4")) {
                return true;
            }
            try {
                Matcher matcher = youtubeIdRegex.matcher(url);
                String id = null;
                if (matcher.find()) {
                    id = matcher.group(1);
                }
                if (id != null) {
                    return true;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                Matcher matcher2 = vimeoIdRegex.matcher(url);
                String id2 = null;
                if (matcher2.find()) {
                    id2 = matcher2.group(3);
                }
                if (id2 != null) {
                    return true;
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            try {
                Matcher matcher3 = aparatIdRegex.matcher(url);
                String id3 = null;
                if (matcher3.find()) {
                    id3 = matcher3.group(1);
                }
                if (id3 != null) {
                    return true;
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
            try {
                Matcher matcher4 = twitchClipIdRegex.matcher(url);
                String id4 = null;
                if (matcher4.find()) {
                    id4 = matcher4.group(1);
                }
                if (id4 != null) {
                    return true;
                }
            } catch (Exception e4) {
                FileLog.e(e4);
            }
            try {
                Matcher matcher5 = twitchStreamIdRegex.matcher(url);
                String id5 = null;
                if (matcher5.find()) {
                    id5 = matcher5.group(1);
                }
                if (id5 != null) {
                    return true;
                }
            } catch (Exception e5) {
                FileLog.e(e5);
            }
            try {
                Matcher matcher6 = coubIdRegex.matcher(url);
                String id6 = null;
                if (matcher6.find()) {
                    id6 = matcher6.group(1);
                }
                return id6 != null;
            } catch (Exception e6) {
                FileLog.e(e6);
                return false;
            }
        }
        return false;
    }

    public void willHandle() {
        this.controlsView.setVisibility(4);
        this.controlsView.show(false, false);
        showProgress(true, false);
    }

    public boolean loadVideo(String url, TLRPC.Photo thumb, Object parentObject, String originalUrl, boolean autoplay) {
        boolean z;
        String youtubeId = null;
        String vimeoId = null;
        String coubId = getCoubId(url);
        if (coubId == null) {
            coubId = getCoubId(originalUrl);
        }
        String twitchClipId = null;
        String twitchStreamId = null;
        String mp4File = null;
        String aparatId = null;
        this.seekToTime = -1;
        if (coubId == null && url != null) {
            if (url.endsWith(".mp4")) {
                mp4File = url;
            } else {
                try {
                    if (originalUrl != null) {
                        try {
                            Uri uri = Uri.parse(originalUrl);
                            String t = uri.getQueryParameter(Theme.THEME_BACKGROUND_SLUG);
                            if (t == null) {
                                t = uri.getQueryParameter("time_continue");
                            }
                            if (t != null) {
                                if (!t.contains("m")) {
                                    this.seekToTime = Utilities.parseInt((CharSequence) t).intValue();
                                } else {
                                    String[] args = t.split("m");
                                    this.seekToTime = (Utilities.parseInt((CharSequence) args[0]).intValue() * 60) + Utilities.parseInt((CharSequence) args[1]).intValue();
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    Matcher matcher = youtubeIdRegex.matcher(url);
                    String id = null;
                    if (matcher.find()) {
                        id = matcher.group(1);
                    }
                    if (id != null) {
                        youtubeId = id;
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                if (youtubeId == null) {
                    try {
                        Matcher matcher2 = vimeoIdRegex.matcher(url);
                        String id2 = null;
                        if (matcher2.find()) {
                            id2 = matcher2.group(3);
                        }
                        if (id2 != null) {
                            vimeoId = id2;
                        }
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                if (vimeoId == null) {
                    try {
                        Matcher matcher3 = aparatIdRegex.matcher(url);
                        String id3 = null;
                        if (matcher3.find()) {
                            id3 = matcher3.group(1);
                        }
                        if (id3 != null) {
                            aparatId = id3;
                        }
                    } catch (Exception e4) {
                        FileLog.e(e4);
                    }
                }
                if (aparatId == null) {
                    try {
                        Matcher matcher4 = twitchClipIdRegex.matcher(url);
                        String id4 = null;
                        if (matcher4.find()) {
                            id4 = matcher4.group(1);
                        }
                        if (id4 != null) {
                            twitchClipId = id4;
                        }
                    } catch (Exception e5) {
                        FileLog.e(e5);
                    }
                }
                if (twitchClipId == null) {
                    try {
                        Matcher matcher5 = twitchStreamIdRegex.matcher(url);
                        String id5 = null;
                        if (matcher5.find()) {
                            id5 = matcher5.group(1);
                        }
                        if (id5 != null) {
                            twitchStreamId = id5;
                        }
                    } catch (Exception e6) {
                        FileLog.e(e6);
                    }
                }
                if (twitchStreamId == null) {
                    try {
                        Matcher matcher6 = coubIdRegex.matcher(url);
                        String id6 = null;
                        if (matcher6.find()) {
                            id6 = matcher6.group(1);
                        }
                        if (id6 != null) {
                            coubId = id6;
                        }
                    } catch (Exception e7) {
                        FileLog.e(e7);
                    }
                }
            }
        }
        this.initied = false;
        this.isCompleted = false;
        this.isAutoplay = autoplay;
        this.playVideoUrl = null;
        this.playAudioUrl = null;
        destroy();
        this.firstFrameRendered = false;
        this.currentAlpha = 1.0f;
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        updateFullscreenButton();
        updateShareButton();
        updateInlineButton();
        updatePlayButton();
        if (thumb == null) {
            this.drawImage = false;
        } else {
            TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(thumb.sizes, 80, true);
            if (photoSize != null) {
                this.controlsView.imageReceiver.setImage(null, null, ImageLocation.getForPhoto(photoSize, thumb), "80_80_b", 0L, null, parentObject, 1);
                this.drawImage = true;
            }
        }
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.progressAnimation = null;
        }
        this.isLoading = true;
        this.controlsView.setProgress(0);
        if (youtubeId != null) {
            this.currentYoutubeId = youtubeId;
            youtubeId = null;
        }
        if (mp4File != null) {
            this.initied = true;
            this.playVideoUrl = mp4File;
            this.playVideoType = "other";
            if (this.isAutoplay) {
                preparePlayer();
            }
            showProgress(false, false);
            this.controlsView.show(true, true);
        } else {
            if (youtubeId != null) {
                YoutubeVideoTask task = new YoutubeVideoTask(youtubeId);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentTask = task;
                z = true;
            } else if (vimeoId != null) {
                VimeoVideoTask task2 = new VimeoVideoTask(vimeoId);
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentTask = task2;
                z = true;
            } else if (coubId != null) {
                CoubVideoTask task3 = new CoubVideoTask(coubId);
                task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentTask = task3;
                this.isStream = true;
                z = true;
            } else if (aparatId != null) {
                AparatVideoTask task4 = new AparatVideoTask(aparatId);
                task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentTask = task4;
                z = true;
            } else if (twitchClipId != null) {
                TwitchClipVideoTask task5 = new TwitchClipVideoTask(url, twitchClipId);
                task5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentTask = task5;
                z = true;
            } else if (twitchStreamId != null) {
                TwitchStreamVideoTask task6 = new TwitchStreamVideoTask(url, twitchStreamId);
                z = true;
                task6.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentTask = task6;
                this.isStream = true;
            } else {
                z = true;
            }
            this.controlsView.show(false, false);
            showProgress(z, false);
        }
        if (youtubeId != null || vimeoId != null || coubId != null || aparatId != null || mp4File != null || twitchClipId != null || twitchStreamId != null) {
            this.controlsView.setVisibility(0);
            return true;
        }
        this.controlsView.setVisibility(8);
        return false;
    }

    public String getCoubId(String url) {
        String id;
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            Matcher matcher = coubIdRegex.matcher(url);
            id = null;
            if (matcher.find()) {
                id = matcher.group(1);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (id == null) {
            return null;
        }
        return id;
    }

    public View getAspectRatioView() {
        return this.aspectRatioFrameLayout;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public ImageView getTextureImageView() {
        return this.textureImageView;
    }

    public View getControlsView() {
        return this.controlsView;
    }

    public void destroy() {
        this.videoPlayer.releasePlayer(false);
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    public void showProgress(boolean show, boolean animated) {
        float f = 1.0f;
        if (animated) {
            AnimatorSet animatorSet = this.progressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.progressAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            float[] fArr = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, "alpha", fArr);
            animatorSet2.playTogether(animatorArr);
            this.progressAnimation.setDuration(150L);
            this.progressAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.WebPlayerView.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    WebPlayerView.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        RadialProgressView radialProgressView2 = this.progressView;
        if (!show) {
            f = 0.0f;
        }
        radialProgressView2.setAlpha(f);
    }
}
