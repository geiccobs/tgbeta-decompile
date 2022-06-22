package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonWebView;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedNumberLayout;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.InfiniteProgress;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.SlotsDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes3.dex */
public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, DownloadController.FileDownloadProgressListener, TextSelectionHelper.SelectableView, NotificationCenter.NotificationCenterDelegate {
    private static float[] radii = new float[8];
    private final boolean ALPHA_PROPERTY_WORKAROUND;
    public Property<ChatMessageCell, Float> ANIMATION_OFFSET_X;
    private int TAG;
    CharSequence accessibilityText;
    private SparseArray<Rect> accessibilityVirtualViewBounds;
    private int addedCaptionHeight;
    private boolean addedForTest;
    private int additionalTimeOffsetY;
    private StaticLayout adminLayout;
    private boolean allowAssistant;
    private float alphaInternal;
    private int animateFromStatusDrawableParams;
    private boolean animatePollAnswer;
    private boolean animatePollAnswerAlpha;
    private boolean animatePollAvatars;
    private int animateToStatusDrawableParams;
    private int animatingDrawVideoImageButton;
    private float animatingDrawVideoImageButtonProgress;
    private float animatingLoadingProgressProgress;
    private int animatingNoSound;
    private boolean animatingNoSoundPlaying;
    private float animatingNoSoundProgress;
    private float animationOffsetX;
    private boolean animationRunning;
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorX;
    private boolean autoPlayingMedia;
    private int availableTimeWidth;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean avatarPressed;
    private Theme.MessageDrawable.PathDrawParams backgroundCacheParams;
    private MessageBackgroundDrawable backgroundDrawable;
    private int backgroundDrawableLeft;
    private int backgroundDrawableRight;
    private int backgroundDrawableTop;
    private int backgroundHeight;
    private int backgroundWidth;
    private int blurredViewBottomOffset;
    private int blurredViewTopOffset;
    private ArrayList<BotButton> botButtons;
    private HashMap<String, BotButton> botButtonsByData;
    private HashMap<String, BotButton> botButtonsByPosition;
    private String botButtonsLayout;
    private boolean bottomNearToSet;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private final boolean canDrawBackgroundInParent;
    private boolean canStreamVideo;
    private int captionHeight;
    private StaticLayout captionLayout;
    private int captionOffsetX;
    private AtomicReference<Layout> captionPatchedSpoilersLayout;
    private List<SpoilerEffect> captionSpoilers;
    private Stack<SpoilerEffect> captionSpoilersPool;
    private int captionWidth;
    private float captionX;
    private float captionY;
    private CheckBoxBase checkBox;
    private boolean checkBoxAnimationInProgress;
    private float checkBoxAnimationProgress;
    private int checkBoxTranslation;
    private boolean checkBoxVisible;
    private boolean checkOnlyButtonPressed;
    private String closeTimeText;
    private int closeTimeWidth;
    private int commentArrowX;
    private AvatarDrawable[] commentAvatarDrawables;
    private ImageReceiver[] commentAvatarImages;
    private boolean[] commentAvatarImagesVisible;
    private boolean commentButtonPressed;
    private Rect commentButtonRect;
    private boolean commentDrawUnread;
    private StaticLayout commentLayout;
    private AnimatedNumberLayout commentNumberLayout;
    private int commentNumberWidth;
    private InfiniteProgress commentProgress;
    private float commentProgressAlpha;
    private long commentProgressLastUpadteTime;
    private int commentUnreadX;
    private int commentWidth;
    private int commentX;
    private AvatarDrawable contactAvatarDrawable;
    private float controlsAlpha;
    private int currentAccount;
    private Theme.MessageDrawable currentBackgroundDrawable;
    private Theme.MessageDrawable currentBackgroundSelectedDrawable;
    private CharSequence currentCaption;
    private TLRPC$Chat currentChat;
    private int currentFocusedVirtualView;
    private TLRPC$Chat currentForwardChannel;
    private String currentForwardName;
    private String currentForwardNameString;
    private TLRPC$User currentForwardUser;
    private int currentMapProvider;
    private MessageObject currentMessageObject;
    private MessageObject.GroupedMessages currentMessagesGroup;
    private String currentNameString;
    private TLRPC$FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private TLRPC$PhotoSize currentPhotoObject;
    private TLRPC$PhotoSize currentPhotoObjectThumb;
    private BitmapDrawable currentPhotoObjectThumbStripped;
    private MessageObject.GroupedMessagePosition currentPosition;
    private String currentRepliesString;
    private TLRPC$PhotoSize currentReplyPhoto;
    private float currentSelectedBackgroundAlpha;
    private String currentTimeString;
    private String currentUrl;
    private TLRPC$User currentUser;
    private TLRPC$User currentViaBotUser;
    private String currentViewsString;
    private WebFile currentWebFile;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect;
    private StaticLayout descriptionLayout;
    private int descriptionX;
    private int descriptionY;
    private Runnable diceFinishCallback;
    private boolean disallowLongPress;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private int docTitleWidth;
    private TLRPC$Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground;
    private boolean drawCommentButton;
    private boolean drawCommentNumber;
    private boolean drawForwardedName;
    public boolean drawFromPinchToZoom;
    private boolean drawImageButton;
    private boolean drawInstantView;
    private int drawInstantViewType;
    private boolean drawMediaCheckBox;
    private boolean drawName;
    private boolean drawNameLayout;
    private boolean drawPhotoImage;
    public boolean drawPinnedBottom;
    private boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    private boolean drawSelectionBackground;
    private int drawSideButton;
    private boolean drawTime;
    private float drawTimeX;
    private float drawTimeY;
    private boolean drawVideoImageButton;
    private boolean drawVideoSize;
    private StaticLayout durationLayout;
    private int durationWidth;
    private boolean edited;
    boolean enterTransitionInProgress;
    private boolean firstCircleLength;
    private int firstVisibleBlockNum;
    private boolean flipImage;
    private boolean forceNotDrawTime;
    private boolean forwardBotPressed;
    private int forwardNameCenterX;
    private float[] forwardNameOffsetX;
    private boolean forwardNamePressed;
    private float forwardNameX;
    private int forwardNameY;
    private StaticLayout[] forwardedNameLayout;
    private int forwardedNameWidth;
    private boolean fullyDraw;
    private boolean gamePreviewPressed;
    private LinearGradient gradientShader;
    private boolean groupPhotoInvisible;
    private MessageObject.GroupedMessages groupedMessagesToSet;
    private boolean hadLongPress;
    public boolean hasDiscussion;
    private boolean hasEmbed;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private boolean hasPsaHint;
    private int highlightProgress;
    private float hintButtonProgress;
    private boolean hintButtonVisible;
    private int imageBackgroundColor;
    private int imageBackgroundGradientColor1;
    private int imageBackgroundGradientColor2;
    private int imageBackgroundGradientColor3;
    private int imageBackgroundGradientRotation;
    private float imageBackgroundIntensity;
    private int imageBackgroundSideColor;
    private int imageBackgroundSideWidth;
    private boolean imageDrawn;
    private boolean imagePressed;
    private boolean inLayout;
    private StaticLayout infoLayout;
    private int infoWidth;
    private int infoX;
    private boolean instantButtonPressed;
    private RectF instantButtonRect;
    private boolean instantPressed;
    private int instantTextLeftX;
    private boolean instantTextNewLine;
    private int instantTextX;
    private StaticLayout instantViewLayout;
    private int instantWidth;
    private Runnable invalidateRunnable;
    private boolean invalidateSpoilersParent;
    private boolean invalidatesParent;
    private boolean isAvatarVisible;
    public boolean isBlurred;
    public boolean isBot;
    private boolean isCaptionSpoilerPressed;
    public boolean isChat;
    private boolean isCheckPressed;
    private boolean isHighlighted;
    private boolean isHighlightedAnimated;
    public boolean isMegagroup;
    public boolean isPinned;
    public boolean isPinnedChat;
    private boolean isPlayingRound;
    private boolean isPressed;
    public boolean isRepliesChat;
    private boolean isRoundVideo;
    private boolean isSmallImage;
    private boolean isSpoilerRevealing;
    public boolean isThreadChat;
    private boolean isThreadPost;
    private boolean isUpdating;
    private int keyboardHeight;
    private long lastAnimationTime;
    private long lastCheckBoxAnimationTime;
    private long lastControlsAlphaChangeTime;
    private int lastDeleteDate;
    private float lastDrawingAudioProgress;
    private int lastHeight;
    private long lastHighlightProgressTime;
    private long lastLoadingSizeTotal;
    private long lastNamesAnimationTime;
    private TLRPC$Poll lastPoll;
    private long lastPollCloseTime;
    private ArrayList<TLRPC$TL_pollAnswerVoters> lastPollResults;
    private int lastPollResultsVoters;
    private String lastPostAuthor;
    private TLRPC$TL_messageReactions lastReactions;
    private int lastRepliesCount;
    private TLRPC$Message lastReplyMessage;
    private long lastSeekUpdateTime;
    private int lastSendState;
    int lastSize;
    private int lastTime;
    private float lastTouchX;
    private float lastTouchY;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    private WebFile lastWebFile;
    private int lastWidth;
    private int layoutHeight;
    private int layoutWidth;
    private int linkBlockNum;
    private int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private int linkSelectionBlockNum;
    public long linkedChatId;
    private LinkSpanDrawable.LinkCollector links;
    private StaticLayout loadingProgressLayout;
    private boolean locationExpired;
    private ImageReceiver locationImageReceiver;
    private boolean mediaBackground;
    private CheckBoxBase mediaCheckBox;
    private int mediaOffsetY;
    private boolean mediaWasInvisible;
    private MessageObject messageObjectToSet;
    private int miniButtonPressed;
    private int miniButtonState;
    private MotionBackgroundDrawable motionBackgroundDrawable;
    private StaticLayout nameLayout;
    private float nameOffsetX;
    private int nameWidth;
    private float nameX;
    private float nameY;
    private int namesOffset;
    private boolean needNewVisiblePart;
    public boolean needReplyImage;
    private int noSoundCenterX;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private int overideShouldDrawTimeOnMedia;
    private int parentHeight;
    public float parentViewTopOffset;
    private int parentWidth;
    private StaticLayout performerLayout;
    private int performerX;
    private ImageReceiver photoImage;
    private boolean photoNotSet;
    private TLObject photoParentObject;
    private StaticLayout photosCountLayout;
    private int photosCountWidth;
    public boolean pinnedBottom;
    public boolean pinnedTop;
    private float pollAnimationProgress;
    private float pollAnimationProgressTime;
    private AvatarDrawable[] pollAvatarDrawables;
    private ImageReceiver[] pollAvatarImages;
    private boolean[] pollAvatarImagesVisible;
    private ArrayList<PollButton> pollButtons;
    private CheckBoxBase[] pollCheckBox;
    private boolean pollClosed;
    private boolean pollHintPressed;
    private int pollHintX;
    private int pollHintY;
    private boolean pollUnvoteInProgress;
    private boolean pollVoteInProgress;
    private int pollVoteInProgressNum;
    private boolean pollVoted;
    private int pressedBotButton;
    private LinkSpanDrawable pressedLink;
    private int pressedLinkType;
    private int[] pressedState;
    private int pressedVoteButton;
    private float psaButtonProgress;
    private boolean psaButtonVisible;
    private int psaHelpX;
    private int psaHelpY;
    private boolean psaHintPressed;
    private RadialProgress2 radialProgress;
    public final ReactionsLayoutInBubble reactionsLayoutInBubble;
    private RectF rect;
    private Path rectPath;
    private StaticLayout repliesLayout;
    private int repliesTextWidth;
    public ImageReceiver replyImageReceiver;
    public StaticLayout replyNameLayout;
    private int replyNameOffset;
    private int replyNameWidth;
    private boolean replyPanelIsForward;
    private boolean replyPressed;
    public List<SpoilerEffect> replySpoilers;
    private Stack<SpoilerEffect> replySpoilersPool;
    public int replyStartX;
    public int replyStartY;
    public StaticLayout replyTextLayout;
    private int replyTextOffset;
    private int replyTextWidth;
    private final Theme.ResourcesProvider resourcesProvider;
    private float roundPlayingDrawableProgress;
    private float roundProgressAlpha;
    float roundSeekbarOutAlpha;
    float roundSeekbarOutProgress;
    int roundSeekbarTouched;
    private float roundToPauseProgress;
    private float roundToPauseProgress2;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    private Path sPath;
    private boolean scheduledInvalidate;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarX;
    private int seekBarY;
    float seekbarRoundX;
    float seekbarRoundY;
    private float selectedBackgroundProgress;
    private Paint selectionOverlayPaint;
    private Drawable[] selectorDrawable;
    private int[] selectorDrawableMaskType;
    private AnimatorSet shakeAnimation;
    private boolean sideButtonPressed;
    private float sideStartX;
    private float sideStartY;
    private StaticLayout siteNameLayout;
    private boolean siteNameRtl;
    private int siteNameWidth;
    private float slidingOffsetX;
    private StaticLayout songLayout;
    private int songX;
    private SpoilerEffect spoilerPressed;
    private AtomicReference<Layout> spoilersPatchedReplyTextLayout;
    private boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private float statusDrawableProgress;
    private int substractBackgroundHeight;
    private int textX;
    private int textY;
    private float timeAlpha;
    private int timeAudioX;
    private StaticLayout timeLayout;
    private boolean timePressed;
    private int timeTextWidth;
    private boolean timeWasInvisible;
    private int timeWidth;
    private int timeWidthAudio;
    private int timeX;
    private TimerParticles timerParticles;
    private float timerTransitionProgress;
    private StaticLayout titleLayout;
    private int titleX;
    private float toSeekBarProgress;
    private boolean topNearToSet;
    private long totalChangeTime;
    private int totalCommentWidth;
    private int totalHeight;
    private int totalVisibleBlocksCount;
    private TranscribeButton transcribeButton;
    private float transcribeX;
    private float transcribeY;
    private final TransitionParams transitionParams;
    float transitionYOffsetForDrawables;
    private int unmovedTextX;
    private Runnable unregisterFlagSecure;
    private ArrayList<LinkPath> urlPathCache;
    private ArrayList<LinkPath> urlPathSelection;
    private boolean useSeekBarWaveform;
    private boolean useTranscribeButton;
    private int viaNameWidth;
    private TypefaceSpan viaSpan1;
    private TypefaceSpan viaSpan2;
    private int viaWidth;
    private boolean vibrateOnPollVote;
    private int videoButtonPressed;
    private int videoButtonX;
    private int videoButtonY;
    VideoForwardDrawable videoForwardDrawable;
    private StaticLayout videoInfoLayout;
    VideoPlayerRewinder videoPlayerRewinder;
    private RadialProgress2 videoRadialProgress;
    private float viewTop;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    private float voteCurrentCircleLength;
    private float voteCurrentProgressTime;
    private long voteLastUpdateTime;
    private float voteRadOffset;
    private boolean voteRisingCircleLength;
    private boolean wasLayout;
    private boolean wasPinned;
    private boolean wasSending;
    private int widthBeforeNewTimeLine;
    private int widthForButtons;
    private boolean willRemoved;

    /* loaded from: classes3.dex */
    public interface ChatMessageCellDelegate {

        /* renamed from: org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$canDrawOutboundsContent(ChatMessageCellDelegate chatMessageCellDelegate) {
                return true;
            }

            public static boolean $default$canPerformActions(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$didLongPress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didLongPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
            }

            public static boolean $default$didLongPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                return false;
            }

            public static boolean $default$didLongPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                return false;
            }

            public static void $default$didPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
            }

            public static void $default$didPressCancelSendButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
            }

            public static void $default$didPressCommentButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressHiddenForward(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressHint(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressImage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressInstantButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressOther(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressReaction(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount, boolean z) {
            }

            public static void $default$didPressReplyMessage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressSideButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressTime(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressUrl(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
            }

            public static void $default$didPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
            }

            public static void $default$didPressViaBot(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, String str) {
            }

            public static void $default$didPressViaBotNotInline(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, long j) {
            }

            public static void $default$didPressVoteButtons(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
            }

            public static void $default$didStartVideoStream(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static String $default$getAdminRank(ChatMessageCellDelegate chatMessageCellDelegate, long j) {
                return null;
            }

            public static PinchToZoomHelper $default$getPinchToZoomHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static TextSelectionHelper.ChatListTextSelectionHelper $default$getTextSelectionHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static boolean $default$hasSelectedMessages(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$invalidateBlur(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static boolean $default$isLandscape(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static boolean $default$keyboardIsOpened(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$needOpenWebView(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
            }

            public static boolean $default$needPlayMessage(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return false;
            }

            public static void $default$needReloadPolls(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$needShowPremiumFeatures(ChatMessageCellDelegate chatMessageCellDelegate, String str) {
            }

            public static boolean $default$onAccessibilityAction(ChatMessageCellDelegate chatMessageCellDelegate, int i, Bundle bundle) {
                return false;
            }

            public static void $default$onDiceFinished(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$setShouldNotRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static boolean $default$shouldDrawThreadProgress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
                return false;
            }

            public static boolean $default$shouldRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return true;
            }

            public static void $default$videoTimerReached(ChatMessageCellDelegate chatMessageCellDelegate) {
            }
        }

        boolean canDrawOutboundsContent();

        boolean canPerformActions();

        void didLongPress(ChatMessageCell chatMessageCell, float f, float f2);

        void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton);

        boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2);

        boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2);

        void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2);

        void didPressCommentButton(ChatMessageCell chatMessageCell);

        void didPressHiddenForward(ChatMessageCell chatMessageCell);

        void didPressHint(ChatMessageCell chatMessageCell, int i);

        void didPressImage(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressOther(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount, boolean z);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressSideButton(ChatMessageCell chatMessageCell);

        void didPressTime(ChatMessageCell chatMessageCell);

        void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j);

        void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TLRPC$TL_pollAnswer> arrayList, int i, int i2, int i3);

        void didStartVideoStream(MessageObject messageObject);

        String getAdminRank(long j);

        PinchToZoomHelper getPinchToZoomHelper();

        TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper();

        boolean hasSelectedMessages();

        void invalidateBlur();

        boolean isLandscape();

        boolean keyboardIsOpened();

        void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(MessageObject messageObject);

        void needReloadPolls();

        void needShowPremiumFeatures(String str);

        boolean onAccessibilityAction(int i, Bundle bundle);

        void onDiceFinished();

        void setShouldNotRepeatSticker(MessageObject messageObject);

        boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell);

        boolean shouldRepeatSticker(MessageObject messageObject);

        void videoTimerReached();
    }

    private boolean intersect(float f, float f2, float f3, float f4) {
        return f <= f3 ? f2 >= f3 : f <= f4;
    }

    public RadialProgress2 getRadialProgress() {
        return this.radialProgress;
    }

    public void setEnterTransitionInProgress(boolean z) {
        this.enterTransitionInProgress = z;
        invalidate();
    }

    public ReactionsLayoutInBubble.ReactionButton getReactionButton(String str) {
        return this.reactionsLayoutInBubble.getReactionButton(str);
    }

    public MessageObject getPrimaryMessageObject() {
        MessageObject messageObject = this.currentMessageObject;
        MessageObject findPrimaryMessageObject = (messageObject == null || this.currentMessagesGroup == null || !messageObject.hasValidGroupId()) ? null : this.currentMessagesGroup.findPrimaryMessageObject();
        return findPrimaryMessageObject != null ? findPrimaryMessageObject : this.currentMessageObject;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
        } else if (i != NotificationCenter.stopSpoilers) {
        } else {
            setSpoilersSuppressed(true);
        }
    }

    public void setSpoilersSuppressed(boolean z) {
        for (SpoilerEffect spoilerEffect : this.captionSpoilers) {
            spoilerEffect.setSuppressUpdates(z);
        }
        for (SpoilerEffect spoilerEffect2 : this.replySpoilers) {
            spoilerEffect2.setSuppressUpdates(z);
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
        while (it.hasNext()) {
            for (SpoilerEffect spoilerEffect3 : it.next().spoilers) {
                spoilerEffect3.setSuppressUpdates(z);
            }
        }
    }

    public boolean hasSpoilers() {
        if ((!hasCaptionLayout() || this.captionSpoilers.isEmpty()) && (this.replyTextLayout == null || this.replySpoilers.isEmpty())) {
            if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
                return false;
            }
            Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
            while (it.hasNext()) {
                if (!it.next().spoilers.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private void updateSpoilersVisiblePart(int i, int i2) {
        if (hasCaptionLayout()) {
            float f = -this.captionY;
            for (SpoilerEffect spoilerEffect : this.captionSpoilers) {
                spoilerEffect.setVisibleBounds(0.0f, i + f, getWidth(), i2 + f);
            }
        }
        StaticLayout staticLayout = this.replyTextLayout;
        if (staticLayout != null) {
            float height = (-this.replyStartY) - staticLayout.getHeight();
            for (SpoilerEffect spoilerEffect2 : this.replySpoilers) {
                spoilerEffect2.setVisibleBounds(0.0f, i + height, getWidth(), i2 + height);
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
        while (it.hasNext()) {
            MessageObject.TextLayoutBlock next = it.next();
            for (SpoilerEffect spoilerEffect3 : next.spoilers) {
                spoilerEffect3.setVisibleBounds(0.0f, (i - next.textYOffset) - this.textY, getWidth(), (i2 - next.textYOffset) - this.textY);
            }
        }
    }

    public void setScrimReaction(String str) {
        this.reactionsLayoutInBubble.setScrimReaction(str);
    }

    public void drawScrimReaction(Canvas canvas, String str) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (!reactionsLayoutInBubble.isSmall) {
            reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, str);
        }
    }

    public boolean checkUnreadReactions(float f, int i) {
        if (!this.reactionsLayoutInBubble.hasUnreadReactions) {
            return false;
        }
        float y = getY();
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        float f2 = y + reactionsLayoutInBubble.y;
        return f2 > f && (f2 + ((float) reactionsLayoutInBubble.height)) - ((float) AndroidUtilities.dp(16.0f)) < ((float) i);
    }

    public void markReactionsAsRead() {
        this.reactionsLayoutInBubble.hasUnreadReactions = false;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markReactionsAsRead();
    }

    /* loaded from: classes3.dex */
    public static class BotButton {
        private int angle;
        private TLRPC$KeyboardButton button;
        private int height;
        private boolean isInviteButton;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        private int x;
        private int y;

        private BotButton() {
        }

        static /* synthetic */ float access$2716(BotButton botButton, float f) {
            float f2 = botButton.progressAlpha + f;
            botButton.progressAlpha = f2;
            return f2;
        }

        static /* synthetic */ float access$2724(BotButton botButton, float f) {
            float f2 = botButton.progressAlpha - f;
            botButton.progressAlpha = f2;
            return f2;
        }

        static /* synthetic */ int access$2816(BotButton botButton, float f) {
            int i = (int) (botButton.angle + f);
            botButton.angle = i;
            return i;
        }

        static /* synthetic */ int access$2820(BotButton botButton, int i) {
            int i2 = botButton.angle - i;
            botButton.angle = i2;
            return i2;
        }
    }

    /* loaded from: classes3.dex */
    public static class PollButton {
        private TLRPC$TL_pollAnswer answer;
        private boolean chosen;
        private boolean correct;
        private int count;
        private float decimal;
        public int height;
        private int percent;
        private float percentProgress;
        private boolean prevChosen;
        private int prevPercent;
        private float prevPercentProgress;
        private StaticLayout title;
        public int x;
        public int y;

        static /* synthetic */ int access$1712(PollButton pollButton, int i) {
            int i2 = pollButton.percent + i;
            pollButton.percent = i2;
            return i2;
        }

        static /* synthetic */ float access$2424(PollButton pollButton, float f) {
            float f2 = pollButton.decimal - f;
            pollButton.decimal = f2;
            return f2;
        }
    }

    public ChatMessageCell(Context context) {
        this(context, false, null);
    }

    public ChatMessageCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.reactionsLayoutInBubble = new ReactionsLayoutInBubble(this);
        this.scrollRect = new Rect();
        this.imageBackgroundGradientRotation = 45;
        this.selectorDrawable = new Drawable[2];
        this.selectorDrawableMaskType = new int[2];
        this.instantButtonRect = new RectF();
        this.pressedState = new int[]{16842910, 16842919};
        this.deleteProgressRect = new RectF();
        this.rect = new RectF();
        this.timeAlpha = 1.0f;
        this.controlsAlpha = 1.0f;
        this.links = new LinkSpanDrawable.LinkCollector(this);
        this.urlPathCache = new ArrayList<>();
        this.urlPathSelection = new ArrayList<>();
        this.rectPath = new Path();
        this.pollButtons = new ArrayList<>();
        this.botButtons = new ArrayList<>();
        this.botButtonsByData = new HashMap<>();
        this.botButtonsByPosition = new HashMap<>();
        this.currentAccount = UserConfig.selectedAccount;
        this.isCheckPressed = true;
        this.drawBackground = true;
        this.backgroundWidth = 100;
        this.commentButtonRect = new Rect();
        this.spoilersPatchedReplyTextLayout = new AtomicReference<>();
        this.forwardedNameLayout = new StaticLayout[2];
        this.forwardNameOffsetX = new float[2];
        this.drawTime = true;
        this.ALPHA_PROPERTY_WORKAROUND = Build.VERSION.SDK_INT == 28;
        this.alphaInternal = 1.0f;
        this.transitionParams = new TransitionParams();
        this.diceFinishCallback = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell.1
            @Override // java.lang.Runnable
            public void run() {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.onDiceFinished();
                }
            }
        };
        this.invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell.2
            @Override // java.lang.Runnable
            public void run() {
                ChatMessageCell.this.checkLocationExpired();
                if (ChatMessageCell.this.locationExpired) {
                    ChatMessageCell.this.invalidate();
                    ChatMessageCell.this.scheduledInvalidate = false;
                    return;
                }
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                chatMessageCell.invalidate(((int) chatMessageCell.rect.left) - 5, ((int) ChatMessageCell.this.rect.top) - 5, ((int) ChatMessageCell.this.rect.right) + 5, ((int) ChatMessageCell.this.rect.bottom) + 5);
                if (!ChatMessageCell.this.scheduledInvalidate) {
                    return;
                }
                AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000L);
            }
        };
        this.accessibilityVirtualViewBounds = new SparseArray<>();
        this.currentFocusedVirtualView = -1;
        this.backgroundCacheParams = new Theme.MessageDrawable.PathDrawParams();
        this.replySpoilers = new ArrayList();
        this.replySpoilersPool = new Stack<>();
        this.captionSpoilers = new ArrayList();
        this.captionSpoilersPool = new Stack<>();
        this.captionPatchedSpoilersLayout = new AtomicReference<>();
        this.sPath = new Path();
        this.hadLongPress = false;
        this.ANIMATION_OFFSET_X = new Property<ChatMessageCell, Float>(this, Float.class, "animationOffsetX") { // from class: org.telegram.ui.Cells.ChatMessageCell.7
            public Float get(ChatMessageCell chatMessageCell) {
                return Float.valueOf(chatMessageCell.animationOffsetX);
            }

            public void set(ChatMessageCell chatMessageCell, Float f) {
                chatMessageCell.setAnimationOffsetX(f.floatValue());
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.canDrawBackgroundInParent = z;
        this.backgroundDrawable = new MessageBackgroundDrawable(this);
        ImageReceiver imageReceiver = new ImageReceiver();
        this.avatarImage = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.replyImageReceiver = imageReceiver2;
        imageReceiver2.setRoundRadius(AndroidUtilities.dp(2.0f));
        ImageReceiver imageReceiver3 = new ImageReceiver(this);
        this.locationImageReceiver = imageReceiver3;
        imageReceiver3.setRoundRadius(AndroidUtilities.dp(26.1f));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver4 = new ImageReceiver(this);
        this.photoImage = imageReceiver4;
        imageReceiver4.setUseRoundForThumbDrawable(true);
        this.photoImage.setDelegate(this);
        this.radialProgress = new RadialProgress2(this, resourcesProvider);
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider);
        this.videoRadialProgress = radialProgress2;
        radialProgress2.setDrawBackground(false);
        this.videoRadialProgress.setCircleRadius(AndroidUtilities.dp(15.0f));
        SeekBar seekBar = new SeekBar(this);
        this.seekBar = seekBar;
        seekBar.setDelegate(this);
        SeekBarWaveform seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform = seekBarWaveform;
        seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.seekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.Cells.ChatMessageCell.3
            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    return ChatMessageCell.this.seekBar.getProgress();
                }
                if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                    return ChatMessageCell.this.useSeekBarWaveform ? ChatMessageCell.this.seekBarWaveform.getProgress() : ChatMessageCell.this.seekBar.getProgress();
                } else if (!ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    return 0.0f;
                } else {
                    return ChatMessageCell.this.currentMessageObject.audioProgress;
                }
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float f) {
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    ChatMessageCell.this.seekBar.setProgress(f);
                } else if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                    if (ChatMessageCell.this.useSeekBarWaveform) {
                        ChatMessageCell.this.seekBarWaveform.setProgress(f);
                    } else {
                        ChatMessageCell.this.seekBar.setProgress(f);
                    }
                } else if (!ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    return;
                } else {
                    ChatMessageCell.this.currentMessageObject.audioProgress = f;
                }
                ChatMessageCell.this.onSeekBarDrag(f);
                ChatMessageCell.this.invalidate();
            }
        };
        this.roundVideoPlayingDrawable = new RoundVideoPlayingDrawable(this, resourcesProvider);
        setImportantForAccessibility(1);
    }

    private void createPollUI() {
        if (this.pollAvatarImages != null) {
            return;
        }
        this.pollAvatarImages = new ImageReceiver[3];
        this.pollAvatarDrawables = new AvatarDrawable[3];
        this.pollAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
            if (i >= imageReceiverArr.length) {
                break;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.pollAvatarImages[i].setRoundRadius(AndroidUtilities.dp(8.0f));
            this.pollAvatarDrawables[i] = new AvatarDrawable();
            this.pollAvatarDrawables[i].setTextSize(AndroidUtilities.dp(6.0f));
            i++;
        }
        this.pollCheckBox = new CheckBoxBase[10];
        int i2 = 0;
        while (true) {
            CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
            if (i2 >= checkBoxBaseArr.length) {
                return;
            }
            checkBoxBaseArr[i2] = new CheckBoxBase(this, 20, this.resourcesProvider);
            this.pollCheckBox[i2].setDrawUnchecked(false);
            this.pollCheckBox[i2].setBackgroundType(9);
            i2++;
        }
    }

    private void createCommentUI() {
        if (this.commentAvatarImages != null) {
            return;
        }
        this.commentAvatarImages = new ImageReceiver[3];
        this.commentAvatarDrawables = new AvatarDrawable[3];
        this.commentAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.commentAvatarImages;
            if (i >= imageReceiverArr.length) {
                return;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.commentAvatarImages[i].setRoundRadius(AndroidUtilities.dp(12.0f));
            this.commentAvatarDrawables[i] = new AvatarDrawable();
            this.commentAvatarDrawables[i].setTextSize(AndroidUtilities.dp(8.0f));
            i++;
        }
    }

    public void resetPressedLink(int i) {
        if (i != -1) {
            this.links.removeLinks(Integer.valueOf(i));
        } else {
            this.links.clear();
        }
        if (this.pressedLink != null) {
            if (this.pressedLinkType != i && i != -1) {
                return;
            }
            this.pressedLink = null;
            this.pressedLinkType = -1;
            invalidate();
        }
    }

    private void resetUrlPaths() {
        if (this.urlPathSelection.isEmpty()) {
            return;
        }
        this.urlPathCache.addAll(this.urlPathSelection);
        this.urlPathSelection.clear();
    }

    private LinkPath obtainNewUrlPath() {
        LinkPath linkPath;
        if (!this.urlPathCache.isEmpty()) {
            linkPath = this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        } else {
            linkPath = new LinkPath(true);
        }
        linkPath.reset();
        this.urlPathSelection.add(linkPath);
        return linkPath;
    }

    public int[] getRealSpanStartAndEnd(Spannable spannable, CharacterStyle characterStyle) {
        boolean z;
        int i;
        int i2;
        TextStyleSpan.TextStyleRun style;
        TLRPC$MessageEntity tLRPC$MessageEntity;
        if (!(characterStyle instanceof URLSpanBrowser) || (style = ((URLSpanBrowser) characterStyle).getStyle()) == null || (tLRPC$MessageEntity = style.urlEntity) == null) {
            i2 = 0;
            i = 0;
            z = false;
        } else {
            i = tLRPC$MessageEntity.offset;
            i2 = tLRPC$MessageEntity.length + i;
            z = true;
        }
        if (!z) {
            i = spannable.getSpanStart(characterStyle);
            i2 = spannable.getSpanEnd(characterStyle);
        }
        return new int[]{i, i2};
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x024a A[Catch: Exception -> 0x0263, TryCatch #1 {Exception -> 0x0263, blocks: (B:30:0x0073, B:32:0x0088, B:34:0x008e, B:36:0x00b0, B:38:0x00bb, B:40:0x00cb, B:44:0x00d1, B:45:0x00da, B:47:0x00dd, B:49:0x00e3, B:55:0x00ed, B:57:0x00f3, B:59:0x00f9, B:61:0x00fd, B:63:0x0105, B:67:0x012c, B:105:0x0238, B:106:0x023b, B:107:0x0246, B:109:0x024a, B:111:0x0254, B:68:0x0137, B:70:0x0169, B:71:0x016b, B:73:0x0175, B:75:0x0181, B:76:0x018c, B:78:0x0198, B:80:0x019b, B:83:0x01a6, B:86:0x01c9, B:87:0x01cc, B:89:0x01d2, B:91:0x01d6, B:93:0x01e2, B:94:0x01f1, B:96:0x0201, B:98:0x0204, B:101:0x020f), top: B:119:0x0073, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00ed A[Catch: Exception -> 0x0263, TryCatch #1 {Exception -> 0x0263, blocks: (B:30:0x0073, B:32:0x0088, B:34:0x008e, B:36:0x00b0, B:38:0x00bb, B:40:0x00cb, B:44:0x00d1, B:45:0x00da, B:47:0x00dd, B:49:0x00e3, B:55:0x00ed, B:57:0x00f3, B:59:0x00f9, B:61:0x00fd, B:63:0x0105, B:67:0x012c, B:105:0x0238, B:106:0x023b, B:107:0x0246, B:109:0x024a, B:111:0x0254, B:68:0x0137, B:70:0x0169, B:71:0x016b, B:73:0x0175, B:75:0x0181, B:76:0x018c, B:78:0x0198, B:80:0x019b, B:83:0x01a6, B:86:0x01c9, B:87:0x01cc, B:89:0x01d2, B:91:0x01d6, B:93:0x01e2, B:94:0x01f1, B:96:0x0201, B:98:0x0204, B:101:0x020f), top: B:119:0x0073, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00f9 A[Catch: Exception -> 0x0263, TryCatch #1 {Exception -> 0x0263, blocks: (B:30:0x0073, B:32:0x0088, B:34:0x008e, B:36:0x00b0, B:38:0x00bb, B:40:0x00cb, B:44:0x00d1, B:45:0x00da, B:47:0x00dd, B:49:0x00e3, B:55:0x00ed, B:57:0x00f3, B:59:0x00f9, B:61:0x00fd, B:63:0x0105, B:67:0x012c, B:105:0x0238, B:106:0x023b, B:107:0x0246, B:109:0x024a, B:111:0x0254, B:68:0x0137, B:70:0x0169, B:71:0x016b, B:73:0x0175, B:75:0x0181, B:76:0x018c, B:78:0x0198, B:80:0x019b, B:83:0x01a6, B:86:0x01c9, B:87:0x01cc, B:89:0x01d2, B:91:0x01d6, B:93:0x01e2, B:94:0x01f1, B:96:0x0201, B:98:0x0204, B:101:0x020f), top: B:119:0x0073, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r19) {
        /*
            Method dump skipped, instructions count: 620
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00df  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkCaptionMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 315
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkCaptionMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00ef  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkGameMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 461
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkGameMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkTranscribeButtonMotionEvent(MotionEvent motionEvent) {
        TranscribeButton transcribeButton;
        return this.useTranscribeButton && (transcribeButton = this.transcribeButton) != null && transcribeButton.onTouch(motionEvent.getAction(), motionEvent.getX() - this.transcribeX, motionEvent.getY() - this.transcribeY);
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x00e9  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x016a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkLinkPreviewMotionEvent(android.view.MotionEvent r19) {
        /*
            Method dump skipped, instructions count: 1001
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkLinkPreviewMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkPollButtonMotionEvent(MotionEvent motionEvent) {
        int i;
        int i2;
        if (this.currentMessageObject.eventId != 0 || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty()) {
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type != 17 || !messageObject.isSent()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            this.pressedVoteButton = -1;
            this.pollHintPressed = false;
            if (this.hintButtonVisible && (i = this.pollHintX) != -1 && x >= i && x <= i + AndroidUtilities.dp(40.0f) && y >= (i2 = this.pollHintY) && y <= i2 + AndroidUtilities.dp(40.0f)) {
                this.pollHintPressed = true;
                this.selectorDrawableMaskType[0] = 3;
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (drawableArr[0] != null) {
                        drawableArr[0].setBounds(this.pollHintX - AndroidUtilities.dp(8.0f), this.pollHintY - AndroidUtilities.dp(8.0f), this.pollHintX + AndroidUtilities.dp(32.0f), this.pollHintY + AndroidUtilities.dp(32.0f));
                        this.selectorDrawable[0].setHotspot(x, y);
                        this.selectorDrawable[0].setState(this.pressedState);
                    }
                }
                invalidate();
            } else {
                for (int i3 = 0; i3 < this.pollButtons.size(); i3++) {
                    PollButton pollButton = this.pollButtons.get(i3);
                    int dp = (pollButton.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                    int i4 = pollButton.x;
                    if (x >= i4 && x <= (i4 + this.backgroundWidth) - AndroidUtilities.dp(31.0f) && y >= dp && y <= pollButton.height + dp + AndroidUtilities.dp(26.0f)) {
                        this.pressedVoteButton = i3;
                        if (!this.pollVoted && !this.pollClosed) {
                            this.selectorDrawableMaskType[0] = 1;
                            if (Build.VERSION.SDK_INT >= 21) {
                                Drawable[] drawableArr2 = this.selectorDrawable;
                                if (drawableArr2[0] != null) {
                                    drawableArr2[0].setBounds(pollButton.x - AndroidUtilities.dp(9.0f), dp, (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), pollButton.height + dp + AndroidUtilities.dp(26.0f));
                                    this.selectorDrawable[0].setHotspot(x, y);
                                    this.selectorDrawable[0].setState(this.pressedState);
                                }
                            }
                            invalidate();
                        }
                    }
                }
                return false;
            }
            return true;
        } else if (motionEvent.getAction() == 1) {
            if (this.pollHintPressed) {
                playSoundEffect(0);
                this.delegate.didPressHint(this, 0);
                this.pollHintPressed = false;
                if (Build.VERSION.SDK_INT < 21) {
                    return false;
                }
                Drawable[] drawableArr3 = this.selectorDrawable;
                if (drawableArr3[0] == null) {
                    return false;
                }
                drawableArr3[0].setState(StateSet.NOTHING);
                return false;
            } else if (this.pressedVoteButton == -1) {
                return false;
            } else {
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr4 = this.selectorDrawable;
                    if (drawableArr4[0] != null) {
                        drawableArr4[0].setState(StateSet.NOTHING);
                    }
                }
                if (this.currentMessageObject.scheduled) {
                    Toast.makeText(getContext(), LocaleController.getString("MessageScheduledVote", R.string.MessageScheduledVote), 1).show();
                } else {
                    PollButton pollButton2 = this.pollButtons.get(this.pressedVoteButton);
                    TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = pollButton2.answer;
                    if (this.pollVoted || this.pollClosed) {
                        ArrayList<TLRPC$TL_pollAnswer> arrayList = new ArrayList<>();
                        arrayList.add(tLRPC$TL_pollAnswer);
                        this.delegate.didPressVoteButtons(this, arrayList, pollButton2.count, pollButton2.x + AndroidUtilities.dp(50.0f), this.namesOffset + pollButton2.y);
                    } else if (this.lastPoll.multiple_choice) {
                        if (this.currentMessageObject.checkedVotes.contains(tLRPC$TL_pollAnswer)) {
                            this.currentMessageObject.checkedVotes.remove(tLRPC$TL_pollAnswer);
                            this.pollCheckBox[this.pressedVoteButton].setChecked(false, true);
                        } else {
                            this.currentMessageObject.checkedVotes.add(tLRPC$TL_pollAnswer);
                            this.pollCheckBox[this.pressedVoteButton].setChecked(true, true);
                        }
                    } else {
                        this.pollVoteInProgressNum = this.pressedVoteButton;
                        this.pollVoteInProgress = true;
                        this.vibrateOnPollVote = true;
                        this.voteCurrentProgressTime = 0.0f;
                        this.firstCircleLength = true;
                        this.voteCurrentCircleLength = 360.0f;
                        this.voteRisingCircleLength = false;
                        ArrayList<TLRPC$TL_pollAnswer> arrayList2 = new ArrayList<>();
                        arrayList2.add(tLRPC$TL_pollAnswer);
                        this.delegate.didPressVoteButtons(this, arrayList2, -1, 0, 0);
                    }
                }
                this.pressedVoteButton = -1;
                invalidate();
                return false;
            }
        } else if (motionEvent.getAction() != 2) {
            return false;
        } else {
            if ((this.pressedVoteButton == -1 && !this.pollHintPressed) || Build.VERSION.SDK_INT < 21) {
                return false;
            }
            Drawable[] drawableArr5 = this.selectorDrawable;
            if (drawableArr5[0] == null) {
                return false;
            }
            drawableArr5[0].setHotspot(x, y);
            return false;
        }
    }

    private boolean checkInstantButtonMotionEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject.isSponsored() || (this.drawInstantView && this.currentMessageObject.type != 0)) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int i = 2;
            if (motionEvent.getAction() == 0) {
                if (this.drawInstantView) {
                    float f = x;
                    float f2 = y;
                    if (this.instantButtonRect.contains(f, f2)) {
                        int[] iArr = this.selectorDrawableMaskType;
                        if (this.lastPoll == null) {
                            i = 0;
                        }
                        iArr[0] = i;
                        this.instantPressed = true;
                        if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null && this.instantButtonRect.contains(f, f2)) {
                            this.selectorDrawable[0].setHotspot(f, f2);
                            this.selectorDrawable[0].setState(this.pressedState);
                            this.instantButtonPressed = true;
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (motionEvent.getAction() == 1) {
                if (this.instantPressed) {
                    ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                    if (chatMessageCellDelegate != null) {
                        if (this.lastPoll != null) {
                            MessageObject messageObject = this.currentMessageObject;
                            if (messageObject.scheduled) {
                                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledVoteResults", R.string.MessageScheduledVoteResults), 1).show();
                            } else if (this.pollVoted || this.pollClosed) {
                                chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                            } else {
                                if (!messageObject.checkedVotes.isEmpty()) {
                                    this.pollVoteInProgressNum = -1;
                                    this.pollVoteInProgress = true;
                                    this.vibrateOnPollVote = true;
                                    this.voteCurrentProgressTime = 0.0f;
                                    this.firstCircleLength = true;
                                    this.voteCurrentCircleLength = 360.0f;
                                    this.voteRisingCircleLength = false;
                                }
                                this.delegate.didPressVoteButtons(this, this.currentMessageObject.checkedVotes, -1, 0, this.namesOffset);
                            }
                        } else {
                            chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                        }
                    }
                    playSoundEffect(0);
                    if (Build.VERSION.SDK_INT >= 21) {
                        Drawable[] drawableArr = this.selectorDrawable;
                        if (drawableArr[0] != null) {
                            drawableArr[0].setState(StateSet.NOTHING);
                        }
                    }
                    this.instantButtonPressed = false;
                    this.instantPressed = false;
                    invalidate();
                }
            } else if (motionEvent.getAction() == 2 && this.instantButtonPressed && Build.VERSION.SDK_INT >= 21) {
                Drawable[] drawableArr2 = this.selectorDrawable;
                if (drawableArr2[0] != null) {
                    drawableArr2[0].setHotspot(x, y);
                }
            }
            return false;
        }
        return false;
    }

    private void invalidateWithParent() {
        if (this.currentMessagesGroup != null && getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
        invalidate();
    }

    private boolean checkCommentButtonMotionEvent(MotionEvent motionEvent) {
        int i = 0;
        if (!this.drawCommentButton) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && (groupedMessagePosition.flags & 1) == 0 && this.commentButtonRect.contains(x, y)) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            int childCount = viewGroup.getChildCount();
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = viewGroup.getChildAt(i);
                if (childAt != this && (childAt instanceof ChatMessageCell)) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    if (chatMessageCell.drawCommentButton && chatMessageCell.currentMessagesGroup == this.currentMessagesGroup && (chatMessageCell.currentPosition.flags & 1) != 0) {
                        MotionEvent obtain = MotionEvent.obtain(0L, 0L, motionEvent.getActionMasked(), (motionEvent.getX() + getLeft()) - chatMessageCell.getLeft(), (motionEvent.getY() + getTop()) - chatMessageCell.getTop(), 0);
                        chatMessageCell.checkCommentButtonMotionEvent(obtain);
                        obtain.recycle();
                        break;
                    }
                }
                i++;
            }
            return true;
        }
        if (motionEvent.getAction() == 0) {
            if (this.commentButtonRect.contains(x, y)) {
                if (this.currentMessageObject.isSent()) {
                    this.selectorDrawableMaskType[1] = 2;
                    this.commentButtonPressed = true;
                    if (Build.VERSION.SDK_INT >= 21) {
                        Drawable[] drawableArr = this.selectorDrawable;
                        if (drawableArr[1] != null) {
                            drawableArr[1].setHotspot(x, y);
                            this.selectorDrawable[1].setState(this.pressedState);
                        }
                    }
                    invalidateWithParent();
                }
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.commentButtonPressed) {
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    if (this.isRepliesChat) {
                        chatMessageCellDelegate.didPressSideButton(this);
                    } else {
                        chatMessageCellDelegate.didPressCommentButton(this);
                    }
                }
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr2 = this.selectorDrawable;
                    if (drawableArr2[1] != null) {
                        drawableArr2[1].setState(StateSet.NOTHING);
                    }
                }
                this.commentButtonPressed = false;
                invalidateWithParent();
            }
        } else if (motionEvent.getAction() == 2 && this.commentButtonPressed && Build.VERSION.SDK_INT >= 21) {
            Drawable[] drawableArr3 = this.selectorDrawable;
            if (drawableArr3[1] != null) {
                drawableArr3[1].setHotspot(x, y);
            }
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r11v24, types: [boolean] */
    private boolean checkOtherButtonMotionEvent(MotionEvent motionEvent) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        int i = this.documentAttachType;
        if ((i == 5 || i == 1) && (groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.flags & 4) == 0) {
            return false;
        }
        int i2 = this.currentMessageObject.type;
        boolean z = i2 == 16;
        if (!z) {
            z = (i == 1 || i2 == 12 || i == 5 || i == 4 || i == 2 || i2 == 8) && !this.hasGamePreview && !this.hasInvoicePreview;
        }
        if (!z) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject.type == 16) {
                ?? isVideoCall = messageObject.isVideoCall();
                int i3 = this.otherX;
                if (x >= i3) {
                    if (x <= i3 + AndroidUtilities.dp((isVideoCall == 0 ? 202 : 200) + 30) && y >= this.otherY - AndroidUtilities.dp(14.0f) && y <= this.otherY + AndroidUtilities.dp(50.0f)) {
                        this.otherPressed = true;
                        this.selectorDrawableMaskType[0] = 4;
                        if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null) {
                            int dp = this.otherX + AndroidUtilities.dp(isVideoCall == 0 ? 202.0f : 200.0f) + (Theme.chat_msgInCallDrawable[isVideoCall == true ? 1 : 0].getIntrinsicWidth() / 2);
                            int intrinsicHeight = this.otherY + (Theme.chat_msgInCallDrawable[isVideoCall].getIntrinsicHeight() / 2);
                            this.selectorDrawable[0].setBounds(dp - AndroidUtilities.dp(20.0f), intrinsicHeight - AndroidUtilities.dp(20.0f), dp + AndroidUtilities.dp(20.0f), intrinsicHeight + AndroidUtilities.dp(20.0f));
                            this.selectorDrawable[0].setHotspot(x, y);
                            this.selectorDrawable[0].setState(this.pressedState);
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(30.0f)) {
                this.otherPressed = true;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.otherPressed) {
                if (this.currentMessageObject.type == 16 && Build.VERSION.SDK_INT >= 21) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (drawableArr[0] != null) {
                        drawableArr[0].setState(StateSet.NOTHING);
                    }
                }
                this.otherPressed = false;
                playSoundEffect(0);
                this.delegate.didPressOther(this, this.otherX, this.otherY);
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.currentMessageObject.type == 16 && this.otherPressed && Build.VERSION.SDK_INT >= 21) {
            Drawable[] drawableArr2 = this.selectorDrawable;
            if (drawableArr2[0] != null) {
                drawableArr2[0].setHotspot(x, y);
            }
        }
        return false;
    }

    private boolean checkDateMotionEvent(MotionEvent motionEvent) {
        if (!this.currentMessageObject.isImportedForward()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            float f = x;
            float f2 = this.drawTimeX;
            if (f < f2 || f > f2 + this.timeWidth) {
                return false;
            }
            float f3 = y;
            float f4 = this.drawTimeY;
            if (f3 < f4 || f3 > f4 + AndroidUtilities.dp(20.0f)) {
                return false;
            }
            this.timePressed = true;
            invalidate();
        } else if (motionEvent.getAction() != 1 || !this.timePressed) {
            return false;
        } else {
            this.timePressed = false;
            playSoundEffect(0);
            this.delegate.didPressTime(this);
            invalidate();
        }
        return true;
    }

    private boolean checkRoundSeekbar(MotionEvent motionEvent) {
        if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || !MediaController.getInstance().isMessagePaused()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            float f = x;
            if (f >= this.seekbarRoundX - AndroidUtilities.dp(20.0f) && f <= this.seekbarRoundX + AndroidUtilities.dp(20.0f)) {
                float f2 = y;
                if (f2 >= this.seekbarRoundY - AndroidUtilities.dp(20.0f) && f2 <= this.seekbarRoundY + AndroidUtilities.dp(20.0f)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    cancelCheckLongPress();
                    this.roundSeekbarTouched = 1;
                    invalidate();
                }
            }
            float centerX = f - this.photoImage.getCenterX();
            float centerY = y - this.photoImage.getCenterY();
            float imageWidth = (this.photoImage.getImageWidth() - AndroidUtilities.dp(64.0f)) / 2.0f;
            float f3 = (centerX * centerX) + (centerY * centerY);
            if (f3 < ((this.photoImage.getImageWidth() / 2.0f) * this.photoImage.getImageWidth()) / 2.0f && f3 > imageWidth * imageWidth) {
                getParent().requestDisallowInterceptTouchEvent(true);
                cancelCheckLongPress();
                this.roundSeekbarTouched = 1;
                invalidate();
            }
        } else if (this.roundSeekbarTouched == 1 && motionEvent.getAction() == 2) {
            float degrees = ((float) Math.toDegrees(Math.atan2(y - this.photoImage.getCenterY(), x - this.photoImage.getCenterX()))) + 90.0f;
            if (degrees < 0.0f) {
                degrees += 360.0f;
            }
            float f4 = degrees / 360.0f;
            if (Math.abs(this.currentMessageObject.audioProgress - f4) > 0.9f) {
                if (this.roundSeekbarOutAlpha == 0.0f) {
                    performHapticFeedback(3);
                }
                this.roundSeekbarOutAlpha = 1.0f;
                this.roundSeekbarOutProgress = this.currentMessageObject.audioProgress;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.lastSeekUpdateTime > 100) {
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f4);
                this.lastSeekUpdateTime = currentTimeMillis;
            }
            this.currentMessageObject.audioProgress = f4;
            updatePlayingMessageProgress();
        }
        if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.roundSeekbarTouched != 0) {
            if (motionEvent.getAction() == 1) {
                float degrees2 = ((float) Math.toDegrees(Math.atan2(y - this.photoImage.getCenterY(), x - this.photoImage.getCenterX()))) + 90.0f;
                if (degrees2 < 0.0f) {
                    degrees2 += 360.0f;
                }
                float f5 = degrees2 / 360.0f;
                this.currentMessageObject.audioProgress = f5;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f5);
                updatePlayingMessageProgress();
            }
            MediaController.getInstance().playMessage(this.currentMessageObject);
            this.roundSeekbarTouched = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return this.roundSeekbarTouched != 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0199  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01be  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkPhotoImageMotionEvent(android.view.MotionEvent r9) {
        /*
            Method dump skipped, instructions count: 590
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkPhotoImageMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:57:0x00da, code lost:
        if (r3 <= (r0 + r6)) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00dc, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0114, code lost:
        if (r3 <= (r0 + r6)) goto L58;
     */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x011e  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x012e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 398
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    public boolean checkSpoilersMotionEvent(MotionEvent motionEvent) {
        int i;
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                View childAt = viewGroup.getChildAt(i2);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i3 = currentPosition.flags;
                        if ((i3 & 8) != 0 && (i3 & 1) != 0 && chatMessageCell != this) {
                            motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                            boolean checkSpoilersMotionEvent = chatMessageCell.checkSpoilersMotionEvent(motionEvent);
                            motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                            return checkSpoilersMotionEvent;
                        }
                    }
                }
            }
        }
        if (this.isSpoilerRevealing) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int i4 = this.textX;
            if (x >= i4 && y >= (i = this.textY)) {
                MessageObject messageObject = this.currentMessageObject;
                if (x <= i4 + messageObject.textWidth && y <= i + messageObject.textHeight) {
                    ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                    for (int i5 = 0; i5 < arrayList.size() && arrayList.get(i5).textYOffset <= y; i5++) {
                        MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i5);
                        int i6 = textLayoutBlock.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                        for (SpoilerEffect spoilerEffect : textLayoutBlock.spoilers) {
                            if (spoilerEffect.getBounds().contains((x - this.textX) + i6, (int) ((y - this.textY) - textLayoutBlock.textYOffset))) {
                                this.spoilerPressed = spoilerEffect;
                                this.isCaptionSpoilerPressed = false;
                                return true;
                            }
                        }
                    }
                }
            }
            if (hasCaptionLayout()) {
                float f = x;
                float f2 = this.captionX;
                if (f >= f2) {
                    float f3 = y;
                    if (f3 >= this.captionY && f <= f2 + this.captionLayout.getWidth() && f3 <= this.captionY + this.captionLayout.getHeight()) {
                        for (SpoilerEffect spoilerEffect2 : this.captionSpoilers) {
                            if (spoilerEffect2.getBounds().contains((int) (f - this.captionX), (int) (f3 - this.captionY))) {
                                this.spoilerPressed = spoilerEffect2;
                                this.isCaptionSpoilerPressed = true;
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (actionMasked == 1 && this.spoilerPressed != null) {
            playSoundEffect(0);
            this.sPath.rewind();
            if (this.isCaptionSpoilerPressed) {
                for (SpoilerEffect spoilerEffect3 : this.captionSpoilers) {
                    Rect bounds = spoilerEffect3.getBounds();
                    this.sPath.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
                }
            } else {
                Iterator<MessageObject.TextLayoutBlock> it = this.currentMessageObject.textLayoutBlocks.iterator();
                while (it.hasNext()) {
                    MessageObject.TextLayoutBlock next = it.next();
                    for (SpoilerEffect spoilerEffect4 : next.spoilers) {
                        Rect bounds2 = spoilerEffect4.getBounds();
                        float f4 = next.textYOffset;
                        this.sPath.addRect(bounds2.left, bounds2.top + f4, bounds2.right, bounds2.bottom + f4, Path.Direction.CW);
                    }
                }
            }
            this.sPath.computeBounds(this.rect, false);
            float sqrt = (float) Math.sqrt(Math.pow(this.rect.width(), 2.0d) + Math.pow(this.rect.height(), 2.0d));
            this.isSpoilerRevealing = true;
            this.spoilerPressed.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatMessageCell.this.lambda$checkSpoilersMotionEvent$1();
                }
            });
            if (this.isCaptionSpoilerPressed) {
                for (SpoilerEffect spoilerEffect5 : this.captionSpoilers) {
                    spoilerEffect5.startRipple(x - this.captionX, y - this.captionY, sqrt);
                }
            } else {
                ArrayList<MessageObject.TextLayoutBlock> arrayList2 = this.currentMessageObject.textLayoutBlocks;
                if (arrayList2 != null) {
                    Iterator<MessageObject.TextLayoutBlock> it2 = arrayList2.iterator();
                    while (it2.hasNext()) {
                        MessageObject.TextLayoutBlock next2 = it2.next();
                        int i7 = next2.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                        for (SpoilerEffect spoilerEffect6 : next2.spoilers) {
                            spoilerEffect6.startRipple((x - this.textX) + i7, (y - next2.textYOffset) - this.textY, sqrt);
                        }
                    }
                }
            }
            if (getParent() instanceof RecyclerListView) {
                ViewGroup viewGroup2 = (ViewGroup) getParent();
                for (int i8 = 0; i8 < viewGroup2.getChildCount(); i8++) {
                    View childAt2 = viewGroup2.getChildAt(i8);
                    if (childAt2 instanceof ChatMessageCell) {
                        final ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                        if (chatMessageCell2.getMessageObject() != null && chatMessageCell2.getMessageObject().getReplyMsgId() == getMessageObject().getId() && !chatMessageCell2.replySpoilers.isEmpty()) {
                            chatMessageCell2.replySpoilers.get(0).setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ChatMessageCell.this.lambda$checkSpoilersMotionEvent$3(chatMessageCell2);
                                }
                            });
                            for (SpoilerEffect spoilerEffect7 : chatMessageCell2.replySpoilers) {
                                spoilerEffect7.startRipple(spoilerEffect7.getBounds().centerX(), spoilerEffect7.getBounds().centerY(), sqrt);
                            }
                        }
                    }
                }
            }
            this.spoilerPressed = null;
            return true;
        }
        return false;
    }

    public /* synthetic */ void lambda$checkSpoilersMotionEvent$1() {
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChatMessageCell.this.lambda$checkSpoilersMotionEvent$0();
            }
        });
    }

    public /* synthetic */ void lambda$checkSpoilersMotionEvent$0() {
        this.isSpoilerRevealing = false;
        getMessageObject().isSpoilersRevealed = true;
        if (this.isCaptionSpoilerPressed) {
            this.captionSpoilers.clear();
        } else {
            ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
            if (arrayList != null) {
                Iterator<MessageObject.TextLayoutBlock> it = arrayList.iterator();
                while (it.hasNext()) {
                    it.next().spoilers.clear();
                }
            }
        }
        invalidate();
    }

    public /* synthetic */ void lambda$checkSpoilersMotionEvent$3(ChatMessageCell chatMessageCell) {
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChatMessageCell.lambda$checkSpoilersMotionEvent$2(ChatMessageCell.this);
            }
        });
    }

    public static /* synthetic */ void lambda$checkSpoilersMotionEvent$2(ChatMessageCell chatMessageCell) {
        chatMessageCell.getMessageObject().replyMessageObject.isSpoilersRevealed = true;
        chatMessageCell.replySpoilers.clear();
        chatMessageCell.invalidate();
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        int i;
        if (this.botButtons.isEmpty() || this.currentMessageObject.eventId != 0) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.currentMessageObject.isOutOwner()) {
                i = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                i = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            for (int i2 = 0; i2 < this.botButtons.size(); i2++) {
                BotButton botButton = this.botButtons.get(i2);
                int dp = (botButton.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x >= botButton.x + i && x <= botButton.x + i + botButton.width && y >= dp && y <= dp + botButton.height) {
                    this.pressedBotButton = i2;
                    invalidate();
                    final int i3 = this.pressedBotButton;
                    postDelayed(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatMessageCell.this.lambda$checkBotButtonMotionEvent$4(i3);
                        }
                    }, ViewConfiguration.getLongPressTimeout() - 1);
                    return true;
                }
            }
            return false;
        } else if (motionEvent.getAction() != 1 || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(0);
            if (this.currentMessageObject.scheduled) {
                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledBotAction", R.string.MessageScheduledBotAction), 1).show();
            } else {
                BotButton botButton2 = this.botButtons.get(this.pressedBotButton);
                if (botButton2.button != null) {
                    this.delegate.didPressBotButton(this, botButton2.button);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    public /* synthetic */ void lambda$checkBotButtonMotionEvent$4(int i) {
        int i2 = this.pressedBotButton;
        if (i == i2) {
            if (!this.currentMessageObject.scheduled) {
                BotButton botButton = this.botButtons.get(i2);
                if (botButton.button != null) {
                    cancelCheckLongPress();
                    this.delegate.didLongPressBotButton(this, botButton.button);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:385:0x05ca, code lost:
        if (r4 > (r0 + org.telegram.messenger.AndroidUtilities.dp(32 + ((r19.drawSideButton != 3 || r19.commentLayout == null) ? 0 : 18)))) goto L386;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            Method dump skipped, instructions count: 1498
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkReactionsTouchEvent(MotionEvent motionEvent) {
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (viewGroup == null) {
                return false;
            }
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i2 = currentPosition.flags;
                        if ((i2 & 8) != 0 && (i2 & 1) != 0) {
                            if (chatMessageCell == this) {
                                return this.reactionsLayoutInBubble.chekTouchEvent(motionEvent);
                            }
                            motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                            boolean chekTouchEvent = chatMessageCell.reactionsLayoutInBubble.chekTouchEvent(motionEvent);
                            motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                            return chekTouchEvent;
                        }
                    }
                }
            }
            return false;
        }
        return this.reactionsLayoutInBubble.chekTouchEvent(motionEvent);
    }

    private boolean checkPinchToZoom(MotionEvent motionEvent) {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        PinchToZoomHelper pinchToZoomHelper = chatMessageCellDelegate == null ? null : chatMessageCellDelegate.getPinchToZoomHelper();
        if (this.currentMessageObject == null || !this.photoImage.hasNotThumb() || pinchToZoomHelper == null || this.currentMessageObject.isSticker() || this.currentMessageObject.isAnimatedEmoji()) {
            return false;
        }
        if ((this.currentMessageObject.isVideo() && !this.autoPlayingMedia) || this.isRoundVideo || this.currentMessageObject.isAnimatedSticker()) {
            return false;
        }
        if ((this.currentMessageObject.isDocument() && !this.currentMessageObject.isGif()) || this.currentMessageObject.needDrawBluredPreview()) {
            return false;
        }
        return pinchToZoomHelper.checkPinchToZoom(motionEvent, this, this.photoImage, this.currentMessageObject);
    }

    private boolean checkTextSelection(MotionEvent motionEvent) {
        MessageObject messageObject;
        TLRPC$Message tLRPC$Message;
        int i;
        int i2;
        int i3;
        MessageObject.GroupedMessages groupedMessages;
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper = this.delegate.getTextSelectionHelper();
        if (textSelectionHelper == null || MessagesController.getInstance(this.currentAccount).isChatNoForwards(this.currentMessageObject.getChatId()) || ((tLRPC$Message = (messageObject = this.currentMessageObject).messageOwner) != null && tLRPC$Message.noforwards)) {
            return false;
        }
        ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
        if (!(arrayList != null && !arrayList.isEmpty()) && !hasCaptionLayout()) {
            return false;
        }
        if ((!this.drawSelectionBackground && this.currentMessagesGroup == null) || (this.currentMessagesGroup != null && !this.delegate.hasSelectedMessages())) {
            return false;
        }
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                View childAt = viewGroup.getChildAt(i4);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i5 = currentPosition.flags;
                        if ((i5 & 8) != 0 && (i5 & 1) != 0) {
                            textSelectionHelper.setMaybeTextCord((int) chatMessageCell.captionX, (int) chatMessageCell.captionY);
                            textSelectionHelper.setMessageObject(chatMessageCell);
                            if (chatMessageCell == this) {
                                return textSelectionHelper.onTouchEvent(motionEvent);
                            }
                            motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                            boolean onTouchEvent = textSelectionHelper.onTouchEvent(motionEvent);
                            motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                            return onTouchEvent;
                        }
                    }
                }
            }
            return false;
        }
        if (hasCaptionLayout()) {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setMaybeTextCord((int) this.captionX, (int) this.captionY);
        } else if (this.descriptionLayout != null && motionEvent.getY() > this.descriptionY) {
            textSelectionHelper.setIsDescription(true);
            if (this.hasGamePreview) {
                i = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else {
                if (this.hasInvoicePreview) {
                    i3 = this.unmovedTextX;
                    i2 = AndroidUtilities.dp(1.0f);
                } else {
                    i3 = this.unmovedTextX;
                    i2 = AndroidUtilities.dp(1.0f);
                }
                i = i3 + i2;
            }
            textSelectionHelper.setMaybeTextCord(i + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
        } else {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setMaybeTextCord(this.textX, this.textY);
        }
        textSelectionHelper.setMessageObject(this);
        return textSelectionHelper.onTouchEvent(motionEvent);
    }

    private void updateSelectionTextPosition() {
        int i;
        int i2;
        int i3;
        if (getDelegate() == null || getDelegate().getTextSelectionHelper() == null || !getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
            return;
        }
        int textSelectionType = getDelegate().getTextSelectionHelper().getTextSelectionType(this);
        if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_DESCRIPTION) {
            if (this.hasGamePreview) {
                i = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else {
                if (this.hasInvoicePreview) {
                    i3 = this.unmovedTextX;
                    i2 = AndroidUtilities.dp(1.0f);
                } else {
                    i3 = this.unmovedTextX;
                    i2 = AndroidUtilities.dp(1.0f);
                }
                i = i3 + i2;
            }
            getDelegate().getTextSelectionHelper().updateTextPosition(i + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
        } else if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_CAPTION) {
            getDelegate().getTextSelectionHelper().updateTextPosition((int) this.captionX, (int) this.captionY);
        } else {
            getDelegate().getTextSelectionHelper().updateTextPosition(this.textX, this.textY);
        }
    }

    public ArrayList<PollButton> getPollButtons() {
        return this.pollButtons;
    }

    public void updatePlayingMessageProgress() {
        String formatShortDuration;
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        VideoPlayerRewinder videoPlayerRewinder = this.videoPlayerRewinder;
        if (videoPlayerRewinder != null && videoPlayerRewinder.rewindCount != 0 && videoPlayerRewinder.rewindByBackSeek) {
            messageObject.audioProgress = videoPlayerRewinder.getVideoProgress();
        }
        int i2 = 0;
        if (this.documentAttachType == 4) {
            if (this.infoLayout != null && (PhotoViewer.isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject))) {
                return;
            }
            AnimatedFileDrawable animation = this.photoImage.getAnimation();
            if (animation != null) {
                MessageObject messageObject2 = this.currentMessageObject;
                i2 = animation.getDurationMs() / 1000;
                messageObject2.audioPlayerDuration = i2;
                MessageObject messageObject3 = this.currentMessageObject;
                TLRPC$Message tLRPC$Message = messageObject3.messageOwner;
                if (tLRPC$Message.ttl > 0 && tLRPC$Message.destroyTime == 0 && !messageObject3.needDrawBluredPreview() && this.currentMessageObject.isVideo() && animation.hasBitmap()) {
                    this.delegate.didStartVideoStream(this.currentMessageObject);
                }
            }
            if (i2 == 0) {
                i2 = this.currentMessageObject.getDuration();
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                float f = i2;
                i2 = (int) (f - (this.currentMessageObject.audioProgress * f));
            } else if (animation != null) {
                if (i2 != 0) {
                    i2 -= animation.getCurrentProgressMs() / 1000;
                }
                if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                    this.delegate.videoTimerReached();
                }
            }
            if (this.lastTime == i2) {
                return;
            }
            String formatShortDuration2 = AndroidUtilities.formatShortDuration(i2);
            this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(formatShortDuration2));
            this.infoLayout = new StaticLayout(formatShortDuration2, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.lastTime = i2;
        } else if (this.isRoundVideo) {
            TLRPC$Document document = this.currentMessageObject.getDocument();
            int i3 = 0;
            while (true) {
                if (i3 >= document.attributes.size()) {
                    i = 0;
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i3);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    i = tLRPC$DocumentAttribute.duration;
                    break;
                }
                i3++;
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                i = Math.max(0, i - this.currentMessageObject.audioProgressSec);
            }
            if (this.lastTime != i) {
                this.lastTime = i;
                String formatLongDuration = AndroidUtilities.formatLongDuration(i);
                this.timeWidthAudio = (int) Math.ceil(Theme.chat_timePaint.measureText(formatLongDuration));
                this.durationLayout = new StaticLayout(formatLongDuration, Theme.chat_timePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            float f2 = this.currentMessageObject.audioProgress;
            if (f2 != 0.0f) {
                this.lastDrawingAudioProgress = f2;
                if (f2 > 0.9f) {
                    this.lastDrawingAudioProgress = 1.0f;
                }
            }
            invalidate();
        } else if (this.documentAttach == null) {
        } else {
            if (this.useSeekBarWaveform) {
                if (!this.seekBarWaveform.isDragging()) {
                    this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress, true);
                }
            } else if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
            }
            if (this.documentAttachType == 3) {
                if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= this.documentAttach.attributes.size()) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = this.documentAttach.attributes.get(i4);
                        if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeAudio) {
                            i2 = tLRPC$DocumentAttribute2.duration;
                            break;
                        }
                        i4++;
                    }
                } else {
                    i2 = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != i2) {
                    this.lastTime = i2;
                    String formatLongDuration2 = AndroidUtilities.formatLongDuration(i2);
                    this.timeWidthAudio = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(formatLongDuration2));
                    this.durationLayout = new StaticLayout(formatLongDuration2, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            } else {
                int duration = this.currentMessageObject.getDuration();
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    i2 = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != i2) {
                    this.lastTime = i2;
                    this.durationLayout = new StaticLayout(AndroidUtilities.formatShortDuration(i2, duration), Theme.chat_audioTimePaint, (int) Math.ceil(Theme.chat_audioTimePaint.measureText(formatShortDuration)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            }
            invalidate();
        }
    }

    public void setFullyDraw(boolean z) {
        this.fullyDraw = z;
    }

    public void setParentViewSize(int i, int i2) {
        Theme.MessageDrawable messageDrawable;
        this.parentWidth = i;
        this.parentHeight = i2;
        this.backgroundHeight = i2;
        if ((this.currentMessageObject == null || !hasGradientService() || !this.currentMessageObject.shouldDrawWithoutBackground()) && ((messageDrawable = this.currentBackgroundDrawable) == null || messageDrawable.getGradientShader() == null)) {
            return;
        }
        invalidate();
    }

    public void setVisiblePart(int i, int i2, int i3, float f, float f2, int i4, int i5, int i6, int i7) {
        MessageObject.TextLayoutBlock textLayoutBlock;
        this.parentWidth = i4;
        this.parentHeight = i5;
        this.backgroundHeight = i5;
        this.blurredViewTopOffset = i6;
        this.blurredViewBottomOffset = i7;
        this.viewTop = f2;
        if (i3 != i5 || f != this.parentViewTopOffset) {
            this.parentViewTopOffset = f;
            this.parentHeight = i3;
        }
        if (this.currentMessageObject != null && hasGradientService() && this.currentMessageObject.shouldDrawWithoutBackground()) {
            invalidate();
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.textLayoutBlocks == null) {
            return;
        }
        int i8 = i - this.textY;
        int i9 = 0;
        int i10 = 0;
        for (int i11 = 0; i11 < this.currentMessageObject.textLayoutBlocks.size() && this.currentMessageObject.textLayoutBlocks.get(i11).textYOffset <= i8; i11++) {
            i10 = i11;
        }
        int i12 = -1;
        int i13 = -1;
        while (i10 < this.currentMessageObject.textLayoutBlocks.size()) {
            float f3 = this.currentMessageObject.textLayoutBlocks.get(i10).textYOffset;
            float f4 = i8;
            if (intersect(f3, textLayoutBlock.height + f3, f4, i8 + i2)) {
                if (i12 == -1) {
                    i12 = i10;
                }
                i9++;
                i13 = i10;
            } else if (f3 > f4) {
                break;
            }
            i10++;
        }
        if (this.lastVisibleBlockNum == i13 && this.firstVisibleBlockNum == i12 && this.totalVisibleBlocksCount == i9) {
            return;
        }
        this.lastVisibleBlockNum = i13;
        this.firstVisibleBlockNum = i12;
        this.totalVisibleBlocksCount = i9;
        invalidate();
    }

    public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int i5 = i;
        int i6 = 0;
        for (int i7 = 0; i7 < i3; i7++) {
            staticLayout.getLineDirections(i7);
            if (staticLayout.getLineLeft(i7) != 0.0f || staticLayout.isRtlCharAt(staticLayout.getLineStart(i7)) || staticLayout.isRtlCharAt(staticLayout.getLineEnd(i7))) {
                i5 = i2;
            }
            int lineEnd = staticLayout.getLineEnd(i7);
            if (lineEnd == charSequence.length()) {
                break;
            }
            int i8 = (lineEnd - 1) + i6;
            if (spannableStringBuilder.charAt(i8) == ' ') {
                spannableStringBuilder.replace(i8, i8 + 1, (CharSequence) "\n");
            } else if (spannableStringBuilder.charAt(i8) != '\n') {
                spannableStringBuilder.insert(i8, (CharSequence) "\n");
                i6++;
            }
            if (i7 == staticLayout.getLineCount() - 1 || i7 == i4 - 1) {
                break;
            }
        }
        int i9 = i5;
        return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i9, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1.0f), false, TextUtils.TruncateAt.END, i9, i4, true);
    }

    private void didClickedImage() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        TLRPC$WebPage tLRPC$WebPage;
        boolean z;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type == 1 || messageObject.isAnyKindOfSticker()) {
            int i = this.buttonState;
            if (i == -1) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                return;
            } else if (i != 0) {
                return;
            } else {
                didPressButton(true, false);
                return;
            }
        }
        MessageObject messageObject2 = this.currentMessageObject;
        int i2 = messageObject2.type;
        if (i2 == 12) {
            long j = messageObject2.messageOwner.media.user_id;
            TLRPC$User tLRPC$User = null;
            if (j != 0) {
                tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
            }
            this.delegate.didPressUserAvatar(this, tLRPC$User, this.lastTouchX, this.lastTouchY);
        } else if (i2 == 5) {
            if (this.buttonState != -1) {
                didPressButton(true, false);
            } else if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.delegate.needPlayMessage(this.currentMessageObject);
            } else {
                MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject);
            }
        } else if (i2 == 8) {
            int i3 = this.buttonState;
            if (i3 == -1 || (i3 == 1 && this.canStreamVideo && this.autoPlayingMedia)) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i3 != 2 && i3 != 0) {
            } else {
                didPressButton(true, false);
            }
        } else {
            int i4 = this.documentAttachType;
            if (i4 == 4) {
                int i5 = this.buttonState;
                if (i5 == -1 || ((z = this.drawVideoImageButton) && (this.autoPlayingMedia || (SharedConfig.streamMedia && this.canStreamVideo)))) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (z) {
                    didPressButton(true, true);
                } else if (i5 != 0 && i5 != 3) {
                } else {
                    didPressButton(true, false);
                }
            } else if (i2 == 4) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i4 == 1) {
                if (this.buttonState != -1) {
                    return;
                }
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i4 == 2) {
                if (this.buttonState != -1 || (tLRPC$WebPage = messageObject2.messageOwner.media.webpage) == null) {
                    return;
                }
                String str = tLRPC$WebPage.embed_url;
                if (str != null && str.length() != 0) {
                    this.delegate.needOpenWebView(this.currentMessageObject, tLRPC$WebPage.embed_url, tLRPC$WebPage.site_name, tLRPC$WebPage.description, tLRPC$WebPage.url, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height);
                } else {
                    Browser.openUrl(getContext(), tLRPC$WebPage.url);
                }
            } else if (this.hasInvoicePreview) {
                if (this.buttonState != -1) {
                    return;
                }
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (Build.VERSION.SDK_INT < 26 || (chatMessageCellDelegate = this.delegate) == null) {
            } else {
                if (i2 == 16) {
                    chatMessageCellDelegate.didLongPress(this, 0.0f, 0.0f);
                } else {
                    chatMessageCellDelegate.didPressOther(this, this.otherX, this.otherY);
                }
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        String secretTimeString;
        if (messageObject == null || !messageObject.needDrawBluredPreview() || (secretTimeString = messageObject.getSecretTimeString()) == null) {
            return;
        }
        int ceil = (int) Math.ceil(Theme.chat_infoPaint.measureText(secretTimeString));
        this.infoWidth = ceil;
        this.infoLayout = new StaticLayout(TextUtils.ellipsize(secretTimeString, Theme.chat_infoPaint, ceil, TextUtils.TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        invalidate();
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isPhotoDataChanged(org.telegram.messenger.MessageObject r25) {
        /*
            Method dump skipped, instructions count: 349
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isPhotoDataChanged(org.telegram.messenger.MessageObject):boolean");
    }

    public int getRepliesCount() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty()) {
            return this.currentMessagesGroup.messages.get(0).getRepliesCount();
        }
        return this.currentMessageObject.getRepliesCount();
    }

    private ArrayList<TLRPC$Peer> getRecentRepliers() {
        TLRPC$MessageReplies tLRPC$MessageReplies;
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty() && (tLRPC$MessageReplies = this.currentMessagesGroup.messages.get(0).messageOwner.replies) != null) {
            return tLRPC$MessageReplies.recent_repliers;
        }
        TLRPC$MessageReplies tLRPC$MessageReplies2 = this.currentMessageObject.messageOwner.replies;
        if (tLRPC$MessageReplies2 == null) {
            return null;
        }
        return tLRPC$MessageReplies2.recent_repliers;
    }

    private void updateCaptionSpoilers() {
        this.captionSpoilersPool.addAll(this.captionSpoilers);
        this.captionSpoilers.clear();
        if (this.captionLayout == null || getMessageObject().isSpoilersRevealed) {
            return;
        }
        SpoilerEffect.addSpoilers(this, this.captionLayout, this.captionSpoilersPool, this.captionSpoilers);
    }

    /* JADX WARN: Removed duplicated region for block: B:82:0x00ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isUserDataChanged() {
        /*
            Method dump skipped, instructions count: 269
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isUserDataChanged():boolean");
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    public int getNoSoundIconCenterX() {
        return this.noSoundCenterX;
    }

    public int getForwardNameCenterX() {
        float f;
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User != null && tLRPC$User.id == 0) {
            f = this.avatarImage.getCenterX();
        } else {
            f = this.forwardNameX + this.forwardNameCenterX;
        }
        return (int) f;
    }

    public int getChecksX() {
        return this.layoutWidth - AndroidUtilities.dp(SharedConfig.bubbleRadius >= 10 ? 27.3f : 25.3f);
    }

    public int getChecksY() {
        float f;
        int intrinsicHeight;
        if (this.currentMessageObject.shouldDrawWithoutBackground()) {
            f = this.drawTimeY;
            intrinsicHeight = getThemedDrawable("drawableMsgStickerCheck").getIntrinsicHeight();
        } else {
            f = this.drawTimeY;
            intrinsicHeight = Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight();
        }
        return (int) (f - intrinsicHeight);
    }

    public TLRPC$User getCurrentUser() {
        return this.currentUser;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopSpoilers);
        cancelShakeAnimation();
        if (this.animationRunning) {
            return;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onDetachedFromWindow();
        }
        if (this.pollCheckBox != null) {
            int i = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (i >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[i].onDetachedFromWindow();
                i++;
            }
        }
        this.attachedToWindow = false;
        this.radialProgress.onDetachedFromWindow();
        this.videoRadialProgress.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
        if (this.pollAvatarImages != null) {
            int i2 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
                if (i2 >= imageReceiverArr.length) {
                    break;
                }
                imageReceiverArr[i2].onDetachedFromWindow();
                i2++;
            }
        }
        if (this.commentAvatarImages != null) {
            int i3 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr2 = this.commentAvatarImages;
                if (i3 >= imageReceiverArr2.length) {
                    break;
                }
                imageReceiverArr2[i3].onDetachedFromWindow();
                i3++;
            }
        }
        this.replyImageReceiver.onDetachedFromWindow();
        this.locationImageReceiver.onDetachedFromWindow();
        this.photoImage.onDetachedFromWindow();
        if (this.addedForTest && this.currentUrl != null && this.currentWebFile != null) {
            ImageLoader.getInstance().removeTestWebFile(this.currentUrl);
            this.addedForTest = false;
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellDetached(this);
        }
        this.transitionParams.onDetach();
        if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            Theme.getCurrentAudiVisualizerDrawable().setParentView(null);
        }
        ValueAnimator valueAnimator = this.statusDrawableAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.statusDrawableAnimator.cancel();
        }
        this.reactionsLayoutInBubble.onDetachFromWindow();
        this.statusDrawableAnimationInProgress = false;
        Runnable runnable = this.unregisterFlagSecure;
        if (runnable == null) {
            return;
        }
        runnable.run();
        this.unregisterFlagSecure = null;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopSpoilers);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            messageObject.animateComments = false;
        }
        MessageObject messageObject2 = this.messageObjectToSet;
        if (messageObject2 != null) {
            messageObject2.animateComments = false;
            setMessageContent(messageObject2, this.groupedMessagesToSet, this.bottomNearToSet, this.topNearToSet);
            this.messageObjectToSet = null;
            this.groupedMessagesToSet = null;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onAttachedToWindow();
        }
        if (this.pollCheckBox != null) {
            int i = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (i >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[i].onAttachedToWindow();
                i++;
            }
        }
        this.attachedToWindow = true;
        float f = 0.0f;
        this.animationOffsetX = 0.0f;
        this.slidingOffsetX = 0.0f;
        this.checkBoxTranslation = 0;
        updateTranslation();
        this.radialProgress.onAttachedToWindow();
        this.videoRadialProgress.onAttachedToWindow();
        this.avatarImage.setParentView((View) getParent());
        this.avatarImage.onAttachedToWindow();
        if (this.pollAvatarImages != null) {
            int i2 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
                if (i2 >= imageReceiverArr.length) {
                    break;
                }
                imageReceiverArr[i2].onAttachedToWindow();
                i2++;
            }
        }
        if (this.commentAvatarImages != null) {
            int i3 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr2 = this.commentAvatarImages;
                if (i3 >= imageReceiverArr2.length) {
                    break;
                }
                imageReceiverArr2[i3].onAttachedToWindow();
                i3++;
            }
        }
        this.replyImageReceiver.onAttachedToWindow();
        this.locationImageReceiver.onAttachedToWindow();
        if (this.photoImage.onAttachedToWindow()) {
            if (this.drawPhotoImage) {
                updateButtonState(false, false, false);
            }
        } else {
            updateButtonState(false, false, false);
        }
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null && (this.isRoundVideo || messageObject3.isVideo())) {
            checkVideoPlayback(true, null);
        }
        int i4 = this.documentAttachType;
        if (i4 == 4 && this.autoPlayingMedia) {
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            this.animatingNoSoundPlaying = isPlayingMessage;
            this.animatingNoSoundProgress = isPlayingMessage ? 0.0f : 1.0f;
            this.animatingNoSound = 0;
        } else {
            this.animatingNoSoundPlaying = false;
            this.animatingNoSoundProgress = 0.0f;
            this.animatingDrawVideoImageButtonProgress = ((i4 == 4 || i4 == 2) && this.drawVideoSize) ? 1.0f : 0.0f;
        }
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellAttached(this);
        }
        if (this.documentAttachType == 5) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                f = 1.0f;
            }
            this.toSeekBarProgress = f;
        }
        this.reactionsLayoutInBubble.onAttachToWindow();
        updateFlagSecure();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(34:(3:3801|(4:2938|(1:(3:2940|2941|(2:3938|2943)(1:2944))(2:3937|2945))|2946|(1:2948))(1:2949)|2950)|(29:2955|2976|2977|(4:2979|(1:2981)(1:2982)|2983|(6:2985|(1:2987)|2988|(1:2990)(1:2991)|2992|(2:2994|(1:(4:3005|(1:3007)|3008|(1:3010)))(22:2998|(2:3000|(1:3002))|3003|3012|(3:3019|(1:3021)|3022)|3030|(2:3045|(2:3055|(1:3057)(1:3058)))(3:3034|(1:3043)(2:3040|(1:3042))|3044)|3059|(12:3064|3067|(1:3073)(1:3072)|3074|(1:3079)|3080|(2:3082|(1:3102)(6:3092|(3:3094|(1:3099)(1:3098)|3100)(1:3101)|3105|(3:3107|(3:3112|(1:3115)|3116)(1:3111)|3117)(2:3118|(3:3120|(1:3122)(2:3125|(3:3127|(1:3129)(1:3130)|(3:3148|(1:3150)(1:3151)|3152)(2:3138|(3:3143|(1:3145)(1:3146)|3147)(1:3142)))(1:3153))|3123)(3:3154|(13:3179|(1:3181)(2:3182|(1:3184)(1:3185))|3186|(4:3190|3193|(4:3260|(2:3262|(1:3266))(1:3268)|(1:3273)(1:3272)|3274)(2:(2:3237|(3:3239|(1:3241)(1:3242)|3243)(2:3244|(1:3258)(5:3250|(1:3252)|3253|(1:3255)(1:3256)|3257)))(4:3212|(1:3214)|(2:3229|(1:3235)(1:3234))(5:3221|(1:3223)|3224|(1:3226)(1:3227)|3228)|3236)|3259)|3267)|3192|3193|(1:3195)|3197|3260|(0)(0)|(1:3270)|3273|3274)(3:3158|(5:3168|(1:3172)|3173|(1:3175)(1:3176)|3177)(3:3163|(1:3165)(1:3166)|3167)|3178)|3275))|3124|3275))(1:3103)|3104|3105|(0)(0)|3124|3275)|3066|3067|(0)|3073|3074|(2:3077|3079)|3080|(0)(0)|3104|3105|(0)(0)|3124|3275))))|3011|3012|(1:3023)(4:3014|3019|(0)|3022)|3030|(0)|3045|(18:3047|3055|(0)(0)|3059|(1:3065)(14:3061|3064|3067|(0)|3073|3074|(0)|3080|(0)(0)|3104|3105|(0)(0)|3124|3275)|3066|3067|(0)|3073|3074|(0)|3080|(0)(0)|3104|3105|(0)(0)|3124|3275)|3049|3055|(0)(0)|3059|(0)(0)|3066|3067|(0)|3073|3074|(0)|3080|(0)(0)|3104|3105|(0)(0)|3124|3275)|2956|3797|2957|(29:2965|2966|2977|(0)|3011|3012|(0)(0)|3030|(0)|3045|(0)|3049|3055|(0)(0)|3059|(0)(0)|3066|3067|(0)|3073|3074|(0)|3080|(0)(0)|3104|3105|(0)(0)|3124|3275)|2974|2977|(0)|3011|3012|(0)(0)|3030|(0)|3045|(0)|3049|3055|(0)(0)|3059|(0)(0)|3066|3067|(0)|3073|3074|(0)|3080|(0)(0)|3104|3105|(0)(0)|3124|3275) */
    /* JADX WARN: Code restructure failed: missing block: B:2376:0x3671, code lost:
        if (r0 < (r73.timeWidth + org.telegram.messenger.AndroidUtilities.dp((r74.isOutOwner() ? 20 : 0) + 20))) goto L2377;
     */
    /* JADX WARN: Code restructure failed: missing block: B:2918:0x422b, code lost:
        if (r5.isSmall == false) goto L2919;
     */
    /* JADX WARN: Code restructure failed: missing block: B:2967:0x436a, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:2968:0x436b, code lost:
        r5 = r0;
        r0 = r12;
        r12 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3026:0x4496, code lost:
        if (r3 >= (r73.captionWidth + org.telegram.messenger.AndroidUtilities.dp(10.0f))) goto L3030;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3027:0x4498, code lost:
        r3 = r73.captionWidth + org.telegram.messenger.AndroidUtilities.dp(10.0f);
        r4 = org.telegram.messenger.AndroidUtilities.dp(8.0f) + r3;
        r73.backgroundWidth = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3028:0x44a8, code lost:
        if (r73.mediaBackground != false) goto L3030;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3029:0x44aa, code lost:
        r73.backgroundWidth = r4 + org.telegram.messenger.AndroidUtilities.dp(9.0f);
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x008a, code lost:
        if (r73.isPlayingRound != (org.telegram.messenger.MediaController.getInstance().isPlayingMessage(r73.currentMessageObject) && (r7 = r73.delegate) != null && !r7.keyboardIsOpened())) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:783:0x0ebd, code lost:
        if (r3 != 13) goto L789;
     */
    /* JADX WARN: Multi-variable search skipped. Vars limit reached: 6652 (expected less than 5000) */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1038:0x1417  */
    /* JADX WARN: Removed duplicated region for block: B:1045:0x1429  */
    /* JADX WARN: Removed duplicated region for block: B:1047:0x142c  */
    /* JADX WARN: Removed duplicated region for block: B:1240:0x191c  */
    /* JADX WARN: Removed duplicated region for block: B:1255:0x195c  */
    /* JADX WARN: Removed duplicated region for block: B:1256:0x1967  */
    /* JADX WARN: Removed duplicated region for block: B:1259:0x196f  */
    /* JADX WARN: Removed duplicated region for block: B:1270:0x1990  */
    /* JADX WARN: Removed duplicated region for block: B:1374:0x1b04  */
    /* JADX WARN: Removed duplicated region for block: B:1377:0x1b1c  */
    /* JADX WARN: Removed duplicated region for block: B:1527:0x2005  */
    /* JADX WARN: Removed duplicated region for block: B:1548:0x2074  */
    /* JADX WARN: Removed duplicated region for block: B:1579:0x21c5  */
    /* JADX WARN: Removed duplicated region for block: B:1670:0x2433  */
    /* JADX WARN: Removed duplicated region for block: B:1671:0x2436  */
    /* JADX WARN: Removed duplicated region for block: B:1674:0x2449  */
    /* JADX WARN: Removed duplicated region for block: B:1676:0x2453  */
    /* JADX WARN: Removed duplicated region for block: B:1706:0x2549  */
    /* JADX WARN: Removed duplicated region for block: B:1709:0x2556  */
    /* JADX WARN: Removed duplicated region for block: B:1710:0x255a  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x01f9  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x0206  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x020d  */
    /* JADX WARN: Removed duplicated region for block: B:2149:0x2fc3  */
    /* JADX WARN: Removed duplicated region for block: B:2150:0x2fc9  */
    /* JADX WARN: Removed duplicated region for block: B:2166:0x3007  */
    /* JADX WARN: Removed duplicated region for block: B:2298:0x32e0  */
    /* JADX WARN: Removed duplicated region for block: B:2313:0x330f  */
    /* JADX WARN: Removed duplicated region for block: B:2347:0x3520  */
    /* JADX WARN: Removed duplicated region for block: B:237:0x02f9  */
    /* JADX WARN: Removed duplicated region for block: B:2401:0x3740  */
    /* JADX WARN: Removed duplicated region for block: B:2412:0x3758  */
    /* JADX WARN: Removed duplicated region for block: B:2415:0x3767  */
    /* JADX WARN: Removed duplicated region for block: B:2417:0x3778  */
    /* JADX WARN: Removed duplicated region for block: B:2436:0x37ca  */
    /* JADX WARN: Removed duplicated region for block: B:2440:0x37fb  */
    /* JADX WARN: Removed duplicated region for block: B:2533:0x3a63  */
    /* JADX WARN: Removed duplicated region for block: B:2541:0x3a99  */
    /* JADX WARN: Removed duplicated region for block: B:2565:0x3b4f  */
    /* JADX WARN: Removed duplicated region for block: B:2566:0x3b72  */
    /* JADX WARN: Removed duplicated region for block: B:2619:0x3c72  */
    /* JADX WARN: Removed duplicated region for block: B:2622:0x3c7c  */
    /* JADX WARN: Removed duplicated region for block: B:2627:0x3c87  */
    /* JADX WARN: Removed duplicated region for block: B:2657:0x3cf1  */
    /* JADX WARN: Removed duplicated region for block: B:2658:0x3cf6  */
    /* JADX WARN: Removed duplicated region for block: B:2683:0x3d41  */
    /* JADX WARN: Removed duplicated region for block: B:2700:0x3d94 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:2704:0x3da2  */
    /* JADX WARN: Removed duplicated region for block: B:2709:0x3dba  */
    /* JADX WARN: Removed duplicated region for block: B:2716:0x3dd3  */
    /* JADX WARN: Removed duplicated region for block: B:2723:0x3e15  */
    /* JADX WARN: Removed duplicated region for block: B:2726:0x3e21  */
    /* JADX WARN: Removed duplicated region for block: B:2729:0x3e4e  */
    /* JADX WARN: Removed duplicated region for block: B:272:0x0352  */
    /* JADX WARN: Removed duplicated region for block: B:2730:0x3e51  */
    /* JADX WARN: Removed duplicated region for block: B:2733:0x3e59  */
    /* JADX WARN: Removed duplicated region for block: B:2734:0x3e5c  */
    /* JADX WARN: Removed duplicated region for block: B:2737:0x3e66  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x0357  */
    /* JADX WARN: Removed duplicated region for block: B:2740:0x3e6d  */
    /* JADX WARN: Removed duplicated region for block: B:2741:0x3e7f  */
    /* JADX WARN: Removed duplicated region for block: B:2751:0x3ea9  */
    /* JADX WARN: Removed duplicated region for block: B:2907:0x41f5  */
    /* JADX WARN: Removed duplicated region for block: B:2924:0x4245  */
    /* JADX WARN: Removed duplicated region for block: B:2979:0x4388  */
    /* JADX WARN: Removed duplicated region for block: B:3014:0x446c  */
    /* JADX WARN: Removed duplicated region for block: B:3021:0x4481  */
    /* JADX WARN: Removed duplicated region for block: B:3023:0x448c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3032:0x44b7 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3047:0x455a  */
    /* JADX WARN: Removed duplicated region for block: B:3057:0x4572  */
    /* JADX WARN: Removed duplicated region for block: B:3058:0x459d  */
    /* JADX WARN: Removed duplicated region for block: B:305:0x03f3  */
    /* JADX WARN: Removed duplicated region for block: B:3061:0x45b7  */
    /* JADX WARN: Removed duplicated region for block: B:3065:0x45c1 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3069:0x45c9 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3076:0x45d8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3082:0x45e4  */
    /* JADX WARN: Removed duplicated region for block: B:3103:0x4626  */
    /* JADX WARN: Removed duplicated region for block: B:3107:0x462e  */
    /* JADX WARN: Removed duplicated region for block: B:3118:0x46e6  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x0426  */
    /* JADX WARN: Removed duplicated region for block: B:3262:0x4a57  */
    /* JADX WARN: Removed duplicated region for block: B:3268:0x4a9a  */
    /* JADX WARN: Removed duplicated region for block: B:3278:0x4ae5  */
    /* JADX WARN: Removed duplicated region for block: B:3289:0x4b07  */
    /* JADX WARN: Removed duplicated region for block: B:3298:0x4b31  */
    /* JADX WARN: Removed duplicated region for block: B:3305:0x4b52  */
    /* JADX WARN: Removed duplicated region for block: B:3308:0x4b69  */
    /* JADX WARN: Removed duplicated region for block: B:3320:0x4ba4  */
    /* JADX WARN: Removed duplicated region for block: B:3323:0x4bb1  */
    /* JADX WARN: Removed duplicated region for block: B:3324:0x4bc1  */
    /* JADX WARN: Removed duplicated region for block: B:3327:0x4bd4  */
    /* JADX WARN: Removed duplicated region for block: B:3356:0x4c49  */
    /* JADX WARN: Removed duplicated region for block: B:3366:0x4c67  */
    /* JADX WARN: Removed duplicated region for block: B:337:0x04f1  */
    /* JADX WARN: Removed duplicated region for block: B:3454:0x4e3c  */
    /* JADX WARN: Removed duplicated region for block: B:3460:0x4e4b  */
    /* JADX WARN: Removed duplicated region for block: B:3466:0x4e67  */
    /* JADX WARN: Removed duplicated region for block: B:3508:0x4f92  */
    /* JADX WARN: Removed duplicated region for block: B:3522:0x4fcf  */
    /* JADX WARN: Removed duplicated region for block: B:3523:0x4fdd  */
    /* JADX WARN: Removed duplicated region for block: B:3526:0x4fe2  */
    /* JADX WARN: Removed duplicated region for block: B:3530:0x4fee  */
    /* JADX WARN: Removed duplicated region for block: B:354:0x054a  */
    /* JADX WARN: Removed duplicated region for block: B:355:0x055b  */
    /* JADX WARN: Removed duplicated region for block: B:357:0x0569  */
    /* JADX WARN: Removed duplicated region for block: B:3622:0x528f  */
    /* JADX WARN: Removed duplicated region for block: B:3629:0x52ac  */
    /* JADX WARN: Removed duplicated region for block: B:3633:0x52bc  */
    /* JADX WARN: Removed duplicated region for block: B:3634:0x52c6  */
    /* JADX WARN: Removed duplicated region for block: B:3645:0x52e5  */
    /* JADX WARN: Removed duplicated region for block: B:3650:0x5304  */
    /* JADX WARN: Removed duplicated region for block: B:3653:0x531b  */
    /* JADX WARN: Removed duplicated region for block: B:3656:0x5326  */
    /* JADX WARN: Removed duplicated region for block: B:3663:0x5359  */
    /* JADX WARN: Removed duplicated region for block: B:3665:0x5361  */
    /* JADX WARN: Removed duplicated region for block: B:3715:0x53dd  */
    /* JADX WARN: Removed duplicated region for block: B:3721:0x53ec  */
    /* JADX WARN: Removed duplicated region for block: B:3733:0x540e  */
    /* JADX WARN: Removed duplicated region for block: B:3741:0x5436  */
    /* JADX WARN: Removed duplicated region for block: B:3754:0x547d  */
    /* JADX WARN: Removed duplicated region for block: B:3765:0x54a5  */
    /* JADX WARN: Removed duplicated region for block: B:3771:0x54b9  */
    /* JADX WARN: Removed duplicated region for block: B:3780:0x54ee  */
    /* JADX WARN: Removed duplicated region for block: B:3791:0x4254 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3793:0x4d6d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3799:0x0faa A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3803:0x203e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3848:0x043b A[EDGE_INSN: B:3848:0x043b->B:317:0x043b ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:450:0x0766  */
    /* JADX WARN: Removed duplicated region for block: B:465:0x07d3  */
    /* JADX WARN: Removed duplicated region for block: B:662:0x0c68  */
    /* JADX WARN: Removed duplicated region for block: B:668:0x0c77  */
    /* JADX WARN: Removed duplicated region for block: B:676:0x0cbe  */
    /* JADX WARN: Removed duplicated region for block: B:683:0x0cf9  */
    /* JADX WARN: Removed duplicated region for block: B:689:0x0d1b  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x00d3  */
    /* JADX WARN: Removed duplicated region for block: B:697:0x0d43  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:700:0x0d6c  */
    /* JADX WARN: Removed duplicated region for block: B:705:0x0d7b  */
    /* JADX WARN: Removed duplicated region for block: B:712:0x0d9d  */
    /* JADX WARN: Removed duplicated region for block: B:715:0x0db5  */
    /* JADX WARN: Removed duplicated region for block: B:720:0x0dd1  */
    /* JADX WARN: Removed duplicated region for block: B:723:0x0de6  */
    /* JADX WARN: Removed duplicated region for block: B:774:0x0ea8  */
    /* JADX WARN: Removed duplicated region for block: B:788:0x0ec8  */
    /* JADX WARN: Removed duplicated region for block: B:791:0x0ecd A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:797:0x0eec  */
    /* JADX WARN: Removed duplicated region for block: B:811:0x0f55  */
    /* JADX WARN: Removed duplicated region for block: B:812:0x0f5f  */
    /* JADX WARN: Removed duplicated region for block: B:824:0x0f92  */
    /* JADX WARN: Removed duplicated region for block: B:825:0x0f94  */
    /* JADX WARN: Removed duplicated region for block: B:828:0x0fa2 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:890:0x110a  */
    /* JADX WARN: Removed duplicated region for block: B:893:0x111c  */
    /* JADX WARN: Removed duplicated region for block: B:943:0x1229  */
    /* JADX WARN: Removed duplicated region for block: B:945:0x1230 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:973:0x12ea  */
    /* JADX WARN: Type inference failed for: r0v187, types: [android.text.StaticLayout$Builder] */
    /* JADX WARN: Type inference failed for: r14v12 */
    /* JADX WARN: Type inference failed for: r14v13 */
    /* JADX WARN: Type inference failed for: r14v173 */
    /* JADX WARN: Type inference failed for: r14v174 */
    /* JADX WARN: Type inference failed for: r14v2, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r3v275, types: [org.telegram.tgnet.TLRPC$InputStickerSet] */
    /* JADX WARN: Type inference failed for: r6v201 */
    /* JADX WARN: Type inference failed for: r6v204 */
    /* JADX WARN: Type inference failed for: r6v45, types: [android.graphics.drawable.BitmapDrawable, org.telegram.tgnet.TLRPC$PhotoSize] */
    /* JADX WARN: Type inference failed for: r8v16 */
    /* JADX WARN: Type inference failed for: r8v330 */
    /* JADX WARN: Type inference failed for: r8v8, types: [org.telegram.ui.Cells.ChatMessageCell$1] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageContent(org.telegram.messenger.MessageObject r74, org.telegram.messenger.MessageObject.GroupedMessages r75, boolean r76, boolean r77) {
        /*
            Method dump skipped, instructions count: 21775
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    public static /* synthetic */ int lambda$setMessageContent$5(PollButton pollButton, PollButton pollButton2) {
        if (pollButton.decimal > pollButton2.decimal) {
            return -1;
        }
        if (pollButton.decimal < pollButton2.decimal) {
            return 1;
        }
        if (pollButton.decimal != pollButton2.decimal) {
            return 0;
        }
        if (pollButton.percent > pollButton2.percent) {
            return 1;
        }
        return pollButton.percent < pollButton2.percent ? -1 : 0;
    }

    private void updateFlagSecure() {
        Runnable runnable;
        TLRPC$Message tLRPC$Message;
        MessageObject messageObject = this.currentMessageObject;
        boolean z = (messageObject == null || (tLRPC$Message = messageObject.messageOwner) == null || !tLRPC$Message.noforwards) ? false : true;
        Activity findActivity = AndroidUtilities.findActivity(getContext());
        if (z && this.unregisterFlagSecure == null && findActivity != null) {
            this.unregisterFlagSecure = AndroidUtilities.registerFlagSecure(findActivity.getWindow());
        } else if (z || (runnable = this.unregisterFlagSecure) == null) {
        } else {
            runnable.run();
            this.unregisterFlagSecure = null;
        }
    }

    public void checkVideoPlayback(boolean z, Bitmap bitmap) {
        if (this.currentMessageObject.isVideo()) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                return;
            }
            this.photoImage.setAllowStartAnimation(true);
            this.photoImage.startAnimation();
            return;
        }
        if (z) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            z = playingMessageObject == null || !playingMessageObject.isRoundVideo();
        }
        this.photoImage.setAllowStartAnimation(z);
        if (bitmap != null) {
            this.photoImage.startCrossfadeFromStaticThumb(bitmap);
        }
        if (z) {
            this.photoImage.startAnimation();
        } else {
            this.photoImage.stopAnimation();
        }
    }

    private static boolean spanSupportsLongPress(CharacterStyle characterStyle) {
        return (characterStyle instanceof URLSpanMono) || (characterStyle instanceof URLSpan);
    }

    @Override // org.telegram.ui.Cells.BaseCell
    protected boolean onLongPress() {
        int i;
        int i2;
        boolean z = false;
        if (this.isRoundVideo && this.isPlayingRound && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && ((this.lastTouchX - this.photoImage.getCenterX()) * (this.lastTouchX - this.photoImage.getCenterX())) + ((this.lastTouchY - this.photoImage.getCenterY()) * (this.lastTouchY - this.photoImage.getCenterY())) < (this.photoImage.getImageWidth() / 2.0f) * (this.photoImage.getImageWidth() / 2.0f) && (this.lastTouchX > this.photoImage.getCenterX() + (this.photoImage.getImageWidth() / 4.0f) || this.lastTouchX < this.photoImage.getCenterX() - (this.photoImage.getImageWidth() / 4.0f))) {
            boolean z2 = this.lastTouchX > this.photoImage.getCenterX();
            if (this.videoPlayerRewinder == null) {
                this.videoForwardDrawable = new VideoForwardDrawable(true);
                this.videoPlayerRewinder = new VideoPlayerRewinder() { // from class: org.telegram.ui.Cells.ChatMessageCell.4
                    @Override // org.telegram.messenger.video.VideoPlayerRewinder
                    protected void onRewindCanceled() {
                        ChatMessageCell.this.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                        ChatMessageCell.this.videoForwardDrawable.setShowing(false);
                    }

                    @Override // org.telegram.messenger.video.VideoPlayerRewinder
                    protected void updateRewindProgressUi(long j, float f, boolean z3) {
                        ChatMessageCell.this.videoForwardDrawable.setTime(Math.abs(j));
                        if (z3) {
                            ChatMessageCell.this.currentMessageObject.audioProgress = f;
                            ChatMessageCell.this.updatePlayingMessageProgress();
                        }
                    }

                    @Override // org.telegram.messenger.video.VideoPlayerRewinder
                    protected void onRewindStart(boolean z3) {
                        ChatMessageCell.this.videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() { // from class: org.telegram.ui.Cells.ChatMessageCell.4.1
                            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                            public void onAnimationEnd() {
                            }

                            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                            public void invalidate() {
                                ChatMessageCell.this.invalidate();
                            }
                        });
                        ChatMessageCell.this.videoForwardDrawable.setOneShootAnimation(false);
                        ChatMessageCell.this.videoForwardDrawable.setLeftSide(!z3);
                        ChatMessageCell.this.videoForwardDrawable.setShowing(true);
                        ChatMessageCell.this.invalidate();
                    }
                };
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            this.videoPlayerRewinder.startRewind(MediaController.getInstance().getVideoPlayer(), z2, MediaController.getInstance().getPlaybackSpeed(false));
            return false;
        }
        LinkSpanDrawable linkSpanDrawable = this.pressedLink;
        if (linkSpanDrawable != null) {
            if (linkSpanDrawable.getSpan() instanceof URLSpanMono) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            } else if (this.pressedLink.getSpan() instanceof URLSpanNoUnderline) {
                URLSpanNoUnderline uRLSpanNoUnderline = (URLSpanNoUnderline) this.pressedLink.getSpan();
                if (ChatActivity.isClickableLink(uRLSpanNoUnderline.getURL()) || uRLSpanNoUnderline.getURL().startsWith("/")) {
                    this.hadLongPress = true;
                    this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                    return true;
                }
            } else if (this.pressedLink.getSpan() instanceof URLSpan) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            }
        }
        resetPressedLink(-1);
        if (this.buttonPressed != 0 || this.miniButtonPressed != 0 || this.videoButtonPressed != 0 || this.pressedBotButton != -1) {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.videoButtonPressed = 0;
            this.pressedBotButton = -1;
            invalidate();
        }
        this.linkPreviewPressed = false;
        this.sideButtonPressed = false;
        this.imagePressed = false;
        this.timePressed = false;
        this.gamePreviewPressed = false;
        if (this.pressedVoteButton != -1 || this.pollHintPressed || this.psaHintPressed || this.instantPressed || this.otherPressed || this.commentButtonPressed) {
            this.commentButtonPressed = false;
            this.instantButtonPressed = false;
            this.instantPressed = false;
            this.pressedVoteButton = -1;
            this.pollHintPressed = false;
            this.psaHintPressed = false;
            this.otherPressed = false;
            if (Build.VERSION.SDK_INT >= 21) {
                int i3 = 0;
                while (true) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (i3 >= drawableArr.length) {
                        break;
                    }
                    if (drawableArr[i3] != null) {
                        drawableArr[i3].setState(StateSet.NOTHING);
                    }
                    i3++;
                }
            }
            invalidate();
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null) {
            if (this.avatarPressed) {
                TLRPC$User tLRPC$User = this.currentUser;
                if (tLRPC$User != null) {
                    if (tLRPC$User.id != 0) {
                        z = chatMessageCellDelegate.didLongPressUserAvatar(this, tLRPC$User, this.lastTouchX, this.lastTouchY);
                    }
                } else {
                    TLRPC$Chat tLRPC$Chat = this.currentChat;
                    if (tLRPC$Chat != null) {
                        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
                        if (tLRPC$MessageFwdHeader != null) {
                            if ((tLRPC$MessageFwdHeader.flags & 16) != 0) {
                                i2 = tLRPC$MessageFwdHeader.saved_from_msg_id;
                            } else {
                                i2 = tLRPC$MessageFwdHeader.channel_post;
                            }
                            i = i2;
                        } else {
                            i = 0;
                        }
                        z = chatMessageCellDelegate.didLongPressChannelAvatar(this, tLRPC$Chat, i, this.lastTouchX, this.lastTouchY);
                    }
                }
            }
            if (!z) {
                this.delegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
            }
        }
        return true;
    }

    public void showHintButton(boolean z, boolean z2, int i) {
        float f = 1.0f;
        if (i == -1 || i == 0) {
            if (this.hintButtonVisible == z) {
                return;
            }
            this.hintButtonVisible = z;
            if (!z2) {
                this.hintButtonProgress = z ? 1.0f : 0.0f;
            } else {
                invalidate();
            }
        }
        if ((i == -1 || i == 1) && this.psaButtonVisible != z) {
            this.psaButtonVisible = z;
            if (!z2) {
                if (!z) {
                    f = 0.0f;
                }
                this.psaButtonProgress = f;
                return;
            }
            setInvalidatesParent(true);
            invalidate();
        }
    }

    public void setCheckPressed(boolean z, boolean z2) {
        this.isCheckPressed = z;
        this.isPressed = z2;
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    public void setInvalidateSpoilersParent(boolean z) {
        this.invalidateSpoilersParent = z;
    }

    public void setInvalidatesParent(boolean z) {
        this.invalidatesParent = z;
    }

    @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
    public void invalidate() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate();
        if ((this.invalidatesParent || (this.currentMessagesGroup != null && !this.links.isEmpty())) && getParent() != null) {
            View view = (View) getParent();
            if (view.getParent() != null) {
                view.invalidate();
                ((View) view.getParent()).invalidate();
            }
        }
        if (!this.isBlurred || (chatMessageCellDelegate = this.delegate) == null) {
            return;
        }
        chatMessageCellDelegate.invalidateBlur();
    }

    @Override // android.view.View
    public void invalidate(int i, int i2, int i3, int i4) {
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate(i, i2, i3, i4);
        if (!this.invalidatesParent || getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate(((int) getX()) + i, ((int) getY()) + i2, ((int) getX()) + i3, ((int) getY()) + i4);
    }

    public boolean isHighlightedAnimated() {
        return this.isHighlightedAnimated;
    }

    public void setHighlightedAnimated() {
        this.isHighlightedAnimated = true;
        this.highlightProgress = 1000;
        this.lastHighlightProgressTime = System.currentTimeMillis();
        invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    public void setHighlighted(boolean z) {
        if (this.isHighlighted == z) {
            return;
        }
        this.isHighlighted = z;
        if (!z) {
            this.lastHighlightProgressTime = System.currentTimeMillis();
            this.isHighlightedAnimated = true;
            this.highlightProgress = 300;
        } else {
            this.isHighlightedAnimated = false;
            this.highlightProgress = 0;
        }
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
        if (getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        super.setPressed(z);
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    private void updateRadialProgressBackground() {
        if (this.drawRadialCheckBackground) {
            return;
        }
        boolean z = true;
        boolean z2 = (this.isHighlighted || this.isPressed || isPressed()) && (!this.drawPhotoImage || !this.photoImage.hasBitmapImage());
        this.radialProgress.setPressed(z2 || this.buttonPressed != 0, false);
        if (this.hasMiniProgress != 0) {
            this.radialProgress.setPressed(z2 || this.miniButtonPressed != 0, true);
        }
        RadialProgress2 radialProgress2 = this.videoRadialProgress;
        if (!z2 && this.videoButtonPressed == 0) {
            z = false;
        }
        radialProgress2.setPressed(z, false);
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
        updatePlayingMessageProgress();
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarContinuousDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        messageObject.audioProgressSec = (int) (messageObject.getDuration() * f);
        updatePlayingMessageProgress();
    }

    public boolean isAnimatingPollAnswer() {
        return this.animatePollAnswerAlpha;
    }

    private void updateWaveform() {
        TLRPC$Message tLRPC$Message;
        if (this.currentMessageObject == null || this.documentAttachType != 3) {
            return;
        }
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= this.documentAttach.attributes.size()) {
                break;
            }
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.documentAttach.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                byte[] bArr = tLRPC$DocumentAttribute.waveform;
                if (bArr == null || bArr.length == 0) {
                    MediaController.getInstance().generateWaveform(this.currentMessageObject);
                }
                byte[] bArr2 = tLRPC$DocumentAttribute.waveform;
                this.useSeekBarWaveform = bArr2 != null;
                this.seekBarWaveform.setWaveform(bArr2);
            } else {
                i++;
            }
        }
        if (this.currentMessageObject.isVoice() && this.useSeekBarWaveform && (tLRPC$Message = this.currentMessageObject.messageOwner) != null && !(tLRPC$Message.media instanceof TLRPC$TL_messageMediaWebPage) && UserConfig.getInstance(this.currentAccount).isPremium()) {
            z = true;
        }
        this.useTranscribeButton = z;
        updateSeekBarWaveformWidth();
    }

    private void updateSeekBarWaveformWidth() {
        if (this.seekBarWaveform != null) {
            int dp = (-AndroidUtilities.dp((this.hasLinkPreview ? 10 : 0) + 92)) - AndroidUtilities.dp(this.useTranscribeButton ? 34.0f : 0.0f);
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.animateBackgroundBoundsInner && this.documentAttachType == 3) {
                int i = this.backgroundWidth;
                this.seekBarWaveform.setSize(((int) ((i - transitionParams.deltaLeft) + transitionParams.deltaRight)) + dp, AndroidUtilities.dp(30.0f), i + dp, ((int) ((i - transitionParams.toDeltaLeft) + transitionParams.toDeltaRight)) + dp);
                return;
            }
            this.seekBarWaveform.setSize(this.backgroundWidth + dp, AndroidUtilities.dp(30.0f));
        }
    }

    private int createDocumentLayout(int i, MessageObject messageObject) {
        int i2;
        int i3;
        int i4 = i;
        if (messageObject.type == 0) {
            this.documentAttach = messageObject.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.getDocument();
        }
        TLRPC$Document tLRPC$Document = this.documentAttach;
        int i5 = 0;
        if (tLRPC$Document == null) {
            return 0;
        }
        if (MessageObject.isVoiceDocument(tLRPC$Document)) {
            this.documentAttachType = 3;
            int i6 = 0;
            while (true) {
                if (i6 >= this.documentAttach.attributes.size()) {
                    i3 = 0;
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.documentAttach.attributes.get(i6);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    i3 = tLRPC$DocumentAttribute.duration;
                    break;
                }
                i6++;
            }
            this.widthBeforeNewTimeLine = (i4 - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil(Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = i4 - AndroidUtilities.dp(18.0f);
            measureTime(messageObject);
            int dp = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(i4, dp + ((int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatLongDuration(i3)))));
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return 0;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = 4;
            if (!messageObject.needDrawBluredPreview()) {
                updatePlayingMessageProgress();
                String format = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(format));
                this.docTitleLayout = new StaticLayout(format, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            int dp2 = i4 - AndroidUtilities.dp(92.0f);
            if (dp2 < 0) {
                dp2 = AndroidUtilities.dp(100.0f);
            }
            int i7 = dp2;
            StaticLayout staticLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, i7 - AndroidUtilities.dp(12.0f), TextUtils.TruncateAt.END), Theme.chat_audioTitlePaint, i7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.songLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil(this.songLayout.getLineLeft(0)));
            }
            StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, i7, TextUtils.TruncateAt.END), Theme.chat_audioPerformerPaint, i7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.performerLayout = staticLayout2;
            if (staticLayout2.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil(this.performerLayout.getLineLeft(0)));
            }
            int i8 = 0;
            while (true) {
                if (i8 >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute2 = this.documentAttach.attributes.get(i8);
                if (tLRPC$DocumentAttribute2 instanceof TLRPC$TL_documentAttributeAudio) {
                    i5 = tLRPC$DocumentAttribute2.duration;
                    break;
                }
                i8++;
            }
            int ceil = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatShortDuration(i5, i5)));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - ceil;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return ceil;
        } else if (MessageObject.isGifDocument(this.documentAttach, messageObject.hasValidGroupId())) {
            this.documentAttachType = 2;
            if (!messageObject.needDrawBluredPreview()) {
                String string = LocaleController.getString("AttachGif", R.string.AttachGif);
                this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(string));
                this.infoLayout = new StaticLayout(string, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                String format2 = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(format2));
                this.docTitleLayout = new StaticLayout(format2, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else {
            String str = this.documentAttach.mime_type;
            boolean z = (str != null && (str.toLowerCase().startsWith("image/") || this.documentAttach.mime_type.toLowerCase().startsWith("video/mp4"))) || MessageObject.isDocumentHasThumb(this.documentAttach);
            this.drawPhotoImage = z;
            if (!z) {
                i4 += AndroidUtilities.dp(30.0f);
            }
            this.documentAttachType = 1;
            String documentFileName = FileLoader.getDocumentFileName(this.documentAttach);
            if (documentFileName.length() == 0) {
                documentFileName = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            }
            StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(documentFileName, Theme.chat_docNamePaint, i4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.MIDDLE, i4, 2, false);
            this.docTitleLayout = createStaticLayout;
            this.docTitleOffsetX = Integer.MIN_VALUE;
            if (createStaticLayout != null && createStaticLayout.getLineCount() > 0) {
                int i9 = 0;
                while (i5 < this.docTitleLayout.getLineCount()) {
                    i9 = Math.max(i9, (int) Math.ceil(this.docTitleLayout.getLineWidth(i5)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil(-this.docTitleLayout.getLineLeft(i5)));
                    i5++;
                }
                i2 = Math.min(i4, i9);
            } else {
                this.docTitleOffsetX = 0;
                i2 = i4;
            }
            int dp3 = i4 - AndroidUtilities.dp(30.0f);
            TextPaint textPaint = Theme.chat_infoPaint;
            int min = Math.min(dp3, (int) Math.ceil(textPaint.measureText("000.0 mm / " + AndroidUtilities.formatFileSize(this.documentAttach.size))));
            this.infoWidth = min;
            CharSequence ellipsize = TextUtils.ellipsize(AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach), Theme.chat_infoPaint, (float) min, TextUtils.TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(ellipsize, Theme.chat_infoPaint, this.infoWidth + AndroidUtilities.dp(6.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320);
                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 40);
                if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                    this.currentPhotoObject = null;
                }
                TLRPC$PhotoSize tLRPC$PhotoSize = this.currentPhotoObject;
                if (tLRPC$PhotoSize == null || tLRPC$PhotoSize == this.currentPhotoObjectThumb) {
                    this.currentPhotoObject = null;
                    this.photoImage.setNeedsQualityThumb(true);
                    this.photoImage.setShouldGenerateQualityThumb(true);
                } else {
                    BitmapDrawable bitmapDrawable = this.currentMessageObject.strippedThumb;
                    if (bitmapDrawable != null) {
                        this.currentPhotoObjectThumb = null;
                        this.currentPhotoObjectThumbStripped = bitmapDrawable;
                    }
                }
                this.currentPhotoFilter = "86_86_b";
                this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, messageObject.photoThumbsObject), "86_86", ImageLocation.getForObject(this.currentPhotoObjectThumb, messageObject.photoThumbsObject), this.currentPhotoFilter, this.currentPhotoObjectThumbStripped, 0L, null, messageObject, 1);
            }
            return i2;
        }
    }

    private void calcBackgroundWidth(int i, int i2, int i3) {
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        boolean z = reactionsLayoutInBubble.isEmpty;
        int i4 = (z || reactionsLayoutInBubble.isSmall) ? this.currentMessageObject.lastLineWidth : reactionsLayoutInBubble.lastLineX;
        boolean z2 = false;
        if (!z && !reactionsLayoutInBubble.isSmall) {
            if (i - i4 < i2 || this.currentMessageObject.hasRtl) {
                z2 = true;
            }
            if (this.hasInvoicePreview) {
                this.totalHeight += AndroidUtilities.dp(14.0f);
            }
        } else if (this.hasLinkPreview || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview || i - i4 < i2 || this.currentMessageObject.hasRtl) {
            z2 = true;
        }
        if (z2) {
            this.totalHeight += AndroidUtilities.dp(14.0f);
            this.hasNewLineForTime = true;
            int max = Math.max(i3, i4) + AndroidUtilities.dp(31.0f);
            this.backgroundWidth = max;
            this.backgroundWidth = Math.max(max, (this.currentMessageObject.isOutOwner() ? this.timeWidth + AndroidUtilities.dp(17.0f) : this.timeWidth) + AndroidUtilities.dp(31.0f));
            return;
        }
        int extraTextX = (i3 - getExtraTextX()) - i4;
        if (extraTextX >= 0 && extraTextX <= i2) {
            this.backgroundWidth = ((i3 + i2) - extraTextX) + AndroidUtilities.dp(31.0f);
        } else {
            this.backgroundWidth = Math.max(i3, i4 + i2) + AndroidUtilities.dp(31.0f);
        }
    }

    public void setHighlightedText(String str) {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        if (messageObject == null || messageObject.messageOwner.message == null || TextUtils.isEmpty(str)) {
            if (this.urlPathSelection.isEmpty()) {
                return;
            }
            this.linkSelectionBlockNum = -1;
            resetUrlPaths();
            invalidate();
            return;
        }
        String lowerCase = str.toLowerCase();
        String lowerCase2 = messageObject.messageOwner.message.toLowerCase();
        int length = lowerCase2.length();
        int i = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < length; i3++) {
            int min = Math.min(lowerCase.length(), length - i3);
            int i4 = 0;
            for (int i5 = 0; i5 < min; i5++) {
                boolean z = lowerCase2.charAt(i3 + i5) == lowerCase.charAt(i5);
                if (z) {
                    if (i4 != 0 || i3 == 0 || " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(lowerCase2.charAt(i3 - 1)) >= 0) {
                        i4++;
                    } else {
                        z = false;
                    }
                }
                if (!z || i5 == min - 1) {
                    if (i4 > 0 && i4 > i2) {
                        i = i3;
                        i2 = i4;
                    }
                }
            }
        }
        if (i == -1) {
            if (this.urlPathSelection.isEmpty()) {
                return;
            }
            this.linkSelectionBlockNum = -1;
            resetUrlPaths();
            invalidate();
            return;
        }
        int length2 = lowerCase2.length();
        for (int i6 = i + i2; i6 < length2 && " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(lowerCase2.charAt(i6)) < 0; i6++) {
            i2++;
        }
        int i7 = i + i2;
        if (this.captionLayout != null && !TextUtils.isEmpty(messageObject.caption)) {
            resetUrlPaths();
            try {
                LinkPath obtainNewUrlPath = obtainNewUrlPath();
                obtainNewUrlPath.setCurrentLayout(this.captionLayout, i, 0.0f);
                this.captionLayout.getSelectionPath(i, i7, obtainNewUrlPath);
            } catch (Exception e) {
                FileLog.e(e);
            }
            invalidate();
        } else if (messageObject.textLayoutBlocks != null) {
            for (int i8 = 0; i8 < messageObject.textLayoutBlocks.size(); i8++) {
                MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i8);
                if (i >= textLayoutBlock.charactersOffset && i < textLayoutBlock.charactersEnd) {
                    this.linkSelectionBlockNum = i8;
                    resetUrlPaths();
                    try {
                        LinkPath obtainNewUrlPath2 = obtainNewUrlPath();
                        obtainNewUrlPath2.setCurrentLayout(textLayoutBlock.textLayout, i, 0.0f);
                        textLayoutBlock.textLayout.getSelectionPath(i, i7, obtainNewUrlPath2);
                        if (i7 >= textLayoutBlock.charactersOffset + i2) {
                            for (int i9 = i8 + 1; i9 < messageObject.textLayoutBlocks.size(); i9++) {
                                MessageObject.TextLayoutBlock textLayoutBlock2 = messageObject.textLayoutBlocks.get(i9);
                                int i10 = textLayoutBlock2.charactersEnd - textLayoutBlock2.charactersOffset;
                                LinkPath obtainNewUrlPath3 = obtainNewUrlPath();
                                obtainNewUrlPath3.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.height);
                                textLayoutBlock2.textLayout.getSelectionPath(0, i7 - textLayoutBlock2.charactersOffset, obtainNewUrlPath3);
                                if (i7 < (textLayoutBlock.charactersOffset + i10) - 1) {
                                    break;
                                }
                            }
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    invalidate();
                    return;
                }
            }
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        if (!super.verifyDrawable(drawable)) {
            Drawable[] drawableArr = this.selectorDrawable;
            if (drawable != drawableArr[0] && drawable != drawableArr[1]) {
                return false;
            }
        }
        return true;
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (this.currentMessagesGroup != null) {
            invalidateWithParent();
        }
    }

    private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
        return this.currentMessageObject.messageOwner.media.period % 60 == 0 ? Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period : Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > messageObject.messageOwner.media.period + (-5);
    }

    public void checkLocationExpired() {
        boolean isCurrentLocationTimeExpired;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (isCurrentLocationTimeExpired = isCurrentLocationTimeExpired(messageObject)) == this.locationExpired) {
            return;
        }
        this.locationExpired = isCurrentLocationTimeExpired;
        if (!isCurrentLocationTimeExpired) {
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
            this.scheduledInvalidate = true;
            int dp = this.backgroundWidth - AndroidUtilities.dp(91.0f);
            this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, dp, TextUtils.TruncateAt.END), Theme.chat_locationTitlePaint, dp, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        this.currentMessageObject = null;
        setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
    }

    public void setIsUpdating(boolean z) {
        this.isUpdating = true;
    }

    public void setMessageObject(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean z, boolean z2) {
        if (this.attachedToWindow) {
            setMessageContent(messageObject, groupedMessages, z, z2);
            return;
        }
        this.messageObjectToSet = messageObject;
        this.groupedMessagesToSet = groupedMessages;
        this.bottomNearToSet = z;
        this.topNearToSet = z2;
    }

    private int getAdditionalWidthForPosition(MessageObject.GroupedMessagePosition groupedMessagePosition) {
        int i = 0;
        if (groupedMessagePosition != null) {
            if ((groupedMessagePosition.flags & 2) == 0) {
                i = 0 + AndroidUtilities.dp(4.0f);
            }
            return (groupedMessagePosition.flags & 1) == 0 ? i + AndroidUtilities.dp(4.0f) : i;
        }
        return 0;
    }

    public void createSelectorDrawable(final int i) {
        int i2;
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        String str = "chat_outPreviewInstantText";
        if (this.psaHintPressed) {
            i2 = getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outViews" : "chat_inViews");
        } else {
            i2 = getThemedColor(this.currentMessageObject.isOutOwner() ? str : "chat_inPreviewInstantText");
        }
        Drawable[] drawableArr = this.selectorDrawable;
        if (drawableArr[i] == null) {
            final Paint paint = new Paint(1);
            paint.setColor(-1);
            Drawable drawable = new Drawable() { // from class: org.telegram.ui.Cells.ChatMessageCell.5
                RectF rect = new RectF();
                Path path = new Path();

                @Override // android.graphics.drawable.Drawable
                public int getOpacity() {
                    return -2;
                }

                @Override // android.graphics.drawable.Drawable
                public void setAlpha(int i3) {
                }

                @Override // android.graphics.drawable.Drawable
                public void setColorFilter(ColorFilter colorFilter) {
                }

                @Override // android.graphics.drawable.Drawable
                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                    if (ChatMessageCell.this.selectorDrawableMaskType[i] != 3 && ChatMessageCell.this.selectorDrawableMaskType[i] != 4) {
                        float f = 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[i] == 2) {
                            this.path.reset();
                            boolean z = ChatMessageCell.this.currentMessageObject != null && ChatMessageCell.this.currentMessageObject.isOutOwner();
                            for (int i3 = 0; i3 < 4; i3++) {
                                if (!ChatMessageCell.this.instantTextNewLine) {
                                    if (i3 == 2 && !z) {
                                        float[] fArr = ChatMessageCell.radii;
                                        int i4 = i3 * 2;
                                        float dp = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                                        ChatMessageCell.radii[i4 + 1] = dp;
                                        fArr[i4] = dp;
                                    } else if (i3 != 3 || !z) {
                                        if ((ChatMessageCell.this.mediaBackground || ChatMessageCell.this.pinnedBottom) && (i3 == 2 || i3 == 3)) {
                                            float[] fArr2 = ChatMessageCell.radii;
                                            int i5 = i3 * 2;
                                            float[] fArr3 = ChatMessageCell.radii;
                                            int i6 = i5 + 1;
                                            float dp2 = AndroidUtilities.dp(ChatMessageCell.this.pinnedBottom ? Math.min(5, SharedConfig.bubbleRadius) : SharedConfig.bubbleRadius);
                                            fArr3[i6] = dp2;
                                            fArr2[i5] = dp2;
                                        }
                                    } else {
                                        float[] fArr4 = ChatMessageCell.radii;
                                        int i7 = i3 * 2;
                                        float dp3 = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                                        ChatMessageCell.radii[i7 + 1] = dp3;
                                        fArr4[i7] = dp3;
                                    }
                                }
                                float[] fArr5 = ChatMessageCell.radii;
                                int i8 = i3 * 2;
                                ChatMessageCell.radii[i8 + 1] = 0.0f;
                                fArr5[i8] = 0.0f;
                            }
                            this.path.addRoundRect(this.rect, ChatMessageCell.radii, Path.Direction.CW);
                            this.path.close();
                            canvas.drawPath(this.path, paint);
                            return;
                        }
                        RectF rectF = this.rect;
                        float dp4 = ChatMessageCell.this.selectorDrawableMaskType[i] == 0 ? AndroidUtilities.dp(6.0f) : 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[i] == 0) {
                            f = AndroidUtilities.dp(6.0f);
                        }
                        canvas.drawRoundRect(rectF, dp4, f, paint);
                        return;
                    }
                    canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), AndroidUtilities.dp(ChatMessageCell.this.selectorDrawableMaskType[i] == 3 ? 16.0f : 20.0f), paint);
                }
            };
            int[][] iArr = {StateSet.WILD_CARD};
            int[] iArr2 = new int[1];
            if (!this.currentMessageObject.isOutOwner()) {
                str = "chat_inPreviewInstantText";
            }
            iArr2[0] = getThemedColor(str) & 436207615;
            this.selectorDrawable[i] = new RippleDrawable(new ColorStateList(iArr, iArr2), null, drawable);
            this.selectorDrawable[i].setCallback(this);
        } else {
            Theme.setSelectorDrawableColor(drawableArr[i], i2 & 436207615, true);
        }
        this.selectorDrawable[i].setVisible(true, false);
    }

    private void createInstantViewButton() {
        String str;
        int measureText;
        if (Build.VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable(0);
        }
        if (!this.drawInstantView || this.instantViewLayout != null) {
            return;
        }
        this.instantWidth = AndroidUtilities.dp(33.0f);
        int i = this.drawInstantViewType;
        if (i == 12) {
            str = LocaleController.getString("OpenChannelPost", R.string.OpenChannelPost);
        } else if (i == 1) {
            str = LocaleController.getString("OpenChannel", R.string.OpenChannel);
        } else if (i == 13) {
            str = LocaleController.getString("SendMessage", R.string.SendMessage).toUpperCase();
        } else if (i == 10) {
            str = LocaleController.getString("OpenBot", R.string.OpenBot);
        } else if (i == 2) {
            str = LocaleController.getString("OpenGroup", R.string.OpenGroup);
        } else if (i == 3) {
            str = LocaleController.getString("OpenMessage", R.string.OpenMessage);
        } else if (i == 5) {
            str = LocaleController.getString("ViewContact", R.string.ViewContact);
        } else if (i == 6) {
            str = LocaleController.getString("OpenBackground", R.string.OpenBackground);
        } else if (i == 7) {
            str = LocaleController.getString("OpenTheme", R.string.OpenTheme);
        } else if (i == 8) {
            if (this.pollVoted || this.pollClosed) {
                str = LocaleController.getString("PollViewResults", R.string.PollViewResults);
            } else {
                str = LocaleController.getString("PollSubmitVotes", R.string.PollSubmitVotes);
            }
        } else if (i == 9 || i == 11) {
            TLRPC$TL_webPage tLRPC$TL_webPage = (TLRPC$TL_webPage) this.currentMessageObject.messageOwner.media.webpage;
            if (tLRPC$TL_webPage != null && tLRPC$TL_webPage.url.contains("voicechat=")) {
                str = LocaleController.getString("VoipGroupJoinAsSpeaker", R.string.VoipGroupJoinAsSpeaker);
            } else {
                str = LocaleController.getString("VoipGroupJoinAsLinstener", R.string.VoipGroupJoinAsLinstener);
            }
        } else {
            str = LocaleController.getString("InstantView", R.string.InstantView);
        }
        if (this.currentMessageObject.isSponsored() && this.backgroundWidth < (measureText = (int) (Theme.chat_instantViewPaint.measureText(str) + AndroidUtilities.dp(75.0f)))) {
            this.backgroundWidth = measureText;
        }
        int dp = this.backgroundWidth - AndroidUtilities.dp(75.0f);
        this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, dp, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, dp + AndroidUtilities.dp(2.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (this.drawInstantViewType == 8) {
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(13.0f);
        } else {
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(34.0f);
        }
        int dp2 = this.totalHeight + AndroidUtilities.dp(46.0f);
        this.totalHeight = dp2;
        if (this.currentMessageObject.type == 12) {
            this.totalHeight = dp2 + AndroidUtilities.dp(14.0f);
        }
        if (this.currentMessageObject.isSponsored() && this.hasNewLineForTime) {
            this.totalHeight += AndroidUtilities.dp(16.0f);
        }
        StaticLayout staticLayout = this.instantViewLayout;
        if (staticLayout == null || staticLayout.getLineCount() <= 0) {
            return;
        }
        double d = this.instantWidth;
        double ceil = Math.ceil(this.instantViewLayout.getLineWidth(0));
        Double.isNaN(d);
        this.instantTextX = (((int) (d - ceil)) / 2) + (this.drawInstantViewType == 0 ? AndroidUtilities.dp(8.0f) : 0);
        int lineLeft = (int) this.instantViewLayout.getLineLeft(0);
        this.instantTextLeftX = lineLeft;
        this.instantTextX += -lineLeft;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.inLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (messageObject.checkLayout() || this.lastHeight != AndroidUtilities.displaySize.y)) {
            this.inLayout = true;
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            this.inLayout = false;
        }
        updateSelectionTextPosition();
        setMeasuredDimension(View.MeasureSpec.getSize(i), this.totalHeight + this.keyboardHeight);
    }

    public void forceResetMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        this.currentMessageObject = null;
        setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
    }

    private int getGroupPhotosWidth() {
        int parentWidth = getParentWidth();
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.preview) {
            parentWidth = this.parentWidth;
        }
        if (AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet()) {
            return parentWidth;
        }
        if (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2) {
            return parentWidth;
        }
        int i = (parentWidth / 100) * 35;
        if (i < AndroidUtilities.dp(320.0f)) {
            i = AndroidUtilities.dp(320.0f);
        }
        return parentWidth - i;
    }

    private int getExtraTextX() {
        int i = SharedConfig.bubbleRadius;
        if (i >= 15) {
            return AndroidUtilities.dp(2.0f);
        }
        if (i < 11) {
            return 0;
        }
        return AndroidUtilities.dp(1.0f);
    }

    private int getExtraTimeX() {
        int i;
        if (!this.currentMessageObject.isOutOwner() && ((!this.mediaBackground || this.captionLayout != null) && (i = SharedConfig.bubbleRadius) > 11)) {
            return AndroidUtilities.dp((i - 11) / 1.5f);
        }
        if (!this.currentMessageObject.isOutOwner() && this.isPlayingRound && this.isAvatarVisible && this.currentMessageObject.type == 5) {
            return (int) ((AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f);
        }
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        if (this.currentMessageObject == null) {
            return;
        }
        int measuredHeight = getMeasuredHeight() + (getMeasuredWidth() << 16);
        int i15 = 10;
        if (this.lastSize != measuredHeight || !this.wasLayout) {
            this.layoutWidth = getMeasuredWidth();
            this.layoutHeight = getMeasuredHeight() - this.substractBackgroundHeight;
            if (this.timeTextWidth < 0) {
                this.timeTextWidth = AndroidUtilities.dp(10.0f);
            }
            this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, AndroidUtilities.dp(100.0f) + this.timeTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.mediaBackground) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f);
                } else {
                    this.timeX = (this.backgroundWidth - AndroidUtilities.dp(4.0f)) - this.timeWidth;
                    if (this.currentMessageObject.isAnyKindOfSticker()) {
                        this.timeX = Math.max(AndroidUtilities.dp(26.0f), this.timeX);
                    }
                    if (this.isAvatarVisible) {
                        this.timeX += AndroidUtilities.dp(48.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                    if (groupedMessagePosition != null && (i14 = groupedMessagePosition.leftSpanOffset) != 0) {
                        this.timeX += (int) Math.ceil((i14 / 1000.0f) * getGroupPhotosWidth());
                    }
                    if (this.captionLayout != null && this.currentPosition != null) {
                        this.timeX += AndroidUtilities.dp(4.0f);
                    }
                }
                if (SharedConfig.bubbleRadius >= 10 && this.captionLayout == null && (i13 = this.documentAttachType) != 7 && i13 != 6) {
                    this.timeX -= AndroidUtilities.dp(2.0f);
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f);
            } else {
                this.timeX = (this.backgroundWidth - AndroidUtilities.dp(9.0f)) - this.timeWidth;
                if (this.currentMessageObject.isAnyKindOfSticker()) {
                    this.timeX = Math.max(0, this.timeX);
                }
                if (this.isAvatarVisible) {
                    this.timeX += AndroidUtilities.dp(48.0f);
                }
                if (shouldDrawTimeOnMedia()) {
                    this.timeX -= AndroidUtilities.dp(7.0f);
                }
            }
            this.timeX -= getExtraTimeX();
            if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                this.viewsLayout = new StaticLayout(this.currentViewsString, Theme.chat_timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.viewsLayout = null;
            }
            if (this.currentRepliesString != null && !this.currentMessageObject.scheduled) {
                this.repliesLayout = new StaticLayout(this.currentRepliesString, Theme.chat_timePaint, this.repliesTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } else {
                this.repliesLayout = null;
            }
            if (this.isAvatarVisible) {
                this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), this.avatarImage.getImageY(), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            }
            this.wasLayout = true;
        }
        this.lastSize = measuredHeight;
        if (this.currentMessageObject.type == 0) {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (this.isRoundVideo) {
            updatePlayingMessageProgress();
        }
        int i16 = this.documentAttachType;
        if (i16 == 3) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(57.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                this.seekBarX = AndroidUtilities.dp(114.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(66.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            updateSeekBarWaveformWidth();
            SeekBar seekBar = this.seekBar;
            int i17 = this.backgroundWidth;
            if (!this.hasLinkPreview) {
                i15 = 0;
            }
            seekBar.setSize(i17 - AndroidUtilities.dp(i15 + 72), AndroidUtilities.dp(30.0f));
            this.seekBarY = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            int dp = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp;
            RadialProgress2 radialProgress2 = this.radialProgress;
            int i18 = this.buttonX;
            radialProgress2.setProgressRect(i18, dp, AndroidUtilities.dp(44.0f) + i18, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i16 == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(56.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                this.seekBarX = AndroidUtilities.dp(113.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(65.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            SeekBar seekBar2 = this.seekBar;
            int i19 = this.backgroundWidth;
            if (!this.hasLinkPreview) {
                i15 = 0;
            }
            seekBar2.setSize(i19 - AndroidUtilities.dp(i15 + 65), AndroidUtilities.dp(30.0f));
            this.seekBarY = AndroidUtilities.dp(29.0f) + this.namesOffset + this.mediaOffsetY;
            int dp2 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp2;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i20 = this.buttonX;
            radialProgress22.setProgressRect(i20, dp2, AndroidUtilities.dp(44.0f) + i20, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i16 == 1 && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                this.buttonX = AndroidUtilities.dp(71.0f);
            } else {
                this.buttonX = AndroidUtilities.dp(23.0f);
            }
            if (this.hasLinkPreview) {
                this.buttonX += AndroidUtilities.dp(10.0f);
            }
            int dp3 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp3;
            RadialProgress2 radialProgress23 = this.radialProgress;
            int i21 = this.buttonX;
            radialProgress23.setProgressRect(i21, dp3, AndroidUtilities.dp(44.0f) + i21, this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else {
            MessageObject messageObject = this.currentMessageObject;
            int i22 = messageObject.type;
            if (i22 == 12) {
                if (messageObject.isOutOwner()) {
                    i12 = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                    i12 = AndroidUtilities.dp(72.0f);
                } else {
                    i12 = AndroidUtilities.dp(23.0f);
                }
                this.photoImage.setImageCoords(i12, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
                return;
            }
            if (i22 == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                if (this.hasGamePreview) {
                    i8 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else {
                    if (this.hasInvoicePreview) {
                        i11 = this.unmovedTextX;
                        i10 = AndroidUtilities.dp(1.0f);
                    } else {
                        i11 = this.unmovedTextX;
                        i10 = AndroidUtilities.dp(1.0f);
                    }
                    i8 = i11 + i10;
                }
                if (this.isSmallImage) {
                    i5 = i8 + this.backgroundWidth;
                    i9 = AndroidUtilities.dp(81.0f);
                    i5 -= i9;
                } else {
                    i7 = this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f);
                    i5 = i8 + i7;
                }
            } else {
                if (messageObject.isOutOwner()) {
                    if (this.mediaBackground) {
                        i5 = this.layoutWidth - this.backgroundWidth;
                        i9 = AndroidUtilities.dp(3.0f);
                    } else {
                        i8 = this.layoutWidth - this.backgroundWidth;
                        i7 = AndroidUtilities.dp(6.0f);
                        i5 = i8 + i7;
                    }
                } else {
                    if (this.isChat && this.isAvatarVisible && !this.isPlayingRound) {
                        i5 = AndroidUtilities.dp(63.0f);
                    } else {
                        i5 = AndroidUtilities.dp(15.0f);
                    }
                    MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
                    if (groupedMessagePosition2 != null && !groupedMessagePosition2.edge) {
                        i9 = AndroidUtilities.dp(10.0f);
                    }
                }
                i5 -= i9;
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
            if (groupedMessagePosition3 != null) {
                if ((groupedMessagePosition3.flags & 1) == 0) {
                    i5 -= AndroidUtilities.dp(2.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    i5 += (int) Math.ceil((i6 / 1000.0f) * getGroupPhotosWidth());
                }
            }
            if (this.currentMessageObject.type != 0) {
                i5 -= AndroidUtilities.dp(2.0f);
            }
            TransitionParams transitionParams = this.transitionParams;
            if (!transitionParams.imageChangeBoundsTransition || transitionParams.updatePhotoImageX) {
                transitionParams.updatePhotoImageX = false;
                ImageReceiver imageReceiver = this.photoImage;
                imageReceiver.setImageCoords(i5, imageReceiver.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            }
            this.buttonX = (int) (i5 + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY;
            RadialProgress2 radialProgress24 = this.radialProgress;
            int i23 = this.buttonX;
            radialProgress24.setProgressRect(i23, imageY, AndroidUtilities.dp(48.0f) + i23, this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(5.0f), this.buttonY + AndroidUtilities.dp(5.0f), this.buttonX + AndroidUtilities.dp(43.0f), this.buttonY + AndroidUtilities.dp(43.0f));
            int i24 = this.documentAttachType;
            if (i24 != 4 && i24 != 2) {
                return;
            }
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY2 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY2;
            RadialProgress2 radialProgress25 = this.videoRadialProgress;
            int i25 = this.videoButtonX;
            radialProgress25.setProgressRect(i25, imageY2, AndroidUtilities.dp(24.0f) + i25, this.videoButtonY + AndroidUtilities.dp(24.0f));
        }
    }

    public boolean needDelayRoundProgressDraw() {
        int i = this.documentAttachType;
        return (i == 7 || i == 4) && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0069  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00cb  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0128  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0140  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x018c  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0191  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0197  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x024c  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0255  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x026d  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x02b4  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x02cc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawRoundProgress(android.graphics.Canvas r20) {
        /*
            Method dump skipped, instructions count: 727
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawRoundProgress(android.graphics.Canvas):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00b8  */
    /* JADX WARN: Removed duplicated region for block: B:61:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updatePollAnimations(long r9) {
        /*
            Method dump skipped, instructions count: 277
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updatePollAnimations(long):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:364:0x083a  */
    /* JADX WARN: Removed duplicated region for block: B:371:0x0884  */
    /* JADX WARN: Removed duplicated region for block: B:381:0x091d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawContent(android.graphics.Canvas r32) {
        /*
            Method dump skipped, instructions count: 4360
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawContent(android.graphics.Canvas):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0017, code lost:
        if ((r1 & 1) != 0) goto L10;
     */
    /* JADX WARN: Removed duplicated region for block: B:70:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0136  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateReactionLayoutPosition() {
        /*
            Method dump skipped, instructions count: 377
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateReactionLayoutPosition():void");
    }

    /* JADX WARN: Type inference failed for: r11v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r11v45 */
    /* JADX WARN: Type inference failed for: r11v54 */
    public void drawLinkPreview(Canvas canvas, float f) {
        int dp;
        int i;
        int dp2;
        int i2;
        int i3;
        int i4;
        int i5;
        ?? r11;
        Drawable drawable;
        int i6;
        int i7;
        int i8;
        int i9;
        boolean z;
        boolean z2;
        int i10;
        int i11;
        int i12;
        Paint paint;
        if (this.currentMessageObject.isSponsored() || this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview) {
            if (this.hasGamePreview) {
                dp = AndroidUtilities.dp(14.0f) + this.namesOffset;
                i2 = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else {
                if (this.hasInvoicePreview) {
                    dp = AndroidUtilities.dp(14.0f) + this.namesOffset;
                    i = this.unmovedTextX;
                    dp2 = AndroidUtilities.dp(1.0f);
                } else if (this.currentMessageObject.isSponsored()) {
                    dp = (this.textY + this.currentMessageObject.textHeight) - AndroidUtilities.dp(2.0f);
                    if (this.hasNewLineForTime) {
                        dp += AndroidUtilities.dp(16.0f);
                    }
                    i = this.unmovedTextX;
                    dp2 = AndroidUtilities.dp(1.0f);
                } else {
                    dp = this.textY + this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f);
                    i = this.unmovedTextX;
                    dp2 = AndroidUtilities.dp(1.0f);
                }
                i2 = i + dp2;
            }
            int i13 = dp;
            int i14 = i2;
            if (!this.hasInvoicePreview && !this.currentMessageObject.isSponsored()) {
                Theme.chat_replyLinePaint.setColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outPreviewLine" : "chat_inPreviewLine"));
                if (f != 1.0f) {
                    Theme.chat_replyLinePaint.setAlpha((int) (paint.getAlpha() * f));
                }
                canvas.drawRect(i14, i13 - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(2.0f) + i14, this.linkPreviewHeight + i13 + AndroidUtilities.dp(3.0f), Theme.chat_replyLinePaint);
            }
            if (this.siteNameLayout != null) {
                int dp3 = i13 - AndroidUtilities.dp(1.0f);
                Theme.chat_replyNamePaint.setColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outSiteNameText" : "chat_inSiteNameText"));
                if (f != 1.0f) {
                    Theme.chat_replyNamePaint.setAlpha((int) (Theme.chat_replyLinePaint.getAlpha() * f));
                }
                canvas.save();
                if (this.siteNameRtl) {
                    i12 = (this.backgroundWidth - this.siteNameWidth) - AndroidUtilities.dp(32.0f);
                    if (this.isSmallImage) {
                        i12 -= AndroidUtilities.dp(54.0f);
                    }
                } else {
                    i12 = this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f);
                }
                canvas.translate(i12 + i14, i13 - AndroidUtilities.dp(3.0f));
                this.siteNameLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout = this.siteNameLayout;
                i4 = staticLayout.getLineBottom(staticLayout.getLineCount() - 1) + i13;
                i3 = dp3;
            } else {
                i4 = i13;
                i3 = 0;
            }
            if ((this.hasGamePreview || this.hasInvoicePreview) && (i11 = this.currentMessageObject.textHeight) != 0) {
                i13 += i11 + AndroidUtilities.dp(4.0f);
                i4 += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
            }
            if ((!this.drawPhotoImage || !this.drawInstantView || (i10 = this.drawInstantViewType) == 9 || i10 == 13 || i10 == 11 || i10 == 1) && (this.drawInstantViewType != 6 || this.imageBackgroundColor == 0)) {
                r11 = 1;
                i5 = 0;
            } else {
                if (i4 != i13) {
                    i4 += AndroidUtilities.dp(2.0f);
                }
                int i15 = i4;
                if (this.imageBackgroundSideColor != 0) {
                    int dp4 = AndroidUtilities.dp(10.0f) + i14;
                    ImageReceiver imageReceiver = this.photoImage;
                    float f2 = dp4;
                    imageReceiver.setImageCoords(((this.imageBackgroundSideWidth - imageReceiver.getImageWidth()) / 2.0f) + f2, i15, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    this.rect.set(f2, this.photoImage.getImageY(), dp4 + this.imageBackgroundSideWidth, this.photoImage.getImageY2());
                    Theme.chat_instantViewPaint.setColor(ColorUtils.setAlphaComponent(this.imageBackgroundSideColor, (int) (f * 255.0f)));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                } else {
                    this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + i14, i15, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                }
                if (this.imageBackgroundColor != 0) {
                    this.rect.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
                    if (this.imageBackgroundGradientColor1 != 0) {
                        if (this.imageBackgroundGradientColor2 != 0) {
                            if (this.motionBackgroundDrawable == null) {
                                MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(this.imageBackgroundColor, this.imageBackgroundGradientColor1, this.imageBackgroundGradientColor2, this.imageBackgroundGradientColor3, true);
                                this.motionBackgroundDrawable = motionBackgroundDrawable;
                                if (this.imageBackgroundIntensity < 0.0f) {
                                    this.photoImage.setGradientBitmap(motionBackgroundDrawable.getBitmap());
                                }
                                if (!this.photoImage.hasImageSet()) {
                                    this.motionBackgroundDrawable.setRoundRadius(AndroidUtilities.dp(4.0f));
                                }
                            }
                        } else {
                            if (this.gradientShader == null) {
                                Rect gradientPoints = BackgroundGradientDrawable.getGradientPoints(AndroidUtilities.getWallpaperRotation(this.imageBackgroundGradientRotation, false), (int) this.rect.width(), (int) this.rect.height());
                                this.gradientShader = new LinearGradient(gradientPoints.left, gradientPoints.top, gradientPoints.right, gradientPoints.bottom, new int[]{this.imageBackgroundColor, this.imageBackgroundGradientColor1}, (float[]) null, Shader.TileMode.CLAMP);
                            }
                            Theme.chat_instantViewPaint.setShader(this.gradientShader);
                            if (f != 1.0f) {
                                Theme.chat_instantViewPaint.setAlpha((int) (f * 255.0f));
                            }
                        }
                    } else {
                        Theme.chat_instantViewPaint.setShader(null);
                        Theme.chat_instantViewPaint.setColor(this.imageBackgroundColor);
                        if (f != 1.0f) {
                            Theme.chat_instantViewPaint.setAlpha((int) (f * 255.0f));
                        }
                    }
                    MotionBackgroundDrawable motionBackgroundDrawable2 = this.motionBackgroundDrawable;
                    if (motionBackgroundDrawable2 != null) {
                        RectF rectF = this.rect;
                        motionBackgroundDrawable2.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        this.motionBackgroundDrawable.draw(canvas);
                        i9 = i15;
                        z2 = true;
                        i5 = 0;
                    } else if (this.imageBackgroundSideColor != 0) {
                        i9 = i15;
                        i5 = 0;
                        z2 = true;
                        canvas.drawRect(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2(), Theme.chat_instantViewPaint);
                    } else {
                        i9 = i15;
                        z2 = true;
                        i5 = 0;
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_instantViewPaint);
                    }
                    Theme.chat_instantViewPaint.setShader(null);
                    Theme.chat_instantViewPaint.setAlpha(255);
                    z = z2;
                } else {
                    i9 = i15;
                    z = true;
                    i5 = 0;
                }
                if (this.drawPhotoImage && this.drawInstantView && this.drawInstantViewType != 9) {
                    if (this.drawImageButton) {
                        int dp5 = AndroidUtilities.dp(48.0f);
                        float f3 = dp5;
                        int imageX = (int) (this.photoImage.getImageX() + ((this.photoImage.getImageWidth() - f3) / 2.0f));
                        this.buttonX = imageX;
                        this.buttonX = imageX;
                        int imageY = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - f3) / 2.0f));
                        this.buttonY = imageY;
                        this.buttonY = imageY;
                        RadialProgress2 radialProgress2 = this.radialProgress;
                        int i16 = this.buttonX;
                        radialProgress2.setProgressRect(i16, imageY, i16 + dp5, dp5 + imageY);
                    }
                    ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                    if (chatMessageCellDelegate == null || chatMessageCellDelegate.getPinchToZoomHelper() == null || !this.delegate.getPinchToZoomHelper().isInOverlayModeFor(this)) {
                        if (f != 1.0f) {
                            this.photoImage.setAlpha(f);
                            this.imageDrawn = this.photoImage.draw(canvas);
                            this.photoImage.setAlpha(1.0f);
                        } else {
                            this.imageDrawn = this.photoImage.draw(canvas);
                        }
                    }
                }
                i4 = (int) (i9 + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0f));
                r11 = z;
            }
            if (this.currentMessageObject.isOutOwner()) {
                int i17 = (int) (f * 255.0f);
                Theme.chat_replyNamePaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextOut"), i17));
                Theme.chat_replyTextPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextOut"), i17));
            } else {
                int i18 = (int) (f * 255.0f);
                Theme.chat_replyNamePaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextIn"), i18));
                Theme.chat_replyTextPaint.setColor(ColorUtils.setAlphaComponent(getThemedColor("chat_messageTextIn"), i18));
            }
            if (this.titleLayout != null) {
                if (i4 != i13) {
                    i4 += AndroidUtilities.dp(2.0f);
                }
                if (i3 == 0) {
                    i3 = i4 - AndroidUtilities.dp(1.0f);
                }
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) + i14 + this.titleX, i4 - AndroidUtilities.dp(3.0f));
                this.titleLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout2 = this.titleLayout;
                i4 += staticLayout2.getLineBottom(staticLayout2.getLineCount() - r11);
            }
            if (this.authorLayout != null) {
                if (i4 != i13) {
                    i4 += AndroidUtilities.dp(2.0f);
                }
                if (i3 == 0) {
                    i3 = i4 - AndroidUtilities.dp(1.0f);
                }
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) + i14 + this.authorX, i4 - AndroidUtilities.dp(3.0f));
                this.authorLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout3 = this.authorLayout;
                i4 += staticLayout3.getLineBottom(staticLayout3.getLineCount() - r11);
            }
            if (this.descriptionLayout != null) {
                if (i4 != i13) {
                    i4 += AndroidUtilities.dp(2.0f);
                }
                if (i3 == 0) {
                    i3 = i4 - AndroidUtilities.dp(1.0f);
                }
                this.descriptionY = i4 - AndroidUtilities.dp(3.0f);
                canvas.save();
                canvas.translate((this.hasInvoicePreview ? 0 : AndroidUtilities.dp(10.0f)) + i14 + this.descriptionX, this.descriptionY);
                if (this.linkBlockNum == -10 && this.links.draw(canvas)) {
                    invalidate();
                }
                ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
                if (chatMessageCellDelegate2 != null && chatMessageCellDelegate2.getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
                    this.delegate.getTextSelectionHelper().drawDescription(this.currentMessageObject.isOutOwner(), this.descriptionLayout, canvas);
                }
                this.descriptionLayout.draw(canvas);
                canvas.restore();
                StaticLayout staticLayout4 = this.descriptionLayout;
                int lineCount = staticLayout4.getLineCount();
                int i19 = r11 == true ? 1 : 0;
                int i20 = r11 == true ? 1 : 0;
                int i21 = r11 == true ? 1 : 0;
                int i22 = r11 == true ? 1 : 0;
                int i23 = r11 == true ? 1 : 0;
                int i24 = r11 == true ? 1 : 0;
                int i25 = r11 == true ? 1 : 0;
                i4 += staticLayout4.getLineBottom(lineCount - i19);
            }
            int i26 = i3;
            if (this.drawPhotoImage && (!this.drawInstantView || (i8 = this.drawInstantViewType) == 9 || i8 == 11 || i8 == 13 || i8 == r11)) {
                if (i4 != i13) {
                    i4 += AndroidUtilities.dp(2.0f);
                }
                if (this.isSmallImage) {
                    this.photoImage.setImageCoords((this.backgroundWidth + i14) - AndroidUtilities.dp(81.0f), i26, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                } else {
                    this.photoImage.setImageCoords((this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f)) + i14, i4, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    if (this.drawImageButton) {
                        int dp6 = AndroidUtilities.dp(48.0f);
                        float f4 = dp6;
                        int imageX2 = (int) (this.photoImage.getImageX() + ((this.photoImage.getImageWidth() - f4) / 2.0f));
                        this.buttonX = imageX2;
                        this.buttonX = imageX2;
                        int imageY2 = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - f4) / 2.0f));
                        this.buttonY = imageY2;
                        this.buttonY = imageY2;
                        RadialProgress2 radialProgress22 = this.radialProgress;
                        int i27 = this.buttonX;
                        radialProgress22.setProgressRect(i27, imageY2, i27 + dp6, dp6 + imageY2);
                    }
                }
                if (this.isRoundVideo && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && MediaController.getInstance().isVideoDrawingReady() && canvas.isHardwareAccelerated()) {
                    this.imageDrawn = r11;
                    this.drawTime = r11;
                } else {
                    ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
                    if (chatMessageCellDelegate3 == null || chatMessageCellDelegate3.getPinchToZoomHelper() == null || !this.delegate.getPinchToZoomHelper().isInOverlayModeFor(this)) {
                        if (f != 1.0f) {
                            this.photoImage.setAlpha(f);
                            this.imageDrawn = this.photoImage.draw(canvas);
                            this.photoImage.setAlpha(1.0f);
                        } else {
                            this.imageDrawn = this.photoImage.draw(canvas);
                        }
                    }
                }
            }
            int i28 = this.documentAttachType;
            if (i28 == 4 || i28 == 2) {
                this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
                int imageY3 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
                this.videoButtonY = imageY3;
                RadialProgress2 radialProgress23 = this.videoRadialProgress;
                int i29 = this.videoButtonX;
                radialProgress23.setProgressRect(i29, imageY3, AndroidUtilities.dp(24.0f) + i29, this.videoButtonY + AndroidUtilities.dp(24.0f));
            }
            Paint themedPaint = getThemedPaint("paintChatTimeBackground");
            if (this.photosCountLayout != null && this.photoImage.getVisible()) {
                int imageX3 = (int) (((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.photosCountWidth);
                int imageY4 = (int) ((this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f));
                this.rect.set(imageX3 - AndroidUtilities.dp(4.0f), imageY4 - AndroidUtilities.dp(1.5f), this.photosCountWidth + imageX3 + AndroidUtilities.dp(4.0f), imageY4 + AndroidUtilities.dp(14.5f));
                int alpha = themedPaint.getAlpha();
                themedPaint.setAlpha((int) (alpha * this.controlsAlpha));
                Theme.chat_durationPaint.setAlpha((int) (this.controlsAlpha * 255.0f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), themedPaint);
                themedPaint.setAlpha(alpha);
                canvas.save();
                canvas.translate(imageX3, imageY4);
                this.photosCountLayout.draw(canvas);
                canvas.restore();
                Theme.chat_durationPaint.setAlpha(255);
            }
            if (this.videoInfoLayout != null && ((!this.drawPhotoImage || this.photoImage.getVisible()) && this.imageBackgroundSideColor == 0)) {
                if (this.hasGamePreview || this.hasInvoicePreview || this.documentAttachType == 8) {
                    if (this.drawPhotoImage) {
                        i6 = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.5f));
                        i7 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(6.0f));
                        this.rect.set(i6 - AndroidUtilities.dp(4.0f), i7 - AndroidUtilities.dp(1.5f), this.durationWidth + i6 + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(this.documentAttachType == 8 ? 14.5f : 16.5f) + i7);
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), themedPaint);
                    } else {
                        i7 = i4;
                        i6 = i14;
                    }
                } else {
                    i6 = (int) (((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth);
                    int imageY5 = (int) ((this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f));
                    this.rect.set(i6 - AndroidUtilities.dp(4.0f), imageY5 - AndroidUtilities.dp(1.5f), this.durationWidth + i6 + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(14.5f) + imageY5);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), getThemedPaint("paintChatTimeBackground"));
                    i7 = imageY5;
                }
                canvas.save();
                canvas.translate(i6, i7);
                if (this.hasInvoicePreview) {
                    if (this.drawPhotoImage) {
                        Theme.chat_shipmentPaint.setColor(getThemedColor("chat_previewGameText"));
                    } else if (this.currentMessageObject.isOutOwner()) {
                        Theme.chat_shipmentPaint.setColor(getThemedColor("chat_messageTextOut"));
                    } else {
                        Theme.chat_shipmentPaint.setColor(getThemedColor("chat_messageTextIn"));
                    }
                }
                this.videoInfoLayout.draw(canvas);
                canvas.restore();
            }
            if (!this.drawInstantView) {
                return;
            }
            int dp7 = i13 + this.linkPreviewHeight + AndroidUtilities.dp(10.0f);
            Paint paint2 = Theme.chat_instantViewRectPaint;
            if (this.currentMessageObject.isOutOwner()) {
                drawable = getThemedDrawable("drawableMsgOutInstant");
                Theme.chat_instantViewPaint.setColor(getThemedColor("chat_outPreviewInstantText"));
                paint2.setColor(getThemedColor("chat_outPreviewInstantText"));
            } else {
                drawable = Theme.chat_msgInInstantDrawable;
                Theme.chat_instantViewPaint.setColor(getThemedColor("chat_inPreviewInstantText"));
                paint2.setColor(getThemedColor("chat_inPreviewInstantText"));
            }
            this.instantButtonRect.set(i14, dp7, this.instantWidth + i14, AndroidUtilities.dp(36.0f) + dp7);
            if (Build.VERSION.SDK_INT >= 21) {
                this.selectorDrawableMaskType[i5] = i5;
                this.selectorDrawable[i5].setBounds(i14, dp7, this.instantWidth + i14, AndroidUtilities.dp(36.0f) + dp7);
                this.selectorDrawable[i5].draw(canvas);
            }
            canvas.drawRoundRect(this.instantButtonRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint2);
            if (this.drawInstantViewType == 0) {
                BaseCell.setDrawableBounds(drawable, ((this.instantTextLeftX + this.instantTextX) + i14) - AndroidUtilities.dp(15.0f), AndroidUtilities.dp(11.5f) + dp7, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(13.0f));
                drawable.draw(canvas);
            }
            if (this.instantViewLayout == null) {
                return;
            }
            canvas.save();
            canvas.translate(i14 + this.instantTextX, dp7 + AndroidUtilities.dp(10.5f));
            this.instantViewLayout.draw(canvas);
            canvas.restore();
        }
    }

    public boolean shouldDrawMenuDrawable() {
        return this.currentMessagesGroup == null || (this.currentPosition.flags & 4) != 0;
    }

    private void drawBotButtons(Canvas canvas, ArrayList<BotButton> arrayList, float f) {
        int dp;
        BotButton botButton;
        Drawable drawable;
        if (this.currentMessageObject.isOutOwner()) {
            dp = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
        } else {
            dp = this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f);
        }
        int i = dp;
        float f2 = 2.0f;
        float dp2 = (this.layoutHeight - AndroidUtilities.dp(2.0f)) + this.transitionParams.deltaBottom;
        float f3 = 0.0f;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            BotButton botButton2 = arrayList.get(i2);
            float f4 = botButton2.y + botButton2.height;
            if (f4 > f3) {
                f3 = f4;
            }
        }
        this.rect.set(0.0f, dp2, getMeasuredWidth(), f3 + dp2);
        if (f != 1.0f) {
            canvas.saveLayerAlpha(this.rect, (int) (f * 255.0f), 31);
        } else {
            canvas.save();
        }
        int i3 = 0;
        while (i3 < arrayList.size()) {
            BotButton botButton3 = arrayList.get(i3);
            float dp3 = ((botButton3.y + this.layoutHeight) - AndroidUtilities.dp(f2)) + this.transitionParams.deltaBottom;
            this.rect.set(botButton3.x + i, dp3, botButton3.x + i + botButton3.width, botButton3.height + dp3);
            applyServiceShaderMatrix();
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), getThemedPaint(i3 == this.pressedBotButton ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            canvas.save();
            boolean z = true;
            canvas.translate(botButton3.x + i + AndroidUtilities.dp(5.0f), ((AndroidUtilities.dp(44.0f) - botButton3.title.getLineBottom(botButton3.title.getLineCount() - 1)) / 2) + dp3);
            botButton3.title.draw(canvas);
            canvas.restore();
            if (!(botButton3.button instanceof TLRPC$TL_keyboardButtonWebView)) {
                if (botButton3.button instanceof TLRPC$TL_keyboardButtonUrl) {
                    if (botButton3.isInviteButton) {
                        drawable = getThemedDrawable("drawable_botInvite");
                    } else {
                        drawable = getThemedDrawable("drawableBotLink");
                    }
                    BaseCell.setDrawableBounds(drawable, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(3.0f)) - drawable.getIntrinsicWidth()) + i, dp3 + AndroidUtilities.dp(3.0f));
                    drawable.draw(canvas);
                } else if (!(botButton3.button instanceof TLRPC$TL_keyboardButtonSwitchInline)) {
                    if ((botButton3.button instanceof TLRPC$TL_keyboardButtonCallback) || (botButton3.button instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) || (botButton3.button instanceof TLRPC$TL_keyboardButtonGame) || (botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) || (botButton3.button instanceof TLRPC$TL_keyboardButtonUrlAuth)) {
                        if (botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) {
                            BaseCell.setDrawableBounds(Theme.chat_botCardDrawable, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(5.0f)) - Theme.chat_botCardDrawable.getIntrinsicWidth()) + i, AndroidUtilities.dp(4.0f) + dp3);
                            Theme.chat_botCardDrawable.draw(canvas);
                        }
                        if (((!(botButton3.button instanceof TLRPC$TL_keyboardButtonCallback) && !(botButton3.button instanceof TLRPC$TL_keyboardButtonGame) && !(botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) && !(botButton3.button instanceof TLRPC$TL_keyboardButtonUrlAuth)) || !SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, botButton3.button)) && (!(botButton3.button instanceof TLRPC$TL_keyboardButtonRequestGeoLocation) || !SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, botButton3.button))) {
                            z = false;
                        }
                        if (z || botButton3.progressAlpha != 0.0f) {
                            Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (botButton3.progressAlpha * 255.0f)));
                            int dp4 = ((botButton3.x + botButton3.width) - AndroidUtilities.dp(12.0f)) + i;
                            if (botButton3.button instanceof TLRPC$TL_keyboardButtonBuy) {
                                dp3 += AndroidUtilities.dp(26.0f);
                            }
                            this.rect.set(dp4, AndroidUtilities.dp(4.0f) + dp3, dp4 + AndroidUtilities.dp(8.0f), dp3 + AndroidUtilities.dp(12.0f));
                            canvas.drawArc(this.rect, botButton3.angle, 220.0f, false, Theme.chat_botProgressPaint);
                            invalidate();
                            long currentTimeMillis = System.currentTimeMillis();
                            if (Math.abs(botButton3.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                                long j = currentTimeMillis - botButton3.lastUpdateTime;
                                botButton = botButton3;
                                BotButton.access$2816(botButton, ((float) (360 * j)) / 2000.0f);
                                BotButton.access$2820(botButton, (botButton.angle / 360) * 360);
                                if (z) {
                                    if (botButton.progressAlpha < 1.0f) {
                                        BotButton.access$2716(botButton, ((float) j) / 200.0f);
                                        if (botButton.progressAlpha > 1.0f) {
                                            botButton.progressAlpha = 1.0f;
                                        }
                                    }
                                } else if (botButton.progressAlpha > 0.0f) {
                                    BotButton.access$2724(botButton, ((float) j) / 200.0f);
                                    if (botButton.progressAlpha < 0.0f) {
                                        botButton.progressAlpha = 0.0f;
                                    }
                                }
                            } else {
                                botButton = botButton3;
                            }
                            botButton.lastUpdateTime = currentTimeMillis;
                        }
                    }
                } else {
                    Drawable themedDrawable = getThemedDrawable("drawableBotInline");
                    BaseCell.setDrawableBounds(themedDrawable, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(3.0f)) - themedDrawable.getIntrinsicWidth()) + i, dp3 + AndroidUtilities.dp(3.0f));
                    themedDrawable.draw(canvas);
                }
            } else {
                Drawable themedDrawable2 = getThemedDrawable("drawableBotWebView");
                BaseCell.setDrawableBounds(themedDrawable2, (((botButton3.x + botButton3.width) - AndroidUtilities.dp(3.0f)) - themedDrawable2.getIntrinsicWidth()) + i, dp3 + AndroidUtilities.dp(3.0f));
                themedDrawable2.draw(canvas);
            }
            i3++;
            f2 = 2.0f;
        }
        canvas.restore();
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0136  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0141  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0176 A[LOOP:1: B:63:0x016e->B:65:0x0176, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01f4  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:99:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"Range"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawMessageText(android.graphics.Canvas r27, java.util.ArrayList<org.telegram.messenger.MessageObject.TextLayoutBlock> r28, boolean r29, float r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 527
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawMessageText(android.graphics.Canvas, java.util.ArrayList, boolean, float, boolean):void");
    }

    public void updateCaptionLayout() {
        float f;
        float f2;
        float f3;
        MessageObject messageObject = this.currentMessageObject;
        int i = messageObject.type;
        if (i == 1 || this.documentAttachType == 4 || i == 8) {
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.imageChangeBoundsTransition) {
                f2 = transitionParams.animateToImageX;
                f = transitionParams.animateToImageY;
                f3 = transitionParams.animateToImageH;
            } else {
                f2 = this.photoImage.getImageX();
                f = this.photoImage.getImageY();
                f3 = this.photoImage.getImageHeight();
            }
            this.captionX = f2 + AndroidUtilities.dp(5.0f) + this.captionOffsetX;
            this.captionY = f + f3 + AndroidUtilities.dp(6.0f);
        } else {
            float f4 = 41.3f;
            float f5 = 9.0f;
            float f6 = 11.0f;
            if (this.hasOldCaptionPreview) {
                int i2 = this.backgroundDrawableLeft;
                if (!messageObject.isOutOwner()) {
                    f6 = 17.0f;
                }
                this.captionX = i2 + AndroidUtilities.dp(f6) + this.captionOffsetX;
                int i3 = this.totalHeight - this.captionHeight;
                if (!this.drawPinnedTop) {
                    f5 = 10.0f;
                }
                float dp = ((i3 - AndroidUtilities.dp(f5)) - this.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                this.captionY = dp;
                if (this.drawCommentButton && this.drawSideButton != 3) {
                    if (!shouldDrawTimeOnMedia()) {
                        f4 = 43.0f;
                    }
                    this.captionY = dp - AndroidUtilities.dp(f4);
                }
            } else {
                int i4 = this.backgroundDrawableLeft;
                if (!messageObject.isOutOwner() && !this.mediaBackground && !this.drawPinnedBottom) {
                    f6 = 17.0f;
                }
                this.captionX = i4 + AndroidUtilities.dp(f6) + this.captionOffsetX;
                int i5 = this.totalHeight - this.captionHeight;
                if (!this.drawPinnedTop) {
                    f5 = 10.0f;
                }
                float dp2 = i5 - AndroidUtilities.dp(f5);
                this.captionY = dp2;
                if (this.drawCommentButton && this.drawSideButton != 3) {
                    if (!shouldDrawTimeOnMedia()) {
                        f4 = 43.0f;
                    }
                    this.captionY = dp2 - AndroidUtilities.dp(f4);
                }
                ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
                if (!reactionsLayoutInBubble.isEmpty && !reactionsLayoutInBubble.isSmall) {
                    this.captionY -= reactionsLayoutInBubble.totalHeight;
                }
            }
        }
        this.captionX += getExtraTextX();
    }

    private boolean textIsSelectionMode() {
        return getCurrentMessagesGroup() == null && this.delegate.getTextSelectionHelper() != null && this.delegate.getTextSelectionHelper().isSelected(this.currentMessageObject);
    }

    public float getViewTop() {
        return this.viewTop;
    }

    public int getBackgroundHeight() {
        return this.backgroundHeight;
    }

    public int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    public int getIconForCurrentState() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
            } else {
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            }
            int i2 = this.buttonState;
            if (i2 == 1) {
                return 1;
            }
            if (i2 == 2) {
                return 2;
            }
            return i2 == 4 ? 3 : 0;
        }
        if (i == 1 && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.radialProgress.setColors("chat_outLoader", "chat_outLoaderSelected", "chat_outMediaIcon", "chat_outMediaIconSelected");
            } else {
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            }
            int i3 = this.buttonState;
            if (i3 == -1) {
                return 5;
            }
            if (i3 == 0) {
                return 2;
            }
            if (i3 == 1) {
                return 3;
            }
        } else {
            this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            this.videoRadialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
            int i4 = this.buttonState;
            if (i4 >= 0 && i4 < 4) {
                if (i4 == 0) {
                    return 2;
                }
                if (i4 == 1) {
                    return 3;
                }
                return (i4 != 2 && this.autoPlayingMedia) ? 4 : 0;
            } else if (i4 == -1) {
                if (this.documentAttachType == 1) {
                    if (this.drawPhotoImage && (this.currentPhotoObject != null || this.currentPhotoObjectThumb != null)) {
                        if (this.photoImage.hasBitmapImage()) {
                            return 4;
                        }
                        MessageObject messageObject = this.currentMessageObject;
                        if (messageObject.mediaExists || messageObject.attachPathExists) {
                            return 4;
                        }
                    }
                    return 5;
                } else if (this.currentMessageObject.needDrawBluredPreview()) {
                    MessageObject messageObject2 = this.currentMessageObject;
                    if (messageObject2.messageOwner.destroyTime == 0) {
                        return 7;
                    }
                    return messageObject2.isOutOwner() ? 9 : 11;
                } else if (this.hasEmbed) {
                    return 0;
                }
            }
        }
        return 4;
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x00e7  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00f5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getMaxNameWidth() {
        /*
            Method dump skipped, instructions count: 254
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMaxNameWidth():int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:85:0x011c, code lost:
        if ((r9 & 2) != 0) goto L90;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateButtonState(boolean r17, boolean r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 2127
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateButtonState(boolean, boolean, boolean):void");
    }

    private void didPressMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i != 0) {
            if (i != 1) {
                return;
            }
            int i2 = this.documentAttachType;
            if ((i2 == 3 || i2 == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            this.currentMessageObject.loadingCancelled = true;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
            return;
        }
        this.miniButtonState = 1;
        this.radialProgress.setProgress(0.0f, false);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isAnyKindOfSticker()) {
            this.currentMessageObject.putInDownloadsStore = true;
        }
        int i3 = this.documentAttachType;
        if (i3 == 3 || i3 == 5) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            this.currentMessageObject.loadingCancelled = false;
        } else if (i3 == 4) {
            createLoadingProgressLayout(this.documentAttach);
            FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
            TLRPC$Document tLRPC$Document = this.documentAttach;
            MessageObject messageObject2 = this.currentMessageObject;
            fileLoader.loadFile(tLRPC$Document, messageObject2, 1, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 0);
            this.currentMessageObject.loadingCancelled = false;
        }
        this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
        invalidate();
    }

    private void didPressButton(boolean z, boolean z2) {
        String str;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isAnyKindOfSticker()) {
            this.currentMessageObject.putInDownloadsStore = true;
        }
        int i = this.buttonState;
        int i2 = 2;
        if (i == 0 && (!this.drawVideoImageButton || z2)) {
            int i3 = this.documentAttachType;
            if (i3 == 3 || i3 == 5) {
                if (this.miniButtonState == 0) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                    this.currentMessageObject.loadingCancelled = false;
                }
                if (!this.delegate.needPlayMessage(this.currentMessageObject)) {
                    return;
                }
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
                }
                updatePlayingMessageProgress();
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
                return;
            }
            if (z2) {
                this.videoRadialProgress.setProgress(0.0f, false);
            } else {
                this.radialProgress.setProgress(0.0f, false);
            }
            if (this.currentPhotoObject != null && (this.photoImage.hasNotThumb() || this.currentPhotoObjectThumb == null)) {
                tLRPC$PhotoSize = this.currentPhotoObject;
                str = ((tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || "s".equals(tLRPC$PhotoSize.type)) ? this.currentPhotoFilterThumb : this.currentPhotoFilter;
            } else {
                tLRPC$PhotoSize = this.currentPhotoObjectThumb;
                str = this.currentPhotoFilterThumb;
            }
            String str2 = str;
            MessageObject messageObject2 = this.currentMessageObject;
            int i4 = messageObject2.type;
            if (i4 == 1) {
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                String str3 = this.currentPhotoFilter;
                ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject);
                String str4 = this.currentPhotoFilterThumb;
                BitmapDrawable bitmapDrawable = this.currentPhotoObjectThumbStripped;
                long j = this.currentPhotoObject.size;
                MessageObject messageObject3 = this.currentMessageObject;
                imageReceiver.setImage(forObject, str3, forObject2, str4, bitmapDrawable, j, null, messageObject3, messageObject3.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (i4 == 8) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                if (this.currentMessageObject.loadedFileSize > 0) {
                    createLoadingProgressLayout(this.documentAttach);
                }
            } else if (this.isRoundVideo) {
                if (messageObject2.isSecretMedia()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
                } else {
                    MessageObject messageObject4 = this.currentMessageObject;
                    messageObject4.gifState = 2.0f;
                    TLRPC$Document document = messageObject4.getDocument();
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForObject(tLRPC$PhotoSize, document), str2, document.size, null, this.currentMessageObject, 0);
                }
            } else if (i4 == 9) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                if (this.currentMessageObject.loadedFileSize > 0) {
                    createLoadingProgressLayout(this.documentAttach);
                }
            } else {
                int i5 = this.documentAttachType;
                if (i5 == 4) {
                    FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
                    TLRPC$Document tLRPC$Document = this.documentAttach;
                    MessageObject messageObject5 = this.currentMessageObject;
                    if (!messageObject5.shouldEncryptPhotoOrVideo()) {
                        i2 = 0;
                    }
                    fileLoader.loadFile(tLRPC$Document, messageObject5, 1, i2);
                    MessageObject messageObject6 = this.currentMessageObject;
                    if (messageObject6.loadedFileSize > 0) {
                        createLoadingProgressLayout(messageObject6.getDocument());
                    }
                } else if (i4 != 0 || i5 == 0) {
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject), this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, 0L, null, this.currentMessageObject, 0);
                } else if (i5 == 2) {
                    this.photoImage.setForceLoading(true);
                    this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), null, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
                    MessageObject messageObject7 = this.currentMessageObject;
                    messageObject7.gifState = 2.0f;
                    if (messageObject7.loadedFileSize > 0) {
                        createLoadingProgressLayout(messageObject7.getDocument());
                    }
                } else if (i5 == 1) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
                } else if (i5 == 8) {
                    this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), this.currentPhotoFilter, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), "b1", 0L, "jpg", this.currentMessageObject, 1);
                }
            }
            this.currentMessageObject.loadingCancelled = false;
            this.buttonState = 1;
            if (z2) {
                this.videoRadialProgress.setIcon(14, false, z);
            } else {
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
            }
            invalidate();
        } else if (i == 1 && (!this.drawVideoImageButton || z2)) {
            this.photoImage.setForceLoading(false);
            int i6 = this.documentAttachType;
            if (i6 == 3 || i6 == 5) {
                if (!MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                    return;
                }
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            } else if (this.currentMessageObject.isOut() && !this.drawVideoImageButton && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) {
                if (this.radialProgress.getIcon() == 6) {
                    return;
                }
                this.delegate.didPressCancelSendButton(this);
            } else {
                MessageObject messageObject8 = this.currentMessageObject;
                messageObject8.loadingCancelled = true;
                int i7 = this.documentAttachType;
                if (i7 == 2 || i7 == 4 || i7 == 1 || i7 == 8) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else {
                    int i8 = messageObject8.type;
                    if (i8 == 0 || i8 == 1 || i8 == 8 || i8 == 5) {
                        ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                        this.photoImage.cancelLoadImage();
                    } else if (i8 == 9) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    }
                }
                this.buttonState = 0;
                if (z2) {
                    this.videoRadialProgress.setIcon(2, false, z);
                } else {
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                }
                invalidate();
            }
        } else if (i == 2) {
            int i9 = this.documentAttachType;
            if (i9 == 3 || i9 == 5) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                this.currentMessageObject.loadingCancelled = false;
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                invalidate();
                return;
            }
            if (this.isRoundVideo) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject == null || !playingMessageObject.isRoundVideo()) {
                    this.photoImage.setAllowStartAnimation(true);
                    this.photoImage.startAnimation();
                }
            } else {
                this.photoImage.setAllowStartAnimation(true);
                this.photoImage.startAnimation();
            }
            this.currentMessageObject.gifState = 0.0f;
            this.buttonState = -1;
            this.radialProgress.setIcon(getIconForCurrentState(), false, z);
        } else if (i == 3 || i == 0) {
            if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                this.miniButtonState = 1;
                this.radialProgress.setProgress(0.0f, false);
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, z);
            }
            this.delegate.didPressImage(this, 0.0f, 0.0f);
        } else if (i != 4) {
        } else {
            int i10 = this.documentAttachType;
            if (i10 != 3 && i10 != 5) {
                return;
            }
            if ((this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) || this.currentMessageObject.isSendError()) {
                if (this.delegate == null || this.radialProgress.getIcon() == 6) {
                    return;
                }
                this.delegate.didPressCancelSendButton(this);
                return;
            }
            this.currentMessageObject.loadingCancelled = true;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.buttonState = 2;
            this.radialProgress.setIcon(getIconForCurrentState(), false, z);
            invalidate();
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String str, boolean z) {
        int i = this.documentAttachType;
        updateButtonState(true, i == 3 || i == 5, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b8, code lost:
        if ((r7 & 2) != 0) goto L41;
     */
    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onSuccessDownload(java.lang.String r23) {
        /*
            Method dump skipped, instructions count: 454
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onSuccessDownload(java.lang.String):void");
    }

    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !z) {
            return;
        }
        if (setCurrentDiceValue(!z3 && !messageObject.wasUnread) || z2) {
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2.mediaExists || messageObject2.attachPathExists) {
            return;
        }
        int i2 = messageObject2.type;
        if ((i2 != 0 || ((i = this.documentAttachType) != 8 && i != 0 && i != 6)) && i2 != 1) {
            return;
        }
        messageObject2.mediaExists = true;
        updateButtonState(false, true, false);
    }

    public boolean setCurrentDiceValue(boolean z) {
        MessagesController.DiceFrameSuccess diceFrameSuccess;
        if (this.currentMessageObject.isDice()) {
            Drawable drawable = this.photoImage.getDrawable();
            if (drawable instanceof RLottieDrawable) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                String diceEmoji = this.currentMessageObject.getDiceEmoji();
                TLRPC$TL_messages_stickerSet stickerSetByEmojiOrName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(diceEmoji);
                if (stickerSetByEmojiOrName != null) {
                    int diceValue = this.currentMessageObject.getDiceValue();
                    if ("🎰".equals(this.currentMessageObject.getDiceEmoji())) {
                        if (diceValue >= 0 && diceValue <= 64) {
                            ((SlotsDrawable) rLottieDrawable).setDiceNumber(this, diceValue, stickerSetByEmojiOrName, z);
                            if (this.currentMessageObject.isOut()) {
                                rLottieDrawable.setOnFinishCallback(this.diceFinishCallback, ConnectionsManager.DEFAULT_DATACENTER_ID);
                            }
                            this.currentMessageObject.wasUnread = false;
                        }
                        if (!rLottieDrawable.hasBaseDice() && stickerSetByEmojiOrName.documents.size() > 0) {
                            ((SlotsDrawable) rLottieDrawable).setBaseDice(this, stickerSetByEmojiOrName);
                        }
                    } else {
                        if (!rLottieDrawable.hasBaseDice() && stickerSetByEmojiOrName.documents.size() > 0) {
                            TLRPC$Document tLRPC$Document = stickerSetByEmojiOrName.documents.get(0);
                            if (rLottieDrawable.setBaseDice(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Document, true))) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$Document), this.currentMessageObject, this);
                                FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$Document, stickerSetByEmojiOrName, 1, 1);
                            }
                        }
                        if (diceValue >= 0 && diceValue < stickerSetByEmojiOrName.documents.size()) {
                            if (!z && this.currentMessageObject.isOut() && (diceFrameSuccess = MessagesController.getInstance(this.currentAccount).diceSuccess.get(diceEmoji)) != null && diceFrameSuccess.num == diceValue) {
                                rLottieDrawable.setOnFinishCallback(this.diceFinishCallback, diceFrameSuccess.frame);
                            }
                            TLRPC$Document tLRPC$Document2 = stickerSetByEmojiOrName.documents.get(Math.max(diceValue, 0));
                            if (rLottieDrawable.setDiceNumber(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Document2, true), z)) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$Document2), this.currentMessageObject, this);
                                FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$Document2, stickerSetByEmojiOrName, 1, 1);
                            }
                            this.currentMessageObject.wasUnread = false;
                        }
                    }
                } else {
                    MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(diceEmoji, true, true);
                }
            }
            return true;
        }
        return false;
    }

    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
    public void onAnimationReady(ImageReceiver imageReceiver) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || imageReceiver != this.photoImage || !messageObject.isAnimatedSticker()) {
            return;
        }
        this.delegate.setShouldNotRepeatSticker(this.currentMessageObject);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String str, long j, long j2) {
        float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
        this.currentMessageObject.loadedFileSize = j;
        createLoadingProgressLayout(j, j2);
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(min, true);
        } else {
            this.radialProgress.setProgress(min, true);
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.hasMiniProgress != 0) {
                if (this.miniButtonState == 1) {
                    return;
                }
                updateButtonState(false, false, false);
            } else if (this.buttonState == 4) {
            } else {
                updateButtonState(false, false, false);
            }
        } else if (this.hasMiniProgress != 0) {
            if (this.miniButtonState == 1) {
                return;
            }
            updateButtonState(false, false, false);
        } else if (this.buttonState == 1) {
        } else {
            updateButtonState(false, false, false);
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String str, long j, long j2, boolean z) {
        int i;
        float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
        this.currentMessageObject.loadedFileSize = j;
        this.radialProgress.setProgress(min, true);
        if (j == j2 && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) && ((i = this.buttonState) == 1 || (i == 4 && this.documentAttachType == 5))) {
            this.drawRadialCheckBackground = true;
            getIconForCurrentState();
            this.radialProgress.setIcon(6, false, true);
        }
        createLoadingProgressLayout(j, j2);
    }

    private void createLoadingProgressLayout(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return;
        }
        long[] fileProgressSizes = ImageLoader.getInstance().getFileProgressSizes(FileLoader.getDocumentFileName(tLRPC$Document));
        if (fileProgressSizes != null) {
            createLoadingProgressLayout(fileProgressSizes[0], fileProgressSizes[1]);
        } else {
            createLoadingProgressLayout(this.currentMessageObject.loadedFileSize, tLRPC$Document.size);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x006a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void createLoadingProgressLayout(long r17, long r19) {
        /*
            Method dump skipped, instructions count: 263
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.createLoadingProgressLayout(long, long):void");
    }

    @Override // android.view.View
    public void onProvideStructure(ViewStructure viewStructure) {
        super.onProvideStructure(viewStructure);
        if (!this.allowAssistant || Build.VERSION.SDK_INT < 23) {
            return;
        }
        CharSequence charSequence = this.currentMessageObject.messageText;
        if (charSequence != null && charSequence.length() > 0) {
            viewStructure.setText(this.currentMessageObject.messageText);
            return;
        }
        CharSequence charSequence2 = this.currentMessageObject.caption;
        if (charSequence2 == null || charSequence2.length() <= 0) {
            return;
        }
        viewStructure.setText(this.currentMessageObject.caption);
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public ChatMessageCellDelegate getDelegate() {
        return this.delegate;
    }

    public void setAllowAssistant(boolean z) {
        this.allowAssistant = z;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:103:0x023c  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0275  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x02b9  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x02cf  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0308  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x031a  */
    /* JADX WARN: Removed duplicated region for block: B:153:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00b6  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0179  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0183  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01d7  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0207  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0221  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void measureTime(org.telegram.messenger.MessageObject r18) {
        /*
            Method dump skipped, instructions count: 902
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.measureTime(org.telegram.messenger.MessageObject):void");
    }

    private boolean shouldDrawSelectionOverlay() {
        return hasSelectionOverlay() && ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted || this.isHighlightedAnimated)) && !textIsSelectionMode() && ((this.currentMessagesGroup == null || this.drawSelectionBackground) && this.currentBackgroundDrawable != null);
    }

    private Integer getSelectionOverlayColor() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider == null) {
            return null;
        }
        MessageObject messageObject = this.currentMessageObject;
        return resourcesProvider.getColor((messageObject == null || !messageObject.isOut()) ? "chat_inBubbleSelectedOverlay" : "chat_outBubbleSelectedOverlay");
    }

    private boolean hasSelectionOverlay() {
        Integer selectionOverlayColor = getSelectionOverlayColor();
        return (selectionOverlayColor == null || selectionOverlayColor.intValue() == -65536) ? false : true;
    }

    private boolean isDrawSelectionBackground() {
        return ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted)) && !textIsSelectionMode() && !hasSelectionOverlay();
    }

    public boolean isOpenChatByShare(MessageObject messageObject) {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = messageObject.messageOwner.fwd_from;
        return (tLRPC$MessageFwdHeader == null || tLRPC$MessageFwdHeader.saved_from_peer == null) ? false : true;
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2.deleted || messageObject2.isSponsored()) {
            return false;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && !this.currentMessagesGroup.isDocuments && !groupedMessagePosition.last) {
            return false;
        }
        return messageObject.needDrawShareButton();
    }

    public boolean isInsideBackground(float f, float f2) {
        if (this.currentBackgroundDrawable != null) {
            int i = this.backgroundDrawableLeft;
            if (f >= i && f <= i + this.backgroundDrawableRight) {
                return true;
            }
        }
        return false;
    }

    private void updateCurrentUserAndChat() {
        TLRPC$Peer tLRPC$Peer;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (tLRPC$MessageFwdHeader != null && (tLRPC$MessageFwdHeader.from_id instanceof TLRPC$TL_peerChannel) && this.currentMessageObject.getDialogId() == clientUserId) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$MessageFwdHeader.from_id.channel_id));
        } else if (tLRPC$MessageFwdHeader != null && (tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) != null) {
            long j = tLRPC$Peer.user_id;
            if (j != 0) {
                TLRPC$Peer tLRPC$Peer2 = tLRPC$MessageFwdHeader.from_id;
                if (tLRPC$Peer2 instanceof TLRPC$TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer2.user_id));
                } else {
                    this.currentUser = messagesController.getUser(Long.valueOf(j));
                }
            } else if (tLRPC$Peer.channel_id != 0) {
                if (this.currentMessageObject.isSavedFromMegagroup()) {
                    TLRPC$Peer tLRPC$Peer3 = tLRPC$MessageFwdHeader.from_id;
                    if (tLRPC$Peer3 instanceof TLRPC$TL_peerUser) {
                        this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer3.user_id));
                        return;
                    }
                }
                this.currentChat = messagesController.getChat(Long.valueOf(tLRPC$MessageFwdHeader.saved_from_peer.channel_id));
            } else {
                long j2 = tLRPC$Peer.chat_id;
                if (j2 == 0) {
                    return;
                }
                TLRPC$Peer tLRPC$Peer4 = tLRPC$MessageFwdHeader.from_id;
                if (tLRPC$Peer4 instanceof TLRPC$TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$Peer4.user_id));
                } else {
                    this.currentChat = messagesController.getChat(Long.valueOf(j2));
                }
            }
        } else if (tLRPC$MessageFwdHeader != null && (tLRPC$MessageFwdHeader.from_id instanceof TLRPC$TL_peerUser) && (tLRPC$MessageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            this.currentUser = messagesController.getUser(Long.valueOf(tLRPC$MessageFwdHeader.from_id.user_id));
        } else if (tLRPC$MessageFwdHeader != null && !TextUtils.isEmpty(tLRPC$MessageFwdHeader.from_name) && (tLRPC$MessageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
            this.currentUser = tLRPC$TL_user;
            tLRPC$TL_user.first_name = tLRPC$MessageFwdHeader.from_name;
        } else {
            long fromChatId = this.currentMessageObject.getFromChatId();
            if (DialogObject.isUserDialog(fromChatId) && !this.currentMessageObject.messageOwner.post) {
                this.currentUser = messagesController.getUser(Long.valueOf(fromChatId));
            } else if (DialogObject.isChatDialog(fromChatId)) {
                this.currentChat = messagesController.getChat(Long.valueOf(-fromChatId));
            } else {
                TLRPC$Message tLRPC$Message = this.currentMessageObject.messageOwner;
                if (!tLRPC$Message.post) {
                    return;
                }
                this.currentChat = messagesController.getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id));
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(16:258|(2:262|(1:264))|265|(1:267)(2:268|(1:270))|271|(6:(10:277|(3:279|(3:281|(1:283)(1:284)|285)(1:286)|287)(1:288)|(1:290)|(1:307)(3:302|(1:304)(1:305)|306)|308|(2:310|(2:312|(2:314|(1:316)(1:317))(1:318))(1:319))(2:320|(2:324|(2:326|(1:328))(2:329|(2:331|(1:333))(2:334|(1:336)))))|(1:338)|339|(1:341)(2:342|(1:344)(3:345|(4:347|(1:349)|350|(1:352))(2:354|(1:364)(4:358|(1:360)|361|(1:363)))|353))|365)(6:366|(1:368)(2:369|(1:371)(2:372|(1:374)))|375|(1:377)|378|(7:385|(1:(1:388)(1:389))(1:(1:391)(1:392))|393|(1:395)(1:396)|397|(1:403)|404)(15:384|(1:407)|458|408|409|450|410|(1:412)(1:413)|414|(2:416|(1:418))|456|421|(1:423)(1:424)|425|(8:427|(3:429|(2:431|466)(1:467)|432)|465|(1:434)|435|(1:437)|438|(1:442))))|456|421|(0)(0)|425|(0))|405|(0)|458|408|409|450|410|(0)(0)|414|(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(21:258|(2:262|(1:264))|265|(1:267)(2:268|(1:270))|271|(10:277|(3:279|(3:281|(1:283)(1:284)|285)(1:286)|287)(1:288)|(1:290)|(1:307)(3:302|(1:304)(1:305)|306)|308|(2:310|(2:312|(2:314|(1:316)(1:317))(1:318))(1:319))(2:320|(2:324|(2:326|(1:328))(2:329|(2:331|(1:333))(2:334|(1:336)))))|(1:338)|339|(1:341)(2:342|(1:344)(3:345|(4:347|(1:349)|350|(1:352))(2:354|(1:364)(4:358|(1:360)|361|(1:363)))|353))|365)(6:366|(1:368)(2:369|(1:371)(2:372|(1:374)))|375|(1:377)|378|(7:385|(1:(1:388)(1:389))(1:(1:391)(1:392))|393|(1:395)(1:396)|397|(1:403)|404)(15:384|(1:407)|458|408|409|450|410|(1:412)(1:413)|414|(2:416|(1:418))|456|421|(1:423)(1:424)|425|(8:427|(3:429|(2:431|466)(1:467)|432)|465|(1:434)|435|(1:437)|438|(1:442))))|405|(0)|458|408|409|450|410|(0)(0)|414|(0)|456|421|(0)(0)|425|(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(22:66|(1:68)|69|(1:78)(2:75|(17:77|86|104|(1:106)(1:(1:108)(1:109))|110|(1:112)(1:113)|114|(8:116|(1:118)|119|(1:121)(3:123|(1:125)(1:126)|127)|122|128|(1:130)(1:131)|132)(1:133)|452|134|135|464|136|(3:138|(1:140)|141)(1:142)|(1:144)(1:145)|148|(1:150)))|79|(2:87|(16:103|104|(0)(0)|110|(0)(0)|114|(0)(0)|452|134|135|464|136|(0)(0)|(0)(0)|148|(0))(3:99|(1:101)|102))(1:85)|86|104|(0)(0)|110|(0)(0)|114|(0)(0)|452|134|135|464|136|(0)(0)|(0)(0)|148|(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x0433, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x0434, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:419:0x0c5f, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:420:0x0c60, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0283  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0286  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x02a3  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x02a6  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x02b1  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x039f  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x03d4 A[Catch: Exception -> 0x0433, TryCatch #7 {Exception -> 0x0433, blocks: (B:136:0x03b1, B:138:0x03d4, B:140:0x03e8, B:141:0x03f3, B:142:0x03fc, B:144:0x0400, B:145:0x042f), top: B:464:0x03b1 }] */
    /* JADX WARN: Removed duplicated region for block: B:142:0x03fc A[Catch: Exception -> 0x0433, TryCatch #7 {Exception -> 0x0433, blocks: (B:136:0x03b1, B:138:0x03d4, B:140:0x03e8, B:141:0x03f3, B:142:0x03fc, B:144:0x0400, B:145:0x042f), top: B:464:0x03b1 }] */
    /* JADX WARN: Removed duplicated region for block: B:144:0x0400 A[Catch: Exception -> 0x0433, TryCatch #7 {Exception -> 0x0433, blocks: (B:136:0x03b1, B:138:0x03d4, B:140:0x03e8, B:141:0x03f3, B:142:0x03fc, B:144:0x0400, B:145:0x042f), top: B:464:0x03b1 }] */
    /* JADX WARN: Removed duplicated region for block: B:145:0x042f A[Catch: Exception -> 0x0433, TRY_LEAVE, TryCatch #7 {Exception -> 0x0433, blocks: (B:136:0x03b1, B:138:0x03d4, B:140:0x03e8, B:141:0x03f3, B:142:0x03fc, B:144:0x0400, B:145:0x042f), top: B:464:0x03b1 }] */
    /* JADX WARN: Removed duplicated region for block: B:150:0x0440  */
    /* JADX WARN: Removed duplicated region for block: B:407:0x0be8  */
    /* JADX WARN: Removed duplicated region for block: B:412:0x0c0e  */
    /* JADX WARN: Removed duplicated region for block: B:413:0x0c11  */
    /* JADX WARN: Removed duplicated region for block: B:416:0x0c1c A[Catch: Exception -> 0x0c5f, TryCatch #0 {Exception -> 0x0c5f, blocks: (B:410:0x0c0a, B:414:0x0c12, B:416:0x0c1c, B:418:0x0c3d), top: B:450:0x0c0a }] */
    /* JADX WARN: Removed duplicated region for block: B:423:0x0c67  */
    /* JADX WARN: Removed duplicated region for block: B:424:0x0c6a  */
    /* JADX WARN: Removed duplicated region for block: B:427:0x0c75 A[Catch: Exception -> 0x0d09, TryCatch #3 {Exception -> 0x0d09, blocks: (B:421:0x0c63, B:425:0x0c6b, B:427:0x0c75, B:429:0x0c8c, B:431:0x0c97, B:432:0x0c9c, B:434:0x0ca1, B:435:0x0caa, B:437:0x0cc7, B:438:0x0ce8, B:440:0x0cf5, B:442:0x0cff), top: B:456:0x0c63 }] */
    /* JADX WARN: Type inference failed for: r0v18, types: [android.text.SpannableStringBuilder, java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r0v19 */
    /* JADX WARN: Type inference failed for: r0v34, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.text.StaticLayout[]] */
    /* JADX WARN: Type inference failed for: r11v1, types: [java.lang.CharSequence, java.lang.String] */
    /* JADX WARN: Type inference failed for: r11v2, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r11v4 */
    /* JADX WARN: Type inference failed for: r11v6, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r11v7, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r3v116 */
    /* JADX WARN: Type inference failed for: r3v4, types: [org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v94 */
    /* JADX WARN: Type inference failed for: r3v95 */
    /* JADX WARN: Type inference failed for: r4v100, types: [java.lang.String] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r41) {
        /*
            Method dump skipped, instructions count: 3345
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
    }

    private boolean isNeedAuthorName() {
        return (this.isPinnedChat && this.currentMessageObject.type == 0) || ((!this.pinnedTop || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup)) && this.drawName && this.isChat && (!this.currentMessageObject.isOutOwner() || (this.currentMessageObject.isSupergroup() && this.currentMessageObject.isFromGroup()))) || (this.currentMessageObject.isImportedForward() && this.currentMessageObject.messageOwner.fwd_from.from_id == null);
    }

    private String getAuthorName() {
        TLRPC$Chat tLRPC$Chat;
        String str;
        String str2;
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User != null) {
            return UserObject.getUserName(tLRPC$User);
        }
        TLRPC$Chat tLRPC$Chat2 = this.currentChat;
        if (tLRPC$Chat2 != null) {
            return tLRPC$Chat2.title;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.isSponsored()) {
            return "DELETED";
        }
        TLRPC$ChatInvite tLRPC$ChatInvite = this.currentMessageObject.sponsoredChatInvite;
        return (tLRPC$ChatInvite == null || (str2 = tLRPC$ChatInvite.title) == null) ? (tLRPC$ChatInvite == null || (tLRPC$Chat = tLRPC$ChatInvite.chat) == null || (str = tLRPC$Chat.title) == null) ? "" : str : str2;
    }

    private String getForwardedMessageText(MessageObject messageObject) {
        if (this.hasPsaHint) {
            String string = LocaleController.getString("PsaMessage_" + messageObject.messageOwner.fwd_from.psa_type);
            return string == null ? LocaleController.getString("PsaMessageDefault", R.string.PsaMessageDefault) : string;
        }
        return LocaleController.getString("ForwardedMessage", R.string.ForwardedMessage);
    }

    public int getExtraInsetHeight() {
        int i = this.addedCaptionHeight;
        if (this.drawCommentButton) {
            i += AndroidUtilities.dp(shouldDrawTimeOnMedia() ? 41.3f : 43.0f);
        }
        return (this.reactionsLayoutInBubble.isEmpty || !this.currentMessageObject.shouldDrawReactionsInLayout()) ? i : i + this.reactionsLayoutInBubble.totalHeight;
    }

    public ImageReceiver getAvatarImage() {
        if (this.isAvatarVisible) {
            return this.avatarImage;
        }
        return null;
    }

    public float getCheckBoxTranslation() {
        return this.checkBoxTranslation;
    }

    public boolean shouldDrawAlphaLayer() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        return (groupedMessages == null || !groupedMessages.transitionParams.backgroundChangeBounds) && getAlpha() != 1.0f;
    }

    public float getCaptionX() {
        return this.captionX;
    }

    public boolean isDrawPinnedBottom() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        return this.mediaBackground || this.drawPinnedBottom || (groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments);
    }

    public void drawCheckBox(Canvas canvas) {
        float f;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSending() || this.currentMessageObject.isSendError() || this.checkBox == null) {
            return;
        }
        if (!this.checkBoxVisible && !this.checkBoxAnimationInProgress) {
            return;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        canvas.save();
        float y = getY();
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.messages.size() > 1) {
            f = (getTop() + this.currentMessagesGroup.transitionParams.offsetTop) - getTranslationY();
        } else {
            f = y + this.transitionParams.deltaTop;
        }
        canvas.translate(0.0f, f + this.transitionYOffsetForDrawables);
        this.checkBox.draw(canvas);
        canvas.restore();
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0046  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setBackgroundTopY(boolean r14) {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
        L2:
            r2 = 2
            if (r1 >= r2) goto L6f
            r2 = 1
            if (r1 != r2) goto Lb
            if (r14 != 0) goto Lb
            return
        Lb:
            if (r1 != 0) goto L10
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r13.currentBackgroundDrawable
            goto L12
        L10:
            org.telegram.ui.ActionBar.Theme$MessageDrawable r3 = r13.currentBackgroundSelectedDrawable
        L12:
            r4 = r3
            if (r4 != 0) goto L16
            goto L6c
        L16:
            int r3 = r13.parentWidth
            int r5 = r13.parentHeight
            if (r5 != 0) goto L3d
            int r3 = r13.getParentWidth()
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.y
            android.view.ViewParent r6 = r13.getParent()
            boolean r6 = r6 instanceof android.view.View
            if (r6 == 0) goto L3d
            android.view.ViewParent r3 = r13.getParent()
            android.view.View r3 = (android.view.View) r3
            int r5 = r3.getMeasuredWidth()
            int r3 = r3.getMeasuredHeight()
            r7 = r3
            r6 = r5
            goto L3f
        L3d:
            r6 = r3
            r7 = r5
        L3f:
            if (r14 == 0) goto L46
            float r3 = r13.getY()
            goto L4b
        L46:
            int r3 = r13.getTop()
            float r3 = (float) r3
        L4b:
            float r5 = r13.parentViewTopOffset
            float r3 = r3 + r5
            int r3 = (int) r3
            int r8 = (int) r5
            int r9 = r13.blurredViewTopOffset
            int r10 = r13.blurredViewBottomOffset
            boolean r11 = r13.pinnedTop
            boolean r5 = r13.pinnedBottom
            if (r5 != 0) goto L67
            org.telegram.ui.Cells.ChatMessageCell$TransitionParams r5 = r13.transitionParams
            float r5 = r5.changePinnedBottomProgress
            r12 = 1065353216(0x3f800000, float:1.0)
            int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r5 == 0) goto L65
            goto L67
        L65:
            r12 = 0
            goto L68
        L67:
            r12 = 1
        L68:
            r5 = r3
            r4.setTop(r5, r6, r7, r8, r9, r10, r11, r12)
        L6c:
            int r1 = r1 + 1
            goto L2
        L6f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setBackgroundTopY(boolean):void");
    }

    public void setBackgroundTopY(int i) {
        int i2;
        int i3;
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        int i4 = this.parentWidth;
        int i5 = this.parentHeight;
        if (i5 == 0) {
            i4 = getParentWidth();
            i5 = AndroidUtilities.displaySize.y;
            if (getParent() instanceof View) {
                View view = (View) getParent();
                i3 = view.getMeasuredWidth();
                i2 = view.getMeasuredHeight();
                float f = this.parentViewTopOffset;
                messageDrawable.setTop((int) (i + f), i3, i2, (int) f, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, !this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
            }
        }
        i2 = i5;
        i3 = i4;
        float f2 = this.parentViewTopOffset;
        messageDrawable.setTop((int) (i + f2), i3, i2, (int) f2, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, !this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
    }

    public void setDrawableBoundsInner(Drawable drawable, int i, int i2, int i3, int i4) {
        if (drawable != null) {
            float f = i4 + i2;
            TransitionParams transitionParams = this.transitionParams;
            float f2 = transitionParams.deltaBottom;
            this.transitionYOffsetForDrawables = (f + f2) - ((int) (f + f2));
            drawable.setBounds((int) (i + transitionParams.deltaLeft), (int) (i2 + transitionParams.deltaTop), (int) (i + i3 + transitionParams.deltaRight), (int) (f + f2));
        }
    }

    @Override // android.view.View
    @SuppressLint({"WrongCall"})
    protected void onDraw(Canvas canvas) {
        int i;
        boolean z;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Theme.MessageDrawable messageDrawable;
        int i2;
        int i3;
        int i4;
        int i5;
        if (this.currentMessageObject == null) {
            return;
        }
        boolean z2 = this.wasLayout;
        if (!z2 && !this.animationRunning) {
            forceLayout();
            return;
        }
        if (!z2) {
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        }
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextOut"));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor("chat_messageTextOut"));
            Theme.chat_msgGameTextPaint.linkColor = getThemedColor("chat_messageLinkOut");
            Theme.chat_replyTextPaint.linkColor = getThemedColor("chat_messageLinkOut");
            Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkOut");
        } else {
            Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextIn"));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor("chat_messageTextIn"));
            Theme.chat_msgGameTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
            Theme.chat_replyTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
            Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
        }
        if (this.documentAttach != null) {
            int i6 = this.documentAttachType;
            if (i6 == 3) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarWaveform.setColors(getThemedColor("chat_outVoiceSeekbar"), getThemedColor("chat_outVoiceSeekbarFill"), getThemedColor("chat_outVoiceSeekbarSelected"));
                    this.seekBar.setColors(getThemedColor("chat_outAudioSeekbar"), getThemedColor("chat_outAudioCacheSeekbar"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarSelected"));
                } else {
                    this.seekBarWaveform.setColors(getThemedColor("chat_inVoiceSeekbar"), getThemedColor("chat_inVoiceSeekbarFill"), getThemedColor("chat_inVoiceSeekbarSelected"));
                    this.seekBar.setColors(getThemedColor("chat_inAudioSeekbar"), getThemedColor("chat_inAudioCacheSeekbar"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarSelected"));
                }
            } else if (i6 == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBar.setColors(getThemedColor("chat_outAudioSeekbar"), getThemedColor("chat_outAudioCacheSeekbar"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarFill"), getThemedColor("chat_outAudioSeekbarSelected"));
                } else {
                    this.seekBar.setColors(getThemedColor("chat_inAudioSeekbar"), getThemedColor("chat_inAudioCacheSeekbar"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarFill"), getThemedColor("chat_inAudioSeekbarSelected"));
                }
            }
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type == 5) {
            Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
        } else if (this.mediaBackground) {
            if (messageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor("chat_mediaTimeText"));
            }
        } else if (messageObject.isOutOwner()) {
            Theme.chat_timePaint.setColor(getThemedColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
        } else {
            Theme.chat_timePaint.setColor(getThemedColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
        }
        drawBackgroundInternal(canvas, false);
        long j = 17;
        if (this.isHighlightedAnimated) {
            long currentTimeMillis = System.currentTimeMillis();
            long abs = Math.abs(currentTimeMillis - this.lastHighlightProgressTime);
            if (abs > 17) {
                abs = 17;
            }
            int i7 = (int) (this.highlightProgress - abs);
            this.highlightProgress = i7;
            this.lastHighlightProgressTime = currentTimeMillis;
            if (i7 <= 0) {
                this.highlightProgress = 0;
                this.isHighlightedAnimated = false;
            }
            invalidate();
            if (getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        if (this.alphaInternal != 1.0f) {
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            Theme.MessageDrawable messageDrawable2 = this.currentBackgroundDrawable;
            if (messageDrawable2 != null) {
                i5 = messageDrawable2.getBounds().top;
                i4 = this.currentBackgroundDrawable.getBounds().bottom;
                i3 = this.currentBackgroundDrawable.getBounds().left;
                i2 = this.currentBackgroundDrawable.getBounds().right;
            } else {
                i2 = measuredWidth;
                i3 = 0;
                i4 = measuredHeight;
                i5 = 0;
            }
            if (this.drawSideButton != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    i3 -= AndroidUtilities.dp(40.0f);
                } else {
                    i2 += AndroidUtilities.dp(40.0f);
                }
            }
            if (getY() < 0.0f) {
                i5 = (int) (-getY());
            }
            float y = getY() + getMeasuredHeight();
            int i8 = this.parentHeight;
            if (y > i8) {
                i4 = (int) (i8 - getY());
            }
            this.rect.set(i3, i5, i2, i4);
            i = canvas.saveLayerAlpha(this.rect, (int) (this.alphaInternal * 255.0f), 31);
        } else {
            i = Integer.MIN_VALUE;
        }
        if (!this.transitionParams.animateBackgroundBoundsInner || (messageDrawable = this.currentBackgroundDrawable) == null || this.isRoundVideo) {
            z = false;
        } else {
            Rect bounds = messageDrawable.getBounds();
            canvas.save();
            canvas.clipRect(bounds.left + AndroidUtilities.dp(4.0f), bounds.top + AndroidUtilities.dp(4.0f), bounds.right - AndroidUtilities.dp(4.0f), bounds.bottom - AndroidUtilities.dp(4.0f));
            z = true;
        }
        drawContent(canvas);
        if (z) {
            canvas.restore();
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null || chatMessageCellDelegate.canDrawOutboundsContent() || getAlpha() != 1.0f) {
            drawOutboundsContent(canvas);
        }
        if (this.replyNameLayout != null) {
            float f = 12.0f;
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                if (this.currentMessageObject.isOutOwner()) {
                    int dp = AndroidUtilities.dp(23.0f);
                    this.replyStartX = dp;
                    if (this.isPlayingRound) {
                        this.replyStartX = dp - (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize);
                    }
                } else if (this.currentMessageObject.type == 5) {
                    this.replyStartX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(4.0f);
                } else {
                    this.replyStartX = this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(17.0f);
                }
                if (this.drawForwardedName) {
                    this.replyStartY = this.forwardNameY + AndroidUtilities.dp(38.0f);
                } else {
                    this.replyStartY = AndroidUtilities.dp(12.0f);
                }
            } else {
                if (this.currentMessageObject.isOutOwner()) {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                } else if (this.mediaBackground) {
                    this.replyStartX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                } else {
                    int i9 = this.backgroundDrawableLeft;
                    if (!this.drawPinnedBottom) {
                        f = 18.0f;
                    }
                    this.replyStartX = i9 + AndroidUtilities.dp(f) + getExtraTextX();
                }
                this.replyStartY = AndroidUtilities.dp(12 + ((!this.drawForwardedName || this.forwardedNameLayout[0] == null) ? 0 : 36) + ((!this.drawNameLayout || this.nameLayout == null) ? 0 : 20));
            }
        }
        if (this.currentPosition == null && !this.transitionParams.animateBackgroundBoundsInner && (!this.enterTransitionInProgress || this.currentMessageObject.isVoice())) {
            drawNamesLayout(canvas, 1.0f);
        }
        if ((!this.autoPlayingMedia || !MediaController.getInstance().isPlayingMessageAndReadyToDraw(this.currentMessageObject) || this.isRoundVideo) && !this.transitionParams.animateBackgroundBoundsInner) {
            drawOverlays(canvas);
        }
        if ((this.drawTime || !this.mediaBackground) && !this.forceNotDrawTime && !this.transitionParams.animateBackgroundBoundsInner && (!this.enterTransitionInProgress || this.currentMessageObject.isVoice())) {
            drawTime(canvas, 1.0f, false);
        }
        if ((this.controlsAlpha != 1.0f || this.timeAlpha != 1.0f) && this.currentMessageObject.type != 5) {
            long currentTimeMillis2 = System.currentTimeMillis();
            long abs2 = Math.abs(this.lastControlsAlphaChangeTime - currentTimeMillis2);
            if (abs2 <= 17) {
                j = abs2;
            }
            long j2 = this.totalChangeTime + j;
            this.totalChangeTime = j2;
            if (j2 > 100) {
                this.totalChangeTime = 100L;
            }
            this.lastControlsAlphaChangeTime = currentTimeMillis2;
            if (this.controlsAlpha != 1.0f) {
                this.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
            }
            if (this.timeAlpha != 1.0f) {
                this.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
            }
            invalidate();
            if (this.forceNotDrawTime && (groupedMessagePosition = this.currentPosition) != null && groupedMessagePosition.last && getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        if (this.drawBackground && shouldDrawSelectionOverlay() && this.currentMessagesGroup == null) {
            if (this.selectionOverlayPaint == null) {
                this.selectionOverlayPaint = new Paint(1);
            }
            this.selectionOverlayPaint.setColor(getSelectionOverlayColor().intValue());
            int alpha = this.selectionOverlayPaint.getAlpha();
            this.selectionOverlayPaint.setAlpha((int) (alpha * getHighlightAlpha() * getAlpha()));
            if (this.selectionOverlayPaint.getAlpha() > 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams, this.selectionOverlayPaint);
                canvas.restore();
            }
            this.selectionOverlayPaint.setAlpha(alpha);
        }
        if (i != Integer.MIN_VALUE) {
            canvas.restoreToCount(i);
        }
        updateSelectionTextPosition();
    }

    @SuppressLint({"WrongCall"})
    public void drawBackgroundInternal(Canvas canvas, boolean z) {
        float f;
        Drawable drawable;
        boolean z2;
        Theme.MessageDrawable messageDrawable;
        int i;
        int i2;
        MessageObject.GroupedMessages groupedMessages;
        int i3;
        int i4;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Drawable drawable2;
        int i5;
        int i6;
        int i7;
        int i8;
        Drawable drawable3;
        int i9;
        int i10;
        int i11;
        int i12;
        if (this.currentMessageObject == null) {
            return;
        }
        boolean z3 = this.wasLayout;
        if (!z3 && !this.animationRunning) {
            forceLayout();
            return;
        }
        if (!z3) {
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
        boolean z4 = groupedMessagePosition2 != null && (groupedMessagePosition2.flags & 8) == 0 && this.currentMessagesGroup.isDocuments && !this.drawPinnedBottom;
        if (this.currentMessageObject.isOutOwner()) {
            if (this.transitionParams.changePinnedBottomProgress >= 1.0f && !this.mediaBackground && !this.drawPinnedBottom && !z4) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutSelected");
                this.transitionParams.drawPinnedBottomBackground = false;
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMedia");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMediaSelected");
                this.transitionParams.drawPinnedBottomBackground = true;
            }
            setBackgroundTopY(true);
            if (isDrawSelectionBackground() && (this.currentPosition == null || getBackground() != null)) {
                drawable3 = this.currentBackgroundSelectedDrawable.getShadowDrawable();
            } else {
                drawable3 = this.currentBackgroundDrawable.getShadowDrawable();
            }
            Drawable drawable4 = drawable3;
            this.backgroundDrawableLeft = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
            int dp = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            this.backgroundDrawableRight = dp;
            MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
            if (groupedMessages2 != null && !groupedMessages2.isDocuments && !this.currentPosition.edge) {
                this.backgroundDrawableRight = dp + AndroidUtilities.dp(10.0f);
            }
            int i13 = this.backgroundDrawableLeft;
            if (!z4 && this.transitionParams.changePinnedBottomProgress != 1.0f) {
                if (!this.mediaBackground) {
                    this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                }
            } else if (!this.mediaBackground && this.drawPinnedBottom) {
                this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
            if (groupedMessagePosition3 != null) {
                if ((groupedMessagePosition3.flags & 2) == 0) {
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 1) == 0) {
                    i13 -= AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 4) == 0) {
                    i11 = 0 - AndroidUtilities.dp(SharedConfig.bubbleRadius + 3);
                    i10 = AndroidUtilities.dp(SharedConfig.bubbleRadius + 3) + 0;
                } else {
                    i11 = 0;
                    i10 = 0;
                }
                if ((this.currentPosition.flags & 8) == 0) {
                    i10 += AndroidUtilities.dp(SharedConfig.bubbleRadius + 3);
                }
                i9 = i13;
            } else {
                i9 = i13;
                i11 = 0;
                i10 = 0;
            }
            boolean z5 = this.drawPinnedBottom;
            if (z5 && this.drawPinnedTop) {
                i12 = 0;
            } else if (z5) {
                i12 = AndroidUtilities.dp(1.0f);
            } else {
                i12 = AndroidUtilities.dp(2.0f);
            }
            int dp2 = i11 + (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f));
            this.backgroundDrawableTop = dp2;
            int i14 = (this.layoutHeight - i12) + i10;
            if (z4) {
                setDrawableBoundsInner(this.currentBackgroundDrawable, i9, dp2 - i11, this.backgroundDrawableRight, (i14 - i10) + 10);
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight - AndroidUtilities.dp(6.0f), i14);
            } else {
                int i15 = i9;
                setDrawableBoundsInner(this.currentBackgroundDrawable, i15, dp2, this.backgroundDrawableRight, i14);
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, i15, this.backgroundDrawableTop, this.backgroundDrawableRight, i14);
            }
            setDrawableBoundsInner(drawable4, i9, this.backgroundDrawableTop, this.backgroundDrawableRight, i14);
            drawable = drawable4;
            f = 1.0f;
        } else {
            f = 1.0f;
            if (this.transitionParams.changePinnedBottomProgress >= 1.0f && !this.mediaBackground && !this.drawPinnedBottom && !z4) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInSelected");
                this.transitionParams.drawPinnedBottomBackground = false;
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
                this.currentBackgroundSelectedDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMediaSelected");
                this.transitionParams.drawPinnedBottomBackground = true;
            }
            setBackgroundTopY(true);
            if (isDrawSelectionBackground() && (this.currentPosition == null || getBackground() != null)) {
                drawable2 = this.currentBackgroundSelectedDrawable.getShadowDrawable();
            } else {
                drawable2 = this.currentBackgroundDrawable.getShadowDrawable();
            }
            Drawable drawable5 = drawable2;
            this.backgroundDrawableLeft = AndroidUtilities.dp(((!this.isChat || !this.isAvatarVisible) ? 0 : 48) + (!this.mediaBackground ? 3 : 9));
            this.backgroundDrawableRight = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            MessageObject.GroupedMessages groupedMessages3 = this.currentMessagesGroup;
            if (groupedMessages3 != null && !groupedMessages3.isDocuments) {
                if (!this.currentPosition.edge) {
                    this.backgroundDrawableLeft -= AndroidUtilities.dp(10.0f);
                    this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    this.backgroundDrawableLeft += (int) Math.ceil((i8 / 1000.0f) * getGroupPhotosWidth());
                }
            }
            boolean z6 = this.mediaBackground;
            if ((!z6 && this.drawPinnedBottom) || (!z4 && this.transitionParams.changePinnedBottomProgress != 1.0f)) {
                if (this.drawPinnedBottom || !z6) {
                    this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                }
                if (!this.mediaBackground) {
                    this.backgroundDrawableLeft += AndroidUtilities.dp(6.0f);
                }
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition4 = this.currentPosition;
            if (groupedMessagePosition4 != null) {
                if ((groupedMessagePosition4.flags & 2) == 0) {
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 1) == 0) {
                    this.backgroundDrawableLeft -= AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                    this.backgroundDrawableRight += AndroidUtilities.dp(SharedConfig.bubbleRadius + 2);
                }
                if ((this.currentPosition.flags & 4) == 0) {
                    i6 = 0 - AndroidUtilities.dp(SharedConfig.bubbleRadius + 3);
                    i5 = AndroidUtilities.dp(SharedConfig.bubbleRadius + 3) + 0;
                } else {
                    i6 = 0;
                    i5 = 0;
                }
                if ((this.currentPosition.flags & 8) == 0) {
                    i5 += AndroidUtilities.dp(SharedConfig.bubbleRadius + 4);
                }
            } else {
                i6 = 0;
                i5 = 0;
            }
            boolean z7 = this.drawPinnedBottom;
            if (z7 && this.drawPinnedTop) {
                i7 = 0;
            } else if (z7) {
                i7 = AndroidUtilities.dp(1.0f);
            } else {
                i7 = AndroidUtilities.dp(2.0f);
            }
            int dp3 = (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f)) + i6;
            this.backgroundDrawableTop = dp3;
            int i16 = (this.layoutHeight - i7) + i5;
            setDrawableBoundsInner(this.currentBackgroundDrawable, this.backgroundDrawableLeft, dp3, this.backgroundDrawableRight, i16);
            if (z4) {
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, AndroidUtilities.dp(6.0f) + this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight - AndroidUtilities.dp(6.0f), i16);
            } else {
                setDrawableBoundsInner(this.currentBackgroundSelectedDrawable, this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight, i16);
            }
            setDrawableBoundsInner(drawable5, this.backgroundDrawableLeft, this.backgroundDrawableTop, this.backgroundDrawableRight, i16);
            drawable = drawable5;
        }
        if (!this.currentMessageObject.isOutOwner() && this.transitionParams.changePinnedBottomProgress != f && !this.mediaBackground && !this.drawPinnedBottom) {
            this.backgroundDrawableLeft -= AndroidUtilities.dp(6.0f);
            this.backgroundDrawableRight += AndroidUtilities.dp(6.0f);
        }
        if (this.hasPsaHint) {
            MessageObject.GroupedMessagePosition groupedMessagePosition5 = this.currentPosition;
            if (groupedMessagePosition5 == null || (groupedMessagePosition5.flags & 2) != 0) {
                i3 = this.currentBackgroundDrawable.getBounds().right;
            } else {
                int groupPhotosWidth = getGroupPhotosWidth();
                i3 = 0;
                for (int i17 = 0; i17 < this.currentMessagesGroup.posArray.size(); i17++) {
                    if (this.currentMessagesGroup.posArray.get(i17).minY != 0) {
                        break;
                    }
                    double d = i3;
                    double ceil = Math.ceil(((groupedMessagePosition.pw + groupedMessagePosition.leftSpanOffset) / 1000.0f) * groupPhotosWidth);
                    Double.isNaN(d);
                    i3 = (int) (d + ceil);
                }
            }
            Drawable drawable6 = Theme.chat_psaHelpDrawable[this.currentMessageObject.isOutOwner() ? 1 : 0];
            if (this.currentMessageObject.type == 5) {
                i4 = AndroidUtilities.dp(12.0f);
            } else {
                i4 = AndroidUtilities.dp((this.drawNameLayout ? 19 : 0) + 10);
            }
            this.psaHelpX = (i3 - drawable6.getIntrinsicWidth()) - AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 20.0f : 14.0f);
            this.psaHelpY = i4 + AndroidUtilities.dp(4.0f);
        }
        boolean z8 = this.checkBoxVisible;
        if (z8 || this.checkBoxAnimationInProgress) {
            if ((z8 && this.checkBoxAnimationProgress == f) || (!z8 && this.checkBoxAnimationProgress == 0.0f)) {
                this.checkBoxAnimationInProgress = false;
            }
            this.checkBoxTranslation = (int) Math.ceil((z8 ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
            if (!this.currentMessageObject.isOutOwner()) {
                updateTranslation();
            }
            int dp4 = AndroidUtilities.dp(21.0f);
            this.checkBox.setBounds(AndroidUtilities.dp(-27.0f) + this.checkBoxTranslation, (this.currentBackgroundDrawable.getBounds().bottom - AndroidUtilities.dp(8.0f)) - dp4, dp4, dp4);
            if (this.checkBoxAnimationInProgress) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastCheckBoxAnimationTime;
                this.lastCheckBoxAnimationTime = elapsedRealtime;
                if (this.checkBoxVisible) {
                    float f2 = this.checkBoxAnimationProgress + (((float) j) / 200.0f);
                    this.checkBoxAnimationProgress = f2;
                    if (f2 > f) {
                        this.checkBoxAnimationProgress = f;
                    }
                } else {
                    float f3 = this.checkBoxAnimationProgress - (((float) j) / 200.0f);
                    this.checkBoxAnimationProgress = f3;
                    if (f3 <= 0.0f) {
                        this.checkBoxAnimationProgress = 0.0f;
                    }
                }
                invalidate();
                ((View) getParent()).invalidate();
            }
        }
        if (!z && drawBackgroundInParent()) {
            return;
        }
        if (this.transitionYOffsetForDrawables != 0.0f) {
            canvas.save();
            canvas.translate(0.0f, this.transitionYOffsetForDrawables);
            z2 = true;
        } else {
            z2 = false;
        }
        if (this.drawBackground && this.currentBackgroundDrawable != null && ((this.currentPosition == null || (isDrawSelectionBackground() && (this.currentMessageObject.isMusic() || this.currentMessageObject.isDocument()))) && (!this.enterTransitionInProgress || this.currentMessageObject.isVoice()))) {
            float f4 = this.alphaInternal;
            if (z) {
                f4 *= getAlpha();
            }
            if (hasSelectionOverlay()) {
                this.currentSelectedBackgroundAlpha = 0.0f;
                int i18 = (int) (f4 * 255.0f);
                this.currentBackgroundDrawable.setAlpha(i18);
                this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                if (drawable != null && this.currentPosition == null) {
                    drawable.setAlpha(i18);
                    drawable.draw(canvas);
                }
            } else {
                if (this.isHighlightedAnimated) {
                    this.currentBackgroundDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                    float highlightAlpha = getHighlightAlpha();
                    this.currentSelectedBackgroundAlpha = highlightAlpha;
                    if (this.currentPosition == null) {
                        this.currentBackgroundSelectedDrawable.setAlpha((int) (highlightAlpha * f4 * 255.0f));
                        this.currentBackgroundSelectedDrawable.drawCached(canvas, this.backgroundCacheParams);
                    }
                } else if (this.selectedBackgroundProgress != 0.0f && ((groupedMessages = this.currentMessagesGroup) == null || !groupedMessages.isDocuments)) {
                    this.currentBackgroundDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                    float f5 = this.selectedBackgroundProgress;
                    this.currentSelectedBackgroundAlpha = f5;
                    this.currentBackgroundSelectedDrawable.setAlpha((int) (f5 * f4 * 255.0f));
                    this.currentBackgroundSelectedDrawable.drawCached(canvas, this.backgroundCacheParams);
                    if (this.currentBackgroundDrawable.getGradientShader() == null) {
                        drawable = null;
                    }
                } else if (isDrawSelectionBackground() && (this.currentPosition == null || this.currentMessageObject.isMusic() || this.currentMessageObject.isDocument() || getBackground() != null)) {
                    if (this.currentPosition != null) {
                        canvas.save();
                        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    }
                    this.currentSelectedBackgroundAlpha = f;
                    this.currentBackgroundSelectedDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundSelectedDrawable.drawCached(canvas, this.backgroundCacheParams);
                    if (this.currentPosition != null) {
                        canvas.restore();
                    }
                } else {
                    this.currentSelectedBackgroundAlpha = 0.0f;
                    this.currentBackgroundDrawable.setAlpha((int) (f4 * 255.0f));
                    this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
                }
                if (drawable != null && this.currentPosition == null) {
                    drawable.setAlpha((int) (f4 * 255.0f));
                    drawable.draw(canvas);
                }
                if (this.transitionParams.changePinnedBottomProgress != f && this.currentPosition == null) {
                    if (this.currentMessageObject.isOutOwner()) {
                        Theme.MessageDrawable messageDrawable2 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
                        Rect bounds = this.currentBackgroundDrawable.getBounds();
                        messageDrawable2.setBounds(bounds.left, bounds.top, bounds.right + AndroidUtilities.dp(6.0f), bounds.bottom);
                        canvas.save();
                        canvas.clipRect(bounds.right - AndroidUtilities.dp(12.0f), bounds.bottom - AndroidUtilities.dp(16.0f), bounds.right + AndroidUtilities.dp(12.0f), bounds.bottom);
                        int i19 = this.parentWidth;
                        int i20 = this.parentHeight;
                        if (i20 == 0) {
                            i19 = getParentWidth();
                            i20 = AndroidUtilities.displaySize.y;
                            if (getParent() instanceof View) {
                                View view = (View) getParent();
                                int measuredWidth = view.getMeasuredWidth();
                                i = view.getMeasuredHeight();
                                i2 = measuredWidth;
                                float y = getY();
                                float f6 = this.parentViewTopOffset;
                                messageDrawable2.setTop((int) (y + f6), i2, i, (int) f6, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom);
                                messageDrawable2.setAlpha((int) (((!this.mediaBackground || this.pinnedBottom) ? f - this.transitionParams.changePinnedBottomProgress : this.transitionParams.changePinnedBottomProgress) * 255.0f));
                                messageDrawable2.draw(canvas);
                                messageDrawable2.setAlpha(255);
                                canvas.restore();
                            }
                        }
                        i2 = i19;
                        i = i20;
                        float y2 = getY();
                        float f62 = this.parentViewTopOffset;
                        messageDrawable2.setTop((int) (y2 + f62), i2, i, (int) f62, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom);
                        messageDrawable2.setAlpha((int) (((!this.mediaBackground || this.pinnedBottom) ? f - this.transitionParams.changePinnedBottomProgress : this.transitionParams.changePinnedBottomProgress) * 255.0f));
                        messageDrawable2.draw(canvas);
                        messageDrawable2.setAlpha(255);
                        canvas.restore();
                    } else {
                        if (this.transitionParams.drawPinnedBottomBackground) {
                            messageDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
                        } else {
                            messageDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
                        }
                        messageDrawable.setAlpha((int) (((this.mediaBackground || this.pinnedBottom) ? f - this.transitionParams.changePinnedBottomProgress : this.transitionParams.changePinnedBottomProgress) * 255.0f));
                        Rect bounds2 = this.currentBackgroundDrawable.getBounds();
                        messageDrawable.setBounds(bounds2.left - AndroidUtilities.dp(6.0f), bounds2.top, bounds2.right, bounds2.bottom);
                        canvas.save();
                        canvas.clipRect(bounds2.left - AndroidUtilities.dp(6.0f), bounds2.bottom - AndroidUtilities.dp(16.0f), bounds2.left + AndroidUtilities.dp(6.0f), bounds2.bottom);
                        messageDrawable.draw(canvas);
                        messageDrawable.setAlpha(255);
                        canvas.restore();
                    }
                }
            }
        }
        if (!z2) {
            return;
        }
        canvas.restore();
    }

    public boolean drawBackgroundInParent() {
        MessageObject messageObject;
        if (!this.canDrawBackgroundInParent || (messageObject = this.currentMessageObject) == null || !messageObject.isOutOwner()) {
            return false;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.getCurrentColor("chat_outBubbleGradient") != null : Theme.getColorOrNull("chat_outBubbleGradient") != null;
    }

    public void drawCommentButton(Canvas canvas, float f) {
        if (this.drawSideButton != 3) {
            return;
        }
        int dp = AndroidUtilities.dp(32.0f);
        if (this.commentLayout != null) {
            this.sideStartY -= AndroidUtilities.dp(18.0f);
            dp += AndroidUtilities.dp(18.0f);
        }
        RectF rectF = this.rect;
        float f2 = this.sideStartX;
        rectF.set(f2, this.sideStartY, AndroidUtilities.dp(32.0f) + f2, this.sideStartY + dp);
        applyServiceShaderMatrix();
        String str = "paintChatActionBackground";
        if (f != 1.0f) {
            int alpha = getThemedPaint(str).getAlpha();
            getThemedPaint(str).setAlpha((int) (alpha * f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(str));
            getThemedPaint(str).setAlpha(alpha);
        } else {
            RectF rectF2 = this.rect;
            float dp2 = AndroidUtilities.dp(16.0f);
            float dp3 = AndroidUtilities.dp(16.0f);
            if (this.sideButtonPressed) {
                str = "paintChatActionBackgroundSelected";
            }
            canvas.drawRoundRect(rectF2, dp2, dp3, getThemedPaint(str));
        }
        if (hasGradientService()) {
            if (f != 1.0f) {
                int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
            } else {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
        }
        Drawable themeDrawable = Theme.getThemeDrawable("drawableCommentSticker");
        BaseCell.setDrawableBounds(themeDrawable, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(4.0f));
        if (f != 1.0f) {
            themeDrawable.setAlpha((int) (f * 255.0f));
            themeDrawable.draw(canvas);
            themeDrawable.setAlpha(255);
        } else {
            themeDrawable.draw(canvas);
        }
        if (this.commentLayout == null) {
            return;
        }
        Theme.chat_stickerCommentCountPaint.setColor(getThemedColor("chat_stickerReplyNameText"));
        Theme.chat_stickerCommentCountPaint.setAlpha((int) (f * 255.0f));
        if (this.transitionParams.animateComments) {
            if (this.transitionParams.animateCommentsLayout != null) {
                canvas.save();
                TextPaint textPaint = Theme.chat_stickerCommentCountPaint;
                double d = this.transitionParams.animateChangeProgress;
                Double.isNaN(d);
                double d2 = f;
                Double.isNaN(d2);
                textPaint.setAlpha((int) ((1.0d - d) * 255.0d * d2));
                canvas.translate(this.sideStartX + ((AndroidUtilities.dp(32.0f) - this.transitionParams.animateTotalCommentWidth) / 2), this.sideStartY + AndroidUtilities.dp(30.0f));
                this.transitionParams.animateCommentsLayout.draw(canvas);
                canvas.restore();
            }
            Theme.chat_stickerCommentCountPaint.setAlpha((int) (this.transitionParams.animateChangeProgress * 255.0f));
        }
        canvas.save();
        canvas.translate(this.sideStartX + ((AndroidUtilities.dp(32.0f) - this.totalCommentWidth) / 2), this.sideStartY + AndroidUtilities.dp(30.0f));
        this.commentLayout.draw(canvas);
        canvas.restore();
    }

    public void applyServiceShaderMatrix() {
        applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, getX(), this.viewTop);
    }

    private void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(i, i2, f, f2);
        } else {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }
    }

    public boolean hasOutboundsContent() {
        if (getAlpha() != 1.0f) {
            return false;
        }
        return (!this.transitionParams.transitionBotButtons.isEmpty() && this.transitionParams.animateBotButtonsChanged) || !this.botButtons.isEmpty() || this.drawSideButton != 0;
    }

    public void drawOutboundsContent(Canvas canvas) {
        float f = 1.0f;
        if (!this.transitionParams.transitionBotButtons.isEmpty()) {
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.animateBotButtonsChanged) {
                drawBotButtons(canvas, transitionParams.transitionBotButtons, 1.0f - this.transitionParams.animateChangeProgress);
            }
        }
        if (!this.botButtons.isEmpty()) {
            ArrayList<BotButton> arrayList = this.botButtons;
            TransitionParams transitionParams2 = this.transitionParams;
            if (transitionParams2.animateBotButtonsChanged) {
                f = transitionParams2.animateChangeProgress;
            }
            drawBotButtons(canvas, arrayList, f);
        }
        drawSideButton(canvas);
    }

    private void drawSideButton(Canvas canvas) {
        if (this.drawSideButton != 0) {
            if (this.currentMessageObject.isOutOwner()) {
                float dp = this.transitionParams.lastBackgroundLeft - AndroidUtilities.dp(40.0f);
                this.sideStartX = dp;
                MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
                if (groupedMessages != null) {
                    this.sideStartX = dp + (groupedMessages.transitionParams.offsetLeft - this.animationOffsetX);
                }
            } else {
                float dp2 = this.transitionParams.lastBackgroundRight + AndroidUtilities.dp(8.0f);
                this.sideStartX = dp2;
                MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
                if (groupedMessages2 != null) {
                    this.sideStartX = dp2 + (groupedMessages2.transitionParams.offsetRight - this.animationOffsetX);
                }
            }
            float dp3 = (this.layoutHeight - AndroidUtilities.dp(41.0f)) + this.transitionParams.deltaBottom;
            this.sideStartY = dp3;
            MessageObject.GroupedMessages groupedMessages3 = this.currentMessagesGroup;
            if (groupedMessages3 != null) {
                MessageObject.GroupedMessages.TransitionParams transitionParams = groupedMessages3.transitionParams;
                float f = dp3 + transitionParams.offsetBottom;
                this.sideStartY = f;
                if (transitionParams.backgroundChangeBounds) {
                    this.sideStartY = f - getTranslationY();
                }
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
            if (!reactionsLayoutInBubble.isSmall && reactionsLayoutInBubble.drawServiceShaderBackground) {
                this.sideStartY -= reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress);
            }
            if (!this.currentMessageObject.isOutOwner() && this.isRoundVideo && this.isAvatarVisible) {
                float f2 = (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f;
                boolean z = this.isPlayingRound;
                float f3 = z ? f2 : 0.0f;
                TransitionParams transitionParams2 = this.transitionParams;
                if (transitionParams2.animatePlayingRound) {
                    f3 = (z ? transitionParams2.animateChangeProgress : 1.0f - transitionParams2.animateChangeProgress) * f2;
                }
                this.sideStartX -= f3;
            }
            if (this.drawSideButton == 3) {
                if (this.enterTransitionInProgress && !this.currentMessageObject.isVoice()) {
                    return;
                }
                drawCommentButton(canvas, 1.0f);
                return;
            }
            RectF rectF = this.rect;
            float f4 = this.sideStartX;
            rectF.set(f4, this.sideStartY, AndroidUtilities.dp(32.0f) + f4, this.sideStartY + AndroidUtilities.dp(32.0f));
            applyServiceShaderMatrix();
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(this.sideButtonPressed ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (this.drawSideButton == 2) {
                Drawable themedDrawable = getThemedDrawable("drawableGoIcon");
                if (this.currentMessageObject.isOutOwner()) {
                    BaseCell.setDrawableBounds(themedDrawable, this.sideStartX + AndroidUtilities.dp(10.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
                    canvas.save();
                    canvas.scale(-1.0f, 1.0f, themedDrawable.getBounds().centerX(), themedDrawable.getBounds().centerY());
                } else {
                    BaseCell.setDrawableBounds(themedDrawable, this.sideStartX + AndroidUtilities.dp(12.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
                }
                themedDrawable.draw(canvas);
                if (!this.currentMessageObject.isOutOwner()) {
                    return;
                }
                canvas.restore();
                return;
            }
            Drawable themedDrawable2 = getThemedDrawable("drawableShareIcon");
            BaseCell.setDrawableBounds(themedDrawable2, this.sideStartX + AndroidUtilities.dp(8.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
            themedDrawable2.draw(canvas);
        }
    }

    public void setTimeAlpha(float f) {
        this.timeAlpha = f;
    }

    public float getTimeAlpha() {
        return this.timeAlpha;
    }

    public int getBackgroundDrawableLeft() {
        int i;
        int i2 = 0;
        if (this.currentMessageObject.isOutOwner()) {
            int i3 = this.layoutWidth - this.backgroundWidth;
            if (this.mediaBackground) {
                i2 = AndroidUtilities.dp(9.0f);
            }
            return i3 - i2;
        }
        if (this.isChat && this.isAvatarVisible) {
            i2 = 48;
        }
        int dp = AndroidUtilities.dp(i2 + (!this.mediaBackground ? 3 : 9));
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments && (i = this.currentPosition.leftSpanOffset) != 0) {
            dp += (int) Math.ceil((i / 1000.0f) * getGroupPhotosWidth());
        }
        return (this.mediaBackground || !this.drawPinnedBottom) ? dp : dp + AndroidUtilities.dp(6.0f);
    }

    public int getBackgroundDrawableRight() {
        int dp = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
        if (!this.mediaBackground && this.drawPinnedBottom && this.currentMessageObject.isOutOwner()) {
            dp -= AndroidUtilities.dp(6.0f);
        }
        if (!this.mediaBackground && this.drawPinnedBottom && !this.currentMessageObject.isOutOwner()) {
            dp -= AndroidUtilities.dp(6.0f);
        }
        return getBackgroundDrawableLeft() + dp;
    }

    public int getBackgroundDrawableTop() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int i = 0;
        int dp = (groupedMessagePosition == null || (groupedMessagePosition.flags & 4) != 0) ? 0 : 0 - AndroidUtilities.dp(3.0f);
        if (!this.drawPinnedTop) {
            i = AndroidUtilities.dp(1.0f);
        }
        return dp + i;
    }

    public int getBackgroundDrawableBottom() {
        int i;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int i2 = 0;
        if (groupedMessagePosition != null) {
            int i3 = 4;
            i = (groupedMessagePosition.flags & 4) == 0 ? AndroidUtilities.dp(3.0f) + 0 : 0;
            if ((this.currentPosition.flags & 8) == 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    i3 = 3;
                }
                i += AndroidUtilities.dp(i3);
            }
        } else {
            i = 0;
        }
        boolean z = this.drawPinnedBottom;
        if (!z || !this.drawPinnedTop) {
            if (z) {
                i2 = AndroidUtilities.dp(1.0f);
            } else {
                i2 = AndroidUtilities.dp(2.0f);
            }
        }
        return ((getBackgroundDrawableTop() + this.layoutHeight) - i2) + i;
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawBackground(android.graphics.Canvas r16, int r17, int r18, int r19, int r20, boolean r21, boolean r22, boolean r23, int r24) {
        /*
            r15 = this;
            r0 = r15
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            org.telegram.messenger.MessageObject r6 = r0.currentMessageObject
            boolean r6 = r6.isOutOwner()
            if (r6 == 0) goto L39
            boolean r6 = r0.mediaBackground
            if (r6 != 0) goto L29
            if (r22 != 0) goto L29
            if (r23 == 0) goto L1e
            java.lang.String r6 = "drawableMsgOutSelected"
            goto L20
        L1e:
            java.lang.String r6 = "drawableMsgOut"
        L20:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
            goto L5e
        L29:
            if (r23 == 0) goto L2e
            java.lang.String r6 = "drawableMsgOutMediaSelected"
            goto L30
        L2e:
            java.lang.String r6 = "drawableMsgOutMedia"
        L30:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
            goto L5e
        L39:
            boolean r6 = r0.mediaBackground
            if (r6 != 0) goto L4f
            if (r22 != 0) goto L4f
            if (r23 == 0) goto L44
            java.lang.String r6 = "drawableMsgInSelected"
            goto L46
        L44:
            java.lang.String r6 = "drawableMsgIn"
        L46:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
            goto L5e
        L4f:
            if (r23 == 0) goto L54
            java.lang.String r6 = "drawableMsgInMediaSelected"
            goto L56
        L54:
            java.lang.String r6 = "drawableMsgInMedia"
        L56:
            android.graphics.drawable.Drawable r6 = r15.getThemedDrawable(r6)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = (org.telegram.ui.ActionBar.Theme.MessageDrawable) r6
            r0.currentBackgroundDrawable = r6
        L5e:
            int r6 = r0.parentWidth
            int r7 = r0.parentHeight
            if (r7 != 0) goto L85
            int r6 = r15.getParentWidth()
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
            int r7 = r7.y
            android.view.ViewParent r8 = r15.getParent()
            boolean r8 = r8 instanceof android.view.View
            if (r8 == 0) goto L85
            android.view.ViewParent r6 = r15.getParent()
            android.view.View r6 = (android.view.View) r6
            int r7 = r6.getMeasuredWidth()
            int r6 = r6.getMeasuredHeight()
            r9 = r6
            r8 = r7
            goto L87
        L85:
            r8 = r6
            r9 = r7
        L87:
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            if (r6 == 0) goto Ld7
            float r7 = r0.parentViewTopOffset
            int r10 = (int) r7
            int r11 = r0.blurredViewTopOffset
            int r12 = r0.blurredViewBottomOffset
            r7 = r24
            r13 = r21
            r14 = r22
            r6.setTop(r7, r8, r9, r10, r11, r12, r13, r14)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            android.graphics.drawable.Drawable r6 = r6.getShadowDrawable()
            r7 = 255(0xff, float:3.57E-43)
            r8 = 1132396544(0x437f0000, float:255.0)
            if (r6 == 0) goto Lba
            float r9 = r15.getAlpha()
            float r9 = r9 * r8
            int r9 = (int) r9
            r6.setAlpha(r9)
            r6.setBounds(r2, r3, r4, r5)
            r6.draw(r1)
            r6.setAlpha(r7)
        Lba:
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            float r9 = r15.getAlpha()
            float r9 = r9 * r8
            int r8 = (int) r9
            r6.setAlpha(r8)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r6 = r0.currentBackgroundDrawable
            r6.setBounds(r2, r3, r4, r5)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r2 = r0.currentBackgroundDrawable
            org.telegram.ui.ActionBar.Theme$MessageDrawable$PathDrawParams r3 = r0.backgroundCacheParams
            r2.drawCached(r1, r3)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.currentBackgroundDrawable
            r1.setAlpha(r7)
        Ld7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawBackground(android.graphics.Canvas, int, int, int, int, boolean, boolean, boolean, int):void");
    }

    public boolean hasNameLayout() {
        if (!this.drawNameLayout || this.nameLayout == null) {
            if (this.drawForwardedName) {
                StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
                if (staticLayoutArr[0] != null && staticLayoutArr[1] != null) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                    if (groupedMessagePosition == null) {
                        return true;
                    }
                    if (groupedMessagePosition.minY == 0 && groupedMessagePosition.minX == 0) {
                        return true;
                    }
                }
            }
            return this.replyNameLayout != null;
        }
        return true;
    }

    public boolean isDrawNameLayout() {
        return this.drawNameLayout && this.nameLayout != null;
    }

    public boolean isAdminLayoutChanged() {
        return !TextUtils.equals(this.lastPostAuthor, this.currentMessageObject.messageOwner.post_author);
    }

    /* JADX WARN: Removed duplicated region for block: B:172:0x04aa  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x04cc  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x04ce  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x04d9  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x04f6  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x04fc  */
    /* JADX WARN: Removed duplicated region for block: B:298:0x07bb  */
    /* JADX WARN: Removed duplicated region for block: B:306:0x0813  */
    /* JADX WARN: Removed duplicated region for block: B:309:0x081a  */
    /* JADX WARN: Removed duplicated region for block: B:342:0x08fd  */
    /* JADX WARN: Removed duplicated region for block: B:430:0x0bf3  */
    /* JADX WARN: Removed duplicated region for block: B:437:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawNamesLayout(android.graphics.Canvas r29, float r30) {
        /*
            Method dump skipped, instructions count: 3063
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawNamesLayout(android.graphics.Canvas, float):void");
    }

    public boolean hasCaptionLayout() {
        return this.captionLayout != null;
    }

    public boolean hasCommentLayout() {
        return this.drawCommentButton;
    }

    public StaticLayout getCaptionLayout() {
        return this.captionLayout;
    }

    public void setDrawSelectionBackground(boolean z) {
        if (this.drawSelectionBackground != z) {
            this.drawSelectionBackground = z;
            invalidate();
        }
    }

    public boolean isDrawingSelectionBackground() {
        return this.drawSelectionBackground || this.isHighlightedAnimated || this.isHighlighted;
    }

    public float getHighlightAlpha() {
        int i;
        if (this.drawSelectionBackground || !this.isHighlightedAnimated || (i = this.highlightProgress) >= 300) {
            return 1.0f;
        }
        return i / 300.0f;
    }

    public void setCheckBoxVisible(boolean z, boolean z2) {
        MessageObject.GroupedMessages groupedMessages;
        MessageObject.GroupedMessages groupedMessages2;
        if (z && this.checkBox == null) {
            CheckBoxBase checkBoxBase = new CheckBoxBase(this, 21, this.resourcesProvider);
            this.checkBox = checkBoxBase;
            if (this.attachedToWindow) {
                checkBoxBase.onAttachedToWindow();
            }
        }
        if (z && this.mediaCheckBox == null && (((groupedMessages = this.currentMessagesGroup) != null && groupedMessages.messages.size() > 1) || ((groupedMessages2 = this.groupedMessagesToSet) != null && groupedMessages2.messages.size() > 1))) {
            CheckBoxBase checkBoxBase2 = new CheckBoxBase(this, 21, this.resourcesProvider);
            this.mediaCheckBox = checkBoxBase2;
            checkBoxBase2.setUseDefaultCheck(true);
            if (this.attachedToWindow) {
                this.mediaCheckBox.onAttachedToWindow();
            }
        }
        float f = 1.0f;
        if (this.checkBoxVisible == z) {
            if (z2 == this.checkBoxAnimationInProgress || z2) {
                return;
            }
            if (!z) {
                f = 0.0f;
            }
            this.checkBoxAnimationProgress = f;
            invalidate();
            return;
        }
        this.checkBoxAnimationInProgress = z2;
        this.checkBoxVisible = z;
        if (z2) {
            this.lastCheckBoxAnimationTime = SystemClock.elapsedRealtime();
        } else {
            if (!z) {
                f = 0.0f;
            }
            this.checkBoxAnimationProgress = f;
        }
        invalidate();
    }

    public void setChecked(boolean z, boolean z2, boolean z3) {
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.setChecked(z2, z3);
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.setChecked(z, z3);
        }
        this.backgroundDrawable.setSelected(z2, z3);
    }

    public void setLastTouchCoords(float f, float f2) {
        this.lastTouchX = f;
        this.lastTouchY = f2;
        this.backgroundDrawable.setTouchCoords(f + getTranslationX(), this.lastTouchY);
    }

    public MessageBackgroundDrawable getBackgroundDrawable() {
        return this.backgroundDrawable;
    }

    public Theme.MessageDrawable getCurrentBackgroundDrawable(boolean z) {
        if (z) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            boolean z2 = groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments && !this.drawPinnedBottom;
            if (this.currentMessageObject.isOutOwner()) {
                if (!this.mediaBackground && !this.drawPinnedBottom && !z2) {
                    this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
                } else {
                    this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMedia");
                }
            } else if (!this.mediaBackground && !this.drawPinnedBottom && !z2) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
            }
        }
        this.currentBackgroundDrawable.getBackgroundDrawable();
        return this.currentBackgroundDrawable;
    }

    public void drawCaptionLayout(Canvas canvas, boolean z, float f) {
        TransitionParams transitionParams = this.transitionParams;
        float f2 = 1.0f;
        if (!transitionParams.animateReplaceCaptionLayout || transitionParams.animateChangeProgress == 1.0f) {
            drawCaptionLayout(canvas, this.captionLayout, z, f);
        } else {
            drawCaptionLayout(canvas, transitionParams.animateOutCaptionLayout, z, (1.0f - this.transitionParams.animateChangeProgress) * f);
            drawCaptionLayout(canvas, this.captionLayout, z, f * this.transitionParams.animateChangeProgress);
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.messageOwner != null && messageObject.isVoiceTranscriptionOpen()) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (!messageObject2.messageOwner.voiceTranscriptionFinal && TranscribeButton.isTranscribing(messageObject2)) {
                invalidate();
            }
        }
        if (!z) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null) {
                int i = groupedMessagePosition.flags;
                if ((i & 8) == 0 || (i & 1) == 0) {
                    return;
                }
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
            if (reactionsLayoutInBubble.isSmall) {
                return;
            }
            if (reactionsLayoutInBubble.drawServiceShaderBackground) {
                applyServiceShaderMatrix();
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
            if (reactionsLayoutInBubble2.drawServiceShaderBackground || !this.transitionParams.animateBackgroundBoundsInner || this.currentPosition != null) {
                TransitionParams transitionParams2 = this.transitionParams;
                if (transitionParams2.animateChange) {
                    f2 = transitionParams2.animateChangeProgress;
                }
                reactionsLayoutInBubble2.draw(canvas, f2, null);
                return;
            }
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), getBackgroundDrawableBottom() + this.transitionParams.deltaBottom);
            ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams3 = this.transitionParams;
            if (transitionParams3.animateChange) {
                f2 = transitionParams3.animateChangeProgress;
            }
            reactionsLayoutInBubble3.draw(canvas, f2, null);
            canvas.restore();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:215:0x04f0  */
    /* JADX WARN: Removed duplicated region for block: B:225:0x053e  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x0558  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x0567  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x056c  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x0586  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0589  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x05a0  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x0645  */
    /* JADX WARN: Removed duplicated region for block: B:365:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawCaptionLayout(android.graphics.Canvas r27, android.text.StaticLayout r28, boolean r29, float r30) {
        /*
            Method dump skipped, instructions count: 2220
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawCaptionLayout(android.graphics.Canvas, android.text.StaticLayout, boolean, float):void");
    }

    public boolean needDrawTime() {
        return !this.forceNotDrawTime;
    }

    public boolean shouldDrawTimeOnMedia() {
        int i = this.overideShouldDrawTimeOnMedia;
        if (i != 0) {
            return i == 1;
        } else if (!this.mediaBackground || this.captionLayout != null) {
            return false;
        } else {
            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
            return reactionsLayoutInBubble.isEmpty || reactionsLayoutInBubble.isSmall || this.currentMessageObject.isAnyKindOfSticker() || this.currentMessageObject.isRoundVideo();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0126  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawTime(android.graphics.Canvas r17, float r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 332
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawTime(android.graphics.Canvas, float, boolean):void");
    }

    private void drawTimeInternal(Canvas canvas, float f, boolean z, float f2, StaticLayout staticLayout, float f3, boolean z2) {
        int i;
        float f4;
        float f5;
        boolean z3;
        int i2;
        boolean z4;
        int i3;
        Paint paint;
        int i4;
        int i5;
        float f6;
        float f7;
        TextPaint textPaint;
        if (((!this.drawTime || this.groupPhotoInvisible) && shouldDrawTimeOnMedia()) || staticLayout == null) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        if ((messageObject.deleted && this.currentPosition != null) || (i = messageObject.type) == 16) {
            return;
        }
        if (i == 5) {
            Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
        } else if (shouldDrawTimeOnMedia()) {
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor("chat_serviceText"));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor("chat_mediaTimeText"));
            }
        } else if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_timePaint.setColor(getThemedColor(z2 ? "chat_outTimeSelectedText" : "chat_outTimeText"));
        } else {
            Theme.chat_timePaint.setColor(getThemedColor(z2 ? "chat_inTimeSelectedText" : "chat_inTimeText"));
        }
        float f8 = getTransitionParams().animateDrawingTimeAlpha ? getTransitionParams().animateChangeProgress * f : f;
        if (f8 != 1.0f) {
            Theme.chat_timePaint.setAlpha((int) (textPaint.getAlpha() * f8));
        }
        canvas.save();
        if (this.drawPinnedBottom && !shouldDrawTimeOnMedia()) {
            canvas.translate(0.0f, AndroidUtilities.dp(2.0f));
        }
        TransitionParams transitionParams = this.transitionParams;
        float f9 = this.layoutHeight + transitionParams.deltaBottom;
        if (transitionParams.shouldAnimateTimeX) {
            float f10 = transitionParams.animateChangeProgress;
            f4 = (transitionParams.animateFromTimeX * (1.0f - f10)) + (this.timeX * f10);
        } else {
            f4 = f2;
        }
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages == null || !groupedMessages.transitionParams.backgroundChangeBounds) {
            f5 = f2;
        } else {
            f9 -= getTranslationY();
            float f11 = this.currentMessagesGroup.transitionParams.offsetRight;
            f5 = f2 + f11;
            f4 += f11;
        }
        if (this.drawPinnedBottom && shouldDrawTimeOnMedia()) {
            f9 += AndroidUtilities.dp(1.0f);
        }
        float f12 = f9;
        TransitionParams transitionParams2 = this.transitionParams;
        boolean z5 = transitionParams2.animateBackgroundBoundsInner;
        if (z5) {
            float f13 = this.animationOffsetX;
            f5 += f13;
            f4 += f13;
        }
        float f14 = f5;
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            if (z5 && transitionParams2.deltaRight != 0.0f) {
                f7 = reactionsLayoutInBubble.getCurrentWidth(1.0f);
            } else {
                f7 = reactionsLayoutInBubble.getCurrentWidth(transitionParams2.animateChangeProgress);
            }
            f4 += f7;
        }
        if (this.transitionParams.animateEditedEnter) {
            f4 -= this.transitionParams.animateEditedWidthDiff * (1.0f - this.transitionParams.animateChangeProgress);
        }
        float f15 = f4;
        if (shouldDrawTimeOnMedia()) {
            int i6 = -(this.drawCommentButton ? AndroidUtilities.dp(41.3f) : 0);
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                paint = getThemedPaint("paintChatActionBackground");
            } else {
                paint = getThemedPaint("paintChatTimeBackground");
            }
            int alpha = paint.getAlpha();
            paint.setAlpha((int) (alpha * this.timeAlpha * f8));
            Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f * f8));
            int i7 = this.documentAttachType;
            float f16 = 4.0f;
            if (i7 != 7 && i7 != 6) {
                int[] roundRadius = this.photoImage.getRoundRadius();
                i4 = Math.min(AndroidUtilities.dp(8.0f), Math.max(roundRadius[2], roundRadius[3]));
                z3 = SharedConfig.bubbleRadius >= 10;
            } else {
                i4 = AndroidUtilities.dp(4.0f);
                z3 = false;
            }
            if (z3) {
                f16 = 6.0f;
            }
            float dp = f14 - AndroidUtilities.dp(f16);
            float imageY2 = this.photoImage.getImageY2() + this.additionalTimeOffsetY;
            float dp2 = imageY2 - AndroidUtilities.dp(23.0f);
            this.rect.set(dp, dp2, dp + f3 + AndroidUtilities.dp((z3 ? 12 : 8) + (this.currentMessageObject.isOutOwner() ? 20 : 0)), AndroidUtilities.dp(17.0f) + dp2);
            applyServiceShaderMatrix();
            float f17 = i4;
            canvas.drawRoundRect(this.rect, f17, f17, paint);
            if (paint == getThemedPaint("paintChatActionBackground") && hasGradientService()) {
                int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * this.timeAlpha * f8));
                canvas.drawRoundRect(this.rect, f17, f17, Theme.chat_actionBackgroundGradientDarkenPaint);
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
            }
            paint.setAlpha(alpha);
            float f18 = -staticLayout.getLineLeft(0);
            if (this.reactionsLayoutInBubble.isSmall) {
                updateReactionLayoutPosition();
                this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, null);
            }
            if ((!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) && (this.currentMessageObject.messageOwner.flags & 1024) == 0 && this.repliesLayout == null && !this.isPinned) {
                i5 = i6;
            } else {
                float lineWidth = f18 + (this.timeWidth - staticLayout.getLineWidth(0));
                ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
                if (reactionsLayoutInBubble2.isSmall && !reactionsLayoutInBubble2.isEmpty) {
                    lineWidth -= reactionsLayoutInBubble2.width;
                }
                float f19 = lineWidth;
                int createStatusDrawableParams = this.transitionParams.createStatusDrawableParams();
                int i8 = this.transitionParams.lastStatusDrawableParams;
                if (i8 >= 0 && i8 != createStatusDrawableParams && !this.statusDrawableAnimationInProgress) {
                    createStatusDrawableAnimator(i8, createStatusDrawableParams, z);
                }
                boolean z6 = this.statusDrawableAnimationInProgress;
                if (z6) {
                    createStatusDrawableParams = this.animateToStatusDrawableParams;
                }
                boolean z7 = (createStatusDrawableParams & 4) != 0;
                boolean z8 = (createStatusDrawableParams & 8) != 0;
                if (z6) {
                    int i9 = this.animateFromStatusDrawableParams;
                    boolean z9 = (i9 & 4) != 0;
                    boolean z10 = (i9 & 8) != 0;
                    float f20 = i6;
                    float f21 = f8;
                    f6 = f19;
                    i5 = i6;
                    drawClockOrErrorLayout(canvas, z9, z10, f12, f21, f20, f14, 1.0f - this.statusDrawableProgress, z2);
                    drawClockOrErrorLayout(canvas, z7, z8, f12, f21, f20, f14, this.statusDrawableProgress, z2);
                    if (!this.currentMessageObject.isOutOwner()) {
                        if (!z9 && !z10) {
                            drawViewsAndRepliesLayout(canvas, f12, f8, f20, f14, 1.0f - this.statusDrawableProgress, z2);
                        }
                        if (!z7 && !z8) {
                            drawViewsAndRepliesLayout(canvas, f12, f8, f20, f14, this.statusDrawableProgress, z2);
                        }
                    }
                } else {
                    f6 = f19;
                    i5 = i6;
                    if (!this.currentMessageObject.isOutOwner() && !z7 && !z8) {
                        drawViewsAndRepliesLayout(canvas, f12, f8, i5, f14, 1.0f, z2);
                    }
                    drawClockOrErrorLayout(canvas, z7, z8, f12, f8, i5, f14, 1.0f, z2);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    drawViewsAndRepliesLayout(canvas, f12, f8, i5, f14, 1.0f, z2);
                }
                TransitionParams transitionParams3 = this.transitionParams;
                transitionParams3.lastStatusDrawableParams = transitionParams3.createStatusDrawableParams();
                if (z7 && z && getParent() != null) {
                    ((View) getParent()).invalidate();
                }
                f18 = f6;
            }
            canvas.save();
            float f22 = f15 + f18;
            this.drawTimeX = f22;
            float dp3 = (imageY2 - AndroidUtilities.dp(7.3f)) - staticLayout.getHeight();
            this.drawTimeY = dp3;
            canvas.translate(f22, dp3);
            staticLayout.draw(canvas);
            canvas.restore();
            Theme.chat_timePaint.setAlpha(255);
            i2 = i5;
        } else {
            if (this.currentMessageObject.isSponsored()) {
                i3 = -AndroidUtilities.dp(48.0f);
                if (this.hasNewLineForTime) {
                    i3 -= AndroidUtilities.dp(16.0f);
                }
            } else {
                i3 = -(this.drawCommentButton ? AndroidUtilities.dp(43.0f) : 0);
            }
            int i10 = i3;
            float f23 = -staticLayout.getLineLeft(0);
            if (this.reactionsLayoutInBubble.isSmall) {
                updateReactionLayoutPosition();
                this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, null);
            }
            if ((ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) || (this.currentMessageObject.messageOwner.flags & 1024) != 0 || this.repliesLayout != null || this.transitionParams.animateReplies || this.isPinned || this.transitionParams.animatePinned) {
                float lineWidth2 = f23 + (this.timeWidth - staticLayout.getLineWidth(0));
                ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
                if (reactionsLayoutInBubble3.isSmall && !reactionsLayoutInBubble3.isEmpty) {
                    lineWidth2 -= reactionsLayoutInBubble3.width;
                }
                float f24 = lineWidth2;
                int createStatusDrawableParams2 = this.transitionParams.createStatusDrawableParams();
                int i11 = this.transitionParams.lastStatusDrawableParams;
                if (i11 >= 0 && i11 != createStatusDrawableParams2 && !this.statusDrawableAnimationInProgress) {
                    createStatusDrawableAnimator(i11, createStatusDrawableParams2, z);
                }
                boolean z11 = this.statusDrawableAnimationInProgress;
                if (z11) {
                    createStatusDrawableParams2 = this.animateToStatusDrawableParams;
                }
                boolean z12 = (createStatusDrawableParams2 & 4) != 0;
                boolean z13 = (createStatusDrawableParams2 & 8) != 0;
                if (z11) {
                    int i12 = this.animateFromStatusDrawableParams;
                    boolean z14 = (i12 & 4) != 0;
                    boolean z15 = (i12 & 8) != 0;
                    float f25 = i10;
                    float f26 = f8;
                    drawClockOrErrorLayout(canvas, z14, z15, f12, f26, f25, f14, 1.0f - this.statusDrawableProgress, z2);
                    drawClockOrErrorLayout(canvas, z12, z13, f12, f26, f25, f14, this.statusDrawableProgress, z2);
                    if (!this.currentMessageObject.isOutOwner()) {
                        if (!z14 && !z15) {
                            drawViewsAndRepliesLayout(canvas, f12, f8, f25, f14, 1.0f - this.statusDrawableProgress, z2);
                        }
                        if (!z12 && !z13) {
                            drawViewsAndRepliesLayout(canvas, f12, f8, f25, f14, this.statusDrawableProgress, z2);
                        }
                    }
                } else {
                    if (!this.currentMessageObject.isOutOwner() && !z12 && !z13) {
                        drawViewsAndRepliesLayout(canvas, f12, f8, i10, f14, 1.0f, z2);
                    }
                    drawClockOrErrorLayout(canvas, z12, z13, f12, f8, i10, f14, 1.0f, z2);
                }
                if (this.currentMessageObject.isOutOwner()) {
                    drawViewsAndRepliesLayout(canvas, f12, f8, i10, f14, 1.0f, z2);
                }
                TransitionParams transitionParams4 = this.transitionParams;
                transitionParams4.lastStatusDrawableParams = transitionParams4.createStatusDrawableParams();
                if (z12 && z && getParent() != null) {
                    ((View) getParent()).invalidate();
                }
                f23 = f24;
            }
            canvas.save();
            float f27 = 6.5f;
            if (this.transitionParams.animateEditedEnter) {
                TransitionParams transitionParams5 = this.transitionParams;
                if (transitionParams5.animateChangeProgress != 1.0f) {
                    if (transitionParams5.animateEditedLayout != null) {
                        float f28 = f15 + f23;
                        if (this.pinnedBottom || this.pinnedTop) {
                            f27 = 7.5f;
                        }
                        canvas.translate(f28, ((f12 - AndroidUtilities.dp(f27)) - staticLayout.getHeight()) + i10);
                        int alpha3 = Theme.chat_timePaint.getAlpha();
                        Theme.chat_timePaint.setAlpha((int) (alpha3 * this.transitionParams.animateChangeProgress));
                        this.transitionParams.animateEditedLayout.draw(canvas);
                        Theme.chat_timePaint.setAlpha(alpha3);
                        this.transitionParams.animateTimeLayout.draw(canvas);
                    } else {
                        int alpha4 = Theme.chat_timePaint.getAlpha();
                        canvas.save();
                        float f29 = i10;
                        canvas.translate(this.transitionParams.animateFromTimeX + f23, ((f12 - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 7.5f : 6.5f)) - staticLayout.getHeight()) + f29);
                        float f30 = alpha4;
                        Theme.chat_timePaint.setAlpha((int) ((1.0f - this.transitionParams.animateChangeProgress) * f30));
                        this.transitionParams.animateTimeLayout.draw(canvas);
                        canvas.restore();
                        float f31 = f15 + f23;
                        if (this.pinnedBottom || this.pinnedTop) {
                            f27 = 7.5f;
                        }
                        canvas.translate(f31, ((f12 - AndroidUtilities.dp(f27)) - staticLayout.getHeight()) + f29);
                        Theme.chat_timePaint.setAlpha((int) (f30 * this.transitionParams.animateChangeProgress));
                        staticLayout.draw(canvas);
                        Theme.chat_timePaint.setAlpha(alpha4);
                    }
                    canvas.restore();
                    i2 = i10;
                    z3 = false;
                }
            }
            float f32 = f15 + f23;
            this.drawTimeX = f32;
            if (this.pinnedBottom || this.pinnedTop) {
                f27 = 7.5f;
            }
            float dp4 = ((f12 - AndroidUtilities.dp(f27)) - staticLayout.getHeight()) + i10;
            this.drawTimeY = dp4;
            canvas.translate(f32, dp4);
            staticLayout.draw(canvas);
            canvas.restore();
            i2 = i10;
            z3 = false;
        }
        if (this.currentMessageObject.isOutOwner()) {
            int createStatusDrawableParams3 = this.transitionParams.createStatusDrawableParams();
            int i13 = this.transitionParams.lastStatusDrawableParams;
            if (i13 >= 0 && i13 != createStatusDrawableParams3 && !this.statusDrawableAnimationInProgress) {
                createStatusDrawableAnimator(i13, createStatusDrawableParams3, z);
            }
            if (this.statusDrawableAnimationInProgress) {
                createStatusDrawableParams3 = this.animateToStatusDrawableParams;
            }
            boolean z16 = (createStatusDrawableParams3 & 1) != 0;
            boolean z17 = (createStatusDrawableParams3 & 2) != 0;
            boolean z18 = (createStatusDrawableParams3 & 4) != 0;
            boolean z19 = (createStatusDrawableParams3 & 8) != 0;
            if (this.transitionYOffsetForDrawables != 0.0f) {
                canvas.save();
                canvas.translate(0.0f, this.transitionYOffsetForDrawables);
                z4 = true;
            } else {
                z4 = false;
            }
            if (this.statusDrawableAnimationInProgress) {
                int i14 = this.animateFromStatusDrawableParams;
                boolean z20 = (i14 & 1) != 0;
                boolean z21 = (i14 & 2) != 0;
                boolean z22 = (i14 & 4) != 0;
                boolean z23 = (i14 & 8) != 0;
                if (!z22 && z21 && z17 && !z20 && z16) {
                    drawStatusDrawable(canvas, z16, z17, z18, z19, f8, z3, i2, f12, this.statusDrawableProgress, true, z2);
                } else {
                    float f33 = i2;
                    float f34 = f8;
                    boolean z24 = z3;
                    drawStatusDrawable(canvas, z20, z21, z22, z23, f34, z24, f33, f12, 1.0f - this.statusDrawableProgress, false, z2);
                    drawStatusDrawable(canvas, z16, z17, z18, z19, f34, z24, f33, f12, this.statusDrawableProgress, false, z2);
                }
            } else {
                drawStatusDrawable(canvas, z16, z17, z18, z19, f8, z3, i2, f12, 1.0f, false, z2);
            }
            if (z4) {
                canvas.restore();
            }
            TransitionParams transitionParams6 = this.transitionParams;
            transitionParams6.lastStatusDrawableParams = transitionParams6.createStatusDrawableParams();
            if (z && z18 && getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        canvas.restore();
    }

    public void createStatusDrawableAnimator(int i, int i2, final boolean z) {
        boolean z2 = false;
        boolean z3 = (i2 & 1) != 0;
        boolean z4 = (i2 & 2) != 0;
        boolean z5 = (i & 1) != 0;
        boolean z6 = (i & 2) != 0;
        if (!((i & 4) != 0) && z6 && z4 && !z5 && z3) {
            z2 = true;
        }
        if (!this.transitionParams.messageEntering || z2) {
            this.statusDrawableProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.statusDrawableAnimator = ofFloat;
            if (z2) {
                ofFloat.setDuration(220L);
            } else {
                ofFloat.setDuration(150L);
            }
            this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animateFromStatusDrawableParams = i;
            this.animateToStatusDrawableParams = i2;
            this.statusDrawableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatMessageCell.this.lambda$createStatusDrawableAnimator$6(z, valueAnimator);
                }
            });
            this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ChatMessageCell.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    int createStatusDrawableParams = ChatMessageCell.this.transitionParams.createStatusDrawableParams();
                    if (ChatMessageCell.this.animateToStatusDrawableParams == createStatusDrawableParams) {
                        ChatMessageCell.this.statusDrawableAnimationInProgress = false;
                        ChatMessageCell.this.transitionParams.lastStatusDrawableParams = ChatMessageCell.this.animateToStatusDrawableParams;
                        return;
                    }
                    ChatMessageCell chatMessageCell = ChatMessageCell.this;
                    chatMessageCell.createStatusDrawableAnimator(chatMessageCell.animateToStatusDrawableParams, createStatusDrawableParams, z);
                }
            });
            this.statusDrawableAnimationInProgress = true;
            this.statusDrawableAnimator.start();
        }
    }

    public /* synthetic */ void lambda$createStatusDrawableAnimator$6(boolean z, ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (!z || getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    private void drawClockOrErrorLayout(Canvas canvas, boolean z, boolean z2, float f, float f2, float f3, float f4, float f5, boolean z3) {
        float f6;
        int i;
        float f7;
        int i2 = 0;
        boolean z4 = f5 != 1.0f;
        float f8 = (f5 * 0.5f) + 0.5f;
        float f9 = f2 * f5;
        if (z) {
            if (this.currentMessageObject.isOutOwner()) {
                return;
            }
            MsgClockDrawable msgClockDrawable = Theme.chat_msgClockDrawable;
            String str = "chat_mediaSentClock";
            if (shouldDrawTimeOnMedia()) {
                i = getThemedColor(str);
            } else {
                if (z3) {
                    str = "chat_outSentClockSelected";
                }
                i = getThemedColor(str);
            }
            msgClockDrawable.setColor(i);
            if (shouldDrawTimeOnMedia()) {
                f7 = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(9.0f);
            } else {
                f7 = (f - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.5f : 8.5f)) + f3;
            }
            if (!this.currentMessageObject.scheduled) {
                i2 = AndroidUtilities.dp(11.0f);
            }
            BaseCell.setDrawableBounds(msgClockDrawable, f4 + i2, f7 - msgClockDrawable.getIntrinsicHeight());
            msgClockDrawable.setAlpha((int) (f9 * 255.0f));
            if (z4) {
                canvas.save();
                canvas.scale(f8, f8, msgClockDrawable.getBounds().centerX(), msgClockDrawable.getBounds().centerY());
            }
            msgClockDrawable.draw(canvas);
            msgClockDrawable.setAlpha(255);
            invalidate();
            if (!z4) {
                return;
            }
            canvas.restore();
        } else if (!z2 || this.currentMessageObject.isOutOwner()) {
        } else {
            if (!this.currentMessageObject.scheduled) {
                i2 = AndroidUtilities.dp(11.0f);
            }
            float f10 = f4 + i2;
            float f11 = 21.5f;
            if (shouldDrawTimeOnMedia()) {
                f6 = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(21.5f);
            } else {
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f11 = 20.5f;
                }
                f6 = (f - AndroidUtilities.dp(f11)) + f3;
            }
            this.rect.set(f10, f6, AndroidUtilities.dp(14.0f) + f10, AndroidUtilities.dp(14.0f) + f6);
            int alpha = Theme.chat_msgErrorPaint.getAlpha();
            int i3 = (int) (f9 * 255.0f);
            Theme.chat_msgErrorPaint.setAlpha(i3);
            if (z4) {
                canvas.save();
                canvas.scale(f8, f8, this.rect.centerX(), this.rect.centerY());
            }
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
            Theme.chat_msgErrorPaint.setAlpha(alpha);
            Drawable themedDrawable = getThemedDrawable("drawableMsgError");
            BaseCell.setDrawableBounds(themedDrawable, f10 + AndroidUtilities.dp(6.0f), f6 + AndroidUtilities.dp(2.0f));
            themedDrawable.setAlpha(i3);
            themedDrawable.draw(canvas);
            themedDrawable.setAlpha(255);
            if (!z4) {
                return;
            }
            canvas.restore();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x01e3  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x01fb  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x020d  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x0218  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0152  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0180  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01c7  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x01df  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawViewsAndRepliesLayout(android.graphics.Canvas r24, float r25, float r26, float r27, float r28, float r29, boolean r30) {
        /*
            Method dump skipped, instructions count: 1170
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawViewsAndRepliesLayout(android.graphics.Canvas, float, float, float, float, float, boolean):void");
    }

    private void drawStatusDrawable(Canvas canvas, boolean z, boolean z2, boolean z3, boolean z4, float f, boolean z5, float f2, float f3, float f4, boolean z6, boolean z7) {
        int dp;
        int dp2;
        Drawable drawable;
        Drawable drawable2;
        int i;
        boolean z8 = f4 != 1.0f && !z6;
        float f5 = (f4 * 0.5f) + 0.5f;
        float f6 = z8 ? f * f4 : f;
        float imageY2 = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(8.5f);
        if (z3) {
            MsgClockDrawable msgClockDrawable = Theme.chat_msgClockDrawable;
            if (shouldDrawTimeOnMedia()) {
                float f7 = 24.0f;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    i = getThemedColor("chat_serviceText");
                    int i2 = this.layoutWidth;
                    if (!z5) {
                        f7 = 22.0f;
                    }
                    BaseCell.setDrawableBounds(msgClockDrawable, (i2 - AndroidUtilities.dp(f7)) - msgClockDrawable.getIntrinsicWidth(), (imageY2 - msgClockDrawable.getIntrinsicHeight()) + f2);
                    msgClockDrawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                } else {
                    i = getThemedColor("chat_mediaSentClock");
                    int i3 = this.layoutWidth;
                    if (!z5) {
                        f7 = 22.0f;
                    }
                    BaseCell.setDrawableBounds(msgClockDrawable, (i3 - AndroidUtilities.dp(f7)) - msgClockDrawable.getIntrinsicWidth(), (imageY2 - msgClockDrawable.getIntrinsicHeight()) + f2);
                    msgClockDrawable.setAlpha((int) (f6 * 255.0f));
                }
            } else {
                int themedColor = getThemedColor("chat_outSentClock");
                BaseCell.setDrawableBounds(msgClockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - msgClockDrawable.getIntrinsicWidth(), ((f3 - AndroidUtilities.dp(8.5f)) - msgClockDrawable.getIntrinsicHeight()) + f2);
                msgClockDrawable.setAlpha((int) (f6 * 255.0f));
                i = themedColor;
            }
            msgClockDrawable.setColor(i);
            if (z8) {
                canvas.save();
                canvas.scale(f5, f5, msgClockDrawable.getBounds().centerX(), msgClockDrawable.getBounds().centerY());
            }
            msgClockDrawable.draw(canvas);
            msgClockDrawable.setAlpha(255);
            if (z8) {
                canvas.restore();
            }
            invalidate();
        }
        float f8 = 23.5f;
        float f9 = 9.0f;
        if (z2) {
            if (shouldDrawTimeOnMedia()) {
                if (z6) {
                    canvas.save();
                }
                float f10 = 28.3f;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    drawable2 = getThemedDrawable("drawableMsgStickerCheck");
                    if (z) {
                        if (z6) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - f4), 0.0f);
                        }
                        int i4 = this.layoutWidth;
                        if (!z5) {
                            f10 = 26.3f;
                        }
                        BaseCell.setDrawableBounds(drawable2, (i4 - AndroidUtilities.dp(f10)) - drawable2.getIntrinsicWidth(), (imageY2 - drawable2.getIntrinsicHeight()) + f2);
                    } else {
                        BaseCell.setDrawableBounds(drawable2, (this.layoutWidth - AndroidUtilities.dp(z5 ? 23.5f : 21.5f)) - drawable2.getIntrinsicWidth(), (imageY2 - drawable2.getIntrinsicHeight()) + f2);
                    }
                    drawable2.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                } else {
                    if (z) {
                        if (z6) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - f4), 0.0f);
                        }
                        Drawable drawable3 = Theme.chat_msgMediaCheckDrawable;
                        int i5 = this.layoutWidth;
                        if (!z5) {
                            f10 = 26.3f;
                        }
                        BaseCell.setDrawableBounds(drawable3, (i5 - AndroidUtilities.dp(f10)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (imageY2 - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + f2);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(z5 ? 23.5f : 21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (imageY2 - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + f2);
                    }
                    Theme.chat_msgMediaCheckDrawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                    drawable2 = Theme.chat_msgMediaCheckDrawable;
                }
                if (z8) {
                    canvas.save();
                    canvas.scale(f5, f5, drawable2.getBounds().centerX(), drawable2.getBounds().centerY());
                }
                drawable2.draw(canvas);
                if (z8) {
                    canvas.restore();
                }
                if (z6) {
                    canvas.restore();
                }
                drawable2.setAlpha(255);
            } else {
                if (z6) {
                    canvas.save();
                }
                if (z) {
                    if (z6) {
                        canvas.translate(AndroidUtilities.dp(4.0f) * (1.0f - f4), 0.0f);
                    }
                    drawable = getThemedDrawable(z7 ? "drawableMsgOutCheckReadSelected" : "drawableMsgOutCheckRead");
                    BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable.getIntrinsicWidth(), ((f3 - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.0f : 8.0f)) - drawable.getIntrinsicHeight()) + f2);
                } else {
                    drawable = getThemedDrawable(z7 ? "drawableMsgOutCheckSelected" : "drawableMsgOutCheck");
                    BaseCell.setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable.getIntrinsicWidth(), ((f3 - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.0f : 8.0f)) - drawable.getIntrinsicHeight()) + f2);
                }
                drawable.setAlpha((int) (f6 * 255.0f));
                if (z8) {
                    canvas.save();
                    canvas.scale(f5, f5, drawable.getBounds().centerX(), drawable.getBounds().centerY());
                }
                drawable.draw(canvas);
                if (z8) {
                    canvas.restore();
                }
                if (z6) {
                    canvas.restore();
                }
                drawable.setAlpha(255);
            }
        }
        if (z) {
            if (shouldDrawTimeOnMedia()) {
                Drawable themedDrawable = this.currentMessageObject.shouldDrawWithoutBackground() ? getThemedDrawable("drawableMsgStickerHalfCheck") : Theme.chat_msgMediaHalfCheckDrawable;
                int i6 = this.layoutWidth;
                if (!z5) {
                    f8 = 21.5f;
                }
                BaseCell.setDrawableBounds(themedDrawable, (i6 - AndroidUtilities.dp(f8)) - themedDrawable.getIntrinsicWidth(), (imageY2 - themedDrawable.getIntrinsicHeight()) + f2);
                themedDrawable.setAlpha((int) (this.timeAlpha * 255.0f * f6));
                if (z8 || z6) {
                    canvas.save();
                    canvas.scale(f5, f5, themedDrawable.getBounds().centerX(), themedDrawable.getBounds().centerY());
                }
                themedDrawable.draw(canvas);
                if (z8 || z6) {
                    canvas.restore();
                }
                themedDrawable.setAlpha(255);
            } else {
                Drawable themedDrawable2 = getThemedDrawable(z7 ? "drawableMsgOutHalfCheckSelected" : "drawableMsgOutHalfCheck");
                float dp3 = (this.layoutWidth - AndroidUtilities.dp(18.0f)) - themedDrawable2.getIntrinsicWidth();
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f9 = 8.0f;
                }
                BaseCell.setDrawableBounds(themedDrawable2, dp3, ((f3 - AndroidUtilities.dp(f9)) - themedDrawable2.getIntrinsicHeight()) + f2);
                themedDrawable2.setAlpha((int) (f6 * 255.0f));
                if (z8 || z6) {
                    canvas.save();
                    canvas.scale(f5, f5, themedDrawable2.getBounds().centerX(), themedDrawable2.getBounds().centerY());
                }
                themedDrawable2.draw(canvas);
                if (z8 || z6) {
                    canvas.restore();
                }
                themedDrawable2.setAlpha(255);
            }
        }
        if (z4) {
            if (shouldDrawTimeOnMedia()) {
                dp = this.layoutWidth - AndroidUtilities.dp(34.5f);
                dp2 = AndroidUtilities.dp(26.5f);
            } else {
                dp = this.layoutWidth - AndroidUtilities.dp(32.0f);
                dp2 = AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 22.0f : 21.0f);
            }
            float f11 = (f3 - dp2) + f2;
            this.rect.set(dp, f11, AndroidUtilities.dp(14.0f) + dp, AndroidUtilities.dp(14.0f) + f11);
            int alpha = Theme.chat_msgErrorPaint.getAlpha();
            Theme.chat_msgErrorPaint.setAlpha((int) (alpha * f6));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
            Theme.chat_msgErrorPaint.setAlpha(alpha);
            BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), f11 + AndroidUtilities.dp(2.0f));
            Theme.chat_msgErrorDrawable.setAlpha((int) (f6 * 255.0f));
            if (z8) {
                canvas.save();
                canvas.scale(f5, f5, Theme.chat_msgErrorDrawable.getBounds().centerX(), Theme.chat_msgErrorDrawable.getBounds().centerY());
            }
            Theme.chat_msgErrorDrawable.draw(canvas);
            Theme.chat_msgErrorDrawable.setAlpha(255);
            if (!z8) {
                return;
            }
            canvas.restore();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:333:0x09c9, code lost:
        if (r1[0] == 3) goto L335;
     */
    /* JADX WARN: Code restructure failed: missing block: B:587:0x110d, code lost:
        if (r3 == 2) goto L590;
     */
    /* JADX WARN: Removed duplicated region for block: B:475:0x0dc6  */
    /* JADX WARN: Removed duplicated region for block: B:730:0x13d7  */
    /* JADX WARN: Removed duplicated region for block: B:734:0x13f0  */
    /* JADX WARN: Removed duplicated region for block: B:742:0x1411  */
    /* JADX WARN: Removed duplicated region for block: B:746:0x1428  */
    /* JADX WARN: Removed duplicated region for block: B:768:0x1466  */
    /* JADX WARN: Removed duplicated region for block: B:772:0x1477  */
    /* JADX WARN: Removed duplicated region for block: B:818:0x1585  */
    /* JADX WARN: Removed duplicated region for block: B:824:0x1599  */
    /* JADX WARN: Removed duplicated region for block: B:828:0x15a7  */
    /* JADX WARN: Removed duplicated region for block: B:862:0x166d  */
    /* JADX WARN: Removed duplicated region for block: B:865:0x1674  */
    /* JADX WARN: Removed duplicated region for block: B:915:0x17bd  */
    /* JADX WARN: Removed duplicated region for block: B:922:0x17dc  */
    /* JADX WARN: Removed duplicated region for block: B:925:0x1830  */
    /* JADX WARN: Removed duplicated region for block: B:948:0x0dcf A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:949:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v439, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawOverlays(android.graphics.Canvas r28) {
        /*
            Method dump skipped, instructions count: 6367
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawOverlays(android.graphics.Canvas):void");
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        return messageObject != null ? messageObject : this.currentMessageObject;
    }

    public TLRPC$Document getStreamingMedia() {
        int i = this.documentAttachType;
        if (i == 4 || i == 7 || i == 2) {
            return this.documentAttach;
        }
        return null;
    }

    public boolean drawPinnedBottom() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.isDocuments) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null && (groupedMessagePosition.flags & 8) != 0) {
                return this.pinnedBottom;
            }
            return true;
        }
        return this.pinnedBottom;
    }

    public boolean drawPinnedTop() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.isDocuments) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null && (groupedMessagePosition.flags & 4) != 0) {
                return this.pinnedTop;
            }
            return true;
        }
        return this.pinnedTop;
    }

    public boolean isPinnedBottom() {
        return this.pinnedBottom;
    }

    public boolean isPinnedTop() {
        return this.pinnedTop;
    }

    public MessageObject.GroupedMessages getCurrentMessagesGroup() {
        return this.currentMessagesGroup;
    }

    public MessageObject.GroupedMessagePosition getCurrentPosition() {
        return this.currentPosition;
    }

    public int getLayoutHeight() {
        return this.layoutHeight;
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
        if (chatMessageCellDelegate2 == null || !chatMessageCellDelegate2.onAccessibilityAction(i, bundle)) {
            if (i == 16) {
                int iconForCurrentState = getIconForCurrentState();
                if (iconForCurrentState != 4 && iconForCurrentState != 5) {
                    didPressButton(true, false);
                } else if (this.currentMessageObject.type == 16) {
                    this.delegate.didPressOther(this, this.otherX, this.otherY);
                } else {
                    didClickedImage();
                }
                return true;
            }
            if (i == R.id.acc_action_small_button) {
                didPressMiniButton(true);
            } else if (i == R.id.acc_action_msg_options) {
                ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
                if (chatMessageCellDelegate3 != null) {
                    if (this.currentMessageObject.type == 16) {
                        chatMessageCellDelegate3.didLongPress(this, 0.0f, 0.0f);
                    } else {
                        chatMessageCellDelegate3.didPressOther(this, this.otherX, this.otherY);
                    }
                }
            } else if (i == R.id.acc_action_open_forwarded_origin && (chatMessageCellDelegate = this.delegate) != null) {
                TLRPC$Chat tLRPC$Chat = this.currentForwardChannel;
                if (tLRPC$Chat != null) {
                    chatMessageCellDelegate.didPressChannelAvatar(this, tLRPC$Chat, this.currentMessageObject.messageOwner.fwd_from.channel_post, this.lastTouchX, this.lastTouchY);
                } else {
                    TLRPC$User tLRPC$User = this.currentForwardUser;
                    if (tLRPC$User != null) {
                        chatMessageCellDelegate.didPressUserAvatar(this, tLRPC$User, this.lastTouchX, this.lastTouchY);
                    } else if (this.currentForwardName != null) {
                        chatMessageCellDelegate.didPressHiddenForward(this);
                    }
                }
            }
            if ((!this.currentMessageObject.isVoice() && !this.currentMessageObject.isRoundVideo() && (!this.currentMessageObject.isMusic() || !MediaController.getInstance().isPlayingMessage(this.currentMessageObject))) || !this.seekBarAccessibilityDelegate.performAccessibilityActionInternal(i, bundle)) {
                return super.performAccessibilityAction(i, bundle);
            }
            return true;
        }
        return false;
    }

    public void setAnimationRunning(boolean z, boolean z2) {
        this.animationRunning = z;
        if (z) {
            this.willRemoved = z2;
        } else {
            this.willRemoved = false;
        }
        if (getParent() != null || !this.attachedToWindow) {
            return;
        }
        onDetachedFromWindow();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
            for (int i = 0; i < this.accessibilityVirtualViewBounds.size(); i++) {
                if (this.accessibilityVirtualViewBounds.valueAt(i).contains(x, y)) {
                    int keyAt = this.accessibilityVirtualViewBounds.keyAt(i);
                    if (keyAt == this.currentFocusedVirtualView) {
                        return true;
                    }
                    this.currentFocusedVirtualView = keyAt;
                    sendAccessibilityEventForVirtualView(keyAt, 32768);
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 10) {
            this.currentFocusedVirtualView = 0;
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return new MessageAccessibilityNodeProvider();
    }

    public void sendAccessibilityEventForVirtualView(int i, int i2) {
        sendAccessibilityEventForVirtualView(i, i2, null);
    }

    private void sendAccessibilityEventForVirtualView(int i, int i2, String str) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
            obtain.setPackageName(getContext().getPackageName());
            obtain.setSource(this, i);
            if (str != null) {
                obtain.getText().add(str);
            }
            if (getParent() == null) {
                return;
            }
            getParent().requestSendAccessibilityEvent(this, obtain);
        }
    }

    public static Point getMessageSize(int i, int i2) {
        return getMessageSize(i, i2, 0, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0042  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x004c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static org.telegram.ui.Components.Point getMessageSize(int r3, int r4, int r5, int r6) {
        /*
            if (r6 == 0) goto L4
            if (r5 != 0) goto L50
        L4:
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            r6 = 1060320051(0x3f333333, float:0.7)
            if (r5 == 0) goto L16
            int r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide()
        L11:
            float r5 = (float) r5
            float r5 = r5 * r6
            int r5 = (int) r5
            goto L35
        L16:
            if (r3 < r4) goto L2a
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r6, r5)
            r6 = 1115684864(0x42800000, float:64.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            goto L35
        L2a:
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r5.x
            int r5 = r5.y
            int r5 = java.lang.Math.min(r0, r5)
            goto L11
        L35:
            r6 = 1120403456(0x42c80000, float:100.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r5
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r5 <= r0) goto L46
            int r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L46:
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            if (r6 <= r0) goto L50
            int r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
        L50:
            float r3 = (float) r3
            float r5 = (float) r5
            float r0 = r3 / r5
            float r1 = r3 / r0
            int r1 = (int) r1
            float r4 = (float) r4
            float r0 = r4 / r0
            int r0 = (int) r0
            r2 = 1125515264(0x43160000, float:150.0)
            if (r1 != 0) goto L63
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L63:
            if (r0 != 0) goto L69
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
        L69:
            if (r0 <= r6) goto L72
            float r3 = (float) r0
            float r4 = (float) r6
            float r3 = r3 / r4
            float r4 = (float) r1
            float r4 = r4 / r3
            int r1 = (int) r4
            goto L88
        L72:
            r6 = 1123024896(0x42f00000, float:120.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            if (r0 >= r2) goto L87
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r0 = (float) r6
            float r4 = r4 / r0
            float r3 = r3 / r4
            int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r4 >= 0) goto L88
            int r1 = (int) r3
            goto L88
        L87:
            r6 = r0
        L88:
            org.telegram.ui.Components.Point r3 = new org.telegram.ui.Components.Point
            float r4 = (float) r1
            float r5 = (float) r6
            r3.<init>(r4, r5)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMessageSize(int, int, int, int):org.telegram.ui.Components.Point");
    }

    public StaticLayout getDescriptionlayout() {
        return this.descriptionLayout;
    }

    public void setSelectedBackgroundProgress(float f) {
        this.selectedBackgroundProgress = f;
        invalidate();
    }

    public int computeHeight(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages) {
        this.photoImage.setIgnoreImageSet(true);
        this.avatarImage.setIgnoreImageSet(true);
        this.replyImageReceiver.setIgnoreImageSet(true);
        this.locationImageReceiver.setIgnoreImageSet(true);
        if (groupedMessages != null && groupedMessages.messages.size() != 1) {
            int i = 0;
            for (int i2 = 0; i2 < groupedMessages.messages.size(); i2++) {
                MessageObject messageObject2 = groupedMessages.messages.get(i2);
                MessageObject.GroupedMessagePosition groupedMessagePosition = groupedMessages.positions.get(messageObject2);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & 1) != 0) {
                    setMessageContent(messageObject2, groupedMessages, false, false);
                    i += this.totalHeight + this.keyboardHeight;
                }
            }
            return i;
        }
        setMessageContent(messageObject, groupedMessages, false, false);
        this.photoImage.setIgnoreImageSet(false);
        this.avatarImage.setIgnoreImageSet(false);
        this.replyImageReceiver.setIgnoreImageSet(false);
        this.locationImageReceiver.setIgnoreImageSet(false);
        return this.totalHeight + this.keyboardHeight;
    }

    public void shakeView() {
        PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe(View.ROTATION, Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.2f, 3.0f), Keyframe.ofFloat(0.4f, -3.0f), Keyframe.ofFloat(0.6f, 3.0f), Keyframe.ofFloat(0.8f, -3.0f), Keyframe.ofFloat(1.0f, 0.0f));
        Keyframe ofFloat = Keyframe.ofFloat(0.0f, 1.0f);
        Keyframe ofFloat2 = Keyframe.ofFloat(0.5f, 0.97f);
        Keyframe ofFloat3 = Keyframe.ofFloat(1.0f, 1.0f);
        PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe(View.SCALE_X, ofFloat, ofFloat2, ofFloat3);
        PropertyValuesHolder ofKeyframe3 = PropertyValuesHolder.ofKeyframe(View.SCALE_Y, ofFloat, ofFloat2, ofFloat3);
        AnimatorSet animatorSet = new AnimatorSet();
        this.shakeAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe), ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe2), ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe3));
        this.shakeAnimation.setDuration(500L);
        this.shakeAnimation.start();
    }

    private void cancelShakeAnimation() {
        AnimatorSet animatorSet = this.shakeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.shakeAnimation = null;
            setScaleX(1.0f);
            setScaleY(1.0f);
            setRotation(0.0f);
        }
    }

    public void setSlidingOffset(float f) {
        if (this.slidingOffsetX != f) {
            this.slidingOffsetX = f;
            updateTranslation();
        }
    }

    public void setAnimationOffsetX(float f) {
        if (this.animationOffsetX != f) {
            this.animationOffsetX = f;
            updateTranslation();
        }
    }

    private void updateTranslation() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        setTranslationX(this.slidingOffsetX + this.animationOffsetX + (!messageObject.isOutOwner() ? this.checkBoxTranslation : 0));
    }

    public float getNonAnimationTranslationX(boolean z) {
        boolean z2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isOutOwner()) {
            if (z && ((z2 = this.checkBoxVisible) || this.checkBoxAnimationInProgress)) {
                this.checkBoxTranslation = (int) Math.ceil((z2 ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
            }
            return this.slidingOffsetX + this.checkBoxTranslation;
        }
        return this.slidingOffsetX;
    }

    public float getSlidingOffsetX() {
        return this.slidingOffsetX;
    }

    public boolean willRemovedAfterAnimation() {
        return this.willRemoved;
    }

    public float getAnimationOffsetX() {
        return this.animationOffsetX;
    }

    @Override // android.view.View
    public void setTranslationX(float f) {
        super.setTranslationX(f);
    }

    public SeekBar getSeekBar() {
        return this.seekBar;
    }

    public SeekBarWaveform getSeekBarWaveform() {
        return this.seekBarWaveform;
    }

    /* loaded from: classes3.dex */
    public class MessageAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private Path linkPath;
        private Rect rect;
        private RectF rectF;

        private MessageAccessibilityNodeProvider() {
            ChatMessageCell.this = r1;
            this.linkPath = new Path();
            this.rectF = new RectF();
            this.rect = new Rect();
        }

        /* loaded from: classes3.dex */
        private class ProfileSpan extends ClickableSpan {
            private TLRPC$User user;

            public ProfileSpan(TLRPC$User tLRPC$User) {
                MessageAccessibilityNodeProvider.this = r1;
                this.user = tLRPC$User;
            }

            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.didPressUserAvatar(ChatMessageCell.this, this.user, 0.0f, 0.0f);
                }
            }
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            String str;
            String str2;
            int i2;
            CharacterStyle[] characterStyleArr;
            CharacterStyle[] characterStyleArr2;
            String str3;
            AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo;
            String str4;
            String str5;
            CharacterStyle[] characterStyleArr3;
            boolean z;
            TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction;
            String str6;
            int i3;
            String str7;
            int[] iArr = {0, 0};
            ChatMessageCell.this.getLocationOnScreen(iArr);
            int i4 = 10;
            if (i == -1) {
                AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(ChatMessageCell.this);
                ChatMessageCell.this.onInitializeAccessibilityNodeInfo(obtain);
                if (ChatMessageCell.this.accessibilityText == null) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    ChatMessageCell chatMessageCell = ChatMessageCell.this;
                    if (chatMessageCell.isChat && chatMessageCell.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                        spannableStringBuilder.append((CharSequence) UserObject.getUserName(ChatMessageCell.this.currentUser));
                        spannableStringBuilder.setSpan(new ProfileSpan(ChatMessageCell.this.currentUser), 0, spannableStringBuilder.length(), 33);
                        spannableStringBuilder.append('\n');
                    }
                    if (ChatMessageCell.this.drawForwardedName) {
                        int i5 = 0;
                        while (i5 < 2) {
                            if (ChatMessageCell.this.forwardedNameLayout[i5] != null) {
                                spannableStringBuilder.append(ChatMessageCell.this.forwardedNameLayout[i5].getText());
                                spannableStringBuilder.append(i5 == 0 ? " " : "\n");
                            }
                            i5++;
                        }
                    }
                    if (!TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.messageText)) {
                        spannableStringBuilder.append(ChatMessageCell.this.currentMessageObject.messageText);
                    }
                    if (ChatMessageCell.this.documentAttach == null || !((ChatMessageCell.this.documentAttachType == 1 || ChatMessageCell.this.documentAttachType == 2 || ChatMessageCell.this.documentAttachType == 4) && ChatMessageCell.this.buttonState == 1 && ChatMessageCell.this.loadingProgressLayout != null)) {
                        str4 = " ";
                    } else {
                        spannableStringBuilder.append((CharSequence) "\n");
                        boolean isSending = ChatMessageCell.this.currentMessageObject.isSending();
                        str4 = " ";
                        spannableStringBuilder.append((CharSequence) LocaleController.formatString(isSending ? "AccDescrUploadProgress" : "AccDescrDownloadProgress", isSending ? R.string.AccDescrUploadProgress : R.string.AccDescrDownloadProgress, AndroidUtilities.formatFileSize(ChatMessageCell.this.currentMessageObject.loadedFileSize), AndroidUtilities.formatFileSize(ChatMessageCell.this.lastLoadingSizeTotal)));
                    }
                    if (!ChatMessageCell.this.currentMessageObject.isMusic()) {
                        if (ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.isRoundVideo) {
                            spannableStringBuilder.append((CharSequence) ", ");
                            spannableStringBuilder.append((CharSequence) LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                            spannableStringBuilder.append((CharSequence) ", ");
                            if (ChatMessageCell.this.currentMessageObject.isContentUnread()) {
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgNotPlayed", R.string.AccDescrMsgNotPlayed));
                            } else {
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgPlayed", R.string.AccDescrMsgPlayed));
                            }
                        }
                    } else {
                        spannableStringBuilder.append((CharSequence) "\n");
                        spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, ChatMessageCell.this.currentMessageObject.getMusicAuthor(), ChatMessageCell.this.currentMessageObject.getMusicTitle()));
                        spannableStringBuilder.append((CharSequence) ", ");
                        spannableStringBuilder.append((CharSequence) LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                    }
                    if (ChatMessageCell.this.lastPoll != null) {
                        spannableStringBuilder.append((CharSequence) ", ");
                        spannableStringBuilder.append((CharSequence) ChatMessageCell.this.lastPoll.question);
                        spannableStringBuilder.append((CharSequence) ", ");
                        if (!ChatMessageCell.this.pollClosed) {
                            if (ChatMessageCell.this.lastPoll.quiz) {
                                if (ChatMessageCell.this.lastPoll.public_voters) {
                                    str7 = LocaleController.getString("QuizPoll", R.string.QuizPoll);
                                } else {
                                    str7 = LocaleController.getString("AnonymousQuizPoll", R.string.AnonymousQuizPoll);
                                }
                            } else if (ChatMessageCell.this.lastPoll.public_voters) {
                                str7 = LocaleController.getString("PublicPoll", R.string.PublicPoll);
                            } else {
                                str7 = LocaleController.getString("AnonymousPoll", R.string.AnonymousPoll);
                            }
                        } else {
                            str7 = LocaleController.getString("FinalResults", R.string.FinalResults);
                        }
                        spannableStringBuilder.append((CharSequence) str7);
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageOwner.media != null && !TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.caption)) {
                        spannableStringBuilder.append((CharSequence) "\n");
                        spannableStringBuilder.append(ChatMessageCell.this.currentMessageObject.caption);
                    }
                    if (ChatMessageCell.this.documentAttach != null) {
                        if (ChatMessageCell.this.documentAttachType == 4) {
                            spannableStringBuilder.append((CharSequence) ", ");
                            spannableStringBuilder.append((CharSequence) LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                        }
                        if (ChatMessageCell.this.buttonState == 0 || ChatMessageCell.this.documentAttachType == 1) {
                            spannableStringBuilder.append((CharSequence) ", ");
                            spannableStringBuilder.append((CharSequence) AndroidUtilities.formatFileSize(ChatMessageCell.this.documentAttach.size));
                        }
                    }
                    if (ChatMessageCell.this.currentMessageObject.isOut()) {
                        if (!ChatMessageCell.this.currentMessageObject.isSent()) {
                            str5 = str4;
                            if (!ChatMessageCell.this.currentMessageObject.isSending()) {
                                if (ChatMessageCell.this.currentMessageObject.isSendError()) {
                                    spannableStringBuilder.append((CharSequence) "\n");
                                    spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgSendingError", R.string.AccDescrMsgSendingError));
                                }
                            } else {
                                spannableStringBuilder.append((CharSequence) "\n");
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("AccDescrMsgSending", R.string.AccDescrMsgSending));
                                float progress = ChatMessageCell.this.radialProgress.getProgress();
                                if (progress > 0.0f) {
                                    spannableStringBuilder.append((CharSequence) ", ").append((CharSequence) Integer.toString(Math.round(progress * 100.0f))).append((CharSequence) "%");
                                }
                            }
                        } else {
                            spannableStringBuilder.append((CharSequence) "\n");
                            if (ChatMessageCell.this.currentMessageObject.scheduled) {
                                spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrScheduledDate", R.string.AccDescrScheduledDate, ChatMessageCell.this.currentTimeString));
                                str5 = str4;
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append(LocaleController.getString("TodayAt", R.string.TodayAt));
                                str5 = str4;
                                sb.append(str5);
                                sb.append(ChatMessageCell.this.currentTimeString);
                                spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrSentDate", R.string.AccDescrSentDate, sb.toString()));
                                spannableStringBuilder.append((CharSequence) ", ");
                                if (ChatMessageCell.this.currentMessageObject.isUnread()) {
                                    i3 = R.string.AccDescrMsgUnread;
                                    str6 = "AccDescrMsgUnread";
                                } else {
                                    i3 = R.string.AccDescrMsgRead;
                                    str6 = "AccDescrMsgRead";
                                }
                                spannableStringBuilder.append((CharSequence) LocaleController.getString(str6, i3));
                            }
                        }
                    } else {
                        str5 = str4;
                        spannableStringBuilder.append((CharSequence) "\n");
                        spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrReceivedDate", R.string.AccDescrReceivedDate, LocaleController.getString("TodayAt", R.string.TodayAt) + str5 + ChatMessageCell.this.currentTimeString));
                    }
                    if (ChatMessageCell.this.getRepliesCount() > 0 && !ChatMessageCell.this.hasCommentLayout()) {
                        spannableStringBuilder.append((CharSequence) "\n");
                        spannableStringBuilder.append((CharSequence) LocaleController.formatPluralString("AccDescrNumberOfReplies", ChatMessageCell.this.getRepliesCount(), new Object[0]));
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions != null && ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results != null) {
                        if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.size() == 1) {
                            TLRPC$TL_reactionCount tLRPC$TL_reactionCount = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.get(0);
                            int i6 = tLRPC$TL_reactionCount.count;
                            if (i6 == 1) {
                                spannableStringBuilder.append((CharSequence) "\n");
                                String str8 = "";
                                if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions == null || ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions.size() != 1 || (tLRPC$TL_messagePeerReaction = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions.get(0)) == null) {
                                    z = false;
                                } else {
                                    TLRPC$User user = MessagesController.getInstance(ChatMessageCell.this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$TL_messagePeerReaction.peer_id)));
                                    z = UserObject.isUserSelf(user);
                                    if (user != null) {
                                        str8 = UserObject.getFirstName(user);
                                    }
                                }
                                if (z) {
                                    spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrYouReactedWith", R.string.AccDescrYouReactedWith, tLRPC$TL_reactionCount.reaction));
                                } else {
                                    spannableStringBuilder.append((CharSequence) LocaleController.formatString("AccDescrReactedWith", R.string.AccDescrReactedWith, str8, tLRPC$TL_reactionCount.reaction));
                                }
                            } else if (i6 > 1) {
                                spannableStringBuilder.append((CharSequence) "\n");
                                spannableStringBuilder.append((CharSequence) LocaleController.formatPluralString("AccDescrNumberOfPeopleReactions", tLRPC$TL_reactionCount.count, tLRPC$TL_reactionCount.reaction));
                            }
                        } else {
                            spannableStringBuilder.append((CharSequence) LocaleController.getString("Reactions", R.string.Reactions)).append((CharSequence) ": ");
                            int size = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.size();
                            for (int i7 = 0; i7 < size; i7++) {
                                TLRPC$TL_reactionCount tLRPC$TL_reactionCount2 = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.get(i7);
                                if (tLRPC$TL_reactionCount2 != null) {
                                    spannableStringBuilder.append((CharSequence) tLRPC$TL_reactionCount2.reaction).append((CharSequence) str5).append((CharSequence) (tLRPC$TL_reactionCount2.count + ""));
                                    if (i7 + 1 < size) {
                                        spannableStringBuilder.append((CharSequence) ", ");
                                    }
                                }
                            }
                            spannableStringBuilder.append((CharSequence) "\n");
                        }
                    }
                    if ((ChatMessageCell.this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                        spannableStringBuilder.append((CharSequence) "\n");
                        spannableStringBuilder.append((CharSequence) LocaleController.formatPluralString("AccDescrNumberOfViews", ChatMessageCell.this.currentMessageObject.messageOwner.views, new Object[0]));
                    }
                    spannableStringBuilder.append((CharSequence) "\n");
                    for (final CharacterStyle characterStyle : (CharacterStyle[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ClickableSpan.class)) {
                        int spanStart = spannableStringBuilder.getSpanStart(characterStyle);
                        int spanEnd = spannableStringBuilder.getSpanEnd(characterStyle);
                        spannableStringBuilder.removeSpan(characterStyle);
                        spannableStringBuilder.setSpan(new ClickableSpan() { // from class: org.telegram.ui.Cells.ChatMessageCell.MessageAccessibilityNodeProvider.1
                            @Override // android.text.style.ClickableSpan
                            public void onClick(View view) {
                                CharacterStyle characterStyle2 = characterStyle;
                                if (!(characterStyle2 instanceof ProfileSpan)) {
                                    if (ChatMessageCell.this.delegate == null) {
                                        return;
                                    }
                                    ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, characterStyle, false);
                                    return;
                                }
                                ((ProfileSpan) characterStyle2).onClick(view);
                            }
                        }, spanStart, spanEnd, 33);
                    }
                    ChatMessageCell.this.accessibilityText = spannableStringBuilder;
                }
                int i8 = Build.VERSION.SDK_INT;
                if (i8 < 24) {
                    obtain.setContentDescription(ChatMessageCell.this.accessibilityText.toString());
                } else {
                    obtain.setText(ChatMessageCell.this.accessibilityText);
                }
                obtain.setEnabled(true);
                if (i8 >= 19 && (collectionItemInfo = obtain.getCollectionItemInfo()) != null) {
                    obtain.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), 1, 0, 1, false));
                }
                if (i8 >= 21) {
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_msg_options, LocaleController.getString("AccActionMessageOptions", R.string.AccActionMessageOptions)));
                    int iconForCurrentState = ChatMessageCell.this.getIconForCurrentState();
                    if (iconForCurrentState == 0) {
                        str3 = LocaleController.getString("AccActionPlay", R.string.AccActionPlay);
                    } else if (iconForCurrentState == 1) {
                        str3 = LocaleController.getString("AccActionPause", R.string.AccActionPause);
                    } else if (iconForCurrentState == 2) {
                        str3 = LocaleController.getString("AccActionDownload", R.string.AccActionDownload);
                    } else if (iconForCurrentState == 3) {
                        str3 = LocaleController.getString("AccActionCancelDownload", R.string.AccActionCancelDownload);
                    } else if (iconForCurrentState == 5) {
                        str3 = LocaleController.getString("AccActionOpenFile", R.string.AccActionOpenFile);
                    } else {
                        str3 = ChatMessageCell.this.currentMessageObject.type == 16 ? LocaleController.getString("CallAgain", R.string.CallAgain) : null;
                    }
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, str3));
                    obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccActionEnterSelectionMode", R.string.AccActionEnterSelectionMode)));
                    if (ChatMessageCell.this.getMiniIconForCurrentState() == 2) {
                        obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_small_button, LocaleController.getString("AccActionDownload", R.string.AccActionDownload)));
                    }
                } else {
                    obtain.addAction(16);
                    obtain.addAction(32);
                }
                if ((ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.currentMessageObject.isRoundVideo() || ChatMessageCell.this.currentMessageObject.isMusic()) && MediaController.getInstance().isPlayingMessage(ChatMessageCell.this.currentMessageObject)) {
                    ChatMessageCell.this.seekBarAccessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(obtain);
                }
                if (i8 < 24) {
                    ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                    if (chatMessageCell2.isChat && chatMessageCell2.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                        obtain.addChild(ChatMessageCell.this, 5000);
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) {
                        Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                        int i9 = 0;
                        for (CharacterStyle characterStyle2 : (CharacterStyle[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class)) {
                            obtain.addChild(ChatMessageCell.this, i9 + 2000);
                            i9++;
                        }
                    }
                    if ((ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) && ChatMessageCell.this.captionLayout != null) {
                        Spannable spannable2 = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                        int i10 = 0;
                        for (CharacterStyle characterStyle3 : (CharacterStyle[]) spannable2.getSpans(0, spannable2.length(), ClickableSpan.class)) {
                            obtain.addChild(ChatMessageCell.this, i10 + 3000);
                            i10++;
                        }
                    }
                }
                Iterator it = ChatMessageCell.this.botButtons.iterator();
                int i11 = 0;
                while (it.hasNext()) {
                    BotButton botButton = (BotButton) it.next();
                    obtain.addChild(ChatMessageCell.this, i11 + 1000);
                    i11++;
                }
                if (ChatMessageCell.this.hintButtonVisible && ChatMessageCell.this.pollHintX != -1 && ChatMessageCell.this.currentMessageObject.isPoll()) {
                    obtain.addChild(ChatMessageCell.this, 495);
                }
                Iterator it2 = ChatMessageCell.this.pollButtons.iterator();
                int i12 = 0;
                while (it2.hasNext()) {
                    PollButton pollButton = (PollButton) it2.next();
                    obtain.addChild(ChatMessageCell.this, i12 + 500);
                    i12++;
                }
                if (ChatMessageCell.this.drawInstantView && !ChatMessageCell.this.instantButtonRect.isEmpty()) {
                    obtain.addChild(ChatMessageCell.this, 499);
                }
                if (ChatMessageCell.this.commentLayout != null) {
                    obtain.addChild(ChatMessageCell.this, 496);
                }
                if (ChatMessageCell.this.drawSideButton == 1) {
                    obtain.addChild(ChatMessageCell.this, 498);
                }
                ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                if (chatMessageCell3.replyNameLayout != null) {
                    obtain.addChild(chatMessageCell3, 497);
                }
                if (ChatMessageCell.this.forwardedNameLayout[0] != null && ChatMessageCell.this.forwardedNameLayout[1] != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        obtain.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_open_forwarded_origin, LocaleController.getString("AccActionOpenForwardedOrigin", R.string.AccActionOpenForwardedOrigin)));
                    } else {
                        obtain.addChild(ChatMessageCell.this, 494);
                    }
                }
                if (ChatMessageCell.this.drawSelectionBackground || ChatMessageCell.this.getBackground() != null) {
                    obtain.setSelected(true);
                }
                return obtain;
            }
            AccessibilityNodeInfo obtain2 = AccessibilityNodeInfo.obtain();
            obtain2.setSource(ChatMessageCell.this, i);
            obtain2.setParent(ChatMessageCell.this);
            obtain2.setPackageName(ChatMessageCell.this.getContext().getPackageName());
            if (i == 5000) {
                if (ChatMessageCell.this.currentUser == null) {
                    return null;
                }
                obtain2.setText(UserObject.getUserName(ChatMessageCell.this.currentUser));
                Rect rect = this.rect;
                int i13 = (int) ChatMessageCell.this.nameX;
                int i14 = (int) ChatMessageCell.this.nameY;
                int i15 = (int) (ChatMessageCell.this.nameX + ChatMessageCell.this.nameWidth);
                float f = ChatMessageCell.this.nameY;
                if (ChatMessageCell.this.nameLayout != null) {
                    i4 = ChatMessageCell.this.nameLayout.getHeight();
                }
                rect.set(i13, i14, i15, (int) (f + i4));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else if (i >= 3000) {
                if (!(ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) || ChatMessageCell.this.captionLayout == null) {
                    return null;
                }
                Spannable spannable3 = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                ClickableSpan linkById = getLinkById(i, true);
                if (linkById == null) {
                    return null;
                }
                int[] realSpanStartAndEnd = ChatMessageCell.this.getRealSpanStartAndEnd(spannable3, linkById);
                obtain2.setText(spannable3.subSequence(realSpanStartAndEnd[0], realSpanStartAndEnd[1]).toString());
                ChatMessageCell.this.captionLayout.getText().length();
                ChatMessageCell.this.captionLayout.getSelectionPath(realSpanStartAndEnd[0], realSpanStartAndEnd[1], this.linkPath);
                this.linkPath.computeBounds(this.rectF, true);
                Rect rect2 = this.rect;
                RectF rectF = this.rectF;
                rect2.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                this.rect.offset((int) ChatMessageCell.this.captionX, (int) ChatMessageCell.this.captionY);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else if (i >= 2000) {
                if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable)) {
                    return null;
                }
                Spannable spannable4 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                ClickableSpan linkById2 = getLinkById(i, false);
                if (linkById2 == null) {
                    return null;
                }
                int[] realSpanStartAndEnd2 = ChatMessageCell.this.getRealSpanStartAndEnd(spannable4, linkById2);
                obtain2.setText(spannable4.subSequence(realSpanStartAndEnd2[0], realSpanStartAndEnd2[1]).toString());
                Iterator<MessageObject.TextLayoutBlock> it3 = ChatMessageCell.this.currentMessageObject.textLayoutBlocks.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    MessageObject.TextLayoutBlock next = it3.next();
                    int length = next.textLayout.getText().length();
                    int i16 = next.charactersOffset;
                    if (i16 <= realSpanStartAndEnd2[0] && length + i16 >= realSpanStartAndEnd2[1]) {
                        next.textLayout.getSelectionPath(realSpanStartAndEnd2[0] - i16, realSpanStartAndEnd2[1] - i16, this.linkPath);
                        this.linkPath.computeBounds(this.rectF, true);
                        Rect rect3 = this.rect;
                        RectF rectF2 = this.rectF;
                        rect3.set((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                        this.rect.offset(0, (int) next.textYOffset);
                        this.rect.offset(ChatMessageCell.this.textX, ChatMessageCell.this.textY);
                        obtain2.setBoundsInParent(this.rect);
                        if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                            ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                        }
                        this.rect.offset(iArr[0], iArr[1]);
                        obtain2.setBoundsInScreen(this.rect);
                    }
                }
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else if (i >= 1000) {
                int i17 = i - 1000;
                if (i17 >= ChatMessageCell.this.botButtons.size()) {
                    return null;
                }
                BotButton botButton2 = (BotButton) ChatMessageCell.this.botButtons.get(i17);
                obtain2.setText(botButton2.title.getText());
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.addAction(16);
                this.rect.set(botButton2.x, botButton2.y, botButton2.x + botButton2.width, botButton2.y + botButton2.height);
                this.rect.offset(ChatMessageCell.this.currentMessageObject.isOutOwner() ? (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f) : ChatMessageCell.this.backgroundDrawableLeft + AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f), ChatMessageCell.this.layoutHeight);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
            } else if (i >= 500) {
                int i18 = i - 500;
                if (i18 >= ChatMessageCell.this.pollButtons.size()) {
                    return null;
                }
                PollButton pollButton2 = (PollButton) ChatMessageCell.this.pollButtons.get(i18);
                StringBuilder sb2 = new StringBuilder(pollButton2.title.getText());
                if (ChatMessageCell.this.pollVoted) {
                    obtain2.setSelected(pollButton2.chosen);
                    sb2.append(", ");
                    sb2.append(pollButton2.percent);
                    sb2.append("%");
                    if (ChatMessageCell.this.lastPoll != null && ChatMessageCell.this.lastPoll.quiz && (pollButton2.chosen || pollButton2.correct)) {
                        sb2.append(", ");
                        if (pollButton2.correct) {
                            i2 = R.string.AccDescrQuizCorrectAnswer;
                            str2 = "AccDescrQuizCorrectAnswer";
                        } else {
                            i2 = R.string.AccDescrQuizIncorrectAnswer;
                            str2 = "AccDescrQuizIncorrectAnswer";
                        }
                        sb2.append(LocaleController.getString(str2, i2));
                    }
                } else {
                    obtain2.setClassName("android.widget.Button");
                }
                obtain2.setText(sb2);
                obtain2.setEnabled(true);
                obtain2.addAction(16);
                int i19 = pollButton2.y + ChatMessageCell.this.namesOffset;
                int dp = ChatMessageCell.this.backgroundWidth - AndroidUtilities.dp(76.0f);
                Rect rect4 = this.rect;
                int i20 = pollButton2.x;
                rect4.set(i20, i19, dp + i20, pollButton2.height + i19);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 495) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                obtain2.setText(LocaleController.getString("AccDescrQuizExplanation", R.string.AccDescrQuizExplanation));
                obtain2.addAction(16);
                this.rect.set(ChatMessageCell.this.pollHintX - AndroidUtilities.dp(8.0f), ChatMessageCell.this.pollHintY - AndroidUtilities.dp(8.0f), ChatMessageCell.this.pollHintX + AndroidUtilities.dp(32.0f), ChatMessageCell.this.pollHintY + AndroidUtilities.dp(32.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 499) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                if (ChatMessageCell.this.instantViewLayout != null) {
                    obtain2.setText(ChatMessageCell.this.instantViewLayout.getText());
                }
                obtain2.addAction(16);
                ChatMessageCell.this.instantButtonRect.round(this.rect);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 498) {
                obtain2.setClassName("android.widget.ImageButton");
                obtain2.setEnabled(true);
                ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
                if (chatMessageCell4.isOpenChatByShare(chatMessageCell4.currentMessageObject)) {
                    obtain2.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
                } else {
                    obtain2.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
                }
                obtain2.addAction(16);
                this.rect.set((int) ChatMessageCell.this.sideStartX, (int) ChatMessageCell.this.sideStartY, ((int) ChatMessageCell.this.sideStartX) + AndroidUtilities.dp(40.0f), ((int) ChatMessageCell.this.sideStartY) + AndroidUtilities.dp(32.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 497) {
                obtain2.setEnabled(true);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(LocaleController.getString("Reply", R.string.Reply));
                sb3.append(", ");
                StaticLayout staticLayout = ChatMessageCell.this.replyNameLayout;
                if (staticLayout != null) {
                    sb3.append(staticLayout.getText());
                    sb3.append(", ");
                }
                StaticLayout staticLayout2 = ChatMessageCell.this.replyTextLayout;
                if (staticLayout2 != null) {
                    sb3.append(staticLayout2.getText());
                }
                obtain2.setContentDescription(sb3.toString());
                obtain2.addAction(16);
                Rect rect5 = this.rect;
                ChatMessageCell chatMessageCell5 = ChatMessageCell.this;
                int i21 = chatMessageCell5.replyStartX;
                rect5.set(i21, chatMessageCell5.replyStartY, Math.max(chatMessageCell5.replyNameWidth, ChatMessageCell.this.replyTextWidth) + i21, ChatMessageCell.this.replyStartY + AndroidUtilities.dp(35.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 494) {
                obtain2.setEnabled(true);
                StringBuilder sb4 = new StringBuilder();
                if (ChatMessageCell.this.forwardedNameLayout[0] != null && ChatMessageCell.this.forwardedNameLayout[1] != null) {
                    int i22 = 0;
                    while (i22 < 2) {
                        sb4.append(ChatMessageCell.this.forwardedNameLayout[i22].getText());
                        sb4.append(i22 == 0 ? " " : "\n");
                        i22++;
                    }
                }
                obtain2.setContentDescription(sb4.toString());
                obtain2.addAction(16);
                int min = (int) Math.min(ChatMessageCell.this.forwardNameX - ChatMessageCell.this.forwardNameOffsetX[0], ChatMessageCell.this.forwardNameX - ChatMessageCell.this.forwardNameOffsetX[1]);
                this.rect.set(min, ChatMessageCell.this.forwardNameY, ChatMessageCell.this.forwardedNameWidth + min, ChatMessageCell.this.forwardNameY + AndroidUtilities.dp(32.0f));
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            } else if (i == 496) {
                obtain2.setClassName("android.widget.Button");
                obtain2.setEnabled(true);
                int repliesCount = ChatMessageCell.this.getRepliesCount();
                if (ChatMessageCell.this.currentMessageObject != null && !ChatMessageCell.this.currentMessageObject.shouldDrawWithoutBackground() && !ChatMessageCell.this.currentMessageObject.isAnimatedEmoji()) {
                    if (ChatMessageCell.this.isRepliesChat) {
                        str = LocaleController.getString("ViewInChat", R.string.ViewInChat);
                    } else {
                        str = repliesCount == 0 ? LocaleController.getString("LeaveAComment", R.string.LeaveAComment) : LocaleController.formatPluralString("CommentsCount", repliesCount, new Object[0]);
                    }
                } else {
                    str = (ChatMessageCell.this.isRepliesChat || repliesCount <= 0) ? null : LocaleController.formatShortNumber(repliesCount, null);
                }
                if (str != null) {
                    obtain2.setText(str);
                }
                obtain2.addAction(16);
                this.rect.set(ChatMessageCell.this.commentButtonRect);
                obtain2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(i)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                }
                this.rect.offset(iArr[0], iArr[1]);
                obtain2.setBoundsInScreen(this.rect);
                obtain2.setClickable(true);
            }
            obtain2.setFocusable(true);
            obtain2.setVisibleToUser(true);
            return obtain2;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int i, int i2, Bundle bundle) {
            if (i == -1) {
                ChatMessageCell.this.performAccessibilityAction(i2, bundle);
            } else if (i2 == 64) {
                ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 32768);
            } else {
                boolean z = false;
                if (i2 == 16) {
                    if (i == 5000) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCellDelegate chatMessageCellDelegate = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell = ChatMessageCell.this;
                            chatMessageCellDelegate.didPressUserAvatar(chatMessageCell, chatMessageCell.currentUser, 0.0f, 0.0f);
                        }
                    } else if (i >= 3000) {
                        ClickableSpan linkById = getLinkById(i, true);
                        if (linkById != null) {
                            ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById, false);
                            ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                        }
                    } else if (i >= 2000) {
                        ClickableSpan linkById2 = getLinkById(i, false);
                        if (linkById2 != null) {
                            ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById2, false);
                            ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                        }
                    } else if (i >= 1000) {
                        int i3 = i - 1000;
                        if (i3 >= ChatMessageCell.this.botButtons.size()) {
                            return false;
                        }
                        BotButton botButton = (BotButton) ChatMessageCell.this.botButtons.get(i3);
                        if (ChatMessageCell.this.delegate != null && botButton.button != null) {
                            ChatMessageCell.this.delegate.didPressBotButton(ChatMessageCell.this, botButton.button);
                        }
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                    } else if (i >= 500) {
                        int i4 = i - 500;
                        if (i4 >= ChatMessageCell.this.pollButtons.size()) {
                            return false;
                        }
                        PollButton pollButton = (PollButton) ChatMessageCell.this.pollButtons.get(i4);
                        if (ChatMessageCell.this.delegate != null) {
                            ArrayList<TLRPC$TL_pollAnswer> arrayList = new ArrayList<>();
                            arrayList.add(pollButton.answer);
                            ChatMessageCell.this.delegate.didPressVoteButtons(ChatMessageCell.this, arrayList, -1, 0, 0);
                        }
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                    } else if (i == 495) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell.this.delegate.didPressHint(ChatMessageCell.this, 0);
                        }
                    } else if (i == 499) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCellDelegate chatMessageCellDelegate2 = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                            chatMessageCellDelegate2.didPressInstantButton(chatMessageCell2, chatMessageCell2.drawInstantViewType);
                        }
                    } else if (i == 498) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell.this.delegate.didPressSideButton(ChatMessageCell.this);
                        }
                    } else if (i == 497) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                            if ((!chatMessageCell3.isThreadChat || chatMessageCell3.currentMessageObject.getReplyTopMsgId() != 0) && ChatMessageCell.this.currentMessageObject.hasValidReplyMessageObject()) {
                                ChatMessageCellDelegate chatMessageCellDelegate3 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
                                chatMessageCellDelegate3.didPressReplyMessage(chatMessageCell4, chatMessageCell4.currentMessageObject.getReplyMsgId());
                            }
                        }
                    } else if (i == 494) {
                        if (ChatMessageCell.this.delegate != null) {
                            if (ChatMessageCell.this.currentForwardChannel != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate4 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell5 = ChatMessageCell.this;
                                chatMessageCellDelegate4.didPressChannelAvatar(chatMessageCell5, chatMessageCell5.currentForwardChannel, ChatMessageCell.this.currentMessageObject.messageOwner.fwd_from.channel_post, ChatMessageCell.this.lastTouchX, ChatMessageCell.this.lastTouchY);
                            } else if (ChatMessageCell.this.currentForwardUser != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate5 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell6 = ChatMessageCell.this;
                                chatMessageCellDelegate5.didPressUserAvatar(chatMessageCell6, chatMessageCell6.currentForwardUser, ChatMessageCell.this.lastTouchX, ChatMessageCell.this.lastTouchY);
                            } else if (ChatMessageCell.this.currentForwardName != null) {
                                ChatMessageCell.this.delegate.didPressHiddenForward(ChatMessageCell.this);
                            }
                        }
                    } else if (i == 496 && ChatMessageCell.this.delegate != null) {
                        ChatMessageCell chatMessageCell7 = ChatMessageCell.this;
                        if (chatMessageCell7.isRepliesChat) {
                            chatMessageCell7.delegate.didPressSideButton(ChatMessageCell.this);
                        } else {
                            chatMessageCell7.delegate.didPressCommentButton(ChatMessageCell.this);
                        }
                    }
                } else if (i2 == 32) {
                    if (i >= 3000) {
                        z = true;
                    }
                    ClickableSpan linkById3 = getLinkById(i, z);
                    if (linkById3 != null) {
                        ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, linkById3, true);
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 2);
                    }
                }
            }
            return true;
        }

        private ClickableSpan getLinkById(int i, boolean z) {
            if (i == 5000) {
                return null;
            }
            if (z) {
                int i2 = i - 3000;
                if (!(ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) || i2 < 0) {
                    return null;
                }
                Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class);
                if (clickableSpanArr.length > i2) {
                    return clickableSpanArr[i2];
                }
                return null;
            }
            int i3 = i - 2000;
            if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) || i3 < 0) {
                return null;
            }
            Spannable spannable2 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
            ClickableSpan[] clickableSpanArr2 = (ClickableSpan[]) spannable2.getSpans(0, spannable2.length(), ClickableSpan.class);
            if (clickableSpanArr2.length > i3) {
                return clickableSpanArr2[i3];
            }
            return null;
        }
    }

    public void setImageCoords(float f, float f2, float f3, float f4) {
        this.photoImage.setImageCoords(f, f2, f3, f4);
        int i = this.documentAttachType;
        if (i == 4 || i == 2) {
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY;
            RadialProgress2 radialProgress2 = this.videoRadialProgress;
            int i2 = this.videoButtonX;
            radialProgress2.setProgressRect(i2, imageY, AndroidUtilities.dp(24.0f) + i2, this.videoButtonY + AndroidUtilities.dp(24.0f));
            this.buttonX = (int) (f + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY2 = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY2;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i3 = this.buttonX;
            radialProgress22.setProgressRect(i3, imageY2, AndroidUtilities.dp(48.0f) + i3, this.buttonY + AndroidUtilities.dp(48.0f));
        }
    }

    @Override // android.view.View
    public float getAlpha() {
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            return this.alphaInternal;
        }
        return super.getAlpha();
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        boolean z = true;
        boolean z2 = f == 1.0f;
        if (getAlpha() != 1.0f) {
            z = false;
        }
        if (z2 != z) {
            invalidate();
        }
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            this.alphaInternal = f;
            invalidate();
            return;
        }
        super.setAlpha(f);
    }

    public int getCurrentBackgroundLeft() {
        int i = this.currentBackgroundDrawable.getBounds().left;
        return (this.currentMessageObject.isOutOwner() || this.transitionParams.changePinnedBottomProgress == 1.0f || this.mediaBackground || this.drawPinnedBottom) ? i : i - AndroidUtilities.dp(6.0f);
    }

    public TransitionParams getTransitionParams() {
        return this.transitionParams;
    }

    public int getTopMediaOffset() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.type != 14) {
            return 0;
        }
        return this.mediaOffsetY + this.namesOffset;
    }

    public int getTextX() {
        return this.textX;
    }

    public int getTextY() {
        return this.textY;
    }

    public boolean isPlayingRound() {
        return this.isRoundVideo && this.isPlayingRound;
    }

    public int getParentWidth() {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            messageObject = this.messageObjectToSet;
        }
        return (messageObject == null || !messageObject.preview || (i = this.parentWidth) <= 0) ? AndroidUtilities.displaySize.x : i;
    }

    /* loaded from: classes3.dex */
    public class TransitionParams {
        public boolean animateBackgroundBoundsInner;
        boolean animateBotButtonsChanged;
        private boolean animateButton;
        public boolean animateChange;
        private int animateCommentArrowX;
        private boolean animateCommentDrawUnread;
        private int animateCommentUnreadX;
        private float animateCommentX;
        private boolean animateComments;
        private StaticLayout animateCommentsLayout;
        private boolean animateDrawCommentNumber;
        public boolean animateDrawingTimeAlpha;
        private boolean animateEditedEnter;
        private StaticLayout animateEditedLayout;
        private int animateEditedWidthDiff;
        int animateForwardNameWidth;
        float animateForwardNameX;
        public boolean animateForwardedLayout;
        public int animateForwardedNamesOffset;
        private float animateFromButtonX;
        private float animateFromButtonY;
        public float animateFromReplyY;
        public float animateFromRoundVideoDotY;
        public float animateFromTextY;
        public int animateFromTimeX;
        public float animateFromTimeXPinned;
        private float animateFromTimeXReplies;
        private float animateFromTimeXViews;
        public boolean animateLocationIsExpired;
        boolean animateMessageText;
        private float animateNameX;
        private StaticLayout animateOutCaptionLayout;
        private ArrayList<MessageObject.TextLayoutBlock> animateOutTextBlocks;
        private boolean animatePinned;
        public boolean animatePlayingRound;
        public boolean animateRadius;
        boolean animateReplaceCaptionLayout;
        private boolean animateReplies;
        private StaticLayout animateRepliesLayout;
        public boolean animateRoundVideoDotY;
        private boolean animateShouldDrawMenuDrawable;
        private boolean animateShouldDrawTimeOnMedia;
        private boolean animateSign;
        public boolean animateText;
        private StaticLayout animateTimeLayout;
        private int animateTimeWidth;
        public float animateToImageH;
        public float animateToImageW;
        public float animateToImageX;
        public float animateToImageY;
        public int[] animateToRadius;
        private int animateTotalCommentWidth;
        private StaticLayout animateViewsLayout;
        public float captionFromX;
        public float captionFromY;
        public float deltaBottom;
        public float deltaLeft;
        public float deltaRight;
        public float deltaTop;
        public boolean drawPinnedBottomBackground;
        public boolean ignoreAlpha;
        public boolean imageChangeBoundsTransition;
        public int lastBackgroundLeft;
        public int lastBackgroundRight;
        private float lastButtonX;
        private float lastButtonY;
        private int lastCommentArrowX;
        private boolean lastCommentDrawUnread;
        private StaticLayout lastCommentLayout;
        private int lastCommentUnreadX;
        private float lastCommentX;
        private int lastCommentsCount;
        private boolean lastDrawCommentNumber;
        public StaticLayout lastDrawDocTitleLayout;
        public StaticLayout lastDrawInfoLayout;
        public float lastDrawLocationExpireProgress;
        public String lastDrawLocationExpireText;
        public float lastDrawReplyY;
        public float lastDrawRoundVideoDotY;
        public boolean lastDrawTime;
        private StaticLayout lastDrawingCaptionLayout;
        public float lastDrawingCaptionX;
        public float lastDrawingCaptionY;
        private boolean lastDrawingEdited;
        public float lastDrawingImageH;
        public float lastDrawingImageW;
        public float lastDrawingImageX;
        public float lastDrawingImageY;
        private ArrayList<MessageObject.TextLayoutBlock> lastDrawingTextBlocks;
        public float lastDrawingTextY;
        public boolean lastDrawnForwardedName;
        int lastForwardNameWidth;
        float lastForwardNameX;
        public int lastForwardedNamesOffset;
        private boolean lastIsPinned;
        private boolean lastIsPlayingRound;
        public boolean lastLocatinIsExpired;
        private int lastRepliesCount;
        private StaticLayout lastRepliesLayout;
        private boolean lastShouldDrawMenuDrawable;
        private boolean lastShouldDrawTimeOnMedia;
        private String lastSignMessage;
        private StaticLayout lastTimeLayout;
        private int lastTimeWidth;
        public int lastTimeX;
        public float lastTimeXPinned;
        private float lastTimeXReplies;
        private float lastTimeXViews;
        public int lastTopOffset;
        private int lastTotalCommentWidth;
        private int lastViewsCount;
        private StaticLayout lastViewsLayout;
        public boolean messageEntering;
        private boolean moveCaption;
        public boolean shouldAnimateTimeX;
        public float toDeltaLeft;
        public float toDeltaRight;
        public boolean transformGroupToSingleMessage;
        public boolean updatePhotoImageX;
        public boolean wasDraw;
        public int[] imageRoundRadius = new int[4];
        public float captionEnterProgress = 1.0f;
        public float changePinnedBottomProgress = 1.0f;
        public Rect lastDrawingBackgroundRect = new Rect();
        public float animateChangeProgress = 1.0f;
        private ArrayList<BotButton> lastDrawBotButtons = new ArrayList<>();
        private ArrayList<BotButton> transitionBotButtons = new ArrayList<>();
        public int lastStatusDrawableParams = -1;
        public StaticLayout[] lastDrawnForwardedNameLayout = new StaticLayout[2];
        public StaticLayout[] animatingForwardedNameLayout = new StaticLayout[2];

        public boolean supportChangeAnimation() {
            return true;
        }

        public TransitionParams() {
            ChatMessageCell.this = r2;
        }

        public void recordDrawingState() {
            this.wasDraw = true;
            this.lastDrawingImageX = ChatMessageCell.this.photoImage.getImageX();
            this.lastDrawingImageY = ChatMessageCell.this.photoImage.getImageY();
            this.lastDrawingImageW = ChatMessageCell.this.photoImage.getImageWidth();
            this.lastDrawingImageH = ChatMessageCell.this.photoImage.getImageHeight();
            System.arraycopy(ChatMessageCell.this.photoImage.getRoundRadius(), 0, this.imageRoundRadius, 0, 4);
            if (ChatMessageCell.this.currentBackgroundDrawable != null) {
                this.lastDrawingBackgroundRect.set(ChatMessageCell.this.currentBackgroundDrawable.getBounds());
            }
            this.lastDrawingTextBlocks = ChatMessageCell.this.currentMessageObject.textLayoutBlocks;
            this.lastDrawingEdited = ChatMessageCell.this.edited;
            this.lastDrawingCaptionX = ChatMessageCell.this.captionX;
            this.lastDrawingCaptionY = ChatMessageCell.this.captionY;
            this.lastDrawingCaptionLayout = ChatMessageCell.this.captionLayout;
            this.lastDrawBotButtons.clear();
            if (!ChatMessageCell.this.botButtons.isEmpty()) {
                this.lastDrawBotButtons.addAll(ChatMessageCell.this.botButtons);
            }
            if (ChatMessageCell.this.commentLayout != null) {
                this.lastCommentsCount = ChatMessageCell.this.getRepliesCount();
                this.lastTotalCommentWidth = ChatMessageCell.this.totalCommentWidth;
                this.lastCommentLayout = ChatMessageCell.this.commentLayout;
                this.lastCommentArrowX = ChatMessageCell.this.commentArrowX;
                this.lastCommentUnreadX = ChatMessageCell.this.commentUnreadX;
                this.lastCommentDrawUnread = ChatMessageCell.this.commentDrawUnread;
                this.lastCommentX = ChatMessageCell.this.commentX;
                this.lastDrawCommentNumber = ChatMessageCell.this.drawCommentNumber;
            }
            this.lastRepliesCount = ChatMessageCell.this.getRepliesCount();
            this.lastViewsCount = ChatMessageCell.this.getMessageObject().messageOwner.views;
            this.lastRepliesLayout = ChatMessageCell.this.repliesLayout;
            this.lastViewsLayout = ChatMessageCell.this.viewsLayout;
            ChatMessageCell chatMessageCell = ChatMessageCell.this;
            this.lastIsPinned = chatMessageCell.isPinned;
            this.lastSignMessage = chatMessageCell.lastPostAuthor;
            this.lastButtonX = ChatMessageCell.this.buttonX;
            this.lastButtonY = ChatMessageCell.this.buttonY;
            this.lastDrawTime = !ChatMessageCell.this.forceNotDrawTime;
            this.lastTimeX = ChatMessageCell.this.timeX;
            this.lastTimeLayout = ChatMessageCell.this.timeLayout;
            this.lastTimeWidth = ChatMessageCell.this.timeWidth;
            this.lastShouldDrawTimeOnMedia = ChatMessageCell.this.shouldDrawTimeOnMedia();
            this.lastTopOffset = ChatMessageCell.this.getTopMediaOffset();
            this.lastShouldDrawMenuDrawable = ChatMessageCell.this.shouldDrawMenuDrawable();
            this.lastLocatinIsExpired = ChatMessageCell.this.locationExpired;
            this.lastIsPlayingRound = ChatMessageCell.this.isPlayingRound;
            this.lastDrawingTextY = ChatMessageCell.this.textY;
            int unused = ChatMessageCell.this.textX;
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            this.lastForwardedNamesOffset = ChatMessageCell.this.namesOffset;
            this.lastForwardNameWidth = ChatMessageCell.this.forwardedNameWidth;
            this.lastBackgroundLeft = ChatMessageCell.this.getCurrentBackgroundLeft();
            this.lastBackgroundRight = ChatMessageCell.this.currentBackgroundDrawable.getBounds().right;
            ChatMessageCell.this.reactionsLayoutInBubble.recordDrawingState();
            ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
            if (chatMessageCell2.replyNameLayout != null) {
                this.lastDrawReplyY = chatMessageCell2.replyStartY;
            } else {
                this.lastDrawReplyY = 0.0f;
            }
        }

        public void recordDrawingStatePreview() {
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            this.lastForwardedNamesOffset = ChatMessageCell.this.namesOffset;
            this.lastForwardNameWidth = ChatMessageCell.this.forwardedNameWidth;
        }

        /* JADX WARN: Removed duplicated region for block: B:103:0x0273  */
        /* JADX WARN: Removed duplicated region for block: B:106:0x0283  */
        /* JADX WARN: Removed duplicated region for block: B:115:0x02b8  */
        /* JADX WARN: Removed duplicated region for block: B:116:0x02bd  */
        /* JADX WARN: Removed duplicated region for block: B:119:0x02c2  */
        /* JADX WARN: Removed duplicated region for block: B:126:0x02de  */
        /* JADX WARN: Removed duplicated region for block: B:131:0x02fa  */
        /* JADX WARN: Removed duplicated region for block: B:142:0x034c  */
        /* JADX WARN: Removed duplicated region for block: B:145:0x0361  */
        /* JADX WARN: Removed duplicated region for block: B:147:0x0367  */
        /* JADX WARN: Removed duplicated region for block: B:156:0x03b3  */
        /* JADX WARN: Removed duplicated region for block: B:159:0x03bf  */
        /* JADX WARN: Removed duplicated region for block: B:162:0x03cb  */
        /* JADX WARN: Removed duplicated region for block: B:165:0x03db  */
        /* JADX WARN: Removed duplicated region for block: B:168:0x03ea  */
        /* JADX WARN: Removed duplicated region for block: B:173:0x0422  */
        /* JADX WARN: Removed duplicated region for block: B:176:0x042f  */
        /* JADX WARN: Removed duplicated region for block: B:187:0x0462  */
        /* JADX WARN: Removed duplicated region for block: B:34:0x00a6  */
        /* JADX WARN: Removed duplicated region for block: B:45:0x00e2  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x014d  */
        /* JADX WARN: Removed duplicated region for block: B:50:0x016c  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x01d4  */
        /* JADX WARN: Removed duplicated region for block: B:85:0x0210  */
        /* JADX WARN: Removed duplicated region for block: B:89:0x022e  */
        /* JADX WARN: Removed duplicated region for block: B:92:0x0234  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean animateChange() {
            /*
                Method dump skipped, instructions count: 1141
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.TransitionParams.animateChange():boolean");
        }

        public void onDetach() {
            this.wasDraw = false;
        }

        public void resetAnimation() {
            this.animateChange = false;
            this.animatePinned = false;
            this.animateBackgroundBoundsInner = false;
            this.deltaLeft = 0.0f;
            this.deltaRight = 0.0f;
            this.deltaBottom = 0.0f;
            this.deltaTop = 0.0f;
            this.toDeltaLeft = 0.0f;
            this.toDeltaRight = 0.0f;
            if (this.imageChangeBoundsTransition && this.animateToImageW != 0.0f && this.animateToImageH != 0.0f) {
                ChatMessageCell.this.photoImage.setImageCoords(this.animateToImageX, this.animateToImageY, this.animateToImageW, this.animateToImageH);
            }
            if (this.animateRadius) {
                ChatMessageCell.this.photoImage.setRoundRadius(this.animateToRadius);
            }
            this.animateToImageX = 0.0f;
            this.animateToImageY = 0.0f;
            this.animateToImageW = 0.0f;
            this.animateToImageH = 0.0f;
            this.imageChangeBoundsTransition = false;
            this.changePinnedBottomProgress = 1.0f;
            this.captionEnterProgress = 1.0f;
            this.animateRadius = false;
            this.animateChangeProgress = 1.0f;
            this.animateMessageText = false;
            this.animateOutTextBlocks = null;
            this.animateEditedLayout = null;
            this.animateTimeLayout = null;
            this.animateEditedEnter = false;
            this.animateReplaceCaptionLayout = false;
            this.transformGroupToSingleMessage = false;
            this.animateOutCaptionLayout = null;
            this.moveCaption = false;
            this.animateDrawingTimeAlpha = false;
            this.transitionBotButtons.clear();
            this.animateButton = false;
            this.animateReplies = false;
            this.animateRepliesLayout = null;
            this.animateComments = false;
            this.animateCommentsLayout = null;
            this.animateViewsLayout = null;
            this.animateShouldDrawTimeOnMedia = false;
            this.animateShouldDrawMenuDrawable = false;
            this.shouldAnimateTimeX = false;
            this.animateSign = false;
            this.animateDrawingTimeAlpha = false;
            this.animateLocationIsExpired = false;
            this.animatePlayingRound = false;
            this.animateText = false;
            this.animateForwardedLayout = false;
            StaticLayout[] staticLayoutArr = this.animatingForwardedNameLayout;
            staticLayoutArr[0] = null;
            staticLayoutArr[1] = null;
            this.animateRoundVideoDotY = false;
            ChatMessageCell.this.reactionsLayoutInBubble.resetAnimation();
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x006b  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x006d  */
        /* JADX WARN: Removed duplicated region for block: B:28:0x0072  */
        /* JADX WARN: Removed duplicated region for block: B:31:0x0077  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public int createStatusDrawableParams() {
            /*
                r7 = this;
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isOutOwner()
                r1 = 8
                r2 = 4
                r3 = 1
                r4 = 0
                if (r0 == 0) goto L7a
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSending()
                if (r0 != 0) goto L65
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isEditing()
                if (r0 == 0) goto L2a
                goto L65
            L2a:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSendError()
                if (r0 == 0) goto L3b
                r0 = 0
                r3 = 0
                r5 = 0
                r6 = 1
                goto L69
            L3b:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSent()
                if (r0 == 0) goto L61
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.scheduled
                if (r0 != 0) goto L5f
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isUnread()
                if (r0 != 0) goto L5f
                r0 = 1
                goto L63
            L5f:
                r0 = 0
                goto L63
            L61:
                r0 = 0
                r3 = 0
            L63:
                r5 = 0
                goto L68
            L65:
                r0 = 0
                r3 = 0
                r5 = 1
            L68:
                r6 = 0
            L69:
                if (r3 == 0) goto L6d
                r3 = 2
                goto L6e
            L6d:
                r3 = 0
            L6e:
                r0 = r0 | r3
                if (r5 == 0) goto L72
                goto L73
            L72:
                r2 = 0
            L73:
                r0 = r0 | r2
                if (r6 == 0) goto L77
                goto L78
            L77:
                r1 = 0
            L78:
                r0 = r0 | r1
                return r0
            L7a:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSending()
                if (r0 != 0) goto L94
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isEditing()
                if (r0 == 0) goto L93
                goto L94
            L93:
                r3 = 0
            L94:
                org.telegram.ui.Cells.ChatMessageCell r0 = org.telegram.ui.Cells.ChatMessageCell.this
                org.telegram.messenger.MessageObject r0 = org.telegram.ui.Cells.ChatMessageCell.access$600(r0)
                boolean r0 = r0.isSendError()
                if (r3 == 0) goto La1
                goto La2
            La1:
                r2 = 0
            La2:
                if (r0 == 0) goto La5
                goto La6
            La5:
                r1 = 0
            La6:
                r0 = r2 | r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.TransitionParams.createStatusDrawableParams():int");
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
