package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedPrefsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BotCommandsMenuView;
import org.telegram.ui.Components.BotKeyboardView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SenderSelectPopup;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.StickersActivity;
/* loaded from: classes5.dex */
public class ChatActivityEnterView extends BlurredFrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    private static final int POPUP_CONTENT_BOT_KEYBOARD = 1;
    private static final int RECORD_STATE_CANCEL = 2;
    private static final int RECORD_STATE_CANCEL_BY_GESTURE = 5;
    private static final int RECORD_STATE_CANCEL_BY_TIME = 4;
    private static final int RECORD_STATE_ENTER = 0;
    private static final int RECORD_STATE_PREPARING = 3;
    private static final int RECORD_STATE_SENDING = 1;
    private AccountInstance accountInstance;
    private AdjustPanLayoutHelper adjustPanLayoutHelper;
    public boolean allowBlur;
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    protected int animatedTop;
    private int animatingContentType;
    private HashMap<View, Float> animationParamsX;
    private ImageView attachButton;
    private LinearLayout attachLayout;
    private ImageView audioSendButton;
    private TLRPC.TL_document audioToSend;
    private MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    private AnimatorSet audioVideoButtonAnimation;
    private FrameLayout audioVideoButtonContainer;
    Paint backgroundPaint;
    private ImageView botButton;
    private ReplaceableIconDrawable botButtonDrawable;
    private MessageObject botButtonsMessageObject;
    int botCommandLastPosition;
    int botCommandLastTop;
    private BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter;
    private BotCommandsMenuView botCommandsMenuButton;
    public BotCommandsMenuContainer botCommandsMenuContainer;
    private int botCount;
    private BotKeyboardView botKeyboardView;
    private boolean botKeyboardViewVisible;
    private BotMenuButtonType botMenuButtonType;
    private String botMenuWebViewTitle;
    private String botMenuWebViewUrl;
    private MessageObject botMessageObject;
    private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
    private ChatActivityBotWebViewButton botWebViewButton;
    private BotWebViewMenuContainer botWebViewMenuContainer;
    private boolean calledRecordRunnable;
    private Drawable cameraDrawable;
    private Drawable cameraOutline;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private NumberTextView captionLimitView;
    private float chatSearchExpandOffset;
    private boolean clearBotButtonsOnKeyboardOpen;
    private boolean closeAnimationInProgress;
    private int codePointCount;
    private float composeShadowAlpha;
    private boolean configAnimationsEnabled;
    private int currentAccount;
    private int currentEmojiIcon;
    private int currentLimit;
    private int currentPopupContentType;
    private Animator currentResizeAnimation;
    public ValueAnimator currentTopViewAnimation;
    private ChatActivityEnterViewDelegate delegate;
    private boolean destroyed;
    private long dialog_id;
    private float distCanMove;
    private AnimatorSet doneButtonAnimation;
    private ValueAnimator doneButtonColorAnimator;
    private FrameLayout doneButtonContainer;
    boolean doneButtonEnabled;
    private float doneButtonEnabledProgress;
    private ImageView doneButtonImage;
    private ContextProgressView doneButtonProgress;
    private final Drawable doneCheckDrawable;
    private Paint dotPaint;
    private CharSequence draftMessage;
    private boolean draftSearchWebpage;
    private boolean editingCaption;
    private MessageObject editingMessageObject;
    private ImageView[] emojiButton;
    private AnimatorSet emojiButtonAnimation;
    private int emojiPadding;
    private boolean emojiTabOpen;
    private EmojiView emojiView;
    private boolean emojiViewVisible;
    private ImageView expandStickersButton;
    private boolean expandStickersWithKeyboard;
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean gifsTabOpen;
    private boolean hasBotCommands;
    private boolean hasRecordVideo;
    private Runnable hideKeyboardRunnable;
    private boolean ignoreTextChange;
    private Drawable inactinveSendButtonDrawable;
    private TLRPC.ChatFull info;
    private int innerTextChange;
    private boolean isInitLineCount;
    private boolean isPaste;
    private boolean isPaused;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private long lastTypingTimeSend;
    private int lineCount;
    private int[] location;
    private Drawable lockShadowDrawable;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate;
    protected EditTextCaption messageEditText;
    boolean messageTransitionIsRunning;
    private TLRPC.WebPage messageWebPage;
    private boolean messageWebPageSearch;
    private Drawable micDrawable;
    private Drawable micOutline;
    private Runnable moveToSendStateRunnable;
    private boolean needShowTopView;
    private int notificationsIndex;
    private ImageView notifyButton;
    private CrossOutDrawable notifySilentDrawable;
    private Runnable onEmojiSearchClosed;
    private Runnable onFinishInitCameraRunnable;
    private Runnable onKeyboardClosed;
    private Runnable openKeyboardRunnable;
    private int originalViewHeight;
    private Paint paint;
    private AnimatorSet panelAnimation;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private RectF pauseRect;
    private TLRPC.KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private MediaActionDrawable playPauseDrawable;
    private int popupX;
    private int popupY;
    public boolean preventInput;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable;
    private boolean recordAudioVideoRunnableStarted;
    private RecordCircle recordCircle;
    private Property<RecordCircle, Float> recordCircleScale;
    private RLottieImageView recordDeleteImageView;
    private RecordDot recordDot;
    private int recordInterfaceState;
    private boolean recordIsCanceled;
    private FrameLayout recordPanel;
    private AnimatorSet recordPannelAnimation;
    private LinearLayout recordTimeContainer;
    private TimerView recordTimerView;
    private View recordedAudioBackground;
    private FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudioVideo;
    private int recordingGuid;
    private android.graphics.Rect rect;
    private Paint redDotPaint;
    private boolean removeEmojiViewAfterAnimation;
    private MessageObject replyingMessageObject;
    private final Theme.ResourcesProvider resourcesProvider;
    private Property<View, Integer> roundedTranslationYProperty;
    private Runnable runEmojiPanelAnimation;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private boolean scheduleButtonHidden;
    private ImageView scheduledButton;
    private AnimatorSet scheduledButtonAnimation;
    private ValueAnimator searchAnimator;
    private float searchToOpenProgress;
    private int searchingType;
    private SeekBarWaveform seekBarWaveform;
    private View sendButton;
    private FrameLayout sendButtonContainer;
    private Drawable sendButtonDrawable;
    private Drawable sendButtonInverseDrawable;
    private boolean sendByEnter;
    private Drawable sendDrawable;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private android.graphics.Rect sendRect;
    private SenderSelectPopup senderSelectPopupWindow;
    private SenderSelectView senderSelectView;
    private Runnable setTextFieldRunnable;
    protected boolean shouldAnimateEditTextWithBounds;
    private boolean showKeyboardOnResume;
    private Runnable showTopViewRunnable;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private SlideTextView slideText;
    private SimpleTextView slowModeButton;
    private int slowModeTimer;
    private boolean smoothKeyboard;
    private float startedDraggingX;
    private AnimatedArrowDrawable stickersArrow;
    private boolean stickersDragging;
    private boolean stickersExpanded;
    private int stickersExpandedHeight;
    private Animator stickersExpansionAnim;
    private float stickersExpansionProgress;
    private boolean stickersTabOpen;
    private FrameLayout textFieldContainer;
    boolean textTransitionIsRunning;
    protected View topLineView;
    protected View topView;
    protected float topViewEnterProgress;
    protected boolean topViewShowed;
    private final ValueAnimator.AnimatorUpdateListener topViewUpdateListener;
    private TrendingStickersAlert trendingStickersAlert;
    private Runnable updateExpandabilityRunnable;
    private Runnable updateSlowModeRunnable;
    private ImageView videoSendButton;
    private VideoTimelineView videoTimelineView;
    private VideoEditedInfo videoToSendMessageObject;
    private boolean waitingForKeyboardOpen;
    private boolean waitingForKeyboardOpenAfterAnimation;
    private PowerManager.WakeLock wakeLock;
    private boolean wasSendTyping;

    /* loaded from: classes5.dex */
    public enum BotMenuButtonType {
        NO_BUTTON,
        COMMANDS,
        WEB_VIEW
    }

    /* loaded from: classes5.dex */
    public interface ChatActivityEnterViewDelegate {
        void bottomPanelTranslationYChanged(float f);

        void didPressAttachButton();

        int getContentViewHeight();

        TLRPC.TL_channels_sendAsPeers getSendAsPeers();

        boolean hasForwardingMessages();

        boolean hasScheduledMessages();

        int measureKeyboardHeight();

        void needChangeVideoPreviewState(int i, float f);

        void needSendTyping();

        void needShowMediaBanHint();

        void needStartRecordAudio(int i);

        void needStartRecordVideo(int i, boolean z, int i2);

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onAudioVideoInterfaceUpdated();

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence, boolean z, int i);

        void onPreAudioVideoRecord();

        void onSendLongClick();

        void onStickersExpandedChange();

        void onStickersTab(boolean z);

        void onSwitchRecordMode(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z);

        void onTextSelectionChanged(int i, int i2);

        void onTextSpansChanged(CharSequence charSequence);

        void onTrendingStickersShowed(boolean z);

        void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence);

        void onWindowSizeChanged(int i);

        void openScheduledMessages();

        void prepareMessageSending();

        void scrollToSendingMessage();

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$scrollToSendingMessage(ChatActivityEnterViewDelegate _this) {
            }

            public static void $default$openScheduledMessages(ChatActivityEnterViewDelegate _this) {
            }

            public static boolean $default$hasScheduledMessages(ChatActivityEnterViewDelegate _this) {
                return true;
            }

            public static void $default$bottomPanelTranslationYChanged(ChatActivityEnterViewDelegate _this, float translation) {
            }

            public static void $default$prepareMessageSending(ChatActivityEnterViewDelegate _this) {
            }

            public static void $default$onTrendingStickersShowed(ChatActivityEnterViewDelegate _this, boolean show) {
            }

            public static boolean $default$hasForwardingMessages(ChatActivityEnterViewDelegate _this) {
                return false;
            }

            public static int $default$getContentViewHeight(ChatActivityEnterViewDelegate _this) {
                return 0;
            }

            public static int $default$measureKeyboardHeight(ChatActivityEnterViewDelegate _this) {
                return 0;
            }

            public static TLRPC.TL_channels_sendAsPeers $default$getSendAsPeers(ChatActivityEnterViewDelegate _this) {
                return null;
            }
        }
    }

    /* loaded from: classes5.dex */
    public class SeekBarWaveformView extends View {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SeekBarWaveformView(Context context) {
            super(context);
            ChatActivityEnterView.this = r2;
            r2.seekBarWaveform = new SeekBarWaveform(context);
            r2.seekBarWaveform.setDelegate(new SeekBar.SeekBarDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$SeekBarWaveformView$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public final void onSeekBarDrag(float f) {
                    ChatActivityEnterView.SeekBarWaveformView.this.m2359x426e2bc3(f);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatActivityEnterView$SeekBarWaveformView */
        public /* synthetic */ void m2359x426e2bc3(float progress) {
            if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = progress;
                MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, progress);
            }
        }

        public void setWaveform(byte[] waveform) {
            ChatActivityEnterView.this.seekBarWaveform.setWaveform(waveform);
            invalidate();
        }

        public void setProgress(float progress) {
            ChatActivityEnterView.this.seekBarWaveform.setProgress(progress);
            invalidate();
        }

        public boolean isDragging() {
            return ChatActivityEnterView.this.seekBarWaveform.isDragging();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            boolean result = ChatActivityEnterView.this.seekBarWaveform.onTouch(event.getAction(), event.getX(), event.getY());
            if (result) {
                if (event.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            return result || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ChatActivityEnterView.this.seekBarWaveform.setSize(right - left, bottom - top);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordedVoiceProgress), ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordedVoiceProgressInner), ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordedVoiceProgress));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas, this);
        }
    }

    /* loaded from: classes5.dex */
    public class RecordDot extends View {
        private float alpha;
        boolean attachedToWindow;
        RLottieDrawable drawable;
        private boolean enterAnimation;
        private boolean isIncr;
        private long lastUpdateTime;
        boolean playing;

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attachedToWindow = true;
            if (this.playing) {
                this.drawable.start();
            }
            this.drawable.addParentView(this);
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attachedToWindow = false;
            this.drawable.stop();
            this.drawable.removeParentView(this);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RecordDot(Context context) {
            super(context);
            ChatActivityEnterView.this = r9;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.chat_audio_record_delete, "" + R.raw.chat_audio_record_delete, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), false, null);
            this.drawable = rLottieDrawable;
            rLottieDrawable.setCurrentParentView(this);
            this.drawable.setInvalidateOnProgressSet(true);
            updateColors();
        }

        public void updateColors() {
            int dotColor = ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordedVoiceDot);
            int background = ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelBackground);
            ChatActivityEnterView.this.redDotPaint.setColor(dotColor);
            this.drawable.beginApplyLayerColors();
            this.drawable.setLayerColor("Cup Red.**", dotColor);
            this.drawable.setLayerColor("Box.**", dotColor);
            this.drawable.setLayerColor("Line 1.**", background);
            this.drawable.setLayerColor("Line 2.**", background);
            this.drawable.setLayerColor("Line 3.**", background);
            this.drawable.commitApplyLayerColors();
            if (ChatActivityEnterView.this.playPauseDrawable != null) {
                ChatActivityEnterView.this.playPauseDrawable.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordedVoicePlayPause));
            }
        }

        public void resetAlpha() {
            this.alpha = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            this.playing = false;
            this.drawable.stop();
            invalidate();
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.playing) {
                this.drawable.setAlpha((int) (this.alpha * 255.0f));
            }
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.enterAnimation) {
                this.alpha = 1.0f;
            } else if (!this.isIncr && !this.playing) {
                float f = this.alpha - (((float) dt) / 600.0f);
                this.alpha = f;
                if (f <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            } else {
                float f2 = this.alpha + (((float) dt) / 600.0f);
                this.alpha = f2;
                if (f2 >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.playing) {
                this.drawable.draw(canvas);
            }
            if (!this.playing || !this.drawable.hasBitmap()) {
                canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.redDotPaint);
            }
            invalidate();
        }

        public void playDeleteAnimation() {
            this.playing = true;
            this.drawable.setProgress(0.0f);
            if (this.attachedToWindow) {
                this.drawable.start();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        private boolean canceledByGesture;
        public float drawingCircleRadius;
        public float drawingCx;
        public float drawingCy;
        private float exitTransition;
        float idleProgress;
        boolean incIdle;
        private float lastMovingX;
        private float lastMovingY;
        private int lastSize;
        private long lastUpdateTime;
        private float lockAnimatedTranslation;
        private int paintAlpha;
        private boolean pressed;
        private float progressToSeekbarStep3;
        private float progressToSendButton;
        private float scale;
        private boolean sendButtonVisible;
        private boolean showTooltip;
        private long showTooltipStartTime;
        public boolean skipDraw;
        private int slideDelta;
        private float slideToCancelLockProgress;
        private float slideToCancelProgress;
        private float snapAnimationProgress;
        private float startTranslation;
        private float tooltipAlpha;
        private Drawable tooltipBackground;
        private Drawable tooltipBackgroundArrow;
        private StaticLayout tooltipLayout;
        private float tooltipWidth;
        private float touchSlop;
        private float transformToSeekbar;
        private VirtualViewHelper virtualViewHelper;
        public boolean voiceEnterTransitionInProgress;
        BlobDrawable tinyWaveDrawable = new BlobDrawable(11);
        BlobDrawable bigWaveDrawable = new BlobDrawable(12);
        private TextPaint tooltipPaint = new TextPaint(1);
        private float circleRadius = AndroidUtilities.dpf2(41.0f);
        private float circleRadiusAmplitude = AndroidUtilities.dp(30.0f);
        Paint lockBackgroundPaint = new Paint(1);
        Paint lockPaint = new Paint(1);
        Paint lockOutlinePaint = new Paint(1);
        RectF rectF = new RectF();
        Path path = new Path();
        private float wavesEnterAnimation = 0.0f;
        private boolean showWaves = true;
        private Paint p = new Paint(1);
        private String tooltipMessage = LocaleController.getString("SlideUpToLock", R.string.SlideUpToLock);
        public float iconScale = 1.0f;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RecordCircle(Context context) {
            super(context);
            ChatActivityEnterView.this = this$0;
            this$0.micDrawable = getResources().getDrawable(R.drawable.input_mic_pressed).mutate();
            this$0.micDrawable.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
            this$0.cameraDrawable = getResources().getDrawable(R.drawable.input_video_pressed).mutate();
            this$0.cameraDrawable.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
            this$0.sendDrawable = getResources().getDrawable(R.drawable.attach_send).mutate();
            this$0.sendDrawable.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
            this$0.micOutline = getResources().getDrawable(R.drawable.input_mic).mutate();
            this$0.micOutline.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this$0.cameraOutline = getResources().getDrawable(R.drawable.input_video).mutate();
            this$0.cameraOutline.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            VirtualViewHelper virtualViewHelper = new VirtualViewHelper(this);
            this.virtualViewHelper = virtualViewHelper;
            ViewCompat.setAccessibilityDelegate(this, virtualViewHelper);
            this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(47.0f);
            this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(55.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = AndroidUtilities.dp(47.0f);
            this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(55.0f);
            this.bigWaveDrawable.generateBlob();
            this.lockOutlinePaint.setStyle(Paint.Style.STROKE);
            this.lockOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
            this.lockOutlinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.7f));
            this$0.lockShadowDrawable = getResources().getDrawable(R.drawable.lock_round_shadow);
            this$0.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(this$0.getThemedColor(Theme.key_chat_messagePanelVoiceLockShadow), PorterDuff.Mode.MULTIPLY));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), this$0.getThemedColor(Theme.key_chat_gifSaveHintBackground));
            this.tooltipPaint.setTextSize(AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, R.drawable.tooltip_arrow);
            ViewConfiguration vc = ViewConfiguration.get(context);
            float scaledTouchSlop = vc.getScaledTouchSlop();
            this.touchSlop = scaledTouchSlop;
            this.touchSlop = scaledTouchSlop * scaledTouchSlop;
            updateColors();
        }

        public void setAmplitude(double value) {
            this.bigWaveDrawable.setValue((float) (Math.min(1800.0d, value) / 1800.0d), true);
            this.tinyWaveDrawable.setValue((float) (Math.min(1800.0d, value) / 1800.0d), false);
            float min = (float) (Math.min(1800.0d, value) / 1800.0d);
            this.animateToAmplitude = min;
            this.animateAmplitudeDiff = (min - this.amplitude) / 375.0f;
            invalidate();
        }

        public float getScale() {
            return this.scale;
        }

        public void setScale(float value) {
            this.scale = value;
            invalidate();
        }

        public void setLockAnimatedTranslation(float value) {
            this.lockAnimatedTranslation = value;
            invalidate();
        }

        public void setSnapAnimationProgress(float snapAnimationProgress) {
            this.snapAnimationProgress = snapAnimationProgress;
            invalidate();
        }

        public float getLockAnimatedTranslation() {
            return this.lockAnimatedTranslation;
        }

        public boolean isSendButtonVisible() {
            return this.sendButtonVisible;
        }

        public void setSendButtonInvisible() {
            this.sendButtonVisible = false;
            invalidate();
        }

        public int setLockTranslation(float value) {
            if (value == 10000.0f) {
                this.sendButtonVisible = false;
                this.lockAnimatedTranslation = -1.0f;
                this.startTranslation = -1.0f;
                invalidate();
                this.snapAnimationProgress = 0.0f;
                this.transformToSeekbar = 0.0f;
                this.exitTransition = 0.0f;
                this.iconScale = 1.0f;
                this.scale = 0.0f;
                this.tooltipAlpha = 0.0f;
                this.showTooltip = false;
                this.progressToSendButton = 0.0f;
                this.slideToCancelProgress = 1.0f;
                this.slideToCancelLockProgress = 1.0f;
                this.canceledByGesture = false;
                return 0;
            } else if (this.sendButtonVisible) {
                return 2;
            } else {
                if (this.lockAnimatedTranslation == -1.0f) {
                    this.startTranslation = value;
                }
                this.lockAnimatedTranslation = value;
                invalidate();
                if (this.canceledByGesture || this.slideToCancelProgress < 0.7f || this.startTranslation - this.lockAnimatedTranslation < AndroidUtilities.dp(57.0f)) {
                    return 1;
                }
                this.sendButtonVisible = true;
                return 2;
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (this.sendButtonVisible) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (event.getAction() == 0) {
                    boolean contains = ChatActivityEnterView.this.pauseRect.contains(x, y);
                    this.pressed = contains;
                    return contains;
                } else if (this.pressed) {
                    if (event.getAction() == 1 && ChatActivityEnterView.this.pauseRect.contains(x, y)) {
                        if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null) {
                            ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                        } else {
                            MediaController.getInstance().stopRecording(2, true, 0);
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                        }
                        ChatActivityEnterView.this.slideText.setEnabled(false);
                    }
                    return true;
                }
            }
            return false;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int currentSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.dp(194.0f);
            if (this.lastSize != currentSize) {
                this.lastSize = currentSize;
                StaticLayout staticLayout = new StaticLayout(this.tooltipMessage, this.tooltipPaint, AndroidUtilities.dp(220.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
                this.tooltipLayout = staticLayout;
                int n = staticLayout.getLineCount();
                this.tooltipWidth = 0.0f;
                for (int i = 0; i < n; i++) {
                    float w = this.tooltipLayout.getLineWidth(i);
                    if (w > this.tooltipWidth) {
                        this.tooltipWidth = w;
                    }
                }
            }
            StaticLayout staticLayout2 = this.tooltipLayout;
            if (staticLayout2 != null && staticLayout2.getLineCount() > 1) {
                h += this.tooltipLayout.getHeight() - this.tooltipLayout.getLineBottom(0);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h, C.BUFFER_FLAG_ENCRYPTED));
            float distance = getMeasuredWidth() * 0.35f;
            if (distance > AndroidUtilities.dp(140.0f)) {
                distance = AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-distance) * (1.0f - this.slideToCancelProgress));
        }

        /* JADX WARN: Code restructure failed: missing block: B:174:0x06fd, code lost:
            if ((java.lang.System.currentTimeMillis() - r52.showTooltipStartTime) <= 200) goto L176;
         */
        /* JADX WARN: Removed duplicated region for block: B:102:0x02db  */
        /* JADX WARN: Removed duplicated region for block: B:105:0x030e  */
        /* JADX WARN: Removed duplicated region for block: B:108:0x0322  */
        /* JADX WARN: Removed duplicated region for block: B:113:0x033c  */
        /* JADX WARN: Removed duplicated region for block: B:114:0x03a1  */
        /* JADX WARN: Removed duplicated region for block: B:117:0x03b2  */
        /* JADX WARN: Removed duplicated region for block: B:118:0x03b5  */
        /* JADX WARN: Removed duplicated region for block: B:121:0x03bf  */
        /* JADX WARN: Removed duplicated region for block: B:143:0x0474  */
        /* JADX WARN: Removed duplicated region for block: B:162:0x05e6  */
        /* JADX WARN: Removed duplicated region for block: B:165:0x0600  */
        /* JADX WARN: Removed duplicated region for block: B:170:0x0670  */
        /* JADX WARN: Removed duplicated region for block: B:173:0x06ef  */
        /* JADX WARN: Removed duplicated region for block: B:175:0x0700  */
        /* JADX WARN: Removed duplicated region for block: B:180:0x0710  */
        /* JADX WARN: Removed duplicated region for block: B:189:0x072a  */
        /* JADX WARN: Removed duplicated region for block: B:194:0x0742  */
        /* JADX WARN: Removed duplicated region for block: B:199:0x0769  */
        /* JADX WARN: Removed duplicated region for block: B:200:0x0895  */
        /* JADX WARN: Removed duplicated region for block: B:201:0x089a  */
        /* JADX WARN: Removed duplicated region for block: B:204:0x08c2  */
        /* JADX WARN: Removed duplicated region for block: B:205:0x08c6  */
        /* JADX WARN: Removed duplicated region for block: B:214:0x08df  */
        /* JADX WARN: Removed duplicated region for block: B:223:0x0903  */
        /* JADX WARN: Removed duplicated region for block: B:228:0x0931  */
        /* JADX WARN: Removed duplicated region for block: B:229:0x0934  */
        /* JADX WARN: Removed duplicated region for block: B:232:0x0a48  */
        /* JADX WARN: Removed duplicated region for block: B:235:0x0a59  */
        /* JADX WARN: Removed duplicated region for block: B:240:0x0b4a  */
        /* JADX WARN: Removed duplicated region for block: B:243:0x0b63  */
        /* JADX WARN: Removed duplicated region for block: B:70:0x01e5  */
        /* JADX WARN: Removed duplicated region for block: B:76:0x0205  */
        /* JADX WARN: Removed duplicated region for block: B:77:0x0225  */
        /* JADX WARN: Removed duplicated region for block: B:80:0x023f  */
        /* JADX WARN: Removed duplicated region for block: B:93:0x027e  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r53) {
            /*
                Method dump skipped, instructions count: 2977
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.RecordCircle.onDraw(android.graphics.Canvas):void");
        }

        public void drawIcon(Canvas canvas, int cx, int cy, float alpha) {
            Drawable drawable;
            Drawable replaceDrawable = null;
            if (!isSendButtonVisible()) {
                drawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            } else {
                if (this.progressToSendButton != 1.0f) {
                    replaceDrawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
                }
                drawable = ChatActivityEnterView.this.sendDrawable;
            }
            ChatActivityEnterView.this.sendRect.set(cx - (drawable.getIntrinsicWidth() / 2), cy - (drawable.getIntrinsicHeight() / 2), (drawable.getIntrinsicWidth() / 2) + cx, (drawable.getIntrinsicHeight() / 2) + cy);
            drawable.setBounds(ChatActivityEnterView.this.sendRect);
            if (replaceDrawable != null) {
                replaceDrawable.setBounds(cx - (replaceDrawable.getIntrinsicWidth() / 2), cy - (replaceDrawable.getIntrinsicHeight() / 2), (replaceDrawable.getIntrinsicWidth() / 2) + cx, (replaceDrawable.getIntrinsicHeight() / 2) + cy);
            }
            drawIconInternal(canvas, drawable, replaceDrawable, this.progressToSendButton, (int) (255.0f * alpha));
        }

        private void drawIconInternal(Canvas canvas, Drawable drawable, Drawable replaceDrawable, float progressToSendButton, int alpha) {
            float f = 0.0f;
            if (progressToSendButton == 0.0f || progressToSendButton == 1.0f || replaceDrawable == null) {
                boolean z = this.canceledByGesture;
                if (z && this.slideToCancelProgress == 1.0f) {
                    View v = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.videoSendButton : ChatActivityEnterView.this.audioSendButton;
                    v.setAlpha(1.0f);
                    setVisibility(8);
                    return;
                } else if (z && this.slideToCancelProgress < 1.0f) {
                    Drawable outlineDrawable = ChatActivityEnterView.this.isInVideoMode() ? ChatActivityEnterView.this.cameraOutline : ChatActivityEnterView.this.micOutline;
                    outlineDrawable.setBounds(drawable.getBounds());
                    float f2 = this.slideToCancelProgress;
                    if (f2 >= 0.93f) {
                        f = 255.0f * ((f2 - 0.93f) / 0.07f);
                    }
                    int a = (int) f;
                    outlineDrawable.setAlpha(a);
                    outlineDrawable.draw(canvas);
                    outlineDrawable.setAlpha(255);
                    drawable.setAlpha(255 - a);
                    drawable.draw(canvas);
                    return;
                } else if (!z) {
                    drawable.setAlpha(alpha);
                    drawable.draw(canvas);
                    return;
                } else {
                    return;
                }
            }
            canvas.save();
            canvas.scale(progressToSendButton, progressToSendButton, drawable.getBounds().centerX(), drawable.getBounds().centerY());
            drawable.setAlpha((int) (alpha * progressToSendButton));
            drawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.scale(1.0f - progressToSendButton, 1.0f - progressToSendButton, drawable.getBounds().centerX(), drawable.getBounds().centerY());
            replaceDrawable.setAlpha((int) (alpha * (1.0f - progressToSendButton)));
            replaceDrawable.draw(canvas);
            canvas.restore();
        }

        @Override // android.view.View
        protected boolean dispatchHoverEvent(MotionEvent event) {
            return super.dispatchHoverEvent(event) || this.virtualViewHelper.dispatchHoverEvent(event);
        }

        public void setTransformToSeekbar(float value) {
            this.transformToSeekbar = value;
            invalidate();
        }

        public float getTransformToSeekbarProgressStep3() {
            return this.progressToSeekbarStep3;
        }

        public float getExitTransition() {
            return this.exitTransition;
        }

        public void setExitTransition(float exitTransition) {
            this.exitTransition = exitTransition;
            invalidate();
        }

        public void updateColors() {
            ChatActivityEnterView.this.paint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoiceBackground));
            this.tinyWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoiceBackground), 38));
            this.bigWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoiceBackground), 76));
            this.tooltipPaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_gifSaveHintText));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.getThemedColor(Theme.key_chat_gifSaveHintBackground));
            this.tooltipBackgroundArrow.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_gifSaveHintBackground), PorterDuff.Mode.MULTIPLY));
            this.lockBackgroundPaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoiceLockBackground));
            this.lockPaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoiceLock));
            this.lockOutlinePaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoiceLock));
            this.paintAlpha = ChatActivityEnterView.this.paint.getAlpha();
        }

        public void showTooltipIfNeed() {
            if (SharedConfig.lockRecordAudioVideoHint < 3) {
                this.showTooltip = true;
                this.showTooltipStartTime = System.currentTimeMillis();
            }
        }

        public float getSlideToCancelProgress() {
            return this.slideToCancelProgress;
        }

        public void setSlideToCancelProgress(float slideToCancelProgress) {
            this.slideToCancelProgress = slideToCancelProgress;
            float distance = getMeasuredWidth() * 0.35f;
            if (distance > AndroidUtilities.dp(140.0f)) {
                distance = AndroidUtilities.dp(140.0f);
            }
            this.slideDelta = (int) ((-distance) * (1.0f - slideToCancelProgress));
            invalidate();
        }

        public void canceledByGesture() {
            this.canceledByGesture = true;
        }

        public void setMovingCords(float x, float y) {
            float f = this.lastMovingX;
            float f2 = (x - f) * (x - f);
            float f3 = this.lastMovingY;
            float delta = f2 + ((y - f3) * (y - f3));
            this.lastMovingY = y;
            this.lastMovingX = x;
            if (this.showTooltip && this.tooltipAlpha == 0.0f && delta > this.touchSlop) {
                this.showTooltipStartTime = System.currentTimeMillis();
            }
        }

        public void showWaves(boolean b, boolean animated) {
            if (!animated) {
                this.wavesEnterAnimation = b ? 1.0f : 0.5f;
            }
            this.showWaves = b;
        }

        public void drawWaves(Canvas canvas, float cx, float cy, float additionalScale) {
            float enter = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnterAnimation);
            float f = this.slideToCancelProgress;
            float slideToCancelProgress1 = f > 0.7f ? 1.0f : f / 0.7f;
            canvas.save();
            float s = this.scale * slideToCancelProgress1 * enter * (BlobDrawable.SCALE_BIG_MIN + (this.bigWaveDrawable.amplitude * 1.4f)) * additionalScale;
            canvas.scale(s, s, cx, cy);
            BlobDrawable blobDrawable = this.bigWaveDrawable;
            blobDrawable.draw(cx, cy, canvas, blobDrawable.paint);
            canvas.restore();
            float s2 = this.scale * slideToCancelProgress1 * enter * (BlobDrawable.SCALE_SMALL_MIN + (this.tinyWaveDrawable.amplitude * 1.4f)) * additionalScale;
            canvas.save();
            canvas.scale(s2, s2, cx, cy);
            BlobDrawable blobDrawable2 = this.tinyWaveDrawable;
            blobDrawable2.draw(cx, cy, canvas, blobDrawable2.paint);
            canvas.restore();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes5.dex */
        public class VirtualViewHelper extends ExploreByTouchHelper {
            private int[] coords = new int[2];

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public VirtualViewHelper(View host) {
                super(host);
                RecordCircle.this = r1;
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected int getVirtualViewAt(float x, float y) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    if (ChatActivityEnterView.this.sendRect.contains((int) x, (int) y)) {
                        return 1;
                    }
                    if (!ChatActivityEnterView.this.pauseRect.contains(x, y)) {
                        if (ChatActivityEnterView.this.slideText != null && ChatActivityEnterView.this.slideText.cancelRect != null) {
                            AndroidUtilities.rectTmp.set(ChatActivityEnterView.this.slideText.cancelRect);
                            ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                            RectF rectF = AndroidUtilities.rectTmp;
                            int[] iArr = this.coords;
                            rectF.offset(iArr[0], iArr[1]);
                            ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                            RectF rectF2 = AndroidUtilities.rectTmp;
                            int[] iArr2 = this.coords;
                            rectF2.offset(-iArr2[0], -iArr2[1]);
                            if (AndroidUtilities.rectTmp.contains(x, y)) {
                                return 3;
                            }
                            return -1;
                        }
                        return -1;
                    }
                    return 2;
                }
                return -1;
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected void getVisibleVirtualViews(List<Integer> list) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    list.add(1);
                    list.add(2);
                    list.add(3);
                }
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected void onPopulateNodeForVirtualView(int id, AccessibilityNodeInfoCompat info) {
                if (id == 1) {
                    info.setBoundsInParent(ChatActivityEnterView.this.sendRect);
                    info.setText(LocaleController.getString("Send", R.string.Send));
                } else if (id == 2) {
                    ChatActivityEnterView.this.rect.set((int) ChatActivityEnterView.this.pauseRect.left, (int) ChatActivityEnterView.this.pauseRect.top, (int) ChatActivityEnterView.this.pauseRect.right, (int) ChatActivityEnterView.this.pauseRect.bottom);
                    info.setBoundsInParent(ChatActivityEnterView.this.rect);
                    info.setText(LocaleController.getString("Stop", R.string.Stop));
                } else if (id == 3) {
                    if (ChatActivityEnterView.this.slideText != null && ChatActivityEnterView.this.slideText.cancelRect != null) {
                        AndroidUtilities.rectTmp2.set(ChatActivityEnterView.this.slideText.cancelRect);
                        ChatActivityEnterView.this.slideText.getLocationOnScreen(this.coords);
                        android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                        int[] iArr = this.coords;
                        rect.offset(iArr[0], iArr[1]);
                        ChatActivityEnterView.this.recordCircle.getLocationOnScreen(this.coords);
                        android.graphics.Rect rect2 = AndroidUtilities.rectTmp2;
                        int[] iArr2 = this.coords;
                        rect2.offset(-iArr2[0], -iArr2[1]);
                        info.setBoundsInParent(AndroidUtilities.rectTmp2);
                    }
                    info.setText(LocaleController.getString("Cancel", R.string.Cancel));
                }
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected boolean onPerformActionForVirtualView(int id, int action, Bundle args) {
                return true;
            }
        }
    }

    public ChatActivityEnterView(Activity context, SizeNotifierFrameLayout parent, ChatActivity fragment, boolean isChat) {
        this(context, parent, fragment, isChat, null);
    }

    public ChatActivityEnterView(final Activity context, SizeNotifierFrameLayout parent, final ChatActivity fragment, boolean isChat, final Theme.ResourcesProvider resourcesProvider) {
        super(context, fragment == null ? null : fragment.contentView);
        String str;
        String str2;
        int i;
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        this.currentAccount = UserConfig.selectedAccount;
        this.accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        this.lineCount = 1;
        this.currentLimit = -1;
        this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
        this.animationParamsX = new HashMap<>();
        this.mediaMessageButtonsDelegate = new View.AccessibilityDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setClassName("android.widget.ImageButton");
                info.setClickable(true);
                info.setLongClickable(true);
            }
        };
        this.emojiButton = new ImageView[2];
        this.currentPopupContentType = -1;
        this.currentEmojiIcon = -1;
        this.isPaused = true;
        this.startedDraggingX = -1.0f;
        this.distCanMove = AndroidUtilities.dp(80.0f);
        this.location = new int[2];
        this.messageWebPageSearch = true;
        this.animatingContentType = -1;
        this.doneButtonEnabledProgress = 1.0f;
        this.doneButtonEnabled = true;
        this.openKeyboardRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.2
            @Override // java.lang.Runnable
            public void run() {
                if ((!ChatActivityEnterView.this.hasBotWebView() || !ChatActivityEnterView.this.botCommandsMenuIsShowing()) && !ChatActivityEnterView.this.destroyed && ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                    ChatActivityEnterView.this.messageEditText.requestFocus();
                    AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                    AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                    AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100L);
                }
            }
        };
        this.updateExpandabilityRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.3
            private int lastKnownPage = -1;

            @Override // java.lang.Runnable
            public void run() {
                int curPage;
                if (ChatActivityEnterView.this.emojiView != null && (curPage = ChatActivityEnterView.this.emojiView.getCurrentPage()) != this.lastKnownPage) {
                    this.lastKnownPage = curPage;
                    boolean prevOpen = ChatActivityEnterView.this.stickersTabOpen;
                    int i2 = 2;
                    ChatActivityEnterView.this.stickersTabOpen = curPage == 1 || curPage == 2;
                    boolean prevOpen2 = ChatActivityEnterView.this.emojiTabOpen;
                    ChatActivityEnterView.this.emojiTabOpen = curPage == 0;
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingType == 0) {
                            if (!ChatActivityEnterView.this.stickersTabOpen) {
                                ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                            }
                        } else {
                            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                            if (curPage != 0) {
                                i2 = 1;
                            }
                            chatActivityEnterView.setSearchingTypeInternal(i2, true);
                            ChatActivityEnterView.this.checkStickresExpandHeight();
                        }
                    }
                    if (prevOpen != ChatActivityEnterView.this.stickersTabOpen || prevOpen2 != ChatActivityEnterView.this.emojiTabOpen) {
                        ChatActivityEnterView.this.checkSendButton(true);
                    }
                }
            }
        };
        this.roundedTranslationYProperty = new Property<View, Integer>(Integer.class, "translationY") { // from class: org.telegram.ui.Components.ChatActivityEnterView.4
            public Integer get(View object) {
                return Integer.valueOf(Math.round(object.getTranslationY()));
            }

            public void set(View object, Integer value) {
                object.setTranslationY(value.intValue());
            }
        };
        this.recordCircleScale = new Property<RecordCircle, Float>(Float.class, "scale") { // from class: org.telegram.ui.Components.ChatActivityEnterView.5
            public Float get(RecordCircle object) {
                return Float.valueOf(object.getScale());
            }

            public void set(RecordCircle object, Float value) {
                object.setScale(value.floatValue());
            }
        };
        this.redDotPaint = new Paint(1);
        this.onFinishInitCameraRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.6
            @Override // java.lang.Runnable
            public void run() {
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.needStartRecordVideo(0, true, 0);
                }
            }
        };
        this.recordAudioVideoRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.7
            @Override // java.lang.Runnable
            public void run() {
                if (ChatActivityEnterView.this.delegate != null && ChatActivityEnterView.this.parentActivity != null) {
                    ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                    ChatActivityEnterView.this.calledRecordRunnable = true;
                    ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                    ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                    ChatActivityEnterView.this.slideText.setTranslationY(0.0f);
                    if (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                        if (ChatActivityEnterView.this.parentFragment == null || Build.VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                            ChatActivityEnterView.this.startedDraggingX = -1.0f;
                            MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), ChatActivityEnterView.this.recordingGuid);
                            ChatActivityEnterView.this.recordingAudioVideo = true;
                            ChatActivityEnterView.this.updateRecordInterface(0);
                            ChatActivityEnterView.this.recordTimerView.start();
                            ChatActivityEnterView.this.recordDot.enterAnimation = false;
                            ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                            ChatActivityEnterView.this.recordCircle.showWaves(true, false);
                            return;
                        }
                        ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 23) {
                        boolean hasAudio = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0;
                        boolean hasVideo = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") == 0;
                        if (!hasAudio || !hasVideo) {
                            String[] permissions = new String[(hasAudio || hasVideo) ? 1 : 2];
                            if (!hasAudio && !hasVideo) {
                                permissions[0] = "android.permission.RECORD_AUDIO";
                                permissions[1] = "android.permission.CAMERA";
                            } else if (!hasAudio) {
                                permissions[0] = "android.permission.RECORD_AUDIO";
                            } else {
                                permissions[0] = "android.permission.CAMERA";
                            }
                            ChatActivityEnterView.this.parentActivity.requestPermissions(permissions, 150);
                            return;
                        }
                    }
                    if (!CameraController.getInstance().isCameraInitied()) {
                        CameraController.getInstance().initCamera(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                    } else {
                        ChatActivityEnterView.this.onFinishInitCameraRunnable.run();
                    }
                    if (!ChatActivityEnterView.this.recordingAudioVideo) {
                        ChatActivityEnterView.this.recordingAudioVideo = true;
                        ChatActivityEnterView.this.updateRecordInterface(0);
                        ChatActivityEnterView.this.recordCircle.showWaves(false, false);
                        ChatActivityEnterView.this.recordTimerView.reset();
                    }
                }
            }
        };
        this.paint = new Paint(1);
        this.pauseRect = new RectF();
        this.sendRect = new android.graphics.Rect();
        this.rect = new android.graphics.Rect();
        this.runEmojiPanelAnimation = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.8
            @Override // java.lang.Runnable
            public void run() {
                if (ChatActivityEnterView.this.panelAnimation != null && !ChatActivityEnterView.this.panelAnimation.isRunning()) {
                    ChatActivityEnterView.this.panelAnimation.start();
                }
            }
        };
        this.allowBlur = true;
        this.backgroundPaint = new Paint();
        this.composeShadowAlpha = 1.0f;
        this.topViewUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatActivityEnterView.this.m2324lambda$new$37$orgtelegramuiComponentsChatActivityEnterView(valueAnimator);
            }
        };
        this.botCommandLastPosition = -1;
        this.resourcesProvider = resourcesProvider;
        this.backgroundColor = getThemedColor(Theme.key_chat_messagePanelBackground);
        this.drawBlur = false;
        this.smoothKeyboard = isChat && SharedConfig.smoothKeyboard && !AndroidUtilities.isInMultiwindow && (fragment == null || !fragment.isInBubbleMode());
        Paint paint = new Paint(1);
        this.dotPaint = paint;
        paint.setColor(getThemedColor(Theme.key_chat_emojiPanelNewTrending));
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setClipChildren(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateBotMenuButton);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.parentActivity = context;
        this.parentFragment = fragment;
        if (fragment != null) {
            this.recordingGuid = fragment.getClassGuid();
        }
        this.sizeNotifierLayout = parent;
        parent.setDelegate(this);
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.sendByEnter = preferences.getBoolean("send_by_enter", false);
        this.configAnimationsEnabled = preferences.getBoolean("view_animations", true);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.9
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ChatActivityEnterView.this.botWebViewButton.getVisibility() == 0) {
                    return ChatActivityEnterView.this.botWebViewButton.dispatchTouchEvent(ev);
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        this.textFieldContainer = frameLayout;
        frameLayout.setClipChildren(false);
        this.textFieldContainer.setClipToPadding(false);
        this.textFieldContainer.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 1.0f, 0.0f, 0.0f));
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.10
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (ChatActivityEnterView.this.scheduledButton != null) {
                    int x = (getMeasuredWidth() - AndroidUtilities.dp((ChatActivityEnterView.this.botButton == null || ChatActivityEnterView.this.botButton.getVisibility() != 0) ? 48.0f : 96.0f)) - AndroidUtilities.dp(48.0f);
                    ChatActivityEnterView.this.scheduledButton.layout(x, ChatActivityEnterView.this.scheduledButton.getTop(), ChatActivityEnterView.this.scheduledButton.getMeasuredWidth() + x, ChatActivityEnterView.this.scheduledButton.getBottom());
                }
                if (!ChatActivityEnterView.this.animationParamsX.isEmpty()) {
                    for (int i2 = 0; i2 < getChildCount(); i2++) {
                        View child = getChildAt(i2);
                        Float fromX = (Float) ChatActivityEnterView.this.animationParamsX.get(child);
                        if (fromX != null) {
                            child.setTranslationX(fromX.floatValue() - child.getLeft());
                            child.animate().translationX(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        }
                    }
                    ChatActivityEnterView.this.animationParamsX.clear();
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == ChatActivityEnterView.this.messageEditText) {
                    canvas.save();
                    canvas.clipRect(0, ((-getTop()) - ChatActivityEnterView.this.textFieldContainer.getTop()) - ChatActivityEnterView.this.getTop(), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(6.0f));
                    boolean rez = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return rez;
                }
                boolean rez2 = super.drawChild(canvas, child, drawingTime);
                return rez2;
            }
        };
        frameLayout2.setClipChildren(false);
        this.textFieldContainer.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 48.0f, 0.0f));
        int a = 0;
        for (int i2 = 2; a < i2; i2 = 2) {
            this.emojiButton[a] = new ImageView(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.11
                @Override // android.widget.ImageView, android.view.View
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (getTag() != null && ChatActivityEnterView.this.attachLayout != null && !ChatActivityEnterView.this.emojiViewVisible && !MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).getUnreadStickerSets().isEmpty() && ChatActivityEnterView.this.dotPaint != null) {
                        int x = (getWidth() / 2) + AndroidUtilities.dp(9.0f);
                        int y = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                        canvas.drawCircle(x, y, AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.dotPaint);
                    }
                }
            };
            this.emojiButton[a].setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.emojiButton[a].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (Build.VERSION.SDK_INT >= 21) {
                this.emojiButton[a].setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
            }
            frameLayout2.addView(this.emojiButton[a], LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
            this.emojiButton[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.m2305lambda$new$1$orgtelegramuiComponentsChatActivityEnterView(view);
                }
            });
            this.emojiButton[a].setContentDescription(LocaleController.getString("AccDescrEmojiButton", R.string.AccDescrEmojiButton));
            if (a == 1) {
                this.emojiButton[a].setVisibility(4);
                this.emojiButton[a].setAlpha(0.0f);
                this.emojiButton[a].setScaleX(0.1f);
                this.emojiButton[a].setScaleY(0.1f);
            }
            a++;
        }
        setEmojiButtonImage(false, false);
        NumberTextView numberTextView = new NumberTextView(context);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 0.0f, 48.0f));
        AnonymousClass12 anonymousClass12 = new AnonymousClass12(context, resourcesProvider, resourcesProvider, fragment, context);
        this.messageEditText = anonymousClass12;
        anonymousClass12.setDelegate(new EditTextCaption.EditTextCaptionDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda53
            @Override // org.telegram.ui.Components.EditTextCaption.EditTextCaptionDelegate
            public final void onSpansChanged() {
                ChatActivityEnterView.this.m2313lambda$new$2$orgtelegramuiComponentsChatActivityEnterView();
            }
        });
        this.messageEditText.setIncludeFontPadding(false);
        this.messageEditText.setWindowView(this.parentActivity.getWindow().getDecorView());
        ChatActivity chatActivity = this.parentFragment;
        TLRPC.EncryptedChat encryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        this.messageEditText.setAllowTextEntitiesIntersection(supportsSendingNewEntities());
        updateFieldHint(false);
        int flags = 268435456;
        this.messageEditText.setImeOptions(encryptedChat != null ? 268435456 | 16777216 : flags);
        EditTextCaption editTextCaption = this.messageEditText;
        editTextCaption.setInputType(editTextCaption.getInputType() | 16384 | 131072);
        this.messageEditText.setSingleLine(false);
        this.messageEditText.setMaxLines(6);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        this.messageEditText.setTextColor(getThemedColor(Theme.key_chat_messagePanelText));
        this.messageEditText.setLinkTextColor(getThemedColor(Theme.key_chat_messageLinkOut));
        this.messageEditText.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        this.messageEditText.setHintColor(getThemedColor(Theme.key_chat_messagePanelHint));
        this.messageEditText.setHintTextColor(getThemedColor(Theme.key_chat_messagePanelHint));
        this.messageEditText.setCursorColor(getThemedColor(Theme.key_chat_messagePanelCursor));
        this.messageEditText.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        frameLayout2.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, isChat ? 50.0f : 2.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.13
            boolean ctrlPressed = false;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                boolean z = false;
                if (keyCode == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing() && keyEvent.getAction() == 1) {
                    if (!ContentPreviewViewer.hasInstance() || !ContentPreviewViewer.getInstance().isVisible()) {
                        if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                            return false;
                        }
                        if (keyEvent.getAction() == 1) {
                            if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                                SharedPreferences preferences2 = MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount);
                                SharedPreferences.Editor edit = preferences2.edit();
                                edit.putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                            }
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                                if (ChatActivityEnterView.this.emojiView != null) {
                                    ChatActivityEnterView.this.emojiView.closeSearch(true);
                                }
                                ChatActivityEnterView.this.messageEditText.requestFocus();
                            } else if (!ChatActivityEnterView.this.stickersExpanded) {
                                if (ChatActivityEnterView.this.stickersExpansionAnim == null) {
                                    if (ChatActivityEnterView.this.botButtonsMessageObject == null || ChatActivityEnterView.this.currentPopupContentType == 1 || !TextUtils.isEmpty(ChatActivityEnterView.this.messageEditText.getText())) {
                                        ChatActivityEnterView.this.showPopup(0, 0);
                                    } else {
                                        ChatActivityEnterView.this.showPopup(1, 1);
                                    }
                                }
                            } else {
                                ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                            }
                        }
                        return true;
                    }
                    ContentPreviewViewer.getInstance().closeWithMenu();
                    return true;
                } else if (keyCode == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null)) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (keyCode != 113 && keyCode != 114) {
                    return false;
                } else {
                    if (keyEvent.getAction() == 0) {
                        z = true;
                    }
                    this.ctrlPressed = z;
                    return true;
                }
            }
        });
        this.messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.14
            boolean ctrlPressed = false;

            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                if (i3 == 4) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (keyEvent != null && i3 == 0) {
                    if ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null) {
                        ChatActivityEnterView.this.sendMessage();
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            }
        });
        this.messageEditText.addTextChangedListener(new AnonymousClass15());
        if (isChat) {
            if (this.parentFragment != null) {
                Drawable drawable1 = context.getResources().getDrawable(R.drawable.input_calendar1).mutate();
                Drawable drawable2 = context.getResources().getDrawable(R.drawable.input_calendar2).mutate();
                str = Theme.key_chat_messagePanelIcons;
                drawable1.setColorFilter(new PorterDuffColorFilter(getThemedColor(str), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_recordedVoiceDot), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                ImageView imageView = new ImageView(context);
                this.scheduledButton = imageView;
                imageView.setImageDrawable(combinedDrawable);
                this.scheduledButton.setVisibility(8);
                this.scheduledButton.setContentDescription(LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages));
                this.scheduledButton.setScaleType(ImageView.ScaleType.CENTER);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.scheduledButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
                }
                frameLayout2.addView(this.scheduledButton, LayoutHelper.createFrame(48, 48, 85));
                this.scheduledButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda10
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChatActivityEnterView.this.m2322lambda$new$3$orgtelegramuiComponentsChatActivityEnterView(view);
                    }
                });
            } else {
                str = Theme.key_chat_messagePanelIcons;
            }
            LinearLayout linearLayout = new LinearLayout(context);
            this.attachLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.attachLayout.setEnabled(false);
            this.attachLayout.setPivotX(AndroidUtilities.dp(48.0f));
            this.attachLayout.setClipChildren(false);
            frameLayout2.addView(this.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
            BotCommandsMenuView botCommandsMenuView = new BotCommandsMenuView(getContext());
            this.botCommandsMenuButton = botCommandsMenuView;
            botCommandsMenuView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.m2325lambda$new$4$orgtelegramuiComponentsChatActivityEnterView(view);
                }
            });
            frameLayout2.addView(this.botCommandsMenuButton, LayoutHelper.createFrame(-2, 32.0f, 83, 10.0f, 8.0f, 10.0f, 8.0f));
            AndroidUtilities.updateViewVisibilityAnimated(this.botCommandsMenuButton, false, 1.0f, false);
            this.botCommandsMenuButton.setExpanded(true, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            BotCommandsMenuContainer botCommandsMenuContainer = new BotCommandsMenuContainer(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.16
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.BotCommandsMenuContainer
                public void onDismiss() {
                    super.onDismiss();
                    ChatActivityEnterView.this.botCommandsMenuButton.setOpened(false);
                }
            };
            this.botCommandsMenuContainer = botCommandsMenuContainer;
            botCommandsMenuContainer.listView.setLayoutManager(layoutManager);
            RecyclerListView recyclerListView = this.botCommandsMenuContainer.listView;
            BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter = new BotCommandsMenuView.BotCommandsAdapter();
            this.botCommandsAdapter = botCommandsAdapter;
            recyclerListView.setAdapter(botCommandsAdapter);
            this.botCommandsMenuContainer.listView.setOnItemClickListener(new AnonymousClass17(resourcesProvider, fragment));
            this.botCommandsMenuContainer.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.18
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public boolean onItemClick(View view, int position) {
                    if (view instanceof BotCommandsMenuView.BotCommandView) {
                        String command = ((BotCommandsMenuView.BotCommandView) view).getCommand();
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.setFieldText(command + " ");
                        ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            this.botCommandsMenuContainer.setClipToPadding(false);
            this.sizeNotifierLayout.addView(this.botCommandsMenuContainer, 14, LayoutHelper.createFrame(-1, -1, 80));
            this.botCommandsMenuContainer.setVisibility(8);
            BotWebViewMenuContainer botWebViewMenuContainer = new BotWebViewMenuContainer(context, this) { // from class: org.telegram.ui.Components.ChatActivityEnterView.19
                @Override // org.telegram.ui.Components.BotWebViewMenuContainer
                public void onDismiss() {
                    super.onDismiss();
                    ChatActivityEnterView.this.botCommandsMenuButton.setOpened(false);
                }
            };
            this.botWebViewMenuContainer = botWebViewMenuContainer;
            this.sizeNotifierLayout.addView(botWebViewMenuContainer, 15, LayoutHelper.createFrame(-1, -1, 80));
            this.botWebViewMenuContainer.setVisibility(8);
            this.botWebViewMenuContainer.setOnDismissGlobalListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.m2326lambda$new$5$orgtelegramuiComponentsChatActivityEnterView();
                }
            });
            ImageView imageView2 = new ImageView(context);
            this.botButton = imageView2;
            ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context);
            this.botButtonDrawable = replaceableIconDrawable;
            imageView2.setImageDrawable(replaceableIconDrawable);
            this.botButtonDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(str), PorterDuff.Mode.MULTIPLY));
            this.botButtonDrawable.setIcon(R.drawable.input_bot2, false);
            this.botButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.botButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
            }
            this.botButton.setVisibility(8);
            AndroidUtilities.updateViewVisibilityAnimated(this.botButton, false, 0.1f, false);
            this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda14
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.m2327lambda$new$6$orgtelegramuiComponentsChatActivityEnterView(view);
                }
            });
            this.notifyButton = new ImageView(context);
            CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context, R.drawable.input_notify_on, str);
            this.notifySilentDrawable = crossOutDrawable;
            this.notifyButton.setImageDrawable(crossOutDrawable);
            this.notifySilentDrawable.setCrossOut(this.silent, false);
            ImageView imageView3 = this.notifyButton;
            if (this.silent) {
                i = R.string.AccDescrChanSilentOn;
                str2 = "AccDescrChanSilentOn";
            } else {
                i = R.string.AccDescrChanSilentOff;
                str2 = "AccDescrChanSilentOff";
            }
            imageView3.setContentDescription(LocaleController.getString(str2, i));
            this.notifyButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(str), PorterDuff.Mode.MULTIPLY));
            this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.notifyButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
            }
            this.notifyButton.setVisibility((!this.canWriteToChannel || ((chatActivityEnterViewDelegate = this.delegate) != null && chatActivityEnterViewDelegate.hasScheduledMessages())) ? 8 : 0);
            this.attachLayout.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.20
                private Toast visibleToast;

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    String str3;
                    int i3;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.silent = !chatActivityEnterView.silent;
                    if (ChatActivityEnterView.this.notifySilentDrawable == null) {
                        ChatActivityEnterView.this.notifySilentDrawable = new CrossOutDrawable(context, R.drawable.input_notify_on, Theme.key_chat_messagePanelIcons);
                    }
                    ChatActivityEnterView.this.notifySilentDrawable.setCrossOut(ChatActivityEnterView.this.silent, true);
                    ChatActivityEnterView.this.notifyButton.setImageDrawable(ChatActivityEnterView.this.notifySilentDrawable);
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ChatActivityEnterView.this.currentAccount).edit();
                    edit.putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
                    NotificationsController.getInstance(ChatActivityEnterView.this.currentAccount).updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
                    try {
                        Toast toast = this.visibleToast;
                        if (toast != null) {
                            toast.cancel();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    fragment.getUndoView().showWithAction(0L, !ChatActivityEnterView.this.silent ? 54 : 55, (Runnable) null);
                    ImageView imageView4 = ChatActivityEnterView.this.notifyButton;
                    if (ChatActivityEnterView.this.silent) {
                        i3 = R.string.AccDescrChanSilentOn;
                        str3 = "AccDescrChanSilentOn";
                    } else {
                        i3 = R.string.AccDescrChanSilentOff;
                        str3 = "AccDescrChanSilentOff";
                    }
                    imageView4.setContentDescription(LocaleController.getString(str3, i3));
                    ChatActivityEnterView.this.updateFieldHint(true);
                }
            });
            ImageView imageView4 = new ImageView(context);
            this.attachButton = imageView4;
            imageView4.setColorFilter(new PorterDuffColorFilter(getThemedColor(str), PorterDuff.Mode.MULTIPLY));
            this.attachButton.setImageResource(R.drawable.input_attach);
            this.attachButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.attachButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
            }
            this.attachLayout.addView(this.attachButton, LayoutHelper.createLinear(48, 48));
            this.attachButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda15
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityEnterView.this.m2328lambda$new$7$orgtelegramuiComponentsChatActivityEnterView(view);
                }
            });
            this.attachButton.setContentDescription(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
        } else {
            str = Theme.key_chat_messagePanelIcons;
        }
        SenderSelectView senderSelectView = new SenderSelectView(getContext());
        this.senderSelectView = senderSelectView;
        senderSelectView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatActivityEnterView.this.m2310lambda$new$14$orgtelegramuiComponentsChatActivityEnterView(context, view);
            }
        });
        this.senderSelectView.setVisibility(8);
        frameLayout2.addView(this.senderSelectView, LayoutHelper.createFrame(32, 32.0f, 83, 10.0f, 8.0f, 10.0f, 8.0f));
        FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.26
            @Override // android.view.View
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                ChatActivityEnterView.this.updateSendAsButton();
            }
        };
        this.recordedAudioPanel = frameLayout3;
        frameLayout3.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        frameLayout2.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.recordDeleteImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.recordDeleteImageView.setAnimation(R.raw.chat_audio_record_delete_2, 28, 28);
        this.recordDeleteImageView.getAnimatedDrawable().setInvalidateOnProgressSet(true);
        updateRecordedDeleteIconColors();
        this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
        if (Build.VERSION.SDK_INT >= 21) {
            this.recordDeleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        }
        this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        this.recordDeleteImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatActivityEnterView.this.m2311lambda$new$15$orgtelegramuiComponentsChatActivityEnterView(view);
            }
        });
        VideoTimelineView videoTimelineView = new VideoTimelineView(context);
        this.videoTimelineView = videoTimelineView;
        videoTimelineView.setColor(getThemedColor(Theme.key_chat_messagePanelVideoFrame));
        this.videoTimelineView.setRoundFrames(true);
        this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.27
            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void onLeftProgressChanged(float progress) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.startTime = ((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * progress;
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, progress);
                }
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void onRightProgressChanged(float progress) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.endTime = ((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * progress;
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, progress);
                }
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void didStartDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(1, 0.0f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void didStopDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(0, 0.0f);
            }
        });
        this.recordedAudioPanel.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, -1.0f, 19, 56.0f, 0.0f, 8.0f, 0.0f));
        VideoTimelineView.TimeHintView videoTimeHintView = new VideoTimelineView.TimeHintView(context);
        this.videoTimelineView.setTimeHintView(videoTimeHintView);
        this.sizeNotifierLayout.addView(videoTimeHintView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 52.0f));
        View view = new View(context);
        this.recordedAudioBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor(Theme.key_chat_recordedVoiceBackground)));
        this.recordedAudioPanel.addView(this.recordedAudioBackground, LayoutHelper.createFrame(-1, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioSeekBar = new SeekBarWaveformView(context);
        LinearLayout waveFormTimerLayout = new LinearLayout(context);
        waveFormTimerLayout.setOrientation(0);
        this.recordedAudioPanel.addView(waveFormTimerLayout, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 13.0f, 0.0f));
        this.playPauseDrawable = new MediaActionDrawable();
        this.recordedAudioPlayButton = new ImageView(context);
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f, AndroidUtilities.dpf2(24.0f), AndroidUtilities.dpf2(24.0f));
        this.recordedAudioPlayButton.setImageMatrix(matrix);
        this.recordedAudioPlayButton.setImageDrawable(this.playPauseDrawable);
        this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.MATRIX);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 13.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatActivityEnterView.this.m2312lambda$new$16$orgtelegramuiComponentsChatActivityEnterView(view2);
            }
        });
        TextView textView = new TextView(context);
        this.recordedAudioTimeTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_chat_messagePanelVoiceDuration));
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        waveFormTimerLayout.addView(this.recordedAudioSeekBar, LayoutHelper.createLinear(0, 32, 1.0f, 16, 0, 0, 4, 0));
        waveFormTimerLayout.addView(this.recordedAudioTimeTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 16));
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.recordPanel = frameLayout4;
        frameLayout4.setClipChildren(false);
        this.recordPanel.setVisibility(8);
        frameLayout2.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48.0f));
        this.recordPanel.setOnTouchListener(ChatActivityEnterView$$ExternalSyntheticLambda23.INSTANCE);
        SlideTextView slideTextView = new SlideTextView(context);
        this.slideText = slideTextView;
        this.recordPanel.addView(slideTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 45.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.recordTimeContainer = linearLayout2;
        linearLayout2.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordTimeContainer.setFocusable(false);
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-1, -1, 16));
        RecordDot recordDot = new RecordDot(context);
        this.recordDot = recordDot;
        this.recordTimeContainer.addView(recordDot, LayoutHelper.createLinear(28, 28, 16, 0, 0, 0, 0));
        TimerView timerView = new TimerView(context);
        this.recordTimerView = timerView;
        this.recordTimeContainer.addView(timerView, LayoutHelper.createLinear(-1, -1, 16, 6, 0, 0, 0));
        FrameLayout frameLayout5 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.28
            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == ChatActivityEnterView.this.sendButton && ChatActivityEnterView.this.textTransitionIsRunning) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.sendButtonContainer = frameLayout5;
        frameLayout5.setClipChildren(false);
        this.sendButtonContainer.setClipToPadding(false);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createFrame(48, 48, 85));
        FrameLayout frameLayout6 = new FrameLayout(context);
        this.audioVideoButtonContainer = frameLayout6;
        frameLayout6.setSoundEffectsEnabled(false);
        this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        this.audioVideoButtonContainer.setFocusable(true);
        this.audioVideoButtonContainer.setImportantForAccessibility(1);
        this.audioVideoButtonContainer.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda21
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return ChatActivityEnterView.this.m2316lambda$new$24$orgtelegramuiComponentsChatActivityEnterView(resourcesProvider, view2, motionEvent);
            }
        });
        ImageView imageView5 = new ImageView(context);
        this.audioSendButton = imageView5;
        imageView5.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.audioSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(str), PorterDuff.Mode.MULTIPLY));
        this.audioSendButton.setImageResource(R.drawable.input_mic);
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.audioSendButton.setContentDescription(LocaleController.getString("AccDescrVoiceMessage", R.string.AccDescrVoiceMessage));
        this.audioSendButton.setFocusable(true);
        this.audioSendButton.setImportantForAccessibility(1);
        this.audioSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
        this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (isChat) {
            ImageView imageView6 = new ImageView(context);
            this.videoSendButton = imageView6;
            imageView6.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.videoSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(str), PorterDuff.Mode.MULTIPLY));
            this.videoSendButton.setImageResource(R.drawable.input_video);
            this.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            this.videoSendButton.setContentDescription(LocaleController.getString("AccDescrVideoMessage", R.string.AccDescrVideoMessage));
            this.videoSendButton.setFocusable(true);
            this.videoSendButton.setImportantForAccessibility(1);
            this.videoSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
            this.audioVideoButtonContainer.addView(this.videoSendButton, LayoutHelper.createFrame(48, 48.0f));
        }
        RecordCircle recordCircle = new RecordCircle(context);
        this.recordCircle = recordCircle;
        recordCircle.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView7 = new ImageView(context);
        this.cancelBotButton = imageView7;
        imageView7.setVisibility(4);
        this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView8 = this.cancelBotButton;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.ChatActivityEnterView.29
            @Override // org.telegram.ui.Components.CloseProgressDrawable2
            protected int getCurrentColor() {
                return Theme.getColor(Theme.key_chat_messagePanelCancelInlineBot);
            }
        };
        this.progressDrawable = closeProgressDrawable2;
        imageView8.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setContentDescription(LocaleController.getString("Cancel", R.string.Cancel));
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            this.cancelBotButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        }
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatActivityEnterView.this.m2317lambda$new$25$orgtelegramuiComponentsChatActivityEnterView(view2);
            }
        });
        if (isInScheduleMode()) {
            this.sendButtonDrawable = context.getResources().getDrawable(R.drawable.input_schedule).mutate();
            this.sendButtonInverseDrawable = context.getResources().getDrawable(R.drawable.input_schedule).mutate();
            this.inactinveSendButtonDrawable = context.getResources().getDrawable(R.drawable.input_schedule).mutate();
        } else {
            this.sendButtonDrawable = context.getResources().getDrawable(R.drawable.ic_send).mutate();
            this.sendButtonInverseDrawable = context.getResources().getDrawable(R.drawable.ic_send).mutate();
            this.inactinveSendButtonDrawable = context.getResources().getDrawable(R.drawable.ic_send).mutate();
        }
        View view2 = new View(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.30
            private float animateBounce;
            private float animationDuration;
            private float animationProgress;
            private int drawableColor;
            private long lastAnimationTime;
            private int prevColorType;

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int colorType;
                int color;
                int x = (getMeasuredWidth() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth()) / 2;
                int y = (getMeasuredHeight() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight()) / 2;
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    y -= AndroidUtilities.dp(1.0f);
                } else {
                    x += AndroidUtilities.dp(2.0f);
                }
                boolean z = ChatActivityEnterView.this.sendPopupWindow != null && ChatActivityEnterView.this.sendPopupWindow.isShowing();
                boolean showingPopup = z;
                if (z) {
                    color = ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoicePressed);
                    colorType = 1;
                } else {
                    color = ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelSend);
                    colorType = 2;
                }
                if (color != this.drawableColor) {
                    this.lastAnimationTime = SystemClock.elapsedRealtime();
                    int i3 = this.prevColorType;
                    if (i3 != 0 && i3 != colorType) {
                        this.animationProgress = 0.0f;
                        if (showingPopup) {
                            this.animationDuration = 200.0f;
                        } else {
                            this.animationDuration = 120.0f;
                        }
                    } else {
                        this.animationProgress = 1.0f;
                    }
                    this.prevColorType = colorType;
                    this.drawableColor = color;
                    ChatActivityEnterView.this.sendButtonDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelSend), PorterDuff.Mode.MULTIPLY));
                    int c = ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelIcons);
                    ChatActivityEnterView.this.inactinveSendButtonDrawable.setColorFilter(new PorterDuffColorFilter(Color.argb(180, Color.red(c), Color.green(c), Color.blue(c)), PorterDuff.Mode.MULTIPLY));
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setColorFilter(new PorterDuffColorFilter(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
                }
                if (this.animationProgress < 1.0f) {
                    long newTime = SystemClock.elapsedRealtime();
                    long dt = newTime - this.lastAnimationTime;
                    float f = this.animationProgress + (((float) dt) / this.animationDuration);
                    this.animationProgress = f;
                    if (f > 1.0f) {
                        this.animationProgress = 1.0f;
                    }
                    this.lastAnimationTime = newTime;
                    invalidate();
                }
                if (!showingPopup) {
                    if (ChatActivityEnterView.this.slowModeTimer != Integer.MAX_VALUE || ChatActivityEnterView.this.isInScheduleMode()) {
                        ChatActivityEnterView.this.sendButtonDrawable.setBounds(x, y, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + x, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + y);
                        ChatActivityEnterView.this.sendButtonDrawable.draw(canvas);
                    } else {
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.setBounds(x, y, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + x, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + y);
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.draw(canvas);
                    }
                }
                if (showingPopup || this.animationProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelSend));
                    int rad = AndroidUtilities.dp(20.0f);
                    if (showingPopup) {
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(255);
                        float p = this.animationProgress;
                        if (p <= 0.25f) {
                            float progress = p / 0.25f;
                            rad = (int) (rad + (AndroidUtilities.dp(2.0f) * CubicBezierInterpolator.EASE_IN.getInterpolation(progress)));
                        } else {
                            float p2 = p - 0.25f;
                            if (p2 <= 0.5f) {
                                float progress2 = p2 / 0.5f;
                                rad = (int) (rad + (AndroidUtilities.dp(2.0f) - (AndroidUtilities.dp(3.0f) * CubicBezierInterpolator.EASE_IN.getInterpolation(progress2))));
                            } else {
                                float progress3 = (p2 - 0.5f) / 0.25f;
                                rad = (int) (rad + (-AndroidUtilities.dp(1.0f)) + (AndroidUtilities.dp(1.0f) * CubicBezierInterpolator.EASE_IN.getInterpolation(progress3)));
                            }
                        }
                    } else {
                        int alpha = (int) ((1.0f - this.animationProgress) * 255.0f);
                        Theme.dialogs_onlineCirclePaint.setAlpha(alpha);
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(alpha);
                    }
                    canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, rad, Theme.dialogs_onlineCirclePaint);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setBounds(x, y, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + x, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + y);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.draw(canvas);
                }
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (getAlpha() <= 0.0f) {
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        this.sendButton = view2;
        view2.setVisibility(4);
        int color = getThemedColor(Theme.key_chat_messagePanelSend);
        this.sendButton.setContentDescription(LocaleController.getString("Send", R.string.Send));
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            View view3 = this.sendButton;
            int red = Color.red(color);
            int green = Color.green(color);
            int flags2 = Color.blue(color);
            view3.setBackgroundDrawable(Theme.createSelectorDrawable(Color.argb(24, red, green, flags2), 1));
        }
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
        this.sendButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                ChatActivityEnterView.this.m2318lambda$new$26$orgtelegramuiComponentsChatActivityEnterView(view4);
            }
        });
        this.sendButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda20
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view4) {
                boolean onSendLongClick;
                onSendLongClick = ChatActivityEnterView.this.onSendLongClick(view4);
                return onSendLongClick;
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.slowModeButton = simpleTextView;
        simpleTextView.setTextSize(18);
        this.slowModeButton.setVisibility(4);
        this.slowModeButton.setSoundEffectsEnabled(false);
        this.slowModeButton.setScaleX(0.1f);
        this.slowModeButton.setScaleY(0.1f);
        this.slowModeButton.setAlpha(0.0f);
        this.slowModeButton.setPadding(0, 0, AndroidUtilities.dp(13.0f), 0);
        this.slowModeButton.setGravity(21);
        this.slowModeButton.setTextColor(getThemedColor(str));
        this.sendButtonContainer.addView(this.slowModeButton, LayoutHelper.createFrame(64, 48, 53));
        this.slowModeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                ChatActivityEnterView.this.m2319lambda$new$27$orgtelegramuiComponentsChatActivityEnterView(view4);
            }
        });
        this.slowModeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view4) {
                return ChatActivityEnterView.this.m2320lambda$new$28$orgtelegramuiComponentsChatActivityEnterView(view4);
            }
        });
        ImageView imageView9 = new ImageView(context) { // from class: org.telegram.ui.Components.ChatActivityEnterView.31
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (getAlpha() <= 0.0f) {
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        this.expandStickersButton = imageView9;
        imageView9.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView10 = this.expandStickersButton;
        AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(getThemedColor(str), false);
        this.stickersArrow = animatedArrowDrawable;
        imageView10.setImageDrawable(animatedArrowDrawable);
        this.expandStickersButton.setVisibility(8);
        this.expandStickersButton.setScaleX(0.1f);
        this.expandStickersButton.setScaleY(0.1f);
        this.expandStickersButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            this.expandStickersButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        }
        this.sendButtonContainer.addView(this.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
        this.expandStickersButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                ChatActivityEnterView.this.m2321lambda$new$29$orgtelegramuiComponentsChatActivityEnterView(view4);
            }
        });
        this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", R.string.AccDescrExpandPanel));
        FrameLayout frameLayout7 = new FrameLayout(context);
        this.doneButtonContainer = frameLayout7;
        frameLayout7.setVisibility(8);
        this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createFrame(48, 48, 85));
        this.doneButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                ChatActivityEnterView.this.m2323lambda$new$30$orgtelegramuiComponentsChatActivityEnterView(view4);
            }
        });
        Drawable doneCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), getThemedColor(Theme.key_chat_messagePanelSend));
        Drawable mutate = context.getResources().getDrawable(R.drawable.input_done).mutate();
        this.doneCheckDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(doneCircleDrawable, mutate, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable2.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView11 = new ImageView(context);
        this.doneButtonImage = imageView11;
        imageView11.setScaleType(ImageView.ScaleType.CENTER);
        this.doneButtonImage.setImageDrawable(combinedDrawable2);
        this.doneButtonImage.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.doneButtonContainer.addView(this.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context, 0);
        this.doneButtonProgress = contextProgressView;
        contextProgressView.setVisibility(4);
        this.doneButtonContainer.addView(this.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences sharedPreferences = MessagesController.getGlobalEmojiSettings();
        this.keyboardHeight = sharedPreferences.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        this.keyboardHeightLand = sharedPreferences.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
        ChatActivityBotWebViewButton chatActivityBotWebViewButton = new ChatActivityBotWebViewButton(context);
        this.botWebViewButton = chatActivityBotWebViewButton;
        chatActivityBotWebViewButton.setVisibility(8);
        this.botWebViewButton.setBotMenuButton(this.botCommandsMenuButton);
        frameLayout2.addView(this.botWebViewButton, LayoutHelper.createFrame(-1, -1, 80));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2305lambda$new$1$orgtelegramuiComponentsChatActivityEnterView(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper != null && adjustPanLayoutHelper.animationInProgress()) {
            return;
        }
        if (hasBotWebView() && botCommandsMenuIsShowing()) {
            BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
            view.getClass();
            botWebViewMenuContainer.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda26(view));
            return;
        }
        boolean z = true;
        if (!isPopupShowing() || this.currentPopupContentType != 0) {
            showPopup(1, 0);
            EmojiView emojiView = this.emojiView;
            if (this.messageEditText.length() <= 0) {
                z = false;
            }
            emojiView.onOpen(z);
            return;
        }
        if (this.searchingType != 0) {
            setSearchingTypeInternal(0, true);
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.closeSearch(false);
            }
            this.messageEditText.requestFocus();
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, true, false);
            this.waitingForKeyboardOpenAfterAnimation = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.m2304lambda$new$0$orgtelegramuiComponentsChatActivityEnterView();
                }
            }, 200L);
            return;
        }
        openKeyboardInternal();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2304lambda$new$0$orgtelegramuiComponentsChatActivityEnterView() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$12 */
    /* loaded from: classes5.dex */
    public class AnonymousClass12 extends EditTextCaption {
        final /* synthetic */ Activity val$context;
        final /* synthetic */ ChatActivity val$fragment;
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass12(Context context, Theme.ResourcesProvider resourcesProvider, Theme.ResourcesProvider resourcesProvider2, ChatActivity chatActivity, Activity activity) {
            super(context, resourcesProvider);
            ChatActivityEnterView.this = this$0;
            this.val$resourcesProvider = resourcesProvider2;
            this.val$fragment = chatActivity;
            this.val$context = activity;
        }

        /* renamed from: send */
        public void m2351xe2b77348(InputContentInfoCompat inputContentInfo, boolean notify, int scheduleDate) {
            ClipDescription description = inputContentInfo.getDescription();
            if (description.hasMimeType("image/gif")) {
                SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, null, null, inputContentInfo.getContentUri(), null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), inputContentInfo, null, notify, 0);
            } else {
                SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, null, inputContentInfo.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, null, null, inputContentInfo, 0, null, notify, 0);
            }
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onMessageSend(null, true, scheduleDate);
            }
        }

        @Override // android.widget.TextView, android.view.View
        public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
            InputConnection ic = super.onCreateInputConnection(editorInfo);
            if (ic == null) {
                return null;
            }
            try {
                EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png", "image/webp"});
                final Theme.ResourcesProvider resourcesProvider = this.val$resourcesProvider;
                InputConnectionCompat.OnCommitContentListener callback = new InputConnectionCompat.OnCommitContentListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda0
                    @Override // androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener
                    public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
                        return ChatActivityEnterView.AnonymousClass12.this.m2352x6ff224c9(resourcesProvider, inputContentInfoCompat, i, bundle);
                    }
                };
                return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
            } catch (Throwable e) {
                FileLog.e(e);
                return ic;
            }
        }

        /* renamed from: lambda$onCreateInputConnection$1$org-telegram-ui-Components-ChatActivityEnterView$12 */
        public /* synthetic */ boolean m2352x6ff224c9(Theme.ResourcesProvider resourcesProvider, final InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
            if (BuildCompat.isAtLeastNMR1() && (flags & 1) != 0) {
                try {
                    inputContentInfo.requestPermission();
                } catch (Exception e) {
                    return false;
                }
            }
            if (inputContentInfo.getDescription().hasMimeType("image/gif") || SendMessagesHelper.shouldSendWebPAsSticker(null, inputContentInfo.getContentUri())) {
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i) {
                            ChatActivityEnterView.AnonymousClass12.this.m2351xe2b77348(inputContentInfo, z, i);
                        }
                    }, resourcesProvider);
                } else {
                    m2351xe2b77348(inputContentInfo, true, 0);
                }
            } else {
                editPhoto(inputContentInfo.getContentUri(), inputContentInfo.getDescription().getMimeType(0));
            }
            return true;
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (ChatActivityEnterView.this.stickersDragging || ChatActivityEnterView.this.stickersExpansionAnim != null) {
                return false;
            }
            if (ChatActivityEnterView.this.isPopupShowing() && event.getAction() == 0) {
                if (ChatActivityEnterView.this.searchingType != 0) {
                    ChatActivityEnterView.this.setSearchingTypeInternal(0, false);
                    ChatActivityEnterView.this.emojiView.closeSearch(false);
                    requestFocus();
                }
                ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                if (!ChatActivityEnterView.this.stickersExpanded) {
                    ChatActivityEnterView.this.openKeyboardInternal();
                } else {
                    ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                    ChatActivityEnterView.this.waitingForKeyboardOpenAfterAnimation = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.AnonymousClass12.this.m2353xe9004f71();
                        }
                    }, 200L);
                }
                return true;
            }
            try {
                return super.onTouchEvent(event);
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }

        /* renamed from: lambda$onTouchEvent$2$org-telegram-ui-Components-ChatActivityEnterView$12 */
        public /* synthetic */ void m2353xe9004f71() {
            ChatActivityEnterView.this.waitingForKeyboardOpenAfterAnimation = false;
            ChatActivityEnterView.this.openKeyboardInternal();
        }

        @Override // android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (ChatActivityEnterView.this.preventInput) {
                return false;
            }
            return super.dispatchKeyEvent(event);
        }

        @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
        public void onSelectionChanged(int selStart, int selEnd) {
            super.onSelectionChanged(selStart, selEnd);
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onTextSelectionChanged(selStart, selEnd);
            }
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor
        protected void extendActionMode(ActionMode actionMode, Menu menu) {
            if (ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.parentFragment.extendActionMode(menu);
            }
        }

        @Override // android.view.View
        public boolean requestRectangleOnScreen(android.graphics.Rect rectangle) {
            rectangle.bottom += AndroidUtilities.dp(1000.0f);
            return super.requestRectangleOnScreen(rectangle);
        }

        @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            ChatActivityEnterView.this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (ChatActivityEnterView.this.isInitLineCount) {
                ChatActivityEnterView.this.lineCount = getLineCount();
            }
            ChatActivityEnterView.this.isInitLineCount = false;
        }

        @Override // android.widget.TextView
        public boolean onTextContextMenuItem(int id) {
            if (id == 16908322) {
                ChatActivityEnterView.this.isPaste = true;
            }
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService("clipboard");
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() == 1 && clipData.getDescription().hasMimeType("image/*")) {
                editPhoto(clipData.getItemAt(0).getUri(), clipData.getDescription().getMimeType(0));
            }
            return super.onTextContextMenuItem(id);
        }

        private void editPhoto(final Uri uri, String mime) {
            ChatActivity chatActivity = this.val$fragment;
            final File file = AndroidUtilities.generatePicturePath(chatActivity != null && chatActivity.isSecretChat(), MimeTypeMap.getSingleton().getExtensionFromMimeType(mime));
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            final Activity activity = this.val$context;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.AnonymousClass12.this.m2350xaed47d87(activity, uri, file);
                }
            });
        }

        /* renamed from: lambda$editPhoto$4$org-telegram-ui-Components-ChatActivityEnterView$12 */
        public /* synthetic */ void m2350xaed47d87(Activity context, Uri uri, final File file) {
            Throwable e;
            try {
                try {
                    InputStream in = context.getContentResolver().openInputStream(uri);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int lengthRead = in.read(buffer);
                        if (lengthRead > 0) {
                            fos.write(buffer, 0, lengthRead);
                            fos.flush();
                        } else {
                            in.close();
                            fos.close();
                            Object photoEntry = new MediaController.PhotoEntry(0, -1, 0L, file.getAbsolutePath(), 0, false, 0, 0, 0L);
                            final ArrayList<Object> entries = new ArrayList<>();
                            entries.add(photoEntry);
                            try {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$12$$ExternalSyntheticLambda3
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        ChatActivityEnterView.AnonymousClass12.this.m2349x2199cc06(entries, file);
                                    }
                                });
                                return;
                            } catch (Throwable th) {
                                e = th;
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                } catch (Throwable th2) {
                    e = th2;
                }
            } catch (Throwable th3) {
                e = th3;
            }
        }

        /* renamed from: openPhotoViewerForEdit */
        public void m2349x2199cc06(final ArrayList<Object> entries, final File sourceFile) {
            final MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entries.get(0);
            if (!ChatActivityEnterView.this.keyboardVisible) {
                PhotoViewer.getInstance().setParentActivity(ChatActivityEnterView.this.parentActivity, this.val$resourcesProvider);
                PhotoViewer.getInstance().openPhotoForSelect(entries, 0, 2, false, new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.Components.ChatActivityEnterView.12.2
                    boolean sending;

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
                        ArrayList<SendMessagesHelper.SendingMediaInfo> photos = new ArrayList<>();
                        SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                        if (!photoEntry.isVideo && photoEntry.imagePath != null) {
                            info.path = photoEntry.imagePath;
                        } else if (photoEntry.path != null) {
                            info.path = photoEntry.path;
                        }
                        info.thumbPath = photoEntry.thumbPath;
                        info.isVideo = photoEntry.isVideo;
                        info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                        info.entities = photoEntry.entities;
                        info.masks = photoEntry.stickers;
                        info.ttl = photoEntry.ttl;
                        info.videoEditedInfo = videoEditedInfo;
                        info.canDeleteAfter = true;
                        photos.add(info);
                        photoEntry.reset();
                        this.sending = true;
                        SendMessagesHelper.prepareSendingMedia(ChatActivityEnterView.this.accountInstance, photos, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, false, ChatActivityEnterView.this.editingMessageObject, notify, scheduleDate);
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend(null, true, scheduleDate);
                        }
                    }

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public void willHidePhotoViewer() {
                        if (!this.sending) {
                            try {
                                sourceFile.delete();
                            } catch (Throwable th) {
                            }
                        }
                    }

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public boolean canCaptureMorePhotos() {
                        return false;
                    }
                }, ChatActivityEnterView.this.parentFragment);
                return;
            }
            AndroidUtilities.hideKeyboard(ChatActivityEnterView.this.messageEditText);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.12.1
                @Override // java.lang.Runnable
                public void run() {
                    AnonymousClass12.this.m2349x2199cc06(entries, sourceFile);
                }
            }, 100L);
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor
        protected Theme.ResourcesProvider getResourcesProvider() {
            return this.val$resourcesProvider;
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2313lambda$new$2$orgtelegramuiComponentsChatActivityEnterView() {
        this.messageEditText.invalidateEffects();
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$15 */
    /* loaded from: classes5.dex */
    public class AnonymousClass15 implements TextWatcher {
        private boolean ignorePrevTextChange;
        private boolean nextChangeIsSend;
        private CharSequence prevText;
        private boolean processChange;

        AnonymousClass15() {
            ChatActivityEnterView.this = this$0;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!this.ignorePrevTextChange && ChatActivityEnterView.this.recordingAudioVideo) {
                this.prevText = charSequence.toString();
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!this.ignorePrevTextChange) {
                if (ChatActivityEnterView.this.lineCount != ChatActivityEnterView.this.messageEditText.getLineCount()) {
                    if (!ChatActivityEnterView.this.isInitLineCount && ChatActivityEnterView.this.messageEditText.getMeasuredWidth() > 0) {
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        chatActivityEnterView.onLineCountChanged(chatActivityEnterView.lineCount, ChatActivityEnterView.this.messageEditText.getLineCount());
                    }
                    ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                    chatActivityEnterView2.lineCount = chatActivityEnterView2.messageEditText.getLineCount();
                }
                if (ChatActivityEnterView.this.innerTextChange != 1) {
                    if (ChatActivityEnterView.this.sendByEnter && !ChatActivityEnterView.this.isPaste && ChatActivityEnterView.this.editingMessageObject == null && count > before && charSequence.length() > 0 && charSequence.length() == start + count && charSequence.charAt(charSequence.length() - 1) == '\n') {
                        this.nextChangeIsSend = true;
                    }
                    boolean z = false;
                    ChatActivityEnterView.this.isPaste = false;
                    ChatActivityEnterView.this.checkSendButton(true);
                    CharSequence message = AndroidUtilities.getTrimmedString(charSequence.toString());
                    if (ChatActivityEnterView.this.delegate != null && !ChatActivityEnterView.this.ignoreTextChange) {
                        if (before > count + 1 || count - before > 2 || TextUtils.isEmpty(charSequence)) {
                            ChatActivityEnterView.this.messageWebPageSearch = true;
                        }
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = ChatActivityEnterView.this.delegate;
                        if (before > count + 1 || count - before > 2) {
                            z = true;
                        }
                        chatActivityEnterViewDelegate.onTextChanged(charSequence, z);
                    }
                    if (ChatActivityEnterView.this.innerTextChange != 2 && count - before > 1) {
                        this.processChange = true;
                    }
                    if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && message.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS && !ChatActivityEnterView.this.ignoreTextChange) {
                        ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.needSendTyping();
                        }
                    }
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:42:0x015c  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x01b4  */
        @Override // android.text.TextWatcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void afterTextChanged(android.text.Editable r13) {
            /*
                Method dump skipped, instructions count: 449
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.AnonymousClass15.afterTextChanged(android.text.Editable):void");
        }

        /* renamed from: lambda$afterTextChanged$0$org-telegram-ui-Components-ChatActivityEnterView$15 */
        public /* synthetic */ void m2354xe529eea3(ValueAnimator valueAnimator) {
            int color = ChatActivityEnterView.this.getThemedColor(Theme.key_chat_messagePanelVoicePressed);
            int defaultAlpha = Color.alpha(color);
            ChatActivityEnterView.this.doneButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatActivityEnterView.this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (defaultAlpha * ((ChatActivityEnterView.this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
            ChatActivityEnterView.this.doneButtonImage.invalidate();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2322lambda$new$3$orgtelegramuiComponentsChatActivityEnterView(View v) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2325lambda$new$4$orgtelegramuiComponentsChatActivityEnterView(View view) {
        boolean open = !this.botCommandsMenuButton.isOpened();
        this.botCommandsMenuButton.setOpened(open);
        try {
            performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        if (hasBotWebView()) {
            if (open) {
                if (this.emojiViewVisible || this.botKeyboardViewVisible) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda40
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.this.openWebViewMenu();
                        }
                    }, 275L);
                    hidePopup(false);
                    return;
                }
                openWebViewMenu();
                return;
            }
            this.botWebViewMenuContainer.dismiss();
        } else if (open) {
            this.botCommandsMenuContainer.show();
        } else {
            this.botCommandsMenuContainer.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$17 */
    /* loaded from: classes5.dex */
    public class AnonymousClass17 implements RecyclerListView.OnItemClickListener {
        final /* synthetic */ ChatActivity val$fragment;
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass17(Theme.ResourcesProvider resourcesProvider, ChatActivity chatActivity) {
            ChatActivityEnterView.this = this$0;
            this.val$resourcesProvider = resourcesProvider;
            this.val$fragment = chatActivity;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
        public void onItemClick(View view, int position) {
            if (view instanceof BotCommandsMenuView.BotCommandView) {
                final String command = ((BotCommandsMenuView.BotCommandView) view).getCommand();
                if (TextUtils.isEmpty(command)) {
                    return;
                }
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$17$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i) {
                            ChatActivityEnterView.AnonymousClass17.this.m2355xdb888916(command, z, i);
                        }
                    }, this.val$resourcesProvider);
                    return;
                }
                ChatActivity chatActivity = this.val$fragment;
                if (chatActivity == null || !chatActivity.checkSlowMode(view)) {
                    SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(command, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, null, null, null, true, 0, null);
                    ChatActivityEnterView.this.setFieldText("");
                    ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
                }
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-Components-ChatActivityEnterView$17 */
        public /* synthetic */ void m2355xdb888916(String command, boolean notify, int scheduleDate) {
            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(command, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, null, null, null, notify, scheduleDate, null);
            ChatActivityEnterView.this.setFieldText("");
            ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2326lambda$new$5$orgtelegramuiComponentsChatActivityEnterView() {
        if (this.botButtonsMessageObject != null && TextUtils.isEmpty(this.messageEditText.getText()) && !this.botWebViewMenuContainer.hasSavedText()) {
            showPopup(1, 1);
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2327lambda$new$6$orgtelegramuiComponentsChatActivityEnterView(View v) {
        if (hasBotWebView() && botCommandsMenuIsShowing()) {
            BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
            v.getClass();
            botWebViewMenuContainer.dismiss(new ChatActivityEnterView$$ExternalSyntheticLambda26(v));
            return;
        }
        if (this.searchingType != 0) {
            setSearchingTypeInternal(0, false);
            this.emojiView.closeSearch(false);
            this.messageEditText.requestFocus();
        }
        if (this.botReplyMarkup != null) {
            if (!isPopupShowing() || this.currentPopupContentType != 1) {
                showPopup(1, 1);
            }
        } else if (this.hasBotCommands) {
            setFieldText("/");
            this.messageEditText.requestFocus();
            openKeyboard();
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, false, false);
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2328lambda$new$7$orgtelegramuiComponentsChatActivityEnterView(View v) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper != null && adjustPanLayoutHelper.animationInProgress()) {
            return;
        }
        this.delegate.didPressAttachButton();
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2310lambda$new$14$orgtelegramuiComponentsChatActivityEnterView(Activity context, View v) {
        int popupY;
        if (getTranslationY() != 0.0f) {
            this.onEmojiSearchClosed = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.m2329lambda$new$8$orgtelegramuiComponentsChatActivityEnterView();
                }
            };
            hidePopup(true, true);
            return;
        }
        if (this.delegate.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            int totalHeight = this.delegate.getContentViewHeight();
            int keyboard = this.delegate.measureKeyboardHeight();
            if (keyboard <= AndroidUtilities.dp(20.0f)) {
                totalHeight += keyboard;
            }
            if (this.emojiViewVisible) {
                totalHeight -= getEmojiPadding();
            }
            if (totalHeight < AndroidUtilities.dp(200.0f)) {
                this.onKeyboardClosed = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda34
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.m2330lambda$new$9$orgtelegramuiComponentsChatActivityEnterView();
                    }
                };
                closeKeyboard();
                return;
            }
        }
        if (this.delegate.getSendAsPeers() != null) {
            try {
                v.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
            int i = 0;
            if (senderSelectPopup != null) {
                senderSelectPopup.setPauseNotifications(false);
                this.senderSelectPopupWindow.startDismissAnimation(new SpringAnimation[0]);
                return;
            }
            final MessagesController controller = MessagesController.getInstance(this.currentAccount);
            final TLRPC.ChatFull chatFull = controller.getChatFull(-this.dialog_id);
            if (chatFull == null) {
                return;
            }
            final ViewGroup fl = this.parentFragment.getParentLayout();
            SenderSelectPopup senderSelectPopup2 = new SenderSelectPopup(context, this.parentFragment, controller, chatFull, this.delegate.getSendAsPeers(), new SenderSelectPopup.OnSelectCallback() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda54
                @Override // org.telegram.ui.Components.SenderSelectPopup.OnSelectCallback
                public final void onPeerSelected(RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC.Peer peer) {
                    ChatActivityEnterView.this.m2309lambda$new$13$orgtelegramuiComponentsChatActivityEnterView(chatFull, controller, recyclerView, senderView, peer);
                }
            }) { // from class: org.telegram.ui.Components.ChatActivityEnterView.21
                @Override // org.telegram.ui.Components.SenderSelectPopup, org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
                public void dismiss() {
                    if (ChatActivityEnterView.this.senderSelectPopupWindow == this) {
                        ChatActivityEnterView.this.senderSelectPopupWindow = null;
                        if (!this.runningCustomSprings) {
                            startDismissAnimation(new SpringAnimation[0]);
                            ChatActivityEnterView.this.senderSelectView.setProgress(0.0f, true, true);
                            return;
                        }
                        for (SpringAnimation springAnimation : this.springAnimations) {
                            springAnimation.cancel();
                        }
                        this.springAnimations.clear();
                        super.dismiss();
                        return;
                    }
                    fl.removeView(this.dimView);
                    super.dismiss();
                }
            };
            this.senderSelectPopupWindow = senderSelectPopup2;
            senderSelectPopup2.setPauseNotifications(true);
            this.senderSelectPopupWindow.setDismissAnimationDuration(220);
            this.senderSelectPopupWindow.setOutsideTouchable(true);
            this.senderSelectPopupWindow.setClippingEnabled(true);
            this.senderSelectPopupWindow.setFocusable(true);
            this.senderSelectPopupWindow.getContentView().measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.senderSelectPopupWindow.setInputMethodMode(2);
            this.senderSelectPopupWindow.setSoftInputMode(0);
            this.senderSelectPopupWindow.getContentView().setFocusableInTouchMode(true);
            this.senderSelectPopupWindow.setAnimationEnabled(false);
            int pad = -AndroidUtilities.dp(4.0f);
            int[] location = new int[2];
            int popupX = pad;
            if (AndroidUtilities.isTablet()) {
                this.parentFragment.getFragmentView().getLocationInWindow(location);
                popupX += location[0];
            }
            int totalHeight2 = this.delegate.getContentViewHeight();
            int height = this.senderSelectPopupWindow.getContentView().getMeasuredHeight();
            int keyboard2 = this.delegate.measureKeyboardHeight();
            if (keyboard2 <= AndroidUtilities.dp(20.0f)) {
                totalHeight2 += keyboard2;
            }
            if (this.emojiViewVisible) {
                totalHeight2 -= getEmojiPadding();
            }
            int shadowPad = AndroidUtilities.dp(1.0f);
            if (height >= (((pad * 2) + totalHeight2) - (this.parentFragment.isInBubbleMode() ? 0 : AndroidUtilities.statusBarHeight)) - this.senderSelectPopupWindow.headerText.getMeasuredHeight()) {
                if (!this.parentFragment.isInBubbleMode()) {
                    i = AndroidUtilities.statusBarHeight;
                }
                popupY = i;
                int off = AndroidUtilities.dp(14.0f);
                this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height = ((totalHeight2 - popupY) - off) - getHeightWithTopView();
                fl.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, off + popupY + this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height + shadowPad));
            } else {
                getLocationInWindow(location);
                popupY = ((location[1] - height) - pad) - AndroidUtilities.dp(2.0f);
                fl.addView(this.senderSelectPopupWindow.dimView, new FrameLayout.LayoutParams(-1, popupY + pad + height + shadowPad + AndroidUtilities.dp(2.0f)));
            }
            this.senderSelectPopupWindow.startShowAnimation();
            SenderSelectPopup senderSelectPopup3 = this.senderSelectPopupWindow;
            this.popupX = popupX;
            this.popupY = popupY;
            senderSelectPopup3.showAtLocation(v, 51, popupX, popupY);
            this.senderSelectView.setProgress(1.0f);
        }
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2329lambda$new$8$orgtelegramuiComponentsChatActivityEnterView() {
        this.senderSelectView.callOnClick();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2330lambda$new$9$orgtelegramuiComponentsChatActivityEnterView() {
        this.senderSelectView.callOnClick();
    }

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2309lambda$new$13$orgtelegramuiComponentsChatActivityEnterView(TLRPC.ChatFull chatFull, MessagesController controller, RecyclerView recyclerView, final SenderSelectPopup.SenderView senderView, TLRPC.Peer peer) {
        TLRPC.User user;
        if (this.senderSelectPopupWindow == null) {
            return;
        }
        if (chatFull != null) {
            chatFull.default_send_as = peer;
            updateSendAsButton();
        }
        this.parentFragment.getMessagesController().setDefaultSendAs(this.dialog_id, peer.user_id != 0 ? peer.user_id : -peer.channel_id);
        final int[] loc = new int[2];
        boolean wasSelected = senderView.avatar.isSelected();
        senderView.avatar.getLocationInWindow(loc);
        senderView.avatar.setSelected(true, true);
        final SimpleAvatarView avatar = new SimpleAvatarView(getContext());
        if (peer.channel_id != 0) {
            TLRPC.Chat chat = controller.getChat(Long.valueOf(peer.channel_id));
            if (chat != null) {
                avatar.setAvatar(chat);
            }
        } else if (peer.user_id != 0 && (user = controller.getUser(Long.valueOf(peer.user_id))) != null) {
            avatar.setAvatar(user);
        }
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View ch = recyclerView.getChildAt(i);
            if ((ch instanceof SenderSelectPopup.SenderView) && ch != senderView) {
                SenderSelectPopup.SenderView childSenderView = (SenderSelectPopup.SenderView) ch;
                childSenderView.avatar.setSelected(false, true);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivityEnterView.this.m2308lambda$new$12$orgtelegramuiComponentsChatActivityEnterView(avatar, loc, senderView);
            }
        }, wasSelected ? 0L : 200L);
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2308lambda$new$12$orgtelegramuiComponentsChatActivityEnterView(final SimpleAvatarView avatar, int[] loc, SenderSelectPopup.SenderView senderView) {
        if (this.senderSelectPopupWindow == null) {
            return;
        }
        final Dialog d = new Dialog(getContext(), R.style.TransparentDialogNoAnimation);
        FrameLayout aFrame = new FrameLayout(getContext());
        aFrame.addView(avatar, LayoutHelper.createFrame(40, 40, 3));
        d.setContentView(aFrame);
        d.getWindow().setLayout(-1, -1);
        if (Build.VERSION.SDK_INT >= 21) {
            d.getWindow().clearFlags(1024);
            d.getWindow().clearFlags(ConnectionsManager.FileTypeFile);
            d.getWindow().clearFlags(134217728);
            d.getWindow().addFlags(Integer.MIN_VALUE);
            d.getWindow().addFlags(512);
            d.getWindow().addFlags(131072);
            d.getWindow().getAttributes().windowAnimations = 0;
            d.getWindow().getDecorView().setSystemUiVisibility(1792);
            d.getWindow().setStatusBarColor(0);
            d.getWindow().setNavigationBarColor(0);
            int color = Theme.getColor(Theme.key_actionBarDefault, null, true);
            AndroidUtilities.setLightStatusBar(d.getWindow(), color == -1);
            if (Build.VERSION.SDK_INT >= 26) {
                int color2 = Theme.getColor(Theme.key_windowBackgroundGray, null, true);
                float brightness = AndroidUtilities.computePerceivedBrightness(color2);
                AndroidUtilities.setLightNavigationBar(d.getWindow(), brightness >= 0.721f);
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            WindowInsets wi = getRootWindowInsets();
            this.popupX += wi.getSystemWindowInsetLeft();
        }
        this.senderSelectView.getLocationInWindow(this.location);
        int[] iArr = this.location;
        final float endX = iArr[0];
        final float endY = iArr[1];
        float off = AndroidUtilities.dp(5.0f);
        float startX = loc[0] + this.popupX + off + AndroidUtilities.dp(4.0f) + 0.0f;
        float startY = loc[1] + this.popupY + off + 0.0f;
        avatar.setTranslationX(startX);
        avatar.setTranslationY(startY);
        float endScale = this.senderSelectView.getLayoutParams().width / AndroidUtilities.dp(40.0f);
        avatar.setPivotX(0.0f);
        avatar.setPivotY(0.0f);
        avatar.setScaleX(0.75f);
        avatar.setScaleY(0.75f);
        avatar.getViewTreeObserver().addOnDrawListener(new AnonymousClass22(avatar, senderView));
        d.show();
        this.senderSelectView.setScaleX(1.0f);
        this.senderSelectView.setScaleY(1.0f);
        this.senderSelectView.setAlpha(1.0f);
        this.senderSelectPopupWindow.startDismissAnimation(new SpringAnimation(this.senderSelectView, DynamicAnimation.SCALE_X).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.senderSelectView, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(0.5f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.senderSelectView, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda24
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ChatActivityEnterView.this.m2306lambda$new$10$orgtelegramuiComponentsChatActivityEnterView(d, avatar, endX, endY, dynamicAnimation, z, f, f2);
            }
        }), new SpringAnimation(avatar, DynamicAnimation.TRANSLATION_X).setStartValue(MathUtils.clamp(startX, endX - AndroidUtilities.dp(6.0f), startX)).setSpring(new SpringForce(endX).setStiffness(700.0f).setDampingRatio(0.75f)).setMinValue(endX - AndroidUtilities.dp(6.0f)), new SpringAnimation(avatar, DynamicAnimation.TRANSLATION_Y).setStartValue(MathUtils.clamp(startY, startY, AndroidUtilities.dp(6.0f) + endY)).setSpring(new SpringForce(endY).setStiffness(700.0f).setDampingRatio(0.75f)).setMaxValue(AndroidUtilities.dp(6.0f) + endY).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.24
            boolean performedHapticFeedback = false;

            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                if (!this.performedHapticFeedback && value >= endY) {
                    this.performedHapticFeedback = true;
                    try {
                        avatar.performHapticFeedback(3, 2);
                    } catch (Exception e) {
                    }
                }
            }
        }).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda25
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ChatActivityEnterView.this.m2307lambda$new$11$orgtelegramuiComponentsChatActivityEnterView(d, avatar, endX, endY, dynamicAnimation, z, f, f2);
            }
        }), new SpringAnimation(avatar, DynamicAnimation.SCALE_X).setSpring(new SpringForce(endScale).setStiffness(1000.0f).setDampingRatio(1.0f)), new SpringAnimation(avatar, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(endScale).setStiffness(1000.0f).setDampingRatio(1.0f)));
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$22 */
    /* loaded from: classes5.dex */
    public class AnonymousClass22 implements ViewTreeObserver.OnDrawListener {
        final /* synthetic */ SimpleAvatarView val$avatar;
        final /* synthetic */ SenderSelectPopup.SenderView val$senderView;

        AnonymousClass22(SimpleAvatarView simpleAvatarView, SenderSelectPopup.SenderView senderView) {
            ChatActivityEnterView.this = this$0;
            this.val$avatar = simpleAvatarView;
            this.val$senderView = senderView;
        }

        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            final SimpleAvatarView simpleAvatarView = this.val$avatar;
            final SenderSelectPopup.SenderView senderView = this.val$senderView;
            simpleAvatarView.post(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$22$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.AnonymousClass22.this.m2356xc0393b77(simpleAvatarView, senderView);
                }
            });
        }

        /* renamed from: lambda$onDraw$0$org-telegram-ui-Components-ChatActivityEnterView$22 */
        public /* synthetic */ void m2356xc0393b77(SimpleAvatarView avatar, SenderSelectPopup.SenderView senderView) {
            avatar.getViewTreeObserver().removeOnDrawListener(this);
            senderView.avatar.setHideAvatar(true);
        }
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2306lambda$new$10$orgtelegramuiComponentsChatActivityEnterView(final Dialog d, SimpleAvatarView avatar, float endX, float endY, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (d.isShowing()) {
            avatar.setTranslationX(endX);
            avatar.setTranslationY(endY);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.23
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView senderSelectView = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog = d;
                    dialog.getClass();
                    senderSelectView.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog), 100L);
                    return true;
                }
            });
        }
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2307lambda$new$11$orgtelegramuiComponentsChatActivityEnterView(final Dialog d, SimpleAvatarView avatar, float endX, float endY, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (d.isShowing()) {
            avatar.setTranslationX(endX);
            avatar.setTranslationY(endY);
            this.senderSelectView.setProgress(0.0f, false);
            this.senderSelectView.setScaleX(1.0f);
            this.senderSelectView.setScaleY(1.0f);
            this.senderSelectView.setAlpha(1.0f);
            this.senderSelectView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.25
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ChatActivityEnterView.this.senderSelectView.getViewTreeObserver().removeOnPreDrawListener(this);
                    SenderSelectView senderSelectView = ChatActivityEnterView.this.senderSelectView;
                    Dialog dialog = d;
                    dialog.getClass();
                    senderSelectView.postDelayed(new ChatActivityEnterView$23$$ExternalSyntheticLambda0(dialog), 100L);
                    return true;
                }
            });
        }
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2311lambda$new$15$orgtelegramuiComponentsChatActivityEnterView(View v) {
        AnimatorSet animatorSet = this.runningAnimationAudio;
        if (animatorSet != null && animatorSet.isRunning()) {
            return;
        }
        if (this.videoToSendMessageObject != null) {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2, true, 0);
        } else {
            MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
            if (playing != null && playing == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
        }
        if (this.audioToSendPath != null) {
            new File(this.audioToSendPath).delete();
        }
        hideRecordedAudioPanel(false);
        checkSendButton(true);
    }

    /* renamed from: lambda$new$16$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2312lambda$new$16$orgtelegramuiComponentsChatActivityEnterView(View v) {
        if (this.audioToSend == null) {
            return;
        }
        if (MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) && !MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.audioToSendMessageObject);
            this.playPauseDrawable.setIcon(0, true);
            this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
            return;
        }
        this.playPauseDrawable.setIcon(1, true);
        MediaController.getInstance().playMessage(this.audioToSendMessageObject);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
    }

    public static /* synthetic */ boolean lambda$new$17(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$24$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ boolean m2316lambda$new$24$orgtelegramuiComponentsChatActivityEnterView(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        TLRPC.Chat chat;
        int i = 3;
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                boolean z2 = this.hasRecordVideo;
                if (!z2 || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (z2 && this.videoSendButton.getTag() != null) {
                        this.delegate.needStartRecordVideo(1, true, 0);
                    } else {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), ChatActivityEnterView$$ExternalSyntheticLambda50.INSTANCE, ChatActivityEnterView$$ExternalSyntheticLambda45.INSTANCE, resourcesProvider);
                        }
                        MediaController mediaController = MediaController.getInstance();
                        if (!isInScheduleMode()) {
                            i = 1;
                        }
                        mediaController.stopRecording(i, true, 0);
                        this.delegate.needStartRecordAudio(0);
                    }
                    this.recordingAudioVideo = false;
                    this.messageTransitionIsRunning = false;
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda29
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.this.m2314lambda$new$20$orgtelegramuiComponentsChatActivityEnterView();
                        }
                    };
                    this.moveToSendStateRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 200L);
                }
                return false;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && (chat = chatActivity.getCurrentChat()) != null && !ChatObject.canSendMedia(chat)) {
                this.delegate.needShowMediaBanHint();
                return false;
            }
            if (this.hasRecordVideo) {
                this.calledRecordRunnable = false;
                this.recordAudioVideoRunnableStarted = true;
                AndroidUtilities.runOnUIThread(this.recordAudioVideoRunnable, 150L);
            } else {
                this.recordAudioVideoRunnable.run();
            }
            return true;
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 3 && this.recordingAudioVideo) {
                if (this.recordCircle.slideToCancelProgress >= 0.7f) {
                    this.recordCircle.sendButtonVisible = true;
                    startLockTransition();
                } else {
                    if (this.hasRecordVideo && this.videoSendButton.getTag() != null) {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    } else {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordInterface(5);
                }
                return false;
            } else if (this.recordCircle.isSendButtonVisible() || this.recordedAudioPanel.getVisibility() == 0) {
                if (this.recordAudioVideoRunnableStarted) {
                    AndroidUtilities.cancelRunOnUIThread(this.recordAudioVideoRunnable);
                }
                return false;
            } else {
                float dist = (motionEvent.getX() + this.audioVideoButtonContainer.getX()) - this.startedDraggingX;
                if ((dist / this.distCanMove) + 1.0f < 0.45d) {
                    if (this.hasRecordVideo && this.videoSendButton.getTag() != null) {
                        CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                        this.delegate.needStartRecordVideo(2, true, 0);
                    } else {
                        this.delegate.needStartRecordAudio(0);
                        MediaController.getInstance().stopRecording(0, false, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordInterface(5);
                } else if (this.recordAudioVideoRunnableStarted) {
                    AndroidUtilities.cancelRunOnUIThread(this.recordAudioVideoRunnable);
                    this.delegate.onSwitchRecordMode(this.videoSendButton.getTag() == null);
                    if (this.videoSendButton.getTag() == null) {
                        z = true;
                    }
                    setRecordVideoButtonVisible(z, true);
                    performHapticFeedback(3);
                    sendAccessibilityEvent(1);
                } else {
                    boolean z3 = this.hasRecordVideo;
                    if (!z3 || this.calledRecordRunnable) {
                        this.startedDraggingX = -1.0f;
                        if (z3 && this.videoSendButton.getTag() != null) {
                            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                            this.delegate.needStartRecordVideo(1, true, 0);
                        } else {
                            if (this.recordingAudioVideo && isInScheduleMode()) {
                                AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), ChatActivityEnterView$$ExternalSyntheticLambda51.INSTANCE, ChatActivityEnterView$$ExternalSyntheticLambda46.INSTANCE, resourcesProvider);
                            }
                            this.delegate.needStartRecordAudio(0);
                            MediaController mediaController2 = MediaController.getInstance();
                            if (!isInScheduleMode()) {
                                i = 1;
                            }
                            mediaController2.stopRecording(i, true, 0);
                        }
                        this.recordingAudioVideo = false;
                        this.messageTransitionIsRunning = false;
                        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda30
                            @Override // java.lang.Runnable
                            public final void run() {
                                ChatActivityEnterView.this.m2315lambda$new$23$orgtelegramuiComponentsChatActivityEnterView();
                            }
                        };
                        this.moveToSendStateRunnable = runnable2;
                        AndroidUtilities.runOnUIThread(runnable2, 500L);
                    }
                }
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.recordingAudioVideo) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (this.recordCircle.isSendButtonVisible()) {
                return false;
            }
            if (this.recordCircle.setLockTranslation(y) == 2) {
                startLockTransition();
                return false;
            }
            this.recordCircle.setMovingCords(x, y);
            if (this.startedDraggingX == -1.0f) {
                this.startedDraggingX = x;
                double measuredWidth = this.sizeNotifierLayout.getMeasuredWidth();
                Double.isNaN(measuredWidth);
                float f = (float) (measuredWidth * 0.35d);
                this.distCanMove = f;
                if (f > AndroidUtilities.dp(140.0f)) {
                    this.distCanMove = AndroidUtilities.dp(140.0f);
                }
            }
            float x2 = x + this.audioVideoButtonContainer.getX();
            float f2 = this.startedDraggingX;
            float dist2 = x2 - f2;
            float alpha = (dist2 / this.distCanMove) + 1.0f;
            if (f2 != -1.0f) {
                if (alpha > 1.0f) {
                    alpha = 1.0f;
                } else if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                this.slideText.setSlideX(alpha);
                this.recordCircle.setSlideToCancelProgress(alpha);
            }
            if (alpha == 0.0f) {
                if (this.hasRecordVideo && this.videoSendButton.getTag() != null) {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2, true, 0);
                } else {
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0, false, 0);
                }
                this.recordingAudioVideo = false;
                updateRecordInterface(5);
            }
            return true;
        } else {
            view.onTouchEvent(motionEvent);
            return true;
        }
    }

    /* renamed from: lambda$new$20$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2314lambda$new$20$orgtelegramuiComponentsChatActivityEnterView() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    /* renamed from: lambda$new$23$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2315lambda$new$23$orgtelegramuiComponentsChatActivityEnterView() {
        this.moveToSendStateRunnable = null;
        updateRecordInterface(1);
    }

    /* renamed from: lambda$new$25$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2317lambda$new$25$orgtelegramuiComponentsChatActivityEnterView(View view) {
        String text = this.messageEditText.getText().toString();
        int idx = text.indexOf(32);
        if (idx == -1 || idx == text.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(text.substring(0, idx + 1));
        }
    }

    /* renamed from: lambda$new$26$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2318lambda$new$26$orgtelegramuiComponentsChatActivityEnterView(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            AnimatorSet animatorSet = this.runningAnimationAudio;
            if ((animatorSet != null && animatorSet.isRunning()) || this.moveToSendStateRunnable != null) {
                return;
            }
            sendMessage();
        }
    }

    /* renamed from: lambda$new$27$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2319lambda$new$27$orgtelegramuiComponentsChatActivityEnterView(View v) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    /* renamed from: lambda$new$28$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ boolean m2320lambda$new$28$orgtelegramuiComponentsChatActivityEnterView(View v) {
        if (this.messageEditText.length() == 0) {
            return false;
        }
        return onSendLongClick(v);
    }

    /* renamed from: lambda$new$29$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2321lambda$new$29$orgtelegramuiComponentsChatActivityEnterView(View v) {
        EmojiView emojiView;
        if (this.expandStickersButton.getVisibility() == 0 && this.expandStickersButton.getAlpha() == 1.0f && !this.waitingForKeyboardOpen) {
            if (this.keyboardVisible && this.messageEditText.isFocused()) {
                return;
            }
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    setSearchingTypeInternal(0, true);
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                    if (this.emojiTabOpen) {
                        checkSendButton(true);
                    }
                } else if (!this.stickersDragging && (emojiView = this.emojiView) != null) {
                    emojiView.showSearchField(false);
                }
            } else if (!this.stickersDragging) {
                this.emojiView.showSearchField(true);
            }
            if (!this.stickersDragging) {
                setStickersExpanded(!this.stickersExpanded, true, false);
            }
        }
    }

    /* renamed from: lambda$new$30$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2323lambda$new$30$orgtelegramuiComponentsChatActivityEnterView(View view) {
        doneEditingMessage();
    }

    public void openWebViewMenu() {
        final Runnable onRequestWebView = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivityEnterView.this.m2336xc1606e8();
            }
        };
        if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            onRequestWebView.run();
        } else {
            new AlertDialog.Builder(this.parentFragment.getParentActivity()).setTitle(LocaleController.getString((int) R.string.BotOpenPageTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotOpenPageMessage, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialog_id)))))).setPositiveButton(LocaleController.getString((int) R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda59
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.m2337xfdbfad07(onRequestWebView, dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ChatActivityEnterView.this.m2338xef695326(dialogInterface);
                }
            }).show();
        }
    }

    /* renamed from: lambda$openWebViewMenu$31$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2336xc1606e8() {
        AndroidUtilities.hideKeyboard(this);
        if (AndroidUtilities.isTablet()) {
            BotWebViewSheet webViewSheet = new BotWebViewSheet(getContext(), this.parentFragment.getResourceProvider());
            webViewSheet.setParentActivity(this.parentActivity);
            int i = this.currentAccount;
            long j = this.dialog_id;
            webViewSheet.requestWebView(i, j, j, this.botMenuWebViewTitle, this.botMenuWebViewUrl, 2, 0, false);
            webViewSheet.show();
            this.botCommandsMenuButton.setOpened(false);
            return;
        }
        this.botWebViewMenuContainer.show(this.currentAccount, this.dialog_id, this.botMenuWebViewUrl);
    }

    /* renamed from: lambda$openWebViewMenu$32$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2337xfdbfad07(Runnable onRequestWebView, DialogInterface dialog, int which) {
        onRequestWebView.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, this.dialog_id, true);
    }

    /* renamed from: lambda$openWebViewMenu$33$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2338xef695326(DialogInterface dialog) {
        if (!SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id)) {
            this.botCommandsMenuButton.setOpened(false);
        }
    }

    public void setBotWebViewButtonOffsetX(float offset) {
        ImageView[] imageViewArr;
        for (ImageView imageView : this.emojiButton) {
            imageView.setTranslationX(offset);
        }
        this.messageEditText.setTranslationX(offset);
        this.attachButton.setTranslationX(offset);
        this.audioSendButton.setTranslationX(offset);
        this.videoSendButton.setTranslationX(offset);
        ImageView imageView2 = this.botButton;
        if (imageView2 != null) {
            imageView2.setTranslationX(offset);
        }
    }

    public void setComposeShadowAlpha(float alpha) {
        this.composeShadowAlpha = alpha;
        invalidate();
    }

    public ChatActivityBotWebViewButton getBotWebViewButton() {
        return this.botWebViewButton;
    }

    public ChatActivity getParentFragment() {
        return this.parentFragment;
    }

    public void checkBotMenu() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null) {
            boolean wasExpanded = botCommandsMenuView.expanded;
            this.botCommandsMenuButton.setExpanded(TextUtils.isEmpty(this.messageEditText.getText()) && !this.keyboardVisible && !this.waitingForKeyboardOpen && !isPopupShowing(), true);
            if (wasExpanded != this.botCommandsMenuButton.expanded) {
                beginDelayedTransition();
            }
        }
    }

    public void forceSmoothKeyboard(boolean smoothKeyboard) {
        ChatActivity chatActivity;
        this.smoothKeyboard = smoothKeyboard && SharedConfig.smoothKeyboard && !AndroidUtilities.isInMultiwindow && ((chatActivity = this.parentFragment) == null || !chatActivity.isInBubbleMode());
    }

    protected void onLineCountChanged(int oldLineCount, int newLineCount) {
    }

    private void startLockTransition() {
        AnimatorSet animatorSet = new AnimatorSet();
        performHapticFeedback(3, 2);
        RecordCircle recordCircle = this.recordCircle;
        ObjectAnimator translate = ObjectAnimator.ofFloat(recordCircle, "lockAnimatedTranslation", recordCircle.startTranslation);
        translate.setStartDelay(100L);
        translate.setDuration(350L);
        ObjectAnimator snap = ObjectAnimator.ofFloat(this.recordCircle, "snapAnimationProgress", 1.0f);
        snap.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        snap.setDuration(250L);
        SharedConfig.removeLockRecordAudioVideoHint();
        animatorSet.playTogether(snap, translate, ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", 1.0f).setDuration(200L), ObjectAnimator.ofFloat(this.slideText, "cancelToProgress", 1.0f));
        animatorSet.start();
    }

    public int getBackgroundTop() {
        int t = getTop();
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            return t + this.topView.getLayoutParams().height;
        }
        return t;
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean clip = child == this.topView || child == this.textFieldContainer;
        if (clip) {
            canvas.save();
            if (child == this.textFieldContainer) {
                int top = (int) (this.animatedTop + AndroidUtilities.dp(2.0f) + this.chatSearchExpandOffset);
                View view = this.topView;
                if (view != null && view.getVisibility() == 0) {
                    top += this.topView.getHeight();
                }
                canvas.clipRect(0, top, getMeasuredWidth(), getMeasuredHeight());
            } else {
                canvas.clipRect(0, this.animatedTop, getMeasuredWidth(), this.animatedTop + child.getLayoutParams().height + AndroidUtilities.dp(2.0f));
            }
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (clip) {
            canvas.restore();
        }
        return result;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int top = (int) (this.animatedTop + (Theme.chat_composeShadowDrawable.getIntrinsicHeight() * (1.0f - this.composeShadowAlpha)));
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            top = (int) (top + ((1.0f - this.topViewEnterProgress) * this.topView.getLayoutParams().height));
        }
        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + top;
        Theme.chat_composeShadowDrawable.setAlpha((int) (this.composeShadowAlpha * 255.0f));
        Theme.chat_composeShadowDrawable.setBounds(0, top, getMeasuredWidth(), bottom);
        Theme.chat_composeShadowDrawable.draw(canvas);
        int bottom2 = (int) (bottom + this.chatSearchExpandOffset);
        if (this.allowBlur) {
            this.backgroundPaint.setColor(getThemedColor(Theme.key_chat_messagePanelBackground));
            if (SharedConfig.chatBlurEnabled() && this.sizeNotifierLayout != null) {
                AndroidUtilities.rectTmp2.set(0, bottom2, getWidth(), getHeight());
                this.sizeNotifierLayout.drawBlurRect(canvas, getTop(), AndroidUtilities.rectTmp2, this.backgroundPaint, false);
                return;
            }
            canvas.drawRect(0.0f, bottom2, getWidth(), getHeight(), this.backgroundPaint);
            return;
        }
        canvas.drawRect(0.0f, bottom2, getWidth(), getHeight(), getThemedPaint(Theme.key_paint_chatComposeBackground));
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:5|(1:10)(1:9)|11|(8:13|(1:18)(1:17)|(1:25)(1:24)|26|(5:28|(1:30)(1:31)|32|(1:34)(1:35)|36)|(3:38|(1:40)(1:41)|42)|43|(1:45))|46|(4:48|(1:53)(1:52)|54|(5:56|58|62|59|61))|57|58|62|59|61) */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onSendLongClick(android.view.View r14) {
        /*
            Method dump skipped, instructions count: 443
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.onSendLongClick(android.view.View):boolean");
    }

    /* renamed from: lambda$onSendLongClick$34$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2332xf47f317c(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$35$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2333xe628d79b(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new ChatActivityEnterView$$ExternalSyntheticLambda48(this), this.resourcesProvider);
    }

    /* renamed from: lambda$onSendLongClick$36$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2334xd7d27dba(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendMessageInternal(false, 0);
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    private void setRecordVideoButtonVisible(boolean visible, boolean animated) {
        ImageView imageView = this.videoSendButton;
        if (imageView == null) {
            return;
        }
        imageView.setTag(visible ? 1 : null);
        AnimatorSet animatorSet = this.audioVideoButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.audioVideoButtonAnimation = null;
        }
        float f = 0.0f;
        float f2 = 0.1f;
        if (animated) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean isChannel = false;
            if (DialogObject.isChatDialog(this.dialog_id)) {
                TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
            }
            preferences.edit().putBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", visible).commit();
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.audioVideoButtonAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[6];
            ImageView imageView2 = this.videoSendButton;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = visible ? 1.0f : 0.1f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
            ImageView imageView3 = this.videoSendButton;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = visible ? 1.0f : 0.1f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
            ImageView imageView4 = this.videoSendButton;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = visible ? 1.0f : 0.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
            ImageView imageView5 = this.audioSendButton;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = visible ? 0.1f : 1.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(imageView5, property4, fArr4);
            ImageView imageView6 = this.audioSendButton;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (!visible) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            animatorArr[4] = ObjectAnimator.ofFloat(imageView6, property5, fArr5);
            ImageView imageView7 = this.audioSendButton;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!visible) {
                f = 1.0f;
            }
            fArr6[0] = f;
            animatorArr[5] = ObjectAnimator.ofFloat(imageView7, property6, fArr6);
            animatorSet2.playTogether(animatorArr);
            this.audioVideoButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.34
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ChatActivityEnterView.this.audioVideoButtonAnimation)) {
                        ChatActivityEnterView.this.audioVideoButtonAnimation = null;
                    }
                    (ChatActivityEnterView.this.videoSendButton.getTag() == null ? ChatActivityEnterView.this.audioSendButton : ChatActivityEnterView.this.videoSendButton).sendAccessibilityEvent(8);
                }
            });
            this.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
            this.audioVideoButtonAnimation.setDuration(150L);
            this.audioVideoButtonAnimation.start();
            return;
        }
        this.videoSendButton.setScaleX(visible ? 1.0f : 0.1f);
        this.videoSendButton.setScaleY(visible ? 1.0f : 0.1f);
        this.videoSendButton.setAlpha(visible ? 1.0f : 0.0f);
        this.audioSendButton.setScaleX(visible ? 0.1f : 1.0f);
        ImageView imageView8 = this.audioSendButton;
        if (!visible) {
            f2 = 1.0f;
        }
        imageView8.setScaleY(f2);
        ImageView imageView9 = this.audioSendButton;
        if (!visible) {
            f = 1.0f;
        }
        imageView9.setAlpha(f);
    }

    public boolean isRecordingAudioVideo() {
        AnimatorSet animatorSet;
        return this.recordingAudioVideo || ((animatorSet = this.runningAnimationAudio) != null && animatorSet.isRunning());
    }

    public boolean isRecordLocked() {
        return this.recordingAudioVideo && this.recordCircle.isSendButtonVisible();
    }

    public void cancelRecordingAudioVideo() {
        ImageView imageView;
        if (this.hasRecordVideo && (imageView = this.videoSendButton) != null && imageView.getTag() != null) {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(5, true, 0);
        } else {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordInterface(2);
    }

    public void showContextProgress(boolean show) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 == null) {
            return;
        }
        if (show) {
            closeProgressDrawable2.startAnimation();
        } else {
            closeProgressDrawable2.stopAnimation();
        }
    }

    public void setCaption(String caption) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setCaption(caption);
            checkSendButton(true);
        }
    }

    public void setSlowModeTimer(int time) {
        this.slowModeTimer = time;
        updateSlowModeText();
    }

    public CharSequence getSlowModeTimer() {
        if (this.slowModeTimer > 0) {
            return this.slowModeButton.getText();
        }
        return null;
    }

    public void updateSlowModeText() {
        int currentTime;
        boolean isUploading;
        int serverTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        AndroidUtilities.cancelRunOnUIThread(this.updateSlowModeRunnable);
        this.updateSlowModeRunnable = null;
        TLRPC.ChatFull chatFull = this.info;
        int i = 2147483646;
        if (chatFull == null || chatFull.slowmode_seconds == 0 || this.info.slowmode_next_send_date > serverTime || (!(isUploading = SendMessagesHelper.getInstance(this.currentAccount).isUploadingMessageIdDialog(this.dialog_id)) && !SendMessagesHelper.getInstance(this.currentAccount).isSendingMessageIdDialog(this.dialog_id))) {
            int i2 = this.slowModeTimer;
            if (i2 >= 2147483646) {
                currentTime = 0;
                if (this.info != null) {
                    this.accountInstance.getMessagesController().loadFullChat(this.info.id, 0, true);
                }
            } else {
                currentTime = i2 - serverTime;
            }
        } else {
            TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id));
            if (!ChatObject.hasAdminRights(chat)) {
                currentTime = this.info.slowmode_seconds;
                if (isUploading) {
                    i = Integer.MAX_VALUE;
                }
                this.slowModeTimer = i;
            } else {
                currentTime = 0;
            }
        }
        if (this.slowModeTimer != 0 && currentTime > 0) {
            this.slowModeButton.setText(AndroidUtilities.formatDurationNoHours(Math.max(1, currentTime), false));
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, false, simpleTextView.getText());
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.updateSlowModeText();
                }
            };
            this.updateSlowModeRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 100L);
        } else {
            this.slowModeTimer = 0;
        }
        if (!isInScheduleMode()) {
            checkSendButton(true);
        }
    }

    public void addTopView(View view, View lineView, int height) {
        if (view == null) {
            return;
        }
        this.topLineView = lineView;
        lineView.setVisibility(8);
        this.topLineView.setAlpha(0.0f);
        addView(this.topLineView, LayoutHelper.createFrame(-1, 1.0f, 51, 0.0f, height + 1, 0.0f, 0.0f));
        this.topView = view;
        view.setVisibility(8);
        this.topViewEnterProgress = 0.0f;
        this.topView.setTranslationY(height);
        addView(this.topView, 0, LayoutHelper.createFrame(-1, height, 51, 0.0f, 2.0f, 0.0f, 0.0f));
        this.needShowTopView = false;
    }

    public void setForceShowSendButton(boolean value, boolean animated) {
        this.forceShowSendButton = value;
        checkSendButton(animated);
    }

    public void setAllowStickersAndGifs(boolean value, boolean value2) {
        setAllowStickersAndGifs(value, value2, false);
    }

    public void setAllowStickersAndGifs(boolean value, boolean value2, boolean waitingForKeyboardOpen) {
        if ((this.allowStickers != value || this.allowGifs != value2) && this.emojiView != null) {
            if (!SharedConfig.smoothKeyboard) {
                if (this.emojiViewVisible) {
                    hidePopup(false);
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
            } else if (this.emojiViewVisible && !waitingForKeyboardOpen) {
                this.removeEmojiViewAfterAnimation = true;
                hidePopup(false);
            } else {
                if (waitingForKeyboardOpen) {
                    openKeyboardInternal();
                }
                this.sizeNotifierLayout.removeView(this.emojiView);
                this.emojiView = null;
            }
        }
        this.allowStickers = value;
        this.allowGifs = value2;
        setEmojiButtonImage(false, !this.isPaused);
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(code);
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
        this.emojiView.switchToGifRecent();
    }

    /* renamed from: lambda$new$37$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2324lambda$new$37$orgtelegramuiComponentsChatActivityEnterView(ValueAnimator animation) {
        if (this.topView != null) {
            float v = ((Float) animation.getAnimatedValue()).floatValue();
            this.topViewEnterProgress = v;
            View view = this.topView;
            view.setTranslationY(this.animatedTop + ((1.0f - v) * view.getLayoutParams().height));
            this.topLineView.setAlpha(v);
            this.topLineView.setTranslationY(this.animatedTop);
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && chatActivity.mentionContainer != null) {
                this.parentFragment.mentionContainer.setTranslationY((1.0f - v) * this.topView.getLayoutParams().height);
            }
        }
    }

    public void showTopView(boolean animated, boolean openKeyboard) {
        showTopView(animated, openKeyboard, false);
    }

    private void showTopView(boolean animated, boolean openKeyboard, boolean skipAwait) {
        if (this.topView == null || this.topViewShowed || getVisibility() != 0) {
            if (this.recordedAudioPanel.getVisibility() == 0) {
                return;
            }
            if (!this.forceShowSendButton || openKeyboard) {
                openKeyboard();
                return;
            }
            return;
        }
        boolean openKeyboardInternal = this.recordedAudioPanel.getVisibility() != 0 && (!this.forceShowSendButton || openKeyboard) && (this.botReplyMarkup == null || this.editingMessageObject != null);
        if (!skipAwait && animated && openKeyboardInternal && !this.keyboardVisible && !isPopupShowing()) {
            openKeyboard();
            Runnable runnable = this.showTopViewRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterView.this.m2346xbe36d130();
                }
            };
            this.showTopViewRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 200L);
            return;
        }
        this.needShowTopView = true;
        this.topViewShowed = true;
        if (this.allowShowTopView) {
            this.topView.setVisibility(0);
            this.topLineView.setVisibility(0);
            ValueAnimator valueAnimator = this.currentTopViewAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.currentTopViewAnimation = null;
            }
            resizeForTopView(true);
            if (animated) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.topViewEnterProgress, 1.0f);
                this.currentTopViewAnimation = ofFloat;
                ofFloat.addUpdateListener(this.topViewUpdateListener);
                this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.35
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                            ChatActivityEnterView.this.currentTopViewAnimation = null;
                        }
                        NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.mentionContainer != null) {
                            ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                        }
                    }
                });
                this.currentTopViewAnimation.setDuration(270L);
                this.currentTopViewAnimation.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                this.currentTopViewAnimation.start();
                this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
            } else {
                this.topViewEnterProgress = 1.0f;
                this.topView.setTranslationY(0.0f);
                this.topLineView.setAlpha(1.0f);
            }
            if (openKeyboardInternal) {
                this.messageEditText.requestFocus();
                openKeyboard();
            }
        }
    }

    /* renamed from: lambda$showTopView$38$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2346xbe36d130() {
        showTopView(true, false, true);
        this.showTopViewRunnable = null;
    }

    public void onEditTimeExpired() {
        this.doneButtonContainer.setVisibility(8);
    }

    public void showEditDoneProgress(final boolean show, boolean animated) {
        AnimatorSet animatorSet = this.doneButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animated) {
            this.doneButtonAnimation = new AnimatorSet();
            if (show) {
                this.doneButtonProgress.setVisibility(0);
                this.doneButtonContainer.setEnabled(false);
                this.doneButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, 1.0f));
            } else {
                this.doneButtonImage.setVisibility(0);
                this.doneButtonContainer.setEnabled(true);
                this.doneButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, 1.0f));
            }
            this.doneButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.36
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        if (!show) {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        ChatActivityEnterView.this.doneButtonAnimation = null;
                    }
                }
            });
            this.doneButtonAnimation.setDuration(150L);
            this.doneButtonAnimation.start();
        } else if (show) {
            this.doneButtonImage.setScaleX(0.1f);
            this.doneButtonImage.setScaleY(0.1f);
            this.doneButtonImage.setAlpha(0.0f);
            this.doneButtonProgress.setScaleX(1.0f);
            this.doneButtonProgress.setScaleY(1.0f);
            this.doneButtonProgress.setAlpha(1.0f);
            this.doneButtonImage.setVisibility(4);
            this.doneButtonProgress.setVisibility(0);
            this.doneButtonContainer.setEnabled(false);
        } else {
            this.doneButtonProgress.setScaleX(0.1f);
            this.doneButtonProgress.setScaleY(0.1f);
            this.doneButtonProgress.setAlpha(0.0f);
            this.doneButtonImage.setScaleX(1.0f);
            this.doneButtonImage.setScaleY(1.0f);
            this.doneButtonImage.setAlpha(1.0f);
            this.doneButtonImage.setVisibility(0);
            this.doneButtonProgress.setVisibility(4);
            this.doneButtonContainer.setEnabled(true);
        }
    }

    public void hideTopView(boolean animated) {
        if (this.topView == null || !this.topViewShowed) {
            return;
        }
        Runnable runnable = this.showTopViewRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.topViewShowed = false;
        this.needShowTopView = false;
        if (this.allowShowTopView) {
            ValueAnimator valueAnimator = this.currentTopViewAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.currentTopViewAnimation = null;
            }
            if (animated) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.topViewEnterProgress, 0.0f);
                this.currentTopViewAnimation = ofFloat;
                ofFloat.addUpdateListener(this.topViewUpdateListener);
                this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.37
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                            ChatActivityEnterView.this.topView.setVisibility(8);
                            ChatActivityEnterView.this.topLineView.setVisibility(8);
                            ChatActivityEnterView.this.resizeForTopView(false);
                            ChatActivityEnterView.this.currentTopViewAnimation = null;
                        }
                        if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.mentionContainer != null) {
                            ChatActivityEnterView.this.parentFragment.mentionContainer.setTranslationY(0.0f);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                            ChatActivityEnterView.this.currentTopViewAnimation = null;
                        }
                    }
                });
                this.currentTopViewAnimation.setDuration(250L);
                this.currentTopViewAnimation.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                this.currentTopViewAnimation.start();
                return;
            }
            this.topViewEnterProgress = 0.0f;
            this.topView.setVisibility(8);
            this.topLineView.setVisibility(8);
            this.topLineView.setAlpha(0.0f);
            resizeForTopView(false);
            View view = this.topView;
            view.setTranslationY(view.getLayoutParams().height);
        }
    }

    public boolean isTopViewVisible() {
        View view = this.topView;
        return view != null && view.getVisibility() == 0;
    }

    public void onAdjustPanTransitionUpdate(float y, float progress, boolean keyboardVisible) {
        this.botWebViewMenuContainer.setTranslationY(y);
    }

    public void onAdjustPanTransitionEnd() {
        this.botWebViewMenuContainer.onPanTransitionEnd();
        Runnable runnable = this.onKeyboardClosed;
        if (runnable != null) {
            runnable.run();
            this.onKeyboardClosed = null;
        }
    }

    public void onAdjustPanTransitionStart(boolean keyboardVisible, int contentHeight) {
        Runnable runnable;
        this.botWebViewMenuContainer.onPanTransitionStart(keyboardVisible, contentHeight);
        if (keyboardVisible && (runnable = this.showTopViewRunnable) != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showTopViewRunnable.run();
        }
        Runnable runnable2 = this.setTextFieldRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.setTextFieldRunnable.run();
        }
        if (keyboardVisible && this.messageEditText.hasFocus() && hasBotWebView() && botCommandsMenuIsShowing()) {
            this.botWebViewMenuContainer.dismiss();
        }
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onWindowSizeChanged(size);
        }
        if (this.topView != null) {
            if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
                if (this.allowShowTopView) {
                    this.allowShowTopView = false;
                    if (this.needShowTopView) {
                        this.topView.setVisibility(8);
                        this.topLineView.setVisibility(8);
                        this.topLineView.setAlpha(0.0f);
                        resizeForTopView(false);
                        this.topViewEnterProgress = 0.0f;
                        View view = this.topView;
                        view.setTranslationY(view.getLayoutParams().height);
                    }
                }
            } else if (!this.allowShowTopView) {
                this.allowShowTopView = true;
                if (this.needShowTopView) {
                    this.topView.setVisibility(0);
                    this.topLineView.setVisibility(0);
                    this.topLineView.setAlpha(1.0f);
                    resizeForTopView(true);
                    this.topViewEnterProgress = 1.0f;
                    this.topView.setTranslationY(0.0f);
                }
            }
        }
    }

    public void resizeForTopView(boolean show) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (show ? this.topView.getLayoutParams().height : 0);
        this.textFieldContainer.setLayoutParams(layoutParams);
        setMinimumHeight(AndroidUtilities.dp(51.0f) + (show ? this.topView.getLayoutParams().height : 0));
        if (this.stickersExpanded) {
            if (this.searchingType == 0) {
                setStickersExpanded(false, true, false);
            } else {
                checkStickresExpandHeight();
            }
        }
    }

    public void onDestroy() {
        this.destroyed = true;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateBotMenuButton);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        Runnable runnable = this.updateSlowModeRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateSlowModeRunnable = null;
        }
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null) {
            try {
                wakeLock.release();
                this.wakeLock = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate(null);
        }
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
    }

    public void checkChannelRights() {
        TLRPC.Chat chat;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && (chat = chatActivity.getCurrentChat()) != null) {
            this.audioVideoButtonContainer.setAlpha(ChatObject.canSendMedia(chat) ? 1.0f : 0.5f);
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.setStickersBanned(!ChatObject.canSendStickers(chat), chat.id);
            }
        }
    }

    public void onBeginHide() {
        Runnable runnable = this.focusRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.focusRunnable = null;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
    }

    public void onPause() {
        this.isPaused = true;
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
        if (this.keyboardVisible) {
            this.showKeyboardOnResume = true;
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivityEnterView.this.m2331x3ecdff49();
            }
        };
        this.hideKeyboardRunnable = runnable;
        AndroidUtilities.runOnUIThread(runnable, 500L);
    }

    /* renamed from: lambda$onPause$39$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2331x3ecdff49() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || chatActivity.isLastFragment()) {
            closeKeyboard();
        }
        this.hideKeyboardRunnable = null;
    }

    public void onResume() {
        this.isPaused = false;
        Runnable runnable = this.hideKeyboardRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideKeyboardRunnable = null;
        }
        if (hasBotWebView() && botCommandsMenuIsShowing()) {
            return;
        }
        getVisibility();
        if (this.showKeyboardOnResume && this.parentFragment.isLastFragment()) {
            this.showKeyboardOnResume = false;
            if (this.searchingType == 0) {
                this.messageEditText.requestFocus();
            }
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
            }
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.messageEditText.setEnabled(visibility == 0);
    }

    public void setDialogId(long id, int account) {
        this.dialog_id = id;
        int i = this.currentAccount;
        if (i != account) {
            NotificationCenter.getInstance(i).onAnimationFinish(this.notificationsIndex);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
            this.currentAccount = account;
            this.accountInstance = AccountInstance.getInstance(account);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.sendingMessagesChanged);
        }
        updateScheduleButton(false);
        checkRoundVideo();
        updateFieldHint(false);
        updateSendAsButton(false);
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.setChatInfo(chatInfo);
        }
        setSlowModeTimer(chatInfo.slowmode_next_send_date);
    }

    public void checkRoundVideo() {
        if (this.hasRecordVideo) {
            return;
        }
        if (this.attachLayout == null || Build.VERSION.SDK_INT < 18) {
            this.hasRecordVideo = false;
            setRecordVideoButtonVisible(false, false);
            return;
        }
        boolean z = true;
        this.hasRecordVideo = true;
        boolean isChannel = false;
        if (DialogObject.isChatDialog(this.dialog_id)) {
            TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                z = false;
            }
            isChannel = z;
            if (isChannel && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages)) {
                this.hasRecordVideo = false;
            }
        }
        if (!SharedConfig.inappCamera) {
            this.hasRecordVideo = false;
        }
        if (this.hasRecordVideo) {
            if (SharedConfig.hasCameraCache) {
                CameraController.getInstance().initCamera(null);
            }
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean currentModeVideo = preferences.getBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", isChannel);
            setRecordVideoButtonVisible(currentModeVideo, false);
            return;
        }
        setRecordVideoButtonVisible(false, false);
    }

    public boolean isInVideoMode() {
        ImageView imageView = this.videoSendButton;
        return (imageView == null || imageView.getTag() == null) ? false : true;
    }

    public boolean hasRecordVideo() {
        return this.hasRecordVideo;
    }

    public MessageObject getReplyingMessageObject() {
        return this.replyingMessageObject;
    }

    public void updateFieldHint(boolean animated) {
        MessageObject messageObject;
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && messageObject2.messageOwner.reply_markup != null && !TextUtils.isEmpty(this.replyingMessageObject.messageOwner.reply_markup.placeholder)) {
            this.messageEditText.setHintText(this.replyingMessageObject.messageOwner.reply_markup.placeholder, animated);
            return;
        }
        MessageObject messageObject3 = this.editingMessageObject;
        int i = R.string.TypeMessage;
        String str = "TypeMessage";
        if (messageObject3 != null) {
            EditTextCaption editTextCaption = this.messageEditText;
            if (this.editingCaption) {
                i = R.string.Caption;
                str = "Caption";
            }
            editTextCaption.setHintText(LocaleController.getString(str, i));
        } else if (this.botKeyboardViewVisible && (messageObject = this.botButtonsMessageObject) != null && messageObject.messageOwner.reply_markup != null && !TextUtils.isEmpty(this.botButtonsMessageObject.messageOwner.reply_markup.placeholder)) {
            this.messageEditText.setHintText(this.botButtonsMessageObject.messageOwner.reply_markup.placeholder, animated);
        } else {
            boolean isChannel = false;
            boolean anonymously = false;
            if (DialogObject.isChatDialog(this.dialog_id)) {
                TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(-this.dialog_id);
                boolean z = true;
                isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                if (ChatObject.getSendAsPeerId(chat, chatFull) != chat.id) {
                    z = false;
                }
                anonymously = z;
            }
            if (anonymously) {
                this.messageEditText.setHintText(LocaleController.getString("SendAnonymously", R.string.SendAnonymously));
                return;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && chatActivity.isThreadChat()) {
                if (this.parentFragment.isReplyChatComment()) {
                    this.messageEditText.setHintText(LocaleController.getString("Comment", R.string.Comment));
                } else {
                    this.messageEditText.setHintText(LocaleController.getString("Reply", R.string.Reply));
                }
            } else if (isChannel) {
                if (this.silent) {
                    this.messageEditText.setHintText(LocaleController.getString("ChannelSilentBroadcast", R.string.ChannelSilentBroadcast), animated);
                } else {
                    this.messageEditText.setHintText(LocaleController.getString("ChannelBroadcast", R.string.ChannelBroadcast), animated);
                }
            } else {
                this.messageEditText.setHintText(LocaleController.getString(str, R.string.TypeMessage));
            }
        }
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        MessageObject messageObject2;
        if (messageObject != null) {
            if (this.botMessageObject == null && (messageObject2 = this.botButtonsMessageObject) != this.replyingMessageObject) {
                this.botMessageObject = messageObject2;
            }
            this.replyingMessageObject = messageObject;
            setButtons(messageObject, true);
        } else if (this.replyingMessageObject == this.botButtonsMessageObject) {
            this.replyingMessageObject = null;
            setButtons(this.botMessageObject, false);
            this.botMessageObject = null;
        } else {
            this.replyingMessageObject = null;
        }
        MediaController.getInstance().setReplyingMessage(messageObject, getThreadMessage());
        updateFieldHint(false);
    }

    public void setWebPage(TLRPC.WebPage webPage, boolean searchWebPages) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = searchWebPages;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    private void hideRecordedAudioPanel(boolean wasSent) {
        AnimatorSet attachIconAnimator;
        AnimatorSet animatorSet = this.recordPannelAnimation;
        if (animatorSet == null || !animatorSet.isRunning()) {
            this.audioToSendPath = null;
            this.audioToSend = null;
            this.audioToSendMessageObject = null;
            this.videoToSendMessageObject = null;
            this.videoTimelineView.destroy();
            if (this.videoSendButton != null && isInVideoMode()) {
                this.videoSendButton.setVisibility(0);
            } else {
                ImageView imageView = this.audioSendButton;
                if (imageView != null) {
                    imageView.setVisibility(0);
                }
            }
            if (wasSent) {
                this.attachButton.setAlpha(0.0f);
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.attachButton.setScaleX(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.attachButton.setScaleY(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, 0.0f));
                BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
                if (botCommandsMenuView != null) {
                    botCommandsMenuView.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    this.recordPannelAnimation.playTogether(ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, 1.0f));
                }
                this.recordPannelAnimation.setDuration(150L);
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.38
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                    }
                });
            } else {
                this.recordDeleteImageView.playAnimation();
                AnimatorSet exitAnimation = new AnimatorSet();
                if (isInVideoMode()) {
                    exitAnimation.playTogether(ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, 0.0f));
                } else {
                    this.messageEditText.setAlpha(1.0f);
                    this.messageEditText.setTranslationX(0.0f);
                    exitAnimation.playTogether(ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioSeekBar, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.recordedAudioPlayButton, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.recordedAudioBackground, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)));
                }
                exitAnimation.setDuration(200L);
                ImageView imageView2 = this.attachButton;
                if (imageView2 != null) {
                    imageView2.setAlpha(0.0f);
                    this.attachButton.setScaleX(0.0f);
                    this.attachButton.setScaleY(0.0f);
                    attachIconAnimator = new AnimatorSet();
                    attachIconAnimator.playTogether(ObjectAnimator.ofFloat(this.attachButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, 1.0f));
                    attachIconAnimator.setDuration(150L);
                } else {
                    attachIconAnimator = null;
                }
                this.emojiButton[0].setAlpha(0.0f);
                this.emojiButton[1].setAlpha(0.0f);
                this.emojiButton[0].setScaleX(0.0f);
                this.emojiButton[1].setScaleX(0.0f);
                this.emojiButton[0].setScaleY(0.0f);
                this.emojiButton[1].setScaleY(0.0f);
                AnimatorSet iconsEndAnimator = new AnimatorSet();
                iconsEndAnimator.playTogether(ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 1.0f));
                BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                if (botCommandsMenuView2 != null) {
                    botCommandsMenuView2.setAlpha(0.0f);
                    this.botCommandsMenuButton.setScaleY(0.0f);
                    this.botCommandsMenuButton.setScaleX(0.0f);
                    iconsEndAnimator.playTogether(ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, 1.0f));
                }
                iconsEndAnimator.setDuration(150L);
                iconsEndAnimator.setStartDelay(600L);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.recordPannelAnimation = animatorSet3;
                if (attachIconAnimator != null) {
                    animatorSet3.playTogether(exitAnimation, attachIconAnimator, iconsEndAnimator);
                } else {
                    animatorSet3.playTogether(exitAnimation, iconsEndAnimator);
                }
                this.recordPannelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.39
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ChatActivityEnterView.this.recordedAudioSeekBar.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioSeekBar.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordedAudioPlayButton.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioPlayButton.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioBackground.setTranslationX(0.0f);
                        ChatActivityEnterView.this.recordedAudioTimeTextView.setAlpha(1.0f);
                        ChatActivityEnterView.this.recordedAudioTimeTextView.setTranslationX(0.0f);
                        ChatActivityEnterView.this.videoTimelineView.setAlpha(1.0f);
                        ChatActivityEnterView.this.videoTimelineView.setTranslationX(0.0f);
                        ChatActivityEnterView.this.messageEditText.setAlpha(1.0f);
                        ChatActivityEnterView.this.messageEditText.setTranslationX(0.0f);
                        ChatActivityEnterView.this.messageEditText.requestFocus();
                        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
                    }
                });
            }
            this.recordPannelAnimation.start();
        }
    }

    public void sendMessage() {
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new ChatActivityEnterView$$ExternalSyntheticLambda48(this), this.resourcesProvider);
        } else {
            sendMessageInternal(true, 0);
        }
    }

    public void sendMessageInternal(final boolean notify, final int scheduleDate) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        TLRPC.Chat chat;
        EmojiView emojiView;
        if (this.slowModeTimer == Integer.MAX_VALUE && !isInScheduleMode()) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null) {
                chatActivityEnterViewDelegate2.scrollToSendingMessage();
                return;
            }
            return;
        }
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC.Chat chat2 = chatActivity.getCurrentChat();
            TLRPC.User user = this.parentFragment.getCurrentUser();
            if (user != null || ((ChatObject.isChannel(chat2) && chat2.megagroup) || !ChatObject.isChannel(chat2))) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit.putBoolean("silent_" + this.dialog_id, !notify).commit();
            }
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, true, false);
            if (this.searchingType != 0 && (emojiView = this.emojiView) != null) {
                emojiView.closeSearch(false);
                this.emojiView.hideSearchKeyboard();
            }
        }
        if (this.videoToSendMessageObject != null) {
            this.delegate.needStartRecordVideo(4, notify, scheduleDate);
            hideRecordedAudioPanel(true);
            checkSendButton(true);
        } else if (this.audioToSend == null) {
            final CharSequence message = this.messageEditText.getText();
            ChatActivity chatActivity2 = this.parentFragment;
            if (chatActivity2 != null && (chat = chatActivity2.getCurrentChat()) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
                if (message.length() > this.accountInstance.getMessagesController().maxMessageLength) {
                    AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendErrorTooLong", R.string.SlowmodeSendErrorTooLong), this.resourcesProvider);
                    return;
                } else if (this.forceShowSendButton && message.length() > 0) {
                    AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError), this.resourcesProvider);
                    return;
                }
            }
            if (processSendingText(message, notify, scheduleDate)) {
                if (this.delegate.hasForwardingMessages() || ((scheduleDate != 0 && !isInScheduleMode()) || isInScheduleMode())) {
                    this.messageEditText.setText("");
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                    if (chatActivityEnterViewDelegate3 != null) {
                        chatActivityEnterViewDelegate3.onMessageSend(message, notify, scheduleDate);
                    }
                } else {
                    this.messageTransitionIsRunning = false;
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda42
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatActivityEnterView.this.m2339xa32fd0d8(message, notify, scheduleDate);
                        }
                    };
                    this.moveToSendStateRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 200L);
                }
                this.lastTypingTimeSend = 0L;
            } else if (this.forceShowSendButton && (chatActivityEnterViewDelegate = this.delegate) != null) {
                chatActivityEnterViewDelegate.onMessageSend(null, notify, scheduleDate);
            }
        } else {
            MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
            if (playing != null && playing == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, null, null, null, notify, scheduleDate, 0, null, null);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
            if (chatActivityEnterViewDelegate4 != null) {
                chatActivityEnterViewDelegate4.onMessageSend(null, notify, scheduleDate);
            }
            hideRecordedAudioPanel(true);
            checkSendButton(true);
        }
    }

    /* renamed from: lambda$sendMessageInternal$40$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2339xa32fd0d8(CharSequence message, boolean notify, int scheduleDate) {
        this.moveToSendStateRunnable = null;
        hideTopView(true);
        this.messageEditText.setText("");
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(message, notify, scheduleDate);
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject == null) {
            return;
        }
        if (this.currentLimit - this.codePointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator v = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200L);
                return;
            }
            return;
        }
        if (this.searchingType != 0) {
            setSearchingTypeInternal(0, true);
            this.emojiView.closeSearch(false);
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                this.waitingForKeyboardOpenAfterAnimation = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda27
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.m2303xb8852e06();
                    }
                }, 200L);
            }
        }
        CharSequence[] message = {AndroidUtilities.getTrimmedString(this.messageEditText.getText())};
        ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(message, supportsSendingNewEntities());
        if (!TextUtils.equals(message[0], this.editingMessageObject.messageText) || ((entities != null && !entities.isEmpty()) || (((entities == null || entities.isEmpty()) && !this.editingMessageObject.messageOwner.entities.isEmpty()) || (this.editingMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))) {
            this.editingMessageObject.editingMessage = message[0];
            this.editingMessageObject.editingMessageEntities = entities;
            this.editingMessageObject.editingMessageSearchWebPage = this.messageWebPageSearch;
            SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, null, null, null, null, null, false, null);
        }
        setEditingMessageObject(null, false);
    }

    /* renamed from: lambda$doneEditingMessage$41$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2303xb8852e06() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    public boolean processSendingText(CharSequence text, boolean notify, int scheduleDate) {
        int enterIndex;
        int tabIndex;
        int dotIndex;
        int whitespaceIndex;
        int end;
        MessageObject.SendAnimationData sendAnimationData;
        ChatActivity chatActivity;
        CharSequence text2 = AndroidUtilities.getTrimmedString(text);
        boolean supportsNewEntities = supportsSendingNewEntities();
        int maxLength = this.accountInstance.getMessagesController().maxMessageLength;
        int end2 = 0;
        if (text2.length() != 0) {
            if (this.delegate != null && (chatActivity = this.parentFragment) != null) {
                if ((scheduleDate != 0) == chatActivity.isInScheduleMode()) {
                    this.delegate.prepareMessageSending();
                }
            }
            int start = 0;
            while (true) {
                int whitespaceIndex2 = -1;
                int dotIndex2 = -1;
                int enterIndex2 = -1;
                if (text2.length() <= start + maxLength) {
                    whitespaceIndex = -1;
                    dotIndex = -1;
                    tabIndex = -1;
                    enterIndex = -1;
                } else {
                    int i = (start + maxLength) - 1;
                    for (int k = 0; i > start && k < 300; k++) {
                        char c = text2.charAt(i);
                        char c2 = i > 0 ? text2.charAt(i - 1) : ' ';
                        if (c == '\n' && c2 == '\n') {
                            whitespaceIndex = whitespaceIndex2;
                            dotIndex = dotIndex2;
                            tabIndex = i;
                            enterIndex = enterIndex2;
                            break;
                        }
                        if (c == '\n') {
                            enterIndex2 = i;
                        } else if (dotIndex2 < 0 && Character.isWhitespace(c) && c2 == '.') {
                            dotIndex2 = i;
                        } else if (whitespaceIndex2 < 0 && Character.isWhitespace(c)) {
                            whitespaceIndex2 = i;
                        }
                        i--;
                    }
                    whitespaceIndex = whitespaceIndex2;
                    dotIndex = dotIndex2;
                    tabIndex = -1;
                    enterIndex = enterIndex2;
                }
                int whitespaceIndex3 = start + maxLength;
                int end3 = Math.min(whitespaceIndex3, text2.length());
                if (tabIndex > 0) {
                    int end4 = tabIndex;
                    end = end4;
                } else if (enterIndex > 0) {
                    int end5 = enterIndex;
                    end = end5;
                } else if (dotIndex > 0) {
                    int end6 = dotIndex;
                    end = end6;
                } else if (whitespaceIndex <= 0) {
                    end = end3;
                } else {
                    int end7 = whitespaceIndex;
                    end = end7;
                }
                CharSequence[] message = new CharSequence[1];
                message[end2] = AndroidUtilities.getTrimmedString(text2.subSequence(start, end));
                ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(message, supportsNewEntities);
                if (this.delegate.hasForwardingMessages()) {
                    sendAnimationData = null;
                } else {
                    MessageObject.SendAnimationData sendAnimationData2 = new MessageObject.SendAnimationData();
                    float dp = AndroidUtilities.dp(22.0f);
                    sendAnimationData2.height = dp;
                    sendAnimationData2.width = dp;
                    this.messageEditText.getLocationInWindow(this.location);
                    sendAnimationData2.x = this.location[end2] + AndroidUtilities.dp(11.0f);
                    sendAnimationData2.y = this.location[1] + AndroidUtilities.dp(19.0f);
                    sendAnimationData = sendAnimationData2;
                }
                int end8 = end;
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message[end2].toString(), this.dialog_id, this.replyingMessageObject, getThreadMessage(), this.messageWebPage, this.messageWebPageSearch, entities, null, null, notify, scheduleDate, sendAnimationData);
                start = end8 + 1;
                if (end8 == text2.length()) {
                    return true;
                }
                end2 = 0;
            }
        } else {
            return false;
        }
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentFragment;
        TLRPC.EncryptedChat encryptedChat = chatActivity != null ? chatActivity.getCurrentEncryptedChat() : null;
        return encryptedChat == null || AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 101;
    }

    public void checkSendButton(boolean animated) {
        boolean animated2;
        int i;
        int i2;
        if (this.editingMessageObject != null || this.recordingAudioVideo) {
            return;
        }
        if (!this.isPaused) {
            animated2 = animated;
        } else {
            animated2 = false;
        }
        CharSequence message = AndroidUtilities.getTrimmedString(this.messageEditText.getText());
        int i3 = this.slowModeTimer;
        if (i3 > 0 && i3 != Integer.MAX_VALUE && !isInScheduleMode()) {
            if (this.slowModeButton.getVisibility() != 0) {
                if (animated2) {
                    if (this.runningAnimationType == 5) {
                        return;
                    }
                    AnimatorSet animatorSet = this.runningAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.runningAnimation = null;
                    }
                    AnimatorSet animatorSet2 = this.runningAnimation2;
                    if (animatorSet2 != null) {
                        animatorSet2.cancel();
                        this.runningAnimation2 = null;
                    }
                    if (this.attachLayout != null) {
                        this.runningAnimation2 = new AnimatorSet();
                        ArrayList<Animator> animators = new ArrayList<>();
                        animators.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 0.0f));
                        this.scheduleButtonHidden = false;
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                        boolean hasScheduled = chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages();
                        ImageView imageView = this.scheduledButton;
                        if (imageView != null) {
                            imageView.setScaleY(1.0f);
                            if (hasScheduled) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(1);
                                this.scheduledButton.setPivotX(AndroidUtilities.dp(48.0f));
                                ImageView imageView2 = this.scheduledButton;
                                Property property = View.TRANSLATION_X;
                                float[] fArr = new float[1];
                                ImageView imageView3 = this.botButton;
                                fArr[0] = AndroidUtilities.dp((imageView3 == null || imageView3.getVisibility() != 0) ? 48.0f : 96.0f);
                                animators.add(ObjectAnimator.ofFloat(imageView2, property, fArr));
                                animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                                animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 1.0f));
                            } else {
                                ImageView imageView4 = this.scheduledButton;
                                ImageView imageView5 = this.botButton;
                                imageView4.setTranslationX(AndroidUtilities.dp((imageView5 == null || imageView5.getVisibility() != 0) ? 48.0f : 96.0f));
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(animators);
                        this.runningAnimation2.setDuration(100L);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.40
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }
                        });
                        this.runningAnimation2.start();
                        updateFieldRight(0);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                    }
                    this.runningAnimationType = 5;
                    this.runningAnimation = new AnimatorSet();
                    ArrayList<Animator> animators2 = new ArrayList<>();
                    if (this.audioVideoButtonContainer.getVisibility() == 0) {
                        animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 0.0f));
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 0.0f));
                    }
                    if (this.sendButton.getVisibility() == 0) {
                        animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                    }
                    if (this.cancelBotButton.getVisibility() == 0) {
                        animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                        animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                    }
                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 1.0f));
                    setSlowModeButtonVisible(true);
                    this.runningAnimation.playTogether(animators2);
                    this.runningAnimation.setDuration(150L);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.41
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                ChatActivityEnterView.this.runningAnimation = null;
                                ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                    return;
                }
                this.slowModeButton.setScaleX(1.0f);
                this.slowModeButton.setScaleY(1.0f);
                this.slowModeButton.setAlpha(1.0f);
                setSlowModeButtonVisible(true);
                this.audioVideoButtonContainer.setScaleX(0.1f);
                this.audioVideoButtonContainer.setScaleY(0.1f);
                this.audioVideoButtonContainer.setAlpha(0.0f);
                this.audioVideoButtonContainer.setVisibility(8);
                this.sendButton.setScaleX(0.1f);
                this.sendButton.setScaleY(0.1f);
                this.sendButton.setAlpha(0.0f);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.cancelBotButton.setVisibility(8);
                if (this.expandStickersButton.getVisibility() != 0) {
                    i2 = 8;
                } else {
                    this.expandStickersButton.setScaleX(0.1f);
                    this.expandStickersButton.setScaleY(0.1f);
                    this.expandStickersButton.setAlpha(0.0f);
                    i2 = 8;
                    this.expandStickersButton.setVisibility(8);
                }
                LinearLayout linearLayout = this.attachLayout;
                if (linearLayout != null) {
                    linearLayout.setVisibility(i2);
                    if (this.delegate != null && getVisibility() == 0) {
                        this.delegate.onAttachButtonHidden();
                    }
                    updateFieldRight(0);
                }
                this.scheduleButtonHidden = false;
                if (this.scheduledButton != null) {
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                    if (chatActivityEnterViewDelegate2 != null && chatActivityEnterViewDelegate2.hasScheduledMessages()) {
                        this.scheduledButton.setVisibility(0);
                        this.scheduledButton.setTag(1);
                    }
                    ImageView imageView6 = this.scheduledButton;
                    ImageView imageView7 = this.botButton;
                    imageView6.setTranslationX(AndroidUtilities.dp((imageView7 == null || imageView7.getVisibility() != 0) ? 48.0f : 96.0f));
                    this.scheduledButton.setAlpha(1.0f);
                    this.scheduledButton.setScaleX(1.0f);
                    this.scheduledButton.setScaleY(1.0f);
                    return;
                }
                return;
            }
            return;
        }
        if (message.length() <= 0 && !this.forceShowSendButton && this.audioToSend == null && this.videoToSendMessageObject == null) {
            if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                if (this.emojiView != null && this.emojiViewVisible && ((this.stickersTabOpen || (this.emojiTabOpen && this.searchingType == 2)) && !AndroidUtilities.isInMultiwindow)) {
                    if (animated2) {
                        if (this.runningAnimationType == 4) {
                            return;
                        }
                        AnimatorSet animatorSet3 = this.runningAnimation;
                        if (animatorSet3 != null) {
                            animatorSet3.cancel();
                            this.runningAnimation = null;
                        }
                        AnimatorSet animatorSet4 = this.runningAnimation2;
                        if (animatorSet4 != null) {
                            animatorSet4.cancel();
                            this.runningAnimation2 = null;
                        }
                        LinearLayout linearLayout2 = this.attachLayout;
                        if (linearLayout2 != null && this.recordInterfaceState == 0) {
                            linearLayout2.setVisibility(0);
                            this.runningAnimation2 = new AnimatorSet();
                            ArrayList<Animator> animators3 = new ArrayList<>();
                            animators3.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                            animators3.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 1.0f));
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                            boolean hasScheduled2 = chatActivityEnterViewDelegate3 != null && chatActivityEnterViewDelegate3.hasScheduledMessages();
                            this.scheduleButtonHidden = false;
                            ImageView imageView8 = this.scheduledButton;
                            if (imageView8 != null) {
                                imageView8.setScaleY(1.0f);
                                if (hasScheduled2) {
                                    this.scheduledButton.setVisibility(0);
                                    this.scheduledButton.setTag(1);
                                    this.scheduledButton.setPivotX(AndroidUtilities.dp(48.0f));
                                    animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                                    animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 1.0f));
                                    animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, 0.0f));
                                } else {
                                    this.scheduledButton.setAlpha(1.0f);
                                    this.scheduledButton.setScaleX(1.0f);
                                    this.scheduledButton.setTranslationX(0.0f);
                                }
                            }
                            this.runningAnimation2.playTogether(animators3);
                            this.runningAnimation2.setDuration(100L);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.44
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }

                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationCancel(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }
                            });
                            this.runningAnimation2.start();
                            updateFieldRight(1);
                            if (getVisibility() == 0) {
                                this.delegate.onAttachButtonShow();
                            }
                        }
                        this.expandStickersButton.setVisibility(0);
                        this.runningAnimation = new AnimatorSet();
                        this.runningAnimationType = 4;
                        ArrayList<Animator> animators4 = new ArrayList<>();
                        animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 1.0f));
                        animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 1.0f));
                        animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 1.0f));
                        if (this.cancelBotButton.getVisibility() == 0) {
                            animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                        } else if (this.audioVideoButtonContainer.getVisibility() == 0) {
                            animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 0.0f));
                        } else if (this.slowModeButton.getVisibility() == 0) {
                            animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 0.0f));
                        } else {
                            animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                            animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                        }
                        this.runningAnimation.playTogether(animators4);
                        this.runningAnimation.setDuration(250L);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.45
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                    ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                        return;
                    }
                    this.slowModeButton.setScaleX(0.1f);
                    this.slowModeButton.setScaleY(0.1f);
                    this.slowModeButton.setAlpha(0.0f);
                    setSlowModeButtonVisible(false);
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.sendButton.setVisibility(8);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.cancelBotButton.setVisibility(8);
                    this.audioVideoButtonContainer.setScaleX(0.1f);
                    this.audioVideoButtonContainer.setScaleY(0.1f);
                    this.audioVideoButtonContainer.setAlpha(0.0f);
                    this.audioVideoButtonContainer.setVisibility(8);
                    this.expandStickersButton.setScaleX(1.0f);
                    this.expandStickersButton.setScaleY(1.0f);
                    this.expandStickersButton.setAlpha(1.0f);
                    this.expandStickersButton.setVisibility(0);
                    if (this.attachLayout != null) {
                        if (getVisibility() == 0) {
                            this.delegate.onAttachButtonShow();
                        }
                        this.attachLayout.setVisibility(0);
                        updateFieldRight(1);
                    }
                    this.scheduleButtonHidden = false;
                    if (this.scheduledButton != null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
                        if (chatActivityEnterViewDelegate4 != null && chatActivityEnterViewDelegate4.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                        }
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        this.scheduledButton.setTranslationX(0.0f);
                        return;
                    }
                    return;
                } else if (this.sendButton.getVisibility() == 0 || this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0 || this.slowModeButton.getVisibility() == 0) {
                    if (animated2) {
                        if (this.runningAnimationType == 2) {
                            return;
                        }
                        AnimatorSet animatorSet5 = this.runningAnimation;
                        if (animatorSet5 != null) {
                            animatorSet5.cancel();
                            this.runningAnimation = null;
                        }
                        AnimatorSet animatorSet6 = this.runningAnimation2;
                        if (animatorSet6 != null) {
                            animatorSet6.cancel();
                            this.runningAnimation2 = null;
                        }
                        LinearLayout linearLayout3 = this.attachLayout;
                        if (linearLayout3 != null) {
                            if (linearLayout3.getVisibility() != 0) {
                                this.attachLayout.setVisibility(0);
                                this.attachLayout.setAlpha(0.0f);
                                this.attachLayout.setScaleX(0.0f);
                            }
                            this.runningAnimation2 = new AnimatorSet();
                            ArrayList<Animator> animators5 = new ArrayList<>();
                            animators5.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                            animators5.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 1.0f));
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate5 = this.delegate;
                            boolean hasScheduled3 = chatActivityEnterViewDelegate5 != null && chatActivityEnterViewDelegate5.hasScheduledMessages();
                            this.scheduleButtonHidden = false;
                            ImageView imageView9 = this.scheduledButton;
                            if (imageView9 != null) {
                                if (hasScheduled3) {
                                    imageView9.setVisibility(0);
                                    this.scheduledButton.setTag(1);
                                    this.scheduledButton.setPivotX(AndroidUtilities.dp(48.0f));
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 1.0f));
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, 0.0f));
                                } else {
                                    imageView9.setAlpha(1.0f);
                                    this.scheduledButton.setScaleX(1.0f);
                                    this.scheduledButton.setScaleY(1.0f);
                                    this.scheduledButton.setTranslationX(0.0f);
                                }
                            }
                            this.runningAnimation2.playTogether(animators5);
                            this.runningAnimation2.setDuration(100L);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.46
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }

                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationCancel(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }
                            });
                            this.runningAnimation2.start();
                            updateFieldRight(1);
                            if (getVisibility() == 0) {
                                this.delegate.onAttachButtonShow();
                            }
                        }
                        this.audioVideoButtonContainer.setVisibility(0);
                        this.runningAnimation = new AnimatorSet();
                        this.runningAnimationType = 2;
                        ArrayList<Animator> animators6 = new ArrayList<>();
                        animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 1.0f));
                        animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 1.0f));
                        animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 1.0f));
                        if (this.cancelBotButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                        } else if (this.expandStickersButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 0.0f));
                        } else if (this.slowModeButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 0.0f));
                        } else {
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                        }
                        this.runningAnimation.playTogether(animators6);
                        this.runningAnimation.setDuration(150L);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.47
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                    if (ChatActivityEnterView.this.audioVideoButtonContainer != null) {
                                        ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                    }
                                }
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                        return;
                    }
                    this.slowModeButton.setScaleX(0.1f);
                    this.slowModeButton.setScaleY(0.1f);
                    this.slowModeButton.setAlpha(0.0f);
                    setSlowModeButtonVisible(false);
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.sendButton.setVisibility(8);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.cancelBotButton.setVisibility(8);
                    this.expandStickersButton.setScaleX(0.1f);
                    this.expandStickersButton.setScaleY(0.1f);
                    this.expandStickersButton.setAlpha(0.0f);
                    this.expandStickersButton.setVisibility(8);
                    this.audioVideoButtonContainer.setScaleX(1.0f);
                    this.audioVideoButtonContainer.setScaleY(1.0f);
                    this.audioVideoButtonContainer.setAlpha(1.0f);
                    this.audioVideoButtonContainer.setVisibility(0);
                    if (this.attachLayout != null) {
                        if (getVisibility() == 0) {
                            this.delegate.onAttachButtonShow();
                        }
                        this.attachLayout.setAlpha(1.0f);
                        this.attachLayout.setScaleX(1.0f);
                        this.attachLayout.setVisibility(0);
                        updateFieldRight(1);
                    }
                    this.scheduleButtonHidden = false;
                    if (this.scheduledButton != null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate6 = this.delegate;
                        if (chatActivityEnterViewDelegate6 != null && chatActivityEnterViewDelegate6.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                        }
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        this.scheduledButton.setTranslationX(0.0f);
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
        }
        final String caption = this.messageEditText.getCaption();
        boolean showBotButton = caption != null && (this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
        boolean showSendButton = caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
        int color = (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) ? getThemedColor(Theme.key_chat_messagePanelSend) : getThemedColor(Theme.key_chat_messagePanelIcons);
        Theme.setSelectorDrawableColor(this.sendButton.getBackground(), Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), true);
        if (this.audioVideoButtonContainer.getVisibility() == 0 || this.slowModeButton.getVisibility() == 0 || showBotButton || showSendButton) {
            if (animated2) {
                if (this.runningAnimationType != 1 || this.messageEditText.getCaption() != null) {
                    if (this.runningAnimationType == 3 && caption != null) {
                        return;
                    }
                    AnimatorSet animatorSet7 = this.runningAnimation;
                    if (animatorSet7 != null) {
                        animatorSet7.cancel();
                        this.runningAnimation = null;
                    }
                    AnimatorSet animatorSet8 = this.runningAnimation2;
                    if (animatorSet8 != null) {
                        animatorSet8.cancel();
                        this.runningAnimation2 = null;
                    }
                    if (this.attachLayout != null) {
                        this.runningAnimation2 = new AnimatorSet();
                        ArrayList<Animator> animators7 = new ArrayList<>();
                        animators7.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 0.0f));
                        animators7.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, 0.0f));
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate7 = this.delegate;
                        final boolean hasScheduled4 = chatActivityEnterViewDelegate7 != null && chatActivityEnterViewDelegate7.hasScheduledMessages();
                        this.scheduleButtonHidden = true;
                        ImageView imageView10 = this.scheduledButton;
                        if (imageView10 != null) {
                            imageView10.setScaleY(1.0f);
                            if (hasScheduled4) {
                                this.scheduledButton.setTag(null);
                                animators7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 0.0f));
                                animators7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, 0.0f));
                                ImageView imageView11 = this.scheduledButton;
                                Property property2 = View.TRANSLATION_X;
                                float[] fArr2 = new float[1];
                                ImageView imageView12 = this.botButton;
                                fArr2[0] = AndroidUtilities.dp((imageView12 == null || imageView12.getVisibility() == 8) ? 48.0f : 96.0f);
                                animators7.add(ObjectAnimator.ofFloat(imageView11, property2, fArr2));
                            } else {
                                this.scheduledButton.setAlpha(0.0f);
                                this.scheduledButton.setScaleX(0.0f);
                                ImageView imageView13 = this.scheduledButton;
                                ImageView imageView14 = this.botButton;
                                imageView13.setTranslationX(AndroidUtilities.dp((imageView14 == null || imageView14.getVisibility() == 8) ? 48.0f : 96.0f));
                            }
                        }
                        this.runningAnimation2.playTogether(animators7);
                        this.runningAnimation2.setDuration(100L);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.42
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    if (hasScheduled4) {
                                        ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                                    }
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }
                        });
                        this.runningAnimation2.start();
                        updateFieldRight(0);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                    }
                    this.runningAnimation = new AnimatorSet();
                    ArrayList<Animator> animators8 = new ArrayList<>();
                    if (this.audioVideoButtonContainer.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 0.0f));
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, 0.0f));
                    }
                    if (this.slowModeButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, 0.0f));
                    }
                    if (showBotButton) {
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 0.0f));
                    } else if (showSendButton) {
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 0.1f));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 0.0f));
                    }
                    if (caption != null) {
                        this.runningAnimationType = 3;
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, 1.0f));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, 1.0f));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, 1.0f));
                        this.cancelBotButton.setVisibility(0);
                    } else {
                        this.runningAnimationType = 1;
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, 1.0f));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, 1.0f));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, 1.0f));
                        this.sendButton.setVisibility(0);
                    }
                    this.runningAnimation.playTogether(animators8);
                    this.runningAnimation.setDuration(150L);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.43
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                if (caption != null) {
                                    ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                                    ChatActivityEnterView.this.sendButton.setVisibility(8);
                                } else {
                                    ChatActivityEnterView.this.sendButton.setVisibility(0);
                                    ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                }
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                ChatActivityEnterView.this.setSlowModeButtonVisible(false);
                                ChatActivityEnterView.this.runningAnimation = null;
                                ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                    return;
                }
                return;
            }
            this.audioVideoButtonContainer.setScaleX(0.1f);
            this.audioVideoButtonContainer.setScaleY(0.1f);
            this.audioVideoButtonContainer.setAlpha(0.0f);
            this.audioVideoButtonContainer.setVisibility(8);
            if (this.slowModeButton.getVisibility() == 0) {
                this.slowModeButton.setScaleX(0.1f);
                this.slowModeButton.setScaleY(0.1f);
                this.slowModeButton.setAlpha(0.0f);
                setSlowModeButtonVisible(false);
            }
            if (caption != null) {
                this.sendButton.setScaleX(0.1f);
                this.sendButton.setScaleY(0.1f);
                this.sendButton.setAlpha(0.0f);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setScaleX(1.0f);
                this.cancelBotButton.setScaleY(1.0f);
                this.cancelBotButton.setAlpha(1.0f);
                this.cancelBotButton.setVisibility(0);
            } else {
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.sendButton.setVisibility(0);
                this.sendButton.setScaleX(1.0f);
                this.sendButton.setScaleY(1.0f);
                this.sendButton.setAlpha(1.0f);
                this.cancelBotButton.setVisibility(8);
            }
            if (this.expandStickersButton.getVisibility() != 0) {
                i = 8;
            } else {
                this.expandStickersButton.setScaleX(0.1f);
                this.expandStickersButton.setScaleY(0.1f);
                this.expandStickersButton.setAlpha(0.0f);
                i = 8;
                this.expandStickersButton.setVisibility(8);
            }
            LinearLayout linearLayout4 = this.attachLayout;
            if (linearLayout4 != null) {
                linearLayout4.setVisibility(i);
                if (this.delegate != null && getVisibility() == 0) {
                    this.delegate.onAttachButtonHidden();
                }
                updateFieldRight(0);
            }
            this.scheduleButtonHidden = true;
            if (this.scheduledButton != null) {
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate8 = this.delegate;
                if (chatActivityEnterViewDelegate8 != null && chatActivityEnterViewDelegate8.hasScheduledMessages()) {
                    this.scheduledButton.setVisibility(8);
                    this.scheduledButton.setTag(null);
                }
                this.scheduledButton.setAlpha(0.0f);
                this.scheduledButton.setScaleX(0.0f);
                this.scheduledButton.setScaleY(1.0f);
                ImageView imageView15 = this.scheduledButton;
                ImageView imageView16 = this.botButton;
                imageView15.setTranslationX(AndroidUtilities.dp((imageView16 == null || imageView16.getVisibility() == 8) ? 48.0f : 96.0f));
            }
        }
    }

    public void setSlowModeButtonVisible(boolean visible) {
        this.slowModeButton.setVisibility(visible ? 0 : 8);
        int padding = visible ? AndroidUtilities.dp(16.0f) : 0;
        if (this.messageEditText.getPaddingRight() != padding) {
            this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), padding, AndroidUtilities.dp(12.0f));
        }
    }

    private void updateFieldRight(int attachVisible) {
        ImageView imageView;
        ImageView imageView2;
        ImageView imageView3;
        LinearLayout linearLayout;
        ImageView imageView4;
        ImageView imageView5;
        ImageView imageView6;
        LinearLayout linearLayout2;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null || this.editingMessageObject != null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editTextCaption.getLayoutParams();
        int oldRightMargin = layoutParams.rightMargin;
        if (attachVisible == 1) {
            ImageView imageView7 = this.botButton;
            if (imageView7 != null && imageView7.getVisibility() == 0 && (imageView6 = this.scheduledButton) != null && imageView6.getVisibility() == 0 && (linearLayout2 = this.attachLayout) != null && linearLayout2.getVisibility() == 0) {
                layoutParams.rightMargin = AndroidUtilities.dp(146.0f);
            } else {
                ImageView imageView8 = this.botButton;
                if ((imageView8 != null && imageView8.getVisibility() == 0) || (((imageView4 = this.notifyButton) != null && imageView4.getVisibility() == 0) || ((imageView5 = this.scheduledButton) != null && imageView5.getTag() != null))) {
                    layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                }
            }
        } else if (attachVisible == 2) {
            if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                ImageView imageView9 = this.botButton;
                if (imageView9 != null && imageView9.getVisibility() == 0 && (imageView3 = this.scheduledButton) != null && imageView3.getVisibility() == 0 && (linearLayout = this.attachLayout) != null && linearLayout.getVisibility() == 0) {
                    layoutParams.rightMargin = AndroidUtilities.dp(146.0f);
                } else {
                    ImageView imageView10 = this.botButton;
                    if ((imageView10 != null && imageView10.getVisibility() == 0) || (((imageView = this.notifyButton) != null && imageView.getVisibility() == 0) || ((imageView2 = this.scheduledButton) != null && imageView2.getTag() != null))) {
                        layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                    } else {
                        layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                    }
                }
            }
        } else {
            ImageView imageView11 = this.scheduledButton;
            if (imageView11 != null && imageView11.getTag() != null) {
                layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
            }
        }
        if (oldRightMargin != layoutParams.rightMargin) {
            this.messageEditText.setLayoutParams(layoutParams);
        }
    }

    public void startMessageTransition() {
        Runnable runnable = this.moveToSendStateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.messageTransitionIsRunning = true;
            this.moveToSendStateRunnable.run();
            this.moveToSendStateRunnable = null;
        }
    }

    public boolean canShowMessageTransition() {
        return this.moveToSendStateRunnable != null;
    }

    public void updateRecordInterface(final int recordState) {
        ViewGroup.LayoutParams oldLayoutParams;
        Runnable runnable = this.moveToSendStateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.moveToSendStateRunnable = null;
        }
        this.recordCircle.voiceEnterTransitionInProgress = false;
        if (this.recordingAudioVideo) {
            if (this.recordInterfaceState == 1) {
                return;
            }
            this.recordInterfaceState = 1;
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.setEnabled(false);
            }
            AnimatorSet animatorSet = this.emojiButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            try {
                if (this.wakeLock == null) {
                    PowerManager pm = (PowerManager) ApplicationLoader.applicationContext.getSystemService("power");
                    PowerManager.WakeLock newWakeLock = pm.newWakeLock(536870918, "telegram:audio_record_lock");
                    this.wakeLock = newWakeLock;
                    newWakeLock.acquire();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.needStartRecordAudio(0);
            }
            AnimatorSet animatorSet2 = this.runningAnimationAudio;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            AnimatorSet animatorSet3 = this.recordPannelAnimation;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
            }
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            this.recordDot.resetAlpha();
            this.runningAnimationAudio = new AnimatorSet();
            this.recordDot.setScaleX(0.0f);
            this.recordDot.setScaleY(0.0f);
            this.recordDot.enterAnimation = true;
            this.recordTimerView.setTranslationX(AndroidUtilities.dp(20.0f));
            this.recordTimerView.setAlpha(0.0f);
            this.slideText.setTranslationX(AndroidUtilities.dp(20.0f));
            this.slideText.setAlpha(0.0f);
            this.slideText.setCancelToProgress(0.0f);
            this.slideText.setSlideX(1.0f);
            this.recordCircle.setLockTranslation(10000.0f);
            this.slideText.setEnabled(true);
            this.recordIsCanceled = false;
            AnimatorSet iconChanges = new AnimatorSet();
            iconChanges.playTogether(ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, 1.0f));
            ImageView imageView = this.audioSendButton;
            if (imageView != null) {
                iconChanges.playTogether(ObjectAnimator.ofFloat(imageView, View.ALPHA, 0.0f));
            }
            ImageView imageView2 = this.videoSendButton;
            if (imageView2 != null) {
                iconChanges.playTogether(ObjectAnimator.ofFloat(imageView2, View.ALPHA, 0.0f));
            }
            BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
            if (botCommandsMenuView != null) {
                iconChanges.playTogether(ObjectAnimator.ofFloat(botCommandsMenuView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 0.0f));
            }
            AnimatorSet viewTransition = new AnimatorSet();
            viewTransition.playTogether(ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, 1.0f));
            ImageView imageView3 = this.scheduledButton;
            if (imageView3 != null) {
                viewTransition.playTogether(ObjectAnimator.ofFloat(imageView3, View.TRANSLATION_X, AndroidUtilities.dp(30.0f)), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 0.0f));
            }
            LinearLayout linearLayout = this.attachLayout;
            if (linearLayout != null) {
                viewTransition.playTogether(ObjectAnimator.ofFloat(linearLayout, View.TRANSLATION_X, AndroidUtilities.dp(30.0f)), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 0.0f));
            }
            this.runningAnimationAudio.playTogether(iconChanges.setDuration(150L), viewTransition.setDuration(150L), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, 1.0f).setDuration(300L));
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.48
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                        ChatActivityEnterView.this.runningAnimationAudio = null;
                    }
                    ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                    ChatActivityEnterView.this.slideText.setTranslationX(0.0f);
                    ChatActivityEnterView.this.recordCircle.showTooltipIfNeed();
                    ChatActivityEnterView.this.messageEditText.setAlpha(0.0f);
                }
            });
            this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
            this.runningAnimationAudio.start();
            this.recordTimerView.start();
        } else if (this.recordIsCanceled && recordState == 3) {
            return;
        } else {
            PowerManager.WakeLock wakeLock = this.wakeLock;
            if (wakeLock != null) {
                try {
                    wakeLock.release();
                    this.wakeLock = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            this.wasSendTyping = false;
            if (this.recordInterfaceState != 0) {
                this.accountInstance.getMessagesController().sendTyping(this.dialog_id, getThreadMessageId(), 2, 0);
                this.recordInterfaceState = 0;
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 != null) {
                    emojiView2.setEnabled(true);
                }
                boolean shouldShowFastTransition = false;
                AnimatorSet animatorSet4 = this.runningAnimationAudio;
                if (animatorSet4 != null) {
                    shouldShowFastTransition = animatorSet4.isRunning();
                    ImageView imageView4 = this.videoSendButton;
                    if (imageView4 != null) {
                        imageView4.setScaleX(1.0f);
                        this.videoSendButton.setScaleY(1.0f);
                    }
                    ImageView imageView5 = this.audioSendButton;
                    if (imageView5 != null) {
                        imageView5.setScaleX(1.0f);
                        this.audioSendButton.setScaleY(1.0f);
                    }
                    this.runningAnimationAudio.removeAllListeners();
                    this.runningAnimationAudio.cancel();
                }
                AnimatorSet animatorSet5 = this.recordPannelAnimation;
                if (animatorSet5 != null) {
                    animatorSet5.cancel();
                }
                this.messageEditText.setVisibility(0);
                this.runningAnimationAudio = new AnimatorSet();
                if (shouldShowFastTransition || recordState == 4) {
                    if (this.videoSendButton != null && isInVideoMode()) {
                        this.videoSendButton.setVisibility(0);
                    } else {
                        ImageView imageView6 = this.audioSendButton;
                        if (imageView6 != null) {
                            imageView6.setVisibility(0);
                        }
                    }
                    this.runningAnimationAudio.playTogether(ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, 0.0f), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, 0.0f), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.messageEditText, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", 1.0f));
                    BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
                    if (botCommandsMenuView2 != null) {
                        this.runningAnimationAudio.playTogether(ObjectAnimator.ofFloat(botCommandsMenuView2, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f));
                    }
                    ImageView imageView7 = this.audioSendButton;
                    if (imageView7 != null) {
                        imageView7.setScaleX(1.0f);
                        this.audioSendButton.setScaleY(1.0f);
                        AnimatorSet animatorSet6 = this.runningAnimationAudio;
                        Animator[] animatorArr = new Animator[1];
                        ImageView imageView8 = this.audioSendButton;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr[0] = ObjectAnimator.ofFloat(imageView8, property, fArr);
                        animatorSet6.playTogether(animatorArr);
                    }
                    ImageView imageView9 = this.videoSendButton;
                    if (imageView9 != null) {
                        imageView9.setScaleX(1.0f);
                        this.videoSendButton.setScaleY(1.0f);
                        AnimatorSet animatorSet7 = this.runningAnimationAudio;
                        Animator[] animatorArr2 = new Animator[1];
                        ImageView imageView10 = this.videoSendButton;
                        Property property2 = View.ALPHA;
                        float[] fArr2 = new float[1];
                        fArr2[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr2[0] = ObjectAnimator.ofFloat(imageView10, property2, fArr2);
                        animatorSet7.playTogether(animatorArr2);
                    }
                    ImageView imageView11 = this.scheduledButton;
                    if (imageView11 != null) {
                        this.runningAnimationAudio.playTogether(ObjectAnimator.ofFloat(imageView11, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                    }
                    LinearLayout linearLayout2 = this.attachLayout;
                    if (linearLayout2 != null) {
                        this.runningAnimationAudio.playTogether(ObjectAnimator.ofFloat(linearLayout2, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                    }
                    this.recordIsCanceled = true;
                    this.runningAnimationAudio.setDuration(150L);
                } else if (recordState == 3) {
                    this.slideText.setEnabled(false);
                    if (isInVideoMode()) {
                        this.recordedAudioBackground.setVisibility(8);
                        this.recordedAudioTimeTextView.setVisibility(8);
                        this.recordedAudioPlayButton.setVisibility(8);
                        this.recordedAudioSeekBar.setVisibility(8);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                        this.recordDeleteImageView.setProgress(0.0f);
                        this.recordDeleteImageView.stopAnimation();
                    } else {
                        this.videoTimelineView.setVisibility(8);
                        this.recordedAudioBackground.setVisibility(0);
                        this.recordedAudioTimeTextView.setVisibility(0);
                        this.recordedAudioPlayButton.setVisibility(0);
                        this.recordedAudioSeekBar.setVisibility(0);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioBackground.setAlpha(0.0f);
                        this.recordedAudioTimeTextView.setAlpha(0.0f);
                        this.recordedAudioPlayButton.setAlpha(0.0f);
                        this.recordedAudioSeekBar.setAlpha(0.0f);
                        this.recordedAudioPanel.setVisibility(0);
                    }
                    this.recordDeleteImageView.setAlpha(0.0f);
                    this.recordDeleteImageView.setScaleX(0.0f);
                    this.recordDeleteImageView.setScaleY(0.0f);
                    this.recordDeleteImageView.setProgress(0.0f);
                    this.recordDeleteImageView.stopAnimation();
                    ValueAnimator transformToSeekbar = ValueAnimator.ofFloat(0.0f, 1.0f);
                    transformToSeekbar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda44
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.m2347xbc96a979(valueAnimator);
                        }
                    });
                    ViewGroup parent = null;
                    if (!isInVideoMode()) {
                        ViewGroup parent2 = (ViewGroup) this.recordedAudioPanel.getParent();
                        ViewGroup.LayoutParams oldLayoutParams2 = this.recordedAudioPanel.getLayoutParams();
                        parent2.removeView(this.recordedAudioPanel);
                        FrameLayout.LayoutParams newLayoutParams = new FrameLayout.LayoutParams(parent2.getMeasuredWidth(), AndroidUtilities.dp(48.0f));
                        newLayoutParams.gravity = 80;
                        this.sizeNotifierLayout.addView(this.recordedAudioPanel, newLayoutParams);
                        this.videoTimelineView.setVisibility(8);
                        parent = parent2;
                        oldLayoutParams = oldLayoutParams2;
                    } else {
                        this.videoTimelineView.setVisibility(0);
                        oldLayoutParams = null;
                    }
                    this.recordDeleteImageView.setAlpha(0.0f);
                    this.recordDeleteImageView.setScaleX(0.0f);
                    this.recordDeleteImageView.setScaleY(0.0f);
                    AnimatorSet iconsAnimator = new AnimatorSet();
                    iconsAnimator.playTogether(ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.recordDeleteImageView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 0.0f));
                    ImageView imageView12 = this.videoSendButton;
                    if (imageView12 != null) {
                        Animator[] animatorArr3 = new Animator[3];
                        Property property3 = View.ALPHA;
                        float[] fArr3 = new float[1];
                        fArr3[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr3[0] = ObjectAnimator.ofFloat(imageView12, property3, fArr3);
                        animatorArr3[1] = ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_X, 1.0f);
                        animatorArr3[2] = ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_Y, 1.0f);
                        iconsAnimator.playTogether(animatorArr3);
                    }
                    ImageView imageView13 = this.audioSendButton;
                    if (imageView13 != null) {
                        Animator[] animatorArr4 = new Animator[3];
                        Property property4 = View.ALPHA;
                        float[] fArr4 = new float[1];
                        fArr4[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr4[0] = ObjectAnimator.ofFloat(imageView13, property4, fArr4);
                        animatorArr4[1] = ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_X, 1.0f);
                        animatorArr4[2] = ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_Y, 1.0f);
                        iconsAnimator.playTogether(animatorArr4);
                    }
                    BotCommandsMenuView botCommandsMenuView3 = this.botCommandsMenuButton;
                    if (botCommandsMenuView3 != null) {
                        iconsAnimator.playTogether(ObjectAnimator.ofFloat(botCommandsMenuView3, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_Y, 0.0f));
                    }
                    iconsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.49
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.videoSendButton != null) {
                                ChatActivityEnterView.this.videoSendButton.setScaleX(1.0f);
                                ChatActivityEnterView.this.videoSendButton.setScaleY(1.0f);
                            }
                            if (ChatActivityEnterView.this.audioSendButton != null) {
                                ChatActivityEnterView.this.audioSendButton.setScaleX(1.0f);
                                ChatActivityEnterView.this.audioSendButton.setScaleY(1.0f);
                            }
                        }
                    });
                    iconsAnimator.setDuration(150L);
                    iconsAnimator.setStartDelay(150L);
                    AnimatorSet videoAdditionalAnimations = new AnimatorSet();
                    if (isInVideoMode()) {
                        this.recordedAudioTimeTextView.setAlpha(0.0f);
                        this.videoTimelineView.setAlpha(0.0f);
                        videoAdditionalAnimations.playTogether(ObjectAnimator.ofFloat(this.recordedAudioTimeTextView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.videoTimelineView, View.ALPHA, 1.0f));
                        videoAdditionalAnimations.setDuration(150L);
                        videoAdditionalAnimations.setStartDelay(430L);
                    }
                    transformToSeekbar.setDuration(isInVideoMode() ? 490L : 580L);
                    this.runningAnimationAudio.playTogether(iconsAnimator, transformToSeekbar, videoAdditionalAnimations);
                    final ViewGroup finalParent = parent;
                    final ViewGroup.LayoutParams finalOldLayoutParams = oldLayoutParams;
                    this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.50
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (finalParent != null) {
                                ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.recordedAudioPanel);
                                finalParent.addView(ChatActivityEnterView.this.recordedAudioPanel, finalOldLayoutParams);
                            }
                            ChatActivityEnterView.this.recordedAudioPanel.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioTimeTextView.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioPlayButton.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordedAudioPlayButton.setScaleY(1.0f);
                            ChatActivityEnterView.this.recordedAudioPlayButton.setScaleX(1.0f);
                            ChatActivityEnterView.this.recordedAudioSeekBar.setAlpha(1.0f);
                            for (int i = 0; i < 2; i++) {
                                ChatActivityEnterView.this.emojiButton[i].setScaleY(0.0f);
                                ChatActivityEnterView.this.emojiButton[i].setScaleX(0.0f);
                                ChatActivityEnterView.this.emojiButton[i].setAlpha(0.0f);
                            }
                            if (ChatActivityEnterView.this.botCommandsMenuButton != null) {
                                ChatActivityEnterView.this.botCommandsMenuButton.setAlpha(0.0f);
                                ChatActivityEnterView.this.botCommandsMenuButton.setScaleX(0.0f);
                                ChatActivityEnterView.this.botCommandsMenuButton.setScaleY(0.0f);
                            }
                        }
                    });
                } else if (recordState == 2 || recordState == 5) {
                    if (this.videoSendButton != null && isInVideoMode()) {
                        this.videoSendButton.setVisibility(0);
                    } else {
                        ImageView imageView14 = this.audioSendButton;
                        if (imageView14 != null) {
                            imageView14.setVisibility(0);
                        }
                    }
                    this.recordIsCanceled = true;
                    AnimatorSet iconsAnimator2 = new AnimatorSet();
                    iconsAnimator2.playTogether(ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, 0.0f));
                    BotCommandsMenuView botCommandsMenuView4 = this.botCommandsMenuButton;
                    if (botCommandsMenuView4 != null) {
                        iconsAnimator2.playTogether(ObjectAnimator.ofFloat(botCommandsMenuView4, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f));
                    }
                    AnimatorSet recordTimer = new AnimatorSet();
                    recordTimer.playTogether(ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, -AndroidUtilities.dp(20.0f)));
                    if (recordState != 5) {
                        this.audioVideoButtonContainer.setScaleX(0.0f);
                        this.audioVideoButtonContainer.setScaleY(0.0f);
                        ImageView imageView15 = this.attachButton;
                        if (imageView15 != null && imageView15.getVisibility() == 0) {
                            this.attachButton.setScaleX(0.0f);
                            this.attachButton.setScaleY(0.0f);
                        }
                        ImageView imageView16 = this.botButton;
                        if (imageView16 != null && imageView16.getVisibility() == 0) {
                            this.botButton.setScaleX(0.0f);
                            this.botButton.setScaleY(0.0f);
                        }
                        iconsAnimator2.playTogether(ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", 1.0f), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 1.0f));
                        LinearLayout linearLayout3 = this.attachLayout;
                        if (linearLayout3 != null) {
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(linearLayout3, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.attachLayout, View.TRANSLATION_X, 0.0f));
                        }
                        ImageView imageView17 = this.attachButton;
                        if (imageView17 != null) {
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(imageView17, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.attachButton, View.SCALE_Y, 1.0f));
                        }
                        ImageView imageView18 = this.botButton;
                        if (imageView18 != null) {
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(imageView18, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botButton, View.SCALE_Y, 1.0f));
                        }
                        ImageView imageView19 = this.videoSendButton;
                        if (imageView19 != null) {
                            Animator[] animatorArr5 = new Animator[1];
                            Property property5 = View.ALPHA;
                            float[] fArr5 = new float[1];
                            fArr5[0] = isInVideoMode() ? 1.0f : 0.0f;
                            animatorArr5[0] = ObjectAnimator.ofFloat(imageView19, property5, fArr5);
                            iconsAnimator2.playTogether(animatorArr5);
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_X, 1.0f));
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(this.videoSendButton, View.SCALE_Y, 1.0f));
                        }
                        ImageView imageView20 = this.audioSendButton;
                        if (imageView20 != null) {
                            Animator[] animatorArr6 = new Animator[1];
                            Property property6 = View.ALPHA;
                            float[] fArr6 = new float[1];
                            fArr6[0] = isInVideoMode() ? 0.0f : 1.0f;
                            animatorArr6[0] = ObjectAnimator.ofFloat(imageView20, property6, fArr6);
                            iconsAnimator2.playTogether(animatorArr6);
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_X, 1.0f));
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(this.audioSendButton, View.SCALE_Y, 1.0f));
                        }
                        ImageView imageView21 = this.scheduledButton;
                        if (imageView21 != null) {
                            iconsAnimator2.playTogether(ObjectAnimator.ofFloat(imageView21, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, 0.0f));
                        }
                    } else {
                        AnimatorSet icons2 = new AnimatorSet();
                        icons2.playTogether(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 1.0f));
                        LinearLayout linearLayout4 = this.attachLayout;
                        if (linearLayout4 != null) {
                            icons2.playTogether(ObjectAnimator.ofFloat(linearLayout4, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                        }
                        ImageView imageView22 = this.scheduledButton;
                        if (imageView22 != null) {
                            icons2.playTogether(ObjectAnimator.ofFloat(imageView22, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, 0.0f));
                        }
                        icons2.setDuration(150L);
                        icons2.setStartDelay(110L);
                        icons2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.51
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                float f = 0.0f;
                                if (ChatActivityEnterView.this.audioSendButton != null) {
                                    ChatActivityEnterView.this.audioSendButton.setAlpha(ChatActivityEnterView.this.isInVideoMode() ? 0.0f : 1.0f);
                                }
                                if (ChatActivityEnterView.this.videoSendButton != null) {
                                    ImageView imageView23 = ChatActivityEnterView.this.videoSendButton;
                                    if (ChatActivityEnterView.this.isInVideoMode()) {
                                        f = 1.0f;
                                    }
                                    imageView23.setAlpha(f);
                                }
                            }
                        });
                        this.runningAnimationAudio.playTogether(icons2);
                    }
                    iconsAnimator2.setDuration(150L);
                    iconsAnimator2.setStartDelay(700L);
                    recordTimer.setDuration(200L);
                    recordTimer.setStartDelay(200L);
                    this.messageEditText.setTranslationX(0.0f);
                    ObjectAnimator messageEditTextAniamtor = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f);
                    messageEditTextAniamtor.setStartDelay(300L);
                    messageEditTextAniamtor.setDuration(200L);
                    AnimatorSet animatorSet8 = this.runningAnimationAudio;
                    RecordCircle recordCircle = this.recordCircle;
                    animatorSet8.playTogether(iconsAnimator2, recordTimer, messageEditTextAniamtor, ObjectAnimator.ofFloat(recordCircle, "lockAnimatedTranslation", recordCircle.startTranslation).setDuration(200L));
                    if (recordState == 5) {
                        this.recordCircle.canceledByGesture();
                        ObjectAnimator cancel = ObjectAnimator.ofFloat(this.recordCircle, "slideToCancelProgress", 1.0f).setDuration(200L);
                        cancel.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                        this.runningAnimationAudio.playTogether(cancel);
                    } else {
                        Animator recordCircleAnimator = ObjectAnimator.ofFloat(this.recordCircle, "exitTransition", 1.0f);
                        recordCircleAnimator.setDuration(360L);
                        recordCircleAnimator.setStartDelay(490L);
                        this.runningAnimationAudio.playTogether(recordCircleAnimator);
                    }
                    this.recordDot.playDeleteAnimation();
                } else {
                    if (this.videoSendButton != null && isInVideoMode()) {
                        this.videoSendButton.setVisibility(0);
                    } else {
                        ImageView imageView23 = this.audioSendButton;
                        if (imageView23 != null) {
                            imageView23.setVisibility(0);
                        }
                    }
                    AnimatorSet iconsAnimator3 = new AnimatorSet();
                    iconsAnimator3.playTogether(ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.recordDot, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, 1.0f));
                    BotCommandsMenuView botCommandsMenuView5 = this.botCommandsMenuButton;
                    if (botCommandsMenuView5 != null) {
                        iconsAnimator3.playTogether(ObjectAnimator.ofFloat(botCommandsMenuView5, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.botCommandsMenuButton, View.ALPHA, 1.0f));
                    }
                    ImageView imageView24 = this.audioSendButton;
                    if (imageView24 != null) {
                        imageView24.setScaleX(1.0f);
                        this.audioSendButton.setScaleY(1.0f);
                        Animator[] animatorArr7 = new Animator[1];
                        ImageView imageView25 = this.audioSendButton;
                        Property property7 = View.ALPHA;
                        float[] fArr7 = new float[1];
                        fArr7[0] = isInVideoMode() ? 0.0f : 1.0f;
                        animatorArr7[0] = ObjectAnimator.ofFloat(imageView25, property7, fArr7);
                        iconsAnimator3.playTogether(animatorArr7);
                    }
                    ImageView imageView26 = this.videoSendButton;
                    if (imageView26 != null) {
                        imageView26.setScaleX(1.0f);
                        this.videoSendButton.setScaleY(1.0f);
                        Animator[] animatorArr8 = new Animator[1];
                        ImageView imageView27 = this.videoSendButton;
                        Property property8 = View.ALPHA;
                        float[] fArr8 = new float[1];
                        fArr8[0] = isInVideoMode() ? 1.0f : 0.0f;
                        animatorArr8[0] = ObjectAnimator.ofFloat(imageView27, property8, fArr8);
                        iconsAnimator3.playTogether(animatorArr8);
                    }
                    LinearLayout linearLayout5 = this.attachLayout;
                    if (linearLayout5 != null) {
                        linearLayout5.setTranslationX(0.0f);
                        iconsAnimator3.playTogether(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, 1.0f));
                    }
                    ImageView imageView28 = this.scheduledButton;
                    if (imageView28 != null) {
                        imageView28.setTranslationX(0.0f);
                        iconsAnimator3.playTogether(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, 1.0f));
                    }
                    iconsAnimator3.setDuration(150L);
                    iconsAnimator3.setStartDelay(200L);
                    AnimatorSet recordTimer2 = new AnimatorSet();
                    recordTimer2.playTogether(ObjectAnimator.ofFloat(this.recordTimerView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.recordTimerView, View.TRANSLATION_X, AndroidUtilities.dp(40.0f)), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_X, AndroidUtilities.dp(40.0f)));
                    recordTimer2.setDuration(150L);
                    Animator recordCircleAnimator2 = ObjectAnimator.ofFloat(this.recordCircle, "exitTransition", 1.0f);
                    recordCircleAnimator2.setDuration(this.messageTransitionIsRunning ? 220L : 360L);
                    this.messageEditText.setTranslationX(0.0f);
                    ObjectAnimator messageEditTextAniamtor2 = ObjectAnimator.ofFloat(this.messageEditText, View.ALPHA, 1.0f);
                    messageEditTextAniamtor2.setStartDelay(150L);
                    messageEditTextAniamtor2.setDuration(200L);
                    this.runningAnimationAudio.playTogether(iconsAnimator3, recordTimer2, messageEditTextAniamtor2, recordCircleAnimator2);
                }
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.52
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setSendButtonInvisible();
                            ChatActivityEnterView.this.runningAnimationAudio = null;
                            if (recordState != 3) {
                                ChatActivityEnterView.this.messageEditText.requestFocus();
                            }
                            ChatActivityEnterView.this.recordedAudioBackground.setAlpha(1.0f);
                            if (ChatActivityEnterView.this.attachLayout != null) {
                                ChatActivityEnterView.this.attachLayout.setTranslationX(0.0f);
                            }
                            ChatActivityEnterView.this.slideText.setCancelToProgress(0.0f);
                            ChatActivityEnterView.this.delegate.onAudioVideoInterfaceUpdated();
                            ChatActivityEnterView.this.updateSendAsButton();
                        }
                    }
                });
                this.runningAnimationAudio.start();
                this.recordTimerView.stop();
            } else {
                return;
            }
        }
        this.delegate.onAudioVideoInterfaceUpdated();
        updateSendAsButton();
    }

    /* renamed from: lambda$updateRecordInterface$42$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2347xbc96a979(ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        if (!isInVideoMode()) {
            this.recordCircle.setTransformToSeekbar(value);
            this.seekBarWaveform.setWaveScaling(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioSeekBar.invalidate();
            this.recordedAudioTimeTextView.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setScaleX(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioPlayButton.setScaleY(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.recordedAudioSeekBar.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            return;
        }
        this.recordCircle.setExitTransition(value);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.recordingAudioVideo) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setDelegate(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
        this.delegate = chatActivityEnterViewDelegate;
    }

    public void setCommand(MessageObject messageObject, String command, boolean longPress, boolean username) {
        String text;
        if (command == null || getVisibility() != 0) {
            return;
        }
        TLRPC.User user = null;
        if (longPress) {
            String text2 = this.messageEditText.getText().toString();
            if (messageObject != null && DialogObject.isChatDialog(this.dialog_id)) {
                user = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.from_id.user_id));
            }
            if ((this.botCount != 1 || username) && user != null && user.bot && !command.contains("@")) {
                text = String.format(Locale.US, "%s@%s", command, user.username) + " " + text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
            } else {
                text = command + " " + text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
            }
            this.ignoreTextChange = true;
            this.messageEditText.setText(text);
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setSelection(editTextCaption.getText().length());
            this.ignoreTextChange = false;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
            }
            if (!this.keyboardVisible && this.currentPopupContentType == -1) {
                openKeyboard();
            }
        } else if (this.slowModeTimer > 0 && !isInScheduleMode()) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
            }
        } else {
            if (messageObject != null && DialogObject.isChatDialog(this.dialog_id)) {
                user = this.accountInstance.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.from_id.user_id));
            }
            TLRPC.User user2 = user;
            if ((this.botCount != 1 || username) && user2 != null && user2.bot && !command.contains("@")) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", command, user2.username), this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, false, null, null, null, true, 0, null);
            } else {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(command, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, false, null, null, null, true, 0, null);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x02b2  */
    /* JADX WARN: Removed duplicated region for block: B:102:0x02bf  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x030c  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x028c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0296  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x029d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setEditingMessageObject(org.telegram.messenger.MessageObject r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 1134
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.setEditingMessageObject(org.telegram.messenger.MessageObject, boolean):void");
    }

    /* renamed from: lambda$setEditingMessageObject$43$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2341x885c5769(CharSequence textToSetWithKeyboard) {
        setFieldText(textToSetWithKeyboard);
        this.setTextFieldRunnable = null;
    }

    public ImageView getAttachButton() {
        return this.attachButton;
    }

    public View getSendButton() {
        return this.sendButton.getVisibility() == 0 ? this.sendButton : this.audioVideoButtonContainer;
    }

    public View getAudioVideoButtonContainer() {
        return this.audioVideoButtonContainer;
    }

    public View getEmojiButton() {
        return this.emojiButton[0];
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public TrendingStickersAlert getTrendingStickersAlert() {
        return this.trendingStickersAlert;
    }

    public void updateColors() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.sendPopupLayout;
        if (actionBarPopupWindowLayout != null) {
            int count = actionBarPopupWindowLayout.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.sendPopupLayout.getChildAt(a);
                if (view instanceof ActionBarMenuSubItem) {
                    ActionBarMenuSubItem item = (ActionBarMenuSubItem) view;
                    item.setColors(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon));
                    item.setSelectorColor(getThemedColor(Theme.key_dialogButtonSelector));
                }
            }
            this.sendPopupLayout.setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupLayout.invalidate();
            }
        }
        updateRecordedDeleteIconColors();
        this.recordCircle.updateColors();
        this.recordDot.updateColors();
        this.slideText.updateColors();
        this.recordTimerView.updateColors();
        this.videoTimelineView.updateColors();
        NumberTextView numberTextView = this.captionLimitView;
        if (numberTextView != null && this.messageEditText != null) {
            if (this.codePointCount - this.currentLimit < 0) {
                numberTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText));
            } else {
                numberTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
            }
        }
        int color = getThemedColor(Theme.key_chat_messagePanelVoicePressed);
        int defaultAlpha = Color.alpha(color);
        this.doneCheckDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (defaultAlpha * ((this.doneButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
        BotCommandsMenuContainer botCommandsMenuContainer = this.botCommandsMenuContainer;
        if (botCommandsMenuContainer != null) {
            botCommandsMenuContainer.updateColors();
        }
        BotKeyboardView botKeyboardView = this.botKeyboardView;
        if (botKeyboardView != null) {
            botKeyboardView.updateColors();
        }
        for (int i = 0; i < 2; i++) {
            this.emojiButton[i].setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            if (Build.VERSION.SDK_INT >= 21) {
                this.emojiButton[i].setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
            }
        }
    }

    private void updateRecordedDeleteIconColors() {
        int dotColor = getThemedColor(Theme.key_chat_recordedVoiceDot);
        int background = getThemedColor(Theme.key_chat_messagePanelBackground);
        int greyColor = getThemedColor(Theme.key_chat_messagePanelVoiceDelete);
        this.recordDeleteImageView.setLayerColor("Cup Red.**", dotColor);
        this.recordDeleteImageView.setLayerColor("Box Red.**", dotColor);
        this.recordDeleteImageView.setLayerColor("Cup Grey.**", greyColor);
        this.recordDeleteImageView.setLayerColor("Box Grey.**", greyColor);
        this.recordDeleteImageView.setLayerColor("Line 1.**", background);
        this.recordDeleteImageView.setLayerColor("Line 2.**", background);
        this.recordDeleteImageView.setLayerColor("Line 3.**", background);
    }

    public void setFieldText(CharSequence text) {
        setFieldText(text, true);
    }

    public void setFieldText(CharSequence text, boolean ignoreChange) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return;
        }
        this.ignoreTextChange = ignoreChange;
        editTextCaption.setText(text);
        EditTextCaption editTextCaption2 = this.messageEditText;
        editTextCaption2.setSelection(editTextCaption2.getText().length());
        this.ignoreTextChange = false;
        if (ignoreChange && (chatActivityEnterViewDelegate = this.delegate) != null) {
            chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
        }
    }

    public void setSelection(int start) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return;
        }
        editTextCaption.setSelection(start, editTextCaption.length());
    }

    public int getCursorPosition() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        return editTextCaption.getSelectionStart();
    }

    public int getSelectionLength() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        try {
            return editTextCaption.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    public void replaceWithText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setFieldFocused() {
        AccessibilityManager am = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !am.isTouchExplorationEnabled()) {
            try {
                this.messageEditText.requestFocus();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void setFieldFocused(boolean focus) {
        AccessibilityManager am = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText == null || am.isTouchExplorationEnabled()) {
            return;
        }
        if (focus) {
            if (this.searchingType == 0 && !this.messageEditText.isFocused()) {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda37
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterView.this.m2342xffa0deb7();
                    }
                };
                this.focusRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 600L);
                return;
            }
            return;
        }
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null || !editTextCaption.isFocused()) {
            return;
        }
        if (!this.keyboardVisible || this.isPaused) {
            this.messageEditText.clearFocus();
        }
    }

    /* renamed from: lambda$setFieldFocused$44$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2342xffa0deb7() {
        boolean allowFocus;
        EditTextCaption editTextCaption;
        this.focusRunnable = null;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if (activity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) activity;
                View layout = launchActivity.getLayersActionBarLayout();
                allowFocus = layout == null || layout.getVisibility() != 0;
            } else {
                allowFocus = true;
            }
        } else {
            allowFocus = true;
        }
        if (!this.isPaused && allowFocus && (editTextCaption = this.messageEditText) != null) {
            try {
                editTextCaption.requestFocus();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public boolean hasText() {
        EditTextCaption editTextCaption = this.messageEditText;
        return editTextCaption != null && editTextCaption.length() > 0;
    }

    public EditTextCaption getEditField() {
        return this.messageEditText;
    }

    public CharSequence getDraftMessage() {
        if (this.editingMessageObject != null) {
            if (!TextUtils.isEmpty(this.draftMessage)) {
                return this.draftMessage;
            }
            return null;
        } else if (!hasText()) {
            return null;
        } else {
            return this.messageEditText.getText();
        }
    }

    public CharSequence getFieldText() {
        if (hasText()) {
            return this.messageEditText.getText();
        }
        return null;
    }

    public void updateScheduleButton(boolean animated) {
        ImageView imageView;
        ImageView imageView2;
        boolean notifyVisible = false;
        int i = 0;
        if (DialogObject.isChatDialog(this.dialog_id)) {
            TLRPC.Chat currentChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            this.silent = notificationsSettings.getBoolean("silent_" + this.dialog_id, false);
            this.canWriteToChannel = ChatObject.isChannel(currentChat) && (currentChat.creator || (currentChat.admin_rights != null && currentChat.admin_rights.post_messages)) && !currentChat.megagroup;
            if (this.notifyButton != null) {
                notifyVisible = this.canWriteToChannel;
                if (this.notifySilentDrawable == null) {
                    this.notifySilentDrawable = new CrossOutDrawable(getContext(), R.drawable.input_notify_on, Theme.key_chat_messagePanelIcons);
                }
                this.notifySilentDrawable.setCrossOut(this.silent, false);
                this.notifyButton.setImageDrawable(this.notifySilentDrawable);
            }
            LinearLayout linearLayout = this.attachLayout;
            if (linearLayout != null) {
                updateFieldRight(linearLayout.getVisibility() == 0 ? 1 : 0);
            }
        }
        boolean hasScheduled = this.delegate != null && !isInScheduleMode() && this.delegate.hasScheduledMessages();
        final boolean visible = hasScheduled && !this.scheduleButtonHidden && !this.recordingAudioVideo;
        ImageView imageView3 = this.scheduledButton;
        float f = 96.0f;
        if (imageView3 != null) {
            if ((imageView3.getTag() != null && visible) || (this.scheduledButton.getTag() == null && !visible)) {
                if (this.notifyButton != null) {
                    if (hasScheduled || !notifyVisible || this.scheduledButton.getVisibility() == 0) {
                        i = 8;
                    }
                    int newVisibility = i;
                    if (newVisibility != this.notifyButton.getVisibility()) {
                        this.notifyButton.setVisibility(newVisibility);
                        LinearLayout linearLayout2 = this.attachLayout;
                        if (linearLayout2 != null) {
                            ImageView imageView4 = this.botButton;
                            if ((imageView4 == null || imageView4.getVisibility() == 8) && ((imageView2 = this.notifyButton) == null || imageView2.getVisibility() == 8)) {
                                f = 48.0f;
                            }
                            linearLayout2.setPivotX(AndroidUtilities.dp(f));
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            this.scheduledButton.setTag(visible ? 1 : null);
        }
        AnimatorSet animatorSet = this.scheduledButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.scheduledButtonAnimation = null;
        }
        float f2 = 0.1f;
        if (!animated || notifyVisible) {
            ImageView imageView5 = this.scheduledButton;
            if (imageView5 != null) {
                imageView5.setVisibility(visible ? 0 : 8);
                this.scheduledButton.setAlpha(visible ? 1.0f : 0.0f);
                this.scheduledButton.setScaleX(visible ? 1.0f : 0.1f);
                ImageView imageView6 = this.scheduledButton;
                if (visible) {
                    f2 = 1.0f;
                }
                imageView6.setScaleY(f2);
                ImageView imageView7 = this.notifyButton;
                if (imageView7 != null) {
                    if (!notifyVisible || this.scheduledButton.getVisibility() == 0) {
                        i = 8;
                    }
                    imageView7.setVisibility(i);
                }
            }
        } else {
            ImageView imageView8 = this.scheduledButton;
            if (imageView8 != null) {
                if (visible) {
                    imageView8.setVisibility(0);
                }
                this.scheduledButton.setPivotX(AndroidUtilities.dp(24.0f));
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.scheduledButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView9 = this.scheduledButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = visible ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView9, property, fArr);
                ImageView imageView10 = this.scheduledButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = visible ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView10, property2, fArr2);
                ImageView imageView11 = this.scheduledButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (visible) {
                    f2 = 1.0f;
                }
                fArr3[0] = f2;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView11, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.scheduledButtonAnimation.setDuration(180L);
                this.scheduledButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.53
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ChatActivityEnterView.this.scheduledButtonAnimation = null;
                        if (!visible) {
                            ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                        }
                    }
                });
                this.scheduledButtonAnimation.start();
            }
        }
        LinearLayout linearLayout3 = this.attachLayout;
        if (linearLayout3 != null) {
            ImageView imageView12 = this.botButton;
            if ((imageView12 == null || imageView12.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) {
                f = 48.0f;
            }
            linearLayout3.setPivotX(AndroidUtilities.dp(f));
        }
    }

    public void updateSendAsButton() {
        updateSendAsButton(true);
    }

    public void updateSendAsButton(boolean animated) {
        TLRPC.Peer defPeer;
        ImageView[] imageViewArr;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && this.delegate != null) {
            TLRPC.ChatFull full = chatActivity.getMessagesController().getChatFull(-this.dialog_id);
            TLRPC.Peer defPeer2 = full != null ? full.default_send_as : null;
            if (defPeer2 == null && this.delegate.getSendAsPeers() != null && !this.delegate.getSendAsPeers().peers.isEmpty()) {
                defPeer = this.delegate.getSendAsPeers().peers.get(0);
            } else {
                defPeer = defPeer2;
            }
            if (defPeer != null) {
                if (defPeer.channel_id != 0) {
                    TLRPC.Chat ch = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(defPeer.channel_id));
                    if (ch != null) {
                        this.senderSelectView.setAvatar(ch);
                    }
                } else {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(defPeer.user_id));
                    if (user != null) {
                        this.senderSelectView.setAvatar(user);
                    }
                }
            }
            boolean z = true;
            boolean wasVisible = this.senderSelectView.getVisibility() == 0;
            if (defPeer == null || ((this.delegate.getSendAsPeers() != null && this.delegate.getSendAsPeers().peers.size() <= 1) || isEditingMessage() || isRecordingAudioVideo() || this.recordedAudioPanel.getVisibility() == 0)) {
                z = false;
            }
            final boolean isVisible = z;
            int pad = AndroidUtilities.dp(2.0f);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.senderSelectView.getLayoutParams();
            float f = 1.0f;
            float translationX = 0.0f;
            final float startAlpha = isVisible ? 0.0f : 1.0f;
            final float startX = isVisible ? ((-this.senderSelectView.getLayoutParams().width) - params.leftMargin) - pad : 0.0f;
            if (!isVisible) {
                f = 0.0f;
            }
            final float endAlpha = f;
            final float endX = isVisible ? 0.0f : ((-this.senderSelectView.getLayoutParams().width) - params.leftMargin) - pad;
            if (wasVisible != isVisible) {
                ValueAnimator animator = (ValueAnimator) this.senderSelectView.getTag();
                if (animator != null) {
                    animator.cancel();
                    this.senderSelectView.setTag(null);
                }
                if (this.parentFragment.getOtherSameChatsDiff() == 0 && this.parentFragment.fragmentOpened && animated) {
                    ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
                    this.senderSelectView.setTranslationX(startX);
                    this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda55
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.m2348x6afb3626(startAlpha, endAlpha, startX, endX, valueAnimator);
                        }
                    });
                    final float endX2 = startAlpha;
                    anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.54
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            ImageView[] imageViewArr2;
                            if (isVisible) {
                                ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                            }
                            ChatActivityEnterView.this.senderSelectView.setAlpha(endX2);
                            ChatActivityEnterView.this.senderSelectView.setTranslationX(startX);
                            for (ImageView emoji : ChatActivityEnterView.this.emojiButton) {
                                emoji.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                            }
                            ChatActivityEnterView.this.messageEditText.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                            if (ChatActivityEnterView.this.botCommandsMenuButton.getTag() == null) {
                                ChatActivityEnterView.this.animationParamsX.clear();
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            ImageView[] imageViewArr2;
                            if (!isVisible) {
                                ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                                for (ImageView emoji : ChatActivityEnterView.this.emojiButton) {
                                    emoji.setTranslationX(0.0f);
                                }
                                ChatActivityEnterView.this.messageEditText.setTranslationX(0.0f);
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animation) {
                            ImageView[] imageViewArr2;
                            if (isVisible) {
                                ChatActivityEnterView.this.senderSelectView.setVisibility(0);
                            } else {
                                ChatActivityEnterView.this.senderSelectView.setVisibility(8);
                            }
                            ChatActivityEnterView.this.senderSelectView.setAlpha(endAlpha);
                            ChatActivityEnterView.this.senderSelectView.setTranslationX(endX);
                            for (ImageView emoji : ChatActivityEnterView.this.emojiButton) {
                                emoji.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                            }
                            ChatActivityEnterView.this.messageEditText.setTranslationX(ChatActivityEnterView.this.senderSelectView.getTranslationX());
                            ChatActivityEnterView.this.requestLayout();
                        }
                    });
                    anim.start();
                    this.senderSelectView.setTag(anim);
                    return;
                }
                this.senderSelectView.setVisibility(isVisible ? 0 : 8);
                this.senderSelectView.setTranslationX(endX);
                if (isVisible) {
                    translationX = endX;
                }
                for (ImageView emoji : this.emojiButton) {
                    emoji.setTranslationX(translationX);
                }
                this.messageEditText.setTranslationX(translationX);
                this.senderSelectView.setAlpha(endAlpha);
                this.senderSelectView.setTag(null);
            }
        }
    }

    /* renamed from: lambda$updateSendAsButton$45$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2348x6afb3626(float startAlpha, float endAlpha, float startX, float endX, ValueAnimator animation) {
        ImageView[] imageViewArr;
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.senderSelectView.setAlpha(((endAlpha - startAlpha) * val) + startAlpha);
        this.senderSelectView.setTranslationX(((endX - startX) * val) + startX);
        for (ImageView emoji : this.emojiButton) {
            emoji.setTranslationX(this.senderSelectView.getTranslationX());
        }
        this.messageEditText.setTranslationX(this.senderSelectView.getTranslationX());
    }

    public boolean onBotWebViewBackPressed() {
        BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
        return botWebViewMenuContainer != null && botWebViewMenuContainer.onBackPressed();
    }

    public boolean hasBotWebView() {
        return this.botMenuButtonType == BotMenuButtonType.WEB_VIEW;
    }

    private void updateBotButton(boolean animated) {
        ImageView imageView;
        if (this.botButton == null) {
            return;
        }
        if (!this.parentFragment.openAnimationEnded) {
            animated = false;
        }
        boolean hasBotWebView = hasBotWebView();
        boolean canShowBotsMenu = this.botMenuButtonType != BotMenuButtonType.NO_BUTTON && this.dialog_id > 0;
        boolean wasVisible = this.botButton.getVisibility() == 0;
        if (!hasBotWebView && !this.hasBotCommands && this.botReplyMarkup == null) {
            this.botButton.setVisibility(8);
        } else if (this.botReplyMarkup != null) {
            if (isPopupShowing() && this.currentPopupContentType == 1) {
                if (this.botButton.getVisibility() != 8) {
                    this.botButton.setVisibility(8);
                }
            } else {
                if (this.botButton.getVisibility() != 0) {
                    this.botButton.setVisibility(0);
                }
                this.botButtonDrawable.setIcon(R.drawable.input_bot2, true);
                this.botButton.setContentDescription(LocaleController.getString("AccDescrBotKeyboard", R.string.AccDescrBotKeyboard));
            }
        } else if (canShowBotsMenu) {
            this.botButton.setVisibility(8);
        } else {
            this.botButtonDrawable.setIcon(R.drawable.input_bot1, true);
            this.botButton.setContentDescription(LocaleController.getString("AccDescrBotCommands", R.string.AccDescrBotCommands));
            this.botButton.setVisibility(0);
        }
        boolean wasWebView = this.botCommandsMenuButton.isWebView;
        this.botCommandsMenuButton.setWebView(this.botMenuButtonType == BotMenuButtonType.WEB_VIEW);
        boolean textChanged = this.botCommandsMenuButton.setMenuText(this.botMenuButtonType == BotMenuButtonType.COMMANDS ? LocaleController.getString((int) R.string.BotsMenuTitle) : this.botMenuWebViewTitle);
        AndroidUtilities.updateViewVisibilityAnimated(this.botCommandsMenuButton, canShowBotsMenu, 0.5f, animated);
        boolean changed = ((this.botButton.getVisibility() == 0) == wasVisible && !textChanged && wasWebView == this.botCommandsMenuButton.isWebView) ? false : true;
        if (changed && animated) {
            beginDelayedTransition();
            boolean show = this.botButton.getVisibility() == 0;
            if (show != wasVisible) {
                this.botButton.setVisibility(0);
                if (show) {
                    this.botButton.setAlpha(0.0f);
                    this.botButton.setScaleX(0.1f);
                    this.botButton.setScaleY(0.1f);
                } else if (!show) {
                    this.botButton.setAlpha(1.0f);
                    this.botButton.setScaleX(1.0f);
                    this.botButton.setScaleY(1.0f);
                }
                AndroidUtilities.updateViewVisibilityAnimated(this.botButton, show, 0.1f, true);
            }
        }
        updateFieldRight(2);
        LinearLayout linearLayout = this.attachLayout;
        ImageView imageView2 = this.botButton;
        linearLayout.setPivotX(AndroidUtilities.dp(((imageView2 == null || imageView2.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) ? 48.0f : 96.0f));
    }

    public boolean isRtlText() {
        try {
            return this.messageEditText.getLayout().getParagraphDirection(0) == -1;
        } catch (Throwable th) {
            return false;
        }
    }

    public void updateBotWebView(boolean animated) {
        this.botCommandsMenuButton.setWebView(hasBotWebView());
        updateBotButton(animated);
    }

    public void setBotsCount(int count, boolean hasCommands, boolean animated) {
        this.botCount = count;
        if (this.hasBotCommands != hasCommands) {
            this.hasBotCommands = hasCommands;
            updateBotButton(animated);
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean openKeyboard) {
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && messageObject2 == this.botButtonsMessageObject && messageObject2 != messageObject) {
            this.botMessageObject = messageObject;
        } else if (this.botButton == null) {
        } else {
            MessageObject messageObject3 = this.botButtonsMessageObject;
            if (messageObject3 != null && messageObject3 == messageObject) {
                return;
            }
            if (messageObject3 == null && messageObject == null) {
                return;
            }
            if (this.botKeyboardView == null) {
                BotKeyboardView botKeyboardView = new BotKeyboardView(this.parentActivity, this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatActivityEnterView.55
                    @Override // android.view.View
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        if (ChatActivityEnterView.this.panelAnimation != null && ChatActivityEnterView.this.animatingContentType == 1) {
                            ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(translationY);
                        }
                    }
                };
                this.botKeyboardView = botKeyboardView;
                botKeyboardView.setVisibility(8);
                this.botKeyboardViewVisible = false;
                this.botKeyboardView.setDelegate(new BotKeyboardView.BotKeyboardViewDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda52
                    @Override // org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate
                    public final void didPressedButton(TLRPC.KeyboardButton keyboardButton) {
                        ChatActivityEnterView.this.m2340x30a8cbef(keyboardButton);
                    }
                });
                SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                sizeNotifierFrameLayout.addView(this.botKeyboardView, sizeNotifierFrameLayout.getChildCount() - 1);
            }
            this.botButtonsMessageObject = messageObject;
            this.botReplyMarkup = (messageObject == null || !(messageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardMarkup)) ? null : (TLRPC.TL_replyKeyboardMarkup) messageObject.messageOwner.reply_markup;
            this.botKeyboardView.setPanelHeight(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight);
            if (this.botReplyMarkup != null) {
                SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
                boolean showPopup = true;
                if (this.botButtonsMessageObject != this.replyingMessageObject && this.botReplyMarkup.single_use) {
                    if (preferences.getInt("answered_" + this.dialog_id, 0) == messageObject.getId()) {
                        showPopup = false;
                    }
                }
                if (showPopup && this.messageEditText.length() == 0 && !isPopupShowing()) {
                    showPopup(1, 1);
                }
                this.botKeyboardView.setButtons(this.botReplyMarkup);
            } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                if (openKeyboard) {
                    this.clearBotButtonsOnKeyboardOpen = true;
                    openKeyboardInternal();
                } else {
                    showPopup(0, 1);
                }
            }
            updateBotButton(true);
        }
    }

    /* renamed from: lambda$setButtons$46$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2340x30a8cbef(TLRPC.KeyboardButton button) {
        MessageObject object = this.replyingMessageObject;
        if (object == null) {
            object = DialogObject.isChatDialog(this.dialog_id) ? this.botButtonsMessageObject : null;
        }
        MessageObject messageObject = this.replyingMessageObject;
        if (messageObject == null) {
            messageObject = this.botButtonsMessageObject;
        }
        boolean open = didPressedBotButton(button, object, messageObject);
        if (this.replyingMessageObject != null) {
            openKeyboardInternal();
            setButtons(this.botMessageObject, false);
        } else {
            MessageObject messageObject2 = this.botButtonsMessageObject;
            if (messageObject2 != null && messageObject2.messageOwner.reply_markup.single_use) {
                if (open) {
                    openKeyboardInternal();
                } else {
                    showPopup(0, 0);
                }
                SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putInt("answered_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
            }
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(null, true, 0);
        }
    }

    public boolean didPressedBotButton(final TLRPC.KeyboardButton button, final MessageObject replyMessageObject, final MessageObject messageObject) {
        if (button == null || messageObject == null) {
            return false;
        }
        if (button instanceof TLRPC.TL_keyboardButton) {
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(button.text, this.dialog_id, replyMessageObject, getThreadMessage(), null, false, null, null, null, true, 0, null);
        } else if (button instanceof TLRPC.TL_keyboardButtonUrl) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, button.url, false, true, this.resourcesProvider);
        } else if (button instanceof TLRPC.TL_keyboardButtonRequestPhone) {
            this.parentFragment.shareMyContact(2, messageObject);
        } else {
            Boolean bool = null;
            if (button instanceof TLRPC.TL_keyboardButtonRequestPoll) {
                ChatActivity chatActivity = this.parentFragment;
                if ((button.flags & 1) != 0) {
                    bool = Boolean.valueOf(button.quiz);
                }
                chatActivity.openPollCreate(bool);
                return false;
            } else if ((button instanceof TLRPC.TL_keyboardButtonWebView) || (button instanceof TLRPC.TL_keyboardButtonSimpleWebView)) {
                final long botId = messageObject.messageOwner.via_bot_id != 0 ? messageObject.messageOwner.via_bot_id : messageObject.messageOwner.from_id.user_id;
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(botId));
                final Runnable onRequestWebView = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.56
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ChatActivityEnterView.this.sizeNotifierLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                            AndroidUtilities.hideKeyboard(ChatActivityEnterView.this);
                            AndroidUtilities.runOnUIThread(this, 150L);
                            return;
                        }
                        BotWebViewSheet webViewSheet = new BotWebViewSheet(ChatActivityEnterView.this.getContext(), ChatActivityEnterView.this.resourcesProvider);
                        webViewSheet.setParentActivity(ChatActivityEnterView.this.parentActivity);
                        int i = ChatActivityEnterView.this.currentAccount;
                        long j = messageObject.messageOwner.dialog_id;
                        long j2 = botId;
                        String str = button.text;
                        String str2 = button.url;
                        boolean z = button instanceof TLRPC.TL_keyboardButtonSimpleWebView;
                        MessageObject messageObject2 = replyMessageObject;
                        webViewSheet.requestWebView(i, j, j2, str, str2, z ? 1 : 0, messageObject2 != null ? messageObject2.messageOwner.id : 0, false);
                        webViewSheet.show();
                    }
                };
                if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, botId)) {
                    onRequestWebView.run();
                } else {
                    new AlertDialog.Builder(this.parentFragment.getParentActivity()).setTitle(LocaleController.getString((int) R.string.BotOpenPageTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotOpenPageMessage", R.string.BotOpenPageMessage, UserObject.getUserName(user)))).setPositiveButton(LocaleController.getString((int) R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda60
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatActivityEnterView.this.m2300x2184bbeb(onRequestWebView, botId, dialogInterface, i);
                        }
                    }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).show();
                }
            } else if (button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", R.string.ShareYouLocationInfo));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.m2301x132e620a(messageObject, button, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                this.parentFragment.showDialog(builder.create());
            } else if ((button instanceof TLRPC.TL_keyboardButtonCallback) || (button instanceof TLRPC.TL_keyboardButtonGame) || (button instanceof TLRPC.TL_keyboardButtonBuy) || (button instanceof TLRPC.TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject, button, this.parentFragment);
            } else if (button instanceof TLRPC.TL_keyboardButtonSwitchInline) {
                if (this.parentFragment.processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline) button)) {
                    return true;
                }
                if (button.same_peer) {
                    long uid = messageObject.messageOwner.from_id.user_id;
                    if (messageObject.messageOwner.via_bot_id != 0) {
                        uid = messageObject.messageOwner.via_bot_id;
                    }
                    TLRPC.User user2 = this.accountInstance.getMessagesController().getUser(Long.valueOf(uid));
                    if (user2 == null) {
                        return true;
                    }
                    setFieldText("@" + user2.username + " " + button.query);
                } else {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 1);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda56
                        @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                        public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            ChatActivityEnterView.this.m2302x4d80829(messageObject, button, dialogsActivity, arrayList, charSequence, z);
                        }
                    });
                    this.parentFragment.presentFragment(fragment);
                }
            } else if ((button instanceof TLRPC.TL_keyboardButtonUserProfile) && MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(button.user_id)) != null) {
                Bundle args2 = new Bundle();
                args2.putLong("user_id", button.user_id);
                this.parentFragment.presentFragment(new ProfileActivity(args2));
            }
        }
        return true;
    }

    /* renamed from: lambda$didPressedBotButton$47$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2300x2184bbeb(Runnable onRequestWebView, long botId, DialogInterface dialog, int which) {
        onRequestWebView.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, botId, true);
    }

    /* renamed from: lambda$didPressedBotButton$48$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2301x132e620a(MessageObject messageObject, TLRPC.KeyboardButton button, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT >= 23 && this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            this.pendingMessageObject = messageObject;
            this.pendingLocationButton = button;
            return;
        }
        SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, button);
    }

    /* renamed from: lambda$didPressedBotButton$49$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2302x4d80829(MessageObject messageObject, TLRPC.KeyboardButton button, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        long uid = messageObject.messageOwner.from_id.user_id;
        if (messageObject.messageOwner.via_bot_id != 0) {
            uid = messageObject.messageOwner.via_bot_id;
        }
        TLRPC.User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(uid));
        if (user == null) {
            fragment1.finishFragment();
            return;
        }
        long did = ((Long) dids.get(0)).longValue();
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        mediaDataController.saveDraft(did, 0, "@" + user.username + " " + button.query, null, null, true);
        if (did != this.dialog_id) {
            if (!DialogObject.isEncryptedDialog(did)) {
                Bundle args1 = new Bundle();
                if (DialogObject.isUserDialog(did)) {
                    args1.putLong("user_id", did);
                } else {
                    args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
                }
                if (!this.accountInstance.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                    return;
                }
                ChatActivity chatActivity = new ChatActivity(args1);
                if (this.parentFragment.presentFragment(chatActivity, true)) {
                    if (!AndroidUtilities.isTablet()) {
                        this.parentFragment.removeSelfFromStack();
                        return;
                    }
                    return;
                }
                fragment1.finishFragment();
                return;
            }
            fragment1.finishFragment();
            return;
        }
        fragment1.finishFragment();
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    public SizeNotifierFrameLayout getSizeNotifierLayout() {
        return this.sizeNotifierLayout;
    }

    private void createEmojiView() {
        if (this.emojiView != null) {
            return;
        }
        EmojiView emojiView = new EmojiView(this.allowStickers, this.allowGifs, getContext(), true, this.info, this.sizeNotifierLayout, this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatActivityEnterView.57
            @Override // org.telegram.ui.Components.EmojiView, android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (ChatActivityEnterView.this.panelAnimation != null && ChatActivityEnterView.this.animatingContentType == 0) {
                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(translationY);
                }
            }
        };
        this.emojiView = emojiView;
        emojiView.setVisibility(8);
        this.emojiView.setShowing(false);
        this.emojiView.setDelegate(new AnonymousClass58());
        this.emojiView.setDragListener(new EmojiView.DragListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.59
            int initialOffset;
            boolean wasExpanded;

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragStart() {
                if (allowDragging()) {
                    if (ChatActivityEnterView.this.stickersExpansionAnim != null) {
                        ChatActivityEnterView.this.stickersExpansionAnim.cancel();
                    }
                    ChatActivityEnterView.this.stickersDragging = true;
                    this.wasExpanded = ChatActivityEnterView.this.stickersExpanded;
                    ChatActivityEnterView.this.stickersExpanded = true;
                    int i = 0;
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    int height = chatActivityEnterView.sizeNotifierLayout.getHeight();
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    chatActivityEnterView.stickersExpandedHeight = (((height - i) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    if (ChatActivityEnterView.this.searchingType == 2) {
                        ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                        chatActivityEnterView2.stickersExpandedHeight = Math.min(chatActivityEnterView2.stickersExpandedHeight, AndroidUtilities.dp(120.0f) + (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
                    }
                    ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                    ChatActivityEnterView.this.emojiView.setLayerType(2, null);
                    ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                    ChatActivityEnterView.this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                    this.initialOffset = (int) ChatActivityEnterView.this.getTranslationY();
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onStickersExpandedChange();
                    }
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragEnd(float velocity) {
                if (allowDragging()) {
                    ChatActivityEnterView.this.stickersDragging = false;
                    if ((this.wasExpanded && velocity >= AndroidUtilities.dp(200.0f)) || ((!this.wasExpanded && velocity <= AndroidUtilities.dp(-200.0f)) || ((this.wasExpanded && ChatActivityEnterView.this.stickersExpansionProgress <= 0.6f) || (!this.wasExpanded && ChatActivityEnterView.this.stickersExpansionProgress >= 0.4f)))) {
                        ChatActivityEnterView.this.setStickersExpanded(!this.wasExpanded, true, true);
                    } else {
                        ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, true);
                    }
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragCancel() {
                if (ChatActivityEnterView.this.stickersTabOpen) {
                    ChatActivityEnterView.this.stickersDragging = false;
                    ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, false);
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDrag(int offset) {
                if (!allowDragging()) {
                    return;
                }
                int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                int offset2 = Math.max(Math.min(offset + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - origHeight));
                ChatActivityEnterView.this.emojiView.setTranslationY(offset2);
                ChatActivityEnterView.this.setTranslationY(offset2);
                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                chatActivityEnterView.stickersExpansionProgress = offset2 / (-(chatActivityEnterView.stickersExpandedHeight - origHeight));
                ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
            }

            private boolean allowDragging() {
                return ChatActivityEnterView.this.stickersTabOpen && (ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers() && !ChatActivityEnterView.this.waitingForKeyboardOpen;
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
        checkChannelRights();
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView$58 */
    /* loaded from: classes5.dex */
    public class AnonymousClass58 implements EmojiView.EmojiViewDelegate {
        AnonymousClass58() {
            ChatActivityEnterView.this = this$0;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean onBackspace() {
            if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                return false;
            }
            ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onEmojiSelected(String symbol) {
            int i = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
            if (i < 0) {
                i = 0;
            }
            try {
                try {
                    ChatActivityEnterView.this.innerTextChange = 2;
                    CharSequence localCharSequence = Emoji.replaceEmoji(symbol, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                    int j = localCharSequence.length() + i;
                    ChatActivityEnterView.this.messageEditText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } finally {
                ChatActivityEnterView.this.innerTextChange = 0;
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickerSelected(View view, TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean notify, int scheduleDate) {
            if (ChatActivityEnterView.this.trendingStickersAlert != null) {
                ChatActivityEnterView.this.trendingStickersAlert.dismiss();
                ChatActivityEnterView.this.trendingStickersAlert = null;
            }
            if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                if (ChatActivityEnterView.this.stickersExpanded) {
                    if (ChatActivityEnterView.this.searchingType != 0) {
                        ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                        ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(sticker));
                        ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                    }
                    ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                }
                ChatActivityEnterView.this.m2335xe7085eb6(sticker, query, parent, sendAnimationData, false, notify, scheduleDate);
                if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id) && MessageObject.isGifDocument(sticker)) {
                    ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(parent, sticker);
                }
            } else if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickersSettingsClick() {
            if (ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        /* renamed from: onGifSelected */
        public void m2358x1774b15d(final View view, final Object gif, final String query, final Object parent, boolean notify, int scheduleDate) {
            int i;
            if (!isInScheduleMode() || scheduleDate != 0) {
                if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                    if (ChatActivityEnterView.this.stickersExpanded) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                    }
                    if (gif instanceof TLRPC.Document) {
                        TLRPC.Document document = (TLRPC.Document) gif;
                        i = scheduleDate;
                        SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, query, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), parent, null, notify, scheduleDate);
                        MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000), true);
                        if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                            ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(parent, document);
                        }
                    } else {
                        i = scheduleDate;
                        if (gif instanceof TLRPC.BotInlineResult) {
                            TLRPC.BotInlineResult result = (TLRPC.BotInlineResult) gif;
                            if (result.document != null) {
                                MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(result.document, (int) (System.currentTimeMillis() / 1000), false);
                                if (DialogObject.isEncryptedDialog(ChatActivityEnterView.this.dialog_id)) {
                                    ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(parent, result.document);
                                }
                            }
                            TLRPC.User user = (TLRPC.User) parent;
                            HashMap<String, String> params = new HashMap<>();
                            params.put("id", result.id);
                            params.put("query_id", "" + result.query_id);
                            params.put("force_gif", IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
                            SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, result, params, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), notify, scheduleDate);
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.setSearchingTypeInternal(0, true);
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                        }
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null, notify, i);
                        return;
                    }
                    return;
                } else if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    return;
                } else {
                    return;
                }
            }
            AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$58$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i2) {
                    ChatActivityEnterView.AnonymousClass58.this.m2358x1774b15d(view, gif, query, parent, z, i2);
                }
            }, ChatActivityEnterView.this.resourcesProvider);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onTabOpened(int type) {
            ChatActivityEnterView.this.delegate.onStickersTab(type == 3);
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            chatActivityEnterView.post(chatActivityEnterView.updateExpandabilityRunnable);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onClearEmojiRecent() {
            if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentActivity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearRecentEmoji", R.string.ClearRecentEmoji));
            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$58$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.AnonymousClass58.this.m2357x718147cb(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
        }

        /* renamed from: lambda$onClearEmojiRecent$1$org-telegram-ui-Components-ChatActivityEnterView$58 */
        public /* synthetic */ void m2357x718147cb(DialogInterface dialogInterface, int i) {
            ChatActivityEnterView.this.emojiView.clearRecentEmoji();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
            if (ChatActivityEnterView.this.trendingStickersAlert == null || ChatActivityEnterView.this.trendingStickersAlert.isDismissed()) {
                if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentActivity == null) {
                    return;
                }
                if (stickerSet != null) {
                    inputStickerSet = new TLRPC.TL_inputStickerSetID();
                    inputStickerSet.access_hash = stickerSet.access_hash;
                    inputStickerSet.id = stickerSet.id;
                }
                ChatActivity chatActivity = ChatActivityEnterView.this.parentFragment;
                Activity activity = ChatActivityEnterView.this.parentActivity;
                ChatActivity chatActivity2 = ChatActivityEnterView.this.parentFragment;
                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                chatActivity.showDialog(new StickersAlert(activity, chatActivity2, inputStickerSet, null, chatActivityEnterView, chatActivityEnterView.resourcesProvider));
                return;
            }
            ChatActivityEnterView.this.trendingStickersAlert.getLayout().showStickerSet(stickerSet, inputStickerSet);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet) {
            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, stickerSet, 2, ChatActivityEnterView.this.parentFragment, false, false);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).toggleStickerSet(ChatActivityEnterView.this.parentActivity, stickerSet, 0, ChatActivityEnterView.this.parentFragment, false, false);
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onStickersGroupClick(long chatId) {
            if (ChatActivityEnterView.this.parentFragment != null) {
                if (AndroidUtilities.isTablet()) {
                    ChatActivityEnterView.this.hidePopup(false);
                }
                GroupStickersActivity fragment = new GroupStickersActivity(chatId);
                fragment.setInfo(ChatActivityEnterView.this.info);
                ChatActivityEnterView.this.parentFragment.presentFragment(fragment);
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onSearchOpenClose(int type) {
            ChatActivityEnterView.this.setSearchingTypeInternal(type, true);
            if (type != 0) {
                ChatActivityEnterView.this.setStickersExpanded(true, true, false);
            }
            if (ChatActivityEnterView.this.emojiTabOpen && ChatActivityEnterView.this.searchingType == 2) {
                ChatActivityEnterView.this.checkStickresExpandHeight();
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isSearchOpened() {
            return ChatActivityEnterView.this.searchingType != 0;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isExpanded() {
            return ChatActivityEnterView.this.stickersExpanded;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean canSchedule() {
            return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.canScheduleMessage();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isInScheduleMode() {
            return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.isInScheduleMode();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public long getDialogId() {
            return ChatActivityEnterView.this.dialog_id;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public int getThreadId() {
            return ChatActivityEnterView.this.getThreadMessageId();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void showTrendingStickersAlert(TrendingStickersLayout layout) {
            if (ChatActivityEnterView.this.parentActivity != null && ChatActivityEnterView.this.parentFragment != null) {
                ChatActivityEnterView.this.trendingStickersAlert = new TrendingStickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, layout, ChatActivityEnterView.this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatActivityEnterView.58.1
                    @Override // org.telegram.ui.Components.TrendingStickersAlert, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
                    public void dismiss() {
                        super.dismiss();
                        if (ChatActivityEnterView.this.trendingStickersAlert == this) {
                            ChatActivityEnterView.this.trendingStickersAlert = null;
                        }
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onTrendingStickersShowed(false);
                        }
                    }
                };
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onTrendingStickersShowed(true);
                }
                ChatActivityEnterView.this.trendingStickersAlert.show();
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void invalidateEnterView() {
            ChatActivityEnterView.this.invalidate();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public float getProgressToSearchOpened() {
            return ChatActivityEnterView.this.searchToOpenProgress;
        }
    }

    @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
    /* renamed from: onStickerSelected */
    public void m2335xe7085eb6(final TLRPC.Document sticker, final String query, final Object parent, final MessageObject.SendAnimationData sendAnimationData, final boolean clearsInputField, boolean notify, int scheduleDate) {
        if (isInScheduleMode() && scheduleDate == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda49
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.m2335xe7085eb6(sticker, query, parent, sendAnimationData, clearsInputField, z, i);
                }
            }, this.resourcesProvider);
        } else if (this.slowModeTimer > 0 && !isInScheduleMode()) {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
            }
        } else {
            if (this.searchingType != 0) {
                setSearchingTypeInternal(0, true);
                this.emojiView.closeSearch(true);
                this.emojiView.hideSearchKeyboard();
            }
            setStickersExpanded(false, true, false);
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(sticker, query, this.dialog_id, this.replyingMessageObject, getThreadMessage(), parent, sendAnimationData, notify, scheduleDate);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null) {
                chatActivityEnterViewDelegate2.onMessageSend(null, true, scheduleDate);
            }
            if (clearsInputField) {
                setFieldText("");
            }
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, parent, sticker, (int) (System.currentTimeMillis() / 1000), false);
        }
    }

    @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
    public boolean canSchedule() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.canScheduleMessage();
    }

    @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
    public boolean isInScheduleMode() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.isInScheduleMode();
    }

    public void addStickerToRecent(TLRPC.Document sticker) {
        createEmojiView();
        this.emojiView.addRecentSticker(sticker);
    }

    public void hideEmojiView() {
        EmojiView emojiView;
        if (!this.emojiViewVisible && (emojiView = this.emojiView) != null && emojiView.getVisibility() != 8) {
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView.setVisibility(8);
            this.emojiView.setShowing(false);
        }
    }

    public void showEmojiView() {
        showPopup(1, 0);
    }

    public void showPopup(int show, int contentType) {
        showPopup(show, contentType, true);
    }

    private void showPopup(final int show, int contentType, boolean allowAnimation) {
        int previousHeight;
        SizeNotifierFrameLayout sizeNotifierFrameLayout;
        if (show == 2) {
            return;
        }
        if (show == 1) {
            if (contentType == 0 && this.emojiView == null) {
                if (this.parentActivity == null) {
                    return;
                }
                createEmojiView();
            }
            View currentView = null;
            int previousHeight2 = 0;
            if (contentType == 0) {
                if (this.emojiView.getParent() == null) {
                    this.sizeNotifierLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 5);
                }
                boolean z = this.emojiViewVisible && this.emojiView.getVisibility() == 0;
                this.emojiView.setVisibility(0);
                this.emojiViewVisible = true;
                BotKeyboardView botKeyboardView = this.botKeyboardView;
                if (botKeyboardView != null && botKeyboardView.getVisibility() != 8) {
                    this.botKeyboardView.setVisibility(8);
                    this.botKeyboardViewVisible = false;
                    previousHeight2 = this.botKeyboardView.getMeasuredHeight();
                }
                this.emojiView.setShowing(true);
                currentView = this.emojiView;
                this.animatingContentType = 0;
                previousHeight = previousHeight2;
            } else if (contentType != 1) {
                previousHeight = 0;
            } else {
                boolean z2 = this.botKeyboardViewVisible && this.botKeyboardView.getVisibility() == 0;
                this.botKeyboardViewVisible = true;
                EmojiView emojiView = this.emojiView;
                if (emojiView != null && emojiView.getVisibility() != 8) {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                    this.emojiView.setShowing(false);
                    this.emojiViewVisible = false;
                    previousHeight2 = this.emojiView.getMeasuredHeight();
                }
                this.botKeyboardView.setVisibility(0);
                currentView = this.botKeyboardView;
                this.animatingContentType = 1;
                previousHeight = previousHeight2;
            }
            this.currentPopupContentType = contentType;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (contentType == 1) {
                currentHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), currentHeight);
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.setPanelHeight(currentHeight);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout2 != null) {
                this.emojiPadding = currentHeight;
                sizeNotifierFrameLayout2.requestLayout();
                setEmojiButtonImage(true, true);
                updateBotButton(true);
                onWindowSizeChanged();
                if (this.smoothKeyboard && !this.keyboardVisible && currentHeight != previousHeight && allowAnimation) {
                    this.panelAnimation = new AnimatorSet();
                    currentView.setTranslationY(currentHeight - previousHeight);
                    this.panelAnimation.playTogether(ObjectAnimator.ofFloat(currentView, View.TRANSLATION_Y, currentHeight - previousHeight, 0.0f));
                    this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    this.panelAnimation.setDuration(250L);
                    this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.60
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            ChatActivityEnterView.this.panelAnimation = null;
                            if (ChatActivityEnterView.this.delegate != null) {
                                ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                            }
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                            ChatActivityEnterView.this.requestLayout();
                        }
                    });
                    AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                    requestLayout();
                }
            }
        } else {
            if (this.emojiButton != null) {
                setEmojiButtonImage(false, true);
            }
            this.currentPopupContentType = -1;
            if (this.emojiView != null) {
                if (show != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    if (this.smoothKeyboard && !this.keyboardVisible && !this.stickersExpanded) {
                        this.emojiViewVisible = true;
                        this.animatingContentType = 0;
                        this.emojiView.setShowing(false);
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.panelAnimation = animatorSet;
                        animatorSet.playTogether(ObjectAnimator.ofFloat(this.emojiView, View.TRANSLATION_Y, this.emojiView.getMeasuredHeight()));
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250L);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.61
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (show == 0) {
                                    ChatActivityEnterView.this.emojiPadding = 0;
                                }
                                ChatActivityEnterView.this.panelAnimation = null;
                                if (ChatActivityEnterView.this.emojiView != null) {
                                    ChatActivityEnterView.this.emojiView.setTranslationY(0.0f);
                                    ChatActivityEnterView.this.emojiView.setVisibility(8);
                                    ChatActivityEnterView.this.sizeNotifierLayout.removeView(ChatActivityEnterView.this.emojiView);
                                    if (ChatActivityEnterView.this.removeEmojiViewAfterAnimation) {
                                        ChatActivityEnterView.this.removeEmojiViewAfterAnimation = false;
                                        ChatActivityEnterView.this.emojiView = null;
                                    }
                                }
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                        AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                        requestLayout();
                    } else {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                        if (chatActivityEnterViewDelegate != null) {
                            chatActivityEnterViewDelegate.bottomPanelTranslationYChanged(0.0f);
                        }
                        this.emojiPadding = 0;
                        this.sizeNotifierLayout.removeView(this.emojiView);
                        this.emojiView.setVisibility(8);
                        this.emojiView.setShowing(false);
                    }
                } else {
                    this.removeEmojiViewAfterAnimation = false;
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                    if (chatActivityEnterViewDelegate2 != null) {
                        chatActivityEnterViewDelegate2.bottomPanelTranslationYChanged(0.0f);
                    }
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView = null;
                }
                this.emojiViewVisible = false;
            }
            BotKeyboardView botKeyboardView3 = this.botKeyboardView;
            if (botKeyboardView3 != null && botKeyboardView3.getVisibility() == 0) {
                if (show != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    if (this.smoothKeyboard && !this.keyboardVisible) {
                        if (this.botKeyboardViewVisible) {
                            this.animatingContentType = 1;
                        }
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.panelAnimation = animatorSet2;
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.botKeyboardView, View.TRANSLATION_Y, this.botKeyboardView.getMeasuredHeight()));
                        this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                        this.panelAnimation.setDuration(250L);
                        this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.62
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (show == 0) {
                                    ChatActivityEnterView.this.emojiPadding = 0;
                                }
                                ChatActivityEnterView.this.panelAnimation = null;
                                ChatActivityEnterView.this.botKeyboardView.setTranslationY(0.0f);
                                ChatActivityEnterView.this.botKeyboardView.setVisibility(8);
                                NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                if (ChatActivityEnterView.this.delegate != null) {
                                    ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                }
                                ChatActivityEnterView.this.requestLayout();
                            }
                        });
                        this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                        AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                        requestLayout();
                    } else if (!this.waitingForKeyboardOpen) {
                        this.botKeyboardView.setVisibility(8);
                    }
                }
                this.botKeyboardViewVisible = false;
            }
            if (this.sizeNotifierLayout != null && !SharedConfig.smoothKeyboard && show == 0) {
                this.emojiPadding = 0;
                this.sizeNotifierLayout.requestLayout();
                onWindowSizeChanged();
            }
            updateBotButton(true);
        }
        if (this.stickersTabOpen || this.emojiTabOpen) {
            checkSendButton(true);
        }
        if (this.stickersExpanded && show != 1) {
            setStickersExpanded(false, false, false);
        }
        updateFieldHint(false);
        checkBotMenu();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v2 */
    /* JADX WARN: Type inference failed for: r15v3 */
    /* JADX WARN: Type inference failed for: r15v4 */
    /* JADX WARN: Type inference failed for: r15v8 */
    private void setEmojiButtonImage(boolean byOpen, boolean animated) {
        int currentPage;
        int currentPage2;
        FrameLayout frameLayout;
        boolean showingRecordInterface = this.recordInterfaceState == 1 || ((frameLayout = this.recordedAudioPanel) != null && frameLayout.getVisibility() == 0);
        ?? r15 = animated;
        if (showingRecordInterface) {
            this.emojiButton[0].setScaleX(0.0f);
            this.emojiButton[0].setScaleY(0.0f);
            this.emojiButton[0].setAlpha(0.0f);
            this.emojiButton[1].setScaleX(0.0f);
            this.emojiButton[1].setScaleY(0.0f);
            this.emojiButton[1].setAlpha(0.0f);
            r15 = 0;
        }
        if (r15 != 0 && this.currentEmojiIcon == -1) {
            r15 = 0;
        }
        if (byOpen && this.currentPopupContentType == 0) {
            currentPage = 0;
        } else {
            EmojiView emojiView = this.emojiView;
            if (emojiView == null) {
                currentPage2 = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
            } else {
                currentPage2 = emojiView.getCurrentPage();
            }
            if (currentPage2 == 0 || (!this.allowStickers && !this.allowGifs)) {
                currentPage = 1;
            } else if (currentPage2 == 1) {
                currentPage = 2;
            } else {
                currentPage = 3;
            }
        }
        if (this.currentEmojiIcon == currentPage) {
            return;
        }
        AnimatorSet animatorSet = this.emojiButtonAnimation;
        Integer num = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.emojiButtonAnimation = null;
        }
        if (currentPage == 0) {
            this.emojiButton[r15].setImageResource(R.drawable.input_keyboard);
        } else if (currentPage == 1) {
            this.emojiButton[r15].setImageResource(R.drawable.input_smile);
        } else if (currentPage == 2) {
            this.emojiButton[r15].setImageResource(R.drawable.input_sticker);
        } else if (currentPage == 3) {
            this.emojiButton[r15].setImageResource(R.drawable.input_gif);
        }
        ImageView[] imageViewArr = this.emojiButton;
        char c = r15 == true ? 1 : 0;
        char c2 = r15 == true ? 1 : 0;
        char c3 = r15 == true ? 1 : 0;
        ImageView imageView = imageViewArr[c];
        if (currentPage == 2) {
            num = 1;
        }
        imageView.setTag(num);
        this.currentEmojiIcon = currentPage;
        if (r15 != 0) {
            this.emojiButton[1].setVisibility(0);
            this.emojiButton[1].setAlpha(0.0f);
            this.emojiButton[1].setScaleX(0.1f);
            this.emojiButton[1].setScaleY(0.1f);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.emojiButtonAnimation = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.emojiButton[0], View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.emojiButton[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.emojiButton[1], View.ALPHA, 1.0f));
            this.emojiButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.63
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ChatActivityEnterView.this.emojiButtonAnimation)) {
                        ChatActivityEnterView.this.emojiButtonAnimation = null;
                        ImageView temp = ChatActivityEnterView.this.emojiButton[1];
                        ChatActivityEnterView.this.emojiButton[1] = ChatActivityEnterView.this.emojiButton[0];
                        ChatActivityEnterView.this.emojiButton[0] = temp;
                        ChatActivityEnterView.this.emojiButton[1].setVisibility(4);
                        ChatActivityEnterView.this.emojiButton[1].setAlpha(0.0f);
                        ChatActivityEnterView.this.emojiButton[1].setScaleX(0.1f);
                        ChatActivityEnterView.this.emojiButton[1].setScaleY(0.1f);
                    }
                }
            });
            this.emojiButtonAnimation.setDuration(150L);
            this.emojiButtonAnimation.start();
        }
        onEmojiIconChanged(currentPage);
    }

    protected void onEmojiIconChanged(int currentIcon) {
        if (currentIcon == 3 && this.emojiView == null) {
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            ArrayList<String> gifSearchEmojies = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
            int N = Math.min(10, gifSearchEmojies.size());
            for (int i = 0; i < N; i++) {
                Emoji.preloadEmoji(gifSearchEmojies.get(i));
            }
        }
    }

    public boolean hidePopup(boolean byBackButton) {
        return hidePopup(byBackButton, false);
    }

    public boolean hidePopup(boolean byBackButton, boolean forceAnimate) {
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && byBackButton && this.botButtonsMessageObject != null) {
                return false;
            }
            if ((byBackButton && this.searchingType != 0) || forceAnimate) {
                setSearchingTypeInternal(0, true);
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.closeSearch(true);
                }
                this.messageEditText.requestFocus();
                setStickersExpanded(false, true, false);
                if (this.emojiTabOpen) {
                    checkSendButton(true);
                }
            } else {
                if (this.searchingType != 0) {
                    setSearchingTypeInternal(0, false);
                    this.emojiView.closeSearch(false);
                    this.messageEditText.requestFocus();
                }
                showPopup(0, 0);
            }
            return true;
        }
        return false;
    }

    public void setSearchingTypeInternal(int searchingType, boolean animated) {
        final boolean showSearchingNew = searchingType != 0;
        boolean showSearchingOld = this.searchingType != 0;
        if (showSearchingNew != showSearchingOld) {
            ValueAnimator valueAnimator = this.searchAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.searchAnimator.cancel();
            }
            float f = 1.0f;
            if (!animated) {
                if (!showSearchingNew) {
                    f = 0.0f;
                }
                this.searchToOpenProgress = f;
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.searchProgressChanged();
                }
            } else {
                float[] fArr = new float[2];
                fArr[0] = this.searchToOpenProgress;
                if (!showSearchingNew) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.searchAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda33
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatActivityEnterView.this.m2343x20ac0ec5(valueAnimator2);
                    }
                });
                this.searchAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.64
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ChatActivityEnterView.this.searchToOpenProgress = showSearchingNew ? 1.0f : 0.0f;
                        if (ChatActivityEnterView.this.emojiView != null) {
                            ChatActivityEnterView.this.emojiView.searchProgressChanged();
                        }
                    }
                });
                this.searchAnimator.setDuration(220L);
                this.searchAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.searchAnimator.start();
            }
        }
        this.searchingType = searchingType;
    }

    /* renamed from: lambda$setSearchingTypeInternal$51$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2343x20ac0ec5(ValueAnimator valueAnimator) {
        this.searchToOpenProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.searchProgressChanged();
        }
    }

    public void openKeyboardInternal() {
        ChatActivity chatActivity;
        if (hasBotWebView() && botCommandsMenuIsShowing()) {
            return;
        }
        showPopup((AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow || ((chatActivity = this.parentFragment) != null && chatActivity.isInBubbleMode()) || this.isPaused) ? 0 : 2, 0);
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (AndroidUtilities.usingHardwareInput || this.keyboardVisible || AndroidUtilities.isInMultiwindow) {
        } else {
            ChatActivity chatActivity2 = this.parentFragment;
            if (chatActivity2 == null || !chatActivity2.isInBubbleMode()) {
                this.waitingForKeyboardOpen = true;
                EmojiView emojiView = this.emojiView;
                if (emojiView != null) {
                    emojiView.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 3, 0.0f, 0.0f, 0));
                }
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
            }
        }
    }

    public boolean isEditingMessage() {
        return this.editingMessageObject != null;
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public boolean isEditingCaption() {
        return this.editingCaption;
    }

    public boolean hasAudioToSend() {
        return (this.audioToSendMessageObject == null && this.videoToSendMessageObject == null) ? false : true;
    }

    public void openKeyboard() {
        if ((!hasBotWebView() || !botCommandsMenuIsShowing()) && !AndroidUtilities.showKeyboard(this.messageEditText)) {
            this.messageEditText.clearFocus();
            this.messageEditText.requestFocus();
        }
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public boolean isPopupShowing() {
        return this.emojiViewVisible || this.botKeyboardViewVisible;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(TLRPC.Document searchImage) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(searchImage, (int) (System.currentTimeMillis() / 1000), true);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.addRecentGif(searchImage);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && this.stickersExpanded) {
            setSearchingTypeInternal(0, false);
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false, false);
        }
        this.videoTimelineView.clearFrames();
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        MessageObject messageObject;
        boolean z2 = true;
        if (this.searchingType != 0) {
            this.lastSizeChangeValue1 = height;
            this.lastSizeChangeValue2 = isWidthGreater;
            if (height <= 0) {
                z2 = false;
            }
            this.keyboardVisible = z2;
            checkBotMenu();
            return;
        }
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (this.keyboardVisible && this.emojiViewVisible && this.emojiView == null) {
            this.emojiViewVisible = false;
        }
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                newHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), newHeight);
            }
            View currentView = null;
            int i = this.currentPopupContentType;
            if (i == 0) {
                currentView = this.emojiView;
            } else if (i == 1) {
                currentView = this.botKeyboardView;
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.setPanelHeight(newHeight);
            }
            if (currentView != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
                if (!this.closeAnimationInProgress && ((layoutParams.width != AndroidUtilities.displaySize.x || layoutParams.height != newHeight) && !this.stickersExpanded)) {
                    layoutParams.width = AndroidUtilities.displaySize.x;
                    layoutParams.height = newHeight;
                    currentView.setLayoutParams(layoutParams);
                    if (this.sizeNotifierLayout != null) {
                        int oldHeight = this.emojiPadding;
                        this.emojiPadding = layoutParams.height;
                        this.sizeNotifierLayout.requestLayout();
                        onWindowSizeChanged();
                        if (this.smoothKeyboard && !this.keyboardVisible && oldHeight != this.emojiPadding && pannelAnimationEnabled()) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            this.panelAnimation = animatorSet;
                            animatorSet.playTogether(ObjectAnimator.ofFloat(currentView, View.TRANSLATION_Y, this.emojiPadding - oldHeight, 0.0f));
                            this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                            this.panelAnimation.setDuration(250L);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.65
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    ChatActivityEnterView.this.panelAnimation = null;
                                    if (ChatActivityEnterView.this.delegate != null) {
                                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                    }
                                    ChatActivityEnterView.this.requestLayout();
                                    NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                                }
                            });
                            AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                            this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                            requestLayout();
                        }
                    }
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        this.keyboardVisible = height > 0;
        checkBotMenu();
        if (this.keyboardVisible && isPopupShowing() && this.stickersExpansionAnim == null) {
            showPopup(0, this.currentPopupContentType);
        } else if (!this.keyboardVisible && !isPopupShowing() && (messageObject = this.botButtonsMessageObject) != null && this.replyingMessageObject != messageObject && ((!hasBotWebView() || !botCommandsMenuIsShowing()) && TextUtils.isEmpty(this.messageEditText.getText()))) {
            if (this.sizeNotifierLayout.adjustPanLayoutHelper.animationInProgress()) {
                this.sizeNotifierLayout.adjustPanLayoutHelper.stopTransition();
            } else {
                this.sizeNotifierLayout.adjustPanLayoutHelper.ignoreOnce();
            }
            showPopup(1, 1, false);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            if (this.clearBotButtonsOnKeyboardOpen) {
                this.clearBotButtonsOnKeyboardOpen = false;
                this.botKeyboardView.setButtons(this.botReplyMarkup);
            }
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public int getVisibleEmojiPadding() {
        if (this.emojiViewVisible) {
            return this.emojiPadding;
        }
        return 0;
    }

    public MessageObject getThreadMessage() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            return chatActivity.getThreadMessage();
        }
        return null;
    }

    public int getThreadMessageId() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || chatActivity.getThreadMessage() == null) {
            return 0;
        }
        return this.parentFragment.getThreadMessage().getId();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int state;
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat;
        if (id == NotificationCenter.emojiLoaded) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.invalidateViews();
            }
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                editTextCaption.postInvalidate();
                return;
            }
            return;
        }
        int i = 0;
        if (id == NotificationCenter.recordProgressChanged) {
            int guid = ((Integer) args[0]).intValue();
            if (guid != this.recordingGuid) {
                return;
            }
            if (this.recordInterfaceState != 0 && !this.wasSendTyping && !isInScheduleMode()) {
                this.wasSendTyping = true;
                MessagesController messagesController = this.accountInstance.getMessagesController();
                long j = this.dialog_id;
                int threadMessageId = getThreadMessageId();
                ImageView imageView = this.videoSendButton;
                messagesController.sendTyping(j, threadMessageId, (imageView == null || imageView.getTag() == null) ? 1 : 7, 0);
            }
            RecordCircle recordCircle = this.recordCircle;
            if (recordCircle != null) {
                recordCircle.setAmplitude(((Double) args[1]).doubleValue());
            }
        } else if (id == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption2 = this.messageEditText;
            if (editTextCaption2 != null && editTextCaption2.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else if (id == NotificationCenter.recordStartError || id == NotificationCenter.recordStopped) {
            int guid2 = ((Integer) args[0]).intValue();
            if (guid2 != this.recordingGuid) {
                return;
            }
            if (this.recordingAudioVideo) {
                this.recordingAudioVideo = false;
                if (id == NotificationCenter.recordStopped) {
                    Integer reason = (Integer) args[1];
                    if (reason.intValue() == 4) {
                        state = 4;
                    } else if (isInVideoMode() && reason.intValue() == 5) {
                        state = 1;
                    } else if (reason.intValue() == 0) {
                        state = 5;
                    } else if (reason.intValue() == 6) {
                        state = 2;
                    } else {
                        state = 3;
                    }
                    if (state != 3) {
                        updateRecordInterface(state);
                    }
                } else {
                    updateRecordInterface(2);
                }
            }
            if (id == NotificationCenter.recordStopped) {
                Integer num = (Integer) args[1];
            }
        } else {
            Integer num2 = null;
            if (id == NotificationCenter.recordStarted) {
                int guid3 = ((Integer) args[0]).intValue();
                if (guid3 != this.recordingGuid) {
                    return;
                }
                boolean audio = ((Boolean) args[1]).booleanValue();
                ImageView imageView2 = this.videoSendButton;
                if (imageView2 != null) {
                    if (!audio) {
                        num2 = 1;
                    }
                    imageView2.setTag(num2);
                    int i2 = 8;
                    this.videoSendButton.setVisibility(audio ? 8 : 0);
                    ImageView imageView3 = this.videoSendButton;
                    if (audio) {
                        i2 = 0;
                    }
                    imageView3.setVisibility(i2);
                }
                if (!this.recordingAudioVideo) {
                    this.recordingAudioVideo = true;
                    updateRecordInterface(0);
                } else {
                    this.recordCircle.showWaves(true, true);
                }
                this.recordTimerView.start();
                this.recordDot.enterAnimation = false;
            } else if (id == NotificationCenter.audioDidSent) {
                int guid4 = ((Integer) args[0]).intValue();
                if (guid4 != this.recordingGuid) {
                    return;
                }
                Object audio2 = args[1];
                if (audio2 instanceof VideoEditedInfo) {
                    this.videoToSendMessageObject = (VideoEditedInfo) audio2;
                    String str = (String) args[2];
                    this.audioToSendPath = str;
                    ArrayList<Bitmap> keyframes = (ArrayList) args[3];
                    this.videoTimelineView.setVideoPath(str);
                    this.videoTimelineView.setKeyframes(keyframes);
                    this.videoTimelineView.setVisibility(0);
                    this.videoTimelineView.setMinProgressDiff(1000.0f / ((float) this.videoToSendMessageObject.estimatedDuration));
                    updateRecordInterface(3);
                    checkSendButton(false);
                    return;
                }
                TLRPC.TL_document tL_document = (TLRPC.TL_document) args[1];
                this.audioToSend = tL_document;
                this.audioToSendPath = (String) args[2];
                if (tL_document != null) {
                    if (this.recordedAudioPanel == null) {
                        return;
                    }
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.out = true;
                    message.id = 0;
                    message.peer_id = new TLRPC.TL_peerUser();
                    message.from_id = new TLRPC.TL_peerUser();
                    TLRPC.Peer peer = message.peer_id;
                    TLRPC.Peer peer2 = message.from_id;
                    long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    peer2.user_id = clientUserId;
                    peer.user_id = clientUserId;
                    message.date = (int) (System.currentTimeMillis() / 1000);
                    message.message = "";
                    message.attachPath = this.audioToSendPath;
                    message.media = new TLRPC.TL_messageMediaDocument();
                    message.media.flags |= 3;
                    message.media.document = this.audioToSend;
                    message.flags |= 768;
                    this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, message, false, true);
                    this.recordedAudioPanel.setAlpha(1.0f);
                    this.recordedAudioPanel.setVisibility(0);
                    this.recordDeleteImageView.setVisibility(0);
                    this.recordDeleteImageView.setAlpha(0.0f);
                    this.recordDeleteImageView.setScaleY(0.0f);
                    this.recordDeleteImageView.setScaleX(0.0f);
                    int duration = 0;
                    int a = 0;
                    while (true) {
                        if (a >= this.audioToSend.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = this.audioToSend.attributes.get(a);
                        if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                            a++;
                        } else {
                            duration = attribute.duration;
                            break;
                        }
                    }
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.audioToSend.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute2 = this.audioToSend.attributes.get(a2);
                        if (!(attribute2 instanceof TLRPC.TL_documentAttributeAudio)) {
                            a2++;
                        } else {
                            if (attribute2.waveform == null || attribute2.waveform.length == 0) {
                                attribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                            }
                            this.recordedAudioSeekBar.setWaveform(attribute2.waveform);
                        }
                    }
                    this.recordedAudioTimeTextView.setText(AndroidUtilities.formatShortDuration(duration));
                    checkSendButton(false);
                    updateRecordInterface(3);
                    return;
                }
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onMessageSend(null, true, 0);
                }
            } else if (id == NotificationCenter.audioRouteChanged) {
                if (this.parentActivity != null) {
                    boolean frontSpeaker = ((Boolean) args[0]).booleanValue();
                    Activity activity = this.parentActivity;
                    if (!frontSpeaker) {
                        i = Integer.MIN_VALUE;
                    }
                    activity.setVolumeControlStream(i);
                }
            } else if (id == NotificationCenter.messagePlayingDidReset) {
                if (this.audioToSendMessageObject != null && !MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    this.playPauseDrawable.setIcon(0, true);
                    this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                    this.recordedAudioSeekBar.setProgress(0.0f);
                }
            } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
                Integer num3 = (Integer) args[0];
                if (this.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                    MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                    this.audioToSendMessageObject.audioProgress = player.audioProgress;
                    this.audioToSendMessageObject.audioProgressSec = player.audioProgressSec;
                    if (!this.recordedAudioSeekBar.isDragging()) {
                        this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
                    }
                }
            } else if (id == NotificationCenter.featuredStickersDidLoad) {
                if (this.emojiButton != null) {
                    int a3 = 0;
                    while (true) {
                        ImageView[] imageViewArr = this.emojiButton;
                        if (a3 < imageViewArr.length) {
                            imageViewArr[a3].invalidate();
                            a3++;
                        } else {
                            return;
                        }
                    }
                }
            } else if (id == NotificationCenter.messageReceivedByServer) {
                Boolean scheduled = (Boolean) args[6];
                if (scheduled.booleanValue()) {
                    return;
                }
                long did = ((Long) args[3]).longValue();
                if (did == this.dialog_id && (chatFull = this.info) != null && chatFull.slowmode_seconds != 0 && (chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id))) != null && !ChatObject.hasAdminRights(chat)) {
                    this.info.slowmode_next_send_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + this.info.slowmode_seconds;
                    this.info.flags |= 262144;
                    setSlowModeTimer(this.info.slowmode_next_send_date);
                }
            } else if (id == NotificationCenter.sendingMessagesChanged) {
                if (this.info != null) {
                    updateSlowModeText();
                }
            } else if (id == NotificationCenter.audioRecordTooShort) {
                updateRecordInterface(4);
            } else if (id == NotificationCenter.updateBotMenuButton) {
                long botId = ((Long) args[0]).longValue();
                TLRPC.BotMenuButton botMenuButton = (TLRPC.BotMenuButton) args[1];
                if (botId == this.dialog_id) {
                    if (!(botMenuButton instanceof TLRPC.TL_botMenuButton)) {
                        if (this.hasBotCommands) {
                            this.botMenuButtonType = BotMenuButtonType.COMMANDS;
                        } else {
                            this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
                        }
                    } else {
                        TLRPC.TL_botMenuButton webViewButton = (TLRPC.TL_botMenuButton) botMenuButton;
                        this.botMenuWebViewTitle = webViewButton.text;
                        this.botMenuWebViewUrl = webViewButton.url;
                        this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
                    }
                    updateBotButton(false);
                }
            }
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2 && this.pendingLocationButton != null) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }

    public void checkStickresExpandHeight() {
        if (this.emojiView == null) {
            return;
        }
        int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
        int newHeight = (((this.originalViewHeight - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
        if (this.searchingType == 2) {
            newHeight = Math.min(newHeight, AndroidUtilities.dp(120.0f) + origHeight);
        }
        int currentHeight = this.emojiView.getLayoutParams().height;
        if (currentHeight == newHeight) {
            return;
        }
        Animator animator = this.stickersExpansionAnim;
        if (animator != null) {
            animator.cancel();
            this.stickersExpansionAnim = null;
        }
        this.stickersExpandedHeight = newHeight;
        if (currentHeight <= newHeight) {
            this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
            this.sizeNotifierLayout.requestLayout();
            int start = this.messageEditText.getSelectionStart();
            int end = this.messageEditText.getSelectionEnd();
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setText(editTextCaption.getText());
            this.messageEditText.setSelection(start, end);
            AnimatorSet anims = new AnimatorSet();
            anims.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - origHeight)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - origHeight)));
            ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda11
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatActivityEnterView.this.m2299x8a895ab3(valueAnimator);
                }
            });
            anims.setDuration(300L);
            anims.setInterpolator(CubicBezierInterpolator.DEFAULT);
            anims.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.67
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ChatActivityEnterView.this.stickersExpansionAnim = null;
                    ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                }
            });
            this.stickersExpansionAnim = anims;
            this.emojiView.setLayerType(2, null);
            anims.start();
            return;
        }
        AnimatorSet anims2 = new AnimatorSet();
        anims2.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - origHeight)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - origHeight)));
        ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatActivityEnterView.this.m2298x98dfb494(valueAnimator);
            }
        });
        anims2.setDuration(300L);
        anims2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        anims2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.66
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ChatActivityEnterView.this.stickersExpansionAnim = null;
                if (ChatActivityEnterView.this.emojiView != null) {
                    ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                    ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                }
            }
        });
        this.stickersExpansionAnim = anims2;
        this.emojiView.setLayerType(2, null);
        anims2.start();
    }

    /* renamed from: lambda$checkStickresExpandHeight$52$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2298x98dfb494(ValueAnimator animation) {
        this.sizeNotifierLayout.invalidate();
    }

    /* renamed from: lambda$checkStickresExpandHeight$53$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2299x8a895ab3(ValueAnimator animation) {
        this.sizeNotifierLayout.invalidate();
    }

    public void setStickersExpanded(boolean expanded, boolean animated, boolean byDrag) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if ((adjustPanLayoutHelper == null || !adjustPanLayoutHelper.animationInProgress()) && !this.waitingForKeyboardOpenAfterAnimation && this.emojiView != null) {
            if (!byDrag && this.stickersExpanded == expanded) {
                return;
            }
            this.stickersExpanded = expanded;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onStickersExpandedChange();
            }
            final int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            Animator animator = this.stickersExpansionAnim;
            if (animator != null) {
                animator.cancel();
                this.stickersExpansionAnim = null;
            }
            if (this.stickersExpanded) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                int height = this.sizeNotifierLayout.getHeight();
                this.originalViewHeight = height;
                int currentActionBarHeight = (((height - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                this.stickersExpandedHeight = currentActionBarHeight;
                if (this.searchingType == 2) {
                    this.stickersExpandedHeight = Math.min(currentActionBarHeight, AndroidUtilities.dp(120.0f) + origHeight);
                }
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                int start = this.messageEditText.getSelectionStart();
                int end = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(start, end);
                if (animated) {
                    AnimatorSet anims = new AnimatorSet();
                    anims.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - origHeight)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - origHeight)), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", 1.0f));
                    anims.setDuration(300L);
                    anims.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda57
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.m2344xc70b418a(origHeight, valueAnimator);
                        }
                    });
                    anims.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.68
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionAnim = anims;
                    this.emojiView.setLayerType(2, null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                    this.stickersExpansionProgress = 0.0f;
                    this.sizeNotifierLayout.invalidate();
                    anims.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY(-(this.stickersExpandedHeight - origHeight));
                    this.emojiView.setTranslationY(-(this.stickersExpandedHeight - origHeight));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 1);
                if (!animated) {
                    this.stickersExpansionProgress = 0.0f;
                    setTranslationY(0.0f);
                    this.emojiView.setTranslationY(0.0f);
                    this.emojiView.getLayoutParams().height = origHeight;
                    this.sizeNotifierLayout.requestLayout();
                    this.sizeNotifierLayout.setForeground(null);
                    this.sizeNotifierLayout.setWillNotDraw(false);
                    this.stickersArrow.setAnimationProgress(0.0f);
                } else {
                    this.closeAnimationInProgress = true;
                    AnimatorSet anims2 = new AnimatorSet();
                    anims2.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, 0), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, 0), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", 0.0f));
                    anims2.setDuration(300L);
                    anims2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda58
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.m2345xb8b4e7a9(origHeight, valueAnimator);
                        }
                    });
                    anims2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.69
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            ChatActivityEnterView.this.closeAnimationInProgress = false;
                            ChatActivityEnterView.this.stickersExpansionAnim = null;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.getLayoutParams().height = origHeight;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                            }
                            if (ChatActivityEnterView.this.sizeNotifierLayout != null) {
                                ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                                ChatActivityEnterView.this.sizeNotifierLayout.setForeground(null);
                                ChatActivityEnterView.this.sizeNotifierLayout.setWillNotDraw(false);
                            }
                            if (ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                                chatActivityEnterView.showPopup(0, chatActivityEnterView.currentPopupContentType);
                            }
                            if (ChatActivityEnterView.this.onEmojiSearchClosed != null) {
                                ChatActivityEnterView.this.onEmojiSearchClosed.run();
                                ChatActivityEnterView.this.onEmojiSearchClosed = null;
                            }
                            NotificationCenter.getInstance(ChatActivityEnterView.this.currentAccount).onAnimationFinish(ChatActivityEnterView.this.notificationsIndex);
                        }
                    });
                    this.stickersExpansionProgress = 1.0f;
                    this.sizeNotifierLayout.invalidate();
                    this.stickersExpansionAnim = anims2;
                    this.emojiView.setLayerType(2, null);
                    this.notificationsIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.notificationsIndex, null);
                    anims2.start();
                }
            }
            if (expanded) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", R.string.AccDescrCollapsePanel));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", R.string.AccDescrExpandPanel));
            }
        }
    }

    /* renamed from: lambda$setStickersExpanded$54$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2344xc70b418a(int origHeight, ValueAnimator animation) {
        this.stickersExpansionProgress = Math.abs(getTranslationY() / (-(this.stickersExpandedHeight - origHeight)));
        this.sizeNotifierLayout.invalidate();
    }

    /* renamed from: lambda$setStickersExpanded$55$org-telegram-ui-Components-ChatActivityEnterView */
    public /* synthetic */ void m2345xb8b4e7a9(int origHeight, ValueAnimator animation) {
        this.stickersExpansionProgress = getTranslationY() / (-(this.stickersExpandedHeight - origHeight));
        this.sizeNotifierLayout.invalidate();
    }

    public boolean swipeToBackEnabled() {
        FrameLayout frameLayout;
        if (this.recordingAudioVideo) {
            return false;
        }
        if (this.videoSendButton != null && isInVideoMode() && (frameLayout = this.recordedAudioPanel) != null && frameLayout.getVisibility() == 0) {
            return false;
        }
        return !hasBotWebView() || !this.botCommandsMenuButton.isOpened();
    }

    public int getHeightWithTopView() {
        int h = getMeasuredHeight();
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            return (int) (h - ((1.0f - this.topViewEnterProgress) * this.topView.getLayoutParams().height));
        }
        return h;
    }

    public void setAdjustPanLayoutHelper(AdjustPanLayoutHelper adjustPanLayoutHelper) {
        this.adjustPanLayoutHelper = adjustPanLayoutHelper;
    }

    public AdjustPanLayoutHelper getAdjustPanLayoutHelper() {
        return this.adjustPanLayoutHelper;
    }

    public boolean panelAnimationInProgress() {
        return this.panelAnimation != null;
    }

    public float getTopViewTranslation() {
        View view = this.topView;
        if (view == null || view.getVisibility() == 8) {
            return 0.0f;
        }
        return this.topView.getTranslationY();
    }

    public int getAnimatedTop() {
        return this.animatedTop;
    }

    public void checkAnimation() {
    }

    /* loaded from: classes5.dex */
    public class ScrimDrawable extends Drawable {
        private Paint paint;

        public ScrimDrawable() {
            ChatActivityEnterView.this = r2;
            Paint paint = new Paint();
            this.paint = paint;
            paint.setColor(0);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (ChatActivityEnterView.this.emojiView != null) {
                this.paint.setAlpha(Math.round(ChatActivityEnterView.this.stickersExpansionProgress * 102.0f));
                canvas.drawRect(0.0f, 0.0f, ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight(), this.paint);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }
    }

    /* loaded from: classes5.dex */
    public class SlideTextView extends View {
        TextPaint bluePaint;
        float cancelAlpha;
        int cancelCharOffset;
        StaticLayout cancelLayout;
        String cancelString;
        float cancelToProgress;
        float cancelWidth;
        TextPaint grayPaint;
        private int lastSize;
        long lastUpdateTime;
        boolean moveForward;
        private boolean pressed;
        Drawable selectableBackground;
        float slideProgress;
        float slideToAlpha;
        String slideToCancelString;
        float slideToCancelWidth;
        StaticLayout slideToLayout;
        boolean smallSize;
        Paint arrowPaint = new Paint(1);
        float xOffset = 0.0f;
        Path arrowPath = new Path();
        public android.graphics.Rect cancelRect = new android.graphics.Rect();

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == 3 || event.getAction() == 1) {
                setPressed(false);
            }
            if (this.cancelToProgress == 0.0f || !isEnabled()) {
                return false;
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == 0) {
                boolean contains = this.cancelRect.contains(x, y);
                this.pressed = contains;
                if (contains) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.selectableBackground.setHotspot(x, y);
                    }
                    setPressed(true);
                }
                return this.pressed;
            }
            boolean z = this.pressed;
            if (z) {
                if (event.getAction() == 2 && !this.cancelRect.contains(x, y)) {
                    setPressed(false);
                    return false;
                }
                if (event.getAction() == 1 && this.cancelRect.contains(x, y)) {
                    onCancelButtonPressed();
                }
                return true;
            }
            return z;
        }

        public void onCancelButtonPressed() {
            if (!ChatActivityEnterView.this.hasRecordVideo || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                MediaController.getInstance().stopRecording(0, false, 0);
            } else {
                CameraController.getInstance().cancelOnInitRunnable(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                ChatActivityEnterView.this.delegate.needStartRecordVideo(5, true, 0);
            }
            ChatActivityEnterView.this.recordingAudioVideo = false;
            ChatActivityEnterView.this.updateRecordInterface(2);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SlideTextView(Context context) {
            super(context);
            ChatActivityEnterView.this = r6;
            this.smallSize = AndroidUtilities.displaySize.x <= AndroidUtilities.dp(320.0f);
            TextPaint textPaint = new TextPaint(1);
            this.grayPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(this.smallSize ? 13.0f : 15.0f));
            TextPaint textPaint2 = new TextPaint(1);
            this.bluePaint = textPaint2;
            textPaint2.setTextSize(AndroidUtilities.dp(15.0f));
            this.bluePaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.arrowPaint.setColor(r6.getThemedColor(Theme.key_chat_messagePanelIcons));
            this.arrowPaint.setStyle(Paint.Style.STROKE);
            this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(this.smallSize ? 1.0f : 1.6f));
            this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
            this.arrowPaint.setStrokeJoin(Paint.Join.ROUND);
            this.slideToCancelString = LocaleController.getString("SlideToCancel", R.string.SlideToCancel);
            this.slideToCancelString = this.slideToCancelString.charAt(0) + this.slideToCancelString.substring(1).toLowerCase();
            String upperCase = LocaleController.getString("Cancel", R.string.Cancel).toUpperCase();
            this.cancelString = upperCase;
            this.cancelCharOffset = this.slideToCancelString.indexOf(upperCase);
            updateColors();
        }

        public void updateColors() {
            this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordTime));
            this.bluePaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordVoiceCancel));
            this.slideToAlpha = this.grayPaint.getAlpha();
            this.cancelAlpha = this.bluePaint.getAlpha();
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(60.0f), 0, ColorUtils.setAlphaComponent(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordVoiceCancel), 26));
            this.selectableBackground = createSimpleSelectorCircleDrawable;
            createSimpleSelectorCircleDrawable.setCallback(this);
        }

        @Override // android.view.View
        protected void drawableStateChanged() {
            super.drawableStateChanged();
            this.selectableBackground.setState(getDrawableState());
        }

        @Override // android.view.View
        public boolean verifyDrawable(Drawable drawable) {
            return this.selectableBackground == drawable || super.verifyDrawable(drawable);
        }

        @Override // android.view.View
        public void jumpDrawablesToCurrentState() {
            super.jumpDrawablesToCurrentState();
            Drawable drawable = this.selectableBackground;
            if (drawable != null) {
                drawable.jumpToCurrentState();
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int currentSize = getMeasuredHeight() + (getMeasuredWidth() << 16);
            if (this.lastSize != currentSize) {
                this.lastSize = currentSize;
                this.slideToCancelWidth = this.grayPaint.measureText(this.slideToCancelString);
                this.cancelWidth = this.bluePaint.measureText(this.cancelString);
                this.lastUpdateTime = System.currentTimeMillis();
                int heightHalf = getMeasuredHeight() >> 1;
                this.arrowPath.reset();
                if (this.smallSize) {
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(2.5f), heightHalf - AndroidUtilities.dpf2(3.12f));
                    this.arrowPath.lineTo(0.0f, heightHalf);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(2.5f), heightHalf + AndroidUtilities.dpf2(3.12f));
                } else {
                    this.arrowPath.setLastPoint(AndroidUtilities.dpf2(4.0f), heightHalf - AndroidUtilities.dpf2(5.0f));
                    this.arrowPath.lineTo(0.0f, heightHalf);
                    this.arrowPath.lineTo(AndroidUtilities.dpf2(4.0f), heightHalf + AndroidUtilities.dpf2(5.0f));
                }
                this.slideToLayout = new StaticLayout(this.slideToCancelString, this.grayPaint, (int) this.slideToCancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.cancelLayout = new StaticLayout(this.cancelString, this.bluePaint, (int) this.cancelWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            StaticLayout staticLayout;
            float xi;
            if (this.slideToLayout == null || (staticLayout = this.cancelLayout) == null) {
                return;
            }
            int w = staticLayout.getWidth() + AndroidUtilities.dp(16.0f);
            this.grayPaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordTime));
            this.grayPaint.setAlpha((int) (this.slideToAlpha * (1.0f - this.cancelToProgress) * this.slideProgress));
            this.bluePaint.setAlpha((int) (this.cancelAlpha * this.cancelToProgress));
            this.arrowPaint.setColor(this.grayPaint.getColor());
            boolean z = true;
            if (this.smallSize) {
                this.xOffset = AndroidUtilities.dp(16.0f);
            } else {
                long dt = System.currentTimeMillis() - this.lastUpdateTime;
                this.lastUpdateTime = System.currentTimeMillis();
                if (this.cancelToProgress == 0.0f && this.slideProgress > 0.8f) {
                    if (this.moveForward) {
                        float dp = this.xOffset + ((AndroidUtilities.dp(3.0f) / 250.0f) * ((float) dt));
                        this.xOffset = dp;
                        if (dp > AndroidUtilities.dp(6.0f)) {
                            this.xOffset = AndroidUtilities.dp(6.0f);
                            this.moveForward = false;
                        }
                    } else {
                        float dp2 = this.xOffset - ((AndroidUtilities.dp(3.0f) / 250.0f) * ((float) dt));
                        this.xOffset = dp2;
                        if (dp2 < (-AndroidUtilities.dp(6.0f))) {
                            this.xOffset = -AndroidUtilities.dp(6.0f);
                            this.moveForward = true;
                        }
                    }
                }
            }
            if (this.cancelCharOffset < 0) {
                z = false;
            }
            boolean enableTransition = z;
            int slideX = ((int) ((getMeasuredWidth() - this.slideToCancelWidth) / 2.0f)) + AndroidUtilities.dp(5.0f);
            int cancelX = (int) ((getMeasuredWidth() - this.cancelWidth) / 2.0f);
            float offset = enableTransition ? this.slideToLayout.getPrimaryHorizontal(this.cancelCharOffset) : 0.0f;
            float cancelDiff = enableTransition ? (slideX + offset) - cancelX : 0.0f;
            float f = this.xOffset;
            float f2 = this.cancelToProgress;
            float x = ((slideX + ((f * (1.0f - f2)) * this.slideProgress)) - (f2 * cancelDiff)) + AndroidUtilities.dp(16.0f);
            float offsetY = enableTransition ? 0.0f : this.cancelToProgress * AndroidUtilities.dp(12.0f);
            if (this.cancelToProgress != 1.0f) {
                int slideDelta = (int) (((-getMeasuredWidth()) / 4) * (1.0f - this.slideProgress));
                canvas.save();
                canvas.clipRect(ChatActivityEnterView.this.recordTimerView.getLeftProperty() + AndroidUtilities.dp(4.0f), 0.0f, getMeasuredWidth(), getMeasuredHeight());
                canvas.save();
                canvas.translate((((int) x) - AndroidUtilities.dp(this.smallSize ? 7.0f : 10.0f)) + slideDelta, offsetY);
                canvas.drawPath(this.arrowPath, this.arrowPaint);
                canvas.restore();
                canvas.save();
                canvas.translate(((int) x) + slideDelta, ((getMeasuredHeight() - this.slideToLayout.getHeight()) / 2.0f) + offsetY);
                this.slideToLayout.draw(canvas);
                canvas.restore();
                canvas.restore();
            }
            float yi = (getMeasuredHeight() - this.cancelLayout.getHeight()) / 2.0f;
            if (!enableTransition) {
                yi -= AndroidUtilities.dp(12.0f) - offsetY;
            }
            if (enableTransition) {
                xi = x + offset;
            } else {
                xi = cancelX;
            }
            this.cancelRect.set((int) xi, (int) yi, (int) (this.cancelLayout.getWidth() + xi), (int) (this.cancelLayout.getHeight() + yi));
            this.cancelRect.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(16.0f));
            if (this.cancelToProgress <= 0.0f) {
                setPressed(false);
            } else {
                this.selectableBackground.setBounds((getMeasuredWidth() / 2) - w, (getMeasuredHeight() / 2) - w, (getMeasuredWidth() / 2) + w, (getMeasuredHeight() / 2) + w);
                this.selectableBackground.draw(canvas);
                canvas.save();
                canvas.translate(xi, yi);
                this.cancelLayout.draw(canvas);
                canvas.restore();
            }
            if (this.cancelToProgress != 1.0f) {
                invalidate();
            }
        }

        public void setCancelToProgress(float cancelToProgress) {
            this.cancelToProgress = cancelToProgress;
        }

        public float getSlideToCancelWidth() {
            return this.slideToCancelWidth;
        }

        public void setSlideX(float v) {
            this.slideProgress = v;
        }
    }

    /* loaded from: classes5.dex */
    public class TimerView extends View {
        StaticLayout inLayout;
        boolean isRunning;
        long lastSendTypingTime;
        float left;
        String oldString;
        StaticLayout outLayout;
        float replaceTransition;
        long startTime;
        long stopTime;
        boolean stoppedInternal;
        final TextPaint textPaint;
        SpannableStringBuilder replaceIn = new SpannableStringBuilder();
        SpannableStringBuilder replaceOut = new SpannableStringBuilder();
        SpannableStringBuilder replaceStable = new SpannableStringBuilder();
        final float replaceDistance = AndroidUtilities.dp(15.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TimerView(Context context) {
            super(context);
            ChatActivityEnterView.this = this$0;
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(15.0f));
            textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            updateColors();
        }

        public void start() {
            this.isRunning = true;
            long currentTimeMillis = System.currentTimeMillis();
            this.startTime = currentTimeMillis;
            this.lastSendTypingTime = currentTimeMillis;
            invalidate();
        }

        public void stop() {
            if (this.isRunning) {
                this.isRunning = false;
                if (this.startTime > 0) {
                    this.stopTime = System.currentTimeMillis();
                }
                invalidate();
            }
            this.lastSendTypingTime = 0L;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            String newString;
            SpannableStringBuilder spannableStringBuilder;
            String str;
            long t;
            long currentTimeMillis = System.currentTimeMillis();
            long t2 = this.isRunning ? currentTimeMillis - this.startTime : this.stopTime - this.startTime;
            long time = t2 / 1000;
            int ms = ((int) (t2 % 1000)) / 10;
            if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null && t2 >= 59500 && !this.stoppedInternal) {
                ChatActivityEnterView.this.startedDraggingX = -1.0f;
                ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                this.stoppedInternal = true;
            }
            if (this.isRunning && currentTimeMillis > this.lastSendTypingTime + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                this.lastSendTypingTime = currentTimeMillis;
                MessagesController.getInstance(ChatActivityEnterView.this.currentAccount).sendTyping(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.getThreadMessageId(), (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? 1 : 7, 0);
            }
            if (time / 60 >= 60) {
                newString = String.format(Locale.US, "%01d:%02d:%02d,%d", Long.valueOf((time / 60) / 60), Long.valueOf((time / 60) % 60), Long.valueOf(time % 60), Integer.valueOf(ms / 10));
            } else {
                newString = String.format(Locale.US, "%01d:%02d,%d", Long.valueOf(time / 60), Long.valueOf(time % 60), Integer.valueOf(ms / 10));
            }
            if (newString.length() >= 3 && (str = this.oldString) != null && str.length() >= 3 && newString.length() == this.oldString.length() && newString.charAt(newString.length() - 3) != this.oldString.charAt(newString.length() - 3)) {
                int n = newString.length();
                this.replaceIn.clear();
                this.replaceOut.clear();
                this.replaceStable.clear();
                this.replaceIn.append((CharSequence) newString);
                this.replaceOut.append((CharSequence) this.oldString);
                this.replaceStable.append((CharSequence) newString);
                int inLast = -1;
                int inCount = 0;
                int outCount = 0;
                int outLast = -1;
                int i = 0;
                while (true) {
                    long currentTimeMillis2 = currentTimeMillis;
                    if (i >= n - 1) {
                        break;
                    }
                    if (this.oldString.charAt(i) != newString.charAt(i)) {
                        if (outCount == 0) {
                            outLast = i;
                        }
                        outCount++;
                        if (inCount == 0) {
                            t = t2;
                        } else {
                            EmptyStubSpan span = new EmptyStubSpan();
                            if (i == n - 2) {
                                inCount++;
                            }
                            t = t2;
                            this.replaceIn.setSpan(span, inLast, inLast + inCount, 33);
                            this.replaceOut.setSpan(span, inLast, inLast + inCount, 33);
                            inCount = 0;
                        }
                    } else {
                        t = t2;
                        if (inCount == 0) {
                            inLast = i;
                        }
                        inCount++;
                        if (outCount != 0) {
                            this.replaceStable.setSpan(new EmptyStubSpan(), outLast, outLast + outCount, 33);
                            outCount = 0;
                        }
                    }
                    i++;
                    currentTimeMillis = currentTimeMillis2;
                    t2 = t;
                }
                if (inCount != 0) {
                    EmptyStubSpan span2 = new EmptyStubSpan();
                    this.replaceIn.setSpan(span2, inLast, inLast + inCount + 1, 33);
                    this.replaceOut.setSpan(span2, inLast, inLast + inCount + 1, 33);
                }
                if (outCount != 0) {
                    this.replaceStable.setSpan(new EmptyStubSpan(), outLast, outLast + outCount, 33);
                }
                this.inLayout = new StaticLayout(this.replaceIn, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.outLayout = new StaticLayout(this.replaceOut, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.replaceTransition = 1.0f;
            } else {
                if (this.replaceStable == null) {
                    this.replaceStable = new SpannableStringBuilder(newString);
                }
                if (this.replaceStable.length() == 0 || this.replaceStable.length() != newString.length()) {
                    this.replaceStable.clear();
                    this.replaceStable.append((CharSequence) newString);
                } else {
                    this.replaceStable.replace(spannableStringBuilder.length() - 1, this.replaceStable.length(), (CharSequence) newString, (newString.length() - 1) - (newString.length() - this.replaceStable.length()), newString.length());
                }
            }
            float f = this.replaceTransition;
            if (f != 0.0f) {
                float f2 = f - 0.15f;
                this.replaceTransition = f2;
                if (f2 < 0.0f) {
                    this.replaceTransition = 0.0f;
                }
            }
            float y = getMeasuredHeight() / 2;
            if (this.replaceTransition == 0.0f) {
                this.replaceStable.clearSpans();
                StaticLayout staticLayout = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas.save();
                canvas.translate(0.0f, y - (staticLayout.getHeight() / 2.0f));
                staticLayout.draw(canvas);
                canvas.restore();
                this.left = staticLayout.getLineWidth(0) + 0.0f;
            } else {
                if (this.inLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) ((1.0f - this.replaceTransition) * 255.0f));
                    canvas.translate(0.0f, (y - (this.inLayout.getHeight() / 2.0f)) - (this.replaceDistance * this.replaceTransition));
                    this.inLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.outLayout != null) {
                    canvas.save();
                    this.textPaint.setAlpha((int) (this.replaceTransition * 255.0f));
                    canvas.translate(0.0f, (y - (this.outLayout.getHeight() / 2.0f)) + (this.replaceDistance * (1.0f - this.replaceTransition)));
                    this.outLayout.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                this.textPaint.setAlpha(255);
                StaticLayout staticLayout2 = new StaticLayout(this.replaceStable, this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                canvas.translate(0.0f, y - (staticLayout2.getHeight() / 2.0f));
                staticLayout2.draw(canvas);
                canvas.restore();
                this.left = staticLayout2.getLineWidth(0) + 0.0f;
            }
            this.oldString = newString;
            if (this.isRunning || this.replaceTransition != 0.0f) {
                invalidate();
            }
        }

        public void updateColors() {
            this.textPaint.setColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_recordTime));
        }

        public float getLeftProperty() {
            return this.left;
        }

        public void reset() {
            this.isRunning = false;
            this.startTime = 0L;
            this.stopTime = 0L;
            this.stoppedInternal = false;
        }
    }

    protected boolean pannelAnimationEnabled() {
        return true;
    }

    public RecordCircle getRecordCicle() {
        return this.recordCircle;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int padding;
        LinearLayoutManager layoutManager;
        int p;
        View view;
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null && botCommandsMenuView.getTag() != null) {
            this.botCommandsMenuButton.measure(widthMeasureSpec, heightMeasureSpec);
            int i = 0;
            while (true) {
                ImageView[] imageViewArr = this.emojiButton;
                if (i >= imageViewArr.length) {
                    break;
                }
                ((ViewGroup.MarginLayoutParams) imageViewArr[i].getLayoutParams()).leftMargin = AndroidUtilities.dp(10.0f) + this.botCommandsMenuButton.getMeasuredWidth();
                i++;
            }
            ((ViewGroup.MarginLayoutParams) this.messageEditText.getLayoutParams()).leftMargin = AndroidUtilities.dp(57.0f) + this.botCommandsMenuButton.getMeasuredWidth();
        } else {
            SenderSelectView senderSelectView = this.senderSelectView;
            if (senderSelectView != null && senderSelectView.getVisibility() == 0) {
                int width = this.senderSelectView.getLayoutParams().width;
                int height = this.senderSelectView.getLayoutParams().height;
                this.senderSelectView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                int i2 = 0;
                while (true) {
                    ImageView[] imageViewArr2 = this.emojiButton;
                    if (i2 >= imageViewArr2.length) {
                        break;
                    }
                    ((ViewGroup.MarginLayoutParams) imageViewArr2[i2].getLayoutParams()).leftMargin = AndroidUtilities.dp(16.0f) + width;
                    i2++;
                }
                ((ViewGroup.MarginLayoutParams) this.messageEditText.getLayoutParams()).leftMargin = AndroidUtilities.dp(63.0f) + width;
            } else {
                int i3 = 0;
                while (true) {
                    ImageView[] imageViewArr3 = this.emojiButton;
                    if (i3 >= imageViewArr3.length) {
                        break;
                    }
                    ((ViewGroup.MarginLayoutParams) imageViewArr3[i3].getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
                    i3++;
                }
                ((ViewGroup.MarginLayoutParams) this.messageEditText.getLayoutParams()).leftMargin = AndroidUtilities.dp(50.0f);
            }
        }
        if (this.botCommandsMenuContainer != null) {
            if (this.botCommandsAdapter.getItemCount() > 4) {
                padding = Math.max(0, this.sizeNotifierLayout.getMeasuredHeight() - AndroidUtilities.dp(162.8f));
            } else {
                padding = Math.max(0, this.sizeNotifierLayout.getMeasuredHeight() - AndroidUtilities.dp((Math.max(1, Math.min(4, this.botCommandsAdapter.getItemCount())) * 36) + 8));
            }
            if (this.botCommandsMenuContainer.listView.getPaddingTop() != padding) {
                this.botCommandsMenuContainer.listView.setTopGlowOffset(padding);
                if (this.botCommandLastPosition == -1 && this.botCommandsMenuContainer.getVisibility() == 0 && this.botCommandsMenuContainer.listView.getLayoutManager() != null && (p = (layoutManager = (LinearLayoutManager) this.botCommandsMenuContainer.listView.getLayoutManager()).findFirstVisibleItemPosition()) >= 0 && (view = layoutManager.findViewByPosition(p)) != null) {
                    this.botCommandLastPosition = p;
                    this.botCommandLastTop = view.getTop() - this.botCommandsMenuContainer.listView.getPaddingTop();
                }
                this.botCommandsMenuContainer.listView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ChatActivityBotWebViewButton chatActivityBotWebViewButton = this.botWebViewButton;
        if (chatActivityBotWebViewButton != null) {
            BotCommandsMenuView botCommandsMenuView2 = this.botCommandsMenuButton;
            if (botCommandsMenuView2 != null) {
                chatActivityBotWebViewButton.setMeasuredButtonWidth(botCommandsMenuView2.getMeasuredWidth());
            }
            this.botWebViewButton.getLayoutParams().height = getMeasuredHeight() - AndroidUtilities.dp(2.0f);
            measureChild(this.botWebViewButton, widthMeasureSpec, heightMeasureSpec);
        }
        BotWebViewMenuContainer botWebViewMenuContainer = this.botWebViewMenuContainer;
        if (botWebViewMenuContainer != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) botWebViewMenuContainer.getLayoutParams();
            params.bottomMargin = this.messageEditText.getMeasuredHeight();
            measureChild(this.botWebViewMenuContainer, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.botCommandLastPosition != -1) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) this.botCommandsMenuContainer.listView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(this.botCommandLastPosition, this.botCommandLastTop);
            }
            this.botCommandLastPosition = -1;
        }
    }

    private void beginDelayedTransition() {
        HashMap<View, Float> hashMap = this.animationParamsX;
        ImageView[] imageViewArr = this.emojiButton;
        hashMap.put(imageViewArr[0], Float.valueOf(imageViewArr[0].getX()));
        HashMap<View, Float> hashMap2 = this.animationParamsX;
        ImageView[] imageViewArr2 = this.emojiButton;
        hashMap2.put(imageViewArr2[1], Float.valueOf(imageViewArr2[1].getX()));
        HashMap<View, Float> hashMap3 = this.animationParamsX;
        EditTextCaption editTextCaption = this.messageEditText;
        hashMap3.put(editTextCaption, Float.valueOf(editTextCaption.getX()));
    }

    public void setBotInfo(LongSparseArray<TLRPC.BotInfo> botInfo) {
        if (botInfo.size() == 1 && botInfo.valueAt(0).user_id == this.dialog_id) {
            TLRPC.BotInfo info = botInfo.valueAt(0);
            TLRPC.BotMenuButton menuButton = info.menu_button;
            if (menuButton instanceof TLRPC.TL_botMenuButton) {
                TLRPC.TL_botMenuButton webViewButton = (TLRPC.TL_botMenuButton) menuButton;
                this.botMenuWebViewTitle = webViewButton.text;
                this.botMenuWebViewUrl = webViewButton.url;
                this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
            } else if (!info.commands.isEmpty()) {
                this.botMenuButtonType = BotMenuButtonType.COMMANDS;
            } else {
                this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
            }
        } else {
            this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
        }
        BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter = this.botCommandsAdapter;
        if (botCommandsAdapter != null) {
            botCommandsAdapter.setBotInfo(botInfo);
        }
    }

    public boolean botCommandsMenuIsShowing() {
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        return botCommandsMenuView != null && botCommandsMenuView.isOpened();
    }

    public void hideBotCommands() {
        this.botCommandsMenuButton.setOpened(false);
        if (hasBotWebView()) {
            this.botWebViewMenuContainer.dismiss();
        } else {
            this.botCommandsMenuContainer.dismiss();
        }
    }

    public void setTextTransitionIsRunning(boolean b) {
        this.textTransitionIsRunning = b;
        this.sendButtonContainer.invalidate();
    }

    public float getTopViewHeight() {
        View view = this.topView;
        if (view != null && view.getVisibility() == 0) {
            return this.topView.getLayoutParams().height;
        }
        return 0.0f;
    }

    public void runEmojiPanelAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.runEmojiPanelAnimation);
        this.runEmojiPanelAnimation.run();
    }

    public Drawable getStickersArrowDrawable() {
        return this.stickersArrow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.BlurredFrameLayout, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        EmojiView emojiView = this.emojiView;
        if (emojiView == null || emojiView.getVisibility() != 0 || this.emojiView.getStickersExpandOffset() == 0.0f) {
            super.dispatchDraw(canvas);
            return;
        }
        canvas.save();
        canvas.clipRect(0, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
        canvas.translate(0.0f, -this.emojiView.getStickersExpandOffset());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    public void setChatSearchExpandOffset(float chatSearchExpandOffset) {
        this.chatSearchExpandOffset = chatSearchExpandOffset;
        invalidate();
    }
}
