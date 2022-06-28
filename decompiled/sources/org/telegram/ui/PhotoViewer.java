package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.FloatProperty;
import android.util.Property;
import android.util.Range;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.internal.view.SupportMenu;
import androidx.core.net.MailTo;
import androidx.core.view.InputDeviceCompat;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.appindexing.builders.TimerBuilder;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.Constants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.ChooseSpeedLayout;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FadingTextViewLayout;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.OtherDocumentPlaceholderDrawable;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoPaintView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.PlayPauseDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.Tooltip;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMentionPhotoViewer;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayerSeekBar;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.Components.ViewHelper;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector2.OnGestureListener, GestureDetector2.OnDoubleTapListener {
    private static final int EDIT_MODE_CROP = 1;
    private static final int EDIT_MODE_FILTER = 2;
    private static final int EDIT_MODE_NONE = 0;
    private static final int EDIT_MODE_PAINT = 3;
    private static volatile PhotoViewer Instance = null;
    private static final int PROGRESS_CANCEL = 1;
    private static final int PROGRESS_EMPTY = 0;
    private static final int PROGRESS_LOAD = 2;
    private static final int PROGRESS_NONE = -1;
    private static final int PROGRESS_PAUSE = 4;
    private static final int PROGRESS_PLAY = 3;
    private static volatile PhotoViewer PipInstance = null;
    public static final int SELECT_TYPE_AVATAR = 1;
    public static final int SELECT_TYPE_QR = 10;
    public static final int SELECT_TYPE_WALLPAPER = 3;
    private static final Property<VideoPlayerControlFrameLayout, Float> VPC_PROGRESS;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_cancel_loading = 7;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_edit_avatar = 17;
    private static final int gallery_menu_masks = 13;
    private static final int gallery_menu_masks2 = 15;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_pip = 5;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_savegif = 14;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_set_as_main = 16;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_share2 = 18;
    private static final int gallery_menu_showall = 2;
    private static final int gallery_menu_showinchat = 4;
    private static final int gallery_menu_speed = 19;
    private static Drawable[] progressDrawables = null;
    private static Paint progressPaint = null;
    private static final int thumbSize = 512;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimator;
    private Context activityContext;
    private ActionBarMenuSubItem allMediaItem;
    private boolean allowMentions;
    private boolean allowShare;
    private boolean allowShowFullscreenButton;
    private float animateToMirror;
    private float animateToRotate;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private boolean applying;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private long audioFramesSize;
    private float avatarStartProgress;
    private long avatarStartTime;
    private long avatarsDialogId;
    private int bitrate;
    private LinearLayout bottomButtonsLayout;
    private FrameLayout bottomLayout;
    private ImageView cameraItem;
    private boolean canEditAvatar;
    private FrameLayout captionContainer;
    private PhotoViewerCaptionEnterView captionEditText;
    public CharSequence captionForAllMedia;
    private boolean captionHwLayerEnabled;
    private TextView captionLimitView;
    private CaptionScrollView captionScrollView;
    private CaptionTextViewSwitcher captionTextViewSwitcher;
    private boolean centerImageIsVideo;
    private AnimatorSet changeModeAnimation;
    private TextureView changedTextureView;
    private boolean changingPage;
    private boolean changingTextureView;
    private CheckBox checkImageView;
    ChooseSpeedLayout chooseSpeedLayout;
    private int classGuid;
    private ImageView compressItem;
    private AnimatorSet compressItemAnimation;
    private FrameLayoutDrawer containerView;
    private boolean cropInitied;
    private ImageView cropItem;
    private int currentAccount;
    private AnimatedFileDrawable currentAnimation;
    private Bitmap currentBitmap;
    private TLRPC.BotInlineResult currentBotInlineResult;
    private long currentDialogId;
    private int currentEditMode;
    private ImageLocation currentFileLocation;
    private ImageLocation currentFileLocationVideo;
    private String currentImageFaceKey;
    private int currentImageHasFace;
    private String currentImagePath;
    private int currentIndex;
    private AnimatorSet currentListViewAnimation;
    private Runnable currentLoadingVideoRunnable;
    private MessageObject currentMessageObject;
    private TLRPC.PageBlock currentPageBlock;
    private float currentPanTranslationY;
    private String currentPathObject;
    private PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private SecureDocument currentSecureDocument;
    private String currentSubtitle;
    private ImageReceiver.BitmapHolder currentThumb;
    private boolean currentVideoFinishedLoading;
    private float currentVideoSpeed;
    private int dateOverride;
    private FadingTextViewLayout dateTextView;
    private boolean disableShowCheck;
    private boolean discardTap;
    private TextView docInfoTextView;
    private TextView docNameTextView;
    private boolean doneButtonPressed;
    private boolean dontAutoPlay;
    private boolean dontChangeCaptionPosition;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private boolean doubleTapEnabled;
    private float dragY;
    private boolean draggingDown;
    private PickerBottomLayoutViewer editorDoneLayout;
    private long endTime;
    private long estimatedDuration;
    private long estimatedSize;
    private ImageView exitFullscreenButton;
    private boolean firstAnimationDelay;
    private FirstFrameView firstFrameView;
    private AnimatorSet flashAnimator;
    private View flashView;
    boolean fromCamera;
    private int fullscreenedByButton;
    private GestureDetector2 gestureDetector;
    private GroupedPhotosListView groupedPhotosListView;
    public boolean hasCaptionForAllMedia;
    private PlaceProviderObject hideAfterAnimation;
    private UndoView hintView;
    private boolean ignoreDidSetImage;
    private AnimatorSet imageMoveAnimation;
    private boolean inBubbleMode;
    private boolean inPreview;
    private VideoPlayer injectingVideoPlayer;
    private SurfaceTexture injectingVideoPlayerSurface;
    private float inlineOutAnimationProgress;
    private boolean invalidCoords;
    private boolean isCurrentVideo;
    private boolean isDocumentsPicker;
    private boolean isEmbedVideo;
    private boolean isEvent;
    private boolean isFirstLoading;
    private boolean isInline;
    private boolean isPhotosListViewVisible;
    private boolean isPlaying;
    private boolean isStreaming;
    private boolean isVisible;
    private LinearLayout itemsLayout;
    private boolean keepScreenOnFlagSet;
    boolean keyboardAnimationEnabled;
    private int keyboardSize;
    private long lastBufferedPositionCheck;
    private Object lastInsets;
    private long lastPhotoSetTime;
    private long lastSaveTime;
    private String lastTitle;
    private MediaController.CropState leftCropState;
    private boolean leftImageIsVideo;
    private PaintingOverlay leftPaintingOverlay;
    private boolean loadInitialVideo;
    private boolean loadingMoreImages;
    float longPressX;
    private boolean manuallyPaused;
    private StickersAlert masksAlert;
    private ActionBarMenuItem masksItem;
    private float maxX;
    private float maxY;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private MentionsAdapter mentionsAdapter;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    private AnimatorSet miniProgressAnimator;
    private RadialProgressView miniProgressView;
    private ImageView mirrorItem;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private ImageView muteItem;
    private boolean muteVideo;
    private String nameOverride;
    private FadingTextViewLayout nameTextView;
    private ValueAnimator navBarAnimator;
    private View navigationBar;
    private int navigationBarHeight;
    private boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    private boolean needShowOnReady;
    private boolean openAnimationInProgress;
    private boolean openedFullScreenVideo;
    private boolean opennedFromMedia;
    private OrientationEventListener orientationEventListener;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private int originalWidth;
    private boolean padImageForHorizontalInsets;
    private PageBlocksAdapter pageBlocksAdapter;
    private ImageView paintButton;
    private ImageView paintItem;
    private int paintViewTouched;
    private PaintingOverlay paintingOverlay;
    private Activity parentActivity;
    private ChatAttachAlert parentAlert;
    private ChatActivity parentChatActivity;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private PhotoPaintView photoPaintView;
    private PhotoViewerWebView photoViewerWebView;
    private CounterView photosCounterView;
    private FrameLayout pickerView;
    private ImageView pickerViewSendButton;
    private Drawable pickerViewSendDrawable;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartX;
    private float pinchStartY;
    private boolean pipAnimationInProgress;
    private boolean pipAvailable;
    private ActionBarMenuItem pipItem;
    private PhotoViewerProvider placeProvider;
    private View playButtonAccessibilityOverlay;
    private boolean playerAutoStarted;
    private boolean playerInjected;
    private boolean playerLooping;
    private boolean playerWasPlaying;
    private boolean playerWasReady;
    private int previewViewEnd;
    private int previousCompression;
    private boolean previousCropMirrored;
    private int previousCropOrientation;
    private float previousCropPh;
    private float previousCropPw;
    private float previousCropPx;
    private float previousCropPy;
    private float previousCropRotation;
    private float previousCropScale;
    private boolean previousHasTransform;
    private RadialProgressView progressView;
    private QualityChooseView qualityChooseView;
    private AnimatorSet qualityChooseViewAnimation;
    private PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    private TextView resetButton;
    private Theme.ResourcesProvider resourcesProvider;
    private int resultHeight;
    private int resultWidth;
    private MediaController.CropState rightCropState;
    private boolean rightImageIsVideo;
    private PaintingOverlay rightPaintingOverlay;
    private ImageView rotateItem;
    private int rotationValue;
    private Scroller scroller;
    private float seekToProgressPending;
    private float seekToProgressPending2;
    private int selectedCompression;
    private ListAdapter selectedPhotosAdapter;
    private SelectedPhotosListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    private int sendPhotoType;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private ImageView shareButton;
    private ActionBarMenuItem shareItem;
    private int sharedMediaType;
    private String shouldSavePositionForCurrentVideo;
    private String shouldSavePositionForCurrentVideoShortTerm;
    private PlaceProviderObject showAfterAnimation;
    private ImageReceiver sideImage;
    private boolean skipFirstBufferingProgress;
    private int slideshowMessageId;
    private ActionBarPopupWindow.GapView speedGap;
    private ActionBarMenuSubItem speedItem;
    private int startOffset;
    private long startTime;
    private long startedPlayTime;
    private boolean streamingAlertShown;
    private int switchImageAfterAnimation;
    private boolean switchingInlineMode;
    private int switchingToIndex;
    private ImageView textureImageView;
    private boolean textureUploaded;
    private ImageView timeItem;
    private Tooltip tooltip;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    long totalRewinding;
    private int touchSlop;
    private long transitionAnimationStartTime;
    private int transitionIndex;
    private float translationX;
    private float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    private boolean useSmoothKeyboard;
    private VelocityTracker velocityTracker;
    private TextView videoAvatarTooltip;
    private boolean videoConvertSupported;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoCutEnd;
    private float videoCutStart;
    private float videoDuration;
    private VideoForwardDrawable videoForwardDrawable;
    private int videoFramerate;
    private long videoFramesSize;
    private int videoHeight;
    private Runnable videoPlayRunnable;
    private VideoPlayer videoPlayer;
    private Animator videoPlayerControlAnimator;
    private VideoPlayerControlFrameLayout videoPlayerControlFrameLayout;
    private VideoPlayerSeekBar videoPlayerSeekbar;
    private View videoPlayerSeekbarView;
    private SimpleTextView videoPlayerTime;
    private VideoSeekPreviewImage videoPreviewFrame;
    private AnimatorSet videoPreviewFrameAnimation;
    private MessageObject videoPreviewMessageObject;
    private boolean videoSizeSet;
    private TextureView videoTextureView;
    private ObjectAnimator videoTimelineAnimator;
    private VideoTimelinePlayView videoTimelineView;
    private int videoWidth;
    private AlertDialog visibleDialog;
    private int waitingForDraw;
    private int waitingForFirstTextureUpload;
    private boolean wasLayout;
    private boolean wasRotated;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;
    private int maxSelectedPhotos = -1;
    private boolean allowOrder = true;
    private Runnable miniProgressShowRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda43
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.m4212lambda$new$0$orgtelegramuiPhotoViewer();
        }
    };
    private boolean isActionBarVisible = true;
    private Map<View, Boolean> actionBarItemsVisibility = new HashMap(3);
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
    private Paint blackPaint = new Paint();
    private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
    private Runnable onUserLeaveHintListener = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda51
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.onUserLeaveHint();
        }
    };
    private GradientDrawable[] pressedDrawable = new GradientDrawable[2];
    private boolean[] drawPressedDrawable = new boolean[2];
    private float[] pressedDrawableAlpha = new float[2];
    private CropTransform cropTransform = new CropTransform();
    private CropTransform leftCropTransform = new CropTransform();
    private CropTransform rightCropTransform = new CropTransform();
    private Paint bitmapPaint = new Paint(2);
    private Runnable setLoadingRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer.1
        @Override // java.lang.Runnable
        public void run() {
            if (PhotoViewer.this.currentMessageObject != null) {
                FileLoader.getInstance(PhotoViewer.this.currentMessageObject.currentAccount).setLoadingVideo(PhotoViewer.this.currentMessageObject.getDocument(), true, false);
            }
        }
    };
    private Runnable hideActionBarRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer.2
        @Override // java.lang.Runnable
        public void run() {
            if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying && !ApplicationLoader.mainInterfacePaused) {
                if (PhotoViewer.this.menuItem == null || !PhotoViewer.this.menuItem.isSubMenuShowing()) {
                    if (PhotoViewer.this.captionScrollView == null || PhotoViewer.this.captionScrollView.getScrollY() == 0) {
                        if (PhotoViewer.this.miniProgressView == null || PhotoViewer.this.miniProgressView.getVisibility() != 0) {
                            PhotoViewer photoViewer = PhotoViewer.PipInstance;
                            PhotoViewer photoViewer2 = PhotoViewer.this;
                            if (photoViewer != photoViewer2) {
                                photoViewer2.toggleActionBar(false, true);
                            }
                        }
                    }
                }
            }
        }
    };
    private ArrayMap<String, SavedVideoPosition> savedVideoPositions = new ArrayMap<>();
    private boolean videoPlayerControlVisible = true;
    private int[] videoPlayerCurrentTime = new int[2];
    private int[] videoPlayerTotalTime = new int[2];
    private ImageView[] fullscreenButton = new ImageView[3];
    private int[] pipPosition = new int[2];
    private boolean pipVideoOverlayAnimateFlag = true;
    private int lastImageId = -1;
    private int prevOrientation = -10;
    VideoPlayerRewinder videoPlayerRewinder = new VideoPlayerRewinder() { // from class: org.telegram.ui.PhotoViewer.3
        @Override // org.telegram.messenger.video.VideoPlayerRewinder
        protected void onRewindCanceled() {
            PhotoViewer.this.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
            PhotoViewer.this.videoForwardDrawable.setShowing(false);
            PipVideoOverlay.onRewindCanceled();
        }

        @Override // org.telegram.messenger.video.VideoPlayerRewinder
        protected void updateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
            PhotoViewer.this.videoForwardDrawable.setTime(Math.abs(timeDiff));
            if (rewindByBackSeek) {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
            }
            PipVideoOverlay.onUpdateRewindProgressUi(timeDiff, progress, rewindByBackSeek);
        }

        @Override // org.telegram.messenger.video.VideoPlayerRewinder
        protected void onRewindStart(boolean rewindForward) {
            PhotoViewer.this.videoForwardDrawable.setOneShootAnimation(false);
            PhotoViewer.this.videoForwardDrawable.setLeftSide(!rewindForward);
            PhotoViewer.this.videoForwardDrawable.setShowing(true);
            PhotoViewer.this.containerView.invalidate();
            PipVideoOverlay.onRewindStart(rewindForward);
        }
    };
    public final Property<View, Float> FLASH_VIEW_VALUE = new AnimationProperties.FloatProperty<View>("flashViewAlpha") { // from class: org.telegram.ui.PhotoViewer.4
        public void setValue(View object, float value) {
            object.setAlpha(value);
            if (PhotoViewer.this.photoCropView != null) {
                PhotoViewer.this.photoCropView.setVideoThumbFlashAlpha(value);
            }
        }

        public Float get(View object) {
            return Float.valueOf(object.getAlpha());
        }
    };
    private Runnable updateProgressRunnable = new AnonymousClass5();
    private Runnable switchToInlineRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer.6
        @Override // java.lang.Runnable
        public void run() {
            if (!PipVideoOverlay.isVisible()) {
                PhotoViewer.this.switchingInlineMode = false;
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.currentBitmap.recycle();
                    PhotoViewer.this.currentBitmap = null;
                }
                PhotoViewer.this.changingTextureView = true;
                if (PhotoViewer.this.textureImageView != null) {
                    try {
                        PhotoViewer photoViewer = PhotoViewer.this;
                        photoViewer.currentBitmap = Bitmaps.createBitmap(photoViewer.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
                        PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
                    } catch (Throwable e) {
                        if (PhotoViewer.this.currentBitmap != null) {
                            PhotoViewer.this.currentBitmap.recycle();
                            PhotoViewer.this.currentBitmap = null;
                        }
                        FileLog.e(e);
                    }
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.textureImageView.setVisibility(0);
                        PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                    } else {
                        PhotoViewer.this.textureImageView.setImageDrawable(null);
                    }
                }
                PhotoViewer.this.isInline = true;
                PhotoViewer.this.changedTextureView = new TextureView(PhotoViewer.this.parentActivity);
                if (PipVideoOverlay.show(false, PhotoViewer.this.parentActivity, PhotoViewer.this.changedTextureView, PhotoViewer.this.videoWidth, PhotoViewer.this.videoHeight, PhotoViewer.this.pipVideoOverlayAnimateFlag)) {
                    PipVideoOverlay.setPhotoViewer(PhotoViewer.this);
                }
                PhotoViewer.this.pipVideoOverlayAnimateFlag = true;
                PhotoViewer.this.changedTextureView.setVisibility(4);
                PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
                return;
            }
            PipVideoOverlay.dismiss();
            AndroidUtilities.runOnUIThread(this, 250L);
        }
    };
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: org.telegram.ui.PhotoViewer.7
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (PhotoViewer.this.videoTextureView != null && PhotoViewer.this.changingTextureView) {
                if (PhotoViewer.this.switchingInlineMode) {
                    PhotoViewer.this.waitingForFirstTextureUpload = 2;
                }
                PhotoViewer.this.videoTextureView.setSurfaceTexture(surface);
                PhotoViewer.this.videoTextureView.setVisibility(0);
                PhotoViewer.this.changingTextureView = false;
                PhotoViewer.this.containerView.invalidate();
                return false;
            }
            return true;
        }

        /* renamed from: org.telegram.ui.PhotoViewer$7$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements ViewTreeObserver.OnPreDrawListener {
            AnonymousClass1() {
                AnonymousClass7.this = this$1;
            }

            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (PhotoViewer.this.textureImageView != null) {
                    if (!PhotoViewer.this.isInline) {
                        PhotoViewer.this.textureImageView.setVisibility(4);
                        PhotoViewer.this.textureImageView.setImageDrawable(null);
                        if (PhotoViewer.this.currentBitmap != null) {
                            PhotoViewer.this.currentBitmap.recycle();
                            PhotoViewer.this.currentBitmap = null;
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$7$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                PhotoViewer.AnonymousClass7.AnonymousClass1.this.m4293lambda$onPreDraw$0$orgtelegramuiPhotoViewer$7$1();
                            }
                        }, 300L);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$7$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass7.AnonymousClass1.this.m4294lambda$onPreDraw$1$orgtelegramuiPhotoViewer$7$1();
                    }
                });
                PhotoViewer.this.waitingForFirstTextureUpload = 0;
                return true;
            }

            /* renamed from: lambda$onPreDraw$0$org-telegram-ui-PhotoViewer$7$1 */
            public /* synthetic */ void m4293lambda$onPreDraw$0$orgtelegramuiPhotoViewer$7$1() {
                PhotoViewer.this.textureImageView.setVisibility(4);
                PhotoViewer.this.textureImageView.setImageDrawable(null);
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.currentBitmap.recycle();
                    PhotoViewer.this.currentBitmap = null;
                }
            }

            /* renamed from: lambda$onPreDraw$1$org-telegram-ui-PhotoViewer$7$1 */
            public /* synthetic */ void m4294lambda$onPreDraw$1$orgtelegramuiPhotoViewer$7$1() {
                if (PhotoViewer.this.isInline) {
                    PhotoViewer.this.dismissInternal();
                }
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 1) {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new AnonymousClass1());
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    };
    private float[][] animationValues = (float[][]) Array.newInstance(float.class, 2, 13);
    private final Runnable updateContainerFlagsRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda45
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.m4213lambda$new$3$orgtelegramuiPhotoViewer();
        }
    };
    private ImageReceiver leftImage = new ImageReceiver();
    private ImageReceiver centerImage = new ImageReceiver();
    private ImageReceiver rightImage = new ImageReceiver();
    private Paint videoFrameBitmapPaint = new Paint();
    private Bitmap videoFrameBitmap = null;
    private EditState editState = new EditState();
    private String[] currentFileNames = new String[3];
    private boolean[] endReached = {false, true};
    private boolean startReached = false;
    private float scale = 1.0f;
    private float rotate = 0.0f;
    private float mirror = 0.0f;
    private int switchingToMode = -1;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private float pinchStartScale = 1.0f;
    private boolean canZoom = true;
    private boolean canDragDown = true;
    private boolean shownControlsByEnd = false;
    private boolean actionBarWasShownBeforeByEnd = false;
    private boolean bottomTouchEnabled = true;
    private ArrayList<MessageObject> imagesArrTemp = new ArrayList<>();
    private SparseArray<MessageObject>[] imagesByIdsTemp = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<MessageObject> imagesArr = new ArrayList<>();
    private SparseArray<MessageObject>[] imagesByIds = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<ImageLocation> imagesArrLocations = new ArrayList<>();
    private ArrayList<ImageLocation> imagesArrLocationsVideo = new ArrayList<>();
    private ArrayList<Long> imagesArrLocationsSizes = new ArrayList<>();
    private ArrayList<TLRPC.Message> imagesArrMessages = new ArrayList<>();
    private ArrayList<SecureDocument> secureDocuments = new ArrayList<>();
    private ArrayList<TLRPC.Photo> avatarsArr = new ArrayList<>();
    private ArrayList<Object> imagesArrLocals = new ArrayList<>();
    private ImageLocation currentAvatarLocation = null;
    private SavedState savedState = null;
    private Rect hitRect = new Rect();
    Runnable longPressRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda50
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.onLongPress();
        }
    };
    private int[] tempInt = new int[2];
    private long captureFrameAtTime = -1;
    private long captureFrameReadyAtTime = -1;
    private long needCaptureFrameReadyAtTime = -1;
    private int compressionsCount = -1;

    /* loaded from: classes4.dex */
    public interface PageBlocksAdapter {
        TLRPC.PageBlock get(int i);

        List<TLRPC.PageBlock> getAll();

        CharSequence getCaption(int i);

        File getFile(int i);

        TLRPC.PhotoSize getFileLocation(TLObject tLObject, int[] iArr);

        String getFileName(int i);

        int getItemsCount();

        TLObject getMedia(int i);

        Object getParentObject();

        boolean isVideo(int i);

        void updateSlideshowCell(TLRPC.PageBlock pageBlock);
    }

    /* loaded from: classes4.dex */
    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean allowSendingSubmenu();

        boolean canCaptureMorePhotos();

        boolean canReplace(int i);

        boolean canScrollAway();

        boolean cancelButtonPressed();

        boolean closeKeyboard();

        void deleteImageAtIndex(int i);

        String getDeleteMessageString();

        MessageObject getEditingMessageObject();

        int getPhotoIndex(int i);

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        CharSequence getSubtitleFor(int i);

        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);

        CharSequence getTitleFor(int i);

        int getTotalImageCount();

        boolean isPhotoChecked(int i);

        boolean loadMore();

        void needAddMorePhotos();

        void onApplyCaption(CharSequence charSequence);

        void onCaptionChanged(CharSequence charSequence);

        void onClose();

        void onOpen();

        void openPhotoForEdit(String str, String str2, boolean z);

        void replaceButtonPressed(int i, VideoEditedInfo videoEditedInfo);

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoUnchecked(Object obj);

        void updatePhotoAtIndex(int i);

        boolean validateGroupId(long j);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);
    }

    /* loaded from: classes4.dex */
    public static class PlaceProviderObject {
        public ClippingImageView animatingImageView;
        public int animatingImageViewYOffset;
        public boolean canEdit;
        public int clipBottomAddition;
        public int clipTopAddition;
        public long dialogId;
        public ImageReceiver imageReceiver;
        public int index;
        public boolean isEvent;
        public View parentView;
        public int[] radius;
        public long size;
        public int starOffset;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
        public int viewY2;
        public float scale = 1.0f;
        public boolean allowTakeAnimation = true;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4212lambda$new$0$orgtelegramuiPhotoViewer() {
        toggleMiniProgressInternal(true);
    }

    public void addPhoto(MessageObject message, int classGuid) {
        if (classGuid != this.classGuid) {
            return;
        }
        if (this.imagesByIds[0].indexOfKey(message.getId()) < 0) {
            if (this.opennedFromMedia) {
                this.imagesArr.add(message);
            } else {
                this.imagesArr.add(0, message);
            }
            this.imagesByIds[0].put(message.getId(), message);
        }
        this.endReached[0] = this.imagesArr.size() == this.totalImagesCount;
        setImages();
    }

    public int getClassGuid() {
        return this.classGuid;
    }

    public void setCaption(CharSequence caption) {
        this.hasCaptionForAllMedia = true;
        this.captionForAllMedia = caption;
        setCurrentCaption(null, caption, false);
        updateCaptionTextForCurrentPhoto(null);
    }

    /* loaded from: classes4.dex */
    public static class SavedVideoPosition {
        public final float position;
        public final long timestamp;

        public SavedVideoPosition(float position, long timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }
    }

    public void onLinkClick(ClickableSpan link, TextView widget) {
        if (widget != null && (link instanceof URLSpan)) {
            String url = ((URLSpan) link).getURL();
            if (url.startsWith("video")) {
                if (this.videoPlayer != null && this.currentMessageObject != null) {
                    int seconds = Utilities.parseInt((CharSequence) url).intValue();
                    if (this.videoPlayer.getDuration() == C.TIME_UNSET) {
                        this.seekToProgressPending = seconds / this.currentMessageObject.getDuration();
                        return;
                    }
                    this.videoPlayer.seekTo(seconds * 1000);
                    this.videoPlayerSeekbar.setProgress(((float) (seconds * 1000)) / ((float) this.videoPlayer.getDuration()), true);
                    this.videoPlayerSeekbarView.invalidate();
                    return;
                }
                return;
            } else if (url.startsWith("#")) {
                if (this.parentActivity instanceof LaunchActivity) {
                    DialogsActivity fragment = new DialogsActivity(null);
                    fragment.setSearchString(url);
                    ((LaunchActivity) this.parentActivity).presentFragment(fragment, false, true);
                    closePhoto(false, false);
                    return;
                }
                return;
            } else if (this.parentChatActivity != null && ((link instanceof URLSpanReplacement) || AndroidUtilities.shouldShowUrlInAlert(url))) {
                AlertsCreator.showOpenUrlAlert(this.parentChatActivity, url, true, true);
                return;
            } else {
                link.onClick(widget);
                return;
            }
        }
        link.onClick(widget);
    }

    public void onLinkLongPress(final URLSpan link, final TextView widget, final Runnable onDismiss) {
        int timestamp = -1;
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity, false, this.resourcesProvider, -14933463);
        if (link.getURL().startsWith("video?")) {
            try {
                String timestampStr = link.getURL().substring(link.getURL().indexOf(63) + 1);
                timestamp = Integer.parseInt(timestampStr);
            } catch (Throwable th) {
            }
        }
        if (timestamp >= 0) {
            builder.setTitle(AndroidUtilities.formatDuration(timestamp, false));
        } else {
            builder.setTitle(link.getURL());
        }
        final int finalTimestamp = timestamp;
        builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda80
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.m4216lambda$onLinkLongPress$1$orgtelegramuiPhotoViewer(link, widget, finalTimestamp, dialogInterface, i);
            }
        });
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                onDismiss.run();
            }
        });
        BottomSheet bottomSheet = builder.create();
        bottomSheet.scrollNavBar = true;
        bottomSheet.show();
        try {
            this.containerView.performHapticFeedback(0, 2);
        } catch (Exception e) {
        }
        bottomSheet.setItemColor(0, -1, -1);
        bottomSheet.setItemColor(1, -1, -1);
        bottomSheet.setBackgroundColor(-14933463);
        bottomSheet.setTitleColor(-7697782);
        bottomSheet.setCalcMandatoryInsets(true);
        AndroidUtilities.setNavigationBarColor(bottomSheet.getWindow(), -14933463, false);
        AndroidUtilities.setLightNavigationBar(bottomSheet.getWindow(), false);
        bottomSheet.scrollNavBar = true;
    }

    /* renamed from: lambda$onLinkLongPress$1$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4216lambda$onLinkLongPress$1$orgtelegramuiPhotoViewer(URLSpan link, TextView widget, int finalTimestamp, DialogInterface dialog, int which) {
        String bulletinMessage;
        MessageObject messageObject;
        if (which == 0) {
            onLinkClick(link, widget);
            return;
        }
        boolean isMedia = true;
        if (which == 1) {
            String url1 = link.getURL();
            boolean tel = false;
            if (url1.startsWith(MailTo.MAILTO_SCHEME)) {
                url1 = url1.substring(7);
            } else if (url1.startsWith("tel:")) {
                url1 = url1.substring(4);
                tel = true;
            } else if (finalTimestamp >= 0 && (messageObject = this.currentMessageObject) != null && !messageObject.scheduled) {
                MessageObject messageObject1 = this.currentMessageObject;
                if (!this.currentMessageObject.isVideo() && !this.currentMessageObject.isRoundVideo() && !this.currentMessageObject.isVoice() && !this.currentMessageObject.isMusic()) {
                    isMedia = false;
                }
                if (!isMedia && this.currentMessageObject.replyMessageObject != null) {
                    messageObject1 = this.currentMessageObject.replyMessageObject;
                }
                long dialogId = messageObject1.getDialogId();
                int messageId = messageObject1.getId();
                if (messageObject1.messageOwner.fwd_from != null) {
                    if (messageObject1.messageOwner.fwd_from.saved_from_peer != null) {
                        dialogId = MessageObject.getPeerId(messageObject1.messageOwner.fwd_from.saved_from_peer);
                        messageId = messageObject1.messageOwner.fwd_from.saved_from_msg_id;
                    } else if (messageObject1.messageOwner.fwd_from.from_id != null) {
                        dialogId = MessageObject.getPeerId(messageObject1.messageOwner.fwd_from.from_id);
                        messageId = messageObject1.messageOwner.fwd_from.channel_post;
                    }
                }
                if (DialogObject.isChatDialog(dialogId)) {
                    TLRPC.Chat currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
                    if (currentChat != null && currentChat.username != null) {
                        url1 = "https://t.me/" + currentChat.username + "/" + messageId + "?t=" + finalTimestamp;
                    }
                } else {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId));
                    if (user != null && user.username != null) {
                        url1 = "https://t.me/" + user.username + "/" + messageId + "?t=" + finalTimestamp;
                    }
                }
            }
            AndroidUtilities.addToClipboard(url1);
            if (tel) {
                bulletinMessage = LocaleController.getString("PhoneCopied", R.string.PhoneCopied);
            } else if (url1.startsWith("#")) {
                bulletinMessage = LocaleController.getString("HashtagCopied", R.string.HashtagCopied);
            } else if (url1.startsWith("@")) {
                bulletinMessage = LocaleController.getString("UsernameCopied", R.string.UsernameCopied);
            } else {
                bulletinMessage = LocaleController.getString("LinkCopied", R.string.LinkCopied);
            }
            if (Build.VERSION.SDK_INT < 31) {
                BulletinFactory.of(this.containerView, this.resourcesProvider).createSimpleBulletin(R.raw.voip_invite, bulletinMessage).show();
            }
        }
    }

    public void cancelFlashAnimations() {
        View view = this.flashView;
        if (view != null) {
            view.animate().setListener(null).cancel();
            this.flashView.setAlpha(0.0f);
        }
        AnimatorSet animatorSet = this.flashAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.flashAnimator = null;
        }
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView != null) {
            photoCropView.cancelThumbAnimation();
        }
    }

    public void cancelVideoPlayRunnable() {
        Runnable runnable = this.videoPlayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.videoPlayRunnable = null;
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 implements Runnable {
        AnonymousClass5() {
            PhotoViewer.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            float bufferedProgress;
            if (PhotoViewer.this.videoPlayer != null) {
                if (PhotoViewer.this.isCurrentVideo) {
                    if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                        float progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                        if (PhotoViewer.this.shownControlsByEnd && !PhotoViewer.this.actionBarWasShownBeforeByEnd) {
                            progress = 0.0f;
                        }
                        if (PhotoViewer.this.inPreview || (PhotoViewer.this.currentEditMode == 0 && PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
                            if (PhotoViewer.this.sendPhotoType != 1) {
                                PhotoViewer.this.videoTimelineView.setProgress(progress);
                            }
                        } else if (progress >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                            PhotoViewer.this.videoTimelineView.setProgress(PhotoViewer.this.videoTimelineView.getLeftProgress());
                            PhotoViewer.this.videoPlayer.seekTo((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration())));
                            PhotoViewer.this.manuallyPaused = false;
                            PhotoViewer.this.cancelVideoPlayRunnable();
                            if (PhotoViewer.this.muteVideo || PhotoViewer.this.sendPhotoType == 1 || PhotoViewer.this.currentEditMode != 0 || PhotoViewer.this.switchingToMode > 0) {
                                PhotoViewer.this.videoPlayer.play();
                            } else {
                                PhotoViewer.this.videoPlayer.pause();
                            }
                            PhotoViewer.this.containerView.invalidate();
                        } else {
                            PhotoViewer.this.videoTimelineView.setProgress(progress);
                        }
                        PhotoViewer.this.updateVideoPlayerTime();
                    }
                } else {
                    float progress2 = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.shownControlsByEnd && !PhotoViewer.this.actionBarWasShownBeforeByEnd) {
                        progress2 = 0.0f;
                    }
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        bufferedProgress = 1.0f;
                    } else {
                        long newTime = SystemClock.elapsedRealtime();
                        if (Math.abs(newTime - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.isStreaming) {
                                bufferedProgress = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : progress2, PhotoViewer.this.currentFileNames[0]);
                            } else {
                                bufferedProgress = 1.0f;
                            }
                            PhotoViewer.this.lastBufferedPositionCheck = newTime;
                        } else {
                            bufferedProgress = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f && (PhotoViewer.this.videoPlayerRewinder.rewindCount == 0 || !PhotoViewer.this.videoPlayerRewinder.rewindByBackSeek)) {
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(progress2, false);
                        }
                        if (bufferedProgress != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                            PipVideoOverlay.setBufferedProgress(bufferedProgress);
                        }
                    } else if (progress2 >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.manuallyPaused = false;
                        PhotoViewer.this.videoPlayer.pause();
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration())));
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float progress3 = progress2 - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress3 < 0.0f) {
                            progress3 = 0.0f;
                        }
                        progress2 = progress3 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (progress2 > 1.0f) {
                            progress2 = 1.0f;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(progress2);
                    }
                    PhotoViewer.this.videoPlayerSeekbarView.invalidate();
                    if (PhotoViewer.this.shouldSavePositionForCurrentVideo != null) {
                        final float value = progress2;
                        if (value >= 0.0f && SystemClock.elapsedRealtime() - PhotoViewer.this.lastSaveTime >= 1000) {
                            String unused = PhotoViewer.this.shouldSavePositionForCurrentVideo;
                            PhotoViewer.this.lastSaveTime = SystemClock.elapsedRealtime();
                            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$5$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    PhotoViewer.AnonymousClass5.this.m4280lambda$run$0$orgtelegramuiPhotoViewer$5(value);
                                }
                            });
                        }
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.updateAlpha();
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17L);
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-PhotoViewer$5 */
        public /* synthetic */ void m4280lambda$run$0$orgtelegramuiPhotoViewer$5(float value) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit();
            editor.putFloat(PhotoViewer.this.shouldSavePositionForCurrentVideo, value).commit();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4213lambda$new$3$orgtelegramuiPhotoViewer() {
        if (this.isVisible && this.animationInProgress == 0) {
            updateContainerFlags(this.isActionBarVisible);
        }
    }

    /* loaded from: classes4.dex */
    public static class EditState {
        public long averageDuration;
        public MediaController.CropState cropState;
        public ArrayList<VideoEditedInfo.MediaEntity> croppedMediaEntities;
        public String croppedPaintPath;
        public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
        public String paintPath;
        public MediaController.SavedFilterState savedFilterState;

        private EditState() {
        }

        public void reset() {
            this.paintPath = null;
            this.cropState = null;
            this.savedFilterState = null;
            this.mediaEntities = null;
            this.croppedPaintPath = null;
            this.croppedMediaEntities = null;
            this.averageDuration = 0L;
        }
    }

    /* loaded from: classes4.dex */
    public class SavedState {
        private int index;
        private ArrayList<MessageObject> messages;
        private PhotoViewerProvider provider;

        public SavedState(int index, ArrayList<MessageObject> messages, PhotoViewerProvider provider) {
            PhotoViewer.this = r1;
            this.messages = messages;
            this.index = index;
            this.provider = provider;
        }

        public void restore() {
            PhotoViewer.this.placeProvider = this.provider;
            if (Build.VERSION.SDK_INT >= 21) {
                PhotoViewer.this.windowLayoutParams.flags = -2147286784;
            } else {
                PhotoViewer.this.windowLayoutParams.flags = 131072;
            }
            PhotoViewer.this.windowLayoutParams.softInputMode = (PhotoViewer.this.useSmoothKeyboard ? 32 : 16) | 256;
            PhotoViewer.this.windowView.setFocusable(false);
            PhotoViewer.this.containerView.setFocusable(false);
            PhotoViewer.this.backgroundDrawable.setAlpha(255);
            PhotoViewer.this.containerView.setAlpha(1.0f);
            PhotoViewer photoViewer = PhotoViewer.this;
            ArrayList<MessageObject> arrayList = this.messages;
            int i = this.index;
            photoViewer.onPhotoShow(null, null, null, null, arrayList, null, null, i, this.provider.getPlaceForPhoto(arrayList.get(i), null, this.index, true));
        }
    }

    /* loaded from: classes4.dex */
    public class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        private Runnable drawRunnable;
        private final Paint paint;
        private final RectF rect = new RectF();
        private final RectF visibleRect = new RectF();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BackgroundDrawable(int color) {
            super(color);
            PhotoViewer.this = r2;
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setColor(color);
        }

        @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                this.allowDrawContent = !PhotoViewer.this.isVisible || alpha != 255;
                ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                if (PhotoViewer.this.parentAlert != null) {
                    if (this.allowDrawContent) {
                        PhotoViewer.this.parentAlert.setAllowDrawContent(true);
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$BackgroundDrawable$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                PhotoViewer.BackgroundDrawable.this.m4297lambda$setAlpha$0$orgtelegramuiPhotoViewer$BackgroundDrawable();
                            }
                        }, 50L);
                    }
                }
            }
            super.setAlpha(alpha);
            this.paint.setAlpha(alpha);
        }

        /* renamed from: lambda$setAlpha$0$org-telegram-ui-PhotoViewer$BackgroundDrawable */
        public /* synthetic */ void m4297lambda$setAlpha$0$orgtelegramuiPhotoViewer$BackgroundDrawable() {
            if (PhotoViewer.this.parentAlert != null) {
                PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
            }
        }

        @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Runnable runnable;
            if (PhotoViewer.this.animationInProgress != 0 && !AndroidUtilities.isTablet() && PhotoViewer.this.currentPlaceObject != null && PhotoViewer.this.currentPlaceObject.animatingImageView != null) {
                PhotoViewer.this.animatingImageView.getClippedVisibleRect(this.visibleRect);
                if (!this.visibleRect.isEmpty()) {
                    this.visibleRect.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    Rect boundsRect = getBounds();
                    float width = boundsRect.right;
                    float height = boundsRect.bottom;
                    for (int i = 0; i < 4; i++) {
                        switch (i) {
                            case 0:
                                this.rect.set(0.0f, this.visibleRect.top, this.visibleRect.left, this.visibleRect.bottom);
                                break;
                            case 1:
                                this.rect.set(0.0f, 0.0f, width, this.visibleRect.top);
                                break;
                            case 2:
                                this.rect.set(this.visibleRect.right, this.visibleRect.top, width, this.visibleRect.bottom);
                                break;
                            case 3:
                                this.rect.set(0.0f, this.visibleRect.bottom, width, height);
                                break;
                        }
                        canvas.drawRect(this.rect, this.paint);
                    }
                }
            } else {
                super.draw(canvas);
            }
            if (getAlpha() != 0 && (runnable = this.drawRunnable) != null) {
                AndroidUtilities.runOnUIThread(runnable);
                this.drawRunnable = null;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class SelectedPhotosListView extends RecyclerListView {
        private Drawable arrowDrawable;
        private Paint paint = new Paint(1);
        private RectF rect = new RectF();

        public SelectedPhotosListView(Context context) {
            super(context);
            setWillNotDraw(false);
            setClipToPadding(false);
            setTranslationY(-AndroidUtilities.dp(10.0f));
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.PhotoViewer.SelectedPhotosListView.1
                @Override // androidx.recyclerview.widget.DefaultItemAnimator
                protected void onMoveAnimationUpdate(RecyclerView.ViewHolder holder) {
                    SelectedPhotosListView.this.invalidate();
                }
            };
            setItemAnimator(defaultItemAnimator);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f));
            this.paint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.arrowDrawable = context.getResources().getDrawable(R.drawable.photo_tooltip2).mutate();
        }

        @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
        public void onDraw(Canvas c) {
            super.onDraw(c);
            int count = getChildCount();
            if (count > 0) {
                int x = getMeasuredWidth() - AndroidUtilities.dp(87.0f);
                Drawable drawable = this.arrowDrawable;
                drawable.setBounds(x, 0, drawable.getIntrinsicWidth() + x, AndroidUtilities.dp(6.0f));
                this.arrowDrawable.draw(c);
                int minX = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                for (int a = 0; a < count; a++) {
                    View v = getChildAt(a);
                    minX = (int) Math.min(minX, Math.floor(v.getX()));
                    maxX = (int) Math.max(maxX, Math.ceil(v.getX() + v.getMeasuredWidth()));
                }
                if (minX != Integer.MAX_VALUE && maxX != Integer.MIN_VALUE) {
                    this.rect.set(minX - AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f) + maxX, AndroidUtilities.dp(103.0f));
                    c.drawRoundRect(this.rect, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), this.paint);
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class CounterView extends View {
        private int height;
        private Paint paint;
        private float rotation;
        private StaticLayout staticLayout;
        private TextPaint textPaint;
        private int width;
        private int currentCount = 0;
        private RectF rect = new RectF();

        public CounterView(Context context) {
            super(context);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(15.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textPaint.setColor(-1);
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setColor(-1);
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            setCount(0);
        }

        @Override // android.view.View
        public void setScaleX(float scaleX) {
            super.setScaleX(scaleX);
            invalidate();
        }

        @Override // android.view.View
        public void setRotationX(float rotationX) {
            this.rotation = rotationX;
            invalidate();
        }

        @Override // android.view.View
        public float getRotationX() {
            return this.rotation;
        }

        public void setCount(int value) {
            StaticLayout staticLayout = new StaticLayout("" + Math.max(1, value), this.textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.staticLayout = staticLayout;
            this.width = (int) Math.ceil((double) staticLayout.getLineWidth(0));
            this.height = this.staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (value == 0) {
                animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f), ObjectAnimator.ofInt(this.paint, AnimationProperties.PAINT_ALPHA, 0), ObjectAnimator.ofInt(this.textPaint, (Property<TextPaint, Integer>) AnimationProperties.PAINT_ALPHA, 0));
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else {
                int i = this.currentCount;
                if (i == 0) {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f, 1.0f), ObjectAnimator.ofInt(this.paint, AnimationProperties.PAINT_ALPHA, 0, 255), ObjectAnimator.ofInt(this.textPaint, (Property<TextPaint, Integer>) AnimationProperties.PAINT_ALPHA, 0, 255));
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                } else if (value < i) {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 1.1f, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.1f, 1.0f));
                    animatorSet.setInterpolator(new OvershootInterpolator());
                } else {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.9f, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.9f, 1.0f));
                    animatorSet.setInterpolator(new OvershootInterpolator());
                }
            }
            animatorSet.setDuration(180L);
            animatorSet.start();
            requestLayout();
            this.currentCount = value;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(30.0f)), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int cy = getMeasuredHeight() / 2;
            this.paint.setAlpha(255);
            this.rect.set(AndroidUtilities.dp(1.0f), cy - AndroidUtilities.dp(14.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(14.0f) + cy);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), this.paint);
            if (this.staticLayout != null) {
                this.textPaint.setAlpha((int) ((1.0f - this.rotation) * 255.0f));
                canvas.save();
                canvas.translate((getMeasuredWidth() - this.width) / 2, ((getMeasuredHeight() - this.height) / 2) + AndroidUtilities.dpf2(0.2f) + (this.rotation * AndroidUtilities.dp(5.0f)));
                this.staticLayout.draw(canvas);
                canvas.restore();
                this.paint.setAlpha((int) (this.rotation * 255.0f));
                int cx = (int) this.rect.centerX();
                int cy2 = (int) (((int) this.rect.centerY()) - (AndroidUtilities.dp(5.0f) * (1.0f - this.rotation)));
                canvas.drawLine(AndroidUtilities.dp(5.0f) + cx, cy2 - AndroidUtilities.dp(5.0f), cx - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f) + cy2, this.paint);
                canvas.drawLine(cx - AndroidUtilities.dp(5.0f), cy2 - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(5.0f) + cy2, this.paint);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class PhotoProgressView {
        private View parent;
        private final CombinedDrawable playDrawable;
        private final PlayPauseDrawable playPauseDrawable;
        private boolean visible;
        private long lastUpdateTime = 0;
        private float radOffset = 0.0f;
        private float currentProgress = 0.0f;
        private float animationProgressStart = 0.0f;
        private long currentProgressTime = 0;
        private float animatedProgressValue = 0.0f;
        private RectF progressRect = new RectF();
        private int backgroundState = -1;
        private int size = AndroidUtilities.dp(64.0f);
        private int previousBackgroundState = -2;
        private float animatedAlphaValue = 1.0f;
        private float[] animAlphas = new float[3];
        private float[] alphas = new float[3];
        private float scale = 1.0f;

        public PhotoProgressView(View parentView) {
            PhotoViewer.this = r5;
            if (PhotoViewer.decelerateInterpolator == null) {
                DecelerateInterpolator unused = PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                Paint unused2 = PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
            resetAlphas();
            PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable(28);
            this.playPauseDrawable = playPauseDrawable;
            playPauseDrawable.setDuration(200);
            Drawable circleDrawable = ContextCompat.getDrawable(r5.parentActivity, R.drawable.circle_big);
            this.playDrawable = new CombinedDrawable(circleDrawable.mutate(), playPauseDrawable);
        }

        private void updateAnimation(boolean withProgressAnimation) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 18) {
                dt = 18;
            }
            this.lastUpdateTime = newTime;
            boolean postInvalidate = false;
            if (withProgressAnimation) {
                if (this.animatedProgressValue != 1.0f || this.currentProgress != 1.0f) {
                    this.radOffset += ((float) (360 * dt)) / 3000.0f;
                    float progressDiff = this.currentProgress - this.animationProgressStart;
                    if (Math.abs(progressDiff) > 0.0f) {
                        long j = this.currentProgressTime + dt;
                        this.currentProgressTime = j;
                        if (j < 300) {
                            this.animatedProgressValue = this.animationProgressStart + (PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
                        } else {
                            float f = this.currentProgress;
                            this.animatedProgressValue = f;
                            this.animationProgressStart = f;
                            this.currentProgressTime = 0L;
                        }
                    }
                    postInvalidate = true;
                }
                float progressDiff2 = this.animatedAlphaValue;
                if (progressDiff2 > 0.0f && this.previousBackgroundState != -2) {
                    float f2 = progressDiff2 - (((float) dt) / 200.0f);
                    this.animatedAlphaValue = f2;
                    if (f2 <= 0.0f) {
                        this.animatedAlphaValue = 0.0f;
                        this.previousBackgroundState = -2;
                    }
                    postInvalidate = true;
                }
            }
            int i = 0;
            while (true) {
                float[] fArr = this.alphas;
                if (i >= fArr.length) {
                    break;
                }
                float f3 = fArr[i];
                float[] fArr2 = this.animAlphas;
                if (f3 > fArr2[i]) {
                    fArr2[i] = Math.min(1.0f, fArr2[i] + (((float) dt) / 200.0f));
                    postInvalidate = true;
                } else if (fArr[i] < fArr2[i]) {
                    fArr2[i] = Math.max(0.0f, fArr2[i] - (((float) dt) / 200.0f));
                    postInvalidate = true;
                }
                i++;
            }
            if (postInvalidate) {
                this.parent.postInvalidateOnAnimation();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (!animated) {
                this.animatedProgressValue = value;
                this.animationProgressStart = value;
            } else {
                this.animationProgressStart = this.animatedProgressValue;
            }
            this.currentProgress = value;
            this.currentProgressTime = 0L;
            this.parent.invalidate();
        }

        public void setBackgroundState(int state, boolean animated, boolean animateIcon) {
            int i;
            int i2 = this.backgroundState;
            if (i2 == state) {
                return;
            }
            PlayPauseDrawable playPauseDrawable = this.playPauseDrawable;
            if (playPauseDrawable != null) {
                boolean animatePlayPause = animateIcon && (i2 == 3 || i2 == 4);
                if (state == 3) {
                    playPauseDrawable.setPause(false, animatePlayPause);
                } else if (state == 4) {
                    playPauseDrawable.setPause(true, animatePlayPause);
                }
                this.playPauseDrawable.setParent(this.parent);
                this.playPauseDrawable.invalidateSelf();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (animated && (i = this.backgroundState) != state) {
                this.previousBackgroundState = i;
                this.animatedAlphaValue = 1.0f;
            } else {
                this.previousBackgroundState = -2;
            }
            this.backgroundState = state;
            onBackgroundStateUpdated(state);
            this.parent.invalidate();
        }

        protected void onBackgroundStateUpdated(int state) {
        }

        public void setAlpha(float value) {
            setIndexedAlpha(0, value, false);
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public void setIndexedAlpha(int index, float alpha, boolean animated) {
            float[] fArr = this.alphas;
            if (fArr[index] != alpha) {
                fArr[index] = alpha;
                if (!animated) {
                    this.animAlphas[index] = alpha;
                }
                checkVisibility();
                this.parent.invalidate();
            }
        }

        public void resetAlphas() {
            int i = 0;
            while (true) {
                float[] fArr = this.alphas;
                if (i < fArr.length) {
                    this.animAlphas[i] = 1.0f;
                    fArr[i] = 1.0f;
                    i++;
                } else {
                    checkVisibility();
                    return;
                }
            }
        }

        private float calculateAlpha() {
            float f;
            float alpha = 1.0f;
            int i = 0;
            while (true) {
                float[] fArr = this.animAlphas;
                if (i < fArr.length) {
                    if (i == 2) {
                        f = AndroidUtilities.accelerateInterpolator.getInterpolation(this.animAlphas[i]);
                    } else {
                        f = fArr[i];
                    }
                    alpha *= f;
                    i++;
                } else {
                    return alpha;
                }
            }
        }

        private void checkVisibility() {
            boolean newVisible = true;
            int i = 0;
            while (true) {
                float[] fArr = this.alphas;
                if (i >= fArr.length) {
                    break;
                } else if (fArr[i] == 1.0f) {
                    i++;
                } else {
                    newVisible = false;
                    break;
                }
            }
            if (newVisible != this.visible) {
                this.visible = newVisible;
                onVisibilityChanged(newVisible);
            }
        }

        protected void onVisibilityChanged(boolean visible) {
        }

        public boolean isVisible() {
            return this.visible;
        }

        public int getX() {
            return (PhotoViewer.this.containerView.getWidth() - ((int) (this.size * this.scale))) / 2;
        }

        public int getY() {
            int y = (int) ((((AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - ((int) (this.size * this.scale))) / 2) + PhotoViewer.this.currentPanTranslationY);
            if (PhotoViewer.this.sendPhotoType == 1) {
                return y - AndroidUtilities.dp(38.0f);
            }
            return y;
        }

        public void onDraw(Canvas canvas) {
            int i;
            Drawable drawable;
            Drawable drawable2;
            int sizeScaled = (int) (this.size * this.scale);
            int x = getX();
            int y = getY();
            float alpha = calculateAlpha();
            int i2 = this.previousBackgroundState;
            if (i2 >= 0 && i2 < PhotoViewer.progressDrawables.length + 2) {
                if (this.previousBackgroundState < PhotoViewer.progressDrawables.length) {
                    drawable2 = PhotoViewer.progressDrawables[this.previousBackgroundState];
                } else {
                    drawable2 = this.playDrawable;
                }
                if (drawable2 != null) {
                    drawable2.setAlpha((int) (this.animatedAlphaValue * 255.0f * alpha));
                    drawable2.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable2.draw(canvas);
                }
            }
            int i3 = this.backgroundState;
            if (i3 >= 0 && i3 < PhotoViewer.progressDrawables.length + 2) {
                if (this.backgroundState < PhotoViewer.progressDrawables.length) {
                    drawable = PhotoViewer.progressDrawables[this.backgroundState];
                } else {
                    drawable = this.playDrawable;
                }
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * alpha));
                    } else {
                        drawable.setAlpha((int) (alpha * 255.0f));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            int i4 = this.backgroundState;
            if (i4 == 0 || i4 == 1 || (i = this.previousBackgroundState) == 0 || i == 1) {
                int diff = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (255.0f * alpha));
                }
                this.progressRect.set(x + diff, y + diff, (x + sizeScaled) - diff, (y + sizeScaled) - diff);
                canvas.drawArc(this.progressRect, (-90.0f) + this.radOffset, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, PhotoViewer.progressPaint);
                updateAnimation(true);
                return;
            }
            updateAnimation(false);
        }
    }

    /* loaded from: classes4.dex */
    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoUnchecked(Object photoEntry) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isPhotoChecked(int index) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean cancelButtonPressed() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canReplace(int index) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getSelectedCount() {
            return 0;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void updatePhotoAtIndex(int index) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowSendingSubmenu() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowCaption() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean scaleToFill() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canScrollAway() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void needAddMorePhotos() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getPhotoIndex(int index) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void deleteImageAtIndex(int index) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public String getDeleteMessageString() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canCaptureMorePhotos() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void openPhotoForEdit(String file, String thumb, boolean isVideo) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getTotalImageCount() {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean loadMore() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getTitleFor(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getSubtitleFor(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public MessageObject getEditingMessageObject() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onCaptionChanged(CharSequence caption) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean closeKeyboard() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean validateGroupId(long groupId) {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onApplyCaption(CharSequence caption) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onOpen() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onClose() {
        }
    }

    /* loaded from: classes4.dex */
    public class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private boolean captionAbove;
        private boolean ignoreLayout;
        private Paint paint = new Paint();
        AdjustPanLayoutHelper adjustPanLayoutHelper = new AdjustPanLayoutHelper(this, false) { // from class: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
                PhotoViewer.this.currentPanTranslationY = y;
                if (PhotoViewer.this.currentEditMode != 3) {
                    PhotoViewer.this.actionBar.setTranslationY(y);
                }
                if (PhotoViewer.this.miniProgressView != null) {
                    PhotoViewer.this.miniProgressView.setTranslationY(y);
                }
                if (PhotoViewer.this.progressView != null) {
                    PhotoViewer.this.progressView.setTranslationY(y);
                }
                if (PhotoViewer.this.checkImageView != null) {
                    PhotoViewer.this.checkImageView.setTranslationY(y);
                }
                if (PhotoViewer.this.photosCounterView != null) {
                    PhotoViewer.this.photosCounterView.setTranslationY(y);
                }
                if (PhotoViewer.this.selectedPhotosListView != null) {
                    PhotoViewer.this.selectedPhotosListView.setTranslationY(y);
                }
                if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                    PhotoViewer.this.aspectRatioFrameLayout.setTranslationY(y);
                }
                if (PhotoViewer.this.textureImageView != null) {
                    PhotoViewer.this.textureImageView.setTranslationY(y);
                }
                if (PhotoViewer.this.photoCropView != null) {
                    PhotoViewer.this.photoCropView.setTranslationY(y);
                }
                if (PhotoViewer.this.photoFilterView != null) {
                    PhotoViewer.this.photoFilterView.setTranslationY(y);
                }
                if (PhotoViewer.this.pickerView != null) {
                    PhotoViewer.this.pickerView.setTranslationY(y);
                }
                if (PhotoViewer.this.pickerViewSendButton != null) {
                    PhotoViewer.this.pickerViewSendButton.setTranslationY(y);
                }
                float f = 0.0f;
                if (PhotoViewer.this.currentEditMode == 3) {
                    if (PhotoViewer.this.captionEditText != null) {
                        PhotoViewer.this.captionEditText.setTranslationY(y);
                    }
                    if (PhotoViewer.this.photoPaintView != null) {
                        PhotoViewer.this.photoPaintView.setTranslationY(0.0f);
                        PhotoViewer.this.photoPaintView.getColorPicker().setTranslationY(y);
                        PhotoViewer.this.photoPaintView.getToolsView().setTranslationY(y);
                        PhotoViewer.this.photoPaintView.getColorPickerBackground().setTranslationY(y);
                        PhotoViewer.this.photoPaintView.getCurtainView().setTranslationY(y);
                    }
                } else {
                    if (PhotoViewer.this.photoPaintView != null) {
                        PhotoViewer.this.photoPaintView.setTranslationY(y);
                    }
                    if (PhotoViewer.this.captionEditText != null) {
                        if (progress >= 0.5f) {
                            f = (progress - 0.5f) / 0.5f;
                        }
                        float p = f;
                        PhotoViewer.this.captionEditText.setAlpha(p);
                        PhotoViewer.this.captionEditText.setTranslationY((y - this.keyboardSize) + (AndroidUtilities.dp(this.keyboardSize / 2.0f) * (1.0f - progress)));
                    }
                }
                if (PhotoViewer.this.muteItem != null) {
                    PhotoViewer.this.muteItem.setTranslationY(y);
                }
                if (PhotoViewer.this.cameraItem != null) {
                    PhotoViewer.this.cameraItem.setTranslationY(y);
                }
                if (PhotoViewer.this.captionLimitView != null) {
                    PhotoViewer.this.captionLimitView.setTranslationY(y);
                }
                FrameLayoutDrawer.this.invalidate();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
                String str;
                int i;
                PhotoViewer.this.navigationBar.setVisibility(4);
                PhotoViewer.this.animateNavBarColorTo(-16777216);
                if (PhotoViewer.this.captionEditText.getTag() == null || !keyboardVisible) {
                    PhotoViewer.this.checkImageView.animate().alpha(1.0f).setDuration(220L).start();
                    PhotoViewer.this.photosCounterView.animate().alpha(1.0f).setDuration(220L).start();
                    if (PhotoViewer.this.lastTitle != null && !PhotoViewer.this.isCurrentVideo) {
                        PhotoViewer.this.actionBar.setTitleAnimated(PhotoViewer.this.lastTitle, false, 220L);
                        PhotoViewer.this.lastTitle = null;
                        return;
                    }
                    return;
                }
                if (PhotoViewer.this.isCurrentVideo) {
                    if (PhotoViewer.this.muteVideo) {
                        i = R.string.GifCaption;
                        str = "GifCaption";
                    } else {
                        i = R.string.VideoCaption;
                        str = "VideoCaption";
                    }
                    CharSequence title = LocaleController.getString(str, i);
                    PhotoViewer.this.actionBar.setTitleAnimated(title, true, 220L);
                } else {
                    PhotoViewer.this.actionBar.setTitleAnimated(LocaleController.getString("PhotoCaption", R.string.PhotoCaption), true, 220L);
                }
                PhotoViewer.this.captionEditText.setAlpha(0.0f);
                PhotoViewer.this.checkImageView.animate().alpha(0.0f).setDuration(220L).start();
                PhotoViewer.this.photosCounterView.animate().alpha(0.0f).setDuration(220L).start();
                PhotoViewer.this.selectedPhotosListView.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(220L).start();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            public void onTransitionEnd() {
                super.onTransitionEnd();
                PhotoViewer.this.navigationBar.setVisibility(PhotoViewer.this.currentEditMode != 2 ? 0 : 4);
                if (PhotoViewer.this.captionEditText.getTag() == null) {
                    PhotoViewer.this.captionEditText.setVisibility(8);
                }
                PhotoViewer.this.captionEditText.setTranslationY(0.0f);
            }

            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            protected boolean heightAnimationEnabled() {
                return !PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.keyboardAnimationEnabled;
            }
        };

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FrameLayoutDrawer(Context context) {
            super(context, false);
            PhotoViewer.this = r2;
            setWillNotDraw(false);
            this.paint.setColor(AndroidUtilities.DARK_STATUS_BAR_OVERLAY);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int heightSize;
            int groupedPhotosHeight;
            int i;
            int height;
            int width;
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
            if (getLayoutParams().height <= 0) {
                heightSize = heightSize2;
            } else {
                int heightSize3 = getLayoutParams().height;
                heightSize = heightSize3;
            }
            setMeasuredDimension(widthSize, heightSize);
            boolean z = true;
            if (!PhotoViewer.this.isCurrentVideo) {
                this.ignoreLayout = true;
                if (!PhotoViewer.this.needCaptionLayout) {
                    PhotoViewer.this.captionTextViewSwitcher.getCurrentView().setMaxLines(Integer.MAX_VALUE);
                    PhotoViewer.this.captionTextViewSwitcher.getNextView().setMaxLines(Integer.MAX_VALUE);
                } else {
                    int maxLines = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 5 : 10;
                    PhotoViewer.this.captionTextViewSwitcher.getCurrentView().setMaxLines(maxLines);
                    PhotoViewer.this.captionTextViewSwitcher.getNextView().setMaxLines(maxLines);
                }
                this.ignoreLayout = false;
            }
            measureChildWithMargins(PhotoViewer.this.captionEditText, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int inputFieldHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int i2 = 8;
            int bottomLayoutHeight = PhotoViewer.this.bottomLayout.getVisibility() != 8 ? AndroidUtilities.dp(48.0f) : 0;
            if (PhotoViewer.this.groupedPhotosListView != null && PhotoViewer.this.groupedPhotosListView.getVisibility() != 8) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) PhotoViewer.this.groupedPhotosListView.getLayoutParams();
                lp.bottomMargin = bottomLayoutHeight;
                measureChildWithMargins(PhotoViewer.this.groupedPhotosListView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int groupedPhotosHeight2 = PhotoViewer.this.groupedPhotosListView.getMeasuredHeight();
                this.ignoreLayout = true;
                if (AndroidUtilities.isTablet() || heightSize >= widthSize) {
                    if (PhotoViewer.this.groupedPhotosListView.getVisibility() != 0) {
                        PhotoViewer.this.groupedPhotosListView.setVisibility(0);
                    }
                } else if (PhotoViewer.this.groupedPhotosListView.getVisibility() != 4) {
                    PhotoViewer.this.groupedPhotosListView.setVisibility(4);
                }
                this.ignoreLayout = false;
                groupedPhotosHeight = groupedPhotosHeight2;
            } else {
                groupedPhotosHeight = 0;
            }
            if (PhotoViewer.this.videoPlayerControlFrameLayout != null) {
                PhotoViewer.this.videoPlayerControlFrameLayout.parentWidth = widthSize;
                PhotoViewer.this.videoPlayerControlFrameLayout.parentHeight = heightSize;
            }
            int widthSize2 = widthSize - (getPaddingRight() + getPaddingLeft());
            int heightSize4 = heightSize - getPaddingBottom();
            int childCount = getChildCount();
            int i3 = 0;
            while (i3 < childCount) {
                View child = getChildAt(i3);
                if (child.getVisibility() == i2 || child == PhotoViewer.this.captionEditText) {
                    i = i3;
                } else if (child != PhotoViewer.this.groupedPhotosListView) {
                    if (child != PhotoViewer.this.aspectRatioFrameLayout) {
                        if (child == PhotoViewer.this.paintingOverlay) {
                            if (PhotoViewer.this.aspectRatioFrameLayout == null || PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                width = PhotoViewer.this.centerImage.getBitmapWidth();
                                height = PhotoViewer.this.centerImage.getBitmapHeight();
                            } else {
                                width = PhotoViewer.this.videoTextureView.getMeasuredWidth();
                                height = PhotoViewer.this.videoTextureView.getMeasuredHeight();
                            }
                            if (width == 0 || height == 0) {
                                width = widthSize2;
                                height = heightSize4;
                            }
                            PhotoViewer.this.paintingOverlay.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                            i = i3;
                        } else if (PhotoViewer.this.captionEditText.isPopupView(child)) {
                            if (PhotoViewer.this.inBubbleMode) {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(heightSize4 - inputFieldHeight, C.BUFFER_FLAG_ENCRYPTED));
                                i = i3;
                            } else if (AndroidUtilities.isInMultiwindow) {
                                if (AndroidUtilities.isTablet()) {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (heightSize4 - inputFieldHeight) - AndroidUtilities.statusBarHeight), C.BUFFER_FLAG_ENCRYPTED));
                                    i = i3;
                                } else {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize4 - inputFieldHeight) - AndroidUtilities.statusBarHeight, C.BUFFER_FLAG_ENCRYPTED));
                                    i = i3;
                                }
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                                i = i3;
                            }
                        } else if (child == PhotoViewer.this.captionScrollView) {
                            int bottomMargin = bottomLayoutHeight;
                            if (!PhotoViewer.this.dontChangeCaptionPosition) {
                                if (!PhotoViewer.this.groupedPhotosListView.hasPhotos() || (!AndroidUtilities.isTablet() && heightSize4 <= widthSize2)) {
                                    this.captionAbove = false;
                                } else {
                                    bottomMargin += groupedPhotosHeight;
                                    this.captionAbove = z;
                                }
                            } else if (this.captionAbove) {
                                bottomMargin += groupedPhotosHeight;
                            }
                            int topMargin = (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                            ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionScrollView.getLayoutParams()).bottomMargin = bottomMargin;
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize4 - topMargin) - bottomMargin, C.BUFFER_FLAG_ENCRYPTED));
                            i = i3;
                        } else {
                            i = i3;
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    } else {
                        int heightSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0), C.BUFFER_FLAG_ENCRYPTED);
                        child.measure(widthMeasureSpec, heightSpec);
                        i = i3;
                    }
                } else {
                    i = i3;
                }
                i3 = i + 1;
                z = true;
                i2 = 8;
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int _l, int t, int _r, int _b) {
            int paddingBottom;
            int keyboardHeight;
            int count;
            int b;
            int r;
            int l;
            int childLeft;
            int childTop;
            FrameLayoutDrawer frameLayoutDrawer = this;
            int count2 = getChildCount();
            int keyboardHeight2 = measureKeyboardHeight();
            PhotoViewer.this.keyboardSize = keyboardHeight2;
            int paddingBottom2 = (keyboardHeight2 > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
            int i = 0;
            while (i < count2) {
                View child = frameLayoutDrawer.getChildAt(i);
                if (child.getVisibility() != 8) {
                    if (child == PhotoViewer.this.aspectRatioFrameLayout) {
                        l = _l;
                        r = _r;
                        b = _b;
                    } else {
                        l = _l + getPaddingLeft();
                        r = _r - getPaddingRight();
                        b = _b - getPaddingBottom();
                    }
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int horizontalGravity = gravity & 7;
                    int verticalGravity = gravity & 112;
                    switch (horizontalGravity) {
                        case 1:
                            count = count2;
                            int count3 = r - l;
                            childLeft = (((count3 - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            count = count2;
                            int count4 = lp.rightMargin;
                            childLeft = ((r - l) - width) - count4;
                            break;
                        default:
                            count = count2;
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case 16:
                            keyboardHeight = keyboardHeight2;
                            childTop = (((((b - paddingBottom2) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                            keyboardHeight = keyboardHeight2;
                            childTop = (((b - paddingBottom2) - t) - height) - lp.bottomMargin;
                            break;
                        default:
                            keyboardHeight = keyboardHeight2;
                            childTop = lp.topMargin;
                            break;
                    }
                    if (child == PhotoViewer.this.mentionListView) {
                        childTop -= PhotoViewer.this.captionEditText.getMeasuredHeight();
                        paddingBottom = paddingBottom2;
                    } else if (!PhotoViewer.this.captionEditText.isPopupView(child)) {
                        if (child == PhotoViewer.this.selectedPhotosListView) {
                            childTop = PhotoViewer.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(5.0f);
                            paddingBottom = paddingBottom2;
                        } else {
                            if (child != PhotoViewer.this.cameraItem) {
                                if (child == PhotoViewer.this.muteItem) {
                                    paddingBottom = paddingBottom2;
                                } else if (child == PhotoViewer.this.videoTimelineView) {
                                    childTop -= PhotoViewer.this.pickerView.getHeight();
                                    paddingBottom = paddingBottom2;
                                    if (PhotoViewer.this.sendPhotoType == 1) {
                                        childTop -= AndroidUtilities.dp(52.0f);
                                    }
                                } else {
                                    paddingBottom = paddingBottom2;
                                    if (child == PhotoViewer.this.videoAvatarTooltip) {
                                        childTop -= PhotoViewer.this.pickerView.getHeight() + AndroidUtilities.dp(31.0f);
                                    }
                                }
                            } else {
                                paddingBottom = paddingBottom2;
                            }
                            int top = (PhotoViewer.this.videoTimelineView == null || PhotoViewer.this.videoTimelineView.getVisibility() != 0) ? PhotoViewer.this.pickerView.getTop() : PhotoViewer.this.videoTimelineView.getTop();
                            childTop = (top - AndroidUtilities.dp((PhotoViewer.this.sendPhotoType == 4 || PhotoViewer.this.sendPhotoType == 5) ? 40.0f : 15.0f)) - child.getMeasuredHeight();
                        }
                    } else if (AndroidUtilities.isInMultiwindow) {
                        childTop = (PhotoViewer.this.captionEditText.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        paddingBottom = paddingBottom2;
                    } else {
                        childTop = PhotoViewer.this.captionEditText.getBottom();
                        paddingBottom = paddingBottom2;
                    }
                    child.layout(childLeft + l, childTop, childLeft + width + l, childTop + height);
                } else {
                    count = count2;
                    keyboardHeight = keyboardHeight2;
                    paddingBottom = paddingBottom2;
                }
                i++;
                frameLayoutDrawer = this;
                count2 = count;
                keyboardHeight2 = keyboardHeight;
                paddingBottom2 = paddingBottom;
            }
            notifyHeightChanged();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (PhotoViewer.this.isStatusBarVisible() && AndroidUtilities.statusBarHeight != 0 && PhotoViewer.this.actionBar != null) {
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.2f));
                canvas.drawRect(0.0f, PhotoViewer.this.currentPanTranslationY, getMeasuredWidth(), PhotoViewer.this.currentPanTranslationY + AndroidUtilities.statusBarHeight, this.paint);
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.498f));
                if (getPaddingRight() > 0) {
                    canvas.drawRect(getMeasuredWidth() - getPaddingRight(), 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
                }
                if (getPaddingLeft() > 0) {
                    canvas.drawRect(0.0f, 0.0f, getPaddingLeft(), getMeasuredHeight(), this.paint);
                }
                if (getPaddingBottom() > 0) {
                    float offset = AndroidUtilities.dpf2(24.0f) * (1.0f - PhotoViewer.this.actionBar.getAlpha());
                    canvas.drawRect(0.0f, (getMeasuredHeight() - getPaddingBottom()) + offset, getMeasuredWidth(), getMeasuredHeight() + offset, this.paint);
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == PhotoViewer.this.leftPaintingOverlay || child == PhotoViewer.this.rightPaintingOverlay) {
                return false;
            }
            if (child != PhotoViewer.this.navigationBar) {
                canvas.save();
                canvas.clipRect(0, 0, getWidth(), getHeight());
            }
            boolean result = drawChildInternal(canvas, child, drawingTime);
            if (child != PhotoViewer.this.navigationBar) {
                canvas.restore();
            }
            return result;
        }

        protected boolean drawChildInternal(Canvas canvas, View child, long drawingTime) {
            if (child == PhotoViewer.this.mentionListView || child == PhotoViewer.this.captionEditText) {
                if (PhotoViewer.this.currentEditMode != 0 && PhotoViewer.this.currentPanTranslationY == 0.0f) {
                    return false;
                }
                if (AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) {
                    if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && PhotoViewer.this.captionEditText.getTag() == null) {
                        return false;
                    }
                } else if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && getKeyboardHeight() == 0 && PhotoViewer.this.currentPanTranslationY == 0.0f) {
                    return false;
                }
                if (child == PhotoViewer.this.mentionListView) {
                    canvas.save();
                    canvas.clipRect(child.getX(), child.getY(), child.getX() + child.getWidth(), child.getY() + child.getHeight());
                    boolean r = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return r;
                }
            } else if (child == PhotoViewer.this.cameraItem || child == PhotoViewer.this.muteItem || child == PhotoViewer.this.pickerView || child == PhotoViewer.this.videoTimelineView || child == PhotoViewer.this.pickerViewSendButton || child == PhotoViewer.this.captionLimitView || child == PhotoViewer.this.captionTextViewSwitcher || (PhotoViewer.this.muteItem.getVisibility() == 0 && child == PhotoViewer.this.bottomLayout)) {
                if (PhotoViewer.this.captionEditText.isPopupAnimating()) {
                    child.setTranslationY(PhotoViewer.this.captionEditText.getEmojiPadding());
                    PhotoViewer.this.bottomTouchEnabled = false;
                } else {
                    int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
                    if (PhotoViewer.this.captionEditText.isPopupShowing() || (((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) && PhotoViewer.this.captionEditText.getTag() != null) || getKeyboardHeight() > AndroidUtilities.dp(80.0f) || paddingBottom != 0)) {
                        PhotoViewer.this.bottomTouchEnabled = false;
                        return false;
                    }
                    PhotoViewer.this.bottomTouchEnabled = true;
                }
            } else if (child == PhotoViewer.this.checkImageView || child == PhotoViewer.this.photosCounterView) {
                if (PhotoViewer.this.captionEditText.getTag() != null) {
                    PhotoViewer.this.bottomTouchEnabled = false;
                    if (child.getAlpha() < 0.0f) {
                        return false;
                    }
                } else {
                    PhotoViewer.this.bottomTouchEnabled = true;
                }
            } else if (child == PhotoViewer.this.miniProgressView) {
                return false;
            }
            if (child != PhotoViewer.this.videoTimelineView || PhotoViewer.this.videoTimelineView.getTranslationY() <= 0.0f || PhotoViewer.this.pickerView.getTranslationY() != 0.0f) {
                try {
                    if (child != PhotoViewer.this.aspectRatioFrameLayout && child != PhotoViewer.this.paintingOverlay) {
                        if (super.drawChild(canvas, child, drawingTime)) {
                            return true;
                        }
                    }
                    return false;
                } catch (Throwable th) {
                    return true;
                }
            }
            canvas.save();
            canvas.clipRect(PhotoViewer.this.videoTimelineView.getX(), PhotoViewer.this.videoTimelineView.getY(), PhotoViewer.this.videoTimelineView.getX() + PhotoViewer.this.videoTimelineView.getMeasuredWidth(), PhotoViewer.this.videoTimelineView.getBottom());
            boolean b = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return b;
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.adjustPanLayoutHelper.setResizableView(PhotoViewer.this.windowView);
            this.adjustPanLayoutHelper.onAttach();
            Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.2
                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public int getBottomOffset(int tag) {
                    int offset = 0;
                    if (PhotoViewer.this.bottomLayout != null && PhotoViewer.this.bottomLayout.getVisibility() == 0) {
                        offset = 0 + PhotoViewer.this.bottomLayout.getHeight();
                    }
                    if (PhotoViewer.this.groupedPhotosListView != null && PhotoViewer.this.groupedPhotosListView.hasPhotos()) {
                        if (AndroidUtilities.isTablet() || PhotoViewer.this.containerView.getMeasuredHeight() > PhotoViewer.this.containerView.getMeasuredWidth()) {
                            return offset + PhotoViewer.this.groupedPhotosListView.getHeight();
                        }
                        return offset;
                    }
                    return offset;
                }
            });
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.adjustPanLayoutHelper.onDetach();
            Bulletin.removeDelegate(this);
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto
        public void notifyHeightChanged() {
            super.notifyHeightChanged();
            if (PhotoViewer.this.isCurrentVideo) {
                PhotoViewer.this.photoProgressViews[0].setIndexedAlpha(2, getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? 1.0f : 0.0f, true);
            }
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 24) {
            VPC_PROGRESS = new FloatProperty<VideoPlayerControlFrameLayout>(NotificationCompat.CATEGORY_PROGRESS) { // from class: org.telegram.ui.PhotoViewer.8
                public void setValue(VideoPlayerControlFrameLayout object, float value) {
                    object.setProgress(value);
                }

                public Float get(VideoPlayerControlFrameLayout object) {
                    return Float.valueOf(object.getProgress());
                }
            };
        } else {
            VPC_PROGRESS = new Property<VideoPlayerControlFrameLayout, Float>(Float.class, NotificationCompat.CATEGORY_PROGRESS) { // from class: org.telegram.ui.PhotoViewer.9
                public void set(VideoPlayerControlFrameLayout object, Float value) {
                    object.setProgress(value.floatValue());
                }

                public Float get(VideoPlayerControlFrameLayout object) {
                    return Float.valueOf(object.getProgress());
                }
            };
        }
        Instance = null;
        PipInstance = null;
    }

    /* loaded from: classes4.dex */
    public class VideoPlayerControlFrameLayout extends FrameLayout {
        private boolean ignoreLayout;
        private int parentHeight;
        private int parentWidth;
        private boolean seekBarTransitionEnabled;
        private float progress = 1.0f;
        private boolean translationYAnimationEnabled = true;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public VideoPlayerControlFrameLayout(Context context) {
            super(context);
            PhotoViewer.this = r1;
            setWillNotDraw(false);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (this.progress >= 1.0f) {
                if (!PhotoViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - AndroidUtilities.dp(2.0f), event.getY())) {
                    return true;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
                return true;
            }
            return false;
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int extraWidth;
            long duration;
            this.ignoreLayout = true;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.videoPlayerTime.getLayoutParams();
            if (this.parentWidth > this.parentHeight) {
                if (PhotoViewer.this.exitFullscreenButton.getVisibility() != 0) {
                    PhotoViewer.this.exitFullscreenButton.setVisibility(0);
                }
                extraWidth = AndroidUtilities.dp(48.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(47.0f);
            } else {
                if (PhotoViewer.this.exitFullscreenButton.getVisibility() != 4) {
                    PhotoViewer.this.exitFullscreenButton.setVisibility(4);
                }
                extraWidth = 0;
                layoutParams.rightMargin = AndroidUtilities.dp(12.0f);
            }
            this.ignoreLayout = false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (PhotoViewer.this.videoPlayer != null) {
                duration = PhotoViewer.this.videoPlayer.getDuration();
                if (duration == C.TIME_UNSET) {
                    duration = 0;
                }
            } else {
                duration = 0;
            }
            long duration2 = duration / 1000;
            int size = (int) Math.ceil(PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format(Locale.ROOT, "%02d:%02d / %02d:%02d", Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60), Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60))));
            PhotoViewer.this.videoPlayerSeekbar.setSize(((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) - size) - extraWidth, getMeasuredHeight());
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            float progress = 0.0f;
            if (PhotoViewer.this.videoPlayer != null) {
                progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
            }
            if (PhotoViewer.this.playerWasReady) {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
            }
            PhotoViewer.this.videoTimelineView.setProgress(progress);
        }

        public float getProgress() {
            return this.progress;
        }

        public void setProgress(float progress) {
            if (this.progress != progress) {
                this.progress = progress;
                onProgressChanged(progress);
            }
        }

        private void onProgressChanged(float progress) {
            PhotoViewer.this.videoPlayerTime.setAlpha(progress);
            PhotoViewer.this.exitFullscreenButton.setAlpha(progress);
            if (this.seekBarTransitionEnabled) {
                PhotoViewer.this.videoPlayerTime.setPivotX(PhotoViewer.this.videoPlayerTime.getWidth());
                PhotoViewer.this.videoPlayerTime.setPivotY(PhotoViewer.this.videoPlayerTime.getHeight());
                PhotoViewer.this.videoPlayerTime.setScaleX(1.0f - ((1.0f - progress) * 0.1f));
                PhotoViewer.this.videoPlayerTime.setScaleY(1.0f - ((1.0f - progress) * 0.1f));
                PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(1.0f - progress);
                return;
            }
            if (this.translationYAnimationEnabled) {
                setTranslationY(AndroidUtilities.dpf2(24.0f) * (1.0f - progress));
            }
            PhotoViewer.this.videoPlayerSeekbarView.setAlpha(progress);
        }

        public boolean isSeekBarTransitionEnabled() {
            return this.seekBarTransitionEnabled;
        }

        public void setSeekBarTransitionEnabled(boolean seekBarTransitionEnabled) {
            if (this.seekBarTransitionEnabled != seekBarTransitionEnabled) {
                this.seekBarTransitionEnabled = seekBarTransitionEnabled;
                if (!seekBarTransitionEnabled) {
                    PhotoViewer.this.videoPlayerTime.setScaleX(1.0f);
                    PhotoViewer.this.videoPlayerTime.setScaleY(1.0f);
                    PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(0.0f);
                } else {
                    setTranslationY(0.0f);
                    PhotoViewer.this.videoPlayerSeekbarView.setAlpha(1.0f);
                }
                onProgressChanged(this.progress);
            }
        }

        public void setTranslationYAnimationEnabled(boolean translationYAnimationEnabled) {
            if (this.translationYAnimationEnabled != translationYAnimationEnabled) {
                this.translationYAnimationEnabled = translationYAnimationEnabled;
                if (!translationYAnimationEnabled) {
                    setTranslationY(0.0f);
                }
                onProgressChanged(this.progress);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class CaptionTextViewSwitcher extends TextViewSwitcher {
        private boolean inScrollView = false;
        private float alpha = 1.0f;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CaptionTextViewSwitcher(Context context) {
            super(context);
            PhotoViewer.this = r1;
        }

        @Override // android.view.View
        public void setVisibility(int visibility) {
            setVisibility(visibility, true);
        }

        public void setVisibility(int visibility, boolean withScrollView) {
            super.setVisibility(visibility);
            if (this.inScrollView && withScrollView) {
                PhotoViewer.this.captionScrollView.setVisibility(visibility);
            }
        }

        @Override // android.view.View
        public void setAlpha(float alpha) {
            this.alpha = alpha;
            if (this.inScrollView) {
                PhotoViewer.this.captionScrollView.setAlpha(alpha);
            } else {
                super.setAlpha(alpha);
            }
        }

        @Override // android.view.View
        public float getAlpha() {
            if (this.inScrollView) {
                return this.alpha;
            }
            return super.getAlpha();
        }

        @Override // android.view.View
        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            if (this.inScrollView) {
                PhotoViewer.this.captionScrollView.invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (PhotoViewer.this.captionContainer != null && getParent() == PhotoViewer.this.captionContainer) {
                this.inScrollView = true;
                PhotoViewer.this.captionScrollView.setVisibility(getVisibility());
                PhotoViewer.this.captionScrollView.setAlpha(this.alpha);
                super.setAlpha(1.0f);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.inScrollView) {
                this.inScrollView = false;
                PhotoViewer.this.captionScrollView.setVisibility(8);
                super.setAlpha(this.alpha);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class CaptionScrollView extends NestedScrollView {
        private Method abortAnimatedScrollMethod;
        private boolean dontChangeTopMargin;
        private boolean isLandscape;
        private boolean nestedScrollStarted;
        private float overScrollY;
        private final Paint paint;
        private int prevHeight;
        private OverScroller scroller;
        private final SpringAnimation springAnimation;
        private int textHash;
        private float velocitySign;
        private float velocityY;
        private float backgroundAlpha = 1.0f;
        private int pendingTopMargin = -1;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CaptionScrollView(Context context) {
            super(context);
            PhotoViewer.this = r7;
            Paint paint = new Paint(1);
            this.paint = paint;
            setClipChildren(false);
            setOverScrollMode(2);
            paint.setColor(-16777216);
            setFadingEdgeLength(AndroidUtilities.dp(12.0f));
            setVerticalFadingEdgeEnabled(true);
            setWillNotDraw(false);
            SpringAnimation springAnimation = new SpringAnimation(r7.captionTextViewSwitcher, DynamicAnimation.TRANSLATION_Y, 0.0f);
            this.springAnimation = springAnimation;
            springAnimation.getSpring().setStiffness(100.0f);
            springAnimation.setMinimumVisibleChange(1.0f);
            springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.PhotoViewer$CaptionScrollView$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    PhotoViewer.CaptionScrollView.this.m4298lambda$new$0$orgtelegramuiPhotoViewer$CaptionScrollView(dynamicAnimation, f, f2);
                }
            });
            springAnimation.getSpring().setDampingRatio(1.0f);
            try {
                Method declaredMethod = NestedScrollView.class.getDeclaredMethod("abortAnimatedScroll", new Class[0]);
                this.abortAnimatedScrollMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (Exception e) {
                this.abortAnimatedScrollMethod = null;
                FileLog.e(e);
            }
            try {
                Field scrollerField = NestedScrollView.class.getDeclaredField("mScroller");
                scrollerField.setAccessible(true);
                this.scroller = (OverScroller) scrollerField.get(this);
            } catch (Exception e2) {
                this.scroller = null;
                FileLog.e(e2);
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-PhotoViewer$CaptionScrollView */
        public /* synthetic */ void m4298lambda$new$0$orgtelegramuiPhotoViewer$CaptionScrollView(DynamicAnimation animation, float value, float velocity) {
            this.overScrollY = value;
            this.velocityY = velocity;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            if (ev.getAction() == 0 && ev.getY() < (PhotoViewer.this.captionContainer.getTop() - getScrollY()) + PhotoViewer.this.captionTextViewSwitcher.getTranslationY()) {
                return false;
            }
            return super.onTouchEvent(ev);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            updateTopMargin(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void applyPendingTopMargin() {
            this.dontChangeTopMargin = false;
            if (this.pendingTopMargin >= 0) {
                ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin = this.pendingTopMargin;
                this.pendingTopMargin = -1;
                requestLayout();
            }
        }

        public int getPendingMarginTopDiff() {
            int i = this.pendingTopMargin;
            if (i >= 0) {
                return i - ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin;
            }
            return 0;
        }

        public void updateTopMargin() {
            updateTopMargin(getWidth(), getHeight());
        }

        private void updateTopMargin(int width, int height) {
            int marginTop = calculateNewContainerMarginTop(width, height);
            if (marginTop >= 0) {
                if (!this.dontChangeTopMargin) {
                    ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin = marginTop;
                    this.pendingTopMargin = -1;
                    return;
                }
                this.pendingTopMargin = marginTop;
            }
        }

        public int calculateNewContainerMarginTop(int width, int height) {
            if (width != 0 && height != 0) {
                TextView textView = PhotoViewer.this.captionTextViewSwitcher.getCurrentView();
                CharSequence text = textView.getText();
                int textHash = text.hashCode();
                boolean isLandscape = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
                if (this.textHash == textHash && this.isLandscape == isLandscape && this.prevHeight == height) {
                    return -1;
                }
                this.textHash = textHash;
                this.isLandscape = isLandscape;
                this.prevHeight = height;
                textView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                Layout layout = textView.getLayout();
                int lineCount = layout.getLineCount();
                int i = 2;
                if ((isLandscape && lineCount <= 2) || (!isLandscape && lineCount <= 5)) {
                    return height - textView.getMeasuredHeight();
                }
                if (!isLandscape) {
                    i = 5;
                }
                int i2 = Math.min(i, lineCount);
                loop0: while (i2 > 1) {
                    for (int j = layout.getLineStart(i2 - 1); j < layout.getLineEnd(i2 - 1); j++) {
                        if (Character.isLetterOrDigit(text.charAt(j))) {
                            break loop0;
                        }
                    }
                    i2--;
                }
                int lineHeight = textView.getPaint().getFontMetricsInt(null);
                return (height - (lineHeight * i2)) - AndroidUtilities.dp(8.0f);
            }
            return -1;
        }

        public void reset() {
            scrollTo(0, 0);
        }

        public void stopScrolling() {
            Method method = this.abortAnimatedScrollMethod;
            if (method != null) {
                try {
                    method.invoke(this, new Object[0]);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        @Override // androidx.core.widget.NestedScrollView
        public void fling(int velocityY) {
            super.fling(velocityY);
            this.velocitySign = Math.signum(velocityY);
            this.velocityY = 0.0f;
        }

        @Override // androidx.core.widget.NestedScrollView, androidx.core.view.NestedScrollingChild2
        public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
            consumed[1] = 0;
            if (this.nestedScrollStarted) {
                float f = this.overScrollY;
                if ((f > 0.0f && dy > 0) || (f < 0.0f && dy < 0)) {
                    float delta = f - dy;
                    if (f > 0.0f) {
                        if (delta < 0.0f) {
                            this.overScrollY = 0.0f;
                            consumed[1] = (int) (consumed[1] + dy + delta);
                        } else {
                            this.overScrollY = delta;
                            consumed[1] = consumed[1] + dy;
                        }
                    } else if (delta > 0.0f) {
                        this.overScrollY = 0.0f;
                        consumed[1] = (int) (consumed[1] + dy + delta);
                    } else {
                        this.overScrollY = delta;
                        consumed[1] = consumed[1] + dy;
                    }
                    PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                    return true;
                }
            }
            return false;
        }

        @Override // androidx.core.widget.NestedScrollView, androidx.core.view.NestedScrollingChild3
        public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type, int[] consumed) {
            int consumedY;
            float clampedVelocity;
            if (dyUnconsumed != 0) {
                int topMargin = (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                int dy = Math.round(dyUnconsumed * (1.0f - Math.abs((-this.overScrollY) / (PhotoViewer.this.captionContainer.getTop() - topMargin))));
                if (dy != 0) {
                    if (!this.nestedScrollStarted) {
                        if (!this.springAnimation.isRunning()) {
                            OverScroller overScroller = this.scroller;
                            float velocity = overScroller != null ? overScroller.getCurrVelocity() : Float.NaN;
                            if (!Float.isNaN(velocity)) {
                                float clampedVelocity2 = Math.min(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 3000.0f : 5000.0f, velocity);
                                consumedY = (int) ((dy * clampedVelocity2) / velocity);
                                clampedVelocity = clampedVelocity2 * (-this.velocitySign);
                            } else {
                                consumedY = dy;
                                clampedVelocity = 0.0f;
                            }
                            if (consumedY != 0) {
                                this.overScrollY -= consumedY;
                                PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                            }
                            startSpringAnimationIfNotRunning(clampedVelocity);
                            return;
                        }
                        return;
                    }
                    this.overScrollY -= dy;
                    PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                }
            }
        }

        private void startSpringAnimationIfNotRunning(float velocityY) {
            if (!this.springAnimation.isRunning()) {
                this.springAnimation.setStartVelocity(velocityY);
                this.springAnimation.start();
            }
        }

        @Override // androidx.core.widget.NestedScrollView, androidx.core.view.NestedScrollingChild2
        public boolean startNestedScroll(int axes, int type) {
            if (type == 0) {
                this.springAnimation.cancel();
                this.nestedScrollStarted = true;
                this.overScrollY = PhotoViewer.this.captionTextViewSwitcher.getTranslationY();
            }
            return true;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        public void computeScroll() {
            OverScroller overScroller;
            super.computeScroll();
            if (!this.nestedScrollStarted && this.overScrollY != 0.0f && (overScroller = this.scroller) != null && overScroller.isFinished()) {
                startSpringAnimationIfNotRunning(0.0f);
            }
        }

        @Override // androidx.core.widget.NestedScrollView, androidx.core.view.NestedScrollingChild2
        public void stopNestedScroll(int type) {
            OverScroller overScroller;
            if (this.nestedScrollStarted && type == 0) {
                this.nestedScrollStarted = false;
                if (this.overScrollY != 0.0f && (overScroller = this.scroller) != null && overScroller.isFinished()) {
                    startSpringAnimationIfNotRunning(this.velocityY);
                }
            }
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        protected float getTopFadingEdgeStrength() {
            return 1.0f;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        protected float getBottomFadingEdgeStrength() {
            return 1.0f;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        public void draw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int scrollY = getScrollY();
            int saveCount = canvas.save();
            canvas.clipRect(0, scrollY, width, height + scrollY);
            this.paint.setAlpha((int) (this.backgroundAlpha * 127.0f));
            canvas.drawRect(0.0f, PhotoViewer.this.captionContainer.getTop() + PhotoViewer.this.captionTextViewSwitcher.getTranslationY(), width, height + scrollY, this.paint);
            super.draw(canvas);
            canvas.restoreToCount(saveCount);
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            if (PhotoViewer.this.isActionBarVisible) {
                int scrollY = getScrollY();
                float translationY = PhotoViewer.this.captionTextViewSwitcher.getTranslationY();
                float f = 0.0f;
                boolean buttonVisible = scrollY == 0 && translationY == 0.0f;
                boolean enalrgeIconVisible = scrollY == 0 && translationY == 0.0f;
                if (!buttonVisible) {
                    int progressBottom = PhotoViewer.this.photoProgressViews[0].getY() + PhotoViewer.this.photoProgressViews[0].size;
                    int topMargin = (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    int captionTop = (((PhotoViewer.this.captionContainer.getTop() + ((int) translationY)) - scrollY) + topMargin) - AndroidUtilities.dp(12.0f);
                    int enlargeIconTop = (int) PhotoViewer.this.fullscreenButton[0].getY();
                    enalrgeIconVisible = captionTop > AndroidUtilities.dp(32.0f) + enlargeIconTop;
                    buttonVisible = captionTop > progressBottom;
                }
                if (PhotoViewer.this.allowShowFullscreenButton) {
                    if (PhotoViewer.this.fullscreenButton[0].getTag() == null || ((Integer) PhotoViewer.this.fullscreenButton[0].getTag()).intValue() != 3 || !enalrgeIconVisible) {
                        if (PhotoViewer.this.fullscreenButton[0].getTag() == null && !enalrgeIconVisible) {
                            PhotoViewer.this.fullscreenButton[0].setTag(3);
                            PhotoViewer.this.fullscreenButton[0].animate().alpha(0.0f).setListener(null).setDuration(150L).start();
                        }
                    } else {
                        PhotoViewer.this.fullscreenButton[0].setTag(2);
                        PhotoViewer.this.fullscreenButton[0].animate().alpha(1.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.CaptionScrollView.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                PhotoViewer.this.fullscreenButton[0].setTag(null);
                            }
                        }).start();
                    }
                }
                PhotoProgressView photoProgressView = PhotoViewer.this.photoProgressViews[0];
                if (buttonVisible) {
                    f = 1.0f;
                }
                photoProgressView.setIndexedAlpha(2, f, true);
            }
        }
    }

    public static PhotoViewer getPipInstance() {
        return PipInstance;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    PhotoViewer photoViewer = new PhotoViewer();
                    localInstance = photoViewer;
                    Instance = photoViewer;
                }
            }
        }
        return localInstance;
    }

    public boolean isOpenedFullScreenVideo() {
        return this.openedFullScreenVideo;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public PhotoViewer() {
        this.blackPaint.setColor(-16777216);
        this.videoFrameBitmapPaint.setColor(-1);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        boolean scheduled;
        int loadFromMaxId;
        int i;
        ImageLocation location;
        int i2;
        long did;
        int guid;
        float bufferedProgress;
        float progress;
        MessageObject messageObject;
        TLRPC.BotInlineResult botInlineResult;
        PageBlocksAdapter pageBlocksAdapter;
        float f = 1.0f;
        int i3 = 3;
        int i4 = 2;
        int i5 = 1;
        int i6 = 0;
        if (id == NotificationCenter.fileLoadFailed) {
            String location2 = (String) args[0];
            int a = 0;
            while (true) {
                if (a >= 3) {
                    break;
                }
                String[] strArr = this.currentFileNames;
                if (strArr[a] == null || !strArr[a].equals(location2)) {
                    a++;
                } else {
                    this.photoProgressViews[a].setProgress(1.0f, a == 0 || (a == 1 && this.sideImage == this.rightImage) || (a == 2 && this.sideImage == this.leftImage));
                    checkProgress(a, false, true);
                }
            }
        } else if (id == NotificationCenter.fileLoaded) {
            String location3 = (String) args[0];
            int a2 = 0;
            while (true) {
                if (a2 >= 3) {
                    break;
                }
                String[] strArr2 = this.currentFileNames;
                if (strArr2[a2] == null || !strArr2[a2].equals(location3)) {
                    a2++;
                } else {
                    boolean animated = a2 == 0 || (a2 == 1 && this.sideImage == this.rightImage) || (a2 == 2 && this.sideImage == this.leftImage);
                    this.photoProgressViews[a2].setProgress(1.0f, animated);
                    checkProgress(a2, false, animated);
                    if (this.videoPlayer == null && a2 == 0 && (((messageObject = this.currentMessageObject) != null && messageObject.isVideo()) || (((botInlineResult = this.currentBotInlineResult) != null && (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) || ((pageBlocksAdapter = this.pageBlocksAdapter) != null && pageBlocksAdapter.isVideo(this.currentIndex))))) {
                        onActionClick(false);
                    }
                    if (a2 == 0 && this.videoPlayer != null) {
                        this.currentVideoFinishedLoading = true;
                    }
                }
            }
        } else {
            long j = 0;
            if (id == NotificationCenter.fileLoadProgressChanged) {
                String location4 = (String) args[0];
                int a3 = 0;
                while (a3 < i3) {
                    String[] strArr3 = this.currentFileNames;
                    if (strArr3[a3] != null && strArr3[a3].equals(location4)) {
                        Long loadedSize = (Long) args[i5];
                        Long totalSize = (Long) args[i4];
                        float loadProgress = Math.min(f, ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
                        this.photoProgressViews[a3].setProgress(loadProgress, a3 == 0 || (a3 == i5 && this.sideImage == this.rightImage) || (a3 == i4 && this.sideImage == this.leftImage));
                        if (a3 == 0 && this.videoPlayer != null && this.videoPlayerSeekbar != null) {
                            if (this.currentVideoFinishedLoading) {
                                bufferedProgress = 1.0f;
                            } else {
                                long newTime = SystemClock.elapsedRealtime();
                                if (Math.abs(newTime - this.lastBufferedPositionCheck) >= 500) {
                                    if (this.seekToProgressPending != 0.0f) {
                                        progress = this.seekToProgressPending;
                                    } else {
                                        long duration = this.videoPlayer.getDuration();
                                        long position = this.videoPlayer.getCurrentPosition();
                                        if (duration >= j && duration != C.TIME_UNSET && position >= j) {
                                            progress = ((float) position) / ((float) duration);
                                        } else {
                                            progress = 0.0f;
                                        }
                                    }
                                    float bufferedProgress2 = this.isStreaming ? FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(progress, this.currentFileNames[0]) : 1.0f;
                                    this.lastBufferedPositionCheck = newTime;
                                    bufferedProgress = bufferedProgress2;
                                } else {
                                    bufferedProgress = -1.0f;
                                }
                            }
                            if (bufferedProgress != -1.0f) {
                                this.videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                                PipVideoOverlay.setBufferedProgress(bufferedProgress);
                                this.videoPlayerSeekbarView.invalidate();
                            }
                            checkBufferedProgress(loadProgress);
                        }
                    }
                    a3++;
                    f = 1.0f;
                    i3 = 3;
                    i4 = 2;
                    i5 = 1;
                    j = 0;
                }
                return;
            }
            int i7 = -1;
            if (id == NotificationCenter.dialogPhotosLoaded) {
                int guid2 = ((Integer) args[3]).intValue();
                long did2 = ((Long) args[0]).longValue();
                if (this.avatarsDialogId == did2 && this.classGuid == guid2) {
                    boolean fromCache = ((Boolean) args[2]).booleanValue();
                    int setToImage = -1;
                    ArrayList<TLRPC.Photo> photos = (ArrayList) args[4];
                    if (photos.isEmpty()) {
                        return;
                    }
                    ArrayList<TLRPC.Message> messages = (ArrayList) args[5];
                    this.imagesArrLocations.clear();
                    this.imagesArrLocationsSizes.clear();
                    this.imagesArrLocationsVideo.clear();
                    this.imagesArrMessages.clear();
                    this.avatarsArr.clear();
                    int a4 = 0;
                    while (a4 < photos.size()) {
                        TLRPC.Photo photo = photos.get(a4);
                        if (photo == null || (photo instanceof TLRPC.TL_photoEmpty)) {
                            guid = guid2;
                            did = did2;
                        } else if (photo.sizes == null) {
                            guid = guid2;
                            did = did2;
                        } else {
                            TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                            TLRPC.VideoSize videoSize = photo.video_sizes.isEmpty() ? null : photo.video_sizes.get(i6);
                            if (sizeFull == null) {
                                guid = guid2;
                                did = did2;
                            } else {
                                if (setToImage != i7 || this.currentFileLocation == null) {
                                    guid = guid2;
                                    did = did2;
                                } else {
                                    int b = 0;
                                    while (true) {
                                        if (b >= photo.sizes.size()) {
                                            guid = guid2;
                                            did = did2;
                                            break;
                                        }
                                        TLRPC.PhotoSize size = photo.sizes.get(b);
                                        if (size.location != null) {
                                            guid = guid2;
                                            if (size.location.local_id == this.currentFileLocation.location.local_id) {
                                                did = did2;
                                                if (size.location.volume_id == this.currentFileLocation.location.volume_id) {
                                                    setToImage = this.imagesArrLocations.size();
                                                    break;
                                                }
                                            } else {
                                                did = did2;
                                            }
                                        } else {
                                            guid = guid2;
                                            did = did2;
                                        }
                                        b++;
                                        guid2 = guid;
                                        did2 = did;
                                    }
                                }
                                if (photo.dc_id != 0) {
                                    sizeFull.location.dc_id = photo.dc_id;
                                    sizeFull.location.file_reference = photo.file_reference;
                                }
                                ImageLocation location5 = ImageLocation.getForPhoto(sizeFull, photo);
                                ImageLocation videoLocation = videoSize != null ? ImageLocation.getForPhoto(videoSize, photo) : location5;
                                if (location5 != null) {
                                    this.imagesArrLocations.add(location5);
                                    this.imagesArrLocationsSizes.add(Long.valueOf(videoLocation.currentSize));
                                    this.imagesArrLocationsVideo.add(videoLocation);
                                    if (messages != null) {
                                        this.imagesArrMessages.add(messages.get(a4));
                                    } else {
                                        this.imagesArrMessages.add(null);
                                    }
                                    this.avatarsArr.add(photo);
                                }
                            }
                        }
                        a4++;
                        guid2 = guid;
                        did2 = did;
                        i7 = -1;
                        i6 = 0;
                    }
                    if (!this.avatarsArr.isEmpty()) {
                        this.menuItem.showSubItem(6);
                    } else {
                        this.menuItem.hideSubItem(6);
                    }
                    this.needSearchImageInArr = false;
                    this.currentIndex = -1;
                    if (setToImage != -1) {
                        setImageIndex(setToImage);
                    } else {
                        TLRPC.User user = null;
                        TLRPC.Chat chat = null;
                        if (this.avatarsDialogId > 0) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.avatarsDialogId));
                        } else {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.avatarsDialogId));
                        }
                        if (user != null || chat != null) {
                            if (user != null) {
                                i2 = 0;
                                location = ImageLocation.getForUserOrChat(user, 0);
                            } else {
                                i2 = 0;
                                location = ImageLocation.getForUserOrChat(chat, 0);
                            }
                            if (location != null) {
                                if (!this.imagesArrLocations.isEmpty() && this.imagesArrLocations.get(i2).photoId == location.photoId) {
                                    this.imagesArrLocations.remove(i2);
                                    this.avatarsArr.remove(i2);
                                    this.imagesArrLocationsSizes.remove(i2);
                                    this.imagesArrLocationsVideo.remove(i2);
                                    this.imagesArrMessages.remove(i2);
                                }
                                this.imagesArrLocations.add(i2, location);
                                this.avatarsArr.add(i2, new TLRPC.TL_photoEmpty());
                                this.imagesArrLocationsSizes.add(i2, Long.valueOf(this.currentFileLocationVideo.currentSize));
                                this.imagesArrLocationsVideo.add(i2, this.currentFileLocationVideo);
                                this.imagesArrMessages.add(i2, null);
                                setImageIndex(i2);
                            }
                        }
                    }
                    if (fromCache) {
                        MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.avatarsDialogId, 80, 0, false, this.classGuid);
                    }
                }
            } else if (id == NotificationCenter.mediaCountDidLoad) {
                long uid = ((Long) args[0]).longValue();
                if (uid == this.currentDialogId || uid == this.mergeDialogId) {
                    MessageObject messageObject2 = this.currentMessageObject;
                    if (messageObject2 == null || MediaDataController.getMediaType(messageObject2.messageOwner) == this.sharedMediaType) {
                        if (uid == this.currentDialogId) {
                            this.totalImagesCount = ((Integer) args[1]).intValue();
                        } else {
                            this.totalImagesCountMerge = ((Integer) args[1]).intValue();
                        }
                        if (this.needSearchImageInArr && this.isFirstLoading) {
                            this.isFirstLoading = false;
                            this.loadingMoreImages = true;
                            MediaDataController.getInstance(this.currentAccount).loadMedia(this.currentDialogId, 20, 0, 0, this.sharedMediaType, 1, this.classGuid, 0);
                        } else if (!this.imagesArr.isEmpty()) {
                            if (this.opennedFromMedia) {
                                this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.startOffset + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                            } else {
                                this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                            }
                        }
                    }
                }
            } else if (id == NotificationCenter.mediaDidLoad) {
                long uid2 = ((Long) args[0]).longValue();
                int guid3 = ((Integer) args[3]).intValue();
                long j2 = this.currentDialogId;
                if ((uid2 == j2 || uid2 == this.mergeDialogId) && guid3 == this.classGuid) {
                    this.loadingMoreImages = false;
                    int loadIndex = uid2 == j2 ? 0 : 1;
                    ArrayList<MessageObject> arr = (ArrayList) args[2];
                    this.endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                    boolean fromStart = ((Boolean) args[6]).booleanValue();
                    if (this.needSearchImageInArr) {
                        if (!arr.isEmpty() || (loadIndex == 0 && this.mergeDialogId != 0)) {
                            int i8 = this.currentIndex;
                            if (i8 < 0) {
                                this.needSearchImageInArr = false;
                                return;
                            } else if (i8 < this.imagesArr.size()) {
                                int foundIndex = -1;
                                MessageObject currentMessage = this.imagesArr.get(this.currentIndex);
                                int added = 0;
                                for (int a5 = 0; a5 < arr.size(); a5++) {
                                    MessageObject message = arr.get(a5);
                                    if (this.imagesByIdsTemp[loadIndex].indexOfKey(message.getId()) < 0) {
                                        this.imagesByIdsTemp[loadIndex].put(message.getId(), message);
                                        if (this.opennedFromMedia) {
                                            this.imagesArrTemp.add(message);
                                            if (message.getId() == currentMessage.getId()) {
                                                foundIndex = added;
                                            }
                                            added++;
                                        } else {
                                            added++;
                                            this.imagesArrTemp.add(0, message);
                                            if (message.getId() == currentMessage.getId()) {
                                                foundIndex = arr.size() - added;
                                            }
                                        }
                                    }
                                }
                                if (added == 0 && (loadIndex != 0 || this.mergeDialogId == 0)) {
                                    this.totalImagesCount = this.imagesArr.size();
                                    this.totalImagesCountMerge = 0;
                                }
                                if (foundIndex != -1) {
                                    this.imagesArr.clear();
                                    this.imagesArr.addAll(this.imagesArrTemp);
                                    for (int a6 = 0; a6 < 2; a6++) {
                                        this.imagesByIds[a6] = this.imagesByIdsTemp[a6].clone();
                                        this.imagesByIdsTemp[a6].clear();
                                    }
                                    this.imagesArrTemp.clear();
                                    this.needSearchImageInArr = false;
                                    this.currentIndex = -1;
                                    if (foundIndex >= this.imagesArr.size()) {
                                        foundIndex = this.imagesArr.size() - 1;
                                    }
                                    setImageIndex(foundIndex);
                                    return;
                                }
                                if (!this.opennedFromMedia) {
                                    loadFromMaxId = this.imagesArrTemp.isEmpty() ? 0 : this.imagesArrTemp.get(0).getId();
                                    if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                                        if (!this.imagesArrTemp.isEmpty() && this.imagesArrTemp.get(0).getDialogId() != this.mergeDialogId) {
                                            loadFromMaxId = 0;
                                            loadIndex = 1;
                                        } else {
                                            loadIndex = 1;
                                        }
                                    }
                                } else {
                                    if (this.imagesArrTemp.isEmpty()) {
                                        i = 0;
                                    } else {
                                        ArrayList<MessageObject> arrayList = this.imagesArrTemp;
                                        i = arrayList.get(arrayList.size() - 1).getId();
                                    }
                                    loadFromMaxId = i;
                                    if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                                        loadIndex = 1;
                                        if (!this.imagesArrTemp.isEmpty()) {
                                            ArrayList<MessageObject> arrayList2 = this.imagesArrTemp;
                                            if (arrayList2.get(arrayList2.size() - 1).getDialogId() != this.mergeDialogId) {
                                                loadFromMaxId = 0;
                                            }
                                        }
                                    }
                                }
                                if (!this.endReached[loadIndex]) {
                                    this.loadingMoreImages = true;
                                    MediaDataController.getInstance(this.currentAccount).loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 40, loadFromMaxId, 0, this.sharedMediaType, 1, this.classGuid, 0);
                                    return;
                                }
                                return;
                            }
                        }
                        this.needSearchImageInArr = false;
                        return;
                    }
                    int added2 = 0;
                    for (int i9 = 0; i9 < arr.size(); i9++) {
                        MessageObject message2 = arr.get(fromStart ? (arr.size() - 1) - i9 : i9);
                        if (this.imagesByIds[loadIndex].indexOfKey(message2.getId()) < 0) {
                            added2++;
                            if (this.opennedFromMedia) {
                                if (!fromStart) {
                                    this.imagesArr.add(message2);
                                } else {
                                    this.imagesArr.add(0, message2);
                                    int i10 = this.startOffset - 1;
                                    this.startOffset = i10;
                                    this.currentIndex++;
                                    if (i10 < 0) {
                                        this.startOffset = 0;
                                    }
                                }
                            } else {
                                this.imagesArr.add(0, message2);
                            }
                            this.imagesByIds[loadIndex].put(message2.getId(), message2);
                        }
                    }
                    if (this.opennedFromMedia) {
                        if (added2 == 0 && !fromStart) {
                            this.totalImagesCount = this.startOffset + this.imagesArr.size();
                            this.totalImagesCountMerge = 0;
                        }
                    } else if (added2 == 0) {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = 0;
                    } else {
                        int index = this.currentIndex;
                        this.currentIndex = -1;
                        setImageIndex(index + added2);
                    }
                }
            } else if (id == NotificationCenter.emojiLoaded) {
                CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
                if (captionTextViewSwitcher != null) {
                    captionTextViewSwitcher.invalidateViews();
                }
            } else if (id == NotificationCenter.filePreparingFailed) {
                MessageObject messageObject3 = (MessageObject) args[0];
                if (this.loadInitialVideo) {
                    this.loadInitialVideo = false;
                    this.progressView.setVisibility(4);
                    preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                } else if (this.tryStartRequestPreviewOnFinish) {
                    releasePlayer(false);
                    this.tryStartRequestPreviewOnFinish = !MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true);
                } else if (messageObject3 == this.videoPreviewMessageObject) {
                    this.requestingPreview = false;
                    this.progressView.setVisibility(4);
                }
            } else if (id == NotificationCenter.fileNewChunkAvailable) {
                MessageObject messageObject4 = (MessageObject) args[0];
                if (messageObject4 == this.videoPreviewMessageObject) {
                    String finalPath = (String) args[1];
                    long finalSize = ((Long) args[3]).longValue();
                    float progress2 = ((Float) args[4]).floatValue();
                    this.photoProgressViews[0].setProgress(progress2, true);
                    if (finalSize != 0) {
                        this.requestingPreview = false;
                        this.photoProgressViews[0].setProgress(1.0f, true);
                        this.photoProgressViews[0].setBackgroundState(3, true, true);
                        preparePlayer(Uri.fromFile(new File(finalPath)), false, true, this.editState.savedFilterState);
                    }
                }
            } else if (id == NotificationCenter.messagesDeleted && !(scheduled = ((Boolean) args[2]).booleanValue())) {
                long channelId = ((Long) args[1]).longValue();
                ArrayList<Integer> markAsDeletedMessages = (ArrayList) args[0];
                boolean reset = false;
                boolean resetCurrent = false;
                int x = 0;
                while (x < 2) {
                    ArrayList<MessageObject> arr2 = x == 0 ? this.imagesArr : this.imagesArrTemp;
                    SparseArray<MessageObject>[] ids = x == 0 ? this.imagesByIds : this.imagesByIdsTemp;
                    if (!arr2.isEmpty()) {
                        int b2 = 0;
                        while (b2 < 2) {
                            if (ids[b2].size() > 0) {
                                MessageObject messageObject5 = ids[b2].valueAt(0);
                                if (messageObject5.messageOwner.peer_id.channel_id == channelId) {
                                    int a7 = 0;
                                    int N = markAsDeletedMessages.size();
                                    while (a7 < N) {
                                        int mid = markAsDeletedMessages.get(a7).intValue();
                                        boolean scheduled2 = scheduled;
                                        MessageObject message3 = ids[b2].get(markAsDeletedMessages.get(a7).intValue());
                                        if (message3 != null) {
                                            ids[b2].remove(mid);
                                            arr2.remove(message3);
                                            if (b2 == 0) {
                                                this.totalImagesCount--;
                                            } else {
                                                this.totalImagesCountMerge--;
                                            }
                                            if (message3 == this.currentMessageObject) {
                                                resetCurrent = true;
                                            }
                                            reset = true;
                                        }
                                        a7++;
                                        scheduled = scheduled2;
                                    }
                                }
                            }
                            b2++;
                            scheduled = scheduled;
                        }
                    }
                    x++;
                    scheduled = scheduled;
                }
                if (reset) {
                    if (resetCurrent && this == PipInstance) {
                        destroyPhotoViewer();
                    } else if (this.imagesArr.isEmpty()) {
                        closePhoto(false, true);
                    } else {
                        int index2 = this.currentIndex;
                        this.currentIndex = -1;
                        if (index2 >= this.imagesArr.size()) {
                            index2 = this.imagesArr.size() - 1;
                        }
                        setImageIndex(index2);
                    }
                }
            }
        }
    }

    public void showDownloadAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity, this.resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        MessageObject messageObject = this.currentMessageObject;
        boolean z = false;
        if (messageObject != null && messageObject.isVideo() && FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            z = true;
        }
        boolean alreadyDownloading = z;
        if (alreadyDownloading) {
            builder.setMessage(LocaleController.getString("PleaseStreamDownload", R.string.PleaseStreamDownload));
        } else {
            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
        }
        showAlertDialog(builder);
    }

    public void onSharePressed() {
        boolean z;
        if (this.parentActivity == null || !this.allowShare) {
            return;
        }
        File f = null;
        boolean isVideo = false;
        try {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                isVideo = messageObject.isVideo();
                if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    f = new File(this.currentMessageObject.messageOwner.attachPath);
                    if (!f.exists()) {
                        f = null;
                    }
                }
                if (f == null) {
                    f = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
                }
            } else if (this.currentFileLocationVideo != null) {
                FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
                TLRPC.FileLocation fileLocation = getFileLocation(this.currentFileLocationVideo);
                String fileLocationExt = getFileLocationExt(this.currentFileLocationVideo);
                if (this.avatarsDialogId == 0 && !this.isEvent) {
                    z = false;
                    f = fileLoader.getPathToAttach(fileLocation, fileLocationExt, z);
                }
                z = true;
                f = fileLoader.getPathToAttach(fileLocation, fileLocationExt, z);
            } else {
                PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                if (pageBlocksAdapter != null) {
                    f = pageBlocksAdapter.getFile(this.currentIndex);
                }
            }
            if (f != null && f.exists()) {
                Intent intent = new Intent("android.intent.action.SEND");
                if (isVideo) {
                    intent.setType(MimeTypes.VIDEO_MP4);
                } else {
                    MessageObject messageObject2 = this.currentMessageObject;
                    if (messageObject2 != null) {
                        intent.setType(messageObject2.getMimeType());
                    } else {
                        intent.setType("image/jpeg");
                    }
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", f));
                        intent.setFlags(1);
                    } catch (Exception e) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                }
                this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                return;
            }
            showDownloadAlert();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void setScaleToFill() {
        float bitmapWidth = this.centerImage.getBitmapWidth();
        float bitmapHeight = this.centerImage.getBitmapHeight();
        if (bitmapWidth == 0.0f || bitmapHeight == 0.0f) {
            return;
        }
        float containerWidth = getContainerViewWidth();
        float containerHeight = getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        float width = (int) (bitmapWidth * scaleFit);
        float height = (int) (bitmapHeight * scaleFit);
        float max = Math.max(containerWidth / width, containerHeight / height);
        this.scale = max;
        updateMinMax(max);
    }

    public void setParentAlert(ChatAttachAlert alert) {
        this.parentAlert = alert;
    }

    public void setParentActivity(Activity activity) {
        setParentActivity(activity, null);
    }

    public void setParentActivity(final Activity activity, final Theme.ResourcesProvider resourcesProvider) {
        Theme.createChatResources(activity, false);
        this.resourcesProvider = resourcesProvider;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.currentAccount = UserConfig.selectedAccount;
        }
        if (this.parentActivity != activity && activity != null) {
            this.inBubbleMode = activity instanceof BubbleActivity;
            this.parentActivity = activity;
            this.activityContext = new ContextThemeWrapper(this.parentActivity, (int) R.style.Theme_TMessages);
            this.touchSlop = ViewConfiguration.get(this.parentActivity).getScaledTouchSlop();
            if (progressDrawables == null) {
                Drawable circleDrawable = ContextCompat.getDrawable(this.parentActivity, R.drawable.circle_big);
                progressDrawables = new Drawable[]{circleDrawable, ContextCompat.getDrawable(this.parentActivity, R.drawable.cancel_big), ContextCompat.getDrawable(this.parentActivity, R.drawable.load_big)};
            }
            this.scroller = new Scroller(activity);
            AnonymousClass10 anonymousClass10 = new AnonymousClass10(activity);
            this.windowView = anonymousClass10;
            anonymousClass10.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setFocusable(false);
            ClippingImageView clippingImageView = new ClippingImageView(activity);
            this.animatingImageView = clippingImageView;
            clippingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(activity);
            this.containerView = frameLayoutDrawer;
            frameLayoutDrawer.setFocusable(false);
            this.containerView.setClipChildren(true);
            this.containerView.setClipToPadding(true);
            this.windowView.setClipChildren(false);
            this.windowView.setClipToPadding(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (Build.VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnApplyWindowInsetsListener
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        return PhotoViewer.this.m4246lambda$setParentActivity$4$orgtelegramuiPhotoViewer(view, windowInsets);
                    }
                });
                this.containerView.setSystemUiVisibility(1792);
            }
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 28) {
                this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147286784;
            } else {
                this.windowLayoutParams.flags = 131072;
            }
            PaintingOverlay paintingOverlay = new PaintingOverlay(this.parentActivity);
            this.paintingOverlay = paintingOverlay;
            this.containerView.addView(paintingOverlay, LayoutHelper.createFrame(-2, -2.0f));
            PaintingOverlay paintingOverlay2 = new PaintingOverlay(this.parentActivity);
            this.leftPaintingOverlay = paintingOverlay2;
            this.containerView.addView(paintingOverlay2, LayoutHelper.createFrame(-2, -2.0f));
            PaintingOverlay paintingOverlay3 = new PaintingOverlay(this.parentActivity);
            this.rightPaintingOverlay = paintingOverlay3;
            this.containerView.addView(paintingOverlay3, LayoutHelper.createFrame(-2, -2.0f));
            ActionBar actionBar = new ActionBar(activity) { // from class: org.telegram.ui.PhotoViewer.11
                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    PhotoViewer.this.containerView.invalidate();
                }
            };
            this.actionBar = actionBar;
            actionBar.setOverlayTitleAnimation(true);
            this.actionBar.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(isStatusBarVisible());
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass12(resourcesProvider));
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem addItem = menu.addItem(13, R.drawable.msg_mask);
            this.masksItem = addItem;
            addItem.setContentDescription(LocaleController.getString("Masks", R.string.Masks));
            ActionBarMenuItem addItem2 = menu.addItem(5, R.drawable.ic_goinline);
            this.pipItem = addItem2;
            addItem2.setContentDescription(LocaleController.getString("AccDescrPipMode", R.string.AccDescrPipMode));
            ActionBarMenuItem addItem3 = menu.addItem(3, R.drawable.msg_forward);
            this.sendItem = addItem3;
            addItem3.setContentDescription(LocaleController.getString("Forward", R.string.Forward));
            ActionBarMenuItem addItem4 = menu.addItem(18, R.drawable.share);
            this.shareItem = addItem4;
            addItem4.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
            ActionBarMenuItem addItem5 = menu.addItem(0, R.drawable.ic_ab_other);
            this.menuItem = addItem5;
            addItem5.getPopupLayout().swipeBackGravityRight = true;
            this.chooseSpeedLayout = new ChooseSpeedLayout(this.activityContext, this.menuItem.getPopupLayout().getSwipeBack(), new ChooseSpeedLayout.Callback() { // from class: org.telegram.ui.PhotoViewer.13
                @Override // org.telegram.ui.ChooseSpeedLayout.Callback
                public void onSpeedSelected(float speed) {
                    PhotoViewer.this.menuItem.toggleSubMenu();
                    if (speed != PhotoViewer.this.currentVideoSpeed) {
                        PhotoViewer.this.currentVideoSpeed = speed;
                        if (PhotoViewer.this.currentMessageObject != null) {
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("playback_speed", 0);
                            if (Math.abs(PhotoViewer.this.currentVideoSpeed - 1.0f) < 0.001f) {
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.remove("speed" + PhotoViewer.this.currentMessageObject.getDialogId() + "_" + PhotoViewer.this.currentMessageObject.getId()).commit();
                            } else {
                                SharedPreferences.Editor edit2 = preferences.edit();
                                edit2.putFloat("speed" + PhotoViewer.this.currentMessageObject.getDialogId() + "_" + PhotoViewer.this.currentMessageObject.getId(), PhotoViewer.this.currentVideoSpeed).commit();
                            }
                        }
                        if (PhotoViewer.this.videoPlayer != null) {
                            PhotoViewer.this.videoPlayer.setPlaybackSpeed(PhotoViewer.this.currentVideoSpeed);
                        }
                        if (PhotoViewer.this.photoViewerWebView != null) {
                            PhotoViewer.this.photoViewerWebView.setPlaybackSpeed(PhotoViewer.this.currentVideoSpeed);
                        }
                        PhotoViewer.this.setMenuItemIcon();
                    }
                }
            });
            this.speedItem = this.menuItem.addSwipeBackItem(R.drawable.msg_speed, null, LocaleController.getString("Speed", R.string.Speed), this.chooseSpeedLayout.speedSwipeBackLayout);
            this.menuItem.getPopupLayout().setSwipeBackForegroundColor(-14540254);
            this.speedItem.setSubtext(LocaleController.getString("SpeedNormal", R.string.SpeedNormal));
            this.speedItem.setColors(-328966, -328966);
            ActionBarPopupWindow.GapView addColoredGap = this.menuItem.addColoredGap();
            this.speedGap = addColoredGap;
            addColoredGap.setColor(-15198184);
            this.menuItem.getPopupLayout().setFitItems(true);
            this.menuItem.addSubItem(11, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp)).setColors(-328966, -328966);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            ActionBarMenuSubItem addSubItem = this.menuItem.addSubItem(2, R.drawable.msg_media, LocaleController.getString("ShowAllMedia", R.string.ShowAllMedia));
            this.allMediaItem = addSubItem;
            addSubItem.setColors(-328966, -328966);
            this.menuItem.addSubItem(14, R.drawable.msg_gif, LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs)).setColors(-328966, -328966);
            this.menuItem.addSubItem(4, R.drawable.msg_message, LocaleController.getString("ShowInChat", R.string.ShowInChat)).setColors(-328966, -328966);
            this.menuItem.addSubItem(15, R.drawable.msg_sticker, LocaleController.getString("ShowStickers", R.string.ShowStickers)).setColors(-328966, -328966);
            this.menuItem.addSubItem(10, R.drawable.msg_shareout, LocaleController.getString("ShareFile", R.string.ShareFile)).setColors(-328966, -328966);
            this.menuItem.addSubItem(1, R.drawable.msg_gallery, LocaleController.getString("SaveToGallery", R.string.SaveToGallery)).setColors(-328966, -328966);
            this.menuItem.addSubItem(16, R.drawable.msg_openprofile, LocaleController.getString("SetAsMain", R.string.SetAsMain)).setColors(-328966, -328966);
            this.menuItem.addSubItem(6, R.drawable.msg_delete, LocaleController.getString("Delete", R.string.Delete)).setColors(-328966, -328966);
            this.menuItem.addSubItem(7, R.drawable.msg_cancel, LocaleController.getString("StopDownload", R.string.StopDownload)).setColors(-328966, -328966);
            this.menuItem.redrawPopup(-115203550);
            setMenuItemIcon();
            this.menuItem.setPopupItemsSelectorColor(268435455);
            this.menuItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() { // from class: org.telegram.ui.PhotoViewer.14
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarSubMenuItemDelegate
                public void onShowSubMenu() {
                    if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying) {
                        AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.hideActionBarRunnable);
                    }
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarSubMenuItemDelegate
                public void onHideSubMenu() {
                    if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying) {
                        PhotoViewer.this.scheduleActionBarHide();
                    }
                }
            });
            FrameLayout frameLayout = new FrameLayout(this.activityContext) { // from class: org.telegram.ui.PhotoViewer.15
                @Override // android.view.ViewGroup
                protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
                    if (child == PhotoViewer.this.nameTextView || child == PhotoViewer.this.dateTextView) {
                        widthUsed = PhotoViewer.this.bottomButtonsLayout.getMeasuredWidth();
                    }
                    super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
                }
            };
            this.bottomLayout = frameLayout;
            frameLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            View view = new View(this.activityContext);
            this.navigationBar = view;
            view.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.windowView.addView(this.navigationBar, LayoutHelper.createFrame(-1.0f, this.navigationBarHeight / AndroidUtilities.density, 87));
            this.pressedDrawable[0] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{838860800, 0});
            this.pressedDrawable[0].setShape(0);
            this.pressedDrawable[1] = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{838860800, 0});
            this.pressedDrawable[1].setShape(0);
            GroupedPhotosListView groupedPhotosListView = new GroupedPhotosListView(this.activityContext, AndroidUtilities.dp(10.0f));
            this.groupedPhotosListView = groupedPhotosListView;
            this.containerView.addView(groupedPhotosListView, LayoutHelper.createFrame(-1, 68, 83));
            this.groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() { // from class: org.telegram.ui.PhotoViewer.16
                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public int getCurrentIndex() {
                    return PhotoViewer.this.currentIndex;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public int getCurrentAccount() {
                    return PhotoViewer.this.currentAccount;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public long getAvatarsDialogId() {
                    return PhotoViewer.this.avatarsDialogId;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public int getSlideshowMessageId() {
                    return PhotoViewer.this.slideshowMessageId;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public ArrayList<ImageLocation> getImagesArrLocations() {
                    return PhotoViewer.this.imagesArrLocations;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public ArrayList<MessageObject> getImagesArr() {
                    return PhotoViewer.this.imagesArr;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public List<TLRPC.PageBlock> getPageBlockArr() {
                    if (PhotoViewer.this.pageBlocksAdapter != null) {
                        return PhotoViewer.this.pageBlocksAdapter.getAll();
                    }
                    return null;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public Object getParentObject() {
                    if (PhotoViewer.this.pageBlocksAdapter != null) {
                        return PhotoViewer.this.pageBlocksAdapter.getParentObject();
                    }
                    return null;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public void setCurrentIndex(int index) {
                    PhotoViewer.this.currentIndex = -1;
                    if (PhotoViewer.this.currentThumb != null) {
                        PhotoViewer.this.currentThumb.release();
                        PhotoViewer.this.currentThumb = null;
                    }
                    PhotoViewer.this.dontAutoPlay = true;
                    PhotoViewer.this.setImageIndex(index);
                    PhotoViewer.this.dontAutoPlay = false;
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public void onShowAnimationStart() {
                    PhotoViewer.this.containerView.requestLayout();
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public void onStopScrolling() {
                    PhotoViewer photoViewer = PhotoViewer.this;
                    if (photoViewer.shouldMessageObjectAutoPlayed(photoViewer.currentMessageObject)) {
                        PhotoViewer.this.playerAutoStarted = true;
                        PhotoViewer.this.onActionClick(true);
                        PhotoViewer.this.checkProgress(0, false, true);
                    }
                }

                @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
                public boolean validGroupId(long groupId) {
                    if (PhotoViewer.this.placeProvider != null) {
                        return PhotoViewer.this.placeProvider.validateGroupId(groupId);
                    }
                    return true;
                }
            });
            for (int a = 0; a < 3; a++) {
                this.fullscreenButton[a] = new ImageView(this.parentActivity);
                this.fullscreenButton[a].setImageResource(R.drawable.msg_maxvideo);
                this.fullscreenButton[a].setContentDescription(LocaleController.getString("AccSwitchToFullscreen", R.string.AccSwitchToFullscreen));
                this.fullscreenButton[a].setScaleType(ImageView.ScaleType.CENTER);
                this.fullscreenButton[a].setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
                this.fullscreenButton[a].setVisibility(4);
                this.fullscreenButton[a].setAlpha(1.0f);
                this.containerView.addView(this.fullscreenButton[a], LayoutHelper.createFrame(48, 48.0f));
                this.fullscreenButton[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda25
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        PhotoViewer.this.m4249lambda$setParentActivity$5$orgtelegramuiPhotoViewer(view2);
                    }
                });
            }
            CaptionTextViewSwitcher captionTextViewSwitcher = new CaptionTextViewSwitcher(this.containerView.getContext());
            this.captionTextViewSwitcher = captionTextViewSwitcher;
            captionTextViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda40
                @Override // android.widget.ViewSwitcher.ViewFactory
                public final View makeView() {
                    return PhotoViewer.this.m4250lambda$setParentActivity$6$orgtelegramuiPhotoViewer();
                }
            });
            this.captionTextViewSwitcher.setVisibility(4);
            setCaptionHwLayerEnabled(true);
            for (int a2 = 0; a2 < 3; a2++) {
                this.photoProgressViews[a2] = new PhotoProgressView(this.containerView) { // from class: org.telegram.ui.PhotoViewer.17
                    @Override // org.telegram.ui.PhotoViewer.PhotoProgressView
                    protected void onBackgroundStateUpdated(int state) {
                        if (this == PhotoViewer.this.photoProgressViews[0]) {
                            PhotoViewer.this.updateAccessibilityOverlayVisibility();
                        }
                    }

                    @Override // org.telegram.ui.PhotoViewer.PhotoProgressView
                    protected void onVisibilityChanged(boolean visible) {
                        if (this == PhotoViewer.this.photoProgressViews[0]) {
                            PhotoViewer.this.updateAccessibilityOverlayVisibility();
                        }
                    }
                };
                this.photoProgressViews[a2].setBackgroundState(0, false, true);
            }
            RadialProgressView radialProgressView = new RadialProgressView(this.activityContext, resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.18
                @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }

                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }
            };
            this.miniProgressView = radialProgressView;
            radialProgressView.setUseSelfAlpha(true);
            this.miniProgressView.setProgressColor(-1);
            this.miniProgressView.setSize(AndroidUtilities.dp(54.0f));
            this.miniProgressView.setBackgroundResource(R.drawable.circle_big);
            this.miniProgressView.setVisibility(4);
            this.miniProgressView.setAlpha(0.0f);
            this.containerView.addView(this.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
            LinearLayout linearLayout = new LinearLayout(this.containerView.getContext());
            this.bottomButtonsLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.bottomLayout.addView(this.bottomButtonsLayout, LayoutHelper.createFrame(-2, -1, 53));
            ImageView imageView = new ImageView(this.containerView.getContext());
            this.paintButton = imageView;
            imageView.setImageResource(R.drawable.msg_photo_draw);
            this.paintButton.setScaleType(ImageView.ScaleType.CENTER);
            this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.bottomButtonsLayout.addView(this.paintButton, LayoutHelper.createFrame(50, -1.0f));
            this.paintButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda26
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4251lambda$setParentActivity$7$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.paintButton.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", R.string.AccDescrPhotoEditor));
            ImageView imageView2 = new ImageView(this.containerView.getContext());
            this.shareButton = imageView2;
            imageView2.setImageResource(R.drawable.share);
            this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.bottomButtonsLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1.0f));
            this.shareButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda27
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4252lambda$setParentActivity$8$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.shareButton.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
            FadingTextViewLayout fadingTextViewLayout = new FadingTextViewLayout(this.containerView.getContext()) { // from class: org.telegram.ui.PhotoViewer.19
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.FadingTextViewLayout
                public void onTextViewCreated(TextView textView) {
                    super.onTextViewCreated(textView);
                    textView.setTextSize(1, 14.0f);
                    textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textView.setTextColor(-1);
                    textView.setGravity(3);
                }
            };
            this.nameTextView = fadingTextViewLayout;
            this.bottomLayout.addView(fadingTextViewLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, 8.0f, 0.0f));
            FadingTextViewLayout fadingTextViewLayout2 = new FadingTextViewLayout(this.containerView.getContext(), true) { // from class: org.telegram.ui.PhotoViewer.20
                private LocaleController.LocaleInfo lastLocaleInfo = null;
                private int staticCharsCount = 0;

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.FadingTextViewLayout
                public void onTextViewCreated(TextView textView) {
                    super.onTextViewCreated(textView);
                    textView.setTextSize(1, 13.0f);
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textView.setTextColor(-1);
                    textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    textView.setGravity(3);
                }

                @Override // org.telegram.ui.Components.FadingTextViewLayout
                protected int getStaticCharsCount() {
                    LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                    if (this.lastLocaleInfo != localeInfo) {
                        this.lastLocaleInfo = localeInfo;
                        this.staticCharsCount = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date()), LocaleController.getInstance().formatterDay.format(new Date())).length();
                    }
                    return this.staticCharsCount;
                }

                @Override // org.telegram.ui.Components.FadingTextViewLayout
                public void setText(CharSequence text, boolean animated) {
                    int staticCharsCount;
                    if (animated) {
                        boolean dontAnimateUnchangedStaticChars = true;
                        if (LocaleController.isRTL && (staticCharsCount = getStaticCharsCount()) > 0 && (text.length() != staticCharsCount || getText() == null || getText().length() != staticCharsCount)) {
                            dontAnimateUnchangedStaticChars = false;
                        }
                        setText(text, true, dontAnimateUnchangedStaticChars);
                        return;
                    }
                    setText(text, false, false);
                }
            };
            this.dateTextView = fadingTextViewLayout2;
            this.bottomLayout.addView(fadingTextViewLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 8.0f, 0.0f));
            createVideoControlsInterface();
            RadialProgressView radialProgressView2 = new RadialProgressView(this.parentActivity, resourcesProvider);
            this.progressView = radialProgressView2;
            radialProgressView2.setProgressColor(-1);
            this.progressView.setBackgroundResource(R.drawable.circle_big);
            this.progressView.setVisibility(4);
            this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54, 17));
            PickerBottomLayoutViewer pickerBottomLayoutViewer = new PickerBottomLayoutViewer(this.parentActivity);
            this.qualityPicker = pickerBottomLayoutViewer;
            pickerBottomLayoutViewer.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.qualityPicker.updateSelectedCount(0, false);
            this.qualityPicker.setTranslationY(AndroidUtilities.dp(120.0f));
            this.qualityPicker.doneButton.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
            this.qualityPicker.doneButton.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
            this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
            this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda28
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4253lambda$setParentActivity$9$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4220lambda$setParentActivity$10$orgtelegramuiPhotoViewer(view2);
                }
            });
            VideoForwardDrawable videoForwardDrawable = new VideoForwardDrawable(false);
            this.videoForwardDrawable = videoForwardDrawable;
            videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() { // from class: org.telegram.ui.PhotoViewer.21
                @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                public void onAnimationEnd() {
                }

                @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                public void invalidate() {
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            QualityChooseView qualityChooseView = new QualityChooseView(this.parentActivity);
            this.qualityChooseView = qualityChooseView;
            qualityChooseView.setTranslationY(AndroidUtilities.dp(120.0f));
            this.qualityChooseView.setVisibility(4);
            this.qualityChooseView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 70.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            FrameLayout frameLayout2 = new FrameLayout(this.activityContext) { // from class: org.telegram.ui.PhotoViewer.22
                @Override // android.view.ViewGroup, android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                }

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                        PhotoViewer.this.videoTimelineView.setTranslationY(translationY);
                        PhotoViewer.this.videoAvatarTooltip.setTranslationY(translationY);
                    }
                    if (PhotoViewer.this.videoAvatarTooltip != null && PhotoViewer.this.videoAvatarTooltip.getVisibility() != 8) {
                        PhotoViewer.this.videoAvatarTooltip.setTranslationY(translationY);
                    }
                }

                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                        PhotoViewer.this.videoTimelineView.setAlpha(alpha);
                    }
                }

                @Override // android.view.View
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                        PhotoViewer.this.videoTimelineView.setVisibility(visibility == 0 ? 0 : 4);
                    }
                }

                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    if (PhotoViewer.this.itemsLayout.getVisibility() != 8) {
                        int x = (((right - left) - AndroidUtilities.dp(70.0f)) - PhotoViewer.this.itemsLayout.getMeasuredWidth()) / 2;
                        PhotoViewer.this.itemsLayout.layout(x, PhotoViewer.this.itemsLayout.getTop(), PhotoViewer.this.itemsLayout.getMeasuredWidth() + x, PhotoViewer.this.itemsLayout.getTop() + PhotoViewer.this.itemsLayout.getMeasuredHeight());
                    }
                }
            };
            this.pickerView = frameLayout2;
            frameLayout2.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, -2, 83));
            TextView textView = new TextView(this.containerView.getContext());
            this.docNameTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.docNameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.docNameTextView.setSingleLine(true);
            this.docNameTextView.setMaxLines(1);
            this.docNameTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.docNameTextView.setTextColor(-1);
            this.docNameTextView.setGravity(3);
            this.pickerView.addView(this.docNameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 23.0f, 84.0f, 0.0f));
            TextView textView2 = new TextView(this.containerView.getContext());
            this.docInfoTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.docInfoTextView.setSingleLine(true);
            this.docInfoTextView.setMaxLines(1);
            this.docInfoTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.docInfoTextView.setTextColor(-1);
            this.docInfoTextView.setGravity(3);
            this.pickerView.addView(this.docInfoTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 46.0f, 84.0f, 0.0f));
            VideoTimelinePlayView videoTimelinePlayView = new VideoTimelinePlayView(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.23
                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    if (getTranslationY() != translationY) {
                        super.setTranslationY(translationY);
                        PhotoViewer.this.containerView.invalidate();
                    }
                }
            };
            this.videoTimelineView = videoTimelinePlayView;
            videoTimelinePlayView.setDelegate(new AnonymousClass24());
            showVideoTimeline(false, false);
            this.videoTimelineView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 58.0f, 83, 0.0f, 8.0f, 0.0f, 0.0f));
            TextView textView3 = new TextView(this.parentActivity);
            this.videoAvatarTooltip = textView3;
            textView3.setSingleLine(true);
            this.videoAvatarTooltip.setVisibility(8);
            this.videoAvatarTooltip.setText(LocaleController.getString("ChooseCover", R.string.ChooseCover));
            this.videoAvatarTooltip.setGravity(1);
            this.videoAvatarTooltip.setTextSize(1, 14.0f);
            this.videoAvatarTooltip.setTextColor(-7566196);
            this.containerView.addView(this.videoAvatarTooltip, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 8.0f, 0.0f, 0.0f));
            ImageView imageView3 = new ImageView(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.25
                @Override // android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }

                @Override // android.widget.ImageView, android.view.View
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (PhotoViewer.this.captionEditText.getCaptionLimitOffset() < 0) {
                        PhotoViewer.this.captionLimitView.setVisibility(visibility);
                    } else {
                        PhotoViewer.this.captionLimitView.setVisibility(8);
                    }
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    PhotoViewer.this.captionLimitView.setTranslationY(translationY);
                }

                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    PhotoViewer.this.captionLimitView.setAlpha(alpha);
                }
            };
            this.pickerViewSendButton = imageView3;
            imageView3.setScaleType(ImageView.ScaleType.CENTER);
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_dialogFloatingButton), getThemedColor(Build.VERSION.SDK_INT >= 21 ? Theme.key_dialogFloatingButtonPressed : Theme.key_dialogFloatingButton));
            this.pickerViewSendDrawable = createSimpleSelectorCircleDrawable;
            this.pickerViewSendButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
            this.containerView.addView(this.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
            this.pickerViewSendButton.setContentDescription(LocaleController.getString("Send", R.string.Send));
            this.pickerViewSendButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4221lambda$setParentActivity$11$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.pickerViewSendButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda36
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view2) {
                    return PhotoViewer.this.m4225lambda$setParentActivity$15$orgtelegramuiPhotoViewer(resourcesProvider, view2);
                }
            });
            TextView textView4 = new TextView(this.parentActivity);
            this.captionLimitView = textView4;
            textView4.setTextSize(1, 15.0f);
            this.captionLimitView.setTextColor(-1280137);
            this.captionLimitView.setGravity(17);
            this.captionLimitView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.containerView.addView(this.captionLimitView, LayoutHelper.createFrame(56, 20.0f, 85, 3.0f, 0.0f, 14.0f, 78.0f));
            LinearLayout linearLayout2 = new LinearLayout(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.26
                boolean ignoreLayout;

                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int compressIconWidth;
                    int visibleItemsCount = 0;
                    int count = getChildCount();
                    for (int a3 = 0; a3 < count; a3++) {
                        if (getChildAt(a3).getVisibility() == 0) {
                            visibleItemsCount++;
                        }
                    }
                    int width = View.MeasureSpec.getSize(widthMeasureSpec);
                    int height = View.MeasureSpec.getSize(heightMeasureSpec);
                    if (visibleItemsCount != 0) {
                        int itemWidth = Math.min(AndroidUtilities.dp(70.0f), width / visibleItemsCount);
                        if (PhotoViewer.this.compressItem.getVisibility() == 0) {
                            this.ignoreLayout = true;
                            if (PhotoViewer.this.selectedCompression < 2) {
                                compressIconWidth = 48;
                            } else {
                                compressIconWidth = 64;
                            }
                            int padding = Math.max(0, (itemWidth - AndroidUtilities.dp(compressIconWidth)) / 2);
                            PhotoViewer.this.compressItem.setPadding(padding, 0, padding, 0);
                            this.ignoreLayout = false;
                        }
                        for (int a4 = 0; a4 < count; a4++) {
                            View v = getChildAt(a4);
                            if (v.getVisibility() != 8) {
                                v.measure(View.MeasureSpec.makeMeasureSpec(itemWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                            }
                        }
                        int a5 = itemWidth * visibleItemsCount;
                        setMeasuredDimension(a5, height);
                        return;
                    }
                    setMeasuredDimension(width, height);
                }
            };
            this.itemsLayout = linearLayout2;
            linearLayout2.setOrientation(0);
            this.pickerView.addView(this.itemsLayout, LayoutHelper.createFrame(-2, 48.0f, 81, 0.0f, 0.0f, 70.0f, 0.0f));
            ImageView imageView4 = new ImageView(this.parentActivity);
            this.cropItem = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.cropItem.setImageResource(R.drawable.msg_photo_crop);
            this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.itemsLayout.addView(this.cropItem, LayoutHelper.createLinear(48, 48));
            this.cropItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4226lambda$setParentActivity$16$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.cropItem.setContentDescription(LocaleController.getString("CropImage", R.string.CropImage));
            ImageView imageView5 = new ImageView(this.parentActivity);
            this.rotateItem = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.rotateItem.setImageResource(R.drawable.msg_photo_rotate);
            this.rotateItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.itemsLayout.addView(this.rotateItem, LayoutHelper.createLinear(48, 48));
            this.rotateItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4227lambda$setParentActivity$17$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.rotateItem.setContentDescription(LocaleController.getString("AccDescrRotate", R.string.AccDescrRotate));
            ImageView imageView6 = new ImageView(this.parentActivity);
            this.mirrorItem = imageView6;
            imageView6.setScaleType(ImageView.ScaleType.CENTER);
            this.mirrorItem.setImageResource(R.drawable.msg_photo_flip);
            this.mirrorItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.itemsLayout.addView(this.mirrorItem, LayoutHelper.createLinear(48, 48));
            this.mirrorItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda14
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4228lambda$setParentActivity$18$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.mirrorItem.setContentDescription(LocaleController.getString("AccDescrMirror", R.string.AccDescrMirror));
            ImageView imageView7 = new ImageView(this.parentActivity);
            this.paintItem = imageView7;
            imageView7.setScaleType(ImageView.ScaleType.CENTER);
            this.paintItem.setImageResource(R.drawable.msg_photo_draw);
            this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.itemsLayout.addView(this.paintItem, LayoutHelper.createLinear(48, 48));
            this.paintItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda15
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4229lambda$setParentActivity$19$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.paintItem.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", R.string.AccDescrPhotoEditor));
            ImageView imageView8 = new ImageView(this.parentActivity);
            this.muteItem = imageView8;
            imageView8.setScaleType(ImageView.ScaleType.CENTER);
            this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.containerView.addView(this.muteItem, LayoutHelper.createFrame(48, 48.0f, 83, 16.0f, 0.0f, 0.0f, 0.0f));
            this.muteItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda16
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4230lambda$setParentActivity$20$orgtelegramuiPhotoViewer(view2);
                }
            });
            ImageView imageView9 = new ImageView(this.parentActivity);
            this.cameraItem = imageView9;
            imageView9.setScaleType(ImageView.ScaleType.CENTER);
            this.cameraItem.setImageResource(R.drawable.photo_add);
            this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.cameraItem.setContentDescription(LocaleController.getString("AccDescrTakeMorePics", R.string.AccDescrTakeMorePics));
            this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            this.cameraItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda17
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4231lambda$setParentActivity$21$orgtelegramuiPhotoViewer(view2);
                }
            });
            ImageView imageView10 = new ImageView(this.parentActivity);
            this.tuneItem = imageView10;
            imageView10.setScaleType(ImageView.ScaleType.CENTER);
            this.tuneItem.setImageResource(R.drawable.msg_photo_settings);
            this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.itemsLayout.addView(this.tuneItem, LayoutHelper.createLinear(48, 48));
            this.tuneItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda18
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4232lambda$setParentActivity$22$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.tuneItem.setContentDescription(LocaleController.getString("AccDescrPhotoAdjust", R.string.AccDescrPhotoAdjust));
            ImageView imageView11 = new ImageView(this.parentActivity);
            this.compressItem = imageView11;
            imageView11.setTag(1);
            this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
            this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            int selectCompression = selectCompression();
            this.selectedCompression = selectCompression;
            if (selectCompression <= 1) {
                this.compressItem.setImageResource(R.drawable.video_quality1);
            } else if (selectCompression != 2) {
                this.selectedCompression = this.compressionsCount - 1;
                this.compressItem.setImageResource(R.drawable.video_quality3);
            } else {
                this.compressItem.setImageResource(R.drawable.video_quality2);
            }
            this.compressItem.setContentDescription(LocaleController.getString("AccDescrVideoQuality", R.string.AccDescrVideoQuality));
            this.itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(48, 48));
            this.compressItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda32
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4233lambda$setParentActivity$23$orgtelegramuiPhotoViewer(activity, view2);
                }
            });
            ImageView imageView12 = new ImageView(this.parentActivity);
            this.timeItem = imageView12;
            imageView12.setScaleType(ImageView.ScaleType.CENTER);
            this.timeItem.setImageResource(R.drawable.msg_autodelete);
            this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", R.string.SetTimer));
            this.itemsLayout.addView(this.timeItem, LayoutHelper.createLinear(48, 48));
            this.timeItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda34
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4235lambda$setParentActivity$29$orgtelegramuiPhotoViewer(resourcesProvider, view2);
                }
            });
            PickerBottomLayoutViewer pickerBottomLayoutViewer2 = new PickerBottomLayoutViewer(this.activityContext);
            this.editorDoneLayout = pickerBottomLayoutViewer2;
            pickerBottomLayoutViewer2.setBackgroundColor(-872415232);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda19
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4237lambda$setParentActivity$31$orgtelegramuiPhotoViewer(view2);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda20
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4238lambda$setParentActivity$32$orgtelegramuiPhotoViewer(view2);
                }
            });
            TextView textView5 = new TextView(this.activityContext);
            this.resetButton = textView5;
            textView5.setClickable(false);
            this.resetButton.setVisibility(8);
            this.resetButton.setTextSize(1, 14.0f);
            this.resetButton.setTextColor(-1);
            this.resetButton.setGravity(17);
            this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
            this.resetButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.resetButton.setText(LocaleController.getString(TimerBuilder.RESET, R.string.CropReset).toUpperCase());
            this.resetButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
            this.resetButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda21
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4240lambda$setParentActivity$34$orgtelegramuiPhotoViewer(view2);
                }
            });
            GestureDetector2 gestureDetector2 = new GestureDetector2(this.containerView.getContext(), this);
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setIsLongpressEnabled(false);
            setDoubleTapEnabled(true);
            ImageReceiver.ImageReceiverDelegate imageReceiverDelegate = new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda69
                @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                    PhotoViewer.this.m4241lambda$setParentActivity$35$orgtelegramuiPhotoViewer(imageReceiver, z, z2, z3);
                }

                @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                }
            };
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setDelegate(imageReceiverDelegate);
            this.leftImage.setParentView(this.containerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.leftImage.setDelegate(imageReceiverDelegate);
            this.rightImage.setParentView(this.containerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            this.rightImage.setDelegate(imageReceiverDelegate);
            WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            int rotation = manager.getDefaultDisplay().getRotation();
            CheckBox checkBox = new CheckBox(this.containerView.getContext(), R.drawable.selectphoto_large) { // from class: org.telegram.ui.PhotoViewer.28
                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.checkImageView = checkBox;
            checkBox.setDrawBackground(true);
            this.checkImageView.setHasBorder(true);
            this.checkImageView.setSize(34);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
            this.checkImageView.setColor(getThemedColor(Theme.key_dialogFloatingButton), -1);
            this.checkImageView.setVisibility(8);
            this.containerView.addView(this.checkImageView, LayoutHelper.createFrame(34, 34.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 61.0f : 71.0f, 11.0f, 0.0f));
            if (isStatusBarVisible()) {
                ((FrameLayout.LayoutParams) this.checkImageView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.checkImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda23
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4242lambda$setParentActivity$36$orgtelegramuiPhotoViewer(view2);
                }
            });
            CounterView counterView = new CounterView(this.parentActivity);
            this.photosCounterView = counterView;
            this.containerView.addView(counterView, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 64.0f, 0.0f));
            if (isStatusBarVisible()) {
                ((FrameLayout.LayoutParams) this.photosCounterView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.photosCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda24
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.m4243lambda$setParentActivity$37$orgtelegramuiPhotoViewer(view2);
                }
            });
            SelectedPhotosListView selectedPhotosListView = new SelectedPhotosListView(this.parentActivity);
            this.selectedPhotosListView = selectedPhotosListView;
            selectedPhotosListView.setVisibility(8);
            this.selectedPhotosListView.setAlpha(0.0f);
            this.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(this.parentActivity, 0, true) { // from class: org.telegram.ui.PhotoViewer.29
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    LinearSmoothScrollerEnd linearSmoothScroller = new LinearSmoothScrollerEnd(recyclerView.getContext()) { // from class: org.telegram.ui.PhotoViewer.29.1
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // androidx.recyclerview.widget.LinearSmoothScrollerEnd
                        public int calculateTimeForDeceleration(int dx) {
                            return Math.max(180, super.calculateTimeForDeceleration(dx));
                        }
                    };
                    linearSmoothScroller.setTargetPosition(position);
                    startSmoothScroll(linearSmoothScroller);
                }
            });
            SelectedPhotosListView selectedPhotosListView2 = this.selectedPhotosListView;
            ListAdapter listAdapter = new ListAdapter(this.parentActivity);
            this.selectedPhotosAdapter = listAdapter;
            selectedPhotosListView2.setAdapter(listAdapter);
            this.containerView.addView(this.selectedPhotosListView, LayoutHelper.createFrame(-1, 103, 51));
            this.selectedPhotosListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda73
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view2, int i2) {
                    PhotoViewer.this.m4244lambda$setParentActivity$38$orgtelegramuiPhotoViewer(view2, i2);
                }
            });
            PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = new PhotoViewerCaptionEnterView(this.activityContext, this.containerView, this.windowView, resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.30
                @Override // android.view.ViewGroup, android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    try {
                        if (PhotoViewer.this.bottomTouchEnabled) {
                            return false;
                        }
                        return super.dispatchTouchEvent(ev);
                    } catch (Exception e) {
                        FileLog.e(e);
                        return false;
                    }
                }

                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    try {
                        if (PhotoViewer.this.bottomTouchEnabled) {
                            return false;
                        }
                        return super.onInterceptTouchEvent(ev);
                    } catch (Exception e) {
                        FileLog.e(e);
                        return false;
                    }
                }

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    if (PhotoViewer.this.bottomTouchEnabled && event.getAction() == 0) {
                        PhotoViewer.this.keyboardAnimationEnabled = true;
                    }
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }

                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView
                protected void extendActionMode(ActionMode actionMode, Menu menu2) {
                    if (PhotoViewer.this.parentChatActivity != null) {
                        PhotoViewer.this.parentChatActivity.extendActionMode(menu2);
                    }
                }
            };
            this.captionEditText = photoViewerCaptionEnterView2;
            photoViewerCaptionEnterView2.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate() { // from class: org.telegram.ui.PhotoViewer.31
                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
                public void onCaptionEnter() {
                    PhotoViewer.this.closeCaptionEnter(true);
                }

                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
                public void onTextChanged(CharSequence text) {
                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && text != null) {
                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false, false);
                    }
                    int color = PhotoViewer.this.getThemedColor(Theme.key_dialogFloatingIcon);
                    if (PhotoViewer.this.captionEditText.getCaptionLimitOffset() < 0) {
                        PhotoViewer.this.captionLimitView.setText(Integer.toString(PhotoViewer.this.captionEditText.getCaptionLimitOffset()));
                        PhotoViewer.this.captionLimitView.setVisibility(PhotoViewer.this.pickerViewSendButton.getVisibility());
                        PhotoViewer.this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (Color.alpha(color) * 0.58f)), PorterDuff.Mode.MULTIPLY));
                    } else {
                        PhotoViewer.this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        PhotoViewer.this.captionLimitView.setVisibility(8);
                    }
                    if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.placeProvider.onCaptionChanged(text);
                    }
                }

                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
                public void onWindowSizeChanged(int size) {
                    int height = AndroidUtilities.dp((Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0));
                    if (size - (ActionBar.getCurrentActionBarHeight() * 2) < height) {
                        PhotoViewer.this.allowMentions = false;
                        if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setVisibility(4);
                            return;
                        }
                        return;
                    }
                    PhotoViewer.this.allowMentions = true;
                    if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 4) {
                        PhotoViewer.this.mentionListView.setVisibility(0);
                    }
                }

                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
                public void onEmojiViewOpen() {
                    PhotoViewer.this.navigationBar.setVisibility(4);
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.animateNavBarColorTo(photoViewer.getThemedColor(Theme.key_chat_emojiPanelBackground), false);
                }

                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
                public void onEmojiViewCloseStart() {
                    String str;
                    int i2;
                    PhotoViewer.this.navigationBar.setVisibility(PhotoViewer.this.currentEditMode != 2 ? 0 : 4);
                    PhotoViewer.this.animateNavBarColorTo(-16777216);
                    setOffset(PhotoViewer.this.captionEditText.getEmojiPadding());
                    if (PhotoViewer.this.captionEditText.getTag() != null) {
                        if (PhotoViewer.this.isCurrentVideo) {
                            ActionBar actionBar2 = PhotoViewer.this.actionBar;
                            if (PhotoViewer.this.muteVideo) {
                                i2 = R.string.GifCaption;
                                str = "GifCaption";
                            } else {
                                i2 = R.string.VideoCaption;
                                str = "VideoCaption";
                            }
                            actionBar2.setTitleAnimated(LocaleController.getString(str, i2), true, 220L);
                        } else {
                            PhotoViewer.this.actionBar.setTitleAnimated(LocaleController.getString("PhotoCaption", R.string.PhotoCaption), true, 220L);
                        }
                        PhotoViewer.this.checkImageView.animate().alpha(0.0f).setDuration(220L).start();
                        PhotoViewer.this.photosCounterView.animate().alpha(0.0f).setDuration(220L).start();
                        PhotoViewer.this.selectedPhotosListView.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(220L).start();
                        return;
                    }
                    PhotoViewer.this.checkImageView.animate().alpha(1.0f).setDuration(220L).start();
                    PhotoViewer.this.photosCounterView.animate().alpha(1.0f).setDuration(220L).start();
                    if (PhotoViewer.this.lastTitle != null) {
                        PhotoViewer.this.actionBar.setTitleAnimated(PhotoViewer.this.lastTitle, false, 220L);
                        PhotoViewer.this.lastTitle = null;
                    }
                }

                @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
                public void onEmojiViewCloseEnd() {
                    setOffset(0);
                    PhotoViewer.this.captionEditText.setVisibility(8);
                }

                private void setOffset(int offset) {
                    for (int i2 = 0; i2 < PhotoViewer.this.containerView.getChildCount(); i2++) {
                        View child = PhotoViewer.this.containerView.getChildAt(i2);
                        if (child == PhotoViewer.this.cameraItem || child == PhotoViewer.this.muteItem || child == PhotoViewer.this.pickerView || child == PhotoViewer.this.videoTimelineView || child == PhotoViewer.this.pickerViewSendButton || child == PhotoViewer.this.captionTextViewSwitcher || (PhotoViewer.this.muteItem.getVisibility() == 0 && child == PhotoViewer.this.bottomLayout)) {
                            child.setTranslationY(offset);
                        }
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= 19) {
                this.captionEditText.setImportantForAccessibility(4);
            }
            this.captionEditText.setVisibility(8);
            this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
            RecyclerListView recyclerListView = new RecyclerListView(this.activityContext, resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.32
                @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.mentionListView = recyclerListView;
            recyclerListView.setTag(5);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activityContext) { // from class: org.telegram.ui.PhotoViewer.33
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mentionLayoutManager = linearLayoutManager;
            linearLayoutManager.setOrientation(1);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.mentionListView.setVisibility(8);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(2);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView2 = this.mentionListView;
            MentionsAdapter mentionsAdapter = new MentionsAdapter(this.activityContext, true, 0L, 0, new MentionsAdapter.MentionsAdapterDelegate() { // from class: org.telegram.ui.PhotoViewer.34
                @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
                public void onItemCountUpdate(int oldCount, int newCount) {
                }

                @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
                public void needChangePanelVisibility(boolean show) {
                    if (show) {
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        int height = (Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0);
                        layoutParams3.height = AndroidUtilities.dp(height);
                        layoutParams3.topMargin = -AndroidUtilities.dp(height);
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams3);
                        if (PhotoViewer.this.mentionListAnimation != null) {
                            PhotoViewer.this.mentionListAnimation.cancel();
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                        if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setAlpha(1.0f);
                            return;
                        }
                        PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListView.setVisibility(0);
                            PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, View.ALPHA, 0.0f, 1.0f));
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.34.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                        PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200L);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setAlpha(1.0f);
                        PhotoViewer.this.mentionListView.setVisibility(4);
                        return;
                    }
                    if (PhotoViewer.this.mentionListAnimation != null) {
                        PhotoViewer.this.mentionListAnimation.cancel();
                        PhotoViewer.this.mentionListAnimation = null;
                    }
                    if (PhotoViewer.this.mentionListView.getVisibility() != 8) {
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, View.ALPHA, 0.0f));
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.34.2
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                        PhotoViewer.this.mentionListView.setVisibility(8);
                                        PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200L);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setVisibility(8);
                    }
                }

                @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
                public void onContextSearch(boolean searching) {
                }

                @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
                public void onContextClick(TLRPC.BotInlineResult result) {
                }
            }, resourcesProvider);
            this.mentionsAdapter = mentionsAdapter;
            recyclerListView2.setAdapter(mentionsAdapter);
            this.mentionListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda74
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view2, int i2) {
                    PhotoViewer.this.m4245lambda$setParentActivity$39$orgtelegramuiPhotoViewer(view2, i2);
                }
            });
            this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda75
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public final boolean onItemClick(View view2, int i2) {
                    return PhotoViewer.this.m4248lambda$setParentActivity$41$orgtelegramuiPhotoViewer(resourcesProvider, view2, i2);
                }
            });
            UndoView undoView = new UndoView(this.activityContext, null, false, resourcesProvider);
            this.hintView = undoView;
            undoView.setAdditionalTranslationY(AndroidUtilities.dp(112.0f));
            this.hintView.setColors(-115203550, -1);
            this.containerView.addView(this.hintView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
            if (AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                View view2 = new View(this.activityContext);
                this.playButtonAccessibilityOverlay = view2;
                view2.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                this.playButtonAccessibilityOverlay.setFocusable(true);
                this.containerView.addView(this.playButtonAccessibilityOverlay, LayoutHelper.createFrame(64, 64, 17));
                return;
            }
            return;
        }
        updateColors();
    }

    /* renamed from: org.telegram.ui.PhotoViewer$10 */
    /* loaded from: classes4.dex */
    public class AnonymousClass10 extends FrameLayout {
        private Runnable attachRunnable;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass10(Context arg0) {
            super(arg0);
            PhotoViewer.this = this$0;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            event.getKeyCode();
            if (!PhotoViewer.this.muteVideo && PhotoViewer.this.sendPhotoType != 1 && PhotoViewer.this.isCurrentVideo && PhotoViewer.this.videoPlayer != null && event.getRepeatCount() == 0 && event.getAction() == 0 && (event.getKeyCode() == 24 || event.getKeyCode() == 25)) {
                PhotoViewer.this.videoPlayer.setVolume(1.0f);
            }
            return super.dispatchKeyEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying) {
                switch (ev.getActionMasked()) {
                    case 0:
                    case 5:
                        AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.hideActionBarRunnable);
                        break;
                    case 1:
                    case 3:
                    case 6:
                        PhotoViewer.this.scheduleActionBarHide();
                        break;
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            try {
                boolean result = super.drawChild(canvas, child, drawingTime);
                return result;
            } catch (Throwable th) {
                return false;
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int newSize;
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (Build.VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                WindowInsets insets = (WindowInsets) PhotoViewer.this.lastInsets;
                if (!PhotoViewer.this.inBubbleMode) {
                    if (AndroidUtilities.incorrectDisplaySizeFix) {
                        if (heightSize > AndroidUtilities.displaySize.y) {
                            heightSize = AndroidUtilities.displaySize.y;
                        }
                        heightSize += AndroidUtilities.statusBarHeight;
                    } else {
                        int insetBottom = insets.getStableInsetBottom();
                        if (insetBottom >= 0 && AndroidUtilities.statusBarHeight >= 0 && (newSize = (heightSize - AndroidUtilities.statusBarHeight) - insets.getStableInsetBottom()) > 0 && newSize < 4096) {
                            AndroidUtilities.displaySize.y = newSize;
                        }
                    }
                }
                int bottomInsets = insets.getSystemWindowInsetBottom();
                if (PhotoViewer.this.captionEditText.isPopupShowing()) {
                    bottomInsets -= PhotoViewer.this.containerView.getKeyboardHeight();
                }
                heightSize -= bottomInsets;
            } else if (heightSize > AndroidUtilities.displaySize.y) {
                heightSize = AndroidUtilities.displaySize.y;
            }
            int widthSize2 = widthSize - (getPaddingLeft() + getPaddingRight());
            int heightSize2 = heightSize - getPaddingBottom();
            setMeasuredDimension(widthSize2, heightSize2);
            ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
            PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
            PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(heightSize2, C.BUFFER_FLAG_ENCRYPTED));
            PhotoViewer.this.navigationBar.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(PhotoViewer.this.navigationBarHeight, C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            PhotoViewer.this.animatingImageView.layout(getPaddingLeft(), 0, getPaddingLeft() + PhotoViewer.this.animatingImageView.getMeasuredWidth(), PhotoViewer.this.animatingImageView.getMeasuredHeight());
            PhotoViewer.this.containerView.layout(getPaddingLeft(), 0, getPaddingLeft() + PhotoViewer.this.containerView.getMeasuredWidth(), PhotoViewer.this.containerView.getMeasuredHeight());
            PhotoViewer.this.navigationBar.layout(getPaddingLeft(), PhotoViewer.this.containerView.getMeasuredHeight(), PhotoViewer.this.navigationBar.getMeasuredWidth(), PhotoViewer.this.containerView.getMeasuredHeight() + PhotoViewer.this.navigationBar.getMeasuredHeight());
            PhotoViewer.this.wasLayout = true;
            if (changed) {
                if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                    PhotoViewer.this.scale = 1.0f;
                    PhotoViewer.this.translationX = 0.0f;
                    PhotoViewer.this.translationY = 0.0f;
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.updateMinMax(photoViewer.scale);
                }
                if (PhotoViewer.this.checkImageView != null) {
                    PhotoViewer.this.checkImageView.post(new Runnable() { // from class: org.telegram.ui.PhotoViewer$10$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.AnonymousClass10.this.m4264lambda$onLayout$0$orgtelegramuiPhotoViewer$10();
                        }
                    });
                }
            }
            if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                PhotoViewer.this.setScaleToFill();
                PhotoViewer.this.dontResetZoomOnFirstLayout = false;
            }
        }

        /* renamed from: lambda$onLayout$0$org-telegram-ui-PhotoViewer$10 */
        public /* synthetic */ void m4264lambda$onLayout$0$orgtelegramuiPhotoViewer$10() {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
            WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            manager.getDefaultDisplay().getRotation();
            int i = 0;
            int newMargin = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(34.0f)) / 2) + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
            if (newMargin != layoutParams.topMargin) {
                layoutParams.topMargin = newMargin;
                PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) PhotoViewer.this.photosCounterView.getLayoutParams();
            int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2;
            if (PhotoViewer.this.isStatusBarVisible()) {
                i = AndroidUtilities.statusBarHeight;
            }
            int newMargin2 = currentActionBarHeight + i;
            if (layoutParams2.topMargin != newMargin2) {
                layoutParams2.topMargin = newMargin2;
                PhotoViewer.this.photosCounterView.setLayoutParams(layoutParams2);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            PhotoViewer.this.centerImage.onAttachedToWindow();
            PhotoViewer.this.leftImage.onAttachedToWindow();
            PhotoViewer.this.rightImage.onAttachedToWindow();
            PhotoViewer.this.attachedToWindow = true;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            PhotoViewer.this.centerImage.onDetachedFromWindow();
            PhotoViewer.this.leftImage.onDetachedFromWindow();
            PhotoViewer.this.rightImage.onDetachedFromWindow();
            PhotoViewer.this.attachedToWindow = false;
            PhotoViewer.this.wasLayout = false;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (event != null && event.getKeyCode() == 4 && event.getAction() == 1) {
                if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                    PhotoViewer.this.closeCaptionEnter(true);
                    return false;
                }
                PhotoViewer.getInstance().closePhoto(true, false);
                return true;
            }
            return super.dispatchKeyEventPreIme(event);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (Build.VERSION.SDK_INT >= 21 && PhotoViewer.this.isVisible && PhotoViewer.this.lastInsets != null) {
                WindowInsets insets = (WindowInsets) PhotoViewer.this.lastInsets;
                PhotoViewer.this.blackPaint.setAlpha(PhotoViewer.this.backgroundDrawable.getAlpha());
                canvas.drawRect(0.0f, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + insets.getSystemWindowInsetBottom(), PhotoViewer.this.blackPaint);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (PhotoViewer.this.parentChatActivity != null) {
                View undoView = PhotoViewer.this.parentChatActivity.getUndoView();
                if (undoView.getVisibility() == 0) {
                    canvas.save();
                    View parent = (View) undoView.getParent();
                    canvas.clipRect(parent.getX(), parent.getY(), parent.getX() + parent.getWidth(), parent.getY() + parent.getHeight());
                    canvas.translate(undoView.getX(), undoView.getY());
                    undoView.draw(canvas);
                    canvas.restore();
                    invalidate();
                }
            }
        }
    }

    /* renamed from: lambda$setParentActivity$4$org-telegram-ui-PhotoViewer */
    public /* synthetic */ WindowInsets m4246lambda$setParentActivity$4$orgtelegramuiPhotoViewer(View v, WindowInsets insets) {
        int newTopInset = insets.getSystemWindowInsetTop();
        if ((this.parentActivity instanceof LaunchActivity) && ((newTopInset != 0 || AndroidUtilities.isInMultiwindow) && !this.inBubbleMode && AndroidUtilities.statusBarHeight != newTopInset)) {
            AndroidUtilities.statusBarHeight = newTopInset;
            ((LaunchActivity) this.parentActivity).drawerLayoutContainer.requestLayout();
        }
        WindowInsets oldInsets = (WindowInsets) this.lastInsets;
        this.lastInsets = insets;
        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
            int i = this.animationInProgress;
            if (i == 1 || i == 3) {
                ClippingImageView clippingImageView = this.animatingImageView;
                clippingImageView.setTranslationX(clippingImageView.getTranslationX() - getLeftInset());
                this.animationValues[0][2] = this.animatingImageView.getTranslationX();
            }
            FrameLayout frameLayout = this.windowView;
            if (frameLayout != null) {
                frameLayout.requestLayout();
            }
        }
        if (this.navigationBar != null) {
            this.navigationBarHeight = insets.getSystemWindowInsetBottom();
            ViewGroup.MarginLayoutParams navigationBarLayoutParams = (ViewGroup.MarginLayoutParams) this.navigationBar.getLayoutParams();
            navigationBarLayoutParams.height = this.navigationBarHeight;
            navigationBarLayoutParams.bottomMargin = (-this.navigationBarHeight) / 2;
            this.navigationBar.setLayoutParams(navigationBarLayoutParams);
        }
        this.containerView.setPadding(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), 0);
        if (this.actionBar != null) {
            AndroidUtilities.cancelRunOnUIThread(this.updateContainerFlagsRunnable);
            if (this.isVisible && this.animationInProgress == 0) {
                AndroidUtilities.runOnUIThread(this.updateContainerFlagsRunnable, 200L);
            }
        }
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    /* renamed from: org.telegram.ui.PhotoViewer$12 */
    /* loaded from: classes4.dex */
    public class AnonymousClass12 extends ActionBar.ActionBarMenuOnItemClick {
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass12(Theme.ResourcesProvider resourcesProvider) {
            PhotoViewer.this = this$0;
            this.val$resourcesProvider = resourcesProvider;
        }

        /* JADX WARN: Code restructure failed: missing block: B:204:0x063e, code lost:
            if (r14.id == org.telegram.messenger.UserConfig.getInstance(org.telegram.ui.PhotoViewer.this.currentAccount).getClientUserId()) goto L206;
         */
        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onItemClick(int r33) {
            /*
                Method dump skipped, instructions count: 3328
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass12.onItemClick(int):void");
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4265lambda$onItemClick$0$orgtelegramuiPhotoViewer$12(boolean isVideo) {
            BulletinFactory.createSaveToGalleryBulletin(PhotoViewer.this.containerView, isVideo, -115203550, -1).show();
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4266lambda$onItemClick$1$orgtelegramuiPhotoViewer$12(DialogInterface di, int a) {
            ArrayList<MessageObject> singleMessage = new ArrayList<>(1);
            singleMessage.add(PhotoViewer.this.currentMessageObject);
            PhotoViewer.this.showShareAlert(singleMessage);
        }

        /* renamed from: lambda$onItemClick$2$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4267lambda$onItemClick$2$orgtelegramuiPhotoViewer$12(ArrayList msgs, DialogInterface di, int a) {
            PhotoViewer.this.showShareAlert(msgs);
        }

        /* renamed from: lambda$onItemClick$4$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4268lambda$onItemClick$4$orgtelegramuiPhotoViewer$12(ArrayList fmessages, ChatActivity parentChatActivityFinal, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
            if (dids.size() <= 1 && ((Long) dids.get(0)).longValue() != UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) {
                if (message == null) {
                    long did = ((Long) dids.get(0)).longValue();
                    Bundle args1 = new Bundle();
                    args1.putBoolean("scrollToTopOnResume", true);
                    if (DialogObject.isEncryptedDialog(did)) {
                        args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
                    } else if (DialogObject.isUserDialog(did)) {
                        args1.putLong("user_id", did);
                    } else {
                        args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
                    }
                    NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ChatActivity chatActivity = new ChatActivity(args1);
                    if (((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(chatActivity, true, false)) {
                        chatActivity.showFieldPanelForForward(true, fmessages);
                        return;
                    } else {
                        fragment1.finishFragment();
                        return;
                    }
                }
            }
            for (int a = 0; a < dids.size(); a++) {
                long did2 = ((Long) dids.get(a)).longValue();
                if (message != null) {
                    SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(message.toString(), did2, null, null, null, true, null, null, null, true, 0, null);
                }
                SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage((ArrayList<MessageObject>) fmessages, did2, false, false, true, 0);
            }
            fragment1.finishFragment();
            if (parentChatActivityFinal != null) {
                if (dids.size() == 1) {
                    parentChatActivityFinal.getUndoView().showWithAction(((Long) dids.get(0)).longValue(), 53, Integer.valueOf(fmessages.size()));
                } else {
                    parentChatActivityFinal.getUndoView().showWithAction(0L, 53, Integer.valueOf(fmessages.size()), Integer.valueOf(dids.size()), (Runnable) null, (Runnable) null);
                }
            }
        }

        public static /* synthetic */ void lambda$onItemClick$5(boolean[] deleteForAll, View v) {
            CheckBoxCell cell1 = (CheckBoxCell) v;
            deleteForAll[0] = !deleteForAll[0];
            cell1.setChecked(deleteForAll[0], true);
        }

        /* renamed from: lambda$onItemClick$6$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4269lambda$onItemClick$6$orgtelegramuiPhotoViewer$12(boolean[] deleteForAll, DialogInterface dialogInterface, int i) {
            TLRPC.EncryptedChat encryptedChat;
            ArrayList<Long> random_ids;
            if (!PhotoViewer.this.imagesArr.isEmpty()) {
                if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                    MessageObject obj = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                    if (obj.isSent()) {
                        PhotoViewer.this.closePhoto(false, false);
                        ArrayList<Integer> arr = new ArrayList<>();
                        if (PhotoViewer.this.slideshowMessageId != 0) {
                            arr.add(Integer.valueOf(PhotoViewer.this.slideshowMessageId));
                        } else {
                            arr.add(Integer.valueOf(obj.getId()));
                        }
                        if (DialogObject.isEncryptedDialog(obj.getDialogId()) && obj.messageOwner.random_id != 0) {
                            ArrayList<Long> random_ids2 = new ArrayList<>();
                            random_ids2.add(Long.valueOf(obj.messageOwner.random_id));
                            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(obj.getDialogId())));
                            random_ids = random_ids2;
                            encryptedChat = encryptedChat2;
                        } else {
                            random_ids = null;
                            encryptedChat = null;
                        }
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arr, random_ids, encryptedChat, obj.getDialogId(), deleteForAll[0], obj.scheduled);
                    }
                }
            } else if (!PhotoViewer.this.avatarsArr.isEmpty()) {
                if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
                    TLRPC.Message message = (TLRPC.Message) PhotoViewer.this.imagesArrMessages.get(PhotoViewer.this.currentIndex);
                    if (message != null) {
                        ArrayList<Integer> arr2 = new ArrayList<>();
                        arr2.add(Integer.valueOf(message.id));
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arr2, null, null, MessageObject.getDialogId(message), true, false);
                        NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.reloadDialogPhotos, new Object[0]);
                    }
                    if (PhotoViewer.this.isCurrentAvatarSet()) {
                        if (PhotoViewer.this.avatarsDialogId > 0) {
                            MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(null);
                        } else {
                            MessagesController.getInstance(PhotoViewer.this.currentAccount).changeChatAvatar(-PhotoViewer.this.avatarsDialogId, null, null, null, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, null, null, null, null);
                        }
                        PhotoViewer.this.closePhoto(false, false);
                        return;
                    }
                    TLRPC.Photo photo = (TLRPC.Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                    if (photo == null) {
                        return;
                    }
                    TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
                    inputPhoto.id = photo.id;
                    inputPhoto.access_hash = photo.access_hash;
                    inputPhoto.file_reference = photo.file_reference;
                    if (inputPhoto.file_reference == null) {
                        inputPhoto.file_reference = new byte[0];
                    }
                    if (PhotoViewer.this.avatarsDialogId > 0) {
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(inputPhoto);
                    }
                    MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo.id);
                    PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                    PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                    PhotoViewer.this.imagesArrLocationsVideo.remove(PhotoViewer.this.currentIndex);
                    PhotoViewer.this.imagesArrMessages.remove(PhotoViewer.this.currentIndex);
                    PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                    if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                        int index = PhotoViewer.this.currentIndex;
                        if (index >= PhotoViewer.this.avatarsArr.size()) {
                            index = PhotoViewer.this.avatarsArr.size() - 1;
                        }
                        PhotoViewer.this.currentIndex = -1;
                        PhotoViewer.this.setImageIndex(index);
                    } else {
                        PhotoViewer.this.closePhoto(false, false);
                    }
                    if (message == null) {
                        NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.reloadDialogPhotos, new Object[0]);
                    }
                }
            } else if (!PhotoViewer.this.secureDocuments.isEmpty() && PhotoViewer.this.placeProvider != null) {
                PhotoViewer.this.secureDocuments.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.placeProvider.deleteImageAtIndex(PhotoViewer.this.currentIndex);
                if (!PhotoViewer.this.secureDocuments.isEmpty()) {
                    int index2 = PhotoViewer.this.currentIndex;
                    if (index2 >= PhotoViewer.this.secureDocuments.size()) {
                        index2 = PhotoViewer.this.secureDocuments.size() - 1;
                    }
                    PhotoViewer.this.currentIndex = -1;
                    PhotoViewer.this.setImageIndex(index2);
                    return;
                }
                PhotoViewer.this.closePhoto(false, false);
            }
        }

        /* renamed from: lambda$onItemClick$8$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4271lambda$onItemClick$8$orgtelegramuiPhotoViewer$12(final UserConfig userConfig, final TLRPC.Photo photo, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$12$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass12.this.m4270lambda$onItemClick$7$orgtelegramuiPhotoViewer$12(response, userConfig, photo);
                }
            });
        }

        /* renamed from: lambda$onItemClick$7$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4270lambda$onItemClick$7$orgtelegramuiPhotoViewer$12(TLObject response, UserConfig userConfig, TLRPC.Photo photo) {
            if (response instanceof TLRPC.TL_photos_photo) {
                TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                MessagesController.getInstance(PhotoViewer.this.currentAccount).putUsers(photos_photo.users, false);
                TLRPC.User user = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Long.valueOf(userConfig.clientUserId));
                if (photos_photo.photo instanceof TLRPC.TL_photo) {
                    int idx = PhotoViewer.this.avatarsArr.indexOf(photo);
                    if (idx >= 0) {
                        PhotoViewer.this.avatarsArr.set(idx, photos_photo.photo);
                    }
                    if (user != null) {
                        user.photo.photo_id = photos_photo.photo.id;
                        userConfig.setCurrentUser(user);
                        userConfig.saveConfig(true);
                    }
                }
            }
        }

        /* renamed from: lambda$onItemClick$9$org-telegram-ui-PhotoViewer$12 */
        public /* synthetic */ void m4272lambda$onItemClick$9$orgtelegramuiPhotoViewer$12() {
            if (PhotoViewer.this.menuItem != null) {
                PhotoViewer.this.menuItem.hideSubItem(16);
            }
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public boolean canOpenMenu() {
            boolean z = true;
            if (PhotoViewer.this.currentMessageObject == null && PhotoViewer.this.currentSecureDocument == null) {
                if (PhotoViewer.this.currentFileLocationVideo == null) {
                    return PhotoViewer.this.pageBlocksAdapter != null;
                }
                FileLoader fileLoader = FileLoader.getInstance(PhotoViewer.this.currentAccount);
                TLRPC.FileLocation fileLocation = PhotoViewer.getFileLocation(PhotoViewer.this.currentFileLocationVideo);
                String fileLocationExt = PhotoViewer.getFileLocationExt(PhotoViewer.this.currentFileLocationVideo);
                if (PhotoViewer.this.avatarsDialogId == 0 && !PhotoViewer.this.isEvent) {
                    z = false;
                }
                File f = fileLoader.getPathToAttach(fileLocation, fileLocationExt, z);
                return f.exists();
            }
            return true;
        }
    }

    /* renamed from: lambda$setParentActivity$5$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4249lambda$setParentActivity$5$orgtelegramuiPhotoViewer(View v) {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return;
        }
        this.wasRotated = false;
        this.fullscreenedByButton = 1;
        if (this.prevOrientation == -10) {
            this.prevOrientation = activity.getRequestedOrientation();
        }
        WindowManager manager = (WindowManager) this.parentActivity.getSystemService("window");
        int displayRotation = manager.getDefaultDisplay().getRotation();
        if (displayRotation != 3) {
            this.parentActivity.setRequestedOrientation(0);
        } else {
            this.parentActivity.setRequestedOrientation(8);
        }
        toggleActionBar(false, false);
    }

    /* renamed from: lambda$setParentActivity$7$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4251lambda$setParentActivity$7$orgtelegramuiPhotoViewer(View v) {
        openCurrentPhotoInPaintModeForSelect();
    }

    /* renamed from: lambda$setParentActivity$8$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4252lambda$setParentActivity$8$orgtelegramuiPhotoViewer(View v) {
        onSharePressed();
    }

    /* renamed from: lambda$setParentActivity$9$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4253lambda$setParentActivity$9$orgtelegramuiPhotoViewer(View view) {
        this.selectedCompression = this.previousCompression;
        didChangedCompressionLevel(false);
        showQualityView(false);
        requestVideoPreview(2);
    }

    /* renamed from: lambda$setParentActivity$10$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4220lambda$setParentActivity$10$orgtelegramuiPhotoViewer(View view) {
        showQualityView(false);
        requestVideoPreview(2);
    }

    /* renamed from: org.telegram.ui.PhotoViewer$24 */
    /* loaded from: classes4.dex */
    public class AnonymousClass24 implements VideoTimelinePlayView.VideoTimelineViewDelegate {
        private int seekTo;
        private Runnable seekToRunnable;
        private boolean wasPlaying;

        AnonymousClass24() {
            PhotoViewer.this = this$0;
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void onLeftProgressChanged(float progress) {
            if (PhotoViewer.this.videoPlayer != null) {
                if (PhotoViewer.this.videoPlayer.isPlaying()) {
                    PhotoViewer.this.manuallyPaused = false;
                    PhotoViewer.this.videoPlayer.pause();
                    PhotoViewer.this.containerView.invalidate();
                }
                updateAvatarStartTime(1);
                seekTo(progress);
                PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                PhotoViewer.this.videoTimelineView.setProgress(progress);
                PhotoViewer.this.updateVideoInfo();
            }
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void onRightProgressChanged(float progress) {
            if (PhotoViewer.this.videoPlayer != null) {
                if (PhotoViewer.this.videoPlayer.isPlaying()) {
                    PhotoViewer.this.manuallyPaused = false;
                    PhotoViewer.this.videoPlayer.pause();
                    PhotoViewer.this.containerView.invalidate();
                }
                updateAvatarStartTime(2);
                seekTo(progress);
                PhotoViewer.this.videoPlayerSeekbar.setProgress(1.0f);
                PhotoViewer.this.videoTimelineView.setProgress(progress);
                PhotoViewer.this.updateVideoInfo();
            }
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void onPlayProgressChanged(float progress) {
            if (PhotoViewer.this.videoPlayer != null) {
                if (PhotoViewer.this.sendPhotoType == 1) {
                    updateAvatarStartTime(0);
                }
                seekTo(progress);
            }
        }

        private void seekTo(float progress) {
            this.seekTo = (int) (PhotoViewer.this.videoDuration * progress);
            if (SharedConfig.getDevicePerformanceClass() == 2) {
                if (PhotoViewer.this.videoPlayer != null) {
                    PhotoViewer.this.videoPlayer.seekTo(this.seekTo);
                }
                if (PhotoViewer.this.sendPhotoType == 1) {
                    PhotoViewer.this.needCaptureFrameReadyAtTime = this.seekTo;
                    if (PhotoViewer.this.captureFrameReadyAtTime != PhotoViewer.this.needCaptureFrameReadyAtTime) {
                        PhotoViewer.this.captureFrameReadyAtTime = -1L;
                    }
                }
                this.seekToRunnable = null;
            } else if (this.seekToRunnable == null) {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$24$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass24.this.m4273lambda$seekTo$0$orgtelegramuiPhotoViewer$24();
                    }
                };
                this.seekToRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 100L);
            }
        }

        /* renamed from: lambda$seekTo$0$org-telegram-ui-PhotoViewer$24 */
        public /* synthetic */ void m4273lambda$seekTo$0$orgtelegramuiPhotoViewer$24() {
            if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.seekTo(this.seekTo);
            }
            if (PhotoViewer.this.sendPhotoType == 1) {
                PhotoViewer.this.needCaptureFrameReadyAtTime = this.seekTo;
                if (PhotoViewer.this.captureFrameReadyAtTime != PhotoViewer.this.needCaptureFrameReadyAtTime) {
                    PhotoViewer.this.captureFrameReadyAtTime = -1L;
                }
            }
            this.seekToRunnable = null;
        }

        private void updateAvatarStartTime(int fix) {
            if (PhotoViewer.this.sendPhotoType != 1) {
                return;
            }
            if (fix != 0) {
                if (PhotoViewer.this.photoCropView != null) {
                    if (PhotoViewer.this.videoTimelineView.getLeftProgress() > PhotoViewer.this.avatarStartProgress || PhotoViewer.this.videoTimelineView.getRightProgress() < PhotoViewer.this.avatarStartProgress) {
                        PhotoViewer.this.photoCropView.setVideoThumbVisible(false);
                        if (fix == 1) {
                            PhotoViewer photoViewer = PhotoViewer.this;
                            photoViewer.avatarStartTime = photoViewer.videoDuration * 1000.0f * PhotoViewer.this.videoTimelineView.getLeftProgress();
                        } else {
                            PhotoViewer photoViewer2 = PhotoViewer.this;
                            photoViewer2.avatarStartTime = photoViewer2.videoDuration * 1000.0f * PhotoViewer.this.videoTimelineView.getRightProgress();
                        }
                        PhotoViewer.this.captureFrameAtTime = -1L;
                        return;
                    }
                    return;
                }
                return;
            }
            PhotoViewer photoViewer3 = PhotoViewer.this;
            photoViewer3.avatarStartProgress = photoViewer3.videoTimelineView.getProgress();
            PhotoViewer photoViewer4 = PhotoViewer.this;
            photoViewer4.avatarStartTime = photoViewer4.videoDuration * 1000.0f * PhotoViewer.this.avatarStartProgress;
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void didStartDragging(int type) {
            if (type == VideoTimelinePlayView.TYPE_PROGRESS) {
                PhotoViewer.this.cancelVideoPlayRunnable();
                boolean z = true;
                if (PhotoViewer.this.sendPhotoType == 1) {
                    PhotoViewer.this.cancelFlashAnimations();
                    PhotoViewer.this.captureFrameAtTime = -1L;
                }
                if (PhotoViewer.this.videoPlayer == null || !PhotoViewer.this.videoPlayer.isPlaying()) {
                    z = false;
                }
                this.wasPlaying = z;
                if (z) {
                    PhotoViewer.this.manuallyPaused = false;
                    PhotoViewer.this.videoPlayer.pause();
                    PhotoViewer.this.containerView.invalidate();
                }
            }
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void didStopDragging(int type) {
            Runnable runnable = this.seekToRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.seekToRunnable.run();
            }
            PhotoViewer.this.cancelVideoPlayRunnable();
            if (PhotoViewer.this.sendPhotoType != 1 || PhotoViewer.this.flashView == null || type != VideoTimelinePlayView.TYPE_PROGRESS) {
                if (PhotoViewer.this.sendPhotoType == 1 || this.wasPlaying) {
                    PhotoViewer.this.manuallyPaused = false;
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.play();
                        return;
                    }
                    return;
                }
                return;
            }
            PhotoViewer.this.cancelFlashAnimations();
            PhotoViewer photoViewer = PhotoViewer.this;
            photoViewer.captureFrameAtTime = photoViewer.avatarStartTime;
            if (PhotoViewer.this.captureFrameReadyAtTime == this.seekTo) {
                PhotoViewer.this.captureCurrentFrame();
            }
        }
    }

    /* renamed from: lambda$setParentActivity$11$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4221lambda$setParentActivity$11$orgtelegramuiPhotoViewer(View v) {
        if (this.captionEditText.getCaptionLimitOffset() < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator vibrator = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
                return;
            }
            return;
        }
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && chatActivity.isInScheduleMode() && !this.parentChatActivity.isEditingMessageMedia()) {
            showScheduleDatePickerDialog();
        } else {
            sendPressed(true, 0);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x00f5, code lost:
        if (r12 == 3) goto L60;
     */
    /* JADX WARN: Removed duplicated region for block: B:72:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x011c  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x011f  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0121  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0129  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x014c  */
    /* renamed from: lambda$setParentActivity$15$org-telegram-ui-PhotoViewer */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ boolean m4225lambda$setParentActivity$15$orgtelegramuiPhotoViewer(org.telegram.ui.ActionBar.Theme.ResourcesProvider r19, android.view.View r20) {
        /*
            Method dump skipped, instructions count: 618
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.m4225lambda$setParentActivity$15$orgtelegramuiPhotoViewer(org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.view.View):boolean");
    }

    /* renamed from: lambda$setParentActivity$12$org-telegram-ui-PhotoViewer */
    public /* synthetic */ boolean m4222lambda$setParentActivity$12$orgtelegramuiPhotoViewer(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            v.getHitRect(this.hitRect);
            if (!this.hitRect.contains((int) event.getX(), (int) event.getY())) {
                this.sendPopupWindow.dismiss();
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$setParentActivity$13$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4223lambda$setParentActivity$13$orgtelegramuiPhotoViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$setParentActivity$14$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4224lambda$setParentActivity$14$orgtelegramuiPhotoViewer(int a, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (a == 0) {
            showScheduleDatePickerDialog();
        } else if (a == 1) {
            sendPressed(false, 0);
        } else if (a == 2) {
            replacePressed();
        } else if (a == 3) {
            sendPressed(true, 0);
        } else if (a == 4) {
            sendPressed(true, 0, false, true);
        }
    }

    /* renamed from: lambda$setParentActivity$16$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4226lambda$setParentActivity$16$orgtelegramuiPhotoViewer(View v) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        if (this.isCurrentVideo) {
            if (!this.videoConvertSupported) {
                return;
            }
            TextureView textureView = this.videoTextureView;
            if (textureView instanceof VideoEditTextureView) {
                VideoEditTextureView textureView2 = (VideoEditTextureView) textureView;
                if (textureView2.getVideoWidth() <= 0 || textureView2.getVideoHeight() <= 0) {
                    return;
                }
            } else {
                return;
            }
        }
        switchToEditMode(1);
    }

    /* renamed from: lambda$setParentActivity$17$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4227lambda$setParentActivity$17$orgtelegramuiPhotoViewer(View v) {
        cropRotate(-90.0f);
    }

    /* renamed from: lambda$setParentActivity$18$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4228lambda$setParentActivity$18$orgtelegramuiPhotoViewer(View v) {
        cropMirror();
    }

    /* renamed from: lambda$setParentActivity$19$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4229lambda$setParentActivity$19$orgtelegramuiPhotoViewer(View v) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        if (this.isCurrentVideo) {
            if (!this.videoConvertSupported) {
                return;
            }
            TextureView textureView = this.videoTextureView;
            if (textureView instanceof VideoEditTextureView) {
                VideoEditTextureView textureView2 = (VideoEditTextureView) textureView;
                if (textureView2.getVideoWidth() <= 0 || textureView2.getVideoHeight() <= 0) {
                    return;
                }
            } else {
                return;
            }
        }
        switchToEditMode(3);
    }

    /* renamed from: lambda$setParentActivity$20$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4230lambda$setParentActivity$20$orgtelegramuiPhotoViewer(View v) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        this.muteVideo = !this.muteVideo;
        updateMuteButton();
        updateVideoInfo();
        if (this.muteVideo && !this.checkImageView.isChecked()) {
            this.checkImageView.callOnClick();
            return;
        }
        Object object = this.imagesArrLocals.get(this.currentIndex);
        if (object instanceof MediaController.MediaEditState) {
            ((MediaController.MediaEditState) object).editedInfo = getCurrentVideoEditedInfo();
        }
    }

    /* renamed from: lambda$setParentActivity$21$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4231lambda$setParentActivity$21$orgtelegramuiPhotoViewer(View v) {
        if (this.placeProvider == null || this.captionEditText.getTag() != null) {
            return;
        }
        this.placeProvider.needAddMorePhotos();
        closePhoto(true, false);
    }

    /* renamed from: lambda$setParentActivity$22$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4232lambda$setParentActivity$22$orgtelegramuiPhotoViewer(View v) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        if (this.isCurrentVideo) {
            if (!this.videoConvertSupported) {
                return;
            }
            TextureView textureView = this.videoTextureView;
            if (textureView instanceof VideoEditTextureView) {
                VideoEditTextureView textureView2 = (VideoEditTextureView) textureView;
                if (textureView2.getVideoWidth() <= 0 || textureView2.getVideoHeight() <= 0) {
                    return;
                }
            } else {
                return;
            }
        }
        switchToEditMode(2);
    }

    /* renamed from: lambda$setParentActivity$23$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4233lambda$setParentActivity$23$orgtelegramuiPhotoViewer(Activity activity, View v) {
        if (this.captionEditText.getTag() != null || this.muteVideo) {
            return;
        }
        if (this.compressItem.getTag() == null) {
            if (this.videoConvertSupported) {
                if (this.tooltip == null) {
                    this.tooltip = new Tooltip(activity, this.containerView, -871296751, -1);
                }
                this.tooltip.setText(LocaleController.getString("VideoQualityIsTooLow", R.string.VideoQualityIsTooLow));
                this.tooltip.show(this.compressItem);
                return;
            }
            return;
        }
        showQualityView(true);
        requestVideoPreview(1);
    }

    /* renamed from: lambda$setParentActivity$29$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4235lambda$setParentActivity$29$orgtelegramuiPhotoViewer(Theme.ResourcesProvider resourcesProvider, View v) {
        String str;
        int i;
        int currentTTL;
        if (this.parentActivity == null || this.captionEditText.getTag() != null) {
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity, false, resourcesProvider, -16777216);
        builder.setUseHardwareLayer(false);
        LinearLayout linearLayout = new LinearLayout(this.parentActivity);
        linearLayout.setOrientation(1);
        builder.setCustomView(linearLayout);
        TextView titleView = new TextView(this.parentActivity);
        titleView.setLines(1);
        titleView.setSingleLine(true);
        titleView.setText(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        titleView.setTextColor(-1);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleView.setTextSize(1, 20.0f);
        titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
        titleView.setGravity(16);
        linearLayout.addView(titleView, LayoutHelper.createFrame(-1, -2.0f));
        titleView.setOnTouchListener(PhotoViewer$$ExternalSyntheticLambda38.INSTANCE);
        TextView titleView2 = new TextView(this.parentActivity);
        if (this.isCurrentVideo) {
            i = R.string.MessageLifetimeVideo;
            str = "MessageLifetimeVideo";
        } else {
            i = R.string.MessageLifetimePhoto;
            str = "MessageLifetimePhoto";
        }
        titleView2.setText(LocaleController.getString(str, i));
        titleView2.setTextColor(-8355712);
        titleView2.setTextSize(1, 14.0f);
        titleView2.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        titleView2.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
        titleView2.setGravity(16);
        linearLayout.addView(titleView2, LayoutHelper.createFrame(-1, -2.0f));
        titleView2.setOnTouchListener(PhotoViewer$$ExternalSyntheticLambda39.INSTANCE);
        final BottomSheet bottomSheet = builder.create();
        final NumberPicker numberPicker = new NumberPicker(this.parentActivity, resourcesProvider);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(28);
        Object object = this.imagesArrLocals.get(this.currentIndex);
        if (object instanceof MediaController.PhotoEntry) {
            currentTTL = ((MediaController.PhotoEntry) object).ttl;
        } else if (object instanceof MediaController.SearchImage) {
            currentTTL = ((MediaController.SearchImage) object).ttl;
        } else {
            currentTTL = 0;
        }
        if (currentTTL == 0) {
            SharedPreferences preferences1 = MessagesController.getGlobalMainSettings();
            numberPicker.setValue(preferences1.getInt("self_destruct", 7));
        } else if (currentTTL < 0 || currentTTL >= 21) {
            numberPicker.setValue(((currentTTL / 5) + 21) - 5);
        } else {
            numberPicker.setValue(currentTTL);
        }
        numberPicker.setTextColor(-1);
        numberPicker.setSelectorColor(-11711155);
        numberPicker.setFormatter(PhotoViewer$$ExternalSyntheticLambda72.INSTANCE);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
        FrameLayout buttonsLayout = new FrameLayout(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.27
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int count = getChildCount();
                int width = right - left;
                for (int a = 0; a < count; a++) {
                    View child = getChildAt(a);
                    if (((Integer) child.getTag()).intValue() == -1) {
                        child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                    } else if (((Integer) child.getTag()).intValue() == -2) {
                        int x = getPaddingLeft();
                        child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                    } else {
                        child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                    }
                }
            }
        };
        buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        linearLayout.addView(buttonsLayout, LayoutHelper.createLinear(-1, 52));
        TextView textView = new TextView(this.parentActivity);
        textView.setMinWidth(AndroidUtilities.dp(64.0f));
        textView.setTag(-1);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-11944718));
        textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
        textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoViewer.this.m4234lambda$setParentActivity$27$orgtelegramuiPhotoViewer(numberPicker, bottomSheet, view);
            }
        });
        TextView textView2 = new TextView(this.parentActivity);
        textView2.setMinWidth(AndroidUtilities.dp(64.0f));
        textView2.setTag(-2);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(-1);
        textView2.setGravity(17);
        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView2.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        textView2.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-1));
        textView2.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        buttonsLayout.addView(textView2, LayoutHelper.createFrame(-2, 36, 53));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BottomSheet.this.dismiss();
            }
        });
        bottomSheet.setBackgroundColor(-16777216);
        bottomSheet.show();
        AndroidUtilities.setNavigationBarColor(bottomSheet.getWindow(), -16777216, false);
        AndroidUtilities.setLightNavigationBar(bottomSheet.getWindow(), false);
    }

    public static /* synthetic */ boolean lambda$setParentActivity$24(View v13, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ boolean lambda$setParentActivity$25(View v12, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ String lambda$setParentActivity$26(int value) {
        if (value == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
        }
        if (value >= 1 && value < 21) {
            return LocaleController.formatTTLString(value);
        }
        return LocaleController.formatTTLString((value - 16) * 5);
    }

    /* renamed from: lambda$setParentActivity$27$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4234lambda$setParentActivity$27$orgtelegramuiPhotoViewer(NumberPicker numberPicker, BottomSheet bottomSheet, View v1) {
        int seconds;
        int value = numberPicker.getValue();
        SharedPreferences preferences1 = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences1.edit();
        editor.putInt("self_destruct", value);
        editor.commit();
        bottomSheet.dismiss();
        if (value >= 0 && value < 21) {
            seconds = value;
        } else {
            seconds = (value - 16) * 5;
        }
        Object object1 = this.imagesArrLocals.get(this.currentIndex);
        if (object1 instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) object1).ttl = seconds;
        } else if (object1 instanceof MediaController.SearchImage) {
            ((MediaController.SearchImage) object1).ttl = seconds;
        }
        this.timeItem.setColorFilter(seconds != 0 ? new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY) : null);
        if (!this.checkImageView.isChecked()) {
            this.checkImageView.callOnClick();
        }
    }

    /* renamed from: lambda$setParentActivity$31$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4237lambda$setParentActivity$31$orgtelegramuiPhotoViewer(View view) {
        if (this.imageMoveAnimation != null) {
            return;
        }
        Runnable onEnd = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4236lambda$setParentActivity$30$orgtelegramuiPhotoViewer();
            }
        };
        if (!this.previousHasTransform) {
            float backRotate = this.previousCropOrientation - this.photoCropView.cropView.getStateOrientation();
            if (Math.abs(backRotate) > 180.0f) {
                backRotate = backRotate < 0.0f ? 360.0f + backRotate : -(360.0f - backRotate);
            }
            cropRotate(backRotate, this.photoCropView.cropView.getStateMirror(), onEnd);
            return;
        }
        onEnd.run();
    }

    /* renamed from: lambda$setParentActivity$30$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4236lambda$setParentActivity$30$orgtelegramuiPhotoViewer() {
        this.cropTransform.setViewTransform(this.previousHasTransform, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, 1.0f, 1.0f, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f, this.previousCropMirrored);
        switchToEditMode(0);
    }

    /* renamed from: lambda$setParentActivity$32$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4238lambda$setParentActivity$32$orgtelegramuiPhotoViewer(View view) {
        if (this.currentEditMode == 1 && !this.photoCropView.isReady()) {
            return;
        }
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    /* renamed from: lambda$setParentActivity$34$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4240lambda$setParentActivity$34$orgtelegramuiPhotoViewer(View v) {
        float backRotate = -this.photoCropView.cropView.getStateOrientation();
        if (Math.abs(backRotate) > 180.0f) {
            backRotate = backRotate < 0.0f ? 360.0f + backRotate : -(360.0f - backRotate);
        }
        cropRotate(backRotate, this.photoCropView.cropView.getStateMirror(), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4239lambda$setParentActivity$33$orgtelegramuiPhotoViewer();
            }
        });
    }

    /* renamed from: lambda$setParentActivity$33$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4239lambda$setParentActivity$33$orgtelegramuiPhotoViewer() {
        this.photoCropView.reset(true);
    }

    /* renamed from: lambda$setParentActivity$35$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4241lambda$setParentActivity$35$orgtelegramuiPhotoViewer(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        PhotoViewerProvider photoViewerProvider;
        Bitmap bitmap;
        if (imageReceiver == this.centerImage && set && !thumb) {
            if (!this.isCurrentVideo && ((this.currentEditMode == 1 || this.sendPhotoType == 1) && this.photoCropView != null && (bitmap = imageReceiver.getBitmap()) != null)) {
                this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), this.sendPhotoType != 1, true, this.paintingOverlay, this.cropTransform, null, null);
            }
            if (this.paintingOverlay.getVisibility() == 0) {
                this.containerView.requestLayout();
            }
            detectFaces();
        }
        if (imageReceiver == this.centerImage && set && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.scaleToFill() && !this.ignoreDidSetImage && this.sendPhotoType != 1) {
            if (!this.wasLayout) {
                this.dontResetZoomOnFirstLayout = true;
            } else {
                setScaleToFill();
            }
        }
    }

    /* renamed from: lambda$setParentActivity$36$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4242lambda$setParentActivity$36$orgtelegramuiPhotoViewer(View v) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        setPhotoChecked();
    }

    /* renamed from: lambda$setParentActivity$37$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4243lambda$setParentActivity$37$orgtelegramuiPhotoViewer(View v) {
        PhotoViewerProvider photoViewerProvider;
        if (this.captionEditText.getTag() != null || (photoViewerProvider = this.placeProvider) == null || photoViewerProvider.getSelectedPhotosOrder() == null || this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
            return;
        }
        togglePhotosListView(!this.isPhotosListViewVisible, true);
    }

    /* renamed from: lambda$setParentActivity$38$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4244lambda$setParentActivity$38$orgtelegramuiPhotoViewer(View view, int position) {
        int i;
        if (!this.imagesArrLocals.isEmpty() && (i = this.currentIndex) >= 0 && i < this.imagesArrLocals.size()) {
            Object entry = this.imagesArrLocals.get(this.currentIndex);
            if (entry instanceof MediaController.MediaEditState) {
                ((MediaController.MediaEditState) entry).editedInfo = getCurrentVideoEditedInfo();
            }
        }
        this.ignoreDidSetImage = true;
        int idx = this.imagesArrLocals.indexOf(view.getTag());
        if (idx >= 0) {
            this.currentIndex = -1;
            setImageIndex(idx);
        }
        this.ignoreDidSetImage = false;
    }

    /* renamed from: lambda$setParentActivity$39$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4245lambda$setParentActivity$39$orgtelegramuiPhotoViewer(View view, int position) {
        Object object = this.mentionsAdapter.getItem(position);
        int start = this.mentionsAdapter.getResultStartPosition();
        int len = this.mentionsAdapter.getResultLength();
        if (!(object instanceof TLRPC.User)) {
            if (object instanceof String) {
                PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
                photoViewerCaptionEnterView.replaceWithText(start, len, object + " ", false);
                return;
            } else if (object instanceof MediaDataController.KeywordResult) {
                String code = ((MediaDataController.KeywordResult) object).emoji;
                this.captionEditText.addEmojiToRecent(code);
                this.captionEditText.replaceWithText(start, len, code, true);
                return;
            } else {
                return;
            }
        }
        TLRPC.User user = (TLRPC.User) object;
        if (user.username != null) {
            PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = this.captionEditText;
            photoViewerCaptionEnterView2.replaceWithText(start, len, "@" + user.username + " ", false);
            return;
        }
        String name = UserObject.getFirstName(user);
        Spannable spannable = new SpannableString(name + " ");
        spannable.setSpan(new URLSpanUserMentionPhotoViewer("" + user.id, true), 0, spannable.length(), 33);
        this.captionEditText.replaceWithText(start, len, spannable, false);
    }

    /* renamed from: lambda$setParentActivity$41$org-telegram-ui-PhotoViewer */
    public /* synthetic */ boolean m4248lambda$setParentActivity$41$orgtelegramuiPhotoViewer(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        Object object = this.mentionsAdapter.getItem(position);
        if (object instanceof String) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity, resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda77
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PhotoViewer.this.m4247lambda$setParentActivity$40$orgtelegramuiPhotoViewer(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showAlertDialog(builder);
            return true;
        }
        return false;
    }

    /* renamed from: lambda$setParentActivity$40$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4247lambda$setParentActivity$40$orgtelegramuiPhotoViewer(DialogInterface dialogInterface, int i) {
        this.mentionsAdapter.clearRecentHashtags();
    }

    public void animateNavBarColorTo(int color) {
        animateNavBarColorTo(color, true);
    }

    public void animateNavBarColorTo(final int color, boolean animated) {
        ValueAnimator valueAnimator = this.navBarAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final int fromColor = this.blackPaint.getColor();
        AndroidUtilities.setLightNavigationBar(this.windowView, ((double) AndroidUtilities.computePerceivedBrightness(color)) >= 0.721d);
        if (animated) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.navBarAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda44
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    PhotoViewer.this.m4194lambda$animateNavBarColorTo$42$orgtelegramuiPhotoViewer(fromColor, color, valueAnimator2);
                }
            });
            this.navBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.35
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.blackPaint.setColor(color);
                    PhotoViewer.this.windowView.invalidate();
                }
            });
            this.navBarAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.navBarAnimator.setDuration(200L);
            this.navBarAnimator.start();
            return;
        }
        this.navBarAnimator = null;
        this.blackPaint.setColor(color);
        this.windowView.invalidate();
    }

    /* renamed from: lambda$animateNavBarColorTo$42$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4194lambda$animateNavBarColorTo$42$orgtelegramuiPhotoViewer(int fromColor, int toColor, ValueAnimator a) {
        this.blackPaint.setColor(ColorUtils.blendARGB(fromColor, toColor, ((Float) a.getAnimatedValue()).floatValue()));
        this.windowView.invalidate();
    }

    private void showScheduleDatePickerDialog() {
        if (this.parentChatActivity == null) {
            return;
        }
        AlertsCreator.ScheduleDatePickerColors colors = new AlertsCreator.ScheduleDatePickerColors(-1, -14342875, -1, 520093695, -1, -115203550, 620756991);
        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentChatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda71
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i) {
                PhotoViewer.this.sendPressed(z, i);
            }
        }, colors);
    }

    public void sendPressed(boolean notify, int scheduleDate) {
        sendPressed(notify, scheduleDate, false, false);
    }

    private void replacePressed() {
        sendPressed(false, 0, true, false);
    }

    private void sendPressed(boolean notify, int scheduleDate, boolean replace, boolean forceDocument) {
        int i;
        ChatActivity chatActivity;
        if (this.captionEditText.getTag() == null && this.placeProvider != null && !this.doneButtonPressed) {
            if (this.sendPhotoType == 1) {
                applyCurrentEditMode();
            }
            if (!replace && (chatActivity = this.parentChatActivity) != null) {
                TLRPC.Chat chat = chatActivity.getCurrentChat();
                TLRPC.User user = this.parentChatActivity.getCurrentUser();
                if (user != null || ((ChatObject.isChannel(chat) && chat.megagroup) || !ChatObject.isChannel(chat))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + this.parentChatActivity.getDialogId(), !notify).commit();
                }
            }
            VideoEditedInfo videoEditedInfo = getCurrentVideoEditedInfo();
            if (!this.imagesArrLocals.isEmpty() && (i = this.currentIndex) >= 0 && i < this.imagesArrLocals.size()) {
                Object entry = this.imagesArrLocals.get(this.currentIndex);
                if (entry instanceof MediaController.MediaEditState) {
                    ((MediaController.MediaEditState) entry).editedInfo = videoEditedInfo;
                }
            }
            this.doneButtonPressed = true;
            if (!replace) {
                this.placeProvider.sendButtonPressed(this.currentIndex, videoEditedInfo, notify, scheduleDate, forceDocument);
            } else {
                this.placeProvider.replaceButtonPressed(this.currentIndex, videoEditedInfo);
            }
            closePhoto(false, false);
        }
    }

    public void showShareAlert(ArrayList<MessageObject> messages) {
        boolean openKeyboardOnShareAlertClose;
        FrameLayout photoContainerView = this.containerView;
        requestAdjustToNothing();
        boolean openKeyboardOnShareAlertClose2 = false;
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && chatActivity.getChatActivityEnterView() != null && this.parentChatActivity.getFragmentView() != null) {
            boolean keyboardVisible = this.parentChatActivity.getChatActivityEnterView().isKeyboardVisible();
            if (keyboardVisible) {
                this.parentChatActivity.getChatActivityEnterView().showEmojiView();
                openKeyboardOnShareAlertClose2 = true;
            }
            AndroidUtilities.setAdjustResizeToNothing(this.parentChatActivity.getParentActivity(), this.classGuid);
            this.parentChatActivity.getFragmentView().requestLayout();
            openKeyboardOnShareAlertClose = openKeyboardOnShareAlertClose2;
        } else {
            openKeyboardOnShareAlertClose = false;
        }
        boolean finalOpenKeyboardOnShareAlertClose = openKeyboardOnShareAlertClose;
        final ShareAlert alert = new AnonymousClass36(this.parentActivity, this.parentChatActivity, messages, null, null, false, null, null, false, true, null, photoContainerView, finalOpenKeyboardOnShareAlertClose);
        alert.setFocusable(false);
        alert.getWindow().setSoftInputMode(48);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4256lambda$showShareAlert$43$orgtelegramuiPhotoViewer(alert);
            }
        }, 250L);
        alert.show();
    }

    /* renamed from: org.telegram.ui.PhotoViewer$36 */
    /* loaded from: classes4.dex */
    public class AnonymousClass36 extends ShareAlert {
        final /* synthetic */ boolean val$finalOpenKeyboardOnShareAlertClose;
        final /* synthetic */ FrameLayout val$photoContainerView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass36(Context context, ChatActivity fragment, ArrayList arrayList, String text, String text2, boolean channel, String copyLink, String copyLink2, boolean fullScreen, boolean forCall, Theme.ResourcesProvider resourcesProvider, FrameLayout frameLayout, boolean z) {
            super(context, fragment, arrayList, text, text2, channel, copyLink, copyLink2, fullScreen, forCall, resourcesProvider);
            PhotoViewer.this = this$0;
            this.val$photoContainerView = frameLayout;
            this.val$finalOpenKeyboardOnShareAlertClose = z;
        }

        @Override // org.telegram.ui.Components.ShareAlert
        protected void onSend(final LongSparseArray<TLRPC.Dialog> dids, final int count) {
            final FrameLayout frameLayout = this.val$photoContainerView;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$36$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass36.this.m4275lambda$onSend$0$orgtelegramuiPhotoViewer$36(frameLayout, dids, count);
                }
            }, 250L);
        }

        /* renamed from: lambda$onSend$0$org-telegram-ui-PhotoViewer$36 */
        public /* synthetic */ void m4275lambda$onSend$0$orgtelegramuiPhotoViewer$36(FrameLayout photoContainerView, LongSparseArray dids, int count) {
            BulletinFactory.createForwardedBulletin(PhotoViewer.this.parentActivity, photoContainerView, dids.size(), dids.size() == 1 ? ((TLRPC.Dialog) dids.valueAt(0)).id : 0L, count, -115203550, -1).show();
        }

        @Override // org.telegram.ui.Components.ShareAlert, org.telegram.ui.ActionBar.BottomSheet
        public void dismissInternal() {
            super.dismissInternal();
            if (this.val$finalOpenKeyboardOnShareAlertClose) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$36$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass36.this.m4274lambda$dismissInternal$1$orgtelegramuiPhotoViewer$36();
                    }
                }, 50L);
            }
            PhotoViewer.this.requestAdjust();
        }

        /* renamed from: lambda$dismissInternal$1$org-telegram-ui-PhotoViewer$36 */
        public /* synthetic */ void m4274lambda$dismissInternal$1$orgtelegramuiPhotoViewer$36() {
            if (PhotoViewer.this.parentChatActivity != null && PhotoViewer.this.parentChatActivity.getChatActivityEnterView() != null) {
                PhotoViewer.this.parentChatActivity.getChatActivityEnterView().openKeyboard();
            }
        }
    }

    /* renamed from: lambda$showShareAlert$43$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4256lambda$showShareAlert$43$orgtelegramuiPhotoViewer(ShareAlert alert) {
        if (alert != null && alert.getWindow() != null) {
            alert.setFocusable(true);
            ChatActivity chatActivity = this.parentChatActivity;
            if (chatActivity != null && chatActivity.getChatActivityEnterView() != null) {
                this.parentChatActivity.getChatActivityEnterView().hidePopup(false);
            }
        }
    }

    public void setMenuItemIcon() {
        if (this.speedItem.getVisibility() != 0) {
            this.menuItem.setIcon(R.drawable.ic_ab_other);
            return;
        }
        if (Math.abs(this.currentVideoSpeed - 0.25f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.msg_more_0_2);
            this.speedItem.setSubtext(LocaleController.getString("SpeedVerySlow", R.string.SpeedVerySlow));
        } else if (Math.abs(this.currentVideoSpeed - 0.5f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.msg_more_0_5);
            this.speedItem.setSubtext(LocaleController.getString("SpeedSlow", R.string.SpeedSlow));
        } else if (Math.abs(this.currentVideoSpeed - 1.0f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.ic_ab_other);
            this.speedItem.setSubtext(LocaleController.getString("SpeedNormal", R.string.SpeedNormal));
        } else if (Math.abs(this.currentVideoSpeed - 1.5f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.msg_more_1_5);
            this.speedItem.setSubtext(LocaleController.getString("SpeedFast", R.string.SpeedFast));
        } else {
            this.menuItem.setIcon(R.drawable.msg_more_2);
            this.speedItem.setSubtext(LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast));
        }
        this.chooseSpeedLayout.update(this.currentVideoSpeed);
    }

    public float getCurrentVideoSpeed() {
        return this.currentVideoSpeed;
    }

    private boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, null).show();
        return false;
    }

    public void captureCurrentFrame() {
        TextureView textureView;
        if (this.captureFrameAtTime == -1 || (textureView = this.videoTextureView) == null) {
            return;
        }
        this.captureFrameAtTime = -1L;
        final Bitmap bitmap = textureView.getBitmap();
        this.flashView.animate().alpha(1.0f).setInterpolator(CubicBezierInterpolator.EASE_BOTH).setDuration(85L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.37
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PhotoViewer.this.photoCropView.setVideoThumb(bitmap, 0);
                PhotoViewer.this.flashAnimator = new AnimatorSet();
                PhotoViewer.this.flashAnimator.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.flashView, PhotoViewer.this.FLASH_VIEW_VALUE, 0.0f));
                PhotoViewer.this.flashAnimator.setDuration(85L);
                PhotoViewer.this.flashAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                PhotoViewer.this.flashAnimator.addListener(new AnonymousClass1());
                PhotoViewer.this.flashAnimator.start();
            }

            /* renamed from: org.telegram.ui.PhotoViewer$37$1 */
            /* loaded from: classes4.dex */
            public class AnonymousClass1 extends AnimatorListenerAdapter {
                AnonymousClass1() {
                    AnonymousClass37.this = this$1;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PhotoViewer.this.flashAnimator == null) {
                        return;
                    }
                    AndroidUtilities.runOnUIThread(PhotoViewer.this.videoPlayRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$37$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.AnonymousClass37.AnonymousClass1.this.m4276lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$37$1();
                        }
                    }, 860L);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-PhotoViewer$37$1 */
                public /* synthetic */ void m4276lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$37$1() {
                    PhotoViewer.this.manuallyPaused = false;
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.play();
                    }
                    PhotoViewer.this.videoPlayRunnable = null;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    PhotoViewer.this.flashAnimator = null;
                }
            }
        }).start();
    }

    /* renamed from: org.telegram.ui.PhotoViewer$38 */
    /* loaded from: classes4.dex */
    public class AnonymousClass38 extends SpoilersTextView {
        private LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector(this);
        private LinkSpanDrawable<ClickableSpan> pressedLink;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass38(Context context) {
            super(context);
            PhotoViewer.this = this$0;
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            boolean linkResult = false;
            if (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1)) {
                int x = (int) (event.getX() - getPaddingLeft());
                int y = (int) (event.getY() - getPaddingTop());
                int line = getLayout().getLineForVertical(y);
                int off = getLayout().getOffsetForHorizontal(line, x);
                float left = getLayout().getLineLeft(line);
                ClickableSpan touchLink = null;
                if (left <= x && getLayout().getLineWidth(line) + left >= x && y >= 0 && y <= getLayout().getHeight()) {
                    Spannable buffer = new SpannableString(getText());
                    ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                    if (link.length != 0) {
                        touchLink = link[0];
                        if (event.getAction() == 0) {
                            this.links.clear();
                            LinkSpanDrawable<ClickableSpan> linkSpanDrawable = new LinkSpanDrawable<>(link[0], null, event.getX(), event.getY());
                            this.pressedLink = linkSpanDrawable;
                            linkSpanDrawable.setColor(1717742051);
                            this.links.addLink(this.pressedLink);
                            int start = buffer.getSpanStart(this.pressedLink.getSpan());
                            int end = buffer.getSpanEnd(this.pressedLink.getSpan());
                            LinkPath path = this.pressedLink.obtainNewPath();
                            path.setCurrentLayout(getLayout(), start, getPaddingTop());
                            getLayout().getSelectionPath(start, end, path);
                            final LinkSpanDrawable<ClickableSpan> savedPressedLink = this.pressedLink;
                            postDelayed(new Runnable() { // from class: org.telegram.ui.PhotoViewer$38$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    PhotoViewer.AnonymousClass38.this.m4278lambda$onTouchEvent$1$orgtelegramuiPhotoViewer$38(savedPressedLink);
                                }
                            }, ViewConfiguration.getLongPressTimeout());
                            linkResult = true;
                        }
                    }
                }
                if (event.getAction() == 1) {
                    this.links.clear();
                    LinkSpanDrawable<ClickableSpan> linkSpanDrawable2 = this.pressedLink;
                    if (linkSpanDrawable2 != null && linkSpanDrawable2.getSpan() == touchLink) {
                        PhotoViewer.this.onLinkClick(this.pressedLink.getSpan(), this);
                    }
                    this.pressedLink = null;
                    linkResult = true;
                }
            } else if (event.getAction() == 3) {
                this.links.clear();
                this.pressedLink = null;
                linkResult = true;
            }
            boolean b = linkResult || super.onTouchEvent(event);
            return PhotoViewer.this.bottomTouchEnabled && b;
        }

        /* renamed from: lambda$onTouchEvent$1$org-telegram-ui-PhotoViewer$38 */
        public /* synthetic */ void m4278lambda$onTouchEvent$1$orgtelegramuiPhotoViewer$38(LinkSpanDrawable savedPressedLink) {
            LinkSpanDrawable<ClickableSpan> linkSpanDrawable = this.pressedLink;
            if (savedPressedLink == linkSpanDrawable && linkSpanDrawable != null && (linkSpanDrawable.getSpan() instanceof URLSpan)) {
                PhotoViewer.this.onLinkLongPress((URLSpan) this.pressedLink.getSpan(), this, new Runnable() { // from class: org.telegram.ui.PhotoViewer$38$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass38.this.m4277lambda$onTouchEvent$0$orgtelegramuiPhotoViewer$38();
                    }
                });
                this.pressedLink = null;
            }
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-PhotoViewer$38 */
        public /* synthetic */ void m4277lambda$onTouchEvent$0$orgtelegramuiPhotoViewer$38() {
            this.links.clear();
        }

        @Override // android.view.View
        public void setPressed(boolean pressed) {
            boolean needsRefresh = pressed != isPressed();
            super.setPressed(pressed);
            if (needsRefresh) {
                invalidate();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.spoilers.SpoilersTextView, android.widget.TextView, android.view.View
        public void onDraw(Canvas canvas) {
            canvas.save();
            canvas.translate(getPaddingLeft(), 0.0f);
            if (this.links.draw(canvas)) {
                invalidate();
            }
            canvas.restore();
            super.onDraw(canvas);
        }
    }

    /* renamed from: createCaptionTextView */
    public TextView m4250lambda$setParentActivity$6$orgtelegramuiPhotoViewer() {
        TextView textView = new AnonymousClass38(this.activityContext);
        ViewHelper.setPadding(textView, 16.0f, 8.0f, 16.0f, 8.0f);
        textView.setLinkTextColor(-8796932);
        textView.setTextColor(-1);
        textView.setHighlightColor(872415231);
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart() | 16);
        textView.setTextSize(1, 16.0f);
        textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoViewer.this.m4202lambda$createCaptionTextView$44$orgtelegramuiPhotoViewer(view);
            }
        });
        return textView;
    }

    /* renamed from: lambda$createCaptionTextView$44$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4202lambda$createCaptionTextView$44$orgtelegramuiPhotoViewer(View v) {
        if (!this.needCaptionLayout) {
            return;
        }
        openCaptionEnter();
    }

    public int getLeftInset() {
        if (this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
            return ((WindowInsets) this.lastInsets).getSystemWindowInsetLeft();
        }
        return 0;
    }

    public int getRightInset() {
        if (this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
            return ((WindowInsets) this.lastInsets).getSystemWindowInsetRight();
        }
        return 0;
    }

    public void dismissInternal() {
        try {
            if (this.windowView.getParent() != null) {
                Activity activity = this.parentActivity;
                if (activity instanceof LaunchActivity) {
                    ((LaunchActivity) activity).drawerLayoutContainer.setAllowDrawContent(true);
                }
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                wm.removeView(this.windowView);
                onHideView();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void switchToPip(boolean fromGesture) {
        CubicBezierInterpolator interpolator;
        if (this.videoPlayer == null || !this.textureUploaded || !checkInlinePermissions() || this.changingTextureView || this.switchingInlineMode || this.isInline) {
            return;
        }
        if (PipInstance != null) {
            PipInstance.destroyPhotoViewer();
        }
        this.openedFullScreenVideo = false;
        PipInstance = Instance;
        Instance = null;
        this.switchingInlineMode = true;
        this.isVisible = false;
        AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
        PlaceProviderObject placeProviderObject = this.currentPlaceObject;
        if (placeProviderObject != null && !placeProviderObject.imageReceiver.getVisible()) {
            this.currentPlaceObject.imageReceiver.setVisible(true, true);
            AnimatedFileDrawable animation = this.currentPlaceObject.imageReceiver.getAnimation();
            if (animation != null) {
                Bitmap bitmap = animation.getAnimatedBitmap();
                if (bitmap != null) {
                    try {
                        Bitmap src = this.videoTextureView.getBitmap(bitmap.getWidth(), bitmap.getHeight());
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawBitmap(src, 0.0f, 0.0f, (Paint) null);
                        src.recycle();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                animation.seekTo(this.videoPlayer.getCurrentPosition(), true);
                if (fromGesture) {
                    this.currentPlaceObject.imageReceiver.setAlpha(0.0f);
                    final ImageReceiver imageReceiver = this.currentPlaceObject.imageReceiver;
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            ImageReceiver.this.setAlpha(((Float) valueAnimator2.getAnimatedValue()).floatValue());
                        }
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.39
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation2) {
                            imageReceiver.setAlpha(1.0f);
                        }
                    });
                    valueAnimator.setDuration(250L);
                    valueAnimator.start();
                }
                this.currentPlaceObject.imageReceiver.setAllowStartAnimation(true);
                this.currentPlaceObject.imageReceiver.startAnimation();
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.pipAnimationInProgress = true;
            org.telegram.ui.Components.Rect rect = PipVideoOverlay.getPipRect(true, this.aspectRatioFrameLayout.getAspectRatio());
            final float scale = rect.width / this.videoTextureView.getWidth();
            final ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            final float fromX = this.videoTextureView.getTranslationX();
            final float fromY = this.videoTextureView.getTranslationY() + this.translationY;
            final float fromY2 = this.textureImageView.getTranslationY() + this.translationY;
            final float f = rect.x;
            final float toX2 = (rect.x - this.aspectRatioFrameLayout.getX()) + getLeftInset();
            final float toY = rect.y;
            final float toY2 = rect.y - this.aspectRatioFrameLayout.getY();
            this.textureImageView.setTranslationY(fromY2);
            this.videoTextureView.setTranslationY(fromY);
            FirstFrameView firstFrameView = this.firstFrameView;
            if (firstFrameView != null) {
                firstFrameView.setTranslationY(fromY);
            }
            this.translationY = 0.0f;
            this.containerView.invalidate();
            if (fromGesture) {
                if (fromY < toY2) {
                    interpolator = new CubicBezierInterpolator(0.5d, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 0.9d, 0.9d);
                } else {
                    interpolator = new CubicBezierInterpolator((double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 0.5d, 0.9d, 0.9d);
                }
            } else {
                interpolator = null;
            }
            ViewOutlineProvider outlineProvider = new ViewOutlineProvider() { // from class: org.telegram.ui.PhotoViewer.40
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), ((Float) valueAnimator2.getAnimatedValue()).floatValue() * AndroidUtilities.dp(10.0f) * (1.0f / scale));
                }
            };
            this.videoTextureView.setOutlineProvider(outlineProvider);
            this.videoTextureView.setClipToOutline(true);
            this.textureImageView.setOutlineProvider(outlineProvider);
            this.textureImageView.setClipToOutline(true);
            FirstFrameView firstFrameView2 = this.firstFrameView;
            if (firstFrameView2 != null) {
                firstFrameView2.setOutlineProvider(outlineProvider);
                this.firstFrameView.setClipToOutline(true);
            }
            final CubicBezierInterpolator cubicBezierInterpolator = interpolator;
            valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda55
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    PhotoViewer.this.m4261lambda$switchToPip$46$orgtelegramuiPhotoViewer(cubicBezierInterpolator, fromX, f, fromY2, toY, toX2, fromY, toY2, valueAnimator3);
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_X, scale), ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_Y, scale), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_X, scale), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_Y, scale), ObjectAnimator.ofInt(this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0), valueAnimator2);
            if (fromGesture) {
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatorSet.setDuration(300L);
            } else {
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250L);
            }
            animatorSet.addListener(new AnonymousClass41());
            animatorSet.start();
            if (!fromGesture) {
                toggleActionBar(false, true, new ActionBarToggleParams().enableStatusBarAnimation(false).enableTranslationAnimation(false).animationDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION).animationInterpolator(new DecelerateInterpolator()));
            }
        } else {
            this.switchToInlineRunnable.run();
            dismissInternal();
        }
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null) {
            chatActivity.getFragmentView().invalidate();
        }
    }

    /* renamed from: lambda$switchToPip$46$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4261lambda$switchToPip$46$orgtelegramuiPhotoViewer(CubicBezierInterpolator interpolator, float fromX, float toX, float fromY2, float toY, float toX2, float fromY, float toY2, ValueAnimator animation) {
        float xValue = ((Float) animation.getAnimatedValue()).floatValue();
        float yValue = interpolator == null ? xValue : interpolator.getInterpolation(xValue);
        ImageView imageView = this.textureImageView;
        if (imageView != null) {
            imageView.setTranslationX(((1.0f - xValue) * fromX) + (toX * xValue));
            this.textureImageView.setTranslationY(((1.0f - yValue) * fromY2) + (toY * yValue));
            this.textureImageView.invalidateOutline();
        }
        TextureView textureView = this.videoTextureView;
        if (textureView != null) {
            textureView.setTranslationX(((1.0f - xValue) * fromX) + (toX2 * xValue));
            this.videoTextureView.setTranslationY(((1.0f - yValue) * fromY) + (toY2 * yValue));
            this.videoTextureView.invalidateOutline();
        }
        FirstFrameView firstFrameView = this.firstFrameView;
        if (firstFrameView != null) {
            firstFrameView.setTranslationX(this.videoTextureView.getTranslationX());
            this.firstFrameView.setTranslationY(this.videoTextureView.getTranslationY());
            this.firstFrameView.setScaleX(this.videoTextureView.getScaleX());
            this.firstFrameView.setScaleY(this.videoTextureView.getScaleY());
            this.firstFrameView.invalidateOutline();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$41 */
    /* loaded from: classes4.dex */
    public class AnonymousClass41 extends AnimatorListenerAdapter {
        AnonymousClass41() {
            PhotoViewer.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            PhotoViewer.this.pipAnimationInProgress = false;
            PhotoViewer.this.switchToInlineRunnable.run();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$41$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass41.this.m4279lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$41();
                }
            }, 100L);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-PhotoViewer$41 */
        public /* synthetic */ void m4279lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$41() {
            PhotoViewer.this.videoTextureView.setOutlineProvider(null);
            PhotoViewer.this.textureImageView.setOutlineProvider(null);
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.setOutlineProvider(null);
            }
        }
    }

    public boolean cropMirror() {
        if (this.imageMoveAnimation != null || this.photoCropView == null) {
            return false;
        }
        this.mirror = 0.0f;
        this.animateToMirror = 1.0f;
        this.animationStartTime = System.currentTimeMillis();
        AnimatorSet animatorSet = new AnimatorSet();
        this.imageMoveAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
        this.imageMoveAnimation.setDuration(250L);
        this.imageMoveAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.42
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PhotoViewer.this.imageMoveAnimation = null;
                if (PhotoViewer.this.photoCropView != null) {
                    if (PhotoViewer.this.photoCropView.mirror()) {
                        PhotoViewer.this.mirrorItem.setColorFilter(new PorterDuffColorFilter(PhotoViewer.this.getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
                    } else {
                        PhotoViewer.this.mirrorItem.setColorFilter((ColorFilter) null);
                    }
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.mirror = photoViewer.animateToMirror = 0.0f;
                    PhotoViewer.this.containerView.invalidate();
                }
            }
        });
        this.imageMoveAnimation.start();
        return !this.photoCropView.cropView.isMirrored();
    }

    public boolean cropRotate(float diff) {
        return cropRotate(diff, false, null);
    }

    private boolean cropRotate(final float diff, boolean restoreMirror, final Runnable onEnd) {
        PhotoCropView photoCropView;
        if (this.imageMoveAnimation == null && (photoCropView = this.photoCropView) != null) {
            photoCropView.cropView.maximize(true);
            this.rotate = 0.0f;
            this.animateToRotate = 0.0f + diff;
            if (restoreMirror) {
                this.mirror = 0.0f;
                this.animateToMirror = 1.0f;
            }
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            ImageReceiver imageReceiver = this.centerImage;
            if (imageReceiver != null) {
                int bitmapWidth = imageReceiver.getBitmapWidth();
                int bitmapHeight = this.centerImage.getBitmapHeight();
                if (Math.abs((((int) this.photoCropView.cropView.getStateOrientation()) / 90) % 2) == 1) {
                    bitmapWidth = bitmapHeight;
                    bitmapHeight = bitmapWidth;
                }
                if (this.editState.cropState != null) {
                    bitmapWidth = (int) (bitmapWidth * this.editState.cropState.cropPw);
                    bitmapHeight = (int) (bitmapHeight * this.editState.cropState.cropPh);
                }
                float oldScale = Math.min(getContainerViewWidth(1) / bitmapWidth, getContainerViewHeight(1) / bitmapHeight);
                float newScale = oldScale;
                if (Math.abs((diff / 90.0f) % 2.0f) == 1.0f) {
                    newScale = Math.min(getContainerViewWidth(1) / bitmapHeight, getContainerViewHeight(1) / bitmapWidth);
                }
                this.animateToScale = newScale / oldScale;
            }
            ValueAnimator areaRotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            final float wasRotation = this.photoCropView.wheelView.getRotation();
            areaRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda33
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewer.this.m4208lambda$cropRotate$47$orgtelegramuiPhotoViewer(diff, wasRotation, valueAnimator);
                }
            });
            this.imageMoveAnimation.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), areaRotateAnimator);
            this.imageMoveAnimation.setDuration(250L);
            this.imageMoveAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.43
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.rotate = photoViewer.animateToRotate = 0.0f;
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    photoViewer2.mirror = photoViewer2.animateToMirror = 0.0f;
                    PhotoViewer photoViewer3 = PhotoViewer.this;
                    photoViewer3.scale = photoViewer3.animateToScale = 1.0f;
                    PhotoViewer.this.containerView.invalidate();
                    PhotoViewer.this.photoCropView.cropView.areaView.setRotationScaleTranslation(0.0f, 1.0f, 0.0f, 0.0f);
                    PhotoViewer.this.photoCropView.wheelView.setRotated(false);
                    if (Math.abs(diff) > 0.0f) {
                        if (PhotoViewer.this.photoCropView.rotate(diff)) {
                            PhotoViewer.this.rotateItem.setColorFilter(new PorterDuffColorFilter(PhotoViewer.this.getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
                        } else {
                            PhotoViewer.this.rotateItem.setColorFilter((ColorFilter) null);
                        }
                    }
                    if (PhotoViewer.this.editState.cropState != null) {
                        MediaController.CropState cropState = PhotoViewer.this.editState.cropState;
                        PhotoViewer.this.editState.cropState.cropPy = 0.0f;
                        cropState.cropPx = 0.0f;
                        MediaController.CropState cropState2 = PhotoViewer.this.editState.cropState;
                        PhotoViewer.this.editState.cropState.cropPh = 1.0f;
                        cropState2.cropPw = 1.0f;
                    }
                    Runnable runnable = onEnd;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            this.imageMoveAnimation.start();
            return Math.abs(this.photoCropView.cropView.getStateOrientation() + diff) > 0.01f;
        }
        return false;
    }

    /* renamed from: lambda$cropRotate$47$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4208lambda$cropRotate$47$orgtelegramuiPhotoViewer(float diff, float wasRotation, ValueAnimator a) {
        float f = this.scale;
        this.photoCropView.cropView.areaView.setRotationScaleTranslation(((Float) a.getAnimatedValue()).floatValue() * diff, f + ((this.animateToScale - f) * this.animationValue), 0.0f, 0.0f);
        this.photoCropView.wheelView.setRotation(AndroidUtilities.lerp(wasRotation, 0.0f, ((Float) a.getAnimatedValue()).floatValue()), false);
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    public void exitFromPip() {
        TextureView textureView;
        if (!this.isInline) {
            return;
        }
        if (Instance != null) {
            Instance.closePhoto(false, true);
        }
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null) {
            photoViewerWebView.exitFromPip();
        }
        Instance = PipInstance;
        PipInstance = null;
        if (this.photoViewerWebView == null) {
            this.switchingInlineMode = true;
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
        }
        this.isInline = false;
        if (this.photoViewerWebView == null && (textureView = this.videoTextureView) != null) {
            if (textureView.getParent() != null) {
                ((ViewGroup) this.videoTextureView.getParent()).removeView(this.videoTextureView);
            }
            this.videoTextureView.setVisibility(4);
            this.aspectRatioFrameLayout.addView(this.videoTextureView);
        }
        if (ApplicationLoader.mainInterfacePaused) {
            try {
                this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (this.photoViewerWebView == null) {
            if (Build.VERSION.SDK_INT >= 21 && this.videoTextureView != null) {
                this.pipAnimationInProgress = true;
                org.telegram.ui.Components.Rect rect = PipVideoOverlay.getPipRect(false, this.aspectRatioFrameLayout.getAspectRatio());
                final float scale = rect.width / this.textureImageView.getLayoutParams().width;
                this.textureImageView.setScaleX(scale);
                this.textureImageView.setScaleY(scale);
                this.textureImageView.setTranslationX(rect.x);
                this.textureImageView.setTranslationY(rect.y);
                this.videoTextureView.setScaleX(scale);
                this.videoTextureView.setScaleY(scale);
                this.videoTextureView.setTranslationX(rect.x - this.aspectRatioFrameLayout.getX());
                this.videoTextureView.setTranslationY(rect.y - this.aspectRatioFrameLayout.getY());
                FirstFrameView firstFrameView = this.firstFrameView;
                if (firstFrameView != null) {
                    firstFrameView.setScaleX(scale);
                    this.firstFrameView.setScaleY(scale);
                    this.firstFrameView.setTranslationX(this.videoTextureView.getTranslationX());
                    this.firstFrameView.setTranslationY(this.videoTextureView.getTranslationY());
                }
                this.inlineOutAnimationProgress = 0.0f;
                ViewOutlineProvider outlineProvider = new ViewOutlineProvider() { // from class: org.telegram.ui.PhotoViewer.44
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f - PhotoViewer.this.inlineOutAnimationProgress) * AndroidUtilities.dp(10.0f) * (1.0f / scale));
                    }
                };
                this.videoTextureView.setOutlineProvider(outlineProvider);
                this.videoTextureView.setClipToOutline(true);
                this.textureImageView.setOutlineProvider(outlineProvider);
                this.textureImageView.setClipToOutline(true);
                FirstFrameView firstFrameView2 = this.firstFrameView;
                if (firstFrameView2 != null) {
                    firstFrameView2.setOutlineProvider(outlineProvider);
                    this.firstFrameView.setClipToOutline(true);
                }
            } else {
                PipVideoOverlay.dismiss(true);
            }
        }
        try {
            this.isVisible = true;
            WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
            wm.addView(this.windowView, this.windowLayoutParams);
            onShowView();
            PlaceProviderObject placeProviderObject = this.currentPlaceObject;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(false, false);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.waitingForDraw = 4;
        }
    }

    private void onShowView() {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            LaunchActivity launchActivity = (LaunchActivity) activity;
            launchActivity.addOnUserLeaveHintListener(this.onUserLeaveHintListener);
        }
    }

    private void onHideView() {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            LaunchActivity launchActivity = (LaunchActivity) activity;
            launchActivity.removeOnUserLeaveHintListener(this.onUserLeaveHintListener);
        }
    }

    public void onUserLeaveHint() {
        if (this.pipItem.getAlpha() != 1.0f || !AndroidUtilities.checkInlinePermissions(this.parentActivity) || PipVideoOverlay.isVisible()) {
            return;
        }
        if (this.isEmbedVideo) {
            PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
            if (photoViewerWebView != null && !photoViewerWebView.isInAppOnly() && this.photoViewerWebView.openInPip()) {
                this.pipVideoOverlayAnimateFlag = false;
                if (PipInstance != null) {
                    PipInstance.destroyPhotoViewer();
                }
                this.isInline = true;
                PipInstance = Instance;
                Instance = null;
                this.isVisible = false;
                PlaceProviderObject placeProviderObject = this.currentPlaceObject;
                if (placeProviderObject != null && !placeProviderObject.imageReceiver.getVisible()) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                }
                dismissInternal();
                return;
            }
            return;
        }
        this.pipVideoOverlayAnimateFlag = false;
        switchToPip(false);
    }

    public void updateVideoSeekPreviewPosition() {
        int x = (this.videoPlayerSeekbar.getThumbX() + AndroidUtilities.dp(2.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        int min = AndroidUtilities.dp(10.0f);
        int max = (this.videoPlayerControlFrameLayout.getMeasuredWidth() - AndroidUtilities.dp(10.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        if (x < min) {
            x = min;
        } else if (x >= max) {
            x = max;
        }
        this.videoPreviewFrame.setTranslationX(x);
    }

    public void showVideoSeekPreviewPosition(boolean show) {
        if (!show || this.videoPreviewFrame.getTag() == null) {
            if (!show && this.videoPreviewFrame.getTag() == null) {
                return;
            }
            if (show && !this.videoPreviewFrame.isReady()) {
                this.needShowOnReady = true;
                return;
            }
            AnimatorSet animatorSet = this.videoPreviewFrameAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.videoPreviewFrame.setTag(show ? 1 : null);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.videoPreviewFrameAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(videoSeekPreviewImage, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.videoPreviewFrameAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.45
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.videoPreviewFrameAnimation = null;
                }
            });
            this.videoPreviewFrameAnimation.setDuration(180L);
            this.videoPreviewFrameAnimation.start();
        }
    }

    private void createVideoControlsInterface() {
        VideoPlayerControlFrameLayout videoPlayerControlFrameLayout = new VideoPlayerControlFrameLayout(this.containerView.getContext());
        this.videoPlayerControlFrameLayout = videoPlayerControlFrameLayout;
        this.containerView.addView(videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, 48, 83));
        final VideoPlayerSeekBar.SeekBarDelegate seekBarDelegate = new VideoPlayerSeekBar.SeekBarDelegate() { // from class: org.telegram.ui.PhotoViewer.46
            @Override // org.telegram.ui.Components.VideoPlayerSeekBar.SeekBarDelegate
            public void onSeekBarDrag(float progress) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        progress = PhotoViewer.this.videoTimelineView.getLeftProgress() + ((PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * progress);
                    }
                    long duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration == C.TIME_UNSET) {
                        PhotoViewer.this.seekToProgressPending = progress;
                    } else {
                        PhotoViewer.this.videoPlayer.seekTo((int) (((float) duration) * progress));
                    }
                    PhotoViewer.this.showVideoSeekPreviewPosition(false);
                    PhotoViewer.this.needShowOnReady = false;
                }
            }

            @Override // org.telegram.ui.Components.VideoPlayerSeekBar.SeekBarDelegate
            public void onSeekBarContinuousDrag(float progress) {
                if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPreviewFrame != null) {
                    PhotoViewer.this.videoPreviewFrame.setProgress(progress, PhotoViewer.this.videoPlayerSeekbar.getWidth());
                }
                PhotoViewer.this.showVideoSeekPreviewPosition(true);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }
        };
        FloatSeekBarAccessibilityDelegate accessibilityDelegate = new FloatSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.PhotoViewer.47
            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                return PhotoViewer.this.videoPlayerSeekbar.getProgress();
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float progress) {
                seekBarDelegate.onSeekBarDrag(progress);
                PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
            }

            @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
            public String getContentDescription(View host) {
                String time = LocaleController.formatPluralString("Minutes", PhotoViewer.this.videoPlayerCurrentTime[0], new Object[0]) + ' ' + LocaleController.formatPluralString("Seconds", PhotoViewer.this.videoPlayerCurrentTime[1], new Object[0]);
                String totalTime = LocaleController.formatPluralString("Minutes", PhotoViewer.this.videoPlayerTotalTime[0], new Object[0]) + ' ' + LocaleController.formatPluralString("Seconds", PhotoViewer.this.videoPlayerTotalTime[1], new Object[0]);
                return LocaleController.formatString("AccDescrPlayerDuration", R.string.AccDescrPlayerDuration, time, totalTime);
            }
        };
        View view = new View(this.containerView.getContext()) { // from class: org.telegram.ui.PhotoViewer.48
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                PhotoViewer.this.videoPlayerSeekbar.draw(canvas, this);
            }
        };
        this.videoPlayerSeekbarView = view;
        view.setAccessibilityDelegate(accessibilityDelegate);
        this.videoPlayerSeekbarView.setImportantForAccessibility(1);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerSeekbarView, LayoutHelper.createFrame(-1, -1.0f));
        VideoPlayerSeekBar videoPlayerSeekBar = new VideoPlayerSeekBar(this.videoPlayerSeekbarView);
        this.videoPlayerSeekbar = videoPlayerSeekBar;
        videoPlayerSeekBar.setHorizontalPadding(AndroidUtilities.dp(2.0f));
        this.videoPlayerSeekbar.setColors(872415231, 872415231, -1, -1, -1, 1509949439);
        this.videoPlayerSeekbar.setDelegate(seekBarDelegate);
        VideoSeekPreviewImage videoSeekPreviewImage = new VideoSeekPreviewImage(this.containerView.getContext(), new VideoSeekPreviewImage.VideoSeekPreviewImageDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda78
            @Override // org.telegram.ui.Components.VideoSeekPreviewImage.VideoSeekPreviewImageDelegate
            public final void onReady() {
                PhotoViewer.this.m4206xfc42aa91();
            }
        }) { // from class: org.telegram.ui.PhotoViewer.49
            @Override // android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }

            @Override // android.view.View
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (visibility == 0) {
                    PhotoViewer.this.updateVideoSeekPreviewPosition();
                }
            }
        };
        this.videoPreviewFrame = videoSeekPreviewImage;
        videoSeekPreviewImage.setAlpha(0.0f);
        this.containerView.addView(this.videoPreviewFrame, LayoutHelper.createFrame(-2, -2.0f, 83, 0.0f, 0.0f, 0.0f, 58.0f));
        SimpleTextView simpleTextView = new SimpleTextView(this.containerView.getContext());
        this.videoPlayerTime = simpleTextView;
        simpleTextView.setTextColor(-1);
        this.videoPlayerTime.setGravity(53);
        this.videoPlayerTime.setTextSize(14);
        this.videoPlayerTime.setImportantForAccessibility(2);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 15.0f, 12.0f, 0.0f));
        ImageView imageView = new ImageView(this.containerView.getContext());
        this.exitFullscreenButton = imageView;
        imageView.setImageResource(R.drawable.msg_minvideo);
        this.exitFullscreenButton.setContentDescription(LocaleController.getString("AccExitFullscreen", R.string.AccExitFullscreen));
        this.exitFullscreenButton.setScaleType(ImageView.ScaleType.CENTER);
        this.exitFullscreenButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.exitFullscreenButton.setVisibility(4);
        this.videoPlayerControlFrameLayout.addView(this.exitFullscreenButton, LayoutHelper.createFrame(48, 48, 53));
        this.exitFullscreenButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.m4207x25928f12(view2);
            }
        });
    }

    /* renamed from: lambda$createVideoControlsInterface$48$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4206xfc42aa91() {
        if (this.needShowOnReady) {
            showVideoSeekPreviewPosition(true);
        }
    }

    /* renamed from: lambda$createVideoControlsInterface$49$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4207x25928f12(View v) {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return;
        }
        this.wasRotated = false;
        this.fullscreenedByButton = 2;
        if (this.prevOrientation == -10) {
            this.prevOrientation = activity.getRequestedOrientation();
        }
        this.parentActivity.setRequestedOrientation(1);
    }

    private void openCaptionEnter() {
        int i;
        if (this.imageMoveAnimation != null || this.changeModeAnimation != null || this.currentEditMode != 0 || (i = this.sendPhotoType) == 1 || i == 3 || i == 10) {
            return;
        }
        if (!this.windowView.isFocusable()) {
            makeFocusable();
        }
        this.keyboardAnimationEnabled = true;
        this.selectedPhotosListView.setEnabled(false);
        this.photosCounterView.setRotationX(0.0f);
        this.isPhotosListViewVisible = false;
        this.captionEditText.setTag(1);
        this.captionEditText.openKeyboard();
        this.captionEditText.setImportantForAccessibility(0);
        this.lastTitle = this.actionBar.getTitle();
        this.captionEditText.setVisibility(0);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:16:0x0075 -> B:20:0x0077). Please submit an issue!!! */
    private int[] fixVideoWidthHeight(int w, int h) {
        int[] result = {w, h};
        if (Build.VERSION.SDK_INT >= 21) {
            MediaCodec encoder = null;
            try {
                try {
                    encoder = MediaCodec.createEncoderByType("video/avc");
                    MediaCodecInfo.CodecCapabilities capabilities = encoder.getCodecInfo().getCapabilitiesForType("video/avc");
                    MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                    Range<Integer> widths = videoCapabilities.getSupportedWidths();
                    Range<Integer> heights = videoCapabilities.getSupportedHeights();
                    result[0] = Math.max(widths.getLower().intValue(), Math.round(w / 16.0f) * 16);
                    result[1] = Math.max(heights.getLower().intValue(), Math.round(h / 16.0f) * 16);
                    if (encoder != null) {
                        encoder.release();
                    }
                } catch (Exception e) {
                    if (encoder != null) {
                        encoder.release();
                    }
                } catch (Throwable th) {
                    if (encoder != null) {
                        try {
                            encoder.release();
                        } catch (Exception e2) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e3) {
            }
        }
        return result;
    }

    public VideoEditedInfo getCurrentVideoEditedInfo() {
        int i = -1;
        boolean z = false;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = null;
        if (!this.isCurrentVideo && hasAnimatedMediaEntities() && this.centerImage.getBitmapWidth() > 0) {
            float maxSize = this.sendPhotoType == 1 ? 800.0f : 854.0f;
            VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
            videoEditedInfo.startTime = 0L;
            videoEditedInfo.start = (float) 0;
            videoEditedInfo.endTime = Math.min(3000L, this.editState.averageDuration);
            while (videoEditedInfo.endTime > 0 && videoEditedInfo.endTime < 1000) {
                videoEditedInfo.endTime *= 2;
            }
            videoEditedInfo.end = (float) videoEditedInfo.endTime;
            videoEditedInfo.rotationValue = 0;
            videoEditedInfo.originalPath = this.currentImagePath;
            videoEditedInfo.estimatedSize = (int) ((((float) videoEditedInfo.endTime) / 1000.0f) * 115200.0f);
            videoEditedInfo.estimatedDuration = videoEditedInfo.endTime;
            videoEditedInfo.framerate = 30;
            videoEditedInfo.originalDuration = videoEditedInfo.endTime;
            videoEditedInfo.filterState = this.editState.savedFilterState;
            if (this.editState.croppedPaintPath != null) {
                videoEditedInfo.paintPath = this.editState.croppedPaintPath;
                if (this.editState.croppedMediaEntities != null && !this.editState.croppedMediaEntities.isEmpty()) {
                    arrayList = this.editState.croppedMediaEntities;
                }
                videoEditedInfo.mediaEntities = arrayList;
            } else {
                videoEditedInfo.paintPath = this.editState.paintPath;
                videoEditedInfo.mediaEntities = this.editState.mediaEntities;
            }
            videoEditedInfo.isPhoto = true;
            int width = this.centerImage.getBitmapWidth();
            int height = this.centerImage.getBitmapHeight();
            if (this.editState.cropState != null) {
                if (this.editState.cropState.transformRotation == 90 || this.editState.cropState.transformRotation == 270) {
                    width = height;
                    height = width;
                }
                width = (int) (width * this.editState.cropState.cropPw);
                height = (int) (height * this.editState.cropState.cropPh);
            }
            if (this.sendPhotoType == 1) {
                width = height;
            }
            float scale = Math.max(width / maxSize, height / maxSize);
            if (scale < 1.0f) {
                scale = 1.0f;
            }
            int width2 = (int) (width / scale);
            int height2 = (int) (height / scale);
            if (width2 % 16 != 0) {
                width2 = Math.max(1, Math.round(width2 / 16.0f)) * 16;
            }
            if (height2 % 16 != 0) {
                height2 = Math.max(1, Math.round(height2 / 16.0f)) * 16;
            }
            videoEditedInfo.resultWidth = width2;
            videoEditedInfo.originalWidth = width2;
            videoEditedInfo.resultHeight = height2;
            videoEditedInfo.originalHeight = height2;
            videoEditedInfo.bitrate = -1;
            videoEditedInfo.muted = true;
            videoEditedInfo.avatarStartTime = 0L;
            return videoEditedInfo;
        } else if (!this.isCurrentVideo || this.currentPlayingVideoFile == null || this.compressionsCount == 0) {
            return null;
        } else {
            VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
            videoEditedInfo2.startTime = this.startTime;
            videoEditedInfo2.endTime = this.endTime;
            videoEditedInfo2.start = this.videoCutStart;
            videoEditedInfo2.end = this.videoCutEnd;
            videoEditedInfo2.rotationValue = this.rotationValue;
            videoEditedInfo2.originalWidth = this.originalWidth;
            videoEditedInfo2.originalHeight = this.originalHeight;
            videoEditedInfo2.bitrate = this.bitrate;
            videoEditedInfo2.originalPath = this.currentPathObject;
            long j = this.estimatedSize;
            if (j == 0) {
                j = 1;
            }
            videoEditedInfo2.estimatedSize = j;
            videoEditedInfo2.estimatedDuration = this.estimatedDuration;
            videoEditedInfo2.framerate = this.videoFramerate;
            videoEditedInfo2.originalDuration = this.videoDuration * 1000.0f;
            videoEditedInfo2.filterState = this.editState.savedFilterState;
            if (this.editState.croppedPaintPath != null) {
                videoEditedInfo2.paintPath = this.editState.croppedPaintPath;
                if (this.editState.croppedMediaEntities != null && !this.editState.croppedMediaEntities.isEmpty()) {
                    arrayList = this.editState.croppedMediaEntities;
                }
                videoEditedInfo2.mediaEntities = arrayList;
            } else {
                videoEditedInfo2.paintPath = this.editState.paintPath;
                if (this.editState.mediaEntities != null && !this.editState.mediaEntities.isEmpty()) {
                    arrayList = this.editState.mediaEntities;
                }
                videoEditedInfo2.mediaEntities = arrayList;
            }
            if (this.sendPhotoType != 1 && !this.muteVideo && (this.compressItem.getTag() == null || (videoEditedInfo2.resultWidth == this.originalWidth && videoEditedInfo2.resultHeight == this.originalHeight))) {
                videoEditedInfo2.resultWidth = this.originalWidth;
                videoEditedInfo2.resultHeight = this.originalHeight;
                if (!this.muteVideo) {
                    i = this.originalBitrate;
                }
                videoEditedInfo2.bitrate = i;
            } else {
                if (this.muteVideo || this.sendPhotoType == 1) {
                    this.selectedCompression = 1;
                    updateWidthHeightBitrateForCompression();
                }
                videoEditedInfo2.resultWidth = this.resultWidth;
                videoEditedInfo2.resultHeight = this.resultHeight;
                if (!this.muteVideo && this.sendPhotoType != 1) {
                    i = this.bitrate;
                }
                videoEditedInfo2.bitrate = i;
            }
            videoEditedInfo2.cropState = this.editState.cropState;
            if (videoEditedInfo2.cropState != null) {
                videoEditedInfo2.rotationValue += videoEditedInfo2.cropState.transformRotation;
                while (videoEditedInfo2.rotationValue >= 360) {
                    videoEditedInfo2.rotationValue -= 360;
                }
                if (videoEditedInfo2.rotationValue == 90 || videoEditedInfo2.rotationValue == 270) {
                    videoEditedInfo2.cropState.transformWidth = (int) (videoEditedInfo2.resultWidth * videoEditedInfo2.cropState.cropPh);
                    videoEditedInfo2.cropState.transformHeight = (int) (videoEditedInfo2.resultHeight * videoEditedInfo2.cropState.cropPw);
                } else {
                    videoEditedInfo2.cropState.transformWidth = (int) (videoEditedInfo2.resultWidth * videoEditedInfo2.cropState.cropPw);
                    videoEditedInfo2.cropState.transformHeight = (int) (videoEditedInfo2.resultHeight * videoEditedInfo2.cropState.cropPh);
                }
                if (this.sendPhotoType == 1) {
                    if (videoEditedInfo2.cropState.transformWidth > 800) {
                        videoEditedInfo2.cropState.transformWidth = 800;
                    }
                    if (videoEditedInfo2.cropState.transformHeight > 800) {
                        videoEditedInfo2.cropState.transformHeight = 800;
                    }
                    MediaController.CropState cropState = videoEditedInfo2.cropState;
                    MediaController.CropState cropState2 = videoEditedInfo2.cropState;
                    int min = Math.min(videoEditedInfo2.cropState.transformWidth, videoEditedInfo2.cropState.transformHeight);
                    cropState2.transformHeight = min;
                    cropState.transformWidth = min;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("original transformed w = " + videoEditedInfo2.cropState.transformWidth + " h = " + videoEditedInfo2.cropState.transformHeight + " r = " + videoEditedInfo2.rotationValue);
                }
                int[] fixedSize = fixVideoWidthHeight(videoEditedInfo2.cropState.transformWidth, videoEditedInfo2.cropState.transformHeight);
                videoEditedInfo2.cropState.transformWidth = fixedSize[0];
                videoEditedInfo2.cropState.transformHeight = fixedSize[1];
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("fixed transformed w = " + videoEditedInfo2.cropState.transformWidth + " h = " + videoEditedInfo2.cropState.transformHeight);
                }
            }
            if (this.sendPhotoType == 1) {
                videoEditedInfo2.avatarStartTime = this.avatarStartTime;
                videoEditedInfo2.originalBitrate = this.originalBitrate;
            }
            if (this.muteVideo || this.sendPhotoType == 1) {
                z = true;
            }
            videoEditedInfo2.muted = z;
            return videoEditedInfo2;
        }
    }

    private boolean supportsSendingNewEntities() {
        ChatActivity chatActivity = this.parentChatActivity;
        return chatActivity != null && (chatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 101);
    }

    public void closeCaptionEnter(boolean apply) {
        int i = this.currentIndex;
        if (i < 0 || i >= this.imagesArrLocals.size() || this.captionEditText.getTag() == null) {
            return;
        }
        Object object = this.imagesArrLocals.get(this.currentIndex);
        if (apply) {
            CharSequence caption = this.captionEditText.getFieldCharSequence();
            CharSequence[] result = {caption};
            if (this.hasCaptionForAllMedia && !TextUtils.equals(this.captionForAllMedia, caption) && this.placeProvider.getPhotoIndex(this.currentIndex) != 0 && this.placeProvider.getSelectedCount() > 0) {
                this.hasCaptionForAllMedia = false;
            }
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(result, supportsSendingNewEntities());
            this.captionForAllMedia = caption;
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                photoEntry.caption = result[0];
                photoEntry.entities = entities;
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) object;
                photoEntry2.caption = result[0];
                photoEntry2.entities = entities;
            }
            if (this.captionEditText.getFieldCharSequence().length() != 0 && !this.placeProvider.isPhotoChecked(this.currentIndex)) {
                setPhotoChecked();
            }
            PhotoViewerProvider photoViewerProvider = this.placeProvider;
            if (photoViewerProvider != null) {
                photoViewerProvider.onApplyCaption(caption);
            }
            setCurrentCaption(null, result[0], false);
        }
        this.captionEditText.setTag(null);
        if (this.isCurrentVideo) {
            this.actionBar.setTitleAnimated(this.lastTitle, false, 220L);
            this.actionBar.setSubtitle(this.muteVideo ? LocaleController.getString("SoundMuted", R.string.SoundMuted) : this.currentSubtitle);
        }
        updateCaptionTextForCurrentPhoto(object);
        if (this.captionEditText.isPopupShowing()) {
            this.captionEditText.hidePopup();
        }
        this.captionEditText.closeKeyboard();
        if (Build.VERSION.SDK_INT >= 19) {
            this.captionEditText.setImportantForAccessibility(4);
        }
    }

    public void updateVideoPlayerTime() {
        Arrays.fill(this.videoPlayerCurrentTime, 0);
        Arrays.fill(this.videoPlayerTotalTime, 0);
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            long current = Math.max(0L, videoPlayer.getCurrentPosition());
            if (this.shownControlsByEnd && !this.actionBarWasShownBeforeByEnd) {
                current = 0;
            }
            long total = Math.max(0L, this.videoPlayer.getDuration());
            if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                total = ((float) total) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress());
                current = ((float) current) - (this.videoTimelineView.getLeftProgress() * ((float) total));
                if (current > total) {
                    current = total;
                }
            }
            long current2 = current / 1000;
            long total2 = total / 1000;
            int[] iArr = this.videoPlayerCurrentTime;
            iArr[0] = (int) (current2 / 60);
            iArr[1] = (int) (current2 % 60);
            int[] iArr2 = this.videoPlayerTotalTime;
            iArr2[0] = (int) (total2 / 60);
            iArr2[1] = (int) (total2 % 60);
        }
        this.videoPlayerTime.setText(String.format(Locale.ROOT, "%02d:%02d / %02d:%02d", Integer.valueOf(this.videoPlayerCurrentTime[0]), Integer.valueOf(this.videoPlayerCurrentTime[1]), Integer.valueOf(this.videoPlayerTotalTime[0]), Integer.valueOf(this.videoPlayerTotalTime[1])));
    }

    private void checkBufferedProgress(float progress) {
        MessageObject messageObject;
        TLRPC.Document document;
        if (!this.isStreaming || this.parentActivity == null || this.streamingAlertShown || this.videoPlayer == null || (messageObject = this.currentMessageObject) == null || (document = messageObject.getDocument()) == null) {
            return;
        }
        int innerDuration = this.currentMessageObject.getDuration();
        if (innerDuration < 20 || progress >= 0.9f) {
            return;
        }
        if ((((float) document.size) * progress >= 5242880.0f || (progress >= 0.5f && document.size >= 2097152)) && Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
            long duration = this.videoPlayer.getDuration();
            if (duration == C.TIME_UNSET) {
                Toast toast = Toast.makeText(this.parentActivity, LocaleController.getString("VideoDoesNotSupportStreaming", R.string.VideoDoesNotSupportStreaming), 1);
                toast.show();
            }
            this.streamingAlertShown = true;
        }
    }

    public void updateColors() {
        int color = getThemedColor(Theme.key_dialogFloatingButton);
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            Drawable drawable = imageView.getBackground();
            Theme.setSelectorDrawableColor(drawable, color, false);
            Theme.setSelectorDrawableColor(drawable, getThemedColor(Build.VERSION.SDK_INT >= 21 ? Theme.key_dialogFloatingButtonPressed : Theme.key_dialogFloatingButton), true);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
        }
        CheckBox checkBox = this.checkImageView;
        if (checkBox != null) {
            checkBox.setColor(getThemedColor(Theme.key_dialogFloatingButton), -1);
        }
        PorterDuffColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        ImageView imageView2 = this.timeItem;
        if (imageView2 != null && imageView2.getColorFilter() != null) {
            this.timeItem.setColorFilter(filter);
        }
        ImageView imageView3 = this.paintItem;
        if (imageView3 != null && imageView3.getColorFilter() != null) {
            this.paintItem.setColorFilter(filter);
        }
        ImageView imageView4 = this.cropItem;
        if (imageView4 != null && imageView4.getColorFilter() != null) {
            this.cropItem.setColorFilter(filter);
        }
        ImageView imageView5 = this.tuneItem;
        if (imageView5 != null && imageView5.getColorFilter() != null) {
            this.tuneItem.setColorFilter(filter);
        }
        ImageView imageView6 = this.rotateItem;
        if (imageView6 != null && imageView6.getColorFilter() != null) {
            this.rotateItem.setColorFilter(filter);
        }
        ImageView imageView7 = this.mirrorItem;
        if (imageView7 != null && imageView7.getColorFilter() != null) {
            this.mirrorItem.setColorFilter(filter);
        }
        PickerBottomLayoutViewer pickerBottomLayoutViewer = this.editorDoneLayout;
        if (pickerBottomLayoutViewer != null) {
            pickerBottomLayoutViewer.doneButton.setTextColor(color);
        }
        PickerBottomLayoutViewer pickerBottomLayoutViewer2 = this.qualityPicker;
        if (pickerBottomLayoutViewer2 != null) {
            pickerBottomLayoutViewer2.doneButton.setTextColor(color);
        }
        PhotoPaintView photoPaintView = this.photoPaintView;
        if (photoPaintView != null) {
            photoPaintView.updateColors();
        }
        PhotoFilterView photoFilterView = this.photoFilterView;
        if (photoFilterView != null) {
            photoFilterView.updateColors();
        }
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.updateColors();
        }
        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
        if (videoTimelinePlayView != null) {
            videoTimelinePlayView.invalidate();
        }
        SelectedPhotosListView selectedPhotosListView = this.selectedPhotosListView;
        if (selectedPhotosListView != null) {
            int count = selectedPhotosListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.selectedPhotosListView.getChildAt(a);
                if (view instanceof PhotoPickerPhotoCell) {
                    ((PhotoPickerPhotoCell) view).updateColors();
                }
            }
        }
        StickersAlert stickersAlert = this.masksAlert;
        if (stickersAlert != null) {
            stickersAlert.updateColors(true);
        }
    }

    public void injectVideoPlayer(VideoPlayer player) {
        this.injectingVideoPlayer = player;
    }

    public void injectVideoPlayerSurface(SurfaceTexture surface) {
        this.injectingVideoPlayerSurface = surface;
    }

    public boolean isInjectingVideoPlayer() {
        return this.injectingVideoPlayer != null;
    }

    public void scheduleActionBarHide() {
        scheduleActionBarHide(3000);
    }

    private void scheduleActionBarHide(int delay) {
        if (!isAccessibilityEnabled()) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, delay);
        }
    }

    private boolean isAccessibilityEnabled() {
        try {
            AccessibilityManager am = (AccessibilityManager) this.activityContext.getSystemService("accessibility");
            if (!am.isEnabled()) {
                return false;
            }
            return am.isTouchExplorationEnabled();
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public void updatePlayerState(boolean playWhenReady, int playbackState) {
        MessageObject messageObject;
        if (this.videoPlayer == null) {
            return;
        }
        float f = 0.0f;
        if (this.isStreaming) {
            if (playbackState == 2 && this.skipFirstBufferingProgress) {
                if (playWhenReady) {
                    this.skipFirstBufferingProgress = false;
                }
            } else {
                boolean buffering = this.seekToProgressPending != 0.0f || playbackState == 2;
                if (buffering) {
                    AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
                } else {
                    scheduleActionBarHide();
                }
                toggleMiniProgress(buffering, true);
            }
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        int i = 4;
        if (aspectRatioFrameLayout != null) {
            aspectRatioFrameLayout.setKeepScreenOn((!playWhenReady || playbackState == 4 || playbackState == 1) ? false : true);
        }
        if (!playWhenReady || playbackState == 4 || playbackState == 1) {
            try {
                this.parentActivity.getWindow().clearFlags(128);
                this.keepScreenOnFlagSet = false;
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            try {
                this.parentActivity.getWindow().addFlags(128);
                this.keepScreenOnFlagSet = true;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        if (playbackState == 3 || playbackState == 1) {
            if (this.currentMessageObject != null) {
                this.videoPreviewFrame.open(this.videoPlayer.getCurrentUri());
            }
            if (this.seekToProgressPending != 0.0f) {
                int seekTo = (int) (((float) this.videoPlayer.getDuration()) * this.seekToProgressPending);
                this.videoPlayer.seekTo(seekTo);
                this.seekToProgressPending = 0.0f;
                MessageObject messageObject2 = this.currentMessageObject;
                if (messageObject2 != null && !FileLoader.getInstance(messageObject2.currentAccount).isLoadingVideoAny(this.currentMessageObject.getDocument())) {
                    this.skipFirstBufferingProgress = true;
                }
            }
        }
        if (playbackState == 3) {
            if (this.aspectRatioFrameLayout.getVisibility() != 0) {
                this.aspectRatioFrameLayout.setVisibility(0);
            }
            if (!this.pipItem.isEnabled() && this.pipItem.getVisibility() == 0) {
                this.pipAvailable = true;
                this.pipItem.setEnabled(true);
                this.pipItem.animate().alpha(1.0f).setDuration(175L).withEndAction(null).start();
            }
            this.playerWasReady = true;
            MessageObject messageObject3 = this.currentMessageObject;
            if (messageObject3 != null && messageObject3.isVideo()) {
                AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                FileLoader.getInstance(this.currentMessageObject.currentAccount).removeLoadingVideo(this.currentMessageObject.getDocument(), true, false);
            }
        } else if (playbackState == 2 && playWhenReady && (messageObject = this.currentMessageObject) != null && messageObject.isVideo()) {
            if (this.playerWasReady) {
                this.setLoadingRunnable.run();
            } else {
                AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000L);
            }
        }
        long j = 0;
        if (this.videoPlayer.isPlaying() && playbackState != 4) {
            if (!this.isPlaying) {
                this.isPlaying = true;
                PhotoProgressView photoProgressView = this.photoProgressViews[0];
                if (this.isCurrentVideo) {
                    i = -1;
                }
                photoProgressView.setBackgroundState(i, false, true);
                PhotoProgressView photoProgressView2 = this.photoProgressViews[0];
                if (this.isCurrentVideo || ((isAccessibilityEnabled() && !this.playerWasPlaying) || ((!this.playerAutoStarted || this.playerWasPlaying) && this.isActionBarVisible))) {
                    f = 1.0f;
                }
                photoProgressView2.setIndexedAlpha(1, f, false);
                this.playerWasPlaying = true;
                AndroidUtilities.runOnUIThread(this.updateProgressRunnable);
            }
        } else if (this.isPlaying || playbackState == 4) {
            if (this.currentEditMode != 3) {
                this.photoProgressViews[0].setIndexedAlpha(1, 1.0f, playbackState == 4);
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                photoProgressViewArr[0].setBackgroundState(3, false, photoProgressViewArr[0].animAlphas[1] > 0.0f);
            }
            this.isPlaying = false;
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
            if (playbackState == 4) {
                if (this.isCurrentVideo) {
                    if (!this.videoTimelineView.isDragging()) {
                        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
                        videoTimelinePlayView.setProgress(videoTimelinePlayView.getLeftProgress());
                        if (!this.inPreview && (this.currentEditMode != 0 || this.videoTimelineView.getVisibility() == 0)) {
                            this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration())));
                        } else {
                            this.videoPlayer.seekTo(0L);
                        }
                        this.manuallyPaused = false;
                        cancelVideoPlayRunnable();
                        if (this.sendPhotoType != 1 && this.currentEditMode == 0 && this.switchingToMode <= 0) {
                            this.videoPlayer.pause();
                        } else {
                            this.videoPlayer.play();
                        }
                        this.containerView.invalidate();
                    }
                } else {
                    this.videoPlayerSeekbar.setProgress(0.0f);
                    this.videoPlayerSeekbarView.invalidate();
                    if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                        this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration())));
                    } else {
                        this.videoPlayer.seekTo(0L);
                    }
                    this.manuallyPaused = false;
                    this.videoPlayer.pause();
                    if (!this.isActionBarVisible) {
                        toggleActionBar(true, true);
                    }
                }
                PipVideoOverlay.onVideoCompleted();
            }
        }
        PipVideoOverlay.updatePlayButton();
        VideoPlayerSeekBar videoPlayerSeekBar = this.videoPlayerSeekbar;
        MessageObject messageObject4 = this.currentMessageObject;
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            j = videoPlayer.getDuration();
        }
        videoPlayerSeekBar.updateTimestamps(messageObject4, j);
        updateVideoPlayerTime();
    }

    private void preparePlayer(Uri uri, boolean playWhenReady, boolean preview) {
        preparePlayer(uri, playWhenReady, preview, null);
    }

    private void preparePlayer(Uri uri, boolean playWhenReady, boolean preview, MediaController.SavedFilterState savedFilterState) {
        if (!preview) {
            this.currentPlayingVideoFile = uri;
        }
        if (this.parentActivity != null) {
            this.streamingAlertShown = false;
            this.startedPlayTime = SystemClock.elapsedRealtime();
            this.currentVideoFinishedLoading = false;
            this.lastBufferedPositionCheck = 0L;
            this.firstAnimationDelay = true;
            this.inPreview = preview;
            releasePlayer(false);
            if (this.imagesArrLocals.isEmpty()) {
                createVideoTextureView(null);
            }
            if (Build.VERSION.SDK_INT >= 21 && this.textureImageView == null) {
                ImageView imageView = new ImageView(this.parentActivity);
                this.textureImageView = imageView;
                imageView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
                this.textureImageView.setPivotX(0.0f);
                this.textureImageView.setPivotY(0.0f);
                this.textureImageView.setVisibility(4);
                this.containerView.addView(this.textureImageView);
            }
            checkFullscreenButton();
            if (this.orientationEventListener == null) {
                OrientationEventListener orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) { // from class: org.telegram.ui.PhotoViewer.50
                    @Override // android.view.OrientationEventListener
                    public void onOrientationChanged(int orientation) {
                        if (PhotoViewer.this.orientationEventListener != null && PhotoViewer.this.aspectRatioFrameLayout != null && PhotoViewer.this.aspectRatioFrameLayout.getVisibility() == 0 && PhotoViewer.this.parentActivity != null && PhotoViewer.this.fullscreenedByButton != 0) {
                            if (PhotoViewer.this.fullscreenedByButton == 1) {
                                if (orientation < 240 || orientation > 300) {
                                    if (!PhotoViewer.this.wasRotated || orientation <= 0) {
                                        return;
                                    }
                                    if (orientation >= 330 || orientation <= 30) {
                                        PhotoViewer.this.parentActivity.setRequestedOrientation(PhotoViewer.this.prevOrientation);
                                        PhotoViewer.this.fullscreenedByButton = 0;
                                        PhotoViewer.this.wasRotated = false;
                                        return;
                                    }
                                    return;
                                }
                                PhotoViewer.this.wasRotated = true;
                            } else if (orientation <= 0 || (orientation < 330 && orientation > 30)) {
                                if (PhotoViewer.this.wasRotated && orientation >= 240 && orientation <= 300) {
                                    PhotoViewer.this.parentActivity.setRequestedOrientation(PhotoViewer.this.prevOrientation);
                                    PhotoViewer.this.fullscreenedByButton = 0;
                                    PhotoViewer.this.wasRotated = false;
                                }
                            } else {
                                PhotoViewer.this.wasRotated = true;
                            }
                        }
                    }
                };
                this.orientationEventListener = orientationEventListener;
                if (orientationEventListener.canDetectOrientation()) {
                    this.orientationEventListener.enable();
                } else {
                    this.orientationEventListener.disable();
                    this.orientationEventListener = null;
                }
            }
            this.textureUploaded = false;
            this.videoSizeSet = false;
            this.videoCrossfadeStarted = false;
            boolean newPlayerCreated = false;
            this.playerWasReady = false;
            this.playerWasPlaying = false;
            this.captureFrameReadyAtTime = -1L;
            this.captureFrameAtTime = -1L;
            this.needCaptureFrameReadyAtTime = -1L;
            if (this.videoPlayer == null) {
                VideoPlayer videoPlayer = this.injectingVideoPlayer;
                if (videoPlayer == null) {
                    this.videoPlayer = new VideoPlayer() { // from class: org.telegram.ui.PhotoViewer.51
                        @Override // org.telegram.ui.Components.VideoPlayer
                        public void play() {
                            super.play();
                            PhotoViewer.this.playOrStopAnimatedStickers(true);
                        }

                        @Override // org.telegram.ui.Components.VideoPlayer
                        public void pause() {
                            super.pause();
                            if (PhotoViewer.this.currentEditMode == 0) {
                                PhotoViewer.this.playOrStopAnimatedStickers(false);
                            }
                        }

                        @Override // org.telegram.ui.Components.VideoPlayer
                        public void seekTo(long positionMs) {
                            super.seekTo(positionMs);
                            if (PhotoViewer.this.isCurrentVideo) {
                                PhotoViewer.this.seekAnimatedStickersTo(positionMs);
                            }
                        }
                    };
                    newPlayerCreated = true;
                } else {
                    this.videoPlayer = videoPlayer;
                    this.injectingVideoPlayer = null;
                    this.playerInjected = true;
                    updatePlayerState(videoPlayer.getPlayWhenReady(), this.videoPlayer.getPlaybackState());
                }
                TextureView textureView = this.videoTextureView;
                if (textureView != null) {
                    this.videoPlayer.setTextureView(textureView);
                }
                FirstFrameView firstFrameView = this.firstFrameView;
                if (firstFrameView != null) {
                    firstFrameView.clear();
                }
                this.videoPlayer.setDelegate(new AnonymousClass52());
            }
            if (!this.imagesArrLocals.isEmpty()) {
                createVideoTextureView(savedFilterState);
            }
            TextureView textureView2 = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView2.setAlpha(0.0f);
            PaintingOverlay paintingOverlay = this.paintingOverlay;
            if (paintingOverlay != null) {
                paintingOverlay.setAlpha(this.videoCrossfadeAlpha);
            }
            this.shouldSavePositionForCurrentVideo = null;
            this.shouldSavePositionForCurrentVideoShortTerm = null;
            this.lastSaveTime = 0L;
            if (newPlayerCreated) {
                this.seekToProgressPending = this.seekToProgressPending2;
                this.videoPlayerSeekbar.setProgress(0.0f);
                this.videoTimelineView.setProgress(0.0f);
                this.videoPlayerSeekbar.setBufferedProgress(0.0f);
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null) {
                    int duration = messageObject.getDuration();
                    String name = this.currentMessageObject.getFileName();
                    if (!TextUtils.isEmpty(name)) {
                        if (duration >= 600) {
                            if (this.currentMessageObject.forceSeekTo < 0.0f) {
                                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0);
                                float pos = preferences.getFloat(name, -1.0f);
                                if (pos > 0.0f && pos < 0.999f) {
                                    this.currentMessageObject.forceSeekTo = pos;
                                    this.videoPlayerSeekbar.setProgress(pos);
                                }
                            }
                            this.shouldSavePositionForCurrentVideo = name;
                        } else if (duration >= 10) {
                            SavedVideoPosition videoPosition = null;
                            int i = this.savedVideoPositions.size() - 1;
                            while (i >= 0) {
                                SavedVideoPosition item = this.savedVideoPositions.valueAt(i);
                                boolean newPlayerCreated2 = newPlayerCreated;
                                if (item.timestamp < SystemClock.elapsedRealtime() - DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                                    this.savedVideoPositions.removeAt(i);
                                } else if (videoPosition == null && this.savedVideoPositions.keyAt(i).equals(name)) {
                                    videoPosition = item;
                                }
                                i--;
                                newPlayerCreated = newPlayerCreated2;
                            }
                            if (this.currentMessageObject.forceSeekTo < 0.0f && videoPosition != null) {
                                float pos2 = videoPosition.position;
                                if (pos2 > 0.0f && pos2 < 0.999f) {
                                    this.currentMessageObject.forceSeekTo = pos2;
                                    this.videoPlayerSeekbar.setProgress(pos2);
                                }
                            }
                            this.shouldSavePositionForCurrentVideoShortTerm = name;
                        }
                    }
                }
                this.videoPlayer.preparePlayer(uri, "other");
                this.videoPlayer.setPlayWhenReady(playWhenReady);
            }
            MessageObject messageObject2 = this.currentMessageObject;
            boolean z = messageObject2 != null && messageObject2.getDuration() <= 30;
            this.playerLooping = z;
            this.videoPlayerControlFrameLayout.setSeekBarTransitionEnabled(z);
            this.videoPlayer.setLooping(this.playerLooping);
            MessageObject messageObject3 = this.currentMessageObject;
            if (messageObject3 != null && messageObject3.forceSeekTo >= 0.0f) {
                this.seekToProgressPending = this.currentMessageObject.forceSeekTo;
                this.currentMessageObject.forceSeekTo = -1.0f;
            }
            TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
            if (botInlineResult == null || (!botInlineResult.type.equals("video") && !MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                this.bottomLayout.setPadding(0, 0, 0, 0);
            } else {
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
                this.pickerView.setVisibility(8);
            }
            if (this.pageBlocksAdapter != null) {
                this.bottomLayout.setVisibility(0);
            }
            setVideoPlayerControlVisible(!this.isCurrentVideo, true);
            if (!this.isCurrentVideo) {
                scheduleActionBarHide(this.playerAutoStarted ? 3000 : 1000);
            }
            if (this.currentMessageObject != null) {
                this.videoPlayer.setPlaybackSpeed(this.currentVideoSpeed);
            }
            this.inPreview = preview;
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$52 */
    /* loaded from: classes4.dex */
    public class AnonymousClass52 implements VideoPlayer.VideoPlayerDelegate {
        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        AnonymousClass52() {
            PhotoViewer.this = this$0;
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            PhotoViewer.this.updatePlayerState(playWhenReady, playbackState);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onError(VideoPlayer player, Exception e) {
            if (PhotoViewer.this.videoPlayer != player) {
                return;
            }
            FileLog.e(e);
            if (!PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(PhotoViewer.this.parentActivity, PhotoViewer.this.resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("CantPlayVideo", R.string.CantPlayVideo));
            builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$52$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PhotoViewer.AnonymousClass52.this.m4281lambda$onError$0$orgtelegramuiPhotoViewer$52(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            PhotoViewer.this.showAlertDialog(builder);
        }

        /* renamed from: lambda$onError$0$org-telegram-ui-PhotoViewer$52 */
        public /* synthetic */ void m4281lambda$onError$0$orgtelegramuiPhotoViewer$52(DialogInterface dialog, int which) {
            try {
                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity, PhotoViewer.this.resourcesProvider);
                PhotoViewer.this.closePhoto(false, false);
            } catch (Exception e1) {
                FileLog.e(e1);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                    width = height;
                    height = width;
                }
                PhotoViewer.this.videoWidth = (int) (width * pixelWidthHeightRatio);
                PhotoViewer.this.videoHeight = (int) (height * pixelWidthHeightRatio);
                PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? 1.0f : (width * pixelWidthHeightRatio) / height, unappliedRotationDegrees);
                if (PhotoViewer.this.videoTextureView instanceof VideoEditTextureView) {
                    ((VideoEditTextureView) PhotoViewer.this.videoTextureView).setVideoSize((int) (width * pixelWidthHeightRatio), height);
                    if (PhotoViewer.this.sendPhotoType == 1) {
                        PhotoViewer.this.setCropBitmap();
                    }
                }
                PhotoViewer.this.videoSizeSet = true;
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame() {
            if (!PhotoViewer.this.textureUploaded) {
                PhotoViewer.this.textureUploaded = true;
                PhotoViewer.this.containerView.invalidate();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                if (PhotoViewer.this.videoPlayer == null || !PhotoViewer.this.videoPlayer.isLooping()) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$52$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.AnonymousClass52.this.m4282lambda$onRenderedFirstFrame$1$orgtelegramuiPhotoViewer$52();
                        }
                    }, 64L);
                }
            }
        }

        /* renamed from: lambda$onRenderedFirstFrame$1$org-telegram-ui-PhotoViewer$52 */
        public /* synthetic */ void m4282lambda$onRenderedFirstFrame$1$orgtelegramuiPhotoViewer$52() {
            PhotoViewer.this.firstFrameView.updateAlpha();
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            if (eventTime.eventPlaybackPositionMs == PhotoViewer.this.needCaptureFrameReadyAtTime) {
                PhotoViewer.this.captureFrameReadyAtTime = eventTime.eventPlaybackPositionMs;
                PhotoViewer.this.needCaptureFrameReadyAtTime = -1L;
                PhotoViewer.this.captureCurrentFrame();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                if (PhotoViewer.this.videoPlayer == null || !PhotoViewer.this.videoPlayer.isLooping()) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$52$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.AnonymousClass52.this.m4283lambda$onRenderedFirstFrame$2$orgtelegramuiPhotoViewer$52();
                        }
                    }, 64L);
                }
            }
        }

        /* renamed from: lambda$onRenderedFirstFrame$2$org-telegram-ui-PhotoViewer$52 */
        public /* synthetic */ void m4283lambda$onRenderedFirstFrame$2$orgtelegramuiPhotoViewer$52() {
            PhotoViewer.this.firstFrameView.updateAlpha();
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.changingTextureView) {
                PhotoViewer.this.changingTextureView = false;
                if (PhotoViewer.this.isInline) {
                    PhotoViewer.this.waitingForFirstTextureUpload = 1;
                    PhotoViewer.this.changedTextureView.setSurfaceTexture(surfaceTexture);
                    PhotoViewer.this.changedTextureView.setSurfaceTextureListener(PhotoViewer.this.surfaceTextureListener);
                    PhotoViewer.this.changedTextureView.setVisibility(0);
                    return true;
                }
            }
            return false;
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 2) {
                if (PhotoViewer.this.textureImageView != null) {
                    PhotoViewer.this.textureImageView.setVisibility(4);
                    PhotoViewer.this.textureImageView.setImageDrawable(null);
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        PhotoViewer.this.currentBitmap = null;
                    }
                }
                PhotoViewer.this.switchingInlineMode = false;
                if (Build.VERSION.SDK_INT >= 21) {
                    PhotoViewer.this.aspectRatioFrameLayout.getLocationInWindow(PhotoViewer.this.pipPosition);
                    int[] iArr = PhotoViewer.this.pipPosition;
                    iArr[1] = (int) (iArr[1] - PhotoViewer.this.containerView.getTranslationY());
                    if (PhotoViewer.this.textureImageView != null) {
                        PhotoViewer.this.textureImageView.setTranslationX(PhotoViewer.this.textureImageView.getTranslationX() + PhotoViewer.this.getLeftInset());
                    }
                    if (PhotoViewer.this.videoTextureView != null) {
                        PhotoViewer.this.videoTextureView.setTranslationX((PhotoViewer.this.videoTextureView.getTranslationX() + PhotoViewer.this.getLeftInset()) - PhotoViewer.this.aspectRatioFrameLayout.getX());
                    }
                    if (PhotoViewer.this.firstFrameView != null) {
                        PhotoViewer.this.firstFrameView.setTranslationX(PhotoViewer.this.videoTextureView.getTranslationX());
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_X, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_Y, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_X, PhotoViewer.this.pipPosition[0]));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_Y, PhotoViewer.this.pipPosition[1]));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_X, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_Y, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_X, PhotoViewer.this.pipPosition[0] - PhotoViewer.this.aspectRatioFrameLayout.getX()));
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_Y, PhotoViewer.this.pipPosition[1] - PhotoViewer.this.aspectRatioFrameLayout.getY()));
                    animators.add(ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 255));
                    if (PhotoViewer.this.firstFrameView != null) {
                        animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.SCALE_X, 1.0f));
                        animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.SCALE_Y, 1.0f));
                        animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.TRANSLATION_X, PhotoViewer.this.pipPosition[0] - PhotoViewer.this.aspectRatioFrameLayout.getX()));
                        animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.TRANSLATION_Y, PhotoViewer.this.pipPosition[1] - PhotoViewer.this.aspectRatioFrameLayout.getY()));
                    }
                    org.telegram.ui.Components.Rect pipRect = PipVideoOverlay.getPipRect(false, PhotoViewer.this.aspectRatioFrameLayout.getAspectRatio());
                    float width = pipRect.width / PhotoViewer.this.videoTextureView.getWidth();
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$52$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            PhotoViewer.AnonymousClass52.this.m4284lambda$onSurfaceTextureUpdated$3$orgtelegramuiPhotoViewer$52(valueAnimator2);
                        }
                    });
                    animators.add(valueAnimator);
                    animatorSet.playTogether(animators);
                    DecelerateInterpolator interpolator = new DecelerateInterpolator();
                    animatorSet.setInterpolator(interpolator);
                    animatorSet.setDuration(250L);
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.52.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.pipAnimationInProgress = false;
                            if (PhotoViewer.this.videoTextureView != null) {
                                PhotoViewer.this.videoTextureView.setOutlineProvider(null);
                            }
                            if (PhotoViewer.this.textureImageView != null) {
                                PhotoViewer.this.textureImageView.setOutlineProvider(null);
                            }
                            if (PhotoViewer.this.firstFrameView != null) {
                                PhotoViewer.this.firstFrameView.setOutlineProvider(null);
                            }
                        }
                    });
                    animatorSet.start();
                    PhotoViewer.this.toggleActionBar(true, true, new ActionBarToggleParams().enableStatusBarAnimation(false).enableTranslationAnimation(false).animationDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION).animationInterpolator(interpolator));
                } else {
                    PhotoViewer.this.toggleActionBar(true, false);
                }
                PhotoViewer.this.waitingForFirstTextureUpload = 0;
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.checkFromPlayer(PhotoViewer.this.videoPlayer);
            }
        }

        /* renamed from: lambda$onSurfaceTextureUpdated$3$org-telegram-ui-PhotoViewer$52 */
        public /* synthetic */ void m4284lambda$onSurfaceTextureUpdated$3$orgtelegramuiPhotoViewer$52(ValueAnimator animation) {
            PhotoViewer.this.inlineOutAnimationProgress = ((Float) animation.getAnimatedValue()).floatValue();
            if (PhotoViewer.this.videoTextureView != null) {
                PhotoViewer.this.videoTextureView.invalidateOutline();
            }
            if (PhotoViewer.this.textureImageView != null) {
                PhotoViewer.this.textureImageView.invalidateOutline();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.invalidateOutline();
            }
        }
    }

    public void checkFullscreenButton() {
        float currentTranslationX;
        float offsetX;
        TextureView textureView;
        TextureView textureView2;
        if (this.imagesArr.isEmpty()) {
            for (int b = 0; b < 3; b++) {
                this.fullscreenButton[b].setVisibility(4);
            }
            return;
        }
        int b2 = 0;
        while (b2 < 3) {
            int index = this.currentIndex;
            if (b2 == 1) {
                index++;
            } else if (b2 == 2) {
                index--;
            }
            if (index < 0 || index >= this.imagesArr.size()) {
                this.fullscreenButton[b2].setVisibility(4);
            } else {
                MessageObject messageObject = this.imagesArr.get(index);
                if (!messageObject.isVideo()) {
                    this.fullscreenButton[b2].setVisibility(4);
                } else {
                    int w = (b2 != 0 || (textureView2 = this.videoTextureView) == null) ? 0 : textureView2.getMeasuredWidth();
                    int h = (b2 != 0 || (textureView = this.videoTextureView) == null) ? 0 : textureView.getMeasuredHeight();
                    TLRPC.Document document = messageObject.getDocument();
                    int a = 0;
                    int N = document.attributes.size();
                    while (true) {
                        if (a >= N) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                        if (!(attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                            a++;
                        } else {
                            w = attribute.w;
                            h = attribute.h;
                            break;
                        }
                    }
                    if (AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x && !(this.videoTextureView instanceof VideoEditTextureView) && w > h) {
                        if (this.fullscreenButton[b2].getVisibility() != 0) {
                            this.fullscreenButton[b2].setVisibility(0);
                        }
                        float scale = w / this.containerView.getMeasuredWidth();
                        int height = (int) (h / scale);
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.fullscreenButton[b2].getLayoutParams();
                        layoutParams.topMargin = ((this.containerView.getMeasuredHeight() + height) / 2) - AndroidUtilities.dp(48.0f);
                    } else if (this.fullscreenButton[b2].getVisibility() != 4) {
                        this.fullscreenButton[b2].setVisibility(4);
                    }
                    if (this.imageMoveAnimation != null) {
                        float f = this.translationX;
                        currentTranslationX = f + ((this.animateToX - f) * this.animationValue);
                    } else {
                        currentTranslationX = this.translationX;
                    }
                    if (b2 == 1) {
                        offsetX = 0.0f;
                    } else if (b2 == 2) {
                        offsetX = ((-AndroidUtilities.displaySize.x) - AndroidUtilities.dp(15.0f)) + (currentTranslationX - this.maxX);
                    } else {
                        float offsetX2 = this.minX;
                        offsetX = currentTranslationX < offsetX2 ? currentTranslationX - offsetX2 : 0.0f;
                    }
                    this.fullscreenButton[b2].setTranslationX((AndroidUtilities.displaySize.x + offsetX) - AndroidUtilities.dp(48.0f));
                }
            }
            b2++;
        }
    }

    private void createVideoTextureView(final MediaController.SavedFilterState savedFilterState) {
        if (this.videoTextureView != null) {
            return;
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.53
            @Override // com.google.android.exoplayer2.ui.AspectRatioFrameLayout, android.widget.FrameLayout, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (PhotoViewer.this.textureImageView != null) {
                    ViewGroup.LayoutParams layoutParams = PhotoViewer.this.textureImageView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                }
                if (PhotoViewer.this.videoTextureView instanceof VideoEditTextureView) {
                    PhotoViewer.this.videoTextureView.setPivotX(PhotoViewer.this.videoTextureView.getMeasuredWidth() / 2);
                    PhotoViewer.this.firstFrameView.setPivotX(PhotoViewer.this.videoTextureView.getMeasuredWidth() / 2);
                } else {
                    PhotoViewer.this.videoTextureView.setPivotX(0.0f);
                    PhotoViewer.this.firstFrameView.setPivotX(0.0f);
                }
                PhotoViewer.this.checkFullscreenButton();
            }
        };
        this.aspectRatioFrameLayout = aspectRatioFrameLayout;
        aspectRatioFrameLayout.setWillNotDraw(false);
        this.aspectRatioFrameLayout.setVisibility(4);
        this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
        if (this.imagesArrLocals.isEmpty()) {
            this.videoTextureView = new TextureView(this.parentActivity);
        } else {
            VideoEditTextureView videoEditTextureView = new VideoEditTextureView(this.parentActivity, this.videoPlayer);
            if (savedFilterState != null) {
                videoEditTextureView.setDelegate(new VideoEditTextureView.VideoEditTextureViewDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda76
                    @Override // org.telegram.ui.Components.VideoEditTextureView.VideoEditTextureViewDelegate
                    public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
                        filterGLThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(MediaController.SavedFilterState.this));
                    }
                });
            }
            this.videoTextureView = videoEditTextureView;
        }
        SurfaceTexture surfaceTexture = this.injectingVideoPlayerSurface;
        if (surfaceTexture != null) {
            this.videoTextureView.setSurfaceTexture(surfaceTexture);
            this.textureUploaded = true;
            this.videoSizeSet = true;
            this.injectingVideoPlayerSurface = null;
        }
        this.videoTextureView.setPivotX(0.0f);
        this.videoTextureView.setPivotY(0.0f);
        this.videoTextureView.setOpaque(false);
        this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
        FirstFrameView firstFrameView = new FirstFrameView(this.parentActivity);
        this.firstFrameView = firstFrameView;
        firstFrameView.setPivotX(0.0f);
        this.firstFrameView.setPivotY(0.0f);
        this.firstFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.aspectRatioFrameLayout.addView(this.firstFrameView, LayoutHelper.createFrame(-1, -1, 17));
        if (this.sendPhotoType == 1) {
            View view = new View(this.parentActivity);
            this.flashView = view;
            view.setBackgroundColor(-1);
            this.flashView.setAlpha(0.0f);
            this.aspectRatioFrameLayout.addView(this.flashView, LayoutHelper.createFrame(-1, -1, 17));
        }
    }

    public void releasePlayer(boolean onClose) {
        if (this.videoPlayer != null) {
            cancelVideoPlayRunnable();
            AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            if (this.shouldSavePositionForCurrentVideoShortTerm != null) {
                float progress = ((float) this.videoPlayer.getCurrentPosition()) / ((float) this.videoPlayer.getDuration());
                this.savedVideoPositions.put(this.shouldSavePositionForCurrentVideoShortTerm, new SavedVideoPosition(progress, SystemClock.elapsedRealtime()));
            }
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        } else {
            this.playerWasPlaying = false;
        }
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        this.videoPreviewFrame.close();
        toggleMiniProgress(false, false);
        this.pipAvailable = false;
        this.playerInjected = false;
        if (this.pipItem.isEnabled()) {
            this.pipItem.setEnabled(false);
            this.pipItem.animate().alpha(0.5f).setDuration(175L).withEndAction(null).start();
        }
        if (this.keepScreenOnFlagSet) {
            try {
                this.parentActivity.getWindow().clearFlags(128);
                this.keepScreenOnFlagSet = false;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            try {
                this.containerView.removeView(aspectRatioFrameLayout);
            } catch (Throwable th) {
            }
            this.aspectRatioFrameLayout = null;
        }
        cancelFlashAnimations();
        this.flashView = null;
        TextureView textureView = this.videoTextureView;
        if (textureView != null) {
            if (textureView instanceof VideoEditTextureView) {
                ((VideoEditTextureView) textureView).release();
            }
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (!onClose && !this.inPreview && !this.requestingPreview) {
            setVideoPlayerControlVisible(false, true);
        }
        this.photoProgressViews[0].resetAlphas();
    }

    private void setVideoPlayerControlVisible(final boolean visible, boolean animated) {
        if (this.videoPlayerControlVisible != visible) {
            Animator animator = this.videoPlayerControlAnimator;
            if (animator != null) {
                animator.cancel();
            }
            this.videoPlayerControlVisible = visible;
            float f = 1.0f;
            int i = 0;
            if (animated) {
                if (visible) {
                    this.videoPlayerControlFrameLayout.setVisibility(0);
                } else {
                    this.dateTextView.setVisibility(0);
                    this.nameTextView.setVisibility(0);
                    if (this.allowShare) {
                        this.bottomButtonsLayout.setVisibility(0);
                    }
                }
                final boolean shareWasAllowed = this.allowShare;
                float[] fArr = new float[2];
                fArr[0] = this.videoPlayerControlFrameLayout.getAlpha();
                if (!visible) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator anim = ValueAnimator.ofFloat(fArr);
                anim.setDuration(200L);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda66
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.this.m4254xb6e568de(shareWasAllowed, valueAnimator);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.54
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (!visible) {
                            PhotoViewer.this.videoPlayerControlFrameLayout.setVisibility(8);
                            return;
                        }
                        PhotoViewer.this.dateTextView.setVisibility(8);
                        PhotoViewer.this.nameTextView.setVisibility(8);
                        if (shareWasAllowed) {
                            PhotoViewer.this.bottomButtonsLayout.setVisibility(8);
                        }
                    }
                });
                this.videoPlayerControlAnimator = anim;
                anim.start();
            } else {
                this.videoPlayerControlFrameLayout.setVisibility(visible ? 0 : 8);
                this.videoPlayerControlFrameLayout.setAlpha(visible ? 1.0f : 0.0f);
                this.dateTextView.setVisibility(visible ? 8 : 0);
                this.dateTextView.setAlpha(visible ? 0.0f : 1.0f);
                this.nameTextView.setVisibility(visible ? 8 : 0);
                this.nameTextView.setAlpha(visible ? 0.0f : 1.0f);
                if (this.allowShare) {
                    LinearLayout linearLayout = this.bottomButtonsLayout;
                    if (visible) {
                        i = 8;
                    }
                    linearLayout.setVisibility(i);
                    LinearLayout linearLayout2 = this.bottomButtonsLayout;
                    if (visible) {
                        f = 0.0f;
                    }
                    linearLayout2.setAlpha(f);
                }
            }
            if (!this.allowShare || this.pageBlocksAdapter != null) {
                return;
            }
            if (visible) {
                this.menuItem.showSubItem(10);
            } else {
                this.menuItem.hideSubItem(10);
            }
        }
    }

    /* renamed from: lambda$setVideoPlayerControlVisible$51$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4254xb6e568de(boolean shareWasAllowed, ValueAnimator a) {
        float alpha = ((Float) a.getAnimatedValue()).floatValue();
        this.videoPlayerControlFrameLayout.setAlpha(alpha);
        this.dateTextView.setAlpha(1.0f - alpha);
        this.nameTextView.setAlpha(1.0f - alpha);
        if (shareWasAllowed) {
            this.bottomButtonsLayout.setAlpha(1.0f - alpha);
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object object) {
        CharSequence caption = null;
        if (this.hasCaptionForAllMedia) {
            caption = this.captionForAllMedia;
        } else if (object instanceof MediaController.PhotoEntry) {
            caption = ((MediaController.PhotoEntry) object).caption;
        } else if (!(object instanceof TLRPC.BotInlineResult) && (object instanceof MediaController.SearchImage)) {
            caption = ((MediaController.SearchImage) object).caption;
        }
        if (TextUtils.isEmpty(caption)) {
            this.captionEditText.setFieldText("");
        } else {
            this.captionEditText.setFieldText(caption);
        }
        this.captionEditText.setAllowTextEntitiesIntersection(supportsSendingNewEntities());
    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        if (this.parentActivity == null) {
            return;
        }
        try {
            AlertDialog alertDialog = this.visibleDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            AlertDialog show = builder.show();
            this.visibleDialog = show;
            show.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    PhotoViewer.this.m4255lambda$showAlertDialog$52$orgtelegramuiPhotoViewer(dialogInterface);
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: lambda$showAlertDialog$52$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4255lambda$showAlertDialog$52$orgtelegramuiPhotoViewer(DialogInterface dialog) {
        this.visibleDialog = null;
    }

    private void mergeImages(String finalPath, String thumbPath, Bitmap thumb, Bitmap bitmap, float size, boolean reverse) {
        boolean recycle = false;
        if (thumb == null) {
            try {
                thumb = BitmapFactory.decodeFile(thumbPath);
                recycle = true;
            } catch (Throwable e) {
                FileLog.e(e);
                return;
            }
        }
        int w = thumb.getWidth();
        int h = thumb.getHeight();
        if (w > size || h > size) {
            float scale = Math.max(w, h) / size;
            w = (int) (w / scale);
            h = (int) (h / scale);
        }
        Bitmap dst = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        Rect dstRect = new Rect(0, 0, w, h);
        if (reverse) {
            canvas.drawBitmap(bitmap, (Rect) null, dstRect, this.bitmapPaint);
            canvas.drawBitmap(thumb, (Rect) null, dstRect, this.bitmapPaint);
        } else {
            canvas.drawBitmap(thumb, (Rect) null, dstRect, this.bitmapPaint);
            canvas.drawBitmap(bitmap, (Rect) null, dstRect, this.bitmapPaint);
        }
        FileOutputStream stream = new FileOutputStream(new File(finalPath));
        dst.compress(Bitmap.CompressFormat.JPEG, size == 512.0f ? 83 : 87, stream);
        try {
            stream.close();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (recycle) {
            thumb.recycle();
        }
        dst.recycle();
    }

    public void seekAnimatedStickersTo(long ms) {
        if (this.editState.mediaEntities != null) {
            int N = this.editState.mediaEntities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.editState.mediaEntities.get(a);
                if (entity.type == 0 && (entity.subType & 1) != 0 && (entity.view instanceof BackupImageView)) {
                    ImageReceiver imageReceiver = ((BackupImageView) entity.view).getImageReceiver();
                    RLottieDrawable drawable = imageReceiver.getLottieAnimation();
                    if (drawable != null) {
                        long j = this.startTime;
                        long j2 = 0;
                        if (j > 0) {
                            j2 = j / 1000;
                        }
                        drawable.setProgressMs(ms - j2);
                    }
                }
            }
        }
    }

    public void playOrStopAnimatedStickers(boolean play) {
        if (this.editState.mediaEntities != null) {
            int N = this.editState.mediaEntities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.editState.mediaEntities.get(a);
                if (entity.type == 0 && (entity.subType & 1) != 0 && (entity.view instanceof BackupImageView)) {
                    ImageReceiver imageReceiver = ((BackupImageView) entity.view).getImageReceiver();
                    RLottieDrawable drawable = imageReceiver.getLottieAnimation();
                    if (drawable != null) {
                        if (play) {
                            drawable.start();
                        } else {
                            drawable.stop();
                        }
                    }
                }
            }
        }
    }

    private int getAnimatedMediaEntitiesCount(boolean single) {
        int count = 0;
        if (this.editState.mediaEntities != null) {
            int N = this.editState.mediaEntities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.editState.mediaEntities.get(a);
                if (entity.type == 0 && ((entity.subType & 1) != 0 || (entity.subType & 4) != 0)) {
                    count++;
                    if (single) {
                        break;
                    }
                }
            }
        }
        return count;
    }

    private boolean hasAnimatedMediaEntities() {
        return getAnimatedMediaEntitiesCount(true) != 0;
    }

    private Bitmap createCroppedBitmap(Bitmap bitmap, MediaController.CropState cropState, int[] extraTransform, boolean mirror) {
        Throwable e;
        try {
            int i = cropState.transformRotation;
            int i2 = 0;
            if (extraTransform != null) {
                i2 = extraTransform[0];
            }
            int tr = (i + i2) % 360;
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int fw = w;
            int rotatedW = w;
            int fh = h;
            int rotatedH = h;
            if (tr == 90 || tr == 270) {
                rotatedW = fh;
                fw = fh;
                rotatedH = fw;
                fh = fw;
            }
            int fw2 = (int) (fw * cropState.cropPw);
            int fh2 = (int) (fh * cropState.cropPh);
            Bitmap canvasBitmap = Bitmap.createBitmap(fw2, fh2, Bitmap.Config.ARGB_8888);
            Matrix matrix = new Matrix();
            matrix.postTranslate((-w) / 2, (-h) / 2);
            if (mirror && cropState.mirrored) {
                if (tr != 90 && tr != 270) {
                    matrix.postScale(-1.0f, 1.0f);
                }
                matrix.postScale(1.0f, -1.0f);
            }
            matrix.postRotate(cropState.cropRotate + tr);
            matrix.postTranslate(cropState.cropPx * rotatedW, cropState.cropPy * rotatedH);
            matrix.postScale(cropState.cropScale, cropState.cropScale);
            matrix.postTranslate(fw2 / 2, fh2 / 2);
            Canvas canvas = new Canvas(canvasBitmap);
            try {
                canvas.drawBitmap(bitmap, matrix, new Paint(2));
                return canvasBitmap;
            } catch (Throwable th) {
                e = th;
                FileLog.e(e);
                return null;
            }
        } catch (Throwable th2) {
            e = th2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:230:0x082e  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x0837  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x0840  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x085e  */
    /* JADX WARN: Removed duplicated region for block: B:247:0x0891  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x08a7  */
    /* JADX WARN: Removed duplicated region for block: B:256:0x08af  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x090a  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x0917  */
    /* JADX WARN: Removed duplicated region for block: B:267:0x0926  */
    /* JADX WARN: Removed duplicated region for block: B:272:0x0930  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x0942  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void applyCurrentEditMode() {
        /*
            Method dump skipped, instructions count: 2382
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.applyCurrentEditMode():void");
    }

    private void setPhotoChecked() {
        ChatActivity chatActivity;
        TLRPC.Chat chat;
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            if (photoViewerProvider.getSelectedPhotos() != null && this.maxSelectedPhotos > 0 && this.placeProvider.getSelectedPhotos().size() >= this.maxSelectedPhotos && !this.placeProvider.isPhotoChecked(this.currentIndex)) {
                if (this.allowOrder && (chatActivity = this.parentChatActivity) != null && (chat = chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled) {
                    AlertsCreator.createSimpleAlert(this.parentActivity, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError)).show();
                    return;
                }
                return;
            }
            int num = this.placeProvider.setPhotoChecked(this.currentIndex, getCurrentVideoEditedInfo());
            boolean checked = this.placeProvider.isPhotoChecked(this.currentIndex);
            this.checkImageView.setChecked(checked, true);
            if (num >= 0) {
                if (checked) {
                    this.selectedPhotosAdapter.notifyItemInserted(num);
                    this.selectedPhotosListView.smoothScrollToPosition(num);
                } else {
                    this.selectedPhotosAdapter.notifyItemRemoved(num);
                    if (num == 0) {
                        this.selectedPhotosAdapter.notifyItemChanged(0);
                    }
                }
            }
            updateSelectedCount();
        }
    }

    public void updateResetButtonVisibility(final boolean show) {
        boolean isShown = this.resetButton.isClickable();
        if (isShown != show) {
            this.resetButton.setClickable(show);
            this.resetButton.setVisibility(0);
            this.resetButton.clearAnimation();
            this.resetButton.animate().alpha(show ? 1.0f : 0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150L).withEndAction(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda63
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.m4263xc70af73f(show);
                }
            });
        }
    }

    /* renamed from: lambda$updateResetButtonVisibility$53$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4263xc70af73f(boolean show) {
        if (!show) {
            this.resetButton.setVisibility(8);
        }
    }

    private void createCropView() {
        if (this.photoCropView != null) {
            return;
        }
        PhotoCropView photoCropView = new PhotoCropView(this.activityContext, this.resourcesProvider);
        this.photoCropView = photoCropView;
        photoCropView.setVisibility(8);
        this.photoCropView.onDisappear();
        int index = this.containerView.indexOfChild(this.videoTimelineView);
        this.containerView.addView(this.photoCropView, index - 1, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.photoCropView.setDelegate(new AnonymousClass55());
    }

    /* renamed from: org.telegram.ui.PhotoViewer$55 */
    /* loaded from: classes4.dex */
    public class AnonymousClass55 implements PhotoCropView.PhotoCropViewDelegate {
        AnonymousClass55() {
            PhotoViewer.this = this$0;
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onChange(boolean reset) {
            PhotoViewer.this.updateResetButtonVisibility(!reset);
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onUpdate() {
            PhotoViewer.this.containerView.invalidate();
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onTapUp() {
            if (PhotoViewer.this.sendPhotoType == 1) {
                PhotoViewer.this.manuallyPaused = true;
                PhotoViewer.this.toggleVideoPlayer();
            }
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onVideoThumbClick() {
            if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.seekTo(((float) PhotoViewer.this.videoPlayer.getDuration()) * PhotoViewer.this.avatarStartProgress);
                PhotoViewer.this.videoPlayer.pause();
                PhotoViewer.this.videoTimelineView.setProgress(PhotoViewer.this.avatarStartProgress);
                PhotoViewer.this.cancelVideoPlayRunnable();
                AndroidUtilities.runOnUIThread(PhotoViewer.this.videoPlayRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$55$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass55.this.m4285lambda$onVideoThumbClick$0$orgtelegramuiPhotoViewer$55();
                    }
                }, 860L);
            }
        }

        /* renamed from: lambda$onVideoThumbClick$0$org-telegram-ui-PhotoViewer$55 */
        public /* synthetic */ void m4285lambda$onVideoThumbClick$0$orgtelegramuiPhotoViewer$55() {
            PhotoViewer.this.manuallyPaused = false;
            if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.play();
            }
            PhotoViewer.this.videoPlayRunnable = null;
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public boolean rotate() {
            return PhotoViewer.this.cropRotate(-90.0f);
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public boolean mirror() {
            return PhotoViewer.this.cropMirror();
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public int getVideoThumbX() {
            return (int) (AndroidUtilities.dp(16.0f) + ((PhotoViewer.this.videoTimelineView.getMeasuredWidth() - AndroidUtilities.dp(32.0f)) * PhotoViewer.this.avatarStartProgress));
        }
    }

    private void startVideoPlayer() {
        VideoPlayer videoPlayer;
        if (this.isCurrentVideo && (videoPlayer = this.videoPlayer) != null && !videoPlayer.isPlaying()) {
            if (!this.muteVideo || this.sendPhotoType == 1) {
                this.videoPlayer.setVolume(0.0f);
            }
            this.manuallyPaused = false;
            toggleVideoPlayer();
        }
    }

    private void detectFaces() {
        if (this.centerImage.getAnimation() != null || this.imagesArrLocals.isEmpty() || this.sendPhotoType == 1) {
            return;
        }
        String key = this.centerImage.getImageKey();
        String str = this.currentImageFaceKey;
        if (str != null && str.equals(key)) {
            return;
        }
        this.currentImageHasFace = 0;
        ImageReceiver.BitmapHolder bitmap = this.centerImage.getBitmapSafe();
        detectFaces(key, bitmap, this.centerImage.getOrientation());
    }

    private void detectFaces(final String key, final ImageReceiver.BitmapHolder bitmap, final int orientation) {
        if (key == null || bitmap == null || bitmap.bitmap == null) {
            return;
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4211lambda$detectFaces$56$orgtelegramuiPhotoViewer(bitmap, orientation, key);
            }
        });
    }

    /* renamed from: lambda$detectFaces$56$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4211lambda$detectFaces$56$orgtelegramuiPhotoViewer(final ImageReceiver.BitmapHolder bitmap, int orientation, final String key) {
        FaceDetector faceDetector = null;
        try {
            try {
                final boolean hasFaces = false;
                faceDetector = new FaceDetector.Builder(ApplicationLoader.applicationContext).setMode(0).setLandmarkType(0).setTrackingEnabled(false).build();
                if (faceDetector.isOperational()) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap.bitmap).setRotation(orientation).build();
                    SparseArray<Face> faces = faceDetector.detect(frame);
                    if (faces != null && faces.size() != 0) {
                        hasFaces = true;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda56
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.this.m4209lambda$detectFaces$54$orgtelegramuiPhotoViewer(key, hasFaces);
                        }
                    });
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("face detection is not operational");
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda58
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.this.m4210lambda$detectFaces$55$orgtelegramuiPhotoViewer(bitmap, key);
                        }
                    });
                }
                if (faceDetector == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (0 == 0) {
                    return;
                }
            }
            faceDetector.release();
        } catch (Throwable th) {
            if (0 != 0) {
                faceDetector.release();
            }
            throw th;
        }
    }

    /* renamed from: lambda$detectFaces$54$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4209lambda$detectFaces$54$orgtelegramuiPhotoViewer(String key, boolean hasFaces) {
        String imageKey = this.centerImage.getImageKey();
        if (key.equals(imageKey)) {
            this.currentImageHasFace = hasFaces ? 1 : 0;
            this.currentImageFaceKey = key;
        }
    }

    /* renamed from: lambda$detectFaces$55$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4210lambda$detectFaces$55$orgtelegramuiPhotoViewer(ImageReceiver.BitmapHolder bitmap, String key) {
        bitmap.release();
        String imageKey = this.centerImage.getImageKey();
        if (key.equals(imageKey)) {
            this.currentImageHasFace = 2;
            this.currentImageFaceKey = key;
        }
    }

    private void switchToEditMode(final int mode) {
        int navigationBarColorTo;
        int i;
        Bitmap bitmap;
        int hasFaces;
        int i2;
        int i3;
        float oldScale;
        float newScale;
        if (this.currentEditMode != mode) {
            if ((this.isCurrentVideo && this.photoProgressViews[0].backgroundState != 3 && !this.isCurrentVideo && (this.centerImage.getBitmap() == null || this.photoProgressViews[0].backgroundState != -1)) || this.changeModeAnimation != null || this.imageMoveAnimation != null || this.captionEditText.getTag() != null) {
                return;
            }
            this.windowView.setClipChildren(mode == 2);
            int navigationBarColorFrom = Theme.ACTION_BAR_PHOTO_VIEWER_COLOR;
            if (this.navigationBar.getBackground() instanceof ColorDrawable) {
                navigationBarColorFrom = ((ColorDrawable) this.navigationBar.getBackground()).getColor();
            }
            if (mode == 1) {
                navigationBarColorTo = -872415232;
            } else if (mode == 3) {
                navigationBarColorTo = -16777216;
            } else {
                navigationBarColorTo = Theme.ACTION_BAR_PHOTO_VIEWER_COLOR;
            }
            this.navigationBar.setVisibility(mode != 2 ? 0 : 4);
            this.switchingToMode = mode;
            if (mode == 0) {
                Bitmap bitmap2 = this.centerImage.getBitmap();
                if (bitmap2 != null) {
                    int bitmapWidth = this.centerImage.getBitmapWidth();
                    int bitmapHeight = this.centerImage.getBitmapHeight();
                    int i4 = this.currentEditMode;
                    if (i4 == 3) {
                        if (this.sendPhotoType == 1) {
                            if (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270) {
                                bitmapWidth = bitmapHeight;
                                bitmapHeight = bitmapWidth;
                            }
                        } else if (this.editState.cropState != null) {
                            if (this.editState.cropState.transformRotation == 90 || this.editState.cropState.transformRotation == 270) {
                                bitmapWidth = bitmapHeight;
                                bitmapHeight = bitmapWidth;
                            }
                            bitmapWidth = (int) (bitmapWidth * this.editState.cropState.cropPw);
                            bitmapHeight = (int) (bitmapHeight * this.editState.cropState.cropPh);
                        }
                        newScale = Math.min(getContainerViewWidth(0) / bitmapWidth, getContainerViewHeight(0) / bitmapHeight);
                        oldScale = Math.min(getContainerViewWidth(3) / bitmapWidth, getContainerViewHeight(3) / bitmapHeight);
                    } else {
                        if (i4 != 1 && this.editState.cropState != null && (this.editState.cropState.transformRotation == 90 || this.editState.cropState.transformRotation == 270)) {
                            float scaleToFitX = getContainerViewWidth() / bitmapHeight;
                            if (bitmapWidth * scaleToFitX > getContainerViewHeight()) {
                                scaleToFitX = getContainerViewHeight() / bitmapWidth;
                            }
                            float sc = Math.min(getContainerViewWidth() / bitmapWidth, getContainerViewHeight() / bitmapHeight);
                            float cropScale = scaleToFitX / sc;
                            this.scale = 1.0f / cropScale;
                        } else if (this.sendPhotoType == 1 && (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270)) {
                            float scaleToFitX2 = getContainerViewWidth() / bitmapHeight;
                            if (bitmapWidth * scaleToFitX2 > getContainerViewHeight()) {
                                scaleToFitX2 = getContainerViewHeight() / bitmapWidth;
                            }
                            float sc2 = Math.min(getContainerViewWidth() / bitmapWidth, getContainerViewHeight() / bitmapHeight);
                            float cropScale2 = (((this.cropTransform.getScale() / this.cropTransform.getTrueCropScale()) * scaleToFitX2) / sc2) / this.cropTransform.getMinScale();
                            this.scale = 1.0f / cropScale2;
                        }
                        if (this.editState.cropState != null) {
                            if (this.editState.cropState.transformRotation == 90 || this.editState.cropState.transformRotation == 270) {
                                bitmapWidth = bitmapHeight;
                                bitmapHeight = bitmapWidth;
                            }
                            bitmapWidth = (int) (bitmapWidth * this.editState.cropState.cropPw);
                            bitmapHeight = (int) (bitmapHeight * this.editState.cropState.cropPh);
                        } else if (this.sendPhotoType == 1 && (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270)) {
                            bitmapWidth = bitmapHeight;
                            bitmapHeight = bitmapWidth;
                        }
                        int temp = getContainerViewWidth();
                        oldScale = Math.min(temp / bitmapWidth, getContainerViewHeight() / bitmapHeight);
                        if (this.sendPhotoType == 1) {
                            newScale = getCropFillScale(this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270);
                        } else {
                            newScale = Math.min(getContainerViewWidth(0) / bitmapWidth, getContainerViewHeight(0) / bitmapHeight);
                        }
                    }
                    this.animateToScale = newScale / oldScale;
                    this.animateToX = 0.0f;
                    this.translationX = (getLeftInset() / 2) - (getRightInset() / 2);
                    if (this.sendPhotoType == 1) {
                        int i5 = this.currentEditMode;
                        if (i5 == 2) {
                            this.animateToY = AndroidUtilities.dp(36.0f);
                        } else if (i5 == 3) {
                            this.animateToY = -AndroidUtilities.dp(12.0f);
                        }
                    } else {
                        int i6 = this.currentEditMode;
                        if (i6 == 1) {
                            this.animateToY = AndroidUtilities.dp(56.0f);
                        } else if (i6 == 2) {
                            this.animateToY = AndroidUtilities.dp(93.0f);
                        } else if (i6 == 3) {
                            this.animateToY = AndroidUtilities.dp(44.0f);
                        }
                        if (isStatusBarVisible()) {
                            this.animateToY -= AndroidUtilities.statusBarHeight / 2;
                        }
                    }
                    this.animationStartTime = System.currentTimeMillis();
                    this.zoomAnimation = true;
                }
                this.padImageForHorizontalInsets = false;
                this.imageMoveAnimation = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList<>(4);
                int i7 = this.currentEditMode;
                if (i7 == 1) {
                    animators.add(ObjectAnimator.ofFloat(this.editorDoneLayout, View.TRANSLATION_Y, AndroidUtilities.dp(48.0f)));
                    animators.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 0.0f));
                    ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda11
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.this.m4257lambda$switchToEditMode$57$orgtelegramuiPhotoViewer(valueAnimator);
                        }
                    });
                    animators.add(scaleAnimator);
                    i3 = 2;
                } else if (i7 == 2) {
                    this.photoFilterView.shutdown();
                    animators.add(ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(186.0f)));
                    animators.add(ObjectAnimator.ofFloat(this.photoFilterView.getCurveControl(), View.ALPHA, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(this.photoFilterView.getBlurControl(), View.ALPHA, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
                    i3 = 2;
                } else if (i7 != 3) {
                    i3 = 2;
                } else {
                    this.paintingOverlay.showAll();
                    this.containerView.invalidate();
                    this.photoPaintView.shutdown();
                    animators.add(ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f)));
                    animators.add(ObjectAnimator.ofFloat(this.photoPaintView.getColorPickerBackground(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f)));
                    animators.add(ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f)));
                    i3 = 2;
                    animators.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
                }
                View view = this.navigationBar;
                ArgbEvaluator argbEvaluator = new ArgbEvaluator();
                Object[] objArr = new Object[i3];
                objArr[0] = Integer.valueOf(navigationBarColorFrom);
                objArr[1] = Integer.valueOf(navigationBarColorTo);
                animators.add(ObjectAnimator.ofObject(view, TtmlNode.ATTR_TTS_BACKGROUND_COLOR, argbEvaluator, objArr));
                this.imageMoveAnimation.playTogether(animators);
                this.imageMoveAnimation.setDuration(200L);
                this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.56
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.currentEditMode == 1) {
                            PhotoViewer.this.photoCropView.onDisappear();
                            PhotoViewer.this.photoCropView.onHide();
                            PhotoViewer.this.editorDoneLayout.setVisibility(8);
                            PhotoViewer.this.photoCropView.setVisibility(8);
                            PhotoViewer.this.photoCropView.cropView.areaView.setRotationScaleTranslation(0.0f, 1.0f, 0.0f, 0.0f);
                        } else if (PhotoViewer.this.currentEditMode == 2) {
                            try {
                                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            PhotoViewer.this.photoFilterView = null;
                        } else if (PhotoViewer.this.currentEditMode == 3) {
                            try {
                                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                            PhotoViewer.this.photoPaintView = null;
                        }
                        PhotoViewer.this.imageMoveAnimation = null;
                        PhotoViewer.this.currentEditMode = mode;
                        PhotoViewer.this.switchingToMode = -1;
                        PhotoViewer.this.applying = false;
                        if (PhotoViewer.this.sendPhotoType == 1) {
                            PhotoViewer.this.photoCropView.setVisibility(0);
                        }
                        PhotoViewer.this.animateToScale = 1.0f;
                        PhotoViewer.this.animateToX = 0.0f;
                        PhotoViewer.this.animateToY = 0.0f;
                        PhotoViewer.this.scale = 1.0f;
                        PhotoViewer photoViewer = PhotoViewer.this;
                        photoViewer.updateMinMax(photoViewer.scale);
                        PhotoViewer.this.containerView.invalidate();
                        if (PhotoViewer.this.savedState != null) {
                            PhotoViewer.this.savedState.restore();
                            PhotoViewer.this.savedState = null;
                            ActionBarToggleParams toggleParams = new ActionBarToggleParams().enableStatusBarAnimation(false);
                            PhotoViewer.this.toggleActionBar(false, false, toggleParams);
                            PhotoViewer.this.toggleActionBar(true, true, toggleParams);
                            return;
                        }
                        AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList<Animator> arrayList = new ArrayList<>();
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, 0.0f));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, 0.0f));
                        if (PhotoViewer.this.sendPhotoType != 1) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.TRANSLATION_Y, 0.0f));
                        }
                        if (PhotoViewer.this.needCaptionLayout) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.TRANSLATION_Y, 0.0f));
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, View.ALPHA, 1.0f));
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photosCounterView, View.ALPHA, 1.0f));
                        } else if (PhotoViewer.this.sendPhotoType == 1) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, 1.0f));
                        }
                        if (PhotoViewer.this.cameraItem.getTag() != null) {
                            PhotoViewer.this.cameraItem.setVisibility(0);
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.cameraItem, View.ALPHA, 1.0f));
                        }
                        if (PhotoViewer.this.muteItem.getTag() != null) {
                            PhotoViewer.this.muteItem.setVisibility(0);
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.muteItem, View.ALPHA, 1.0f));
                        }
                        if (PhotoViewer.this.navigationBar != null) {
                            PhotoViewer.this.navigationBar.setVisibility(0);
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.navigationBar, View.ALPHA, 1.0f));
                        }
                        animatorSet.playTogether(arrayList);
                        animatorSet.setDuration(200L);
                        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.56.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animation2) {
                                PhotoViewer.this.pickerView.setVisibility(0);
                                PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                                PhotoViewer.this.actionBar.setVisibility(0);
                                if (PhotoViewer.this.needCaptionLayout) {
                                    PhotoViewer.this.captionTextViewSwitcher.setVisibility(PhotoViewer.this.captionTextViewSwitcher.getTag() != null ? 0 : 4);
                                }
                                if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                    PhotoViewer.this.checkImageView.setVisibility(0);
                                    PhotoViewer.this.photosCounterView.setVisibility(0);
                                }
                            }
                        });
                        animatorSet.start();
                    }
                });
                this.imageMoveAnimation.start();
            } else if (mode == 1) {
                startVideoPlayer();
                createCropView();
                this.previousHasTransform = this.cropTransform.hasViewTransform();
                this.previousCropPx = this.cropTransform.getCropPx();
                this.previousCropPy = this.cropTransform.getCropPy();
                this.previousCropScale = this.cropTransform.getScale();
                this.previousCropRotation = this.cropTransform.getRotation();
                this.previousCropOrientation = this.cropTransform.getOrientation();
                this.previousCropPw = this.cropTransform.getCropPw();
                this.previousCropPh = this.cropTransform.getCropPh();
                this.previousCropMirrored = this.cropTransform.isMirrored();
                this.photoCropView.onAppear();
                this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", R.string.Crop));
                this.editorDoneLayout.doneButton.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
                this.changeModeAnimation = new AnimatorSet();
                ArrayList<Animator> arrayList = new ArrayList<>();
                FrameLayout frameLayout = this.pickerView;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[2];
                fArr[0] = 0.0f;
                fArr[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
                ImageView imageView = this.pickerViewSendButton;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[2];
                fArr2[0] = 0.0f;
                fArr2[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList.add(ObjectAnimator.ofFloat(imageView, property2, fArr2));
                arrayList.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, 0.0f, -this.actionBar.getHeight()));
                arrayList.add(ObjectAnimator.ofObject(this.navigationBar, TtmlNode.ATTR_TTS_BACKGROUND_COLOR, new ArgbEvaluator(), Integer.valueOf(navigationBarColorFrom), Integer.valueOf(navigationBarColorTo)));
                if (this.needCaptionLayout) {
                    CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
                    Property property3 = View.TRANSLATION_Y;
                    float[] fArr3 = new float[2];
                    fArr3[0] = 0.0f;
                    fArr3[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                    arrayList.add(ObjectAnimator.ofFloat(captionTextViewSwitcher, property3, fArr3));
                }
                int i8 = this.sendPhotoType;
                if (i8 == 0 || i8 == 4) {
                    arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, 1.0f, 0.0f));
                    arrayList.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.cameraItem.getTag() != null) {
                    arrayList.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.muteItem.getTag() != null) {
                    arrayList.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, 1.0f, 0.0f));
                }
                View view2 = this.navigationBar;
                if (view2 != null) {
                    arrayList.add(ObjectAnimator.ofFloat(view2, View.ALPHA, 1.0f));
                }
                this.changeModeAnimation.playTogether(arrayList);
                this.changeModeAnimation.setDuration(200L);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.57
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhotoViewer photoViewer;
                        VideoEditTextureView videoEditTextureView = null;
                        PhotoViewer.this.changeModeAnimation = null;
                        PhotoViewer.this.pickerView.setVisibility(8);
                        PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                        PhotoViewer.this.cameraItem.setVisibility(8);
                        PhotoViewer.this.muteItem.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0f));
                        PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                        PhotoViewer.this.isPhotosListViewVisible = false;
                        if (PhotoViewer.this.needCaptionLayout) {
                            PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                            PhotoViewer.this.checkImageView.setVisibility(8);
                            PhotoViewer.this.photosCounterView.setVisibility(8);
                        }
                        Bitmap bitmap3 = PhotoViewer.this.centerImage.getBitmap();
                        if (bitmap3 != null || PhotoViewer.this.isCurrentVideo) {
                            PhotoCropView photoCropView = PhotoViewer.this.photoCropView;
                            int orientation = PhotoViewer.this.centerImage.getOrientation();
                            boolean z = PhotoViewer.this.sendPhotoType != 1;
                            PaintingOverlay paintingOverlay = PhotoViewer.this.paintingOverlay;
                            CropTransform cropTransform = PhotoViewer.this.cropTransform;
                            if (PhotoViewer.this.isCurrentVideo) {
                                videoEditTextureView = (VideoEditTextureView) PhotoViewer.this.videoTextureView;
                            }
                            photoCropView.setBitmap(bitmap3, orientation, z, false, paintingOverlay, cropTransform, videoEditTextureView, PhotoViewer.this.editState.cropState);
                            PhotoViewer.this.photoCropView.onDisappear();
                            int bitmapWidth2 = PhotoViewer.this.centerImage.getBitmapWidth();
                            int bitmapHeight2 = PhotoViewer.this.centerImage.getBitmapHeight();
                            if (PhotoViewer.this.editState.cropState != null) {
                                if (PhotoViewer.this.editState.cropState.transformRotation == 90 || PhotoViewer.this.editState.cropState.transformRotation == 270) {
                                    bitmapWidth2 = bitmapHeight2;
                                    bitmapHeight2 = bitmapWidth2;
                                }
                                bitmapWidth2 = (int) (bitmapWidth2 * PhotoViewer.this.editState.cropState.cropPw);
                                bitmapHeight2 = (int) (bitmapHeight2 * PhotoViewer.this.editState.cropState.cropPh);
                            }
                            float scaleX = PhotoViewer.this.getContainerViewWidth() / bitmapWidth2;
                            float scaleY = PhotoViewer.this.getContainerViewHeight() / bitmapHeight2;
                            float newScaleX = PhotoViewer.this.getContainerViewWidth(1) / bitmapWidth2;
                            float newScaleY = PhotoViewer.this.getContainerViewHeight(1) / bitmapHeight2;
                            float scale = Math.min(scaleX, scaleY);
                            float newScale2 = Math.min(newScaleX, newScaleY);
                            if (PhotoViewer.this.sendPhotoType == 1) {
                                float minSide = Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                                float newScaleX2 = minSide / bitmapWidth2;
                                float newScaleY2 = minSide / bitmapHeight2;
                                newScale2 = Math.max(newScaleX2, newScaleY2);
                            }
                            PhotoViewer.this.animateToScale = newScale2 / scale;
                            PhotoViewer.this.animateToX = (photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2);
                            PhotoViewer.this.animateToY = (-AndroidUtilities.dp(56.0f)) + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight / 2 : 0);
                            PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                            PhotoViewer.this.zoomAnimation = true;
                        }
                        PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                        PhotoViewer.this.imageMoveAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, View.TRANSLATION_Y, AndroidUtilities.dp(48.0f), 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, 0.0f, 1.0f));
                        PhotoViewer.this.imageMoveAnimation.setDuration(200L);
                        PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.57.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animation2) {
                                PhotoViewer.this.editorDoneLayout.setVisibility(0);
                                PhotoViewer.this.photoCropView.setVisibility(0);
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation2) {
                                PhotoViewer.this.photoCropView.onAppeared();
                                PhotoViewer.this.photoCropView.onShow();
                                PhotoViewer.this.imageMoveAnimation = null;
                                PhotoViewer.this.currentEditMode = mode;
                                PhotoViewer.this.switchingToMode = -1;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                                PhotoViewer.this.padImageForHorizontalInsets = true;
                                PhotoViewer.this.containerView.invalidate();
                            }
                        });
                        PhotoViewer.this.imageMoveAnimation.start();
                    }
                });
                this.changeModeAnimation.start();
            } else if (mode == 2) {
                startVideoPlayer();
                if (this.photoFilterView == null) {
                    MediaController.SavedFilterState state = null;
                    String originalPath = null;
                    int orientation = 0;
                    if (!this.imagesArrLocals.isEmpty()) {
                        Object object = this.imagesArrLocals.get(this.currentIndex);
                        if (object instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry entry = (MediaController.PhotoEntry) object;
                            orientation = entry.orientation;
                        }
                        MediaController.MediaEditState editState = (MediaController.MediaEditState) object;
                        state = editState.savedFilterState;
                        originalPath = editState.getPath();
                    }
                    if (this.videoTextureView != null) {
                        bitmap = null;
                    } else if (state == null) {
                        bitmap = this.centerImage.getBitmap();
                        orientation = this.centerImage.getOrientation();
                    } else {
                        bitmap = BitmapFactory.decodeFile(originalPath);
                    }
                    if (this.sendPhotoType == 1) {
                        hasFaces = 1;
                    } else if (this.isCurrentVideo || (i2 = this.currentImageHasFace) == 2) {
                        hasFaces = 2;
                    } else {
                        hasFaces = i2 == 1 ? 1 : 0;
                    }
                    Activity activity = this.parentActivity;
                    TextureView textureView = this.videoTextureView;
                    PhotoFilterView photoFilterView = new PhotoFilterView(activity, textureView != null ? (VideoEditTextureView) textureView : null, bitmap, orientation, state, this.isCurrentVideo ? null : this.paintingOverlay, hasFaces, textureView == null && ((this.editState.cropState != null && this.editState.cropState.mirrored) || this.cropTransform.isMirrored()), this.resourcesProvider);
                    this.photoFilterView = photoFilterView;
                    this.containerView.addView(photoFilterView, LayoutHelper.createFrame(-1, -1.0f));
                    this.photoFilterView.getDoneTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda29
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            PhotoViewer.this.m4258lambda$switchToEditMode$58$orgtelegramuiPhotoViewer(view3);
                        }
                    });
                    this.photoFilterView.getCancelTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda30
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            PhotoViewer.this.m4260lambda$switchToEditMode$60$orgtelegramuiPhotoViewer(view3);
                        }
                    });
                    this.photoFilterView.getToolsView().setTranslationY(AndroidUtilities.dp(186.0f));
                }
                this.changeModeAnimation = new AnimatorSet();
                ArrayList<Animator> arrayList2 = new ArrayList<>();
                FrameLayout frameLayout2 = this.pickerView;
                Property property4 = View.TRANSLATION_Y;
                float[] fArr4 = new float[2];
                fArr4[0] = 0.0f;
                fArr4[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList2.add(ObjectAnimator.ofFloat(frameLayout2, property4, fArr4));
                ImageView imageView2 = this.pickerViewSendButton;
                Property property5 = View.TRANSLATION_Y;
                float[] fArr5 = new float[2];
                fArr5[0] = 0.0f;
                fArr5[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList2.add(ObjectAnimator.ofFloat(imageView2, property5, fArr5));
                arrayList2.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, 0.0f, -this.actionBar.getHeight()));
                int i9 = this.sendPhotoType;
                if (i9 == 0 || i9 == 4) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, 1.0f, 0.0f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, 1.0f, 0.0f));
                } else if (i9 == 1) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.cameraItem.getTag() != null) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.muteItem.getTag() == null) {
                    i = 2;
                } else {
                    i = 2;
                    arrayList2.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, 1.0f, 0.0f));
                }
                View view3 = this.navigationBar;
                ArgbEvaluator argbEvaluator2 = new ArgbEvaluator();
                Object[] objArr2 = new Object[i];
                objArr2[0] = Integer.valueOf(navigationBarColorFrom);
                objArr2[1] = Integer.valueOf(navigationBarColorTo);
                arrayList2.add(ObjectAnimator.ofObject(view3, TtmlNode.ATTR_TTS_BACKGROUND_COLOR, argbEvaluator2, objArr2));
                this.changeModeAnimation.playTogether(arrayList2);
                this.changeModeAnimation.setDuration(200L);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.58
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        float oldScale2;
                        PhotoViewer.this.changeModeAnimation = null;
                        PhotoViewer.this.pickerView.setVisibility(8);
                        PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                        PhotoViewer.this.actionBar.setVisibility(8);
                        PhotoViewer.this.cameraItem.setVisibility(8);
                        PhotoViewer.this.muteItem.setVisibility(8);
                        if (PhotoViewer.this.photoCropView != null) {
                            PhotoViewer.this.photoCropView.setVisibility(4);
                        }
                        PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0f));
                        PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                        PhotoViewer.this.isPhotosListViewVisible = false;
                        if (PhotoViewer.this.needCaptionLayout) {
                            PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                            PhotoViewer.this.checkImageView.setVisibility(8);
                            PhotoViewer.this.photosCounterView.setVisibility(8);
                        }
                        Bitmap bitmap3 = PhotoViewer.this.centerImage.getBitmap();
                        if (bitmap3 != null) {
                            int bitmapWidth2 = PhotoViewer.this.centerImage.getBitmapWidth();
                            int bitmapHeight2 = PhotoViewer.this.centerImage.getBitmapHeight();
                            float newScale2 = Math.min(PhotoViewer.this.getContainerViewWidth(2) / bitmapWidth2, PhotoViewer.this.getContainerViewHeight(2) / bitmapHeight2);
                            if (PhotoViewer.this.sendPhotoType == 1) {
                                PhotoViewer.this.animateToY = -AndroidUtilities.dp(36.0f);
                                oldScale2 = PhotoViewer.this.getCropFillScale(false);
                            } else {
                                PhotoViewer.this.animateToY = (-AndroidUtilities.dp(93.0f)) + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight / 2 : 0);
                                oldScale2 = (PhotoViewer.this.editState.cropState == null || !(PhotoViewer.this.editState.cropState.transformRotation == 90 || PhotoViewer.this.editState.cropState.transformRotation == 270)) ? Math.min(PhotoViewer.this.getContainerViewWidth() / bitmapWidth2, PhotoViewer.this.getContainerViewHeight() / bitmapHeight2) : Math.min(PhotoViewer.this.getContainerViewWidth() / bitmapHeight2, PhotoViewer.this.getContainerViewHeight() / bitmapWidth2);
                            }
                            PhotoViewer.this.animateToScale = newScale2 / oldScale2;
                            PhotoViewer photoViewer = PhotoViewer.this;
                            photoViewer.animateToX = (photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2);
                            PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                            PhotoViewer.this.zoomAnimation = true;
                        }
                        PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                        PhotoViewer.this.imageMoveAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(186.0f), 0.0f));
                        PhotoViewer.this.imageMoveAnimation.setDuration(200L);
                        PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.58.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animation2) {
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation2) {
                                PhotoViewer.this.photoFilterView.init();
                                PhotoViewer.this.imageMoveAnimation = null;
                                PhotoViewer.this.currentEditMode = mode;
                                PhotoViewer.this.switchingToMode = -1;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                                PhotoViewer.this.padImageForHorizontalInsets = true;
                                PhotoViewer.this.containerView.invalidate();
                            }
                        });
                        PhotoViewer.this.imageMoveAnimation.start();
                    }
                });
                this.changeModeAnimation.start();
            } else if (mode == 3) {
                startVideoPlayer();
                createPaintView();
                this.changeModeAnimation = new AnimatorSet();
                ArrayList<Animator> arrayList3 = new ArrayList<>();
                FrameLayout frameLayout3 = this.pickerView;
                Property property6 = View.TRANSLATION_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList3.add(ObjectAnimator.ofFloat(frameLayout3, property6, fArr6));
                ImageView imageView3 = this.pickerViewSendButton;
                Property property7 = View.TRANSLATION_Y;
                float[] fArr7 = new float[1];
                fArr7[0] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList3.add(ObjectAnimator.ofFloat(imageView3, property7, fArr7));
                arrayList3.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, -this.actionBar.getHeight()));
                arrayList3.add(ObjectAnimator.ofObject(this.navigationBar, TtmlNode.ATTR_TTS_BACKGROUND_COLOR, new ArgbEvaluator(), Integer.valueOf(navigationBarColorFrom), Integer.valueOf(navigationBarColorTo)));
                if (this.needCaptionLayout) {
                    CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
                    Property property8 = View.TRANSLATION_Y;
                    float[] fArr8 = new float[1];
                    fArr8[0] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                    arrayList3.add(ObjectAnimator.ofFloat(captionTextViewSwitcher2, property8, fArr8));
                }
                int i10 = this.sendPhotoType;
                if (i10 == 0 || i10 == 4) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, 1.0f, 0.0f));
                    arrayList3.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, 1.0f, 0.0f));
                } else if (i10 == 1) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.cameraItem.getTag() != null) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, 1.0f, 0.0f));
                }
                if (this.muteItem.getTag() != null) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, 1.0f, 0.0f));
                }
                this.changeModeAnimation.playTogether(arrayList3);
                this.changeModeAnimation.setDuration(200L);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.59
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhotoViewer.this.switchToPaintMode();
                    }
                });
                this.changeModeAnimation.start();
            }
        }
    }

    /* renamed from: lambda$switchToEditMode$57$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4257lambda$switchToEditMode$57$orgtelegramuiPhotoViewer(ValueAnimator a) {
        this.photoCropView.cropView.areaView.setRotationScaleTranslation(0.0f, AndroidUtilities.lerp(this.scale, this.animateToScale, this.animationValue), AndroidUtilities.lerp(this.translationX, this.animateToX, this.animationValue), AndroidUtilities.lerp(this.translationY, this.animateToY, this.animationValue));
    }

    /* renamed from: lambda$switchToEditMode$58$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4258lambda$switchToEditMode$58$orgtelegramuiPhotoViewer(View v) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    /* renamed from: lambda$switchToEditMode$60$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4260lambda$switchToEditMode$60$orgtelegramuiPhotoViewer(View v) {
        if (this.photoFilterView.hasChanges()) {
            if (this.parentActivity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity, this.resourcesProvider);
            builder.setMessage(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda79
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PhotoViewer.this.m4259lambda$switchToEditMode$59$orgtelegramuiPhotoViewer(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showAlertDialog(builder);
            return;
        }
        switchToEditMode(0);
    }

    /* renamed from: lambda$switchToEditMode$59$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4259lambda$switchToEditMode$59$orgtelegramuiPhotoViewer(DialogInterface dialogInterface, int i) {
        switchToEditMode(0);
    }

    private void createPaintView() {
        int h;
        int w;
        MediaController.CropState state;
        if (this.photoPaintView == null) {
            TextureView textureView = this.videoTextureView;
            if (textureView != null) {
                VideoEditTextureView textureView2 = (VideoEditTextureView) textureView;
                w = textureView2.getVideoWidth();
                h = textureView2.getVideoHeight();
                while (true) {
                    if (w <= 1280 && h <= 1280) {
                        break;
                    }
                    w /= 2;
                    h /= 2;
                }
            } else {
                w = this.centerImage.getBitmapWidth();
                h = this.centerImage.getBitmapHeight();
            }
            Bitmap bitmap = this.paintingOverlay.getBitmap();
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            }
            if (this.sendPhotoType == 1) {
                MediaController.CropState state2 = new MediaController.CropState();
                state2.transformRotation = this.cropTransform.getOrientation();
                state = state2;
            } else {
                state = this.editState.cropState;
            }
            PhotoPaintView photoPaintView = new PhotoPaintView(this.parentActivity, bitmap, this.isCurrentVideo ? null : this.centerImage.getBitmap(), this.centerImage.getOrientation(), this.editState.mediaEntities, state, new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda42
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.m4203lambda$createPaintView$61$orgtelegramuiPhotoViewer();
                }
            }, this.resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.60
                @Override // org.telegram.ui.Components.PhotoPaintView
                protected void onOpenCloseStickersAlert(boolean open) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.manuallyPaused = false;
                        PhotoViewer.this.cancelVideoPlayRunnable();
                        if (open) {
                            PhotoViewer.this.videoPlayer.pause();
                        } else {
                            PhotoViewer.this.videoPlayer.play();
                        }
                    }
                }

                @Override // org.telegram.ui.Components.PhotoPaintView
                protected void didSetAnimatedSticker(RLottieDrawable drawable) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        long currentPosition = PhotoViewer.this.videoPlayer.getCurrentPosition();
                        long j = 0;
                        if (PhotoViewer.this.startTime > 0) {
                            j = PhotoViewer.this.startTime / 1000;
                        }
                        drawable.setProgressMs(currentPosition - j);
                    }
                }

                @Override // org.telegram.ui.Components.PhotoPaintView
                protected void onTextAdd() {
                    if (!PhotoViewer.this.windowView.isFocusable()) {
                        PhotoViewer.this.makeFocusable();
                    }
                }
            };
            this.photoPaintView = photoPaintView;
            this.containerView.addView(photoPaintView, LayoutHelper.createFrame(-1, -1.0f));
            this.photoPaintView.getDoneTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoViewer.this.m4204lambda$createPaintView$62$orgtelegramuiPhotoViewer(view);
                }
            });
            this.photoPaintView.getCancelTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoViewer.this.m4205lambda$createPaintView$63$orgtelegramuiPhotoViewer(view);
                }
            });
            this.photoPaintView.getColorPicker().setTranslationY(AndroidUtilities.dp(126.0f));
            this.photoPaintView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0f));
            this.photoPaintView.getColorPickerBackground().setTranslationY(AndroidUtilities.dp(126.0f));
        }
    }

    /* renamed from: lambda$createPaintView$61$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4203lambda$createPaintView$61$orgtelegramuiPhotoViewer() {
        this.paintingOverlay.hideBitmap();
    }

    /* renamed from: lambda$createPaintView$62$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4204lambda$createPaintView$62$orgtelegramuiPhotoViewer(View v) {
        this.savedState = null;
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    /* renamed from: lambda$createPaintView$63$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4205lambda$createPaintView$63$orgtelegramuiPhotoViewer(View v) {
        closePaintMode();
    }

    private void closePaintMode() {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4199lambda$closePaintMode$64$orgtelegramuiPhotoViewer();
            }
        });
    }

    /* renamed from: lambda$closePaintMode$64$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4199lambda$closePaintMode$64$orgtelegramuiPhotoViewer() {
        switchToEditMode(0);
    }

    public void switchToPaintMode() {
        this.changeModeAnimation = null;
        this.pickerView.setVisibility(8);
        this.pickerViewSendButton.setVisibility(8);
        this.cameraItem.setVisibility(8);
        this.muteItem.setVisibility(8);
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView != null) {
            photoCropView.setVisibility(4);
        }
        this.selectedPhotosListView.setVisibility(8);
        this.selectedPhotosListView.setAlpha(0.0f);
        this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0f));
        this.photosCounterView.setRotationX(0.0f);
        this.selectedPhotosListView.setEnabled(false);
        this.isPhotosListViewVisible = false;
        if (this.needCaptionLayout) {
            this.captionTextViewSwitcher.setVisibility(4);
        }
        int i = this.sendPhotoType;
        if (i == 0 || i == 4 || ((i == 2 || i == 5) && this.imagesArrLocals.size() > 1)) {
            this.checkImageView.setVisibility(8);
            this.photosCounterView.setVisibility(8);
        }
        Bitmap bitmap = this.centerImage.getBitmap();
        float f = this.scale;
        if (bitmap != null) {
            int bitmapWidth = this.centerImage.getBitmapWidth();
            int bitmapHeight = this.centerImage.getBitmapHeight();
            if (this.sendPhotoType == 1) {
                this.animateToY = AndroidUtilities.dp(12.0f);
                if (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270) {
                    bitmapWidth = bitmapHeight;
                    bitmapHeight = bitmapWidth;
                }
            } else {
                this.animateToY = (-AndroidUtilities.dp(44.0f)) + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight / 2 : 0);
                if (this.editState.cropState != null) {
                    if (this.editState.cropState.transformRotation == 90 || this.editState.cropState.transformRotation == 270) {
                        bitmapWidth = bitmapHeight;
                        bitmapHeight = bitmapWidth;
                    }
                    bitmapWidth = (int) (bitmapWidth * this.editState.cropState.cropPw);
                    bitmapHeight = (int) (bitmapHeight * this.editState.cropState.cropPh);
                }
            }
            float oldScale = Math.min(getContainerViewWidth() / bitmapWidth, getContainerViewHeight() / bitmapHeight);
            float newScale = Math.min(getContainerViewWidth(3) / bitmapWidth, getContainerViewHeight(3) / bitmapHeight);
            this.animateToScale = newScale / oldScale;
            this.animateToX = (getLeftInset() / 2) - (getRightInset() / 2);
            this.animationStartTime = System.currentTimeMillis();
            this.zoomAnimation = true;
        }
        this.windowView.setClipChildren(true);
        this.navigationBar.setVisibility(4);
        AnimatorSet animatorSet = new AnimatorSet();
        this.imageMoveAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f), 0.0f), ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f), 0.0f), ObjectAnimator.ofFloat(this.photoPaintView.getColorPickerBackground(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f), 0.0f));
        this.imageMoveAnimation.setDuration(200L);
        this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.61
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PhotoViewer.this.photoPaintView.init();
                PhotoViewer.this.paintingOverlay.hideEntities();
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = 3;
                PhotoViewer.this.switchingToMode = -1;
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.animateToScale = photoViewer.scale = 1.0f;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer photoViewer2 = PhotoViewer.this;
                photoViewer2.updateMinMax(photoViewer2.scale);
                PhotoViewer.this.padImageForHorizontalInsets = true;
                PhotoViewer.this.containerView.invalidate();
            }
        });
        this.imageMoveAnimation.start();
    }

    private void toggleCheckImageView(boolean show) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> arrayList = new ArrayList<>();
        float offsetY = AndroidUtilities.dpf2(24.0f);
        FrameLayout frameLayout = this.pickerView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        float f2 = 0.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        FrameLayout frameLayout2 = this.pickerView;
        Property property2 = View.TRANSLATION_Y;
        float[] fArr2 = new float[1];
        fArr2[0] = show ? 0.0f : offsetY;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
        ImageView imageView = this.pickerViewSendButton;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property3, fArr3));
        ImageView imageView2 = this.pickerViewSendButton;
        Property property4 = View.TRANSLATION_Y;
        float[] fArr4 = new float[1];
        fArr4[0] = show ? 0.0f : offsetY;
        arrayList.add(ObjectAnimator.ofFloat(imageView2, property4, fArr4));
        int i = this.sendPhotoType;
        if (i == 0 || i == 4) {
            CheckBox checkBox = this.checkImageView;
            Property property5 = View.ALPHA;
            float[] fArr5 = new float[1];
            fArr5[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, property5, fArr5));
            CheckBox checkBox2 = this.checkImageView;
            Property property6 = View.TRANSLATION_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = show ? 0.0f : -offsetY;
            arrayList.add(ObjectAnimator.ofFloat(checkBox2, property6, fArr6));
            CounterView counterView = this.photosCounterView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr7[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, property7, fArr7));
            CounterView counterView2 = this.photosCounterView;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!show) {
                f2 = -offsetY;
            }
            fArr8[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(counterView2, property8, fArr8));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200L);
        animatorSet.start();
    }

    private void toggleMiniProgressInternal(final boolean show) {
        if (show) {
            this.miniProgressView.setVisibility(0);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.miniProgressAnimator = animatorSet;
        Animator[] animatorArr = new Animator[1];
        RadialProgressView radialProgressView = this.miniProgressView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, property, fArr);
        animatorSet.playTogether(animatorArr);
        this.miniProgressAnimator.setDuration(200L);
        this.miniProgressAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.62
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.miniProgressAnimator)) {
                    if (!show) {
                        PhotoViewer.this.miniProgressView.setVisibility(4);
                    }
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (animation.equals(PhotoViewer.this.miniProgressAnimator)) {
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }
        });
        this.miniProgressAnimator.start();
    }

    private void toggleMiniProgress(boolean show, boolean animated) {
        AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
        int i = 0;
        if (animated) {
            toggleMiniProgressInternal(show);
            if (show) {
                AnimatorSet animatorSet = this.miniProgressAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.miniProgressAnimator = null;
                }
                if (this.firstAnimationDelay) {
                    this.firstAnimationDelay = false;
                    toggleMiniProgressInternal(true);
                    return;
                }
                AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500L);
                return;
            }
            AnimatorSet animatorSet2 = this.miniProgressAnimator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                toggleMiniProgressInternal(false);
                return;
            }
            return;
        }
        AnimatorSet animatorSet3 = this.miniProgressAnimator;
        if (animatorSet3 != null) {
            animatorSet3.cancel();
            this.miniProgressAnimator = null;
        }
        this.miniProgressView.setAlpha(show ? 1.0f : 0.0f);
        RadialProgressView radialProgressView = this.miniProgressView;
        if (!show) {
            i = 4;
        }
        radialProgressView.setVisibility(i);
    }

    private void updateContainerFlags(boolean actionBarVisible) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (Build.VERSION.SDK_INT >= 21 && this.sendPhotoType != 1 && (frameLayoutDrawer = this.containerView) != null) {
            int flags = 1792;
            if (!actionBarVisible) {
                flags = 1792 | 4;
                if (frameLayoutDrawer.getPaddingLeft() > 0 || this.containerView.getPaddingRight() > 0) {
                    flags |= InputDeviceCompat.SOURCE_TOUCHSCREEN;
                }
            }
            this.containerView.setSystemUiVisibility(flags);
        }
    }

    /* loaded from: classes4.dex */
    public static class ActionBarToggleParams {
        public static final ActionBarToggleParams DEFAULT = new ActionBarToggleParams();
        public Interpolator animationInterpolator;
        public int animationDuration = 200;
        public boolean enableStatusBarAnimation = true;
        public boolean enableTranslationAnimation = true;

        public ActionBarToggleParams enableStatusBarAnimation(boolean val) {
            this.enableStatusBarAnimation = val;
            return this;
        }

        public ActionBarToggleParams enableTranslationAnimation(boolean val) {
            this.enableTranslationAnimation = val;
            return this;
        }

        public ActionBarToggleParams animationDuration(int val) {
            this.animationDuration = val;
            return this;
        }

        public ActionBarToggleParams animationInterpolator(Interpolator val) {
            this.animationInterpolator = val;
            return this;
        }
    }

    public void toggleActionBar(boolean show, boolean animated) {
        toggleActionBar(show, animated, ActionBarToggleParams.DEFAULT);
    }

    public void toggleActionBar(final boolean show, boolean animated, ActionBarToggleParams params) {
        CaptionScrollView captionScrollView;
        CaptionScrollView captionScrollView2;
        if (this.currentEditMode == 0) {
            int i = this.switchingToMode;
            if (i != 0 && i != -1) {
                return;
            }
            AnimatorSet animatorSet = this.actionBarAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (show) {
                this.actionBar.setVisibility(0);
                if (this.bottomLayout.getTag() != null) {
                    this.bottomLayout.setVisibility(0);
                }
                if (this.captionTextViewSwitcher.getTag() != null) {
                    this.captionTextViewSwitcher.setVisibility(0);
                    VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
                    if (videoSeekPreviewImage != null) {
                        videoSeekPreviewImage.requestLayout();
                    }
                }
            }
            this.isActionBarVisible = show;
            if (params.enableStatusBarAnimation) {
                updateContainerFlags(show);
            }
            if (this.videoPlayerControlVisible && this.isPlaying && show) {
                scheduleActionBarHide();
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            }
            if (!show) {
                Bulletin.hide(this.containerView);
            }
            float offsetY = AndroidUtilities.dpf2(24.0f);
            this.videoPlayerControlFrameLayout.setSeekBarTransitionEnabled(params.enableTranslationAnimation && this.playerLooping);
            this.videoPlayerControlFrameLayout.setTranslationYAnimationEnabled(params.enableTranslationAnimation);
            float f = 1.0f;
            if (animated) {
                ArrayList<Animator> arrayList = new ArrayList<>();
                ActionBar actionBar = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBar, property, fArr));
                if (params.enableTranslationAnimation) {
                    ActionBar actionBar2 = this.actionBar;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    fArr2[0] = show ? 0.0f : -offsetY;
                    arrayList.add(ObjectAnimator.ofFloat(actionBar2, property2, fArr2));
                } else {
                    this.actionBar.setTranslationY(0.0f);
                }
                if (this.allowShowFullscreenButton) {
                    ImageView imageView = this.fullscreenButton[0];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = show ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(imageView, property3, fArr3));
                }
                for (int a = 1; a < 3; a++) {
                    this.fullscreenButton[a].setTranslationY(show ? 0.0f : offsetY);
                }
                if (params.enableTranslationAnimation) {
                    ImageView imageView2 = this.fullscreenButton[0];
                    Property property4 = View.TRANSLATION_Y;
                    float[] fArr4 = new float[1];
                    fArr4[0] = show ? 0.0f : offsetY;
                    arrayList.add(ObjectAnimator.ofFloat(imageView2, property4, fArr4));
                } else {
                    this.fullscreenButton[0].setTranslationY(0.0f);
                }
                FrameLayout frameLayout = this.bottomLayout;
                if (frameLayout != null) {
                    Property property5 = View.ALPHA;
                    float[] fArr5 = new float[1];
                    fArr5[0] = show ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(frameLayout, property5, fArr5));
                    if (params.enableTranslationAnimation) {
                        FrameLayout frameLayout2 = this.bottomLayout;
                        Property property6 = View.TRANSLATION_Y;
                        float[] fArr6 = new float[1];
                        fArr6[0] = show ? 0.0f : offsetY;
                        arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property6, fArr6));
                    } else {
                        this.bottomLayout.setTranslationY(0.0f);
                    }
                }
                View view = this.navigationBar;
                if (view != null) {
                    Property property7 = View.ALPHA;
                    float[] fArr7 = new float[1];
                    fArr7[0] = show ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(view, property7, fArr7));
                }
                if (this.videoPlayerControlVisible) {
                    VideoPlayerControlFrameLayout videoPlayerControlFrameLayout = this.videoPlayerControlFrameLayout;
                    Property<VideoPlayerControlFrameLayout, Float> property8 = VPC_PROGRESS;
                    float[] fArr8 = new float[1];
                    fArr8[0] = show ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(videoPlayerControlFrameLayout, property8, fArr8));
                } else {
                    this.videoPlayerControlFrameLayout.setProgress(show ? 1.0f : 0.0f);
                }
                GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
                Property property9 = View.ALPHA;
                float[] fArr9 = new float[1];
                fArr9[0] = show ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView, property9, fArr9));
                if (params.enableTranslationAnimation) {
                    GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
                    Property property10 = View.TRANSLATION_Y;
                    float[] fArr10 = new float[1];
                    fArr10[0] = show ? 0.0f : offsetY;
                    arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property10, fArr10));
                } else {
                    this.groupedPhotosListView.setTranslationY(0.0f);
                }
                if (!this.needCaptionLayout && (captionScrollView2 = this.captionScrollView) != null) {
                    Property property11 = View.ALPHA;
                    float[] fArr11 = new float[1];
                    fArr11[0] = show ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(captionScrollView2, property11, fArr11));
                    if (params.enableTranslationAnimation) {
                        CaptionScrollView captionScrollView3 = this.captionScrollView;
                        Property property12 = View.TRANSLATION_Y;
                        float[] fArr12 = new float[1];
                        fArr12[0] = show ? 0.0f : offsetY;
                        arrayList.add(ObjectAnimator.ofFloat(captionScrollView3, property12, fArr12));
                    } else {
                        this.captionScrollView.setTranslationY(0.0f);
                    }
                }
                if (this.videoPlayerControlVisible && this.isPlaying) {
                    float[] fArr13 = new float[2];
                    fArr13[0] = this.photoProgressViews[0].animAlphas[1];
                    fArr13[1] = show ? 1.0f : 0.0f;
                    ValueAnimator anim = ValueAnimator.ofFloat(fArr13);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda22
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.this.m4262lambda$toggleActionBar$65$orgtelegramuiPhotoViewer(valueAnimator);
                        }
                    });
                    arrayList.add(anim);
                }
                if (this.muteItem.getTag() != null) {
                    ImageView imageView3 = this.muteItem;
                    Property property13 = View.ALPHA;
                    float[] fArr14 = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr14[0] = f;
                    arrayList.add(ObjectAnimator.ofFloat(imageView3, property13, fArr14));
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimator = animatorSet2;
                animatorSet2.playTogether(arrayList);
                this.actionBarAnimator.setDuration(params.animationDuration);
                this.actionBarAnimator.setInterpolator(params.animationInterpolator);
                this.actionBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.63
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoViewer.this.actionBarAnimator)) {
                            if (!show) {
                                PhotoViewer.this.actionBar.setVisibility(4);
                                if (PhotoViewer.this.bottomLayout.getTag() != null) {
                                    PhotoViewer.this.bottomLayout.setVisibility(4);
                                }
                                if (PhotoViewer.this.captionTextViewSwitcher.getTag() != null) {
                                    PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                                }
                            }
                            PhotoViewer.this.actionBarAnimator = null;
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        if (animation.equals(PhotoViewer.this.actionBarAnimator)) {
                            PhotoViewer.this.actionBarAnimator = null;
                        }
                    }
                });
                this.actionBarAnimator.start();
                return;
            }
            this.actionBar.setAlpha(show ? 1.0f : 0.0f);
            if (this.fullscreenButton[0].getTranslationX() != 0.0f && this.allowShowFullscreenButton) {
                this.fullscreenButton[0].setAlpha(show ? 1.0f : 0.0f);
            }
            for (int a2 = 0; a2 < 3; a2++) {
                this.fullscreenButton[a2].setTranslationY(show ? 0.0f : offsetY);
            }
            this.actionBar.setTranslationY(show ? 0.0f : -offsetY);
            this.bottomLayout.setAlpha(show ? 1.0f : 0.0f);
            this.bottomLayout.setTranslationY(show ? 0.0f : offsetY);
            this.navigationBar.setAlpha(show ? 1.0f : 0.0f);
            this.groupedPhotosListView.setAlpha(show ? 1.0f : 0.0f);
            this.groupedPhotosListView.setTranslationY(show ? 0.0f : offsetY);
            if (!this.needCaptionLayout && (captionScrollView = this.captionScrollView) != null) {
                captionScrollView.setAlpha(show ? 1.0f : 0.0f);
                this.captionScrollView.setTranslationY(show ? 0.0f : offsetY);
            }
            this.videoPlayerControlFrameLayout.setProgress(show ? 1.0f : 0.0f);
            if (this.muteItem.getTag() != null) {
                this.muteItem.setAlpha(show ? 1.0f : 0.0f);
            }
            if (this.videoPlayerControlVisible && this.isPlaying) {
                PhotoProgressView photoProgressView = this.photoProgressViews[0];
                if (!show) {
                    f = 0.0f;
                }
                photoProgressView.setIndexedAlpha(1, f, false);
            }
        }
    }

    /* renamed from: lambda$toggleActionBar$65$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4262lambda$toggleActionBar$65$orgtelegramuiPhotoViewer(ValueAnimator a) {
        this.photoProgressViews[0].setIndexedAlpha(1, ((Float) a.getAnimatedValue()).floatValue(), false);
    }

    private void togglePhotosListView(boolean show, boolean animated) {
        if (show == this.isPhotosListViewVisible) {
            return;
        }
        if (show) {
            this.selectedPhotosListView.setVisibility(0);
        }
        this.isPhotosListViewVisible = show;
        this.selectedPhotosListView.setEnabled(show);
        float f = 1.0f;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList<>();
            SelectedPhotosListView selectedPhotosListView = this.selectedPhotosListView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(selectedPhotosListView, property, fArr));
            SelectedPhotosListView selectedPhotosListView2 = this.selectedPhotosListView;
            Property property2 = View.TRANSLATION_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 0.0f : -AndroidUtilities.dp(10.0f);
            arrayList.add(ObjectAnimator.ofFloat(selectedPhotosListView2, property2, fArr2));
            CounterView counterView = this.photosCounterView;
            Property property3 = View.ROTATION_X;
            float[] fArr3 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr3[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, property3, fArr3));
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentListViewAnimation = animatorSet;
            animatorSet.playTogether(arrayList);
            if (!show) {
                this.currentListViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.64
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoViewer.this.currentListViewAnimation != null && PhotoViewer.this.currentListViewAnimation.equals(animation)) {
                            PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                            PhotoViewer.this.currentListViewAnimation = null;
                        }
                    }
                });
            }
            this.currentListViewAnimation.setDuration(200L);
            this.currentListViewAnimation.start();
            return;
        }
        this.selectedPhotosListView.setAlpha(show ? 1.0f : 0.0f);
        this.selectedPhotosListView.setTranslationY(show ? 0.0f : -AndroidUtilities.dp(10.0f));
        CounterView counterView2 = this.photosCounterView;
        if (!show) {
            f = 0.0f;
        }
        counterView2.setRotationX(f);
        if (!show) {
            this.selectedPhotosListView.setVisibility(8);
        }
    }

    public void toggleVideoPlayer() {
        if (this.videoPlayer == null) {
            return;
        }
        cancelVideoPlayRunnable();
        AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
        if (this.isPlaying) {
            this.videoPlayer.pause();
        } else {
            if (this.isCurrentVideo) {
                if (Math.abs(this.videoTimelineView.getProgress() - this.videoTimelineView.getRightProgress()) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                    this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration())));
                }
            } else {
                if (Math.abs(this.videoPlayerSeekbar.getProgress() - this.videoTimelineView.getRightProgress()) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                    this.videoPlayer.seekTo(0L);
                }
                scheduleActionBarHide();
            }
            this.videoPlayer.play();
        }
        this.containerView.invalidate();
    }

    private String getFileName(int index) {
        if (index < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (index >= this.secureDocuments.size()) {
                return null;
            }
            SecureDocument location = this.secureDocuments.get(index);
            return location.secureFile.dc_id + "_" + location.secureFile.id + ".jpg";
        } else if (!this.imagesArrLocations.isEmpty() || !this.imagesArr.isEmpty()) {
            if (!this.imagesArrLocations.isEmpty()) {
                if (index >= this.imagesArrLocations.size()) {
                    return null;
                }
                ImageLocation location2 = this.imagesArrLocations.get(index);
                ImageLocation videoLocation = this.imagesArrLocationsVideo.get(index);
                if (location2 == null) {
                    return null;
                }
                if (videoLocation != location2) {
                    return videoLocation.location.volume_id + "_" + videoLocation.location.local_id + ".mp4";
                }
                return location2.location.volume_id + "_" + location2.location.local_id + ".jpg";
            } else if (index >= this.imagesArr.size()) {
                return null;
            } else {
                return FileLoader.getMessageFileName(this.imagesArr.get(index).messageOwner);
            }
        } else {
            if (!this.imagesArrLocals.isEmpty()) {
                if (index >= this.imagesArrLocals.size()) {
                    return null;
                }
                Object object = this.imagesArrLocals.get(index);
                if (object instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                    return searchImage.getAttachName();
                } else if (object instanceof TLRPC.BotInlineResult) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) object;
                    if (botInlineResult.document != null) {
                        return FileLoader.getAttachFileName(botInlineResult.document);
                    }
                    if (botInlineResult.photo != null) {
                        TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                        return FileLoader.getAttachFileName(sizeFull);
                    } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        return Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, FileLoader.getMimeTypePart(botInlineResult.content.mime_type));
                    }
                }
            } else {
                PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                if (pageBlocksAdapter != null) {
                    return pageBlocksAdapter.getFileName(index);
                }
            }
            return null;
        }
    }

    private ImageLocation getImageLocation(int index, long[] size) {
        if (index < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (index >= this.secureDocuments.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.secureDocuments.get(index).secureFile.size;
            }
            return ImageLocation.getForSecureDocument(this.secureDocuments.get(index));
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (index >= this.imagesArrLocations.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.imagesArrLocationsSizes.get(index).longValue();
            }
            return this.imagesArrLocationsVideo.get(index);
        } else if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject message = this.imagesArr.get(index);
            if (message.messageOwner instanceof TLRPC.TL_messageService) {
                if (message.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    return null;
                }
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    if (size != null) {
                        size[0] = sizeFull.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(sizeFull, message.photoThumbsObject);
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (((message.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
                if (!message.isGif()) {
                    TLRPC.PhotoSize sizeFull2 = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize(), false, null, true);
                    if (sizeFull2 != null) {
                        if (size != null) {
                            size[0] = sizeFull2.size;
                            if (size[0] == 0) {
                                size[0] = -1;
                            }
                        }
                        return ImageLocation.getForObject(sizeFull2, message.photoThumbsObject);
                    } else if (size != null) {
                        size[0] = -1;
                    }
                } else {
                    return ImageLocation.getForDocument(message.getDocument());
                }
            } else if (message.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC.TL_messageMediaInvoice) message.messageOwner.media).photo));
            } else {
                if (message.getDocument() != null) {
                    TLRPC.Document document = message.getDocument();
                    if (this.sharedMediaType == 5) {
                        return ImageLocation.getForDocument(document);
                    }
                    if (MessageObject.isDocumentHasThumb(message.getDocument())) {
                        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (size != null) {
                            size[0] = thumb.size;
                            if (size[0] == 0) {
                                size[0] = -1;
                            }
                        }
                        return ImageLocation.getForDocument(thumb, document);
                    }
                }
            }
            return null;
        }
    }

    public TLObject getFileLocation(int index, long[] size) {
        if (index < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (index >= this.secureDocuments.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.secureDocuments.get(index).secureFile.size;
            }
            return this.secureDocuments.get(index);
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (index >= this.imagesArrLocations.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.imagesArrLocationsSizes.get(index).longValue();
            }
            return this.imagesArrLocationsVideo.get(index).location;
        } else if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject message = this.imagesArr.get(index);
            if (message.messageOwner instanceof TLRPC.TL_messageService) {
                if (message.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    return message.messageOwner.action.newUserPhoto.photo_big;
                }
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    if (size != null) {
                        size[0] = sizeFull.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return sizeFull;
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (((message.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
                TLRPC.PhotoSize sizeFull2 = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize(), false, null, true);
                if (sizeFull2 != null) {
                    if (size != null) {
                        size[0] = sizeFull2.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return sizeFull2;
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (message.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                return ((TLRPC.TL_messageMediaInvoice) message.messageOwner.media).photo;
            } else {
                if (message.getDocument() != null && MessageObject.isDocumentHasThumb(message.getDocument())) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(message.getDocument().thumbs, 90);
                    if (size != null) {
                        size[0] = thumb.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return thumb;
                }
            }
            return null;
        }
    }

    public void updateSelectedCount() {
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider == null) {
            return;
        }
        int count = photoViewerProvider.getSelectedCount();
        this.photosCounterView.setCount(count);
        if (count == 0) {
            togglePhotosListView(false, true);
        }
    }

    public boolean isCurrentAvatarSet() {
        int i;
        if (this.currentAvatarLocation == null || (i = this.currentIndex) < 0 || i >= this.avatarsArr.size()) {
            return false;
        }
        TLRPC.Photo photo = this.avatarsArr.get(this.currentIndex);
        ImageLocation currentLocation = this.imagesArrLocations.get(this.currentIndex);
        if (photo instanceof TLRPC.TL_photoEmpty) {
            photo = null;
        }
        if (photo != null) {
            int N = photo.sizes.size();
            for (int a = 0; a < N; a++) {
                TLRPC.PhotoSize size = photo.sizes.get(a);
                if (size.location != null && size.location.local_id == this.currentAvatarLocation.location.local_id && size.location.volume_id == this.currentAvatarLocation.location.volume_id) {
                    return true;
                }
            }
        } else if (currentLocation.location.local_id == this.currentAvatarLocation.location.local_id && currentLocation.location.volume_id == this.currentAvatarLocation.location.volume_id) {
            return true;
        }
        return false;
    }

    private void setItemVisible(View itemView, boolean visible, boolean animate) {
        setItemVisible(itemView, visible, animate, 1.0f);
    }

    private void setItemVisible(final View itemView, final boolean visible, boolean animate, float maxAlpha) {
        Boolean visibleNow = this.actionBarItemsVisibility.get(itemView);
        if (visibleNow == null || visibleNow.booleanValue() != visible) {
            this.actionBarItemsVisibility.put(itemView, Boolean.valueOf(visible));
            itemView.animate().cancel();
            float alpha = (visible ? 1.0f : 0.0f) * maxAlpha;
            int i = 0;
            if (animate && visibleNow != null) {
                if (visible) {
                    itemView.setVisibility(0);
                }
                itemView.animate().alpha(alpha).setDuration(100L).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda65
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.lambda$setItemVisible$66(visible, itemView);
                    }
                }).start();
                return;
            }
            if (!visible) {
                i = 8;
            }
            itemView.setVisibility(i);
            itemView.setAlpha(alpha);
        }
    }

    public static /* synthetic */ void lambda$setItemVisible$66(boolean visible, View itemView) {
        if (!visible) {
            itemView.setVisibility(8);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:175:0x04e7 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:176:0x04e8  */
    /* JADX WARN: Removed duplicated region for block: B:343:0x0860  */
    /* JADX WARN: Removed duplicated region for block: B:361:0x08b3  */
    /* JADX WARN: Type inference failed for: r11v1, types: [android.animation.AnimatorSet, java.lang.String] */
    /* JADX WARN: Type inference failed for: r11v28 */
    /* JADX WARN: Type inference failed for: r11v29 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onPhotoShow(org.telegram.messenger.MessageObject r26, org.telegram.tgnet.TLRPC.FileLocation r27, org.telegram.messenger.ImageLocation r28, org.telegram.messenger.ImageLocation r29, java.util.ArrayList<org.telegram.messenger.MessageObject> r30, java.util.ArrayList<org.telegram.messenger.SecureDocument> r31, java.util.List<java.lang.Object> r32, int r33, org.telegram.ui.PhotoViewer.PlaceProviderObject r34) {
        /*
            Method dump skipped, instructions count: 2337
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onPhotoShow(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.messenger.ImageLocation, org.telegram.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.List, int, org.telegram.ui.PhotoViewer$PlaceProviderObject):void");
    }

    private boolean canSendMediaToParentChatActivity() {
        ChatActivity chatActivity = this.parentChatActivity;
        return chatActivity != null && (chatActivity.currentUser != null || (this.parentChatActivity.currentChat != null && !ChatObject.isNotInChat(this.parentChatActivity.currentChat) && ChatObject.canSendMedia(this.parentChatActivity.currentChat)));
    }

    private void setDoubleTapEnabled(boolean value) {
        this.doubleTapEnabled = value;
        this.gestureDetector.setOnDoubleTapListener(value ? this : null);
    }

    public void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex, null);
            setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
            setIndexToImage(this.rightImage, this.currentIndex + 1, this.rightCropTransform);
            setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
            setIndexToImage(this.leftImage, this.currentIndex - 1, this.leftCropTransform);
            setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:429:0x09c8  */
    /* JADX WARN: Removed duplicated region for block: B:430:0x0a08  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0198  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x019a  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x019d  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x01a4  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01a9  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01c0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setIsAboutToSwitchToIndex(int r35, boolean r36, boolean r37) {
        /*
            Method dump skipped, instructions count: 3680
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIsAboutToSwitchToIndex(int, boolean, boolean):void");
    }

    private void showVideoTimeline(boolean show, boolean animated) {
        Integer num = null;
        int i = 0;
        if (!animated) {
            this.videoTimelineView.animate().setListener(null).cancel();
            VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
            if (!show) {
                i = 8;
            }
            videoTimelinePlayView.setVisibility(i);
            this.videoTimelineView.setTranslationY(0.0f);
            this.videoTimelineView.setAlpha(this.pickerView.getAlpha());
        } else if (show && this.videoTimelineView.getTag() == null) {
            if (this.videoTimelineView.getVisibility() != 0) {
                this.videoTimelineView.setVisibility(0);
                this.videoTimelineView.setAlpha(this.pickerView.getAlpha());
                this.videoTimelineView.setTranslationY(AndroidUtilities.dp(58.0f));
            }
            ObjectAnimator objectAnimator = this.videoTimelineAnimator;
            if (objectAnimator != null) {
                objectAnimator.removeAllListeners();
                this.videoTimelineAnimator.cancel();
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_Y, this.videoTimelineView.getTranslationY(), 0.0f);
            this.videoTimelineAnimator = ofFloat;
            ofFloat.setDuration(220L);
            this.videoTimelineAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.videoTimelineAnimator.start();
        } else if (!show && this.videoTimelineView.getTag() != null) {
            ObjectAnimator objectAnimator2 = this.videoTimelineAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.removeAllListeners();
                this.videoTimelineAnimator.cancel();
            }
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.videoTimelineView, View.TRANSLATION_Y, this.videoTimelineView.getTranslationY(), AndroidUtilities.dp(58.0f));
            this.videoTimelineAnimator = ofFloat2;
            ofFloat2.addListener(new HideViewAfterAnimation(this.videoTimelineView));
            this.videoTimelineAnimator.setDuration(220L);
            this.videoTimelineAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.videoTimelineAnimator.start();
        }
        VideoTimelinePlayView videoTimelinePlayView2 = this.videoTimelineView;
        if (show) {
            num = 1;
        }
        videoTimelinePlayView2.setTag(num);
    }

    public static TLRPC.FileLocation getFileLocation(ImageLocation location) {
        if (location == null) {
            return null;
        }
        return location.location;
    }

    public static String getFileLocationExt(ImageLocation location) {
        if (location == null || location.imageType != 2) {
            return null;
        }
        return "mp4";
    }

    public void setImageIndex(int index) {
        setImageIndex(index, true, false);
    }

    private void setImageIndex(int index, boolean init, boolean animateCaption) {
        MediaController.CropState prevCropState;
        boolean sameImage;
        boolean z;
        boolean sameImage2;
        boolean sameImage3;
        boolean sameImage4;
        boolean isVideo;
        boolean isVideo2;
        boolean sameImage5;
        MessageObject messageObject;
        ImageReceiver.BitmapHolder bitmapHolder;
        if (this.currentIndex == index || this.placeProvider == null) {
            return;
        }
        if (!init && (bitmapHolder = this.currentThumb) != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.currentFileNames[0] = getFileName(index);
        this.currentFileNames[1] = getFileName(index + 1);
        this.currentFileNames[2] = getFileName(index - 1);
        this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex);
        this.lastPhotoSetTime = SystemClock.elapsedRealtime();
        int prevIndex = this.currentIndex;
        this.currentIndex = index;
        setIsAboutToSwitchToIndex(index, init, animateCaption);
        boolean isVideo3 = false;
        Uri videoPath = null;
        CropTransform prevCropTransform = this.cropTransform.clone();
        EditState editState = this.editState;
        if (editState != null && editState.cropState != null) {
            prevCropState = this.editState.cropState.clone();
        } else {
            prevCropState = null;
        }
        boolean prevIsVideo = this.centerImageIsVideo;
        this.editState.reset();
        if (!this.imagesArr.isEmpty()) {
            int i = this.currentIndex;
            if (i >= 0 && i < this.imagesArr.size()) {
                MessageObject newMessageObject = this.imagesArr.get(this.currentIndex);
                sameImage = init && (messageObject = this.currentMessageObject) != null && messageObject.getId() == newMessageObject.getId();
                this.currentMessageObject = newMessageObject;
                isVideo3 = newMessageObject.isVideo();
                if (this.sharedMediaType == 1) {
                    boolean canPreviewDocument = newMessageObject.canPreviewDocument();
                    this.canZoom = canPreviewDocument;
                    if (canPreviewDocument) {
                        if (this.allowShare) {
                            this.menuItem.showSubItem(1);
                        } else {
                            this.menuItem.hideSubItem(1);
                        }
                        setDoubleTapEnabled(true);
                    } else {
                        this.menuItem.hideSubItem(1);
                        setDoubleTapEnabled(false);
                    }
                }
                if (isVideo3 || this.isEmbedVideo) {
                    this.speedItem.setVisibility(0);
                    this.speedGap.setVisibility(0);
                    this.menuItem.showSubItem(19);
                } else {
                    this.speedItem.setVisibility(8);
                    this.speedGap.setVisibility(8);
                    this.menuItem.checkHideMenuItem();
                }
            } else {
                closePhoto(false, false);
                return;
            }
        } else {
            if (!this.secureDocuments.isEmpty()) {
                if (index >= 0 && index < this.secureDocuments.size()) {
                    this.currentSecureDocument = this.secureDocuments.get(index);
                    sameImage2 = false;
                } else {
                    closePhoto(false, false);
                    return;
                }
            } else if (!this.imagesArrLocations.isEmpty()) {
                if (index >= 0 && index < this.imagesArrLocations.size()) {
                    ImageLocation old = this.currentFileLocation;
                    ImageLocation newLocation = this.imagesArrLocations.get(index);
                    if (init && old != null && newLocation != null && old.location.local_id == newLocation.location.local_id) {
                        sameImage5 = false;
                        if (old.location.volume_id == newLocation.location.volume_id) {
                            sameImage = true;
                            this.currentFileLocation = this.imagesArrLocations.get(index);
                            this.currentFileLocationVideo = this.imagesArrLocationsVideo.get(index);
                        }
                    } else {
                        sameImage5 = false;
                    }
                    sameImage = sameImage5;
                    this.currentFileLocation = this.imagesArrLocations.get(index);
                    this.currentFileLocationVideo = this.imagesArrLocationsVideo.get(index);
                }
                closePhoto(false, false);
                return;
            } else {
                sameImage2 = false;
                if (!this.imagesArrLocals.isEmpty()) {
                    if (index < 0 || index >= this.imagesArrLocals.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    Object object = this.imagesArrLocals.get(index);
                    if (object instanceof TLRPC.BotInlineResult) {
                        TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) object;
                        this.currentBotInlineResult = botInlineResult;
                        if (botInlineResult.document != null) {
                            this.currentPathObject = FileLoader.getInstance(this.currentAccount).getPathToAttach(botInlineResult.document).getAbsolutePath();
                            isVideo3 = MessageObject.isVideoDocument(botInlineResult.document);
                        } else if (botInlineResult.photo != null) {
                            this.currentPathObject = FileLoader.getInstance(this.currentAccount).getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
                        } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                            this.currentPathObject = botInlineResult.content.url;
                            isVideo3 = botInlineResult.type.equals("video");
                        }
                        sameImage3 = false;
                    } else {
                        if (object instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry entry = (MediaController.PhotoEntry) object;
                            String str = entry.path;
                            this.currentPathObject = str;
                            if (str == null) {
                                closePhoto(false, false);
                                return;
                            }
                            boolean isVideo4 = entry.isVideo;
                            this.editState.savedFilterState = entry.savedFilterState;
                            this.editState.paintPath = entry.paintPath;
                            this.editState.croppedPaintPath = entry.croppedPaintPath;
                            this.editState.croppedMediaEntities = entry.croppedMediaEntities;
                            this.editState.averageDuration = entry.averageDuration;
                            this.editState.mediaEntities = entry.mediaEntities;
                            this.editState.cropState = entry.cropState;
                            File file = new File(entry.path);
                            videoPath = Uri.fromFile(file);
                            if (!this.isDocumentsPicker) {
                                isVideo2 = isVideo4;
                            } else {
                                StringBuilder builder = new StringBuilder();
                                if (entry.width != 0 && entry.height != 0) {
                                    if (builder.length() > 0) {
                                        builder.append(", ");
                                    }
                                    builder.append(String.format(Locale.US, "%dx%d", Integer.valueOf(entry.width), Integer.valueOf(entry.height)));
                                }
                                if (entry.isVideo) {
                                    if (builder.length() > 0) {
                                        builder.append(", ");
                                    }
                                    builder.append(AndroidUtilities.formatShortDuration(entry.duration));
                                }
                                isVideo2 = isVideo4;
                                if (entry.size != 0) {
                                    if (builder.length() > 0) {
                                        builder.append(", ");
                                    }
                                    builder.append(AndroidUtilities.formatFileSize(entry.size));
                                }
                                this.docNameTextView.setText(file.getName());
                                this.docInfoTextView.setText(builder);
                            }
                            sameImage3 = this.savedState != null;
                            isVideo3 = isVideo2;
                        } else {
                            if (object instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                                this.currentPathObject = searchImage.getPathToAttach();
                                this.editState.savedFilterState = searchImage.savedFilterState;
                                this.editState.paintPath = searchImage.paintPath;
                                this.editState.croppedPaintPath = searchImage.croppedPaintPath;
                                this.editState.croppedMediaEntities = searchImage.croppedMediaEntities;
                                this.editState.averageDuration = searchImage.averageDuration;
                                this.editState.mediaEntities = searchImage.mediaEntities;
                                this.editState.cropState = searchImage.cropState;
                            }
                            sameImage3 = false;
                        }
                        if (object instanceof MediaController.MediaEditState) {
                            MediaController.MediaEditState state = (MediaController.MediaEditState) object;
                            if (hasAnimatedMediaEntities()) {
                                this.currentImagePath = state.imagePath;
                            } else if (state.filterPath != null) {
                                this.currentImagePath = state.filterPath;
                            } else {
                                this.currentImagePath = this.currentPathObject;
                            }
                        }
                    }
                    if (this.editState.cropState != null) {
                        this.previousHasTransform = true;
                        this.previousCropPx = this.editState.cropState.cropPx;
                        this.previousCropPy = this.editState.cropState.cropPy;
                        this.previousCropScale = this.editState.cropState.cropScale;
                        this.previousCropRotation = this.editState.cropState.cropRotate;
                        this.previousCropOrientation = this.editState.cropState.transformRotation;
                        this.previousCropPw = this.editState.cropState.cropPw;
                        this.previousCropPh = this.editState.cropState.cropPh;
                        boolean z2 = this.editState.cropState.mirrored;
                        this.previousCropMirrored = z2;
                        isVideo = isVideo3;
                        sameImage4 = sameImage3;
                        this.cropTransform.setViewTransform(this.previousHasTransform, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, 1.0f, 1.0f, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f, z2);
                    } else {
                        isVideo = isVideo3;
                        sameImage4 = sameImage3;
                        this.previousHasTransform = false;
                        this.cropTransform.setViewTransform(false, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, 1.0f, 1.0f, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f, this.previousCropMirrored);
                    }
                    isVideo3 = isVideo;
                    sameImage = sameImage4;
                } else {
                    PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                    if (pageBlocksAdapter != null) {
                        int i2 = this.currentIndex;
                        if (i2 < 0 || i2 >= pageBlocksAdapter.getItemsCount()) {
                            closePhoto(false, false);
                            return;
                        }
                        TLRPC.PageBlock pageBlock = this.pageBlocksAdapter.get(this.currentIndex);
                        TLRPC.PageBlock pageBlock2 = this.currentPageBlock;
                        sameImage = pageBlock2 != null && pageBlock2 == pageBlock;
                        this.currentPageBlock = pageBlock;
                        isVideo3 = this.pageBlocksAdapter.isVideo(this.currentIndex);
                    }
                }
            }
            sameImage = sameImage2;
        }
        setMenuItemIcon();
        PlaceProviderObject placeProviderObject = this.currentPlaceObject;
        if (placeProviderObject != null) {
            if (this.animationInProgress == 0) {
                placeProviderObject.imageReceiver.setVisible(true, true);
            } else {
                this.showAfterAnimation = placeProviderObject;
            }
        }
        PlaceProviderObject placeForPhoto = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, false);
        this.currentPlaceObject = placeForPhoto;
        if (placeForPhoto != null) {
            if (this.animationInProgress == 0) {
                placeForPhoto.imageReceiver.setVisible(false, true);
            } else {
                this.hideAfterAnimation = placeForPhoto;
            }
        }
        if (sameImage) {
            z = false;
        } else {
            this.draggingDown = false;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
            this.scale = 1.0f;
            this.animateToX = 0.0f;
            this.animateToY = 0.0f;
            this.animateToScale = 1.0f;
            this.animateToRotate = 0.0f;
            this.animationStartTime = 0L;
            this.zoomAnimation = false;
            this.imageMoveAnimation = null;
            this.changeModeAnimation = null;
            AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
            if (aspectRatioFrameLayout != null) {
                aspectRatioFrameLayout.setVisibility(4);
            }
            this.pinchStartDistance = 0.0f;
            this.pinchStartScale = 1.0f;
            this.pinchCenterX = 0.0f;
            this.pinchCenterY = 0.0f;
            this.pinchStartX = 0.0f;
            this.pinchStartY = 0.0f;
            this.moveStartX = 0.0f;
            this.moveStartY = 0.0f;
            this.zooming = false;
            this.moving = false;
            this.paintViewTouched = 0;
            this.doubleTap = false;
            this.invalidCoords = false;
            this.canDragDown = true;
            this.changingPage = false;
            this.switchImageAfterAnimation = 0;
            if (this.sharedMediaType != 1) {
                this.canZoom = !this.isEmbedVideo && (!this.imagesArrLocals.isEmpty() || !(this.currentFileNames[0] == null || this.photoProgressViews[0].backgroundState == 0));
            }
            updateMinMax(this.scale);
            z = false;
            releasePlayer(false);
        }
        if (isVideo3 && videoPath != null) {
            this.isStreaming = z;
            preparePlayer(videoPath, this.sendPhotoType == 1, z, this.editState.savedFilterState);
        }
        if (this.imagesArrLocals.isEmpty()) {
            this.editState.reset();
        }
        this.centerImageIsVideo = isVideo3;
        if (prevIndex != -1) {
            checkProgress(0, true, false);
            int i3 = this.currentIndex;
            if (prevIndex > i3) {
                ImageReceiver temp = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = temp;
                this.rightImageIsVideo = prevIsVideo;
                this.rightCropTransform = prevCropTransform;
                this.rightCropState = prevCropState;
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                PhotoProgressView tempProgress = photoProgressViewArr[0];
                photoProgressViewArr[0] = photoProgressViewArr[2];
                photoProgressViewArr[2] = tempProgress;
                ImageView[] imageViewArr = this.fullscreenButton;
                ImageView tmp = imageViewArr[0];
                imageViewArr[0] = imageViewArr[2];
                imageViewArr[2] = tmp;
                imageViewArr[0].setTranslationY(tmp.getTranslationY());
                this.leftCropState = null;
                setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
                setIndexToImage(this.leftImage, this.currentIndex - 1, this.leftCropTransform);
                updateAccessibilityOverlayVisibility();
                checkProgress(1, true, false);
                checkProgress(2, true, false);
            } else if (prevIndex < i3) {
                ImageReceiver temp2 = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = temp2;
                this.leftImageIsVideo = prevIsVideo;
                this.leftCropTransform = prevCropTransform;
                this.leftCropState = prevCropState;
                PhotoProgressView[] photoProgressViewArr2 = this.photoProgressViews;
                PhotoProgressView tempProgress2 = photoProgressViewArr2[0];
                photoProgressViewArr2[0] = photoProgressViewArr2[1];
                photoProgressViewArr2[1] = tempProgress2;
                ImageView[] imageViewArr2 = this.fullscreenButton;
                ImageView tmp2 = imageViewArr2[0];
                imageViewArr2[0] = imageViewArr2[1];
                imageViewArr2[1] = tmp2;
                imageViewArr2[0].setTranslationY(tmp2.getTranslationY());
                this.rightCropState = null;
                setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
                setIndexToImage(this.rightImage, this.currentIndex + 1, this.rightCropTransform);
                updateAccessibilityOverlayVisibility();
                checkProgress(1, true, false);
                checkProgress(2, true, false);
            }
            Bitmap bitmap = this.videoFrameBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.videoFrameBitmap = null;
            }
        } else {
            setImages();
            for (int a = 0; a < 3; a++) {
                checkProgress(a, false, false);
            }
        }
        detectFaces();
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence caption, boolean animated) {
        CharSequence str;
        int newCount;
        boolean z = true;
        int i = 0;
        if (this.needCaptionLayout) {
            if (this.captionTextViewSwitcher.getParent() != this.pickerView) {
                FrameLayout frameLayout = this.captionContainer;
                if (frameLayout != null) {
                    frameLayout.removeView(this.captionTextViewSwitcher);
                }
                this.captionTextViewSwitcher.setMeasureAllChildren(false);
                this.pickerView.addView(this.captionTextViewSwitcher, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
            }
        } else {
            if (this.captionScrollView == null) {
                this.captionScrollView = new CaptionScrollView(this.containerView.getContext());
                FrameLayout frameLayout2 = new FrameLayout(this.containerView.getContext());
                this.captionContainer = frameLayout2;
                frameLayout2.setClipChildren(false);
                this.captionScrollView.addView(this.captionContainer, new ViewGroup.LayoutParams(-1, -2));
                this.containerView.addView(this.captionScrollView, LayoutHelper.createFrame(-1, -1, 80));
            }
            if (this.captionTextViewSwitcher.getParent() != this.captionContainer) {
                this.pickerView.removeView(this.captionTextViewSwitcher);
                this.captionTextViewSwitcher.setMeasureAllChildren(true);
                this.captionContainer.addView(this.captionTextViewSwitcher, -1, -2);
                this.videoPreviewFrame.bringToFront();
            }
        }
        boolean isCaptionEmpty = TextUtils.isEmpty(caption);
        boolean isCurrentCaptionEmpty = TextUtils.isEmpty(this.captionTextViewSwitcher.getCurrentView().getText());
        CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
        TextView captionTextView = animated ? captionTextViewSwitcher.getNextView() : captionTextViewSwitcher.getCurrentView();
        if (this.isCurrentVideo) {
            if (captionTextView.getMaxLines() != 1) {
                this.captionTextViewSwitcher.getCurrentView().setMaxLines(1);
                this.captionTextViewSwitcher.getNextView().setMaxLines(1);
                this.captionTextViewSwitcher.getCurrentView().setSingleLine(true);
                this.captionTextViewSwitcher.getNextView().setSingleLine(true);
                this.captionTextViewSwitcher.getCurrentView().setEllipsize(TextUtils.TruncateAt.END);
                this.captionTextViewSwitcher.getNextView().setEllipsize(TextUtils.TruncateAt.END);
            }
        } else {
            int maxLines = captionTextView.getMaxLines();
            if (maxLines == 1) {
                this.captionTextViewSwitcher.getCurrentView().setSingleLine(false);
                this.captionTextViewSwitcher.getNextView().setSingleLine(false);
            }
            if (this.needCaptionLayout) {
                newCount = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 5 : 10;
            } else {
                newCount = Integer.MAX_VALUE;
            }
            if (maxLines != newCount) {
                this.captionTextViewSwitcher.getCurrentView().setMaxLines(newCount);
                this.captionTextViewSwitcher.getNextView().setMaxLines(newCount);
                this.captionTextViewSwitcher.getCurrentView().setEllipsize(null);
                this.captionTextViewSwitcher.getNextView().setEllipsize(null);
            }
        }
        captionTextView.setScrollX(0);
        boolean z2 = this.needCaptionLayout;
        this.dontChangeCaptionPosition = !z2 && animated && isCaptionEmpty;
        boolean withTransition = false;
        if (!z2) {
            this.captionScrollView.dontChangeTopMargin = false;
        }
        if (animated && Build.VERSION.SDK_INT >= 19) {
            withTransition = true;
            if (Build.VERSION.SDK_INT >= 23) {
                TransitionManager.endTransitions(this.needCaptionLayout ? this.pickerView : this.captionScrollView);
            }
            if (this.needCaptionLayout) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.setOrdering(0);
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new Fade(2));
                transitionSet.addTransition(new Fade(1));
                transitionSet.setDuration(200L);
                TransitionManager.beginDelayedTransition(this.pickerView, transitionSet);
            } else {
                TransitionSet transition = new TransitionSet().addTransition(new AnonymousClass66(2, isCurrentCaptionEmpty, isCaptionEmpty)).addTransition(new AnonymousClass65(1, isCurrentCaptionEmpty, isCaptionEmpty)).setDuration(200L);
                if (!isCurrentCaptionEmpty) {
                    this.captionScrollView.dontChangeTopMargin = true;
                    transition.addTransition(new AnonymousClass67());
                }
                if (isCurrentCaptionEmpty && !isCaptionEmpty) {
                    transition.addTarget((View) this.captionTextViewSwitcher);
                }
                TransitionManager.beginDelayedTransition(this.captionScrollView, transition);
            }
        } else {
            this.captionTextViewSwitcher.getCurrentView().setText((CharSequence) null);
            CaptionScrollView captionScrollView = this.captionScrollView;
            if (captionScrollView != null) {
                captionScrollView.scrollTo(0, 0);
            }
        }
        if (!isCaptionEmpty) {
            Theme.createChatResources(null, true);
            if (messageObject == null || messageObject.messageOwner.entities.isEmpty()) {
                str = Emoji.replaceEmoji(new SpannableStringBuilder(caption), captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                Spannable spannableString = new SpannableString(caption);
                messageObject.addEntitiesToText(spannableString, true, false);
                if (messageObject.isVideo()) {
                    MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableString, false, 3, messageObject.getDuration(), false);
                }
                str = Emoji.replaceEmoji(spannableString, captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.captionTextViewSwitcher.setTag(str);
            try {
                this.captionTextViewSwitcher.setText(str, animated);
                CaptionScrollView captionScrollView2 = this.captionScrollView;
                if (captionScrollView2 != null) {
                    captionScrollView2.updateTopMargin();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            captionTextView.setScrollY(0);
            captionTextView.setTextColor(-1);
            if (!this.isActionBarVisible || (this.bottomLayout.getVisibility() != 0 && this.pickerView.getVisibility() != 0 && this.pageBlocksAdapter == null)) {
                z = false;
            }
            boolean visible = z;
            CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
            if (!visible) {
                i = 4;
            }
            captionTextViewSwitcher2.setVisibility(i);
        } else if (this.needCaptionLayout) {
            this.captionTextViewSwitcher.setText(LocaleController.getString("AddCaption", R.string.AddCaption), animated);
            this.captionTextViewSwitcher.getCurrentView().setTextColor(-1291845633);
            this.captionTextViewSwitcher.setTag("empty");
            this.captionTextViewSwitcher.setVisibility(0);
        } else {
            this.captionTextViewSwitcher.setText(null, animated);
            this.captionTextViewSwitcher.getCurrentView().setTextColor(-1);
            CaptionTextViewSwitcher captionTextViewSwitcher3 = this.captionTextViewSwitcher;
            if (withTransition && !isCurrentCaptionEmpty) {
                z = false;
            }
            captionTextViewSwitcher3.setVisibility(4, z);
            this.captionTextViewSwitcher.setTag(null);
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$66 */
    /* loaded from: classes4.dex */
    public class AnonymousClass66 extends Fade {
        final /* synthetic */ boolean val$isCaptionEmpty;
        final /* synthetic */ boolean val$isCurrentCaptionEmpty;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass66(int arg0, boolean z, boolean z2) {
            super(arg0);
            PhotoViewer.this = this$0;
            this.val$isCurrentCaptionEmpty = z;
            this.val$isCaptionEmpty = z2;
        }

        @Override // android.transition.Fade, android.transition.Visibility
        public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
            Animator animator = super.onDisappear(sceneRoot, view, startValues, endValues);
            if (!this.val$isCurrentCaptionEmpty && this.val$isCaptionEmpty && view == PhotoViewer.this.captionTextViewSwitcher) {
                animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.66.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhotoViewer.this.captionScrollView.setVisibility(4);
                        PhotoViewer.this.captionScrollView.backgroundAlpha = 1.0f;
                    }
                });
                ((ObjectAnimator) animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$66$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.AnonymousClass66.this.m4287lambda$onDisappear$0$orgtelegramuiPhotoViewer$66(valueAnimator);
                    }
                });
            }
            return animator;
        }

        /* renamed from: lambda$onDisappear$0$org-telegram-ui-PhotoViewer$66 */
        public /* synthetic */ void m4287lambda$onDisappear$0$orgtelegramuiPhotoViewer$66(ValueAnimator animation) {
            PhotoViewer.this.captionScrollView.backgroundAlpha = ((Float) animation.getAnimatedValue()).floatValue();
            PhotoViewer.this.captionScrollView.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$65 */
    /* loaded from: classes4.dex */
    public class AnonymousClass65 extends Fade {
        final /* synthetic */ boolean val$isCaptionEmpty;
        final /* synthetic */ boolean val$isCurrentCaptionEmpty;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass65(int arg0, boolean z, boolean z2) {
            super(arg0);
            PhotoViewer.this = this$0;
            this.val$isCurrentCaptionEmpty = z;
            this.val$isCaptionEmpty = z2;
        }

        @Override // android.transition.Fade, android.transition.Visibility
        public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
            Animator animator = super.onAppear(sceneRoot, view, startValues, endValues);
            if (this.val$isCurrentCaptionEmpty && !this.val$isCaptionEmpty && view == PhotoViewer.this.captionTextViewSwitcher) {
                animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.65.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhotoViewer.this.captionScrollView.backgroundAlpha = 1.0f;
                    }
                });
                ((ObjectAnimator) animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$65$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.AnonymousClass65.this.m4286lambda$onAppear$0$orgtelegramuiPhotoViewer$65(valueAnimator);
                    }
                });
            }
            return animator;
        }

        /* renamed from: lambda$onAppear$0$org-telegram-ui-PhotoViewer$65 */
        public /* synthetic */ void m4286lambda$onAppear$0$orgtelegramuiPhotoViewer$65(ValueAnimator animation) {
            PhotoViewer.this.captionScrollView.backgroundAlpha = ((Float) animation.getAnimatedValue()).floatValue();
            PhotoViewer.this.captionScrollView.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.PhotoViewer$67 */
    /* loaded from: classes4.dex */
    public class AnonymousClass67 extends Transition {
        AnonymousClass67() {
            PhotoViewer.this = this$0;
        }

        @Override // android.transition.Transition
        public void captureStartValues(TransitionValues transitionValues) {
            if (transitionValues.view == PhotoViewer.this.captionScrollView) {
                transitionValues.values.put("scrollY", Integer.valueOf(PhotoViewer.this.captionScrollView.getScrollY()));
            }
        }

        @Override // android.transition.Transition
        public void captureEndValues(TransitionValues transitionValues) {
            if (transitionValues.view == PhotoViewer.this.captionTextViewSwitcher) {
                transitionValues.values.put("translationY", Integer.valueOf(PhotoViewer.this.captionScrollView.getPendingMarginTopDiff()));
            }
        }

        @Override // android.transition.Transition
        public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
            int endValue;
            if (startValues.view != PhotoViewer.this.captionScrollView) {
                if (endValues.view == PhotoViewer.this.captionTextViewSwitcher && (endValue = ((Integer) endValues.values.get("translationY")).intValue()) != 0) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.TRANSLATION_Y, 0.0f, endValue);
                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.67.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.captionTextViewSwitcher.setTranslationY(0.0f);
                        }
                    });
                    return animator;
                }
                return null;
            }
            ValueAnimator animator2 = ValueAnimator.ofInt(((Integer) startValues.values.get("scrollY")).intValue(), 0);
            animator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.67.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PhotoViewer.this.captionTextViewSwitcher.getNextView().setText((CharSequence) null);
                    PhotoViewer.this.captionScrollView.applyPendingTopMargin();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    PhotoViewer.this.captionScrollView.stopScrolling();
                }
            });
            animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$67$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewer.AnonymousClass67.this.m4288lambda$createAnimator$0$orgtelegramuiPhotoViewer$67(valueAnimator);
                }
            });
            return animator2;
        }

        /* renamed from: lambda$createAnimator$0$org-telegram-ui-PhotoViewer$67 */
        public /* synthetic */ void m4288lambda$createAnimator$0$orgtelegramuiPhotoViewer$67(ValueAnimator a) {
            PhotoViewer.this.captionScrollView.scrollTo(0, ((Integer) a.getAnimatedValue()).intValue());
        }
    }

    public void setCaptionHwLayerEnabled(boolean enabled) {
        if (this.captionHwLayerEnabled != enabled) {
            this.captionHwLayerEnabled = enabled;
            this.captionTextViewSwitcher.setLayerType(2, null);
            this.captionTextViewSwitcher.getCurrentView().setLayerType(2, null);
            this.captionTextViewSwitcher.getNextView().setLayerType(2, null);
        }
    }

    public void checkProgress(final int a, boolean scroll, final boolean animated) {
        int index;
        boolean fileExist;
        MessageObject messageObject;
        boolean canAutoPlay;
        boolean canStream;
        boolean isVideo;
        FileLoader.FileResolver f2Resolver;
        File f1;
        File f2;
        boolean z;
        FileLoader.FileResolver f2Resolver2;
        AnimatedFileDrawable animatedFileDrawable;
        int index2 = this.currentIndex;
        if (a == 1) {
            index = index2 + 1;
        } else if (a != 2) {
            index = index2;
        } else {
            index = index2 - 1;
        }
        if (this.currentFileNames[a] != null) {
            File f12 = null;
            boolean isVideo2 = false;
            if (a == 0 && this.currentIndex == 0 && (animatedFileDrawable = this.currentAnimation) != null) {
                boolean fileExist2 = animatedFileDrawable.hasBitmap();
                fileExist = fileExist2;
            } else {
                fileExist = false;
            }
            if (this.currentMessageObject != null) {
                if (index < 0 || index >= this.imagesArr.size()) {
                    this.photoProgressViews[a].setBackgroundState(-1, animated, true);
                    return;
                }
                MessageObject messageObject2 = this.imagesArr.get(index);
                MessageObject messageObject3 = messageObject2;
                boolean canAutoPlay2 = shouldMessageObjectAutoPlayed(messageObject3);
                if (this.sharedMediaType == 1 && !messageObject3.canPreviewDocument()) {
                    this.photoProgressViews[a].setBackgroundState(-1, animated, true);
                    return;
                }
                if (!TextUtils.isEmpty(messageObject3.messageOwner.attachPath)) {
                    f12 = new File(messageObject3.messageOwner.attachPath);
                }
                if ((messageObject3.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && messageObject3.messageOwner.media.webpage != null && messageObject3.messageOwner.media.webpage.document == null) {
                    final TLObject fileLocation = getFileLocation(index, null);
                    FileLoader.FileResolver f2Resolver3 = new FileLoader.FileResolver() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda67
                        @Override // org.telegram.messenger.FileLoader.FileResolver
                        public final File getFile() {
                            return PhotoViewer.this.m4195lambda$checkProgress$67$orgtelegramuiPhotoViewer(fileLocation);
                        }
                    };
                    f2Resolver2 = f2Resolver3;
                } else {
                    final TLRPC.Message finalMessage = messageObject3.messageOwner;
                    f2Resolver2 = new FileLoader.FileResolver() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda68
                        @Override // org.telegram.messenger.FileLoader.FileResolver
                        public final File getFile() {
                            return PhotoViewer.this.m4196lambda$checkProgress$68$orgtelegramuiPhotoViewer(finalMessage);
                        }
                    };
                }
                if (!messageObject3.isVideo()) {
                    f1 = f12;
                    f2 = null;
                    f2Resolver = f2Resolver2;
                    isVideo = false;
                    canStream = false;
                    canAutoPlay = canAutoPlay2;
                    messageObject = messageObject3;
                } else {
                    boolean canStream2 = SharedConfig.streamMedia && messageObject3.canStreamVideo() && !DialogObject.isEncryptedDialog(messageObject3.getDialogId());
                    f1 = f12;
                    f2 = null;
                    f2Resolver = f2Resolver2;
                    isVideo = true;
                    canStream = canStream2;
                    canAutoPlay = canAutoPlay2;
                    messageObject = messageObject3;
                }
            } else if (this.currentBotInlineResult != null) {
                if (index >= 0 && index < this.imagesArrLocals.size()) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) this.imagesArrLocals.get(index);
                    if (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(botInlineResult.document)) {
                        if (botInlineResult.document != null) {
                            f12 = FileLoader.getInstance(this.currentAccount).getPathToAttach(botInlineResult.document);
                        } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                            f12 = new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                        }
                        isVideo2 = true;
                    } else if (botInlineResult.document != null) {
                        f12 = new File(FileLoader.getDirectory(3), this.currentFileNames[a]);
                    } else if (botInlineResult.photo != null) {
                        f12 = new File(FileLoader.getDirectory(0), this.currentFileNames[a]);
                    }
                    File f22 = new File(FileLoader.getDirectory(4), this.currentFileNames[a]);
                    f1 = f12;
                    f2 = f22;
                    f2Resolver = null;
                    isVideo = isVideo2;
                    canStream = false;
                    canAutoPlay = false;
                    messageObject = null;
                }
                this.photoProgressViews[a].setBackgroundState(-1, animated, true);
                return;
            } else {
                f1 = null;
                if (this.currentFileLocation == null) {
                    if (this.currentSecureDocument != null) {
                        if (index >= 0) {
                            if (index >= this.secureDocuments.size()) {
                                z = true;
                            } else {
                                f1 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.secureDocuments.get(index), true);
                                f2Resolver = null;
                                isVideo = false;
                                canStream = false;
                                canAutoPlay = false;
                                messageObject = null;
                                f2 = null;
                            }
                        } else {
                            z = true;
                        }
                        this.photoProgressViews[a].setBackgroundState(-1, animated, z);
                        return;
                    } else if (this.currentPathObject != null) {
                        f1 = new File(FileLoader.getDirectory(3), this.currentFileNames[a]);
                        f2 = new File(FileLoader.getDirectory(4), this.currentFileNames[a]);
                        f2Resolver = null;
                        isVideo = false;
                        canStream = false;
                        canAutoPlay = false;
                        messageObject = null;
                    } else {
                        PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                        if (pageBlocksAdapter == null) {
                            f2Resolver = null;
                            isVideo = false;
                            canStream = false;
                            canAutoPlay = false;
                            messageObject = null;
                            f2 = null;
                        } else {
                            File f13 = pageBlocksAdapter.getFile(index);
                            boolean isVideo3 = this.pageBlocksAdapter.isVideo(index);
                            boolean canAutoPlay3 = shouldIndexAutoPlayed(index);
                            f1 = f13;
                            f2Resolver = null;
                            isVideo = isVideo3;
                            canStream = false;
                            canAutoPlay = canAutoPlay3;
                            messageObject = null;
                            f2 = null;
                        }
                    }
                } else {
                    if (index >= 0 && index < this.imagesArrLocationsVideo.size()) {
                        ImageLocation location = this.imagesArrLocationsVideo.get(index);
                        f1 = FileLoader.getInstance(this.currentAccount).getPathToAttach(location.location, getFileLocationExt(location), this.avatarsDialogId != 0 || this.isEvent);
                        f2Resolver = null;
                        isVideo = false;
                        canStream = false;
                        canAutoPlay = false;
                        messageObject = null;
                        f2 = null;
                    }
                    this.photoProgressViews[a].setBackgroundState(-1, animated, true);
                    return;
                }
            }
            final File f1Final = f1;
            final File f2Final = f2;
            final FileLoader.FileResolver finalF2Resolver = f2Resolver;
            final MessageObject messageObjectFinal = messageObject;
            final boolean canStreamFinal = canStream;
            final boolean canAutoPlayFinal = (a != 0 || !this.dontAutoPlay) && canAutoPlay;
            final boolean isVideoFinal = isVideo;
            final boolean finalFileExist = fileExist;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda64
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.m4198lambda$checkProgress$70$orgtelegramuiPhotoViewer(finalFileExist, f1Final, f2Final, finalF2Resolver, a, messageObjectFinal, canStreamFinal, isVideoFinal, canAutoPlayFinal, animated);
                }
            });
            return;
        }
        boolean isLocalVideo = false;
        if (!this.imagesArrLocals.isEmpty() && index >= 0 && index < this.imagesArrLocals.size()) {
            Object object = this.imagesArrLocals.get(index);
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                isLocalVideo = photoEntry.isVideo;
            }
        }
        if (isLocalVideo) {
            this.photoProgressViews[a].setBackgroundState(3, animated, true);
        } else {
            this.photoProgressViews[a].setBackgroundState(-1, animated, true);
        }
    }

    /* renamed from: lambda$checkProgress$67$org-telegram-ui-PhotoViewer */
    public /* synthetic */ File m4195lambda$checkProgress$67$orgtelegramuiPhotoViewer(TLObject fileLocation) {
        return FileLoader.getInstance(this.currentAccount).getPathToAttach(fileLocation, true);
    }

    /* renamed from: lambda$checkProgress$68$org-telegram-ui-PhotoViewer */
    public /* synthetic */ File m4196lambda$checkProgress$68$orgtelegramuiPhotoViewer(TLRPC.Message finalMessage) {
        return FileLoader.getInstance(this.currentAccount).getPathToMessage(finalMessage);
    }

    /* renamed from: lambda$checkProgress$70$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4198lambda$checkProgress$70$orgtelegramuiPhotoViewer(boolean finalFileExist, final File f1Final, File f2Final, FileLoader.FileResolver finalF2Resolver, final int a, MessageObject messageObjectFinal, final boolean canStreamFinal, final boolean isVideoFinal, final boolean canAutoPlayFinal, final boolean animated) {
        File f3Local;
        File f2Local;
        boolean exists;
        ChatActivity chatActivity;
        TLRPC.Document document;
        boolean exists2 = finalFileExist;
        if (!exists2 && f1Final != null) {
            exists2 = f1Final.exists();
        }
        if (f2Final == null && finalF2Resolver != null) {
            f2Local = finalF2Resolver.getFile();
            f3Local = null;
        } else if (finalF2Resolver != null) {
            f2Local = f2Final;
            f3Local = finalF2Resolver.getFile();
        } else {
            f2Local = f2Final;
            f3Local = null;
        }
        if (!exists2 && f2Local != null) {
            exists2 = f2Local.exists();
        }
        if (!exists2 && f3Local != null) {
            exists = f3Local.exists();
        } else {
            exists = exists2;
        }
        if (!exists && a != 0 && messageObjectFinal != null && canStreamFinal && DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObjectFinal.messageOwner) != 0 && (((chatActivity = this.parentChatActivity) == null || chatActivity.getCurrentEncryptedChat() == null) && !messageObjectFinal.shouldEncryptPhotoOrVideo() && (document = messageObjectFinal.getDocument()) != null)) {
            FileLoader.getInstance(this.currentAccount).loadFile(document, messageObjectFinal, 0, 10);
        }
        final boolean existsFinal = exists;
        final File finalF2Local = f2Local;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4197lambda$checkProgress$69$orgtelegramuiPhotoViewer(a, f1Final, finalF2Local, existsFinal, canStreamFinal, isVideoFinal, canAutoPlayFinal, animated);
            }
        });
    }

    /* renamed from: lambda$checkProgress$69$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4197lambda$checkProgress$69$orgtelegramuiPhotoViewer(int a, File f1Final, File finalF2Local, boolean existsFinal, boolean canStreamFinal, boolean isVideoFinal, boolean canAutoPlayFinal, boolean animated) {
        boolean z = false;
        if (this.shownControlsByEnd && !this.actionBarWasShownBeforeByEnd && this.isPlaying) {
            this.photoProgressViews[a].setBackgroundState(3, false, false);
            return;
        }
        if ((f1Final != null || finalF2Local != null) && (existsFinal || canStreamFinal)) {
            if (a != 0 || !this.isPlaying) {
                if (!isVideoFinal || (canAutoPlayFinal && (a != 0 || !this.playerWasPlaying))) {
                    this.photoProgressViews[a].setBackgroundState(-1, animated, true);
                } else {
                    this.photoProgressViews[a].setBackgroundState(3, animated, true);
                }
            }
            if (a == 0) {
                if (existsFinal) {
                    this.menuItem.hideSubItem(7);
                } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[a])) {
                    this.menuItem.hideSubItem(7);
                } else {
                    this.menuItem.showSubItem(7);
                }
            }
        } else {
            if (!isVideoFinal) {
                this.photoProgressViews[a].setBackgroundState(0, animated, true);
            } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[a])) {
                this.photoProgressViews[a].setBackgroundState(2, false, true);
            } else {
                this.photoProgressViews[a].setBackgroundState(1, false, true);
            }
            Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
            if (progress == null) {
                progress = Float.valueOf(0.0f);
            }
            this.photoProgressViews[a].setProgress(progress.floatValue(), false);
        }
        if (a == 0) {
            if (!this.isEmbedVideo && (!this.imagesArrLocals.isEmpty() || (this.currentFileNames[0] != null && this.photoProgressViews[0].backgroundState != 0))) {
                z = true;
            }
            this.canZoom = z;
        }
    }

    public int getSelectiongLength() {
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            return photoViewerCaptionEnterView.getSelectionLength();
        }
        return 0;
    }

    private void setIndexToPaintingOverlay(int index, PaintingOverlay paintingOverlay) {
        if (paintingOverlay == null) {
            return;
        }
        paintingOverlay.reset();
        paintingOverlay.setVisibility(8);
        if (!this.imagesArrLocals.isEmpty() && index >= 0 && index < this.imagesArrLocals.size()) {
            Object object = this.imagesArrLocals.get(index);
            boolean isVideo = false;
            String paintPath = null;
            ArrayList<VideoEditedInfo.MediaEntity> mediaEntities = null;
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                isVideo = photoEntry.isVideo;
                paintPath = photoEntry.paintPath;
                mediaEntities = photoEntry.mediaEntities;
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) object;
                paintPath = photoEntry2.paintPath;
                mediaEntities = photoEntry2.mediaEntities;
            }
            paintingOverlay.setVisibility(0);
            paintingOverlay.setData(paintPath, mediaEntities, isVideo, false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:325:0x07c9  */
    /* JADX WARN: Removed duplicated region for block: B:332:0x07e9  */
    /* JADX WARN: Removed duplicated region for block: B:349:0x0845  */
    /* JADX WARN: Removed duplicated region for block: B:365:0x0897  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setIndexToImage(org.telegram.messenger.ImageReceiver r52, int r53, org.telegram.ui.Components.Crop.CropTransform r54) {
        /*
            Method dump skipped, instructions count: 2338
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIndexToImage(org.telegram.messenger.ImageReceiver, int, org.telegram.ui.Components.Crop.CropTransform):void");
    }

    public static boolean isShowingImage(MessageObject object) {
        boolean result = false;
        boolean result2 = true;
        if (Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && !Instance.disableShowCheck && object != null) {
            MessageObject currentMessageObject = Instance.currentMessageObject;
            if (currentMessageObject == null && Instance.placeProvider != null) {
                currentMessageObject = Instance.placeProvider.getEditingMessageObject();
            }
            result = currentMessageObject != null && currentMessageObject.getId() == object.getId() && currentMessageObject.getDialogId() == object.getDialogId();
        }
        if (!result && PipInstance != null) {
            if (!PipInstance.isVisible || PipInstance.disableShowCheck || object == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != object.getId() || PipInstance.currentMessageObject.getDialogId() != object.getDialogId()) {
                result2 = false;
            }
            return result2;
        }
        return result;
    }

    public static boolean isPlayingMessageInPip(MessageObject object) {
        return (PipInstance == null || object == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != object.getId() || PipInstance.currentMessageObject.getDialogId() != object.getDialogId()) ? false : true;
    }

    public static boolean isPlayingMessage(MessageObject object) {
        return Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && object != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == object.getId() && Instance.currentMessageObject.getDialogId() == object.getDialogId();
    }

    public static boolean isShowingImage(TLRPC.FileLocation object) {
        if (Instance == null) {
            return false;
        }
        boolean result = Instance.isVisible && !Instance.disableShowCheck && object != null && ((Instance.currentFileLocation != null && object.local_id == Instance.currentFileLocation.location.local_id && object.volume_id == Instance.currentFileLocation.location.volume_id && object.dc_id == Instance.currentFileLocation.dc_id) || (Instance.currentFileLocationVideo != null && object.local_id == Instance.currentFileLocationVideo.location.local_id && object.volume_id == Instance.currentFileLocationVideo.location.volume_id && object.dc_id == Instance.currentFileLocationVideo.dc_id));
        return result;
    }

    public static boolean isShowingImage(TLRPC.BotInlineResult object) {
        if (Instance == null) {
            return false;
        }
        boolean result = Instance.isVisible && !Instance.disableShowCheck && object != null && Instance.currentBotInlineResult != null && object.id == Instance.currentBotInlineResult.id;
        return result;
    }

    public static boolean isShowingImage(String object) {
        if (Instance == null) {
            return false;
        }
        boolean result = Instance.isVisible && !Instance.disableShowCheck && object != null && object.equals(Instance.currentPathObject);
        return result;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
    }

    public void checkCurrentImageVisibility() {
        PlaceProviderObject placeProviderObject = this.currentPlaceObject;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        PlaceProviderObject placeForPhoto = photoViewerProvider == null ? null : photoViewerProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, false);
        this.currentPlaceObject = placeForPhoto;
        if (placeForPhoto != null) {
            placeForPhoto.imageReceiver.setVisible(false, true);
        }
    }

    public boolean openPhoto(MessageObject messageObject, ChatActivity chatActivity, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, provider, chatActivity, dialogId, mergeDialogId, true, null, null);
    }

    public boolean openPhoto(MessageObject messageObject, int embedSeekTime, ChatActivity chatActivity, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, provider, chatActivity, dialogId, mergeDialogId, true, null, Integer.valueOf(embedSeekTime));
    }

    public boolean openPhoto(MessageObject messageObject, long dialogId, long mergeDialogId, PhotoViewerProvider provider, boolean fullScreenVideo) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, provider, null, dialogId, mergeDialogId, fullScreenVideo, null, null);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, null, null, null, null, null, 0, provider, null, 0L, 0L, true, null, null);
    }

    public boolean openPhotoWithVideo(TLRPC.FileLocation fileLocation, ImageLocation videoLocation, PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, null, videoLocation, null, null, null, 0, provider, null, 0L, 0L, true, null, null);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, PhotoViewerProvider provider) {
        return openPhoto(null, fileLocation, imageLocation, null, null, null, null, 0, provider, null, 0L, 0L, true, null, null);
    }

    public boolean openPhoto(ArrayList<MessageObject> messages, int index, long dialogId, long mergeDialogId, PhotoViewerProvider provider) {
        return openPhoto(messages.get(index), null, null, null, messages, null, null, index, provider, null, dialogId, mergeDialogId, true, null, null);
    }

    public boolean openPhoto(ArrayList<SecureDocument> documents, int index, PhotoViewerProvider provider) {
        return openPhoto(null, null, null, null, null, documents, null, index, provider, null, 0L, 0L, true, null, null);
    }

    public boolean openPhoto(int index, PageBlocksAdapter pageBlocksAdapter, PhotoViewerProvider provider) {
        return openPhoto(null, null, null, null, null, null, null, index, provider, null, 0L, 0L, true, pageBlocksAdapter, null);
    }

    public boolean openPhotoForSelect(ArrayList<Object> photos, int index, int type, boolean documentsPicker, PhotoViewerProvider provider, ChatActivity chatActivity) {
        this.isDocumentsPicker = documentsPicker;
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            if (type == 4 || type == 5) {
                this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
                layoutParams2.bottomMargin = AndroidUtilities.dp(19.0f);
            } else if (type == 1 || type == 3 || type == 10) {
                this.pickerViewSendButton.setImageResource(R.drawable.floating_check);
                this.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams2.bottomMargin = AndroidUtilities.dp(19.0f);
            } else {
                this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
                layoutParams2.bottomMargin = AndroidUtilities.dp(14.0f);
            }
            this.pickerViewSendButton.setLayoutParams(layoutParams2);
        }
        if (this.sendPhotoType != 1 && type == 1 && this.isVisible) {
            this.sendPhotoType = type;
            this.doneButtonPressed = false;
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
            this.placeProvider = provider;
            this.mergeDialogId = 0L;
            this.currentDialogId = 0L;
            this.selectedPhotosAdapter.notifyDataSetChanged();
            this.pageBlocksAdapter = null;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.isVisible = true;
            togglePhotosListView(false, false);
            this.openedFullScreenVideo = false;
            createCropView();
            toggleActionBar(false, false);
            this.seekToProgressPending2 = 0.0f;
            this.skipFirstBufferingProgress = false;
            this.playerInjected = false;
            makeFocusable();
            this.backgroundDrawable.setAlpha(255);
            this.containerView.setAlpha(1.0f);
            onPhotoShow(null, null, null, null, null, null, photos, index, null);
            initCropView();
            setCropBitmap();
            return true;
        }
        this.sendPhotoType = type;
        return openPhoto(null, null, null, null, null, null, photos, index, provider, chatActivity, 0L, 0L, true, null, null);
    }

    private void openCurrentPhotoInPaintModeForSelect() {
        if (!canSendMediaToParentChatActivity()) {
            return;
        }
        File file = null;
        boolean isVideo = false;
        boolean capReplace = false;
        MessageObject messageObject = null;
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null) {
            messageObject = this.currentMessageObject;
            capReplace = messageObject2.canEditMedia() && !this.currentMessageObject.isDocument();
            isVideo = this.currentMessageObject.isVideo();
            if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                file = new File(this.currentMessageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file == null) {
                file = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
            }
        }
        if (file != null && file.exists()) {
            this.savedState = new SavedState(this.currentIndex, new ArrayList(this.imagesArr), this.placeProvider);
            ActionBarToggleParams toggleParams = new ActionBarToggleParams().enableStatusBarAnimation(false);
            toggleActionBar(false, true, toggleParams);
            final File finalFile = file;
            final boolean finalIsVideo = isVideo;
            final boolean finalCanReplace = capReplace;
            final MessageObject finalMessageObject = messageObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.m4218xec3efe9f(finalFile, finalIsVideo, finalMessageObject, finalCanReplace);
                }
            }, toggleParams.animationDuration);
            return;
        }
        showDownloadAlert();
    }

    /* renamed from: lambda$openCurrentPhotoInPaintModeForSelect$71$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4218xec3efe9f(File finalFile, boolean finalIsVideo, final MessageObject finalMessageObject, final boolean finalCanReplace) {
        int orientation;
        ActionBar actionBar;
        int orientation2 = 0;
        try {
            ExifInterface ei = new ExifInterface(finalFile.getAbsolutePath());
            int exif = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (exif) {
                case 3:
                    orientation2 = 180;
                    break;
                case 6:
                    orientation2 = 90;
                    break;
                case 8:
                    orientation2 = 270;
                    break;
            }
            orientation = orientation2;
        } catch (Exception e) {
            FileLog.e(e);
            orientation = 0;
        }
        int i = this.lastImageId;
        this.lastImageId = i - 1;
        final MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, i, 0L, finalFile.getAbsolutePath(), orientation, finalIsVideo, 0, 0, 0L);
        this.sendPhotoType = 2;
        this.doneButtonPressed = false;
        final PhotoViewerProvider chatPhotoProvider = this.placeProvider;
        this.placeProvider = new EmptyPhotoViewerProvider() { // from class: org.telegram.ui.PhotoViewer.68
            private final ImageReceiver.BitmapHolder thumbHolder;

            {
                PhotoViewer.this = this;
                this.thumbHolder = this.centerImage.getBitmapSafe();
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                PhotoViewerProvider photoViewerProvider = chatPhotoProvider;
                if (photoViewerProvider != null) {
                    return photoViewerProvider.getPlaceForPhoto(finalMessageObject, null, 0, needPreview);
                }
                return null;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
                return this.thumbHolder;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
                sendMedia(videoEditedInfo, notify, scheduleDate, false, forceDocument);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void replaceButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
                if (photoEntry.isCropped || photoEntry.isPainted || photoEntry.isFiltered || videoEditedInfo != null || !TextUtils.isEmpty(photoEntry.caption)) {
                    sendMedia(videoEditedInfo, false, 0, true, false);
                }
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean canReplace(int index) {
                return chatPhotoProvider != null && finalCanReplace;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public MessageObject getEditingMessageObject() {
                return finalMessageObject;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean canCaptureMorePhotos() {
                return false;
            }

            private void sendMedia(VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean replace, boolean forceDocument) {
                if (PhotoViewer.this.parentChatActivity != null) {
                    MessageObject editingMessageObject = replace ? finalMessageObject : null;
                    if (editingMessageObject != null && !TextUtils.isEmpty(photoEntry.caption)) {
                        editingMessageObject.editingMessage = photoEntry.caption;
                        editingMessageObject.editingMessageEntities = photoEntry.entities;
                    }
                    if (photoEntry.isVideo) {
                        if (videoEditedInfo != null) {
                            SendMessagesHelper.prepareSendingVideo(PhotoViewer.this.parentChatActivity.getAccountInstance(), photoEntry.path, videoEditedInfo, PhotoViewer.this.parentChatActivity.getDialogId(), PhotoViewer.this.parentChatActivity.getReplyMessage(), PhotoViewer.this.parentChatActivity.getThreadMessage(), photoEntry.caption, photoEntry.entities, photoEntry.ttl, editingMessageObject, notify, scheduleDate, forceDocument);
                        } else {
                            SendMessagesHelper.prepareSendingVideo(PhotoViewer.this.parentChatActivity.getAccountInstance(), photoEntry.path, null, PhotoViewer.this.parentChatActivity.getDialogId(), PhotoViewer.this.parentChatActivity.getReplyMessage(), PhotoViewer.this.parentChatActivity.getThreadMessage(), photoEntry.caption, photoEntry.entities, photoEntry.ttl, editingMessageObject, notify, scheduleDate, forceDocument);
                        }
                    } else if (photoEntry.imagePath != null) {
                        SendMessagesHelper.prepareSendingPhoto(PhotoViewer.this.parentChatActivity.getAccountInstance(), photoEntry.imagePath, photoEntry.thumbPath, null, PhotoViewer.this.parentChatActivity.getDialogId(), PhotoViewer.this.parentChatActivity.getReplyMessage(), PhotoViewer.this.parentChatActivity.getThreadMessage(), photoEntry.caption, photoEntry.entities, photoEntry.stickers, null, photoEntry.ttl, editingMessageObject, videoEditedInfo, notify, scheduleDate, forceDocument);
                    } else if (photoEntry.path != null) {
                        SendMessagesHelper.prepareSendingPhoto(PhotoViewer.this.parentChatActivity.getAccountInstance(), photoEntry.path, photoEntry.thumbPath, null, PhotoViewer.this.parentChatActivity.getDialogId(), PhotoViewer.this.parentChatActivity.getReplyMessage(), PhotoViewer.this.parentChatActivity.getThreadMessage(), photoEntry.caption, photoEntry.entities, photoEntry.stickers, null, photoEntry.ttl, editingMessageObject, videoEditedInfo, notify, scheduleDate, forceDocument);
                    }
                }
            }
        };
        this.selectedPhotosAdapter.notifyDataSetChanged();
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        togglePhotosListView(false, false);
        toggleActionBar(true, false);
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && chatActivity.getChatActivityEnterView() != null && this.parentChatActivity.isKeyboardVisible()) {
            this.parentChatActivity.getChatActivityEnterView().closeKeyboard();
        } else {
            makeFocusable();
        }
        this.backgroundDrawable.setAlpha(255);
        this.containerView.setAlpha(1.0f);
        onPhotoShow(null, null, null, null, null, null, Collections.singletonList(photoEntry), 0, null);
        float f = 154.0f;
        this.pickerView.setTranslationY(AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f));
        this.pickerViewSendButton.setTranslationY(AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f));
        this.actionBar.setTranslationY(-actionBar.getHeight());
        CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
        if (!this.isCurrentVideo) {
            f = 96.0f;
        }
        captionTextViewSwitcher.setTranslationY(AndroidUtilities.dp(f));
        createPaintView();
        switchToPaintMode();
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.animationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        return this.animationInProgress != 0;
    }

    public void setCropBitmap() {
        VideoEditTextureView textureView;
        if (this.cropInitied || this.sendPhotoType != 1) {
            return;
        }
        if (this.isCurrentVideo && ((textureView = (VideoEditTextureView) this.videoTextureView) == null || textureView.getVideoWidth() <= 0 || textureView.getVideoHeight() <= 0)) {
            return;
        }
        this.cropInitied = true;
        Bitmap bitmap = this.centerImage.getBitmap();
        int orientation = this.centerImage.getOrientation();
        if (bitmap == null) {
            bitmap = this.animatingImageView.getBitmap();
            orientation = this.animatingImageView.getOrientation();
        }
        if (bitmap != null || this.videoTextureView != null) {
            this.photoCropView.setBitmap(bitmap, orientation, false, false, this.paintingOverlay, this.cropTransform, this.isCurrentVideo ? (VideoEditTextureView) this.videoTextureView : null, this.editState.cropState);
        }
    }

    private void initCropView() {
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView == null) {
            return;
        }
        photoCropView.setBitmap(null, 0, false, false, null, null, null, null);
        if (this.sendPhotoType != 1) {
            return;
        }
        this.photoCropView.onAppear();
        this.photoCropView.setVisibility(0);
        this.photoCropView.setAlpha(1.0f);
        this.photoCropView.onAppeared();
        this.padImageForHorizontalInsets = true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:86:0x0237, code lost:
        if (org.telegram.messenger.FileLoader.getInstance(r26.currentAccount).isLoadingVideo(r26.getDocument(), false) != false) goto L90;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean openPhoto(org.telegram.messenger.MessageObject r26, org.telegram.tgnet.TLRPC.FileLocation r27, org.telegram.messenger.ImageLocation r28, org.telegram.messenger.ImageLocation r29, java.util.ArrayList<org.telegram.messenger.MessageObject> r30, java.util.ArrayList<org.telegram.messenger.SecureDocument> r31, java.util.ArrayList<java.lang.Object> r32, int r33, final org.telegram.ui.PhotoViewer.PhotoViewerProvider r34, org.telegram.ui.ChatActivity r35, long r36, long r38, boolean r40, org.telegram.ui.PhotoViewer.PageBlocksAdapter r41, final java.lang.Integer r42) {
        /*
            Method dump skipped, instructions count: 1085
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.openPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.messenger.ImageLocation, org.telegram.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, org.telegram.ui.PhotoViewer$PhotoViewerProvider, org.telegram.ui.ChatActivity, long, long, boolean, org.telegram.ui.PhotoViewer$PageBlocksAdapter, java.lang.Integer):boolean");
    }

    /* renamed from: org.telegram.ui.PhotoViewer$69 */
    /* loaded from: classes4.dex */
    public class AnonymousClass69 implements ViewTreeObserver.OnPreDrawListener {
        final /* synthetic */ ClippingImageView[] val$animatingImageViews;
        final /* synthetic */ Integer val$embedSeekTime;
        final /* synthetic */ ViewGroup.LayoutParams val$layoutParams;
        final /* synthetic */ float val$left;
        final /* synthetic */ PlaceProviderObject val$object;
        final /* synthetic */ PageBlocksAdapter val$pageBlocksAdapter;
        final /* synthetic */ ArrayList val$photos;
        final /* synthetic */ PhotoViewerProvider val$provider;
        final /* synthetic */ float val$top;

        AnonymousClass69(ClippingImageView[] clippingImageViewArr, ViewGroup.LayoutParams layoutParams, float f, PlaceProviderObject placeProviderObject, float f2, PageBlocksAdapter pageBlocksAdapter, ArrayList arrayList, Integer num, PhotoViewerProvider photoViewerProvider) {
            PhotoViewer.this = this$0;
            this.val$animatingImageViews = clippingImageViewArr;
            this.val$layoutParams = layoutParams;
            this.val$left = f;
            this.val$object = placeProviderObject;
            this.val$top = f2;
            this.val$pageBlocksAdapter = pageBlocksAdapter;
            this.val$photos = arrayList;
            this.val$embedSeekTime = num;
            this.val$provider = photoViewerProvider;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            float yPos;
            float scale;
            float xPos;
            float scaleY;
            int clipHorizontal;
            ClippingImageView[] clippingImageViewArr;
            int i;
            ClippingImageView[] clippingImageViewArr2 = this.val$animatingImageViews;
            if (clippingImageViewArr2.length > 1) {
                clippingImageViewArr2[1].setAlpha(1.0f);
                this.val$animatingImageViews[1].setAdditionalTranslationX(-PhotoViewer.this.getLeftInset());
            }
            ClippingImageView[] clippingImageViewArr3 = this.val$animatingImageViews;
            clippingImageViewArr3[0].setTranslationX(clippingImageViewArr3[0].getTranslationX() + PhotoViewer.this.getLeftInset());
            PhotoViewer.this.windowView.getViewTreeObserver().removeOnPreDrawListener(this);
            if (PhotoViewer.this.sendPhotoType == 1) {
                float statusBarHeight = PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0;
                float measuredHeight = (PhotoViewer.this.photoCropView.getMeasuredHeight() - AndroidUtilities.dp(64.0f)) - statusBarHeight;
                float minSide = Math.min(PhotoViewer.this.photoCropView.getMeasuredWidth(), measuredHeight) - (AndroidUtilities.dp(16.0f) * 2);
                float centerX = PhotoViewer.this.photoCropView.getMeasuredWidth() / 2.0f;
                float centerY = (measuredHeight / 2.0f) + statusBarHeight;
                float left = centerX - (minSide / 2.0f);
                float top = centerY - (minSide / 2.0f);
                float right = (minSide / 2.0f) + centerX;
                float bottom = (minSide / 2.0f) + centerY;
                float scaleX = (right - left) / this.val$layoutParams.width;
                scaleY = (bottom - top) / this.val$layoutParams.height;
                scale = Math.max(scaleX, scaleY);
                yPos = top + (((bottom - top) - (this.val$layoutParams.height * scale)) / 2.0f);
                xPos = ((((PhotoViewer.this.windowView.getMeasuredWidth() - PhotoViewer.this.getLeftInset()) - PhotoViewer.this.getRightInset()) - (this.val$layoutParams.width * scale)) / 2.0f) + PhotoViewer.this.getLeftInset();
            } else {
                float scaleX2 = PhotoViewer.this.windowView.getMeasuredWidth() / this.val$layoutParams.width;
                scaleY = (AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) / this.val$layoutParams.height;
                scale = Math.min(scaleX2, scaleY);
                yPos = ((AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - (this.val$layoutParams.height * scale)) / 2.0f;
                xPos = (PhotoViewer.this.windowView.getMeasuredWidth() - (this.val$layoutParams.width * scale)) / 2.0f;
            }
            int clipHorizontal2 = (int) Math.abs(this.val$left - this.val$object.imageReceiver.getImageX());
            int clipVertical = (int) Math.abs(this.val$top - this.val$object.imageReceiver.getImageY());
            if (this.val$pageBlocksAdapter != null && this.val$object.imageReceiver.isAspectFit()) {
                clipHorizontal = 0;
            } else {
                clipHorizontal = clipHorizontal2;
            }
            int[] coords2 = new int[2];
            this.val$object.parentView.getLocationInWindow(coords2);
            int clipTop = (int) (((coords2[1] - ((Build.VERSION.SDK_INT >= 21 || PhotoViewer.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) - (this.val$object.viewY + this.val$top)) + this.val$object.clipTopAddition);
            if (clipTop < 0) {
                clipTop = 0;
            }
            int clipBottom = (int) ((((this.val$object.viewY + this.val$top) + this.val$layoutParams.height) - ((coords2[1] + this.val$object.parentView.getHeight()) - ((Build.VERSION.SDK_INT >= 21 || PhotoViewer.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))) + this.val$object.clipBottomAddition);
            if (clipBottom < 0) {
                clipBottom = 0;
            }
            int clipTop2 = Math.max(clipTop, clipVertical);
            int clipBottom2 = Math.max(clipBottom, clipVertical);
            PhotoViewer.this.animationValues[0][0] = PhotoViewer.this.animatingImageView.getScaleX();
            PhotoViewer.this.animationValues[0][1] = PhotoViewer.this.animatingImageView.getScaleY();
            PhotoViewer.this.animationValues[0][2] = PhotoViewer.this.animatingImageView.getTranslationX();
            int i2 = 3;
            PhotoViewer.this.animationValues[0][3] = PhotoViewer.this.animatingImageView.getTranslationY();
            PhotoViewer.this.animationValues[0][4] = clipHorizontal * this.val$object.scale;
            PhotoViewer.this.animationValues[0][5] = clipTop2 * this.val$object.scale;
            PhotoViewer.this.animationValues[0][6] = clipBottom2 * this.val$object.scale;
            int[] rad = PhotoViewer.this.animatingImageView.getRadius();
            int a = 0;
            while (true) {
                float f = 0.0f;
                if (a >= 4) {
                    break;
                }
                float[] fArr = PhotoViewer.this.animationValues[0];
                int i3 = a + 7;
                if (rad != null) {
                    f = rad[a];
                }
                fArr[i3] = f;
                a++;
            }
            PhotoViewer.this.animationValues[0][11] = clipVertical * this.val$object.scale;
            PhotoViewer.this.animationValues[0][12] = clipHorizontal * this.val$object.scale;
            PhotoViewer.this.animationValues[1][0] = scale;
            PhotoViewer.this.animationValues[1][1] = scale;
            PhotoViewer.this.animationValues[1][2] = xPos;
            PhotoViewer.this.animationValues[1][3] = yPos;
            PhotoViewer.this.animationValues[1][4] = 0.0f;
            PhotoViewer.this.animationValues[1][5] = 0.0f;
            PhotoViewer.this.animationValues[1][6] = 0.0f;
            PhotoViewer.this.animationValues[1][7] = 0.0f;
            PhotoViewer.this.animationValues[1][8] = 0.0f;
            PhotoViewer.this.animationValues[1][9] = 0.0f;
            PhotoViewer.this.animationValues[1][10] = 0.0f;
            PhotoViewer.this.animationValues[1][11] = 0.0f;
            PhotoViewer.this.animationValues[1][12] = 0.0f;
            int i4 = 0;
            while (true) {
                ClippingImageView[] clippingImageViewArr4 = this.val$animatingImageViews;
                if (i4 >= clippingImageViewArr4.length) {
                    break;
                }
                clippingImageViewArr4[i4].setAnimationProgress(0.0f);
                i4++;
            }
            PhotoViewer.this.backgroundDrawable.setAlpha(0);
            PhotoViewer.this.containerView.setAlpha(0.0f);
            PhotoViewer.this.navigationBar.setAlpha(0.0f);
            PhotoViewer photoViewer = PhotoViewer.this;
            final ClippingImageView[] clippingImageViewArr5 = this.val$animatingImageViews;
            final ArrayList arrayList = this.val$photos;
            final Integer num = this.val$embedSeekTime;
            final PhotoViewerProvider photoViewerProvider = this.val$provider;
            photoViewer.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$69$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass69.this.m4289lambda$onPreDraw$0$orgtelegramuiPhotoViewer$69(clippingImageViewArr5, arrayList, num, photoViewerProvider);
                }
            };
            if (PhotoViewer.this.openedFullScreenVideo) {
                if (PhotoViewer.this.animationEndRunnable != null) {
                    PhotoViewer.this.animationEndRunnable.run();
                    PhotoViewer.this.animationEndRunnable = null;
                }
                PhotoViewer.this.containerView.setAlpha(1.0f);
                PhotoViewer.this.backgroundDrawable.setAlpha(255);
                int i5 = 0;
                while (true) {
                    ClippingImageView[] clippingImageViewArr6 = this.val$animatingImageViews;
                    if (i5 >= clippingImageViewArr6.length) {
                        break;
                    }
                    clippingImageViewArr6[i5].setAnimationProgress(1.0f);
                    i5++;
                }
                if (PhotoViewer.this.sendPhotoType == 1) {
                    PhotoViewer.this.photoCropView.setAlpha(1.0f);
                }
            } else {
                final AnimatorSet animatorSet = new AnimatorSet();
                if (PhotoViewer.this.sendPhotoType != 1) {
                    i2 = 2;
                }
                ClippingImageView[] clippingImageViewArr7 = this.val$animatingImageViews;
                ArrayList<Animator> animators = new ArrayList<>(i2 + clippingImageViewArr7.length + (clippingImageViewArr7.length > 1 ? 1 : 0));
                int i6 = 0;
                while (true) {
                    clippingImageViewArr = this.val$animatingImageViews;
                    if (i6 >= clippingImageViewArr.length) {
                        break;
                    }
                    animators.add(ObjectAnimator.ofFloat(clippingImageViewArr[i6], AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, 0.0f, 1.0f));
                    i6++;
                }
                if (clippingImageViewArr.length > 1) {
                    i = 2;
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.animatingImageView, View.ALPHA, 0.0f, 1.0f));
                } else {
                    i = 2;
                }
                int[] iArr = new int[i];
                // fill-array-data instruction
                iArr[0] = 0;
                iArr[1] = 255;
                animators.add(ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, iArr));
                float[] fArr2 = new float[i];
                // fill-array-data instruction
                fArr2[0] = 0.0f;
                fArr2[1] = 1.0f;
                animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.containerView, View.ALPHA, fArr2));
                float[] fArr3 = new float[i];
                // fill-array-data instruction
                fArr3[0] = 0.0f;
                fArr3[1] = 1.0f;
                animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.navigationBar, View.ALPHA, fArr3));
                if (PhotoViewer.this.sendPhotoType == 1) {
                    animators.add(ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, 0.0f, 1.0f));
                }
                animatorSet.playTogether(animators);
                animatorSet.setDuration(200L);
                final int account = PhotoViewer.this.currentAccount;
                animatorSet.addListener(new AnonymousClass1(account));
                if (Build.VERSION.SDK_INT >= 18) {
                    PhotoViewer.this.containerView.setLayerType(2, null);
                }
                PhotoViewer.this.setCaptionHwLayerEnabled(false);
                PhotoViewer.this.transitionAnimationStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$69$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass69.this.m4290lambda$onPreDraw$1$orgtelegramuiPhotoViewer$69(account, animatorSet);
                    }
                });
            }
            BackgroundDrawable backgroundDrawable = PhotoViewer.this.backgroundDrawable;
            final PlaceProviderObject placeProviderObject = this.val$object;
            backgroundDrawable.drawRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$69$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass69.this.m4291lambda$onPreDraw$2$orgtelegramuiPhotoViewer$69(placeProviderObject);
                }
            };
            if (PhotoViewer.this.parentChatActivity != null && PhotoViewer.this.parentChatActivity.getFragmentView() != null) {
                PhotoViewer.this.parentChatActivity.getUndoView().hide(false, 1);
                PhotoViewer.this.parentChatActivity.getFragmentView().invalidate();
                return true;
            }
            return true;
        }

        /* renamed from: lambda$onPreDraw$0$org-telegram-ui-PhotoViewer$69 */
        public /* synthetic */ void m4289lambda$onPreDraw$0$orgtelegramuiPhotoViewer$69(ClippingImageView[] animatingImageViews, ArrayList photos, Integer embedSeekTime, PhotoViewerProvider provider) {
            PhotoViewer.this.animationEndRunnable = null;
            if (PhotoViewer.this.containerView == null || PhotoViewer.this.windowView == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                PhotoViewer.this.containerView.setLayerType(0, null);
            }
            PhotoViewer.this.animationInProgress = 0;
            PhotoViewer.this.transitionAnimationStartTime = 0L;
            PhotoViewer.this.leftCropState = null;
            PhotoViewer.this.leftCropTransform.setViewTransform(false);
            PhotoViewer.this.rightCropState = null;
            PhotoViewer.this.rightCropTransform.setViewTransform(false);
            PhotoViewer.this.setImages();
            PhotoViewer.this.setCropBitmap();
            PhotoViewer.this.containerView.invalidate();
            for (ClippingImageView clippingImageView : animatingImageViews) {
                clippingImageView.setVisibility(8);
            }
            if (PhotoViewer.this.showAfterAnimation != null) {
                PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
            }
            if (PhotoViewer.this.hideAfterAnimation != null) {
                PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
            }
            if (photos != null && PhotoViewer.this.sendPhotoType != 3 && (PhotoViewer.this.placeProvider == null || !PhotoViewer.this.placeProvider.closeKeyboard())) {
                PhotoViewer.this.makeFocusable();
            }
            if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPlayer.isPlaying() && PhotoViewer.this.isCurrentVideo && !PhotoViewer.this.imagesArrLocals.isEmpty()) {
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.seekAnimatedStickersTo(photoViewer.videoPlayer.getCurrentPosition());
                PhotoViewer.this.playOrStopAnimatedStickers(true);
            }
            if (PhotoViewer.this.isEmbedVideo) {
                PhotoViewer.this.initEmbedVideo(embedSeekTime.intValue());
            }
            if (provider != null) {
                provider.onOpen();
            }
        }

        /* renamed from: org.telegram.ui.PhotoViewer$69$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final /* synthetic */ int val$account;

            AnonymousClass1(int i) {
                AnonymousClass69.this = this$1;
                this.val$account = i;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                final int i = this.val$account;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$69$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass69.AnonymousClass1.this.m4292lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$69$1(i);
                    }
                });
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-PhotoViewer$69$1 */
            public /* synthetic */ void m4292lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$69$1(int account) {
                NotificationCenter.getInstance(account).onAnimationFinish(PhotoViewer.this.transitionIndex);
                if (PhotoViewer.this.animationEndRunnable != null) {
                    PhotoViewer.this.animationEndRunnable.run();
                    PhotoViewer.this.animationEndRunnable = null;
                }
                PhotoViewer.this.setCaptionHwLayerEnabled(true);
            }
        }

        /* renamed from: lambda$onPreDraw$1$org-telegram-ui-PhotoViewer$69 */
        public /* synthetic */ void m4290lambda$onPreDraw$1$orgtelegramuiPhotoViewer$69(int account, AnimatorSet animatorSet) {
            PhotoViewer.this.transitionIndex = NotificationCenter.getInstance(account).setAnimationInProgress(PhotoViewer.this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaDidLoad, NotificationCenter.dialogPhotosLoaded});
            animatorSet.start();
        }

        /* renamed from: lambda$onPreDraw$2$org-telegram-ui-PhotoViewer$69 */
        public /* synthetic */ void m4291lambda$onPreDraw$2$orgtelegramuiPhotoViewer$69(PlaceProviderObject object) {
            PhotoViewer.this.disableShowCheck = false;
            object.imageReceiver.setVisible(false, true);
        }
    }

    public void initEmbedVideo(int embedSeekTime) {
        if (!this.isEmbedVideo) {
            return;
        }
        PhotoViewerWebView photoViewerWebView = new PhotoViewerWebView(this.parentActivity, this.pipItem) { // from class: org.telegram.ui.PhotoViewer.71
            Rect rect = new Rect();

            @Override // org.telegram.ui.Components.PhotoViewerWebView
            protected void drawBlackBackground(Canvas canvas, int w, int h) {
                Bitmap bitmap = PhotoViewer.this.centerImage.getBitmap();
                if (bitmap != null) {
                    float minScale = Math.min(w / bitmap.getWidth(), h / bitmap.getHeight());
                    int width = (int) (bitmap.getWidth() * minScale);
                    int height = (int) (bitmap.getHeight() * minScale);
                    int top = (h - height) / 2;
                    int left = (w - width) / 2;
                    this.rect.set(left, top, left + width, top + height);
                    canvas.drawBitmap(bitmap, (Rect) null, this.rect, (Paint) null);
                }
            }

            @Override // org.telegram.ui.Components.PhotoViewerWebView
            protected void processTouch(MotionEvent event) {
                PhotoViewer.this.gestureDetector.onTouchEvent(event);
            }
        };
        this.photoViewerWebView = photoViewerWebView;
        photoViewerWebView.init(embedSeekTime, this.currentMessageObject.messageOwner.media.webpage);
        this.photoViewerWebView.setPlaybackSpeed(this.currentVideoSpeed);
        this.containerView.addView(this.photoViewerWebView, 0, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void makeFocusable() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags = -2147417856;
        } else {
            this.windowLayoutParams.flags = 0;
        }
        this.windowLayoutParams.softInputMode = (this.useSmoothKeyboard ? 32 : 16) | 256;
        WindowManager wm1 = (WindowManager) this.parentActivity.getSystemService("window");
        try {
            wm1.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.windowView.setFocusable(true);
        this.containerView.setFocusable(true);
    }

    private void requestAdjustToNothing() {
        this.windowLayoutParams.softInputMode = 48;
        WindowManager wm1 = (WindowManager) this.parentActivity.getSystemService("window");
        try {
            wm1.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void requestAdjust() {
        this.windowLayoutParams.softInputMode = (this.useSmoothKeyboard ? 32 : 16) | 256;
        WindowManager wm1 = (WindowManager) this.parentActivity.getSystemService("window");
        try {
            wm1.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void injectVideoPlayerToMediaController() {
        if (this.videoPlayer.isPlaying()) {
            if (this.playerLooping) {
                this.videoPlayer.setLooping(false);
            }
            MediaController.getInstance().injectVideoPlayer(this.videoPlayer, this.currentMessageObject);
            this.videoPlayer = null;
        }
    }

    public void closePhoto(boolean animated, boolean fromEditMode) {
        float statusBarHeight;
        float yPos;
        AnimatedFileDrawable animation;
        Bitmap bitmap;
        int i;
        int i2;
        if (!fromEditMode && (i2 = this.currentEditMode) != 0) {
            if (i2 == 3 && this.photoPaintView != null) {
                closePaintMode();
                return;
            }
            if (i2 == 1) {
                this.cropTransform.setViewTransform(this.previousHasTransform, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, 1.0f, 1.0f, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f, this.previousCropMirrored);
            }
            switchToEditMode(0);
            return;
        }
        QualityChooseView qualityChooseView = this.qualityChooseView;
        if (qualityChooseView != null && qualityChooseView.getTag() != null) {
            this.qualityPicker.cancelButton.callOnClick();
            return;
        }
        this.openedFullScreenVideo = false;
        try {
            AlertDialog alertDialog = this.visibleDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (Build.VERSION.SDK_INT >= 21 && this.containerView != null) {
            AndroidUtilities.cancelRunOnUIThread(this.updateContainerFlagsRunnable);
            updateContainerFlags(true);
        }
        int i3 = this.currentEditMode;
        if (i3 != 0) {
            if (i3 == 2) {
                this.photoFilterView.shutdown();
                this.containerView.removeView(this.photoFilterView);
                this.photoFilterView = null;
            } else if (i3 == 1) {
                this.editorDoneLayout.setVisibility(8);
                this.photoCropView.setVisibility(8);
            } else if (i3 == 3) {
                this.photoPaintView.shutdown();
                this.containerView.removeView(this.photoPaintView);
                this.photoPaintView = null;
                this.savedState = null;
            }
            this.currentEditMode = 0;
        }
        View view = this.navigationBar;
        if (view != null) {
            view.setVisibility(0);
        }
        FrameLayout frameLayout = this.windowView;
        if (frameLayout != null) {
            frameLayout.setClipChildren(false);
        }
        if (this.parentActivity != null) {
            if ((!this.isInline && !this.isVisible) || checkAnimation() || this.placeProvider == null) {
                return;
            }
            if (this.captionEditText.hideActionMode() && !fromEditMode) {
                return;
            }
            Activity activity = this.parentActivity;
            if (activity != null && this.fullscreenedByButton != 0) {
                activity.setRequestedOrientation(this.prevOrientation);
                this.fullscreenedByButton = 0;
                this.wasRotated = false;
            }
            if (!this.doneButtonPressed && !this.imagesArrLocals.isEmpty() && (i = this.currentIndex) >= 0 && i < this.imagesArrLocals.size()) {
                Object entry = this.imagesArrLocals.get(this.currentIndex);
                if (entry instanceof MediaController.MediaEditState) {
                    ((MediaController.MediaEditState) entry).editedInfo = getCurrentVideoEditedInfo();
                }
            }
            final PlaceProviderObject object = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, true);
            if (this.videoPlayer != null && object != null && (animation = object.imageReceiver.getAnimation()) != null) {
                if (this.textureUploaded && (bitmap = animation.getAnimatedBitmap()) != null) {
                    try {
                        Bitmap src = this.videoTextureView.getBitmap(bitmap.getWidth(), bitmap.getHeight());
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawBitmap(src, 0.0f, 0.0f, (Paint) null);
                        src.recycle();
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
                if (this.currentMessageObject != null) {
                    long startTime = animation.getStartTime();
                    long currentPosition = this.videoPlayer.getCurrentPosition();
                    long j = 0;
                    if (startTime > 0) {
                        j = startTime;
                    }
                    long seekTo = currentPosition + j;
                    animation.seekTo(seekTo, !FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingVideo(this.currentMessageObject.getDocument(), true));
                }
                object.imageReceiver.setAllowStartAnimation(true);
                object.imageReceiver.startAnimation();
            }
            if (!this.doneButtonPressed) {
                releasePlayer(true);
            }
            PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
            if (photoViewerWebView != null) {
                photoViewerWebView.release();
                this.containerView.removeView(this.photoViewerWebView);
                this.photoViewerWebView = null;
            }
            this.captionEditText.onDestroy();
            ChatActivity chatActivity = this.parentChatActivity;
            if (chatActivity != null && chatActivity.getFragmentView() != null) {
                this.parentChatActivity.getFragmentView().invalidate();
            }
            this.parentChatActivity = null;
            removeObservers();
            this.isActionBarVisible = false;
            VelocityTracker velocityTracker = this.velocityTracker;
            if (velocityTracker != null) {
                velocityTracker.recycle();
                this.velocityTracker = null;
            }
            if (this.isInline) {
                this.isInline = false;
                this.animationInProgress = 0;
                onPhotoClosed(object);
                this.containerView.setScaleX(1.0f);
                this.containerView.setScaleY(1.0f);
                return;
            }
            if (animated) {
                ClippingImageView[] animatingImageViews = getAnimatingImageViews(object);
                for (int i4 = 0; i4 < animatingImageViews.length; i4++) {
                    animatingImageViews[i4].setAnimationValues(this.animationValues);
                    animatingImageViews[i4].setVisibility(0);
                }
                this.animationInProgress = 3;
                this.containerView.invalidate();
                AnimatorSet animatorSet = new AnimatorSet();
                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                RectF drawRegion = null;
                if (object != null) {
                    drawRegion = object.imageReceiver.getDrawRegion();
                    layoutParams.width = (int) drawRegion.width();
                    layoutParams.height = (int) drawRegion.height();
                    int orientation = object.imageReceiver.getOrientation();
                    int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                    if (animatedOrientation != 0) {
                        orientation = animatedOrientation;
                    }
                    for (int i5 = 0; i5 < animatingImageViews.length; i5++) {
                        animatingImageViews[i5].setOrientation(orientation);
                        animatingImageViews[i5].setImageBitmap(object.thumb);
                    }
                } else {
                    layoutParams.width = (int) this.centerImage.getImageWidth();
                    layoutParams.height = (int) this.centerImage.getImageHeight();
                    for (int i6 = 0; i6 < animatingImageViews.length; i6++) {
                        animatingImageViews[i6].setOrientation(this.centerImage.getOrientation());
                        animatingImageViews[i6].setImageBitmap(this.centerImage.getBitmapSafe());
                    }
                }
                int i7 = layoutParams.width;
                if (i7 <= 0) {
                    layoutParams.width = 100;
                }
                if (layoutParams.height <= 0) {
                    layoutParams.height = 100;
                }
                if (this.sendPhotoType == 1) {
                    float measuredHeight = (this.photoCropView.getMeasuredHeight() - AndroidUtilities.dp(64.0f)) - (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                    float minSide = Math.min(this.photoCropView.getMeasuredWidth(), measuredHeight) - (AndroidUtilities.dp(16.0f) * 2);
                    float scaleX = minSide / layoutParams.width;
                    float scaleY = minSide / layoutParams.height;
                    statusBarHeight = Math.max(scaleX, scaleY);
                } else {
                    float scaleX2 = this.windowView.getMeasuredWidth() / layoutParams.width;
                    float scaleY2 = (AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) / layoutParams.height;
                    statusBarHeight = Math.min(scaleX2, scaleY2);
                }
                float width = layoutParams.width * this.scale * statusBarHeight;
                float height = layoutParams.height * this.scale * statusBarHeight;
                float xPos = (this.windowView.getMeasuredWidth() - width) / 2.0f;
                if (this.sendPhotoType == 1) {
                    float measuredHeight2 = this.photoCropView.getMeasuredHeight() - (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                    yPos = (measuredHeight2 - height) / 2.0f;
                } else {
                    yPos = ((AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - height) / 2.0f;
                }
                for (int i8 = 0; i8 < animatingImageViews.length; i8++) {
                    animatingImageViews[i8].setLayoutParams(layoutParams);
                    animatingImageViews[i8].setTranslationX(this.translationX + xPos);
                    animatingImageViews[i8].setTranslationY(yPos + this.translationY);
                    animatingImageViews[i8].setScaleX(this.scale * statusBarHeight);
                    animatingImageViews[i8].setScaleY(this.scale * statusBarHeight);
                }
                if (object != null) {
                    object.imageReceiver.setVisible(false, true);
                    int clipHorizontal = (int) Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                    int clipVertical = (int) Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                    if (this.pageBlocksAdapter != null && object.imageReceiver.isAspectFit()) {
                        clipHorizontal = 0;
                    }
                    int[] coords2 = new int[2];
                    object.parentView.getLocationInWindow(coords2);
                    int clipTop = (int) (((coords2[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (object.viewY + drawRegion.top)) + object.clipTopAddition);
                    if (clipTop < 0) {
                        clipTop = 0;
                    }
                    float f = object.viewY + drawRegion.top;
                    float f2 = drawRegion.bottom;
                    float scale2 = drawRegion.top;
                    int clipBottom = (int) (((f + (f2 - scale2)) - ((coords2[1] + object.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + object.clipBottomAddition);
                    if (clipBottom < 0) {
                        clipBottom = 0;
                    }
                    int clipTop2 = Math.max(clipTop, clipVertical);
                    int clipBottom2 = Math.max(clipBottom, clipVertical);
                    this.animationValues[0][0] = this.animatingImageView.getScaleX();
                    this.animationValues[0][1] = this.animatingImageView.getScaleY();
                    this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                    this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                    float[][] fArr = this.animationValues;
                    fArr[0][4] = 0.0f;
                    fArr[0][5] = 0.0f;
                    fArr[0][6] = 0.0f;
                    fArr[0][7] = 0.0f;
                    fArr[0][8] = 0.0f;
                    fArr[0][9] = 0.0f;
                    fArr[0][10] = 0.0f;
                    fArr[0][11] = 0.0f;
                    fArr[0][12] = 0.0f;
                    fArr[1][0] = object.scale;
                    this.animationValues[1][1] = object.scale;
                    float[] fArr2 = this.animationValues[1];
                    float f3 = drawRegion.left;
                    float width2 = object.scale;
                    fArr2[2] = object.viewX + (f3 * width2);
                    this.animationValues[1][3] = object.viewY + (drawRegion.top * object.scale);
                    this.animationValues[1][4] = clipHorizontal * object.scale;
                    this.animationValues[1][5] = clipTop2 * object.scale;
                    this.animationValues[1][6] = clipBottom2 * object.scale;
                    for (int a = 0; a < 4; a++) {
                        this.animationValues[1][a + 7] = object.radius != null ? object.radius[a] : 0.0f;
                    }
                    this.animationValues[1][11] = clipVertical * object.scale;
                    this.animationValues[1][12] = clipHorizontal * object.scale;
                    ArrayList<Animator> animators = new ArrayList<>((this.sendPhotoType == 1 ? 3 : 2) + animatingImageViews.length + (animatingImageViews.length > 1 ? 1 : 0));
                    int i9 = 0;
                    while (i9 < animatingImageViews.length) {
                        animators.add(ObjectAnimator.ofFloat(animatingImageViews[i9], AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, 0.0f, 1.0f));
                        i9++;
                        clipTop2 = clipTop2;
                        clipHorizontal = clipHorizontal;
                    }
                    if (animatingImageViews.length > 1) {
                        animators.add(ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, 0.0f));
                        animatingImageViews[1].setAdditionalTranslationX(-getLeftInset());
                    }
                    animators.add(ObjectAnimator.ofInt(this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0));
                    animators.add(ObjectAnimator.ofFloat(this.containerView, View.ALPHA, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(this.navigationBar, View.ALPHA, 0.0f));
                    if (this.sendPhotoType == 1) {
                        animators.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 0.0f));
                    }
                    animatorSet.playTogether(animators);
                } else {
                    int h = AndroidUtilities.displaySize.y + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
                    Animator[] animatorArr = new Animator[5];
                    animatorArr[0] = ObjectAnimator.ofInt(this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0);
                    animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, 0.0f);
                    ClippingImageView clippingImageView = this.animatingImageView;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr3 = new float[1];
                    fArr3[0] = this.translationY >= 0.0f ? h : -h;
                    animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, property, fArr3);
                    animatorArr[3] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, 0.0f);
                    animatorArr[4] = ObjectAnimator.ofFloat(this.navigationBar, View.ALPHA, 0.0f);
                    animatorSet.playTogether(animatorArr);
                }
                this.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda60
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.this.m4200lambda$closePhoto$72$orgtelegramuiPhotoViewer(object);
                    }
                };
                animatorSet.setDuration(200L);
                animatorSet.addListener(new AnonymousClass72());
                this.transitionAnimationStartTime = System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(2, null);
                }
                animatorSet.start();
            } else {
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.containerView, View.SCALE_X, 0.9f), ObjectAnimator.ofFloat(this.containerView, View.SCALE_Y, 0.9f), ObjectAnimator.ofInt(this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.navigationBar, View.ALPHA, 0.0f));
                this.animationInProgress = 2;
                this.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda61
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.this.m4201lambda$closePhoto$73$orgtelegramuiPhotoViewer(object);
                    }
                };
                animatorSet2.setDuration(200L);
                animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.73
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation2) {
                        if (PhotoViewer.this.animationEndRunnable != null) {
                            ChatActivity chatActivity2 = PhotoViewer.this.parentChatActivity;
                            if (chatActivity2 == null && PhotoViewer.this.parentAlert != null) {
                                BaseFragment baseFragment = PhotoViewer.this.parentAlert.getBaseFragment();
                                if (baseFragment instanceof ChatActivity) {
                                    chatActivity2 = (ChatActivity) baseFragment;
                                }
                            }
                            if (chatActivity2 != null) {
                                chatActivity2.m1899x4c8be4fe(PhotoViewer.this.animationEndRunnable);
                                return;
                            }
                            PhotoViewer.this.animationEndRunnable.run();
                            PhotoViewer.this.animationEndRunnable = null;
                        }
                    }
                });
                this.transitionAnimationStartTime = System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(2, null);
                }
                animatorSet2.start();
            }
            AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
            if (animatedFileDrawable != null) {
                animatedFileDrawable.removeSecondParentView(this.containerView);
                this.currentAnimation = null;
                this.centerImage.setImageBitmap((Drawable) null);
            }
            PhotoViewerProvider photoViewerProvider = this.placeProvider;
            if (photoViewerProvider != null && !photoViewerProvider.canScrollAway()) {
                this.placeProvider.cancelButtonPressed();
            }
        }
    }

    /* renamed from: lambda$closePhoto$72$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4200lambda$closePhoto$72$orgtelegramuiPhotoViewer(PlaceProviderObject object) {
        this.animationEndRunnable = null;
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(object);
    }

    /* renamed from: org.telegram.ui.PhotoViewer$72 */
    /* loaded from: classes4.dex */
    public class AnonymousClass72 extends AnimatorListenerAdapter {
        AnonymousClass72() {
            PhotoViewer.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$72$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass72.this.m4295lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$72();
                }
            });
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-PhotoViewer$72 */
        public /* synthetic */ void m4295lambda$onAnimationEnd$0$orgtelegramuiPhotoViewer$72() {
            if (PhotoViewer.this.animationEndRunnable != null) {
                PhotoViewer.this.animationEndRunnable.run();
                PhotoViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* renamed from: lambda$closePhoto$73$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4201lambda$closePhoto$73$orgtelegramuiPhotoViewer(PlaceProviderObject object) {
        this.animationEndRunnable = null;
        if (this.containerView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(object);
        this.containerView.setScaleX(1.0f);
        this.containerView.setScaleY(1.0f);
    }

    private ClippingImageView[] getAnimatingImageViews(PlaceProviderObject object) {
        boolean hasSecondAnimatingImageView = (AndroidUtilities.isTablet() || object == null || object.animatingImageView == null) ? false : true;
        ClippingImageView[] animatingImageViews = new ClippingImageView[(hasSecondAnimatingImageView ? 1 : 0) + 1];
        animatingImageViews[0] = this.animatingImageView;
        if (hasSecondAnimatingImageView) {
            animatingImageViews[1] = object.animatingImageView;
            object.animatingImageView.setAdditionalTranslationY(object.animatingImageViewYOffset);
        }
        return animatingImageViews;
    }

    private void removeObservers() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity == null || this.windowView == null) {
            return;
        }
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
        }
        removeObservers();
        releasePlayer(false);
        try {
            if (this.windowView.getParent() != null) {
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                wm.removeViewImmediate(this.windowView);
                onHideView();
            }
            this.windowView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.animatingImageView.setImageBitmap(null);
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.onDestroy();
        }
        if (this == PipInstance) {
            PipInstance = null;
        } else {
            Instance = null;
        }
        onHideView();
    }

    private void onPhotoClosed(final PlaceProviderObject object) {
        if (this.doneButtonPressed) {
            releasePlayer(true);
        }
        this.isVisible = false;
        this.cropInitied = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentFileLocationVideo = null;
        this.currentSecureDocument = null;
        this.currentPageBlock = null;
        this.currentPathObject = null;
        if (this.videoPlayerControlFrameLayout != null) {
            setVideoPlayerControlVisible(false, false);
        }
        CaptionScrollView captionScrollView = this.captionScrollView;
        if (captionScrollView != null) {
            captionScrollView.reset();
        }
        this.sendPhotoType = 0;
        this.isDocumentsPicker = false;
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.parentAlert = null;
        AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.removeSecondParentView(this.containerView);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            if (photoProgressViewArr[a] != null) {
                photoProgressViewArr[a].setBackgroundState(-1, false, true);
            }
        }
        requestVideoPreview(0);
        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
        if (videoTimelinePlayView != null) {
            videoTimelinePlayView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.videoTimelineView.destroy();
        }
        this.hintView.hide(false, 0);
        Bitmap bitmap = null;
        this.centerImage.setImageBitmap(bitmap);
        this.leftImage.setImageBitmap(bitmap);
        this.rightImage.setImageBitmap(bitmap);
        this.containerView.post(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.m4217lambda$onPhotoClosed$74$orgtelegramuiPhotoViewer(object);
            }
        });
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            photoViewerProvider.willHidePhotoViewer();
        }
        this.groupedPhotosListView.clear();
        PhotoViewerProvider photoViewerProvider2 = this.placeProvider;
        if (photoViewerProvider2 != null) {
            photoViewerProvider2.onClose();
        }
        this.placeProvider = null;
        this.selectedPhotosAdapter.notifyDataSetChanged();
        this.pageBlocksAdapter = null;
        this.disableShowCheck = false;
        this.shownControlsByEnd = false;
        this.videoCutStart = 0.0f;
        this.videoCutEnd = 1.0f;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null) {
            chatActivity.getFragmentView().invalidate();
        }
        Bitmap bitmap2 = this.videoFrameBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.videoFrameBitmap = null;
        }
    }

    /* renamed from: lambda$onPhotoClosed$74$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4217lambda$onPhotoClosed$74$orgtelegramuiPhotoViewer(PlaceProviderObject object) {
        this.animatingImageView.setImageBitmap(null);
        if (object != null && !AndroidUtilities.isTablet() && object.animatingImageView != null) {
            object.animatingImageView.setImageBitmap(null);
        }
        try {
            if (this.windowView.getParent() != null) {
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                wm.removeView(this.windowView);
                onHideView();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void redraw(final int count) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (count < 6 && (frameLayoutDrawer = this.containerView) != null) {
            frameLayoutDrawer.invalidate();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda52
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.m4219lambda$redraw$75$orgtelegramuiPhotoViewer(count);
                }
            }, 100L);
        }
    }

    /* renamed from: lambda$redraw$75$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4219lambda$redraw$75$orgtelegramuiPhotoViewer(int count) {
        redraw(count + 1);
    }

    public void onResume() {
        redraw(0);
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.seekTo(videoPlayer.getCurrentPosition() + 1);
            if (this.playerLooping) {
                this.videoPlayer.setLooping(true);
            }
        }
        PhotoPaintView photoPaintView = this.photoPaintView;
        if (photoPaintView != null) {
            photoPaintView.onResume();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
            return;
        }
        if (this.lastTitle != null) {
            closeCaptionEnter(true);
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null && this.playerLooping) {
            videoPlayer.setLooping(false);
        }
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    public void updateMinMax(float scale) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == 0 && this.textureUploaded) {
            scale *= Math.min(getContainerViewWidth() / this.videoTextureView.getMeasuredWidth(), getContainerViewHeight() / this.videoTextureView.getMeasuredHeight());
        }
        float w = this.centerImage.getImageWidth();
        float h = this.centerImage.getImageHeight();
        if (this.editState.cropState != null) {
            w *= this.editState.cropState.cropPw;
            h *= this.editState.cropState.cropPh;
        }
        int maxW = ((int) ((w * scale) - getContainerViewWidth())) / 2;
        int maxH = ((int) ((h * scale) - getContainerViewHeight())) / 2;
        if (maxW > 0) {
            this.minX = -maxW;
            this.maxX = maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = -maxH;
            this.maxY = maxH;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getAdditionX() {
        int i = this.currentEditMode;
        if (i == 1 || (i == 0 && this.sendPhotoType == 1)) {
            return AndroidUtilities.dp(16.0f);
        }
        if (i != 0 && i != 3) {
            return AndroidUtilities.dp(14.0f);
        }
        return 0;
    }

    private int getAdditionY() {
        int i = this.currentEditMode;
        int i2 = 0;
        if (i == 1 || (i == 0 && this.sendPhotoType == 1)) {
            int dp = AndroidUtilities.dp(16.0f);
            if (isStatusBarVisible()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp + i2;
        } else if (i == 3) {
            int dp2 = AndroidUtilities.dp(8.0f);
            if (isStatusBarVisible()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp2 + i2;
        } else if (i == 0) {
            return 0;
        } else {
            int dp3 = AndroidUtilities.dp(14.0f);
            if (isStatusBarVisible()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp3 + i2;
        }
    }

    public int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    public int getContainerViewWidth(int mode) {
        int width = this.containerView.getWidth();
        if (mode == 1 || (mode == 0 && this.sendPhotoType == 1)) {
            return width - AndroidUtilities.dp(32.0f);
        }
        if (mode != 0 && mode != 3) {
            return width - AndroidUtilities.dp(28.0f);
        }
        return width;
    }

    public int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    public int getContainerViewHeight(int mode) {
        return getContainerViewHeight(false, mode);
    }

    private int getContainerViewHeight(boolean trueHeight, int mode) {
        int height;
        if (trueHeight || this.inBubbleMode) {
            height = this.containerView.getMeasuredHeight();
        } else {
            height = AndroidUtilities.displaySize.y;
            if (mode == 0 && this.sendPhotoType != 1 && isStatusBarVisible()) {
                height += AndroidUtilities.statusBarHeight;
            }
        }
        if ((mode == 0 && this.sendPhotoType == 1) || mode == 1) {
            return height - AndroidUtilities.dp(144.0f);
        }
        if (mode == 2) {
            return height - AndroidUtilities.dp(214.0f);
        }
        if (mode == 3) {
            return height - (AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight());
        }
        return height;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.currentEditMode == 3 && this.animationStartTime != 0 && (ev.getActionMasked() == 0 || ev.getActionMasked() == 5)) {
            if (ev.getPointerCount() < 2) {
                return true;
            }
            cancelMoveZoomAnimation();
        }
        if (this.animationInProgress == 0 && this.animationStartTime == 0) {
            if (this.videoPlayerRewinder.rewindCount > 0) {
                if (ev.getAction() != 1 && ev.getAction() != 3) {
                    return true;
                }
                this.videoPlayerRewinder.cancelRewind();
                return false;
            }
            int i = this.currentEditMode;
            if (i == 2) {
                this.photoFilterView.onTouch(ev);
                return true;
            } else if (i == 1 || (i != 3 && this.sendPhotoType == 1)) {
                return true;
            } else {
                if (this.captionEditText.isPopupShowing() || this.captionEditText.isKeyboardVisible()) {
                    if (ev.getAction() == 1) {
                        closeCaptionEnter(true);
                    }
                    return true;
                } else if (this.currentEditMode == 0 && this.sendPhotoType != 1 && ev.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(ev) && this.doubleTap) {
                    this.doubleTap = false;
                    this.moving = false;
                    this.zooming = false;
                    checkMinMax(false);
                    return true;
                } else {
                    Tooltip tooltip = this.tooltip;
                    if (tooltip != null) {
                        tooltip.hide();
                    }
                    if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
                        this.discardTap = false;
                        if (!this.scroller.isFinished()) {
                            this.scroller.abortAnimation();
                        }
                        if (!this.draggingDown && !this.changingPage) {
                            if (this.canZoom && ev.getPointerCount() == 2) {
                                if (this.paintViewTouched == 1) {
                                    MotionEvent event = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                                    this.photoPaintView.onTouch(event);
                                    event.recycle();
                                    this.paintViewTouched = 2;
                                }
                                this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                                this.pinchStartScale = this.scale;
                                this.pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                                float y = (ev.getY(0) + ev.getY(1)) / 2.0f;
                                this.pinchCenterY = y;
                                this.pinchStartX = this.translationX;
                                this.pinchStartY = this.translationY;
                                this.zooming = true;
                                this.moving = false;
                                if (this.currentEditMode == 3) {
                                    this.moveStartX = this.pinchCenterX;
                                    this.moveStartY = y;
                                    this.draggingDown = false;
                                    this.canDragDown = false;
                                }
                                hidePressedDrawables();
                                VelocityTracker velocityTracker = this.velocityTracker;
                                if (velocityTracker != null) {
                                    velocityTracker.clear();
                                }
                            } else if (ev.getPointerCount() == 1) {
                                if (this.currentEditMode != 3) {
                                    this.moveStartX = ev.getX();
                                    float y2 = ev.getY();
                                    this.moveStartY = y2;
                                    this.dragY = y2;
                                    this.draggingDown = false;
                                    this.canDragDown = true;
                                    VelocityTracker velocityTracker2 = this.velocityTracker;
                                    if (velocityTracker2 != null) {
                                        velocityTracker2.clear();
                                    }
                                } else if (this.paintViewTouched == 0) {
                                    this.photoPaintView.getHitRect(this.hitRect);
                                    if (this.hitRect.contains((int) ev.getX(), (int) ev.getY())) {
                                        MotionEvent event2 = MotionEvent.obtain(ev);
                                        event2.offsetLocation(-this.photoPaintView.getX(), -this.photoPaintView.getY());
                                        this.photoPaintView.onTouch(event2);
                                        event2.recycle();
                                        this.paintViewTouched = 1;
                                    }
                                }
                            }
                        }
                        if (ev.getActionMasked() == 0) {
                            this.longPressX = ev.getX();
                            AndroidUtilities.runOnUIThread(this.longPressRunnable, 300L);
                        } else {
                            AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                        }
                    } else if (ev.getActionMasked() == 2) {
                        if (this.canZoom && ev.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                            this.discardTap = true;
                            if (this.currentEditMode == 3) {
                                float newPinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                                float newPinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                                float moveDx = this.moveStartX - newPinchCenterX;
                                float moveDy = this.moveStartY - newPinchCenterY;
                                this.moveStartX = newPinchCenterX;
                                this.moveStartY = newPinchCenterY;
                                float f = this.translationX;
                                if (f < this.minX || f > this.maxX) {
                                    moveDx /= 3.0f;
                                }
                                float f2 = this.translationY;
                                if (f2 < this.minY || f2 > this.maxY) {
                                    moveDy /= 3.0f;
                                }
                                this.pinchStartX = ((this.pinchCenterX - (getContainerViewWidth() / 2)) - (((this.pinchCenterX - (getContainerViewWidth() / 2)) - this.translationX) / (this.scale / this.pinchStartScale))) - moveDx;
                                this.pinchStartY = ((this.pinchCenterY - (getContainerViewHeight() / 2)) - (((this.pinchCenterY - (getContainerViewHeight() / 2)) - this.translationY) / (this.scale / this.pinchStartScale))) - moveDy;
                                this.pinchCenterX = newPinchCenterX;
                                this.pinchCenterY = newPinchCenterY;
                            }
                            this.scale = (((float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0))) / this.pinchStartDistance) * this.pinchStartScale;
                            this.translationX = (this.pinchCenterX - (getContainerViewWidth() / 2)) - (((this.pinchCenterX - (getContainerViewWidth() / 2)) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                            float containerViewHeight = this.pinchCenterY - (getContainerViewHeight() / 2);
                            float containerViewHeight2 = (this.pinchCenterY - (getContainerViewHeight() / 2)) - this.pinchStartY;
                            float f3 = this.scale;
                            this.translationY = containerViewHeight - (containerViewHeight2 * (f3 / this.pinchStartScale));
                            updateMinMax(f3);
                            this.containerView.invalidate();
                        } else if (ev.getPointerCount() == 1) {
                            if (this.paintViewTouched == 1) {
                                MotionEvent event3 = MotionEvent.obtain(ev);
                                event3.offsetLocation(-this.photoPaintView.getX(), -this.photoPaintView.getY());
                                this.photoPaintView.onTouch(event3);
                                event3.recycle();
                                return true;
                            }
                            VelocityTracker velocityTracker3 = this.velocityTracker;
                            if (velocityTracker3 != null) {
                                velocityTracker3.addMovement(ev);
                            }
                            float dx = Math.abs(ev.getX() - this.moveStartX);
                            float dy = Math.abs(ev.getY() - this.dragY);
                            int i2 = this.touchSlop;
                            if (dx > i2 || dy > i2) {
                                this.discardTap = true;
                                hidePressedDrawables();
                                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                                QualityChooseView qualityChooseView = this.qualityChooseView;
                                if (qualityChooseView != null && qualityChooseView.getVisibility() == 0) {
                                    return true;
                                }
                            }
                            if (this.placeProvider.canScrollAway() && this.currentEditMode == 0 && this.sendPhotoType != 1 && this.canDragDown && !this.draggingDown && this.scale == 1.0f && dy >= AndroidUtilities.dp(30.0f) && dy / 2.0f > dx) {
                                this.draggingDown = true;
                                hidePressedDrawables();
                                this.moving = false;
                                this.dragY = ev.getY();
                                if (this.isActionBarVisible && this.containerView.getTag() != null) {
                                    toggleActionBar(false, true);
                                } else if (this.pickerView.getVisibility() == 0) {
                                    toggleActionBar(false, true);
                                    togglePhotosListView(false, true);
                                    toggleCheckImageView(false);
                                }
                                return true;
                            } else if (this.draggingDown) {
                                this.translationY = ev.getY() - this.dragY;
                                this.containerView.invalidate();
                            } else if (!this.invalidCoords && this.animationStartTime == 0) {
                                float moveDx2 = this.moveStartX - ev.getX();
                                float moveDy2 = this.moveStartY - ev.getY();
                                if (this.moving || this.currentEditMode != 0 || ((this.scale == 1.0f && Math.abs(moveDy2) + AndroidUtilities.dp(12.0f) < Math.abs(moveDx2)) || this.scale != 1.0f)) {
                                    if (!this.moving) {
                                        moveDx2 = 0.0f;
                                        moveDy2 = 0.0f;
                                        this.moving = true;
                                        this.canDragDown = false;
                                        hidePressedDrawables();
                                    }
                                    this.moveStartX = ev.getX();
                                    this.moveStartY = ev.getY();
                                    updateMinMax(this.scale);
                                    if ((this.translationX < this.minX && (this.currentEditMode != 0 || !this.rightImage.hasImageSet())) || (this.translationX > this.maxX && (this.currentEditMode != 0 || !this.leftImage.hasImageSet()))) {
                                        moveDx2 /= 3.0f;
                                    }
                                    float f4 = this.maxY;
                                    if (f4 == 0.0f) {
                                        float f5 = this.minY;
                                        if (f5 == 0.0f && this.currentEditMode == 0 && this.sendPhotoType != 1) {
                                            float f6 = this.translationY;
                                            if (f6 - moveDy2 < f5) {
                                                this.translationY = f5;
                                                moveDy2 = 0.0f;
                                            } else if (f6 - moveDy2 > f4) {
                                                this.translationY = f4;
                                                moveDy2 = 0.0f;
                                            }
                                            this.translationX -= moveDx2;
                                            if (this.scale == 1.0f || this.currentEditMode != 0) {
                                                this.translationY -= moveDy2;
                                            }
                                            this.containerView.invalidate();
                                        }
                                    }
                                    float f7 = this.translationY;
                                    if (f7 < this.minY || f7 > f4) {
                                        moveDy2 /= 3.0f;
                                    }
                                    this.translationX -= moveDx2;
                                    if (this.scale == 1.0f) {
                                    }
                                    this.translationY -= moveDy2;
                                    this.containerView.invalidate();
                                }
                            } else {
                                this.invalidCoords = false;
                                this.moveStartX = ev.getX();
                                this.moveStartY = ev.getY();
                            }
                        }
                    } else if (ev.getActionMasked() == 3 || ev.getActionMasked() == 1 || ev.getActionMasked() == 6) {
                        AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                        if (this.paintViewTouched == 1) {
                            if (this.photoPaintView != null) {
                                MotionEvent event4 = MotionEvent.obtain(ev);
                                event4.offsetLocation(-this.photoPaintView.getX(), -this.photoPaintView.getY());
                                this.photoPaintView.onTouch(event4);
                                event4.recycle();
                            }
                            this.paintViewTouched = 0;
                            return true;
                        }
                        this.paintViewTouched = 0;
                        if (this.zooming) {
                            this.invalidCoords = true;
                            float f8 = this.scale;
                            if (f8 < 1.0f) {
                                updateMinMax(1.0f);
                                animateTo(1.0f, 0.0f, 0.0f, true);
                            } else if (f8 > 3.0f) {
                                float atx = (this.pinchCenterX - (getContainerViewWidth() / 2)) - (((this.pinchCenterX - (getContainerViewWidth() / 2)) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                                float aty = (this.pinchCenterY - (getContainerViewHeight() / 2)) - (((this.pinchCenterY - (getContainerViewHeight() / 2)) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                                updateMinMax(3.0f);
                                if (atx < this.minX) {
                                    atx = this.minX;
                                } else if (atx > this.maxX) {
                                    atx = this.maxX;
                                }
                                if (aty < this.minY) {
                                    aty = this.minY;
                                } else if (aty > this.maxY) {
                                    aty = this.maxY;
                                }
                                animateTo(3.0f, atx, aty, true);
                            } else {
                                checkMinMax(true);
                                if (this.currentEditMode == 3) {
                                    float moveToX = this.translationX;
                                    float moveToY = this.translationY;
                                    updateMinMax(this.scale);
                                    float f9 = this.translationX;
                                    if (f9 < this.minX) {
                                        moveToX = this.minX;
                                    } else if (f9 > this.maxX) {
                                        moveToX = this.maxX;
                                    }
                                    float f10 = this.translationY;
                                    if (f10 < this.minY) {
                                        moveToY = this.minY;
                                    } else if (f10 > this.maxY) {
                                        moveToY = this.maxY;
                                    }
                                    animateTo(this.scale, moveToX, moveToY, false);
                                }
                            }
                            this.zooming = false;
                            this.moving = false;
                        } else if (this.draggingDown) {
                            if (Math.abs(this.dragY - ev.getY()) > getContainerViewHeight() / 6.0f) {
                                if (enableSwipeToPiP() && this.dragY - ev.getY() > 0.0f) {
                                    switchToPip(true);
                                } else {
                                    closePhoto(true, false);
                                }
                            } else {
                                if (this.pickerView.getVisibility() == 0) {
                                    toggleActionBar(true, true);
                                    toggleCheckImageView(true);
                                }
                                animateTo(1.0f, 0.0f, 0.0f, false);
                            }
                            this.draggingDown = false;
                        } else if (this.moving) {
                            float moveToX2 = this.translationX;
                            float moveToY2 = this.translationY;
                            updateMinMax(this.scale);
                            this.moving = false;
                            this.canDragDown = true;
                            float velocity = 0.0f;
                            VelocityTracker velocityTracker4 = this.velocityTracker;
                            if (velocityTracker4 != null && this.scale == 1.0f) {
                                velocityTracker4.computeCurrentVelocity(1000);
                                velocity = this.velocityTracker.getXVelocity();
                            }
                            if (this.currentEditMode == 0 && this.sendPhotoType != 1) {
                                if ((this.translationX >= this.minX - (getContainerViewWidth() / 3) && velocity >= (-AndroidUtilities.dp(650.0f))) || !this.rightImage.hasImageSet()) {
                                    if ((this.translationX > this.maxX + (getContainerViewWidth() / 3) || velocity > AndroidUtilities.dp(650.0f)) && this.leftImage.hasImageSet()) {
                                        goToPrev();
                                        return true;
                                    }
                                } else {
                                    goToNext();
                                    return true;
                                }
                            }
                            float f11 = this.translationX;
                            if (f11 < this.minX) {
                                moveToX2 = this.minX;
                            } else if (f11 > this.maxX) {
                                moveToX2 = this.maxX;
                            }
                            float f12 = this.translationY;
                            if (f12 < this.minY) {
                                moveToY2 = this.minY;
                            } else if (f12 > this.maxY) {
                                moveToY2 = this.maxY;
                            }
                            animateTo(this.scale, moveToX2, moveToY2, false);
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        float f = this.translationX;
        if (f < this.minX) {
            moveToX = this.minX;
        } else if (f > this.maxX) {
            moveToX = this.maxX;
        }
        float f2 = this.translationY;
        if (f2 < this.minY) {
            moveToY = this.minY;
        } else if (f2 > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2.0f) * this.scale;
        }
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - getContainerViewWidth()) - extra) - (AndroidUtilities.dp(30.0f) / 2), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2.0f) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, this.maxX + getContainerViewWidth() + extra + (AndroidUtilities.dp(30.0f) / 2), this.translationY, false);
    }

    private void cancelMoveZoomAnimation() {
        AnimatorSet animatorSet = this.imageMoveAnimation;
        if (animatorSet == null) {
            return;
        }
        float f = this.scale;
        float f2 = this.animationValue;
        float ts = f + ((this.animateToScale - f) * f2);
        float f3 = this.translationX;
        float tx = f3 + ((this.animateToX - f3) * f2);
        float f4 = this.translationY;
        float ty = f4 + ((this.animateToY - f4) * f2);
        float f5 = this.rotate;
        float tr = f5 + ((this.animateToRotate - f5) * f2);
        animatorSet.cancel();
        this.scale = ts;
        this.translationX = tx;
        this.translationY = ty;
        this.animationStartTime = 0L;
        this.rotate = tr;
        updateMinMax(ts);
        this.zoomAnimation = false;
        this.containerView.invalidate();
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale == newScale && this.translationX == newTx && this.translationY == newTy) {
            return;
        }
        this.zoomAnimation = isZoom;
        this.animateToScale = newScale;
        this.animateToX = newTx;
        this.animateToY = newTy;
        this.animationStartTime = System.currentTimeMillis();
        AnimatorSet animatorSet = new AnimatorSet();
        this.imageMoveAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
        this.imageMoveAnimation.setInterpolator(this.interpolator);
        this.imageMoveAnimation.setDuration(duration);
        this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.74
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.containerView.invalidate();
            }
        });
        this.imageMoveAnimation.start();
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.containerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    private void switchToNextIndex(int add, boolean init) {
        if (this.currentMessageObject != null) {
            releasePlayer(false);
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        } else if (this.currentPageBlock != null) {
            TLObject media = this.pageBlocksAdapter.getMedia(this.currentIndex);
            if (media instanceof TLRPC.Document) {
                releasePlayer(false);
                FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document) media);
            }
        }
        GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
        if (groupedPhotosListView != null) {
            groupedPhotosListView.setAnimateBackground(true);
        }
        this.playerAutoStarted = false;
        setImageIndex(this.currentIndex + add, init, true);
        if (shouldMessageObjectAutoPlayed(this.currentMessageObject) || shouldIndexAutoPlayed(this.currentIndex)) {
            this.playerAutoStarted = true;
            onActionClick(true);
            checkProgress(0, false, true);
        }
        checkFullscreenButton();
    }

    public boolean shouldMessageObjectAutoPlayed(MessageObject messageObject) {
        return messageObject != null && messageObject.isVideo() && (messageObject.mediaExists || messageObject.attachPathExists || (messageObject.canStreamVideo() && SharedConfig.streamMedia)) && SharedConfig.autoplayVideo;
    }

    private boolean shouldIndexAutoPlayed(int index) {
        File mediaFile;
        PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
        if (pageBlocksAdapter != null && pageBlocksAdapter.isVideo(index) && SharedConfig.autoplayVideo && (mediaFile = this.pageBlocksAdapter.getFile(index)) != null && mediaFile.exists()) {
            return true;
        }
        return false;
    }

    public float getCropFillScale(boolean rotated) {
        ImageReceiver imageReceiver = this.centerImage;
        int width = rotated ? imageReceiver.getBitmapHeight() : imageReceiver.getBitmapWidth();
        ImageReceiver imageReceiver2 = this.centerImage;
        int height = rotated ? imageReceiver2.getBitmapWidth() : imageReceiver2.getBitmapHeight();
        float statusBarHeight = isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0;
        float measuredHeight = (this.photoCropView.getMeasuredHeight() - AndroidUtilities.dp(64.0f)) - statusBarHeight;
        float minSide = Math.min(this.photoCropView.getMeasuredWidth(), measuredHeight) - (AndroidUtilities.dp(16.0f) * 2);
        return Math.max(minSide / width, minSide / height);
    }

    public boolean isStatusBarVisible() {
        return Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode;
    }

    /* JADX WARN: Removed duplicated region for block: B:174:0x03d1  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x0527  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x057b  */
    /* JADX WARN: Removed duplicated region for block: B:246:0x061c  */
    /* JADX WARN: Removed duplicated region for block: B:247:0x061f  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x065b  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x074b  */
    /* JADX WARN: Removed duplicated region for block: B:294:0x075a  */
    /* JADX WARN: Removed duplicated region for block: B:302:0x079d  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x07d9  */
    /* JADX WARN: Removed duplicated region for block: B:331:0x07e0  */
    /* JADX WARN: Removed duplicated region for block: B:416:0x097f  */
    /* JADX WARN: Removed duplicated region for block: B:425:0x09bc  */
    /* JADX WARN: Removed duplicated region for block: B:428:0x09c9  */
    /* JADX WARN: Removed duplicated region for block: B:438:0x0a34  */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0a55  */
    /* JADX WARN: Removed duplicated region for block: B:451:0x0a66  */
    /* JADX WARN: Removed duplicated region for block: B:458:0x0a93  */
    /* JADX WARN: Removed duplicated region for block: B:489:0x0b07  */
    /* JADX WARN: Removed duplicated region for block: B:492:0x0b30  */
    /* JADX WARN: Removed duplicated region for block: B:496:0x0b3d  */
    /* JADX WARN: Removed duplicated region for block: B:520:0x0bca  */
    /* JADX WARN: Removed duplicated region for block: B:522:0x0bd2  */
    /* JADX WARN: Removed duplicated region for block: B:527:0x0bef  */
    /* JADX WARN: Removed duplicated region for block: B:530:0x0bf9  */
    /* JADX WARN: Removed duplicated region for block: B:540:0x0c25  */
    /* JADX WARN: Removed duplicated region for block: B:570:0x0d0a  */
    /* JADX WARN: Removed duplicated region for block: B:613:0x0e97  */
    /* JADX WARN: Removed duplicated region for block: B:618:0x0eb2  */
    /* JADX WARN: Removed duplicated region for block: B:638:0x0f0c  */
    /* JADX WARN: Removed duplicated region for block: B:653:0x0cf0 A[EDGE_INSN: B:653:0x0cf0->B:567:0x0cf0 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onDraw(android.graphics.Canvas r55) {
        /*
            Method dump skipped, instructions count: 3930
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    /* renamed from: lambda$onDraw$76$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4214lambda$onDraw$76$orgtelegramuiPhotoViewer() {
        switchToNextIndex(1, false);
    }

    /* renamed from: lambda$onDraw$77$org-telegram-ui-PhotoViewer */
    public /* synthetic */ void m4215lambda$onDraw$77$orgtelegramuiPhotoViewer() {
        switchToNextIndex(-1, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0052  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0069  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawProgress(android.graphics.Canvas r10, float r11, float r12, float r13, float r14) {
        /*
            Method dump skipped, instructions count: 237
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.drawProgress(android.graphics.Canvas, float, float, float, float):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:71:0x0175  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0182  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x01df  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int[] applyCrop(android.graphics.Canvas r30, int r31, int r32, int r33, int r34, float r35, org.telegram.ui.Components.Crop.CropTransform r36, org.telegram.messenger.MediaController.CropState r37) {
        /*
            Method dump skipped, instructions count: 514
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.applyCrop(android.graphics.Canvas, int, int, int, int, float, org.telegram.ui.Components.Crop.CropTransform, org.telegram.messenger.MediaController$CropState):int[]");
    }

    public void onActionClick(boolean download) {
        MessageObject messageObject = this.currentMessageObject;
        if ((messageObject == null && this.currentBotInlineResult == null && this.pageBlocksAdapter == null) || this.currentFileNames[0] == null) {
            return;
        }
        Uri uri = null;
        File file = null;
        this.isStreaming = false;
        if (messageObject != null) {
            if (messageObject.messageOwner.attachPath != null && this.currentMessageObject.messageOwner.attachPath.length() != 0) {
                file = new File(this.currentMessageObject.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            if (file == null) {
                file = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
                if (!file.exists()) {
                    file = null;
                    if (SharedConfig.streamMedia && !DialogObject.isEncryptedDialog(this.currentMessageObject.getDialogId()) && this.currentMessageObject.isVideo() && this.currentMessageObject.canStreamVideo()) {
                        try {
                            int reference = FileLoader.getInstance(this.currentMessageObject.currentAccount).getFileReference(this.currentMessageObject);
                            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
                            TLRPC.Document document = this.currentMessageObject.getDocument();
                            StringBuilder sb = new StringBuilder();
                            sb.append("?account=");
                            sb.append(this.currentMessageObject.currentAccount);
                            sb.append("&id=");
                            sb.append(document.id);
                            sb.append("&hash=");
                            sb.append(document.access_hash);
                            sb.append("&dc=");
                            sb.append(document.dc_id);
                            sb.append("&size=");
                            sb.append(document.size);
                            sb.append("&mime=");
                            sb.append(URLEncoder.encode(document.mime_type, "UTF-8"));
                            sb.append("&rid=");
                            sb.append(reference);
                            sb.append("&name=");
                            sb.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), "UTF-8"));
                            sb.append("&reference=");
                            sb.append(Utilities.bytesToHex(document.file_reference != null ? document.file_reference : new byte[0]));
                            String params = sb.toString();
                            uri = Uri.parse("tg://" + this.currentMessageObject.getFileName() + params);
                            this.isStreaming = true;
                            checkProgress(0, false, false);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } else {
            TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
            if (botInlineResult != null) {
                if (botInlineResult.document != null) {
                    file = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.currentBotInlineResult.document);
                    if (!file.exists()) {
                        file = null;
                    }
                } else if (this.currentBotInlineResult.content instanceof TLRPC.TL_webDocument) {
                    File directory = FileLoader.getDirectory(4);
                    file = new File(directory, Utilities.MD5(this.currentBotInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content.url, "mp4"));
                    if (!file.exists()) {
                        file = null;
                    }
                }
            } else {
                PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                if (pageBlocksAdapter != null) {
                    TLObject media = pageBlocksAdapter.getMedia(this.currentIndex);
                    if (!(media instanceof TLRPC.Document)) {
                        return;
                    }
                    file = this.pageBlocksAdapter.getFile(this.currentIndex);
                    if (file != null && !file.exists()) {
                        file = null;
                    }
                }
            }
        }
        if (file != null && uri == null) {
            uri = Uri.fromFile(file);
        }
        if (uri == null) {
            if (download) {
                if (this.currentMessageObject != null) {
                    if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
                    } else {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    }
                } else {
                    TLRPC.BotInlineResult botInlineResult2 = this.currentBotInlineResult;
                    if (botInlineResult2 != null) {
                        if (botInlineResult2.document != null) {
                            if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                                FileLoader.getInstance(this.currentAccount).loadFile(this.currentBotInlineResult.document, this.currentMessageObject, 1, 0);
                            } else {
                                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentBotInlineResult.document);
                            }
                        } else if (this.currentBotInlineResult.content instanceof TLRPC.TL_webDocument) {
                            if (!ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content.url)) {
                                ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content.url, "mp4", this.currentAccount);
                            } else {
                                ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content.url);
                            }
                        }
                    } else if (this.pageBlocksAdapter != null) {
                        if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                            FileLoader.getInstance(this.currentAccount).loadFile((TLRPC.Document) this.pageBlocksAdapter.getMedia(this.currentIndex), this.pageBlocksAdapter.getParentObject(), 1, 1);
                        } else {
                            FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.Document) this.pageBlocksAdapter.getMedia(this.currentIndex));
                        }
                    }
                }
                Drawable drawable = this.centerImage.getStaticThumb();
                if (drawable instanceof OtherDocumentPlaceholderDrawable) {
                    ((OtherDocumentPlaceholderDrawable) drawable).checkFileExist();
                }
            }
        } else if (this.sharedMediaType == 1 && !this.currentMessageObject.canPreviewDocument()) {
            AndroidUtilities.openDocument(this.currentMessageObject, this.parentActivity, null);
        } else {
            preparePlayer(uri, true, false);
        }
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onDown(MotionEvent e) {
        if (!this.doubleTap && this.checkImageView.getVisibility() != 0) {
            boolean[] zArr = this.drawPressedDrawable;
            if (!zArr[0] && !zArr[1]) {
                float x = e.getX();
                int side = Math.min((int) TsExtractor.TS_STREAM_TYPE_E_AC3, this.containerView.getMeasuredWidth() / 8);
                if (x < side) {
                    if (this.leftImage.hasImageSet()) {
                        this.drawPressedDrawable[0] = true;
                        this.containerView.invalidate();
                    }
                } else if (x > this.containerView.getMeasuredWidth() - side && this.rightImage.hasImageSet()) {
                    this.drawPressedDrawable[1] = true;
                    this.containerView.invalidate();
                }
            }
        }
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean canDoubleTap(MotionEvent e) {
        MessageObject messageObject;
        if (this.checkImageView.getVisibility() != 0) {
            boolean[] zArr = this.drawPressedDrawable;
            if (!zArr[0] && !zArr[1]) {
                float x = e.getX();
                int side = Math.min((int) TsExtractor.TS_STREAM_TYPE_E_AC3, this.containerView.getMeasuredWidth() / 8);
                if ((x >= side && x <= this.containerView.getMeasuredWidth() - side) || (messageObject = this.currentMessageObject) == null) {
                    return true;
                }
                return messageObject.isVideo() && SystemClock.elapsedRealtime() - this.lastPhotoSetTime >= 500 && canDoubleTapSeekVideo(e);
            }
        }
        return true;
    }

    private void hidePressedDrawables() {
        boolean[] zArr = this.drawPressedDrawable;
        zArr[1] = false;
        zArr[0] = false;
        this.containerView.invalidate();
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public void onUp(MotionEvent e) {
        hidePressedDrawables();
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public void onShowPress(MotionEvent e) {
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onSingleTapUp(MotionEvent e) {
        if (!this.canZoom && !this.doubleTapEnabled) {
            return onSingleTapConfirmed(e);
        }
        if (this.containerView.getTag() != null && this.photoProgressViews[0] != null && this.containerView != null) {
            float x = e.getX();
            float y = e.getY();
            boolean rez = false;
            if (x >= (getContainerViewWidth() - AndroidUtilities.dp(100.0f)) / 2.0f && x <= (getContainerViewWidth() + AndroidUtilities.dp(100.0f)) / 2.0f && y >= (getContainerViewHeight() - AndroidUtilities.dp(100.0f)) / 2.0f && y <= (getContainerViewHeight() + AndroidUtilities.dp(100.0f)) / 2.0f) {
                rez = onSingleTapConfirmed(e);
            }
            if (rez) {
                this.discardTap = true;
                return true;
            }
        }
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public void onLongPress(MotionEvent ev) {
    }

    public void onLongPress() {
        boolean forward;
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null && this.videoPlayerControlVisible && this.scale <= 1.1f) {
            long current = videoPlayer.getCurrentPosition();
            long total = this.videoPlayer.getDuration();
            if (current == C.TIME_UNSET || total < 15000) {
                return;
            }
            float x = this.longPressX;
            int width = getContainerViewWidth();
            if (x >= (width / 3) * 2) {
                forward = true;
            } else if (x < width / 3) {
                forward = false;
            } else {
                return;
            }
            this.videoPlayerRewinder.startRewind(this.videoPlayer, forward, this.currentVideoSpeed);
        }
    }

    public VideoPlayerRewinder getVideoPlayerRewinder() {
        return this.videoPlayerRewinder;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale != 1.0f) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
            return false;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent e) {
        MessageObject messageObject;
        if (this.discardTap) {
            return false;
        }
        float x = e.getX();
        float y = e.getY();
        if (this.checkImageView.getVisibility() != 0 && y > ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(40.0f)) {
            int side = Math.min((int) TsExtractor.TS_STREAM_TYPE_E_AC3, this.containerView.getMeasuredWidth() / 8);
            if (x < side) {
                if (this.leftImage.hasImageSet()) {
                    switchToNextIndex(-1, true);
                    return true;
                }
            } else if (x > this.containerView.getMeasuredWidth() - side && this.rightImage.hasImageSet()) {
                switchToNextIndex(1, true);
                return true;
            }
        }
        if (this.containerView.getTag() != null) {
            AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
            boolean drawTextureView = aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == 0;
            if (this.sharedMediaType == 1 && (messageObject = this.currentMessageObject) != null) {
                if (!messageObject.canPreviewDocument()) {
                    float vy = (getContainerViewHeight() - AndroidUtilities.dp(360.0f)) / 2.0f;
                    if (y >= vy && y <= AndroidUtilities.dp(360.0f) + vy) {
                        onActionClick(true);
                        return true;
                    }
                }
            } else {
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                if (photoProgressViewArr[0] != null && this.containerView != null) {
                    int state = photoProgressViewArr[0].backgroundState;
                    if (x >= (getContainerViewWidth() - AndroidUtilities.dp(100.0f)) / 2.0f && x <= (getContainerViewWidth() + AndroidUtilities.dp(100.0f)) / 2.0f && y >= (getContainerViewHeight() - AndroidUtilities.dp(100.0f)) / 2.0f && y <= (getContainerViewHeight() + AndroidUtilities.dp(100.0f)) / 2.0f) {
                        if (!drawTextureView) {
                            if (state > 0 && state <= 3) {
                                onActionClick(true);
                                checkProgress(0, false, true);
                                return true;
                            }
                        } else if ((state == 3 || state == 4) && this.photoProgressViews[0].isVisible()) {
                            this.manuallyPaused = true;
                            toggleVideoPlayer();
                            return true;
                        }
                    }
                }
            }
            toggleActionBar(!this.isActionBarVisible, true);
        } else {
            int i = this.sendPhotoType;
            if (i == 0 || i == 4) {
                if (this.isCurrentVideo) {
                    VideoPlayer videoPlayer = this.videoPlayer;
                    if (videoPlayer != null && !this.muteVideo && i != 1) {
                        videoPlayer.setVolume(1.0f);
                    }
                    this.manuallyPaused = true;
                    toggleVideoPlayer();
                } else {
                    this.checkImageView.performClick();
                }
            } else {
                TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
                if (botInlineResult != null && (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    int state2 = this.photoProgressViews[0].backgroundState;
                    if (state2 > 0 && state2 <= 3 && x >= (getContainerViewWidth() - AndroidUtilities.dp(100.0f)) / 2.0f && x <= (getContainerViewWidth() + AndroidUtilities.dp(100.0f)) / 2.0f && y >= (getContainerViewHeight() - AndroidUtilities.dp(100.0f)) / 2.0f && y <= (getContainerViewHeight() + AndroidUtilities.dp(100.0f)) / 2.0f) {
                        onActionClick(true);
                        checkProgress(0, false, true);
                        return true;
                    }
                } else if (this.sendPhotoType == 2 && this.isCurrentVideo) {
                    this.manuallyPaused = true;
                    toggleVideoPlayer();
                }
            }
        }
        return true;
    }

    private boolean canDoubleTapSeekVideo(MotionEvent e) {
        if (this.videoPlayer == null) {
            return false;
        }
        int width = getContainerViewWidth();
        float x = e.getX();
        boolean forward = x >= ((float) ((width / 3) * 2));
        long current = this.videoPlayer.getCurrentPosition();
        long total = this.videoPlayer.getDuration();
        if (current == C.TIME_UNSET || total <= 15000) {
            return false;
        }
        return !forward || total - current > 10000;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean onDoubleTap(MotionEvent e) {
        boolean z;
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null && this.videoPlayerControlVisible) {
            long current = videoPlayer.getCurrentPosition();
            long total = this.videoPlayer.getDuration();
            float x = e.getX();
            int width = getContainerViewWidth();
            if (x >= (width / 3) * 2) {
            }
            if (canDoubleTapSeekVideo(e)) {
                if (x >= (width / 3) * 2) {
                    current += 10000;
                } else if (x < width / 3) {
                    current -= 10000;
                }
                if (current != current) {
                    boolean apply = true;
                    if (current > total) {
                        current = total;
                    } else if (current < 0) {
                        if (current < -9000) {
                            apply = false;
                        }
                        current = 0;
                    }
                    if (!apply) {
                        return true;
                    }
                    this.videoForwardDrawable.setOneShootAnimation(true);
                    this.videoForwardDrawable.setLeftSide(x < ((float) (width / 3)));
                    this.videoForwardDrawable.addTime(10000L);
                    this.videoPlayer.seekTo(current);
                    this.containerView.invalidate();
                    this.videoPlayerSeekbar.setProgress(((float) current) / ((float) total), true);
                    this.videoPlayerSeekbarView.invalidate();
                    return true;
                }
            }
        }
        if (this.canZoom) {
            float f = this.scale;
            if ((f == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f)) || this.animationStartTime != 0 || this.animationInProgress != 0) {
                return false;
            }
            if (f == 1.0f) {
                float atx = (e.getX() - (getContainerViewWidth() / 2)) - (((e.getX() - (getContainerViewWidth() / 2)) - this.translationX) * (3.0f / this.scale));
                float aty = (e.getY() - (getContainerViewHeight() / 2)) - (((e.getY() - (getContainerViewHeight() / 2)) - this.translationY) * (3.0f / this.scale));
                updateMinMax(3.0f);
                if (atx < this.minX) {
                    atx = this.minX;
                } else if (atx > this.maxX) {
                    atx = this.maxX;
                }
                if (aty < this.minY) {
                    aty = this.minY;
                } else if (aty > this.maxY) {
                    aty = this.maxY;
                }
                z = true;
                animateTo(3.0f, atx, aty, true);
            } else {
                z = true;
                animateTo(1.0f, 0.0f, 0.0f, true);
            }
            this.doubleTap = z;
            hidePressedDrawables();
            return z;
        }
        return false;
    }

    private boolean enableSwipeToPiP() {
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    /* loaded from: classes4.dex */
    public class QualityChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private int sideSide;
        private int startMovingQuality;
        private TextPaint textPaint;
        private Paint paint = new Paint(1);
        private String lowQualityDescription = LocaleController.getString("AccDescrVideoCompressLow", R.string.AccDescrVideoCompressLow);
        private String hightQualityDescription = LocaleController.getString("AccDescrVideoCompressHigh", R.string.AccDescrVideoCompressHigh);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public QualityChooseView(Context context) {
            super(context);
            PhotoViewer.this = r2;
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(14.0f));
            this.textPaint.setColor(-3289651);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            if (event.getAction() == 0) {
                this.startMovingQuality = PhotoViewer.this.selectedCompression;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == 0 || event.getAction() == 2) {
                int a = 0;
                while (true) {
                    if (a >= PhotoViewer.this.compressionsCount) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize;
                    int i3 = this.gapSize;
                    int i4 = this.circleSize;
                    int cx = i + (((i3 * 2) + i2 + i4) * a) + (i4 / 2);
                    int diff = (i2 / 2) + (i4 / 2) + i3;
                    if (x > cx - diff && x < cx + diff) {
                        if (PhotoViewer.this.selectedCompression != a) {
                            PhotoViewer.this.selectedCompression = a;
                            PhotoViewer.this.didChangedCompressionLevel(false);
                            invalidate();
                        }
                    } else {
                        a++;
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (PhotoViewer.this.selectedCompression != this.startMovingQuality) {
                    PhotoViewer.this.requestVideoPreview(1);
                }
                PhotoViewer.this.moving = false;
            }
            return true;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.circleSize = AndroidUtilities.dp(8.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(18.0f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (PhotoViewer.this.compressionsCount != 1) {
                this.lineSize = (((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * ((PhotoViewer.this.compressionsCount * 2) - 2))) - (this.sideSide * 2)) / (PhotoViewer.this.compressionsCount - 1);
            } else {
                this.lineSize = ((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * 2)) - (this.sideSide * 2);
            }
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f);
            int a = 0;
            while (a < PhotoViewer.this.compressionsCount) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= PhotoViewer.this.selectedCompression) {
                    this.paint.setColor(-11292945);
                } else {
                    this.paint.setColor(1728053247);
                }
                canvas.drawCircle(cx, cy, a == PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(6.0f) : this.circleSize / 2, this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    float f = 0.0f;
                    float startPadding = a == PhotoViewer.this.selectedCompression + 1 ? AndroidUtilities.dpf2(2.0f) : 0.0f;
                    if (a == PhotoViewer.this.selectedCompression) {
                        f = AndroidUtilities.dpf2(2.0f);
                    }
                    float endPadding = f;
                    canvas.drawRect(x + startPadding, cy - AndroidUtilities.dp(1.0f), (this.lineSize + x) - endPadding, AndroidUtilities.dp(2.0f) + cy, this.paint);
                }
                a++;
            }
            canvas.drawText(this.lowQualityDescription, this.sideSide, cy - AndroidUtilities.dp(16.0f), this.textPaint);
            float width = this.textPaint.measureText(this.hightQualityDescription);
            canvas.drawText(this.hightQualityDescription, (getMeasuredWidth() - this.sideSide) - width, cy - AndroidUtilities.dp(16.0f), this.textPaint);
        }
    }

    public void updateMuteButton() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setMute(this.muteVideo);
        }
        if (!this.videoConvertSupported) {
            this.muteItem.setEnabled(false);
            this.muteItem.setClickable(false);
            this.muteItem.animate().alpha(0.5f).setDuration(180L).start();
            this.videoTimelineView.setMode(0);
            return;
        }
        this.muteItem.setEnabled(true);
        this.muteItem.setClickable(true);
        this.muteItem.animate().alpha(1.0f).setDuration(180L).start();
        if (this.muteVideo) {
            this.actionBar.setSubtitle(LocaleController.getString("SoundMuted", R.string.SoundMuted));
            this.muteItem.setImageResource(R.drawable.video_send_mute);
            if (this.compressItem.getTag() != null) {
                this.compressItem.setAlpha(0.5f);
                this.compressItem.setEnabled(false);
            }
            if (this.sendPhotoType == 1) {
                this.videoTimelineView.setMaxProgressDiff(9600.0f / this.videoDuration);
                this.videoTimelineView.setMode(1);
                updateVideoInfo();
            } else {
                this.videoTimelineView.setMaxProgressDiff(1.0f);
                this.videoTimelineView.setMode(0);
            }
            this.muteItem.setContentDescription(LocaleController.getString("NoSound", R.string.NoSound));
            return;
        }
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(R.drawable.video_send_unmute);
        this.muteItem.setContentDescription(LocaleController.getString("Sound", R.string.Sound));
        if (this.compressItem.getTag() != null) {
            this.compressItem.setAlpha(1.0f);
            this.compressItem.setEnabled(true);
        }
        this.videoTimelineView.setMaxProgressDiff(1.0f);
        this.videoTimelineView.setMode(0);
    }

    public void didChangedCompressionLevel(boolean request) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(String.format("compress_video_%d", Integer.valueOf(this.compressionsCount)), this.selectedCompression);
        editor.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (request) {
            requestVideoPreview(1);
        }
    }

    public void updateVideoInfo() {
        int width;
        int height;
        float f;
        int bitrate;
        ActionBar actionBar = this.actionBar;
        if (actionBar == null) {
            return;
        }
        if (this.compressionsCount == 0) {
            actionBar.setSubtitle(null);
            return;
        }
        int i = this.selectedCompression;
        if (i < 2) {
            this.compressItem.setImageResource(R.drawable.video_quality1);
        } else if (i == 2) {
            this.compressItem.setImageResource(R.drawable.video_quality2);
        } else if (i == 3) {
            this.compressItem.setImageResource(R.drawable.video_quality3);
        }
        this.itemsLayout.requestLayout();
        long ceil = (long) Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration);
        this.estimatedDuration = ceil;
        if (this.muteVideo) {
            int i2 = this.rotationValue;
            width = (i2 == 90 || i2 == 270) ? this.resultHeight : this.resultWidth;
            height = (i2 == 90 || i2 == 270) ? this.resultWidth : this.resultHeight;
            if (this.sendPhotoType == 1) {
                if (ceil <= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                    bitrate = 2600000;
                } else if (ceil <= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                    bitrate = 2200000;
                } else {
                    bitrate = 1560000;
                }
            } else {
                bitrate = 921600;
            }
            long j = (bitrate / 8) * (((float) ceil) / 1000.0f);
            this.estimatedSize = j;
            this.estimatedSize = j + ((j / 32768) * 16);
        } else if (this.compressItem.getTag() == null) {
            int i3 = this.rotationValue;
            width = (i3 == 90 || i3 == 270) ? this.originalHeight : this.originalWidth;
            height = (i3 == 90 || i3 == 270) ? this.originalWidth : this.originalHeight;
            this.estimatedSize = ((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration);
        } else {
            int height2 = this.rotationValue;
            width = (height2 == 90 || height2 == 270) ? this.resultHeight : this.resultWidth;
            height = (height2 == 90 || height2 == 270) ? this.resultWidth : this.resultHeight;
            long j2 = ((float) ((this.sendPhotoType == 1 ? 0L : this.audioFramesSize) + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration);
            this.estimatedSize = j2;
            this.estimatedSize = j2 + ((j2 / 32768) * 16);
        }
        this.videoCutStart = this.videoTimelineView.getLeftProgress();
        float rightProgress = this.videoTimelineView.getRightProgress();
        this.videoCutEnd = rightProgress;
        if (this.videoCutStart == 0.0f) {
            this.startTime = -1L;
        } else {
            this.startTime = f * this.videoDuration * 1000;
        }
        if (rightProgress == 1.0f) {
            this.endTime = -1L;
        } else {
            this.endTime = rightProgress * this.videoDuration * 1000;
        }
        String videoDimension = String.format("%dx%d", Integer.valueOf(width), Integer.valueOf(height));
        String videoTimeSize = String.format("%s, ~%s", AndroidUtilities.formatShortDuration((int) (this.estimatedDuration / 1000)), AndroidUtilities.formatFileSize(this.estimatedSize));
        this.currentSubtitle = String.format("%s, %s", videoDimension, videoTimeSize);
        this.actionBar.beginDelayedTransition();
        this.actionBar.setSubtitle(this.muteVideo ? LocaleController.getString("SoundMuted", R.string.SoundMuted) : this.currentSubtitle);
    }

    public void requestVideoPreview(int request) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean wasRequestingPreview = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (request == 1) {
            if (this.resultHeight == this.originalHeight && this.resultWidth == this.originalWidth) {
                this.tryStartRequestPreviewOnFinish = false;
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                photoProgressViewArr[0].setProgress(0.0f, photoProgressViewArr[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
                this.photoProgressViews[0].setBackgroundState(3, false, true);
                if (!wasRequestingPreview) {
                    preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                    this.videoPlayer.seekTo(this.videoTimelineView.getLeftProgress() * this.videoDuration);
                } else {
                    this.loadInitialVideo = true;
                }
            } else {
                releasePlayer(false);
                if (this.videoPreviewMessageObject == null) {
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.id = 0;
                    message.message = "";
                    message.media = new TLRPC.TL_messageMediaEmpty();
                    message.action = new TLRPC.TL_messageActionEmpty();
                    message.dialog_id = this.currentDialogId;
                    MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, message, false, false);
                    this.videoPreviewMessageObject = messageObject;
                    messageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
                    this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                    this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
                    this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
                    this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
                    this.videoPreviewMessageObject.videoEditedInfo.framerate = this.videoFramerate;
                    this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
                }
                VideoEditedInfo videoEditedInfo = this.videoPreviewMessageObject.videoEditedInfo;
                long j = this.startTime;
                videoEditedInfo.startTime = j;
                long start = j;
                VideoEditedInfo videoEditedInfo2 = this.videoPreviewMessageObject.videoEditedInfo;
                long j2 = this.endTime;
                videoEditedInfo2.endTime = j2;
                long end = j2;
                if (start == -1) {
                    start = 0;
                }
                if (end == -1) {
                    end = this.videoDuration * 1000.0f;
                }
                if (end - start > 5000000) {
                    this.videoPreviewMessageObject.videoEditedInfo.endTime = 5000000 + start;
                }
                this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
                this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
                this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
                this.videoPreviewMessageObject.videoEditedInfo.needUpdateProgress = true;
                this.videoPreviewMessageObject.videoEditedInfo.originalDuration = this.videoDuration * 1000.0f;
                if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                    this.tryStartRequestPreviewOnFinish = true;
                }
                this.requestingPreview = true;
                PhotoProgressView[] photoProgressViewArr2 = this.photoProgressViews;
                photoProgressViewArr2[0].setProgress(0.0f, photoProgressViewArr2[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
                this.photoProgressViews[0].setBackgroundState(0, false, true);
            }
        } else {
            this.tryStartRequestPreviewOnFinish = false;
            this.photoProgressViews[0].setBackgroundState(3, false, true);
            if (request == 2) {
                preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                this.videoPlayer.seekTo(this.videoTimelineView.getLeftProgress() * this.videoDuration);
            }
        }
        this.containerView.invalidate();
    }

    public void updateWidthHeightBitrateForCompression() {
        float maxSize;
        int i = this.compressionsCount;
        if (i <= 0) {
            return;
        }
        if (this.selectedCompression >= i) {
            this.selectedCompression = i - 1;
        }
        if (this.sendPhotoType == 1) {
            float scale = Math.max(800.0f / this.originalWidth, 800.0f / this.originalHeight);
            this.resultWidth = Math.round((this.originalWidth * scale) / 2.0f) * 2;
            this.resultHeight = Math.round((this.originalHeight * scale) / 2.0f) * 2;
        } else {
            int i2 = this.selectedCompression;
            switch (i2) {
                case 0:
                    maxSize = 480.0f;
                    break;
                case 1:
                    maxSize = 854.0f;
                    break;
                case 2:
                    maxSize = 1280.0f;
                    break;
                default:
                    maxSize = 1920.0f;
                    break;
            }
            int i3 = this.originalWidth;
            int i4 = this.originalHeight;
            float scale2 = maxSize / (i3 > i4 ? i3 : i4);
            if (i2 == i - 1 && scale2 >= 1.0f) {
                this.resultWidth = i3;
                this.resultHeight = i4;
            } else {
                this.resultWidth = Math.round((i3 * scale2) / 2.0f) * 2;
                this.resultHeight = Math.round((this.originalHeight * scale2) / 2.0f) * 2;
            }
        }
        if (this.bitrate != 0) {
            if (this.sendPhotoType == 1) {
                this.bitrate = 1560000;
            } else {
                int i5 = this.resultWidth;
                int i6 = this.originalWidth;
                if (i5 == i6 && this.resultHeight == this.originalHeight) {
                    this.bitrate = this.originalBitrate;
                } else {
                    this.bitrate = MediaController.makeVideoBitrate(this.originalHeight, i6, this.originalBitrate, this.resultHeight, i5);
                }
            }
            this.videoFramesSize = ((this.bitrate / 8) * this.videoDuration) / 1000.0f;
        }
    }

    private void showQualityView(final boolean show) {
        TextureView textureView;
        if (show && this.textureUploaded && this.videoSizeSet && !this.changingTextureView && (textureView = this.videoTextureView) != null) {
            this.videoFrameBitmap = textureView.getBitmap();
        }
        if (show) {
            this.previousCompression = this.selectedCompression;
        }
        AnimatorSet animatorSet = this.qualityChooseViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.qualityChooseViewAnimation = new AnimatorSet();
        float f = 0.0f;
        if (show) {
            this.qualityChooseView.setTag(1);
            this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(152.0f)), ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(152.0f)), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f), AndroidUtilities.dp(104.0f)));
        } else {
            this.qualityChooseView.setTag(null);
            this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(this.qualityChooseView, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(166.0f)), ObjectAnimator.ofFloat(this.qualityPicker, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(166.0f)), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f), AndroidUtilities.dp(118.0f)));
        }
        this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.77
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                    PhotoViewer.this.qualityChooseViewAnimation = new AnimatorSet();
                    if (show) {
                        PhotoViewer.this.qualityChooseView.setVisibility(0);
                        PhotoViewer.this.qualityPicker.setVisibility(0);
                        PhotoViewer.this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f)));
                    } else {
                        PhotoViewer.this.qualityChooseView.setVisibility(4);
                        PhotoViewer.this.qualityPicker.setVisibility(4);
                        PhotoViewer.this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f)));
                    }
                    PhotoViewer.this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.77.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation2) {
                            if (animation2.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                                PhotoViewer.this.qualityChooseViewAnimation = null;
                            }
                        }
                    });
                    PhotoViewer.this.qualityChooseViewAnimation.setDuration(200L);
                    PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(AndroidUtilities.decelerateInterpolator);
                    PhotoViewer.this.qualityChooseViewAnimation.start();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200L);
        this.qualityChooseViewAnimation.setInterpolator(AndroidUtilities.accelerateInterpolator);
        this.qualityChooseViewAnimation.start();
        float f2 = 0.25f;
        if (this.cameraItem.getVisibility() == 0) {
            this.cameraItem.animate().scaleX(show ? 0.25f : 1.0f).scaleY(show ? 0.25f : 1.0f).alpha(show ? 0.0f : 1.0f).setDuration(200L);
        }
        if (this.muteItem.getVisibility() == 0) {
            ViewPropertyAnimator scaleX = this.muteItem.animate().scaleX(show ? 0.25f : 1.0f);
            if (!show) {
                f2 = 1.0f;
            }
            ViewPropertyAnimator scaleY = scaleX.scaleY(f2);
            if (!show) {
                f = 1.0f;
            }
            scaleY.alpha(f).setDuration(200L);
        }
    }

    private ByteArrayInputStream cleanBuffer(byte[] data) {
        byte[] output = new byte[data.length];
        int inPos = 0;
        int outPos = 0;
        while (inPos < data.length) {
            if (data[inPos] == 0 && data[inPos + 1] == 0 && data[inPos + 2] == 3) {
                output[outPos] = 0;
                output[outPos + 1] = 0;
                inPos += 3;
                outPos += 2;
            } else {
                output[outPos] = data[inPos];
                inPos++;
                outPos++;
            }
        }
        return new ByteArrayInputStream(output, 0, outPos);
    }

    private void processOpenVideo(String videoPath, boolean muted, float start, float end) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoTimelineView.setVideoPath(videoPath, start, end);
        this.videoPreviewMessageObject = null;
        boolean z = true;
        if (!muted && this.sendPhotoType != 1) {
            z = false;
        }
        this.muteVideo = z;
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.videoFramerate = 25;
        File file = new File(videoPath);
        this.originalSize = file.length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        AnonymousClass78 anonymousClass78 = new AnonymousClass78(videoPath);
        this.currentLoadingVideoRunnable = anonymousClass78;
        dispatchQueue.postRunnable(anonymousClass78);
    }

    /* renamed from: org.telegram.ui.PhotoViewer$78 */
    /* loaded from: classes4.dex */
    public class AnonymousClass78 implements Runnable {
        final /* synthetic */ String val$videoPath;

        AnonymousClass78(String str) {
            PhotoViewer.this = this$0;
            this.val$videoPath = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                return;
            }
            final int videoBitrate = MediaController.getVideoBitrate(this.val$videoPath);
            final int[] params = new int[11];
            AnimatedFileDrawable.getVideoInfo(this.val$videoPath, params);
            if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$78$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass78.this.m4296lambda$run$0$orgtelegramuiPhotoViewer$78(this, params, videoBitrate);
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-ui-PhotoViewer$78 */
        public /* synthetic */ void m4296lambda$run$0$orgtelegramuiPhotoViewer$78(Runnable thisFinal, int[] params, int videoBitrate) {
            if (PhotoViewer.this.parentActivity != null && thisFinal == PhotoViewer.this.currentLoadingVideoRunnable) {
                PhotoViewer.this.currentLoadingVideoRunnable = null;
                boolean hasAudio = params[10] != 0;
                PhotoViewer.this.videoConvertSupported = params[0] != 0 && (!hasAudio || params[9] != 0);
                PhotoViewer.this.audioFramesSize = params[5];
                PhotoViewer.this.videoDuration = params[4];
                if (videoBitrate == -1) {
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.originalBitrate = photoViewer.bitrate = params[3];
                } else {
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    photoViewer2.originalBitrate = photoViewer2.bitrate = videoBitrate;
                }
                PhotoViewer.this.videoFramerate = params[7];
                PhotoViewer photoViewer3 = PhotoViewer.this;
                photoViewer3.videoFramesSize = ((photoViewer3.bitrate / 8) * PhotoViewer.this.videoDuration) / 1000.0f;
                if (PhotoViewer.this.videoConvertSupported) {
                    PhotoViewer.this.rotationValue = params[8];
                    PhotoViewer photoViewer4 = PhotoViewer.this;
                    photoViewer4.resultWidth = photoViewer4.originalWidth = params[1];
                    PhotoViewer photoViewer5 = PhotoViewer.this;
                    photoViewer5.resultHeight = photoViewer5.originalHeight = params[2];
                    PhotoViewer photoViewer6 = PhotoViewer.this;
                    photoViewer6.updateCompressionsCount(photoViewer6.originalWidth, PhotoViewer.this.originalHeight);
                    PhotoViewer photoViewer7 = PhotoViewer.this;
                    photoViewer7.selectedCompression = photoViewer7.selectCompression();
                    PhotoViewer.this.updateWidthHeightBitrateForCompression();
                    if (PhotoViewer.this.selectedCompression > PhotoViewer.this.compressionsCount - 1) {
                        PhotoViewer photoViewer8 = PhotoViewer.this;
                        photoViewer8.selectedCompression = photoViewer8.compressionsCount - 1;
                    }
                    PhotoViewer photoViewer9 = PhotoViewer.this;
                    photoViewer9.setCompressItemEnabled(photoViewer9.compressionsCount > 1, true);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("compressionsCount = " + PhotoViewer.this.compressionsCount + " w = " + PhotoViewer.this.originalWidth + " h = " + PhotoViewer.this.originalHeight + " r = " + PhotoViewer.this.rotationValue);
                    }
                    if (Build.VERSION.SDK_INT < 18 && PhotoViewer.this.compressItem.getTag() != null) {
                        PhotoViewer.this.videoConvertSupported = false;
                        PhotoViewer.this.setCompressItemEnabled(false, true);
                    }
                    PhotoViewer.this.qualityChooseView.invalidate();
                } else {
                    PhotoViewer.this.setCompressItemEnabled(false, true);
                    PhotoViewer.this.compressionsCount = 0;
                }
                PhotoViewer.this.updateVideoInfo();
                PhotoViewer.this.updateMuteButton();
            }
        }
    }

    public int selectCompression() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        int compressionsCount = this.compressionsCount;
        while (compressionsCount < 5) {
            int selectedCompression = preferences.getInt(String.format(Locale.US, "compress_video_%d", Integer.valueOf(compressionsCount)), -1);
            if (selectedCompression >= 0) {
                return Math.min(selectedCompression, 2);
            }
            compressionsCount++;
        }
        return Math.min(2, Math.round(DownloadController.getInstance(this.currentAccount).getMaxVideoBitrate() / (100.0f / compressionsCount)) - 1);
    }

    public void updateCompressionsCount(int h, int w) {
        int maxSize = Math.max(h, w);
        if (maxSize > 1280) {
            this.compressionsCount = 4;
        } else if (maxSize > 854) {
            this.compressionsCount = 3;
        } else if (maxSize > 640) {
            this.compressionsCount = 2;
        } else {
            this.compressionsCount = 1;
        }
    }

    public void setCompressItemEnabled(boolean enabled, boolean animated) {
        ImageView imageView = this.compressItem;
        if (imageView == null) {
            return;
        }
        if (enabled && imageView.getTag() != null) {
            return;
        }
        if (!enabled && this.compressItem.getTag() == null) {
            return;
        }
        this.compressItem.setTag(enabled ? 1 : null);
        AnimatorSet animatorSet = this.compressItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.compressItemAnimation = null;
        }
        float f = 1.0f;
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.compressItemAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[4];
            ImageView imageView2 = this.compressItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = enabled ? 1.0f : 0.5f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
            ImageView imageView3 = this.paintItem;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = this.videoConvertSupported ? 1.0f : 0.5f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
            ImageView imageView4 = this.tuneItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = this.videoConvertSupported ? 1.0f : 0.5f;
            animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
            ImageView imageView5 = this.cropItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (!this.videoConvertSupported) {
                f = 0.5f;
            }
            fArr4[0] = f;
            animatorArr[3] = ObjectAnimator.ofFloat(imageView5, property4, fArr4);
            animatorSet2.playTogether(animatorArr);
            this.compressItemAnimation.setDuration(180L);
            this.compressItemAnimation.setInterpolator(decelerateInterpolator);
            this.compressItemAnimation.start();
            return;
        }
        ImageView imageView6 = this.compressItem;
        if (!enabled) {
            f = 0.5f;
        }
        imageView6.setAlpha(f);
    }

    public void updateAccessibilityOverlayVisibility() {
        if (this.playButtonAccessibilityOverlay != null) {
            int state = this.photoProgressViews[0].backgroundState;
            if (this.photoProgressViews[0].isVisible() && (state == 3 || state == 4 || state == 2 || state == 1)) {
                if (state == 3) {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                } else if (state == 2) {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionDownload", R.string.AccActionDownload));
                } else if (state == 1) {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionCancelDownload", R.string.AccActionCancelDownload));
                } else {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
                }
                this.playButtonAccessibilityOverlay.setVisibility(0);
                return;
            }
            this.playButtonAccessibilityOverlay.setVisibility(4);
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PhotoViewer.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (PhotoViewer.this.placeProvider != null && PhotoViewer.this.placeProvider.getSelectedPhotosOrder() != null) {
                return PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoPickerPhotoCell cell = new PhotoPickerPhotoCell(this.mContext);
            cell.checkFrame.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$ListAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoViewer.ListAdapter.this.m4303x660e23ec(view);
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-PhotoViewer$ListAdapter */
        public /* synthetic */ void m4303x660e23ec(View v) {
            Object photoEntry = ((View) v.getParent()).getTag();
            int idx = PhotoViewer.this.imagesArrLocals.indexOf(photoEntry);
            if (idx >= 0) {
                int num = PhotoViewer.this.placeProvider.setPhotoChecked(idx, PhotoViewer.this.getCurrentVideoEditedInfo());
                boolean checked = PhotoViewer.this.placeProvider.isPhotoChecked(idx);
                if (idx == PhotoViewer.this.currentIndex) {
                    PhotoViewer.this.checkImageView.setChecked(-1, checked, true);
                }
                if (num >= 0) {
                    PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(num);
                    if (num == 0) {
                        PhotoViewer.this.selectedPhotosAdapter.notifyItemChanged(0);
                    }
                }
                PhotoViewer.this.updateSelectedCount();
                return;
            }
            int num2 = PhotoViewer.this.placeProvider.setPhotoUnchecked(photoEntry);
            if (num2 >= 0) {
                PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(num2);
                if (num2 == 0) {
                    PhotoViewer.this.selectedPhotosAdapter.notifyItemChanged(0);
                }
                PhotoViewer.this.updateSelectedCount();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) holder.itemView;
            cell.setItemWidth(AndroidUtilities.dp(85.0f), position != 0 ? AndroidUtilities.dp(6.0f) : 0);
            BackupImageView imageView = cell.imageView;
            imageView.setOrientation(0, true);
            ArrayList<Object> order = PhotoViewer.this.placeProvider.getSelectedPhotosOrder();
            Object object = PhotoViewer.this.placeProvider.getSelectedPhotos().get(order.get(position));
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                cell.setTag(photoEntry);
                cell.videoInfoContainer.setVisibility(4);
                if (photoEntry.thumbPath != null) {
                    imageView.setImage(photoEntry.thumbPath, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                } else if (photoEntry.path != null) {
                    imageView.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        cell.videoInfoContainer.setVisibility(0);
                        cell.videoTextView.setText(AndroidUtilities.formatShortDuration(photoEntry.duration));
                        imageView.setImage("vthumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                    } else {
                        imageView.setImage("thumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                    }
                } else {
                    imageView.setImageResource(R.drawable.nophotos);
                }
                cell.setChecked(-1, true, false);
                cell.checkBox.setVisibility(0);
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) object;
                cell.setTag(photoEntry2);
                cell.setImage(photoEntry2);
                cell.videoInfoContainer.setVisibility(4);
                cell.setChecked(-1, true, false);
                cell.checkBox.setVisibility(0);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public class FirstFrameView extends ImageView {
        public static final float fadeDuration = 250.0f;
        private VideoPlayer currentVideoPlayer;
        private ValueAnimator fadeAnimator;
        private int gettingFrameIndex = 0;
        private boolean gettingFrame = false;
        private boolean hasFrame = false;
        private boolean gotError = false;
        private final TimeInterpolator fadeInterpolator = CubicBezierInterpolator.EASE_IN;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FirstFrameView(Context context) {
            super(context);
            PhotoViewer.this = r1;
            setAlpha(0.0f);
        }

        public void clear() {
            this.hasFrame = false;
            this.gotError = false;
            if (this.gettingFrame) {
                this.gettingFrameIndex++;
                this.gettingFrame = false;
            }
            setImageResource(17170445);
        }

        public void checkFromPlayer(VideoPlayer videoPlayer) {
            if (this.currentVideoPlayer != videoPlayer) {
                this.gotError = false;
                clear();
            }
            if (videoPlayer != null) {
                long timeToEnd = videoPlayer.getDuration() - videoPlayer.getCurrentPosition();
                if (!this.hasFrame && !this.gotError && !this.gettingFrame && ((float) timeToEnd) < 5250.0f) {
                    final Uri uri = videoPlayer.getCurrentUri();
                    final int index = this.gettingFrameIndex + 1;
                    this.gettingFrameIndex = index;
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.FirstFrameView.this.m4301x99d24718(uri, index);
                        }
                    });
                    this.gettingFrame = true;
                }
            }
            this.currentVideoPlayer = videoPlayer;
        }

        /* renamed from: lambda$checkFromPlayer$2$org-telegram-ui-PhotoViewer$FirstFrameView */
        public /* synthetic */ void m4301x99d24718(Uri uri, final int index) {
            Throwable e;
            try {
                AnimatedFileDrawable drawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0L, null, null, null, 0L, UserConfig.selectedAccount, false, AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                final Bitmap bitmap = drawable.getFrameAtTime(0L);
                drawable.recycle();
                try {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.FirstFrameView.this.m4299x13f9745a(index, bitmap);
                        }
                    });
                } catch (Throwable th) {
                    e = th;
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.FirstFrameView.this.m4300xd6e5ddb9();
                        }
                    });
                }
            } catch (Throwable th2) {
                e = th2;
            }
        }

        /* renamed from: lambda$checkFromPlayer$0$org-telegram-ui-PhotoViewer$FirstFrameView */
        public /* synthetic */ void m4299x13f9745a(int index, Bitmap bitmap) {
            if (index == this.gettingFrameIndex) {
                setImageBitmap(bitmap);
                this.hasFrame = true;
                this.gettingFrame = false;
            }
        }

        /* renamed from: lambda$checkFromPlayer$1$org-telegram-ui-PhotoViewer$FirstFrameView */
        public /* synthetic */ void m4300xd6e5ddb9() {
            this.gotError = true;
        }

        public boolean containsFrame() {
            return this.hasFrame;
        }

        public void updateAlpha() {
            if (PhotoViewer.this.videoPlayer == null || PhotoViewer.this.videoPlayer.getDuration() == C.TIME_UNSET) {
                ValueAnimator valueAnimator = this.fadeAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.fadeAnimator = null;
                }
                setAlpha(0.0f);
                return;
            }
            long toDuration = Math.max(0L, PhotoViewer.this.videoPlayer.getDuration() - PhotoViewer.this.videoPlayer.getCurrentPosition());
            float alpha = 1.0f - Math.max(Math.min(((float) toDuration) / 250.0f, 1.0f), 0.0f);
            if (alpha > 0.0f) {
                if (PhotoViewer.this.videoPlayer.isPlaying()) {
                    if (this.fadeAnimator == null) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(alpha, 1.0f);
                        this.fadeAnimator = ofFloat;
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                PhotoViewer.FirstFrameView.this.m4302lambda$updateAlpha$3$orgtelegramuiPhotoViewer$FirstFrameView(valueAnimator2);
                            }
                        });
                        this.fadeAnimator.setDuration(toDuration);
                        this.fadeAnimator.setInterpolator(this.fadeInterpolator);
                        this.fadeAnimator.start();
                        setAlpha(alpha);
                        return;
                    }
                    return;
                }
                ValueAnimator valueAnimator2 = this.fadeAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                    this.fadeAnimator = null;
                }
                setAlpha(alpha);
                return;
            }
            ValueAnimator valueAnimator3 = this.fadeAnimator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
                this.fadeAnimator = null;
            }
            setAlpha(0.0f);
        }

        /* renamed from: lambda$updateAlpha$3$org-telegram-ui-PhotoViewer$FirstFrameView */
        public /* synthetic */ void m4302lambda$updateAlpha$3$orgtelegramuiPhotoViewer$FirstFrameView(ValueAnimator a) {
            setAlpha(((Float) a.getAnimatedValue()).floatValue());
        }
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
