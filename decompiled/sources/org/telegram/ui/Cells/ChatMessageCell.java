package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.view.animation.Interpolator;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedNumberLayout;
import org.telegram.ui.Components.AvatarDrawable;
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
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.SlotsDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes4.dex */
public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, DownloadController.FileDownloadProgressListener, TextSelectionHelper.SelectableView, NotificationCenter.NotificationCenterDelegate {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_ROUND = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_THEME = 9;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static final int DOCUMENT_ATTACH_TYPE_WALLPAPER = 8;
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
    private int backgroundDrawableBottom;
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
    public boolean clipToGroupBounds;
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
    private TLRPC.Chat currentChat;
    private int currentFocusedVirtualView;
    private TLRPC.Chat currentForwardChannel;
    private String currentForwardName;
    private String currentForwardNameString;
    private TLRPC.User currentForwardUser;
    private int currentMapProvider;
    private MessageObject currentMessageObject;
    private MessageObject.GroupedMessages currentMessagesGroup;
    private String currentNameString;
    private TLRPC.FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private TLRPC.PhotoSize currentPhotoObject;
    private TLRPC.PhotoSize currentPhotoObjectThumb;
    private BitmapDrawable currentPhotoObjectThumbStripped;
    private MessageObject.GroupedMessagePosition currentPosition;
    private String currentRepliesString;
    private TLRPC.PhotoSize currentReplyPhoto;
    private float currentSelectedBackgroundAlpha;
    private String currentTimeString;
    private String currentUrl;
    private TLRPC.User currentUser;
    private TLRPC.User currentViaBotUser;
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
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground;
    private boolean drawCommentButton;
    private boolean drawCommentNumber;
    private boolean drawForwardedName;
    public boolean drawFromPinchToZoom;
    private boolean drawImageButton;
    private boolean drawInstantView;
    private int drawInstantViewType;
    private boolean drawJoinChannelView;
    private boolean drawJoinGroupView;
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
    private boolean forwardName;
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
    private boolean isMedia;
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
    float lastDrawingPlayPauseAlpha;
    private int lastHeight;
    private long lastHighlightProgressTime;
    private long lastLoadingSizeTotal;
    private long lastNamesAnimationTime;
    private TLRPC.Poll lastPoll;
    private long lastPollCloseTime;
    private ArrayList<TLRPC.TL_pollAnswerVoters> lastPollResults;
    private int lastPollResultsVoters;
    private String lastPostAuthor;
    private TLRPC.TL_messageReactions lastReactions;
    private int lastRepliesCount;
    private TLRPC.Message lastReplyMessage;
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

    public RadialProgress2 getRadialProgress() {
        return this.radialProgress;
    }

    public void setEnterTransitionInProgress(boolean b) {
        this.enterTransitionInProgress = b;
        invalidate();
    }

    public ReactionsLayoutInBubble.ReactionButton getReactionButton(String reaction) {
        return this.reactionsLayoutInBubble.getReactionButton(reaction);
    }

    public MessageObject getPrimaryMessageObject() {
        MessageObject messageObject = null;
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && this.currentMessagesGroup != null && messageObject2.hasValidGroupId()) {
            messageObject = this.currentMessagesGroup.findPrimaryMessageObject();
        }
        if (messageObject != null) {
            return messageObject;
        }
        return this.currentMessageObject;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.User user;
        if (id == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
        } else if (id == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
        } else if (id == NotificationCenter.userInfoDidLoad && (user = this.currentUser) != null) {
            Long uid = (Long) args[0];
            if (user.id == uid.longValue()) {
                setAvatar(this.currentMessageObject);
            }
        }
    }

    private void setAvatar(MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        if (this.isAvatarVisible) {
            if (messageObject.customAvatarDrawable != null) {
                this.avatarImage.setImageBitmap(messageObject.customAvatarDrawable);
                return;
            }
            TLRPC.User user = this.currentUser;
            if (user != null) {
                if (user.photo != null) {
                    this.currentPhoto = this.currentUser.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentUser);
                this.avatarImage.setForUserOrChat(this.currentUser, this.avatarDrawable, null, true);
                return;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null) {
                if (chat.photo != null) {
                    this.currentPhoto = this.currentChat.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentChat);
                this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
                return;
            } else if (messageObject != null && messageObject.isSponsored()) {
                if (messageObject.sponsoredChatInvite != null && messageObject.sponsoredChatInvite.chat != null) {
                    this.avatarDrawable.setInfo(messageObject.sponsoredChatInvite.chat);
                    this.avatarImage.setForUserOrChat(messageObject.sponsoredChatInvite.chat, this.avatarDrawable);
                    return;
                }
                this.avatarDrawable.setInfo(messageObject.sponsoredChatInvite);
                TLRPC.Photo photo = messageObject.sponsoredChatInvite.photo;
                if (photo != null) {
                    this.avatarImage.setImage(ImageLocation.getForPhoto(photo.sizes.get(0), photo), "50_50", this.avatarDrawable, null, null, 0);
                    return;
                }
                return;
            } else {
                this.currentPhoto = null;
                this.avatarDrawable.setInfo(messageObject.getFromChatId(), null, null);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                return;
            }
        }
        this.currentPhoto = null;
    }

    public void setSpoilersSuppressed(boolean s) {
        for (SpoilerEffect eff : this.captionSpoilers) {
            eff.setSuppressUpdates(s);
        }
        for (SpoilerEffect eff2 : this.replySpoilers) {
            eff2.setSuppressUpdates(s);
        }
        if (getMessageObject() != null && getMessageObject().textLayoutBlocks != null) {
            Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
            while (it.hasNext()) {
                MessageObject.TextLayoutBlock bl = it.next();
                for (SpoilerEffect eff3 : bl.spoilers) {
                    eff3.setSuppressUpdates(s);
                }
            }
        }
    }

    public boolean hasSpoilers() {
        if ((!hasCaptionLayout() || this.captionSpoilers.isEmpty()) && (this.replyTextLayout == null || this.replySpoilers.isEmpty())) {
            if (getMessageObject() != null && getMessageObject().textLayoutBlocks != null) {
                Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
                while (it.hasNext()) {
                    MessageObject.TextLayoutBlock bl = it.next();
                    if (!bl.spoilers.isEmpty()) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return true;
    }

    private void updateSpoilersVisiblePart(int top, int bottom) {
        if (hasCaptionLayout()) {
            float off = -this.captionY;
            for (SpoilerEffect eff : this.captionSpoilers) {
                eff.setVisibleBounds(0.0f, top + off, getWidth(), bottom + off);
            }
        }
        StaticLayout staticLayout = this.replyTextLayout;
        if (staticLayout != null) {
            float off2 = (-this.replyStartY) - staticLayout.getHeight();
            for (SpoilerEffect eff2 : this.replySpoilers) {
                eff2.setVisibleBounds(0.0f, top + off2, getWidth(), bottom + off2);
            }
        }
        if (getMessageObject() != null && getMessageObject().textLayoutBlocks != null) {
            Iterator<MessageObject.TextLayoutBlock> it = getMessageObject().textLayoutBlocks.iterator();
            while (it.hasNext()) {
                MessageObject.TextLayoutBlock bl = it.next();
                for (SpoilerEffect eff3 : bl.spoilers) {
                    eff3.setVisibleBounds(0.0f, (top - bl.textYOffset) - this.textY, getWidth(), (bottom - bl.textYOffset) - this.textY);
                }
            }
        }
    }

    public void setScrimReaction(String scrimViewReaction) {
        this.reactionsLayoutInBubble.setScrimReaction(scrimViewReaction);
    }

    public void drawScrimReaction(Canvas canvas, String scrimViewReaction) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if ((groupedMessagePosition == null || ((groupedMessagePosition.flags & 8) != 0 && (this.currentPosition.flags & 1) != 0)) && !this.reactionsLayoutInBubble.isSmall) {
            this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, scrimViewReaction);
        }
    }

    public boolean checkUnreadReactions(float clipTop, int clipBottom) {
        if (!this.reactionsLayoutInBubble.hasUnreadReactions) {
            return false;
        }
        float y = getY() + this.reactionsLayoutInBubble.y;
        return y > clipTop && (((float) this.reactionsLayoutInBubble.height) + y) - ((float) AndroidUtilities.dp(16.0f)) < ((float) clipBottom);
    }

    public void markReactionsAsRead() {
        this.reactionsLayoutInBubble.hasUnreadReactions = false;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markReactionsAsRead();
    }

    /* loaded from: classes4.dex */
    public interface ChatMessageCellDelegate {
        boolean canDrawOutboundsContent();

        boolean canPerformActions();

        void didLongPress(ChatMessageCell chatMessageCell, float f, float f2);

        void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton);

        boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2);

        boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2);

        void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2);

        void didPressCommentButton(ChatMessageCell chatMessageCell);

        void didPressHiddenForward(ChatMessageCell chatMessageCell);

        void didPressHint(ChatMessageCell chatMessageCell, int i);

        void didPressImage(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressOther(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount, boolean z);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressSideButton(ChatMessageCell chatMessageCell);

        void didPressTime(ChatMessageCell chatMessageCell);

        void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j);

        void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TLRPC.TL_pollAnswer> arrayList, int i, int i2, int i3);

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

        /* renamed from: org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$didPressUserAvatar(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {
            }

            public static boolean $default$didLongPressUserAvatar(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {
                return false;
            }

            public static void $default$didPressHiddenForward(ChatMessageCellDelegate _this, ChatMessageCell cell) {
            }

            public static void $default$didPressViaBotNotInline(ChatMessageCellDelegate _this, ChatMessageCell cell, long botId) {
            }

            public static void $default$didPressViaBot(ChatMessageCellDelegate _this, ChatMessageCell cell, String username) {
            }

            public static void $default$didPressChannelAvatar(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.Chat chat, int postId, float touchX, float touchY) {
            }

            public static boolean $default$didLongPressChannelAvatar(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.Chat chat, int postId, float touchX, float touchY) {
                return false;
            }

            public static void $default$didPressCancelSendButton(ChatMessageCellDelegate _this, ChatMessageCell cell) {
            }

            public static void $default$didLongPress(ChatMessageCellDelegate _this, ChatMessageCell cell, float x, float y) {
            }

            public static void $default$didPressReplyMessage(ChatMessageCellDelegate _this, ChatMessageCell cell, int id) {
            }

            public static void $default$didPressUrl(ChatMessageCellDelegate _this, ChatMessageCell cell, CharacterStyle url, boolean longPress) {
            }

            public static void $default$needOpenWebView(ChatMessageCellDelegate _this, MessageObject message, String url, String title, String description, String originalUrl, int w, int h) {
            }

            public static void $default$didPressImage(ChatMessageCellDelegate _this, ChatMessageCell cell, float x, float y) {
            }

            public static void $default$didPressSideButton(ChatMessageCellDelegate _this, ChatMessageCell cell) {
            }

            public static void $default$didPressOther(ChatMessageCellDelegate _this, ChatMessageCell cell, float otherX, float otherY) {
            }

            public static void $default$didPressTime(ChatMessageCellDelegate _this, ChatMessageCell cell) {
            }

            public static void $default$didPressBotButton(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.KeyboardButton button) {
            }

            public static void $default$didLongPressBotButton(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.KeyboardButton button) {
            }

            public static void $default$didPressReaction(ChatMessageCellDelegate _this, ChatMessageCell cell, TLRPC.TL_reactionCount reaction, boolean longpress) {
            }

            public static void $default$didPressVoteButtons(ChatMessageCellDelegate _this, ChatMessageCell cell, ArrayList arrayList, int showCount, int x, int y) {
            }

            public static void $default$didPressInstantButton(ChatMessageCellDelegate _this, ChatMessageCell cell, int type) {
            }

            public static void $default$didPressCommentButton(ChatMessageCellDelegate _this, ChatMessageCell cell) {
            }

            public static void $default$didPressHint(ChatMessageCellDelegate _this, ChatMessageCell cell, int type) {
            }

            public static void $default$needShowPremiumFeatures(ChatMessageCellDelegate _this, String source) {
            }

            public static String $default$getAdminRank(ChatMessageCellDelegate _this, long uid) {
                return null;
            }

            public static boolean $default$needPlayMessage(ChatMessageCellDelegate _this, MessageObject messageObject) {
                return false;
            }

            public static boolean $default$canPerformActions(ChatMessageCellDelegate _this) {
                return false;
            }

            public static boolean $default$onAccessibilityAction(ChatMessageCellDelegate _this, int action, Bundle arguments) {
                return false;
            }

            public static void $default$videoTimerReached(ChatMessageCellDelegate _this) {
            }

            public static void $default$didStartVideoStream(ChatMessageCellDelegate _this, MessageObject message) {
            }

            public static boolean $default$shouldRepeatSticker(ChatMessageCellDelegate _this, MessageObject message) {
                return true;
            }

            public static void $default$setShouldNotRepeatSticker(ChatMessageCellDelegate _this, MessageObject message) {
            }

            public static TextSelectionHelper.ChatListTextSelectionHelper $default$getTextSelectionHelper(ChatMessageCellDelegate _this) {
                return null;
            }

            public static boolean $default$hasSelectedMessages(ChatMessageCellDelegate _this) {
                return false;
            }

            public static void $default$needReloadPolls(ChatMessageCellDelegate _this) {
            }

            public static void $default$onDiceFinished(ChatMessageCellDelegate _this) {
            }

            public static boolean $default$shouldDrawThreadProgress(ChatMessageCellDelegate _this, ChatMessageCell cell) {
                return false;
            }

            public static PinchToZoomHelper $default$getPinchToZoomHelper(ChatMessageCellDelegate _this) {
                return null;
            }

            public static boolean $default$keyboardIsOpened(ChatMessageCellDelegate _this) {
                return false;
            }

            public static boolean $default$isLandscape(ChatMessageCellDelegate _this) {
                return false;
            }

            public static void $default$invalidateBlur(ChatMessageCellDelegate _this) {
            }

            public static boolean $default$canDrawOutboundsContent(ChatMessageCellDelegate _this) {
                return true;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class BotButton {
        private int angle;
        private TLRPC.KeyboardButton button;
        private int height;
        private boolean isInviteButton;
        private long lastUpdateTime;
        private float progressAlpha;
        private TLRPC.TL_reactionCount reaction;
        private StaticLayout title;
        private int width;
        private int x;
        private int y;

        private BotButton() {
        }

        static /* synthetic */ float access$2716(BotButton x0, float x1) {
            float f = x0.progressAlpha + x1;
            x0.progressAlpha = f;
            return f;
        }

        static /* synthetic */ float access$2724(BotButton x0, float x1) {
            float f = x0.progressAlpha - x1;
            x0.progressAlpha = f;
            return f;
        }

        static /* synthetic */ int access$2816(BotButton x0, float x1) {
            int i = (int) (x0.angle + x1);
            x0.angle = i;
            return i;
        }

        static /* synthetic */ int access$2820(BotButton x0, int x1) {
            int i = x0.angle - x1;
            x0.angle = i;
            return i;
        }
    }

    /* loaded from: classes4.dex */
    public static class PollButton {
        private TLRPC.TL_pollAnswer answer;
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

        static /* synthetic */ int access$1712(PollButton x0, int x1) {
            int i = x0.percent + x1;
            x0.percent = i;
            return i;
        }

        static /* synthetic */ float access$2424(PollButton x0, float x1) {
            float f = x0.decimal - x1;
            x0.decimal = f;
            return f;
        }
    }

    public ChatMessageCell(Context context) {
        this(context, false, null);
    }

    public ChatMessageCell(Context context, boolean canDrawBackgroundInParent, Theme.ResourcesProvider resourcesProvider) {
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
                if (ChatMessageCell.this.scheduledInvalidate) {
                    AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000L);
                }
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
        this.ANIMATION_OFFSET_X = new Property<ChatMessageCell, Float>(Float.class, "animationOffsetX") { // from class: org.telegram.ui.Cells.ChatMessageCell.7
            public Float get(ChatMessageCell object) {
                return Float.valueOf(object.animationOffsetX);
            }

            public void set(ChatMessageCell object, Float value) {
                object.setAnimationOffsetX(value.floatValue());
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.canDrawBackgroundInParent = canDrawBackgroundInParent;
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
                } else if (ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    return ChatMessageCell.this.currentMessageObject.audioProgress;
                } else {
                    return 0.0f;
                }
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float progress) {
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    ChatMessageCell.this.seekBar.setProgress(progress);
                } else if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                    if (ChatMessageCell.this.useSeekBarWaveform) {
                        ChatMessageCell.this.seekBarWaveform.setProgress(progress);
                    } else {
                        ChatMessageCell.this.seekBar.setProgress(progress);
                    }
                } else if (ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    ChatMessageCell.this.currentMessageObject.audioProgress = progress;
                } else {
                    return;
                }
                ChatMessageCell.this.onSeekBarDrag(progress);
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
        int a = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
            if (a >= imageReceiverArr.length) {
                break;
            }
            imageReceiverArr[a] = new ImageReceiver(this);
            this.pollAvatarImages[a].setRoundRadius(AndroidUtilities.dp(8.0f));
            this.pollAvatarDrawables[a] = new AvatarDrawable();
            this.pollAvatarDrawables[a].setTextSize(AndroidUtilities.dp(6.0f));
            a++;
        }
        this.pollCheckBox = new CheckBoxBase[10];
        int a2 = 0;
        while (true) {
            CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
            if (a2 < checkBoxBaseArr.length) {
                checkBoxBaseArr[a2] = new CheckBoxBase(this, 20, this.resourcesProvider);
                this.pollCheckBox[a2].setDrawUnchecked(false);
                this.pollCheckBox[a2].setBackgroundType(9);
                a2++;
            } else {
                return;
            }
        }
    }

    private void createCommentUI() {
        if (this.commentAvatarImages != null) {
            return;
        }
        this.commentAvatarImages = new ImageReceiver[3];
        this.commentAvatarDrawables = new AvatarDrawable[3];
        this.commentAvatarImagesVisible = new boolean[3];
        int a = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.commentAvatarImages;
            if (a < imageReceiverArr.length) {
                imageReceiverArr[a] = new ImageReceiver(this);
                this.commentAvatarImages[a].setRoundRadius(AndroidUtilities.dp(12.0f));
                this.commentAvatarDrawables[a] = new AvatarDrawable();
                this.commentAvatarDrawables[a].setTextSize(AndroidUtilities.dp(8.0f));
                a++;
            } else {
                return;
            }
        }
    }

    public void resetPressedLink(int type) {
        if (type != -1) {
            this.links.removeLinks(Integer.valueOf(type));
        } else {
            this.links.clear();
        }
        if (this.pressedLink != null) {
            if (this.pressedLinkType != type && type != -1) {
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

    public int[] getRealSpanStartAndEnd(Spannable buffer, CharacterStyle link) {
        int start = 0;
        int end = 0;
        boolean ok = false;
        if (link instanceof URLSpanBrowser) {
            URLSpanBrowser span = (URLSpanBrowser) link;
            TextStyleSpan.TextStyleRun style = span.getStyle();
            if (style != null && style.urlEntity != null) {
                start = style.urlEntity.offset;
                end = style.urlEntity.offset + style.urlEntity.length;
                ok = true;
            }
        }
        if (!ok) {
            start = buffer.getSpanStart(link);
            end = buffer.getSpanEnd(link);
        }
        return new int[]{start, end};
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:73:0x015e
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r26) {
        /*
            Method dump skipped, instructions count: 886
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x00b9 A[Catch: Exception -> 0x0139, TryCatch #0 {Exception -> 0x0139, blocks: (B:25:0x0056, B:27:0x007a, B:29:0x0086, B:31:0x0095, B:35:0x009b, B:36:0x00a4, B:38:0x00a8, B:40:0x00ae, B:46:0x00b9, B:48:0x00bf, B:50:0x00c3, B:52:0x00cb, B:56:0x00f9, B:59:0x0126, B:60:0x0129, B:61:0x0134, B:57:0x0102), top: B:72:0x0056, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkCaptionMotionEvent(android.view.MotionEvent r21) {
        /*
            Method dump skipped, instructions count: 345
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkCaptionMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkGameMotionEvent(MotionEvent event) {
        boolean ignore;
        int i;
        int i2;
        if (!this.hasGamePreview) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && x >= (i = this.buttonX) && x <= i + AndroidUtilities.dp(48.0f) && y >= (i2 = this.buttonY) && y <= i2 + AndroidUtilities.dp(48.0f) && this.radialProgress.getIcon() != 4) {
                this.buttonPressed = 1;
                invalidate();
                return true;
            } else if (this.drawPhotoImage && this.photoImage.isInsideImage(x, y)) {
                this.gamePreviewPressed = true;
                return true;
            } else if (this.descriptionLayout != null && y >= this.descriptionY) {
                try {
                    int x2 = x - ((this.unmovedTextX + AndroidUtilities.dp(10.0f)) + this.descriptionX);
                    int y2 = y - this.descriptionY;
                    int line = this.descriptionLayout.getLineForVertical(y2);
                    int off = this.descriptionLayout.getOffsetForHorizontal(line, x2);
                    float left = this.descriptionLayout.getLineLeft(line);
                    if (left <= x2 && this.descriptionLayout.getLineWidth(line) + left >= x2) {
                        Spannable buffer = (Spannable) this.currentMessageObject.linkDescription;
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0 && (!(link[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                            ignore = false;
                            if (ignore && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                                LinkSpanDrawable linkSpanDrawable = this.pressedLink;
                                if (linkSpanDrawable == null || linkSpanDrawable.getSpan() != link[0]) {
                                    this.links.removeLink(this.pressedLink);
                                    LinkSpanDrawable linkSpanDrawable2 = new LinkSpanDrawable(link[0], this.resourcesProvider, x2, y2, spanSupportsLongPress(link[0]));
                                    this.pressedLink = linkSpanDrawable2;
                                    linkSpanDrawable2.setColor(getThemedColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLinkSelectBackground : Theme.key_chat_linkSelectBackground));
                                    this.linkBlockNum = -10;
                                    this.pressedLinkType = 2;
                                    try {
                                        LinkPath path = this.pressedLink.obtainNewPath();
                                        int[] pos = getRealSpanStartAndEnd(buffer, this.pressedLink.getSpan());
                                        path.setCurrentLayout(this.descriptionLayout, pos[0], 0.0f);
                                        this.descriptionLayout.getSelectionPath(pos[0], pos[1], path);
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                    }
                                    this.links.addLink(this.pressedLink, 2);
                                }
                                invalidate();
                                return true;
                            }
                        }
                        ignore = true;
                        return ignore ? false : false;
                    }
                    return false;
                } catch (Exception e2) {
                    FileLog.e(e2);
                    return false;
                }
            } else {
                return false;
            }
        } else if (event.getAction() == 1) {
            if (this.pressedLinkType == 2 || this.gamePreviewPressed || this.buttonPressed != 0) {
                if (this.buttonPressed != 0) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    didPressButton(true, false);
                    invalidate();
                    return false;
                }
                LinkSpanDrawable linkSpanDrawable3 = this.pressedLink;
                if (linkSpanDrawable3 == null) {
                    this.gamePreviewPressed = false;
                    int a = 0;
                    while (true) {
                        if (a >= this.botButtons.size()) {
                            break;
                        }
                        BotButton button = this.botButtons.get(a);
                        if (button.button instanceof TLRPC.TL_keyboardButtonGame) {
                            playSoundEffect(0);
                            this.delegate.didPressBotButton(this, button.button);
                            invalidate();
                            break;
                        }
                        a++;
                    }
                    resetPressedLink(2);
                    return true;
                }
                if (linkSpanDrawable3.getSpan() instanceof URLSpan) {
                    Browser.openUrl(getContext(), ((URLSpan) this.pressedLink.getSpan()).getURL());
                } else if (this.pressedLink.getSpan() instanceof ClickableSpan) {
                    ((ClickableSpan) this.pressedLink.getSpan()).onClick(this);
                }
                resetPressedLink(2);
                return false;
            }
            resetPressedLink(2);
            return false;
        } else {
            return false;
        }
    }

    private boolean checkTranscribeButtonMotionEvent(MotionEvent event) {
        TranscribeButton transcribeButton;
        return this.useTranscribeButton && (transcribeButton = this.transcribeButton) != null && transcribeButton.onTouch(event.getAction(), event.getX() - this.transcribeX, event.getY() - this.transcribeY);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(13:42|(3:47|61|62)|48|(1:50)(1:51)|52|247|53|54|249|55|60|61|62) */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0138, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x013a, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x013d, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean checkLinkPreviewMotionEvent(android.view.MotionEvent r23) {
        /*
            Method dump skipped, instructions count: 1087
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkLinkPreviewMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkPollButtonMotionEvent(MotionEvent event) {
        int i;
        int i2;
        if (this.currentMessageObject.eventId != 0 || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty() || this.currentMessageObject.type != 17 || !this.currentMessageObject.isSent()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
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
                return true;
            }
            for (int a = 0; a < this.pollButtons.size(); a++) {
                PollButton button = this.pollButtons.get(a);
                int y2 = (button.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                if (x >= button.x && x <= (button.x + this.backgroundWidth) - AndroidUtilities.dp(31.0f) && y >= y2 && y <= button.height + y2 + AndroidUtilities.dp(26.0f)) {
                    this.pressedVoteButton = a;
                    if (!this.pollVoted && !this.pollClosed) {
                        this.selectorDrawableMaskType[0] = 1;
                        if (Build.VERSION.SDK_INT >= 21) {
                            Drawable[] drawableArr2 = this.selectorDrawable;
                            if (drawableArr2[0] != null) {
                                drawableArr2[0].setBounds(button.x - AndroidUtilities.dp(9.0f), y2, (button.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), button.height + y2 + AndroidUtilities.dp(26.0f));
                                this.selectorDrawable[0].setHotspot(x, y);
                                this.selectorDrawable[0].setState(this.pressedState);
                            }
                        }
                        invalidate();
                    }
                    return true;
                }
            }
            return false;
        } else if (event.getAction() == 1) {
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
                    PollButton button2 = this.pollButtons.get(this.pressedVoteButton);
                    TLRPC.TL_pollAnswer answer = button2.answer;
                    if (this.pollVoted || this.pollClosed) {
                        ArrayList<TLRPC.TL_pollAnswer> answers = new ArrayList<>();
                        answers.add(answer);
                        this.delegate.didPressVoteButtons(this, answers, button2.count, button2.x + AndroidUtilities.dp(50.0f), button2.y + this.namesOffset);
                    } else if (this.lastPoll.multiple_choice) {
                        if (this.currentMessageObject.checkedVotes.contains(answer)) {
                            this.currentMessageObject.checkedVotes.remove(answer);
                            this.pollCheckBox[this.pressedVoteButton].setChecked(false, true);
                        } else {
                            this.currentMessageObject.checkedVotes.add(answer);
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
                        ArrayList<TLRPC.TL_pollAnswer> answers2 = new ArrayList<>();
                        answers2.add(answer);
                        this.delegate.didPressVoteButtons(this, answers2, -1, 0, 0);
                    }
                }
                this.pressedVoteButton = -1;
                invalidate();
                return false;
            }
        } else if (event.getAction() != 2) {
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

    private boolean checkInstantButtonMotionEvent(MotionEvent event) {
        if (this.currentMessageObject.isSponsored() || (this.drawInstantView && this.currentMessageObject.type != 0)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int i = 2;
            if (event.getAction() == 0) {
                if (this.drawInstantView && this.instantButtonRect.contains(x, y)) {
                    int[] iArr = this.selectorDrawableMaskType;
                    if (this.lastPoll == null) {
                        i = 0;
                    }
                    iArr[0] = i;
                    this.instantPressed = true;
                    if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null && this.instantButtonRect.contains(x, y)) {
                        this.selectorDrawable[0].setHotspot(x, y);
                        this.selectorDrawable[0].setState(this.pressedState);
                        this.instantButtonPressed = true;
                    }
                    invalidate();
                    return true;
                }
            } else if (event.getAction() == 1) {
                if (this.instantPressed) {
                    ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                    if (chatMessageCellDelegate != null) {
                        if (this.lastPoll != null) {
                            if (this.currentMessageObject.scheduled) {
                                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledVoteResults", R.string.MessageScheduledVoteResults), 1).show();
                            } else if (this.pollVoted || this.pollClosed) {
                                this.delegate.didPressInstantButton(this, this.drawInstantViewType);
                            } else {
                                if (!this.currentMessageObject.checkedVotes.isEmpty()) {
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
            } else if (event.getAction() == 2 && this.instantButtonPressed && Build.VERSION.SDK_INT >= 21) {
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

    private boolean checkCommentButtonMotionEvent(MotionEvent event) {
        if (!this.drawCommentButton) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int i = 1;
        if (groupedMessagePosition != null && (groupedMessagePosition.flags & 1) == 0 && this.commentButtonRect.contains(x, y)) {
            ViewGroup parent = (ViewGroup) getParent();
            int a = 0;
            int N = parent.getChildCount();
            while (a < N) {
                View view = parent.getChildAt(a);
                if (view != this && (view instanceof ChatMessageCell)) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    if (cell.drawCommentButton && cell.currentMessagesGroup == this.currentMessagesGroup && (cell.currentPosition.flags & i) != 0) {
                        MotionEvent childEvent = MotionEvent.obtain(0L, 0L, event.getActionMasked(), (event.getX() + getLeft()) - cell.getLeft(), (event.getY() + getTop()) - cell.getTop(), 0);
                        cell.checkCommentButtonMotionEvent(childEvent);
                        childEvent.recycle();
                        return true;
                    }
                }
                a++;
                i = 1;
            }
            return true;
        }
        if (event.getAction() == 0) {
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
                    return true;
                }
                return true;
            }
        } else if (event.getAction() == 1) {
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
        } else if (event.getAction() == 2 && this.commentButtonPressed && Build.VERSION.SDK_INT >= 21) {
            Drawable[] drawableArr3 = this.selectorDrawable;
            if (drawableArr3[1] != null) {
                drawableArr3[1].setHotspot(x, y);
            }
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r5v2, types: [boolean] */
    private boolean checkOtherButtonMotionEvent(MotionEvent event) {
        int i;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        int i2 = this.documentAttachType;
        if ((i2 == 5 || i2 == 1) && (groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.flags & 4) == 0) {
            return false;
        }
        boolean allow = this.currentMessageObject.type == 16;
        if (!allow) {
            allow = (this.documentAttachType == 1 || this.currentMessageObject.type == 12 || (i = this.documentAttachType) == 5 || i == 4 || i == 2 || this.currentMessageObject.type == 8) && !this.hasGamePreview && !this.hasInvoicePreview;
        }
        if (!allow) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (this.currentMessageObject.type == 16) {
                ?? isVideoCall = this.currentMessageObject.isVideoCall();
                int i3 = this.otherX;
                if (x < i3) {
                    return false;
                }
                if (x > i3 + AndroidUtilities.dp((isVideoCall == 0 ? SearchViewPager.deleteItemId : 200) + 30) || y < this.otherY - AndroidUtilities.dp(14.0f) || y > this.otherY + AndroidUtilities.dp(50.0f)) {
                    return false;
                }
                this.otherPressed = true;
                this.selectorDrawableMaskType[0] = 4;
                if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null) {
                    int dp = this.otherX + AndroidUtilities.dp(isVideoCall == 0 ? 202.0f : 200.0f);
                    Drawable[] drawableArr = Theme.chat_msgInCallDrawable;
                    int idx = isVideoCall == true ? 1 : 0;
                    int cx = dp + (drawableArr[idx].getIntrinsicWidth() / 2);
                    int cy = this.otherY + (Theme.chat_msgInCallDrawable[isVideoCall].getIntrinsicHeight() / 2);
                    this.selectorDrawable[0].setBounds(cx - AndroidUtilities.dp(20.0f), cy - AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f) + cx, AndroidUtilities.dp(20.0f) + cy);
                    this.selectorDrawable[0].setHotspot(x, y);
                    this.selectorDrawable[0].setState(this.pressedState);
                }
                invalidate();
                return true;
            } else if (x < this.otherX - AndroidUtilities.dp(20.0f) || x > this.otherX + AndroidUtilities.dp(20.0f) || y < this.otherY - AndroidUtilities.dp(4.0f) || y > this.otherY + AndroidUtilities.dp(30.0f)) {
                return false;
            } else {
                this.otherPressed = true;
                invalidate();
                return true;
            }
        } else if (event.getAction() != 1) {
            if (event.getAction() != 2 || this.currentMessageObject.type != 16 || !this.otherPressed || Build.VERSION.SDK_INT < 21) {
                return false;
            }
            Drawable[] drawableArr2 = this.selectorDrawable;
            if (drawableArr2[0] == null) {
                return false;
            }
            drawableArr2[0].setHotspot(x, y);
            return false;
        } else if (!this.otherPressed) {
            return false;
        } else {
            if (this.currentMessageObject.type == 16 && Build.VERSION.SDK_INT >= 21) {
                Drawable[] drawableArr3 = this.selectorDrawable;
                if (drawableArr3[0] != null) {
                    drawableArr3[0].setState(StateSet.NOTHING);
                }
            }
            this.otherPressed = false;
            playSoundEffect(0);
            this.delegate.didPressOther(this, this.otherX, this.otherY);
            invalidate();
            return true;
        }
    }

    private boolean checkDateMotionEvent(MotionEvent event) {
        if (!this.currentMessageObject.isImportedForward()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            float f = this.drawTimeX;
            if (x < f || x > f + this.timeWidth) {
                return false;
            }
            float f2 = this.drawTimeY;
            if (y < f2 || y > f2 + AndroidUtilities.dp(20.0f)) {
                return false;
            }
            this.timePressed = true;
            invalidate();
            return true;
        } else if (event.getAction() != 1 || !this.timePressed) {
            return false;
        } else {
            this.timePressed = false;
            playSoundEffect(0);
            this.delegate.didPressTime(this);
            invalidate();
            return true;
        }
    }

    private boolean checkRoundSeekbar(MotionEvent event) {
        float f;
        if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || !MediaController.getInstance().isMessagePaused()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (x < this.seekbarRoundX - AndroidUtilities.dp(20.0f) || x > this.seekbarRoundX + AndroidUtilities.dp(20.0f) || y < this.seekbarRoundY - AndroidUtilities.dp(20.0f) || y > this.seekbarRoundY + AndroidUtilities.dp(20.0f)) {
                float localX = x - this.photoImage.getCenterX();
                float localY = y - this.photoImage.getCenterY();
                float r2 = (this.photoImage.getImageWidth() - AndroidUtilities.dp(64.0f)) / 2.0f;
                if ((localX * localX) + (localY * localY) < ((this.photoImage.getImageWidth() / 2.0f) * this.photoImage.getImageWidth()) / 2.0f && (localX * localX) + (localY * localY) > r2 * r2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    cancelCheckLongPress();
                    this.roundSeekbarTouched = 1;
                    invalidate();
                }
            } else {
                getParent().requestDisallowInterceptTouchEvent(true);
                cancelCheckLongPress();
                this.roundSeekbarTouched = 1;
                invalidate();
            }
        } else if (this.roundSeekbarTouched == 1 && event.getAction() == 2) {
            float localX2 = x - this.photoImage.getCenterX();
            float localY2 = y - this.photoImage.getCenterY();
            float a = ((float) Math.toDegrees(Math.atan2(localY2, localX2))) + 90.0f;
            if (a < 0.0f) {
                a += 360.0f;
            }
            float p = a / 360.0f;
            if (Math.abs(this.currentMessageObject.audioProgress - p) > 0.9f) {
                if (this.roundSeekbarOutAlpha == 0.0f) {
                    performHapticFeedback(3);
                }
                this.roundSeekbarOutAlpha = 1.0f;
                this.roundSeekbarOutProgress = this.currentMessageObject.audioProgress;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastSeekUpdateTime > 100) {
                MediaController.getInstance().seekToProgress(this.currentMessageObject, p);
                this.lastSeekUpdateTime = currentTime;
            }
            this.currentMessageObject.audioProgress = p;
            updatePlayingMessageProgress();
        }
        if ((event.getAction() == 1 || event.getAction() == 3) && this.roundSeekbarTouched != 0) {
            if (event.getAction() == 1) {
                float localX3 = x - this.photoImage.getCenterX();
                float localY3 = y - this.photoImage.getCenterY();
                float a2 = ((float) Math.toDegrees(Math.atan2(localY3, localX3))) + 90.0f;
                if (a2 >= 0.0f) {
                    f = 360.0f;
                } else {
                    f = 360.0f;
                    a2 += 360.0f;
                }
                float p2 = a2 / f;
                this.currentMessageObject.audioProgress = p2;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, p2);
                updatePlayingMessageProgress();
            }
            MediaController.getInstance().playMessage(this.currentMessageObject);
            this.roundSeekbarTouched = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return this.roundSeekbarTouched != 0;
    }

    private boolean checkPhotoImageMotionEvent(MotionEvent event) {
        int i;
        int i2;
        int i3;
        int i4;
        boolean z;
        if ((this.drawPhotoImage || this.documentAttachType == 1) && (!this.currentMessageObject.isSending() || this.buttonState == 1)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean result = false;
            if (event.getAction() == 0) {
                boolean area2 = false;
                int side = AndroidUtilities.dp(48.0f);
                if (this.miniButtonState >= 0) {
                    int offset = AndroidUtilities.dp(27.0f);
                    int i5 = this.buttonX;
                    if (x >= i5 + offset && x <= i5 + offset + side) {
                        int i6 = this.buttonY;
                        if (y >= i6 + offset && y <= i6 + offset + side) {
                            z = true;
                            area2 = z;
                        }
                    }
                    z = false;
                    area2 = z;
                }
                if (area2) {
                    this.miniButtonPressed = 1;
                    invalidate();
                    result = true;
                } else if (this.buttonState != -1 && this.radialProgress.getIcon() != 4 && x >= (i3 = this.buttonX) && x <= i3 + side && y >= (i4 = this.buttonY) && y <= i4 + side) {
                    this.buttonPressed = 1;
                    invalidate();
                    result = true;
                } else if (this.drawVideoImageButton && this.buttonState != -1 && x >= (i = this.videoButtonX) && x <= i + AndroidUtilities.dp(34.0f) + Math.max(this.infoWidth, this.docTitleWidth) && y >= (i2 = this.videoButtonY) && y <= i2 + AndroidUtilities.dp(30.0f)) {
                    this.videoButtonPressed = 1;
                    invalidate();
                    result = true;
                } else if (this.documentAttachType == 1) {
                    if (x >= this.photoImage.getImageX() && x <= (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(50.0f) && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                        this.imagePressed = true;
                        result = true;
                    }
                } else if (!this.currentMessageObject.isAnyKindOfSticker() || this.currentMessageObject.getInputStickerSet() != null || this.currentMessageObject.isAnimatedEmoji() || this.currentMessageObject.isDice()) {
                    if (x >= this.photoImage.getImageX() && x <= this.photoImage.getImageX() + this.photoImage.getImageWidth() && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                        if (this.isRoundVideo) {
                            if (((x - this.photoImage.getCenterX()) * (x - this.photoImage.getCenterX())) + ((y - this.photoImage.getCenterY()) * (y - this.photoImage.getCenterY())) < (this.photoImage.getImageWidth() / 2.0f) * (this.photoImage.getImageWidth() / 2.0f)) {
                                this.imagePressed = true;
                                result = true;
                            }
                        } else {
                            this.imagePressed = true;
                            result = true;
                        }
                    }
                    if (this.currentMessageObject.type == 12) {
                        long uid = this.currentMessageObject.messageOwner.media.user_id;
                        TLRPC.User user = null;
                        if (uid != 0) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid));
                        }
                        if (user == null) {
                            this.imagePressed = false;
                            result = false;
                        }
                    }
                }
                if (this.imagePressed) {
                    if (this.currentMessageObject.isSendError()) {
                        this.imagePressed = false;
                        return false;
                    } else if (this.currentMessageObject.type == 8 && this.buttonState == -1 && SharedConfig.autoplayGifs && this.photoImage.getAnimation() == null) {
                        this.imagePressed = false;
                        return false;
                    } else {
                        return result;
                    }
                }
                return result;
            } else if (event.getAction() != 1) {
                return false;
            } else {
                if (this.videoButtonPressed == 1) {
                    this.videoButtonPressed = 0;
                    playSoundEffect(0);
                    didPressButton(true, true);
                    invalidate();
                    return false;
                } else if (this.buttonPressed == 1) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    if (this.drawVideoImageButton) {
                        didClickedImage();
                    } else {
                        didPressButton(true, false);
                    }
                    invalidate();
                    return false;
                } else if (this.miniButtonPressed == 1) {
                    this.miniButtonPressed = 0;
                    playSoundEffect(0);
                    didPressMiniButton(true);
                    invalidate();
                    return false;
                } else if (!this.imagePressed) {
                    return false;
                } else {
                    this.imagePressed = false;
                    int i7 = this.buttonState;
                    if (i7 == -1 || i7 == 2 || i7 == 3 || this.drawVideoImageButton) {
                        playSoundEffect(0);
                        didClickedImage();
                    } else if (i7 == 0) {
                        playSoundEffect(0);
                        didPressButton(true, false);
                    }
                    invalidate();
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkAudioMotionEvent(MotionEvent event) {
        boolean result;
        boolean z;
        int i;
        boolean z2;
        int i2 = this.documentAttachType;
        if ((i2 == 3 || i2 == 5) && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (this.useSeekBarWaveform) {
                result = this.seekBarWaveform.onTouch(event.getAction(), (event.getX() - this.seekBarX) - AndroidUtilities.dp(13.0f), event.getY() - this.seekBarY);
            } else if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                result = this.seekBar.onTouch(event.getAction(), event.getX() - this.seekBarX, event.getY() - this.seekBarY);
            } else {
                result = false;
            }
            if (result) {
                if (!this.useSeekBarWaveform && event.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else if (this.useSeekBarWaveform && !this.seekBarWaveform.isStartDraging() && event.getAction() == 1) {
                    didPressButton(true, false);
                }
                this.disallowLongPress = true;
                invalidate();
                return result;
            }
            int side = AndroidUtilities.dp(36.0f);
            boolean area = false;
            boolean area2 = false;
            if (this.miniButtonState >= 0) {
                int offset = AndroidUtilities.dp(27.0f);
                int i3 = this.buttonX;
                if (x >= i3 + offset && x <= i3 + offset + side) {
                    int i4 = this.buttonY;
                    if (y >= i4 + offset && y <= i4 + offset + side) {
                        z2 = true;
                        area2 = z2;
                    }
                }
                z2 = false;
                area2 = z2;
            }
            if (!area2) {
                int i5 = this.buttonState;
                if (i5 == 0 || i5 == 1 || i5 == 2) {
                    if (x >= this.buttonX - AndroidUtilities.dp(12.0f) && x <= (this.buttonX - AndroidUtilities.dp(12.0f)) + this.backgroundWidth) {
                        boolean z3 = this.drawInstantView;
                        if (y >= (z3 ? this.buttonY : this.namesOffset + this.mediaOffsetY)) {
                            if (y <= (z3 ? this.buttonY + side : this.namesOffset + this.mediaOffsetY + AndroidUtilities.dp(82.0f))) {
                                z = true;
                                area = z;
                            }
                        }
                    }
                    z = false;
                    area = z;
                } else {
                    int i6 = this.buttonX;
                    area = x >= i6 && x <= i6 + side && y >= (i = this.buttonY) && y <= i + side;
                }
            }
            if (event.getAction() == 0) {
                if (area || area2) {
                    if (area) {
                        this.buttonPressed = 1;
                    } else {
                        this.miniButtonPressed = 1;
                    }
                    invalidate();
                    return true;
                }
                return result;
            } else if (this.buttonPressed != 0) {
                if (event.getAction() == 1) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    didPressButton(true, false);
                    invalidate();
                    return result;
                } else if (event.getAction() != 3) {
                    if (event.getAction() == 2 && !area) {
                        this.buttonPressed = 0;
                        invalidate();
                        return result;
                    }
                    return result;
                } else {
                    this.buttonPressed = 0;
                    invalidate();
                    return result;
                }
            } else if (this.miniButtonPressed != 0) {
                if (event.getAction() == 1) {
                    this.miniButtonPressed = 0;
                    playSoundEffect(0);
                    didPressMiniButton(true);
                    invalidate();
                    return result;
                } else if (event.getAction() != 3) {
                    if (event.getAction() == 2 && !area2) {
                        this.miniButtonPressed = 0;
                        invalidate();
                        return result;
                    }
                    return result;
                } else {
                    this.miniButtonPressed = 0;
                    invalidate();
                    return result;
                }
            } else {
                return result;
            }
        }
        return false;
    }

    public boolean checkSpoilersMotionEvent(MotionEvent event) {
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup parent = (ViewGroup) getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View v = parent.getChildAt(i);
                if (v instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) v;
                    MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                    if (group != null && group.groupId == this.currentMessagesGroup.groupId && (position.flags & 8) != 0 && (position.flags & 1) != 0 && cell != this) {
                        event.offsetLocation(getLeft() - cell.getLeft(), getTop() - cell.getTop());
                        boolean result = cell.checkSpoilersMotionEvent(event);
                        event.offsetLocation(-(getLeft() - cell.getLeft()), -(getTop() - cell.getTop()));
                        return result;
                    }
                }
            }
        }
        if (this.isSpoilerRevealing) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        int act = event.getActionMasked();
        if (act == 0) {
            int i2 = this.textX;
            if (x >= i2 && y >= this.textY && x <= i2 + this.currentMessageObject.textWidth && y <= this.textY + this.currentMessageObject.textHeight) {
                List<MessageObject.TextLayoutBlock> blocks = this.currentMessageObject.textLayoutBlocks;
                for (int i3 = 0; i3 < blocks.size() && blocks.get(i3).textYOffset <= y; i3++) {
                    MessageObject.TextLayoutBlock block = blocks.get(i3);
                    int offX = block.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                    for (SpoilerEffect eff : block.spoilers) {
                        if (eff.getBounds().contains((x - this.textX) + offX, (int) ((y - this.textY) - block.textYOffset))) {
                            this.spoilerPressed = eff;
                            this.isCaptionSpoilerPressed = false;
                            return true;
                        }
                    }
                }
            }
            if (hasCaptionLayout()) {
                float f = this.captionX;
                if (x >= f && y >= this.captionY && x <= f + this.captionLayout.getWidth() && y <= this.captionY + this.captionLayout.getHeight()) {
                    for (SpoilerEffect eff2 : this.captionSpoilers) {
                        if (eff2.getBounds().contains((int) (x - this.captionX), (int) (y - this.captionY))) {
                            this.spoilerPressed = eff2;
                            this.isCaptionSpoilerPressed = true;
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        } else if (act == 1 && this.spoilerPressed != null) {
            playSoundEffect(0);
            this.sPath.rewind();
            if (this.isCaptionSpoilerPressed) {
                for (SpoilerEffect eff3 : this.captionSpoilers) {
                    Rect b = eff3.getBounds();
                    this.sPath.addRect(b.left, b.top, b.right, b.bottom, Path.Direction.CW);
                }
            } else {
                Iterator<MessageObject.TextLayoutBlock> it = this.currentMessageObject.textLayoutBlocks.iterator();
                while (it.hasNext()) {
                    MessageObject.TextLayoutBlock block2 = it.next();
                    for (SpoilerEffect eff4 : block2.spoilers) {
                        Rect b2 = eff4.getBounds();
                        this.sPath.addRect(b2.left, b2.top + block2.textYOffset, b2.right, b2.bottom + block2.textYOffset, Path.Direction.CW);
                    }
                }
            }
            this.sPath.computeBounds(this.rect, false);
            float width = this.rect.width();
            float height = this.rect.height();
            float rad = (float) Math.sqrt(Math.pow(width, 2.0d) + Math.pow(height, 2.0d));
            this.isSpoilerRevealing = true;
            this.spoilerPressed.setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatMessageCell.this.m1637x3d58ab10();
                }
            });
            if (this.isCaptionSpoilerPressed) {
                for (SpoilerEffect eff5 : this.captionSpoilers) {
                    eff5.startRipple(x - this.captionX, y - this.captionY, rad);
                }
            } else if (this.currentMessageObject.textLayoutBlocks != null) {
                Iterator<MessageObject.TextLayoutBlock> it2 = this.currentMessageObject.textLayoutBlocks.iterator();
                while (it2.hasNext()) {
                    MessageObject.TextLayoutBlock block3 = it2.next();
                    int offX2 = block3.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                    for (SpoilerEffect eff6 : block3.spoilers) {
                        eff6.startRipple((x - this.textX) + offX2, (y - block3.textYOffset) - this.textY, rad);
                    }
                }
            }
            if (getParent() instanceof RecyclerListView) {
                ViewGroup vg = (ViewGroup) getParent();
                for (int i4 = 0; i4 < vg.getChildCount(); i4++) {
                    View ch = vg.getChildAt(i4);
                    if (ch instanceof ChatMessageCell) {
                        final ChatMessageCell cell2 = (ChatMessageCell) ch;
                        if (cell2.getMessageObject() != null && cell2.getMessageObject().getReplyMsgId() == getMessageObject().getId() && !cell2.replySpoilers.isEmpty()) {
                            cell2.replySpoilers.get(0).setOnRippleEndCallback(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ChatMessageCell.this.m1638xcf5a9992(cell2);
                                }
                            });
                            for (SpoilerEffect eff7 : cell2.replySpoilers) {
                                eff7.startRipple(eff7.getBounds().centerX(), eff7.getBounds().centerY(), rad);
                            }
                        }
                    }
                }
            }
            this.spoilerPressed = null;
            return true;
        } else {
            return false;
        }
    }

    /* renamed from: lambda$checkSpoilersMotionEvent$1$org-telegram-ui-Cells-ChatMessageCell */
    public /* synthetic */ void m1637x3d58ab10() {
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChatMessageCell.this.m1636x7457b3cf();
            }
        });
    }

    /* renamed from: lambda$checkSpoilersMotionEvent$0$org-telegram-ui-Cells-ChatMessageCell */
    public /* synthetic */ void m1636x7457b3cf() {
        this.isSpoilerRevealing = false;
        getMessageObject().isSpoilersRevealed = true;
        if (this.isCaptionSpoilerPressed) {
            this.captionSpoilers.clear();
        } else if (this.currentMessageObject.textLayoutBlocks != null) {
            Iterator<MessageObject.TextLayoutBlock> it = this.currentMessageObject.textLayoutBlocks.iterator();
            while (it.hasNext()) {
                MessageObject.TextLayoutBlock block = it.next();
                block.spoilers.clear();
            }
        }
        invalidate();
    }

    /* renamed from: lambda$checkSpoilersMotionEvent$3$org-telegram-ui-Cells-ChatMessageCell */
    public /* synthetic */ void m1638xcf5a9992(ChatMessageCell cell) {
        post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChatMessageCell.lambda$checkSpoilersMotionEvent$2(ChatMessageCell.this);
            }
        });
    }

    public static /* synthetic */ void lambda$checkSpoilersMotionEvent$2(ChatMessageCell cell) {
        cell.getMessageObject().replyMessageObject.isSpoilersRevealed = true;
        cell.replySpoilers.clear();
        cell.invalidate();
    }

    private boolean checkBotButtonMotionEvent(MotionEvent event) {
        int addX;
        if (this.botButtons.isEmpty() || this.currentMessageObject.eventId != 0) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (this.currentMessageObject.isOutOwner()) {
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                addX = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            for (int a = 0; a < this.botButtons.size(); a++) {
                BotButton button = this.botButtons.get(a);
                int y2 = (button.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x >= button.x + addX && x <= button.x + addX + button.width && y >= y2 && y <= button.height + y2) {
                    this.pressedBotButton = a;
                    invalidate();
                    final int longPressedBotButton = this.pressedBotButton;
                    postDelayed(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatMessageCell.this.m1635xa5a14b61(longPressedBotButton);
                        }
                    }, ViewConfiguration.getLongPressTimeout() - 1);
                    return true;
                }
            }
            return false;
        } else if (event.getAction() != 1 || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(0);
            if (this.currentMessageObject.scheduled) {
                Toast.makeText(getContext(), LocaleController.getString("MessageScheduledBotAction", R.string.MessageScheduledBotAction), 1).show();
            } else {
                BotButton button2 = this.botButtons.get(this.pressedBotButton);
                if (button2.button != null) {
                    this.delegate.didPressBotButton(this, button2.button);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    /* renamed from: lambda$checkBotButtonMotionEvent$4$org-telegram-ui-Cells-ChatMessageCell */
    public /* synthetic */ void m1635xa5a14b61(int longPressedBotButton) {
        if (longPressedBotButton == this.pressedBotButton) {
            if (!this.currentMessageObject.scheduled) {
                BotButton button2 = this.botButtons.get(this.pressedBotButton);
                if (button2.button != null) {
                    cancelCheckLongPress();
                    this.delegate.didLongPressBotButton(this, button2.button);
                }
            }
            this.pressedBotButton = -1;
            invalidate();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:271:0x0449, code lost:
        if (r10 <= (r0 + org.telegram.messenger.AndroidUtilities.dp(32.0f))) goto L389;
     */
    /* JADX WARN: Code restructure failed: missing block: B:307:0x04c3, code lost:
        if (r10 <= (r0 + org.telegram.messenger.AndroidUtilities.dp(32.0f))) goto L389;
     */
    /* JADX WARN: Code restructure failed: missing block: B:356:0x059a, code lost:
        if (r10 <= (r1 + org.telegram.messenger.AndroidUtilities.dp(35.0f))) goto L389;
     */
    /* JADX WARN: Code restructure failed: missing block: B:386:0x05f9, code lost:
        if (r10 > (r0 + org.telegram.messenger.AndroidUtilities.dp(32 + ((r19.drawSideButton != 3 || r19.commentLayout == null) ? 0 : 18)))) goto L387;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            Method dump skipped, instructions count: 1545
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkReactionsTouchEvent(MotionEvent event) {
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup parent = (ViewGroup) getParent();
            if (parent == null) {
                return false;
            }
            for (int i = 0; i < parent.getChildCount(); i++) {
                View v = parent.getChildAt(i);
                if (v instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) v;
                    MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                    if (group != null && group.groupId == this.currentMessagesGroup.groupId && (position.flags & 8) != 0 && (position.flags & 1) != 0) {
                        if (cell == this) {
                            return this.reactionsLayoutInBubble.chekTouchEvent(event);
                        }
                        event.offsetLocation(getLeft() - cell.getLeft(), getTop() - cell.getTop());
                        boolean result = cell.reactionsLayoutInBubble.chekTouchEvent(event);
                        event.offsetLocation(-(getLeft() - cell.getLeft()), -(getTop() - cell.getTop()));
                        return result;
                    }
                }
            }
            return false;
        }
        return this.reactionsLayoutInBubble.chekTouchEvent(event);
    }

    private boolean checkPinchToZoom(MotionEvent ev) {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        PinchToZoomHelper pinchToZoomHelper = chatMessageCellDelegate == null ? null : chatMessageCellDelegate.getPinchToZoomHelper();
        if (this.currentMessageObject != null && this.photoImage.hasNotThumb() && pinchToZoomHelper != null && !this.currentMessageObject.isSticker() && !this.currentMessageObject.isAnimatedEmoji()) {
            if ((this.currentMessageObject.isVideo() && !this.autoPlayingMedia) || this.isRoundVideo || this.currentMessageObject.isAnimatedSticker()) {
                return false;
            }
            if ((this.currentMessageObject.isDocument() && !this.currentMessageObject.isGif()) || this.currentMessageObject.needDrawBluredPreview()) {
                return false;
            }
            return pinchToZoomHelper.checkPinchToZoom(ev, this, this.photoImage, this.currentMessageObject);
        }
        return false;
    }

    private boolean checkTextSelection(MotionEvent event) {
        int linkX;
        MessageObject.GroupedMessages groupedMessages;
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper = this.delegate.getTextSelectionHelper();
        if (textSelectionHelper == null || MessagesController.getInstance(this.currentAccount).isChatNoForwards(this.currentMessageObject.getChatId()) || (this.currentMessageObject.messageOwner != null && this.currentMessageObject.messageOwner.noforwards)) {
            return false;
        }
        boolean hasTextBlocks = this.currentMessageObject.textLayoutBlocks != null && !this.currentMessageObject.textLayoutBlocks.isEmpty();
        if (!hasTextBlocks && !hasCaptionLayout()) {
            return false;
        }
        if ((!this.drawSelectionBackground && this.currentMessagesGroup == null) || (this.currentMessagesGroup != null && !this.delegate.hasSelectedMessages())) {
            return false;
        }
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup parent = (ViewGroup) getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View v = parent.getChildAt(i);
                if (v instanceof ChatMessageCell) {
                    ChatMessageCell cell = (ChatMessageCell) v;
                    MessageObject.GroupedMessages group = cell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition position = cell.getCurrentPosition();
                    if (group != null && group.groupId == this.currentMessagesGroup.groupId && (position.flags & 8) != 0 && (position.flags & 1) != 0) {
                        textSelectionHelper.setMaybeTextCord((int) cell.captionX, (int) cell.captionY);
                        textSelectionHelper.setMessageObject(cell);
                        if (cell == this) {
                            return textSelectionHelper.onTouchEvent(event);
                        }
                        event.offsetLocation(getLeft() - cell.getLeft(), getTop() - cell.getTop());
                        boolean result = textSelectionHelper.onTouchEvent(event);
                        event.offsetLocation(-(getLeft() - cell.getLeft()), -(getTop() - cell.getTop()));
                        return result;
                    }
                }
            }
            return false;
        }
        if (hasCaptionLayout()) {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setMaybeTextCord((int) this.captionX, (int) this.captionY);
        } else if (this.descriptionLayout != null && event.getY() > this.descriptionY) {
            textSelectionHelper.setIsDescription(true);
            if (this.hasGamePreview) {
                linkX = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else if (this.hasInvoicePreview) {
                linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
            } else {
                int linkX2 = this.unmovedTextX;
                linkX = linkX2 + AndroidUtilities.dp(1.0f);
            }
            textSelectionHelper.setMaybeTextCord(AndroidUtilities.dp(10.0f) + linkX + this.descriptionX, this.descriptionY);
        } else {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setMaybeTextCord(this.textX, this.textY);
        }
        textSelectionHelper.setMessageObject(this);
        return textSelectionHelper.onTouchEvent(event);
    }

    private void updateSelectionTextPosition() {
        int linkX;
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
            int textSelectionType = getDelegate().getTextSelectionHelper().getTextSelectionType(this);
            if (textSelectionType != TextSelectionHelper.ChatListTextSelectionHelper.TYPE_DESCRIPTION) {
                if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_CAPTION) {
                    getDelegate().getTextSelectionHelper().updateTextPosition((int) this.captionX, (int) this.captionY);
                    return;
                } else {
                    getDelegate().getTextSelectionHelper().updateTextPosition(this.textX, this.textY);
                    return;
                }
            }
            if (this.hasGamePreview) {
                linkX = this.unmovedTextX - AndroidUtilities.dp(10.0f);
            } else if (this.hasInvoicePreview) {
                linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
            } else {
                int linkX2 = this.unmovedTextX;
                linkX = linkX2 + AndroidUtilities.dp(1.0f);
            }
            getDelegate().getTextSelectionHelper().updateTextPosition(AndroidUtilities.dp(10.0f) + linkX + this.descriptionX, this.descriptionY);
        }
    }

    public ArrayList<PollButton> getPollButtons() {
        return this.pollButtons;
    }

    public void updatePlayingMessageProgress() {
        if (this.currentMessageObject == null) {
            return;
        }
        VideoPlayerRewinder videoPlayerRewinder = this.videoPlayerRewinder;
        if (videoPlayerRewinder != null && videoPlayerRewinder.rewindCount != 0 && this.videoPlayerRewinder.rewindByBackSeek) {
            this.currentMessageObject.audioProgress = this.videoPlayerRewinder.getVideoProgress();
        }
        if (this.documentAttachType == 4) {
            if (this.infoLayout != null && (PhotoViewer.isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject))) {
                return;
            }
            int duration = 0;
            AnimatedFileDrawable animation = this.photoImage.getAnimation();
            if (animation != null) {
                MessageObject messageObject = this.currentMessageObject;
                int durationMs = animation.getDurationMs() / 1000;
                messageObject.audioPlayerDuration = durationMs;
                duration = durationMs;
                if (this.currentMessageObject.messageOwner.ttl > 0 && this.currentMessageObject.messageOwner.destroyTime == 0 && !this.currentMessageObject.needDrawBluredPreview() && this.currentMessageObject.isVideo() && animation.hasBitmap()) {
                    this.delegate.didStartVideoStream(this.currentMessageObject);
                }
            }
            if (duration == 0) {
                duration = this.currentMessageObject.getDuration();
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                duration = (int) (duration - (duration * this.currentMessageObject.audioProgress));
            } else if (animation != null) {
                if (duration != 0) {
                    duration -= animation.getCurrentProgressMs() / 1000;
                }
                if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                    this.delegate.videoTimerReached();
                }
            }
            if (this.lastTime != duration) {
                String str = AndroidUtilities.formatShortDuration(duration);
                this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
                this.infoLayout = new StaticLayout(str, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.lastTime = duration;
            }
        } else if (this.isRoundVideo) {
            int duration2 = 0;
            TLRPC.Document document = this.currentMessageObject.getDocument();
            int a = 0;
            while (true) {
                if (a >= document.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                    a++;
                } else {
                    duration2 = attribute.duration;
                    break;
                }
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                duration2 = Math.max(0, duration2 - this.currentMessageObject.audioProgressSec);
            }
            if (this.lastTime != duration2) {
                this.lastTime = duration2;
                String timeString = AndroidUtilities.formatLongDuration(duration2);
                this.timeWidthAudio = (int) Math.ceil(Theme.chat_timePaint.measureText(timeString));
                this.durationLayout = new StaticLayout(timeString, Theme.chat_timePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            if (this.currentMessageObject.audioProgress != 0.0f) {
                float f = this.currentMessageObject.audioProgress;
                this.lastDrawingAudioProgress = f;
                if (f > 0.9f) {
                    this.lastDrawingAudioProgress = 1.0f;
                }
            }
            invalidate();
        } else if (this.documentAttach != null) {
            if (this.useSeekBarWaveform) {
                if (!this.seekBarWaveform.isDragging()) {
                    this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress, true);
                }
            } else if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
            }
            int duration3 = 0;
            if (this.documentAttachType == 3) {
                if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.documentAttach.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute2 = this.documentAttach.attributes.get(a2);
                        if (!(attribute2 instanceof TLRPC.TL_documentAttributeAudio)) {
                            a2++;
                        } else {
                            duration3 = attribute2.duration;
                            break;
                        }
                    }
                } else {
                    duration3 = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != duration3) {
                    this.lastTime = duration3;
                    String timeString2 = AndroidUtilities.formatLongDuration(duration3);
                    this.timeWidthAudio = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(timeString2));
                    this.durationLayout = new StaticLayout(timeString2, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            } else {
                int currentProgress = 0;
                int duration4 = this.currentMessageObject.getDuration();
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    currentProgress = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != currentProgress) {
                    this.lastTime = currentProgress;
                    String timeString3 = AndroidUtilities.formatShortDuration(currentProgress, duration4);
                    int timeWidth = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(timeString3));
                    this.durationLayout = new StaticLayout(timeString3, Theme.chat_audioTimePaint, timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            }
            invalidate();
        }
    }

    public void setFullyDraw(boolean draw) {
        this.fullyDraw = draw;
    }

    public void setParentViewSize(int parentW, int parentH) {
        Theme.MessageDrawable messageDrawable;
        this.parentWidth = parentW;
        this.parentHeight = parentH;
        this.backgroundHeight = parentH;
        if ((this.currentMessageObject != null && hasGradientService() && this.currentMessageObject.shouldDrawWithoutBackground()) || ((messageDrawable = this.currentBackgroundDrawable) != null && messageDrawable.getGradientShader() != null)) {
            invalidate();
        }
    }

    public void setVisiblePart(int position, int height, int parent, float parentOffset, float visibleTop, int parentW, int parentH, int blurredViewTopOffset, int blurredViewBottomOffset) {
        this.parentWidth = parentW;
        this.parentHeight = parentH;
        this.backgroundHeight = parentH;
        this.blurredViewTopOffset = blurredViewTopOffset;
        this.blurredViewBottomOffset = blurredViewBottomOffset;
        this.viewTop = visibleTop;
        if (parent != parentH || parentOffset != this.parentViewTopOffset) {
            this.parentViewTopOffset = parentOffset;
            this.parentHeight = parent;
        }
        if (this.currentMessageObject != null && hasGradientService() && this.currentMessageObject.shouldDrawWithoutBackground()) {
            invalidate();
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.textLayoutBlocks == null) {
            return;
        }
        int position2 = position - this.textY;
        int newFirst = -1;
        int newLast = -1;
        int newCount = 0;
        int startBlock = 0;
        for (int a = 0; a < this.currentMessageObject.textLayoutBlocks.size() && this.currentMessageObject.textLayoutBlocks.get(a).textYOffset <= position2; a++) {
            startBlock = a;
        }
        for (int a2 = startBlock; a2 < this.currentMessageObject.textLayoutBlocks.size(); a2++) {
            MessageObject.TextLayoutBlock block = this.currentMessageObject.textLayoutBlocks.get(a2);
            float y = block.textYOffset;
            if (intersect(y, block.height + y, position2, position2 + height)) {
                if (newFirst == -1) {
                    newFirst = a2;
                }
                int newLast2 = a2;
                newCount++;
                newLast = newLast2;
            } else if (y > position2) {
                break;
            }
        }
        if (this.lastVisibleBlockNum != newLast || this.firstVisibleBlockNum != newFirst || this.totalVisibleBlocksCount != newCount) {
            this.lastVisibleBlockNum = newLast;
            this.firstVisibleBlockNum = newFirst;
            this.totalVisibleBlocksCount = newCount;
            invalidate();
        }
    }

    private boolean intersect(float left1, float right1, float left2, float right2) {
        return left1 <= left2 ? right1 >= left2 : left1 <= right2;
    }

    public static StaticLayout generateStaticLayout(CharSequence text, TextPaint paint, int maxWidth, int smallWidth, int linesCount, int maxLines) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        int addedChars = 0;
        StaticLayout layout = new StaticLayout(text, paint, smallWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int maxWidth2 = maxWidth;
        for (int a = 0; a < linesCount; a++) {
            layout.getLineDirections(a);
            if (layout.getLineLeft(a) != 0.0f || layout.isRtlCharAt(layout.getLineStart(a)) || layout.isRtlCharAt(layout.getLineEnd(a))) {
                maxWidth2 = smallWidth;
            }
            int pos = layout.getLineEnd(a);
            if (pos == text.length()) {
                break;
            }
            int pos2 = pos - 1;
            if (stringBuilder.charAt(pos2 + addedChars) == ' ') {
                stringBuilder.replace(pos2 + addedChars, pos2 + addedChars + 1, (CharSequence) "\n");
            } else if (stringBuilder.charAt(pos2 + addedChars) != '\n') {
                stringBuilder.insert(pos2 + addedChars, (CharSequence) "\n");
                addedChars++;
            }
            if (a == layout.getLineCount() - 1 || a == maxLines - 1) {
                break;
            }
        }
        return StaticLayoutEx.createStaticLayout(stringBuilder, paint, maxWidth2, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1.0f), false, TextUtils.TruncateAt.END, maxWidth2, maxLines, true);
    }

    private void didClickedImage() {
        TLRPC.WebPage webPage;
        if (this.currentMessageObject.type == 1 || this.currentMessageObject.isAnyKindOfSticker()) {
            int i = this.buttonState;
            if (i == -1) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i == 0) {
                didPressButton(true, false);
            }
        } else if (this.currentMessageObject.type == 12) {
            long uid = this.currentMessageObject.messageOwner.media.user_id;
            TLRPC.User user = null;
            if (uid != 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid));
            }
            this.delegate.didPressUserAvatar(this, user, this.lastTouchX, this.lastTouchY);
        } else if (this.currentMessageObject.type == 5) {
            if (this.buttonState != -1) {
                didPressButton(true, false);
            } else if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.delegate.needPlayMessage(this.currentMessageObject);
            } else {
                MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject);
            }
        } else if (this.currentMessageObject.type == 8) {
            int i2 = this.buttonState;
            if (i2 == -1 || (i2 == 1 && this.canStreamVideo && this.autoPlayingMedia)) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i2 == 2 || i2 == 0) {
                didPressButton(true, false);
            }
        } else if (this.documentAttachType == 4) {
            if (this.buttonState == -1 || (this.drawVideoImageButton && (this.autoPlayingMedia || (SharedConfig.streamMedia && this.canStreamVideo)))) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (this.drawVideoImageButton) {
                didPressButton(true, true);
            } else {
                int i3 = this.buttonState;
                if (i3 == 0 || i3 == 3) {
                    didPressButton(true, false);
                }
            }
        } else if (this.currentMessageObject.type == 4) {
            this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
        } else {
            int i4 = this.documentAttachType;
            if (i4 == 1) {
                if (this.buttonState == -1) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                }
            } else if (i4 == 2) {
                if (this.buttonState == -1 && (webPage = this.currentMessageObject.messageOwner.media.webpage) != null) {
                    if (webPage.embed_url != null && webPage.embed_url.length() != 0) {
                        this.delegate.needOpenWebView(this.currentMessageObject, webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                    } else {
                        Browser.openUrl(getContext(), webPage.url);
                    }
                }
            } else if (this.hasInvoicePreview) {
                if (this.buttonState == -1) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                }
            } else if (Build.VERSION.SDK_INT >= 26 && this.delegate != null) {
                if (this.currentMessageObject.type == 16) {
                    this.delegate.didLongPress(this, 0.0f, 0.0f);
                } else {
                    this.delegate.didPressOther(this, this.otherX, this.otherY);
                }
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        String str;
        if (messageObject == null || !messageObject.needDrawBluredPreview() || (str = messageObject.getSecretTimeString()) == null) {
            return;
        }
        this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
        CharSequence str2 = TextUtils.ellipsize(str, Theme.chat_infoPaint, this.infoWidth, TextUtils.TruncateAt.END);
        this.infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        invalidate();
    }

    private boolean isPhotoDataChanged(MessageObject object) {
        int provider;
        String url;
        if (object.type == 0 || object.type == 14) {
            return false;
        }
        if (object.type == 4) {
            if (this.currentUrl == null) {
                return true;
            }
            double lat = object.messageOwner.media.geo.lat;
            double lon = object.messageOwner.media.geo._long;
            if (((int) object.getDialogId()) == 0) {
                if (SharedConfig.mapPreviewType == 0) {
                    provider = -1;
                } else if (SharedConfig.mapPreviewType == 1) {
                    provider = 4;
                } else if (SharedConfig.mapPreviewType == 3) {
                    provider = 1;
                } else {
                    provider = -1;
                }
            } else {
                provider = -1;
            }
            if (object.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) {
                int photoWidth = this.backgroundWidth - AndroidUtilities.dp(21.0f);
                int photoHeight = AndroidUtilities.dp(195.0f);
                double d = 268435456;
                Double.isNaN(d);
                double rad = d / 3.141592653589793d;
                double d2 = 268435456;
                Double.isNaN(d2);
                double y = Math.round(d2 - ((Math.log((Math.sin((lat * 3.141592653589793d) / 180.0d) + 1.0d) / (1.0d - Math.sin((lat * 3.141592653589793d) / 180.0d))) * rad) / 2.0d)) - (AndroidUtilities.dp(10.3f) << 6);
                double d3 = 268435456;
                Double.isNaN(y);
                Double.isNaN(d3);
                double lat2 = ((1.5707963267948966d - (Math.atan(Math.exp((y - d3) / rad)) * 2.0d)) * 180.0d) / 3.141592653589793d;
                int i = this.currentAccount;
                int photoHeight2 = (int) (photoWidth / AndroidUtilities.density);
                url = AndroidUtilities.formapMapUrl(i, lat2, lon, photoHeight2, (int) (photoHeight / AndroidUtilities.density), false, 15, provider);
            } else if (!TextUtils.isEmpty(object.messageOwner.media.title)) {
                int photoWidth2 = this.backgroundWidth - AndroidUtilities.dp(21.0f);
                int photoHeight3 = AndroidUtilities.dp(195.0f);
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (photoWidth2 / AndroidUtilities.density), (int) (photoHeight3 / AndroidUtilities.density), true, 15, provider);
            } else {
                int photoWidth3 = this.backgroundWidth - AndroidUtilities.dp(12.0f);
                int photoHeight4 = AndroidUtilities.dp(195.0f);
                url = AndroidUtilities.formapMapUrl(this.currentAccount, lat, lon, (int) (photoWidth3 / AndroidUtilities.density), (int) (photoHeight4 / AndroidUtilities.density), true, 15, provider);
            }
            return !url.equals(this.currentUrl);
        }
        TLRPC.PhotoSize photoSize = this.currentPhotoObject;
        if (photoSize == null || (photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
            return object.type == 1 || object.type == 5 || object.type == 3 || object.type == 8 || object.isAnyKindOfSticker();
        } else if (this.currentMessageObject == null || !this.photoNotSet) {
            return false;
        } else {
            File cacheFile = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
            return cacheFile.exists();
        }
    }

    public int getRepliesCount() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty()) {
            MessageObject messageObject = this.currentMessagesGroup.messages.get(0);
            return messageObject.getRepliesCount();
        }
        MessageObject messageObject2 = this.currentMessageObject;
        return messageObject2.getRepliesCount();
    }

    private ArrayList<TLRPC.Peer> getRecentRepliers() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty()) {
            MessageObject messageObject = this.currentMessagesGroup.messages.get(0);
            if (messageObject.messageOwner.replies != null) {
                return messageObject.messageOwner.replies.recent_repliers;
            }
        }
        if (this.currentMessageObject.messageOwner.replies != null) {
            return this.currentMessageObject.messageOwner.replies.recent_repliers;
        }
        return null;
    }

    private void updateCaptionSpoilers() {
        this.captionSpoilersPool.addAll(this.captionSpoilers);
        this.captionSpoilers.clear();
        if (this.captionLayout != null && !getMessageObject().isSpoilersRevealed) {
            SpoilerEffect.addSpoilers(this, this.captionLayout, this.captionSpoilersPool, this.captionSpoilers);
        }
    }

    private boolean isUserDataChanged() {
        TLRPC.PhotoSize photoSize;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || this.hasLinkPreview || messageObject.messageOwner.media == null || !(this.currentMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || (this.currentUser == null && this.currentChat == null)) {
                return false;
            }
            if (this.lastSendState != messageObject2.messageOwner.send_state || this.lastDeleteDate != this.currentMessageObject.messageOwner.destroyTime || this.lastViewsCount != this.currentMessageObject.messageOwner.views || this.lastRepliesCount != getRepliesCount() || this.lastReactions != this.currentMessageObject.messageOwner.reactions) {
                return true;
            }
            updateCurrentUserAndChat();
            TLRPC.FileLocation newPhoto = null;
            if (this.isAvatarVisible) {
                TLRPC.User user = this.currentUser;
                if (user != null && user.photo != null) {
                    newPhoto = this.currentUser.photo.photo_small;
                } else {
                    TLRPC.Chat chat = this.currentChat;
                    if (chat != null && chat.photo != null) {
                        newPhoto = this.currentChat.photo.photo_small;
                    }
                }
            }
            if (this.replyTextLayout == null && this.currentMessageObject.replyMessageObject != null && (!this.isThreadChat || this.currentMessageObject.replyMessageObject.messageOwner.fwd_from == null || this.currentMessageObject.replyMessageObject.messageOwner.fwd_from.channel_post == 0)) {
                return true;
            }
            TLRPC.FileLocation fileLocation = this.currentPhoto;
            if ((fileLocation == null && newPhoto != null) || ((fileLocation != null && newPhoto == null) || (fileLocation != null && (fileLocation.local_id != newPhoto.local_id || this.currentPhoto.volume_id != newPhoto.volume_id)))) {
                return true;
            }
            TLRPC.PhotoSize newReplyPhoto = null;
            if (this.replyNameLayout != null && this.currentMessageObject.replyMessageObject != null && (photoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 40)) != null && !this.currentMessageObject.replyMessageObject.isAnyKindOfSticker()) {
                newReplyPhoto = photoSize;
            }
            if (this.currentReplyPhoto == null && newReplyPhoto != null) {
                return true;
            }
            String newNameString = isNeedAuthorName() ? getAuthorName() : null;
            String str = this.currentNameString;
            if ((str == null && newNameString != null) || ((str != null && newNameString == null) || (str != null && !str.equals(newNameString)))) {
                return true;
            }
            if (!this.drawForwardedName || !this.currentMessageObject.needDrawForwarded()) {
                return false;
            }
            String newNameString2 = this.currentMessageObject.getForwardedName();
            String str2 = this.currentForwardNameString;
            if (str2 == null && newNameString2 != null) {
                return true;
            }
            if (str2 != null && newNameString2 == null) {
                return true;
            }
            return str2 != null && !str2.equals(newNameString2);
        }
        return true;
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    public int getNoSoundIconCenterX() {
        return this.noSoundCenterX;
    }

    public int getForwardNameCenterX() {
        TLRPC.User user = this.currentUser;
        if (user != null && user.id == 0) {
            return (int) this.avatarImage.getCenterX();
        }
        return (int) (this.forwardNameX + this.forwardNameCenterX);
    }

    public int getChecksX() {
        return this.layoutWidth - AndroidUtilities.dp(SharedConfig.bubbleRadius >= 10 ? 27.3f : 25.3f);
    }

    public int getChecksY() {
        if (this.currentMessageObject.shouldDrawWithoutBackground()) {
            return (int) (this.drawTimeY - getThemedDrawable(Theme.key_drawable_msgStickerCheck).getIntrinsicHeight());
        }
        return (int) (this.drawTimeY - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
    }

    public TLRPC.User getCurrentUser() {
        return this.currentUser;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopSpoilers);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
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
            int a = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (a >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[a].onDetachedFromWindow();
                a++;
            }
        }
        this.attachedToWindow = false;
        this.radialProgress.onDetachedFromWindow();
        this.videoRadialProgress.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
        if (this.pollAvatarImages != null) {
            int a2 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
                if (a2 >= imageReceiverArr.length) {
                    break;
                }
                imageReceiverArr[a2].onDetachedFromWindow();
                a2++;
            }
        }
        if (this.commentAvatarImages != null) {
            int a3 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr2 = this.commentAvatarImages;
                if (a3 >= imageReceiverArr2.length) {
                    break;
                }
                imageReceiverArr2[a3].onDetachedFromWindow();
                a3++;
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
        if (runnable != null) {
            runnable.run();
            this.unregisterFlagSecure = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopSpoilers);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            messageObject.animateComments = false;
        }
        MessageObject messageObject2 = this.messageObjectToSet;
        if (messageObject2 != null) {
            messageObject2.animateComments = false;
            setMessageContent(this.messageObjectToSet, this.groupedMessagesToSet, this.bottomNearToSet, this.topNearToSet);
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
            int a = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (a >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[a].onAttachedToWindow();
                a++;
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
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null) {
            setAvatar(messageObject3);
        }
        if (this.pollAvatarImages != null) {
            int a2 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
                if (a2 >= imageReceiverArr.length) {
                    break;
                }
                imageReceiverArr[a2].onAttachedToWindow();
                a2++;
            }
        }
        if (this.commentAvatarImages != null) {
            int a3 = 0;
            while (true) {
                ImageReceiver[] imageReceiverArr2 = this.commentAvatarImages;
                if (a3 >= imageReceiverArr2.length) {
                    break;
                }
                imageReceiverArr2[a3].onAttachedToWindow();
                a3++;
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
        MessageObject messageObject4 = this.currentMessageObject;
        if (messageObject4 != null && (this.isRoundVideo || messageObject4.isVideo())) {
            checkVideoPlayback(true, null);
        }
        int i = this.documentAttachType;
        if (i == 4 && this.autoPlayingMedia) {
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            this.animatingNoSoundPlaying = isPlayingMessage;
            this.animatingNoSoundProgress = isPlayingMessage ? 0.0f : 1.0f;
            this.animatingNoSound = 0;
        } else {
            this.animatingNoSoundPlaying = false;
            this.animatingNoSoundProgress = 0.0f;
            this.animatingDrawVideoImageButtonProgress = ((i == 4 || i == 2) && this.drawVideoSize) ? 1.0f : 0.0f;
        }
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellAttached(this);
        }
        if (this.documentAttachType == 5) {
            boolean showSeekbar = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (showSeekbar) {
                f = 1.0f;
            }
            this.toSeekBarProgress = f;
        }
        this.reactionsLayoutInBubble.onAttachToWindow();
        updateFlagSecure();
    }

    /* JADX WARN: Code restructure failed: missing block: B:1442:0x1e39, code lost:
        if (r91.documentAttachType == 0) goto L1449;
     */
    /* JADX WARN: Code restructure failed: missing block: B:2471:0x3c3b, code lost:
        if (r6 < (r91.timeWidth + org.telegram.messenger.AndroidUtilities.dp((r92.isOutOwner() ? 20 : 0) + 20))) goto L2472;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3109:0x4bcf, code lost:
        if (r2 >= (r91.captionWidth + org.telegram.messenger.AndroidUtilities.dp(10.0f))) goto L3114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3110:0x4bd1, code lost:
        r3 = r91.captionWidth + org.telegram.messenger.AndroidUtilities.dp(10.0f);
        r2 = org.telegram.messenger.AndroidUtilities.dp(8.0f) + r3;
        r91.backgroundWidth = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3111:0x4be1, code lost:
        if (r91.mediaBackground != false) goto L3113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3112:0x4be3, code lost:
        r91.backgroundWidth = r2 + org.telegram.messenger.AndroidUtilities.dp(9.0f);
     */
    /* JADX WARN: Code restructure failed: missing block: B:3113:0x4bec, code lost:
        r24 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:3595:0x5792, code lost:
        if (r15.type != 3) goto L3598;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x008e, code lost:
        if (r91.isPlayingRound != (org.telegram.messenger.MediaController.getInstance().isPlayingMessage(r91.currentMessageObject) && (r3 = r91.delegate) != null && !r3.keyboardIsOpened())) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:806:0x1053, code lost:
        if (r7 != 13) goto L812;
     */
    /* JADX WARN: Code restructure failed: missing block: B:887:0x1234, code lost:
        if (r91.isSmallImage != false) goto L892;
     */
    /* JADX WARN: Multi-variable search skipped. Vars limit reached: 7718 (expected less than 5000) */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1004:0x14dc  */
    /* JADX WARN: Removed duplicated region for block: B:1057:0x15e3 A[Catch: Exception -> 0x1676, TRY_LEAVE, TryCatch #4 {Exception -> 0x1676, blocks: (B:1055:0x15db, B:1057:0x15e3, B:1069:0x1618), top: B:3903:0x15db }] */
    /* JADX WARN: Removed duplicated region for block: B:1099:0x1699  */
    /* JADX WARN: Removed duplicated region for block: B:1101:0x16a4  */
    /* JADX WARN: Removed duplicated region for block: B:1107:0x16b4  */
    /* JADX WARN: Removed duplicated region for block: B:1108:0x16b7  */
    /* JADX WARN: Removed duplicated region for block: B:1110:0x16bb  */
    /* JADX WARN: Removed duplicated region for block: B:1304:0x1c26  */
    /* JADX WARN: Removed duplicated region for block: B:1318:0x1c53  */
    /* JADX WARN: Removed duplicated region for block: B:1319:0x1c55  */
    /* JADX WARN: Removed duplicated region for block: B:1322:0x1c62  */
    /* JADX WARN: Removed duplicated region for block: B:1323:0x1c64  */
    /* JADX WARN: Removed duplicated region for block: B:1326:0x1c6d  */
    /* JADX WARN: Removed duplicated region for block: B:1327:0x1c78  */
    /* JADX WARN: Removed duplicated region for block: B:1330:0x1c84  */
    /* JADX WARN: Removed duplicated region for block: B:1343:0x1cab  */
    /* JADX WARN: Removed duplicated region for block: B:1454:0x1e68  */
    /* JADX WARN: Removed duplicated region for block: B:1455:0x1e78  */
    /* JADX WARN: Removed duplicated region for block: B:1458:0x1e82  */
    /* JADX WARN: Removed duplicated region for block: B:1459:0x1e8b  */
    /* JADX WARN: Removed duplicated region for block: B:1476:0x1ee8  */
    /* JADX WARN: Removed duplicated region for block: B:1481:0x1f18  */
    /* JADX WARN: Removed duplicated region for block: B:1484:0x1f31  */
    /* JADX WARN: Removed duplicated region for block: B:1490:0x1f9a  */
    /* JADX WARN: Removed duplicated region for block: B:1491:0x2000  */
    /* JADX WARN: Removed duplicated region for block: B:1620:0x2492  */
    /* JADX WARN: Removed duplicated region for block: B:1626:0x24cd  */
    /* JADX WARN: Removed duplicated region for block: B:1646:0x254b  */
    /* JADX WARN: Removed duplicated region for block: B:1664:0x2639  */
    /* JADX WARN: Removed duplicated region for block: B:1667:0x2641  */
    /* JADX WARN: Removed duplicated region for block: B:1674:0x268c  */
    /* JADX WARN: Removed duplicated region for block: B:2119:0x3274  */
    /* JADX WARN: Removed duplicated region for block: B:2136:0x32ef  */
    /* JADX WARN: Removed duplicated region for block: B:2137:0x32fb  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x0351  */
    /* JADX WARN: Removed duplicated region for block: B:249:0x0358  */
    /* JADX WARN: Removed duplicated region for block: B:2712:0x42bd  */
    /* JADX WARN: Removed duplicated region for block: B:2715:0x42cb  */
    /* JADX WARN: Removed duplicated region for block: B:2719:0x42d7  */
    /* JADX WARN: Removed duplicated region for block: B:2747:0x4337  */
    /* JADX WARN: Removed duplicated region for block: B:2748:0x433d  */
    /* JADX WARN: Removed duplicated region for block: B:2773:0x438b  */
    /* JADX WARN: Removed duplicated region for block: B:2803:0x4422  */
    /* JADX WARN: Removed duplicated region for block: B:2810:0x4465  */
    /* JADX WARN: Removed duplicated region for block: B:2813:0x4472  */
    /* JADX WARN: Removed duplicated region for block: B:2816:0x449f  */
    /* JADX WARN: Removed duplicated region for block: B:2817:0x44a2  */
    /* JADX WARN: Removed duplicated region for block: B:2820:0x44aa  */
    /* JADX WARN: Removed duplicated region for block: B:2821:0x44ad  */
    /* JADX WARN: Removed duplicated region for block: B:2824:0x44b8  */
    /* JADX WARN: Removed duplicated region for block: B:2827:0x44bf  */
    /* JADX WARN: Removed duplicated region for block: B:2828:0x44d2  */
    /* JADX WARN: Removed duplicated region for block: B:2836:0x4505  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x03b2  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x03b9  */
    /* JADX WARN: Removed duplicated region for block: B:2991:0x48fa  */
    /* JADX WARN: Removed duplicated region for block: B:3062:0x4aa2  */
    /* JADX WARN: Removed duplicated region for block: B:3065:0x4aac  */
    /* JADX WARN: Removed duplicated region for block: B:3098:0x4ba5  */
    /* JADX WARN: Removed duplicated region for block: B:3105:0x4bbb  */
    /* JADX WARN: Removed duplicated region for block: B:3106:0x4bc5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3116:0x4bf3 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3132:0x4ca2  */
    /* JADX WARN: Removed duplicated region for block: B:3142:0x4cc0  */
    /* JADX WARN: Removed duplicated region for block: B:3143:0x4ceb  */
    /* JADX WARN: Removed duplicated region for block: B:3146:0x4d06  */
    /* JADX WARN: Removed duplicated region for block: B:3155:0x4d1d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3161:0x4d2c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3167:0x4d3b  */
    /* JADX WARN: Removed duplicated region for block: B:3180:0x4d68  */
    /* JADX WARN: Removed duplicated region for block: B:3188:0x4d80  */
    /* JADX WARN: Removed duplicated region for block: B:3189:0x4d86 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:3193:0x4d8d  */
    /* JADX WARN: Removed duplicated region for block: B:319:0x0460  */
    /* JADX WARN: Removed duplicated region for block: B:3204:0x4e63  */
    /* JADX WARN: Removed duplicated region for block: B:327:0x0494  */
    /* JADX WARN: Removed duplicated region for block: B:3367:0x52e8  */
    /* JADX WARN: Removed duplicated region for block: B:3378:0x530a  */
    /* JADX WARN: Removed duplicated region for block: B:3387:0x5334  */
    /* JADX WARN: Removed duplicated region for block: B:3394:0x5356  */
    /* JADX WARN: Removed duplicated region for block: B:3397:0x536e  */
    /* JADX WARN: Removed duplicated region for block: B:3408:0x53a5  */
    /* JADX WARN: Removed duplicated region for block: B:3411:0x53b4  */
    /* JADX WARN: Removed duplicated region for block: B:3412:0x53c4  */
    /* JADX WARN: Removed duplicated region for block: B:3415:0x53d7  */
    /* JADX WARN: Removed duplicated region for block: B:3421:0x53ee  */
    /* JADX WARN: Removed duplicated region for block: B:3436:0x5429  */
    /* JADX WARN: Removed duplicated region for block: B:3444:0x544f  */
    /* JADX WARN: Removed duplicated region for block: B:3451:0x546a  */
    /* JADX WARN: Removed duplicated region for block: B:351:0x056d  */
    /* JADX WARN: Removed duplicated region for block: B:3551:0x5667  */
    /* JADX WARN: Removed duplicated region for block: B:3562:0x5691  */
    /* JADX WARN: Removed duplicated region for block: B:3606:0x57c8  */
    /* JADX WARN: Removed duplicated region for block: B:3620:0x5805  */
    /* JADX WARN: Removed duplicated region for block: B:3623:0x5816  */
    /* JADX WARN: Removed duplicated region for block: B:369:0x05d0  */
    /* JADX WARN: Removed duplicated region for block: B:370:0x05e2  */
    /* JADX WARN: Removed duplicated region for block: B:3725:0x5b38  */
    /* JADX WARN: Removed duplicated region for block: B:372:0x05f1  */
    /* JADX WARN: Removed duplicated region for block: B:3732:0x5b55  */
    /* JADX WARN: Removed duplicated region for block: B:3736:0x5b67  */
    /* JADX WARN: Removed duplicated region for block: B:3737:0x5b75  */
    /* JADX WARN: Removed duplicated region for block: B:3749:0x5b99  */
    /* JADX WARN: Removed duplicated region for block: B:3754:0x5bb8  */
    /* JADX WARN: Removed duplicated region for block: B:3757:0x5bcf  */
    /* JADX WARN: Removed duplicated region for block: B:3760:0x5bdd  */
    /* JADX WARN: Removed duplicated region for block: B:3767:0x5c10  */
    /* JADX WARN: Removed duplicated region for block: B:3769:0x5c18  */
    /* JADX WARN: Removed duplicated region for block: B:3834:0x5cd7  */
    /* JADX WARN: Removed duplicated region for block: B:3842:0x5d03  */
    /* JADX WARN: Removed duplicated region for block: B:3855:0x5d4a  */
    /* JADX WARN: Removed duplicated region for block: B:3873:0x5d80  */
    /* JADX WARN: Removed duplicated region for block: B:3879:0x5d92  */
    /* JADX WARN: Removed duplicated region for block: B:3888:0x5dc7  */
    /* JADX WARN: Removed duplicated region for block: B:3923:0x558e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3941:0x4977 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3971:0x1590 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:3979:0x04aa A[EDGE_INSN: B:3979:0x04aa->B:331:0x04aa ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:481:0x0894  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x00d7  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00d9  */
    /* JADX WARN: Removed duplicated region for block: B:973:0x13f7  */
    /* JADX WARN: Removed duplicated region for block: B:980:0x1415 A[ADDED_TO_REGION] */
    /* JADX WARN: Type inference failed for: r14v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r14v248 */
    /* JADX WARN: Type inference failed for: r14v249 */
    /* JADX WARN: Type inference failed for: r14v6 */
    /* JADX WARN: Type inference failed for: r14v7 */
    /* JADX WARN: Type inference failed for: r2v1133 */
    /* JADX WARN: Type inference failed for: r2v1137 */
    /* JADX WARN: Type inference failed for: r2v1138 */
    /* JADX WARN: Type inference failed for: r2v75, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r2v851, types: [org.telegram.messenger.ImageReceiver] */
    /* JADX WARN: Type inference failed for: r2v853, types: [org.telegram.messenger.ImageReceiver] */
    /* JADX WARN: Type inference failed for: r4v92, types: [android.text.StaticLayout$Builder] */
    /* JADX WARN: Type inference failed for: r9v141, types: [java.util.List, java.util.ArrayList] */
    /* JADX WARN: Type inference failed for: r9v202, types: [boolean] */
    /* JADX WARN: Type inference failed for: r9v205, types: [boolean] */
    /* JADX WARN: Type inference failed for: r9v244, types: [float] */
    /* JADX WARN: Type inference failed for: r9v296 */
    /* JADX WARN: Type inference failed for: r9v298, types: [android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable] */
    /* JADX WARN: Type inference failed for: r9v299, types: [android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable] */
    /* JADX WARN: Type inference failed for: r9v310 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageContent(org.telegram.messenger.MessageObject r92, org.telegram.messenger.MessageObject.GroupedMessages r93, boolean r94, boolean r95) {
        /*
            Method dump skipped, instructions count: 24042
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    public static /* synthetic */ int lambda$setMessageContent$5(PollButton o1, PollButton o2) {
        if (o1.decimal > o2.decimal) {
            return -1;
        }
        if (o1.decimal < o2.decimal) {
            return 1;
        }
        if (o1.decimal == o2.decimal) {
            if (o1.percent > o2.percent) {
                return 1;
            }
            return o1.percent < o2.percent ? -1 : 0;
        }
        return 0;
    }

    private void updateFlagSecure() {
        Runnable runnable;
        MessageObject messageObject = this.currentMessageObject;
        boolean flagSecure = (messageObject == null || messageObject.messageOwner == null || !this.currentMessageObject.messageOwner.noforwards) ? false : true;
        Activity activity = AndroidUtilities.findActivity(getContext());
        if (flagSecure && this.unregisterFlagSecure == null && activity != null) {
            this.unregisterFlagSecure = AndroidUtilities.registerFlagSecure(activity.getWindow());
        } else if (!flagSecure && (runnable = this.unregisterFlagSecure) != null) {
            runnable.run();
            this.unregisterFlagSecure = null;
        }
    }

    public void checkVideoPlayback(boolean allowStart, Bitmap thumb) {
        boolean z = false;
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
        if (allowStart) {
            MessageObject playingMessage = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessage == null || !playingMessage.isRoundVideo()) {
                z = true;
            }
            allowStart = z;
        }
        this.photoImage.setAllowStartAnimation(allowStart);
        if (thumb != null) {
            this.photoImage.startCrossfadeFromStaticThumb(thumb);
        }
        if (allowStart) {
            this.photoImage.startAnimation();
        } else {
            this.photoImage.stopAnimation();
        }
    }

    private static boolean spanSupportsLongPress(CharacterStyle span) {
        return (span instanceof URLSpanMono) || (span instanceof URLSpan);
    }

    @Override // org.telegram.ui.Cells.BaseCell
    protected boolean onLongPress() {
        int id;
        if (this.isRoundVideo && this.isPlayingRound && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            float touchRadius = ((this.lastTouchX - this.photoImage.getCenterX()) * (this.lastTouchX - this.photoImage.getCenterX())) + ((this.lastTouchY - this.photoImage.getCenterY()) * (this.lastTouchY - this.photoImage.getCenterY()));
            float r1 = (this.photoImage.getImageWidth() / 2.0f) * (this.photoImage.getImageWidth() / 2.0f);
            if (touchRadius < r1 && (this.lastTouchX > this.photoImage.getCenterX() + (this.photoImage.getImageWidth() / 4.0f) || this.lastTouchX < this.photoImage.getCenterX() - (this.photoImage.getImageWidth() / 4.0f))) {
                boolean forward = this.lastTouchX > this.photoImage.getCenterX();
                if (this.videoPlayerRewinder == null) {
                    this.videoForwardDrawable = new VideoForwardDrawable(true);
                    this.videoPlayerRewinder = new VideoPlayerRewinder() { // from class: org.telegram.ui.Cells.ChatMessageCell.4
                        @Override // org.telegram.messenger.video.VideoPlayerRewinder
                        protected void onRewindCanceled() {
                            ChatMessageCell.this.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                            ChatMessageCell.this.videoForwardDrawable.setShowing(false);
                        }

                        @Override // org.telegram.messenger.video.VideoPlayerRewinder
                        protected void updateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
                            ChatMessageCell.this.videoForwardDrawable.setTime(Math.abs(timeDiff));
                            if (rewindByBackSeek) {
                                ChatMessageCell.this.currentMessageObject.audioProgress = progress;
                                ChatMessageCell.this.updatePlayingMessageProgress();
                            }
                        }

                        @Override // org.telegram.messenger.video.VideoPlayerRewinder
                        protected void onRewindStart(boolean rewindForward) {
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
                            ChatMessageCell.this.videoForwardDrawable.setLeftSide(!rewindForward);
                            ChatMessageCell.this.videoForwardDrawable.setShowing(true);
                            ChatMessageCell.this.invalidate();
                        }
                    };
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.videoPlayerRewinder.startRewind(MediaController.getInstance().getVideoPlayer(), forward, MediaController.getInstance().getPlaybackSpeed(false));
                return false;
            }
        }
        LinkSpanDrawable linkSpanDrawable = this.pressedLink;
        if (linkSpanDrawable != null) {
            if (linkSpanDrawable.getSpan() instanceof URLSpanMono) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            } else if (this.pressedLink.getSpan() instanceof URLSpanNoUnderline) {
                URLSpanNoUnderline url = (URLSpanNoUnderline) this.pressedLink.getSpan();
                if (ChatActivity.isClickableLink(url.getURL()) || url.getURL().startsWith("/")) {
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
                int a = 0;
                while (true) {
                    Drawable[] drawableArr = this.selectorDrawable;
                    if (a >= drawableArr.length) {
                        break;
                    }
                    if (drawableArr[a] != null) {
                        drawableArr[a].setState(StateSet.NOTHING);
                    }
                    a++;
                }
            }
            invalidate();
        }
        if (this.delegate != null) {
            boolean handled = false;
            if (this.avatarPressed) {
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    if (user.id != 0) {
                        handled = this.delegate.didLongPressUserAvatar(this, this.currentUser, this.lastTouchX, this.lastTouchY);
                    }
                } else if (this.currentChat != null) {
                    if (this.currentMessageObject.messageOwner.fwd_from != null) {
                        if ((this.currentMessageObject.messageOwner.fwd_from.flags & 16) != 0) {
                            id = this.currentMessageObject.messageOwner.fwd_from.saved_from_msg_id;
                        } else {
                            id = this.currentMessageObject.messageOwner.fwd_from.channel_post;
                        }
                    } else {
                        id = 0;
                    }
                    handled = this.delegate.didLongPressChannelAvatar(this, this.currentChat, id, this.lastTouchX, this.lastTouchY);
                }
            }
            if (!handled) {
                this.delegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
            }
        }
        return true;
    }

    public void showHintButton(boolean show, boolean animated, int type) {
        float f = 1.0f;
        if (type == -1 || type == 0) {
            if (this.hintButtonVisible == show) {
                return;
            }
            this.hintButtonVisible = show;
            if (!animated) {
                this.hintButtonProgress = show ? 1.0f : 0.0f;
            } else {
                invalidate();
            }
        }
        if ((type != -1 && type != 1) || this.psaButtonVisible == show) {
            return;
        }
        this.psaButtonVisible = show;
        if (!animated) {
            if (!show) {
                f = 0.0f;
            }
            this.psaButtonProgress = f;
            return;
        }
        setInvalidatesParent(true);
        invalidate();
    }

    public void setCheckPressed(boolean value, boolean pressed) {
        this.isCheckPressed = value;
        this.isPressed = pressed;
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    public void setInvalidateSpoilersParent(boolean invalidateSpoilersParent) {
        this.invalidateSpoilersParent = invalidateSpoilersParent;
    }

    public void setInvalidatesParent(boolean value) {
        this.invalidatesParent = value;
    }

    @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
    public void invalidate() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate();
        if ((this.invalidatesParent || (this.currentMessagesGroup != null && !this.links.isEmpty())) && getParent() != null) {
            View parent = (View) getParent();
            if (parent.getParent() != null) {
                parent.invalidate();
                ((View) parent.getParent()).invalidate();
            }
        }
        if (this.isBlurred && (chatMessageCellDelegate = this.delegate) != null) {
            chatMessageCellDelegate.invalidateBlur();
        }
    }

    @Override // android.view.View
    public void invalidate(int l, int t, int r, int b) {
        if (this.currentMessageObject == null) {
            return;
        }
        super.invalidate(l, t, r, b);
        if (this.invalidatesParent && getParent() != null) {
            View parent = (View) getParent();
            parent.invalidate(((int) getX()) + l, ((int) getY()) + t, ((int) getX()) + r, ((int) getY()) + b);
        }
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

    public void setHighlighted(boolean value) {
        if (this.isHighlighted == value) {
            return;
        }
        this.isHighlighted = value;
        if (!value) {
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
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    @Override // android.view.View
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
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
        boolean forcePressed = (this.isHighlighted || this.isPressed || isPressed()) && (!this.drawPhotoImage || !this.photoImage.hasBitmapImage());
        this.radialProgress.setPressed(forcePressed || this.buttonPressed != 0, false);
        if (this.hasMiniProgress != 0) {
            this.radialProgress.setPressed(forcePressed || this.miniButtonPressed != 0, true);
        }
        RadialProgress2 radialProgress2 = this.videoRadialProgress;
        if (!forcePressed && this.videoButtonPressed == 0) {
            z = false;
        }
        radialProgress2.setPressed(z, false);
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarDrag(float progress) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = progress;
        MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
        updatePlayingMessageProgress();
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarContinuousDrag(float progress) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = progress;
        MessageObject messageObject2 = this.currentMessageObject;
        messageObject2.audioProgressSec = (int) (messageObject2.getDuration() * progress);
        updatePlayingMessageProgress();
    }

    public boolean isAnimatingPollAnswer() {
        return this.animatePollAnswerAlpha;
    }

    private void updateWaveform() {
        boolean z;
        if (this.currentMessageObject == null || this.documentAttachType != 3) {
            return;
        }
        int a = 0;
        while (true) {
            z = false;
            if (a >= this.documentAttach.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = this.documentAttach.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                a++;
            } else {
                if (attribute.waveform == null || attribute.waveform.length == 0) {
                    MediaController.getInstance().generateWaveform(this.currentMessageObject);
                }
                this.useSeekBarWaveform = attribute.waveform != null;
                this.seekBarWaveform.setWaveform(attribute.waveform);
            }
        }
        if (this.currentMessageObject.isVoice() && this.useSeekBarWaveform && this.currentMessageObject.messageOwner != null && !(this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && UserConfig.getInstance(this.currentAccount).isPremium()) {
            z = true;
        }
        this.useTranscribeButton = z;
        updateSeekBarWaveformWidth();
    }

    private void updateSeekBarWaveformWidth() {
        if (this.seekBarWaveform != null) {
            int offset = (-AndroidUtilities.dp((this.hasLinkPreview ? 10 : 0) + 92)) - AndroidUtilities.dp(this.useTranscribeButton ? 34.0f : 0.0f);
            if (this.transitionParams.animateBackgroundBoundsInner && this.documentAttachType == 3) {
                int fromBackgroundWidth = this.backgroundWidth;
                int toBackgroundWidth = (int) ((this.backgroundWidth - this.transitionParams.toDeltaLeft) + this.transitionParams.toDeltaRight);
                int backgroundWidth = (int) ((this.backgroundWidth - this.transitionParams.deltaLeft) + this.transitionParams.deltaRight);
                this.seekBarWaveform.setSize(backgroundWidth + offset, AndroidUtilities.dp(30.0f), fromBackgroundWidth + offset, toBackgroundWidth + offset);
                return;
            }
            this.seekBarWaveform.setSize(this.backgroundWidth + offset, AndroidUtilities.dp(30.0f));
        }
    }

    private int createDocumentLayout(int maxWidth, MessageObject messageObject) {
        int maxWidth2;
        String name;
        int width;
        if (messageObject.type == 0) {
            this.documentAttach = messageObject.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.getDocument();
        }
        TLRPC.Document document = this.documentAttach;
        if (document == null) {
            return 0;
        }
        if (MessageObject.isVoiceDocument(document)) {
            this.documentAttachType = 3;
            int duration = 0;
            int a = 0;
            while (true) {
                if (a >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = this.documentAttach.attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                    a++;
                } else {
                    duration = attribute.duration;
                    break;
                }
            }
            this.widthBeforeNewTimeLine = (maxWidth - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil(Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = maxWidth - AndroidUtilities.dp(18.0f);
            measureTime(messageObject);
            int minSize = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                String timeString = AndroidUtilities.formatLongDuration(duration);
                int w = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(timeString));
                this.backgroundWidth = Math.min(maxWidth, minSize + w);
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return 0;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = 4;
            if (!messageObject.needDrawBluredPreview()) {
                updatePlayingMessageProgress();
                String str = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
                this.docTitleLayout = new StaticLayout(str, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            int maxWidth3 = maxWidth - AndroidUtilities.dp(92.0f);
            if (maxWidth3 < 0) {
                maxWidth3 = AndroidUtilities.dp(100.0f);
            }
            CharSequence stringFinal = TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, maxWidth3 - AndroidUtilities.dp(12.0f), TextUtils.TruncateAt.END);
            StaticLayout staticLayout = new StaticLayout(stringFinal, Theme.chat_audioTitlePaint, maxWidth3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.songLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil(this.songLayout.getLineLeft(0)));
            }
            CharSequence stringFinal2 = TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, maxWidth3, TextUtils.TruncateAt.END);
            StaticLayout staticLayout2 = new StaticLayout(stringFinal2, Theme.chat_audioPerformerPaint, maxWidth3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.performerLayout = staticLayout2;
            if (staticLayout2.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil(this.performerLayout.getLineLeft(0)));
            }
            int duration2 = 0;
            int a2 = 0;
            while (true) {
                if (a2 >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute2 = this.documentAttach.attributes.get(a2);
                if (!(attribute2 instanceof TLRPC.TL_documentAttributeAudio)) {
                    a2++;
                } else {
                    duration2 = attribute2.duration;
                    break;
                }
            }
            int durationWidth = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatShortDuration(duration2, duration2)));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - durationWidth;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return durationWidth;
        } else if (MessageObject.isGifDocument(this.documentAttach, messageObject.hasValidGroupId())) {
            this.documentAttachType = 2;
            if (!messageObject.needDrawBluredPreview()) {
                String str2 = LocaleController.getString("AttachGif", R.string.AttachGif);
                this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str2));
                this.infoLayout = new StaticLayout(str2, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                String str3 = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(str3));
                this.docTitleLayout = new StaticLayout(str3, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        } else {
            boolean z = (this.documentAttach.mime_type != null && (this.documentAttach.mime_type.toLowerCase().startsWith("image/") || this.documentAttach.mime_type.toLowerCase().startsWith(MimeTypes.VIDEO_MP4))) || MessageObject.isDocumentHasThumb(this.documentAttach);
            this.drawPhotoImage = z;
            if (z) {
                maxWidth2 = maxWidth;
            } else {
                maxWidth2 = maxWidth + AndroidUtilities.dp(30.0f);
            }
            this.documentAttachType = 1;
            String name2 = FileLoader.getDocumentFileName(this.documentAttach);
            if (name2.length() != 0) {
                name = name2;
            } else {
                name = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            }
            StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(name, Theme.chat_docNamePaint, maxWidth2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.MIDDLE, maxWidth2, 2, false);
            this.docTitleLayout = createStaticLayout;
            this.docTitleOffsetX = Integer.MIN_VALUE;
            if (createStaticLayout != null && createStaticLayout.getLineCount() > 0) {
                int maxLineWidth = 0;
                for (int a3 = 0; a3 < this.docTitleLayout.getLineCount(); a3++) {
                    maxLineWidth = Math.max(maxLineWidth, (int) Math.ceil(this.docTitleLayout.getLineWidth(a3)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil(-this.docTitleLayout.getLineLeft(a3)));
                }
                width = Math.min(maxWidth2, maxLineWidth);
            } else {
                int width2 = maxWidth2;
                this.docTitleOffsetX = 0;
                width = width2;
            }
            TextPaint textPaint = Theme.chat_infoPaint;
            this.infoWidth = Math.min(maxWidth2 - AndroidUtilities.dp(30.0f), (int) Math.ceil(textPaint.measureText("000.0 mm / " + AndroidUtilities.formatFileSize(this.documentAttach.size))));
            CharSequence str22 = TextUtils.ellipsize(AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach), Theme.chat_infoPaint, (float) this.infoWidth, TextUtils.TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(str22, Theme.chat_infoPaint, this.infoWidth + AndroidUtilities.dp(6.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, GroupCallActivity.TABLET_LIST_SIZE);
                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 40);
                if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                    this.currentPhotoObject = null;
                }
                TLRPC.PhotoSize photoSize = this.currentPhotoObject;
                if (photoSize == null || photoSize == this.currentPhotoObjectThumb) {
                    this.currentPhotoObject = null;
                    this.photoImage.setNeedsQualityThumb(true);
                    this.photoImage.setShouldGenerateQualityThumb(true);
                } else if (this.currentMessageObject.strippedThumb != null) {
                    this.currentPhotoObjectThumb = null;
                    this.currentPhotoObjectThumbStripped = this.currentMessageObject.strippedThumb;
                }
                this.currentPhotoFilter = "86_86_b";
                this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, messageObject.photoThumbsObject), "86_86", ImageLocation.getForObject(this.currentPhotoObjectThumb, messageObject.photoThumbsObject), this.currentPhotoFilter, this.currentPhotoObjectThumbStripped, 0L, null, messageObject, 1);
            }
            return width;
        }
    }

    private void calcBackgroundWidth(int maxWidth, int timeMore, int maxChildWidth) {
        boolean newLineForTime;
        int lastLineWidth = (this.reactionsLayoutInBubble.isEmpty || this.reactionsLayoutInBubble.isSmall) ? this.currentMessageObject.lastLineWidth : this.reactionsLayoutInBubble.lastLineX;
        boolean z = false;
        if (!this.reactionsLayoutInBubble.isEmpty && !this.reactionsLayoutInBubble.isSmall) {
            if (maxWidth - lastLineWidth < timeMore || this.currentMessageObject.hasRtl) {
                z = true;
            }
            newLineForTime = z;
            if (this.hasInvoicePreview) {
                this.totalHeight += AndroidUtilities.dp(14.0f);
            }
        } else {
            boolean newLineForTime2 = this.hasLinkPreview;
            if (newLineForTime2 || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview || maxWidth - lastLineWidth < timeMore || this.currentMessageObject.hasRtl) {
                z = true;
            }
            newLineForTime = z;
        }
        if (newLineForTime) {
            this.totalHeight += AndroidUtilities.dp(14.0f);
            this.hasNewLineForTime = true;
            int max = Math.max(maxChildWidth, lastLineWidth) + AndroidUtilities.dp(31.0f);
            this.backgroundWidth = max;
            this.backgroundWidth = Math.max(max, (this.currentMessageObject.isOutOwner() ? this.timeWidth + AndroidUtilities.dp(17.0f) : this.timeWidth) + AndroidUtilities.dp(31.0f));
            return;
        }
        int diff = (maxChildWidth - getExtraTextX()) - lastLineWidth;
        if (diff < 0 || diff > timeMore) {
            this.backgroundWidth = Math.max(maxChildWidth, lastLineWidth + timeMore) + AndroidUtilities.dp(31.0f);
        } else {
            this.backgroundWidth = ((maxChildWidth + timeMore) - diff) + AndroidUtilities.dp(31.0f);
        }
    }

    public void setHighlightedText(String text) {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        MessageObject messageObject2 = messageObject;
        if (messageObject2 == null || messageObject2.messageOwner.message == null || TextUtils.isEmpty(text)) {
            if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths();
                invalidate();
                return;
            }
            return;
        }
        String text2 = text.toLowerCase();
        String message = messageObject2.messageOwner.message.toLowerCase();
        int start = -1;
        int length = -1;
        int N1 = message.length();
        for (int a = 0; a < N1; a++) {
            int currentLen = 0;
            int N2 = Math.min(text2.length(), N1 - a);
            for (int b = 0; b < N2; b++) {
                boolean match = message.charAt(a + b) == text2.charAt(b);
                if (match) {
                    if (currentLen != 0 || a == 0 || " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(message.charAt(a - 1)) >= 0) {
                        currentLen++;
                    } else {
                        match = false;
                    }
                }
                if (!match || b == N2 - 1) {
                    if (currentLen > 0 && currentLen > length) {
                        length = currentLen;
                        start = a;
                    }
                }
            }
        }
        if (start == -1) {
            if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths();
                invalidate();
                return;
            }
            return;
        }
        int N = message.length();
        for (int a2 = start + length; a2 < N && " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n".indexOf(message.charAt(a2)) < 0; a2++) {
            length++;
        }
        int N3 = start + length;
        if (this.captionLayout != null && !TextUtils.isEmpty(messageObject2.caption)) {
            resetUrlPaths();
            try {
                LinkPath path = obtainNewUrlPath();
                path.setCurrentLayout(this.captionLayout, start, 0.0f);
                this.captionLayout.getSelectionPath(start, N3, path);
            } catch (Exception e) {
                FileLog.e(e);
            }
            invalidate();
        } else if (messageObject2.textLayoutBlocks != null) {
            for (int c = 0; c < messageObject2.textLayoutBlocks.size(); c++) {
                MessageObject.TextLayoutBlock block = messageObject2.textLayoutBlocks.get(c);
                if (start >= block.charactersOffset && start < block.charactersEnd) {
                    this.linkSelectionBlockNum = c;
                    resetUrlPaths();
                    try {
                        LinkPath path2 = obtainNewUrlPath();
                        path2.setCurrentLayout(block.textLayout, start, 0.0f);
                        block.textLayout.getSelectionPath(start, N3, path2);
                        if (N3 >= block.charactersOffset + length) {
                            for (int a3 = c + 1; a3 < messageObject2.textLayoutBlocks.size(); a3++) {
                                MessageObject.TextLayoutBlock nextBlock = messageObject2.textLayoutBlocks.get(a3);
                                int length2 = nextBlock.charactersEnd - nextBlock.charactersOffset;
                                LinkPath path3 = obtainNewUrlPath();
                                path3.setCurrentLayout(nextBlock.textLayout, 0, nextBlock.height);
                                nextBlock.textLayout.getSelectionPath(0, N3 - nextBlock.charactersOffset, path3);
                                if (N3 < (block.charactersOffset + length2) - 1) {
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
    protected boolean verifyDrawable(Drawable who) {
        if (!super.verifyDrawable(who)) {
            Drawable[] drawableArr = this.selectorDrawable;
            if (who != drawableArr[0] && who != drawableArr[1]) {
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
        boolean newExpired;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (newExpired = isCurrentLocationTimeExpired(messageObject)) != this.locationExpired) {
            this.locationExpired = newExpired;
            if (!newExpired) {
                AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
                this.scheduledInvalidate = true;
                int maxWidth = this.backgroundWidth - AndroidUtilities.dp(91.0f);
                this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, maxWidth, TextUtils.TruncateAt.END), Theme.chat_locationTitlePaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                return;
            }
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        }
    }

    public void setIsUpdating(boolean value) {
        this.isUpdating = true;
    }

    public void setMessageObject(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean bottomNear, boolean topNear) {
        if (this.attachedToWindow) {
            setMessageContent(messageObject, groupedMessages, bottomNear, topNear);
            return;
        }
        this.messageObjectToSet = messageObject;
        this.groupedMessagesToSet = groupedMessages;
        this.bottomNearToSet = bottomNear;
        this.topNearToSet = topNear;
    }

    private int getAdditionalWidthForPosition(MessageObject.GroupedMessagePosition position) {
        int w = 0;
        if (position == null) {
            return 0;
        }
        if ((position.flags & 2) == 0) {
            w = 0 + AndroidUtilities.dp(4.0f);
        }
        if ((position.flags & 1) == 0) {
            return w + AndroidUtilities.dp(4.0f);
        }
        return w;
    }

    public void createSelectorDrawable(final int num) {
        int color;
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        boolean z = this.psaHintPressed;
        String str = Theme.key_chat_outPreviewInstantText;
        if (z) {
            color = getThemedColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outViews : Theme.key_chat_inViews);
        } else {
            color = getThemedColor(this.currentMessageObject.isOutOwner() ? str : Theme.key_chat_inPreviewInstantText);
        }
        Drawable[] drawableArr = this.selectorDrawable;
        if (drawableArr[num] == null) {
            final Paint maskPaint = new Paint(1);
            maskPaint.setColor(-1);
            Drawable maskDrawable = new Drawable() { // from class: org.telegram.ui.Cells.ChatMessageCell.5
                RectF rect = new RectF();
                Path path = new Path();

                @Override // android.graphics.drawable.Drawable
                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    this.rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                    if (ChatMessageCell.this.selectorDrawableMaskType[num] != 3 && ChatMessageCell.this.selectorDrawableMaskType[num] != 4) {
                        float f = 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[num] == 2) {
                            this.path.reset();
                            boolean out = ChatMessageCell.this.currentMessageObject != null && ChatMessageCell.this.currentMessageObject.isOutOwner();
                            for (int a = 0; a < 4; a++) {
                                if (!ChatMessageCell.this.instantTextNewLine) {
                                    if (a == 2 && !out) {
                                        float dp = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                                        ChatMessageCell.radii[(a * 2) + 1] = dp;
                                        ChatMessageCell.radii[a * 2] = dp;
                                    } else if (a != 3 || !out) {
                                        if ((ChatMessageCell.this.mediaBackground || ChatMessageCell.this.pinnedBottom) && (a == 2 || a == 3)) {
                                            float[] fArr = ChatMessageCell.radii;
                                            int i = a * 2;
                                            float[] fArr2 = ChatMessageCell.radii;
                                            int i2 = (a * 2) + 1;
                                            float dp2 = AndroidUtilities.dp(ChatMessageCell.this.pinnedBottom ? Math.min(5, SharedConfig.bubbleRadius) : SharedConfig.bubbleRadius);
                                            fArr2[i2] = dp2;
                                            fArr[i] = dp2;
                                        }
                                    } else {
                                        float dp3 = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                                        ChatMessageCell.radii[(a * 2) + 1] = dp3;
                                        ChatMessageCell.radii[a * 2] = dp3;
                                    }
                                }
                                ChatMessageCell.radii[(a * 2) + 1] = 0.0f;
                                ChatMessageCell.radii[a * 2] = 0.0f;
                            }
                            this.path.addRoundRect(this.rect, ChatMessageCell.radii, Path.Direction.CW);
                            this.path.close();
                            canvas.drawPath(this.path, maskPaint);
                            return;
                        }
                        RectF rectF = this.rect;
                        float dp4 = ChatMessageCell.this.selectorDrawableMaskType[num] == 0 ? AndroidUtilities.dp(6.0f) : 0.0f;
                        if (ChatMessageCell.this.selectorDrawableMaskType[num] == 0) {
                            f = AndroidUtilities.dp(6.0f);
                        }
                        canvas.drawRoundRect(rectF, dp4, f, maskPaint);
                        return;
                    }
                    canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), AndroidUtilities.dp(ChatMessageCell.this.selectorDrawableMaskType[num] == 3 ? 16.0f : 20.0f), maskPaint);
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
            };
            int[][] iArr = {StateSet.WILD_CARD};
            int[] iArr2 = new int[1];
            if (!this.currentMessageObject.isOutOwner()) {
                str = Theme.key_chat_inPreviewInstantText;
            }
            iArr2[0] = getThemedColor(str) & 436207615;
            ColorStateList colorStateList = new ColorStateList(iArr, iArr2);
            this.selectorDrawable[num] = new RippleDrawable(colorStateList, null, maskDrawable);
            this.selectorDrawable[num].setCallback(this);
        } else {
            Theme.setSelectorDrawableColor(drawableArr[num], color & 436207615, true);
        }
        this.selectorDrawable[num].setVisible(true, false);
    }

    private void createInstantViewButton() {
        String str;
        int buttonWidth;
        if (Build.VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable(0);
        }
        if (this.drawInstantView && this.instantViewLayout == null) {
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
                TLRPC.TL_webPage webPage = (TLRPC.TL_webPage) this.currentMessageObject.messageOwner.media.webpage;
                if (webPage != null && webPage.url.contains("voicechat=")) {
                    str = LocaleController.getString("VoipGroupJoinAsSpeaker", R.string.VoipGroupJoinAsSpeaker);
                } else {
                    str = LocaleController.getString("VoipGroupJoinAsLinstener", R.string.VoipGroupJoinAsLinstener);
                }
            } else {
                str = LocaleController.getString("InstantView", R.string.InstantView);
            }
            if (this.currentMessageObject.isSponsored() && this.backgroundWidth < (buttonWidth = (int) (Theme.chat_instantViewPaint.measureText(str) + AndroidUtilities.dp(75.0f)))) {
                this.backgroundWidth = buttonWidth;
            }
            int mWidth = this.backgroundWidth - AndroidUtilities.dp(75.0f);
            this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, mWidth, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, mWidth + AndroidUtilities.dp(2.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.drawInstantViewType == 8) {
                this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(13.0f);
            } else {
                this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(34.0f);
            }
            this.totalHeight += AndroidUtilities.dp(46.0f);
            if (this.currentMessageObject.type == 12) {
                this.totalHeight += AndroidUtilities.dp(14.0f);
            }
            if (this.currentMessageObject.isSponsored() && this.hasNewLineForTime) {
                this.totalHeight += AndroidUtilities.dp(16.0f);
            }
            StaticLayout staticLayout = this.instantViewLayout;
            if (staticLayout != null && staticLayout.getLineCount() > 0) {
                double d = this.instantWidth;
                double ceil = Math.ceil(this.instantViewLayout.getLineWidth(0));
                Double.isNaN(d);
                this.instantTextX = (((int) (d - ceil)) / 2) + (this.drawInstantViewType == 0 ? AndroidUtilities.dp(8.0f) : 0);
                int lineLeft = (int) this.instantViewLayout.getLineLeft(0);
                this.instantTextLeftX = lineLeft;
                this.instantTextX += -lineLeft;
            }
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.inLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (messageObject.checkLayout() || this.lastHeight != AndroidUtilities.displaySize.y)) {
            this.inLayout = true;
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            this.inLayout = false;
        }
        updateSelectionTextPosition();
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), this.totalHeight + this.keyboardHeight);
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
        int width = getParentWidth();
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.preview) {
            width = this.parentWidth;
        }
        if (!AndroidUtilities.isInMultiwindow && AndroidUtilities.isTablet() && (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == 2)) {
            int leftWidth = (width / 100) * 35;
            if (leftWidth < AndroidUtilities.dp(320.0f)) {
                leftWidth = AndroidUtilities.dp(320.0f);
            }
            return width - leftWidth;
        }
        return width;
    }

    private int getExtraTextX() {
        if (SharedConfig.bubbleRadius >= 15) {
            return AndroidUtilities.dp(2.0f);
        }
        if (SharedConfig.bubbleRadius >= 11) {
            return AndroidUtilities.dp(1.0f);
        }
        return 0;
    }

    private int getExtraTimeX() {
        if (!this.currentMessageObject.isOutOwner() && ((!this.mediaBackground || this.captionLayout != null) && SharedConfig.bubbleRadius > 11)) {
            return AndroidUtilities.dp((SharedConfig.bubbleRadius - 11) / 1.5f);
        }
        if (!this.currentMessageObject.isOutOwner() && this.isPlayingRound && this.isAvatarVisible && this.currentMessageObject.type == 5) {
            return (int) ((AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f);
        }
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int x;
        int linkX;
        int x2;
        int i;
        if (this.currentMessageObject == null) {
            return;
        }
        int currentSize = getMeasuredHeight() + (getMeasuredWidth() << 16);
        int i2 = 10;
        if (this.lastSize != currentSize || !this.wasLayout) {
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
                    if (groupedMessagePosition != null && groupedMessagePosition.leftSpanOffset != 0) {
                        this.timeX += (int) Math.ceil((this.currentPosition.leftSpanOffset / 1000.0f) * getGroupPhotosWidth());
                    }
                    if (this.captionLayout != null && this.currentPosition != null) {
                        this.timeX += AndroidUtilities.dp(4.0f);
                    }
                }
                if (SharedConfig.bubbleRadius >= 10 && this.captionLayout == null && (i = this.documentAttachType) != 7 && i != 6) {
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
        this.lastSize = currentSize;
        if (this.currentMessageObject.type == 0) {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (this.isRoundVideo) {
            updatePlayingMessageProgress();
        }
        int i3 = this.documentAttachType;
        if (i3 == 3) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(57.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (!this.isChat || this.isThreadPost || !this.currentMessageObject.needDrawAvatar()) {
                this.seekBarX = AndroidUtilities.dp(66.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(114.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            updateSeekBarWaveformWidth();
            SeekBar seekBar = this.seekBar;
            int i4 = this.backgroundWidth;
            if (!this.hasLinkPreview) {
                i2 = 0;
            }
            seekBar.setSize(i4 - AndroidUtilities.dp(i2 + 72), AndroidUtilities.dp(30.0f));
            this.seekBarY = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            int dp = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp;
            RadialProgress2 radialProgress2 = this.radialProgress;
            int i5 = this.buttonX;
            radialProgress2.setProgressRect(i5, dp, AndroidUtilities.dp(44.0f) + i5, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i3 == 5) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(56.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (!this.isChat || this.isThreadPost || !this.currentMessageObject.needDrawAvatar()) {
                this.seekBarX = AndroidUtilities.dp(65.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(113.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            SeekBar seekBar2 = this.seekBar;
            int i6 = this.backgroundWidth;
            if (!this.hasLinkPreview) {
                i2 = 0;
            }
            seekBar2.setSize(i6 - AndroidUtilities.dp(i2 + 65), AndroidUtilities.dp(30.0f));
            this.seekBarY = AndroidUtilities.dp(29.0f) + this.namesOffset + this.mediaOffsetY;
            int dp2 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp2;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i7 = this.buttonX;
            radialProgress22.setProgressRect(i7, dp2, AndroidUtilities.dp(44.0f) + i7, this.buttonY + AndroidUtilities.dp(44.0f));
            updatePlayingMessageProgress();
        } else if (i3 == 1 && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (!this.isChat || this.isThreadPost || !this.currentMessageObject.needDrawAvatar()) {
                this.buttonX = AndroidUtilities.dp(23.0f);
            } else {
                this.buttonX = AndroidUtilities.dp(71.0f);
            }
            if (this.hasLinkPreview) {
                this.buttonX += AndroidUtilities.dp(10.0f);
            }
            int dp3 = AndroidUtilities.dp(13.0f) + this.namesOffset + this.mediaOffsetY;
            this.buttonY = dp3;
            RadialProgress2 radialProgress23 = this.radialProgress;
            int i8 = this.buttonX;
            radialProgress23.setProgressRect(i8, dp3, AndroidUtilities.dp(44.0f) + i8, this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else if (this.currentMessageObject.type == 12) {
            if (this.currentMessageObject.isOutOwner()) {
                x2 = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (this.isChat && !this.isThreadPost && this.currentMessageObject.needDrawAvatar()) {
                x2 = AndroidUtilities.dp(72.0f);
            } else {
                x2 = AndroidUtilities.dp(23.0f);
            }
            this.photoImage.setImageCoords(x2, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
        } else {
            if (this.currentMessageObject.type == 0 && (this.hasLinkPreview || this.hasGamePreview || this.hasInvoicePreview)) {
                if (this.hasGamePreview) {
                    linkX = this.unmovedTextX - AndroidUtilities.dp(10.0f);
                } else if (this.hasInvoicePreview) {
                    linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
                } else {
                    linkX = this.unmovedTextX + AndroidUtilities.dp(1.0f);
                }
                if (this.isSmallImage) {
                    x = (this.backgroundWidth + linkX) - AndroidUtilities.dp(81.0f);
                } else {
                    x = (this.hasInvoicePreview ? -AndroidUtilities.dp(6.3f) : AndroidUtilities.dp(10.0f)) + linkX;
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                x = this.mediaBackground ? (this.layoutWidth - this.backgroundWidth) - AndroidUtilities.dp(3.0f) : AndroidUtilities.dp(6.0f) + (this.layoutWidth - this.backgroundWidth);
            } else {
                if (this.isChat && this.isAvatarVisible && !this.isPlayingRound) {
                    x = AndroidUtilities.dp(63.0f);
                } else {
                    x = AndroidUtilities.dp(15.0f);
                }
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
                if (groupedMessagePosition2 != null && !groupedMessagePosition2.edge) {
                    x -= AndroidUtilities.dp(10.0f);
                }
            }
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
            if (groupedMessagePosition3 != null) {
                if ((groupedMessagePosition3.flags & 1) == 0) {
                    x -= AndroidUtilities.dp(2.0f);
                }
                if (this.currentPosition.leftSpanOffset != 0) {
                    x += (int) Math.ceil((this.currentPosition.leftSpanOffset / 1000.0f) * getGroupPhotosWidth());
                }
            }
            if (this.currentMessageObject.type != 0) {
                x -= AndroidUtilities.dp(2.0f);
            }
            if (!this.transitionParams.imageChangeBoundsTransition || this.transitionParams.updatePhotoImageX) {
                this.transitionParams.updatePhotoImageX = false;
                ImageReceiver imageReceiver = this.photoImage;
                imageReceiver.setImageCoords(x, imageReceiver.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            }
            this.buttonX = (int) (x + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY;
            RadialProgress2 radialProgress24 = this.radialProgress;
            int i9 = this.buttonX;
            radialProgress24.setProgressRect(i9, imageY, AndroidUtilities.dp(48.0f) + i9, this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(5.0f), this.buttonY + AndroidUtilities.dp(5.0f), this.buttonX + AndroidUtilities.dp(43.0f), this.buttonY + AndroidUtilities.dp(43.0f));
            int i10 = this.documentAttachType;
            if (i10 == 4 || i10 == 2) {
                this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
                int imageY2 = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
                this.videoButtonY = imageY2;
                RadialProgress2 radialProgress25 = this.videoRadialProgress;
                int i11 = this.videoButtonX;
                radialProgress25.setProgressRect(i11, imageY2, AndroidUtilities.dp(24.0f) + i11, this.videoButtonY + AndroidUtilities.dp(24.0f));
            }
        }
    }

    public boolean needDelayRoundProgressDraw() {
        int i = this.documentAttachType;
        return (i == 7 || i == 4) && this.currentMessageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x006b  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0096  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x009f  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00a8  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00d1  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0130  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0143  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0148  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0199  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0262  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x026b  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0283  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x02cd  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x02e1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawRoundProgress(android.graphics.Canvas r22) {
        /*
            Method dump skipped, instructions count: 748
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawRoundProgress(android.graphics.Canvas):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x00b8  */
    /* JADX WARN: Removed duplicated region for block: B:62:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updatePollAnimations(long r10) {
        /*
            Method dump skipped, instructions count: 279
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updatePollAnimations(long):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:368:0x0874  */
    /* JADX WARN: Removed duplicated region for block: B:375:0x08c2  */
    /* JADX WARN: Removed duplicated region for block: B:383:0x0940  */
    /* JADX WARN: Removed duplicated region for block: B:386:0x0966  */
    /* JADX WARN: Removed duplicated region for block: B:517:0x0c3f  */
    /* JADX WARN: Removed duplicated region for block: B:560:0x0d74  */
    /* JADX WARN: Removed duplicated region for block: B:563:0x0d79  */
    /* JADX WARN: Removed duplicated region for block: B:699:0x1126  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawContent(android.graphics.Canvas r33) {
        /*
            Method dump skipped, instructions count: 4509
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawContent(android.graphics.Canvas):void");
    }

    public void updateReactionLayoutPosition() {
        int timeYOffset;
        float f;
        ReactionsLayoutInBubble reactionsLayoutInBubble;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        int i = 0;
        if (!this.reactionsLayoutInBubble.isEmpty && (((groupedMessagePosition = this.currentPosition) == null || ((groupedMessagePosition.flags & 8) != 0 && (this.currentPosition.flags & 1) != 0)) && !this.reactionsLayoutInBubble.isSmall)) {
            float f2 = 11.0f;
            if (this.currentMessageObject.isOutOwner()) {
                this.reactionsLayoutInBubble.x = getCurrentBackgroundLeft() + AndroidUtilities.dp(11.0f);
            } else {
                ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
                int currentBackgroundLeft = getCurrentBackgroundLeft();
                if (this.mediaBackground || !this.drawPinnedBottom) {
                    f2 = 17.0f;
                }
                reactionsLayoutInBubble2.x = currentBackgroundLeft + AndroidUtilities.dp(f2);
                if (this.mediaBackground) {
                    this.reactionsLayoutInBubble.x -= AndroidUtilities.dp(9.0f);
                }
            }
            this.reactionsLayoutInBubble.y = (getBackgroundDrawableBottom() - AndroidUtilities.dp(10.0f)) - this.reactionsLayoutInBubble.height;
            this.reactionsLayoutInBubble.y -= this.drawCommentButton ? AndroidUtilities.dp(43.0f) : 0;
            if (this.hasNewLineForTime) {
                this.reactionsLayoutInBubble.y -= AndroidUtilities.dp(16.0f);
            }
            if (this.captionLayout != null && ((this.currentMessageObject.type != 2 && ((!this.currentMessageObject.isOut() || !this.drawForwardedName || this.drawPhotoImage) && (this.currentMessageObject.type != 9 || !this.drawPhotoImage))) || (this.currentPosition != null && this.currentMessagesGroup != null))) {
                this.reactionsLayoutInBubble.y -= AndroidUtilities.dp(14.0f);
            }
            this.reactionsLayoutInBubble.y += this.reactionsLayoutInBubble.positionOffsetY;
        }
        if (this.reactionsLayoutInBubble.isSmall && !this.reactionsLayoutInBubble.isEmpty) {
            if (shouldDrawTimeOnMedia()) {
                if (this.drawCommentButton) {
                    i = AndroidUtilities.dp(41.3f);
                }
                timeYOffset = -i;
            } else if (this.currentMessageObject.isSponsored()) {
                timeYOffset = -AndroidUtilities.dp(48.0f);
                if (this.hasNewLineForTime) {
                    timeYOffset -= AndroidUtilities.dp(16.0f);
                }
            } else {
                if (this.drawCommentButton) {
                    i = AndroidUtilities.dp(43.0f);
                }
                timeYOffset = -i;
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
            if (shouldDrawTimeOnMedia()) {
                f = ((this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(7.3f)) - this.timeLayout.getHeight();
            } else {
                f = ((this.layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 7.5f : 6.5f)) - this.timeLayout.getHeight()) + timeYOffset;
            }
            reactionsLayoutInBubble3.y = (int) f;
            this.reactionsLayoutInBubble.y = (int) (reactionsLayoutInBubble.y + ((this.timeLayout.getHeight() / 2.0f) - AndroidUtilities.dp(7.0f)));
            this.reactionsLayoutInBubble.x = this.timeX;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:255:0x087d  */
    /* JADX WARN: Type inference failed for: r11v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r11v43 */
    /* JADX WARN: Type inference failed for: r11v52 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawLinkPreview(android.graphics.Canvas r33, float r34) {
        /*
            Method dump skipped, instructions count: 2439
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawLinkPreview(android.graphics.Canvas, float):void");
    }

    public boolean shouldDrawMenuDrawable() {
        return this.currentMessagesGroup == null || (this.currentPosition.flags & 4) != 0;
    }

    private void drawBotButtons(Canvas canvas, ArrayList<BotButton> botButtons, float alpha) {
        int addX;
        int a;
        float y;
        BotButton button;
        Drawable drawable;
        ChatMessageCell chatMessageCell = this;
        if (chatMessageCell.currentMessageObject.isOutOwner()) {
            addX = (getMeasuredWidth() - chatMessageCell.widthForButtons) - AndroidUtilities.dp(10.0f);
        } else {
            addX = chatMessageCell.backgroundDrawableLeft + AndroidUtilities.dp((chatMessageCell.mediaBackground || chatMessageCell.drawPinnedBottom) ? 1.0f : 7.0f);
        }
        float f = 2.0f;
        float top = (chatMessageCell.layoutHeight - AndroidUtilities.dp(2.0f)) + chatMessageCell.transitionParams.deltaBottom;
        float height = 0.0f;
        for (int a2 = 0; a2 < botButtons.size(); a2++) {
            BotButton button2 = botButtons.get(a2);
            int bottom = button2.y + button2.height;
            if (bottom > height) {
                height = bottom;
            }
        }
        float f2 = 0.0f;
        chatMessageCell.rect.set(0.0f, top, getMeasuredWidth(), top + height);
        int a3 = 1132396544;
        if (alpha != 1.0f) {
            canvas.saveLayerAlpha(chatMessageCell.rect, (int) (alpha * 255.0f), 31);
        } else {
            canvas.save();
        }
        int a4 = 0;
        while (a4 < botButtons.size()) {
            BotButton button3 = botButtons.get(a4);
            float y2 = ((button3.y + chatMessageCell.layoutHeight) - AndroidUtilities.dp(f)) + chatMessageCell.transitionParams.deltaBottom;
            chatMessageCell.rect.set(button3.x + addX, y2, button3.x + addX + button3.width, button3.height + y2);
            applyServiceShaderMatrix();
            canvas.drawRoundRect(chatMessageCell.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), chatMessageCell.getThemedPaint(a4 == chatMessageCell.pressedBotButton ? Theme.key_paint_chatActionBackgroundSelected : Theme.key_paint_chatActionBackground));
            if (hasGradientService()) {
                canvas.drawRoundRect(chatMessageCell.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            canvas.save();
            boolean z = true;
            canvas.translate(button3.x + addX + AndroidUtilities.dp(5.0f), ((AndroidUtilities.dp(44.0f) - button3.title.getLineBottom(button3.title.getLineCount() - 1)) / 2) + y2);
            button3.title.draw(canvas);
            canvas.restore();
            if (!(button3.button instanceof TLRPC.TL_keyboardButtonWebView)) {
                if (button3.button instanceof TLRPC.TL_keyboardButtonUrl) {
                    if (button3.isInviteButton) {
                        drawable = chatMessageCell.getThemedDrawable(Theme.key_drawable_botInvite);
                    } else {
                        drawable = chatMessageCell.getThemedDrawable(Theme.key_drawable_botLink);
                    }
                    setDrawableBounds(drawable, (((button3.x + button3.width) - AndroidUtilities.dp(3.0f)) - drawable.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y2);
                    drawable.draw(canvas);
                    a = a4;
                } else {
                    if (!(button3.button instanceof TLRPC.TL_keyboardButtonSwitchInline)) {
                        if ((button3.button instanceof TLRPC.TL_keyboardButtonCallback) || (button3.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) || (button3.button instanceof TLRPC.TL_keyboardButtonGame) || (button3.button instanceof TLRPC.TL_keyboardButtonBuy) || (button3.button instanceof TLRPC.TL_keyboardButtonUrlAuth)) {
                            if (button3.button instanceof TLRPC.TL_keyboardButtonBuy) {
                                setDrawableBounds(Theme.chat_botCardDrawable, (((button3.x + button3.width) - AndroidUtilities.dp(5.0f)) - Theme.chat_botCardDrawable.getIntrinsicWidth()) + addX, AndroidUtilities.dp(4.0f) + y2);
                                Theme.chat_botCardDrawable.draw(canvas);
                            }
                            if (((!(button3.button instanceof TLRPC.TL_keyboardButtonCallback) && !(button3.button instanceof TLRPC.TL_keyboardButtonGame) && !(button3.button instanceof TLRPC.TL_keyboardButtonBuy) && !(button3.button instanceof TLRPC.TL_keyboardButtonUrlAuth)) || !SendMessagesHelper.getInstance(chatMessageCell.currentAccount).isSendingCallback(chatMessageCell.currentMessageObject, button3.button)) && (!(button3.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) || !SendMessagesHelper.getInstance(chatMessageCell.currentAccount).isSendingCurrentLocation(chatMessageCell.currentMessageObject, button3.button))) {
                                z = false;
                            }
                            boolean drawProgress = z;
                            if (drawProgress || button3.progressAlpha != f2) {
                                Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int) (button3.progressAlpha * a3)));
                                int x = ((button3.x + button3.width) - AndroidUtilities.dp(12.0f)) + addX;
                                if (!(button3.button instanceof TLRPC.TL_keyboardButtonBuy)) {
                                    y = y2;
                                } else {
                                    y = y2 + AndroidUtilities.dp(26.0f);
                                }
                                chatMessageCell.rect.set(x, y + AndroidUtilities.dp(4.0f), x + AndroidUtilities.dp(8.0f), y + AndroidUtilities.dp(12.0f));
                                a = a4;
                                canvas.drawArc(chatMessageCell.rect, button3.angle, 220.0f, false, Theme.chat_botProgressPaint);
                                invalidate();
                                long newTime = System.currentTimeMillis();
                                if (Math.abs(button3.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                                    long delta = newTime - button3.lastUpdateTime;
                                    float dt = ((float) (360 * delta)) / 2000.0f;
                                    button = button3;
                                    BotButton.access$2816(button, dt);
                                    BotButton.access$2820(button, (button.angle / 360) * 360);
                                    if (drawProgress) {
                                        if (button.progressAlpha < 1.0f) {
                                            BotButton.access$2716(button, ((float) delta) / 200.0f);
                                            if (button.progressAlpha > 1.0f) {
                                                button.progressAlpha = 1.0f;
                                            }
                                        }
                                    } else if (button.progressAlpha > 0.0f) {
                                        BotButton.access$2724(button, ((float) delta) / 200.0f);
                                        if (button.progressAlpha < 0.0f) {
                                            button.progressAlpha = 0.0f;
                                        }
                                    }
                                } else {
                                    button = button3;
                                }
                                button.lastUpdateTime = newTime;
                            } else {
                                a = a4;
                            }
                        }
                    } else {
                        Drawable drawable2 = chatMessageCell.getThemedDrawable(Theme.key_drawable_botInline);
                        setDrawableBounds(drawable2, (((button3.x + button3.width) - AndroidUtilities.dp(3.0f)) - drawable2.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y2);
                        drawable2.draw(canvas);
                    }
                    a = a4;
                }
            } else {
                Drawable drawable3 = chatMessageCell.getThemedDrawable(Theme.key_drawable_botWebView);
                setDrawableBounds(drawable3, (((button3.x + button3.width) - AndroidUtilities.dp(3.0f)) - drawable3.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y2);
                drawable3.draw(canvas);
                a = a4;
            }
            a4 = a + 1;
            f = 2.0f;
            f2 = 0.0f;
            a3 = 1132396544;
            chatMessageCell = this;
        }
        canvas.restore();
    }

    public void drawMessageText(Canvas canvas, ArrayList<MessageObject.TextLayoutBlock> textLayoutBlocks, boolean origin, float alpha, boolean drawOnlyText) {
        int firstVisibleBlockNum;
        int lastVisibleBlockNum;
        float textY;
        boolean needRestoreColor;
        int oldAlpha;
        int restore;
        int oldLinkAlpha;
        int oldLinkAlpha2;
        int oldLinkAlpha3;
        int a;
        int lastVisibleBlockNum2;
        Exception e;
        if (textLayoutBlocks == null || textLayoutBlocks.isEmpty() || alpha == 0.0f) {
            return;
        }
        if (origin) {
            if (this.fullyDraw) {
                this.firstVisibleBlockNum = 0;
                this.lastVisibleBlockNum = textLayoutBlocks.size();
            }
            firstVisibleBlockNum = this.firstVisibleBlockNum;
            lastVisibleBlockNum = this.lastVisibleBlockNum;
        } else {
            firstVisibleBlockNum = 0;
            lastVisibleBlockNum = textLayoutBlocks.size();
        }
        float textY2 = this.textY;
        if (this.transitionParams.animateText) {
            float textY3 = (this.transitionParams.animateFromTextY * (1.0f - this.transitionParams.animateChangeProgress)) + (this.textY * this.transitionParams.animateChangeProgress);
            textY = textY3;
        } else {
            textY = textY2;
        }
        if (firstVisibleBlockNum >= 0) {
            if (alpha == 1.0f) {
                restore = Integer.MIN_VALUE;
                oldAlpha = 0;
                oldLinkAlpha = 0;
                needRestoreColor = false;
            } else if (drawOnlyText) {
                int oldAlpha2 = Theme.chat_msgTextPaint.getAlpha();
                int oldLinkAlpha4 = Color.alpha(Theme.chat_msgTextPaint.linkColor);
                Theme.chat_msgTextPaint.setAlpha((int) (oldAlpha2 * alpha));
                Theme.chat_msgTextPaint.linkColor = ColorUtils.setAlphaComponent(Theme.chat_msgTextPaint.linkColor, (int) (oldLinkAlpha4 * alpha));
                restore = Integer.MIN_VALUE;
                oldAlpha = oldAlpha2;
                oldLinkAlpha = oldLinkAlpha4;
                needRestoreColor = true;
            } else {
                Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
                if (messageDrawable != null) {
                    int top = messageDrawable.getBounds().top;
                    int bottom = this.currentBackgroundDrawable.getBounds().bottom;
                    if (getY() < 0.0f) {
                        top = (int) (-getY());
                    }
                    float y = getY() + getMeasuredHeight();
                    int i = this.parentHeight;
                    if (y > i) {
                        bottom = (int) (i - getY());
                    }
                    this.rect.set(getCurrentBackgroundLeft(), top, this.currentBackgroundDrawable.getBounds().right, bottom);
                } else {
                    this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                }
                restore = canvas.saveLayerAlpha(this.rect, (int) (255.0f * alpha), 31);
                oldAlpha = 0;
                oldLinkAlpha = 0;
                needRestoreColor = false;
            }
            int spoilersColor = (!this.currentMessageObject.isOut() || ChatObject.isChannelAndNotMegaGroup(this.currentMessageObject.getChatId(), this.currentAccount)) ? Theme.chat_msgTextPaint.getColor() : getThemedColor(Theme.key_chat_outTimeText);
            int a2 = firstVisibleBlockNum;
            while (true) {
                if (a2 > lastVisibleBlockNum) {
                    oldLinkAlpha2 = oldLinkAlpha;
                    break;
                } else if (a2 >= textLayoutBlocks.size()) {
                    oldLinkAlpha2 = oldLinkAlpha;
                    break;
                } else {
                    MessageObject.TextLayoutBlock block = textLayoutBlocks.get(a2);
                    canvas.save();
                    canvas.translate(this.textX - (block.isRtl() ? (int) Math.ceil(this.currentMessageObject.textXOffset) : 0), textY + block.textYOffset + this.transitionYOffsetForDrawables);
                    if (a2 == this.linkBlockNum && !drawOnlyText && this.links.draw(canvas)) {
                        invalidate();
                    }
                    if (a2 == this.linkSelectionBlockNum && !this.urlPathSelection.isEmpty() && !drawOnlyText) {
                        for (int b = 0; b < this.urlPathSelection.size(); b++) {
                            canvas.drawPath(this.urlPathSelection.get(b), Theme.chat_textSearchSelectionPaint);
                        }
                    }
                    if (this.delegate.getTextSelectionHelper() != null && this.transitionParams.animateChangeProgress == 1.0f && !drawOnlyText) {
                        this.delegate.getTextSelectionHelper().draw(this.currentMessageObject, block, canvas);
                    }
                    try {
                        Emoji.emojiDrawingYOffset = -this.transitionYOffsetForDrawables;
                        a = a2;
                        oldLinkAlpha3 = oldLinkAlpha;
                        lastVisibleBlockNum2 = lastVisibleBlockNum;
                        try {
                            SpoilerEffect.renderWithRipple(this, this.invalidateSpoilersParent, spoilersColor, 0, block.spoilersPatchedTextLayout, block.textLayout, block.spoilers, canvas, false);
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        a = a2;
                        oldLinkAlpha3 = oldLinkAlpha;
                        lastVisibleBlockNum2 = lastVisibleBlockNum;
                    }
                    try {
                        Emoji.emojiDrawingYOffset = 0.0f;
                    } catch (Exception e4) {
                        e = e4;
                        FileLog.e(e);
                        canvas.restore();
                        a2 = a + 1;
                        lastVisibleBlockNum = lastVisibleBlockNum2;
                        oldLinkAlpha = oldLinkAlpha3;
                    }
                    canvas.restore();
                    a2 = a + 1;
                    lastVisibleBlockNum = lastVisibleBlockNum2;
                    oldLinkAlpha = oldLinkAlpha3;
                }
            }
            if (needRestoreColor) {
                Theme.chat_msgTextPaint.setAlpha(oldAlpha);
                Theme.chat_msgTextPaint.linkColor = ColorUtils.setAlphaComponent(Theme.chat_msgTextPaint.linkColor, oldLinkAlpha2);
            }
            if (restore != Integer.MIN_VALUE) {
                canvas.restoreToCount(restore);
            }
        }
    }

    public void updateCaptionLayout() {
        float h;
        float y;
        float x;
        if (this.currentMessageObject.type == 1 || this.documentAttachType == 4 || this.currentMessageObject.type == 8) {
            if (this.transitionParams.imageChangeBoundsTransition) {
                x = this.transitionParams.animateToImageX;
                y = this.transitionParams.animateToImageY;
                h = this.transitionParams.animateToImageH;
            } else {
                x = this.photoImage.getImageX();
                y = this.photoImage.getImageY();
                h = this.photoImage.getImageHeight();
            }
            this.captionX = AndroidUtilities.dp(5.0f) + x + this.captionOffsetX;
            this.captionY = y + h + AndroidUtilities.dp(6.0f);
        } else {
            float f = 41.3f;
            float f2 = 9.0f;
            float f3 = 11.0f;
            if (this.hasOldCaptionPreview) {
                int i = this.backgroundDrawableLeft;
                if (!this.currentMessageObject.isOutOwner()) {
                    f3 = 17.0f;
                }
                this.captionX = i + AndroidUtilities.dp(f3) + this.captionOffsetX;
                int i2 = this.totalHeight - this.captionHeight;
                if (!this.drawPinnedTop) {
                    f2 = 10.0f;
                }
                float dp = ((i2 - AndroidUtilities.dp(f2)) - this.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                this.captionY = dp;
                if (this.drawCommentButton && this.drawSideButton != 3) {
                    if (!shouldDrawTimeOnMedia()) {
                        f = 43.0f;
                    }
                    this.captionY = dp - AndroidUtilities.dp(f);
                }
            } else {
                int i3 = this.backgroundDrawableLeft;
                if (!this.currentMessageObject.isOutOwner() && !this.mediaBackground && !this.drawPinnedBottom) {
                    f3 = 17.0f;
                }
                this.captionX = i3 + AndroidUtilities.dp(f3) + this.captionOffsetX;
                int i4 = this.totalHeight - this.captionHeight;
                if (!this.drawPinnedTop) {
                    f2 = 10.0f;
                }
                float dp2 = i4 - AndroidUtilities.dp(f2);
                this.captionY = dp2;
                if (this.drawCommentButton && this.drawSideButton != 3) {
                    if (!shouldDrawTimeOnMedia()) {
                        f = 43.0f;
                    }
                    this.captionY = dp2 - AndroidUtilities.dp(f);
                }
                if (!this.reactionsLayoutInBubble.isEmpty && !this.reactionsLayoutInBubble.isSmall) {
                    this.captionY -= this.reactionsLayoutInBubble.totalHeight;
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
        if (i == 0) {
            return 2;
        }
        return 3;
    }

    public int getIconForCurrentState() {
        int i = this.documentAttachType;
        if (i != 3 && i != 5) {
            if (i == 1 && !this.drawPhotoImage) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.radialProgress.setColors(Theme.key_chat_outLoader, Theme.key_chat_outLoaderSelected, Theme.key_chat_outMediaIcon, Theme.key_chat_outMediaIconSelected);
                } else {
                    this.radialProgress.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
                }
                int i2 = this.buttonState;
                if (i2 == -1) {
                    return 5;
                }
                if (i2 == 0) {
                    return 2;
                }
                if (i2 == 1) {
                    return 3;
                }
            } else {
                this.radialProgress.setColors(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
                this.videoRadialProgress.setColors(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
                int i3 = this.buttonState;
                if (i3 >= 0 && i3 < 4) {
                    if (i3 == 0) {
                        return 2;
                    }
                    if (i3 == 1) {
                        return 3;
                    }
                    return (i3 != 2 && this.autoPlayingMedia) ? 4 : 0;
                } else if (i3 == -1) {
                    if (this.documentAttachType == 1) {
                        return (!this.drawPhotoImage || (this.currentPhotoObject == null && this.currentPhotoObjectThumb == null) || (!this.photoImage.hasBitmapImage() && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists)) ? 5 : 4;
                    } else if (this.currentMessageObject.needDrawBluredPreview()) {
                        if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                            if (this.currentMessageObject.isOutOwner()) {
                                return 9;
                            }
                            return 11;
                        }
                        return 7;
                    } else if (this.hasEmbed) {
                        return 0;
                    }
                }
            }
            return 4;
        }
        if (this.currentMessageObject.isOutOwner()) {
            this.radialProgress.setColors(Theme.key_chat_outLoader, Theme.key_chat_outLoaderSelected, Theme.key_chat_outMediaIcon, Theme.key_chat_outMediaIconSelected);
        } else {
            this.radialProgress.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
        }
        int i4 = this.buttonState;
        if (i4 == 1) {
            return 1;
        }
        if (i4 == 2) {
            return 2;
        }
        return i4 == 4 ? 3 : 0;
    }

    private int getMaxNameWidth() {
        int maxWidth;
        int dWidth;
        int i = this.documentAttachType;
        if (i == 6 || i == 8 || this.currentMessageObject.type == 5) {
            if (AndroidUtilities.isTablet()) {
                if (this.isChat && !this.isThreadPost && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0f);
                } else {
                    maxWidth = AndroidUtilities.getMinTabletSide();
                }
            } else if (this.isChat && !this.isThreadPost && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                maxWidth = Math.min(getParentWidth(), AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0f);
            } else {
                maxWidth = Math.min(getParentWidth(), AndroidUtilities.displaySize.y);
            }
            if (this.isPlayingRound) {
                int backgroundWidthLocal = this.backgroundWidth - (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize);
                return (maxWidth - backgroundWidthLocal) - AndroidUtilities.dp(57.0f);
            }
            int backgroundWidthLocal2 = this.backgroundWidth;
            return (maxWidth - backgroundWidthLocal2) - AndroidUtilities.dp(57.0f);
        }
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments) {
            if (AndroidUtilities.isTablet()) {
                dWidth = AndroidUtilities.getMinTabletSide();
            } else {
                dWidth = getParentWidth();
            }
            int firstLineWidth = 0;
            for (int a = 0; a < this.currentMessagesGroup.posArray.size(); a++) {
                MessageObject.GroupedMessagePosition position = this.currentMessagesGroup.posArray.get(a);
                if (position.minY != 0) {
                    break;
                }
                double d = firstLineWidth;
                double ceil = Math.ceil(((position.pw + position.leftSpanOffset) / 1000.0f) * dWidth);
                Double.isNaN(d);
                firstLineWidth = (int) (d + ceil);
            }
            return firstLineWidth - AndroidUtilities.dp((this.isAvatarVisible ? 48 : 0) + 31);
        }
        int dWidth2 = this.backgroundWidth;
        return dWidth2 - AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
    }

    /* JADX WARN: Removed duplicated region for block: B:510:0x08cf  */
    /* JADX WARN: Removed duplicated region for block: B:516:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateButtonState(boolean r19, boolean r20, boolean r21) {
        /*
            Method dump skipped, instructions count: 2287
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateButtonState(boolean, boolean, boolean):void");
    }

    private void didPressMiniButton(boolean animated) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null && !messageObject.isAnyKindOfSticker()) {
                this.currentMessageObject.putInDownloadsStore = true;
            }
            int i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                this.currentMessageObject.loadingCancelled = false;
            } else if (i2 == 4) {
                createLoadingProgressLayout(this.documentAttach);
                FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
                TLRPC.Document document = this.documentAttach;
                MessageObject messageObject2 = this.currentMessageObject;
                fileLoader.loadFile(document, messageObject2, 1, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 0);
                this.currentMessageObject.loadingCancelled = false;
            }
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 1) {
            int i3 = this.documentAttachType;
            if ((i3 == 3 || i3 == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            this.currentMessageObject.loadingCancelled = true;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    private void didPressButton(boolean animated, boolean video) {
        String thumbFilter;
        TLRPC.PhotoSize thumb;
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isAnyKindOfSticker()) {
            this.currentMessageObject.putInDownloadsStore = true;
        }
        int i2 = this.buttonState;
        int i3 = 2;
        if (i2 != 0 || (this.drawVideoImageButton && !video)) {
            if (i2 == 1 && (!this.drawVideoImageButton || video)) {
                this.photoImage.setForceLoading(false);
                int i4 = this.documentAttachType;
                if (i4 == 3 || i4 == 5) {
                    boolean result = MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject);
                    if (result) {
                        this.buttonState = 0;
                        this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                        invalidate();
                        return;
                    }
                    return;
                } else if (this.currentMessageObject.isOut() && !this.drawVideoImageButton && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) {
                    if (this.radialProgress.getIcon() != 6) {
                        this.delegate.didPressCancelSendButton(this);
                        return;
                    }
                    return;
                } else {
                    this.currentMessageObject.loadingCancelled = true;
                    int i5 = this.documentAttachType;
                    if (i5 == 2 || i5 == 4 || i5 == 1 || i5 == 8) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                    } else if (this.currentMessageObject.type == 0 || this.currentMessageObject.type == 1 || this.currentMessageObject.type == 8 || this.currentMessageObject.type == 5) {
                        ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                        this.photoImage.cancelLoadImage();
                    } else if (this.currentMessageObject.type == 9) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    }
                    this.buttonState = 0;
                    if (video) {
                        this.videoRadialProgress.setIcon(2, false, animated);
                    } else {
                        this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    }
                    invalidate();
                    return;
                }
            } else if (i2 == 2) {
                int i6 = this.documentAttachType;
                if (i6 == 3 || i6 == 5) {
                    this.radialProgress.setProgress(0.0f, false);
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                    this.currentMessageObject.loadingCancelled = false;
                    this.buttonState = 4;
                    this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                    invalidate();
                    return;
                }
                if (this.isRoundVideo) {
                    MessageObject playingMessage = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessage == null || !playingMessage.isRoundVideo()) {
                        this.photoImage.setAllowStartAnimation(true);
                        this.photoImage.startAnimation();
                    }
                } else {
                    this.photoImage.setAllowStartAnimation(true);
                    this.photoImage.startAnimation();
                }
                this.currentMessageObject.gifState = 0.0f;
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                return;
            } else if (i2 == 3 || i2 == 0) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, animated);
                }
                this.delegate.didPressImage(this, 0.0f, 0.0f);
                return;
            } else if (i2 == 4) {
                int i7 = this.documentAttachType;
                if (i7 == 3 || i7 == 5) {
                    if ((this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) || this.currentMessageObject.isSendError()) {
                        if (this.delegate != null && this.radialProgress.getIcon() != 6) {
                            this.delegate.didPressCancelSendButton(this);
                            return;
                        }
                        return;
                    }
                    this.currentMessageObject.loadingCancelled = true;
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                    this.buttonState = 2;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                    return;
                }
                return;
            } else {
                return;
            }
        }
        int i8 = this.documentAttachType;
        if (i8 == 3 || i8 == 5) {
            if (this.miniButtonState == 0) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
                this.currentMessageObject.loadingCancelled = false;
            }
            if (this.delegate.needPlayMessage(this.currentMessageObject)) {
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
            return;
        }
        if (video) {
            this.videoRadialProgress.setProgress(0.0f, false);
        } else {
            this.radialProgress.setProgress(0.0f, false);
        }
        if (this.currentPhotoObject != null && (this.photoImage.hasNotThumb() || this.currentPhotoObjectThumb == null)) {
            thumb = this.currentPhotoObject;
            thumbFilter = ((thumb instanceof TLRPC.TL_photoStrippedSize) || "s".equals(thumb.type)) ? this.currentPhotoFilterThumb : this.currentPhotoFilter;
        } else {
            thumb = this.currentPhotoObjectThumb;
            thumbFilter = this.currentPhotoFilterThumb;
        }
        if (this.currentMessageObject.type == 1) {
            this.photoImage.setForceLoading(true);
            ImageReceiver imageReceiver = this.photoImage;
            ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
            String str = this.currentPhotoFilter;
            ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject);
            String str2 = this.currentPhotoFilterThumb;
            BitmapDrawable bitmapDrawable = this.currentPhotoObjectThumbStripped;
            long j = this.currentPhotoObject.size;
            MessageObject messageObject2 = this.currentMessageObject;
            imageReceiver.setImage(forObject, str, forObject2, str2, bitmapDrawable, j, null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 0);
        } else if (this.currentMessageObject.type == 8) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            if (this.currentMessageObject.loadedFileSize > 0) {
                createLoadingProgressLayout(this.documentAttach);
            }
        } else if (this.isRoundVideo) {
            if (this.currentMessageObject.isSecretMedia()) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
            } else {
                this.currentMessageObject.gifState = 2.0f;
                TLRPC.Document document = this.currentMessageObject.getDocument();
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForObject(thumb, document), thumbFilter, document.size, null, this.currentMessageObject, 0);
            }
        } else if (this.currentMessageObject.type == 9) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            if (this.currentMessageObject.loadedFileSize > 0) {
                createLoadingProgressLayout(this.documentAttach);
            }
        } else if (this.documentAttachType == 4) {
            FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
            TLRPC.Document document2 = this.documentAttach;
            MessageObject messageObject3 = this.currentMessageObject;
            if (!messageObject3.shouldEncryptPhotoOrVideo()) {
                i3 = 0;
            }
            fileLoader.loadFile(document2, messageObject3, 1, i3);
            if (this.currentMessageObject.loadedFileSize > 0) {
                createLoadingProgressLayout(this.currentMessageObject.getDocument());
            }
        } else if (this.currentMessageObject.type == 0 && (i = this.documentAttachType) != 0) {
            if (i == 2) {
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), null, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
                this.currentMessageObject.gifState = 2.0f;
                if (this.currentMessageObject.loadedFileSize > 0) {
                    createLoadingProgressLayout(this.currentMessageObject.getDocument());
                }
            } else if (i == 1) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
            } else if (i == 8) {
                this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), this.currentPhotoFilter, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), "b1", 0L, "jpg", this.currentMessageObject, 1);
            }
        } else {
            this.photoImage.setForceLoading(true);
            this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject), this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, 0L, null, this.currentMessageObject, 0);
        }
        this.currentMessageObject.loadingCancelled = false;
        this.buttonState = 1;
        if (video) {
            this.videoRadialProgress.setIcon(14, false, animated);
        } else {
            this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
        }
        invalidate();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String fileName, boolean canceled) {
        int i = this.documentAttachType;
        updateButtonState(true, i == 3 || i == 5, false);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String fileName) {
        TLRPC.Document document;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        if (this.documentAttachType == 6 && this.currentMessageObject.isDice()) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            setCurrentDiceValue(true);
            return;
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            updateButtonState(false, true, false);
            updateWaveform();
            return;
        }
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(1.0f, true);
        } else {
            this.radialProgress.setProgress(1.0f, true);
        }
        if (!this.currentMessageObject.needDrawBluredPreview() && !this.autoPlayingMedia && (document = this.documentAttach) != null) {
            if (this.documentAttachType == 7) {
                ImageReceiver imageReceiver = this.photoImage;
                ImageLocation forDocument = ImageLocation.getForDocument(document);
                ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                TLRPC.PhotoSize photoSize = this.currentPhotoObject;
                imageReceiver.setImage(forDocument, ImageLoader.AUTOPLAY_FILTER, forObject, ((photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize != null && "s".equals(photoSize.type))) ? this.currentPhotoFilterThumb : this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, this.documentAttach.size, null, this.currentMessageObject, 0);
                this.photoImage.setAllowStartAnimation(true);
                this.photoImage.startAnimation();
                this.autoPlayingMedia = true;
            } else if (SharedConfig.autoplayVideo && this.documentAttachType == 4 && ((groupedMessagePosition = this.currentPosition) == null || ((groupedMessagePosition.flags & 1) != 0 && (this.currentPosition.flags & 2) != 0))) {
                this.animatingNoSound = 2;
                ImageReceiver imageReceiver2 = this.photoImage;
                ImageLocation forDocument2 = ImageLocation.getForDocument(this.documentAttach);
                ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                TLRPC.PhotoSize photoSize2 = this.currentPhotoObject;
                imageReceiver2.setImage(forDocument2, ImageLoader.AUTOPLAY_FILTER, forObject2, ((photoSize2 instanceof TLRPC.TL_photoStrippedSize) || (photoSize2 != null && "s".equals(photoSize2.type))) ? this.currentPhotoFilterThumb : this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, this.documentAttach.size, null, this.currentMessageObject, 0);
                if (!PhotoViewer.isPlayingMessage(this.currentMessageObject)) {
                    this.photoImage.setAllowStartAnimation(true);
                    this.photoImage.startAnimation();
                } else {
                    this.photoImage.setAllowStartAnimation(false);
                }
                this.autoPlayingMedia = true;
            } else if (this.documentAttachType == 2) {
                ImageReceiver imageReceiver3 = this.photoImage;
                ImageLocation forDocument3 = ImageLocation.getForDocument(this.documentAttach);
                ImageLocation forObject3 = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                TLRPC.PhotoSize photoSize3 = this.currentPhotoObject;
                imageReceiver3.setImage(forDocument3, ImageLoader.AUTOPLAY_FILTER, forObject3, ((photoSize3 instanceof TLRPC.TL_photoStrippedSize) || (photoSize3 != null && "s".equals(photoSize3.type))) ? this.currentPhotoFilterThumb : this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, this.currentPhotoObjectThumbStripped, this.documentAttach.size, null, this.currentMessageObject, 0);
                if (SharedConfig.autoplayGifs) {
                    this.photoImage.setAllowStartAnimation(true);
                    this.photoImage.startAnimation();
                } else {
                    this.photoImage.setAllowStartAnimation(false);
                    this.photoImage.stopAnimation();
                }
                this.autoPlayingMedia = true;
            }
        }
        if (this.currentMessageObject.type == 0) {
            if (!this.autoPlayingMedia && this.documentAttachType == 2 && this.currentMessageObject.gifState != 1.0f) {
                this.buttonState = 2;
                didPressButton(true, false);
                return;
            } else if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                return;
            } else {
                updateButtonState(false, true, false);
                return;
            }
        }
        if (!this.photoNotSet) {
            updateButtonState(false, true, false);
        }
        if (this.photoNotSet) {
            setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        }
    }

    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && set) {
            if (setCurrentDiceValue(!memCache && !messageObject.wasUnread) || thumb || this.currentMessageObject.mediaExists || this.currentMessageObject.attachPathExists) {
                return;
            }
            if ((this.currentMessageObject.type == 0 && ((i = this.documentAttachType) == 8 || i == 0 || i == 6)) || this.currentMessageObject.type == 1) {
                this.currentMessageObject.mediaExists = true;
                updateButtonState(false, true, false);
            }
        }
    }

    public boolean setCurrentDiceValue(boolean instant) {
        MessagesController.DiceFrameSuccess frameSuccess;
        if (this.currentMessageObject.isDice()) {
            Drawable drawable = this.photoImage.getDrawable();
            if (drawable instanceof RLottieDrawable) {
                RLottieDrawable lottieDrawable = (RLottieDrawable) drawable;
                String emoji = this.currentMessageObject.getDiceEmoji();
                TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(emoji);
                if (stickerSet != null) {
                    int value = this.currentMessageObject.getDiceValue();
                    if ("🎰".equals(this.currentMessageObject.getDiceEmoji())) {
                        if (value >= 0 && value <= 64) {
                            ((SlotsDrawable) lottieDrawable).setDiceNumber(this, value, stickerSet, instant);
                            if (this.currentMessageObject.isOut()) {
                                lottieDrawable.setOnFinishCallback(this.diceFinishCallback, Integer.MAX_VALUE);
                            }
                            this.currentMessageObject.wasUnread = false;
                        }
                        if (!lottieDrawable.hasBaseDice() && stickerSet.documents.size() > 0) {
                            ((SlotsDrawable) lottieDrawable).setBaseDice(this, stickerSet);
                        }
                    } else {
                        if (!lottieDrawable.hasBaseDice() && stickerSet.documents.size() > 0) {
                            TLRPC.Document document = stickerSet.documents.get(0);
                            File path = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, true);
                            if (lottieDrawable.setBaseDice(path)) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                String fileName = FileLoader.getAttachFileName(document);
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                                FileLoader.getInstance(this.currentAccount).loadFile(document, stickerSet, 1, 1);
                            }
                        }
                        if (value >= 0 && value < stickerSet.documents.size()) {
                            if (!instant && this.currentMessageObject.isOut() && (frameSuccess = MessagesController.getInstance(this.currentAccount).diceSuccess.get(emoji)) != null && frameSuccess.num == value) {
                                lottieDrawable.setOnFinishCallback(this.diceFinishCallback, frameSuccess.frame);
                            }
                            TLRPC.Document document2 = stickerSet.documents.get(Math.max(value, 0));
                            File path2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(document2, true);
                            if (lottieDrawable.setDiceNumber(path2, instant)) {
                                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                            } else {
                                String fileName2 = FileLoader.getAttachFileName(document2);
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName2, this.currentMessageObject, this);
                                FileLoader.getInstance(this.currentAccount).loadFile(document2, stickerSet, 1, 1);
                            }
                            this.currentMessageObject.wasUnread = false;
                        }
                    }
                } else {
                    MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(emoji, true, true);
                }
            }
            return true;
        }
        return false;
    }

    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
    public void onAnimationReady(ImageReceiver imageReceiver) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && imageReceiver == this.photoImage && messageObject.isAnimatedSticker()) {
            this.delegate.setShouldNotRepeatSticker(this.currentMessageObject);
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        float progress = totalSize == 0 ? 0.0f : Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize));
        this.currentMessageObject.loadedFileSize = downloadedSize;
        createLoadingProgressLayout(downloadedSize, totalSize);
        if (this.drawVideoImageButton) {
            this.videoRadialProgress.setProgress(progress, true);
        } else {
            this.radialProgress.setProgress(progress, true);
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.hasMiniProgress != 0) {
                if (this.miniButtonState != 1) {
                    updateButtonState(false, false, false);
                }
            } else if (this.buttonState != 4) {
                updateButtonState(false, false, false);
            }
        } else if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, false, false);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, false, false);
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        int i;
        float progress = totalSize == 0 ? 0.0f : Math.min(1.0f, ((float) uploadedSize) / ((float) totalSize));
        this.currentMessageObject.loadedFileSize = uploadedSize;
        this.radialProgress.setProgress(progress, true);
        if (uploadedSize == totalSize && this.currentPosition != null) {
            boolean sending = SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId());
            if (sending && ((i = this.buttonState) == 1 || (i == 4 && this.documentAttachType == 5))) {
                this.drawRadialCheckBackground = true;
                getIconForCurrentState();
                this.radialProgress.setIcon(6, false, true);
            }
        }
        createLoadingProgressLayout(uploadedSize, totalSize);
    }

    private void createLoadingProgressLayout(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        long[] progresses = ImageLoader.getInstance().getFileProgressSizes(FileLoader.getDocumentFileName(document));
        if (progresses != null) {
            createLoadingProgressLayout(progresses[0], progresses[1]);
        } else {
            createLoadingProgressLayout(this.currentMessageObject.loadedFileSize, document.size);
        }
    }

    private void createLoadingProgressLayout(long loadedSize, long totalSize) {
        long loadedSize2;
        String str;
        long totalSize2 = totalSize;
        if (totalSize2 <= 0 || this.documentAttach == null) {
            this.loadingProgressLayout = null;
            return;
        }
        long j = this.lastLoadingSizeTotal;
        if (j == 0) {
            this.lastLoadingSizeTotal = totalSize2;
            loadedSize2 = loadedSize;
        } else {
            totalSize2 = this.lastLoadingSizeTotal;
            if (loadedSize <= j) {
                loadedSize2 = loadedSize;
            } else {
                loadedSize2 = this.lastLoadingSizeTotal;
            }
        }
        String totalStr = AndroidUtilities.formatFileSize(totalSize2);
        String maxAvailableString = String.format("000.0 mm / %s", totalStr);
        int w = (int) Math.ceil(Theme.chat_infoPaint.measureText(maxAvailableString));
        boolean fullWidth = true;
        if (this.documentAttachType == 1) {
            int max = Math.max(this.infoWidth, this.docTitleWidth);
            str = w <= max ? String.format("%s / %s", AndroidUtilities.formatFileSize(loadedSize2), totalStr) : AndroidUtilities.formatFileSize(loadedSize2);
        } else {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null) {
                fullWidth = (groupedMessagePosition.flags & 3) == 3;
            }
            if (!fullWidth) {
                int percent = (int) (Math.min(1.0f, ((float) loadedSize2) / ((float) totalSize2)) * 100.0f);
                if (percent >= 100) {
                    str = "100%";
                } else {
                    str = String.format(Locale.US, "%2d%%", Integer.valueOf(percent));
                }
            } else {
                str = String.format("%s / %s", AndroidUtilities.formatFileSize(loadedSize2), totalStr);
            }
        }
        int w2 = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
        if (fullWidth && w2 > this.backgroundWidth - AndroidUtilities.dp(48.0f)) {
            int percent2 = (int) (Math.min(1.0f, ((float) loadedSize2) / ((float) totalSize2)) * 100.0f);
            if (percent2 >= 100) {
                str = "100%";
            } else {
                str = String.format(Locale.US, "%2d%%", Integer.valueOf(percent2));
            }
            w2 = (int) Math.ceil(Theme.chat_infoPaint.measureText(str));
        }
        this.loadingProgressLayout = new StaticLayout(str, Theme.chat_infoPaint, w2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    @Override // android.view.View
    public void onProvideStructure(ViewStructure structure) {
        super.onProvideStructure(structure);
        if (this.allowAssistant && Build.VERSION.SDK_INT >= 23) {
            if (this.currentMessageObject.messageText != null && this.currentMessageObject.messageText.length() > 0) {
                structure.setText(this.currentMessageObject.messageText);
            } else if (this.currentMessageObject.caption != null && this.currentMessageObject.caption.length() > 0) {
                structure.setText(this.currentMessageObject.caption);
            }
        }
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public ChatMessageCellDelegate getDelegate() {
        return this.delegate;
    }

    public void setAllowAssistant(boolean value) {
        this.allowAssistant = value;
    }

    private void measureTime(MessageObject messageObject) {
        CharSequence signString;
        String timeString;
        MessageObject.GroupedMessages groupedMessages;
        long fromId = messageObject.getFromChatId();
        if (messageObject.scheduled) {
            signString = null;
        } else if (messageObject.messageOwner.post_author != null) {
            if (this.isMegagroup && messageObject.getFromChatId() == messageObject.getDialogId()) {
                signString = null;
            } else {
                signString = messageObject.messageOwner.post_author.replace("\n", "");
            }
        } else if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.post_author != null) {
            signString = messageObject.messageOwner.fwd_from.post_author.replace("\n", "");
        } else if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.imported) {
            signString = messageObject.messageOwner.fwd_from.date == messageObject.messageOwner.date ? LocaleController.getString("ImportedMessage", R.string.ImportedMessage) : LocaleController.formatImportedDate(messageObject.messageOwner.fwd_from.date) + " " + LocaleController.getString("ImportedMessage", R.string.ImportedMessage);
        } else if (!messageObject.isOutOwner() && fromId > 0 && messageObject.messageOwner.post) {
            TLRPC.User signUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromId));
            if (signUser != null) {
                signString = ContactsController.formatName(signUser.first_name, signUser.last_name).replace('\n', ' ');
            } else {
                signString = null;
            }
        } else {
            signString = null;
        }
        TLRPC.User author = null;
        if (this.currentMessageObject.isFromUser()) {
            author = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromId));
        }
        boolean hasReplies = messageObject.hasReplies();
        if (messageObject.scheduled || messageObject.isLiveLocation() || messageObject.messageOwner.edit_hide || messageObject.getDialogId() == 777000 || messageObject.messageOwner.via_bot_id != 0 || messageObject.messageOwner.via_bot_name != null || (author != null && author.bot)) {
            this.edited = false;
        } else if (this.currentPosition == null || (groupedMessages = this.currentMessagesGroup) == null || groupedMessages.messages.isEmpty()) {
            this.edited = (messageObject.messageOwner.flags & 32768) != 0 || messageObject.isEditing();
        } else {
            this.edited = false;
            hasReplies = this.currentMessagesGroup.messages.get(0).hasReplies();
            if (!this.currentMessagesGroup.messages.get(0).messageOwner.edit_hide) {
                int size = this.currentMessagesGroup.messages.size();
                for (int a = 0; a < size; a++) {
                    MessageObject object = this.currentMessagesGroup.messages.get(a);
                    if ((object.messageOwner.flags & 32768) != 0 || object.isEditing()) {
                        this.edited = true;
                        break;
                    }
                }
            }
        }
        if (this.currentMessageObject.isSponsored()) {
            timeString = LocaleController.getString("SponsoredMessage", R.string.SponsoredMessage);
        } else if (this.currentMessageObject.scheduled && this.currentMessageObject.messageOwner.date == 2147483646) {
            timeString = "";
        } else if (this.edited) {
            timeString = LocaleController.getString("EditedMessage", R.string.EditedMessage) + " " + LocaleController.getInstance().formatterDay.format(messageObject.messageOwner.date * 1000);
        } else {
            timeString = LocaleController.getInstance().formatterDay.format(messageObject.messageOwner.date * 1000);
        }
        if (signString != null) {
            if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.imported) {
                this.currentTimeString = " " + timeString;
            } else {
                this.currentTimeString = ", " + timeString;
            }
        } else {
            this.currentTimeString = timeString;
        }
        int ceil = (int) Math.ceil(Theme.chat_timePaint.measureText(this.currentTimeString));
        this.timeWidth = ceil;
        this.timeTextWidth = ceil;
        if (this.currentMessageObject.scheduled && this.currentMessageObject.messageOwner.date == 2147483646) {
            this.timeWidth -= AndroidUtilities.dp(8.0f);
        }
        if ((messageObject.messageOwner.flags & 1024) != 0) {
            this.currentViewsString = String.format("%s", LocaleController.formatShortNumber(Math.max(1, messageObject.messageOwner.views), null));
            int ceil2 = (int) Math.ceil(Theme.chat_timePaint.measureText(this.currentViewsString));
            this.viewsTextWidth = ceil2;
            this.timeWidth += ceil2 + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(10.0f);
        }
        if (this.isChat && this.isMegagroup && !this.isThreadChat && hasReplies) {
            this.currentRepliesString = String.format("%s", LocaleController.formatShortNumber(getRepliesCount(), null));
            int ceil3 = (int) Math.ceil(Theme.chat_timePaint.measureText(this.currentRepliesString));
            this.repliesTextWidth = ceil3;
            this.timeWidth += ceil3 + Theme.chat_msgInRepliesDrawable.getIntrinsicWidth() + AndroidUtilities.dp(10.0f);
        } else {
            this.currentRepliesString = null;
        }
        if (this.isPinned) {
            this.timeWidth += Theme.chat_msgInPinnedDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0f);
        }
        if (messageObject.scheduled) {
            if (messageObject.isSendError()) {
                this.timeWidth += AndroidUtilities.dp(18.0f);
            } else if (messageObject.isSending() && messageObject.messageOwner.peer_id.channel_id != 0 && !messageObject.isSupergroup()) {
                this.timeWidth += AndroidUtilities.dp(18.0f);
            }
        }
        if (this.reactionsLayoutInBubble.isSmall) {
            this.reactionsLayoutInBubble.measure(Integer.MAX_VALUE, 3);
            this.timeWidth += this.reactionsLayoutInBubble.width;
        }
        if (signString != null) {
            if (this.availableTimeWidth == 0) {
                this.availableTimeWidth = AndroidUtilities.dp(1000.0f);
            }
            int widthForSign = this.availableTimeWidth - this.timeWidth;
            if (messageObject.isOutOwner()) {
                if (messageObject.type == 5) {
                    widthForSign -= AndroidUtilities.dp(20.0f);
                } else {
                    widthForSign -= AndroidUtilities.dp(96.0f);
                }
            }
            int width = (int) Math.ceil(Theme.chat_timePaint.measureText(signString, 0, signString.length()));
            if (width > widthForSign) {
                if (widthForSign <= 0) {
                    signString = "";
                    width = 0;
                } else {
                    signString = TextUtils.ellipsize(signString, Theme.chat_timePaint, widthForSign, TextUtils.TruncateAt.END);
                    width = widthForSign;
                }
            }
            this.currentTimeString = ((Object) signString) + this.currentTimeString;
            this.timeTextWidth = this.timeTextWidth + width;
            this.timeWidth = this.timeWidth + width;
        }
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
        return resourcesProvider.getColor((messageObject == null || !messageObject.isOut()) ? Theme.key_chat_inBubbleSelectedOverlay : Theme.key_chat_outBubbleSelectedOverlay);
    }

    private boolean hasSelectionOverlay() {
        Integer selectionOverlayColor = getSelectionOverlayColor();
        return (selectionOverlayColor == null || selectionOverlayColor.intValue() == -65536) ? false : true;
    }

    private boolean isDrawSelectionBackground() {
        return ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted)) && !textIsSelectionMode() && !hasSelectionOverlay();
    }

    public boolean isOpenChatByShare(MessageObject messageObject) {
        return (messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.saved_from_peer == null) ? false : true;
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        if (this.currentMessageObject.deleted || this.currentMessageObject.isSponsored()) {
            return false;
        }
        if (this.currentPosition != null && !this.currentMessagesGroup.isDocuments && !this.currentPosition.last) {
            return false;
        }
        return messageObject.needDrawShareButton();
    }

    public boolean isInsideBackground(float x, float y) {
        if (this.currentBackgroundDrawable != null) {
            int i = this.backgroundDrawableLeft;
            if (x >= i && x <= i + this.backgroundDrawableRight) {
                return true;
            }
        }
        return false;
    }

    private void updateCurrentUserAndChat() {
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC.MessageFwdHeader fwd_from = this.currentMessageObject.messageOwner.fwd_from;
        long currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (fwd_from != null && (fwd_from.from_id instanceof TLRPC.TL_peerChannel) && this.currentMessageObject.getDialogId() == currentUserId) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(fwd_from.from_id.channel_id));
        } else if (fwd_from != null && fwd_from.saved_from_peer != null) {
            if (fwd_from.saved_from_peer.user_id != 0) {
                if (fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(fwd_from.from_id.user_id));
                } else {
                    this.currentUser = messagesController.getUser(Long.valueOf(fwd_from.saved_from_peer.user_id));
                }
            } else if (fwd_from.saved_from_peer.channel_id != 0) {
                if (this.currentMessageObject.isSavedFromMegagroup() && (fwd_from.from_id instanceof TLRPC.TL_peerUser)) {
                    this.currentUser = messagesController.getUser(Long.valueOf(fwd_from.from_id.user_id));
                } else {
                    this.currentChat = messagesController.getChat(Long.valueOf(fwd_from.saved_from_peer.channel_id));
                }
            } else if (fwd_from.saved_from_peer.chat_id != 0) {
                if (fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(fwd_from.from_id.user_id));
                } else {
                    this.currentChat = messagesController.getChat(Long.valueOf(fwd_from.saved_from_peer.chat_id));
                }
            }
        } else if (fwd_from != null && (fwd_from.from_id instanceof TLRPC.TL_peerUser) && (fwd_from.imported || this.currentMessageObject.getDialogId() == currentUserId)) {
            this.currentUser = messagesController.getUser(Long.valueOf(fwd_from.from_id.user_id));
        } else if (fwd_from != null && !TextUtils.isEmpty(fwd_from.from_name) && (fwd_from.imported || this.currentMessageObject.getDialogId() == currentUserId)) {
            TLRPC.TL_user tL_user = new TLRPC.TL_user();
            this.currentUser = tL_user;
            tL_user.first_name = fwd_from.from_name;
        } else {
            long fromId = this.currentMessageObject.getFromChatId();
            if (DialogObject.isUserDialog(fromId) && !this.currentMessageObject.messageOwner.post) {
                this.currentUser = messagesController.getUser(Long.valueOf(fromId));
            } else if (DialogObject.isChatDialog(fromId)) {
                this.currentChat = messagesController.getChat(Long.valueOf(-fromId));
            } else if (this.currentMessageObject.messageOwner.post) {
                this.currentChat = messagesController.getChat(Long.valueOf(this.currentMessageObject.messageOwner.peer_id.channel_id));
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(23:242|(2:246|(1:248))|249|(1:251)(2:252|(1:254))|255|(6:(2:260|(26:262|(3:264|(3:266|(1:268)|269)(1:270)|271)|(1:273)|(1:290)(3:285|(1:287)(1:288)|289)|291|(2:293|(2:295|(3:297|(1:299)|300)(1:301))(1:302))(2:303|(1:305)(2:306|(2:308|(3:310|(1:312)|313)(2:314|(3:316|(1:318)|319)(2:320|(1:322))))(1:323)))|(1:325)|326|(1:328)(2:329|(1:331)(2:332|(4:334|(1:336)(1:337)|338|(1:340))(2:341|(1:352)(4:345|(1:347)(1:348)|349|(1:351)))))|353|(1:395)(1:396)|397|445|398|399|401|455|402|(1:404)(1:405)|406|(2:408|(1:410))|451|413|(1:415)(1:416)|417|(8:419|(3:421|(2:423|460)(1:461)|424)|459|(1:426)(1:427)|428|(1:430)|431|(1:435)))(1:354))(1:259)|451|413|(0)(0)|417|(0))|355|(1:357)(2:358|(1:360)(2:361|(1:363)))|364|(1:366)|367|(7:374|(1:(1:377)(1:378))(1:(1:380)(1:381))|382|(1:384)(1:385)|386|(1:392)|393)(1:373)|(0)(0)|397|445|398|399|401|455|402|(0)(0)|406|(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(28:242|(2:246|(1:248))|249|(1:251)(2:252|(1:254))|255|(2:260|(26:262|(3:264|(3:266|(1:268)|269)(1:270)|271)|(1:273)|(1:290)(3:285|(1:287)(1:288)|289)|291|(2:293|(2:295|(3:297|(1:299)|300)(1:301))(1:302))(2:303|(1:305)(2:306|(2:308|(3:310|(1:312)|313)(2:314|(3:316|(1:318)|319)(2:320|(1:322))))(1:323)))|(1:325)|326|(1:328)(2:329|(1:331)(2:332|(4:334|(1:336)(1:337)|338|(1:340))(2:341|(1:352)(4:345|(1:347)(1:348)|349|(1:351)))))|353|(1:395)(1:396)|397|445|398|399|401|455|402|(1:404)(1:405)|406|(2:408|(1:410))|451|413|(1:415)(1:416)|417|(8:419|(3:421|(2:423|460)(1:461)|424)|459|(1:426)(1:427)|428|(1:430)|431|(1:435)))(1:354))(1:259)|355|(1:357)(2:358|(1:360)(2:361|(1:363)))|364|(1:366)|367|(7:374|(1:(1:377)(1:378))(1:(1:380)(1:381))|382|(1:384)(1:385)|386|(1:392)|393)(1:373)|(0)(0)|397|445|398|399|401|455|402|(0)(0)|406|(0)|451|413|(0)(0)|417|(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(39:36|(1:38)|39|(1:48)(2:45|(24:47|73|(1:75)(1:(1:77)(1:78))|79|(1:81)(1:82)|83|(7:85|(1:87)|88|(1:90)(3:91|(1:93)(1:94)|95)|96|(1:98)(1:99)|100)(1:101)|449|102|103|440|105|(3:107|(1:109)|110)(1:111)|(1:113)(1:114)|117|(1:119)(1:120)|123|(2:125|(1:127)(2:128|(1:130)(2:131|(1:133))))|134|(1:203)(2:138|(4:143|(1:145)|146|(25:153|(1:(1:156)(2:157|(1:159)(1:160)))(1:(1:162)(1:163))|164|(1:166)|167|441|168|169|(1:173)(1:174)|175|(1:181)|182|457|183|184|443|186|187|447|188|453|189|(1:191)|192|(1:196))(1:152))(1:142))|204|(2:238|(28:242|(2:246|(1:248))|249|(1:251)(2:252|(1:254))|255|(2:260|(26:262|(3:264|(3:266|(1:268)|269)(1:270)|271)|(1:273)|(1:290)(3:285|(1:287)(1:288)|289)|291|(2:293|(2:295|(3:297|(1:299)|300)(1:301))(1:302))(2:303|(1:305)(2:306|(2:308|(3:310|(1:312)|313)(2:314|(3:316|(1:318)|319)(2:320|(1:322))))(1:323)))|(1:325)|326|(1:328)(2:329|(1:331)(2:332|(4:334|(1:336)(1:337)|338|(1:340))(2:341|(1:352)(4:345|(1:347)(1:348)|349|(1:351)))))|353|(1:395)(1:396)|397|445|398|399|401|455|402|(1:404)(1:405)|406|(2:408|(1:410))|451|413|(1:415)(1:416)|417|(8:419|(3:421|(2:423|460)(1:461)|424)|459|(1:426)(1:427)|428|(1:430)|431|(1:435)))(1:354))(1:259)|355|(1:357)(2:358|(1:360)(2:361|(1:363)))|364|(1:366)|367|(7:374|(1:(1:377)(1:378))(1:(1:380)(1:381))|382|(1:384)(1:385)|386|(1:392)|393)(1:373)|(0)(0)|397|445|398|399|401|455|402|(0)(0)|406|(0)|451|413|(0)(0)|417|(0)))(2:214|(6:222|(2:226|(1:228))|229|(1:231)(2:232|(1:234))|235|(1:237)))|438|439))|49|(3:56|(2:66|(3:68|(1:70)|71))|72)(1:55)|73|(0)(0)|79|(0)(0)|83|(0)(0)|449|102|103|440|105|(0)(0)|(0)(0)|117|(0)(0)|123|(0)|134|(1:136)|203|204|(1:206)|208|238|(1:240)|242|(3:244|246|(0))|249|(0)(0)|255|(1:257)|260|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x03c0, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x03c1, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:411:0x0cc1, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:412:0x0cc2, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:101:0x0322  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x035d A[Catch: Exception -> 0x03c0, TryCatch #0 {Exception -> 0x03c0, blocks: (B:105:0x033a, B:107:0x035d, B:109:0x0372, B:110:0x037d, B:111:0x0387, B:113:0x038c, B:114:0x03bc), top: B:440:0x033a }] */
    /* JADX WARN: Removed duplicated region for block: B:111:0x0387 A[Catch: Exception -> 0x03c0, TryCatch #0 {Exception -> 0x03c0, blocks: (B:105:0x033a, B:107:0x035d, B:109:0x0372, B:110:0x037d, B:111:0x0387, B:113:0x038c, B:114:0x03bc), top: B:440:0x033a }] */
    /* JADX WARN: Removed duplicated region for block: B:113:0x038c A[Catch: Exception -> 0x03c0, TryCatch #0 {Exception -> 0x03c0, blocks: (B:105:0x033a, B:107:0x035d, B:109:0x0372, B:110:0x037d, B:111:0x0387, B:113:0x038c, B:114:0x03bc), top: B:440:0x033a }] */
    /* JADX WARN: Removed duplicated region for block: B:114:0x03bc A[Catch: Exception -> 0x03c0, TRY_LEAVE, TryCatch #0 {Exception -> 0x03c0, blocks: (B:105:0x033a, B:107:0x035d, B:109:0x0372, B:110:0x037d, B:111:0x0387, B:113:0x038c, B:114:0x03bc), top: B:440:0x033a }] */
    /* JADX WARN: Removed duplicated region for block: B:119:0x03cc  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x03d0  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x03d2  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x03f7  */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0465  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x06fe  */
    /* JADX WARN: Removed duplicated region for block: B:240:0x07c1  */
    /* JADX WARN: Removed duplicated region for block: B:244:0x07cb  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x07df  */
    /* JADX WARN: Removed duplicated region for block: B:251:0x07f4  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x07fa  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x080c  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x081e  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x011b  */
    /* JADX WARN: Removed duplicated region for block: B:354:0x0ad4  */
    /* JADX WARN: Removed duplicated region for block: B:395:0x0c44  */
    /* JADX WARN: Removed duplicated region for block: B:396:0x0c46  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x0c70  */
    /* JADX WARN: Removed duplicated region for block: B:405:0x0c73  */
    /* JADX WARN: Removed duplicated region for block: B:408:0x0c7e A[Catch: Exception -> 0x0cc1, TryCatch #8 {Exception -> 0x0cc1, blocks: (B:402:0x0c6c, B:406:0x0c74, B:408:0x0c7e, B:410:0x0c9d), top: B:455:0x0c6c }] */
    /* JADX WARN: Removed duplicated region for block: B:415:0x0cc9  */
    /* JADX WARN: Removed duplicated region for block: B:416:0x0ccc  */
    /* JADX WARN: Removed duplicated region for block: B:419:0x0cd7 A[Catch: Exception -> 0x0d77, TryCatch #6 {Exception -> 0x0d77, blocks: (B:413:0x0cc5, B:417:0x0ccd, B:419:0x0cd7, B:421:0x0cee, B:423:0x0cf9, B:424:0x0cfd, B:426:0x0d02, B:428:0x0d0e, B:430:0x0d33, B:431:0x0d56, B:433:0x0d63, B:435:0x0d6d), top: B:451:0x0cc5 }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x01f8  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x01fd  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x021a  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x021d  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0228  */
    /* JADX WARN: Type inference failed for: r9v1, types: [org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v37, types: [android.text.StaticLayout, java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v38 */
    /* JADX WARN: Type inference failed for: r9v41 */
    /* JADX WARN: Type inference failed for: r9v42 */
    /* JADX WARN: Type inference failed for: r9v79 */
    /* JADX WARN: Type inference failed for: r9v80 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r45) {
        /*
            Method dump skipped, instructions count: 3456
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
    }

    private boolean isNeedAuthorName() {
        return (this.isPinnedChat && this.currentMessageObject.type == 0) || ((!this.pinnedTop || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup)) && this.drawName && this.isChat && (!this.currentMessageObject.isOutOwner() || (this.currentMessageObject.isSupergroup() && this.currentMessageObject.isFromGroup()))) || (this.currentMessageObject.isImportedForward() && this.currentMessageObject.messageOwner.fwd_from.from_id == null);
    }

    private String getAuthorName() {
        TLRPC.User user = this.currentUser;
        if (user != null) {
            return UserObject.getUserName(user);
        }
        TLRPC.Chat chat = this.currentChat;
        if (chat != null) {
            return chat.title;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.isSponsored()) {
            if (this.currentMessageObject.sponsoredChatInvite != null && this.currentMessageObject.sponsoredChatInvite.title != null) {
                return this.currentMessageObject.sponsoredChatInvite.title;
            }
            if (this.currentMessageObject.sponsoredChatInvite != null && this.currentMessageObject.sponsoredChatInvite.chat != null && this.currentMessageObject.sponsoredChatInvite.chat.title != null) {
                return this.currentMessageObject.sponsoredChatInvite.chat.title;
            }
            return "";
        }
        return "DELETED";
    }

    private String getForwardedMessageText(MessageObject messageObject) {
        if (this.hasPsaHint) {
            String forwardedString = LocaleController.getString("PsaMessage_" + messageObject.messageOwner.fwd_from.psa_type);
            if (forwardedString == null) {
                return LocaleController.getString("PsaMessageDefault", R.string.PsaMessageDefault);
            }
            return forwardedString;
        }
        return LocaleController.getString("ForwardedMessage", R.string.ForwardedMessage);
    }

    public int getExtraInsetHeight() {
        int h = this.addedCaptionHeight;
        if (this.drawCommentButton) {
            h += AndroidUtilities.dp(shouldDrawTimeOnMedia() ? 41.3f : 43.0f);
        }
        if (!this.reactionsLayoutInBubble.isEmpty && this.currentMessageObject.shouldDrawReactionsInLayout()) {
            return h + this.reactionsLayoutInBubble.totalHeight;
        }
        return h;
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
        boolean forceMediaByGroup = groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments;
        return this.mediaBackground || this.drawPinnedBottom || forceMediaByGroup;
    }

    public void drawCheckBox(Canvas canvas) {
        float y;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSending() || this.currentMessageObject.isSendError() || this.checkBox == null) {
            return;
        }
        if (!this.checkBoxVisible && !this.checkBoxAnimationInProgress) {
            return;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition == null || ((groupedMessagePosition.flags & 8) != 0 && (this.currentPosition.flags & 1) != 0)) {
            canvas.save();
            float y2 = getY();
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages != null && groupedMessages.messages.size() > 1) {
                y = (getTop() + this.currentMessagesGroup.transitionParams.offsetTop) - getTranslationY();
            } else {
                y = this.transitionParams.deltaTop + y2;
            }
            canvas.translate(0.0f, this.transitionYOffsetForDrawables + y);
            this.checkBox.draw(canvas);
            canvas.restore();
        }
    }

    public void setBackgroundTopY(boolean fromParent) {
        int h;
        int w;
        int a = 0;
        while (a < 2) {
            if (a == 1 && !fromParent) {
                return;
            }
            Theme.MessageDrawable drawable = a == 0 ? this.currentBackgroundDrawable : this.currentBackgroundSelectedDrawable;
            if (drawable != null) {
                int w2 = this.parentWidth;
                int h2 = this.parentHeight;
                if (h2 != 0) {
                    w = w2;
                    h = h2;
                } else {
                    int w3 = getParentWidth();
                    int h3 = AndroidUtilities.displaySize.y;
                    if (!(getParent() instanceof View)) {
                        w = w3;
                        h = h3;
                    } else {
                        View view = (View) getParent();
                        int w4 = view.getMeasuredWidth();
                        w = w4;
                        h = view.getMeasuredHeight();
                    }
                }
                float y = fromParent ? getY() : getTop();
                float f = this.parentViewTopOffset;
                drawable.setTop((int) (y + f), w, h, (int) f, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
            }
            a++;
        }
    }

    public void setBackgroundTopY(int offset) {
        int h;
        int w;
        Theme.MessageDrawable drawable = this.currentBackgroundDrawable;
        int w2 = this.parentWidth;
        int h2 = this.parentHeight;
        if (h2 != 0) {
            w = w2;
            h = h2;
        } else {
            int w3 = getParentWidth();
            int h3 = AndroidUtilities.displaySize.y;
            if (!(getParent() instanceof View)) {
                w = w3;
                h = h3;
            } else {
                View view = (View) getParent();
                int w4 = view.getMeasuredWidth();
                w = w4;
                h = view.getMeasuredHeight();
            }
        }
        float f = this.parentViewTopOffset;
        drawable.setTop((int) (offset + f), w, h, (int) f, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
    }

    public void setDrawableBoundsInner(Drawable drawable, int x, int y, int w, int h) {
        if (drawable != null) {
            this.transitionYOffsetForDrawables = ((y + h) + this.transitionParams.deltaBottom) - ((int) ((y + h) + this.transitionParams.deltaBottom));
            drawable.setBounds((int) (x + this.transitionParams.deltaLeft), (int) (y + this.transitionParams.deltaTop), (int) (x + w + this.transitionParams.deltaRight), (int) (y + h + this.transitionParams.deltaBottom));
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Theme.MessageDrawable messageDrawable;
        if (this.currentMessageObject == null) {
            return;
        }
        boolean z = this.wasLayout;
        if (!z && !this.animationRunning) {
            forceLayout();
            return;
        }
        if (!z) {
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        }
        if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_msgTextPaint.setColor(getThemedColor(Theme.key_chat_messageTextOut));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor(Theme.key_chat_messageTextOut));
            Theme.chat_msgGameTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkOut);
            Theme.chat_replyTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkOut);
            Theme.chat_msgTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkOut);
        } else {
            Theme.chat_msgTextPaint.setColor(getThemedColor(Theme.key_chat_messageTextIn));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor(Theme.key_chat_messageTextIn));
            Theme.chat_msgGameTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkIn);
            Theme.chat_replyTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkIn);
            Theme.chat_msgTextPaint.linkColor = getThemedColor(Theme.key_chat_messageLinkIn);
        }
        if (this.documentAttach != null) {
            int i = this.documentAttachType;
            if (i == 3) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBarWaveform.setColors(getThemedColor(Theme.key_chat_outVoiceSeekbar), getThemedColor(Theme.key_chat_outVoiceSeekbarFill), getThemedColor(Theme.key_chat_outVoiceSeekbarSelected));
                    this.seekBar.setColors(getThemedColor(Theme.key_chat_outAudioSeekbar), getThemedColor(Theme.key_chat_outAudioCacheSeekbar), getThemedColor(Theme.key_chat_outAudioSeekbarFill), getThemedColor(Theme.key_chat_outAudioSeekbarFill), getThemedColor(Theme.key_chat_outAudioSeekbarSelected));
                } else {
                    this.seekBarWaveform.setColors(getThemedColor(Theme.key_chat_inVoiceSeekbar), getThemedColor(Theme.key_chat_inVoiceSeekbarFill), getThemedColor(Theme.key_chat_inVoiceSeekbarSelected));
                    this.seekBar.setColors(getThemedColor(Theme.key_chat_inAudioSeekbar), getThemedColor(Theme.key_chat_inAudioCacheSeekbar), getThemedColor(Theme.key_chat_inAudioSeekbarFill), getThemedColor(Theme.key_chat_inAudioSeekbarFill), getThemedColor(Theme.key_chat_inAudioSeekbarSelected));
                }
            } else if (i == 5) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.seekBar.setColors(getThemedColor(Theme.key_chat_outAudioSeekbar), getThemedColor(Theme.key_chat_outAudioCacheSeekbar), getThemedColor(Theme.key_chat_outAudioSeekbarFill), getThemedColor(Theme.key_chat_outAudioSeekbarFill), getThemedColor(Theme.key_chat_outAudioSeekbarSelected));
                } else {
                    this.seekBar.setColors(getThemedColor(Theme.key_chat_inAudioSeekbar), getThemedColor(Theme.key_chat_inAudioCacheSeekbar), getThemedColor(Theme.key_chat_inAudioSeekbarFill), getThemedColor(Theme.key_chat_inAudioSeekbarFill), getThemedColor(Theme.key_chat_inAudioSeekbarSelected));
                }
            }
        }
        if (this.currentMessageObject.type == 5) {
            Theme.chat_timePaint.setColor(getThemedColor(Theme.key_chat_serviceText));
        } else if (this.mediaBackground) {
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_timePaint.setColor(getThemedColor(Theme.key_chat_serviceText));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor(Theme.key_chat_mediaTimeText));
            }
        } else if (this.currentMessageObject.isOutOwner()) {
            Theme.chat_timePaint.setColor(getThemedColor(isDrawSelectionBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
        } else {
            Theme.chat_timePaint.setColor(getThemedColor(isDrawSelectionBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
        }
        drawBackgroundInternal(canvas, false);
        if (this.isHighlightedAnimated) {
            long newTime = System.currentTimeMillis();
            long dt = Math.abs(newTime - this.lastHighlightProgressTime);
            if (dt > 17) {
                dt = 17;
            }
            int i2 = (int) (this.highlightProgress - dt);
            this.highlightProgress = i2;
            this.lastHighlightProgressTime = newTime;
            if (i2 <= 0) {
                this.highlightProgress = 0;
                this.isHighlightedAnimated = false;
            }
            invalidate();
            if (getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }
        int restore = Integer.MIN_VALUE;
        if (this.alphaInternal != 1.0f) {
            int top = 0;
            int left = 0;
            int bottom = getMeasuredHeight();
            int right = getMeasuredWidth();
            Theme.MessageDrawable messageDrawable2 = this.currentBackgroundDrawable;
            if (messageDrawable2 != null) {
                top = messageDrawable2.getBounds().top;
                bottom = this.currentBackgroundDrawable.getBounds().bottom;
                left = this.currentBackgroundDrawable.getBounds().left;
                right = this.currentBackgroundDrawable.getBounds().right;
            }
            if (this.drawSideButton != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    left -= AndroidUtilities.dp(40.0f);
                } else {
                    right += AndroidUtilities.dp(40.0f);
                }
            }
            if (getY() < 0.0f) {
                top = (int) (-getY());
            }
            float y = getY() + getMeasuredHeight();
            int i3 = this.parentHeight;
            if (y > i3) {
                bottom = (int) (i3 - getY());
            }
            this.rect.set(left, top, right, bottom);
            restore = canvas.saveLayerAlpha(this.rect, (int) (this.alphaInternal * 255.0f), 31);
        }
        boolean clipContent = false;
        if (this.transitionParams.animateBackgroundBoundsInner && (messageDrawable = this.currentBackgroundDrawable) != null && !this.isRoundVideo) {
            Rect r = messageDrawable.getBounds();
            canvas.save();
            canvas.clipRect(r.left + AndroidUtilities.dp(4.0f), r.top + AndroidUtilities.dp(4.0f), r.right - AndroidUtilities.dp(4.0f), r.bottom - AndroidUtilities.dp(4.0f));
            clipContent = true;
        }
        drawContent(canvas);
        if (clipContent) {
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
                    int i4 = this.backgroundDrawableLeft;
                    if (!this.drawPinnedBottom) {
                        f = 18.0f;
                    }
                    this.replyStartX = i4 + AndroidUtilities.dp(f) + getExtraTextX();
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
            long newTime2 = System.currentTimeMillis();
            long dt2 = Math.abs(this.lastControlsAlphaChangeTime - newTime2);
            if (dt2 > 17) {
                dt2 = 17;
            }
            long j = this.totalChangeTime + dt2;
            this.totalChangeTime = j;
            if (j > 100) {
                this.totalChangeTime = 100L;
            }
            this.lastControlsAlphaChangeTime = newTime2;
            if (this.controlsAlpha != 1.0f) {
                this.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
            }
            if (this.timeAlpha != 1.0f) {
                this.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation(((float) this.totalChangeTime) / 100.0f);
            }
            invalidate();
            if (this.forceNotDrawTime && (groupedMessagePosition = this.currentPosition) != null && groupedMessagePosition.last && getParent() != null) {
                View parent = (View) getParent();
                parent.invalidate();
            }
        }
        if (this.drawBackground && shouldDrawSelectionOverlay() && this.currentMessagesGroup == null) {
            if (this.selectionOverlayPaint == null) {
                this.selectionOverlayPaint = new Paint(1);
            }
            this.selectionOverlayPaint.setColor(getSelectionOverlayColor().intValue());
            int wasAlpha = this.selectionOverlayPaint.getAlpha();
            this.selectionOverlayPaint.setAlpha((int) (wasAlpha * getHighlightAlpha() * getAlpha()));
            if (this.selectionOverlayPaint.getAlpha() > 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams, this.selectionOverlayPaint);
                canvas.restore();
            }
            this.selectionOverlayPaint.setAlpha(wasAlpha);
        }
        if (restore != Integer.MIN_VALUE) {
            canvas.restoreToCount(restore);
        }
        updateSelectionTextPosition();
    }

    /* JADX WARN: Removed duplicated region for block: B:220:0x04a1  */
    /* JADX WARN: Removed duplicated region for block: B:221:0x04a8  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x04c5  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x04c8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawBackgroundInternal(android.graphics.Canvas r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 2099
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawBackgroundInternal(android.graphics.Canvas, boolean):void");
    }

    public boolean drawBackgroundInParent() {
        MessageObject messageObject;
        if (!this.canDrawBackgroundInParent || (messageObject = this.currentMessageObject) == null || !messageObject.isOutOwner()) {
            return false;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.getCurrentColor(Theme.key_chat_outBubbleGradient1) != null : Theme.getColorOrNull(Theme.key_chat_outBubbleGradient1) != null;
    }

    public void drawCommentButton(Canvas canvas, float alpha) {
        if (this.drawSideButton != 3) {
            return;
        }
        int height = AndroidUtilities.dp(32.0f);
        if (this.commentLayout != null) {
            this.sideStartY -= AndroidUtilities.dp(18.0f);
            height += AndroidUtilities.dp(18.0f);
        }
        RectF rectF = this.rect;
        float f = this.sideStartX;
        rectF.set(f, this.sideStartY, AndroidUtilities.dp(32.0f) + f, this.sideStartY + height);
        applyServiceShaderMatrix();
        String str = Theme.key_paint_chatActionBackground;
        if (alpha != 1.0f) {
            int oldAlpha = getThemedPaint(str).getAlpha();
            getThemedPaint(str).setAlpha((int) (oldAlpha * alpha));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(str));
            getThemedPaint(str).setAlpha(oldAlpha);
        } else {
            RectF rectF2 = this.rect;
            float dp = AndroidUtilities.dp(16.0f);
            float dp2 = AndroidUtilities.dp(16.0f);
            if (this.sideButtonPressed) {
                str = Theme.key_paint_chatActionBackgroundSelected;
            }
            canvas.drawRoundRect(rectF2, dp, dp2, getThemedPaint(str));
        }
        if (hasGradientService()) {
            if (alpha != 1.0f) {
                int oldAlpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (oldAlpha2 * alpha));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(oldAlpha2);
            } else {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
        }
        Drawable commentStickerDrawable = Theme.getThemeDrawable(Theme.key_drawable_commentSticker);
        setDrawableBounds(commentStickerDrawable, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(4.0f));
        if (alpha != 1.0f) {
            commentStickerDrawable.setAlpha((int) (alpha * 255.0f));
            commentStickerDrawable.draw(canvas);
            commentStickerDrawable.setAlpha(255);
        } else {
            commentStickerDrawable.draw(canvas);
        }
        if (this.commentLayout != null) {
            Theme.chat_stickerCommentCountPaint.setColor(getThemedColor(Theme.key_chat_stickerReplyNameText));
            Theme.chat_stickerCommentCountPaint.setAlpha((int) (alpha * 255.0f));
            if (this.transitionParams.animateComments) {
                if (this.transitionParams.animateCommentsLayout != null) {
                    canvas.save();
                    TextPaint textPaint = Theme.chat_stickerCommentCountPaint;
                    double d = this.transitionParams.animateChangeProgress;
                    Double.isNaN(d);
                    double d2 = alpha;
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
    }

    public void applyServiceShaderMatrix() {
        applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, getX(), this.viewTop);
    }

    private void applyServiceShaderMatrix(int measuredWidth, int backgroundHeight, float x, float viewTop) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(measuredWidth, backgroundHeight, x, viewTop);
        } else {
            Theme.applyServiceShaderMatrix(measuredWidth, backgroundHeight, x, viewTop);
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
        if (!this.transitionParams.transitionBotButtons.isEmpty() && this.transitionParams.animateBotButtonsChanged) {
            drawBotButtons(canvas, this.transitionParams.transitionBotButtons, 1.0f - this.transitionParams.animateChangeProgress);
        }
        if (!this.botButtons.isEmpty()) {
            ArrayList<BotButton> arrayList = this.botButtons;
            if (this.transitionParams.animateBotButtonsChanged) {
                f = this.transitionParams.animateChangeProgress;
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
                this.sideStartY = dp3 + groupedMessages3.transitionParams.offsetBottom;
                if (this.currentMessagesGroup.transitionParams.backgroundChangeBounds) {
                    this.sideStartY -= getTranslationY();
                }
            }
            if (!this.reactionsLayoutInBubble.isSmall && this.reactionsLayoutInBubble.drawServiceShaderBackground) {
                this.sideStartY -= this.reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress);
            }
            if (!this.currentMessageObject.isOutOwner() && this.isRoundVideo && this.isAvatarVisible) {
                float offsetSize = (AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f;
                float offsetX = this.isPlayingRound ? offsetSize : 0.0f;
                if (this.transitionParams.animatePlayingRound) {
                    offsetX = (this.isPlayingRound ? this.transitionParams.animateChangeProgress : 1.0f - this.transitionParams.animateChangeProgress) * offsetSize;
                }
                this.sideStartX -= offsetX;
            }
            if (this.drawSideButton == 3) {
                if (!this.enterTransitionInProgress || this.currentMessageObject.isVoice()) {
                    drawCommentButton(canvas, 1.0f);
                    return;
                }
                return;
            }
            RectF rectF = this.rect;
            float f = this.sideStartX;
            rectF.set(f, this.sideStartY, AndroidUtilities.dp(32.0f) + f, this.sideStartY + AndroidUtilities.dp(32.0f));
            applyServiceShaderMatrix();
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(this.sideButtonPressed ? Theme.key_paint_chatActionBackgroundSelected : Theme.key_paint_chatActionBackground));
            if (hasGradientService()) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (this.drawSideButton == 2) {
                Drawable goIconDrawable = getThemedDrawable(Theme.key_drawable_goIcon);
                if (this.currentMessageObject.isOutOwner()) {
                    setDrawableBounds(goIconDrawable, this.sideStartX + AndroidUtilities.dp(10.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
                    canvas.save();
                    canvas.scale(-1.0f, 1.0f, goIconDrawable.getBounds().centerX(), goIconDrawable.getBounds().centerY());
                } else {
                    setDrawableBounds(goIconDrawable, this.sideStartX + AndroidUtilities.dp(12.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
                }
                goIconDrawable.draw(canvas);
                if (this.currentMessageObject.isOutOwner()) {
                    canvas.restore();
                    return;
                }
                return;
            }
            Drawable drawable = getThemedDrawable(Theme.key_drawable_shareIcon);
            setDrawableBounds(drawable, this.sideStartX + AndroidUtilities.dp(8.0f), this.sideStartY + AndroidUtilities.dp(9.0f));
            drawable.draw(canvas);
        }
    }

    public void setTimeAlpha(float value) {
        this.timeAlpha = value;
    }

    public float getTimeAlpha() {
        return this.timeAlpha;
    }

    public int getBackgroundDrawableLeft() {
        int i = 0;
        if (this.currentMessageObject.isOutOwner()) {
            int i2 = this.layoutWidth - this.backgroundWidth;
            if (this.mediaBackground) {
                i = AndroidUtilities.dp(9.0f);
            }
            return i2 - i;
        }
        if (this.isChat && this.isAvatarVisible) {
            i = 48;
        }
        int r = AndroidUtilities.dp(i + (!this.mediaBackground ? 3 : 9));
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments && this.currentPosition.leftSpanOffset != 0) {
            r += (int) Math.ceil((this.currentPosition.leftSpanOffset / 1000.0f) * getGroupPhotosWidth());
        }
        if (!this.mediaBackground && this.drawPinnedBottom) {
            return r + AndroidUtilities.dp(6.0f);
        }
        return r;
    }

    public int getBackgroundDrawableRight() {
        int right = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
        if (!this.mediaBackground && this.drawPinnedBottom && this.currentMessageObject.isOutOwner()) {
            right -= AndroidUtilities.dp(6.0f);
        }
        if (!this.mediaBackground && this.drawPinnedBottom && !this.currentMessageObject.isOutOwner()) {
            right -= AndroidUtilities.dp(6.0f);
        }
        return getBackgroundDrawableLeft() + right;
    }

    public int getBackgroundDrawableTop() {
        int additionalTop = 0;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && (groupedMessagePosition.flags & 4) == 0) {
            additionalTop = 0 - AndroidUtilities.dp(3.0f);
        }
        return (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f)) + additionalTop;
    }

    public int getBackgroundDrawableBottom() {
        int offsetBottom;
        int additionalBottom = 0;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = 4;
            if ((groupedMessagePosition.flags & 4) == 0) {
                additionalBottom = 0 + AndroidUtilities.dp(3.0f);
            }
            if ((this.currentPosition.flags & 8) == 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    i = 3;
                }
                additionalBottom += AndroidUtilities.dp(i);
            }
        }
        boolean z = this.drawPinnedBottom;
        if (z && this.drawPinnedTop) {
            offsetBottom = 0;
        } else if (z) {
            offsetBottom = AndroidUtilities.dp(1.0f);
        } else {
            offsetBottom = AndroidUtilities.dp(2.0f);
        }
        return ((getBackgroundDrawableTop() + this.layoutHeight) - offsetBottom) + additionalBottom;
    }

    public void drawBackground(Canvas canvas, int left, int top, int right, int bottom, boolean pinnedTop, boolean pinnedBottom, boolean selected, int keyboardHeight) {
        int h;
        int w;
        if (this.currentMessageObject.isOutOwner()) {
            if (this.mediaBackground || pinnedBottom) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(selected ? Theme.key_drawable_msgOutMediaSelected : Theme.key_drawable_msgOutMedia);
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(selected ? Theme.key_drawable_msgOutSelected : Theme.key_drawable_msgOut);
            }
        } else if (this.mediaBackground || pinnedBottom) {
            this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(selected ? Theme.key_drawable_msgInMediaSelected : Theme.key_drawable_msgInMedia);
        } else {
            this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(selected ? Theme.key_drawable_msgInSelected : Theme.key_drawable_msgIn);
        }
        int w2 = this.parentWidth;
        int h2 = this.parentHeight;
        if (h2 != 0) {
            w = w2;
            h = h2;
        } else {
            int w3 = getParentWidth();
            int h3 = AndroidUtilities.displaySize.y;
            if (!(getParent() instanceof View)) {
                w = w3;
                h = h3;
            } else {
                View view = (View) getParent();
                int w4 = view.getMeasuredWidth();
                w = w4;
                h = view.getMeasuredHeight();
            }
        }
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        if (messageDrawable != null) {
            messageDrawable.setTop(keyboardHeight, w, h, (int) this.parentViewTopOffset, this.blurredViewTopOffset, this.blurredViewBottomOffset, pinnedTop, pinnedBottom);
            Drawable currentBackgroundShadowDrawable = this.currentBackgroundDrawable.getShadowDrawable();
            if (currentBackgroundShadowDrawable != null) {
                currentBackgroundShadowDrawable.setAlpha((int) (getAlpha() * 255.0f));
                currentBackgroundShadowDrawable.setBounds(left, top, right, bottom);
                currentBackgroundShadowDrawable.draw(canvas);
                currentBackgroundShadowDrawable.setAlpha(255);
            }
            this.currentBackgroundDrawable.setAlpha((int) (getAlpha() * 255.0f));
            this.currentBackgroundDrawable.setBounds(left, top, right, bottom);
            this.currentBackgroundDrawable.drawCached(canvas, this.backgroundCacheParams);
            this.currentBackgroundDrawable.setAlpha(255);
        }
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
                    if (groupedMessagePosition.minY == 0 && this.currentPosition.minX == 0) {
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

    /* JADX WARN: Removed duplicated region for block: B:303:0x0844  */
    /* JADX WARN: Removed duplicated region for block: B:311:0x089d  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x08a4  */
    /* JADX WARN: Removed duplicated region for block: B:349:0x09a7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawNamesLayout(android.graphics.Canvas r36, float r37) {
        /*
            Method dump skipped, instructions count: 3348
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

    public void setDrawSelectionBackground(boolean value) {
        if (this.drawSelectionBackground != value) {
            this.drawSelectionBackground = value;
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

    public void setCheckBoxVisible(boolean visible, boolean animated) {
        MessageObject.GroupedMessages groupedMessages;
        MessageObject.GroupedMessages groupedMessages2;
        if (visible && this.checkBox == null) {
            CheckBoxBase checkBoxBase = new CheckBoxBase(this, 21, this.resourcesProvider);
            this.checkBox = checkBoxBase;
            if (this.attachedToWindow) {
                checkBoxBase.onAttachedToWindow();
            }
        }
        if (visible && this.mediaCheckBox == null && (((groupedMessages = this.currentMessagesGroup) != null && groupedMessages.messages.size() > 1) || ((groupedMessages2 = this.groupedMessagesToSet) != null && groupedMessages2.messages.size() > 1))) {
            CheckBoxBase checkBoxBase2 = new CheckBoxBase(this, 21, this.resourcesProvider);
            this.mediaCheckBox = checkBoxBase2;
            checkBoxBase2.setUseDefaultCheck(true);
            if (this.attachedToWindow) {
                this.mediaCheckBox.onAttachedToWindow();
            }
        }
        float f = 1.0f;
        if (this.checkBoxVisible == visible) {
            if (animated != this.checkBoxAnimationInProgress && !animated) {
                if (!visible) {
                    f = 0.0f;
                }
                this.checkBoxAnimationProgress = f;
                invalidate();
                return;
            }
            return;
        }
        this.checkBoxAnimationInProgress = animated;
        this.checkBoxVisible = visible;
        if (animated) {
            this.lastCheckBoxAnimationTime = SystemClock.elapsedRealtime();
        } else {
            if (!visible) {
                f = 0.0f;
            }
            this.checkBoxAnimationProgress = f;
        }
        invalidate();
    }

    public void setChecked(boolean checked, boolean allChecked, boolean animated) {
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.setChecked(allChecked, animated);
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.setChecked(checked, animated);
        }
        this.backgroundDrawable.setSelected(allChecked, animated);
    }

    public void setLastTouchCoords(float x, float y) {
        this.lastTouchX = x;
        this.lastTouchY = y;
        this.backgroundDrawable.setTouchCoords(getTranslationX() + x, this.lastTouchY);
    }

    public MessageBackgroundDrawable getBackgroundDrawable() {
        return this.backgroundDrawable;
    }

    public Theme.MessageDrawable getCurrentBackgroundDrawable(boolean update) {
        if (update) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            boolean forceMediaByGroup = groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments && !this.drawPinnedBottom;
            if (this.currentMessageObject.isOutOwner()) {
                if (!this.mediaBackground && !this.drawPinnedBottom && !forceMediaByGroup) {
                    this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOut);
                } else {
                    this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(Theme.key_drawable_msgOutMedia);
                }
            } else if (!this.mediaBackground && !this.drawPinnedBottom && !forceMediaByGroup) {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(Theme.key_drawable_msgIn);
            } else {
                this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(Theme.key_drawable_msgInMedia);
            }
        }
        this.currentBackgroundDrawable.getBackgroundDrawable();
        return this.currentBackgroundDrawable;
    }

    public void drawCaptionLayout(Canvas canvas, boolean selectionOnly, float alpha) {
        float f = 1.0f;
        if (!this.transitionParams.animateReplaceCaptionLayout || this.transitionParams.animateChangeProgress == 1.0f) {
            drawCaptionLayout(canvas, this.captionLayout, selectionOnly, alpha);
        } else {
            drawCaptionLayout(canvas, this.transitionParams.animateOutCaptionLayout, selectionOnly, (1.0f - this.transitionParams.animateChangeProgress) * alpha);
            drawCaptionLayout(canvas, this.captionLayout, selectionOnly, this.transitionParams.animateChangeProgress * alpha);
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.messageOwner != null && this.currentMessageObject.isVoiceTranscriptionOpen() && !this.currentMessageObject.messageOwner.voiceTranscriptionFinal && TranscribeButton.isTranscribing(this.currentMessageObject)) {
            invalidate();
        }
        if (!selectionOnly) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if ((groupedMessagePosition == null || ((groupedMessagePosition.flags & 8) != 0 && (this.currentPosition.flags & 1) != 0)) && !this.reactionsLayoutInBubble.isSmall) {
                if (this.reactionsLayoutInBubble.drawServiceShaderBackground) {
                    applyServiceShaderMatrix();
                }
                if (this.reactionsLayoutInBubble.drawServiceShaderBackground || !this.transitionParams.animateBackgroundBoundsInner || this.currentPosition != null) {
                    ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
                    if (this.transitionParams.animateChange) {
                        f = this.transitionParams.animateChangeProgress;
                    }
                    reactionsLayoutInBubble.draw(canvas, f, null);
                    return;
                }
                canvas.save();
                canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), getBackgroundDrawableBottom() + this.transitionParams.deltaBottom);
                ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
                if (this.transitionParams.animateChange) {
                    f = this.transitionParams.animateChangeProgress;
                }
                reactionsLayoutInBubble2.draw(canvas, f, null);
                canvas.restore();
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:301:0x07d1  */
    /* JADX WARN: Removed duplicated region for block: B:302:0x07f1  */
    /* JADX WARN: Removed duplicated region for block: B:331:0x08cd  */
    /* JADX WARN: Removed duplicated region for block: B:334:0x08d8  */
    /* JADX WARN: Removed duplicated region for block: B:364:0x097a  */
    /* JADX WARN: Removed duplicated region for block: B:367:0x0982  */
    /* JADX WARN: Removed duplicated region for block: B:373:0x08f3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawCaptionLayout(android.graphics.Canvas r35, android.text.StaticLayout r36, boolean r37, float r38) {
        /*
            Method dump skipped, instructions count: 2442
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
            return this.reactionsLayoutInBubble.isEmpty || this.reactionsLayoutInBubble.isSmall || this.currentMessageObject.isAnyKindOfSticker() || this.currentMessageObject.isRoundVideo();
        }
    }

    public void drawTime(Canvas canvas, float alpha, boolean fromParent) {
        ChatMessageCell chatMessageCell;
        ChatMessageCell chatMessageCell2;
        int i;
        float currentAlpha;
        float timeWidth;
        float timeX;
        int i2;
        ChatMessageCellDelegate chatMessageCellDelegate;
        ChatMessageCell chatMessageCell3 = this;
        if (!chatMessageCell3.drawFromPinchToZoom && (chatMessageCellDelegate = chatMessageCell3.delegate) != null && chatMessageCellDelegate.getPinchToZoomHelper() != null && chatMessageCell3.delegate.getPinchToZoomHelper().isInOverlayModeFor(chatMessageCell3) && shouldDrawTimeOnMedia()) {
            return;
        }
        int i3 = 0;
        while (true) {
            if (i3 >= 2) {
                chatMessageCell = chatMessageCell3;
                break;
            }
            if (i3 == 0 && isDrawSelectionBackground() && chatMessageCell3.currentSelectedBackgroundAlpha == 1.0f && !shouldDrawTimeOnMedia()) {
                i = i3;
                chatMessageCell2 = chatMessageCell3;
            } else {
                if (i3 == 1) {
                    if (!isDrawSelectionBackground() && chatMessageCell3.currentSelectedBackgroundAlpha == 0.0f) {
                        chatMessageCell = chatMessageCell3;
                        break;
                    } else if (shouldDrawTimeOnMedia()) {
                        chatMessageCell = chatMessageCell3;
                        break;
                    }
                }
                boolean drawSelectionBackground = i3 == 1;
                if (i3 == 1) {
                    currentAlpha = alpha * chatMessageCell3.currentSelectedBackgroundAlpha;
                } else {
                    currentAlpha = !shouldDrawTimeOnMedia() ? alpha * (1.0f - chatMessageCell3.currentSelectedBackgroundAlpha) : alpha;
                }
                if (!chatMessageCell3.transitionParams.animateShouldDrawTimeOnMedia || chatMessageCell3.transitionParams.animateChangeProgress == 1.0f) {
                    i = i3;
                    chatMessageCell2 = chatMessageCell3;
                    if (chatMessageCell2.transitionParams.shouldAnimateTimeX) {
                        timeX = (chatMessageCell2.timeX * chatMessageCell2.transitionParams.animateChangeProgress) + (chatMessageCell2.transitionParams.animateFromTimeX * (1.0f - chatMessageCell2.transitionParams.animateChangeProgress));
                        timeWidth = (chatMessageCell2.timeWidth * chatMessageCell2.transitionParams.animateChangeProgress) + (chatMessageCell2.transitionParams.animateTimeWidth * (1.0f - chatMessageCell2.transitionParams.animateChangeProgress));
                    } else {
                        timeX = chatMessageCell2.timeX + chatMessageCell2.transitionParams.deltaRight;
                        timeWidth = chatMessageCell2.timeWidth;
                    }
                    drawTimeInternal(canvas, currentAlpha, fromParent, timeX, chatMessageCell2.timeLayout, timeWidth, drawSelectionBackground);
                } else {
                    if (shouldDrawTimeOnMedia()) {
                        chatMessageCell3.overideShouldDrawTimeOnMedia = 1;
                        drawTimeInternal(canvas, currentAlpha * chatMessageCell3.transitionParams.animateChangeProgress, fromParent, chatMessageCell3.timeX, chatMessageCell3.timeLayout, chatMessageCell3.timeWidth, drawSelectionBackground);
                        chatMessageCell3.overideShouldDrawTimeOnMedia = 2;
                        i2 = 0;
                        i = i3;
                        chatMessageCell2 = chatMessageCell3;
                        drawTimeInternal(canvas, currentAlpha * (1.0f - chatMessageCell3.transitionParams.animateChangeProgress), fromParent, chatMessageCell3.transitionParams.animateFromTimeX, chatMessageCell3.transitionParams.animateTimeLayout, chatMessageCell3.transitionParams.animateTimeWidth, drawSelectionBackground);
                    } else {
                        i = i3;
                        chatMessageCell2 = chatMessageCell3;
                        i2 = 0;
                        chatMessageCell2.overideShouldDrawTimeOnMedia = 2;
                        boolean z = drawSelectionBackground;
                        drawTimeInternal(canvas, currentAlpha * chatMessageCell2.transitionParams.animateChangeProgress, fromParent, chatMessageCell2.timeX, chatMessageCell2.timeLayout, chatMessageCell2.timeWidth, z);
                        chatMessageCell2.overideShouldDrawTimeOnMedia = 1;
                        drawTimeInternal(canvas, currentAlpha * (1.0f - chatMessageCell2.transitionParams.animateChangeProgress), fromParent, chatMessageCell2.transitionParams.animateFromTimeX, chatMessageCell2.transitionParams.animateTimeLayout, chatMessageCell2.transitionParams.animateTimeWidth, z);
                    }
                    chatMessageCell2.overideShouldDrawTimeOnMedia = i2;
                }
            }
            i3 = i + 1;
            chatMessageCell3 = chatMessageCell2;
        }
        if (chatMessageCell.transitionParams.animateBackgroundBoundsInner) {
            drawOverlays(canvas);
        }
    }

    private void drawTimeInternal(Canvas canvas, float alpha, boolean fromParent, float timeX, StaticLayout timeLayout, float timeWidth, boolean drawSelectionBackground) {
        float alpha2;
        float timeX2;
        float layoutHeight;
        float timeX3;
        float timeTitleTimeX;
        boolean bigRadius;
        int timeYOffset;
        int currentStatus;
        boolean needRestore;
        ChatMessageCell chatMessageCell;
        float additionalX;
        int currentStatus2;
        Paint paint;
        int r;
        float additionalX2;
        int currentStatus3;
        float additionalX3;
        if (((this.drawTime && !this.groupPhotoInvisible) || !shouldDrawTimeOnMedia()) && timeLayout != null) {
            if ((this.currentMessageObject.deleted && this.currentPosition != null) || this.currentMessageObject.type == 16) {
                return;
            }
            if (this.currentMessageObject.type == 5) {
                Theme.chat_timePaint.setColor(getThemedColor(Theme.key_chat_serviceText));
            } else if (shouldDrawTimeOnMedia()) {
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    Theme.chat_timePaint.setColor(getThemedColor(Theme.key_chat_serviceText));
                } else {
                    Theme.chat_timePaint.setColor(getThemedColor(Theme.key_chat_mediaTimeText));
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_timePaint.setColor(getThemedColor(drawSelectionBackground ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText));
            } else {
                Theme.chat_timePaint.setColor(getThemedColor(drawSelectionBackground ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText));
            }
            if (!getTransitionParams().animateDrawingTimeAlpha) {
                alpha2 = alpha;
            } else {
                alpha2 = getTransitionParams().animateChangeProgress * alpha;
            }
            if (alpha2 != 1.0f) {
                Theme.chat_timePaint.setAlpha((int) (Theme.chat_timePaint.getAlpha() * alpha2));
            }
            canvas.save();
            if (this.drawPinnedBottom && !shouldDrawTimeOnMedia()) {
                canvas.translate(0.0f, AndroidUtilities.dp(2.0f));
            }
            boolean bigRadius2 = false;
            float layoutHeight2 = this.layoutHeight + this.transitionParams.deltaBottom;
            float timeTitleTimeX2 = timeX;
            if (this.transitionParams.shouldAnimateTimeX) {
                timeTitleTimeX2 = (this.transitionParams.animateFromTimeX * (1.0f - this.transitionParams.animateChangeProgress)) + (this.timeX * this.transitionParams.animateChangeProgress);
            }
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages != null && groupedMessages.transitionParams.backgroundChangeBounds) {
                layoutHeight2 -= getTranslationY();
                timeX2 = timeX + this.currentMessagesGroup.transitionParams.offsetRight;
                timeTitleTimeX2 += this.currentMessagesGroup.transitionParams.offsetRight;
            } else {
                timeX2 = timeX;
            }
            if (this.drawPinnedBottom && shouldDrawTimeOnMedia()) {
                layoutHeight = layoutHeight2 + AndroidUtilities.dp(1.0f);
            } else {
                layoutHeight = layoutHeight2;
            }
            if (!this.transitionParams.animateBackgroundBoundsInner) {
                timeX3 = timeX2;
            } else {
                float f = this.animationOffsetX;
                timeTitleTimeX2 += f;
                timeX3 = timeX2 + f;
            }
            if (this.reactionsLayoutInBubble.isSmall) {
                if (!this.transitionParams.animateBackgroundBoundsInner || this.transitionParams.deltaRight == 0.0f) {
                    timeTitleTimeX2 += this.reactionsLayoutInBubble.getCurrentWidth(this.transitionParams.animateChangeProgress);
                } else {
                    timeTitleTimeX2 += this.reactionsLayoutInBubble.getCurrentWidth(1.0f);
                }
            }
            if (this.transitionParams.animateEditedEnter) {
                timeTitleTimeX = timeTitleTimeX2 - (this.transitionParams.animateEditedWidthDiff * (1.0f - this.transitionParams.animateChangeProgress));
            } else {
                timeTitleTimeX = timeTitleTimeX2;
            }
            boolean outDrawError = true;
            if (!shouldDrawTimeOnMedia()) {
                if (this.currentMessageObject.isSponsored()) {
                    int timeYOffset2 = -AndroidUtilities.dp(48.0f);
                    if (!this.hasNewLineForTime) {
                        timeYOffset = timeYOffset2;
                    } else {
                        timeYOffset = timeYOffset2 - AndroidUtilities.dp(16.0f);
                    }
                } else {
                    timeYOffset = -(this.drawCommentButton ? AndroidUtilities.dp(43.0f) : 0);
                }
                float additionalX4 = -timeLayout.getLineLeft(0);
                if (this.reactionsLayoutInBubble.isSmall) {
                    updateReactionLayoutPosition();
                    this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, null);
                }
                if ((ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) || (this.currentMessageObject.messageOwner.flags & 1024) != 0 || this.repliesLayout != null || this.transitionParams.animateReplies || this.isPinned || this.transitionParams.animatePinned) {
                    float additionalX5 = additionalX4 + (this.timeWidth - timeLayout.getLineWidth(0));
                    if (this.reactionsLayoutInBubble.isSmall && !this.reactionsLayoutInBubble.isEmpty) {
                        additionalX = additionalX5 - this.reactionsLayoutInBubble.width;
                    } else {
                        additionalX = additionalX5;
                    }
                    int currentStatus4 = this.transitionParams.createStatusDrawableParams();
                    if (this.transitionParams.lastStatusDrawableParams >= 0 && this.transitionParams.lastStatusDrawableParams != currentStatus4 && !this.statusDrawableAnimationInProgress) {
                        createStatusDrawableAnimator(this.transitionParams.lastStatusDrawableParams, currentStatus4, fromParent);
                    }
                    boolean z = this.statusDrawableAnimationInProgress;
                    if (!z) {
                        currentStatus2 = currentStatus4;
                    } else {
                        currentStatus2 = this.animateToStatusDrawableParams;
                    }
                    boolean drawClock = (currentStatus2 & 4) != 0;
                    boolean drawError = (currentStatus2 & 8) != 0;
                    if (z) {
                        int i = this.animateFromStatusDrawableParams;
                        boolean outDrawClock = (i & 4) != 0;
                        boolean outDrawError2 = (i & 8) != 0;
                        float f2 = layoutHeight;
                        float f3 = alpha2;
                        float f4 = timeX3;
                        drawClockOrErrorLayout(canvas, outDrawClock, outDrawError2, f2, f3, timeYOffset, f4, 1.0f - this.statusDrawableProgress, drawSelectionBackground);
                        drawClockOrErrorLayout(canvas, drawClock, drawError, f2, f3, timeYOffset, f4, this.statusDrawableProgress, drawSelectionBackground);
                        if (!this.currentMessageObject.isOutOwner()) {
                            if (!outDrawClock && !outDrawError2) {
                                drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f - this.statusDrawableProgress, drawSelectionBackground);
                            }
                            if (!drawClock && !drawError) {
                                drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, this.statusDrawableProgress, drawSelectionBackground);
                            }
                        }
                    } else {
                        if (!this.currentMessageObject.isOutOwner() && !drawClock && !drawError) {
                            drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f, drawSelectionBackground);
                        }
                        drawClockOrErrorLayout(canvas, drawClock, drawError, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f, drawSelectionBackground);
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f, drawSelectionBackground);
                    }
                    TransitionParams transitionParams = this.transitionParams;
                    transitionParams.lastStatusDrawableParams = transitionParams.createStatusDrawableParams();
                    if (drawClock && fromParent && getParent() != null) {
                        ((View) getParent()).invalidate();
                    }
                    additionalX4 = additionalX;
                }
                canvas.save();
                float f5 = 6.5f;
                if (!this.transitionParams.animateEditedEnter || this.transitionParams.animateChangeProgress == 1.0f) {
                    float f6 = timeTitleTimeX + additionalX4;
                    this.drawTimeX = f6;
                    if (this.pinnedBottom || this.pinnedTop) {
                        f5 = 7.5f;
                    }
                    float dp = ((layoutHeight - AndroidUtilities.dp(f5)) - timeLayout.getHeight()) + timeYOffset;
                    this.drawTimeY = dp;
                    canvas.translate(f6, dp);
                    timeLayout.draw(canvas);
                } else if (this.transitionParams.animateEditedLayout != null) {
                    float f7 = timeTitleTimeX + additionalX4;
                    if (this.pinnedBottom || this.pinnedTop) {
                        f5 = 7.5f;
                    }
                    canvas.translate(f7, ((layoutHeight - AndroidUtilities.dp(f5)) - timeLayout.getHeight()) + timeYOffset);
                    int oldAlpha = Theme.chat_timePaint.getAlpha();
                    Theme.chat_timePaint.setAlpha((int) (oldAlpha * this.transitionParams.animateChangeProgress));
                    this.transitionParams.animateEditedLayout.draw(canvas);
                    Theme.chat_timePaint.setAlpha(oldAlpha);
                    this.transitionParams.animateTimeLayout.draw(canvas);
                } else {
                    int oldAlpha2 = Theme.chat_timePaint.getAlpha();
                    canvas.save();
                    canvas.translate(this.transitionParams.animateFromTimeX + additionalX4, ((layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 7.5f : 6.5f)) - timeLayout.getHeight()) + timeYOffset);
                    Theme.chat_timePaint.setAlpha((int) (oldAlpha2 * (1.0f - this.transitionParams.animateChangeProgress)));
                    this.transitionParams.animateTimeLayout.draw(canvas);
                    canvas.restore();
                    float f8 = timeTitleTimeX + additionalX4;
                    if (this.pinnedBottom || this.pinnedTop) {
                        f5 = 7.5f;
                    }
                    canvas.translate(f8, ((layoutHeight - AndroidUtilities.dp(f5)) - timeLayout.getHeight()) + timeYOffset);
                    Theme.chat_timePaint.setAlpha((int) (oldAlpha2 * this.transitionParams.animateChangeProgress));
                    timeLayout.draw(canvas);
                    Theme.chat_timePaint.setAlpha(oldAlpha2);
                }
                canvas.restore();
                bigRadius = false;
            } else {
                int timeYOffset3 = -(this.drawCommentButton ? AndroidUtilities.dp(41.3f) : 0);
                if (!this.currentMessageObject.shouldDrawWithoutBackground()) {
                    paint = getThemedPaint(Theme.key_paint_chatTimeBackground);
                } else {
                    paint = getThemedPaint(Theme.key_paint_chatActionBackground);
                }
                int oldAlpha3 = paint.getAlpha();
                paint.setAlpha((int) (oldAlpha3 * this.timeAlpha * alpha2));
                Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f * alpha2));
                int i2 = this.documentAttachType;
                float f9 = 4.0f;
                if (i2 != 7 && i2 != 6) {
                    int[] rad = this.photoImage.getRoundRadius();
                    int r2 = Math.min(AndroidUtilities.dp(8.0f), Math.max(rad[2], rad[3]));
                    bigRadius2 = SharedConfig.bubbleRadius >= 10;
                    r = r2;
                } else {
                    r = AndroidUtilities.dp(4.0f);
                }
                if (bigRadius2) {
                    f9 = 6.0f;
                }
                float x1 = timeX3 - AndroidUtilities.dp(f9);
                float timeY = this.photoImage.getImageY2() + this.additionalTimeOffsetY;
                float y1 = timeY - AndroidUtilities.dp(23.0f);
                this.rect.set(x1, y1, x1 + timeWidth + AndroidUtilities.dp((bigRadius2 ? 12 : 8) + (this.currentMessageObject.isOutOwner() ? 20 : 0)), AndroidUtilities.dp(17.0f) + y1);
                applyServiceShaderMatrix();
                canvas.drawRoundRect(this.rect, r, r, paint);
                if (paint == getThemedPaint(Theme.key_paint_chatActionBackground) && hasGradientService()) {
                    int oldAlpha22 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (oldAlpha22 * this.timeAlpha * alpha2));
                    canvas.drawRoundRect(this.rect, r, r, Theme.chat_actionBackgroundGradientDarkenPaint);
                    Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(oldAlpha22);
                }
                paint.setAlpha(oldAlpha3);
                float additionalX6 = -timeLayout.getLineLeft(0);
                if (this.reactionsLayoutInBubble.isSmall) {
                    updateReactionLayoutPosition();
                    this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, null);
                }
                if ((!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) && (this.currentMessageObject.messageOwner.flags & 1024) == 0 && this.repliesLayout == null && !this.isPinned) {
                    timeYOffset = timeYOffset3;
                } else {
                    float additionalX7 = additionalX6 + (this.timeWidth - timeLayout.getLineWidth(0));
                    if (this.reactionsLayoutInBubble.isSmall && !this.reactionsLayoutInBubble.isEmpty) {
                        additionalX2 = additionalX7 - this.reactionsLayoutInBubble.width;
                    } else {
                        additionalX2 = additionalX7;
                    }
                    int currentStatus5 = this.transitionParams.createStatusDrawableParams();
                    if (this.transitionParams.lastStatusDrawableParams >= 0 && this.transitionParams.lastStatusDrawableParams != currentStatus5 && !this.statusDrawableAnimationInProgress) {
                        createStatusDrawableAnimator(this.transitionParams.lastStatusDrawableParams, currentStatus5, fromParent);
                    }
                    boolean z2 = this.statusDrawableAnimationInProgress;
                    if (!z2) {
                        currentStatus3 = currentStatus5;
                    } else {
                        currentStatus3 = this.animateToStatusDrawableParams;
                    }
                    boolean drawClock2 = (currentStatus3 & 4) != 0;
                    boolean drawError2 = (currentStatus3 & 8) != 0;
                    if (!z2) {
                        additionalX3 = additionalX2;
                        timeYOffset = timeYOffset3;
                        if (!this.currentMessageObject.isOutOwner() && !drawClock2 && !drawError2) {
                            drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f, drawSelectionBackground);
                        }
                        drawClockOrErrorLayout(canvas, drawClock2, drawError2, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f, drawSelectionBackground);
                    } else {
                        int i3 = this.animateFromStatusDrawableParams;
                        boolean outDrawClock2 = (i3 & 4) != 0;
                        boolean outDrawError3 = (i3 & 8) != 0;
                        float y12 = layoutHeight;
                        float x12 = alpha2;
                        float f10 = timeX3;
                        additionalX3 = additionalX2;
                        timeYOffset = timeYOffset3;
                        drawClockOrErrorLayout(canvas, outDrawClock2, outDrawError3, y12, x12, timeYOffset3, f10, 1.0f - this.statusDrawableProgress, drawSelectionBackground);
                        drawClockOrErrorLayout(canvas, drawClock2, drawError2, y12, x12, timeYOffset, f10, this.statusDrawableProgress, drawSelectionBackground);
                        if (!this.currentMessageObject.isOutOwner()) {
                            if (!outDrawClock2 && !outDrawError3) {
                                drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f - this.statusDrawableProgress, drawSelectionBackground);
                            }
                            if (!drawClock2 && !drawError2) {
                                drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, this.statusDrawableProgress, drawSelectionBackground);
                            }
                        }
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        drawViewsAndRepliesLayout(canvas, layoutHeight, alpha2, timeYOffset, timeX3, 1.0f, drawSelectionBackground);
                    }
                    TransitionParams transitionParams2 = this.transitionParams;
                    transitionParams2.lastStatusDrawableParams = transitionParams2.createStatusDrawableParams();
                    if (drawClock2 && fromParent && getParent() != null) {
                        ((View) getParent()).invalidate();
                    }
                    additionalX6 = additionalX3;
                }
                canvas.save();
                float f11 = timeTitleTimeX + additionalX6;
                this.drawTimeX = f11;
                float dp2 = (timeY - AndroidUtilities.dp(7.3f)) - timeLayout.getHeight();
                this.drawTimeY = dp2;
                canvas.translate(f11, dp2);
                timeLayout.draw(canvas);
                canvas.restore();
                Theme.chat_timePaint.setAlpha(255);
                bigRadius = bigRadius2;
            }
            if (this.currentMessageObject.isOutOwner()) {
                int currentStatus6 = this.transitionParams.createStatusDrawableParams();
                if (this.transitionParams.lastStatusDrawableParams >= 0 && this.transitionParams.lastStatusDrawableParams != currentStatus6 && !this.statusDrawableAnimationInProgress) {
                    createStatusDrawableAnimator(this.transitionParams.lastStatusDrawableParams, currentStatus6, fromParent);
                }
                if (!this.statusDrawableAnimationInProgress) {
                    currentStatus = currentStatus6;
                } else {
                    currentStatus = this.animateToStatusDrawableParams;
                }
                boolean drawCheck1 = (currentStatus & 1) != 0;
                boolean drawCheck2 = (currentStatus & 2) != 0;
                boolean drawClock3 = (currentStatus & 4) != 0;
                boolean drawError3 = (currentStatus & 8) != 0;
                if (this.transitionYOffsetForDrawables == 0.0f) {
                    needRestore = false;
                } else {
                    canvas.save();
                    canvas.translate(0.0f, this.transitionYOffsetForDrawables);
                    needRestore = true;
                }
                boolean needRestore2 = this.statusDrawableAnimationInProgress;
                if (!needRestore2) {
                    chatMessageCell = this;
                    drawStatusDrawable(canvas, drawCheck1, drawCheck2, drawClock3, drawError3, alpha2, bigRadius, timeYOffset, layoutHeight, 1.0f, false, drawSelectionBackground);
                } else {
                    int i4 = this.animateFromStatusDrawableParams;
                    boolean outDrawCheck1 = (i4 & 1) != 0;
                    boolean outDrawCheck2 = (i4 & 2) != 0;
                    boolean outDrawClock3 = (i4 & 4) != 0;
                    if ((i4 & 8) == 0) {
                        outDrawError = false;
                    }
                    if (!outDrawClock3 && outDrawCheck2 && drawCheck2 && !outDrawCheck1 && drawCheck1) {
                        drawStatusDrawable(canvas, drawCheck1, drawCheck2, drawClock3, drawError3, alpha2, bigRadius, timeYOffset, layoutHeight, this.statusDrawableProgress, true, drawSelectionBackground);
                        chatMessageCell = this;
                    } else {
                        int timeYOffset4 = timeYOffset;
                        chatMessageCell = this;
                        drawStatusDrawable(canvas, outDrawCheck1, outDrawCheck2, outDrawClock3, outDrawError, alpha2, bigRadius, timeYOffset4, layoutHeight, 1.0f - this.statusDrawableProgress, false, drawSelectionBackground);
                        drawStatusDrawable(canvas, drawCheck1, drawCheck2, drawClock3, drawError3, alpha2, bigRadius, timeYOffset4, layoutHeight, chatMessageCell.statusDrawableProgress, false, drawSelectionBackground);
                    }
                }
                if (needRestore) {
                    canvas.restore();
                }
                TransitionParams transitionParams3 = chatMessageCell.transitionParams;
                transitionParams3.lastStatusDrawableParams = transitionParams3.createStatusDrawableParams();
                if (fromParent && drawClock3 && getParent() != null) {
                    ((View) getParent()).invalidate();
                }
            }
            canvas.restore();
        }
    }

    public void createStatusDrawableAnimator(int lastStatusDrawableParams, int currentStatus, final boolean fromParent) {
        boolean moveCheckTransition = false;
        boolean drawCheck1 = (currentStatus & 1) != 0;
        boolean drawCheck2 = (currentStatus & 2) != 0;
        boolean outDrawCheck1 = (lastStatusDrawableParams & 1) != 0;
        boolean outDrawCheck2 = (lastStatusDrawableParams & 2) != 0;
        boolean outDrawClock = (lastStatusDrawableParams & 4) != 0;
        if (!outDrawClock && outDrawCheck2 && drawCheck2 && !outDrawCheck1 && drawCheck1) {
            moveCheckTransition = true;
        }
        if (this.transitionParams.messageEntering && !moveCheckTransition) {
            return;
        }
        this.statusDrawableProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.statusDrawableAnimator = ofFloat;
        if (moveCheckTransition) {
            ofFloat.setDuration(220L);
        } else {
            ofFloat.setDuration(150L);
        }
        this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animateFromStatusDrawableParams = lastStatusDrawableParams;
        this.animateToStatusDrawableParams = currentStatus;
        this.statusDrawableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatMessageCell.this.m1639x59af0ffd(fromParent, valueAnimator);
            }
        });
        this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ChatMessageCell.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                int currentStatus2 = ChatMessageCell.this.transitionParams.createStatusDrawableParams();
                if (ChatMessageCell.this.animateToStatusDrawableParams == currentStatus2) {
                    ChatMessageCell.this.statusDrawableAnimationInProgress = false;
                    ChatMessageCell.this.transitionParams.lastStatusDrawableParams = ChatMessageCell.this.animateToStatusDrawableParams;
                    return;
                }
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                chatMessageCell.createStatusDrawableAnimator(chatMessageCell.animateToStatusDrawableParams, currentStatus2, fromParent);
            }
        });
        this.statusDrawableAnimationInProgress = true;
        this.statusDrawableAnimator.start();
    }

    /* renamed from: lambda$createStatusDrawableAnimator$6$org-telegram-ui-Cells-ChatMessageCell */
    public /* synthetic */ void m1639x59af0ffd(boolean fromParent, ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (fromParent && getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    private void drawClockOrErrorLayout(Canvas canvas, boolean drawTime, boolean drawError, float layoutHeight, float alpha, float timeYOffset, float timeX, float progress, boolean drawSelectionBackground) {
        float y;
        int clockColor;
        float timeY;
        int i = 0;
        boolean useScale = progress != 1.0f;
        float scale = (progress * 0.5f) + 0.5f;
        float alpha2 = alpha * progress;
        if (drawTime) {
            if (!this.currentMessageObject.isOutOwner()) {
                MsgClockDrawable clockDrawable = Theme.chat_msgClockDrawable;
                boolean shouldDrawTimeOnMedia = shouldDrawTimeOnMedia();
                String str = Theme.key_chat_mediaSentClock;
                if (shouldDrawTimeOnMedia) {
                    clockColor = getThemedColor(str);
                } else {
                    if (drawSelectionBackground) {
                        str = Theme.key_chat_outSentClockSelected;
                    }
                    clockColor = getThemedColor(str);
                }
                clockDrawable.setColor(clockColor);
                if (shouldDrawTimeOnMedia()) {
                    timeY = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(9.0f);
                } else {
                    timeY = (layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.5f : 8.5f)) + timeYOffset;
                }
                if (!this.currentMessageObject.scheduled) {
                    i = AndroidUtilities.dp(11.0f);
                }
                setDrawableBounds(clockDrawable, timeX + i, timeY - clockDrawable.getIntrinsicHeight());
                clockDrawable.setAlpha((int) (255.0f * alpha2));
                if (useScale) {
                    canvas.save();
                    canvas.scale(scale, scale, clockDrawable.getBounds().centerX(), clockDrawable.getBounds().centerY());
                }
                clockDrawable.draw(canvas);
                clockDrawable.setAlpha(255);
                invalidate();
                if (useScale) {
                    canvas.restore();
                }
            }
        } else if (drawError && !this.currentMessageObject.isOutOwner()) {
            if (!this.currentMessageObject.scheduled) {
                i = AndroidUtilities.dp(11.0f);
            }
            float x = timeX + i;
            float f = 21.5f;
            if (shouldDrawTimeOnMedia()) {
                y = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(21.5f);
            } else {
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f = 20.5f;
                }
                y = (layoutHeight - AndroidUtilities.dp(f)) + timeYOffset;
            }
            this.rect.set(x, y, AndroidUtilities.dp(14.0f) + x, AndroidUtilities.dp(14.0f) + y);
            int oldAlpha = Theme.chat_msgErrorPaint.getAlpha();
            Theme.chat_msgErrorPaint.setAlpha((int) (alpha2 * 255.0f));
            if (useScale) {
                canvas.save();
                canvas.scale(scale, scale, this.rect.centerX(), this.rect.centerY());
            }
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
            Theme.chat_msgErrorPaint.setAlpha(oldAlpha);
            Drawable errorDrawable = getThemedDrawable(Theme.key_drawable_msgError);
            setDrawableBounds(errorDrawable, AndroidUtilities.dp(6.0f) + x, AndroidUtilities.dp(2.0f) + y);
            errorDrawable.setAlpha((int) (255.0f * alpha2));
            errorDrawable.draw(canvas);
            errorDrawable.setAlpha(255);
            if (useScale) {
                canvas.restore();
            }
        }
    }

    private void drawViewsAndRepliesLayout(Canvas canvas, float layoutHeight, float alpha, float timeYOffset, float timeX, float progress, boolean drawSelectionBackground) {
        float timeY;
        float alpha2;
        float scale;
        float scale2;
        float pinnedX;
        Drawable pinnedDrawable;
        float viewsX;
        Drawable viewsDrawable;
        float y;
        float repliesX;
        Drawable repliesDrawable;
        boolean useScale = progress != 1.0f;
        float scale3 = (progress * 0.5f) + 0.5f;
        float alpha3 = alpha * progress;
        float offsetX = this.reactionsLayoutInBubble.isSmall ? this.reactionsLayoutInBubble.getCurrentWidth(1.0f) : 0.0f;
        int timeAlpha = Theme.chat_timePaint.getAlpha();
        if (shouldDrawTimeOnMedia()) {
            timeY = ((this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(7.3f)) - this.timeLayout.getHeight();
        } else {
            timeY = ((layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 7.5f : 6.5f)) - this.timeLayout.getHeight()) + timeYOffset;
        }
        if (this.repliesLayout != null || this.transitionParams.animateReplies) {
            float repliesX2 = (this.transitionParams.shouldAnimateTimeX ? this.timeX : timeX) + offsetX;
            boolean inAnimation = this.transitionParams.animateReplies && this.transitionParams.animateRepliesLayout == null && this.repliesLayout != null;
            boolean outAnimation = this.transitionParams.animateReplies && this.transitionParams.animateRepliesLayout != null && this.repliesLayout == null;
            boolean replaceAnimation = (!this.transitionParams.animateReplies || this.transitionParams.animateRepliesLayout == null || this.repliesLayout == null) ? false : true;
            if (this.transitionParams.shouldAnimateTimeX && !inAnimation) {
                if (!outAnimation) {
                    repliesX = (this.transitionParams.animateFromTimeXReplies * (1.0f - this.transitionParams.animateChangeProgress)) + (this.transitionParams.animateChangeProgress * repliesX2);
                } else {
                    repliesX = this.transitionParams.animateFromTimeXReplies;
                }
            } else {
                repliesX = repliesX2 + this.transitionParams.deltaRight;
            }
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages != null && groupedMessages.transitionParams.backgroundChangeBounds) {
                repliesX += this.currentMessagesGroup.transitionParams.offsetRight;
            }
            if (this.transitionParams.animateBackgroundBoundsInner) {
                repliesX += this.animationOffsetX;
            }
            if (shouldDrawTimeOnMedia()) {
                repliesDrawable = this.currentMessageObject.shouldDrawWithoutBackground() ? getThemedDrawable(Theme.key_drawable_msgStickerReplies) : Theme.chat_msgMediaRepliesDrawable;
            } else if (this.currentMessageObject.isOutOwner()) {
                repliesDrawable = getThemedDrawable("drawableMsgOutReplies");
            } else {
                repliesDrawable = drawSelectionBackground ? Theme.chat_msgInRepliesSelectedDrawable : Theme.chat_msgInRepliesDrawable;
            }
            setDrawableBounds(repliesDrawable, repliesX, timeY);
            float repliesAlpha = alpha3;
            if (inAnimation) {
                repliesAlpha *= this.transitionParams.animateChangeProgress;
            } else if (outAnimation) {
                repliesAlpha *= 1.0f - this.transitionParams.animateChangeProgress;
            }
            repliesDrawable.setAlpha((int) (repliesAlpha * 255.0f));
            if (useScale) {
                canvas.save();
                float cx = (((repliesDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0f)) + this.repliesTextWidth) / 2.0f) + repliesX;
                canvas.scale(scale3, scale3, cx, repliesDrawable.getBounds().centerY());
            }
            repliesDrawable.draw(canvas);
            repliesDrawable.setAlpha(255);
            if (this.transitionParams.animateReplies) {
                if (replaceAnimation) {
                    canvas.save();
                    TextPaint textPaint = Theme.chat_timePaint;
                    double d = timeAlpha;
                    alpha2 = alpha3;
                    scale = scale3;
                    double d2 = this.transitionParams.animateChangeProgress;
                    Double.isNaN(d2);
                    Double.isNaN(d);
                    textPaint.setAlpha((int) (d * (1.0d - d2)));
                    canvas.translate(repliesDrawable.getIntrinsicWidth() + repliesX + AndroidUtilities.dp(3.0f), timeY);
                    this.transitionParams.animateRepliesLayout.draw(canvas);
                    canvas.restore();
                } else {
                    alpha2 = alpha3;
                    scale = scale3;
                }
                Theme.chat_timePaint.setAlpha((int) (timeAlpha * repliesAlpha));
            } else {
                alpha2 = alpha3;
                scale = scale3;
            }
            canvas.save();
            canvas.translate(repliesDrawable.getIntrinsicWidth() + repliesX + AndroidUtilities.dp(3.0f), timeY);
            StaticLayout staticLayout = this.repliesLayout;
            if (staticLayout != null) {
                staticLayout.draw(canvas);
            } else if (this.transitionParams.animateRepliesLayout != null) {
                this.transitionParams.animateRepliesLayout.draw(canvas);
            }
            canvas.restore();
            if (this.repliesLayout != null) {
                offsetX += repliesDrawable.getIntrinsicWidth() + this.repliesTextWidth + AndroidUtilities.dp(10.0f);
            }
            if (useScale) {
                canvas.restore();
            }
            if (this.transitionParams.animateReplies) {
                Theme.chat_timePaint.setAlpha(timeAlpha);
            }
            this.transitionParams.lastTimeXReplies = repliesX;
        } else {
            alpha2 = alpha3;
            scale = scale3;
        }
        if (this.viewsLayout == null) {
            scale2 = scale;
        } else {
            float viewsX2 = (this.transitionParams.shouldAnimateTimeX ? this.timeX : timeX) + offsetX;
            if (this.transitionParams.shouldAnimateTimeX) {
                viewsX = (this.transitionParams.animateFromTimeXViews * (1.0f - this.transitionParams.animateChangeProgress)) + (this.transitionParams.animateChangeProgress * viewsX2);
            } else {
                viewsX = this.transitionParams.deltaRight + viewsX2;
            }
            MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
            if (groupedMessages2 != null && groupedMessages2.transitionParams.backgroundChangeBounds) {
                viewsX += this.currentMessagesGroup.transitionParams.offsetRight;
            }
            if (this.transitionParams.animateBackgroundBoundsInner) {
                viewsX += this.animationOffsetX;
            }
            if (shouldDrawTimeOnMedia()) {
                viewsDrawable = this.currentMessageObject.shouldDrawWithoutBackground() ? getThemedDrawable(Theme.key_drawable_msgStickerViews) : Theme.chat_msgMediaViewsDrawable;
            } else if (this.currentMessageObject.isOutOwner()) {
                viewsDrawable = getThemedDrawable(drawSelectionBackground ? Theme.key_drawable_msgOutViewsSelected : Theme.key_drawable_msgOutViews);
            } else {
                viewsDrawable = drawSelectionBackground ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
            }
            float f = 5.5f;
            if (shouldDrawTimeOnMedia()) {
                y = ((this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(5.5f)) - this.timeLayout.getHeight();
            } else {
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f = 4.5f;
                }
                y = ((layoutHeight - AndroidUtilities.dp(f)) - this.timeLayout.getHeight()) + timeYOffset;
            }
            setDrawableBounds(viewsDrawable, viewsX, y);
            if (!useScale) {
                scale2 = scale;
            } else {
                canvas.save();
                float cx2 = (((viewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0f)) + this.viewsTextWidth) / 2.0f) + viewsX;
                scale2 = scale;
                canvas.scale(scale2, scale2, cx2, viewsDrawable.getBounds().centerY());
            }
            viewsDrawable.setAlpha((int) (alpha2 * 255.0f));
            viewsDrawable.draw(canvas);
            viewsDrawable.setAlpha(255);
            if (this.transitionParams.animateViewsLayout != null) {
                canvas.save();
                TextPaint textPaint2 = Theme.chat_timePaint;
                double d3 = timeAlpha;
                double d4 = this.transitionParams.animateChangeProgress;
                Double.isNaN(d4);
                Double.isNaN(d3);
                textPaint2.setAlpha((int) (d3 * (1.0d - d4)));
                canvas.translate(viewsDrawable.getIntrinsicWidth() + viewsX + AndroidUtilities.dp(3.0f), timeY);
                this.transitionParams.animateViewsLayout.draw(canvas);
                canvas.restore();
                Theme.chat_timePaint.setAlpha((int) (timeAlpha * this.transitionParams.animateChangeProgress));
            }
            canvas.save();
            canvas.translate(viewsDrawable.getIntrinsicWidth() + viewsX + AndroidUtilities.dp(3.0f), timeY);
            this.viewsLayout.draw(canvas);
            canvas.restore();
            if (useScale) {
                canvas.restore();
            }
            offsetX += this.viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(10.0f);
            if (this.transitionParams.animateViewsLayout != null) {
                Theme.chat_timePaint.setAlpha(timeAlpha);
            }
            this.transitionParams.lastTimeXViews = viewsX;
        }
        if (this.isPinned || this.transitionParams.animatePinned) {
            float pinnedX2 = (this.transitionParams.shouldAnimateTimeX ? this.timeX : timeX) + offsetX;
            boolean inAnimation2 = this.transitionParams.animatePinned && this.isPinned;
            boolean outAnimation2 = this.transitionParams.animatePinned && !this.isPinned;
            if (this.transitionParams.shouldAnimateTimeX && !inAnimation2) {
                if (!outAnimation2) {
                    pinnedX = (this.transitionParams.animateFromTimeXPinned * (1.0f - this.transitionParams.animateChangeProgress)) + (this.transitionParams.animateChangeProgress * pinnedX2);
                } else {
                    pinnedX = this.transitionParams.animateFromTimeXPinned;
                }
            } else {
                pinnedX = pinnedX2 + this.transitionParams.deltaRight;
            }
            MessageObject.GroupedMessages groupedMessages3 = this.currentMessagesGroup;
            if (groupedMessages3 != null && groupedMessages3.transitionParams.backgroundChangeBounds) {
                pinnedX += this.currentMessagesGroup.transitionParams.offsetRight;
            }
            if (this.transitionParams.animateBackgroundBoundsInner) {
                pinnedX += this.animationOffsetX;
            }
            if (shouldDrawTimeOnMedia()) {
                pinnedDrawable = this.currentMessageObject.shouldDrawWithoutBackground() ? getThemedDrawable(Theme.key_drawable_msgStickerPinned) : Theme.chat_msgMediaPinnedDrawable;
            } else if (this.currentMessageObject.isOutOwner()) {
                pinnedDrawable = getThemedDrawable(drawSelectionBackground ? Theme.key_drawable_msgOutPinnedSelected : Theme.key_drawable_msgOutPinned);
            } else {
                pinnedDrawable = drawSelectionBackground ? Theme.chat_msgInPinnedSelectedDrawable : Theme.chat_msgInPinnedDrawable;
            }
            if (this.transitionParams.animatePinned) {
                if (this.isPinned) {
                    pinnedDrawable.setAlpha((int) (alpha2 * 255.0f * this.transitionParams.animateChangeProgress));
                    setDrawableBounds(pinnedDrawable, pinnedX, timeY);
                } else {
                    pinnedDrawable.setAlpha((int) (alpha2 * 255.0f * (1.0f - this.transitionParams.animateChangeProgress)));
                    setDrawableBounds(pinnedDrawable, pinnedX, timeY);
                }
            } else {
                pinnedDrawable.setAlpha((int) (alpha2 * 255.0f));
                setDrawableBounds(pinnedDrawable, pinnedX, timeY);
            }
            if (useScale) {
                canvas.save();
                float cx3 = (pinnedDrawable.getIntrinsicWidth() / 2.0f) + pinnedX;
                canvas.scale(scale2, scale2, cx3, pinnedDrawable.getBounds().centerY());
            }
            pinnedDrawable.draw(canvas);
            pinnedDrawable.setAlpha(255);
            if (useScale) {
                canvas.restore();
            }
            this.transitionParams.lastTimeXPinned = pinnedX;
        }
    }

    private void drawStatusDrawable(Canvas canvas, boolean drawCheck1, boolean drawCheck2, boolean drawClock, boolean drawError, float alpha, boolean bigRadius, float timeYOffset, float layoutHeight, float progress, boolean moveCheck, boolean drawSelectionBackground) {
        float alpha2;
        float y;
        int x;
        Drawable drawable;
        Drawable drawable2;
        int color;
        boolean useScale = progress != 1.0f && !moveCheck;
        float scale = (progress * 0.5f) + 0.5f;
        if (!useScale) {
            alpha2 = alpha;
        } else {
            alpha2 = alpha * progress;
        }
        float timeY = (this.photoImage.getImageY2() + this.additionalTimeOffsetY) - AndroidUtilities.dp(8.5f);
        if (drawClock) {
            MsgClockDrawable drawable3 = Theme.chat_msgClockDrawable;
            if (!shouldDrawTimeOnMedia()) {
                int color2 = getThemedColor(Theme.key_chat_outSentClock);
                setDrawableBounds(drawable3, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable3.getIntrinsicWidth(), ((layoutHeight - AndroidUtilities.dp(8.5f)) - drawable3.getIntrinsicHeight()) + timeYOffset);
                drawable3.setAlpha((int) (alpha2 * 255.0f));
                color = color2;
            } else {
                float f = 24.0f;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    color = getThemedColor(Theme.key_chat_serviceText);
                    int i = this.layoutWidth;
                    if (!bigRadius) {
                        f = 22.0f;
                    }
                    setDrawableBounds(drawable3, (i - AndroidUtilities.dp(f)) - drawable3.getIntrinsicWidth(), (timeY - drawable3.getIntrinsicHeight()) + timeYOffset);
                    drawable3.setAlpha((int) (this.timeAlpha * 255.0f * alpha2));
                } else {
                    color = getThemedColor(Theme.key_chat_mediaSentClock);
                    int i2 = this.layoutWidth;
                    if (!bigRadius) {
                        f = 22.0f;
                    }
                    setDrawableBounds(drawable3, (i2 - AndroidUtilities.dp(f)) - drawable3.getIntrinsicWidth(), (timeY - drawable3.getIntrinsicHeight()) + timeYOffset);
                    drawable3.setAlpha((int) (alpha2 * 255.0f));
                }
            }
            drawable3.setColor(color);
            if (useScale) {
                canvas.save();
                canvas.scale(scale, scale, drawable3.getBounds().centerX(), drawable3.getBounds().centerY());
            }
            drawable3.draw(canvas);
            drawable3.setAlpha(255);
            if (useScale) {
                canvas.restore();
            }
            invalidate();
        }
        float f2 = 23.5f;
        float f3 = 9.0f;
        if (drawCheck2) {
            if (shouldDrawTimeOnMedia()) {
                if (moveCheck) {
                    canvas.save();
                }
                float f4 = 28.3f;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    drawable2 = getThemedDrawable(Theme.key_drawable_msgStickerCheck);
                    if (drawCheck1) {
                        if (moveCheck) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - progress), 0.0f);
                        }
                        int i3 = this.layoutWidth;
                        if (!bigRadius) {
                            f4 = 26.3f;
                        }
                        setDrawableBounds(drawable2, (i3 - AndroidUtilities.dp(f4)) - drawable2.getIntrinsicWidth(), (timeY - drawable2.getIntrinsicHeight()) + timeYOffset);
                    } else {
                        setDrawableBounds(drawable2, (this.layoutWidth - AndroidUtilities.dp(bigRadius ? 23.5f : 21.5f)) - drawable2.getIntrinsicWidth(), (timeY - drawable2.getIntrinsicHeight()) + timeYOffset);
                    }
                    drawable2.setAlpha((int) (this.timeAlpha * 255.0f * alpha2));
                } else {
                    if (drawCheck1) {
                        if (moveCheck) {
                            canvas.translate(AndroidUtilities.dp(4.8f) * (1.0f - progress), 0.0f);
                        }
                        Drawable drawable4 = Theme.chat_msgMediaCheckDrawable;
                        int i4 = this.layoutWidth;
                        if (!bigRadius) {
                            f4 = 26.3f;
                        }
                        setDrawableBounds(drawable4, (i4 - AndroidUtilities.dp(f4)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (timeY - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + timeYOffset);
                    } else {
                        setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(bigRadius ? 23.5f : 21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (timeY - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight()) + timeYOffset);
                    }
                    Theme.chat_msgMediaCheckDrawable.setAlpha((int) (this.timeAlpha * 255.0f * alpha2));
                    drawable2 = Theme.chat_msgMediaCheckDrawable;
                }
                if (useScale) {
                    canvas.save();
                    canvas.scale(scale, scale, drawable2.getBounds().centerX(), drawable2.getBounds().centerY());
                }
                drawable2.draw(canvas);
                if (useScale) {
                    canvas.restore();
                }
                if (moveCheck) {
                    canvas.restore();
                }
                drawable2.setAlpha(255);
            } else {
                if (moveCheck) {
                    canvas.save();
                }
                if (!drawCheck1) {
                    drawable = getThemedDrawable(drawSelectionBackground ? Theme.key_drawable_msgOutCheckSelected : Theme.key_drawable_msgOutCheck);
                    setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable.getIntrinsicWidth(), ((layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.0f : 8.0f)) - drawable.getIntrinsicHeight()) + timeYOffset);
                } else {
                    if (moveCheck) {
                        canvas.translate(AndroidUtilities.dp(4.0f) * (1.0f - progress), 0.0f);
                    }
                    drawable = getThemedDrawable(drawSelectionBackground ? Theme.key_drawable_msgOutCheckReadSelected : Theme.key_drawable_msgOutCheckRead);
                    setDrawableBounds(drawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable.getIntrinsicWidth(), ((layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 9.0f : 8.0f)) - drawable.getIntrinsicHeight()) + timeYOffset);
                }
                drawable.setAlpha((int) (alpha2 * 255.0f));
                if (useScale) {
                    canvas.save();
                    canvas.scale(scale, scale, drawable.getBounds().centerX(), drawable.getBounds().centerY());
                }
                drawable.draw(canvas);
                if (useScale) {
                    canvas.restore();
                }
                if (moveCheck) {
                    canvas.restore();
                }
                drawable.setAlpha(255);
            }
        }
        if (drawCheck1) {
            if (!shouldDrawTimeOnMedia()) {
                Drawable drawable5 = getThemedDrawable(drawSelectionBackground ? Theme.key_drawable_msgOutHalfCheckSelected : Theme.key_drawable_msgOutHalfCheck);
                float dp = (this.layoutWidth - AndroidUtilities.dp(18.0f)) - drawable5.getIntrinsicWidth();
                if (!this.pinnedBottom && !this.pinnedTop) {
                    f3 = 8.0f;
                }
                setDrawableBounds(drawable5, dp, ((layoutHeight - AndroidUtilities.dp(f3)) - drawable5.getIntrinsicHeight()) + timeYOffset);
                drawable5.setAlpha((int) (alpha2 * 255.0f));
                if (useScale || moveCheck) {
                    canvas.save();
                    canvas.scale(scale, scale, drawable5.getBounds().centerX(), drawable5.getBounds().centerY());
                }
                drawable5.draw(canvas);
                if (useScale || moveCheck) {
                    canvas.restore();
                }
                drawable5.setAlpha(255);
            } else {
                Drawable drawable6 = this.currentMessageObject.shouldDrawWithoutBackground() ? getThemedDrawable(Theme.key_drawable_msgStickerHalfCheck) : Theme.chat_msgMediaHalfCheckDrawable;
                int i5 = this.layoutWidth;
                if (!bigRadius) {
                    f2 = 21.5f;
                }
                setDrawableBounds(drawable6, (i5 - AndroidUtilities.dp(f2)) - drawable6.getIntrinsicWidth(), (timeY - drawable6.getIntrinsicHeight()) + timeYOffset);
                drawable6.setAlpha((int) (this.timeAlpha * 255.0f * alpha2));
                if (useScale || moveCheck) {
                    canvas.save();
                    canvas.scale(scale, scale, drawable6.getBounds().centerX(), drawable6.getBounds().centerY());
                }
                drawable6.draw(canvas);
                if (useScale || moveCheck) {
                    canvas.restore();
                }
                drawable6.setAlpha(255);
            }
        }
        if (drawError) {
            if (shouldDrawTimeOnMedia()) {
                x = this.layoutWidth - AndroidUtilities.dp(34.5f);
                y = (layoutHeight - AndroidUtilities.dp(26.5f)) + timeYOffset;
            } else {
                x = this.layoutWidth - AndroidUtilities.dp(32.0f);
                y = (layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 22.0f : 21.0f)) + timeYOffset;
            }
            this.rect.set(x, y, AndroidUtilities.dp(14.0f) + x, AndroidUtilities.dp(14.0f) + y);
            int oldAlpha = Theme.chat_msgErrorPaint.getAlpha();
            Theme.chat_msgErrorPaint.setAlpha((int) (oldAlpha * alpha2));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
            Theme.chat_msgErrorPaint.setAlpha(oldAlpha);
            setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0f) + x, AndroidUtilities.dp(2.0f) + y);
            Theme.chat_msgErrorDrawable.setAlpha((int) (255.0f * alpha2));
            if (useScale) {
                canvas.save();
                canvas.scale(scale, scale, Theme.chat_msgErrorDrawable.getBounds().centerX(), Theme.chat_msgErrorDrawable.getBounds().centerY());
            }
            Theme.chat_msgErrorDrawable.draw(canvas);
            Theme.chat_msgErrorDrawable.setAlpha(255);
            if (useScale) {
                canvas.restore();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:232:0x0703, code lost:
        if (r45.docTitleLayout.getLineLeft(0) != 0.0f) goto L234;
     */
    /* JADX WARN: Code restructure failed: missing block: B:351:0x0ac4, code lost:
        if (r1[0] == 3) goto L353;
     */
    /* JADX WARN: Code restructure failed: missing block: B:613:0x1268, code lost:
        if (r3 == 2) goto L616;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:252:0x07af  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x07ef  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x07fd  */
    /* JADX WARN: Removed duplicated region for block: B:264:0x0812  */
    /* JADX WARN: Removed duplicated region for block: B:759:0x154b  */
    /* JADX WARN: Removed duplicated region for block: B:763:0x1562  */
    /* JADX WARN: Removed duplicated region for block: B:771:0x1581  */
    /* JADX WARN: Removed duplicated region for block: B:775:0x1599  */
    /* JADX WARN: Removed duplicated region for block: B:797:0x15d7  */
    /* JADX WARN: Removed duplicated region for block: B:801:0x15e6  */
    /* JADX WARN: Removed duplicated region for block: B:947:0x194e  */
    /* JADX WARN: Removed duplicated region for block: B:954:0x196d  */
    /* JADX WARN: Removed duplicated region for block: B:957:0x19c3  */
    /* JADX WARN: Type inference failed for: r15v1, types: [org.telegram.ui.ActionBar.Theme$MessageDrawable, java.lang.String] */
    /* JADX WARN: Type inference failed for: r15v31 */
    /* JADX WARN: Type inference failed for: r15v4 */
    /* JADX WARN: Type inference failed for: r2v242, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawOverlays(android.graphics.Canvas r46) {
        /*
            Method dump skipped, instructions count: 6772
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

    public TLRPC.Document getStreamingMedia() {
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
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
        if (chatMessageCellDelegate2 == null || !chatMessageCellDelegate2.onAccessibilityAction(action, arguments)) {
            if (action == 16) {
                int icon = getIconForCurrentState();
                if (icon == 4 || icon == 5) {
                    if (this.currentMessageObject.type == 16) {
                        this.delegate.didPressOther(this, this.otherX, this.otherY);
                    } else {
                        didClickedImage();
                    }
                } else {
                    didPressButton(true, false);
                }
                return true;
            }
            if (action == R.id.acc_action_small_button) {
                didPressMiniButton(true);
            } else if (action == R.id.acc_action_msg_options) {
                if (this.delegate != null) {
                    if (this.currentMessageObject.type == 16) {
                        this.delegate.didLongPress(this, 0.0f, 0.0f);
                    } else {
                        this.delegate.didPressOther(this, this.otherX, this.otherY);
                    }
                }
            } else if (action == R.id.acc_action_open_forwarded_origin && (chatMessageCellDelegate = this.delegate) != null) {
                TLRPC.Chat chat = this.currentForwardChannel;
                if (chat != null) {
                    chatMessageCellDelegate.didPressChannelAvatar(this, chat, this.currentMessageObject.messageOwner.fwd_from.channel_post, this.lastTouchX, this.lastTouchY);
                } else {
                    TLRPC.User user = this.currentForwardUser;
                    if (user != null) {
                        chatMessageCellDelegate.didPressUserAvatar(this, user, this.lastTouchX, this.lastTouchY);
                    } else if (this.currentForwardName != null) {
                        chatMessageCellDelegate.didPressHiddenForward(this);
                    }
                }
            }
            if ((!this.currentMessageObject.isVoice() && !this.currentMessageObject.isRoundVideo() && (!this.currentMessageObject.isMusic() || !MediaController.getInstance().isPlayingMessage(this.currentMessageObject))) || !this.seekBarAccessibilityDelegate.performAccessibilityActionInternal(action, arguments)) {
                return super.performAccessibilityAction(action, arguments);
            }
            return true;
        }
        return false;
    }

    public void setAnimationRunning(boolean animationRunning, boolean willRemoved) {
        this.animationRunning = animationRunning;
        if (animationRunning) {
            this.willRemoved = willRemoved;
        } else {
            this.willRemoved = false;
        }
        if (getParent() == null && this.attachedToWindow) {
            onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 9 || event.getAction() == 7) {
            for (int i = 0; i < this.accessibilityVirtualViewBounds.size(); i++) {
                Rect rect = this.accessibilityVirtualViewBounds.valueAt(i);
                if (rect.contains(x, y)) {
                    int id = this.accessibilityVirtualViewBounds.keyAt(i);
                    if (id != this.currentFocusedVirtualView) {
                        this.currentFocusedVirtualView = id;
                        sendAccessibilityEventForVirtualView(id, 32768);
                        return true;
                    }
                    return true;
                }
            }
        } else if (event.getAction() == 10) {
            this.currentFocusedVirtualView = 0;
        }
        return super.onHoverEvent(event);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return new MessageAccessibilityNodeProvider();
    }

    public void sendAccessibilityEventForVirtualView(int viewId, int eventType) {
        sendAccessibilityEventForVirtualView(viewId, eventType, null);
    }

    private void sendAccessibilityEventForVirtualView(int viewId, int eventType, String text) {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (am.isTouchExplorationEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
            event.setPackageName(getContext().getPackageName());
            event.setSource(this, viewId);
            if (text != null) {
                event.getText().add(text);
            }
            if (getParent() != null) {
                getParent().requestSendAccessibilityEvent(this, event);
            }
        }
    }

    public static Point getMessageSize(int imageW, int imageH) {
        return getMessageSize(imageW, imageH, 0, 0);
    }

    private static Point getMessageSize(int imageW, int imageH, int photoWidth, int photoHeight) {
        if (photoHeight == 0 || photoWidth == 0) {
            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (AndroidUtilities.getMinTabletSide() * 0.7f);
            } else if (imageW >= imageH) {
                photoWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(64.0f);
            } else {
                photoWidth = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7f);
            }
            photoHeight = photoWidth + AndroidUtilities.dp(100.0f);
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight > AndroidUtilities.getPhotoSize()) {
                photoHeight = AndroidUtilities.getPhotoSize();
            }
        }
        float scale = imageW / photoWidth;
        int w = (int) (imageW / scale);
        int h = (int) (imageH / scale);
        if (w == 0) {
            w = AndroidUtilities.dp(150.0f);
        }
        if (h == 0) {
            h = AndroidUtilities.dp(150.0f);
        }
        if (h > photoHeight) {
            float scale2 = h;
            h = photoHeight;
            w = (int) (w / (scale2 / h));
        } else if (h < AndroidUtilities.dp(120.0f)) {
            h = AndroidUtilities.dp(120.0f);
            float hScale = imageH / h;
            if (imageW / hScale < photoWidth) {
                w = (int) (imageW / hScale);
            }
        }
        return new Point(w, h);
    }

    public StaticLayout getDescriptionlayout() {
        return this.descriptionLayout;
    }

    public void setSelectedBackgroundProgress(float value) {
        this.selectedBackgroundProgress = value;
        invalidate();
    }

    public int computeHeight(MessageObject object, MessageObject.GroupedMessages groupedMessages) {
        this.photoImage.setIgnoreImageSet(true);
        this.avatarImage.setIgnoreImageSet(true);
        this.replyImageReceiver.setIgnoreImageSet(true);
        this.locationImageReceiver.setIgnoreImageSet(true);
        if (groupedMessages != null && groupedMessages.messages.size() != 1) {
            int h = 0;
            for (int i = 0; i < groupedMessages.messages.size(); i++) {
                MessageObject o = groupedMessages.messages.get(i);
                MessageObject.GroupedMessagePosition position = groupedMessages.positions.get(o);
                if (position != null && (position.flags & 1) != 0) {
                    setMessageContent(o, groupedMessages, false, false);
                    h += this.totalHeight + this.keyboardHeight;
                }
            }
            return h;
        }
        setMessageContent(object, groupedMessages, false, false);
        this.photoImage.setIgnoreImageSet(false);
        this.avatarImage.setIgnoreImageSet(false);
        this.replyImageReceiver.setIgnoreImageSet(false);
        this.locationImageReceiver.setIgnoreImageSet(false);
        return this.totalHeight + this.keyboardHeight;
    }

    public void shakeView() {
        Keyframe kf0 = Keyframe.ofFloat(0.0f, 0.0f);
        Keyframe kf1 = Keyframe.ofFloat(0.2f, 3.0f);
        Keyframe kf2 = Keyframe.ofFloat(0.4f, -3.0f);
        Keyframe kf3 = Keyframe.ofFloat(0.6f, 3.0f);
        Keyframe kf4 = Keyframe.ofFloat(0.8f, -3.0f);
        Keyframe kf5 = Keyframe.ofFloat(1.0f, 0.0f);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe(View.ROTATION, kf0, kf1, kf2, kf3, kf4, kf5);
        Keyframe kfs0 = Keyframe.ofFloat(0.0f, 1.0f);
        Keyframe kfs1 = Keyframe.ofFloat(0.5f, 0.97f);
        Keyframe kfs2 = Keyframe.ofFloat(1.0f, 1.0f);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X, kfs0, kfs1, kfs2);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y, kfs0, kfs1, kfs2);
        AnimatorSet animatorSet = new AnimatorSet();
        this.shakeAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation), ObjectAnimator.ofPropertyValuesHolder(this, pvhScaleX), ObjectAnimator.ofPropertyValuesHolder(this, pvhScaleY));
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

    public void setSlidingOffset(float offsetX) {
        if (this.slidingOffsetX != offsetX) {
            this.slidingOffsetX = offsetX;
            updateTranslation();
        }
    }

    public void setAnimationOffsetX(float offsetX) {
        if (this.animationOffsetX != offsetX) {
            this.animationOffsetX = offsetX;
            updateTranslation();
        }
    }

    private void updateTranslation() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        int checkBoxOffset = !messageObject.isOutOwner() ? this.checkBoxTranslation : 0;
        setTranslationX(this.slidingOffsetX + this.animationOffsetX + checkBoxOffset);
    }

    public float getNonAnimationTranslationX(boolean update) {
        boolean z;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isOutOwner()) {
            if (update && ((z = this.checkBoxVisible) || this.checkBoxAnimationInProgress)) {
                Interpolator interpolator = z ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN;
                this.checkBoxTranslation = (int) Math.ceil(interpolator.getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
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
    public void setTranslationX(float translationX) {
        super.setTranslationX(translationX);
    }

    public SeekBar getSeekBar() {
        return this.seekBar;
    }

    public SeekBarWaveform getSeekBarWaveform() {
        return this.seekBarWaveform;
    }

    /* loaded from: classes4.dex */
    public class MessageAccessibilityNodeProvider extends AccessibilityNodeProvider {
        public static final int BOT_BUTTONS_START = 1000;
        public static final int COMMENT = 496;
        public static final int FORWARD = 494;
        public static final int INSTANT_VIEW = 499;
        public static final int LINK_CAPTION_IDS_START = 3000;
        public static final int LINK_IDS_START = 2000;
        public static final int POLL_BUTTONS_START = 500;
        public static final int POLL_HINT = 495;
        public static final int PROFILE = 5000;
        public static final int REPLY = 497;
        public static final int SHARE = 498;
        public static final int TRANSCRIBE = 493;
        private Path linkPath;
        private Rect rect;
        private RectF rectF;

        private MessageAccessibilityNodeProvider() {
            ChatMessageCell.this = r1;
            this.linkPath = new Path();
            this.rectF = new RectF();
            this.rect = new Rect();
        }

        /* loaded from: classes4.dex */
        private class ProfileSpan extends ClickableSpan {
            private TLRPC.User user;

            public ProfileSpan(TLRPC.User user) {
                MessageAccessibilityNodeProvider.this = r1;
                this.user = user;
            }

            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.didPressUserAvatar(ChatMessageCell.this, this.user, 0.0f, 0.0f);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r13v0 */
        /* JADX WARN: Type inference failed for: r13v1, types: [boolean] */
        /* JADX WARN: Type inference failed for: r13v2 */
        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
            boolean z;
            String str;
            int i;
            String str2;
            int i2;
            int i3;
            AccessibilityNodeInfo.CollectionItemInfo itemInfo;
            TLRPC.TL_messagePeerReaction recentReaction;
            String str3;
            int i4;
            String title;
            int[] pos = {0, 0};
            ChatMessageCell.this.getLocationOnScreen(pos);
            int i5 = 10;
            ?? r13 = 1;
            if (virtualViewId == -1) {
                AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain(ChatMessageCell.this);
                ChatMessageCell.this.onInitializeAccessibilityNodeInfo(info);
                if (ChatMessageCell.this.accessibilityText == null) {
                    SpannableStringBuilder sb = new SpannableStringBuilder();
                    if (ChatMessageCell.this.isChat && ChatMessageCell.this.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                        sb.append(UserObject.getUserName(ChatMessageCell.this.currentUser));
                        sb.setSpan(new ProfileSpan(ChatMessageCell.this.currentUser), 0, sb.length(), 33);
                        sb.append('\n');
                    }
                    if (ChatMessageCell.this.drawForwardedName) {
                        int a = 0;
                        while (a < 2) {
                            if (ChatMessageCell.this.forwardedNameLayout[a] != null) {
                                sb.append(ChatMessageCell.this.forwardedNameLayout[a].getText());
                                sb.append(a == 0 ? " " : "\n");
                            }
                            a++;
                        }
                    }
                    if (!TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.messageText)) {
                        sb.append(ChatMessageCell.this.currentMessageObject.messageText);
                    }
                    if (ChatMessageCell.this.documentAttach != null && (ChatMessageCell.this.documentAttachType == 1 || ChatMessageCell.this.documentAttachType == 2 || ChatMessageCell.this.documentAttachType == 4)) {
                        if (ChatMessageCell.this.buttonState == 1 && ChatMessageCell.this.loadingProgressLayout != null) {
                            sb.append("\n");
                            boolean sending = ChatMessageCell.this.currentMessageObject.isSending();
                            String key = sending ? "AccDescrUploadProgress" : "AccDescrDownloadProgress";
                            int resId = sending ? R.string.AccDescrUploadProgress : R.string.AccDescrDownloadProgress;
                            sb.append(LocaleController.formatString(key, resId, AndroidUtilities.formatFileSize(ChatMessageCell.this.currentMessageObject.loadedFileSize), AndroidUtilities.formatFileSize(ChatMessageCell.this.lastLoadingSizeTotal)));
                        }
                    }
                    if (!ChatMessageCell.this.currentMessageObject.isMusic()) {
                        if (ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.isRoundVideo) {
                            sb.append(", ");
                            sb.append(LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                            sb.append(", ");
                            if (ChatMessageCell.this.currentMessageObject.isContentUnread()) {
                                sb.append(LocaleController.getString("AccDescrMsgNotPlayed", R.string.AccDescrMsgNotPlayed));
                            } else {
                                sb.append(LocaleController.getString("AccDescrMsgPlayed", R.string.AccDescrMsgPlayed));
                            }
                        }
                    } else {
                        sb.append("\n");
                        sb.append(LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, ChatMessageCell.this.currentMessageObject.getMusicAuthor(), ChatMessageCell.this.currentMessageObject.getMusicTitle()));
                        sb.append(", ");
                        sb.append(LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                    }
                    if (ChatMessageCell.this.lastPoll != null) {
                        sb.append(", ");
                        sb.append(ChatMessageCell.this.lastPoll.question);
                        sb.append(", ");
                        if (!ChatMessageCell.this.pollClosed) {
                            if (ChatMessageCell.this.lastPoll.quiz) {
                                if (ChatMessageCell.this.lastPoll.public_voters) {
                                    title = LocaleController.getString("QuizPoll", R.string.QuizPoll);
                                } else {
                                    title = LocaleController.getString("AnonymousQuizPoll", R.string.AnonymousQuizPoll);
                                }
                            } else if (ChatMessageCell.this.lastPoll.public_voters) {
                                title = LocaleController.getString("PublicPoll", R.string.PublicPoll);
                            } else {
                                title = LocaleController.getString("AnonymousPoll", R.string.AnonymousPoll);
                            }
                        } else {
                            title = LocaleController.getString("FinalResults", R.string.FinalResults);
                        }
                        sb.append((CharSequence) title);
                    }
                    if (!ChatMessageCell.this.currentMessageObject.isVoiceTranscriptionOpen()) {
                        if (ChatMessageCell.this.currentMessageObject.messageOwner.media != null && !TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.caption)) {
                            sb.append("\n");
                            sb.append(ChatMessageCell.this.currentMessageObject.caption);
                        }
                    } else {
                        sb.append("\n");
                        sb.append(ChatMessageCell.this.currentMessageObject.getVoiceTranscription());
                    }
                    if (ChatMessageCell.this.documentAttach != null) {
                        if (ChatMessageCell.this.documentAttachType == 4) {
                            sb.append(", ");
                            sb.append(LocaleController.formatDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                        }
                        if (ChatMessageCell.this.buttonState == 0 || ChatMessageCell.this.documentAttachType == 1) {
                            sb.append(", ");
                            sb.append(AndroidUtilities.formatFileSize(ChatMessageCell.this.documentAttach.size));
                        }
                    }
                    if (ChatMessageCell.this.currentMessageObject.isOut()) {
                        if (!ChatMessageCell.this.currentMessageObject.isSent()) {
                            if (!ChatMessageCell.this.currentMessageObject.isSending()) {
                                if (ChatMessageCell.this.currentMessageObject.isSendError()) {
                                    sb.append("\n");
                                    sb.append(LocaleController.getString("AccDescrMsgSendingError", R.string.AccDescrMsgSendingError));
                                }
                            } else {
                                sb.append("\n");
                                sb.append(LocaleController.getString("AccDescrMsgSending", R.string.AccDescrMsgSending));
                                float sendingProgress = ChatMessageCell.this.radialProgress.getProgress();
                                if (sendingProgress > 0.0f) {
                                    sb.append(", ").append(Integer.toString(Math.round(100.0f * sendingProgress))).append("%");
                                }
                            }
                        } else {
                            sb.append("\n");
                            if (ChatMessageCell.this.currentMessageObject.scheduled) {
                                sb.append(LocaleController.formatString("AccDescrScheduledDate", R.string.AccDescrScheduledDate, ChatMessageCell.this.currentTimeString));
                            } else {
                                sb.append(LocaleController.formatString("AccDescrSentDate", R.string.AccDescrSentDate, LocaleController.getString("TodayAt", R.string.TodayAt) + " " + ChatMessageCell.this.currentTimeString));
                                sb.append(", ");
                                if (ChatMessageCell.this.currentMessageObject.isUnread()) {
                                    i4 = R.string.AccDescrMsgUnread;
                                    str3 = "AccDescrMsgUnread";
                                } else {
                                    i4 = R.string.AccDescrMsgRead;
                                    str3 = "AccDescrMsgRead";
                                }
                                sb.append(LocaleController.getString(str3, i4));
                            }
                        }
                    } else {
                        sb.append("\n");
                        sb.append(LocaleController.formatString("AccDescrReceivedDate", R.string.AccDescrReceivedDate, LocaleController.getString("TodayAt", R.string.TodayAt) + " " + ChatMessageCell.this.currentTimeString));
                    }
                    if (ChatMessageCell.this.getRepliesCount() > 0 && !ChatMessageCell.this.hasCommentLayout()) {
                        sb.append("\n");
                        sb.append(LocaleController.formatPluralString("AccDescrNumberOfReplies", ChatMessageCell.this.getRepliesCount(), new Object[0]));
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions != null && ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results != null) {
                        if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.size() == 1) {
                            TLRPC.TL_reactionCount reaction = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.get(0);
                            if (reaction.count == 1) {
                                sb.append("\n");
                                boolean isMe = false;
                                String userName = "";
                                if (ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions != null && ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions.size() == 1 && (recentReaction = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.recent_reactions.get(0)) != null) {
                                    TLRPC.User user = MessagesController.getInstance(ChatMessageCell.this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(recentReaction.peer_id)));
                                    isMe = UserObject.isUserSelf(user);
                                    if (user != null) {
                                        userName = UserObject.getFirstName(user);
                                    }
                                }
                                if (isMe) {
                                    sb.append(LocaleController.formatString("AccDescrYouReactedWith", R.string.AccDescrYouReactedWith, reaction.reaction));
                                } else {
                                    sb.append(LocaleController.formatString("AccDescrReactedWith", R.string.AccDescrReactedWith, userName, reaction.reaction));
                                }
                            } else if (reaction.count > 1) {
                                sb.append("\n");
                                sb.append(LocaleController.formatPluralString("AccDescrNumberOfPeopleReactions", reaction.count, reaction.reaction));
                            }
                        } else {
                            sb.append(LocaleController.getString("Reactions", R.string.Reactions)).append(": ");
                            int count = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.size();
                            for (int i6 = 0; i6 < count; i6++) {
                                TLRPC.TL_reactionCount reactionCount = ChatMessageCell.this.currentMessageObject.messageOwner.reactions.results.get(i6);
                                if (reactionCount != null) {
                                    sb.append(reactionCount.reaction).append(" ").append(reactionCount.count + "");
                                    if (i6 + 1 < count) {
                                        sb.append(", ");
                                    }
                                }
                            }
                            sb.append("\n");
                        }
                    }
                    if ((ChatMessageCell.this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                        sb.append("\n");
                        sb.append(LocaleController.formatPluralString("AccDescrNumberOfViews", ChatMessageCell.this.currentMessageObject.messageOwner.views, new Object[0]));
                    }
                    sb.append("\n");
                    CharacterStyle[] links = (CharacterStyle[]) sb.getSpans(0, sb.length(), ClickableSpan.class);
                    for (final CharacterStyle link : links) {
                        int start = sb.getSpanStart(link);
                        int end = sb.getSpanEnd(link);
                        sb.removeSpan(link);
                        ClickableSpan underlineSpan = new ClickableSpan() { // from class: org.telegram.ui.Cells.ChatMessageCell.MessageAccessibilityNodeProvider.1
                            @Override // android.text.style.ClickableSpan
                            public void onClick(View view) {
                                CharacterStyle characterStyle = link;
                                if (!(characterStyle instanceof ProfileSpan)) {
                                    if (ChatMessageCell.this.delegate != null) {
                                        ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, link, false);
                                        return;
                                    }
                                    return;
                                }
                                ((ProfileSpan) characterStyle).onClick(view);
                            }
                        };
                        sb.setSpan(underlineSpan, start, end, 33);
                    }
                    ChatMessageCell.this.accessibilityText = sb;
                }
                if (Build.VERSION.SDK_INT < 24) {
                    info.setContentDescription(ChatMessageCell.this.accessibilityText.toString());
                } else {
                    info.setText(ChatMessageCell.this.accessibilityText);
                }
                info.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 19 && (itemInfo = info.getCollectionItemInfo()) != null) {
                    info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(itemInfo.getRowIndex(), 1, 0, 1, false));
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_msg_options, LocaleController.getString("AccActionMessageOptions", R.string.AccActionMessageOptions)));
                    int icon = ChatMessageCell.this.getIconForCurrentState();
                    CharSequence actionLabel = null;
                    switch (icon) {
                        case 0:
                            actionLabel = LocaleController.getString("AccActionPlay", R.string.AccActionPlay);
                            i3 = 16;
                            break;
                        case 1:
                            actionLabel = LocaleController.getString("AccActionPause", R.string.AccActionPause);
                            i3 = 16;
                            break;
                        case 2:
                            actionLabel = LocaleController.getString("AccActionDownload", R.string.AccActionDownload);
                            i3 = 16;
                            break;
                        case 3:
                            actionLabel = LocaleController.getString("AccActionCancelDownload", R.string.AccActionCancelDownload);
                            i3 = 16;
                            break;
                        case 4:
                        default:
                            i3 = 16;
                            if (ChatMessageCell.this.currentMessageObject.type == 16) {
                                actionLabel = LocaleController.getString("CallAgain", R.string.CallAgain);
                                break;
                            }
                            break;
                        case 5:
                            actionLabel = LocaleController.getString("AccActionOpenFile", R.string.AccActionOpenFile);
                            i3 = 16;
                            break;
                    }
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(i3, actionLabel));
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccActionEnterSelectionMode", R.string.AccActionEnterSelectionMode)));
                    int smallIcon = ChatMessageCell.this.getMiniIconForCurrentState();
                    if (smallIcon == 2) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_small_button, LocaleController.getString("AccActionDownload", R.string.AccActionDownload)));
                    }
                } else {
                    info.addAction(16);
                    info.addAction(32);
                }
                if ((ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.currentMessageObject.isRoundVideo() || ChatMessageCell.this.currentMessageObject.isMusic()) && MediaController.getInstance().isPlayingMessage(ChatMessageCell.this.currentMessageObject)) {
                    ChatMessageCell.this.seekBarAccessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(info);
                }
                if (ChatMessageCell.this.useTranscribeButton && ChatMessageCell.this.transcribeButton != null) {
                    info.addChild(ChatMessageCell.this, TRANSCRIBE);
                }
                if (Build.VERSION.SDK_INT < 24) {
                    if (ChatMessageCell.this.isChat && ChatMessageCell.this.currentUser != null && !ChatMessageCell.this.currentMessageObject.isOut()) {
                        info.addChild(ChatMessageCell.this, 5000);
                    }
                    if (ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) {
                        Spannable buffer = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                        CharacterStyle[] links2 = (CharacterStyle[]) buffer.getSpans(0, buffer.length(), ClickableSpan.class);
                        int i7 = 0;
                        for (CharacterStyle characterStyle : links2) {
                            info.addChild(ChatMessageCell.this, i7 + 2000);
                            i7++;
                        }
                    }
                    if ((ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) && ChatMessageCell.this.captionLayout != null) {
                        Spannable buffer2 = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                        CharacterStyle[] links3 = (CharacterStyle[]) buffer2.getSpans(0, buffer2.length(), ClickableSpan.class);
                        int i8 = 0;
                        for (CharacterStyle characterStyle2 : links3) {
                            info.addChild(ChatMessageCell.this, i8 + 3000);
                            i8++;
                        }
                    }
                }
                int i9 = 0;
                Iterator it = ChatMessageCell.this.botButtons.iterator();
                while (it.hasNext()) {
                    BotButton botButton = (BotButton) it.next();
                    info.addChild(ChatMessageCell.this, i9 + 1000);
                    i9++;
                }
                if (ChatMessageCell.this.hintButtonVisible && ChatMessageCell.this.pollHintX != -1 && ChatMessageCell.this.currentMessageObject.isPoll()) {
                    info.addChild(ChatMessageCell.this, POLL_HINT);
                }
                int i10 = 0;
                Iterator it2 = ChatMessageCell.this.pollButtons.iterator();
                while (it2.hasNext()) {
                    PollButton pollButton = (PollButton) it2.next();
                    info.addChild(ChatMessageCell.this, i10 + 500);
                    i10++;
                }
                if (ChatMessageCell.this.drawInstantView && !ChatMessageCell.this.instantButtonRect.isEmpty()) {
                    info.addChild(ChatMessageCell.this, INSTANT_VIEW);
                }
                if (ChatMessageCell.this.commentLayout != null) {
                    info.addChild(ChatMessageCell.this, COMMENT);
                }
                if (ChatMessageCell.this.drawSideButton == 1) {
                    info.addChild(ChatMessageCell.this, SHARE);
                }
                if (ChatMessageCell.this.replyNameLayout != null) {
                    info.addChild(ChatMessageCell.this, REPLY);
                }
                if (ChatMessageCell.this.forwardedNameLayout[0] != null && ChatMessageCell.this.forwardedNameLayout[1] != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_open_forwarded_origin, LocaleController.getString("AccActionOpenForwardedOrigin", R.string.AccActionOpenForwardedOrigin)));
                    } else {
                        info.addChild(ChatMessageCell.this, FORWARD);
                    }
                }
                if (ChatMessageCell.this.drawSelectionBackground || ChatMessageCell.this.getBackground() != null) {
                    info.setSelected(true);
                }
                return info;
            }
            AccessibilityNodeInfo info2 = AccessibilityNodeInfo.obtain();
            info2.setSource(ChatMessageCell.this, virtualViewId);
            info2.setParent(ChatMessageCell.this);
            info2.setPackageName(ChatMessageCell.this.getContext().getPackageName());
            if (virtualViewId == 5000) {
                if (ChatMessageCell.this.currentUser == null) {
                    return null;
                }
                String content = UserObject.getUserName(ChatMessageCell.this.currentUser);
                info2.setText(content);
                Rect rect = this.rect;
                int i11 = (int) ChatMessageCell.this.nameX;
                int i12 = (int) ChatMessageCell.this.nameY;
                int i13 = (int) (ChatMessageCell.this.nameX + ChatMessageCell.this.nameWidth);
                float f = ChatMessageCell.this.nameY;
                if (ChatMessageCell.this.nameLayout != null) {
                    i5 = ChatMessageCell.this.nameLayout.getHeight();
                }
                rect.set(i11, i12, i13, (int) (f + i5));
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClassName("android.widget.TextView");
                info2.setEnabled(true);
                info2.setClickable(true);
                info2.setLongClickable(true);
                info2.addAction(16);
                info2.addAction(32);
                z = true;
            } else if (virtualViewId >= 3000) {
                if (!(ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) || ChatMessageCell.this.captionLayout == null) {
                    return null;
                }
                Spannable buffer3 = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                ClickableSpan link2 = getLinkById(virtualViewId, true);
                if (link2 == null) {
                    return null;
                }
                int[] linkPos = ChatMessageCell.this.getRealSpanStartAndEnd(buffer3, link2);
                String content2 = buffer3.subSequence(linkPos[0], linkPos[1]).toString();
                info2.setText(content2);
                ChatMessageCell.this.captionLayout.getText().length();
                ChatMessageCell.this.captionLayout.getSelectionPath(linkPos[0], linkPos[1], this.linkPath);
                this.linkPath.computeBounds(this.rectF, true);
                this.rect.set((int) this.rectF.left, (int) this.rectF.top, (int) this.rectF.right, (int) this.rectF.bottom);
                this.rect.offset((int) ChatMessageCell.this.captionX, (int) ChatMessageCell.this.captionY);
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClassName("android.widget.TextView");
                info2.setEnabled(true);
                info2.setClickable(true);
                info2.setLongClickable(true);
                info2.addAction(16);
                info2.addAction(32);
                z = true;
            } else if (virtualViewId >= 2000) {
                if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable)) {
                    return null;
                }
                Spannable buffer4 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                ClickableSpan link3 = getLinkById(virtualViewId, false);
                if (link3 == null) {
                    return null;
                }
                int[] linkPos2 = ChatMessageCell.this.getRealSpanStartAndEnd(buffer4, link3);
                String content3 = buffer4.subSequence(linkPos2[0], linkPos2[1]).toString();
                info2.setText(content3);
                Iterator<MessageObject.TextLayoutBlock> it3 = ChatMessageCell.this.currentMessageObject.textLayoutBlocks.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    MessageObject.TextLayoutBlock block = it3.next();
                    int length = block.textLayout.getText().length();
                    if (block.charactersOffset <= linkPos2[0]) {
                        int i14 = block.charactersOffset + length;
                        char c = r13 == true ? 1 : 0;
                        char c2 = r13 == true ? 1 : 0;
                        if (i14 >= linkPos2[c]) {
                            block.textLayout.getSelectionPath(linkPos2[0] - block.charactersOffset, linkPos2[r13] - block.charactersOffset, this.linkPath);
                            this.linkPath.computeBounds(this.rectF, r13);
                            this.rect.set((int) this.rectF.left, (int) this.rectF.top, (int) this.rectF.right, (int) this.rectF.bottom);
                            this.rect.offset(0, (int) block.textYOffset);
                            this.rect.offset(ChatMessageCell.this.textX, ChatMessageCell.this.textY);
                            info2.setBoundsInParent(this.rect);
                            if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null) {
                                ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                            }
                            this.rect.offset(pos[0], pos[1]);
                            info2.setBoundsInScreen(this.rect);
                        }
                    }
                    r13 = 1;
                }
                info2.setClassName("android.widget.TextView");
                info2.setEnabled(true);
                info2.setClickable(true);
                info2.setLongClickable(true);
                info2.addAction(16);
                info2.addAction(32);
                z = true;
            } else if (virtualViewId >= 1000) {
                int buttonIndex = virtualViewId - 1000;
                if (buttonIndex >= ChatMessageCell.this.botButtons.size()) {
                    return null;
                }
                BotButton button = (BotButton) ChatMessageCell.this.botButtons.get(buttonIndex);
                info2.setText(button.title.getText());
                info2.setClassName("android.widget.Button");
                info2.setEnabled(true);
                info2.setClickable(true);
                info2.addAction(16);
                this.rect.set(button.x, button.y, button.x + button.width, button.y + button.height);
                int addX = ChatMessageCell.this.currentMessageObject.isOutOwner() ? (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f) : ChatMessageCell.this.backgroundDrawableLeft + AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f);
                this.rect.offset(addX, ChatMessageCell.this.layoutHeight);
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                z = true;
            } else if (virtualViewId >= 500) {
                int buttonIndex2 = virtualViewId - 500;
                if (buttonIndex2 >= ChatMessageCell.this.pollButtons.size()) {
                    return null;
                }
                PollButton button2 = (PollButton) ChatMessageCell.this.pollButtons.get(buttonIndex2);
                StringBuilder sb2 = new StringBuilder(button2.title.getText());
                if (ChatMessageCell.this.pollVoted) {
                    info2.setSelected(button2.chosen);
                    sb2.append(", ");
                    sb2.append(button2.percent);
                    sb2.append("%");
                    if (ChatMessageCell.this.lastPoll != null && ChatMessageCell.this.lastPoll.quiz && (button2.chosen || button2.correct)) {
                        sb2.append(", ");
                        if (button2.correct) {
                            i2 = R.string.AccDescrQuizCorrectAnswer;
                            str2 = "AccDescrQuizCorrectAnswer";
                        } else {
                            i2 = R.string.AccDescrQuizIncorrectAnswer;
                            str2 = "AccDescrQuizIncorrectAnswer";
                        }
                        sb2.append(LocaleController.getString(str2, i2));
                    }
                } else {
                    info2.setClassName("android.widget.Button");
                }
                info2.setText(sb2);
                info2.setEnabled(true);
                info2.addAction(16);
                int y = button2.y + ChatMessageCell.this.namesOffset;
                int w = ChatMessageCell.this.backgroundWidth - AndroidUtilities.dp(76.0f);
                this.rect.set(button2.x, y, button2.x + w, button2.height + y);
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId == 495) {
                info2.setClassName("android.widget.Button");
                info2.setEnabled(true);
                info2.setText(LocaleController.getString("AccDescrQuizExplanation", R.string.AccDescrQuizExplanation));
                info2.addAction(16);
                this.rect.set(ChatMessageCell.this.pollHintX - AndroidUtilities.dp(8.0f), ChatMessageCell.this.pollHintY - AndroidUtilities.dp(8.0f), ChatMessageCell.this.pollHintX + AndroidUtilities.dp(32.0f), ChatMessageCell.this.pollHintY + AndroidUtilities.dp(32.0f));
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId == 499) {
                info2.setClassName("android.widget.Button");
                info2.setEnabled(true);
                if (ChatMessageCell.this.instantViewLayout != null) {
                    info2.setText(ChatMessageCell.this.instantViewLayout.getText());
                }
                info2.addAction(16);
                ChatMessageCell.this.instantButtonRect.round(this.rect);
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId == 498) {
                info2.setClassName("android.widget.ImageButton");
                info2.setEnabled(true);
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                if (chatMessageCell.isOpenChatByShare(chatMessageCell.currentMessageObject)) {
                    info2.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
                } else {
                    info2.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
                }
                info2.addAction(16);
                this.rect.set((int) ChatMessageCell.this.sideStartX, (int) ChatMessageCell.this.sideStartY, ((int) ChatMessageCell.this.sideStartX) + AndroidUtilities.dp(40.0f), ((int) ChatMessageCell.this.sideStartY) + AndroidUtilities.dp(32.0f));
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId == 497) {
                info2.setEnabled(true);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(LocaleController.getString("Reply", R.string.Reply));
                sb3.append(", ");
                if (ChatMessageCell.this.replyNameLayout != null) {
                    sb3.append(ChatMessageCell.this.replyNameLayout.getText());
                    sb3.append(", ");
                }
                if (ChatMessageCell.this.replyTextLayout != null) {
                    sb3.append(ChatMessageCell.this.replyTextLayout.getText());
                }
                info2.setContentDescription(sb3.toString());
                info2.addAction(16);
                this.rect.set(ChatMessageCell.this.replyStartX, ChatMessageCell.this.replyStartY, ChatMessageCell.this.replyStartX + Math.max(ChatMessageCell.this.replyNameWidth, ChatMessageCell.this.replyTextWidth), ChatMessageCell.this.replyStartY + AndroidUtilities.dp(35.0f));
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId == 494) {
                info2.setEnabled(true);
                StringBuilder sb4 = new StringBuilder();
                if (ChatMessageCell.this.forwardedNameLayout[0] != null && ChatMessageCell.this.forwardedNameLayout[1] != null) {
                    int a2 = 0;
                    while (a2 < 2) {
                        sb4.append(ChatMessageCell.this.forwardedNameLayout[a2].getText());
                        sb4.append(a2 == 0 ? " " : "\n");
                        a2++;
                    }
                }
                info2.setContentDescription(sb4.toString());
                info2.addAction(16);
                int x = (int) Math.min(ChatMessageCell.this.forwardNameX - ChatMessageCell.this.forwardNameOffsetX[0], ChatMessageCell.this.forwardNameX - ChatMessageCell.this.forwardNameOffsetX[1]);
                this.rect.set(x, ChatMessageCell.this.forwardNameY, ChatMessageCell.this.forwardedNameWidth + x, ChatMessageCell.this.forwardNameY + AndroidUtilities.dp(32.0f));
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId == 496) {
                info2.setClassName("android.widget.Button");
                info2.setEnabled(true);
                int commentCount = ChatMessageCell.this.getRepliesCount();
                String comment = null;
                if (ChatMessageCell.this.currentMessageObject != null && !ChatMessageCell.this.currentMessageObject.shouldDrawWithoutBackground() && !ChatMessageCell.this.currentMessageObject.isAnimatedEmoji()) {
                    comment = ChatMessageCell.this.isRepliesChat ? LocaleController.getString("ViewInChat", R.string.ViewInChat) : commentCount == 0 ? LocaleController.getString("LeaveAComment", R.string.LeaveAComment) : LocaleController.formatPluralString("CommentsCount", commentCount, new Object[0]);
                } else if (!ChatMessageCell.this.isRepliesChat && commentCount > 0) {
                    comment = LocaleController.formatShortNumber(commentCount, null);
                }
                if (comment != null) {
                    info2.setText(comment);
                }
                info2.addAction(16);
                this.rect.set(ChatMessageCell.this.commentButtonRect);
                info2.setBoundsInParent(this.rect);
                if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId) == null || !((Rect) ChatMessageCell.this.accessibilityVirtualViewBounds.get(virtualViewId)).equals(this.rect)) {
                    ChatMessageCell.this.accessibilityVirtualViewBounds.put(virtualViewId, new Rect(this.rect));
                }
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
                z = true;
            } else if (virtualViewId != 493) {
                z = true;
            } else {
                info2.setClassName("android.widget.Button");
                info2.setEnabled(true);
                if (ChatMessageCell.this.currentMessageObject.isVoiceTranscriptionOpen()) {
                    i = R.string.AccActionCloseTranscription;
                    str = "AccActionCloseTranscription";
                } else {
                    i = R.string.AccActionOpenTranscription;
                    str = "AccActionOpenTranscription";
                }
                info2.setText(LocaleController.getString(str, i));
                info2.addAction(16);
                this.rect.set((int) ChatMessageCell.this.transcribeX, (int) ChatMessageCell.this.transcribeY, (int) (ChatMessageCell.this.transcribeX + AndroidUtilities.dp(30.0f)), (int) (ChatMessageCell.this.transcribeY + AndroidUtilities.dp(30.0f)));
                info2.setBoundsInParent(this.rect);
                z = true;
                this.rect.offset(pos[0], pos[1]);
                info2.setBoundsInScreen(this.rect);
                info2.setClickable(true);
            }
            info2.setFocusable(z);
            info2.setVisibleToUser(z);
            return info2;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int virtualViewId, int action, Bundle arguments) {
            if (virtualViewId == -1) {
                ChatMessageCell.this.performAccessibilityAction(action, arguments);
            } else if (action == 64) {
                ChatMessageCell.this.sendAccessibilityEventForVirtualView(virtualViewId, 32768);
            } else {
                boolean z = false;
                if (action == 16) {
                    if (virtualViewId == 5000) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCellDelegate chatMessageCellDelegate = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell = ChatMessageCell.this;
                            chatMessageCellDelegate.didPressUserAvatar(chatMessageCell, chatMessageCell.currentUser, 0.0f, 0.0f);
                        }
                    } else if (virtualViewId >= 3000) {
                        ClickableSpan link = getLinkById(virtualViewId, true);
                        if (link != null) {
                            ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, link, false);
                            ChatMessageCell.this.sendAccessibilityEventForVirtualView(virtualViewId, 1);
                        }
                    } else if (virtualViewId >= 2000) {
                        ClickableSpan link2 = getLinkById(virtualViewId, false);
                        if (link2 != null) {
                            ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, link2, false);
                            ChatMessageCell.this.sendAccessibilityEventForVirtualView(virtualViewId, 1);
                        }
                    } else if (virtualViewId >= 1000) {
                        int buttonIndex = virtualViewId - 1000;
                        if (buttonIndex >= ChatMessageCell.this.botButtons.size()) {
                            return false;
                        }
                        BotButton button = (BotButton) ChatMessageCell.this.botButtons.get(buttonIndex);
                        if (ChatMessageCell.this.delegate != null && button.button != null) {
                            ChatMessageCell.this.delegate.didPressBotButton(ChatMessageCell.this, button.button);
                        }
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(virtualViewId, 1);
                    } else if (virtualViewId >= 500) {
                        int buttonIndex2 = virtualViewId - 500;
                        if (buttonIndex2 >= ChatMessageCell.this.pollButtons.size()) {
                            return false;
                        }
                        PollButton button2 = (PollButton) ChatMessageCell.this.pollButtons.get(buttonIndex2);
                        if (ChatMessageCell.this.delegate != null) {
                            ArrayList<TLRPC.TL_pollAnswer> answers = new ArrayList<>();
                            answers.add(button2.answer);
                            ChatMessageCell.this.delegate.didPressVoteButtons(ChatMessageCell.this, answers, -1, 0, 0);
                        }
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(virtualViewId, 1);
                    } else if (virtualViewId == 495) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell.this.delegate.didPressHint(ChatMessageCell.this, 0);
                        }
                    } else if (virtualViewId == 499) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCellDelegate chatMessageCellDelegate2 = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                            chatMessageCellDelegate2.didPressInstantButton(chatMessageCell2, chatMessageCell2.drawInstantViewType);
                        }
                    } else if (virtualViewId == 498) {
                        if (ChatMessageCell.this.delegate != null) {
                            ChatMessageCell.this.delegate.didPressSideButton(ChatMessageCell.this);
                        }
                    } else if (virtualViewId == 497) {
                        if (ChatMessageCell.this.delegate != null && ((!ChatMessageCell.this.isThreadChat || ChatMessageCell.this.currentMessageObject.getReplyTopMsgId() != 0) && ChatMessageCell.this.currentMessageObject.hasValidReplyMessageObject())) {
                            ChatMessageCellDelegate chatMessageCellDelegate3 = ChatMessageCell.this.delegate;
                            ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                            chatMessageCellDelegate3.didPressReplyMessage(chatMessageCell3, chatMessageCell3.currentMessageObject.getReplyMsgId());
                        }
                    } else if (virtualViewId == 494) {
                        if (ChatMessageCell.this.delegate != null) {
                            if (ChatMessageCell.this.currentForwardChannel != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate4 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
                                chatMessageCellDelegate4.didPressChannelAvatar(chatMessageCell4, chatMessageCell4.currentForwardChannel, ChatMessageCell.this.currentMessageObject.messageOwner.fwd_from.channel_post, ChatMessageCell.this.lastTouchX, ChatMessageCell.this.lastTouchY);
                            } else if (ChatMessageCell.this.currentForwardUser != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate5 = ChatMessageCell.this.delegate;
                                ChatMessageCell chatMessageCell5 = ChatMessageCell.this;
                                chatMessageCellDelegate5.didPressUserAvatar(chatMessageCell5, chatMessageCell5.currentForwardUser, ChatMessageCell.this.lastTouchX, ChatMessageCell.this.lastTouchY);
                            } else if (ChatMessageCell.this.currentForwardName != null) {
                                ChatMessageCell.this.delegate.didPressHiddenForward(ChatMessageCell.this);
                            }
                        }
                    } else if (virtualViewId == 496) {
                        if (ChatMessageCell.this.delegate != null) {
                            if (ChatMessageCell.this.isRepliesChat) {
                                ChatMessageCell.this.delegate.didPressSideButton(ChatMessageCell.this);
                            } else {
                                ChatMessageCell.this.delegate.didPressCommentButton(ChatMessageCell.this);
                            }
                        }
                    } else if (virtualViewId == 493 && ChatMessageCell.this.transcribeButton != null) {
                        ChatMessageCell.this.transcribeButton.onTap();
                    }
                } else if (action == 32) {
                    if (virtualViewId >= 3000) {
                        z = true;
                    }
                    ClickableSpan link3 = getLinkById(virtualViewId, z);
                    if (link3 != null) {
                        ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this, link3, true);
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(virtualViewId, 2);
                    }
                }
            }
            return true;
        }

        private ClickableSpan getLinkById(int id, boolean caption) {
            if (id == 5000) {
                return null;
            }
            if (caption) {
                int id2 = id - 3000;
                if ((ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) && id2 >= 0) {
                    Spannable buffer = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                    ClickableSpan[] links = (ClickableSpan[]) buffer.getSpans(0, buffer.length(), ClickableSpan.class);
                    if (links.length <= id2) {
                        return null;
                    }
                    return links[id2];
                }
                return null;
            }
            int id3 = id - 2000;
            if ((ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) && id3 >= 0) {
                Spannable buffer2 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                ClickableSpan[] links2 = (ClickableSpan[]) buffer2.getSpans(0, buffer2.length(), ClickableSpan.class);
                if (links2.length <= id3) {
                    return null;
                }
                return links2[id3];
            }
            return null;
        }
    }

    public void setImageCoords(float x, float y, float w, float h) {
        this.photoImage.setImageCoords(x, y, w, h);
        int i = this.documentAttachType;
        if (i == 4 || i == 2) {
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY;
            RadialProgress2 radialProgress2 = this.videoRadialProgress;
            int i2 = this.videoButtonX;
            radialProgress2.setProgressRect(i2, imageY, AndroidUtilities.dp(24.0f) + i2, this.videoButtonY + AndroidUtilities.dp(24.0f));
            this.buttonX = (int) (((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f) + x);
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
    public void setAlpha(float alpha) {
        boolean z = true;
        boolean z2 = alpha == 1.0f;
        if (getAlpha() != 1.0f) {
            z = false;
        }
        if (z2 != z) {
            invalidate();
        }
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            this.alphaInternal = alpha;
            invalidate();
            return;
        }
        super.setAlpha(alpha);
    }

    public int getCurrentBackgroundLeft() {
        int left = this.currentBackgroundDrawable.getBounds().left;
        if (!this.currentMessageObject.isOutOwner() && this.transitionParams.changePinnedBottomProgress != 1.0f && !this.mediaBackground && !this.drawPinnedBottom) {
            return left - AndroidUtilities.dp(6.0f);
        }
        return left;
    }

    public TransitionParams getTransitionParams() {
        return this.transitionParams;
    }

    public int getTopMediaOffset() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 14) {
            return this.mediaOffsetY + this.namesOffset;
        }
        return 0;
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
        MessageObject object = this.currentMessageObject;
        if (object == null) {
            object = this.messageObjectToSet;
        }
        if (object != null && object.preview && (i = this.parentWidth) > 0) {
            return i;
        }
        return AndroidUtilities.displaySize.x;
    }

    /* loaded from: classes4.dex */
    public class TransitionParams {
        public boolean animateBackgroundBoundsInner;
        public boolean animateBackgroundWidth;
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
        public int animateFromRepliesTextWidth;
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
        public boolean animateReplyY;
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
        public float lastDrawingTextX;
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

        public TransitionParams() {
            ChatMessageCell.this = this$0;
        }

        public void recordDrawingState() {
            this.wasDraw = true;
            this.lastDrawingImageX = ChatMessageCell.this.photoImage.getImageX();
            this.lastDrawingImageY = ChatMessageCell.this.photoImage.getImageY();
            this.lastDrawingImageW = ChatMessageCell.this.photoImage.getImageWidth();
            this.lastDrawingImageH = ChatMessageCell.this.photoImage.getImageHeight();
            int[] r = ChatMessageCell.this.photoImage.getRoundRadius();
            System.arraycopy(r, 0, this.imageRoundRadius, 0, 4);
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
            this.lastIsPinned = ChatMessageCell.this.isPinned;
            this.lastSignMessage = ChatMessageCell.this.lastPostAuthor;
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
            this.lastDrawingTextX = ChatMessageCell.this.textX;
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            this.lastForwardedNamesOffset = ChatMessageCell.this.namesOffset;
            this.lastForwardNameWidth = ChatMessageCell.this.forwardedNameWidth;
            this.lastBackgroundLeft = ChatMessageCell.this.getCurrentBackgroundLeft();
            this.lastBackgroundRight = ChatMessageCell.this.currentBackgroundDrawable.getBounds().right;
            ChatMessageCell.this.reactionsLayoutInBubble.recordDrawingState();
            if (ChatMessageCell.this.replyNameLayout != null) {
                this.lastDrawReplyY = ChatMessageCell.this.replyStartY;
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

        /* JADX WARN: Code restructure failed: missing block: B:148:0x03a7, code lost:
            if (java.lang.Math.abs(org.telegram.ui.Cells.ChatMessageCell.this.timeX - r18.lastTimeX) > 1) goto L150;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean animateChange() {
            /*
                Method dump skipped, instructions count: 1179
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
            this.animateBackgroundWidth = false;
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
            this.animateReplyY = false;
            ChatMessageCell.this.reactionsLayoutInBubble.resetAnimation();
        }

        public boolean supportChangeAnimation() {
            return true;
        }

        public int createStatusDrawableParams() {
            int i = 8;
            int i2 = 4;
            if (!ChatMessageCell.this.currentMessageObject.isOutOwner()) {
                boolean drawClock = ChatMessageCell.this.currentMessageObject.isSending() || ChatMessageCell.this.currentMessageObject.isEditing();
                boolean drawError = ChatMessageCell.this.currentMessageObject.isSendError();
                if (!drawClock) {
                    i2 = 0;
                }
                if (!drawError) {
                    i = 0;
                }
                return i | i2;
            }
            int i3 = 0;
            boolean drawCheck2 = false;
            boolean drawClock2 = false;
            boolean drawError2 = false;
            if (!ChatMessageCell.this.currentMessageObject.isSending() && !ChatMessageCell.this.currentMessageObject.isEditing()) {
                if (!ChatMessageCell.this.currentMessageObject.isSendError()) {
                    if (ChatMessageCell.this.currentMessageObject.isSent()) {
                        if (!ChatMessageCell.this.currentMessageObject.scheduled && !ChatMessageCell.this.currentMessageObject.isUnread()) {
                            i3 = 1;
                        } else {
                            i3 = 0;
                        }
                        drawCheck2 = true;
                        drawClock2 = false;
                        drawError2 = false;
                    }
                } else {
                    drawCheck2 = false;
                    drawClock2 = false;
                    drawError2 = true;
                }
            } else {
                drawCheck2 = false;
                drawClock2 = true;
                drawError2 = false;
            }
            int i4 = (drawCheck2 ? 2 : 0) | i3;
            if (!drawClock2) {
                i2 = 0;
            }
            int i5 = i2 | i4;
            if (!drawError2) {
                i = 0;
            }
            return i | i5;
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Drawable getThemedDrawable(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(key) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
